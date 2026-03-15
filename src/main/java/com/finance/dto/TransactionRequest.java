package com.finance.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionRequest {

    private Double amount;
    private String category;
    private String description;
    private LocalDate date;
    private String type;
}
