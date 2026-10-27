Chào bạn, dựa trên 2 bức ảnh bạn cung cấp, bài học này đi sâu vào **Cơ chế duyệt dữ liệu (Traversal)** trong Java thông qua bộ ba: `Iterable`, `Iterator`, và `ListIterator`. Đây là những kiến thức nền tảng cực kỳ quan trọng, đặc biệt khi đi làm phỏng vấn (vì liên quan đến lỗi `ConcurrentModificationException` rất phổ biến).

Dưới đây là phần giải thích chi tiết lý thuyết kết hợp code ví dụ.

---

### 1. `Iterable` Interface (Giao diện có thể lặp)

**Lý thuyết:**
*   Ở bài học đầu tiên, chúng ta đã thấy `Collection` kế thừa từ `Iterable`. 
*   `Iterable` là giao diện gốc cao nhất. Nó chỉ có **duy nhất 1 phương thức trừu tượng** là `iterator()`.
*   **Tác dụng:** Khi một lớp implements `Iterable`, nó cam kết rằng: *"Tôi có thể cung cấp một công cụ để duyệt qua các phần tử của tôi"*. 
*   **Liên hệ:** Vòng lặp `for-each` (`for (String s : list)`) mà bạn hay dùng thực chất là "cú pháp đường" (syntactic sugar) của `Iterable` và `Iterator`. Trình biên dịch Java sẽ tự động chuyển vòng lặp `for-each` thành code dùng `Iterator` ở bên dưới.

---

### 2. `Iterator` Interface (Công cụ duyệt)

**Lý thuyết (Dựa trên ảnh 2):**
*   Ảnh 2 mô tả `Iterator` giống như một **con trỏ (cursor)** di chuyển qua các ô dữ liệu.
*   Ban đầu, con trỏ nằm *trước* phần tử đầu tiên.
*   Khi bạn gọi `next()`, con trỏ nhảy qua ô tiếp theo và trả về dữ liệu của ô đó (như mũi tên "returned element" trong ảnh).
*   Giao diện `Iterator` có 3 phương thức chính:
    *   `hasNext()`: Kiểm tra xem phía trước còn phần tử nào không (trả về `true`/`false`).
    *   `next()`: Lấy phần tử hiện tại và di chuyển con trỏ sang phần tử tiếp theo.
    *   `remove()`: Xóa phần tử vừa được lấy ra bởi `next()`.

**Code ví dụ:**
```java
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Táo");
        list.add("Cam");
        list.add("Chuối");

        // Lấy công cụ Iterator từ List
        Iterator<String> iterator = list.iterator();

        // Duyệt bằng Iterator
        while (iterator.hasNext()) {
            String monAn = iterator.next(); // Lấy ra và nhảy sang ô tiếp theo
            System.out.println(monAn);
            
            // Nếu là "Cam" thì xóa nó đi an toàn bằng iterator.remove()
            if (monAn.equals("Cam")) {
                iterator.remove(); 
            }
        }
        System.out.println("Sau khi xóa: " + list); // [Táo, Chuối]
    }
}
```

---

### 3. Các ngoại lệ (Exceptions) thường gặp (Từ Agenda)

**1. `NoSuchElementException`:**
*   Xảy ra khi bạn gọi `iterator.next()` trong khi `iterator.hasNext()` đã trả về `false` (tức là đã duyệt hết danh sách mà cố lấy thêm).

**2. `ConcurrentModificationException` (Lỗi kinh điển):**
*   Xảy ra khi bạn đang duyệt một Collection bằng `Iterator` (hoặc `for-each`), nhưng bạn lại dùng chính đối tượng Collection đó để **thêm (`add`)** hoặc **xóa (`remove`)** phần tử.
*   *Ví dụ gây lỗi:*
    ```java
    List<String> list = new ArrayList<>(List.of("A", "B", "C"));
    for (String s : list) {
        if (s.equals("B")) {
            list.remove("B"); // NÉM LỖI ConcurrentModificationException ngay lập tức!
        }
    }
    ```
*   *Cách khắc phục:* Sử dụng `iterator.remove()` như ví dụ ở mục 2, hoặc dùng `list.removeIf(s -> s.equals("B"))`.

---

### 4. Ba loại Iterator (Fail-fast, Fail-safe, Weakly consistent)

Đây là phần lý thuyết nâng cao (dành cho phỏng vấn) giải thích cách Iterator phản ứng khi Collection bị thay đổi trong lúc đang duyệt.

*   **Fail-fast (Thất bại nhanh):**
    *   Được dùng bởi hầu hết các Collection thông thường (`ArrayList`, `HashMap`, `HashSet`).
    *   Khi bạn duyệt bằng Iterator mà có một luồng (thread) khác (hoặc chính bạn) sửa cấu trúc Collection, nó sẽ **ném ngay lỗi `ConcurrentModificationException`** để bảo vệ dữ liệu không bị sai lệch.
*   **Fail-safe (An toàn thất bại):**
    *   Được dùng bởi các Collection trong gói `java.util.concurrent` (ví dụ: `CopyOnWriteArrayList`, `ConcurrentHashMap`).
    *   Khi duyệt, nó làm việc trên một **bản sao (clone)** của Collection gốc. Do đó, nếu Collection gốc bị sửa, Iterator vẫn chạy bình thường (không ném lỗi), nhưng dữ liệu bạn đang duyệt có thể là dữ liệu cũ.
*   **Weakly consistent (Nhất quán yếu):**
    *   Thường thấy ở `ConcurrentHashMap`. Nó không ném lỗi, không làm việc trên bản sao, nhưng có thể phản ánh một số thay đổi (hoặc không) tùy thời điểm. Nó đảm bảo không bao giờ ném lỗi CME và duyệt qua tất cả các phần tử tồn tại tại thời điểm Iterator được tạo (hoặc sau đó).

---

### 5. `ListIterator` (Iterator nâng cao cho List)

**Lý thuyết:**
*   `Iterator` thông thường chỉ có thể duyệt **tiến (từ trái sang phải)**.
*   `ListIterator` chỉ dành riêng cho các lớp implements `List` (như `ArrayList`, `LinkedList`). Nó mạnh hơn ở chỗ:
    *   Duyệt được cả **tiến và lùi** (`hasPrevious()`, `previous()`).
    *   Lấy được **chỉ mục (index)** hiện tại (`nextIndex()`, `previousIndex()`).
    *   Có thể **thêm (`add`)** hoặc **thay thế (`set`)** phần tử ngay trong lúc duyệt.

**Code ví dụ:**
```java
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ListIteratorDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(List.of("A", "B", "C"));

        // Bắt đầu từ cuối danh sách để duyệt lùi
        ListIterator<String> listIterator = list.listIterator(list.size());

        System.out.println("Duyệt lùi:");
        while (listIterator.hasPrevious()) {
            String item = listIterator.previous();
            System.out.println(item); // C, B, A
        }

        // Sửa đổi phần tử ngay trong lúc duyệt
        ListIterator<String> it2 = list.listIterator();
        while (it2.hasNext()) {
            String item = it2.next();
            if (item.equals("B")) {
                it2.set("B-Đã sửa"); // Thay thế B
            }
            if (item.equals("A")) {
                it2.add("A-Mới"); // Thêm ngay sau A
            }
        }
        System.out.println("List sau khi sửa: " + list); // [A, A-Mới, B-Đã sửa, C]
    }
}
```

---
**Tóm lại (Homework gợi ý):**
Hãy thử viết code tạo một `ArrayList` chứa các số từ 1 đến 10. Dùng `Iterator` để duyệt và xóa tất cả các số chẵn. (Nhớ dùng `iterator.remove()`, tuyệt đối không dùng `list.remove()` để tránh lỗi nhé!).