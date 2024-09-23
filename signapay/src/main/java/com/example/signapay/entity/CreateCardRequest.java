package com.example.signapay.entity;

import lombok.Data;

@Data
public class CreateCardRequest {
    private String accountName;
    private String cardNumber;
}
