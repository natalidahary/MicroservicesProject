package org.example.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PreferencesRequest;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        // Insert a user on startup to ensure database and collection creation
        if (userRepository.count() == 0) {
            User user = User.builder()
                    .email("test@example.com")
                    .preferences(List.of("technology", "news"))
                    .build();
            userRepository.save(user);
        }
    }

    public UserResponse registerUser(UserRequest userRequest) {
        User user = User.builder()
                .email(userRequest.email())
                .preferences(userRequest.preferences())
                .build();
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getEmail(), user.getPreferences());
    }

    public UserResponse updatePreferences(PreferencesRequest preferencesRequest) {
        User user = userRepository.findById(preferencesRequest.userId()).orElseThrow();
        user.setPreferences(preferencesRequest.preferences());
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getEmail(), user.getPreferences());
    }
}
