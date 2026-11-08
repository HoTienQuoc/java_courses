Dưới đây là giải thích chi tiết cho 6 hình ảnh bài giảng của bạn về **Java Executor Service & Thread Pools**, kèm theo lý thuyết, ví dụ minh họa và mã nguồn chi tiết.

---

### 🖼️ 1. Giải thích chi tiết từng hình ảnh

*   **Hình 1 (Agenda - Chương trình học):** Đây là slide tổng quan về các chủ đề sẽ học: `ExecutorService` cơ bản, `Fixed Thread Pool Executor`, `ThreadPoolExecutor`, `Thread Starvation` (Đói luồng), cách tính số lượng thread tối ưu, `Blocking coefficient` (Hệ số chặn), `ThreadFactory`, `Cached Thread Pool`, `Single Thread Pool Executor` và `Scheduled Thread Pool Executor`.

*   **Hình 2 (Công thức tính số lượng thread):** Slide giới thiệu công thức kinh điển của Brian Goetz (trong cuốn *Java Concurrency in Practice*):
    `Number of threads = Number of Available Cores * (1 + Wait time / Service time)`.
    *   **Number of Available Cores:** Số lõi CPU khả dụng (có thể lấy qua `Runtime.getRuntime().availableProcessors()`).
    *   **Wait time (W):** Thời gian một tác vụ phải chờ đợi (ví dụ: chờ I/O, chờ mạng).
    *   **Service time (S):** Thời gian CPU thực sự xử lý tính toán cho tác vụ đó.

*   **Hình 3 (Hệ số chặn & Ví dụ 1):** Slide làm rõ khái niệm **Blocking coefficient** chính là tỷ lệ `Wait time / Service time`. Sau đó đưa ra ví dụ cho **tác vụ tính toán thuần túy (Computation-intensive)**: Vì không có thời gian chờ (W=0), công thức trở thành `Số thread = Số core * (1 + 0)`. Với 4 core, số thread tối ưu là **4**.

*   **Hình 4 (Ví dụ 2 - Có thời gian chờ):** Slide đưa ra ví dụ cho **tác vụ có thời gian chờ (I/O-intensive)**:
    *   `Wait time = 200ms`, `Service time = 20ms`.
    *   Hệ số chặn = `200/20 = 10`.
    *   Số thread = `4 * (1 + 10) = 44`. Con số này lớn hơn nhiều so với số core vì phần lớn thời gian thread chỉ ngồi chờ, cần nhiều thread hơn để lấp đầy thời gian rảnh của CPU.

*   **Hình 5 (Công thức mở rộng với Target CPU utilization):** Slide bổ sung biến **Target CPU utilization** (Mức sử dụng CPU mục tiêu, từ 0.0 đến 1.0). Công thức đầy đủ:
    `Số threads = Số core * Target CPU utilization * (1 + Wait time / Service time)`

*   **Hình 6 (Ví dụ 3 - Áp dụng công thức đầy đủ):** Slide áp dụng công thức mở rộng:
    *   `Số core = 4`, `Target CPU utilization = 0.5`, `Wait time / Service time = 200/20 = 10`.
    *   Số thread = `4 * 0.5 * (1 + 10) = 2 * 11 = 22`.
    *   Việc đặt mục tiêu CPU utilization = 0.5 nghĩa là ta chỉ muốn CPU hoạt động ở 50% công suất để dành tài nguyên cho các tác vụ khác hoặc giảm tải.

---

### 📚 2. Giải thích chi tiết lý thuyết và ví dụ

#### 💡 Lý thuyết: Thread Pool và ExecutorService là gì?
Trong Java, việc tạo mới và hủy bỏ một thread (luồng) là một hoạt động tốn kém tài nguyên. Nếu mỗi tác vụ đều tạo một thread mới, hệ thống có thể bị treo do cạn kiệt tài nguyên (OOM).
**Thread Pool (Bể chứa luồng)** ra đời để giải quyết vấn đề này. Nó là một tập hợp các thread được tạo sẵn và quản lý bởi một bộ quản lý (thường là `ExecutorService`). Khi có một tác vụ mới, bộ quản lý sẽ lấy một thread rảnh trong bể để thực thi, thay vì tạo mới. Khi tác vụ xong, thread được trả về bể để tái sử dụng.

#### 💡 Lý thuyết: Thread Starvation (Đói luồng)
Là hiện tượng một hoặc nhiều thread không thể tiếp tục thực thi vì tài nguyên (CPU, lock) liên tục bị các thread khác (thường có độ ưu tiên cao hơn) chiếm giữ trong thời gian dài.
*   **Cách phòng tránh:** Sử dụng **Fair locks** (khóa công bằng) để đảm bảo thread chờ lâu nhất sẽ được nhận lock trước, hoặc dùng cơ chế **Aging** (tăng dần độ ưu tiên của thread chờ lâu).

#### 💡 Lý thuyết: ThreadFactory là gì?
Là một interface dùng để tạo ra các thread mới. Nó cho phép bạn tùy biến cách thread được khởi tạo, ví dụ như đặt tên, đặt độ ưu tiên, hoặc đánh dấu là `daemon` thread. Điều này rất hữu ích cho việc gỡ lỗi (debug) vì bạn có thể biết thread nào đang chạy tác vụ gì qua tên của nó.

---

### 💻 3. Ví dụ Code chi tiết cho từng nội dung

#### 1. ThreadPoolExecutor (Cấu hình thủ công)
Đây là cách linh hoạt và được khuyến nghị nhất trong môi trường production để kiểm soát tài nguyên.

```java
import java.util.concurrent.*;

public class CustomThreadPoolExample {
    public static void main(String[] args) {
        // 1. Định nghĩa các tham số
        int corePoolSize = 4;          // Số thread nòng cốt luôn được giữ lại
        int maximumPoolSize = 8;       // Số thread tối đa có thể tạo ra
        long keepAliveTime = 60L;      // Thời gian sống của thread dư thừa
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(10); // Hàng đợi chứa task
        
        // 2. Tạo ThreadFactory tùy chỉnh (để đặt tên thread dễ debug)
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "MyPool-Thread-" + count++);
            }
        };

        // 3. Tạo ThreadPoolExecutor
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy() // Chiến lược khi hàng đợi đầy
        );

        // 4. Gửi 20 tác vụ vào pool
        for (int i = 1; i <= 20; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " đang chạy trên: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000); // Giả lập tác vụ tốn 2 giây
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        
        // 5. Tắt pool sau khi dùng xong
        executor.shutdown();
    }
}
```
**Giải thích:**
*   **Cơ chế hoạt động:** Khi có task mới, nếu số thread hiện tại < `corePoolSize`, tạo thread mới. Nếu không, đưa task vào `workQueue`. Nếu `workQueue` đầy, tạo thêm thread (tối đa `maximumPoolSize`). Nếu vẫn đầy, áp dụng `RejectedExecutionHandler`.
*   **`CallerRunsPolicy`:** Nếu pool quá tải, thread nào gọi hàm `execute` sẽ tự mình chạy task đó thay vì ném lỗi, giúp giảm tốc độ gửi task.
*   **`ThreadFactory` tùy chỉnh:** Giúp log hiển thị `MyPool-Thread-1` thay vì `pool-1-thread-1`, giúp việc theo dõi (monitor) và gỡ lỗi dễ dàng hơn nhiều.

#### 2. Các loại Thread Pool có sẵn (Executors)
Java cung cấp sẵn các factory method để tạo nhanh các pool phổ biến.

```java
import java.util.concurrent.*;

public class BuiltInPoolsExample {
    public static void main(String[] args) {
        // a. Fixed Thread Pool: Cố định số lượng thread
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        
        // b. Cached Thread Pool: Tự động co giãn, tạo thread mới khi cần
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        
        // c. Single Thread Pool: Chỉ có 1 thread, các task chạy tuần tự
        ExecutorService singlePool = Executors.newSingleThreadExecutor();
        
        // d. Scheduled Thread Pool: Dùng cho tác vụ hẹn giờ hoặc định kỳ
        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);

        // Ví dụ sử dụng CachedThreadPool
        for (int i = 0; i < 5; i++) {
            final int id = i;
            cachedPool.execute(() -> {
                System.out.println("Task " + id + " chạy trên: " + Thread.currentThread().getName());
            });
        }

        // Tắt tất cả các pool
        fixedPool.shutdown();
        cachedPool.shutdown();
        singlePool.shutdown();
        scheduledPool.shutdown();
    }
}
```
**Giải thích:**
*   `newFixedThreadPool(n)`: Tạo pool với đúng `n` thread. Nếu có nhiều task hơn `n`, các task sẽ phải xếp hàng chờ.
*   `newCachedThreadPool()`: Tạo thread mới nếu không có thread rảnh. Thread nhàn rỗi quá 60 giây sẽ bị hủy. Rất tốt cho các tác vụ ngắn, không yêu cầu cố định tài nguyên.
*   `newSingleThreadExecutor()`: Đảm bảo mọi task được thực thi tuần tự theo đúng thứ tự được gửi đến.
*   `newScheduledThreadPool(n)`: Cho phép lên lịch chạy task sau một khoảng thời gian (`schedule`) hoặc chạy lặp lại (`scheduleAtFixedRate`).

#### 3. Áp dụng công thức tính số lượng thread
Giả sử máy chủ có **8 lõi CPU**, bạn muốn CPU hoạt động ở mức **80%** (`0.8`). Tác vụ của bạn có thời gian chờ I/O là **100ms** và thời gian tính toán là **20ms**.

```java
public class ThreadSizeCalculator {
    public static void main(String[] args) {
        // Lấy số core thực tế của máy
        int cores = Runtime.getRuntime().availableProcessors();
        
        // Các thông số đo đạc được
        double targetUtilization = 0.8; // 80%
        double waitTime = 100.0;        // ms
        double serviceTime = 20.0;      // ms
        
        // Công thức: N = cores * U * (1 + W/S)
        double blockingCoefficient = waitTime / serviceTime; // 100/20 = 5
        int optimalThreads = (int) Math.ceil(cores * targetUtilization * (1 + blockingCoefficient));
        
        System.out.println("Số core: " + cores);
        System.out.println("Hệ số chặn (W/S): " + blockingCoefficient);
        System.out.println("Số thread tối ưu: " + optimalThreads); 
        // Với 8 core: 8 * 0.8 * (1 + 5) = 8 * 0.8 * 6 = 38.4 -> làm tròn lên 39
    }
}
```
**Giải thích:** Kết quả tính toán cho thấy với tác vụ I/O-intensive, bạn cần số lượng thread lớn hơn nhiều so với số core (39 so với 8) để đảm bảo CPU không bị "đói" trong lúc chờ I/O. Nếu bạn chỉ dùng 8 thread, CPU sẽ chỉ hoạt động được khoảng 20-30% công suất vì phần lớn thời gian thread bận chờ đợi.

#### 4. Thread Starvation (Mô phỏng)
Hãy tưởng tượng có một tác vụ "độc quyền" chiếm giữ một tài nguyên quan trọng (ví dụ: một lock), trong khi các tác vụ khác liên tục phải xếp hàng chờ đợi.

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StarvationDemo {
    private static final Lock lock = new ReentrantLock(true); // true = Fair lock

    public static void main(String[] args) {
        Runnable highPriorityTask = () -> {
            while (true) {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " đang chạy tác vụ ưu tiên cao");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        };

        Runnable lowPriorityTask = () -> {
            while (true) {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " đang chạy tác vụ ưu tiên thấp");
                } finally {
                    lock.unlock();
                }
                try {
                    Thread.sleep(10); // Nghỉ một chút để nhường CPU
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Tạo 1 thread ưu tiên cao và 5 thread ưu tiên thấp
        new Thread(highPriorityTask, "High-Priority").start();
        for (int i = 0; i < 5; i++) {
            new Thread(lowPriorityTask, "Low-Priority-" + i).start();
        }
    }
}
```
**Giải thích:**
*   **`ReentrantLock(true)`:** Tham số `true` tạo ra một **Fair Lock**. Nó đảm bảo thread nào chờ lâu nhất sẽ được nhận lock tiếp theo. Nếu bạn thay bằng `false` (Non-fair lock), thread `High-Priority` có thể liên tục chiếm lại lock ngay sau khi vừa nhả ra, khiến các thread `Low-Priority` không bao giờ được chạy -> **Starvation**.
*   **Hạn chế:** Fair lock làm giảm thông lượng (throughput) tổng thể của hệ thống, nhưng nó đảm bảo tính công bằng và tránh đói luồng.

---

### 💎 Tổng kết
*   **Hình 1-6** cung cấp nền tảng lý thuyết vững chắc về cách vận hành của Thread Pool, từ các loại pool cơ bản đến công thức tính toán "vàng" để cấu hình pool sao cho hiệu quả.
*   **ThreadPoolExecutor** là công cụ mạnh mẽ nhất, cho phép bạn kiểm soát mọi khía cạnh của pool.
*   **ThreadFactory** là công cụ hỗ trợ đắc lực cho việc vận hành và gỡ lỗi.
*   **Công thức tính số thread** giúp bạn chuyển từ việc "đoán mò" sang cấu hình dựa trên cơ sở khoa học, tối ưu hóa tài nguyên phần cứng.
*   **Thread Starvation** là một cạm bẫy cần tránh, và **Fair Lock** là một trong những giải pháp hữu hiệu.

Hy vọng những giải thích và ví dụ trên giúp bạn nắm rõ nội dung của các slide bài giảng!


Dưới đây là giải thích chi tiết cho bộ mã nguồn bạn vừa cung cấp. Tôi sẽ chia các file thành các nhóm chủ đề (Fork/Join, Thread Pools cơ bản, Tính năng nâng cao, và Tùy chỉnh) để bạn dễ theo dõi, kèm theo lý thuyết và giải thích code chi tiết.

---

### PHẦN 1: FORK/JOIN FRAMEWORK (CHIA ĐỂ TRỊ)

**Lý thuyết cốt lõi:**
*   **Fork/Join** là một framework chia nhỏ một tác vụ lớn thành các tác vụ con (Fork), giải quyết chúng song song, sau đó tổng hợp kết quả lại (Join).
*   Nó sử dụng cơ chế **Work-Stealing (Đánh cắp công việc)**: Các luồng rảnh rỗi sẽ "lấy" bớt công việc từ hàng đợi của các luồng đang bận để tối ưu hóa CPU.
*   **`RecursiveAction`**: Dùng khi tác vụ **không trả về** kết quả (tương tự `void`).
*   **`RecursiveTask<V>`**: Dùng khi tác vụ **có trả về** kết quả (tương tự `Callable`).

#### 1. `DefaultRecursiveAction.java` & `ForkJoinPoolDemo.java` (Không trả về kết quả)
*   **Mã nguồn:**
    ```java
    public class DefaultRecursiveAction extends RecursiveAction {
        private int workload = 0;
        public DefaultRecursiveAction(int workload) { this.workload = workload; }

        @Override
        protected void compute() {
            if (this.workload < 18) { // Điều kiện dừng (Base case)
                System.out.println("Doing workLoad myself...");
            } else { // Chia nhỏ (Recursive case)
                List<DefaultRecursiveAction> subtasks = createSubtasks();
                for (RecursiveAction subtask : subtasks) {
                    subtask.fork(); // Gửi task con vào pool
                }
                // ForkJoinTask.invokeAll(subtasks); // Cách viết khác
            }
        }
        private List<DefaultRecursiveAction> createSubtasks() {
            return List.of(new DefaultRecursiveAction(this.workload / 2),
                           new DefaultRecursiveAction(this.workload / 2));
        }
    }
    ```
*   **Giải thích:** Nếu khối lượng công việc (`workload`) < 18, nó tự làm. Nếu >= 18, nó chia đôi công việc và gọi `fork()` để đưa các task con vào chạy song song.
*   **Mã nguồn Demo:** `forkJoinPool2.invoke(new DefaultRecursiveAction(32));`
    *   **Giải thích:** `invoke()` là phương thức đồng bộ, nó sẽ chờ cho đến khi tác vụ gốc và tất cả tác vụ con hoàn thành rồi mới đi tiếp.

#### 2. `DefaultRecursiveTask.java` & `ForkJoinPoolDemo2.java` (Có trả về kết quả)
*   **Mã nguồn:**
    ```java
    public class DefaultRecursiveTask extends RecursiveTask<Integer> {
        // ... (tương tự Action)
        @Override
        protected Integer compute() {
            if (this.workload < 18) {
                return workload * 2; // Trả về kết quả
            } else {
                List<DefaultRecursiveTask> subtasks = createSubtasks();
                for (RecursiveTask<Integer> subtask : subtasks) {
                    subtask.fork(); // Chạy song song
                }
                int result = 0;
                for (DefaultRecursiveTask subtask : subtasks) {
                    result += subtask.join(); // Chờ và lấy kết quả từ task con
                }
                return result; // Trả về tổng kết quả
            }
        }
    }
    ```
*   **Giải thích:** Điểm khác biệt lớn nhất là sử dụng `join()` để chờ các task con trả về kết quả và tính tổng (`result += subtask.join()`).
*   **Demo:** `var result = pool.invoke(new DefaultRecursiveTask(40));` -> Kết quả sẽ được tính toán và in ra.

---

### PHẦN 2: CÁC LOẠI THREAD POOL CƠ BẢN

**Lý thuyết cốt lõi:**
*   **`execute(Runnable)`:** Không trả về kết quả, không cho biết task chạy thành công hay thất bại.
*   **`submit(Runnable/Callable)`:** Trả về một `Future`, cho phép kiểm tra trạng thái hoặc lấy kết quả.

#### 3. `ExecuteWithExecutorServicesDemo.java`
*   **Mã nguồn:**
    ```java
    ExecutorService es = Executors.newCachedThreadPool();
    es.execute(() -> System.out.println("Hello from thread " + Thread.currentThread().getName()));
    ```
*   **Giải thích:** Tạo một Cached Thread Pool (tự động tạo thread mới nếu cần, thu hồi nếu rảnh). Dùng `execute` để chạy một task đơn giản in ra tên thread.

#### 4. `FixedThreadPoolDemo.java`
*   **Mã nguồn:**
    ```java
    ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    // Submit 4 tasks...
    System.out.println("Pool size: " + es.getPoolSize()); // Kết quả: 3
    System.out.println("Queue size: " + es.getQueue().size()); // Kết quả: 1
    ```
*   **Giải thích:** Tạo pool cố định 3 thread. Khi submit 4 task, 3 task đầu chạy ngay, task thứ 4 bị đẩy vào hàng đợi (`Queue`). Do đó `Pool size` = 3, `Queue size` = 1. Đoạn cuối code minh họa việc lấy số core CPU thực tế của máy để cấu hình pool.

#### 5. `CachedThreadPoolDemo.java`
*   **Mã nguồn:**
    ```java
    ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    // Submit 4 tasks...
    System.out.println("Pool size: " + es.getPoolSize()); // Kết quả: 4
    System.out.println("Queue size: " + es.getQueue().size()); // Kết quả: 0
    ```
*   **Giải thích:** Với Cached Pool, khi submit 4 task, nó sẽ tạo ngay 4 thread để chạy song song (vì không có giới hạn core pool). Do đó `Pool size` = 4, không có task nào phải chờ trong hàng đợi -> `Queue size` = 0.

---

### PHẦN 3: CÁC TÍNH NĂNG NÂNG CAO CỦA EXECUTORSERVICE

#### 6. `ExecutorServiceInterruptionDemo.java` (Ngắt và Tắt Pool)
*   **Lý thuyết:**
    *   `shutdown()`: Không nhận task mới, nhưng đợi các task đang chạy và trong hàng đợi chạy xong.
    *   `shutdownNow()`: Cố gắng dừng ngay lập tức, hủy các task đang chờ và gửi tín hiệu `interrupt` đến các task đang chạy.
*   **Giải thích code:**
    *   Tạo 10 task, task thứ `i` sẽ `sleep(i)` giây.
    *   Gọi `es.shutdown();` -> Không cho submit thêm.
    *   `es.awaitTermination(2, TimeUnit.SECONDS);` -> Chờ tối đa 2 giây để các task kết thúc.
    *   `finally { es.shutdownNow(); }` -> Sau 2 giây, nếu task nào chưa xong (các task sleep 3,4...9s), sẽ bị gửi tín hiệu ngắt. Khối `catch (InterruptedException e)` sẽ bắt được và in ra "Task #i is interrupted".

#### 7. `InvokeAllDemo.java` (Chạy tất cả và chờ)
*   **Mã nguồn:**
    ```java
    List<Callable<String>> tasks = // task 1, 2, 3
    List<Future<String>> futures = es.invokeAll(tasks);
    ```
*   **Giải thích:** `invokeAll` nhận vào một List các `Callable`, thực thi tất cả và **chờ cho đến khi TẤT CẢ hoàn thành**. Nó trả về một List các `Future`. Bạn phải dùng vòng lặp `future.get()` để lấy kết quả. Nếu một task lỗi, nó sẽ ném exception ra ngoài.

#### 8. `InvokeAnyDemo.java` (Chạy tất cả, lấy cái nhanh nhất)
*   **Mã nguồn:**
    ```java
    // task 1 sleep 500ms, task 2 sleep 100ms, task 3 sleep 300ms
    String result = es.invokeAny(tasks);
    System.out.println(result); // Kết quả: task #2
    ```
*   **Giải thích:** `invokeAny` chạy tất cả các task, nhưng **chỉ trả về kết quả của task hoàn thành ĐẦU TIÊN**. Các task còn lại sẽ bị hủy bỏ (cancel). Rất hữu ích khi bạn có nhiều nguồn cung cấp dữ liệu và chỉ cần lấy dữ liệu từ nguồn phản hồi nhanh nhất.

#### 9. `ScheduledThreadPoolDemo.java` & `ScheduledThreadPoolDemo2.java` (Hẹn giờ)
*   **Lý thuyết:**
    *   `scheduleAtFixedRate`: Chạy lặp lại với chu kỳ cố định. *Thời gian bắt đầu của lần chạy sau = Thời gian bắt đầu lần trước + period.* (Nếu task chạy quá lâu, các lần chạy có thể bị dồn nén).
    *   `scheduleWithFixedDelay`: *Thời gian bắt đầu lần sau = Thời gian kết thúc lần trước + delay.* (Luôn có khoảng nghỉ cố định giữa các lần chạy).
*   **Giải thích:**
    *   `Demo 1`: Hẹn giờ chạy 1 lần duy nhất sau 1 giây.
    *   `Demo 2`: So sánh `FixedRate` (chạy mỗi 1000ms tính từ lúc bắt đầu) và `FixedDelay` (chạy xong, nghỉ 1000ms rồi mới chạy tiếp). Vì task có `sleep(500ms)` nên sự khác biệt về thời gian sẽ thấy rõ nếu bạn in thêm timestamp.

---

### PHẦN 4: TÙY CHỈNH THREAD FACTORY

#### 10. `ThreadFactoryDemo.java`
*   **Lý thuyết:** `ThreadFactory` cho phép bạn can thiệp vào quá trình tạo thread. Bạn có thể đặt tên thread, đặt độ ưu tiên (priority), hoặc biến nó thành `daemon thread`.
*   **Mã nguồn:**
    ```java
    class DefaultThreadFactory implements ThreadFactory {
        private AtomicInteger counter = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Custom Thread Factory | Thread #" + counter.getAndIncrement());
        }
    }
    // Sử dụng:
    var es = Executors.newFixedThreadPool(threadNum, new DefaultThreadFactory());
    ```
*   **Giải thích:** Khi pool cần tạo một thread mới, nó sẽ gọi hàm `newThread()`. Chúng ta sử dụng `AtomicInteger` để đếm và đặt tên cho thread (ví dụ: `Custom Thread Factory | Thread #0`). Điều này cực kỳ hữu ích khi bạn đọc log (nhật ký hệ thống) và muốn biết task đó chạy ở đâu, thuộc luồng nào.