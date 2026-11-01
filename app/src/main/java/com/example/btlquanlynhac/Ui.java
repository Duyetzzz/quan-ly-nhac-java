package com.example.btlquanlynhac;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;

public final class Ui {
    private Ui() { }
    public static String value(EditText text) { return text.getText().toString().trim(); }

    public static boolean require(EditText field, String title) {
        if (value(field).isEmpty()) {
            field.setError("Vui lòng nhập " + title);
            field.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validUrl(String link) {
        try {
            Uri uri = Uri.parse(link.trim());
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null && !uri.getHost().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public static void openLink(Activity activity, String url) {
        if (!validUrl(url)) {
            toast(activity, "Link không hợp lệ. Chỉ hỗ trợ http và https.");
            return;
        }
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            toast(activity, "Thiết bị chưa có ứng dụng mở liên kết.");
        } catch (Exception e) {
            showError(activity, e);
        }
    }

    public static void copyLink(Activity activity, String url) {
        ClipboardManager clip = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clip != null) {
            clip.setPrimaryClip(ClipData.newPlainText("Link tải nhạc", url));
            toast(activity, "Đã sao chép link tải");
        }
    }

    public static void toast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public static void showError(Activity activity, Exception e) {
        String message = e.getMessage() == null ? "Đã xảy ra lỗi, vui lòng thử lại." : e.getMessage();
        if (e instanceof SQLiteConstraintException || message.contains("UNIQUE constraint failed")) {
            message = "Mã đã tồn tại hoặc dữ liệu vi phạm ràng buộc. Hãy kiểm tra mã nhập.";
        } else if (message.contains("FOREIGN KEY constraint failed")) {
            message = "Dữ liệu đang được sử dụng; không thể xóa trực tiếp.";
        }
        new AlertDialog.Builder(activity).setTitle("Không thể thực hiện")
                .setMessage(message).setPositiveButton("Đóng", null).show();
    }

    public static void confirmDelete(Activity activity, String message, Runnable action) {
        new AlertDialog.Builder(activity)
                .setTitle("Xác nhận xóa").setMessage(message)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (d, which) -> {
                    try { action.run(); } catch (Exception e) { showError(activity, e); }
                }).show();
    }
}
