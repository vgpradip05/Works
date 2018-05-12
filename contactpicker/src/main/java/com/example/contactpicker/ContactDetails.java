package com.example.contactpicker;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vgpradip05 on 21/4/18.
 */

public class ContactDetails implements Serializable {
    private String contactName,contactNickName;
    private List<PhoneNumber> phoneNumbers = new ArrayList<>();
    private List<EmailId> emailIds = new ArrayList<>();
    private InputStream contactPicture;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNickName() {
        return contactNickName;
    }

    public void setContactNickName(String contactNickName) {
        this.contactNickName = contactNickName;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<EmailId> getEmailIds() {
        return emailIds;
    }

    public void setEmailIds(List<EmailId> emailIds) {
        this.emailIds = emailIds;
    }

    public InputStream getContactPicture() {
        return contactPicture;
    }

    public void setContactPicture(InputStream contactPicture) {
        this.contactPicture = contactPicture;
    }
}