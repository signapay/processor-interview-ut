package com.example.signapay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
public class Transaction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountName;
    private String cardNumber;
    private double amount;
    private String transactionType;
    private String description;
    private String targetCardNumber;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;  // To differentiate valid/bad transactions

    private String errorReason; // Optional field for bad transactions

    public void setAmount(double amount) {
        this.amount = BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.FLOOR)
                .doubleValue();;
    }
}
