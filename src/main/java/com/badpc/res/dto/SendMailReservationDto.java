package com.badpc.res.dto;

public class SendMailReservationDto {

    private String mail;

    public SendMailReservationDto(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
