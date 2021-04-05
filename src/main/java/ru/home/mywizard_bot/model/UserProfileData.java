package ru.home.mywizard_bot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Данные анкеты пользователя
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData implements Serializable {

    String accountname;
    String name;
    String lastname;
    String adres;
    int number;

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return String.format("Имя: %s%Фамилия: %d%nГород: %s%nАдрес: %d%n" +
                        "Номер телефона: %s%n", getName(), getLastName(), getCity(), getAdres(),
                getNumber());
    }


//    String name;
//    String gender;
//    String color;
//    String movie;
//    String song;
//    int age;
//    int number;
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getColor() {
//        return color;
//    }
//
//    public void setColor(String color) {
//        this.color = color;
//    }
//
//    public String getMovie() {
//        return movie;
//    }
//
//    public void setMovie(String movie) {
//        this.movie = movie;
//    }
//
//    public String getSong() {
//        return song;
//    }
//
//    public void setSong(String song) {
//        this.song = song;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }
//
//    public int getNumber() {
//        return number;
//    }
//
//    public void setNumber(int number) {
//        this.number = number;
//    }
//
//    @Override
//    public String toString() {
//        return String.format("Имя: %s%nВозраст: %d%nПол: %s%nЛюбимая цифра: %d%n" +
//                        "Цвет: %s%nФильм: %s%nПесня: %s%n", getName(), getAge(), getGender(), getNumber(),
//                getColor(), getMovie(), getSong());
//    }
}
