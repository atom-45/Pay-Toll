package com.automatedcartollingsystem.models;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Matome Modiba @13/12/2020
 * User class, storing and create user profiles data.
 */
public class User {

    private final String name;
    private final String email;
    private final String mobile_number;
    private String license_exp;
    private Long account_id;
    private final String password;
    private List<Car> cars;

    //Remember on the activity registration xml to edit Surname into Mobile number
    public User(String name, String email, String mobile_number,
                String license_exp, Long account_id, String password, List<Car> cars) {
        this.name = name;
        this.email = email;
        this.mobile_number = mobile_number;
        this.license_exp = license_exp;
        this.account_id = account_id;
        this.password = password;
        this.cars = cars;
    }

    public void setAccount_id(@NotNull Account acc){this.account_id = acc.getAccountNumber();}

    public void setCars(List<Car> cars){ this.cars=cars;}

    public void  setLicense_exp(String license_exp) {this.license_exp = license_exp;}

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public String getLicense_exp() {
        return license_exp;
    }

    public Long getAccount_id() {
        return account_id;
    }

    public String getPassword() {
        return password;
    }

    public List<Car> getCars() {
        return cars;
    }
    public void addCar(Car car){}
}
