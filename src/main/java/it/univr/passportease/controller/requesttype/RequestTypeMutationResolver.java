package it.univr.passportease.controller.requesttype;

import it.univr.passportease.dto.RequestTypeInput;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.service.requesttype.RequestTypeService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class RequestTypeMutationResolver {
    private final RequestTypeService requestTypeService;

    @MutationMapping
    public RequestType addRequestType(@Argument("requestType") RequestTypeInput requestTypeInput) {
        return requestTypeService.add(requestTypeInput);
    }
}
