package com.badpc.res.controller;

import com.badpc.res.dto.ReservationCreateDto;
import com.badpc.res.dto.ReservationResponseDto;
import com.badpc.res.security.CheckSecurity;
import com.badpc.res.security.service.TokenService;
import com.badpc.res.service.ReservationService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("{id}/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final TokenService tokenService;

    public ReservationController(ReservationService reservationService, TokenService tokenService) {
        this.reservationService = reservationService;
        this.tokenService = tokenService;
    }

    @CheckSecurity(roles = {"ROLE_USER"})
    @PostMapping
    public ResponseEntity<ReservationResponseDto> addReservation(@RequestHeader("Authorization") String authorization,
                                                                 @RequestBody ReservationCreateDto reservationCreateDto) {
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);

        Long l = new Long((Integer)claims.get("id"));

        return reservationService.save(l, reservationCreateDto, authorization);
    }

    @CheckSecurity(roles = {"ROLE_MANAGER"})
    @DeleteMapping("/cancelReservationManager/{resId}")
    public ResponseEntity<Void> cancelReservationM(@RequestHeader("Authorization") String authorization, @PathVariable("resId") Long resId) {
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);

        Long l = new Long((Integer)claims.get("id"));

        return reservationService.cancelResM(l, resId, authorization);
    }


    @CheckSecurity(roles = {"ROLE_USER"})
    @DeleteMapping("/cancelReservationUser/{resId}")
    public ResponseEntity<Void> cancelReservationC(@RequestHeader("Authorization") String authorization, @PathVariable("resId") Long resId) {
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);

        Long l = new Long((Integer)claims.get("id"));

        return reservationService.cancelResC(l, resId, authorization);
    }
}
