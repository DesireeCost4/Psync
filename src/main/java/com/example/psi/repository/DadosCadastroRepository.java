package com.example.psi.repository;

import com.example.psi.model.DadosCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DadosCadastroRepository extends JpaRepository<DadosCadastro, Long> {
}