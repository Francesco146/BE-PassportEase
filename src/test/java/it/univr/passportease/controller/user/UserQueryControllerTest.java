package it.univr.passportease.controller.user;

import it.univr.passportease.entity.RequestType;
import it.univr.passportease.graphql.GraphQLOperations;
import it.univr.passportease.helper.JWT;
import it.univr.passportease.service.jwt.JwtService;
import lombok.NonNull;
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

import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
@AutoConfigureMockMvc
class UserQueryControllerTest {

    private static final String BASE_URL = "http://localhost:8080/graphql";
    @Autowired
    WebApplicationContext context;
    @Autowired
    private GraphQlTester graphQlTester;
    @Autowired
    private JwtService jwtService;

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