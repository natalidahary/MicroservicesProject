//package org.example;
//
//import org.example.dto.PreferencesRequest;
//import org.example.dto.UserRequest;
//import org.example.model.Category;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockserver.client.MockServerClient;
//import org.mockserver.integration.ClientAndServer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.ContextConfiguration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.util.List;
//
//import static org.mockserver.model.HttpRequest.request;
//import static org.mockserver.model.HttpResponse.response;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@TestPropertySource(properties = {"spring.config.name=application-test"})
//public class UserControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private ClientAndServer mockServer;
//
//    private String userId;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        mockServer = ClientAndServer.startClientAndServer(1081);
//
//        new MockServerClient("127.0.0.1", 1081)
//                .when(request()
//                        .withMethod("POST")
//                        .withPath("/v1.0/invoke/service/method"))
//                .respond(response()
//                        .withStatusCode(200)
//                        .withBody("Success"));
//
//        UserRequest userRequest = new UserRequest(
//                "johndoe@example.com",
//                "johndoe",
//                "JohnDoe@123",
//                List.of(
//                        new Category("sports", "Manchester United"),
//                        new Category("tech gadgets", "iPhone")
//                )
//        );
//
//        String response = mockMvc.perform(post("/users/register")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userRequest)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        userId = objectMapper.readTree(response).get("id").asText();
//    }
//
//    @AfterEach
//    public void tearDown() {
//        mockServer.stop();
//    }
//
//    @Test
//    @WithMockUser(username = "user", password = "password", roles = "USER")
//    public void registerUser_ShouldReturnCreatedStatus() throws Exception {
//        UserRequest userRequest = new UserRequest(
//                "janedoe@example.com",
//                "janedoe",
//                "JaneDoe@123",
//                List.of(
//                        new Category("sports", "Real Madrid"),
//                        new Category("tech gadgets", "Samsung Galaxy")
//                )
//        );
//
//        mockMvc.perform(post("/users/register")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userRequest)))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    @WithMockUser(username = "user", password = "password", roles = "USER")
//    public void updatePreferences_ShouldReturnOkStatus() throws Exception {
//        PreferencesRequest preferencesRequest = new PreferencesRequest(
//                userId,
//                List.of(
//                        new Category("sports", "Barcelona"),
//                        new Category("tech gadgets", "MacBook")
//                )
//        );
//
//        mockMvc.perform(put("/users/preferences")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(preferencesRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.categories[0].name").value("sports"))
//                .andExpect(jsonPath("$.categories[0].preference").value("Barcelona"))
//                .andExpect(jsonPath("$.categories[1].name").value("tech gadgets"))
//                .andExpect(jsonPath("$.categories[1].preference").value("MacBook"));
//    }
//
//    @Test
//    @WithMockUser(username = "user", password = "password", roles = "USER")
//    public void getAllUsers_ShouldReturnOkStatus() throws Exception {
//        mockMvc.perform(get("/users")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray());
//    }
//
//    @Test
//    @WithMockUser(username = "user", password = "password", roles = "USER")
//    public void deleteUserById_ShouldReturnNoContentStatus() throws Exception {
//        mockMvc.perform(delete("/users/{userId}", userId)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @WithMockUser(username = "user", password = "password", roles = "USER")
//    public void invokeOtherService_ShouldReturnOkStatus() throws Exception {
//        String serviceId = "test-service";
//        String methodName = "testMethod";
//        String request = "{\"key\": \"value\"}";
//
//        mockMvc.perform(post("/users/invoke")
//                        .param("serviceId", serviceId)
//                        .param("methodName", methodName)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(status().isOk());
//    }
//}
