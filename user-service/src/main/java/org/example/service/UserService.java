package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.client.DaprClient;
import io.dapr.client.domain.HttpExtension;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PreferencesRequest;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.exception.UserNotFoundException;
import org.example.model.Category;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.util.PasswordValidator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final DaprClient daprClient;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        log.info("Initializing user service...");
        try {
            if (userRepository.count() == 0) {
                User user = User.builder()
                        .email("defaultuser@example.com")
                        .username("defaultuser")
                        .password("DefaultUser@123")
                        .categories(List.of(
                                new Category("sports", "Manchester United"),
                                new Category("tech gadgets", "iPhone")
                        ))
                        .build();
                userRepository.save(user);
                log.info("Inserted default user on startup.");
            }
        } catch (Exception e) {
            log.error("Error during initialization", e);
        }
    }

    //@CachePut(value = "users", key = "#userRequest.email")
    public UserResponse registerUser(UserRequest userRequest) {
        log.debug("Attempting to register user with email: {}", userRequest.email());

        if (!PasswordValidator.validate(userRequest.password())) {
            throw new IllegalArgumentException("Password does not meet the requirements");
        }
        // Check if the email already exists
        log.info("Checking if email {} exists", userRequest.email());
        Optional<User> existingUser = userRepository.findByEmail(userRequest.email());
        if (existingUser.isPresent()) {
            log.warn("Email {} already exists", userRequest.email());
            throw new IllegalArgumentException("Email already exists in the system");
        }
        User user = User.builder()
                .email(userRequest.email())
                .username(userRequest.username())
                .password(userRequest.password())
                .categories(userRequest.categories())
                .build();
        userRepository.save(user);
        log.info("User registered successfully with email: {}", user.getEmail());

        // Publish an event to notify other services via RabbitMQ
        try {
            String userJson = objectMapper.writeValueAsString(user);
            daprClient.publishEvent("pubsub", "user-registered", userJson).block();
        } catch (Exception e) {
            log.error("Error publishing event", e);
        }

        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCategories());
    }

    //@CachePut(value = "users", key = "#preferencesRequest.userId")
    public UserResponse updatePreferences(PreferencesRequest preferencesRequest) {
        log.info("Updating preferences for user ID: {}", preferencesRequest.userId());
        User user = userRepository.findById(preferencesRequest.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + preferencesRequest.userId()));

        user.setCategories(preferencesRequest.categories());
        userRepository.save(user);
        log.info("Preferences updated for user ID: {}", user.getId());

        // Publish an event to notify other services via RabbitMQ
        try {
            String userJson = objectMapper.writeValueAsString(user);
            daprClient.publishEvent("pubsub", "preferences-updated", userJson).block();
        } catch (Exception e) {
            log.error("Error publishing event", e);
        }

        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCategories());
    }

    //@Cacheable(value = "users")
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users.");
        List<User> users = userRepository.findAll();
        log.info("Fetched {} users.", users.size());
        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCategories()))
                .collect(Collectors.toList());
    }

    //@CacheEvict(value = "users", key = "#userId")
    public void deleteUserById(String userId) {
        log.info("Deleting user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", userId);

        // Publish an event to notify other services via RabbitMQ
        try {
            String userJson = objectMapper.writeValueAsString(userId);
            daprClient.publishEvent("pubsub", "user-deleted", userJson).block();
        } catch (Exception e) {
            log.error("Error publishing event", e);
        }
    }

    public List<Category> getUserPreferences(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getCategories();
    }

    public void invokeOtherService(String serviceId, String methodName, String requestBody) {
        log.info("Invoking service {} with method {}", serviceId, methodName);
        try {
            daprClient.invokeMethod(serviceId, methodName, requestBody, HttpExtension.POST).block();
            log.info("Service {} invoked successfully with method {}", serviceId, methodName);
        } catch (Exception e) {
            log.error("Error invoking service {} with method {}", serviceId, methodName, e);
        }
    }
}
