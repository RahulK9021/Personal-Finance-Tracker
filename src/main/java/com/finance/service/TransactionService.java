package com.finance.service;

import com.finance.dto.TransactionRequest;
import com.finance.entity.Transaction;
import com.finance.entity.User;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;

    private static final Logger log =
            LoggerFactory.getLogger(TransactionService.class);

    public Transaction createTransaction(TransactionRequest request) {

        User user = getLoggedInUser();

        Transaction transaction = new Transaction();

        transaction.setUser(user);
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setType(request.getType());
        transaction.setCreatedAt(LocalDateTime.now());

        return repository.save(transaction);
    }

    public List<Transaction> getAllTransaction() {
        User user = getLoggedInUser();
        return repository.findByUser(user);
    }

    public void delete(Long id) {

        User user = getLoggedInUser();

        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot delete this transaction");
        }

        repository.delete(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction transaction) {

        User user = getLoggedInUser();

        Transaction existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot update this transaction");
        }

        if (transaction.getAmount() != null)
            existing.setAmount(transaction.getAmount());
        if (transaction.getCategory() != null)
            existing.setCategory(transaction.getCategory());
        if (transaction.getDescription() != null)
            existing.setDescription(transaction.getDescription());
        if (transaction.getDate() != null)
            existing.setDate(transaction.getDate());
        if (transaction.getType() != null)
            existing.setType(transaction.getType());

        return repository.save(existing);
    }

    public List<Transaction> searchByCategory(String category) {

        User user = getLoggedInUser();
        return repository.findByUserAndCategory(user, category);
    }

    public List<Transaction> getRecentTransactions() {

        User user = getLoggedInUser();
        return repository.findTop5ByUserOrderByDateDesc(user);
    }

    public List<Transaction> filterTransactions(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String category
    ) {
        return repository
                .findByUserIdAndDateBetweenAndCategory(userId, startDate, endDate, category);
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}