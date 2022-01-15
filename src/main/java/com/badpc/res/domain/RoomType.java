package com.badpc.res.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name="room_types")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal rate;
    private int odRoom;
    private int doRoom;
    @OneToMany(mappedBy = "roomType", cascade=CascadeType.ALL)
    private List<HotelRoomType> roomTypes;
    private int freeRooms;

    public RoomType() {
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

    public List<HotelRoomType> getRoomTypes() {
        return roomTypes;
    }

    public void setRoomTypes(List<HotelRoomType> roomTypes) {
        this.roomTypes = roomTypes;
    }

    public int getFreeRooms() {
        return freeRooms;
    }

    public void setFreeRooms(int freeRooms) {
        this.freeRooms = freeRooms;
    }
}
