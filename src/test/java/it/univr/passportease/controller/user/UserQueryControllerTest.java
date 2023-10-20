package it.univr.passportease.controller.user;

import it.univr.passportease.entity.*;
import it.univr.passportease.graphql.GraphQLOperations;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.repository.CitizenRepository;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.service.jwt.JwtService;
import it.univr.passportease.service.user.impl.UserMutationServiceImpl;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserQueryControllerTest {

    private static final String BASE_URL = "http://localhost:8080/graphql";
    private static final UUID USER_ID = UUID.fromString("b2e26425-59cb-4d39-9835-4218002a0c73");

    @Autowired
    WebApplicationContext context;
    @Autowired
    private GraphQlTester graphQlTester;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private UserMutationServiceImpl userMutationService;



    Object makeGraphQLRequest(@NonNull String graphQlDocument, String pathResponse, Class<?> objectClass,
                              JWT bearerToken) {
        if (bearerToken == null)
            return graphQlTester
                    .mutate()
                    .build()
                    .document(graphQlDocument)
                    .execute()
                    .path(pathResponse)
                    .entity(objectClass)
                    .get();

        return HttpGraphQlTester.builder(
                        MockMvcWebTestClient.bindToApplicationContext(context)
                                .configureClient()
                                .baseUrl(BASE_URL)
                                .build()
                                .mutate())
                .headers(headers -> headers.setBearerAuth(bearerToken.getToken()))
                .build()
                .document(graphQlDocument)
                .execute();
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void getRequestTypesByUser() {

        JWT token = jwtService.generateAccessToken(USER_ID);

        System.out.println("[!] Generated token: \n" + token + "\n");

        String getRequestTypesByUser = GraphQLOperations.getRequestTypesByUser.getGraphQl();

        GraphQlTester.Response response = (GraphQlTester.Response) makeGraphQLRequest(getRequestTypesByUser,
                "data.getRequestTypesByUser", null, token);

        Assertions.assertNotNull(response);

        Assertions.assertDoesNotThrow(response.errors()::verify);

        List<RequestType> requestType = response
                .path("data.getRequestTypesByUser")
                .entityList(RequestType.class)
                .get();

        requestType.forEach(System.out::println);

        System.out.println("\u001B[32m");
        System.out.println("[!] getRequestTypesByUser successful");
        System.out.println("\u001B[0m");
    }


    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    //@SneakyThrows(ParseException.class)
    void getUserNotifications() {

        JWT token = jwtService.generateAccessToken(USER_ID);

        System.out.println("[!] Generated token: \n" + token + "\n");

        String getUserNotifications = GraphQLOperations.getUserNotifications.getGraphQl();

        GraphQlTester.Response response = (GraphQlTester.Response) makeGraphQLRequest(getUserNotifications,
                "data.getUserNotifications", null, token);

        Assertions.assertNotNull(response);

        Assertions.assertDoesNotThrow(response.errors()::verify);

        List<Notification> notifications = response
                .path("data.getUserNotifications")
                .entityList(Notification.class)
                .get();

        notifications.forEach(System.out::println);

        System.out.println("\u001B[32m");
        System.out.println("[!] getUserNotifications successful");
        System.out.println("\u001B[0m");
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    @SneakyThrows(ParseException.class)
    void getUserDetails() {

        JWT token = jwtService.generateAccessToken(USER_ID);

        System.out.println("[!] Generated token: \n" + token + "\n");

        String getUserDetails = GraphQLOperations.getUserDetails.getGraphQl();

        GraphQlTester.Response response = (GraphQlTester.Response) makeGraphQLRequest(getUserDetails,
                "data.getUserDetails", null, token);

        Assertions.assertNotNull(response);

        Assertions.assertDoesNotThrow(response.errors()::verify);

        User user = response
                .path("data.getUserDetails")
                .entity(User.class)
                .get();

        Assertions.assertEquals(USER_ID, user.getId());
        Assertions.assertEquals("Zelda", user.getName());
        Assertions.assertEquals("NotLink", user.getSurname());
        Assertions.assertEquals("NTLZLD98R52G273R", user.getFiscalCode());
        Assertions.assertEquals(new Date(new SimpleDateFormat("yyyy-MM-dd").parse("1998-10-12").getTime()),
                user.getDateOfBirth());
        Assertions.assertEquals("Palermo", user.getCityOfBirth());

        System.out.println(user);

        System.out.println("\u001B[32m");
        System.out.println("[!] getUserDetails successful");
        System.out.println("\u001B[0m");
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void getUserReservations() {

        JWT token = jwtService.generateAccessToken(USER_ID);

        System.out.println("[!] Generated token: \n" + token + "\n");

        String getUserReservations = GraphQLOperations.getUserReservations.getGraphQl();

        GraphQlTester.Response response = (GraphQlTester.Response) makeGraphQLRequest(getUserReservations, "data.getUserReservations", null, token);

        Assertions.assertNotNull(response);

        Assertions.assertDoesNotThrow(response.errors()::verify);

        List<Availability> availabilities = response
                .path("data.getUserReservations")
                .entityList(Availability.class)
                .get();

        availabilities.forEach(System.out::println);

        System.out.println("\u001B[32m");
        System.out.println("[!] getUserReservations successful");
        System.out.println("\u001B[0m");
    }
}