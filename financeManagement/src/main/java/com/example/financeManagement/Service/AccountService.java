package com.example.financeManagement.Service;

import com.example.financeManagement.Entity.Account;
import com.example.financeManagement.Entity.User;
import com.example.financeManagement.Repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllByUser(User user) {
        return accountRepository.findAllByUser(user);
    }

    public Account getByIdAndUser(Long id, User user) {
        Optional<Account> account = accountRepository.findById(id);
        return account.filter(a -> a.getUser().getId().equals(user.getId())).orElse(null);
    }

    public Account create(Account account, User user) {
        account.setUser(user);
        return accountRepository.save(account);
    }

    public boolean deleteByIdAndUser(Long id, User user) {
        Account account = getByIdAndUser(id, user);
        if (account != null) {
            accountRepository.delete(account);
            return true;
        }
        return false;
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }
}
