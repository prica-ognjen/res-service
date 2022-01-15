package com.badpc.res.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String desc;
    private Long roomNum;
    private String city;
    @OneToMany(mappedBy = "hotel",  cascade=CascadeType.ALL)
    private List<HotelRoomType> hotels;
    @OneToMany(mappedBy = "hotel",  cascade=CascadeType.ALL)
    private List<Reservation> reservations;


    public Hotel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(Long roomNum) {
        this.roomNum = roomNum;
    }

    public List<HotelRoomType> getHotels() {
        return hotels;
    }

    public void setHotels(List<HotelRoomType> hotels) {
        this.hotels = hotels;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
