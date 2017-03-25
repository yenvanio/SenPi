package com.shiv.outsourced.senpi;

import android.graphics.Bitmap;

/**
 * Created by 100603498 on 3/9/2017.
 */

public class QRCode {

    String name;
    String temp;
    String lighting;
    Bitmap image;


    public QRCode() {}

    public void setName(String name) {this.name = name;}
    public String getName(){return this.name;}

    public void setTemp(String temp){this.temp = temp;}
    public String getTemp(){return this.temp;}

    public void setLighting(String lighting){this.lighting = lighting;}
    public String getLighting(){return this.lighting;}

    public Bitmap getImage(){return this.image;}
    public void setBitmap(Bitmap image){this.image = image;}


}
