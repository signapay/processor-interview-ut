package com.transactionprocessor.repository;


import com.transactionprocessor.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface TransactionRepository extends JpaRepository<Transaction, Long> {



}
