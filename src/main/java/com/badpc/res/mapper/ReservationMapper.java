package com.badpc.res.mapper;

import com.badpc.res.domain.Reservation;
import com.badpc.res.domain.RoomType;
import com.badpc.res.dto.ReservationCreateDto;
import com.badpc.res.repository.HotelRepository;
import com.badpc.res.repository.HotelRoomTypesRepository;
import com.badpc.res.repository.RoomTypeRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationMapper {

    private final HotelRoomTypesRepository hotelRoomTypeRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;

    public ReservationMapper(HotelRoomTypesRepository hotelRoomTypeRepository, RoomTypeRepository roomTypeRepository, HotelRepository hotelRepository) {
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.hotelRepository = hotelRepository;
    }

    public Reservation reservationCreateDtoToReservation(ReservationCreateDto reservationCreateDto){
        Reservation reservation = new Reservation();

        reservation.setStart(reservationCreateDto.getStart());
        reservation.setEnd(reservationCreateDto.getEnd());

        Long hotelId = reservationCreateDto.getHotelId();
        reservation.setHotel(hotelRepository.getById(hotelId));
        //room types with same hotel id and type name
        String typeName = reservationCreateDto.getRoomType();
        List<RoomType> t2 = hotelRoomTypeRepository.getRoomTypeByHotelIdAndTypeName(hotelId, typeName);
        RoomType t = t2.get(0);
        reservation.setPrice(t.getRate());
        reservation.setRoomType(t);

        return reservation;
    }


}
