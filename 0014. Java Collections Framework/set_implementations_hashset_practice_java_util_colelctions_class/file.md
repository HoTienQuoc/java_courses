Chào bạn, 4 bức ảnh này (và nội dung bạn yêu cầu) đánh dấu việc chúng ta quay trở lại gia đình `Collection` để khám phá **Giao diện `Set` (Tập hợp)** và các lớp triển khai của nó, cùng với lớp tiện ích cực kỳ hữu dụng `java.util.Collections`.

Hãy cùng bóc tách chi tiết lý thuyết và code ví dụ cho từng phần nhé.

---

### 1. Đặc điểm của `Set` Interface (Dựa trên ảnh 2)

Nhìn vào sơ đồ và ghi chú trong ảnh 2, `Set` có 3 đặc điểm "sống còn":
1.  **Không có phương thức mới:** `Set` kế thừa 100% các phương thức từ `Collection` (như `add`, `remove`, `contains`, `size`...). Nó không thêm hàm nào mới.
2.  **Các phần tử là DUY NHẤT (Unique):** Đây là linh hồn của `Set`. Bạn không thể thêm 2 phần tử giống hệt nhau vào một `Set`. Nếu bạn cố tình thêm, nó sẽ từ chối và trả về `false`.
3.  **Không đảm bảo thứ tự (Doesn't order elements by default):** Khi bạn in một `Set` (mặc định là `HashSet`), các phần tử sẽ không nằm theo thứ tự bạn thêm vào. (Ngoại lệ là `LinkedHashSet` và `TreeSet`).

---

### 2. Hệ thống phân cấp của Set (Set Hierarchy - Ảnh 3)

Dựa vào sơ đồ UML trong ảnh 3, chúng ta có:

**Các Giao diện (Interfaces):**
*   `Set`: Giao diện gốc.
*   `SortedSet`: Cam kết các phần tử được **sắp xếp** (tăng dần hoặc theo Comparator).
*   `NavigableSet`: Mở rộng của `SortedSet`, cung cấp các hàm tìm kiếm điều hướng (tìm phần tử lớn hơn gần nhất, nhỏ hơn gần nhất...).

**Các Lớp triển khai (Implementations):**
*   **`HashSet`:** Lớp phổ biến nhất. Lưu trữ dựa trên cơ chế băm (Hash Table). Tốc độ cực nhanh (O(1)) nhưng thứ tự lộn xộn.
*   **`LinkedHashSet`:** Kế thừa từ `HashSet`, nhưng có thêm Danh sách liên kết để **giữ nguyên thứ tự lúc thêm vào**.
*   **`TreeSet`:** Triển khai của `NavigableSet`. Sắp xếp các phần tử theo thứ tự (dùng Cây Đỏ-Đen). Tốc độ O(log n).
*   **`EnumSet`:** Cực kỳ tối ưu, chuyên dùng cho các kiểu dữ liệu `Enum` (liệt kê).
*   **Các lớp Thread-safe:** `CopyOnWriteArraySet`, `ConcurrentSkipListSet` (Dùng trong đa luồng).

---

### 3. Thực hành với `HashSet` (Trọng tâm Agenda)

**Code ví dụ cơ bản:**
```java
import java.util.HashSet;
import java.util.Set;

public class HashSetDemo {
    public static void main(String[] args) {
        Set<String> tapHop = new HashSet<>();
        
        tapHop.add("Java");
        tapHop.add("Python");
        
        // Thử thêm trùng lặp
        boolean isAdded = tapHop.add("Java"); 
        
        System.out.println("Thêm Java lần 2 thành công? " + isAdded); // false
        System.out.println("Set hiện tại: " + tapHop); // [Java, Python] (Thứ tự có thể thay đổi)
    }
}
```

**Lưu ý "sống còn" khi dùng `HashSet` với đối tượng tự tạo:**
Làm sao `HashSet` biết được 2 đối tượng là "trùng nhau" để loại bỏ? Nó dựa vào cặp hàm **`equals()`** và **`hashCode()`** (Giống hệt như cách `HashMap` hoạt động mà chúng ta đã học). 
Nếu bạn tạo một class `SinhVien` mà không ghi đè 2 hàm này, bạn có thể thêm 2 sinh viên có cùng Mã SV vào Set mà nó không hề báo lỗi!

```java
class SinhVien {
    private String maSV;
    // ... constructor, getter ...

    // PHẢI GHI ĐÈ 2 HÀM NÀY ĐỂ HASHSET HOẠT ĐỘNG ĐÚNG
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SinhVien sv = (SinhVien) o;
        return Objects.equals(maSV, sv.maSV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSV);
    }
}
```

---

### 4. Lớp tiện ích `java.util.Collections` (Agenda)

*(Lưu ý: Phân biệt rõ `Collection` (giao diện) và `Collections` (lớp tiện ích). Lớp này chỉ chứa các phương thức `static` để thao tác với các tập hợp).*

Đây là "bộ đồ nghề đa năng" giúp bạn sắp xếp, đảo ngược, tìm kiếm trong List/Set.

```java
import java.util.*;

public class CollectionsUtilityDemo {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>(Arrays.asList(5, 1, 9, 3, 7));

        // 1. Sắp xếp
        Collections.sort(list); 
        System.out.println("Sau khi sort: " + list); // [1, 3, 5, 7, 9]

        // 2. Đảo ngược
        Collections.reverse(list);
        System.out.println("Sau khi reverse: " + list); // [9, 7, 5, 3, 1]

        // 3. Tìm Max/Min
        System.out.println("Max: " + Collections.max(list)); // 9
        System.out.println("Min: " + Collections.min(list)); // 1
        
        // 4. Tráo đổi ngẫu nhiên (Shuffle)
        Collections.shuffle(list);
        System.out.println("Sau khi xáo trộn: " + list);
    }
}
```

---

### 5. Unmodifiable và Synchronized Wrappers (Agenda)

Đây là các phương thức của lớp `Collections` giúp biến đổi một bộ sưu tập thông thường thành một bộ sưu tập đặc biệt.

**1. Unmodifiable Wrapper (Trình bao không thể sửa đổi):**
*   **Tác dụng:** Biến một Collection thành **Read-only (Chỉ đọc)**. Bạn không thể `add`, `remove` hay `clear` được nữa. Rất hữu ích khi bạn muốn trả về một danh sách cấu hình cho người dùng xem nhưng không cho phép họ sửa.
```java
Set<String> setGoc = new HashSet<>(Arrays.asList("A", "B"));
Set<String> setChiDoc = Collections.unmodifiableSet(setGoc);

// setChiDoc.add("C"); // LỖI NGAY: UnsupportedOperationException
```

**2. Synchronized Wrapper (Trình bao đồng bộ):**
*   **Tác dụng:** Biến một Collection bình thường (không an toàn đa luồng) thành một Collection **Thread-safe (An toàn đa luồng)**. Nó sẽ tự động thêm khóa (lock) vào mọi phương thức.
```java
Set<String> setGoc = new HashSet<>();
// Bọc lại để dùng trong môi trường đa luồng
Set<String> setAnToan = Collections.synchronizedSet(setGoc);
```
*(Lưu ý: Trong Java hiện đại, nếu cần Set an toàn đa luồng, người ta thường dùng `ConcurrentHashMap.newKeySet()` hoặc `CopyOnWriteArraySet` thay vì dùng wrapper này vì hiệu năng tốt hơn).*

---

### Tóm tắt (Cheat Sheet) cho Set:
| Lớp triển khai | Thứ tự | Tốc độ | Khi nào dùng? |
| :--- | :--- | :--- | :--- |
| **`HashSet`** | Không | Nhanh nhất (O(1)) | Mặc định. Khi cần lọc các phần tử duy nhất, không quan tâm thứ tự. |
| **`LinkedHashSet`**| Theo thứ tự chèn | Nhanh (O(1)) | Khi cần lọc duy nhất nhưng vẫn muốn in ra theo đúng thứ tự đã thêm vào. |
| **`TreeSet`** | Đã sắp xếp | Chậm hơn (O(log n)) | Khi cần các phần tử duy nhất VÀ phải được sắp xếp tự động (VD: Danh sách từ khóa tìm kiếm theo A-Z). |