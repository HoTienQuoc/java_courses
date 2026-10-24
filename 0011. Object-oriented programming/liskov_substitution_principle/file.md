Chào bạn, chúng ta tiếp tục hành trình chinh phục SOLID với nguyên lý thứ ba: **Chữ L - Liskov Substitution Principle (Nguyên lý Thay thế Liskov)**. 

Đây là nguyên lý "khó nhằn" nhất và cũng là nguyên lý bị vi phạm nhiều nhất trong thực tế, đặc biệt là khi các lập trình viên lạm dụng tính kế thừa (Inheritance) mà không suy nghĩ kỹ.

---

### 1. ĐỊNH NGHĨA

> *"Nếu S là một lớp con của T, thì các đối tượng của T có thể được thay thế bằng các đối tượng của S mà không làm thay đổi tính đúng đắn của chương trình."* (Barbara Liskov)

**Nói một cách dễ hiểu:**
Nếu bạn có một đoạn code đang sử dụng lớp cha (T). Khi bạn truyền một đối tượng của lớp con (S) vào thay thế, đoạn code đó **phải tiếp tục chạy đúng** và **không gây ra lỗi** hay kết quả bất thường nào.

Điều này có nghĩa là: **Lớp con không được phép "phá vỡ" những cam kết (hợp đồng) của lớp cha.** Lớp con chỉ được phép mở rộng hành vi, chứ không được phép thay đổi bản chất hay làm hỏng các quy tắc mà lớp cha đã thiết lập.

---

### 2. VÍ DỤ KINH ĐIỂN: VI PHẠM LSP (Hình Vuông và Hình Chữ Nhật)

Trong toán học, "Hình vuông là một hình chữ nhật" (vì nó có 4 góc vuông). Nhưng trong lập trình, nếu bạn cho `Square` kế thừa `Rectangle`, bạn sẽ gặp thảm họa.

```java
// ❌ CODE VI PHẠM LSP
class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getArea() { return width * height; }
}

// Hình vuông kế thừa hình chữ nhật
class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // Hình vuông thì chiều dài = chiều rộng
    }

    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height;
    }
}
```
**Tại sao lại vi phạm? Hãy xem đoạn code sử dụng lớp cha `Rectangle` dưới đây:**

```java
public class Main {
    public static void resize(Rectangle r) {
        r.setWidth(5);
        r.setHeight(4);
        
        // Giả sử ta mong đợi diện tích là 5 * 4 = 20
        // Nhưng nếu 'r' thực chất là Square thì sao?
    }

    public static void main(String[] args) {
        Rectangle rect = new Rectangle();
        resize(rect);
        System.out.println(rect.getArea()); // KẾT QUẢ: 20 (ĐÚNG)

        Rectangle square = new Square(); 
        resize(square);
        System.out.println(square.getArea()); // KẾT QUẢ: 16 (SAI! Vì 4 * 4 = 16)
    }
}
```
*   **Giải thích:** Hàm `resize` được viết để làm việc với `Rectangle`. Nó giả định rằng việc set chiều rộng và chiều dài là độc lập. Nhưng khi ta truyền `Square` (lớp con) vào, nó đã **âm thầm phá vỡ giả định đó**. Chương trình trả về kết quả sai (16 thay vì 20). Bạn không thể dùng `Square` thay thế cho `Rectangle` một cách an toàn. **Đây là vi phạm LSP!**

---

### 3. VÍ DỤ KHÁC: CHIM VÀ CHIM CÁNH CỤT (Động vật)

Giả sử bạn có lớp `Bird` (Chim) có phương thức `fly()`.

```java
// ❌ CODE VI PHẠM LSP
class Bird {
    public void fly() {
        System.out.println("I am flying!");
    }
}

class Penguin extends Bird {
    @Override
    public void fly() {
        // Chim cánh cụt không biết bay
        throw new UnsupportedOperationException("Penguins can't fly!");
    }
}
```
*   **Giải thích:** Nếu một hàm nào đó nhận vào một danh sách các `Bird` và gọi `bird.fly()`, chương trình sẽ bị "crash" (lỗi) ngay khi gặp `Penguin`. Lớp con đã phá vỡ hợp đồng của lớp cha. Vi phạm LSP.

---

### 4. CÁCH KHẮC PHỤC (TUÂN THỦ LSP)

**Cách 1: Không dùng kế thừa nếu bản chất không phù hợp (Với Hình Vuông/HCN)**
Hình vuông không nên kế thừa hình chữ nhật. Hãy tạo một Interface chung là `Shape` (Hình dạng) và cả hai tự implement.

```java
// ✅ Giải pháp
interface Shape {
    int getArea();
}

class Rectangle implements Shape {
    private int w, h;
    public Rectangle(int w, int h) { this.w = w; this.h = h; }
    public int getArea() { return w * h; }
}

class Square implements Shape {
    private int side;
    public Square(int side) { this.side = side; }
    public int getArea() { return side * side; }
}
```
*   **Giải thích:** Cả hai đều là `Shape` và đều có hàm `getArea()`. Không có sự phụ thuộc chặt chẽ (setter) nào bị phá vỡ.

**Cách 2: Trích xuất Interface phù hợp (Với Chim)**
Chỉ những con chim biết bay mới implement khả năng bay.

```java
// ✅ Giải pháp
interface Flyable {
    void fly();
}

class Bird { /* Các thuộc tính chung của chim */ }

class Sparrow extends Bird implements Flyable {
    public void fly() { System.out.println("Sparrow flying"); }
}

class Penguin extends Bird {
    // Không implement Flyable, chỉ bơi thôi
    public void swim() { System.out.println("Penguin swimming"); }
}
```

---

### 5. DẤU HIỆU NHẬN BIẾT VI PHẠM LSP

Bạn hãy chú ý đến những dấu hiệu "báo động đỏ" sau trong code của mình:

1.  **Ném ngoại lệ `UnsupportedOperationException`** (hoặc ngoại lệ tương tự) trong một phương thức được kế thừa. (Như ví dụ Chim cánh cụt).
2.  **Ghi đè (Override) một phương thức nhưng để trống (empty body)**, không làm gì cả, vì lớp con không hỗ trợ hành vi đó.
3.  Sử dụng **`instanceof`** để kiểm tra kiểu dữ liệu của đối tượng trước khi gọi hàm trong lớp cha. (Tức là bạn đang cố gắng "lách" luật vì lớp con không hành xử giống lớp cha).
4.  Lớp con thay đổi ý nghĩa logic của phương thức lớp cha (ví dụ lớp cha trả về số dương, lớp con trả về số âm).

**3 QUY TẮC "HỢP ĐỒNG" CỦA LSP (Design by Contract):**
*   **Tiền điều kiện (Pre-conditions) không được phép thắt chặt hơn:** Lớp cha nhận mọi số nguyên, lớp con không được phép bắt lỗi nếu số < 0.
*   **Hậu điều kiện (Post-conditions) không được phép nới lỏng hơn:** Lớp cha cam kết trả về số dương, lớp con không được trả về số âm.
*   **Bất biến (Invariants) phải được giữ nguyên:** Các quy tắc của lớp cha phải được lớp con bảo toàn.

**Tóm lại:** Kế thừa không chỉ đơn thuần là "Là một" (Is-a) theo nghĩa đời thực, mà phải là "Hành xử y hệt như" (Behaves-like-a) trong code. Nếu lớp con không thể thay thế lớp cha mà không gây ra lỗi, bạn hãy dùng **Interface** hoặc **Composition (chứa đối tượng)** thay vì Kế thừa.