Chào bạn, dựa trên 4 bức ảnh bạn cung cấp, bài học này đi sâu vào các **Lớp triển khai (Implementations) của giao diện `List`**. Đây là phần cực kỳ quan trọng để hiểu rõ "hậu trường" hoạt động của các cấu trúc dữ liệu trong Java, giúp bạn chọn đúng công cụ cho đúng bài toán.

Dưới đây là phần giải thích chi tiết lý thuyết từ ảnh, kết hợp với code ví dụ minh họa.

---

### 1. Ưu điểm của giao diện `List` (Dựa trên ảnh 2)

Tại sao các kỹ sư lại chọn `List`?
*   **Hỗ trợ thao tác dựa trên chỉ mục (Index):** Truy cập phần tử cực nhanh thông qua vị trí.
*   **Cho phép trùng lặp (Duplicates):** Thêm 2 sản phẩm giống hệt nhau vào giỏ hàng vẫn hợp lệ.
*   **Tạo danh sách con (Sub-lists):** Dễ dàng cắt một đoạn dữ liệu ra để xử lý riêng.
*   **Sắp xếp (Sorting):** Có sẵn các công cụ để sắp xếp danh sách.
*   **Ứng dụng rộng rãi:** Ảnh minh họa một trang web xem lịch thi đấu bóng đá. Danh sách các trận đấu cần được hiển thị theo thứ tự thời gian, cho phép xem chi tiết từng trận (dựa vào index).

---

### 2. Hệ thống phân cấp (List Hierarchy - Dựa trên ảnh 3)

Bức ảnh UML này rất quan trọng, nó cho thấy:
*   `ArrayList`, `Vector`, `CopyOnWriteArrayList`, `LinkedList` đều là con đẻ của `List`.
*   `Stack` được kế thừa từ `Vector`.
*   **Điểm đặc biệt: Marker Interface `RandomAccess` (Giao diện đánh dấu).**
    *   Hãy nhìn đường nét đứt nối từ `ArrayList`, `Vector`, `Stack` đến `RandomAccess`.
    *   **RandomAccess không có bất kỳ phương thức nào**. Nó chỉ là một "nhãn dán" (Marker).
    *   **Ý nghĩa:** Khi một List implements `RandomAccess`, nó cam kết với máy ảo Java rằng: *"Việc lấy phần tử theo vị trí (hàm `get(index)`) ở lớp này diễn ra cực kỳ nhanh (O(1))"*. Điều này giúp các thuật toán (như sắp xếp, tìm kiếm) tự động tối ưu hóa vòng lặp (dùng vòng lặp `for` thay vì `Iterator`).
    *   `LinkedList` **không** có nhãn này vì muốn lấy phần tử thứ `n`, nó phải duyệt từ đầu đến `n`.

---

### 3. Cơ chế hoạt động của `ArrayList` (Dựa trên ảnh 4)

Bức ảnh 4 giải thích cách `ArrayList` hoạt động bên dưới (nó thực chất là một mảng tĩnh - Array, nhưng biết tự động mở rộng).

*   **Sức chứa mặc định (Default capacity) = 10:** Khi bạn khởi tạo `new ArrayList<>()`, nó tạo một mảng rỗng, nhưng khi bạn thêm phần tử đầu tiên, nó sẽ cấp phát một mảng có kích thước 10.
*   **Khi mảng đầy (Initial Capacity is full):** Nếu bạn thêm phần tử thứ 11, mảng 10 phần tử không chứa nổi nữa.
*   **Cơ chế tự mở rộng (Resizing):**
    1.  Java tạo ra một **mảng mới to hơn** (thường là gấp 1.5 lần hoặc gấp đôi kích thước cũ - tùy phiên bản JDK, như trong ảnh ghi là "Double").
    2.  Nó **copy toàn bộ** dữ liệu từ mảng cũ sang mảng mới.
    3.  Thêm phần tử mới vào mảng mới.
    4.  Hủy mảng cũ.

*   **Hệ quả:** Thao tác thêm/xóa ở **cuối** danh sách rất nhanh. Nhưng thêm/xóa ở **giữa hoặc đầu** danh sách lại **chậm**, vì nó phải dịch chuyển (copy) toàn bộ các phần tử phía sau sang vị trí mới.

---

### 4. Giải thích & Code ví dụ cho từng Lớp triển khai (Implementations)

#### A. `ArrayList` (Lớp phổ biến nhất)
*   **Đặc điểm:** Dựa trên mảng động. Truy cập cực nhanh, thêm/xóa ở giữa chậm. Không đồng bộ (Non-synchronized - chạy nhanh nhưng không an toàn trong đa luồng).
*   **Code ví dụ:**
```java
import java.util.ArrayList;
import java.util.List;

public class ArrayListDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        
        // Thêm 10 phần tử (Mảng ngầm định kích thước 10)
        for (int i = 1; i <= 10; i++) {
            list.add("Item " + i);
        }
        
        // Thêm phần tử thứ 11 -> Java tự động tạo mảng mới to hơn và copy dữ liệu
        list.add("Item 11"); 
        
        // Truy cập cực nhanh nhờ RandomAccess
        System.out.println(list.get(5)); // Item 6
    }
}
```

#### B. `Vector` (Lớp cổ điển)
*   **Đặc điểm:** Giống hệt `ArrayList` về cấu trúc mảng động. Tuy nhiên, `Vector` là **Synchronized (Đồng bộ)**. Điều này có nghĩa là nó an toàn khi dùng trong môi trường đa luồng (nhiều luồng cùng sửa dữ liệu), nhưng tốc độ lại **chậm hơn** `ArrayList`. Ngày nay ít dùng, thường được thay thế bằng `CopyOnWriteArrayList` hoặc `Collections.synchronizedList()`.

#### C. `Stack` (Ngăn xếp)
*   **Đặc điểm:** Kế thừa từ `Vector`. Hoạt động theo nguyên tắc **LIFO (Last-In-First-Out - Vào sau Ra trước)**. Giống như chồng đĩa, bạn xếp đĩa lên trên cùng thì phải lấy cái trên cùng xuống trước.
*   **Các hàm đặc trưng:** `push()` (đẩy vào), `pop()` (lấy ra), `peek()` (xem đỉnh).
*   **Code ví dụ:**
```java
import java.util.Stack;

public class StackDemo {
    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        
        stack.push("Trang 1");
        stack.push("Trang 2");
        stack.push("Trang 3");
        
        // Xem đỉnh
        System.out.println("Đang xem: " + stack.peek()); // Trang 3
        
        // Lấy ra (Vào sau ra trước)
        System.out.println("Quay lại: " + stack.pop()); // Trang 3
        System.out.println("Quay lại: " + stack.pop()); // Trang 2
    }
}
```

#### D. `CopyOnWriteArrayList` (Dành cho Đa luồng - Concurrency)
*   **Đặc điểm:** Đây là lớp rất đặc biệt.
    *   Khi bạn **chỉ ĐỌC (Read)**: Nhiều luồng có thể đọc cùng lúc mà không cần khóa, cực kỳ nhanh.
    *   Khi bạn **THAY ĐỔI (Write - add/set/remove)**: Nó sẽ **tạo ra một bản sao hoàn toàn mới (Copy)** của toàn bộ mảng, thực hiện thay đổi trên bản sao đó, rồi hoán đổi. (Đúng như tên gọi: Copy On Write).
*   **Khi nào dùng?** Khi danh sách của bạn có tần suất Đọc (Read) cực nhiều, nhưng hiếm khi Thay đổi (Write). Ví dụ: Danh sách những người đang theo dõi (subscribers) trong một hệ thống, hoặc danh sách các sự kiện (listeners).
*   **Nhược điểm:** Nếu bạn thêm/xóa liên tục, nó sẽ tạo ra rất nhiều mảng mới, tốn bộ nhớ và cực chậm.

```java
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteDemo {
    public static void main(String[] args) {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.add("A");
        list.add("B");
        
        // Khi add "C", nó tạo ra mảng mới [A, B, C] và trỏ list vào mảng mới.
        list.add("C"); 
        System.out.println(list);
    }
}
```

---

### Bảng so sánh nhanh (Cheat Sheet)

| Lớp triển khai             | Cấu trúc ngầm | Ưu điểm                                                                                               | Nhược điểm                                                               | Trường hợp sử dụng                                                                   |
| :------------------------- | :------------ | :---------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------- | :----------------------------------------------------------------------------------- |
| **`ArrayList`**            | Mảng động     | Truy cập Index cực nhanh (`get`), thêm ở cuối nhanh.                                                  | Thêm/Xóa ở giữa chậm (do phải dịch chuyển mảng). Không an toàn đa luồng. | Mặc định cho hầu hết các trường hợp (90%). Danh sách sản phẩm, danh sách user.       |
| **`Vector`**               | Mảng động     | An toàn đa luồng (Synchronized).                                                                      | Chậm hơn ArrayList. Đã lỗi thời (Legacy).                                | Hiếm khi dùng, trừ khi làm việc với code Java rất cũ.                                |
| **`Stack`**                | Mảng động     | Xử lý theo LIFO.                                                                                      | Chậm, kế thừa từ Vector.                                                 | Tính năng Undo/Redo, duyệt cây, tính toán biểu thức. (Nay khuyên dùng `ArrayDeque`). |
| **`CopyOnWriteArrayList`** | Mảng Copy     | An toàn đa luồng tuyệt đối khi Đọc. Không bao giờ bị lỗi `ConcurrentModificationException` khi duyệt. | Ghi (Write) cực kỳ tốn bộ nhớ và chậm.                                   | Danh sách các sự kiện, danh sách cấu hình ít khi thay đổi nhưng được đọc liên tục.   |

*(Lưu ý: `LinkedList` có trong ảnh 3 nhưng bài học này không đi sâu, nó hoạt động theo cơ chế danh sách liên kết kép, thêm/xóa ở đầu/cuối cực nhanh, nhưng truy cập `get(index)` rất chậm).*