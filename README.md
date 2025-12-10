# 📱 QR Master -- Ứng Dụng Quét QR / Barcode Chuyên Nghiệp

QR Master là ứng dụng Android cho phép quét mã **QR Code** và
**Barcode**, tự động lưu lịch sử, phân loại mã, và hỗ trợ truy vấn thông
tin sản phẩm thông qua API.\
Ứng dụng được xây dựng theo **Clean Architecture**, có sử dụng **Hilt**,
**Room Database**, **Retrofit -- OkHttp**, và **Jetpack Compose**.

------------------------------------------------------------------------

## ✨ Tính năng chính

### 🔍 1. Quét mã QR & Barcode

-   Hỗ trợ nhiều định dạng: EAN-13, EAN-8, Code-128, QR, UPC...
-   Tự động nhận diện nhanh, chính xác.
-   Chế độ quét **Auto Mode / Manual Mode**.

### 🗂 2. Lưu lịch sử quét

-   Tự động lưu tất cả các mã đã quét vào Room Database.
-   Có thể:
    -   Xem lại
    -   Sao chép
    -   Chia sẻ
    -   Xoá từng mục hoặc xoá toàn bộ

### 🗃 3. Phân loại mã

-   Phân loại theo: URL, Text, Product Code, Phone, Email, WiFi...
-   Có icon minh hoạ tương ứng.

### 🔎 4. Truy vấn nguồn gốc & thông tin sản phẩm

-   Gửi mã barcode lên server thông qua API.
-   Sử dụng **Retrofit + OkHttp**.
-   Header tự động truyền token.
-   Token tự động refresh khi hết hạn (nếu API yêu cầu).

### ⚙ 5. Kiến trúc & Công nghệ sử dụng

-   **Clean Architecture**
    -   Domain
    -   Data
    -   Presentation
-   **Hilt (Dagger)** -- Dependency Injection
-   **Room Database** -- Lưu lịch sử quét
-   **Retrofit + OkHttp** -- Gọi API truy vấn sản phẩm
-   **Coroutines -- Flow**
-   **Jetpack Compose** -- Xây UI
-   **CameraX** -- Xử lý camera
-   **MLKit / ZXing** -- Quét mã
