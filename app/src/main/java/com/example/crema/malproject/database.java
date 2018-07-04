package com.example.crema.malproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by crema on 28/09/2016.
 */
public class database extends SQLiteOpenHelper {

    public database(Context context) {
        super(context, "favourite", null, 3);
    }
    public SQLiteDatabase data;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table movie(id text primary key,name text not null,poster_url text not null,overview text not null,data text not null,rate text not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table  movie");
        onCreate(db);
    }

    public void add_movie(String id,String name,String poster_url,String overview,String date,String rate)
    {
        data=this.getWritableDatabase();
        ContentValues con = new ContentValues();
        con.put("id",id);
        con.put("name",name);
        con.put("poster_url",poster_url);
        con.put("overview",overview);
        con.put("data",date);
        con.put("rate", rate);

        data.insert("movie", null, con);

        data.close();

    }
    public void delete_movie(String id) {
        data=this.getWritableDatabase();
        data.execSQL("delete from movie where id='" + id + "'");
        data.close();
    }


    public Cursor get_movies(String id)
    {
        data=this.getReadableDatabase();
        /*String [] s={"id","name","poster_url"};
        Cursor cur=data.query("movie",s,null,null,null,null,null);*/
        Cursor cur=data.rawQuery("select * from movie where id like?",new String[]{"%"+id+"%"});
        if(cur.getCount() !=0)
        {
            cur.moveToFirst();
            data.close();
            return cur;
        }
        return null;
    }
    public Cursor get_all_movies()
    {
        data=this.getReadableDatabase();
        //String [] s={"id","name","poster_url"};
        Cursor cur=data.query("movie",null,null,null,null,null,null);
        if(cur.getCount() !=0)
        {
            cur.moveToFirst();
            data.close();
            return cur;
        }
        return null;
    }
}
