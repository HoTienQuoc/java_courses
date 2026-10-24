Chào bạn, chúng ta đã đi đến nguyên lý cuối cùng và cũng là nguyên lý "đỉnh cao" nhất trong bộ 5 nguyên lý SOLID: **Chữ D - Dependency Inversion Principle (Nguyên lý Đảo ngược Phụ thuộc)**.

Nguyên lý này chính là nền tảng của rất nhiều Framework nổi tiếng hiện nay như Spring Boot, và là "chìa khóa" để viết ra những đoạn code lỏng lẻo (loosely coupled), cực kỳ dễ test và dễ bảo trì.

---

### 1. ĐỊNH NGHĨA

Nguyên lý này gồm 2 câu thần chú chính:
1.  **"Các module cấp cao (High-level) không nên phụ thuộc vào các module cấp thấp (Low-level). Cả hai nên phụ thuộc vào các Abstraction (Interface/Abstract Class)."**
2.  **"Các Abstraction không nên phụ thuộc vào chi tiết (Details). Ngược lại, các chi tiết (cách cài đặt cụ thể) nên phụ thuộc vào Abstraction."**

**Giải thích các khái niệm:**
*   **High-level module:** Là nơi chứa logic nghiệp vụ (Business logic) cốt lõi của hệ thống. Ví dụ: Class xử lý đặt hàng, tính tiền.
*   **Low-level module:** Là nơi chứa các công cụ, chi tiết kỹ thuật. Ví dụ: Class gửi email, class kết nối Database, class gọi API.
*   **Abstraction:** Là Interface hoặc Abstract Class.

**Tại sao gọi là "Đảo ngược" (Inversion)?**
Bình thường, bạn viết code, module cấp cao sẽ gọi trực tiếp module cấp thấp (Ví dụ: Class Đặt Hàng gọi trực tiếp Class Gửi Email). Mũi tên phụ thuộc đi từ trên xuống dưới.
Nguyên lý này yêu cầu "đảo ngược" lại: Cả hai phải hướng về một Interface ở giữa.

---

### 2. VÍ DỤ: VI PHẠM DIP (Bad Example)

Giả sử bạn có một hệ thống xử lý thanh toán. Ban đầu, hệ thống chỉ hỗ trợ thanh toán qua PayPal.

```java
// ❌ MODULE CẤP THẤP (Low-level)
public class PayPalPayment {
    public void pay(double amount) {
        System.out.println("Thanh toán " + amount + " qua PayPal.");
    }
}

// ❌ MODULE CẤP CAO (High-level - Chứa logic nghiệp vụ)
public class CheckoutService {
    // Phụ thuộc trực tiếp vào class cụ thể (Low-level)
    private PayPalPayment payPalPayment = new PayPalPayment();

    public void processOrder(double amount) {
        // Logic nghiệp vụ...
        System.out.println("Đang xử lý đơn hàng...");
        
        // Gọi trực tiếp hàm thanh toán
        payPalPayment.pay(amount); 
    }
}
```
*   **Vấn đề ở đâu?**
    *   `CheckoutService` (cấp cao) đang phụ thuộc trực tiếp vào `PayPalPayment` (cấp thấp). Chúng bị **gắn chặt (tight coupling)** vào nhau.
    *   Ngày mai, sếp yêu cầu thêm cổng thanh toán Momo. Bạn buộc phải mở class `CheckoutService` ra, sửa lại code để thêm `MomoPayment`. Bạn đã vi phạm **nguyên lý Open/Closed (OCP)**.
    *   Bạn không thể viết Unit Test cho `CheckoutService` một cách độc lập, vì muốn test nó, bạn phải kết nối mạng tới PayPal thật.

---

### 3. VÍ DỤ: TUÂN THỦ DIP (Good Example)

Cách khắc phục là tạo ra một **Interface (Abstraction)**. Cả module cấp cao và cấp thấp đều phụ thuộc vào Interface này.

```java
// ✅ BƯỚC 1: TẠO ABSTRACTION (INTERFACE)
public interface PaymentMethod {
    void pay(double amount);
}

// ✅ BƯỚC 2: CÁC MODULE CẤP THẤP THỰC THI INTERFACE
public class PayPalPayment implements PaymentMethod {
    @Override
    public void pay(double amount) { System.out.println("Thanh toán qua PayPal"); }
}

public class MomoPayment implements PaymentMethod {
    @Override
    public void pay(double amount) { System.out.println("Thanh toán qua Momo"); }
}

// ✅ BƯỚC 3: MODULE CẤP CAO CHỈ PHỤ THUỘC VÀO INTERFACE
public class CheckoutService {
    private PaymentMethod paymentMethod;

    // Tiêm sự phụ thuộc (Dependency Injection) qua Constructor
    public CheckoutService(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void processOrder(double amount) {
        System.out.println("Đang xử lý đơn hàng...");
        // Gọi qua Interface, không cần biết cụ thể là PayPal hay Momo
        paymentMethod.pay(amount); 
    }
}
```
*   **Sử dụng:**
```java
// Lúc chạy thực tế, ta "tiêm" (inject) cổng thanh toán mong muốn vào
CheckoutService service1 = new CheckoutService(new PayPalPayment());
service1.processOrder(100);

CheckoutService service2 = new CheckoutService(new MomoPayment());
service2.processOrder(200);
```

**Lợi ích to lớn:**
*   `CheckoutService` (cấp cao) **không còn biết** chi tiết bên dưới là cổng thanh toán nào.
*   Mai mốt thêm VNPay, ZaloPay, bạn **không cần sửa một dòng code nào** trong `CheckoutService`. Bạn chỉ việc tạo class mới `implements PaymentMethod`. (Tuân thủ OCP).
*   Dễ dàng viết Unit Test bằng cách tạo ra một `MockPayment` (giả lập thanh toán) để test mà không cần gọi API thật.
*   **Kỹ thuật "tiêm" đối tượng vào Constructor như trên được gọi là Dependency Injection (DI) - Công cụ để thực hiện DIP.**

---

### 4. DẤU HIỆU NHẬN BIẾT VI PHẠM DIP

1.  Bạn thấy từ khóa **`new`** xuất hiện bên trong các class chứa logic nghiệp vụ (High-level) để tạo ra các class công cụ (Low-level). (Ví dụ: `new EmailSender()`, `new MySqlConnection()`).
2.  Bạn muốn thay thế một thư viện (ví dụ đổi từ MySQL sang MongoDB) nhưng phải sửa code ở hàng chục file khác nhau.
3.  Bạn không thể viết Unit Test cho logic nghiệp vụ vì nó dính chặt với Database, Mail, File System...

### 💡 TÓM TẮT TOÀN BỘ SOLID
Chúc mừng bạn đã đi hết bộ 5 nguyên lý! Hãy nhớ:
*   **S (SRP):** Mỗi class chỉ làm 1 việc.
*   **O (OCP):** Mở để mở rộng, đóng để sửa đổi (thêm code mới, hạn chế sửa code cũ).
*   **L (LSP):** Lớp con phải thay thế được lớp cha mà không gây lỗi.
*   **I (ISP):** Chia nhỏ Interface, không ép class dùng hàm không cần thiết.
*   **D (DIP):** Giao tiếp qua Interface/Abstract, không giao tiếp qua class cụ thể. (Lập trình hướng tới Interface).

**Lời khuyên thực tế:** SOLID là kim chỉ nam, không phải là luật tuyệt đối. Đừng cố gắng áp dụng nó một cách máy móc vào những bài tập nhỏ (như hàm tính tổng 2 số). Chỉ khi dự án lớn dần và phức tạp lên, bạn sẽ thấy giá trị thực sự của SOLID tỏa sáng!