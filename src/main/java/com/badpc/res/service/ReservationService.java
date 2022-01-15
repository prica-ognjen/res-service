package com.badpc.res.service;

import com.badpc.res.dto.ReservationCreateDto;
import com.badpc.res.dto.ReservationResponseDto;
import org.springframework.http.ResponseEntity;

public interface ReservationService {

    ResponseEntity<ReservationResponseDto> save(Long userId, ReservationCreateDto reservationCreateDto, String authorization);

    ResponseEntity<Void> cancelResM(Long l, Long resId, String jwt);

    ResponseEntity<Void> cancelResC(Long l, Long resId, String jwt);
}
