package com.badpc.res.service.impl;

import com.badpc.res.domain.Reservation;
import com.badpc.res.dto.*;
import com.badpc.res.mapper.ReservationMapper;
import com.badpc.res.repository.ReservationRepository;
import com.badpc.res.service.ReservationService;
import com.badpc.res.userservice.CReservationDataDto;
import com.badpc.res.userservice.ManagerDto;
import com.badpc.res.userservice.RankDto;
import com.badpc.res.userservice.UserMailDto;
import javassist.NotFoundException;
import com.badpc.res.helper.MessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestTemplate userServiceRestTemplate;
    private final ReservationMapper reservationMapper;
    private final JmsTemplate jmsTemplate;
    private String sendMailOnReservationDestination;
    private String mSendResConfirmDestination;
    private String cancelReservationDestination;
    private final MessageHelper messageHelper;


    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  RestTemplate userServiceRestTemplate,
                                  ReservationMapper reservationMapper,
                                  JmsTemplate jmsTemplate,
                                  @Value("${destination.sendMailOnReservation}") String sendMailOnReservationDestination,
                                  @Value("${destination.mSendResConfirm}") String mSendResConfirmDestination,
                                  @Value("${destination.cancelReservation}") String cancelReservationDestination,
                                  MessageHelper messageHelper) {
        this.reservationRepository = reservationRepository;
        this.userServiceRestTemplate  = userServiceRestTemplate;
        this.reservationMapper = reservationMapper;
        this.jmsTemplate = jmsTemplate;
        this.sendMailOnReservationDestination = sendMailOnReservationDestination;
        this.messageHelper = messageHelper;
        this.mSendResConfirmDestination = mSendResConfirmDestination;
        this.cancelReservationDestination = cancelReservationDestination;
    }

    @Override
    public ResponseEntity<ReservationResponseDto> save(Long userId, ReservationCreateDto reservationCreateDto, String jwt) {

        int brRezervacija = 0;
        if(reservationCreateDto.getStart().compareTo(reservationCreateDto.getEnd()) >= 0){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        for(Reservation r : reservationRepository.findAllByHotel(reservationCreateDto.getHotelId())){
            //startDate1 <= endDate2 && startDate2 <= endDate1
            if(((reservationCreateDto.getStart().compareTo(r.getEnd()) <= 0) && (r.getStart().compareTo(reservationCreateDto.getEnd())) <= 0) && reservationCreateDto.getRoomType().equals(r.getRoomType().getName())){
                brRezervacija++;
            }
        }
        Reservation r = reservationMapper.reservationCreateDtoToReservation(reservationCreateDto);

        String hotelName = r.getHotel().getName();

        r.setUserId(userId);

        if(r.getRoomType().getFreeRooms() - brRezervacija < 1){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        ResponseEntity<RankDto> rankDto = null;

        try{

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);

            rankDto = userServiceRestTemplate.exchange("/ranks/discount/" + r.getUserId(), HttpMethod.GET, request,RankDto.class);
        }catch(Exception e){
            try {
                throw new NotFoundException("Nije naso 1");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }

        assert rankDto != null;
        //System.out.println((rankDto.getBody()).getPopust() + " POPUST");
        BigDecimal price = BigDecimal.valueOf(Objects.requireNonNull(rankDto.getBody()).getPopust());

        r.setPrice(r.getPrice() .subtract(r.getPrice().multiply(price)));

///DODATo**-************************************************************************
        ResponseEntity<UserMailDto> userMailDto1 = null;
        try{
           // System.out.println("PRE MANGER");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);

            userMailDto1 = userServiceRestTemplate.exchange("/service/managerEmail/" + hotelName, HttpMethod.GET, request, UserMailDto.class);
            //System.out.println(userMailDto1.getBody().getEmail() + " <-OVO JE NESTO");

        }catch(Exception e){
            try {
                throw new NotFoundException("Nije naso 2");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }
        assert userMailDto1 != null;
        System.out.println("MEJL Managera " + Objects.requireNonNull(userMailDto1.getBody()).getEmail());

       // System.out.println("POST MANAGER");
        ResponseEntity<CReservationDataDto> cReservationDataDtoResponseEntity = null;
        try{
            //System.out.println("PRE USER");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);
            cReservationDataDtoResponseEntity= userServiceRestTemplate.exchange("/service/userEmail/" + r.getUserId(), HttpMethod.GET, request, CReservationDataDto.class);

        }catch(Exception e){
            try {
                throw new NotFoundException("Nije naso 3");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }

        reservationRepository.save(r);
        //System.out.println("PRE USER");
        assert cReservationDataDtoResponseEntity != null;

        CReservationNotificationDto cReservationNotificationDto = new CReservationNotificationDto();
        cReservationNotificationDto.setEmail(Objects.requireNonNull(cReservationDataDtoResponseEntity.getBody()).getEmail());
        cReservationNotificationDto.setFirstName(cReservationDataDtoResponseEntity.getBody().getFirstName());
        cReservationNotificationDto.setLastName(cReservationDataDtoResponseEntity.getBody().getLastName());
        cReservationNotificationDto.setIdReservation(r.getId());
        cReservationNotificationDto.setHotelName(r.getHotel().getName());
        cReservationNotificationDto.setTipSobe(r.getRoomType().getName());
        cReservationNotificationDto.setCheckIn(r.getStart());
        cReservationNotificationDto.setType(NotificationType.NOTIFICATION_RESERVE_CLIENT);

        jmsTemplate.convertAndSend(sendMailOnReservationDestination, messageHelper.createMessage(cReservationNotificationDto));

        MReservationNotificationDto mReservationNotificationDto = new MReservationNotificationDto();
        mReservationNotificationDto.setEmail(Objects.requireNonNull(userMailDto1.getBody()).getEmail());
        mReservationNotificationDto.setFirstName(cReservationDataDtoResponseEntity.getBody().getFirstName());
        mReservationNotificationDto.setLastName(cReservationDataDtoResponseEntity.getBody().getLastName());
        mReservationNotificationDto.setIdReservation(r.getId());
        mReservationNotificationDto.setTipSobe(r.getRoomType().getName());
        mReservationNotificationDto.setCheckIn(r.getStart());
        mReservationNotificationDto.setType(NotificationType.NOTIFICATION_RESERVE_MANAGER);

        System.out.println(cReservationDataDtoResponseEntity);
        jmsTemplate.convertAndSend(mSendResConfirmDestination, messageHelper.createMessage(mReservationNotificationDto));



///DODATo**-************************************************************************


        ResponseEntity<RankDto> rankDto2 = null;

        try{

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);

            rankDto = userServiceRestTemplate.exchange("/service/incRez/" + r.getUserId(), HttpMethod.PUT, request,RankDto.class);
        }catch(Exception e){
            try {
                throw new NotFoundException("Nesto je trulo u dzrtavi Danskoj");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }

        ReservationResponseDto res = new ReservationResponseDto();
        res.setResId(r.getId());
        res.setStart(r.getStart());
        res.setEnd(r.getEnd());
        res.setUserId(r.getUserId());
        res.setPrice(r.getPrice());

        //jmsTemplate.convertAndSend(sendMailOnReservationDestination, messageHelper.createMessage(new SendMailReservationDto(res.getUserId(), res.getResId())));

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //Cancelovanje rezervacije sa managerove strane
    @Override
    public ResponseEntity<Void> cancelResM(Long l, Long resId, String jwt) {
        Reservation reservation = reservationRepository.getOne(resId);

        ResponseEntity<ManagerDto> responseManagerDto= null;
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);

            responseManagerDto = userServiceRestTemplate.exchange("/service/daLiJeManager/", HttpMethod.GET, request,ManagerDto.class);

        }catch(Exception e){
            try {
                throw new NotFoundException("Nesto ne moze managera da nadje");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }


        if(!reservation.getHotel().getName().equals(responseManagerDto.getBody().getHotel())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);

            userServiceRestTemplate.exchange("/service/decr/" + reservation.getUserId(), HttpMethod.PUT, request,ManagerDto.class);

        }catch(Exception e){
            try {
                throw new NotFoundException("Nesto sa dekrementacijom rezervacija usera");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }

        //ZA NOTIFIKACIJE:
        ResponseEntity<UserMailDto> userMailDto1 = null;
        try{
            // System.out.println("PRE MANGER");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);

            userMailDto1 = userServiceRestTemplate.exchange("/service/managerEmail/" + reservation.getHotel().getName(), HttpMethod.GET, request, UserMailDto.class);
            //System.out.println(userMailDto1.getBody().getEmail() + " <-OVO JE NESTO");

        }catch(Exception e){
            try {
                throw new NotFoundException("Nije naso 4");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }
        assert userMailDto1 != null;
        CancelReservationNotificationDto cancelReservationNotificationDto = new CancelReservationNotificationDto();
        cancelReservationNotificationDto.setIdReservation(reservation.getId());
        cancelReservationNotificationDto.setEmail(userMailDto1.getBody().getEmail());
        jmsTemplate.convertAndSend(cancelReservationDestination, messageHelper.createMessage(cancelReservationNotificationDto));

        ResponseEntity<CReservationDataDto> cReservationDataDtoResponseEntity = null;
        try{
            //System.out.println("PRE USER");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);
            cReservationDataDtoResponseEntity= userServiceRestTemplate.exchange("/service/userEmail/" + reservation.getUserId(), HttpMethod.GET, request, CReservationDataDto.class);

        }catch(Exception e){
            try {
                throw new NotFoundException("Nije naso rezervacija user");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }
        assert cReservationDataDtoResponseEntity != null;

        cancelReservationNotificationDto.setEmail(cReservationDataDtoResponseEntity.getBody().getEmail());
        jmsTemplate.convertAndSend(cancelReservationDestination, messageHelper.createMessage(cancelReservationNotificationDto));

        reservationRepository.delete(reservation);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> cancelResC(Long l, Long resId, String jwt) {
        Reservation reservation = reservationRepository.getOne(resId);
        if(!(reservation.getUserId().compareTo(l) == 0)){
            System.out.println("USO DE NIJE TREBALO" + l + " " + reservation.getUserId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);

            userServiceRestTemplate.exchange("/service/decr/" + reservation.getUserId(), HttpMethod.PUT, request,ManagerDto.class);

        }catch(Exception e){
            try {
                throw new NotFoundException("Nesto sa dekrementacijom rezervacija usera");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }
        // OBAVESTENJA/MEJLOVI
        ResponseEntity<UserMailDto> userMailDto1 = null;
        try{
            // System.out.println("PRE MANGER");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);

            userMailDto1 = userServiceRestTemplate.exchange("/service/managerEmail/" + reservation.getHotel().getName(), HttpMethod.GET, request, UserMailDto.class);
            //System.out.println(userMailDto1.getBody().getEmail() + " <-OVO JE NESTO");

        }catch(Exception e){
            try {
                throw new NotFoundException("Nije naso 5");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }
        assert userMailDto1 != null;
        CancelReservationNotificationDto cancelReservationNotificationDto = new CancelReservationNotificationDto();
        cancelReservationNotificationDto.setIdReservation(reservation.getId());
        cancelReservationNotificationDto.setEmail(userMailDto1.getBody().getEmail());
        jmsTemplate.convertAndSend(cancelReservationDestination, messageHelper.createMessage(cancelReservationNotificationDto));

        ResponseEntity<CReservationDataDto> cReservationDataDtoResponseEntity = null;
        try{
            //System.out.println("PRE USER");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwt);

            HttpEntity<String> request = new HttpEntity<String>(headers);
            cReservationDataDtoResponseEntity= userServiceRestTemplate.exchange("/service/userEmail/" + reservation.getUserId(), HttpMethod.GET, request, CReservationDataDto.class);

        }catch(Exception e){
            try {
                throw new NotFoundException("Nije naso rezervacija user");
            } catch (NotFoundException ex) {
                ex.printStackTrace();
            }
        }
        assert cReservationDataDtoResponseEntity != null;

        cancelReservationNotificationDto.setEmail(cReservationDataDtoResponseEntity.getBody().getEmail());
        jmsTemplate.convertAndSend(cancelReservationDestination, messageHelper.createMessage(cancelReservationNotificationDto));

        reservationRepository.delete(reservation);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
