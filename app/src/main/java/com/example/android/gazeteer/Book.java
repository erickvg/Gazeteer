package com.example.android.gazeteer;

/**
 * Created by Student on 24/10/2016.
 */

public class Book {

    private String mTitle;
    private String mAuthor;


    public Book(String title, String author) {
        mTitle = title;
        mAuthor = author;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }
}