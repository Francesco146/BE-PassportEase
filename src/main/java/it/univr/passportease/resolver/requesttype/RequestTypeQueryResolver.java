package it.univr.passportease.resolver.requesttype;


import graphql.kickstart.tools.GraphQLQueryResolver;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.service.requesttype.RequestTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@AllArgsConstructor
public class RequestTypeQueryResolver implements GraphQLQueryResolver {

    private final RequestTypeService requestTypeService;

    public List<RequestType> getAllRequestType() {
        return requestTypeService.get();
    }
}
