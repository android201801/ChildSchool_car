package com.bikomobile.multipartsample;

/**
 * Created by user on 2018-06-20.
 */

public class Item {
    int image;
    String title;
    String carnum;

    int getImage(){
        return this.image;
    }

    String getTitle(){
        return this.title;
    }
    String getNum(){
        return this.carnum;
    }

    Item(int image, String title,String carnum) {
        this.image = image;
        this.title = title;
        this.carnum = carnum;
    }

}


