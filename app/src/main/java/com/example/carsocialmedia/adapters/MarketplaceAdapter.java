package com.example.carsocialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.carsocialmedia.R;
import com.example.carsocialmedia.models.Post;

import java.util.List;

public class MarketplaceAdapter extends RecyclerView.Adapter<MarketplaceAdapter.ViewHolder> {

    private List<Post> posts;
    private Context context;

    public MarketplaceAdapter(Context context, List<Post> posts){
        this.context = context;
        this.posts = posts;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView carImg;
        TextView title, price, mileage, descContent, seller;
        Button contact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            carImg = itemView.findViewById(R.id.marketplace_car_image);
            title = itemView.findViewById(R.id.marketplace_title);
            price = itemView.findViewById(R.id.marketplace_price);
            mileage = itemView.findViewById(R.id.marketplace_mileage);
            descContent = itemView.findViewById(R.id.marketplace_description);
            seller = itemView.findViewById(R.id.marketplace_seller);
            contact = itemView.findViewById(R.id.contact_button);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_marketplace, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.title.setText(post.getCar());
        holder.price.setText(String.format("$%,.0f", post.getPrice()));;
        holder.mileage.setText("Kilometers: " + post.getMileage() + " km");
        holder.descContent.setText(post.getDescription());
        holder.seller.setText("Seller: " + post.getUsername());

        Glide.with(context)
                .load(post.getImageUrl())
                .centerCrop()
                .transform(new RoundedCorners(20))
                .into(holder.carImg);

        holder.contact.setOnClickListener( v -> {

            String email = post.getEmail();
            String subject = "Regarding your " + post.getCar();
            String body = "Hi " + post.getUsername() + ",\n\n" +
                    "I'm interested in your " + post.getCar() + ".\n" +
                    "Is it still available?\n\nThanks!";

            subject = Uri.encode(subject);
            body = Uri.encode(body);

            Uri uri = Uri.parse("mailto:" + email + "?subject=" + subject + "&body=" + body);

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);

            context.startActivity(Intent.createChooser(emailIntent, "Send Email"));

        });
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;
    }
}
