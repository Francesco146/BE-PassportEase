package it.univr.passportease.controller.user;

import it.univr.passportease.entity.Citizen;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserQueryControllerTest {

    private static final String BASE_URL = "http://localhost:8080/graphql";
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        User user = new User();

        user.setName("Zelda");
        user.setSurname("NotLink");
        user.setFiscalCode("NTLZLD98R52G273R");
        user.setEmail("zeldanotlink@gmail.com");
        user.setActive(true);
        user.setUpdatedAt(new Date());
        user.setCreatedAt(new Date());
        user.setCityOfBirth("Palermo");
        user.setDateOfBirth(
                new Date(new SimpleDateFormat("yyyy-MM-dd").parse("1998-10-12").getTime())
        );
        user.setHashPassword(passwordEncoder.encode("ciao"));
        user.setRefreshToken(
                jwtService.generateRefreshToken(
                        userRepository.save(user).getId()
                ).getToken()
        );

        userRepository.save(user);

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
    void getRequestTypesByUser() {

        JWT token = jwtService.generateAccessToken(UUID.fromString("320eb378-47ed-4316-87ce-1f344993e4dc"));

        System.out.println("[!] Generated token: \n" + token + "\n");

        String getRequestTypesByUser = GraphQLOperations.getRequestTypesByUser.getGraphQl();

        GraphQlTester.Response response = (GraphQlTester.Response) makeGraphQLRequest(getRequestTypesByUser, "data.getRequestTypesByUser", null, token);

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
}