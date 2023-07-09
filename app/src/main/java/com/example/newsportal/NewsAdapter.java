package com.example.newsportal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private ArrayList<Articles> articlesArrayList;
    private Context context;

    public NewsAdapter(ArrayList<Articles> articlesArrayList, Context context) {
        this.articlesArrayList = articlesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,parent, false);
      return new NewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        Articles articles = articlesArrayList.get(position);
        holder.newsTitle.setText(articles.getTitle());
        holder.newsSource.setText(articles.getAuthor());

        if (articles.getUrlToImage() != null){
            Picasso.get().load(articles.getUrlToImage()).into(holder.newsImage);
        }else{
            Picasso.get().load(R.drawable.no_image).into(holder.newsImage);
        }
    }

    @Override
    public int getItemCount() {
        return articlesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView newsTitle, newsSource;
        private ImageView newsImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsSource = itemView.findViewById(R.id.newsSource);
            newsImage = itemView.findViewById(R.id.newsImage);
        }
    }
}
