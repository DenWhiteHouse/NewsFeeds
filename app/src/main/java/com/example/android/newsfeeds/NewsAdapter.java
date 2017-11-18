package com.example.android.newsfeeds;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by casab on 11/06/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    /*Declaring the List of News Object and the MainActivity as Context of the class*/
    List<News> mNews;
    MainActivity mContext;

    /*Placing a ViewHolder to stabileze the view*/
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView section;
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            section = (TextView) itemView.findViewById(R.id.section);

        }
    }

    /*Constructor of the News Object using a RecycleView in a ViewHolder*/
    public NewsAdapter(MainActivity context, List<News> newses) {
        this.mNews = newses;
        this.mContext = context;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder viewholder = new ViewHolder(listItem);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        final News currentArticle = mNews.get(position);
        holder.section.setText(currentArticle.getSection());
        holder.title.setText(currentArticle.getTitle());
// On click will be open a webpage for the news
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.openWebPage(currentArticle.getUrl());
            }
        });
    }

    /*Simple method to get the number of Items */
    @Override
    public int getItemCount() {
        return mNews.size();
    }
}
