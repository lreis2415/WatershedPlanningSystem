package com.egc.demo;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface StakeholderRepository extends JpaRepository<Stakeholder, Integer> {
    Stakeholder findUserByEmail(String email);
    Stakeholder findById(int userId);
}
