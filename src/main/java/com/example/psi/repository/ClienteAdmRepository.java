package com.example.psi.repository;

import com.example.psi.model.ClienteAdm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteAdmRepository extends JpaRepository<ClienteAdm, Long> {

    Optional<ClienteAdm> findByEmail(String email);
}