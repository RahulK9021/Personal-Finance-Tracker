package com.finance.repository;

import com.finance.entity.TxnType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TxnTypeRepository extends JpaRepository<TxnType, Long> {

    TxnType findByName(String name);

}
