Chào bạn, tiếp nối chuỗi bài học về `Collection Interface`, hôm nay chúng ta sẽ đi sâu vào **3 giao diện con (Sub-interfaces)** quan trọng nhất và được sử dụng nhiều nhất trong Java: **`List`**, **`Set`**, và **`Queue`**.

Hãy nhớ lại sơ đồ UML ở bài trước: `Collection` là giao diện cha. Từ nó sẽ đẻ ra 3 nhánh chính này. Mỗi nhánh sẽ có những "tính cách" (đặc điểm) hoàn toàn khác nhau, phù hợp cho từng bài toán khác nhau.

---

### 1. `List` Interface (Giao diện Danh sách)

**Ẩn dụ:** Giống như một **cuốn sổ ghi chép có đánh số thứ tự** (1, 2, 3...). Bạn có thể ghi trùng nội dung ở các dòng khác nhau, và bạn có thể lật thẳng đến dòng số 5 để đọc mà không cần đọc từ đầu.

**Đặc điểm:**
*   **Có thứ tự (Ordered):** Các phần tử được lưu theo đúng thứ tự bạn thêm vào.
*   **Cho phép trùng lặp (Duplicates):** Bạn có thể thêm 2 chuỗi "Táo" vào cùng một List.
*   **Truy cập bằng chỉ mục (Index):** Bạn có thể lấy phần tử thứ `i` ra bằng hàm `get(i)`.
*   **Các lớp triển khai phổ biến:** `ArrayList` (Mảng động, tìm kiếm cực nhanh, thêm/xóa ở giữa chậm), `LinkedList` (Danh sách liên kết, thêm/xóa ở đầu/cuối nhanh, tìm kiếm chậm).

**Code Ví dụ:**
```java
import java.util.List;
import java.util.ArrayList;

public class ListDemo {
    public static void main(String[] args) {
        List<String> danhSach = new ArrayList<>();
        
        danhSach.add("Táo");
        danhSach.add("Cam");
        danhSach.add("Táo"); // Thêm trùng lặp vẫn OK
        
        System.out.println(danhSach); // [Táo, Cam, Táo]
        
        // Truy cập bằng chỉ mục (index bắt đầu từ 0)
        System.out.println("Phần tử đầu tiên: " + danhSach.get(0)); // Táo
    }
}
```

---

### 2. `Set` Interface (Giao diện Tập hợp)

**Ẩn dụ:** Giống như một **túi bi ve**. Bạn không quan tâm viên bi nào nằm trước viên bi nào, nhưng tuyệt đối **không thể có 2 viên bi giống hệt nhau** trong túi (nếu bạn bỏ viên bi giống hệt vào, nó sẽ tự động bị từ chối).

**Đặc điểm:**
*   **Không cho phép trùng lặp (No Duplicates):** Đây là đặc điểm sống còn của Set. Mỗi phần tử là duy nhất.
*   **Không có thứ tự (Unordered):** (Chính xác hơn là không đảm bảo thứ tự chèn vào). Khi bạn in ra, nó không nhất thiết theo thứ tự bạn thêm vào.
*   **Không có Index:** Bạn không thể dùng `get(0)` vì không có khái niệm vị trí.
*   **Các lớp triển khai phổ biến:**
    *   `HashSet`: Nhanh nhất, thứ tự ngẫu nhiên.
    *   `LinkedHashSet`: Giữ nguyên thứ tự lúc bạn thêm vào.
    *   `TreeSet`: Tự động sắp xếp các phần tử theo thứ tự tăng dần.

**Code Ví dụ:**
```java
import java.util.Set;
import java.util.HashSet;

public class SetDemo {
    public static void main(String[] args) {
        Set<String> tapHop = new HashSet<>();
        
        tapHop.add("Táo");
        tapHop.add("Cam");
        
        // Cố tình thêm "Táo" lần 2
        boolean isAdded = tapHop.add("Táo"); 
        
        System.out.println("Thêm Táo lần 2 thành công không? " + isAdded); // false
        System.out.println(tapHop); // [Táo, Cam] (Chỉ có 1 Táo)
    }
}
```

---

### 3. `Queue` Interface (Giao diện Hàng đợi)

**Ẩn dụ:** Giống như **hàng người xếp hàng mua vé xem phim**. Ai đến trước thì mua trước, ai đến sau thì xếp sau. Nguyên tắc cốt lõi là **FIFO (First-In-First-Out - Vào trước Ra trước)**.

**Đặc điểm:**
*   **Thường dùng để xử lý (Processing):** Thay vì lưu trữ lâu dài, Queue dùng để xếp hàng các tác vụ cần xử lý lần lượt (như xử lý đơn hàng, in ấn, gửi tin nhắn).
*   **Có 2 đầu:** Đầu (Head - nơi lấy ra) và Đuôi (Tail - nơi thêm vào).
*   **Các hàm đặc trưng:**
    *   `offer(e)`: Thêm vào cuối hàng.
    *   `poll()`: Lấy ra và xóa phần tử ở đầu hàng (trả về `null` nếu hàng rỗng).
    *   `peek()`: Xem phần tử ở đầu hàng (không xóa).
*   **Các lớp triển khai phổ biến:** `LinkedList` (có thể dùng làm cả List và Queue), `PriorityQueue` (Hàng đợi ưu tiên - phần tử nhỏ nhất/lớn nhất luôn được lấy ra trước).

**Code Ví dụ:**
```java
import java.util.Queue;
import java.util.LinkedList;

public class QueueDemo {
    public static void main(String[] args) {
        Queue<String> hangDoi = new LinkedList<>();
        
        // Xếp hàng
        hangDoi.offer("Khách A");
        hangDoi.offer("Khách B");
        hangDoi.offer("Khách C");
        
        System.out.println("Hàng đợi hiện tại: " + hangDoi); // [Khách A, Khách B, Khách C]
        
        // Phục vụ (lấy ra người đầu tiên)
        String phucVu1 = hangDoi.poll();
        System.out.println("Đang phục vụ: " + phucVu1); // Khách A
        
        System.out.println("Còn lại trong hàng: " + hangDoi); // [Khách B, Khách C]
    }
}
```

---

### Bảng Tóm tắt nhanh (Cheat Sheet)

| Tiêu chí                   | `List`                                                                              | `Set`                                                                                                      | `Queue`                                                                  |
| :------------------------- | :---------------------------------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------- |
| **Thứ tự (Order)**         | Có thứ tự chèn vào                                                                  | Không đảm bảo (trừ LinkedHashSet)                                                                          | Theo thứ tự hàng đợi (FIFO)                                              |
| **Trùng lặp (Duplicates)** | **Có**                                                                              | **Không**                                                                                                  | **Có** (thường dùng cho các tác vụ giống nhau)                           |
| **Truy cập bằng Index**    | Có (`get(i)`)                                                                       | Không                                                                                                      | Không (chỉ lấy ở đầu)                                                    |
| **Khi nào dùng?**          | Khi cần lưu danh sách, tìm kiếm, lấy theo vị trí (VD: Danh sách sản phẩm, danh bạ). | Khi cần lọc các phần tử duy nhất, kiểm tra tồn tại (VD: Danh sách ID đã xem, danh sách email không trùng). | Khi cần xử lý tuần tự, xếp hàng chờ (VD: Hàng đợi in ấn, xử lý request). |

**Lời khuyên:** Khi mới học, bạn sẽ dùng **`List` (thường là `ArrayList`)** nhiều nhất (khoảng 80% thời gian). Chỉ khi nào bài toán yêu cầu "không được trùng lặp" thì mới chuyển sang `Set`.



Chào bạn, 5 bức ảnh này cung cấp cái nhìn chi tiết nhất về cấu trúc phân cấp của **Collection Framework** trong Java. Chúng ta sẽ bóc tách từng ảnh, kết hợp giải thích lý thuyết và code ví dụ để hiểu rõ cách hoạt động của 3 giao diện con: `List`, `Set`, và `Queue`.

---

### 1. Tổng quan phân cấp (Dựa trên ảnh 1)
Giao diện `Collection` là gốc, sinh ra 3 nhánh chính:
*   **`List`**: Danh sách (có thứ tự, cho phép trùng).
*   **`Set`**: Tập hợp (không trùng lặp).
*   **`Queue`**: Hàng đợi (xử lý theo thứ tự).

---

### 2. Giao diện `List` (Dựa trên ảnh 2)

**Lý thuyết:**
`List` mở rộng `Collection` bằng cách thêm các phương thức thao tác dựa trên **chỉ mục (Index)**. Điều này cho phép bạn truy cập, thay thế, hoặc chèn phần tử vào bất kỳ vị trí nào.

**Các phương thức mới (Abstract Methods):**
*   `get(int index)`: Lấy phần tử tại vị trí `index`.
*   `set(int index, E element)`: Thay thế phần tử tại `index` bằng `element` mới.
*   `add(int index, E element)`: Chèn `element` vào vị trí `index` (các phần tử phía sau bị đẩy lùi).
*   `indexOf(Object o)`: Tìm vị trí **đầu tiên** của phần tử `o`. Trả về `-1` nếu không thấy.
*   `lastIndexOf(Object o)`: Tìm vị trí **cuối cùng** của phần tử `o`.
*   `subList(int fromIndex, int toIndex)`: Cắt lấy một đoạn con từ `fromIndex` đến `toIndex`.
*   `listIterator()`: Trả về một Iterator cho phép duyệt tiến và lùi.

**Phương thức Default (Có sẵn từ Java 8):**
*   `replaceAll(UnaryOperator)`: Biến đổi tất cả phần tử theo một quy tắc.
*   `sort(Comparator)`: Sắp xếp danh sách.

**Các lớp triển khai (Implementations):** `ArrayList`, `LinkedList`, `Stack`, `Vector`.

**Ví dụ Code:**
```java
import java.util.*;

public class ListDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "B"));

        // 1. get & set
        System.out.println("Phần tử index 1: " + list.get(1)); // B
        list.set(1, "X");
        System.out.println("Sau khi set: " + list); // [A, X, C, B]

        // 2. add(index)
        list.add(0, "Z");
        System.out.println("Sau khi chèn Z vào đầu: " + list); // [Z, A, X, C, B]

        // 3. indexOf & lastIndexOf
        System.out.println("Vị trí đầu tiên của B: " + list.indexOf("B")); // 4
        System.out.println("Vị trí cuối cùng của B: " + list.lastIndexOf("B")); // 4

        // 4. subList
        List<String> sub = list.subList(1, 3); // Lấy từ index 1 đến trước index 3
        System.out.println("Đoạn con: " + sub); // [A, X]

        // 5. Default methods
        list.sort(Comparator.naturalOrder()); // Sắp xếp A-Z
        System.out.println("Sau khi sort: " + list);
    }
}
```

---

### 3. Giao diện `Set` (Dựa trên ảnh 3)

**Lý thuyết:**
Nhìn vào ảnh 3, bạn sẽ thấy phần "New abstract methods introduced" ghi là **N/A (Không có gì mới)**. Tại sao? Vì `Set` không thêm phương thức mới nào so với `Collection`.
Sức mạnh của `Set` nằm ở **Hợp đồng (Contract)** của nó: **Không được phép có phần tử trùng lặp**.

**Các lớp triển khai (Implementations):**
*   `HashSet`: Thứ tự ngẫu nhiên, tốc độ cực nhanh.
*   `LinkedHashSet`: Giữ nguyên thứ tự lúc thêm vào.
*   `TreeSet`: Tự động sắp xếp tăng dần.
*   `EnumSet`: Tối ưu riêng cho kiểu dữ liệu Enum.

**Ví dụ Code:**
```java
import java.util.*;

public class SetDemo {
    public static void main(String[] args) {
        // HashSet: Không trùng lặp, không thứ tự
        Set<String> set = new HashSet<>();
        set.add("Java");
        set.add("Python");
        boolean check = set.add("Java"); // Thêm lại Java

        System.out.println("Thêm Java lần 2 thành công? " + check); // false
        System.out.println(set); // [Java, Python] (Hoặc [Python, Java])

        // TreeSet: Tự động sắp xếp
        Set<Integer> treeSet = new TreeSet<>(Arrays.asList(5, 1, 9, 3, 1));
        System.out.println("TreeSet: " + treeSet); // [1, 3, 5, 9] (Đã loại trùng số 1)
    }
}
```

---

### 4. Giao diện `Queue` & Nguyên lý FIFO (Dựa trên ảnh 4 & 5)

**Lý thuyết:**
`Queue` hoạt động theo nguyên lý **FIFO (First In First Out - Vào trước Ra trước)**, như ảnh 5 mô tả (một cái ống, nhét vào đầu này thì chui ra ở đầu kia).
*   Ảnh 4 cung cấp các phương thức mới, chia làm 2 nhóm (dễ gây nhầm lẫn):
    *   **Nhóm an toàn (Không ném Exception):**
        *   `offer(e)`: Thêm vào, trả về `false` nếu đầy.
        *   `poll()`: Lấy ra ở đầu, trả về `null` nếu hàng rỗng.
        *   `peek()`: Xem đầu hàng, trả về `null` nếu rỗng.
    *   **Nhóm ném Exception (Sẽ crash chương trình nếu hàng rỗng/đầy):**
        *   `remove()`: Lấy ra ở đầu, ném lỗi `NoSuchElementException` nếu rỗng.
        *   `element()`: Xem đầu hàng, ném lỗi nếu rỗng.

**Các lớp triển khai:** `ArrayDeque` (Mảng 2 đầu, rất nhanh), `LinkedList`, `PriorityQueue` (Hàng đợi ưu tiên).

**Ví dụ Code (Minh họa FIFO và sự khác biệt giữa poll/remove):**
```java
import java.util.*;

public class QueueDemo {
    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<>();

        // 1. Thêm vào (offer)
        queue.offer("Khách 1");
        queue.offer("Khách 2");
        queue.offer("Khách 3");

        // 2. Xem đầu hàng (peek)
        System.out.println("Đầu hàng: " + queue.peek()); // Khách 1

        // 3. Lấy ra (poll - FIFO)
        System.out.println("Phục vụ: " + queue.poll()); // Khách 1
        System.out.println("Phục vụ: " + queue.poll()); // Khách 2
        System.out.println("Phục vụ: " + queue.poll()); // Khách 3

        // Lúc này hàng đợi đã rỗng
        System.out.println("Poll khi rỗng: " + queue.poll()); // null (An toàn)
        
        // Nếu dùng remove() khi rỗng sẽ bị lỗi:
        // queue.remove(); // Ném ra NoSuchElementException -> Chương trình dừng!
    }
}
```

---

### Tóm tắt nhanh
1.  Dùng **`List`** khi bạn cần một mảng động, cần lấy phần tử theo vị trí (index), và chấp nhận có dữ liệu trùng lặp (VD: Danh sách sản phẩm).
2.  Dùng **`Set`** khi bạn cần lọc các phần tử duy nhất (VD: Danh sách ID người dùng đã like bài viết, không thể like 2 lần).
3.  Dùng **`Queue`** khi bạn cần xử lý tuần tự theo nguyên tắc vào trước ra trước (VD: Hàng đợi in tài liệu, hàng đợi xử lý tin nhắn).