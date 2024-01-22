package com.leisa.microservice.individual.controller;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        // Define the conditions under which you consider the response as an error.
        // For example, you can check for specific status codes.
        return !response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void handleError(ClientHttpResponse response) {
        // Handle the error response here.
        // You can log the error, extract information, or take any custom action.
    }
}

