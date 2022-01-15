package com.badpc.res.service.impl;

import com.badpc.res.domain.Review;
import com.badpc.res.dto.ReviewCreateDto;
import com.badpc.res.dto.ReviewDto;
import com.badpc.res.dto.ReviewFilterDto;
import com.badpc.res.dto.ReviewUpdateDto;
import com.badpc.res.mapper.Reviewmapper;
import com.badpc.res.repository.HotelRepository;
import com.badpc.res.repository.ReviewRepository;
import com.badpc.res.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final Reviewmapper reviewmapper;
    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;

    public ReviewServiceImpl(Reviewmapper reviewmapper, ReviewRepository reviewRepository, HotelRepository hotelRepository) {
        this.reviewmapper = reviewmapper;
        this.reviewRepository = reviewRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public ResponseEntity<ReviewCreateDto> save(Long l, ReviewCreateDto reviewCreateDto) {
        //System.out.println("lololo");
        Review review = reviewmapper.reviewCreateDtoToReview(reviewCreateDto);

        review.setUserId(l);

        reviewRepository.save(review);

        return new ResponseEntity<ReviewCreateDto>(reviewCreateDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> delete(Long l, Long id) {
        Review review = reviewRepository.getOne(id);
        if(!l.equals(review.getUserId())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        reviewRepository.delete(review);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> update(Long l, ReviewUpdateDto reviewUpdateDto, Long reviewId) {
        //System.out.println("1111111111111");
        Review review = reviewRepository.getOne(reviewId);
        //System.out.println("22222222222222");
        if(reviewUpdateDto.getKomentar() != ""){
            //System.out.println("33333333333333333");
            review.setKomentar(reviewUpdateDto.getKomentar());
        }
        if(reviewUpdateDto.getOcena() != null){
            if(reviewUpdateDto.getOcena() > 5 || reviewUpdateDto.getOcena() < 1){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            review.setOcena(reviewUpdateDto.getOcena());
        }
        if(!l.equals(review.getUserId())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReviewDto>> getAll(ReviewFilterDto reviewFilterDto) {
        List<ReviewDto> reviewDtoList = new ArrayList<>();
        List<Review> reviews = new ArrayList<>();

        if(!reviewFilterDto.getGrad().equals("")){
            reviews = reviewRepository.findAllByCity(reviewFilterDto.getGrad());
            if(reviews.isEmpty()){
                return new ResponseEntity<>(reviewDtoList, HttpStatus.BAD_REQUEST);
            }
            for(Review r : reviews){
                ReviewDto temp= reviewmapper.reviewToReviewDto(r);
                reviewDtoList.add(temp);
            }
        }

        if(!reviewFilterDto.getHotel().equals("")){

            if(reviews.isEmpty()){
                reviews = reviewRepository.findAllByHotel(reviewFilterDto.getHotel());
            }else{
                reviews.removeIf(review -> !review.getHotel().getName().equals(reviewFilterDto.getHotel()));

                if(reviews.isEmpty()){
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            for(Review r : reviews){
                ReviewDto temp= reviewmapper.reviewToReviewDto(r);
                reviewDtoList.add(temp);
            }
        }
        return new ResponseEntity<>(reviewDtoList, HttpStatus.OK);
    }
}
