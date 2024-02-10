package it.univr.passportease.dto.output;

import it.univr.passportease.entity.Office;
import it.univr.passportease.entity.Request;

import java.util.List;

public record RequestAndOffices(Request request, List<Office> offices) {
}
