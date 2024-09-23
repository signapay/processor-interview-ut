package com.example.signapay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.signapay.entity.Account;
import java.util.List;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findByNameIgnoreCase(String name);
	@Query("SELECT DISTINCT a FROM Account a JOIN FETCH a.cards c WHERE c.balance < 0")
	List<Account> findAccountsForCollections();
}
