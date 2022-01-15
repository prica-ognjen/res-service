package com.badpc.res.dto;

import java.math.BigDecimal;

public class RoomTypeCreateDto {

    private String name;
    private BigDecimal rate;
    private int odRoom;
    private int doRoom;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public int getOdRoom() {
        return odRoom;
    }

    public void setOdRoom(int odRoom) {
        this.odRoom = odRoom;
    }

    public int getDoRoom() {
        return doRoom;
    }

    public void setDoRoom(int doRoom) {
        this.doRoom = doRoom;
    }
}
