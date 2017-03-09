package com.shiv.outsourced.senpi;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 100603498 on 3/8/2017.
 */

public class User implements Serializable {

    private String id;
    private String email;
    private String name;
    private List<QRCode> qrCodeList;

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public List<QRCode> getQr() {
        return qrCodeList;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQr(List<QRCode> qrCodeList) {
        this.qrCodeList = qrCodeList;
    }

}