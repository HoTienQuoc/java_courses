# Exception Handling Trong Java - Lý Thuyết Chi Tiết và Đầy Đủ Nhất

## 1. Tổng quan về Exception (Ngoại lệ)
**Exception** là một sự kiện bất thường xảy ra trong quá trình thực thi chương trình, làm gián đoạn luồng thực thi bình thường. Khi xảy ra lỗi, Java sẽ tạo ra một đối tượng (Object) chứa thông tin về lỗi và "ném" (throw) nó.

**Mục đích của việc xử lý ngoại lệ:**
- Ngăn chương trình bị "crash" (dừng đột ngột) ngoài ý muốn.
- Tách biệt logic xử lý lỗi ra khỏi logic nghiệp vụ chính của chương trình.
- Giúp việc gỡ lỗi (debug) dễ dàng hơn nhờ thông tin Stack Trace.
- Đảm bảo các tài nguyên quan trọng (file, database connection, network socket) được đóng và giải phóng đúng cách.

---

## 2. Cấu trúc phân cấp (Hierarchy of Exceptions - Theo Slide)

Toàn bộ hệ thống lỗi trong Java được tổ chức thành một cây phân cấp. Lớp cao nhất là `Throwable`. Dưới đây là sơ đồ mô phỏng chính xác 100% nội dung slide bạn đã gửi:

```text
                                  Throwable
                                      │
               ┌──────────────────────┴──────────────────────┐
               │                                             │
             Error                                       Exception
               │                                             │
     ┌─────────┼─────────┐                   ┌───────────────┼───────────────┐
     │         │         │                   │               │               │
OutOfMemory StackOverflow ThreadDeath   RuntimeException IOException     SQLException
   Error        Error                         │
                                   ┌───────────┼────────────┬──────────────┐
                                   │           │            │              │
                             NullPointer  IllegalArgument Arithmetic  ArrayIndexOutOfBounds
                              Exception      Exception    Exception        Exception
                                   │                        │
                            NoSuchElement             ClassCast
                              Exception                Exception
```

### 2.1. Lớp `Throwable` (Gốc rễ - Khối xanh đậm)
- Là lớp cha của mọi lỗi và ngoại lệ trong Java.
- Chỉ các đối tượng kế thừa từ `Throwable` mới có thể được ném (`throw`) hoặc bắt (`catch`) bởi JVM.
- **Các phương thức quan trọng:**
  - `getMessage()`: Lấy thông điệp chi tiết của lỗi.
  - `toString()`: Trả về tên lớp lỗi và thông điệp.
  - `printStackTrace()`: In dấu vết lỗi (vị trí xảy ra lỗi) ra màn hình console để debug.
  - `getCause()`: Lấy nguyên nhân gốc rễ gây ra lỗi (dùng trong Exception Chaining).
  - `getStackTrace()`: Lấy mảng các StackTraceElement.

### 2.2. Nhánh `Error` (Khối màu cam/đỏ)
- Đại diện cho các lỗi **nghiêm trọng** ở mức hệ thống hoặc JVM (Java Virtual Machine). 
- Chương trình **KHÔNG NÊN** cố gắng bắt (catch) các lỗi này vì thông thường không thể phục hồi được.
- **Các lỗi tiêu biểu trong slide:**
  - `OutOfMemoryError`: Hết bộ nhớ RAM (Heap Space) để cấp phát cho đối tượng.
  - `StackOverflowError`: Tràn bộ nhớ Stack (thường do gọi hàm đệ quy vô hạn không có điểm dừng).
  - `ThreadDeath`: Lỗi xảy ra khi một luồng (thread) bị dừng đột ngột (thường bị Deprecated, ít dùng trong thực tế).

### 2.3. Nhánh `Exception` (Khối màu xanh lơ/xanh lá)
Đây là các vấn đề mà chương trình **có thể và nên xử lý**. Nhánh này được chia làm 2 loại cực kỳ quan trọng:

#### A. Unchecked Exception (RuntimeException - Khối xanh mòng két)
- **Đặc điểm:** Xảy ra lúc Runtime (khi chương trình đang chạy). **Không bắt buộc** phải xử lý (không cần try-catch). Thường do lỗi logic của lập trình viên.
- **Các lỗi tiêu biểu trong slide:**
  - `NullPointerException`: Gọi phương thức hoặc truy cập thuộc tính trên một object đang bị `null`.
  - `IllegalArgumentException`: Truyền tham số không hợp lệ vào phương thức.
  - `ArithmeticException`: Lỗi toán học (ví dụ: chia một số cho 0).
  - `NoSuchElementException`: Dùng `Iterator` hoặc `Scanner` để lấy phần tử nhưng không còn phần tử nào.
  - `ClassCastException`: Ép kiểu (cast) không hợp lệ (ví dụ ép một Object không phải String sang String).
  - `ArrayIndexOutOfBoundsException`: Truy cập mảng với chỉ số index ngoài phạm vi (ví dụ mảng có 5 phần tử nhưng truy cập index 10).

#### B. Checked Exception (Màu xanh lá/xanh dương)
- **Đặc điểm:** Được kiểm tra ngay lúc **Compile-time** (biên dịch). **Bắt buộc** phải xử lý (dùng try-catch hoặc throws) nếu không code sẽ báo lỗi đỏ. Thường do các yếu tố khách quan bên ngoài (file, mạng, database).
- **Các lỗi tiêu biểu trong slide:**
  - `IOException`: Lỗi liên quan đến luồng vào/ra (đọc/ghi file, mạng...).
  - `SQLException`: Lỗi khi thao tác với cơ sở dữ liệu (sai câu lệnh, mất kết nối...).

---

## 3. Các cơ chế xử lý ngoại lệ (Mechanisms)

### 3.1. Khối `try - catch`
Dùng để bọc đoạn code có thể gây lỗi và xử lý lỗi đó.
```java
try {
    // Code có thể ném Exception
    int result = 10 / 0; 
} catch (ArithmeticException e) {
    // Xử lý lỗi
    System.out.println("Không thể chia cho 0!");
}
```
**Quy tắc:** Phải catch lỗi **cụ thể** (con) trước, catch lỗi **tổng quát** (cha) sau (ví dụ: catch `ArithmeticException` trước `Exception`).

### 3.2. Khối `try - finally`
- Khối `finally` **LUÔN LUÔN** được thực thi, bất kể có Exception xảy ra hay không (trừ khi JVM bị crash hoặc gọi `System.exit()`).
- Dùng để giải phóng tài nguyên (đóng file, đóng kết nối DB).
- **Lưu ý:** Nếu có lệnh `return` trong `try` hoặc `catch`, thì `finally` vẫn chạy trước khi return. Nếu `finally` cũng có `return`, nó sẽ **ghi đè** kết quả return trước đó (Không khuyến khích dùng).

### 3.3. Khối `try - catch - finally`
Kết hợp cả 2 cơ chế trên.
```java
try {
    // Code nguy hiểm
} catch (Exception e) {
    // Xử lý lỗi
} finally {
    // Luôn chạy để dọn dẹp
}
```

### 3.4. Multi-catch (Java 7+)
Bắt nhiều loại lỗi trong cùng 1 block `catch` để code gọn hơn.
```java
try {
    // ...
} catch (IOException | SQLException e) {
    System.out.println("Đã xảy ra lỗi: " + e.getMessage());
}
```
*Lưu ý: Các exception bắt chung không được có quan hệ cha-con (ví dụ không thể gộp Exception và RuntimeException).*

### 3.5. try-with-resources (Java 7+)
Tự động đóng tài nguyên (gọi hàm `close()`) khi kết thúc khối try, kể cả khi có lỗi. Tài nguyên phải implement interface `AutoCloseable` hoặc `Closeable`.
```java
// Tự động đóng BufferedReader mà không cần viết finally
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
}
```
**Suppressed Exceptions:** Nếu lỗi xảy ra ở cả trong khối `try` và lúc `close()`, lỗi ở `close()` sẽ bị "ức chế" (suppressed) và chỉ hiển thị khi dùng `e.getSuppressed()`. Cơ chế này an toàn và code gọn hơn so với dùng `finally`.

---

## 4. Ném và Khai báo Ngoại lệ (`throw` và `throws`)

- **`throw`**: Ném ra một đối tượng Exception cụ thể tại một thời điểm trong code.
  ```java
  if (age < 0) {
      throw new IllegalArgumentException("Tuổi không hợp lệ");
  }
  ```
- **`throws`**: Khai báo ở chữ ký phương thức (method signature), báo hiệu rằng hàm này "có thể" ném ra lỗi để hàm gọi nó biết đường xử lý (thường dùng cho Checked Exception).
  ```java
  public void readFile() throws IOException {
      // ...
  }
  ```

---

## 5. Custom Exception (Ngoại lệ tùy chỉnh)
Tự tạo lớp lỗi riêng cho nghiệp vụ dự án để code dễ đọc và bảo trì hơn.
- Kế thừa `Exception` -> Tạo ra Checked Exception.
- Kế thừa `RuntimeException` -> Tạo ra Unchecked Exception.
```java
public class AgeInvalidException extends RuntimeException {
    public AgeInvalidException(String message) {
        super(message);
    }
}
```

---

## 6. Exception Chaining (Bọc ngoại lệ)
Kỹ thuật quan trọng khi làm việc với kiến trúc nhiều tầng (Controller -> Service -> Repository). Khi catch một lỗi ở tầng dưới, ta ném ra một lỗi mới ở tầng trên nhưng **giữ lại nguyên nhân gốc (cause)**.
```java
try {
    // Lỗi SQL
} catch (SQLException e) {
    // Ném lỗi mới, truyền e vào làm cause
    throw new ServiceException("Lỗi khi lấy dữ liệu người dùng", e); 
}
```
Khi xem StackTrace, bạn sẽ thấy cả lỗi `ServiceException` và lỗi gốc `SQLException`.

---

## 7. Lan truyền ngoại lệ (Exception Propagation)
Khi một hàm không có `try-catch` xử lý lỗi, lỗi đó sẽ tự động được ném ngược lên hàm gọi nó (caller). Quá trình tiếp tục cho đến khi gặp khối `catch` phù hợp, hoặc lan tới hàm `main()` và làm chương trình dừng (Crash).

---

## 8. Best Practices (Thực hành tốt nhất)

**NÊN LÀM ✅**
1. Bắt lỗi cụ thể (Catch specific exceptions) thay vì `Exception` chung chung.
2. Dùng `try-with-resources` để tự động đóng tài nguyên.
3. Ghi log đầy đủ khi có lỗi (`logger.error("...", e)`).
4. Ném lỗi (Fail fast) ngay khi phát hiện dữ liệu không hợp lệ từ đầu.
5. Sử dụng Exception Chaining khi ném lỗi ở tầng cao hơn.

**KHÔNG NÊN LÀM ❌**
1. Nuốt lỗi (Empty catch block - `catch(Exception e) {}`).
2. Catch `Throwable` hoặc `Error`.
3. Dùng exception để điều khiển luồng (Flow control).
4. In lỗi bằng `e.printStackTrace()` trong môi trường Production (nên dùng logging framework).
5. Dùng `return` bên trong khối `finally`.

---

## 9. Bảng tóm tắt nhanh

| Khái niệm               | Đặc điểm                         | Ví dụ                                         |
| :---------------------- | :------------------------------- | :-------------------------------------------- |
| **Error**               | Lỗi hệ thống, không thể phục hồi | `OutOfMemoryError`, `StackOverflowError`      |
| **Checked Exception**   | Bắt buộc xử lý (lỗi Compile)     | `IOException`, `SQLException`                 |
| **Unchecked Exception** | Không bắt buộc (lỗi Runtime)     | `NullPointerException`, `ArithmeticException` |
| **throw**               | Ném lỗi                          | `throw new RuntimeException();`               |
| **throws**              | Khai báo lỗi                     | `public void run() throws IOException`        |
| **finally**             | Luôn luôn chạy                   | Dùng để đóng file/DB                          |
| **try-with-resources**  | Tự động đóng tài nguyên          | `try (FileReader fr = ...)`                   |

***
