package com.example.crema.malproject;

import java.io.Serializable;

/**
 * Created by crema on 26/08/2016.
 */
public class movie implements Serializable {
    String movie_id;
    String title;
    String poster_url;
    String movie_rate;
    String movie_date;
    String movie_overview;
    String Ismoviefavaourite;

    movie(){
        Ismoviefavaourite = "NO";
    }
   /* movie(String id ,String tit , String imageURL,String overview, String releaseDate , String rate){

        movie_id = id ;
        title = tit;
        poster_url = imageURL;
        movie_overview = overview;
        movie_date = releaseDate;
        movie_rate = rate;
        Ismoviefavaourite = "NO";

    }*/


    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getMovie_rate() {
        return movie_rate;
    }

    public void setMovie_rate(String movie_rate) {
        this.movie_rate = movie_rate;
    }

    public String getMovie_date() {
        return movie_date;
    }

    public void setMovie_date(String movie_date) {
        this.movie_date = movie_date;
    }

    public String getMovie_overview() {
        return movie_overview;
    }

    public void setMovie_overview(String movie_overview) {
        this.movie_overview = movie_overview;
    }
}
