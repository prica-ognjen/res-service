package com.badpc.res.repository;

import com.badpc.res.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r where r.hotel.id = ?1")
    List<Reservation> findAllByHotel(Long id);

}
