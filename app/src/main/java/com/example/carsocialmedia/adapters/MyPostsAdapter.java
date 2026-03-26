package com.example.carsocialmedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carsocialmedia.R;
import com.example.carsocialmedia.models.Post;

import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder> {

    private List<Post> posts;
    private Context context;

    public interface OnPostClickListener{
        void onPostClick(Post post, int position);
    }
    private OnPostClickListener listener;

    public MyPostsAdapter(Context context, List<Post> posts, OnPostClickListener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView postImage;

        public ViewHolder(View itemView){
            super(itemView);
            postImage = itemView.findViewById(R.id.my_posts_image_card);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        Glide.with(context)
                .load(post.getImageUrl())
                .centerCrop()
                .into(holder.postImage);

        holder.itemView.setOnLongClickListener( v -> {
            if (listener != null){
                listener.onPostClick(post, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }
}
