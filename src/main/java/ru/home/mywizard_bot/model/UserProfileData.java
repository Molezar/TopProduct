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
    String country;
    String adres;
    String number;
    static final String bill = "<тут будет биткоин адрес оплаты>";
    static final double ammount = 0.01;

    public String getAccountName() {
        return accountname;
    }

    public void setAccountName(String accountname) {
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

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBill() {
        return bill;
    }

//    public void setBill(String bill) {this.bill = bill;}

    public double getAmmount() {return ammount;}

//    public void setAmmount(double ammount) {
//        this.ammount = ammount;
//    }


    @Override
    public String toString() {
        return String.format("Имя: %s%nФамилия: %s%nСтрана: %s%nАдрес: %s%nНомер телефона: %s%nАдрес для оплаты: %s%nСумма оплаты: %s%n", getName(), getLastName(), getCountry(), getAdres(), getNumber(), getBill(), getAmmount());
    }
}
