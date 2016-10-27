package com.example.android.gazeteer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Student on 24/10/2016.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private static String LOG_TAG = BookAdapter.class.getSimpleName();

    public BookAdapter(Activity context, ArrayList<Book> book_list) {
        super(context, 0, book_list);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View bookListView = convertView;
        ViewHolder holder;

        if (bookListView == null) {
            LayoutInflater.from(getContext())
                            .inflate(R.layout.book_item,parent,false);

            holder = new ViewHolder();
            holder.title = (TextView)  bookListView.findViewById(R.id.book_title);
            holder.authors = (TextView) bookListView.findViewById(R.id.book_authors);

            bookListView.setTag(holder);
        }else {

            holder = (ViewHolder) bookListView.getTag();
        }

        return bookListView;
    }

    static class ViewHolder {

        TextView title;
        TextView authors;
    }
}