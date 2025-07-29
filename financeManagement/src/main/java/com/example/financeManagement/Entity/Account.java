package com.example.financeManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Account {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private AccountLevel type;

    private double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setType(AccountLevel type) { this.type = type; }

    public void setBalance(double balance) { this.balance = balance; }

    public void setUser(User user) { this.user = user; }
}
