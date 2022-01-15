package com.badpc.res.dto;

import com.badpc.res.domain.Hotel;
import com.badpc.res.domain.RoomType;

import java.util.Date;

public class ReservationCreateDto {

    private Long hotelId;
    private Date start;
    private Date end;
    private String roomType;

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getRoomType() {
        return roomType;
    }
}
