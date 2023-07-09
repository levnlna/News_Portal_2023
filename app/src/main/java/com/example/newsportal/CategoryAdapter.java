package com.example.newsportal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<Category> categoryArrayList;
    private Context context;
    private CategoryClicked categoryClicked;

    public CategoryAdapter(ArrayList<Category> categoryArrayList, Context context, CategoryClicked categoryClicked) {
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.categoryClicked = categoryClicked;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category = categoryArrayList.get(position);
        holder.newsCategory.setText(category.getCategoryName());
        holder.newsCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryClicked.onCategoryClick(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public interface CategoryClicked{
        void onCategoryClick(int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView newsCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            newsCategory = itemView.findViewById(R.id.newsCategory);
        }
    }
}
