package com.finder.movie.moviefinder.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.movie.moviefinder.MainActivity;
import com.finder.movie.moviefinder.R;
import com.finder.movie.moviefinder.models.Movie;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Movie> movies;
    private LayoutInflater inflater;
    Context context;
    private OnFilterItemClickListener mOnFilterItemClickListener;
    private View.OnClickListener mOnClickListener;

    public MoviesAdapter(Context context, ArrayList<Movie> movies, OnFilterItemClickListener onFilterItemClickListener) {
        this.movies = movies;
        this.context = context;

        mOnFilterItemClickListener = onFilterItemClickListener;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.root) {
                    mOnFilterItemClickListener.onItemClick((Movie) view.getTag(R.string.movie_tag));
                }
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        View view = inflater.inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MovieViewHolder) holder).bind(movies.get(position));
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private View root;
        private TextView title;
        private TextView voteAverage;
        private ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.movie_title);
            voteAverage = itemView.findViewById(R.id.movie_vote_average);
            poster = itemView.findViewById(R.id.movie_poster);

            root.setOnClickListener(mOnClickListener);
        }

        public void bind(Movie movie) {
            root.setTag(R.string.movie_tag, movie);
            title.setText(movie.getMovieName());
            voteAverage.setText(String.valueOf(movie.getVoteAverage()));
            String url = MainActivity.POSTER_URL + movie.getPosterUrl();
            Glide.with(context).load(url).into(poster);
        }
    }

    public interface OnFilterItemClickListener {
        void onItemClick(Movie movie);
    }
}
