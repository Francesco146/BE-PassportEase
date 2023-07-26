package it.univr.passportease.resolver.requesttype;

import graphql.kickstart.tools.GraphQLMutationResolver;
import it.univr.passportease.dto.RequestTypeInput;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.service.requesttype.RequestTypeService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class RequestTypeMutationResolver implements GraphQLMutationResolver {
    private final RequestTypeService requestTypeService;

    @MutationMapping
    public RequestType addRequestType(@Argument RequestTypeInput requestTypeInput) {
        System.out.println("RequestTypeMutationResolver.addRequestType\n\n\n\n\n\n");
        System.out.println("requestTypeInput = " + requestTypeInput);
        return null;
        //return requestTypeService.add(requestTypeInput);
    }
}
