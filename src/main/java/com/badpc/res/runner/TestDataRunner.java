package com.badpc.res.runner;


import com.badpc.res.domain.Hotel;
import com.badpc.res.domain.HotelRoomType;
import com.badpc.res.domain.RoomType;
import com.badpc.res.repository.HotelRepository;
import com.badpc.res.repository.HotelRoomTypesRepository;
import com.badpc.res.repository.RoomTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Profile({"default"})
@Component
public class TestDataRunner implements CommandLineRunner {

    private HotelRepository hotelRepository;
    private RoomTypeRepository roomTypeRepository;
    private final HotelRoomTypesRepository hotelRoomTypesRepository;

    public TestDataRunner(HotelRepository hotelRepository, RoomTypeRepository roomTypeRepository, HotelRoomTypesRepository hotelRoomTypesRepository) {
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.hotelRoomTypesRepository = hotelRoomTypesRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Hotel hotel1 = new Hotel();
        hotel1.setName("hotel1");
        hotel1.setCity("Novi Sad");
        hotelRepository.save(hotel1);
        Hotel hotel2 = new Hotel();
        hotel2.setName("hotel2");
        hotel2.setCity("Nis");
        hotelRepository.save(hotel2);
        Hotel hotel3 = new Hotel();
        hotel3.setName("hotel3");
        hotel3.setCity("Nis");
        hotelRepository.save(hotel3);
        Hotel hotel4 = new Hotel();
        hotel4.setName("hotel4");
        hotel4.setCity("Beograd");
        hotelRepository.save(hotel4);
        Hotel hotel5 = new Hotel();
        hotel5.setName("hotel5");
        hotel5.setCity("Pancevo");
        hotelRepository.save(hotel5);

        RoomType tip1 = new RoomType();
        tip1.setName("tip1");
        tip1.setOdRoom(1);
        tip1.setDoRoom(5);
        tip1.setRate(new BigDecimal(50));
        tip1.setFreeRooms(tip1.getDoRoom() - tip1.getOdRoom());

        roomTypeRepository.save(tip1);

        RoomType tip2 = new RoomType();
        tip2.setName("tip2");
        tip2.setOdRoom(5);
        tip2.setDoRoom(10);
        tip2.setRate(new BigDecimal(100));
        tip2.setFreeRooms(tip2.getDoRoom() - tip2.getOdRoom());

        roomTypeRepository.save(tip2);

        HotelRoomType hrt1 = new HotelRoomType();
        hrt1.setHotel(hotel1);
        hrt1.setRoomType(tip1);
        hotelRoomTypesRepository.save(hrt1);

        HotelRoomType hrt2 = new HotelRoomType();
        hrt2.setHotel(hotel1);
        hrt2.setRoomType(tip2);
        hotelRoomTypesRepository.save(hrt2);


        RoomType tip3 = new RoomType();
        tip3.setName("tip1");
        tip3.setOdRoom(1);
        tip3.setDoRoom(5);
        tip3.setRate(new BigDecimal(50));
        tip3.setFreeRooms(tip3.getDoRoom() - tip3.getOdRoom());

        roomTypeRepository.save(tip3);

        RoomType tip4 = new RoomType();
        tip4.setName("tip2");
        tip4.setOdRoom(5);
        tip4.setDoRoom(10);
        tip4.setRate(new BigDecimal(100));
        tip4.setFreeRooms(tip4.getDoRoom() - tip4.getOdRoom());

        roomTypeRepository.save(tip4);

        HotelRoomType hrt3 = new HotelRoomType();
        hrt3.setHotel(hotel2);
        hrt3.setRoomType(tip3);
        hotelRoomTypesRepository.save(hrt3);

        HotelRoomType hrt4 = new HotelRoomType();
        hrt4.setHotel(hotel2);
        hrt4.setRoomType(tip4);
        hotelRoomTypesRepository.save(hrt4);

    }
}
