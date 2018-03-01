package com.finder.movie.moviefinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.movie.moviefinder.models.Movie;

public class MovieDetailsActivity extends AppCompatActivity {
    private TextView voteAverage;
    private TextView overview;
    private ImageView poster;
    private Movie movie;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        movie = (Movie) getIntent().getSerializableExtra("movie");
        init();
        setTitle(movie.getMovieName());
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void init(){
        voteAverage = findViewById(R.id.movie_vote_average);
        poster = findViewById(R.id.movie_poster);
        overview = findViewById(R.id.movie_overview);

        overview.setText(movie.getOverview());
        voteAverage.setText(String.valueOf(movie.getVoteAverage()));
        String url = MainActivity.POSTER_URL + movie.getPosterUrl();
        Glide.with(this).load(url).into(poster);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.transition.stay, R.transition.slide_out_up);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.transition.stay, R.transition.slide_out_up);
    }
}
