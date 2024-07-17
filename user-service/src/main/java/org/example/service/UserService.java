package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.client.DaprClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NewsRequest;
import org.example.dto.PreferencesRequest;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.exception.UserNotFoundException;
import org.example.model.Category;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.util.PasswordValidator;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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
    private HttpClient httpClient;


    @PostConstruct
    public void init() {
        log.info("Initializing user service...");
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @PostConstruct
    public void initData() {
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
            daprClient.publishEvent("rabbitmq-pubsub", "user-registered", userJson).block();
        } catch (Exception e) {
            log.error("Error publishing event", e);
        }

        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCategories());
    }


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
            daprClient.publishEvent("rabbitmq-pubsub", "preferences-updated", userJson).block();
        } catch (Exception e) {
            log.error("Error publishing event", e);
        }

        return new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCategories());
    }

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users.");
        List<User> users = userRepository.findAll();
        log.info("Fetched {} users.", users.size());
        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getUsername(), user.getCategories()))
                .collect(Collectors.toList());
    }


    public void deleteUserById(String userId) {
        log.info("Deleting user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", userId);

        // Publish an event to notify other services via RabbitMQ
        try {
            String userJson = objectMapper.writeValueAsString(userId);
            daprClient.publishEvent("rabbitmq-pubsub", "user-deleted", userJson).block();
        } catch (Exception e) {
            log.error("Error publishing event", e);
        }
    }


    public List<String> getUserPreferences(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getCategories().stream()
                .map(Category::getPreference)
                .collect(Collectors.toList());
    }


    public String getUserEmail(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getEmail();
    }


    public void sendPreferencesToNewsService(String userId) {
        List<String> preferences = getUserPreferences(userId);
        String email = getUserEmail(userId);
        try {
            String preferencesJson = objectMapper.writeValueAsString(new NewsRequest(preferences, userId, email));
            invokeOtherService("news-service", "news/processPreferences", preferencesJson);
        } catch (JsonProcessingException e) {
            log.error("Error converting preferences to JSON", e);
            throw new RuntimeException("Failed to convert preferences to JSON", e);
        }
    }


    public void invokeOtherService(String serviceId, String methodName, String requestBody) {
        log.info("Invoking service {} with method {}", serviceId, methodName);
        try {
            String daprUrl = String.format("http://localhost:3500/v1.0/invoke/%s/method/%s", serviceId, methodName);
            log.info("Dapr URL: {}", daprUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(daprUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Service {} invoked successfully with method {}", serviceId, methodName);
            log.info("Response: {}", response.body());
        } catch (Exception e) {
            log.error("Error invoking service {} with method {}", serviceId, methodName, e);
        }
    }


}
