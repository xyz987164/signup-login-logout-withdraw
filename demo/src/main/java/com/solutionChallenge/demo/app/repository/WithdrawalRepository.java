package com.solutionChallenge.demo.app.repository;

import com.solutionChallenge.demo.app.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, Long> {
}
