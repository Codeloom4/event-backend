package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Grouping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupingRepository extends JpaRepository<Grouping, Long> {
    List<Grouping> findByUsername(String username);
}