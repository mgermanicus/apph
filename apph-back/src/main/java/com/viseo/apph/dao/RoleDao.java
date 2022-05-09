package com.viseo.apph.dao;

import com.viseo.apph.domain.ERole;
import com.viseo.apph.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleDao extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}