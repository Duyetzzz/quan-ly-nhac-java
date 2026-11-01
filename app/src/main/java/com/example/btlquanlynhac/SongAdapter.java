package com.example.btlquanlynhac;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends BaseAdapter {
    public interface Actions {
        void onEdit(Song song);
        void onDelete(Song song);
    }

    private final Activity activity;
    private final boolean readOnly;
    private final Actions actions;
    private List<Song> items = new ArrayList<>();

    public SongAdapter(Activity activity, boolean readOnly, Actions actions) {
        this.activity = activity;
        this.readOnly = readOnly;
        this.actions = actions;
    }

    public void replace(List<Song> songs) {
        items = new ArrayList<>(songs);
        notifyDataSetChanged();
    }

    @Override public int getCount() { return items.size(); }
    @Override public Song getItem(int i) { return items.get(i); }
    @Override public long getItemId(int i) { return items.get(i).id; }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : LayoutInflater.from(activity)
                .inflate(R.layout.row_song, parent, false);
        bindView(activity, view, getItem(position), readOnly, actions);
        return view;
    }

    public static void bindView(Activity activity, View view, Song song, boolean readOnly, Actions actions) {
        ((TextView) view.findViewById(R.id.songName)).setText(song.name);
        ((TextView) view.findViewById(R.id.songCode)).setText("Mã: " + song.code);
        ((TextView) view.findViewById(R.id.songMeta)).setText(song.genreName + " · " + song.fileType);
        ((TextView) view.findViewById(R.id.songAge)).setText(
                "Độ tuổi nghe: " + song.minAge + " – " + song.maxAge + " tuổi");
        TextView desc = view.findViewById(R.id.songDesc);
        desc.setText(song.description == null || song.description.trim().isEmpty()
                ? "Chưa có mô tả" : song.description);
        ((TextView) view.findViewById(R.id.songLink)).setText(song.downloadUrl);

        ImageView image = view.findViewById(R.id.songImage);
        TextView icon = view.findViewById(R.id.coverIcon);
        image.setImageDrawable(null); // avoid displaying a recycled row's previous cover
        image.setVisibility(View.GONE);
        icon.setVisibility(View.VISIBLE);
        if (song.imageUri != null && !song.imageUri.isEmpty()) {
            try {
                image.setImageURI(Uri.parse(song.imageUri));
                if (image.getDrawable() != null) {
                    image.setVisibility(View.VISIBLE);
                    icon.setVisibility(View.GONE);
                }
            } catch (Exception ignored) { /* revoked or missing image: show placeholder */ }
        }
        ((Button) view.findViewById(R.id.btnOpenSong)).setOnClickListener(
                v -> Ui.openLink(activity, song.downloadUrl));
        ((Button) view.findViewById(R.id.btnCopySong)).setOnClickListener(
                v -> Ui.copyLink(activity, song.downloadUrl));
        Button edit = view.findViewById(R.id.btnEditSong);
        Button delete = view.findViewById(R.id.btnDeleteSong);
        edit.setVisibility(readOnly ? View.GONE : View.VISIBLE);
        delete.setVisibility(readOnly ? View.GONE : View.VISIBLE);
        if (!readOnly && actions != null) {
            edit.setOnClickListener(v -> actions.onEdit(song));
            delete.setOnClickListener(v -> actions.onDelete(song));
        } else {
            edit.setOnClickListener(null);
            delete.setOnClickListener(null);
        }
    }
}
