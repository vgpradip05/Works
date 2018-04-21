package com.example.contactpicker;

import java.io.Serializable;

/**
 * Created by root on 21/4/18.
 */

public class PhoneNumber implements Serializable {
    private String phoneNumber,category;
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}