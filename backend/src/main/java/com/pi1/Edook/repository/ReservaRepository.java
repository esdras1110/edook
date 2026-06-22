package com.pi1.Edook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi1.Edook.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

}