package com.badpc.res.mapper;

import com.badpc.res.domain.RoomType;
import com.badpc.res.dto.RoomTypeCreateDto;
import org.springframework.stereotype.Component;

@Component
public class RoomTypeMapper {


    public RoomType roomTypeCreateDtoToRoomType(RoomTypeCreateDto roomTypeCreateDto){
        RoomType roomType = new RoomType();
        roomType.setOdRoom(roomTypeCreateDto.getOdRoom());
        roomType.setDoRoom(roomTypeCreateDto.getDoRoom());
        roomType.setName(roomTypeCreateDto.getName());
        roomType.setRate(roomTypeCreateDto.getRate());
        roomType.setFreeRooms(roomType.getDoRoom() - roomType.getOdRoom());
        return roomType;
    }

}
