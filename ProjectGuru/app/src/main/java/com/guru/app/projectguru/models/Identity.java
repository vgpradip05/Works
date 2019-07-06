package com.guru.app.projectguru.models;

import java.io.Serializable;
import java.util.HashMap;

public class Identity implements Serializable {
    private boolean isIntern;

    private HashMap<String,Identity> reqEmails;
    public boolean isIntern() {
        return isIntern;
    }

    public void setIntern(boolean intern) {
        isIntern = intern;
    }

    public boolean isRegerstered() {
        return isRegerstered;
    }

    public void setRegerstered(boolean regerstered) {
        isRegerstered = regerstered;
    }

    boolean isRegerstered;

    public HashMap<String, Identity> getReqEmails() {
        return reqEmails;
    }

    public void setReqEmails(HashMap<String, Identity> reqEmails) {
        this.reqEmails = reqEmails;
    }
}
