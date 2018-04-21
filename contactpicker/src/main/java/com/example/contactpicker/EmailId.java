package com.example.contactpicker;

import java.io.Serializable;

/**
 * Created by root on 21/4/18.
 */

public class EmailId implements Serializable {
    public String geteMailId() {
        return eMailId;
    }
    public void seteMailId(String eMailId) {
        this.eMailId = eMailId;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    private String eMailId,category;
}
