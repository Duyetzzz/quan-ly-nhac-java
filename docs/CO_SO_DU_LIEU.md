# Cơ sở dữ liệu và truy vấn – Quản lý file nhạc

## Mô hình quan hệ

```text
genres (THỂ LOẠI NHẠC)                songs (FILE NHẠC)
---------------------------          ---------------------------------
id          INTEGER  PK   1 ------- N genre_id         INTEGER  FK
code        TEXT     UNIQUE           id               INTEGER  PK
name        TEXT                      code             TEXT     UNIQUE
min_age     INTEGER                   name             TEXT
max_age     INTEGER                   image_uri        TEXT
                                      file_type        TEXT
                                      download_url     TEXT
                                      description      TEXT
```

### Ràng buộc nghiệp vụ
- `genres.min_age` trong khoảng 0..120.
- `genres.max_age >= genres.min_age` và <= 120.
- `songs.genre_id` bắt buộc trỏ đến một thể loại hiện có.
- Mã thể loại/bài hát không trùng (với DB mới: không phân biệt hoa thường).
- Xóa thể loại có bài nhạc bị từ chối.

## Truy vấn 1: Liệt kê bài nhạc theo thể loại

```sql
SELECT s.code, s.name, s.file_type, s.download_url, g.name AS genre_name
FROM songs s
JOIN genres g ON s.genre_id = g.id
WHERE g.id = ?
ORDER BY s.id DESC;
```

## Truy vấn 2: Tìm link tải file nhạc dành cho người trên 40 tuổi

```sql
SELECT s.code, s.name, s.download_url, g.name AS genre_name,
       g.min_age, g.max_age
FROM songs s
JOIN genres g ON s.genre_id = g.id
WHERE g.max_age >= 41
ORDER BY s.id DESC;
```

Chú ý: `max_age >= 41` tức là thể loại có thể phù hợp với ít nhất một độ tuổi lớn hơn 40; không có nghĩa tất cả người nghe của thể loại đều hơn 40 tuổi.

## Truy vấn 3: Thống kê số lượng bài theo thể loại

```sql
SELECT g.code, g.name, g.min_age, g.max_age, COUNT(s.id) AS song_count
FROM genres g
LEFT JOIN songs s ON s.genre_id = g.id
GROUP BY g.id
ORDER BY g.name COLLATE NOCASE;
```

## Truy vấn 4: Tìm theo mã hoặc tên

```sql
SELECT s.code, s.name, g.name AS genre_name
FROM songs s
JOIN genres g ON g.id = s.genre_id
WHERE s.code LIKE ? OR s.name LIKE ?;
-- Bind cả 2 tham số với '%<từ khóa>%'.
```

Các truy vấn trong ứng dụng được viết ở `MusicDatabase.java`. Ứng dụng dùng tham số `?` cho dữ liệu người dùng, không nối trực tiếp chuỗi nhập vào SQL.
