# 🎵 BTL – QUẢN LÝ FILE NHẠC (ANDROID STUDIO – JAVA)

Ứng dụng Android quản lý thư viện nhạc dành cho **bài tập lớn**. Giao diện tiếng Việt, viết bằng **Java**, giao diện **XML**, dữ liệu **SQLite lưu trên thiết bị**, không yêu cầu máy chủ hay tài khoản.

## 1. Chức năng theo yêu cầu đề bài

| Yêu cầu | Màn hình | Triển khai |
|---|---|---|
| Thêm/sửa/xóa thể loại (mã, tên, lứa tuổi nghe) | Quản lý thể loại | Khoảng tuổi từ 0–120; mã duy nhất; kiểm tra nhập liệu |
| Thêm/sửa/xóa file nhạc | Quản lý file nhạc | Mã, tên, hình ảnh, loại file, thể loại, link tải, mô tả |
| Liệt kê file nhạc theo thể loại | Quản lý file nhạc **và** Báo cáo | Chọn thể loại trong danh sách để lọc |
| Tìm link tải cho người trên 40 tuổi | Báo cáo & truy vấn | Lọc nhạc có thể loại có `max_age >= 41`, tìm thêm theo mã/tên |
| Báo cáo số lượng file theo thể loại | Báo cáo & truy vấn | Đếm và biểu diễn bằng thanh tiến độ |

### Các tiện ích thêm
- Trang chủ đẹp hơn, thống kê thể loại/file nhạc, ba file gần nhất.
- Tìm theo mã/tên; lọc bằng Spinner.
- Xem ảnh bìa bằng bộ chọn file Android (Storage Access Framework).
- Mở link tải với trình duyệt; sao chép URL vào bộ nhớ tạm.
- Cảnh báo xác nhận trước khi xóa; không cho xóa thể loại còn bài nhạc.
- SQLite có khóa ngoại, unique code, kiểm tra khoảng tuổi; giữ dữ liệu nếu nâng cấp từ project cũ phiên bản 1.
- Có trạng thái trống cho danh sách chưa có dữ liệu.

**Giới hạn thiết kế:** ứng dụng quản lý *thông tin file nhạc và đường dẫn*, không tự tải file về, không nghe MP3 trong app và không đi tìm nhạc trên Internet. Các URL phải do người dùng nhập, nên chất lượng/khả dụng của link phụ thuộc nguồn của người dùng.

## 2. Cách mở project trên Android Studio (Windows)

1. Giải nén `BTLQuanLyNhac_Pro_Java.zip` vào thư mục bình thường (ví dụ `D:\Android\BTLQuanLyNhac_Pro`).
2. Chọn **Android Studio → File → Open** và mở chính thư mục `BTLQuanLyNhac_Pro` (thư mục chứa `settings.gradle`).
3. Chờ **Gradle Sync**. Hãy có kết nối Internet trong lần đồng bộ đầu tiên để tải Gradle/Android Gradle Plugin.
4. Nếu IDE hỏi, cài **Android SDK Platform 35** và **JDK 17+** (Android Studio thường có Embedded JDK).
5. Chọn emulator Android (API 23+) hoặc điện thoại thật bật **USB debugging**.
6. Bấm **Run ▶**, sau đó vào **Quản lý thể loại** → **Quản lý file nhạc** → **Báo cáo & truy vấn**.

**Thông số build:** AGP `8.9.2` · Gradle `8.11.1` · compile/target SDK `35` · min SDK `23` · Java `17`. Không cần thư viện của bên thứ ba.

> Ghi chú về Gradle Wrapper: Môi trường tạo mã nguồn không thể tải trực tiếp JAR chuẩn của Gradle. Vì vậy project đi kèm một **bootstrap JAR tối giản, có mã nguồn ở `tools/wrapper-bootstrap`**, để lần đầu tự lấy bản phân phối Gradle 8.11.1 từ trang chính thức và kiểm SHA-256 trước khi chạy. Sau khi sync trên máy, bạn có thể thay thế bằng Gradle Wrapper chính thức bằng `gradle wrapper --gradle-version 8.11.1`. Đây **không phải** JAR Gradle Wrapper do Gradle phát hành. Nếu IDE có cấu hình Gradle cục bộ, bạn cũng có thể sử dụng cấu hình đó.

## 3. Quy trình demo bài tập lớn

**Bước A:** Vào **Thể loại nhạc**, kiểm tra các thể loại mẫu: Nhạc trẻ, Nhạc vàng, Nhạc trữ tình, Nhạc thiếu nhi.

**Bước B:** Vào **File nhạc → Thêm file nhạc**:
- Mã: `BH001`
- Tên: `Bài nhạc yêu thích`
- Loại file: `MP3`
- Thể loại: `Nhạc vàng` (35–100 tuổi)
- Link: sử dụng link HTTPS thật tới file/website mà bạn được phép tải hoặc chia sẻ.
- Hình ảnh: chọn từ ảnh có sẵn trên điện thoại.
- Mô tả: `Bài nhạc minh họa cho bài tập lớn`.

**Bước C:** Nhập bài thứ hai vào **Nhạc thiếu nhi** (0–14 tuổi), và quan sát số bài tăng.

**Bước D:** Vào **Báo cáo & truy vấn**:
1. Xem bảng đếm số file theo thể loại.
2. Thử chọn **Nhạc vàng** ở phần liệt kê bài nhạc theo thể loại.
3. Ở phần **người trên 40 tuổi**, bài thuộc **Nhạc vàng** được hiển thị; bài thuộc **Nhạc thiếu nhi** không được hiển thị.
4. Bấm **Sao chép** hoặc **Mở link** trên dòng bài nhạc.

**Bước E:** Sửa và xóa dữ liệu, thử nhập mã trùng, thử xóa thể loại đang có bài để kiểm tra xác thực.

## 4. Cấu trúc project

```text
BTLQuanLyNhac_Pro/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/btlquanlynhac/
│       │   ├── MainActivity.java        // Trang chủ
│       │   ├── GenreActivity.java       // CRUD thể loại
│       │   ├── SongActivity.java        // CRUD nhạc, tìm kiếm, ảnh
│       │   ├── ReportActivity.java      // Báo cáo và truy vấn
│       │   ├── MusicDatabase.java       // SQLiteOpenHelper + CRUD
│       │   ├── Genre.java / Song.java   // Models
│       │   ├── GenreAdapter.java / SongAdapter.java
│       │   └── Ui.java                  // Trợ giúp UI, validation
│       └── res/
│           ├── layout/                 // XML màn hình, dialog, item
│           ├── drawable/               // Nền, icon, button
│           └── values/                 // Colors & styles
├── gradle/wrapper/
├── docs/
│   ├── CO_SO_DU_LIEU.md
│   └── KIEM_THU.md
└── tools/wrapper-bootstrap/          // Nguồn bootstrap Gradle
```

## 5. Dữ liệu và lưu trữ

- File SQLite: `music_manager.db`, có 2 bảng `genres` và `songs`.
- Quan hệ: `genres (1) → (N) songs`.
- Mã thể loại và mã nhạc phải duy nhất, không phân biệt hoa/thường trong DB mới.
- Xóa thể loại đang có bài nhạc sẽ bị chặn (`ON DELETE RESTRICT`).
- Công thức người trên 40: `g.max_age >= 41` (có ít nhất một tuổi phù hợp từ 41 trở lên).
- Ảnh dùng `content://` URI; quyền truy cập được giữ lại nếu bộ chọn hỗ trợ.
- Lần đầu khởi chạy có 4 thể loại mẫu; không có link tải bịa đặt.

## 6. Các vấn đề thường gặp

**Gradle sync failed**: kiểm tra kết nối Internet, SDK Platform 35, Embedded JDK 17, thử **File → Sync Project with Gradle Files**. Nếu công ty chặn domain tải Gradle, cần cài Gradle bằng phương pháp mà mạng của bạn cho phép.

**Không hiện ảnh bìa**: xem quyền truy cập ảnh trong bộ chọn; nếu ảnh gốc bị xóa hoặc provider thu hồi quyền, chọn lại ảnh.

**Link tải không mở**: thiết bị cần trình duyệt và kết nối Internet, URL có định dạng HTTP(S) và đích còn hoạt động.

**Không xóa được thể loại**: xóa hoặc chuyển tất cả file nhạc thuộc thể loại đó trước.

## 7. Lưu ý kiểm thử

Mã nguồn đã được kiểm tra cú pháp XML, đường dẫn resource và một số điều kiện SQL bằng bộ kiểm thử cục bộ. **Chưa thể chạy `assembleDebug` hoặc emulator trong môi trường tạo mã** do không có Android SDK đầy đủ và không thể lấy Gradle từ Internet; hãy mở project trên Android Studio để xác nhận bước build cuối cùng.
