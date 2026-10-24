### 1. ĐỊNH NGHĨA

> *"Các thực thể phần mềm (lớp, module, hàm...) nên **mở để mở rộng (Open for extension)**, nhưng **đóng để sửa đổi (Closed for modification)**."*

*   **Mở để mở rộng (Open for extension):** Bạn có thể dễ dàng thêm các tính năng mới (hành vi mới) vào hệ thống.
*   **Đóng để sửa đổi (Closed for modification):** Khi thêm tính năng mới, bạn **không được phép sửa đổi** mã nguồn của các class đã tồn tại và đang chạy ổn định.

**Tại sao lại phải "đóng"?** Vì khi bạn mở một class đã được kiểm thử (tested) và đang chạy tốt trên môi trường production ra để sửa code, bạn sẽ rất dễ vô tình tạo ra lỗi (bug) làm hỏng các tính năng cũ. Nguyên lý OCP khuyên bạn nên "thêm" code mới thay vì "sửa" code cũ.

---

### 2. VÍ DỤ: VI PHẠM OCP (Bad Example)

Hãy tưởng tượng bạn đang viết một hệ thống tính chiết khấu (Discount) cho khách hàng. Ban đầu, hệ thống chỉ có 2 loại khách: Thường (Regular) và VIP.

```java
// ❌ CODE VI PHẠM OCP
public class DiscountCalculator {

    public double calculateDiscount(String customerType, double amount) {
        if (customerType.equals("REGULAR")) {
            return amount * 0.05; // Giảm 5%
        } 
        else if (customerType.equals("VIP")) {
            return amount * 0.10; // Giảm 10%
        }
        return 0;
    }
}
```
*   **Vấn đề ở đâu?** 
    *   Bây giờ, sếp yêu cầu thêm loại khách hàng "VIP_PLATINUM" giảm 20%. Bạn buộc phải mở class `DiscountCalculator` ra và sửa lại hàm `calculateDiscount` (thêm một lệnh `else if` nữa).
    *   Bạn đã **vi phạm nguyên lý OCP** vì đã sửa đổi class đang hoạt động ổn định.
    *   Nếu có 100 loại khách hàng, hàm `if-else` này sẽ dài vô tận, rất khó đọc và dễ sinh lỗi.

---

### 3. VÍ DỤ: TUÂN THỦ OCP (Good Example)

Để tuân thủ OCP, chúng ta sử dụng **Tính Đa hình (Polymorphism)** và **Interface**. Chúng ta tách mỗi loại chiết khấu ra thành một class riêng biệt.

```java
// ✅ Bước 1: Tạo một Interface chung cho việc tính chiết khấu
public interface DiscountStrategy {
    double calculate(double amount);
}

// ✅ Bước 2: Mỗi loại khách hàng là một class thực thi Interface này
public class RegularDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) {
        return amount * 0.05;
    }
}

public class VipDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) {
        return amount * 0.10;
    }
}

// ✅ Bước 3: Class Calculator giờ đây không cần biết chi tiết, nó chỉ làm việc với Interface
public class DiscountCalculator {
    public double calculateDiscount(DiscountStrategy strategy, double amount) {
        return strategy.calculate(amount);
    }
}
```
*   **Giải thích bên cạnh:**
    *   **Closed for modification:** Nếu mai mốt sếp yêu cầu thêm khách hàng "VIP_PLATINUM", bạn **KHÔNG CẦN** sửa đổi một dòng code nào trong class `DiscountCalculator` hay `RegularDiscount` cả. Hệ thống cũ vẫn an toàn tuyệt đối.
    *   **Open for extension:** Bạn chỉ cần tạo một class mới là xong:
```java
public class VipPlatinumDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) {
        return amount * 0.20;
    }
}
```
*   **Cách sử dụng:**
```java
DiscountCalculator calculator = new DiscountCalculator();
// Tính cho VIP
double vipAmount = calculator.calculateDiscount(new VipDiscount(), 1000);
// Tính cho Platinum mới thêm
double platAmount = calculator.calculateDiscount(new VipPlatinumDiscount(), 1000);
```
*(Lưu ý: Cách làm này chính là nền tảng của **Strategy Pattern** mà chúng ta đã nhắc đến ở bài Interface với ví dụ `PaymentProcessor`).*

---

### 💡 TÓM LẠI & DẤU HIỆU NHẬN BIẾT VI PHẠM

**Dấu hiệu bạn đang vi phạm OCP:**
1.  Bạn thấy mình liên tục phải mở một class cũ ra để thêm các câu lệnh `if-else` hoặc `switch-case` mỗi khi có yêu cầu mới.
2.  Mỗi khi thêm tính năng, bạn lại sợ làm hỏng tính năng cũ.

**Cách khắc phục:**
*   Sử dụng **Interface** hoặc **Abstract Class** để trừu tượng hóa hành vi.
*   Sử dụng **Đa hình (Polymorphism)** để gọi các hành vi đó. 
*   Sử dụng các Design Pattern như **Strategy Pattern** hoặc **Factory Pattern** để linh hoạt trong việc tạo đối tượng.

Nguyên lý này dạy chúng ta tư duy thiết kế hệ thống như một khối Lego: Bạn có thể lắp thêm các khối mới (extension) vào hệ thống mà không cần phải đập bỏ hay cắt gọt những khối cũ đang nằm trong đó.