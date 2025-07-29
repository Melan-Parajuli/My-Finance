package com.example.financeManagement.Repository;

import com.example.financeManagement.Entity.Transaction;
import com.example.financeManagement.Entity.TransactionCategory;
import com.example.financeManagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByUser(User user);

    List<Transaction> findAllByUserAndType(User user, TransactionCategory catrgory);
}
