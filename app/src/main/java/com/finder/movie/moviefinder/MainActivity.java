package com.finder.movie.moviefinder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.finder.movie.moviefinder.adapters.MoviesAdapter;
import com.finder.movie.moviefinder.adapters.SuggestionsAdapter;
import com.finder.movie.moviefinder.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnFilterItemClickListener {
    private final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie?api_key=2696829a81b1b5827d515ff121700838&query=";
    public static final String POSTER_URL = "https://image.tmdb.org/t/p/w92";

    //views types to show and hide according to the data
    private final int START = 0;
    private final int LOADING = 1;
    private final int DATA = 2;

    //to retrieve all history search
    private List<String> historyItems;
    //to filter the right keywords matching the query
    private List<String> finalHistoryItems;

    SearchView search;
    //to call the API
    private OkHttpClient client;
    private MoviesAdapter adapter;

    private TextView startText;
    private ContentLoadingProgressBar loadingProgressBar;
    private RecyclerView moviesRecyclerView;
    private LinearLayoutManager layoutManager;
    private String query;
    private Toolbar mToolbar;
    private boolean isSubmitted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        //retrieve the search history
        historyItems = new ArrayList<>();
        loadHistory(this);

        findViews();

        //show the start message
        showView(START);
    }

    void findViews() {
        startText = findViewById(R.id.start_text);
        loadingProgressBar = findViewById(R.id.loading);
        moviesRecyclerView = findViewById(R.id.movie_list);
        layoutManager = new LinearLayoutManager(this);
        moviesRecyclerView.setLayoutManager(layoutManager);

        client = new OkHttpClient();
    }

    void showData(ArrayList<Movie> movies) {
        //show only the data if it has one item or more
        if (movies.size() > 0) {
            //add the keyword th the history
            if(!isAlreadyAdded(historyItems,query) && isSubmitted) {
                historyItems.add(query);
                saveArray();
            }
            //show the movies list
            showView(DATA);
            //set the adapter
            adapter = new MoviesAdapter(this, movies, this);
            moviesRecyclerView.setAdapter(adapter);
        } else {
            //if there is no result show an error
            showView(START);
            startText.setText("No movies available for your keyword");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu
        getMenuInflater().inflate(R.menu.menu_search, menu);
        //set the search view
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) menu.findItem(R.id.search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                //call the API to search
                isSubmitted = true;
                search(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //filter the search history
                isSubmitted = false;
                if (!query.isEmpty()) {
                    searchHistory(query);
                    search(query);
                }else {
                    searchHistory(query);
                    showView(START);
                }
                return true;
            }
        });


        //to show all search history
        searchHistory("");
        return true;
    }

    private void searchHistory(String query) {
        //initialize the cursor to set the cursorAdapter
        String[] columns = new String[]{"_id", "text"};
        Object[] temp = new Object[]{0, "default"};
        finalHistoryItems = new ArrayList<>();
        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < historyItems.size(); i++) {
            if (historyItems.get(i).contains(query)) {
                temp[0] = i;
                temp[1] = historyItems.get(i);

                cursor.addRow(temp);
                finalHistoryItems.add(historyItems.get(i));
            }
        }

        search.setSuggestionsAdapter(new SuggestionsAdapter(this, cursor, finalHistoryItems));
        //set listener for selecting a query keyword
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                //filter search history
                search(finalHistoryItems.get(i));
                //set the text of the searchView to be the query keyword
                search.setQuery(finalHistoryItems.get(i), true);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                search(finalHistoryItems.get(i));
                search.setQuery(finalHistoryItems.get(i), true);
                return true;
            }
        });

        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                showView(START);
                return false;
            }
        });
    }

    private void search(String query) {
        this.query = query;
        //show the progress
        showView(LOADING);
        //call the API
        final Request request = new Request.Builder()
                .url(SEARCH_URL + query)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                new Thread() {
                    public void run() {
                        //because calling the API is on another thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //show error message
                                showView(START);
                                startText.setText(e.getMessage());
                            }
                        });
                    }
                }.start();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                try {
                    //parse the JsonObject
                    JSONObject responseObject = new JSONObject(s);
                    JSONArray responseArray = responseObject.getJSONArray("results");
                    final ArrayList<Movie> movies = JsonParser.jsonToMovieList(responseArray);
                    new Thread() {
                        public void run() {
                            //because calling the API is on another thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //show the movies list
                                    showData(movies);
                                }
                            });
                        }
                    }.start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadHistory(Context mContext) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        historyItems.clear();
        //retrieve the search history as set and add them to the list
        Set<String> h = sp.getStringSet("history", new HashSet<String>());
        historyItems.addAll(h);
    }

    public boolean saveArray() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        //save the search history list as a set to prevent duplication
        Set<String> h = new HashSet<>(historyItems);
        editor.putStringSet("history", h);

        return editor.commit();
    }

    void showView(int viewType) {
        //show and hide the views according to the viewType
        switch (viewType) {
            case START:
                loadingProgressBar.setVisibility(View.GONE);
                moviesRecyclerView.setVisibility(View.GONE);
                startText.setVisibility(View.VISIBLE);
                break;
            case LOADING:
                loadingProgressBar.setVisibility(View.VISIBLE);
                moviesRecyclerView.setVisibility(View.GONE);
                startText.setVisibility(View.GONE);
                break;
            case DATA:
                loadingProgressBar.setVisibility(View.GONE);
                moviesRecyclerView.setVisibility(View.VISIBLE);
                startText.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onItemClick(Movie movie) {
        //go to movies details activity and pass the selected movie
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
        overridePendingTransition(R.transition.slide_in_up, R.transition.stay);
    }

    @Override
    public void onBackPressed() {
        //close the searchView if it's opened
        if (!search.isIconified()) {
            search.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isAlreadyAdded(List<String> list, String item) {
        for (String s : list) {
            if (s.equals(item))
                return true;
        }
        return false;
    }
}
