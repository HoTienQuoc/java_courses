Dựa trên nội dung các slide trong khóa học "Java from Zero to First Job" và lý thuyết đa luồng (multithreading) trong Java, dưới đây là phần giải thích chi tiết, kết hợp phân tích slide, lý thuyết nền tảng và các ví dụ code minh họa.

---

### 📌 PHÂN TÍCH SLIDE 1: AGENDA (Nội dung khóa học)

Slide đầu tiên liệt kê các chủ đề sẽ học, đặt nền móng cho toàn bộ kiến thức về sau:

- **Thread lifecycle & states**: Vòng đời và các trạng thái của luồng.
- **5 ways to start a thread**: 5 cách khởi tạo luồng (thực chất là 2 cách chính: kế thừa `Thread` hoặc triển khai `Runnable`, cùng các biến thể).
- **Thread class & Runnable interface**: Hai thành phần cốt lõi để tạo luồng.
- **Main properties**: Các thuộc tính quan trọng của đối tượng Thread như `id`, `name`, `priority`, `daemon`.
- **TimeUnit**: Lớp tiện ích để quản lý thời gian chờ (thay vì dùng mili-giây thuần túy).
- **Interrupt thread**: Cách ngắt một luồng đang chạy.
- **Deprecated stop(), suspend(), resume()**: Các phương thức dừng/tạm dừng luồng đã lỗi thời và **không bao giờ nên dùng** (như phụ đề video nhấn mạnh: *"We'll discuss methods that are deprecated for a long time already and that you never should use"*).
- **interrupt() vs interrupted() vs isInterrupted()**: Phân biệt ba phương thức liên quan đến ngắt luồng.
- **currentThread()**: Lấy tham chiếu đến luồng đang thực thi.
- **sleep()**: Tạm dừng luồng một khoảng thời gian.
- **join()**: Chờ một luồng khác kết thúc.

---

### 📌 PHÂN TÍCH SLIDE 2: THREAD LIFECYCLE (Sơ đồ trạng thái)

Sơ đồ trong slide mô tả các trạng thái cơ bản của một luồng trong Java (theo mô hình đơn giản hóa của khóa học):

- **NEW**: Luồng vừa được tạo bằng từ khóa `new` nhưng chưa gọi `start()`.
- **RUNNING**: Luồng đang thực thi (bao gồm cả trạng thái sẵn sàng chờ CPU cấp phép và đang chạy thực sự).
- **NON-RUNNING (waiting / blocked)**: Luồng tạm thời không chạy do phải chờ tài nguyên, chờ sự kiện, hoặc đang `sleep()`.
- **TERMINATED**: Luồng đã kết thúc (phương thức `run()` return hoặc bị dừng).

**Lưu ý**: Trong Java hiện đại (từ JDK 1.5+), `Thread.State` enum định nghĩa chi tiết hơn 6 trạng thái: `NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, `TERMINATED`. Sơ đồ trong slide là phiên bản khái quát, gộp chung nhóm trạng thái chờ vào "NON-RUNNING".

---

### 💻 LÝ THUYẾT CHI TIẾT KÈM VÍ DỤ CODE

#### 1. Cách tạo và khởi chạy luồng (5 ways to start a thread)

Thực chất có 2 cách chính, nhưng được triển khai theo nhiều biến thể (anonymous class, lambda, kế thừa, v.v.):

**Cách 1: Kế thừa lớp `Thread` và ghi đè `run()`**

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread đang chạy: " + Thread.currentThread().getName());
    }
}

public class Main {
    public static void main(String[] args) {
        MyThread t1 = new MyThread();
        t1.start(); // Khởi chạy luồng
    }
}
```
- **Giải thích lý thuyết**: `Thread` class implements `Runnable`, nhưng `run()` mặc định rỗng. Khi kế thừa, ta cung cấp logic riêng. Phải gọi `start()` (không phải `run()`) để JVM tạo luồng mới và gọi `run()` bên trong luồng đó.

**Cách 2: Triển khai interface `Runnable` (khuyến nghị)**

```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable đang chạy: " + Thread.currentThread().getName());
    }
}

public class Main {
    public static void main(String[] args) {
        Thread t2 = new Thread(new MyRunnable());
        t2.start();
    }
}
```
- **Giải thích lý thuyết**: `Runnable` là interface chỉ có một phương thức `run()`. Cách này linh hoạt hơn vì lớp của bạn vẫn có thể kế thừa lớp khác. `Thread` constructor nhận một `Runnable target` và thực thi `run()` của target đó.

**Các biến thể khác (anonymous class, lambda):**
```java
// Anonymous class
new Thread(new Runnable() {
    public void run() { System.out.println("Anonymous"); }
}).start();

// Lambda (Java 8+)
new Thread(() -> System.out.println("Lambda")).start();
```

---

#### 2. Vòng đời chi tiết & các phương thức quan trọng

##### a) `start()` vs `run()`

| Phương thức | Hành vi |
|-------------|----------|
| `start()` | Tạo luồng mới ở cấp hệ điều hành, sau đó JVM gọi `run()` bên trong luồng mới. |
| `run()` | Chỉ là một phương thức thông thường, nếu gọi trực tiếp nó sẽ chạy trên luồng hiện tại (main thread), **không** tạo luồng mới. |

**Ví dụ:**
```java
public class StartVsRun {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("Chạy trong: " + Thread.currentThread().getName());
        });
        t.run();   // Chạy trên "main" -> không tạo luồng mới
        t.start(); // Chạy trên "Thread-0" -> tạo luồng mới
    }
}
```
**Giải thích lý thuyết**: Khi gọi `run()` trực tiếp, nó giống như gọi một phương thức bình thường, không có sự chuyển đổi ngữ cảnh luồng. Chỉ `start()` mới kích hoạt cơ chế tạo luồng của JVM.

##### b) `Thread.sleep(long millis)` — Tạm dừng luồng

```java
public class SleepDemo {
    public static void main(String[] args) {
        System.out.println("Bắt đầu ngủ...");
        try {
            Thread.sleep(2000); // Ngủ 2 giây
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thức dậy!");
    }
}
```
- **Giải thích lý thuyết**: `sleep()` là **static method**, tạm dừng **luồng hiện tại** (không phải luồng được gọi) trong khoảng thời gian chỉ định. Nó ném ra `InterruptedException` nếu bị ngắt bởi luồng khác.

##### c) `join()` — Chờ luồng khác kết thúc

```java
public class JoinDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("Worker: " + i);
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }
        });
        t.start();
        t.join(); // Main thread chờ t kết thúc
        System.out.println("Main kết thúc sau khi Worker xong.");
    }
}
```
- **Giải thích lý thuyết**: `t.join()` khiến luồng gọi (main) tạm dừng cho đến khi luồng `t` kết thúc (hoặc hết thời gian timeout nếu dùng overload). Đây là cách đơn giản để đồng bộ hóa thứ tự hoàn thành giữa các luồng.

##### d) `interrupt()` — Ngắt luồng

```java
public class InterruptDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("Đang làm việc...");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Bị ngắt khi đang ngủ!");
            }
        });
        worker.start();
        Thread.sleep(3000);
        worker.interrupt(); // Gửi tín hiệu ngắt
    }
}
```
- **Giải thích lý thuyết**: `interrupt()` không dừng luồng ngay lập tức mà đặt cờ "interrupted" và đánh thức luồng nếu nó đang ở trạng thái chờ (sleep, wait, join). Luồng cần tự kiểm tra cờ này (qua `isInterrupted()`) để thoát một cách an toàn.

##### e) `currentThread()`

```java
public class CurrentThreadDemo {
    public static void main(String[] args) {
        Thread mainThread = Thread.currentThread();
        System.out.println("Luồng hiện tại: " + mainThread.getName()); // "main"
    }
}
```
- **Giải thích lý thuyết**: Trả về đối tượng `Thread` đại diện cho luồng đang thực thi mã lệnh tại thời điểm gọi.

##### f) Các thuộc tính chính của Thread

| Thuộc tính | Phương thức | Ý nghĩa |
|------------|-------------|---------|
| `id` | `getId()` | ID duy nhất của luồng |
| `name` | `getName()`, `setName()` | Tên luồng (mặc định Thread-0, Thread-1...) |
| `priority` | `getPriority()`, `setPriority()` | Độ ưu tiên (1-10, mặc định 5) |
| `daemon` | `isDaemon()`, `setDaemon()` | Luồng daemon tự động kết thúc khi tất cả luồng non-daemon kết thúc |

**Ví dụ:**
```java
Thread t = new Thread(() -> {});
t.setName("MyWorker");
t.setPriority(Thread.MAX_PRIORITY);
t.setDaemon(true);
System.out.println("Name: " + t.getName() + ", Priority: " + t.getPriority() + ", Daemon: " + t.isDaemon());
t.start();
```

---

#### 3. Các phương thức đã bị "Deprecated" — TUYỆT ĐỐI KHÔNG DÙNG

Slide đề cập `stop()`, `suspend()`, `resume()` nhưng phụ đề video nói rõ: *"you never should use"*.

- **`stop()`**: Dừng luồng ngay lập tức bằng cách ném `ThreadDeath`. Điều này **cực kỳ nguy hiểm** vì luồng có thể đang giữ khóa (lock) trên tài nguyên quan trọng, khi bị dừng đột ngột, lock không được giải phóng đúng cách, khiến tài nguyên rơi vào trạng thái không nhất quán và các luồng khác bị ảnh hưởng.
- **`suspend()` / `resume()`**: Tạm dừng/tiếp tục luồng. Nếu luồng bị `suspend()` đang giữ lock, không ai có thể truy cập tài nguyên đó cho đến khi được `resume()`. Nếu luồng muốn `resume()` lại cần lock đó → **deadlock** (khóa chết).

**Thay thế an toàn**: Sử dụng một biến cờ (flag) `volatile boolean running = true;` và kiểm tra nó trong vòng lặp của `run()` để dừng một cách "êm áp". Kết hợp với `interrupt()` nếu luồng đang chờ đợi.

---

#### 4. Phân biệt `interrupt()`, `interrupted()`, `isInterrupted()`

| Phương thức | Loại | Chức năng |
|-------------|------|-----------|
| `interrupt()` | instance method | Gửi tín hiệu ngắt đến luồng đích |
| `isInterrupted()` | instance method | Kiểm tra cờ ngắt của luồng **mà không xóa cờ** |
| `interrupted()` | **static method** | Kiểm tra cờ ngắt của **luồng hiện tại** và **xóa cờ** (reset về false) |

**Ví dụ:**
```java
public class InterruptCheck {
    public static void main(String[] args) {
        Thread.currentThread().interrupt(); // Đặt cờ ngắt cho main thread
        System.out.println("isInterrupted: " + Thread.currentThread().isInterrupted()); // true
        System.out.println("interrupted(): " + Thread.interrupted()); // true, và xóa cờ
        System.out.println("Sau khi xóa: " + Thread.currentThread().isInterrupted()); // false
    }
}
```
- **Giải thích lý thuyết**: `interrupted()` là static, luôn tác động lên luồng hiện tại và có tác dụng phụ là **xóa cờ**. Điều này quan trọng khi bạn muốn "tiêu thụ" tín hiệu ngắt một lần.

---

#### 5. `TimeUnit` — Quản lý thời gian tiện lợi hơn

Thay vì `Thread.sleep(5000)` (5 giây), có thể dùng `TimeUnit.SECONDS.sleep(5)`. `TimeUnit` là enum trong `java.util.concurrent`, cung cấp các đơn vị thời gian rõ ràng và các phương thức chuyển đổi.

```java
import java.util.concurrent.TimeUnit;

public class TimeUnitDemo {
    public static void main(String[] args) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);   // Ngủ 1 giây
        TimeUnit.MILLISECONDS.sleep(500); // Ngủ 500ms
        System.out.println("Xong");
    }
}
```
- **Giải thích lý thuyết**: `TimeUnit` giúp code dễ đọc hơn và cung cấp các phương thức như `convert()`, `toSeconds()`, v.v.

---

### ⚠️ LƯU Ý VỀ TRẠNG THÁI "NON-RUNNING" TRONG SLIDE

Sơ đồ slide gộp chung "waiting / blocked". Trong thực tế Java hiện đại, cần phân biệt:
- **BLOCKED**: Chờ monitor lock (đang chờ để vào synchronized block).
- **WAITING**: Chờ vô thời hạn (do `wait()`, `join()` không timeout).
- **TIMED_WAITING**: Chờ có thời hạn (do `sleep()`, `wait(timeout)`, `join(timeout)`).

Sự khác biệt này quan trọng khi debug hoặc phân tích deadlock.

---

### 🎯 TÓM TẮT

| Chủ đề | Điểm cốt lõi |
|--------|---------------|
| Tạo luồng | Kế thừa `Thread` hoặc implement `Runnable`; luôn gọi `start()` |
| Vòng đời | NEW → RUNNABLE/RUNNING → (BLOCKED/WAITING/TIMED_WAITING) → TERMINATED |
| `sleep()` | Dừng luồng hiện tại, ném `InterruptedException` |
| `join()` | Chờ luồng khác kết thúc |
| `interrupt()` | Gửi tín hiệu ngắt, không dừng ngay lập tức |
| Deprecated | `stop()`, `suspend()`, `resume()` — không dùng vì nguy cơ deadlock và mất nhất quán dữ liệu |
| `currentThread()` | Lấy tham chiếu luồng đang chạy |
| `TimeUnit` | Thay thế `Thread.sleep` với đơn vị thời gian rõ ràng |

Hy vọng phần giải thích chi tiết này giúp bạn nắm vững toàn bộ nội dung từ hai slide trong khóa học!