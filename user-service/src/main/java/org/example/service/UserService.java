package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PreferencesRequest;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.exception.UserNotFoundException;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final DaprClient daprClient = new DaprClientBuilder().build();

    @PostConstruct
    public void init() {
        log.info("Initializing user service...");
        try {
            if (userRepository.count() == 0) {
                User user = User.builder()
                        .email("test@example.com")
                        .preferences(List.of("technology", "news"))
                        .build();
                userRepository.save(user);
                log.info("Inserted default user on startup.");
            }
        } catch (Exception e) {
            log.error("Error during initialization", e);
        }
    }

    public UserResponse registerUser(UserRequest userRequest) {
        log.info("Registering user with email: {}", userRequest.email());
        User user = User.builder()
                .email(userRequest.email())
                .preferences(userRequest.preferences())
                .build();
        userRepository.save(user);
        log.info("User registered successfully with email: {}", user.getEmail());

        // Publish an event to notify other services
        daprClient.publishEvent("pubsub", "user-registered", user).block();

        return new UserResponse(user.getId(), user.getEmail(), user.getPreferences());
    }

    public UserResponse updatePreferences(PreferencesRequest preferencesRequest) {
        log.info("Updating preferences for user ID: {}", preferencesRequest.userId());
        User user = userRepository.findById(preferencesRequest.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + preferencesRequest.userId()));
        user.setPreferences(preferencesRequest.preferences());
        userRepository.save(user);
        log.info("Preferences updated for user ID: {}", user.getId());

        // Publish an event to notify other services
        daprClient.publishEvent("pubsub", "preferences-updated", user).block();

        return new UserResponse(user.getId(), user.getEmail(), user.getPreferences());
    }

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users.");
        List<User> users = userRepository.findAll();
        log.info("Fetched {} users.", users.size());
        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getPreferences()))
                .collect(Collectors.toList());
    }

    public void deleteUserById(String userId) {
        log.info("Deleting user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", userId);

        // Publish an event to notify other services
        daprClient.publishEvent("pubsub", "user-deleted", userId).block();
    }

    public void invokeOtherService(String serviceId, String methodName, Object request) {
        log.info("Invoking service {} with method {}", serviceId, methodName);
        try {
            byte[] requestData = new ObjectMapper().writeValueAsBytes(request);
            daprClient.invokeMethod(serviceId, methodName, requestData, io.dapr.client.domain.HttpExtension.POST, byte[].class).block();
            log.info("Service {} invoked successfully with method {}", serviceId, methodName);
        } catch (Exception e) {
            log.error("Error invoking service {} with method {}", serviceId, methodName, e);
        }
    }
}