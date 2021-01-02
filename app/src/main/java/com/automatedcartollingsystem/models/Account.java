package com.automatedcartollingsystem.models;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Matome Modiba @13/12/2020
 * Account class
 */
public class Account {

    private double balance;
    private final Long accountNumber;
    private final String bankName;
    private final String accountHolder;
    private final String accountType;
    private final String email;
    private int account_id;

    public Account(int account_id,double balance, Long accountNumber, String bankName,
                   String accountHolder, String accountType, String email) {
        if(bankName.equals("")||accountType.equals("")||accountNumber==0||balance<0)
            throw new IllegalArgumentException("Invalid Bank Account");

        this.balance = balance;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.accountHolder = accountHolder;
        this.accountType = accountType;
        this.email = email;
        this.account_id = account_id;
    }

    public double getBalance() {
        return balance;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getEmail() {
        return email;
    }

    public int getAccount_id() { return account_id; }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Double.compare(account.balance, balance) == 0 &&
                accountNumber.equals(account.accountNumber) &&
                bankName.equals(account.bankName) &&
                accountHolder.equals(account.accountHolder) &&
                accountType.equals(account.accountType) &&
                email.equals(account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, accountNumber, bankName, accountHolder, accountType, email);
    }

}
