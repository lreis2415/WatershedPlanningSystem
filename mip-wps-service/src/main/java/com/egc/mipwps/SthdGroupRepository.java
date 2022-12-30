package com.egc.mipwps;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface SthdGroupRepository extends JpaRepository<SthdGroup, Integer> {
    List<SthdGroup> findAll();
    SthdGroup findByName(String name);
}
