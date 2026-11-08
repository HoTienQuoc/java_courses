Chào bạn, đây là một bộ slide rất tuyệt vời về **Virtual Threads (Luồng ảo)** trong Java (Dự án Loom, JEP 444). 

Dưới đây, tôi sẽ giải thích chi tiết từng hình ảnh, đi kèm lý thuyết dễ hiểu, ví dụ minh họa và code chi tiết cho từng phần.

---

### Hình 1: Agenda (Chương trình nghị sự)
*   **Giải thích hình ảnh:** Đây là slide mở đầu tóm tắt nội dung buổi học: Từ giới thiệu Virtual Threads, điểm lại luồng truyền thống, động lực ra đời, cách hoạt động (carrier threads, continuations), so sánh, lập trình đồng thời có cấu trúc (Structured Concurrency), đến các vấn đề về I/O, pinning và công cụ chẩn đoán.
*   **Lý thuyết:** Buổi học đi từ cái cũ (Platform Threads) đến cái mới (Virtual Threads), chỉ ra vấn đề và cách Java giải quyết chúng.

---

### Hình 2: Traditional Threads in Java (Luồng truyền thống trong Java)
*   **Giải thích hình ảnh:** Hình vẽ minh họa mỗi `Platform Thread` của Java (JVM) được ánh xạ 1:1 với một `Thread` của Hệ điều hành (OS Kernel).
*   **Lý thuyết dễ hiểu:** 
    *   **Platform Thread** giống như một chiếc xe tải hạng nặng. Nó rất chắc chắn nhưng cồng kềnh.
    *   Mỗi luồng tốn khoảng **~1MB bộ nhớ (stack)**. 
    *   Việc chuyển đổi giữa các luồng (Context Switching) do Hệ điều hành quản lý rất **tốn kém**.
    *   Hệ quả: Bạn chỉ có thể tạo được vài nghìn luồng cùng lúc. Nếu làm web server chịu tải cao, đây là **nút thắt cổ chai**.
*   **Ví dụ & Code:**
    ```java
    // Tạo một Platform Thread truyền thống
    public class TraditionalThreadExample {
        public static void main(String[] args) {
            Thread platformThread = new Thread(() -> {
                System.out.println("Đang chạy trên Platform Thread: " + Thread.currentThread());
            });
            platformThread.start();
        }
    }
    ```
*   **Giải thích code:** Đoạn code tạo một luồng thông thường. Khi gọi `.start()`, JVM sẽ yêu cầu Hệ điều hành cấp phát một luồng thật. Nếu bạn chạy vòng lặp tạo 10.000 luồng này, chương trình sẽ sớm báo lỗi `OutOfMemoryError` do cạn kiệt bộ nhớ.

---

### Hình 3: Why Virtual Threads? (Tại sao cần Virtual Threads?)
*   **Giải thích hình ảnh:** Hình vẽ minh họa nhiều `Virtual Threads` (màu xanh nhạt) được xếp chồng lên một số ít `Platform Threads` (Carrier Threads). 
*   **Lý thuyết dễ hiểu:** 
    *   Trước đây, để giải quyết bài toán chịu tải, người ta dùng **Async/Reactive** (như Reactor, RxJava). Nhưng code kiểu này rất khó viết, khó đọc và khó debug (giống như giải đố).
    *   **Virtual Threads** giống như xe máy: Siêu nhẹ, tạo ra cực nhanh, hàng triệu chiếc cùng lúc.
    *   Nó giữ nguyên mô hình lập trình quen thuộc (code tuần tự, blocking), nhưng hiệu năng lại đạt được như mô hình bất đồng bộ.
*   **Ví dụ & Code (So sánh tạo 100.000 task):**
    ```java
    import java.util.concurrent.Executors;
    
    public class ThreadComparison {
        public static void main(String[] args) {
            // Cách cũ: Dùng Thread Pool truyền thống (Platform Threads)
            // Thử đổi 100 thành 100,000 bạn sẽ thấy lỗi
            try (var executor = Executors.newFixedThreadPool(100)) { 
                for (int i = 0; i < 100; i++) {
                    executor.submit(() -> {
                        try { Thread.sleep(1000); } catch (Exception e) {}
                        return "Xong";
                    });
                }
            }
            
            // Cách mới: Dùng Virtual Threads (Java 21+)
            // Tạo 100,000 luồng vẫn chạy vèo vèo
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < 100_000; i++) {
                    executor.submit(() -> {
                        try { Thread.sleep(1000); } catch (Exception e) {}
                        return "Xong";
                    });
                }
            } // Tự động đóng executor
        }
    }
    ```
*   **Giải thích code:** Với `newFixedThreadPool`, nếu bạn đẩy 100.000 task vào, hệ thống sẽ nghẽn vì chỉ có 100 luồng OS. Với `newVirtualThreadPerTaskExecutor`, JVM sẽ tạo 100.000 Virtual Threads trên heap (bộ nhớ trong), cực kỳ nhẹ nhàng.

---

### Hình 4: How Virtual Threads Work (Cách hoạt động)
*   **Giải thích hình ảnh:** Minh họa JVM Scheduler quản lý các Virtual Threads. Chúng được "gắn" (mounted) lên các Carrier Threads (Platform Threads) để chạy. Khi gặp tác vụ chờ (I/O, sleep), nó bị "tháo" (unmounted/parked) ra khỏi Carrier Thread để nhường chỗ cho VT khác.
*   **Lý thuyết dễ hiểu:** Hãy tưởng tượng **Carrier Thread** là những người thợ. **Virtual Thread** là các công việc. Khi người thợ đang làm việc A, nhưng việc A phải chờ máy móc (I/O), người thợ không ngồi không, mà JVM sẽ đưa việc A sang một bên (Parking), và đưa việc B cho người thợ làm. Khi việc A xong, nó được xếp hàng để người thợ nào rảnh làm tiếp.
*   **Ví dụ & Code:**
    ```java
    public class HowItWorks {
        public static void main(String[] args) throws InterruptedException {
            Thread vt = Thread.ofVirtual().start(() -> {
                System.out.println("VT bắt đầu trên Carrier: " + Thread.currentThread());
                try {
                    // JVM sẽ "park" VT này, nhả Carrier thread ra
                    Thread.sleep(2000); 
                } catch (InterruptedException e) {}
                System.out.println("VT thức dậy, có thể trên Carrier khác: " + Thread.currentThread());
            });
            vt.join();
        }
    }
    ```
*   **Giải thích code:** Lệnh `Thread.sleep(2000)` không làm treo OS Thread (Carrier). JVM can thiệp, tạm dừng VT, đưa nó vào bộ nhớ. Sau 2 giây, JVM đánh thức VT và đưa nó lên một Carrier Thread (có thể là cùng hoặc khác Carrier cũ) để chạy tiếp.

---

### Hình 5: Continuations (Sự tiếp diễn)
*   **Giải thích hình ảnh:** Slide nói về "bí mật" đằng sau VT là Continuations (nguyên thủy điều khiển luồng cấp thấp). Stack của VT được cấp phát trên Heap thay vì bộ nhớ Native.
*   **Lý thuyết dễ hiểu:** Giống như tính năng **"Save Game"** trong trò chơi điện tử. Khi bạn gặp I/O (tương đương việc tạm dừng chơi), JVM lưu toàn bộ trạng thái hiện tại (call stack) vào bộ nhớ Heap. Khi I/O xong, JVM "Load Game" và chạy tiếp. Vì lưu trên Heap (RAM) nên nó nhẹ và nhanh hơn nhiều so với việc OS phải quản lý luồng.
*   **Code:** Bạn không thể trực tiếp code Continuation bằng API công khai của Java (nó là internal). Nhưng hãy hiểu nó là cơ chế ngầm giúp VT có thể "dừng và tiếp tục" ở bất kỳ đâu.

---

### Hình 6: Virtual Threads vs Platform Threads
*   **Giải thích hình ảnh:** Bảng so sánh. Platform Thread đắt đỏ, VT cực nhẹ. VT có thể scale lên hàng triệu.
*   **Lý thuyết dễ hiểu:** Mô hình **"Thread-per-task"** (Mỗi tác vụ một luồng) quay trở lại. Trước đây, vì tạo luồng quá đắt, người ta phải dùng Thread Pool (hồ chứa luồng, tái sử dụng). Giờ đây, với VT, bạn cứ tạo một luồng mới cho mỗi request mà không cần lo lắng về tài nguyên. Không cần Thread Pool phức tạp nữa.
*   *(Code đã minh họa ở Hình 3)*.

---

### Hình 7: Structured Concurrency (Lập trình đồng thời có cấu trúc)
*   **Giải thích hình ảnh:** So sánh luồng truyền thống (không có cấu trúc, khó quản lý vòng đời) với Structured Concurrency.
*   **Lý thuyết dễ hiểu:** Giống như quản lý nhân viên. Nếu Sếp (Task cha) giao việc cho 3 nhân viên (Task con). Nếu Sếp bị đuổi việc (bị hủy/hủy bỏ), thì 3 nhân viên cũng phải dừng làm việc ngay lập tức. Tránh tình trạng "luồng mồ côi" chạy ngầm gây rò rỉ bộ nhớ. Nó giúp code dễ đọc, dễ debug lỗi.
*   **Ví dụ & Code (Java 21+):**
    ```java
    import java.util.concurrent.*;
    
    public class StructuredConcurrencyExample {
        public static void main(String[] args) throws Exception {
            // Tạo một phạm vi tác vụ (Scope)
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                // Fork (Giao việc) cho 2 task con
                Future<String> userTask = scope.fork(() -> {
                    Thread.sleep(1000);
                    return "User: Alice";
                });
                
                Future<Integer> orderTask = scope.fork(() -> {
                    Thread.sleep(1500);
                    return 42;
                });
                
                scope.join();           // Chờ tất cả task con hoàn thành
                scope.throwIfFailed();  // Nếu 1 task lỗi, ném ngoại lệ và hủy các task còn lại
                
                System.out.println(userTask.resultNow() + " - " + orderTask.resultNow());
            } // Kết thúc try, tất cả task con đều được đảm bảo dọn dẹp
        }
    }
    ```
*   **Giải thích code:** Khối `StructuredTaskScope` đảm bảo tính toàn vẹn. Nếu `userTask` lỗi, `orderTask` sẽ tự động bị hủy (cancel). Không có luồng nào bị rò rỉ ra ngoài.

---

### Hình 8: Blocking I/O and the Illusion of Scalability (I/O nghẽn và ảo ảnh mở rộng)
*   **Giải thích hình ảnh:** Nói về cách JVM can thiệp vào các lệnh chặn (blocking calls) như I/O, sleep, accept.
*   **Lý thuyết dễ hiểu:** Đây là "phép thuật" của VT. Bạn viết code chặn (blocking) một cách bình thường (ví dụ: `readFile()`). Nhưng JVM sẽ ngầm hiểu: "À, thằng này đang chờ đọc file, mình sẽ tạm cất nó đi (park), nhường Carrier Thread cho thằng khác chạy". Kết quả là code bạn viết rất đơn giản (không cần callback, reactive), nhưng hệ thống vẫn scale (mở rộng) tốt như code bất đồng bộ.

---

### Hình 9: Pinning and Unsafe Blocking Calls (Ghim luồng và các lệnh chặn không an toàn)
*   **Giải thích hình ảnh:** Cảnh báo về các trường hợp VT bị "ghim" (pinned) vào Carrier Thread. 
*   **Lý thuyết dễ hiểu:** Có những lệnh gọi hệ thống cũ (Native Blocking như JNI, `FileChannel.map`, JDBC cũ) mà JVM **không thể can thiệp** để tạm dừng VT. Lúc này, VT bị "dính chặt" (pinned) vào Carrier Thread. Carrier Thread bị chặn (blocked) hoàn toàn, không thể chạy task khác. Hậu quả: Mất đi khả năng scale của VT.
*   **Ví dụ & Code:** *(Lưu ý: Từ Java 24 (JEP 491), việc dùng `synchronized` không còn gây pinning nữa, nhưng các lệnh JNI vẫn thế).*
    ```java
    public class PinningExample {
        public static void main(String[] args) {
            Thread.ofVirtual().start(() -> {
                // Trong Java 21, khối synchronized bên trong Virtual Thread gây ra PINNING
                // Tức là Carrier Thread bị khóa, không làm việc khác được.
                synchronized (PinningExample.class) {
                    try {
                        Thread.sleep(1000); // Bị chặn cứng ở đây
                    } catch (InterruptedException e) {}
                }
            });
        }
    }
    ```
*   **Giải thích code:** Giả sử có 1 Carrier Thread. Nếu VT dùng `synchronized` và bị block, Carrier Thread đó bị treo. 100 VT khác xếp hàng chờ Carrier Thread này sẽ bị đói (starvation). Khuyến nghị: Dùng các HTTP Client hiện đại, Driver DB không đồng bộ (như R2DBC), hoặc thay `synchronized` bằng `ReentrantLock` (trước Java 24).

---

### Hình 10 & 11: Detecting Pinned Virtual Threads & Using JCMD (Phát hiện VT bị ghim)
*   **Giải thích hình ảnh:** Giới thiệu các công cụ chẩn đoán như `jcmd` và `JFR` để tìm ra các VT bị pin. Hình 11 đưa ra lệnh: `jcmd <PID> Thread.pin`.
*   **Lý thuyết dễ hiểu:** Pinning có thể làm chậm hệ thống mà không báo lỗi. Bạn cần công cụ để "bắt bệnh". Bạn chạy lệnh `jcmd` (Java Command) trên terminal, trỏ tới Process ID (PID) của ứng dụng Java để xem stack trace và phát hiện luồng nào đang bị kẹt.
*   **Ví dụ & Code (Lệnh Terminal):**
    ```bash
    # Bật quyền truy cập native
    java --enable-native-access=ALL-UNNAMED -jar myapp.jar
    
    # Lấy PID của tiến trình Java
    jps -l 
    # (Giả sử PID là 12345)
    
    # Chạy lệnh chẩn đoán (Lưu ý: Lệnh chính xác trong JDK hiện đại thường là dùng JFR hoặc Thread.dump_to_file)
    jcmd 12345 Thread.dump_to_file -format=json threads.json
    ```
*   **Giải thích:** File `threads.json` sẽ chứa thông tin chi tiết. Bạn tìm các Virtual Thread có trạng thái `BLOCKED` và xem nó đang gọi hàm native/JNI nào để sửa code (thay thư viện).

---

### Hình 12: ThreadLocal in the Age of Virtual Threads (ThreadLocal trong thời đại VT)
*   **Giải thích hình ảnh:** Nói về sự thay đổi của `ThreadLocal`. VT có vòng đời ngắn, không tái sử dụng. VT có thể park/unpark và chuyển sang Carrier khác.
*   **Lý thuyết dễ hiểu:** `ThreadLocal` giống như một tờ giấy nhớ dán lên một nhân viên. Nhưng với VT, nhân viên (VT) làm xong việc rồi biến mất. Tờ giấy nhớ (dữ liệu) có thể bị thất lạc hoặc dính sang nhân viên khác (Carrier Thread khác) gây ra **rò rỉ bộ nhớ (memory leak)** và **lỗi dữ liệu (subtle bugs)**. Không nên dùng `ThreadLocal` để lưu Context (User ID, Transaction) trong mô hình VT.
*   **Ví dụ & Code (Cách cũ, dễ gây lỗi):**
    ```java
    public class ThreadLocalProblem {
        // Lưu User ID vào ThreadLocal
        private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

        public static void main(String[] args) throws Exception {
            for (int i = 0; i < 1000; i++) {
                int userId = i;
                Thread.ofVirtual().start(() -> {
                    currentUser.set("User_" + userId);
                    try { Thread.sleep(10); } catch (Exception e) {}
                    // Rất có thể currentUser.get() lúc này không còn là User_ ban đầu 
                    // do VT bị chuyển sang Carrier khác.
                    System.out.println(currentUser.get()); 
                    currentUser.remove(); // Bắt buộc phải remove để tránh leak
                });
            }
            Thread.sleep(2000);
        }
    }
    ```
*   **Giải thích code:** Mặc dù `ThreadLocal` vẫn hoạt động với VT trong một số ngữ cảnh (vì nó đi kèm với đối tượng Thread), nhưng nó phá vỡ các giả định cũ và gây khó khăn khi lập trình. Đặc biệt, nếu quên `remove()`, bộ nhớ sẽ bị phình to.

---

### Hình 13: Scoped Values (JEP 429 - Giá trị theo phạm vi)
*   **Giải thích hình ảnh:** Giới thiệu `ScopedValue` như một giải pháp thay thế hiện đại và an toàn cho `ThreadLocal`.
*   **Lý thuyết dễ hiểu:** Thay vì dán giấy nhớ lên người nhân viên (`ThreadLocal`), bạn đưa cho nhân viên một chiếc cặp tài liệu khi giao việc, và thu lại khi xong việc. Dữ liệu gắn liền với **luồng thực thi (execution flow)**, không gắn với Thread. Nó an toàn tuyệt đối với VT, không lo rò rỉ, và giới hạn trong một khối code (lexical scope).
*   **Ví dụ & Code (Java 21+):**
    ```java
    import java.util.concurrent.*;
    import static java.lang.ScopedValue.newInstance;

    public class ScopedValueExample {
        // Khai báo một ScopedValue
        public static final ScopedValue<String> USER_ID = newInstance();

        public static void main(String[] args) throws Exception {
            // Gán giá trị và chạy trong một phạm vi (scope)
            ScopedValue.where(USER_ID, "Alice").run(() -> {
                // Bên trong scope này, bất kỳ luồng nào (kể cả VT con) đều thấy USER_ID
                Thread.ofVirtual().start(() -> {
                    System.out.println("User trong VT: " + USER_ID.get()); // In ra: Alice
                });
            });
            
            // Ra khỏi scope, USER_ID sẽ không còn giá trị (hoặc không thể truy cập)
            // USER_ID.get(); // Sẽ báo lỗi NoSuchElementException
        }
    }
    ```
*   **Giải thích code:** `ScopedValue.where(...).run(...)` tạo ra một vùng an toàn. Biến `USER_ID` chỉ tồn tại bên trong khối `run`. Dù các Virtual Threads có chạy lung tung, chúng vẫn mang theo giá trị `"Alice"` một cách an toàn. Khi khối code kết thúc, bộ nhớ được giải phóng hoàn toàn. Đây là tương lai của việc quản lý Context trong Java.

---
**Tổng kết:** Bộ slide này mô tả một bước ngoặt lớn của Java (từ Java 21). Bạn chuyển từ tư duy "tối ưu hóa tài nguyên OS" sang tư duy "viết code tuần tự đơn giản, JVM lo phần hiệu năng". Hãy nhớ tránh các thư viện dùng Native Blocking (gây pinning) và thay thế `ThreadLocal` bằng `ScopedValue`.


Chào bạn, đây là một file code cực kỳ thực tế (`VirtualThreadDemo.java`) minh họa trực tiếp những lý thuyết từ các slide bạn vừa gửi. 

Dưới đây là phần giải thích chi tiết từng đoạn code, kết hợp chặt chẽ với lý thuyết để bạn dễ hiểu nhất.

---

### Tổng quan về file code
File code này được chia làm 3 phần chính (và 1 phần chú thích):
1. Tạo Virtual Thread bằng cách đơn giản nhất.
2. Sử dụng API chuẩn (Best Practice) cho Virtual Thread.
3. **Bài test hiệu năng (Benchmark)**: So sánh thời gian chạy giữa 100 Virtual Threads và 100 Platform Threads (dùng Thread Pool).

---

### Phần 1: Cách tạo Virtual Thread cơ bản nhất

```java
// --- 1. Basic Hello World using Thread.startVirtualThread() ---
Thread vThread = Thread.startVirtualThread(() -> {
    System.out.println("Hello from a virtual thread (Thread.startVirtualThread)");
});

// Wait for the virtual thread to finish
vThread.join();
```

*   **Giải thích Code:**
    *   `Thread.startVirtualThread(Runnable)`: Đây là một static method mới trong Java 21. Nó tạo và khởi chạy một Virtual Thread ngay lập tức. Bên trong nó tương đương với việc gọi `Thread.ofVirtual().start(Runnable)`.
    *   `vThread.join()`: Hàm `main` (Platform Thread) sẽ đứng chờ cho đến khi `vThread` chạy xong. Nếu không có `join()`, hàm `main` có thể kết thúc và chương trình tắt trước khi Virtual Thread kịp in ra màn hình.
*   **Lý thuyết liên kết (Slide 3 & 4):** 
    *   Khác với Platform Thread tốn ~1MB bộ nhớ, Virtual Thread này được cấp phát cực nhẹ trên Heap.
    *   Khi chạy, nó sẽ được JVM gắn (mount) vào một `Carrier Thread` (là một Platform Thread ngầm định) để thực thi.

---

### Phần 2: Cách tạo chuẩn mực (Preferred API)

```java
// --- 2. Preferred API: Executors.newVirtualThreadPerTaskExecutor() ---
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Submit a simple task
    Future<String> result = executor.submit(() -> {
        System.out.println("Hello from a virtual thread in ExecutorService!");
        return "Task completed";
    });

    // Wait and print result
    System.out.println("Result: " + result.get());
} catch (ExecutionException e) {
    e.printStackTrace();
}
```

*   **Giải thích Code:**
    *   `Executors.newVirtualThreadPerTaskExecutor()`: Đây là **cách được khuyên dùng** (Best Practice) để làm việc với Virtual Threads. Cứ mỗi lần bạn `submit()` một task (nhiệm vụ), nó sẽ tự động tạo ra một Virtual Thread mới tinh cho task đó.
    *   Khối `try (...)` (Try-with-resources): Khi thoát khỏi khối này, `ExecutorService` sẽ tự động gọi hàm `close()`, nó sẽ đợi tất cả các task con chạy xong và dọn dẹp tài nguyên (tương tự như Structured Concurrency ở Slide 7).
    *   `result.get()`: Lấy kết quả trả về của task.
*   **Lý thuyết liên kết (Slide 6):** 
    *   Bạn không cần phải lo lắng về việc "tái sử dụng luồng" (Thread Pool) như trước đây nữa. Mô hình **"Thread-per-task"** (Mỗi task một luồng) đã quay trở lại vì Virtual Thread quá rẻ để tạo ra.

---

### Phần 3: So sánh hiệu năng (Đoạn code quan trọng nhất)

Đoạn code này thực hiện 100 tasks. Mỗi task sẽ mô phỏng một tác vụ I/O bằng cách gọi `Thread.sleep(100)` (ngủ 100 mili-giây).

#### 3.1. Chạy bằng Virtual Threads
```java
int numberOfTasks = 100;
// ...
long startTime = System.nanoTime();

try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    List<Future<?>> futures = IntStream.range(0, numberOfTasks)
            .<Future<?>>mapToObj(i -> executor.submit(() -> {
                System.out.println("Task #" + i + " is running on " + Thread.currentThread());
                try {
                    Thread.sleep(100); // Simulate work (Mô phỏng I/O)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            })).toList();
    // ... chờ future.get()
}
double virtualThreadsTime = (System.nanoTime() - startTime) / 1_000_000.0;
```
*   **Lý thuyết (Slide 8):** Khi 100 Virtual Threads này chạy đến lệnh `Thread.sleep(100)`, JVM sẽ can thiệp. Nó **park** (tạm dừng) 100 Virtual Thread này, lưu trạng thái (call stack) vào Heap, và **nhả các Carrier Thread ra**. Nhờ vậy, dù có 100 tasks, chúng gần như chạy song song hoàn toàn. Tổng thời gian thực tế sẽ rơi vào khoảng **~100ms đến 150ms**.

#### 3.2. Chạy bằng Platform Threads (Thread Pool truyền thống)
```java
startTime = System.nanoTime();
int cpus = Runtime.getRuntime().availableProcessors(); // Lấy số core CPU, ví dụ: 8

try (ExecutorService executor = Executors.newFixedThreadPool(cpus)) {
    List<Future<?>> futures = IntStream.range(0, numberOfTasks)
            .<Future<?>>mapToObj(i -> executor.submit(() -> {
                System.out.println("Task #" + i + " is running on " + Thread.currentThread());
                try {
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            })).toList();
    // ... chờ future.get()
}
double platformThreadsTime = (System.nanoTime() - startTime) / 1_000_000.0;
```
*   **Lý thuyết (Slide 2):** Ở đây tạo một Pool có số luồng bằng số CPU (ví dụ máy bạn có 8 core -> 8 luồng).
    *   Có 100 tasks. 8 luồng sẽ nhận 8 task đầu tiên.
    *   Khi gọi `Thread.sleep(100)`, **cả 8 Platform Threads này bị Hệ điều hành (OS) block hoàn toàn**. Chúng không thể làm việc khác.
    *   Khi 8 tasks này chạy xong (sau 100ms), 8 luồng mới được giải phóng để nhận 8 tasks tiếp theo.
    *   Vòng lặp cứ thế diễn ra: 100 tasks / 8 luồng = 12.5 lượt. Mỗi lượt tốn 100ms. **Tổng thời gian sẽ rơi vào khoảng ~1250ms**.

#### 3.3. In ra kết quả
```java
System.out.println("Virtual Threads Time: " + virtualThreadsTime);
System.out.println("Platform Threads Time: " + platformThreadsTime);
```
*   **Kết quả bạn sẽ thấy khi chạy:** `Virtual Threads Time` sẽ **nhỏ hơn rất nhiều** so với `Platform Threads Time` (khoảng 100ms so với 1200ms+). 
*   **Lý thuyết (Slide 3):** Đây chính là lý do Virtual Threads ra đời. Code bạn viết hoàn toàn giống hệt nhau, vẫn là code tuần tự (blocking code) dễ đọc, nhưng Virtual Threads cho phép mở rộng (scale) lên hàng chục nghìn tasks mà không bị nghẽn như Platform Threads.

---

### 💡 Một số quan sát thực tế khi bạn chạy code này

Nếu bạn nhìn kỹ vào dòng `System.out.println` trong code:
`"Task #" + i + " is running on " + Thread.currentThread()`

*   **Với Virtual Threads:** Bạn sẽ thấy tên luồng in ra có dạng:
    `VirtualThread[#24]/runnable@ForkJoinPool-1-worker-1`
    *(Lý thuyết Slide 4: Chữ `VirtualThread` là luồng ảo, còn `ForkJoinPool-1-worker-1` chính là Carrier Thread đang cõng nó).*
*   **Với Platform Threads:** Bạn sẽ thấy tên luồng in ra có dạng:
    `Thread[#25,pool-2-thread-1,5,main]`
    *(Đây là luồng OS thật, nằm trong Pool tên là pool-2).*

### ⚠️ Lưu ý từ Slide 9 & 10 (Tránh bị "Pinning")
Đoạn code trên dùng `Thread.sleep()` nên Virtual Threads hoạt động hoàn hảo. **Nhưng**, nếu thay `Thread.sleep(100)` bằng một tác vụ Native Blocking như đọc file cũ bằng `FileInputStream` hoặc dùng JDBC cũ, Virtual Thread sẽ bị **Pinned** (Ghim) vào Carrier Thread, và hiệu năng sẽ tụt giảm giống hệt như Platform Thread. 

*(Lưu ý thêm: Nếu bạn chạy thử đoạn code này với 10,000 tasks thay vì 100 tasks, bạn sẽ thấy Virtual Threads vẫn chạy vèo vèo trong ~100ms, còn Platform Threads sẽ mất hơn 2 phút!)*