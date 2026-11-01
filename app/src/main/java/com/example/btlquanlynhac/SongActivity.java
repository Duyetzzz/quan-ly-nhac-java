package com.example.btlquanlynhac;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class SongActivity extends Activity {
    private static final int REQUEST_IMAGE = 101;
    private MusicDatabase db;
    private SongAdapter adapter;
    private EditText search;
    private Spinner genreFilter;
    private TextView total, empty;
    private List<Genre> filterItems = new ArrayList<>();
    private List<Genre> editorGenres = new ArrayList<>();
    private long filterGenreId = -1;
    private String selectedImageUri = "";
    private ImageView activeImagePreview;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        db = new MusicDatabase(this);
        search = findViewById(R.id.inputSearch);
        genreFilter = findViewById(R.id.filterGenre);
        total = findViewById(R.id.songTotal);
        empty = findViewById(R.id.songEmpty);
        adapter = new SongAdapter(this, false, new SongAdapter.Actions() {
            @Override public void onEdit(Song song) { editSong(song); }
            @Override public void onDelete(Song song) {
                Ui.confirmDelete(SongActivity.this, "Xóa bài nhạc “" + song.name + "”?", () -> {
                    db.deleteSong(song.id);
                    Ui.toast(SongActivity.this, "Đã xóa bài nhạc");
                    loadSongs();
                });
            }
        });
        ((ListView) findViewById(R.id.songList)).setAdapter(adapter);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddSong).setOnClickListener(v -> editSong(null));
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { loadSongs(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        genreFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterGenreId = filterItems.get(position).id;
                loadSongs();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        loadGenreFilter();
        loadSongs();
    }

    private void loadGenreFilter() {
        filterItems = new ArrayList<>();
        filterItems.add(new Genre(-1, "ALL", "Tất cả thể loại", 0, 120, 0));
        filterItems.addAll(db.getGenres());
        ArrayAdapter<Genre> array = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, filterItems);
        genreFilter.setAdapter(array);
        int position = 0;
        for (int i = 0; i < filterItems.size(); i++) {
            if (filterItems.get(i).id == filterGenreId) position = i;
        }
        genreFilter.setSelection(position);
    }

    private void loadSongs() {
        if (adapter == null) return;
        List<Song> songs = db.getSongs(Ui.value(search), filterGenreId, false, 0);
        adapter.replace(songs);
        total.setText(songs.size() + " file nhạc phù hợp");
        empty.setVisibility(songs.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void editSong(Song current) {
        editorGenres = db.getGenres();
        if (editorGenres.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Chưa có thể loại")
                    .setMessage("Hãy tạo ít nhất một thể loại nhạc trước khi thêm file nhạc.")
                    .setPositiveButton("Đến quản lý thể loại", (d, w) ->
                            startActivity(new Intent(this, GenreActivity.class)))
                    .setNegativeButton("Đóng", null).show();
            return;
        }
        View form = LayoutInflater.from(this).inflate(R.layout.dialog_song, null);
        EditText code = form.findViewById(R.id.editSongCode);
        EditText name = form.findViewById(R.id.editSongName);
        EditText fileType = form.findViewById(R.id.editFileType);
        EditText url = form.findViewById(R.id.editSongUrl);
        EditText description = form.findViewById(R.id.editSongDescription);
        Spinner genreSpinner = form.findViewById(R.id.editSongGenre);
        ImageView imagePreview = form.findViewById(R.id.editImagePreview);
        selectedImageUri = current == null || current.imageUri == null ? "" : current.imageUri;
        activeImagePreview = imagePreview;
        updateImagePreview();
        genreSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, editorGenres));
        if (current != null) {
            code.setText(current.code);
            name.setText(current.name);
            fileType.setText(current.fileType);
            url.setText(current.downloadUrl);
            description.setText(current.description);
            for (int i = 0; i < editorGenres.size(); i++) {
                if (editorGenres.get(i).id == current.genreId) {
                    genreSpinner.setSelection(i);
                    break;
                }
            }
        } else fileType.setText("MP3");
        form.findViewById(R.id.btnPickImage).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            try { startActivityForResult(intent, REQUEST_IMAGE); }
            catch (Exception e) { Ui.showError(this, e); }
        });
        form.findViewById(R.id.btnClearImage).setOnClickListener(v -> {
            selectedImageUri = "";
            updateImagePreview();
        });
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(current == null ? "Thêm file nhạc" : "Chỉnh sửa file nhạc")
                .setView(form).setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", null).create();
        dialog.setOnDismissListener(d -> activeImagePreview = null);
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    if (!Ui.require(code, "mã file nhạc") ||
                            !Ui.require(name, "tên file nhạc") ||
                            !Ui.require(fileType, "loại file") ||
                            !Ui.require(url, "link tải")) return;
                    if (!Ui.validUrl(Ui.value(url))) {
                        url.setError("Link phải là http:// hoặc https:// có tên miền hợp lệ");
                        url.requestFocus();
                        return;
                    }
                    try {
                        Genre genre = (Genre) genreSpinner.getSelectedItem();
                        if (genre == null) {
                            Ui.toast(this, "Vui lòng chọn thể loại.");
                            return;
                        }
                        db.saveSong(current == null ? -1 : current.id,
                                Ui.value(code), Ui.value(name), selectedImageUri,
                                Ui.value(fileType), genre.id, Ui.value(url), Ui.value(description));
                        dialog.dismiss();
                        loadSongs();
                        Ui.toast(this, current == null ? "Đã thêm file nhạc" : "Đã cập nhật file nhạc");
                    } catch (Exception e) {
                        Ui.showError(this, e);
                    }
                }));
        dialog.show();
    }

    private void updateImagePreview() {
        if (activeImagePreview == null) return;
        activeImagePreview.setImageDrawable(null);
        if (selectedImageUri == null || selectedImageUri.isEmpty()) {
            activeImagePreview.setImageResource(R.drawable.ic_launcher);
            return;
        }
        try {
            activeImagePreview.setImageURI(Uri.parse(selectedImageUri));
            if (activeImagePreview.getDrawable() == null) {
                activeImagePreview.setImageResource(R.drawable.ic_launcher);
            }
        } catch (Exception e) {
            activeImagePreview.setImageResource(R.drawable.ic_launcher);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null
                && data.getData() != null && activeImagePreview != null) {
            Uri uri = data.getData();
            try {
                int flags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, flags);
            } catch (SecurityException e) {
                Ui.toast(this, "Ảnh có thể không còn truy cập được sau khi khởi động lại.");
            }
            selectedImageUri = uri.toString();
            updateImagePreview();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (db != null && genreFilter != null) loadGenreFilter();
    }

    @Override protected void onDestroy() {
        activeImagePreview = null;
        if (db != null) db.close();
        super.onDestroy();
    }
}
