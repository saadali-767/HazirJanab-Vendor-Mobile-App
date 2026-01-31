package com.example.hazirjanabvendorportal;

import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;

public class Class_ServiceBooking {
    int id;
    int user_id;
    int service_id;
    int vendor_id;
    String city;
    String address;
    Date date;
    String time;
    String description;
    Blob picture;
    String type;
    String status;

    public Class_ServiceBooking(int id, int user_id, int service_id, int vendor_id, String city, String address, Date date, String time, String description, Blob picture, String type, String status) {
        this.id = id;
        this.user_id = user_id;
        this.service_id = service_id;
        this.vendor_id = vendor_id;
        this.city = city;
        this.address = address;
        this.date = date;
        this.time = time;
        this.description = description;
        this.picture = picture;
        this.type = type;
        this.status= status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getPicture() {
        return picture;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
