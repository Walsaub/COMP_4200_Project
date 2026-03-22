package com.example.carsocialmedia.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carsocialmedia.R;
import com.example.carsocialmedia.adapters.PostAdapter;
import com.example.carsocialmedia.adapters.SearchAdapter;
import com.example.carsocialmedia.api.ApiClient;
import com.example.carsocialmedia.api.ApiService;
import com.example.carsocialmedia.models.Post;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {


    private ApiService apiService;
    private PostAdapter adapter;
    private List<Post> posts = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.posts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getApiService();

        loadPosts(HomeFragment.super.getContext());

        return view;
    }

    private void loadPosts(Context context){
        apiService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful() && response.body() != null){
                    posts.clear();
                    posts.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}