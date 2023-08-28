package it.univr.passportease.controller.user;

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
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.data.redis.host=localhost", "spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase"}
)
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

        Assertions.assertEquals(UUID.fromString("c1394aeb-0e05-465c-ba05-98dd6fde815c"), loginOutput.id());

        Assertions.assertTrue(jwtSet.accessToken().matches(JWT_REGEX));
        Assertions.assertTrue(jwtSet.refreshToken().matches(JWT_REGEX));

        System.out.println("\u001B[32m");
        System.out.println("[!] Login successful");
        System.out.println("\u001B[0m");
    }

    @Test
    @WithMockUser(authorities = {"USER", "VALIDATED"})
    void logoutUser() {
        String token = jwtService.generateAccessToken(UUID.fromString("c1394aeb-0e05-465c-ba05-98dd6fde815c"));

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
        response.errors().verify();

        System.out.println("[!] Logout output: \n" + logoutJson() + "\n");

        System.out.println("\u001B[32m");
        System.out.println("[!] Logout successful");
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
}