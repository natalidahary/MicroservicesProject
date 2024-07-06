package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.HttpExtension;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final DaprClient daprClient = new DaprClientBuilder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();
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
        try {
            User user = User.builder()
                    .email(userRequest.email())
                    .preferences(userRequest.preferences())
                    .build();
            userRepository.save(user);
            log.info("User registered successfully with email: {}", user.getEmail());
            return new UserResponse(user.getId(), user.getEmail(), user.getPreferences());
        } catch (Exception e) {
            log.error("Error registering user with email: {}", userRequest.email(), e);
            throw e;
        }
    }

    public UserResponse updatePreferences(PreferencesRequest preferencesRequest) {
        log.info("Updating preferences for user ID: {}", preferencesRequest.userId());
        try {
            User user = userRepository.findById(preferencesRequest.userId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + preferencesRequest.userId()));
            user.setPreferences(preferencesRequest.preferences());
            userRepository.save(user);
            log.info("Preferences updated for user ID: {}", user.getId());
            return new UserResponse(user.getId(), user.getEmail(), user.getPreferences());
        } catch (UserNotFoundException e) {
            log.warn("User not found with ID: {}", preferencesRequest.userId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error updating preferences for user ID: {}", preferencesRequest.userId(), e);
            throw e;
        }
    }

    public void invokeOtherService(String serviceId, String methodName, Object request) {
        log.info("Invoking service {} with method {}", serviceId, methodName);
        try {
            byte[] requestData = objectMapper.writeValueAsBytes(request);
            daprClient.invokeMethod(serviceId, methodName, requestData, HttpExtension.POST, byte[].class).block();
            log.info("Service {} invoked successfully with method {}", serviceId, methodName);
        } catch (Exception e) {
            log.error("Error invoking service {} with method {}", serviceId, methodName, e);
        }
    }
}
