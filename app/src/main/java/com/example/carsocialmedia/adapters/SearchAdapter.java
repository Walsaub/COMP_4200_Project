package com.example.carsocialmedia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsocialmedia.R;
import com.example.carsocialmedia.models.Post;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<Post> posts;

    public SearchAdapter(List<Post> posts) {
        this.posts = posts;
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
        holder.title.setText(post.getTitle());
        holder.subtitle.setText(post.getYear() + " " + post.getMake() + " " + post.getModel());
        holder.price.setText(String.format("$%,.0f", post.getPrice()));

        String desc = post.getDescription();
        if (desc != null && !desc.isEmpty()) {
            holder.description.setText(desc);
            holder.description.setVisibility(View.VISIBLE);
        } else {
            holder.description.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, price, description;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.post_title);
            subtitle = itemView.findViewById(R.id.post_subtitle);
            price = itemView.findViewById(R.id.post_price);
            description = itemView.findViewById(R.id.post_description);
        }
    }
}