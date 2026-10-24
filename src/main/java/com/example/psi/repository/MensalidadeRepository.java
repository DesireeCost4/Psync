package com.example.psi.repository;

import com.example.psi.model.Mensalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    List<Mensalidade> findByClienteAdmId(Long clienteAdmId);
    List<Mensalidade> findByClienteFinalId(Long clienteFinalId);
}