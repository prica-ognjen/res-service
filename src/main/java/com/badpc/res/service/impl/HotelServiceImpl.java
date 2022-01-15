package com.badpc.res.service.impl;

import com.badpc.res.domain.*;
import com.badpc.res.dto.*;
import com.badpc.res.mapper.HotelMapper;
import com.badpc.res.mapper.RoomTypeMapper;
import com.badpc.res.repository.*;
import com.badpc.res.service.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final RestTemplate userServiceRestTemplate;
    private final HotelRoomTypesRepository hotelRoomTypesRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    private final HotelMapper hotelMapper;
    private final RoomTypeMapper roomTypeMapper;

    public HotelServiceImpl(HotelRepository hotelRepository, HotelMapper hotelMapper, RestTemplate userServiceRestTemplate, HotelRoomTypesRepository hotelRoomTypesRepository, RoomTypeRepository roomTypeRepository, ReservationRepository reservationRepository, ReviewRepository reviewRepository, RoomTypeMapper roomTypeMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelMapper = hotelMapper;
        this.userServiceRestTemplate = userServiceRestTemplate;
        this.hotelRoomTypesRepository = hotelRoomTypesRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.reservationRepository = reservationRepository;
        this.reviewRepository = reviewRepository;
        this.roomTypeMapper = roomTypeMapper;
    }

    @Override
    public void updateHotel(Long id, String name, String desc, List<RoomTypeCreateDto> roomTypes) {
        Hotel hotel = getHotel(id);

        if(name != null){
            hotel.setName(name);
        }
        if(desc != null){
            hotel.setDesc(desc);
        }
        if(roomTypes != null){

            List<RoomType> roomTypesToDelete = hotelRoomTypesRepository.getRoomTypeByHotelId(id);

            hotelRoomTypesRepository.deleteByHotelId(id);

            for(RoomType roomTypeId: roomTypesToDelete){
                roomTypeRepository.delete(roomTypeId);
            }

            for(RoomTypeCreateDto roomType: roomTypes){

                RoomType rt = roomTypeMapper.roomTypeCreateDtoToRoomType(roomType);
                roomTypeRepository.save(rt);

                HotelRoomType hotelRoomType = new HotelRoomType();
                hotelRoomType.setHotel(hotel);
                hotelRoomType.setRoomType(rt);
                hotelRoomTypesRepository.save(hotelRoomType);
            }
        }
    }

    @Override
    public ResponseEntity<Hotel> save(HotelCreateDto hotelCreateDto, String jwt) {
        if(hotelCreateDto == null || hotelCreateDto.getName() == null || hotelCreateDto.getName().equals("")){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(hotelRepository.findByName(hotelCreateDto.getName()) != null){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Hotel hotel = hotelMapper.hotelCreateDtoToHotel(hotelCreateDto);

        return new ResponseEntity<>(hotelRepository.save(hotel), HttpStatus.CREATED);
    }

    @Override
    public Hotel getHotel(Long hotelId) {
        if(hotelId == null){
            throw new NullPointerException("Hotel id can't be null");
        }
        return hotelRepository.getById(hotelId);
    }

    @Override
    public ResponseEntity<List<FilterResponseDto>> getHotels(FilterDto filterDto) {
        List<FilterResponseDto> filterResponseDtos = new ArrayList<>();
        String city = filterDto.getCity();
        String hotel = filterDto.getHotel();
        String sortType = filterDto.getSortType();
        Date start = filterDto.getStart();
        Date end = filterDto.getEnd();

        List<Hotel> hotels = hotelRepository.findAll();
        if((end != null && start == null) || (end == null && start != null)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        assert end != null;
        if(end.compareTo(start) <= 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(city !=null && !city.equals("")){
            hotels.clear();
            if(hotelRepository.findByCity(city) == null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            hotels = hotelRepository.findByCity(city);
        }
        if(hotel != null && !hotel.equals("")){
            hotels.clear();
            if(hotelRepository.findByName(hotel) == null){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            hotels.add(hotelRepository.findByName(hotel));
        }

        for(Hotel h : hotels){
            System.out.println(h.getId());
            List<Reservation> reservations = reservationRepository.findAllByHotel(h.getId());

            FilterResponseDto f = new FilterResponseDto();
            f.setHotelId(h.getId());

            List<RoomType> roomTypes = hotelRoomTypesRepository.getRoomTypeByHotelId(h.getId());

            for(RoomType r : roomTypes){
                f.getTypeMap().put(r.getName(), r.getFreeRooms());
            }

            for(Reservation r : reservations){
                //startDate1 <= endDate2 && startDate2 <= endDate1
                if(((start.compareTo(r.getEnd()) <= 0) && (r.getStart().compareTo(end)) <= 0)){
                    if(f.getTypeMap().containsKey(r.getRoomType().getName())) {
                        f.getTypeMap().put(r.getRoomType().getName(), f.getTypeMap().get(r.getRoomType().getName()) - 1);
                    }
                }
            }
            Set<Map.Entry<String, Integer>> setOfEntries = f.getTypeMap().entrySet();


            Iterator<Map.Entry<String, Integer>> iterator
                    = setOfEntries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                Integer value = entry.getValue();

                if (value < 1) {
                    iterator.remove();
                }

            }
            //System.out.println(h.getId() +   "pt2");
            if(!f.getTypeMap().isEmpty()){
                System.out.println(h.getId() +   "pt2");
                filterResponseDtos.add(f);
            }

        }
        for(FilterResponseDto filterResponseDto: filterResponseDtos){
            System.out.println(filterResponseDto+ "DTODOT\n");
        }
        return new ResponseEntity<>(filterResponseDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<HotelDto>> getTopHotels() {

        List<Review> reviews = reviewRepository.findAll();

        Map<Long, List<Integer>> ocene = new HashMap<>();

        for(Review r: reviews){
            if(!ocene.containsKey(r.getHotel().getId())){
                List<Integer> tempList = new ArrayList<>();
                tempList.add(0,1);
                tempList.add(1,r.getOcena());
                ocene.put(r.getHotel().getId(), tempList);
                continue;
            }

            List<Integer> list = ocene.get(r.getHotel().getId());

            ocene.get(r.getHotel().getId()).set(0, list.get(0)+1);
            ocene.get(r.getHotel().getId()).set(1, list.get(1)+r.getOcena());
        }

        List<HotelDto> hotelDtos = new ArrayList<>();

        for(Map.Entry<Long,List<Integer>> entry: ocene.entrySet()){
            Double prosek = 1.0 * entry.getValue().get(1) / entry.getValue().get(0);
            String name = hotelRepository.getById(entry.getKey()).getName();

            HotelDto hotelDto = new HotelDto();
            hotelDto.setRating(prosek);
            hotelDto.setName(name);

            hotelDtos.add(hotelDto);
        }

        hotelDtos.sort((o1, o2) -> {
            if(o1.getRating()<o2.getRating())
                return 1;
            else if(Objects.equals(o1.getRating(), o2.getRating()))
                return 0;
            else
                return -1;
        });

        if(hotelDtos.size() > 10){
            hotelDtos.subList(10, hotelDtos.size()).clear();
        }

        return new ResponseEntity<>(hotelDtos, HttpStatus.OK);
    }
}
