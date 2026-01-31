package com.example.hazirjanabvendorportal;

import android.util.Log;

import java.sql.Blob;

public class Class_SingletonVendor {
    private static Class_SingletonVendor instance;
    private int id;
    private String first_name;
    private String last_name;
    private String gender;
    private String cnic;
    private String password;
    private String email;
    private String phone_number;
    private String address;
    private Blob vendor_picture;
    private Blob cnic_front;
    private Blob cnic_back;
    private float total_rating;
    private float aspect_time;
    private float aspect_quality;
    private float aspect_expertise;
    private String category;
    private int approval_status;
    private int total_orders;

    private Class_SingletonVendor() {

    }

    public void DisplayAllInformation(){
        Log.d("Vendor Information", "ID: " + id);
        Log.d("Vendor Information", "First Name: " + first_name);
        Log.d("Vendor Information", "Last Name: " + last_name);
        Log.d("Vendor Information", "Gender: " + gender);
        Log.d("Vendor Information", "CNIC: " + cnic);
        Log.d("Vendor Information", "Password: " + password);
        Log.d("Vendor Information", "Email: " + email);
        Log.d("Vendor Information", "Phone Number: " + phone_number);
        Log.d("Vendor Information", "Address: " + address);
        Log.d("Vendor Information", "Vendor Picture: " + vendor_picture);
        Log.d("Vendor Information", "CNIC Front: " + cnic_front);
        Log.d("Vendor Information", "CNIC Back: " + cnic_back);
        Log.d("Vendor Information", "Total Rating: " + total_rating);
        Log.d("Vendor Information", "Aspect Time: " + aspect_time);
        Log.d("Vendor Information", "Aspect Quality: " + aspect_quality);
        Log.d("Vendor Information", "Aspect Expertise: " + aspect_expertise);
    }

    public static synchronized Class_SingletonVendor getInstance() {
        if (instance == null) {
            instance = new Class_SingletonVendor();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Blob getVendor_picture() {
        return vendor_picture;
    }

    public void setVendor_picture(Blob vendor_picture) {
        this.vendor_picture = vendor_picture;
    }

    public Blob getCnic_front() {
        return cnic_front;
    }

    public void setCnic_front(Blob cnic_front) {
        this.cnic_front = cnic_front;
    }

    public Blob getCnic_back() {
        return cnic_back;
    }

    public void setCnic_back(Blob cnic_back) {
        this.cnic_back = cnic_back;
    }

    public float getTotal_rating() {
        return total_rating;
    }

    public void setTotal_rating(float total_rating) {
        this.total_rating = total_rating;
    }

    public float getAspect_time() {
        return aspect_time;
    }

    public void setAspect_time(float aspect_time) {
        this.aspect_time = aspect_time;
    }

    public float getAspect_quality() {
        return aspect_quality;
    }

    public void setAspect_quality(float aspect_quality) {
        this.aspect_quality = aspect_quality;
    }

    public float getAspect_expertise() {
        return aspect_expertise;
    }

    public void setAspect_expertise(float aspect_expertise) {
        this.aspect_expertise = aspect_expertise;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getApproval_status() {
        return approval_status;
    }

    public void setApproval_status(int approval_status) {
        this.approval_status = approval_status;
    }

    public int getTotal_orders() {
        return total_orders;
    }

    public void setTotal_orders(int total_orders) {
        this.total_orders = total_orders;
    }
}
