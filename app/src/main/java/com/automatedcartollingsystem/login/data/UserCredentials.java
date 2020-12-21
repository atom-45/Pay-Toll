package com.automatedcartollingsystem.login.data;

/*
 * Matome Modiba @15/12/2020
 * Gathers data obtained from the sign in xml activity
 */
public class UserCredentials {
    private String email;
    private String password;

    public UserCredentials(String email,String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail(){return email;}
    public String getPassword(){return password;}
}
