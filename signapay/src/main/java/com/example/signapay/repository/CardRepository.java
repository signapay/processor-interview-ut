package com.example.signapay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.signapay.entity.Card;
import java.util.List;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
	List<Card> findByCardNumber(String cardNumber);
}
