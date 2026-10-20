# `javac` vs `java` — Khác nhau ở đâu?

## Tóm tắt một câu

| Lệnh | Vai trò | Input | Output |
|------|---------|-------|--------|
| `javac` | **Compiler** (trình biên dịch) | `.java` (source code) | `.class` (bytecode) |
| `java` | **Launcher / Runtime** (chạy chương trình) | `.class` (bytecode) hoặc `.java` (từ Java 11) | Kết quả chạy chương trình |

---

## 1. `javac` — Trình biên dịch

```bash
javac helloworld.java
```

- Dịch mã nguồn Java (`.java`) → **bytecode** (`.class`) hiểu bởi JVM.
- Sau khi chạy, bạn sẽ thấy file `helloworld.class` xuất hiện trong thư mục.
- **Không chạy** chương trình. Nó chỉ kiểm tra cú pháp và tạo bytecode.
- Nếu code có lỗi cú pháp → báo lỗi, không tạo `.class`.

Cách chạy kiểu truyền thống (2 bước):

```bash
javac helloworld.java   # Bước 1: biên dịch → tạo helloworld.class
java helloworld         # Bước 2: chạy class (LƯU Ý: không có .class, không có .java)
```

> ⚠️ Lưu ý quan trọng: khi chạy `java helloworld`, **không** viết đuôi `.class`. Bạn đang gọi **tên class**, không phải tên file.

---

## 2. `java` — Chương trình chạy (Launcher)

`java` khởi động JVM và thực thi bytecode. Có **2 chế độ**:

### Chế độ cổ điển (chạy file `.class`)

```bash
java helloworld
```

→ JVM load `helloworld.class` rồi chạy `main()`.

### Chế độ mới (chạy thẳng file `.java`) — từ **Java 11**

```bash
java helloworld.java
```

→ Đây chính là cái bạn vừa chạy! **Đây là "source-file mode"**.

JVM sẽ **tự động**:

1. Biên dịch `helloworld.java` trong bộ nhớ (không tạo file `.class` ra đĩa),
2. Chạy luôn kết quả.

Vì vậy bạn thấy kết quả `Hello World` mà **không** cần gõ `javac` trước.

---

## Vậy tại sao bạn vẫn phải chạy `javac` trước?

Trong terminal của bạn:

```bash
javac helloworld.java   # tạo helloworld.class
java helloworld.java    # chạy thẳng file .java (source-file mode, bỏ qua .class vừa tạo)
```

Bước `javac` thực ra **không cần thiết** trong trường hợp này — bạn có thể chỉ gõ:

```bash
java helloworld.java
```

và nó vẫn chạy được (nhờ source-file mode). Bạn chạy `javac` trước là do thói quen / hướng dẫn cũ.

---

## Khi nào dùng cái nào?

### Dùng `javac` + `java helloworld` (2 bước) khi:

- Bạn có **nhiều file** `.java` phụ thuộc nhau.
- Cần build project lớn (thực tế người ta dùng Maven/Gradle, gọi `javac` bên dưới).
- Cần kiểm tra lỗi biên dịch mà **không chạy** chương trình.
- Cần đóng gói `.class`/`.jar` để phân phối.
- Môi trường production / CI.

### Dùng `java helloworld.java` (1 bước) khi:

- Học Java, viết script nhỏ, demo nhanh.
- File đơn lẻ, không phụ thuộc file khác.
- Java **11 trở lên**.

---

## Kiểm tra phiên bản Java của bạn

```bash
java -version
javac -version
```

Hai lệnh có thể cho version khác nhau nếu bạn cài nhiều JDK. Điều này giải thích tại sao đôi khi `javac` báo lỗi nhưng `java` chạy được (hoặc ngược lại) — chúng có thể đang trỏ tới 2 JDK khác nhau.

---

## Sơ đồ tư duy

```
helloworld.java  ──javac──►  helloworld.class  ──java──►  🖥️ Chạy
       │
       └────────────java (source-file mode, Java 11+)────────►  🖥️ Chạy
                     (bỏ qua .class, biên dịch trong RAM)
```

**Tóm lại:** `javac` = *dịch* (compile), `java` = *chạy* (run). Từ Java 11, `java` có thể "bao trọn" cả 2 bước cho file đơn lẻ.