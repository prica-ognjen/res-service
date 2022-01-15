package com.badpc.res.repository;

import com.badpc.res.domain.HotelRoomType;
import com.badpc.res.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface HotelRoomTypesRepository extends JpaRepository<HotelRoomType, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM HotelRoomType where hotel_id = ?1")
    void deleteByHotelId(Long id);

    @Query("SELECT roomType FROM HotelRoomType WHERE hotel_id=?1")
    List<RoomType> getRoomTypeByHotelId(Long id);

    @Query("SELECT roomType FROM HotelRoomType hrt WHERE hotel_id=?1 and name = ?2")
    List<RoomType> getRoomTypeByHotelIdAndTypeName(Long hotelId, String typeName);
}
