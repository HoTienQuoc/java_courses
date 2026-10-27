Chào bạn, 9 bức ảnh này cung cấp một cái nhìn toàn cảnh và cực kỳ thực tế về **Queue (Hàng đợi)** và **Deque (Hàng đợi hai đầu)** trong Java. 

Đây là những cấu trúc dữ liệu không thể thiếu khi xây dựng các hệ thống lớn (như xử lý đơn hàng, chat, hệ thống gửi mail...). Hãy cùng bóc tách chi tiết lý thuyết và code ví dụ cho từng phần nhé.

---

### 1. `Queue` Interface và Nguyên lý FIFO (Ảnh 2, 3)

**Lý thuyết:**
*   **FIFO (First-In-First-Out):** Vào trước - Ra trước. Hãy tưởng tượng một đường ống nước (như ảnh 2). Bạn bơm nước vào đầu này, nước sẽ chảy ra ở đầu kia theo đúng thứ tự.
*   **Ứng dụng thực tế (Ảnh 3):** Hệ thống kiến trúc Microservices. Các trang web Marketing gửi yêu cầu đến `Onboarding Service`. Dịch vụ này không xử lý ngay mà ném yêu cầu vào một `QUEUE`. Dịch vụ `Payment Service` sẽ lấy từng yêu cầu từ Queue ra để xử lý. Điều này giúp hệ thống không bị quá tải (giống như xếp hàng mua vé, ai đến trước mua trước).

**Code ví dụ (Hàng đợi khách hàng):**
```java
import java.util.LinkedList;
import java.util.Queue;

public class QueueFIFO {
    public static void main(String[] args) {
        Queue<String> khachHang = new LinkedList<>();
        
        // Thêm vào cuối hàng (offer)
        khachHang.offer("Khách A");
        khachHang.offer("Khách B");
        khachHang.offer("Khách C");
        
        // Phục vụ (Lấy ra từ đầu hàng - poll)
        while (!khachHang.isEmpty()) {
            System.out.println("Đang phục vụ: " + khachHang.poll());
        }
        // Kết quả: Khách A -> Khách B -> Khách C
    }
}
```

---

### 2. `Deque` Interface và Nguyên lý LIFO (Ảnh 4, 5)

**Lý thuyết:**
*   **Deque (Double-Ended Queue):** Là hàng đợi mà bạn có thể thêm/xóa ở **cả hai đầu**.
*   Vì có thể thêm/xóa ở cùng một đầu, Deque có thể hoạt động theo nguyên lý **LIFO (Last-In-First-Out - Vào sau Ra trước)**. Giống như xếp đĩa (ảnh 4), cái nào bỏ vào sau cùng sẽ nằm trên cùng và được lấy ra đầu tiên. Hoặc giống như băng đạn trong súng.
*   **Ứng dụng thực tế (Ảnh 5):**
    *   **Undo Operation (Ctrl + Z):** Gõ chữ "Hello", sau đó gõ "World". Khi ấn Undo, chữ "World" (vào sau) bị xóa trước.
    *   **Browser History:** Nút Back/Forward trên trình duyệt.
    *   **Method Invocation Stack (Call Stack):** Khi gọi hàm `method1()` -> gọi `method2()` -> gọi `method3()`. Hàm `method3()` phải kết thúc trước thì `method2()` mới chạy tiếp.

**Code ví dụ (Tính năng Undo với Deque):**
```java
import java.util.ArrayDeque;
import java.util.Deque;

public class DequeLIFO {
    public static void main(String[] args) {
        // Sử dụng ArrayDeque làm Stack (LIFO)
        Deque<String> undoStack = new ArrayDeque<>();
        
        // Thêm vào đầu (giống push vào Stack)
        undoStack.push("Gõ Hello");
        undoStack.push("Gõ World");
        undoStack.push("Tô đậm chữ");
        
        // Lấy ra từ đầu (giống pop khỏi Stack) - Vào sau ra trước
        System.out.println("Hoàn tác: " + undoStack.pop()); // Tô đậm chữ
        System.out.println("Hoàn tác: " + undoStack.pop()); // Gõ World
        System.out.println("Hoàn tác: " + undoStack.pop()); // Gõ Hello
    }
}
```

---

### 3. Các Class Triển khai (Implementations) phổ biến (Ảnh 6, 7)

Bức ảnh 7 phân chia các lớp triển khai thành 2 nhóm cực kỳ quan trọng:

**A. Non-Thread-Safe (Không an toàn trong đa luồng - Dùng trong 90% trường hợp bình thường):**
*   **`LinkedList`:** Có thể dùng làm cả List, Queue và Deque.
*   **`ArrayDeque` (Khuyên dùng):** Hiệu năng cực cao, nhanh hơn `LinkedList` và `Stack`. Thường được dùng khi cần làm Queue hoặc Stack.
*   **`PriorityQueue`:** (Sẽ giải thích kỹ ở mục 4).

**B. Thread-Safe (An toàn trong đa luồng - Dùng khi có nhiều Thread cùng đọc/ghi):**
*   **`ArrayBlockingQueue`, `LinkedBlockingDeque`, `PriorityBlockingQueue`:** Đây là các "Blocking Queue". Chúng thường được dùng trong mô hình **Producer - Consumer** (Người sản xuất - Người tiêu thụ). Khi hàng đợi đầy, luồng Producer sẽ bị block (chờ) cho đến khi Consumer lấy bớt ra. Khi hàng đợi rỗng, luồng Consumer sẽ bị block cho đến khi có hàng mới.

---

### 4. `PriorityQueue` - Hàng đợi ưu tiên (Ảnh 8, 9)

**Lý thuyết:**
*   Đây là một ngoại lệ của Queue. Nó **KHÔNG** hoạt động theo nguyên lý FIFO.
*   `PriorityQueue` tự động sắp xếp các phần tử dựa trên **độ ưu tiên (Priority)**. Phần tử có độ ưu tiên cao nhất sẽ luôn được lấy ra (`poll()`) đầu tiên, bất kể nó được thêm vào lúc nào.
*   Độ ưu tiên được quyết định bởi `Comparable` (mặc định) hoặc `Comparator` (do bạn tự định nghĩa) - (Giống hệt kiến thức bài học trước).
*   **Ứng dụng thực tế (Ảnh 8, 9):** Hệ thống Helpdesk / Chăm sóc khách hàng. Khách hàng VIP gửi ticket sẽ được xử lý trước khách hàng thường, dù khách thường gửi trước.

**Code ví dụ (Hệ thống Helpdesk ưu tiên VIP):**
```java
import java.util.PriorityQueue;
import java.util.Queue;

class Ticket implements Comparable<Ticket> {
    String tenKhach;
    int doUuTien; // Số càng nhỏ ưu tiên càng cao (1: VIP, 2: Thường)

    public Ticket(String tenKhach, int doUuTien) {
        this.tenKhach = tenKhach;
        this.doUuTien = doUuTien;
    }

    // Sắp xếp theo độ ưu tiên (số nhỏ đứng trước)
    @Override
    public int compareTo(Ticket khac) {
        return Integer.compare(this.doUuTien, khac.doUuTien);
    }

    @Override
    public String toString() {
        return tenKhach + " (Ưu tiên: " + doUuTien + ")";
    }
}

public class PriorityQueueDemo {
    public static void main(String[] args) {
        Queue<Ticket> hangCho = new PriorityQueue<>();
        
        hangCho.offer(new Ticket("Khách Thường 1", 2));
        hangCho.offer(new Ticket("Khách VIP", 1)); // Được chen lên đầu
        hangCho.offer(new Ticket("Khách Thường 2", 2));
        
        // Dù "Khách VIP" vào sau, nhưng vẫn được lấy ra đầu tiên
        while (!hangCho.isEmpty()) {
            System.out.println("Đang xử lý: " + hangCho.poll());
        }
        /* Kết quả:
           Khách VIP (Ưu tiên: 1)
           Khách Thường 1 (Ưu tiên: 2)
           Khách Thường 2 (Ưu tiên: 2)
        */
    }
}
```

---
**Tóm tắt lời khuyên khi làm việc thực tế:**
1.  Cần hàng đợi thông thường (FIFO) -> Dùng **`ArrayDeque`** (nhanh nhất) hoặc `LinkedList`.
2.  Cần ngăn xếp (LIFO) -> Dùng **`ArrayDeque`** (tốt hơn class `Stack` cũ kỹ).
3.  Cần ưu tiên xử lý một số phần tử trước -> Dùng **`PriorityQueue`**.
4.  Cần xử lý đa luồng (Producer/Consumer) -> Dùng các lớp `BlockingQueue` trong gói `java.util.concurrent`.