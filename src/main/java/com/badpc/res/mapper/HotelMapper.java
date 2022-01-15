package com.badpc.res.mapper;

import com.badpc.res.domain.Hotel;
import com.badpc.res.dto.HotelCreateDto;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper {

    public Hotel hotelCreateDtoToHotel(HotelCreateDto hotelCreateDto){
        Hotel hotel = new Hotel();
        hotel.setName(hotelCreateDto.getName());
        return hotel;
    }

}
