package com.example.carsocialmedia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SearchFragment extends Fragment {

    private ApiService apiService;
    private SearchAdapter adapter;
    private List<Post> allPosts = new ArrayList<>();

    private SearchView searchView;
    private RecyclerView recycler;
    private TextView emptyText;
    private ProgressBar progress;

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        apiService = ApiClient.getApiService();

        searchView = view.findViewById(R.id.search_view);
        recycler = view.findViewById(R.id.search_recycler);
        emptyText = view.findViewById(R.id.search_empty);
        progress = view.findViewById(R.id.search_progress);

        adapter = new SearchAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPosts(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPosts(newText);
                return true;
            }
        });

        loadAllPosts();
        return view;
    }

    private void loadAllPosts() {
        progress.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);

        apiService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!isAdded()) return;
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allPosts = response.body();
                    filterPosts(searchView.getQuery() != null
                            ? searchView.getQuery().toString() : "");
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                if (!isAdded()) return;
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void filterPosts(String query) {
        String q = query.trim().toLowerCase();
        List<Post> filtered = new ArrayList<>();

        for (Post post : allPosts) {
            if (q.isEmpty()
                    || contains(post.getTitle(), q)
                    || contains(post.getMake(), q)
                    || contains(post.getModel(), q)
                    || contains(post.getDescription(), q)
                    || contains(post.getUsername(), q)
                    || String.valueOf(post.getYear()).contains(q)) {
                filtered.add(post);
            }
        }

        adapter.updatePosts(filtered);
        emptyText.setVisibility(!q.isEmpty() && filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean contains(String field, String query) {
        return field != null && field.toLowerCase().contains(query);
    }
}
