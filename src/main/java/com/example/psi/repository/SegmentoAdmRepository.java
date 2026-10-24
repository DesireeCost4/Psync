package com.example.psi.repository;

import com.example.psi.model.SegmentoAdm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentoAdmRepository extends JpaRepository<SegmentoAdm, Long> {
}