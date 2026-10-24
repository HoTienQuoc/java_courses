### PHẦN 1: TỔNG QUAN VỀ SOLID

SOLID là viết tắt của 5 nguyên lý vàng trong thiết kế phần mềm:

1.  **S** - **S**ingle Responsibility Principle (Nguyên lý Đơn trách nhiệm)
2.  **O** - **O**pen/Closed Principle (Nguyên lý Đóng/Mở)
3.  **L** - **L**iskov Substitution Principle (Nguyên lý Thay thế Liskov)
4.  **I** - **I**nterface Segregation Principle (Nguyên lý Phân tách Interface)
5.  **D** - **D**ependency Inversion Principle (Nguyên lý Đảo ngược phụ thuộc)

**Mục đích của SOLID:** Giúp giảm sự phụ thuộc chặt chẽ (loose coupling) giữa các class, tăng tính độc lập (high cohesion), giúp các lập trình viên khác đọc code của bạn hiểu ngay class đó làm gì, và khi cần sửa một tính năng thì không làm "vỡ" các tính năng khác.

---

### PHẦN 2: CHỮ S - SINGLE RESPONSIBILITY PRINCIPLE (SRP)

#### 1. Định nghĩa
> *"Một class chỉ nên có **một lý do duy nhất để thay đổi** (one, and only one, reason to change)."*

Nói một cách dễ hiểu: **Một class chỉ nên đảm nhận đúng MỘT công việc (trách nhiệm) mà thôi.**
Nếu một class vừa xử lý logic nghiệp vụ, vừa kết nối Database, vừa gửi Email, vừa định dạng JSON... thì nó đang vi phạm SRP. Người ta gọi đó là **"God Class"** (Lớp Thượng Đế - làm mọi thứ).

#### 2. Ví dụ: VI PHẠM SRP (Bad Example)
Hãy xem class `User` dưới đây. Nó đang gánh quá nhiều trách nhiệm.

```java
// ❌ CODE VI PHẠM SRP
public class User {
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Trách nhiệm 1: Quản lý dữ liệu (Đúng)
    public String getName() { return name; }

    // Trách nhiệm 2: Xử lý logic nghiệp vụ (Lưu vào Database)
    public void saveToDatabase() {
        // Code kết nối SQL, chèn dữ liệu...
        System.out.println("Saving user to DB...");
    }

    // Trách nhiệm 3: Gửi Email thông báo
    public void sendWelcomeEmail() {
        // Code cấu hình SMTP, gửi mail...
        System.out.println("Sending welcome email to " + email);
    }
}
```
*   **Giải thích bên cạnh:** 
    *   Class `User` có tới 3 lý do để thay đổi:
        1. Thay đổi thông tin user (thêm số điện thoại).
        2. Thay đổi Database (từ MySQL sang PostgreSQL).
        3. Thay đổi nhà cung cấp dịch vụ Email (từ Gmail sang SendGrid).
    *   **Hậu quả:** Nếu bạn chỉ muốn sửa hàm gửi email, bạn phải mở class `User` ra. Rất dễ vô tình làm hỏng code lưu Database nằm ngay bên cạnh. Code rất khó viết Unit Test vì dính dáng đến DB và SMTP.

#### 3. Ví dụ: TUÂN THỦ SRP (Good Example)
Cách khắc phục là tách các trách nhiệm ra thành các class nhỏ hơn, mỗi class làm đúng một việc.

```java
// ✅ Class User chỉ chứa dữ liệu và logic nghiệp vụ đơn giản
public class User {
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    public String getEmail() { return email; }
    // ...
}

// ✅ Class chuyên trách việc lưu trữ (Repository Pattern)
public class UserRepository {
    public void save(User user) {
        System.out.println("Saving user to DB...");
    }
}

// ✅ Class chuyên trách việc gửi Email
public class EmailService {
    public void sendWelcomeEmail(User user) {
        System.out.println("Sending welcome email to " + user.getEmail());
    }
}
```
*   **Giải thích bên cạnh:**
    *   Bây giờ, class `User` chỉ có 1 lý do để thay đổi: Khi cấu trúc dữ liệu của User thay đổi.
    *   Nếu muốn đổi Database, ta sửa `UserRepository`. Nếu muốn đổi dịch vụ mail, ta sửa `EmailService`. 
    *   Các class hoàn toàn độc lập, dễ viết Unit Test hơn rất nhiều.

#### 4. Làm sao để nhận biết vi phạm SRP?
Hãy nhìn vào tên của Class. Nếu bạn thấy tên class có chữ **"And"** (Và), **"Manager"**, **"Util"** (Tiện ích chung chung), hoặc bạn phải dùng từ "Và" để mô tả class đó làm gì, rất có thể nó đang vi phạm SRP.
*   *Ví dụ:* `UserAndEmailManager`, `ReportGeneratorAndPrinter`.

---
**Tóm lại:** 
Nguyên lý Đơn Trách Nhiệm (SRP) dạy chúng ta cách chia nhỏ hệ thống. Giống như trong một công ty, bạn không nên thuê một người vừa làm Kế toán, vừa làm IT, vừa làm Bảo vệ. Mỗi người (mỗi class) hãy làm tốt nhất một công việc của mình. Điều này giúp hệ thống (công ty) vận hành trơn tru và dễ sửa chữa khi có sự cố.