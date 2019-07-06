package com.guru.app.projectguru.models;

import java.util.HashMap;

public class DataModel {
    private String name, organization, city,skill,university,mobileNo,eMail,excel;
    private boolean isRequested;
    private HashMap<String,String>resumes;
    public DataModel() {
    }

    public DataModel(String name, String organization, String city) {
        this.name = name;
        this.organization = organization;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public HashMap<String, String> getResumes() {
        return resumes;
    }

    public void setResumes(HashMap<String, String> resumes) {
        this.resumes = resumes;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getExcel() {
        return excel;
    }

    public void setExcel(String excel) {
        this.excel = excel;
    }

    public boolean isRequested() {
        return isRequested;
    }

    public void setRequested(boolean requested) {
        isRequested = requested;
    }
}
