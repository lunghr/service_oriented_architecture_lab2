package com.example.endpoint;

import com.example.generated.*;
import com.example.model.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.xml.bind.Marshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import reactor.core.publisher.Mono;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Endpoint
public class HREndpoint {
    private static final String NAMESPACE_URI = "http://example.com/hr-service";
    private final static String PROXY_BASE_URL = "http://localhost:8081/api/hr/";

    private final WebClient webClient;

    public HREndpoint(WebClient webClient) {
        this.webClient = webClient;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FireEmployeeRequest")
    @ResponsePayload
    public FireEmployeeResponse fireEmployee(@RequestPayload FireEmployeeRequest request) {
        long id = request.getId();
        webClient.patch()
                .uri(PROXY_BASE_URL + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"status\": \"FIRED\"}")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        FireEmployeeResponse response = new FireEmployeeResponse();
        response.setMessage("Employee with ID " + id + " has been fired!");
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "IndexSalaryRequest")
    @ResponsePayload
    public IndexSalaryResponse indexSalary(@RequestPayload IndexSalaryRequest request) {
        Long id = request.getId();
        double coeff = request.getCoeff();

        CurrentSalaryResponse currentSalaryResponse = webClient.get()
                .uri(PROXY_BASE_URL + id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new NotFoundException("Worker with ID " + id + " not found")))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new RuntimeException("Server error from worker-service")))
                .bodyToMono(CurrentSalaryResponse.class)
                .block();
        long currentSalary = currentSalaryResponse != null ? currentSalaryResponse.getCurrentSalary() : 0;
        long newSalary = Math.round(currentSalary * coeff);

        webClient.patch()
                .uri(PROXY_BASE_URL + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"salary\":" + newSalary + "}")
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        IndexSalaryResponse response = new IndexSalaryResponse();
        response.setNewSalary(newSalary);
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SayHiRequest")
    @ResponsePayload
    public SayHiResponse sayHi(@RequestPayload SayHiRequest request) {
        SayHiResponse response = new SayHiResponse();
        response.setResponse("Hello!");
        return response;
    }
}
