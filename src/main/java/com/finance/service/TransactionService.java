package com.finance.service;

import com.finance.dto.TransactionRequest;
import com.finance.dto.TransactionResponse;
import com.finance.entity.Transaction;
import com.finance.entity.TxnType;
import com.finance.entity.User;
import com.finance.repository.TransactionRepository;
import com.finance.repository.TxnTypeRepository;
import com.finance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
public class TransactionService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;
    private final TxnTypeRepository txnTypeRepository;

    private static final Logger log =
            LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository repository, UserRepository userRepository, TxnTypeRepository txnTypeRepository) {
        this.repository = repository;
        this.userRepository = userRepository;

        this.txnTypeRepository = txnTypeRepository;
    }

    public TransactionResponse createTransaction(TransactionRequest request) {

        User user = getLoggedInUser();

        TxnType type = txnTypeRepository.findByName(request.getType());

        if (type == null) {
            throw new RuntimeException("Invalid transaction type: " + request.getType());
        }

        Transaction transaction = new Transaction();

        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction.setType(type);
        transaction.setUser(user);
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = repository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    public List<TransactionResponse> getAllTransaction() {

        User user = getLoggedInUser();

        return repository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
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

    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {

        User user = getLoggedInUser();

        Transaction existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot update this transaction");
        }

        if (request.getAmount() != null)
            existing.setAmount(request.getAmount());

        if (request.getCategory() != null)
            existing.setCategory(request.getCategory());

        if (request.getDescription() != null)
            existing.setDescription(request.getDescription());

        if (request.getDate() != null)
            existing.setDate(request.getDate());

        if (request.getType() != null) {

            TxnType type = txnTypeRepository.findByName(request.getType());

            if (type == null) {
                throw new RuntimeException("Invalid transaction type: " + request.getType());
            }

            existing.setType(type);
        }

        Transaction saved = repository.save(existing);

        return mapToResponse(saved);
    }

    public List<TransactionResponse> searchByCategory(String category) {

        User user = getLoggedInUser();

        return repository.findByUserAndCategoryIgnoreCase(user, category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TransactionResponse> getRecentTransactions() {

        User user = getLoggedInUser();

        return repository.findTop5ByUserOrderByDateDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TransactionResponse> filterTransactions(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String category
    ) {
        return repository
                .findByUserIdAndDateBetweenAndCategory(userId, startDate, endDate, category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public TransactionResponse getTransactionById(Long id) {

        User user = getLoggedInUser();

        Transaction transaction = repository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return mapToResponse(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {

        TransactionResponse response = new TransactionResponse();

        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setCategory(transaction.getCategory());
        response.setDescription(transaction.getDescription());
        response.setDate(transaction.getDate());

        if (transaction.getType() != null) {
            response.setType(transaction.getType().getName());
        }

        response.setCreatedAt(transaction.getCreatedAt());

        return response;
    }
}