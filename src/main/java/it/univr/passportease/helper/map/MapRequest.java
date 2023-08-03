package it.univr.passportease.helper.map;

import java.util.Date;

import org.springframework.stereotype.Component;

import it.univr.passportease.dto.input.RequestInput;
import it.univr.passportease.entity.Request;
import it.univr.passportease.entity.RequestType;
import it.univr.passportease.entity.Worker;

@Component
public class MapRequest {
    public Request mapRequestInputToRequest(RequestInput requestInput, RequestType requestType, Worker worker) {
        Request request = new Request();
        request.setDuration(requestInput.getDuration());
        request.setStartDate(requestInput.getStartDate());
        request.setEndDate(requestInput.getEndDate());
        request.setStartTime(requestInput.getStartTime());
        request.setEndTime(requestInput.getEndTime());
        request.setWorker(worker);
        request.setRequestType(requestType);
        request.setCreatedAt(new Date());
        request.setUpdatedAt(new Date());
        return request;
    }
}
