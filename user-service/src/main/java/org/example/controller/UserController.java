package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NewsRequest;
import org.example.dto.PreferencesRequest;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.registerUser(userRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserResponse> updatePreferences(@RequestBody PreferencesRequest preferencesRequest) {
        UserResponse userResponse = userService.updatePreferences(preferencesRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String userId) {
        userService.deleteUserById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{userId}/preferences")
    public ResponseEntity<List<String>> getUserPreferences(@PathVariable String userId) {
        List<String> preferences = userService.getUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }


    @GetMapping("/{userId}/sendPreferences")
    public ResponseEntity<String> sendPreferences(@PathVariable String userId) {
        List<String> preferences = userService.getUserPreferences(userId);
        String preferencesJson = "";
        String email = userService.getUserEmail(userId);
        try {
            preferencesJson = objectMapper.writeValueAsString(new NewsRequest(preferences, userId, email));
        } catch (JsonProcessingException e) {
            log.error("Error converting preferences to JSON", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting preferences to JSON");
        }

        // Send preferences to the news service
        userService.sendPreferencesToNewsService(userId);

        // Return the JSON message in the response
        return ResponseEntity.ok("Preferences sent to news service for userId: " + userId + "\n" + preferencesJson + email);
    }

    @PostMapping("/invoke")
    public ResponseEntity<Void> invokeOtherService(@RequestParam String serviceId, @RequestParam String methodName, @RequestBody String request) {
        userService.invokeOtherService(serviceId, methodName, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
