package it.univr.passportease.controller.user;

import it.univr.passportease.dto.input.RegisterInput;
import it.univr.passportease.dto.output.JWTSet;
import it.univr.passportease.dto.output.LoginOutput;
import it.univr.passportease.graphql.GraphQLOperations;
import it.univr.passportease.service.jwt.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.UUID;

/*
* TODO
*  finire registerUser
*  refreshAccessToken
* */

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
@AutoConfigureMockMvc
class UserAuthControllerTest {
    @Autowired
    WebApplicationContext context;

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private JwtService jwtService;

    @Test
    void loginUser() {
        final String JWT_REGEX = "^[\\w-]*\\.[\\w-]*\\.[\\w-]*$";

        String loginUser = GraphQLOperations.loginUser.getGraphQl("BLSCLL96D55E463O", "ciao");

        System.out.println("[!] Login query: \n" + loginUser + "\n");


        LoginOutput loginOutput = graphQlTester
                .mutate().build()
                .document(loginUser)
                .execute()
                .path("data.loginUser")
                .entity(LoginOutput.class)
                .get();

        System.out.println("[!] Login output: \n" + loginOutputToJson(loginOutput) + "\n");

        Assertions.assertNotNull(loginOutput);

        JWTSet jwtSet = loginOutput.jwtSet();

        Assertions.assertNotNull(jwtSet);

        Assertions.assertEquals(UUID.fromString("09933d03-d5af-40de-a915-89c24dabe84f"), loginOutput.id());

        Assertions.assertTrue(jwtSet.accessToken().matches(JWT_REGEX));
        Assertions.assertTrue(jwtSet.refreshToken().matches(JWT_REGEX));

        System.out.println("\u001B[32m");
        System.out.println("[!] Login successful");
        System.out.println("\u001B[0m");
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void logoutUser() {
        String token = jwtService.generateAccessToken(UUID.fromString("fce25167-0c05-46f1-bbc4-7b83ef8ab1a9"));

        System.out.println("[!] Generated token: " + token);

        String logout = GraphQLOperations.logout.getGraphQl();

        System.out.println("[!] Logout query: \n" + logout + "\n");

        GraphQlTester.Response response = HttpGraphQlTester.builder(
                        MockMvcWebTestClient.bindToApplicationContext(context)
                                .configureClient()
                                .baseUrl("http://localhost:8080/graphql")
                                .build()
                                .mutate()
                )
                .headers(headers -> headers.setBearerAuth(token))
                .build()
                .document(logout)
                .execute();


        Assertions.assertNotNull(response);
        Assertions.assertDoesNotThrow(response.errors()::verify);

        System.out.println("[!] Logout output: \n" + logoutJson() + "\n");

        System.out.println("\u001B[32m");
        System.out.println("[!] Logout successful");
        System.out.println("\u001B[0m");
    }

    @Test
    void registerUser() {
        final String JWT_REGEX = "^[\\w-]*\\.[\\w-]*\\.[\\w-]*$";

        String registerUser = GraphQLOperations.registerUser.getGraphQl("NTLZLD98R52G273R",
                "zeldanotlink@gmail.com",
                "Zelda",
                "NotLink",
                "Palermo",
                "1998-10-12",
                "ciao");

        System.out.println("[!] registerUser query: \n" + registerUser + "\n");

        LoginOutput loginOutput = graphQlTester
                .mutate().build()
                .document(registerUser)
                .execute()
                .path("data.registerUser")
                .entity(LoginOutput.class)
                .get();

        System.out.println("[!] registerUser output: \n" + loginOutputToJson(loginOutput) + "\n");

        Assertions.assertNotNull(loginOutput);

        JWTSet jwtSet = loginOutput.jwtSet();

        Assertions.assertNotNull(jwtSet);

        Assertions.assertNotNull(loginOutput.id());

        Assertions.assertTrue(jwtSet.accessToken().matches(JWT_REGEX));
        Assertions.assertTrue(jwtSet.refreshToken().matches(JWT_REGEX));

        System.out.println("\u001B[32m");
        System.out.println("[!] Registration successful");
        System.out.println("\u001B[0m");
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void changePassword() {
        String token = jwtService.generateAccessToken(UUID.fromString("fce25167-0c05-46f1-bbc4-7b83ef8ab1a9"));

        System.out.println("[!] Generated token: " + token);

        String changePassword = GraphQLOperations.changePassword.getGraphQl("ciao1", "ciao");

        System.out.println("[!] changePassword query: \n" + changePassword + "\n");

        GraphQlTester.Response response = HttpGraphQlTester.builder(
                        MockMvcWebTestClient.bindToApplicationContext(context)
                                .configureClient()
                                .baseUrl("http://localhost:8080/graphql")
                                .build()
                                .mutate()
                )
                .headers(headers -> headers.setBearerAuth(token))
                .build()
                .document(changePassword)
                .execute();

        Assertions.assertNotNull(response);
        Assertions.assertDoesNotThrow(response.errors()::verify);

        System.out.println("[!] changePassword output: \n" + changePasswordJson() + "\n");

        System.out.println("\u001B[32m");
        System.out.println("[!] Change Password successful");
        System.out.println("\u001B[0m");
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void changeEmail() {
        String token = jwtService.generateAccessToken(UUID.fromString("fce25167-0c05-46f1-bbc4-7b83ef8ab1a9"));

        System.out.println("[!] Generated token: " + token);

        String changeEmail = GraphQLOperations.changeEmail.getGraphQl("gaymommy@gmail.com", "dio@candrtde.porco");

        System.out.println("[!] changeEmail query: \n" + changeEmail + "\n");

       GraphQlTester.Response response = HttpGraphQlTester.builder(
                        MockMvcWebTestClient.bindToApplicationContext(context)
                                .configureClient()
                                .baseUrl("http://localhost:8080/graphql")
                                .build()
                                .mutate()
                )
                .headers(headers -> headers.setBearerAuth(token))
                .build()
                .document(changeEmail)
                .execute();

        Assertions.assertNotNull(response);
        Assertions.assertDoesNotThrow(response.errors()::verify);

        String newEmail = response.path("data.changeEmail").entity(String.class).get();

        System.out.println("[!] changeEmail output: \n" + changeEmailJson(newEmail) + "\n");

        System.out.println("\u001B[32m");
        System.out.println("[!] Change Email successful");
        System.out.println("\u001B[0m");
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