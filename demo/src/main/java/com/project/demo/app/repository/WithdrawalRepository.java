package com.project.demo.app.repository;

import com.project.demo.app.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, Long> {
}
