package com.example.core.entities;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Post implements Serializable {
    String ID_objava;
    String Korisnik_ID;
    String Datum_objave;
    double Latitude;
    double longitude;
    String Opis;
    String URL_slike;
    long Broj_lajkova;
    Bitmap Slika;

    public Post(String ID_objava, String korisnik_ID, String datum_objave, double latitude, double longitude, String opis, String URL_slike, long broj_lajkova) {
        this.ID_objava = ID_objava;
        this.Korisnik_ID = korisnik_ID;
        this.Datum_objave = datum_objave;
        this.Latitude = latitude;
        this.longitude = longitude;
        this.Opis = opis;
        this.URL_slike = URL_slike;
        this.Broj_lajkova = broj_lajkova;
    }

    public Post(Post post) {
        this.ID_objava = post.ID_objava;
        this.Korisnik_ID = post.Korisnik_ID;
        this.Datum_objave = post.Datum_objave;
        this.Latitude = post.Latitude;
        this.longitude = post.longitude;
        this.Opis = post.Opis;
        this.URL_slike = post.URL_slike;
        this.Broj_lajkova = post.Broj_lajkova;
        this.Slika=post.Slika;
    }

    public Bitmap getSlika() {
        return Slika;
    }

    public void setSlika(Bitmap slika) {
        Slika = slika;
    }

    public long getBroj_lajkova() {
        return Broj_lajkova;
    }

    public void setBroj_lajkova(long broj_lajkova) {
        Broj_lajkova = broj_lajkova;
    }

    public String getID_objava() {
        return ID_objava;
    }

    public void setID_objava(String ID_objava) {
        this.ID_objava = ID_objava;
    }

    public String getKorisnik_ID() {
        return Korisnik_ID;
    }

    public void setKorisnik_ID(String korisnik_ID) {
        Korisnik_ID = korisnik_ID;
    }

    public String getDatum_objave() {
        return Datum_objave;
    }

    public void setDatum_objave(String datum_objave) {
        Datum_objave = datum_objave;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getOpis() {
        return Opis;
    }

    public void setOpis(String opis) {
        Opis = opis;
    }

    public String getURL_slike() {
        return URL_slike;
    }

    public void setURL_slike(String URL_slike) {
        this.URL_slike = URL_slike;
    }
}
