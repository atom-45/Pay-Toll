package com.automatedcartollingsystem.login.data;

public class UserVerification {

    private final String emailAddress;
    private final String password;

    public UserVerification(UserCredentials userCredentials){
        this.emailAddress = userCredentials.getEmail();
        this.password = userCredentials.getPassword();
    }

    //password should be encrypted.
    public boolean verification(String ea, String pwrd){

        if(ea==null && pwrd==null) {
            return false;
        } else return emailAddress.equals(ea) && pwrd.equals(password);
    }
}
