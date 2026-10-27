Chào bạn, tôi hiểu ý bạn rồi. Bài giảng này mang tên **"Hash Tables & HashMap"** và 12 bức ảnh bạn gửi là một bài học cực kỳ chuyên sâu, giải thích cách "bộ máy" bên dưới hoạt động.

Dưới đây là bài viết tổng hợp **đầy đủ, chi tiết và có hệ thống** lại toàn bộ nội dung từ Agenda (mục lục) trong ảnh, kết hợp lý thuyết từ các hình ảnh và code ví dụ thực tế.

---

# BÀI HỌC: HASH TABLES & HASHMAP TRONG JAVA

## 1. Hash Table là gì và tại sao nó nhanh? (Ảnh 2)

**Hash Table (Bảng băm)** là một cấu trúc dữ liệu cho phép lưu trữ và truy xuất dữ liệu với tốc độ cực kỳ ấn tượng: **O(1) - Constant Time**.
*   Điều này có nghĩa là: Dù bạn lưu 10 phần tử hay 1 triệu phần tử, thời gian để tìm ra một phần tử (bằng các hàm `get`, `put`, `remove`, `containsKey`) gần như là như nhau.
*   `HashMap` và `Hashtable` trong Java đều được xây dựng dựa trên nền tảng lý thuyết của Hash Table.

---

## 2. Cách Hash Table hoạt động (Cơ chế Hashing) (Ảnh 3, 5, 6, 8, 9)

Hãy tưởng tượng `HashMap` giống như một **tủ đựng đồ có nhiều ngăn kéo (Buckets)**. Ban đầu, cái tủ này có **16 ngăn kéo** (Initial Capacity = 16).

**Quy trình lưu dữ liệu (Put):**
1.  Khi bạn `put(key, value)` vào Map, Java sẽ lấy `hashCode()` của Key. (VD: `key1.hashCode() = 65`).
2.  Java dùng một thuật toán băm để tính ra số ngăn kéo: `index = hashCode % Số_ngăn_kéo`.
    *   *Ví dụ:* `65 % 16 = 1`. Vậy cặp `key1 - value` sẽ được cất vào **ngăn kéo số 1**.

**Hiện tượng Đụng độ (Collision):**
*   Nếu bạn thêm `key2` mà cũng có `hashCode() = 65`, nó cũng sẽ bị đẩy vào ngăn kéo số 1.
*   Lúc này, Hash Table sẽ tạo một **Danh sách liên kết (Linked List)** ngay trong ngăn kéo số 1. `key2` sẽ được móc nối vào phía sau `key1`.

**Quy trình lấy dữ liệu (Get):**
*   Khi bạn gọi `get(key2)`, Java tính lại index = 1, chạy đến ngăn kéo số 1.
*   Nó thấy có 2 phần tử. Nó sẽ dùng hàm **`equals()`** để so sánh:
    *   `key1.equals(key2)` -> `false` (Đi tiếp).
    *   `key2.equals(key2)` -> `true` (Đúng rồi, lấy value ra).

**Tối ưu hóa của Java 8 (Ảnh 7):**
*   Nếu một ngăn kéo có quá nhiều phần tử (thành một Linked List dài), việc tìm kiếm sẽ bị chậm đi (O(n)).
*   **Giải pháp:** Nếu số phần tử trong một ngăn kéo vượt quá **8**, Java sẽ tự động chuyển Linked List thành **Cây đỏ-đen (Red-Black Tree)**. Lúc này tốc độ tìm kiếm là **O(log n)** (nhanh hơn rất nhiều).

**Thảm họa Hash Function tồi (Ảnh 10):**
*   Nếu bạn viết hàm `hashCode()` kém (ví dụ: `return 1;` cho mọi đối tượng), thì tất cả dữ liệu sẽ dồn hết vào ngăn kéo số 1. HashMap biến thành một Linked List khổng lồ và mất đi tốc độ O(1) quý giá.

---

## 3. Capacity và Load Factor (Ảnh 4)

*   **Initial Capacity = 16:** Số ngăn kéo mặc định ban đầu.
*   **Load Factor = 0.75:** Hệ số tải. Đây là ngưỡng an toàn để quyết định khi nào cần mở rộng tủ.
*   **Threshold (Ngưỡng) = Capacity * Load Factor = 16 * 0.75 = 12.**
*   **Ý nghĩa:** Khi bạn thêm phần tử thứ 12, tủ vẫn ổn. Khi thêm phần tử thứ **13**, HashMap sẽ tự động **Resize (mở rộng)**: tạo tủ mới gấp đôi (32 ngăn), bê dữ liệu sang và tính lại vị trí. Việc này tốn hiệu năng, nên nếu biết trước số lượng lớn, hãy khởi tạo `new HashMap<>(capacity)`.

---

## 4. Hợp đồng (Contract) của `hashCode()` và `equals()` (Ảnh 11, 12)

Để `HashMap` hoạt động đúng, nếu bạn ghi đè (override) hai hàm này, phải tuân thủ nghiêm ngặt các quy tắc sau:

**Quy tắc `hashCode()`:**
1.  Gọi nhiều lần trên cùng 1 đối tượng phải trả về cùng 1 số nguyên.
2.  Nếu `x.equals(y)` là `true`, thì `x.hashCode()` phải bằng `y.hashCode()`.
3.  Nếu `x.equals(y)` là `false`, `hashCode` có thể bằng nhau (gây đụng độ), nhưng nên khác nhau để tăng hiệu năng.

**Quy tắc `equals()`:**
1.  **Phản xạ:** `x.equals(x)` phải `true`.
2.  **Đối xứng:** `x.equals(y)` == `y.equals(x)`.
3.  **Bắc cầu:** `x.equals(y)` và `y.equals(z)` => `x.equals(z)`.
4.  **Nhất quán:** Kết quả không đổi qua nhiều lần gọi.
5.  `x.equals(null)` phải `false`.

---

## 5. Khóa bất biến (Immutable Keys) và Cách làm mất dữ liệu (Mất Value)

**Tại sao Key phải là Immutable (Bất biến - như String, Integer)?**
Vì khi bạn thay đổi dữ liệu của Key, `hashCode` của nó sẽ thay đổi. Map sẽ đi tìm ở ngăn kéo mới, nhưng dữ liệu lại đang nằm ở ngăn kéo cũ. **Kết quả: Bạn không bao giờ lấy lại được Value đó nữa.**

```java
class MutableKey {
    public String name;
    // ... constructor, equals, hashCode ...
}

Map<MutableKey, String> map = new HashMap<>();
MutableKey key = new MutableKey("Ban đầu");
map.put(key, "Dữ liệu");

key.name = "Đã bị sửa"; // Thay đổi hashcode
System.out.println(map.get(key)); // KẾT QUẢ: NULL (Dữ liệu đã bị mất tích!)
```

---

## 6. Thực hành: Sắp xếp Map (Sort Map by Keys / by Values)

`HashMap` và `Hashtable` không đảm bảo thứ tự. Muốn sắp xếp, ta phải dùng `TreeMap` hoặc `Stream`.

**Sắp xếp theo Khóa (Key):**
```java
Map<String, Integer> map = new HashMap<>();
map.put("C", 3); map.put("A", 1); map.put("B", 2);

// Cách 1: Dùng TreeMap (tự động A-Z)
Map<String, Integer> sortedByKey = new TreeMap<>(map); 
System.out.println(sortedByKey); // {A=1, B=2, C=3}
```

**Sắp xếp theo Giá trị (Value):**
```java
// Dùng Stream để sắp xếp theo Value tăng dần
Map<String, Integer> sortedByValue = map.entrySet().stream()
    .sorted(Map.Entry.comparingByValue())
    .collect(Collectors.toMap(
        Map.Entry::getKey, Map.Entry::getValue, 
        (e1, e2) -> e1, LinkedHashMap::new // Giữ thứ tự
    ));
System.out.println(sortedByValue); // {A=1, B=2, C=3}
```

---

## 7. Code Thực Chiến Tổng Hợp

Dưới đây là code minh họa cách tạo một class chuẩn làm Key cho HashMap và cách dùng nó.

```java
import java.util.*;

class SinhVien {
    private final String maSV; // Dùng final để bất biến
    private final String ten;

    public SinhVien(String maSV, String ten) {
        this.maSV = maSV;
        this.ten = ten;
    }

    // BẮT BUỘC phải ghi đè 2 hàm này để HashMap hoạt động đúng
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SinhVien sv = (SinhVien) o;
        return Objects.equals(maSV, sv.maSV); // So sánh theo Mã SV
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSV); // Tính hash dựa trên Mã SV
    }
}

public class Main {
    public static void main(String[] args) {
        Map<SinhVien, Double> bangDiem = new HashMap<>();
        
        SinhVien sv1 = new SinhVien("SV01", "An");
        bangDiem.put(sv1, 8.5);
        
        // Tạo đối tượng mới có cùng Mã SV
        SinhVien sv2 = new SinhVien("SV01", "An");
        
        // Nhờ ghi đè equals/hashCode, nó tìm ra đúng vị trí
        System.out.println("Điểm của SV01: " + bangDiem.get(sv2)); // 8.5
    }
}
```

*Lưu ý cuối cùng: `Hashtable` (cổ điển) là thread-safe nhưng chậm và cấm Key/Value `null`. `HashMap` (hiện đại) không thread-safe nhưng nhanh và cho phép 1 Key `null`, nhiều Value `null`. Ngày nay, `HashMap` được sử dụng rộng rãi hơn hẳn.*