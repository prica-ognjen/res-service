package com.badpc.res.repository;

import com.badpc.res.domain.Reservation;
import com.badpc.res.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r where r.hotel.name = ?1")
    List<Review> findAllByHotel(String id);

    @Query("select r from Review r where r.hotel.city = ?1")
    List<Review> findAllByCity(String grad);
}
