package com.example.carsocialmedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carsocialmedia.R;
import com.example.carsocialmedia.models.Post;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<Post> posts;
    private Context context;

    public SearchAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    public void updateSearchs(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.user.setText(post.getUsername());
        holder.carInfo.setText(post.getCar());

        Glide.with(context)
                .load(post.getImageUrl())
                .centerCrop()
                .into(holder.carImage);
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView user, carInfo;
        ImageView carImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.creator_name);
            carInfo = itemView.findViewById(R.id.car_info);
            carImage = itemView.findViewById(R.id.car_image);
        }
    }
}