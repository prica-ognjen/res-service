package com.badpc.res.userservice;

public class ManagerDto {
    private String hotel;

    public ManagerDto(String hotel) {
        this.hotel = hotel;
    }

    public ManagerDto() {
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }
}