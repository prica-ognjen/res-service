package com.badpc.res.dto;

public class ReviewDto {
    private Integer ocena;
    private String komentar;
    private String imeHotela;

    public Integer getOcena() {
        return ocena;
    }

    public void setOcena(Integer ocena) {
        this.ocena = ocena;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getImeHotela() {
        return imeHotela;
    }

    public void setImeHotela(String imeHotela) {
        this.imeHotela = imeHotela;
    }
}
