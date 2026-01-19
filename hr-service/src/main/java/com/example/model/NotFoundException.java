package com.example.model;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

// Эта аннотация превратит исключение в SOAP Fault с кодом Client (ошибка клиента)
@SoapFault(faultCode = FaultCode.CLIENT)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
