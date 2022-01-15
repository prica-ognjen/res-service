package com.badpc.res.service;

import com.badpc.res.dto.ReviewCreateDto;
import com.badpc.res.dto.ReviewDto;
import com.badpc.res.dto.ReviewFilterDto;
import com.badpc.res.dto.ReviewUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


public interface ReviewService {

    ResponseEntity<ReviewCreateDto> save(Long l, ReviewCreateDto reviewCreateDto);

    ResponseEntity<Void> delete(Long l, Long id);

    ResponseEntity<Void> update(Long l, ReviewUpdateDto reviewUpdateDto, Long reviewId);

    ResponseEntity<List<ReviewDto>> getAll(ReviewFilterDto reviewFilterDto);
}
