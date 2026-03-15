package com.finance.controller;

import com.finance.dto.TransactionRequest;
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
    public Transaction createTransaction(@RequestBody TransactionRequest request){
        return service.createTransaction(request);
    }
    @GetMapping
    public List<Transaction> getAllTransaction() {
        return service.getAllTransaction();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Transaction is deleted ";
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        return service.updateTransaction(id, transaction);
    }

    @GetMapping("/search")
    public List<Transaction> searchByCategory(@RequestParam String category) {
        return service.searchByCategory(category);
    }

    @GetMapping("/recent")
    public List<Transaction> recentTransaction() {
        return service.getRecentTransactions();
    }

    @GetMapping("/filter")
    public List<Transaction> filterTransactions(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String category
    ) {

        User user = getLoggedInUser();

        return repository.findByUserIdAndDateBetweenAndCategory(
                user.getId(),
                startDate,
                endDate,
                category
        );
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
