package com.example.dvotesystem;

public class UserLocation {
    public String country;
    public String state;
    public String district;
    public String city;
    public String postalCode;
    public double latitude;
    public double longitude;
    public String address;

    public UserLocation() {
    }

    public UserLocation(String country, String state, String district, String city, String postalCode, double latitude, double longitude, String address) {
        this.country = country;
        this.state = state;
        this.district = district;
        this.city = city;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
