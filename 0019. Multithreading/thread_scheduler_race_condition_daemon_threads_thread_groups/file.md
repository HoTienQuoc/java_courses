Chào bạn, dựa trên các hình ảnh slide và 5 file code Java bạn đã cung cấp, mình sẽ hệ thống lại một cách chi tiết nhất về **Thread Scheduler (Bộ lập lịch luồng)**, **Race Condition (Điều kiện tranh chấp)**, **Daemon Threads**, và **Thread Groups**. 

Cấu trúc bài viết sẽ gồm 2 phần chính: 
1. Giải thích chi tiết lý thuyết trên các slide.
2. Phân tích code chi tiết kèm lý thuyết ngay bên cạnh.

---

# PHẦN 1: GIẢI THÍCH CHI TIẾT CÁC SLIDE

### 1. Slide Agenda (Lịch trình)
Slide này tóm tắt các chủ đề chính trong bài học:
*   **Thread scheduler**: Bộ lập lịch luồng.
*   **Race condition**: Điều kiện tranh chấp (lỗi xảy ra khi nhiều luồng truy cập dữ liệu chung).
*   **Priority field in Thread**: Trường ưu tiên của luồng.
*   **yield() method**: Phương thức nhường quyền CPU.
*   **Daemon threads**: Luồng nền.
*   **Thread Groups**: Nhóm luồng.
*   **Throwing exception from run() method**: Ném ngoại lệ từ phương thức run().

### 2. Slide Thread Scheduler – Decision Making Factors (Các yếu tố quyết định)
*   **Thread Scheduler là gì?** Là một phần của Hệ điều hành/JVM, chịu trách nhiệm phân bổ thời gian CPU cho các luồng (Threads).
*   **Decision Making Factors (Yếu tố quyết định):**
    *   **Priority (Độ ưu tiên):** Các luồng có độ ưu tiên cao hơn thường có cơ hội được chọn chạy trước.
    *   **Arrival time (Thời điểm đến):** Luồng nào được tạo/đến trước có thể được xử lý trước (tùy thuật toán).
*   **Mô tả trong hình:** Một Process chứa nhiều Thread với các độ ưu tiên khác nhau (10, 5, 1, 5). Thread Scheduler sẽ xử lý và quyết định đưa Thread nào vào Processor (CPU) để thực thi.

### 3. Slide Thread Scheduler – Scheduling Algorithms (Thuật toán lập lịch)
Các thuật toán phổ biến mà Thread Scheduler sử dụng:
*   **Preemptive-priority scheduling (Lập lịch ưu tiên có độc quyền):** Nếu một luồng có độ ưu tiên cao hơn đang chờ, HĐH sẽ tạm dừng (preempt) luồng đang chạy để nhường CPU cho luồng ưu tiên cao hơn.
*   **First Come First Serve (FCFS):** Luồng nào đến trước thì chạy trước (như xếp hàng mua vé).
*   **Time-slicing scheduling (Lập lịch chia lát thời gian):** Mỗi luồng được cấp một khoảng thời gian cực nhỏ (time slice) để chạy (ví dụ 10ms). Hết thời gian, nó bị tạm dừng để nhường cho luồng khác (Round-Robin).

### 4. Slide Multithreading App Example (Ví dụ ứng dụng đa luồng)
*   Slide này minh họa trực quan: Có 5 luồng (Thread 1 đến 5) với các mức độ ưu tiên khác nhau (5, 3, 6, 8, 6).
*   Các luồng này nằm trong hàng đợi chờ. Thread Scheduler sẽ áp dụng các thuật toán ở trên để đưa từng luồng vào Processor (CPU) thực thi.

### 5. Slide Race Condition (Điều kiện tranh chấp)
*   **Định nghĩa:** Xảy ra khi nhiều luồng cùng truy cập và thay đổi dữ liệu dùng chung (shared data) cùng một lúc mà không có cơ chế đồng bộ (synchronization). Kết quả cuối cùng phụ thuộc vào việc luồng nào chạy trước, luồng nào chạy sau (không thể đoán trước).
*   **Hậu quả (Potential Impacts):**
    *   *Memory leak:* Rò rỉ bộ nhớ.
    *   *Inconsistent data:* Dữ liệu không nhất quán/sai lệch.
    *   *Deadlock:* Khóa chết (các luồng chờ nhau vô hạn).
    *   *Livelock:* Khóa sống (các luồng liên tục thay đổi trạng thái để nhường nhau nhưng không ai thực sự làm việc).

---

# PHẦN 2: PHÂN TÍCH CODE & LÝ THUYẾT ĐI KÈM

### 1. `PriorityDemo.java` - Độ ưu tiên của luồng (Thread Priority)

```java
public class PriorityDemo {
    public static void main(String[] args) {
        var t0 = new Thread(PriorityDemo::execute);
        var t1 = new Thread(PriorityDemo::execute);
        // ... tạo t2, t3, t4, t5 tương tự ...

        t0.setPriority(1);  // MIN_PRIORITY
        t1.setPriority(10); // MAX_PRIORITY
        t2.setPriority(10);
        t3.setPriority(3);
        t4.setPriority(6);
        t5.setPriority(4);

        t0.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }

    public static void execute() {
        System.out.println(Thread.currentThread().getName());
    }
}
```
*   **Giải thích lý thuyết bên cạnh code:**
    *   **Priority (Độ ưu tiên):** Trong Java, độ ưu tiên của luồng nằm trong khoảng từ `1` (Thread.MIN_PRIORITY) đến `10` (Thread.MAX_PRIORITY). Mặc định là `5` (Thread.NORM_PRIORITY).
    *   **Hành vi:** Việc set priority chỉ là **lời khuyên (hint)** gửi đến Thread Scheduler của hệ điều hành. 
    *   **Lưu ý cực kỳ quan trọng:** **KHÔNG** có gì đảm bảo rằng luồng có priority 10 sẽ in ra trước luồng có priority 1. Việc lập lịch phụ thuộc rất nhiều vào Hệ điều hành (Windows, Linux, Mac có cách lập lịch khác nhau). Nếu bạn chạy code này nhiều lần, thứ tự in ra sẽ thay đổi ngẫu nhiên.

### 2. `YieldDemo.java` - Nhường quyền thực thi (yield)

```java
public class YieldDemo {
    public static void main(String[] args) {
        var t0 = new Thread(() -> {
            Thread.yield(); // Nhường CPU
            System.out.println(Thread.currentThread().getName());
        });

        var t1 = new Thread(() -> System.out.println(Thread.currentThread().getName()));

        t0.start();
        t1.start();
    }
}
```
*   **Giải thích lý thuyết bên cạnh code:**
    *   **`Thread.yield()`**: Khi `t0` gọi hàm này, nó đang báo với Thread Scheduler rằng: *"Tôi sẵn sàng nhường CPU hiện tại cho luồng khác chạy trước"*. 
    *   `t0` sẽ chuyển từ trạng thái **Running** (Đang chạy) sang **Runnable** (Sẵn sàng chờ).
    *   **Kết quả:** Trong hầu hết các trường hợp, `t1` sẽ có cơ hội in ra tên của nó trước `t0`. Tuy nhiên, `yield()` chỉ là một gợi ý, Thread Scheduler có thể bỏ qua và tiếp tục cho `t0` chạy nếu nó muốn.

### 3. `DaemonThreadDemo.java` - Luồng nền (Daemon Thread)

```java
public class DaemonThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        var t1 = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    System.out.println("Daemon is working");
                } catch (InterruptedException e) {}
            }
        });

//		t1.setDaemon(true); // Dòng này đang bị comment
        t1.start();

        TimeUnit.SECONDS.sleep(2);
        System.out.println("Main thread finished");
    }
}
```
*   **Giải thích lý thuyết bên cạnh code:**
    *   **User Thread vs Daemon Thread:** Trong Java có 2 loại luồng. Luồng thông thường (User Thread) như `main` hay `t1` sẽ **ngăn JVM thoát** chừng nào chúng còn chạy. Daemon Thread là luồng phục vụ ngầm (như Garbage Collector). JVM sẽ tự động tắt và hủy các Daemon Thread khi tất cả các User Thread đã kết thúc.
    *   **Phân tích code hiện tại:** Dòng `t1.setDaemon(true)` đang bị comment. Do đó, `t1` là một User Thread. Nó có một vòng lặp vô hạn (`while(true)`). Mặc dù `main` chạy xong sau 2 giây và in ra "Main thread finished", nhưng **chương trình sẽ KHÔNG BAO GIỜ DỪNG** vì luồng `t1` vẫn đang chạy.
    *   **Nếu bạn bỏ comment dòng đó:** `t1` trở thành Daemon Thread. Khi `main` kết thúc (sau 2s), JVM sẽ thấy không còn User Thread nào, nó sẽ ngay lập tức tắt và "giết" luồng `t1`. Chương trình sẽ dừng sạch sẽ.

### 4. `ThreadGroupDemo.java` - Nhóm luồng (Thread Group)

```java
public class ThreadGroupDemo {
    public static void main(String[] args) throws InterruptedException {
        ThreadGroup threadGorup = new ThreadGroup("group"); // Tạo nhóm

        var t0 = new Thread(threadGorup, ThreadGroupDemo::execute);
        // ... khởi tạo t1, t2, t3, t4 tương tự ...

        t0.start(); t1.start(); t2.start(); t3.start(); t4.start();

        TimeUnit.SECONDS.sleep(2);

        threadGorup.interrupt(); // Ngắt toàn bộ nhóm
    }

    public static void execute() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                System.out.println(Thread.currentThread().getName());
            } catch (InterruptedException e) {
                break; // Thoát vòng lặp khi bị ngắt
            }
        }
    }
}
```
*   **Giải thích lý thuyết bên cạnh code:**
    *   **ThreadGroup** cho phép quản lý nhiều luồng cùng lúc. Thay vì phải gọi `interrupt()` cho từng luồng `t0`, `t1`..., ta chỉ cần gọi `threadGorup.interrupt()`.
    *   Khi `interrupt()` được gọi, tất cả các luồng đang active trong nhóm sẽ nhận được tín hiệu `InterruptedException`.
    *   Trong hàm `execute()`, khối `catch` sẽ bắt lấy ngoại lệ này và thực hiện lệnh `break` để thoát khỏi vòng lặp `while(true)`. Kết quả là sau 2 giây, cả 5 luồng sẽ đồng loạt dừng lại.

### 5. `ThrowingExceptionFromRunnableDemo.java` - Ném ngoại lệ từ run()

```java
public class ThrowingExceptionFromRunnableDemo {
    public static void main(String[] args) {
        var t1 = new Thread(() -> {
            try {
                Files.readString(Paths.get("non-existing-path")); // Gây lỗi IOException
            } catch (IOException ioException) {
                throw new RuntimeException(ioException); // Ném RuntimeException
            }
        });

        t1.start();
    }
}
```
*   **Giải thích lý thuyết bên cạnh code:**
    *   Phương thức `run()` của Thread **không được phép** khai báo ném ra các Checked Exception (như `IOException`). Do đó, ta phải `try-catch` và bọc nó vào một `RuntimeException` (Unchecked Exception) rồi ném ra.
    *   **Hậu quả:** Khi `t1` ném ra `RuntimeException`, luồng `t1` sẽ bị chấm dứt (terminate) ngay lập tức. 
    *   Tuy nhiên, **luồng `main` sẽ không bị ảnh hưởng** và vẫn tiếp tục chạy (trong trường hợp này là kết thúc bình thường). Exception sẽ được in ra màn hình console (System.err) với stack trace.

---
**Tóm tắt lại:**
*   **Thread Scheduler** quyết định ai được chạy dựa trên Priority và thuật toán.
*   **Race Condition** là hiểm họa khi dùng đa luồng, cần đồng bộ hóa.
*   **`yield()`** là nhường CPU (chỉ là gợi ý).
*   **Daemon Thread** là luồng nền, sẽ chết theo JVM. User Thread thì giữ JVM sống.
*   **Thread Group** giúp quản lý (ngắt, liệt kê) nhiều luồng cùng lúc.
*   **Exception** trong luồng con sẽ giết luồng con, không làm sập luồng main.