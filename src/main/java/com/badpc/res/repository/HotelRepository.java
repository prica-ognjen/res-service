package com.badpc.res.repository;

import com.badpc.res.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("Select h from Hotel h where h.name = ?1")
    Hotel findByName(String name);

    @Query("Select h from Hotel h where h.id = ?1")
    Hotel getById(Long hotelId);

    @Query("Select h from Hotel h where h.city = ?1")
    List<Hotel> findByCity(String city);
}
