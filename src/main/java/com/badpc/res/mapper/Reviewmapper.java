package com.badpc.res.mapper;

import com.badpc.res.domain.Review;
import com.badpc.res.dto.ReviewCreateDto;
import com.badpc.res.dto.ReviewDto;
import com.badpc.res.repository.HotelRepository;
import org.springframework.stereotype.Component;

@Component
public class Reviewmapper {
    private final HotelRepository hotelRepository;

    public Reviewmapper(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public Review reviewCreateDtoToReview(ReviewCreateDto reviewCreateDto){
        Review review= new Review();
        review.setKomentar(reviewCreateDto.getKomentar());
        review.setOcena(reviewCreateDto.getOcena());
        review.setHotel(hotelRepository.getById(reviewCreateDto.getHotelId()));
        return review;
    }

    public ReviewDto reviewToReviewDto(Review review){
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setKomentar(review.getKomentar());
        reviewDto.setOcena(review.getOcena());
        reviewDto.setImeHotela(review.getHotel().getName());
        return reviewDto;
    }

}
