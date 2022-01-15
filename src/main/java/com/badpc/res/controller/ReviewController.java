package com.badpc.res.controller;

import com.badpc.res.dto.ReviewCreateDto;
import com.badpc.res.dto.ReviewDto;
import com.badpc.res.dto.ReviewFilterDto;
import com.badpc.res.dto.ReviewUpdateDto;
import com.badpc.res.security.CheckSecurity;
import com.badpc.res.security.service.TokenService;
import com.badpc.res.service.ReservationService;
import com.badpc.res.service.ReviewService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReservationService reservationService;
    private final RestTemplate userServiceRestTemplate;
    private final ReviewService reviewService;
    private final TokenService tokenService;

    public ReviewController(ReservationService reservationService, RestTemplate userServiceRestTemplate, ReviewService reviewService, TokenService tokenService) {
        this.reservationService = reservationService;
        this.userServiceRestTemplate = userServiceRestTemplate;
        this.reviewService = reviewService;
        this.tokenService = tokenService;
    }

    @CheckSecurity(roles = {"ROLE_USER"})
    @PostMapping
    public ResponseEntity<ReviewCreateDto> addReview(@RequestHeader("Authorization") String authorization,
                                                                 @RequestBody ReviewCreateDto reviewCreateDto) {

        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);

        Long l = new Long((Integer)claims.get("id"));

        return reviewService.save(l, reviewCreateDto);
    }

    @CheckSecurity(roles = {"ROLE_USER"})
    @DeleteMapping("/reservation/{reviewId}")
    public ResponseEntity<Void> deleteReview(@RequestHeader("Authorization") String authorization,
                                               @PathVariable("id") Long reviewId) {

        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);

        Long l = new Long((Integer)claims.get("id"));

        return reviewService.delete(l,reviewId);
    }

    @CheckSecurity(roles = {"ROLE_USER"})
    @PutMapping("/reservationUpdate/{reviewId}")
    public ResponseEntity<Void> updateReservation(@RequestHeader("Authorization") String authorization,
                                                          @RequestBody ReviewUpdateDto reviewUpdateDto,@PathVariable("reviewId") Long reviewId) {

        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);

        Long l = new Long((Integer)claims.get("id"));

        return reviewService.update(l, reviewUpdateDto, reviewId);
    }

    @CheckSecurity(roles = {"ROLE_USER"})
    @PostMapping("/getReviews")
    public ResponseEntity<List<ReviewDto>> getReviews(@RequestHeader("Authorization") String authorization,
                                                     @RequestBody ReviewFilterDto reviewFilterDto) {

        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);

        Long l = new Long((Integer)claims.get("id"));

        return reviewService.getAll(reviewFilterDto);
    }

}