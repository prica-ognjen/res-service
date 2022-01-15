package com.badpc.res.controller;

import com.badpc.res.domain.Hotel;
import com.badpc.res.domain.RoomType;
import com.badpc.res.dto.*;
import com.badpc.res.security.CheckSecurity;
import com.badpc.res.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @CheckSecurity(roles = {"ROLE_MANAGER"})
    @PutMapping(path="{hotelId}")
    public void updateHotel(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("hotelId") Long hotelId,
            String name,
            String desc,
            @RequestBody List<RoomTypeCreateDto> roomTypeCreateDto){
        hotelService.updateHotel(hotelId, name, desc, roomTypeCreateDto);
    }

    @CheckSecurity(roles = {"ROLE_MANAGER"})
    @GetMapping(path="{hotelId}")
    public Hotel getHotel(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("hotelId") Long hotelId){
        return hotelService.getHotel(hotelId);
    }

    @CheckSecurity(roles = {"ROLE_USER", "ROLE_MANAGER"})
    @PostMapping(path = "/freeRooms")
    public ResponseEntity<List<FilterResponseDto>> getFreeRooms(
            @RequestHeader("Authorization") String authorization,
            @RequestBody FilterDto filterDto){
        return hotelService.getHotels(filterDto);
    }

    @CheckSecurity(roles = {"ROLE_MANAGER"})
    @PostMapping
    ResponseEntity<Hotel> createNewHotel(
            @RequestHeader("Authorization") String authorization,
            @RequestBody HotelCreateDto hotelCreateDto) {
        return hotelService.save(hotelCreateDto, authorization);
    }

    @CheckSecurity(roles = {"ROLE_USER", "ROLE_MANAGER"})
    @GetMapping(path="/topHotels")
    public ResponseEntity<List<HotelDto>> getTopHotels(
            @RequestHeader("Authorization") String authorization){
        return hotelService.getTopHotels();
    }
}
