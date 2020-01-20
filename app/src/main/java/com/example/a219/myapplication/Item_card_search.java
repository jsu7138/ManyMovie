package com.example.a219.myapplication;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017-06-17.
 */

public class Item_card_search {
    String TItle;
    String eTitle;
    String pTitle;
    String dTitle;
    String aTitle;
    String rTitle;
    Bitmap bitmap;

    public String getaTitle() {
        return aTitle;
    }

    public String getrTitle() {
        return rTitle;
    }

    public String getTItle() {
        return TItle;
    }

    public String geteTitle() {
        return eTitle;
    }

    public String getpTitle() {
        return pTitle;
    }

    public String getdTitle() {
        return dTitle;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Item_card_search(Bitmap bitmap, String Title, String eTitle, String pTitle, String dTitle,String aTitle,String rTitle) {
        this.bitmap = bitmap;
        this.TItle = Title;
        this.eTitle = eTitle;
        this.pTitle =pTitle;
        this.dTitle =dTitle;
        this.aTitle = aTitle;
        this.rTitle = rTitle;
    }
}
