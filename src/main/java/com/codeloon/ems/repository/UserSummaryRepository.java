package com.codeloon.ems.repository;

import com.codeloon.ems.dto.UserSummaryDto;
import com.codeloon.ems.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSummaryRepository extends JpaRepository<Role, Long> {

    @Query(nativeQuery = true, value =
            "SELECT r.name as role, COUNT(ur.user_id) as count " +
                    "FROM roles r " +
                    "LEFT JOIN user_roles ur ON r.id = ur.role_id " +
                    "GROUP BY r.name")
    List<UserSummaryDto> getUserSummary();
}

