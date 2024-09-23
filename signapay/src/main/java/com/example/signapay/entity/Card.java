package com.example.signapay.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardNumber;
    private double balance;

    @ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	@JsonIgnore
    private Account account;

    public void setBalance(double balance) {
        this.balance = BigDecimal.valueOf(balance)
                .setScale(2, RoundingMode.FLOOR)
                .doubleValue();;
    }
}