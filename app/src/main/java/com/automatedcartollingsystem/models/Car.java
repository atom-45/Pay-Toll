package com.automatedcartollingsystem.models;

import java.util.Objects;

/**
 * Matome Modiba @13/12/2020
 * Car model class
 */
public class Car {

    private final Registration reg;
    private final String model;
    private final String brandLabel;
    private final String motorClass;
    private int user_id;


    public Car(Registration reg, String model, String brandLabel, String motorClass) {
        this.reg = reg;
        this.model = model;
        this.brandLabel = brandLabel;
        this.motorClass = motorClass;
    }

    public Registration getReg() {
        return reg;
    }

    public String getModel() {
        return model;
    }

    public String getBrandLabel() {
        return brandLabel;
    }

    public String getMotorClass() {
        return motorClass;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return getUser_id() == car.getUser_id() &&
                getReg().equals(car.getReg()) &&
                getModel().equals(car.getModel()) &&
                getBrandLabel().equals(car.getBrandLabel()) &&
                getMotorClass().equals(car.getMotorClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReg(), getModel(), getBrandLabel(), getMotorClass(), getUser_id());
    }
}
