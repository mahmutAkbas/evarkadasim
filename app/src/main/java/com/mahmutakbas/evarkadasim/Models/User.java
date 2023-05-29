package com.mahmutakbas.evarkadasim.Models;

public class User {
    String nameSurname,department,userClass,status,lengthType,timeType,mailAddress,phoneNumber,photoUrl,userId;
    int length,timeStay;

    public User(String nameSurname, String department, String userClass, String status, String lengthType, String timeType, String mailAddress, String phoneNumber, String photoUrl, String userId, int length, int timeStay) {
        this.nameSurname = nameSurname;
        this.department = department;
        this.userClass = userClass;
        this.status = status;
        this.lengthType = lengthType;
        this.timeType = timeType;
        this.mailAddress = mailAddress;
        this.phoneNumber = phoneNumber;
        this.photoUrl = photoUrl;
        this.userId = userId;
        this.length = length;
        this.timeStay = timeStay;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public int getTimeStay() {
        return timeStay;
    }

    public void setTimeStay(int timeStay) {
        this.timeStay = timeStay;
    }

    public String getLengthType() {
        return lengthType;
    }

    public void setLengthType(String lengthType) {
        this.lengthType = lengthType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public User(){}

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getClassNumber() {
        return userClass;
    }

    public void setClassNumber(String classNumber) {
        this.userClass = classNumber;
    }
}
