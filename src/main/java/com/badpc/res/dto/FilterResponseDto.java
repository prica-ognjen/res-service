package com.badpc.res.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FilterResponseDto {
    private Long hotelId;
    private Map<String,Integer> typeMap;

    public FilterResponseDto() {
        this.typeMap = new HashMap<>();
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Map<String, Integer> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<String, Integer> typeMap) {
        this.typeMap = typeMap;
    }
}
