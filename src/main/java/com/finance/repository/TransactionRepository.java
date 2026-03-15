package com.finance.repository;

import com.finance.entity.Transaction;
import com.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction , Long> {
    List<Transaction> findByUser(User user);

    List<Transaction> findByUserAndCategoryIgnoreCase(User user , String category);

    List<Transaction> findTop5ByUserOrderByDateDesc(User user);

    List<Transaction> findByUserIdAndDateBetweenAndCategory(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String category
    );

    Optional<Transaction> findByIdAndUser(Long id, User user);
}
