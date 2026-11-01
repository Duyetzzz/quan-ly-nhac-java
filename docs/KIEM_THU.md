# Danh sách kiểm thử thủ công trên Android Studio

| STT | Kịch bản | Kết quả mong đợi |
|---|---|---|
| 01 | Chạy app lần đầu | Trang chủ, 4 thể loại mẫu, 0 file nhạc |
| 02 | Thêm thể loại hợp lệ | Thể loại xuất hiện trong danh sách và spinner |
| 03 | Thêm mã thể loại trùng | Báo lỗi, không thêm bản ghi trùng |
| 04 | Thêm thể loại tuổi 60–30 | Cảnh báo khoảng tuổi không hợp lệ |
| 05 | Thêm 1 file nhạc có URL HTTPS | Bản ghi được lưu, có ảnh bìa nếu chọn |
| 06 | Thêm file nhạc có mã trùng | Báo lỗi, không tạo hai mã giống nhau |
| 07 | Thêm file với URL thiếu http/https | Hiển thị lỗi cạnh ô link |
| 08 | Tìm theo mã/tên | Danh sách lọc đúng |
| 09 | Chọn thể loại ở danh sách file nhạc | Chỉ file thuộc thể loại đã chọn |
| 10 | Sửa thể loại | Thông tin các file thuộc thể loại cập nhật theo |
| 11 | Sửa thông tin bài nhạc, chọn ảnh mới | Danh sách hiển thị bản cập nhật |
| 12 | Nhấn Mở link / Sao chép | Trình duyệt mở URL / URL được sao chép |
| 13 | Xóa thể loại đang có bài | Thông báo không cho xóa |
| 14 | Xóa file nhạc có xác nhận | File biến mất, số lượng thống kê giảm |
| 15 | Xem bảng thống kê | Số lượng file theo từng thể loại chính xác |
| 16 | Liệt kê file theo thể loại trong báo cáo | Đúng các file đang lưu thuộc thể loại chọn |
| 17 | Truy vấn link nhạc >40 | Chỉ nhạc có max_age >= 41 |
| 18 | Tắt/mở ứng dụng | Dữ liệu SQLite vẫn còn |
| 19 | Chọn ảnh bằng trình chọn Android | Ảnh được lưu URI và vẫn hiện sau khi khởi động lại (nếu provider hỗ trợ) |
| 20 | Thử trên màn hình điện thoại nhỏ | Có thể cuộn biểu mẫu, không mất nút chính |

**Trạng thái:** bảng là hướng dẫn kiểm thử để thực hiện trên Android Studio, không phải danh sách test đã chạy trên thiết bị thật.
