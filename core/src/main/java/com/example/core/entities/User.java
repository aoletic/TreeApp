package com.example.core.entities;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {
    public String uid;
    public String ime;
    public String prezime;
    public String email;
    public String profilnaSlika;
    public long uloga;
    public String korisnickoIme;
    public String datumRodenja;
    public long bodovi;
    Bitmap Slika;
    public int SlikaLeaderboard;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilnaSlika() {
        return profilnaSlika;
    }

    public void setProfilnaSlika(String profilnaSlika) {
        this.profilnaSlika = profilnaSlika;
    }

    public long getUloga() {
        return uloga;
    }

    public void setUloga(long uloga) {
        this.uloga = uloga;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getDatumRodenja() {
        return datumRodenja;
    }

    public void setDatumRodenja(String datumRodenja) {
        this.datumRodenja = datumRodenja;
    }

    public long getBodovi() {
        return bodovi;
    }

    public void setBodovi(long bodovi) {
        this.bodovi = bodovi;
    }

    public Bitmap getSlika() {
        return Slika;
    }

    public void setSlika(Bitmap slika) {
        Slika = slika;
    }

    public User(){}

    User(String korime){
        this.korisnickoIme = korime;
    }

    public User(String uid, String ime, String prezime, String email,
    String profilnaSlika, long uloga, String korisnickoIme, String datumRodenja, long bodovi)
    {
        this.uid=uid;
        this.ime=ime;
        this.prezime=prezime;
        this.email=email;
        this.profilnaSlika=profilnaSlika;
        this.uloga=uloga;
        this.bodovi=bodovi;
        this.korisnickoIme=korisnickoIme;
        this.datumRodenja=datumRodenja;
    }

    public User(User user)
    {
        this.uid=user.uid;
        this.ime=user.ime;
        this.prezime=user.prezime;
        this.email=user.email;
        this.profilnaSlika=user.profilnaSlika;
        this.uloga=user.uloga;
        this.bodovi=user.bodovi;
        this.korisnickoIme=user.korisnickoIme;
        this.datumRodenja=user.datumRodenja;
        this.Slika=user.Slika;
    }

}
