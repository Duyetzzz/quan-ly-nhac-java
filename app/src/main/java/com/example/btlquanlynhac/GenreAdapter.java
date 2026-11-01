package com.example.btlquanlynhac;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends BaseAdapter {
    public interface Actions {
        void onEdit(Genre genre);
        void onDelete(Genre genre);
    }
    private final Activity context;
    private final Actions actions;
    private List<Genre> items = new ArrayList<>();

    public GenreAdapter(Activity context, Actions actions) {
        this.context = context;
        this.actions = actions;
    }

    public void replace(List<Genre> newItems) {
        items = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    @Override public int getCount() { return items.size(); }
    @Override public Genre getItem(int index) { return items.get(index); }
    @Override public long getItemId(int index) { return items.get(index).id; }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : LayoutInflater.from(context)
                .inflate(R.layout.row_genre, parent, false);
        Genre genre = getItem(position);
        ((TextView) view.findViewById(R.id.genreName)).setText(genre.name);
        ((TextView) view.findViewById(R.id.genreCode)).setText("Mã: " + genre.code);
        ((TextView) view.findViewById(R.id.genreSongs)).setText(genre.songCount + " bài");
        ((TextView) view.findViewById(R.id.genreAge)).setText(
                "Độ tuổi nghe: " + genre.minAge + " – " + genre.maxAge + " tuổi");
        ((Button) view.findViewById(R.id.btnEditGenre)).setOnClickListener(v -> actions.onEdit(genre));
        ((Button) view.findViewById(R.id.btnDeleteGenre)).setOnClickListener(v -> actions.onDelete(genre));
        return view;
    }
}
