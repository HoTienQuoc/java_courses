Chào bạn, 5 bức ảnh này mang đến một chủ đề rất "hot" và mới mẻ: **Sequenced Collections (Bộ sưu tập có thứ tự)**, được giới thiệu trong **Java 21** thông qua **JEP 431**.

Đây là một trong những thay đổi lớn nhất của Collections Framework trong nhiều năm trở lại đây. Nó giải quyết một "nỗi đau" dai dẳng của lập trình viên Java: Làm sao để thao tác với phần tử đầu/cuối một cách thống nhất.

Dưới đây là phần giải thích chi tiết lý thuyết từ các bức ảnh, kết hợp với code ví dụ minh họa.

---

### 1. Vấn đề trước Java 21 (Tại sao lại cần Sequenced Collections?) (Ảnh 1 & 5)

Trước Java 21, việc quản lý thứ tự trong Java rất "rời rạc" và thiếu nhất quán:
*   `List` có hàm `get(0)` để lấy phần tử đầu, `get(size()-1)` để lấy phần tử cuối.
*   `Deque` có hàm `getFirst()` và `getLast()`.
*   `SortedSet` có hàm `first()` và `last()`.
*   `LinkedHashMap` muốn lấy phần tử đầu/cuối thì phải dùng Iterator hoặc Stream rất lằng nhằng.
*   **Việc đảo ngược (reverse) một List** đòi hỏi phải dùng `Collections.reverse()` (hàm này thay đổi trực tiếp list gốc) hoặc tạo một list mới.
*   **Không có một Interface chung nào** cho các bộ sưu tập có thứ tự.

**Giải pháp (Java 21 - JEP 431):**
Java 21 mang đến 3 Interface mới để thống nhất mọi thứ:
1.  `SequencedCollection`
2.  `SequencedSet`
3.  `SequencedMap`

---

### 2. Giao diện `SequencedCollection` và các hàm cốt lõi (Ảnh 1 & 2)

`SequencedCollection` là giao diện cha (kế thừa từ `Collection`). Nó định nghĩa ra một "khế ước" chung cho mọi bộ sưu tập có thứ tự xác định (well-defined encounter order).

**Các phương thức mới được thêm vào:**
*   `getFirst()`: Lấy phần tử đầu tiên.
*   `getLast()`: Lấy phần tử cuối cùng.
*   `addFirst(E)`: Thêm phần tử vào đầu.
*   `addLast(E)`: Thêm phần tử vào cuối.
*   `removeFirst()`: Xóa và trả về phần tử đầu.
*   `removeLast()`: Xóa và trả về phần tử cuối.
*   `reversed()`: Trả về một **View (chế độ xem)** đảo ngược của bộ sưu tập (không tạo bản sao, không làm thay đổi bộ sưu tập gốc).

**Code Ví dụ với `List`:**
```java
import java.util.ArrayList;
import java.util.List;

public class SequencedListDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("B");
        
        // Thêm vào đầu và cuối (Trước đây List không có hàm này!)
        list.addFirst("A"); 
        list.addLast("C");  
        
        System.out.println(list); // [A, B, C]
        
        // Lấy phần tử đầu/cuối
        System.out.println("Đầu: " + list.getFirst()); // A
        System.out.println("Cuối: " + list.getLast()); // C
        
        // Xem đảo ngược (Không làm thay đổi list gốc)
        System.out.println("Đảo ngược: " + list.reversed()); // [C, B, A]
        System.out.println("List gốc vẫn là: " + list); // [A, B, C]
    }
}
```

---

### 3. Giao diện `SequencedSet` (Ảnh 2)

`SequencedSet` kế thừa từ `SequencedCollection` và `Set`. Nó kết hợp 2 tính chất: **Có thứ tự** (như List) và **Không trùng lặp** (như Set).

**Các lớp triển khai chính:** `LinkedHashSet`, `TreeSet`, `SortedSet`.

**Code Ví dụ với `LinkedHashSet`:**
```java
import java.util.LinkedHashSet;
import java.util.SequencedSet;

public class SequencedSetDemo {
    public static void main(String[] args) {
        SequencedSet<String> set = new LinkedHashSet<>();
        set.add("B");
        set.addFirst("A"); // Thêm vào đầu
        set.addLast("C");  // Thêm vào cuối
        
        // Thử thêm trùng lặp
        set.addFirst("A"); 
        
        System.out.println(set); // [A, B, C] (Vẫn giữ thứ tự, A không bị trùng)
        
        // Đảo ngược
        System.out.println(set.reversed()); // [C, B, A]
    }
}
```

---

### 4. Giao diện `SequencedMap` (Ảnh 2)

Tương tự, `SequencedMap` kế thừa từ `Map` và cam kết các cặp Key-Value có thứ tự rõ ràng.
Nó cung cấp các hàm:
*   `firstEntry()`, `lastEntry()`: Lấy cặp Key-Value đầu/cuối.
*   `pollFirstEntry()`, `pollLastEntry()`: Lấy ra và xóa cặp đầu/cuối.
*   `putFirst(k, v)`, `putLast(k, v)`: Thêm cặp Key-Value vào đầu/cuối.
*   `reversed()`: Xem Map theo thứ tự đảo ngược.

**Các lớp triển khai chính:** `LinkedHashMap`, `TreeMap`.

**Code Ví dụ với `LinkedHashMap`:**
```java
import java.util.LinkedHashMap;
import java.util.SequencedMap;

public class SequencedMapDemo {
    public static void main(String[] args) {
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.put("B", 2);
        
        // Thêm vào đầu và cuối (Trước đây không có)
        map.putFirst("A", 1);
        map.putLast("C", 3);
        
        System.out.println(map); // {A=1, B=2, C=3}
        
        // Lấy cặp Key-Value đầu/cuối
        System.out.println("Đầu: " + map.firstEntry()); // A=1
        System.out.println("Cuối: " + map.lastEntry()); // C=3
        
        // Đảo ngược
        System.out.println("Đảo ngược: " + map.reversed()); // {C=3, B=2, A=1}
    }
}
```

---

### 5. Tính tương thích và Immutability (Ảnh 3)

Điểm cực kỳ tuyệt vời của JEP 431 là **tính tương thích ngược (Backward compatibility)**. Tất cả các lớp cũ (`ArrayList`, `LinkedList`, `TreeMap`...) đều tự động được "nâng cấp" để implement các giao diện Sequenced này mà **bạn không cần phải sửa lại bất kỳ dòng code cũ nào**.

Tuy nhiên, có một lưu ý cực kỳ quan trọng đối với **Immutable Collections (Bộ sưu tập bất biến)** như `List.of()`, `Map.of()`:
*   Các hàm **chỉ đọc** như `getFirst()`, `getLast()`, `reversed()` hoạt động bình thường.
*   Các hàm **thay đổi dữ liệu** như `addFirst()`, `addLast()`, `removeFirst()` sẽ **văng lỗi `UnsupportedOperationException`**.

```java
import java.util.List;

public class ImmutableSequencedDemo {
    public static void main(String[] args) {
        // List bất biến
        List<String> immutableList = List.of("A", "B", "C");
        
        // Lấy đầu/cuối thì OK
        System.out.println(immutableList.getFirst()); // A
        
        // Thử thêm vào đầu -> LỖI NGAY!
        try {
            immutableList.addFirst("Z"); 
        } catch (UnsupportedOperationException e) {
            System.out.println("Lỗi: Không thể thêm vào List bất biến!");
        }
    }
}
```

---

### 6. Bức tranh lớn: Tại sao JEP 431 lại quan trọng? (Ảnh 4)

1.  **API rõ ràng, dễ đoán:** Thay vì mỗi lớp có một kiểu hàm lấy phần tử đầu khác nhau, giờ đây tất cả đều dùng chung `getFirst()`, `getLast()`, `addFirst()`, `addLast()`.
2.  **Thay thế các thủ thuật thủ công:** Không cần phải viết `list.get(0)` hay `list.get(list.size() - 1)` nữa.
3.  **View đảo ngược (`reversed()`) cực kỳ hiệu quả:** Nó không copy dữ liệu mà chỉ trả về một "lớp vỏ" nhìn ngược lại. Nếu bạn thay đổi dữ liệu trên View này, dữ liệu gốc cũng thay đổi theo.
4.  **Hoàn thiện lập trình hướng giao diện (Interface-based programming):** Bạn có thể khai báo `SequencedCollection<String> col = ...` và hoán đổi giữa `ArrayList` hay `LinkedList` mà code xử lý bên dưới không cần đổi.

**Ví dụ thực tế (Ứng dụng vào Stack/Queue hoặc LRU Cache):**
Trước đây, để làm một hàng đợi (Queue) hay ngăn xếp (Stack) đơn giản, người ta hay dùng `LinkedList` và phải nhớ các hàm riêng. Giờ đây, mọi thao tác đều quy về `addFirst`, `removeLast`, `getFirst` rất trực quan và đồng nhất.