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

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Integer> posts;
    private Context context;

    public PostAdapter(Context context, List<Integer> posts){
        this.context = context;
        this.posts = posts;
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
        Integer post = posts.get(position);

        holder.title.setText(post.toString());
        holder.username.setText("User: " + post);
        holder.time.setText("Time: " + post);

        Glide.with(context)
                .load("https://cdn.pixabay.com/photo/2022/07/04/10/46/vintage-car-7300881_1280.jpg")
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
