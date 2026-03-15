package com.finance.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "txn_type")
@Data
public class TxnType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
