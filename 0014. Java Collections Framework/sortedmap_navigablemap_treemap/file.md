Chào bạn, 8 bức ảnh này cung cấp một bài học nâng cao và cực kỳ thú vị về **`TreeMap`** cùng các khái niệm khoa học máy tính đằng sau nó (Cây nhị phân, Cây Đỏ-Đen, và độ phức tạp thuật toán O(log n)).

Đây là bài học giải thích tại sao `TreeMap` lại có thể vừa **sắp xếp** dữ liệu, vừa giữ được **tốc độ tìm kiếm cực tốt**. Hãy cùng bóc tách chi tiết nhé!

---

### 1. Giao diện `SortedMap` và `NavigableMap` (Từ Agenda)

*   **`SortedMap` (Giao diện):** Là một giao diện con của `Map`. Nó cam kết rằng các cặp Key-Value bên trong sẽ được **sắp xếp theo thứ tự của Key** (tăng dần theo thứ tự tự nhiên, hoặc theo một `Comparator` do bạn cung cấp).
*   **`NavigableMap` (Giao diện):** Là giao diện con của `SortedMap`. Nó cung cấp thêm các phương thức "điều hướng" cực mạnh mẽ, cho phép bạn tìm các phần tử gần nhất với một khóa cho trước (ví dụ: tìm Key lớn hơn gần nhất, nhỏ hơn gần nhất, cắt lấy một đoạn Map...).
*   **`TreeMap` (Lớp triển khai):** Là lớp phổ biến nhất thực thi cả 2 giao diện trên.

---

### 2. Big O Notation của TreeMap (Ảnh 2, 3)

*   **O(log n):** Ảnh 2 khẳng định các phương thức chính trên `TreeMap` (`get`, `put`, `remove`, `containsKey`) đều có độ phức tạp là **O(log n)**.
*   **Biểu đồ (Ảnh 3):** Đường màu đỏ `O(log n)` nằm rất thấp so với `O(n)` (tuyến tính). 
*   **Ý nghĩa thực tế:** `HashMap` có tốc độ là O(1) (nhanh nhất), nhưng `TreeMap` là O(log n). Với 1 triệu phần tử, `HashMap` mất 1 bước, `TreeMap` mất khoảng 20 bước (vì 2^20 ~ 1 triệu). Đây vẫn là tốc độ **cực kỳ nhanh** đối với một cấu trúc dữ liệu có sắp xếp.
*   **Tại sao lại là O(log n)?** Câu trả lời nằm ở cấu trúc dữ liệu "Cây Đỏ-Đen" bên dưới nó.

---

### 3. Hành trình: Từ Cây nhị phân đến Cây Đỏ-Đen (Ảnh 4, 5, 6)

**Bước 1: Cây tìm kiếm nhị phân (Binary Search Tree - Ảnh 4)**
*   Quy tắc: Mọi Node bên trái **nhỏ hơn** Node gốc, mọi Node bên phải **lớn hơn** Node gốc.
*   *Ví dụ ảnh 4:* Gốc là 8. Bên trái (3, 1, 6...) đều nhỏ hơn 8. Bên phải (10, 14...) đều lớn hơn 8.
*   Khi tìm kiếm, ta so sánh và loại bỏ được một nửa số Node (đi sang trái hoặc phải). Đây là lý do có độ phức tạp O(log n).

**Bước 2: Vấn đề của Cây nhị phân thường (Ảnh 5)**
*   Hãy tưởng tượng bạn thêm các số theo thứ tự tăng dần: `8, 10, 21, 28, 36, 50`.
*   Cây sẽ bị **mất cân bằng (Unbalanced)**, biến thành một đường thẳng chéo (giống như Linked List).
*   Lúc này, muốn tìm số 50, bạn phải duyệt qua toàn bộ các số trước đó. Độ phức tạp biến thành **O(n)** (chậm thảm họa). `TreeMap` không thể hoạt động tốt nếu cây bị lệch như thế này.

**Bước 3: Giải pháp - Cây Đỏ-Đen (Red-Black Tree - Ảnh 6)**
*   Đây là một dạng cây tự cân bằng (Self-balancing). Mỗi Node được tô màu Đỏ hoặc Đen.
*   Khi bạn thêm hoặc xóa một Node, cây sẽ tự động thực hiện các phép **Xoay (Rotations)** và **Đổi màu** để đảm bảo cây luôn "cân bằng" (không có nhánh nào dài quá bất thường).
*   Nhờ vậy, chiều cao của cây luôn được giữ ở mức tối thiểu, đảm bảo mọi thao tác tìm kiếm luôn luôn là **O(log n)** dù bạn thêm dữ liệu theo thứ tự nào.

*(Lưu ý: `TreeMap` và `TreeSet` trong Java đều được xây dựng dựa trên Cây Đỏ-Đen).*

---

### 4. Thực hành với `TreeMap` (Code ví dụ)

`TreeMap` rất giống `HashMap` nhưng có thêm các hàm "điều hướng" (từ `NavigableMap`) rất tiện lợi.

```java
import java.util.*;

public class TreeMapDemo {
    public static void main(String[] args) {
        // Mặc định sắp xếp theo thứ tự Key tự nhiên (tăng dần)
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        treeMap.put(50, "Năm mươi");
        treeMap.put(10, "Mười");
        treeMap.put(30, "Ba mươi");
        treeMap.put(20, "Hai mươi");

        // Tự động sắp xếp theo Key
        System.out.println("TreeMap cơ bản: " + treeMap); 
        // {10=Mười, 20=Hai mươi, 30=Ba mươi, 50=Năm mươi}

        // --- CÁC HÀM ĐIỀU HƯỚNG (NAVIGABLE) ---
        System.out.println("Key nhỏ nhất: " + treeMap.firstKey()); // 10
        System.out.println("Key lớn nhất: " + treeMap.lastKey());  // 50

        // Lấy Key lớn hơn hoặc bằng 25 gần nhất
        System.out.println("Ceiling(25): " + treeMap.ceilingKey(25)); // 30
        
        // Lấy Key nhỏ hơn hoặc bằng 25 gần nhất
        System.out.println("Floor(25): " + treeMap.floorKey(25)); // 20

        // Cắt lấy một đoạn Map: Từ Key 20 đến trước Key 50
        System.out.println("SubMap(20, 50): " + treeMap.subMap(20, 50)); 
        // {20=Hai mươi, 30=Ba mươi}

        // --- CUSTOM COMPARATOR (Đảo ngược) ---
        // Sắp xếp giảm dần (Z-A)
        TreeMap<Integer, String> reverseMap = new TreeMap<>(Comparator.reverseOrder());
        reverseMap.putAll(treeMap);
        System.out.println("Sắp xếp giảm dần: " + reverseMap); 
        // {50=Năm mươi, 30=Ba mươi, 20=Hai mươi, 10=Mười}
    }
}
```

---

### Tóm lại: Khi nào dùng `HashMap` và khi nào dùng `TreeMap`?

*   **Dùng `HashMap`**: Khi bạn cần **tốc độ tuyệt đối** (O(1)) và không quan tâm đến thứ tự của các phần tử. (Dùng trong 90% trường hợp).
*   **Dùng `TreeMap`**: Khi bạn cần dữ liệu phải được **sắp xếp theo Khóa (Key)** (ví dụ: để in ra báo cáo từ A-Z, hoặc xếp hạng), hoặc khi bạn cần dùng các hàm tìm kiếm "điều hướng" như tìm người có điểm số gần nhất, tìm sản phẩm có giá trong khoảng X-Y...