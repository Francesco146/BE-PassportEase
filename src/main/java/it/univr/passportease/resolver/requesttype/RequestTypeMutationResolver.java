package it.univr.passportease.resolver.requesttype;

import graphql.kickstart.tools.GraphQLQueryResolver;
import it.univr.passportease.dto.RequestTypeInput;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.service.requesttype.RequestTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RequestTypeMutationResolver implements GraphQLQueryResolver {

    private final RequestTypeService requestTypeService;

    public RequestType addRequestType(RequestTypeInput requestTypeInput) {
        return requestTypeService.add(requestTypeInput);
    }
}
