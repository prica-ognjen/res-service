package com.badpc.res.repository;

import com.badpc.res.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("select t from RoomType t where t.name = ?1")
    List<RoomType> findByName(String typeName);
}
