package com.example.btlquanlynhac;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class GenreActivity extends Activity {
    private MusicDatabase db;
    private GenreAdapter adapter;
    private TextView total, empty;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
        db = new MusicDatabase(this);
        adapter = new GenreAdapter(this, new GenreAdapter.Actions() {
            @Override public void onEdit(Genre genre) { editGenre(genre); }
            @Override public void onDelete(Genre genre) { deleteGenre(genre); }
        });
        ((ListView) findViewById(R.id.genreList)).setAdapter(adapter);
        total = findViewById(R.id.genreTotal);
        empty = findViewById(R.id.genreEmpty);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddGenre).setOnClickListener(v -> editGenre(null));
        load();
    }

    private void load() {
        List<Genre> items = db.getGenres();
        adapter.replace(items);
        total.setText(items.size() + " thể loại");
        empty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void deleteGenre(Genre genre) {
        if (genre.songCount > 0) {
            Ui.toast(this, "Thể loại đang có bài nhạc, không được xóa.");
            return;
        }
        Ui.confirmDelete(this, "Xóa thể loại “" + genre.name + "”?", () -> {
            db.deleteGenre(genre);
            Ui.toast(this, "Đã xóa thể loại");
            load();
        });
    }

    private void editGenre(Genre genre) {
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_genre, null);
        EditText code = content.findViewById(R.id.editGenreCode);
        EditText name = content.findViewById(R.id.editGenreName);
        EditText min = content.findViewById(R.id.editMinAge);
        EditText max = content.findViewById(R.id.editMaxAge);
        if (genre != null) {
            code.setText(genre.code);
            name.setText(genre.name);
            min.setText(String.valueOf(genre.minAge));
            max.setText(String.valueOf(genre.maxAge));
        } else {
            min.setText("0");
            max.setText("100");
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(genre == null ? "Thêm thể loại nhạc" : "Chỉnh sửa thể loại")
                .setView(content).setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    if (!Ui.require(code, "mã thể loại") || !Ui.require(name, "tên thể loại") ||
                            !Ui.require(min, "tuổi nhỏ nhất") || !Ui.require(max, "tuổi lớn nhất")) return;
                    try {
                        int low = Integer.parseInt(Ui.value(min));
                        int high = Integer.parseInt(Ui.value(max));
                        if (low < 0 || high > 120 || low > high) {
                            Ui.toast(this, "Khoảng tuổi phải từ 0 đến 120 và tuổi đầu không vượt tuổi cuối.");
                            return;
                        }
                        db.saveGenre(genre == null ? -1 : genre.id, Ui.value(code),
                                Ui.value(name), low, high);
                        dialog.dismiss();
                        load();
                        Ui.toast(this, genre == null ? "Đã thêm thể loại" : "Đã cập nhật thể loại");
                    } catch (NumberFormatException ex) {
                        Ui.toast(this, "Tuổi nghe phải là số nguyên.");
                    } catch (Exception ex) {
                        Ui.showError(this, ex);
                    }
                }));
        dialog.show();
    }

    @Override protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }
}
