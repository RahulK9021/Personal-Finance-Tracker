package com.finance.service;

import com.finance.entity.Transaction;
import com.finance.entity.User;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class DashboardService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;

    public DashboardService(TransactionRepository repository,
                            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Map<String, Double> getCalculation() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = repository.findByUser(user);

        double income = 0;
        double expense = 0;

        for (Transaction t : transactions) {

            double amt;
            if (t.getAmount() == null) {
                amt = 0.0;
            } else {
                amt = t.getAmount();
            }

            if ("INCOME".equalsIgnoreCase(t.getType())) {
                income += amt;
            }
            else if ("EXPENSE".equalsIgnoreCase(t.getType())) {
                expense += amt;
            }
        }

        double balance = income - expense;

        Map<String , Double> calculation = new HashMap<>();

        calculation.put("totalIncome", income);
        calculation.put("totalExpense", expense);
        calculation.put("balance", balance);

        return calculation;
    }
}