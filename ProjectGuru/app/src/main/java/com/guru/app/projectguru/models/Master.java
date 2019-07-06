package com.guru.app.projectguru.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Master {
    public ArrayList<String> getCities() {
        return cities;
    }

    public void setCities(ArrayList<String> cities) {
        this.cities = cities;
    }

    public ArrayList<String> getUniversities() {
        return universities;
    }

    public void setUniversities(ArrayList<String> universities) {
        this.universities = universities;
    }

    public HashMap<String, ArrayList<String>> getSectors() {
        return sectors;
    }

    public void setSectors(HashMap<String, ArrayList<String>> sectors) {
        this.sectors = sectors;
    }

    ArrayList<String> cities,universities;
    HashMap<String,ArrayList<String>>sectors;
}
