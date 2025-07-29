package com.example.financeManagement.Repository;

import com.example.financeManagement.Entity.Financial;
import com.example.financeManagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FinancialRepository extends JpaRepository<Financial, Long> {
    List<Financial> findAllByUser(User user);
}
