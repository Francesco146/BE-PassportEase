package it.univr.passportease.controller.user;

import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.entity.Citizen;
import it.univr.passportease.graphql.GraphQLOperations;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.repository.CitizenRepository;
import it.univr.passportease.repository.UserRepository;
import it.univr.passportease.service.jwt.JwtService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAuthControllerTest {

    private static final String BASE_URL = "http://localhost:8080/graphql";
    private static final String JWT_REGEX = "^[\\w-]*\\.[\\w-]*\\.[\\w-]*$";
    private static UUID MOCK_USER_ID;
    private static JWT REFRESH_TOKEN;

    @Autowired
    WebApplicationContext context;
    @Autowired
    private GraphQlTester graphQlTester;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CitizenRepository citizenRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @SneakyThrows(ParseException.class)
    @BeforeAll
    void createMockUser() {
        // save user if not exists
        if (citizenRepository.findByFiscalCode("NTLZLD98R52G273R").isPresent()) {
            citizenRepository.deleteByFiscalCode("NTLZLD98R52G273R");
            userRepository.deleteByFiscalCode("NTLZLD98R52G273R");
        }

        Citizen citizen = new Citizen();

        citizen.setFiscalCode("NTLZLD98R52G273R");
        citizen.setName("Zelda");
        citizen.setSurname("NotLink");
        citizen.setCityOfBirth("Palermo");
        citizen.setDateOfBirth(
                new Date(new SimpleDateFormat("yyyy-MM-dd").parse("1998-10-12").getTime())
        );
        citizen.setHealthCardNumber("1234567890");

        citizenRepository.save(citizen);

        System.out.println("\u001B[33m");
        System.out.println("[!] Mock User Created");
        System.out.println("\u001B[0m");
    }

    @AfterAll
    void deleteMockUser() {
        userRepository.deleteByFiscalCode("NTLZLD98R52G273R");
        citizenRepository.deleteByFiscalCode("NTLZLD98R52G273R");
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll();

        System.out.println("\u001B[33m");
        System.out.println("[!] Mock User Deleted");
        System.out.println("\u001B[0m");
    }

    @Test
    @Order(1)
    void registerUser() {

        String registerUser = GraphQLOperations.registerUser.getGraphQl(
                "NTLZLD98R52G273R",
                "zeldanotlink@gmail.com",
                "Zelda",
                "NotLink",
                "Palermo",
                "1998-10-12",
                "ciao"
        );

        System.out.println("[!] registerUser query: \n" + registerUser + "\n");

        LoginOutput loginOutput = (LoginOutput) makeGraphQLRequest(registerUser, "data.registerUser", LoginOutput.class, null);

        System.out.println("[!] registerUser output: \n" + loginOutputToJson(loginOutput) + "\n");

        Assertions.assertNotNull(loginOutput);

        JWTSet jwtSet = loginOutput.jwtSet();

        Assertions.assertNotNull(jwtSet);

        Assertions.assertNotNull(loginOutput.id());

        MOCK_USER_ID = loginOutput.id();

        Assertions.assertTrue(jwtSet.accessToken().matches(JWT_REGEX));
        Assertions.assertTrue(jwtSet.refreshToken().matches(JWT_REGEX));

        REFRESH_TOKEN = new JWT(jwtSet.refreshToken());
    }

    @Test
    void loginUser() {
        if (MOCK_USER_ID == null)
            registerUser();

        final String JWT_REGEX = "^[\\w-]*\\.[\\w-]*\\.[\\w-]*$";

        String loginUser = GraphQLOperations.loginUser.getGraphQl("NTLZLD98R52G273R", "ciao");

        System.out.println("[!] Login query: \n" + loginUser + "\n");


        LoginOutput loginOutput = (LoginOutput) makeGraphQLRequest(loginUser, "data.loginUser", LoginOutput.class, null);

        System.out.println("[!] Login output: \n" + loginOutputToJson(loginOutput) + "\n");

        Assertions.assertNotNull(loginOutput);

        JWTSet jwtSet = loginOutput.jwtSet();

        Assertions.assertNotNull(jwtSet);

        Assertions.assertEquals(MOCK_USER_ID, loginOutput.id());

        Assertions.assertTrue(jwtSet.accessToken().matches(JWT_REGEX));
        Assertions.assertTrue(jwtSet.refreshToken().matches(JWT_REGEX));

        REFRESH_TOKEN = new JWT(jwtSet.refreshToken());
    }

    Object makeGraphQLRequest(@NonNull String graphQlDocument, String pathResponse, Class<?> objectClass, JWT bearerToken) {
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
                                .mutate()
                )
                .headers(headers -> headers.setBearerAuth(bearerToken.getToken()))
                .build()
                .document(graphQlDocument)
                .execute();
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void logoutUser() {
        if (MOCK_USER_ID == null)
            registerUser();

        System.out.println("[!] ID: " + MOCK_USER_ID);

        JWT token = jwtService.generateAccessToken(MOCK_USER_ID);

        System.out.println("[!] Generated token: " + token);

        String logout = GraphQLOperations.logout.getGraphQl();

        System.out.println("[!] Logout query: \n" + logout + "\n");

        GraphQlTester.Response response = (GraphQlTester.Response) makeGraphQLRequest(logout, "data.logout", null, token);

        Assertions.assertNotNull(response);
        Assertions.assertDoesNotThrow(response.errors()::verify);

        System.out.println("[!] Logout output: \n" + logoutJson() + "\n");
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void changePassword() {
        if (MOCK_USER_ID == null)
            registerUser();

        JWT token = jwtService.generateAccessToken(MOCK_USER_ID);

        System.out.println("[!] Generated token: " + token);

        String changePassword = GraphQLOperations.changePassword.getGraphQl("ciao1", "ciao");

        System.out.println("[!] changePassword query: \n" + changePassword + "\n");

        GraphQlTester.Response response = (GraphQlTester.Response) makeGraphQLRequest(changePassword, "data.changePassword", null, token);

        Assertions.assertNotNull(response);
        Assertions.assertDoesNotThrow(response.errors()::verify);

        System.out.println("[!] changePassword output: \n" + changePasswordJson() + "\n");
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void changeEmail() {
        if (MOCK_USER_ID == null)
            registerUser();

        JWT token = jwtService.generateAccessToken(MOCK_USER_ID);

        System.out.println("[!] Generated token: " + token);

        String changeEmail = GraphQLOperations.changeEmail.getGraphQl("dio@candrtde.porco", "zeldanotlink@gmail.com");

        System.out.println("[!] changeEmail query: \n" + changeEmail + "\n");

        GraphQlTester.Response response = (GraphQlTester.Response) makeGraphQLRequest(changeEmail, "data.changeEmail", null, token);

        Assertions.assertNotNull(response);
        Assertions.assertDoesNotThrow(response.errors()::verify);

        String newEmail = response.path("data.changeEmail")
                .entity(String.class)
                .get();

        System.out.println("[!] changeEmail output: \n" + changeEmailJson(newEmail) + "\n");
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void refreshAccessToken() {
        if (MOCK_USER_ID == null)
            registerUser();
        if (REFRESH_TOKEN == null)
            loginUser();

        JWT token = jwtService.generateAccessToken(MOCK_USER_ID);

        System.out.println("[!] Generated token: " + token);

        String refreshAccessToken = GraphQLOperations.refreshAccessToken.getGraphQl(REFRESH_TOKEN.getToken());

        System.out.println("[!] refreshAccessToken query: \n" + refreshAccessToken + "\n");

        GraphQlTester.Response response = (GraphQlTester.Response) makeGraphQLRequest(refreshAccessToken, "data.refreshAccessToken", null, token);

        Assertions.assertNotNull(response);
        Assertions.assertDoesNotThrow(response.errors()::verify);

        JWTSet jwtSet = response.path("data.refreshAccessToken")
                .entity(JWTSet.class)
                .get();


        Assertions.assertTrue(jwtSet.accessToken().matches(JWT_REGEX));
        Assertions.assertTrue(jwtSet.refreshToken().matches(JWT_REGEX));

        System.out.println("[!] refreshAccessToken output: \n" + jwtSet.accessToken() + "\n");
    }

    private String loginOutputToJson(LoginOutput loginOutput) {
        return """
                "data": {
                    "loginUser": {
                        "id": "%s",
                        "jwtSet": {
                            "accessToken": "%s",
                            "refreshToken": "%s"
                        }
                    }
                }
                """.formatted(loginOutput.id(), loginOutput.jwtSet().accessToken(), loginOutput.jwtSet().refreshToken());
    }

    private String logoutJson() {
        return """
                "data": {
                    "logout": {
                        null
                    }
                }
                """;
    }

    private String changePasswordJson() {
        return """
                "data": {
                    "changePassword": {
                        null
                    }
                }
                """;
    }

    private String changeEmailJson(String newEmail) {
        return """
                "data": {
                    "changeEmail": "%s"
                }
                """.formatted(newEmail);
    }
}