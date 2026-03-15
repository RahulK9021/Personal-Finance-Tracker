package com.finance.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private Long id;
    private Double amount;
    private String category;
    private String description;
    private LocalDate date;
    private String type;
    private LocalDateTime createdAt;

}
