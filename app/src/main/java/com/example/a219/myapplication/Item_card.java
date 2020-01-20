package com.example.a219.myapplication;

import android.graphics.Bitmap;

/**
 * Created by 219 on 2017-06-14.
 */

public class Item_card {
    Bitmap image;
    String imagetitle;
    String eTitle;
    String pTitle;
    String dTitle;
    String aTitle;
    String rTitle;

    public String geteTitle() {
        return eTitle;
    }

    public String getpTitle() {
        return pTitle;
    }

    public String getdTitle() {
        return dTitle;
    }

    public String getaTitle() {
        return aTitle;
    }

    public String getrTitle() {
        return rTitle;
    }

    public Bitmap getImage(){
        return image;
    }
    public String getImagetitle(){
        return imagetitle;
    }
    public Item_card(Bitmap image, String imagetitle,String eTitle, String pTitle, String dTitle, String aTitle, String rTitle) {
        this.image=image;
        this.imagetitle=imagetitle;
        this.eTitle = eTitle;
        this.pTitle =pTitle;
        this.dTitle =dTitle;
        this.aTitle = aTitle;
        this.rTitle = rTitle;
    }

}
