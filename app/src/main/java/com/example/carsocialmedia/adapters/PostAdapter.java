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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> posts;
    private Context context;

    public PostAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    public void updatePosts(List<Post> newPosts){
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView username, time, title;
        ImageView image;

        public ViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.user_name_post);
            time = itemView.findViewById(R.id.time_post);
            title = itemView.findViewById(R.id.title_post);
            image = itemView.findViewById(R.id.image_post);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_post_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.title.setText(post.toString());
        holder.username.setText("User: " + post.getUsername());
        holder.time.setText("Time: " + post.getCreatedAt());

        Glide.with(context)
                .load(post.getImageUrl())
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }
}
