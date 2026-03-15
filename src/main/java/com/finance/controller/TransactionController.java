package com.finance.controller;

import com.finance.dto.TransactionRequest;
import com.finance.dto.TransactionResponse;
import com.finance.entity.Transaction;
import com.finance.entity.User;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import com.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;
    private final TransactionRepository repository;
    private final UserRepository userRepository;

    @PostMapping
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        return service.createTransaction(request);
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        return service.getAllTransaction();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Transaction is deleted ";
    }

    @PutMapping("/{id}")
    public TransactionResponse updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionRequest request
    ) {
        return service.updateTransaction(id, request);
    }

    @GetMapping("/search")
    public List<TransactionResponse> searchByCategory(@RequestParam String category) {
        return service.searchByCategory(category);
    }

    @GetMapping("/{id}")
    public TransactionResponse getTransactionById(@PathVariable Long id) {
        return service.getTransactionById(id);
    }

    @GetMapping("/recent")
    public List<TransactionResponse> getRecentTransactions() {
        return service.getRecentTransactions();
    }

    @GetMapping("/filter")
    public List<TransactionResponse> filterTransactions(
            @RequestParam Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String category
    ) {
        return service.filterTransactions(userId, startDate, endDate, category);
    }

    private User getLoggedInUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
