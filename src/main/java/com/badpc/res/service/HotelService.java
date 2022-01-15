package com.badpc.res.service;

import com.badpc.res.domain.Hotel;
import com.badpc.res.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface HotelService {

    void updateHotel(Long hotelId, String imeHotela, String opisHotela, List<RoomTypeCreateDto> roomTypes);

    ResponseEntity<Hotel> save(HotelCreateDto hotelCreateDto, String authorization);

    Hotel getHotel(Long hotelId);

    ResponseEntity<List<FilterResponseDto>> getHotels(FilterDto filterDto);

    ResponseEntity<List<HotelDto>> getTopHotels();
}
