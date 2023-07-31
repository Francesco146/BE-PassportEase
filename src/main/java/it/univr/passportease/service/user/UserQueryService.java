package it.univr.passportease.service.user;

import it.univr.passportease.dto.output.UserOutput;
import org.springframework.web.bind.annotation.RequestHeader;

public interface UserQueryService {
    UserOutput getUserDetails(@RequestHeader("Authorization") String token);
}
