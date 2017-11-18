package com.example.android.newsfeeds;

/**
 * Created by casab on 11/06/2017.
 */

public class News {
    /*Declaring the variable of the Object News that will be used to populate the list of News in the Main Activity*/
    private String mSection;
    private String mTitle;
    private String mUrl;

    public News(String section, String title, String url){
        mSection = section;
        mTitle = title;
        mUrl = url;
    }
    /*Declaring the public methods to get the private parameters of the Object News*/
    public String getSection() { return mSection; }
    public String getTitle() { return mTitle; }
    public String getUrl() { return mUrl; }
}
