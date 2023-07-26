package it.univr.passportease.resolver.requesttype;


import it.univr.passportease.entity.RequestType;
import it.univr.passportease.service.requesttype.RequestTypeService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@AllArgsConstructor
public class RequestTypeQueryResolver {

    private final RequestTypeService requestTypeService;

    @QueryMapping
    public List<RequestType> getAllRequestType() {
        return requestTypeService.get();
    }
}
