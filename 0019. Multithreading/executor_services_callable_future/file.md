# Giải thích chi tiết: ExecutorService, Callable & Future trong Java

## ⚠️ Lưu ý về hình ảnh

Bạn có nhắc đến "từng hình ảnh mà tôi gửi cho bạn", nhưng **hiện tại tôi không nhận được hình ảnh nào** trong tin nhắn của bạn (chỉ có text). Nếu bạn muốn tôi giải thích từng hình, vui lòng gửi lại ảnh. Trong khi chờ, tôi sẽ giải thích **chi tiết toàn bộ lý thuyết + ví dụ code** cho 3 chủ đề này — đây thường là nội dung chính trong các slide/bài giảng về chủ đề này.

---

# PHẦN 1: LÝ THUYẾT TỔNG QUAN

## 1.1. Vấn đề của `Thread` truyền thống

Trước Java 5, muốn chạy đa luồng ta làm:

```java
Thread t = new Thread(() -> System.out.println("Hello"));
t.start();
```

**Nhược điểm:**
- Mỗi task tạo 1 Thread mới → **tốn tài nguyên** (mỗi thread ~1MB stack).
- Không kiểm soát được **số lượng thread** đang chạy → dễ gây **quá tải** (hàng nghìn thread).
- Không có cơ chế **tái sử dụng thread** (thread chết sau khi xong task).
- Không có cách **nhận kết quả trả về** từ thread (Runnable không return được).
- Không có cách **theo dõi/hủy** task đang chạy.

→ **Giải pháp:** Java 5 ra đời `java.util.concurrent` với **ExecutorService**, **Callable**, **Future**.

---

## 1.2. Bức tranh tổng thể

```
┌─────────────────────────────────────────────────────────────┐
│  Bạn submit task (Runnable/Callable) vào ExecutorService   │
│                                                             │
│   ExecutorService ──> Thread Pool (nhiều thread tái sử dụng)│
│         │                                                   │
│         └── trả về Future (nếu là Callable)                 │
│                                                             │
│   Future = "vé nhận kết quả" trong tương lai               │
│         ├─ get()      → lấy kết quả (chờ nếu chưa xong)    │
│         ├─ isDone()   → task xong chưa?                    │
│         ├─ cancel()   → hủy task                           │
│         └─ isCancelled() → đã hủy chưa?                    │
└─────────────────────────────────────────────────────────────┘
```

**Ẩn dụ dễ hiểu:**
- **ExecutorService** = *quầy giao dịch ngân hàng* có sẵn 5 giao dịch viên (thread pool).
- **Callable** = *phiếu yêu cầu* có ghi rõ "tôi muốn nhận lại kết quả" (ví dụ: "tính tổng 1..100").
- **Runnable** = *phiếu yêu cầu* kiểu "làm xong là được, không cần trả kết quả".
- **Future** = *biên lai* bạn cầm. Khi cần kết quả, đưa biên lai ra quầy → `get()` → nhận tiền (hoặc đợi nếu chưa xong).

---

# PHẦN 2: ExecutorService

## 2.1. Lý thuyết

**Định nghĩa:** `ExecutorService` là interface trong `java.util.concurrent`, quản lý một **pool các thread** để thực thi bất đồng bộ các task.

**Các loại pool phổ biến** (qua factory class `Executors`):

| Factory Method | Đặc điểm | Dùng khi nào |
|---|---|---|
| `newFixedThreadPool(n)` | Cố định n thread, task xếp hàng nếu bận | Biết trước tải, muốn giới hạn tài nguyên |
| `newCachedThreadPool()` | Tạo thread mới khi cần, tái sử dụng thread rảnh (60s) | Nhiều task ngắn, tải biến động |
| `newSingleThreadExecutor()` | Chỉ 1 thread, task chạy tuần tự | Cần đảm bảo thứ tự thực thi |
| `newScheduledThreadPool(n)` | Hỗ trợ chạy định kỳ / delay | Cron job, timer |

**Các method quan trọng:**
- `execute(Runnable)` — chạy task, không trả kết quả.
- `submit(Runnable)` / `submit(Callable)` — chạy task, **trả về `Future`**.
- `invokeAll(Collection<Callable>)` — chạy tất cả, trả `List<Future>`.
- `invokeAny(...)` — chạy tất cả, trả về kết quả của **task xong đầu tiên**.
- `shutdown()` — ngừng nhận task mới, **chờ task đang chạy xong**.
- `shutdownNow()` — ngắt các task đang chạy ngay lập tức.
- `awaitTermination(timeout, unit)` — chờ pool đóng hoàn toàn.

## 2.2. Ví dụ code chi tiết

```java
import java.util.concurrent.*;

public class ExecutorServiceDemo {
    public static void main(String[] args) throws Exception {
        // 1. Tạo pool có 3 thread
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // 2. Submit 5 task — chỉ 3 task chạy song song, 2 task xếp hàng
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            pool.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("Task " + taskId + " đang chạy trên " + threadName);
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                System.out.println("Task " + taskId + " xong");
            });
        }

        // 3. Ngừng nhận task mới, chờ task đang chạy xong
        pool.shutdown();

        // 4. Chờ tối đa 10s để pool đóng hẳn
        if (pool.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.println("Tất cả task đã hoàn thành!");
        }
    }
}
```

### 🔍 Giải thích từng dòng

| Dòng | Ý nghĩa | Lý thuyết bên cạnh |
|---|---|---|
| `Executors.newFixedThreadPool(3)` | Tạo pool 3 thread cố định | Bên trong dùng `ThreadPoolExecutor` với `LinkedBlockingQueue` không giới hạn |
| `pool.execute(...)` | Giao task cho pool | Nếu pool đủ thread rảnh → chạy ngay; nếu bận → **xếp vào queue** (FIFO) |
| `final int taskId = i` | Bắt buộc phải `final`/effectively final | Vì lambda capture biến — nếu không final sẽ lỗi compile |
| `pool.shutdown()` | **Không** kill task đang chạy | Khác với `shutdownNow()` (dùng `Thread.interrupt()`) |
| `awaitTermination(10, SECONDS)` | Block main thread chờ | Trả `true` nếu pool đóng xong trong 10s |

### ⚠️ Lưu ý quan trọng
- **Luôn gọi `shutdown()`** — nếu không, JVM sẽ không thoát (vì thread pool là **non-daemon thread**).
- Không tạo `newFixedThreadPool` với size quá lớn → dễ OOM.
- Từ Java 21, có **Virtual Threads** (`Executors.newVirtualThreadPerTaskExecutor()`) — nhẹ hơn nhiều.

---

# PHẦN 3: Callable

## 3.1. Lý thuyết

**Định nghĩa:** `Callable<V>` là functional interface (Java 5) giống `Runnable` nhưng:
- Có **giá trị trả về** kiểu `V`.
- Có thể **ném checked exception**.

```java
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;   // có return, có throws
}

@FunctionalInterface
public interface Runnable {
    void run();                  // không return, không throws checked
}
```

### Bảng so sánh

| Tiêu chí | `Runnable` | `Callable<V>` |
|---|---|---|
| Method | `void run()` | `V call() throws Exception` |
| Trả kết quả | ❌ Không | ✅ Có (kiểu V) |
| Ném checked exception | ❌ Không | ✅ Có |
| Dùng với | `Thread`, `execute()`, `submit()` | **Chỉ** `submit()` / `invokeAll()` |
| Kết quả trả về khi submit | `Future<?>` | **`Future<V>`** |

## 3.2. Ví dụ code chi tiết

```java
import java.util.concurrent.*;

public class CallableDemo {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newSingleThreadExecutor();

        // Tạo Callable trả về Integer
        Callable<Integer> sumTask = () -> {
            int sum = 0;
            for (int i = 1; i <= 100; i++) {
                sum += i;
                Thread.sleep(10); // giả lập task nặng
            }
            return sum;  // ✅ có return
        };

        // submit() trả về Future<Integer>
        Future<Integer> future = pool.submit(sumTask);

        System.out.println("Đã submit, làm việc khác trong lúc chờ...");

        // Lấy kết quả — sẽ block cho tới khi task xong
        Integer result = future.get();
        System.out.println("Tổng 1..100 = " + result);

        pool.shutdown();
    }
}
```

### 🔍 Giải thích từng bước

**Bước 1 — Định nghĩa Callable:**
```java
Callable<Integer> sumTask = () -> { ... return sum; };
```
- Lambda thay cho `new Callable<Integer>() { public Integer call() {...} }`.
- Kiểu generic `<Integer>` = kiểu kết quả trả về.

**Bước 2 — Submit:**
```java
Future<Integer> future = pool.submit(sumTask);
```
- `submit()` **không block** — trả về `Future` ngay lập tức.
- Task chạy **bất đồng bộ** trên thread khác.

**Bước 3 — Get kết quả:**
```java
Integer result = future.get();   // BLOCK tới khi có kết quả
```
- Nếu task chưa xong → thread gọi `get()` bị **treo**.
- Nếu task ném exception → `get()` ném `ExecutionException` (bọc exception gốc bên trong).

### ⚠️ Bẫy thường gặp

```java
// ❌ SAI: Callable có return nhưng không dùng Future → mất kết quả
pool.submit(sumTask);   // trả về Future nhưng không ai get

// ✅ ĐÚNG
Future<Integer> f = pool.submit(sumTask);
Integer r = f.get();
```

---

# PHẦN 4: Future

## 4.1. Lý thuyết

**Định nghĩa:** `Future<V>` đại diện cho **kết quả của một computation bất đồng bộ** — giống như "biên lai" sẽ có kết quả trong tương lai.

```java
public interface Future<V> {
    boolean cancel(boolean mayInterruptIfRunning);
    boolean isCancelled();
    boolean isDone();
    V get() throws InterruptedException, ExecutionException;
    V get(long timeout, TimeUnit unit) throws ...;
}
```

### Giải thích từng method

| Method | Ý nghĩa | Ghi chú |
|---|---|---|
| `get()` | **Block** để lấy kết quả | Treo vô hạn nếu task chưa xong |
| `get(timeout, unit)` | Block tối đa timeout | Ném `TimeoutException` nếu quá hạn |
| `isDone()` | Task đã xong chưa? | Trả `true` cả khi task lỗi hoặc bị cancel |
| `cancel(mayInterrupt)` | Hủy task | Trả `false` nếu đã xong, không hủy được |
| `isCancelled()` | Đã bị hủy thành công chưa? | Chỉ `true` nếu `cancel()` trả `true` |

### Mối quan hệ 3 khái niệm

```
ExecutorService.submit(Callable<V>) → Future<V>
       │
       ├─ task đang chạy trên thread pool
       │
       └─ Future = handle để:
            • kiểm tra trạng thái (isDone, isCancelled)
            • lấy kết quả (get)
            • hủy (cancel)
```

## 4.2. Ví dụ code chi tiết

```java
import java.util.concurrent.*;

public class FutureDemo {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        // ===== Task 1: Task nặng, giả lập 2 giây =====
        Callable<String> heavyTask = () -> {
            Thread.sleep(2000);
            return "Kết quả nặng";
        };

        // ===== Task 2: Task lỗi =====
        Callable<Integer> errorTask = () -> {
            throw new RuntimeException("Task bị lỗi!");
        };

        // ===== Task 3: Task chạy lâu để test cancel =====
        Callable<String> longTask = () -> {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                if (Thread.interrupted()) {
                    System.out.println("Long task bị ngắt!");
                    throw new InterruptedException();
                }
            }
            return "Long task xong";
        };

        Future<String> f1 = pool.submit(heavyTask);
        Future<Integer> f2 = pool.submit(errorTask);
        Future<String> f3 = pool.submit(longTask);

        // --- Kiểm tra trạng thái TRƯỚC khi get ---
        System.out.println("f1 done? " + f1.isDone());       // false
        System.out.println("f3 cancelled? " + f3.isCancelled()); // false

        // --- get() với timeout ---
        try {
            String r1 = f1.get(3, TimeUnit.SECONDS);
            System.out.println("f1 = " + r1);
        } catch (TimeoutException e) {
            System.out.println("f1 quá hạn!");
        }

        // --- get() task lỗi → ExecutionException ---
        try {
            f2.get();
        } catch (ExecutionException e) {
            System.out.println("f2 lỗi: " + e.getCause().getMessage());
        }

        // --- Hủy task đang chạy ---
        boolean cancelled = f3.cancel(true);   // true = interrupt thread đang chạy
        System.out.println("Hủy f3: " + cancelled);
        System.out.println("f3 isCancelled? " + f3.isCancelled()); // true

        pool.shutdown();
    }
}
```

### 🔍 Giải thích từng đoạn

**1. Kiểm tra trạng thái trước khi get**
```java
System.out.println("f1 done? " + f1.isDone());  // false
```
- `isDone()` **không block** — dùng để "poll" trạng thái.
- Pattern phổ biến: vòng lặp `while (!f.isDone())` để theo dõi tiến độ.

**2. `get(timeout)` — tránh treo vô hạn**
```java
String r1 = f1.get(3, TimeUnit.SECONDS);
```
- Task chạy 2s < 3s → OK, lấy được kết quả.
- Nếu task chạy 5s → ném `TimeoutException`.
- ⚠️ **Lưu ý:** timeout **không hủy task**! Task vẫn tiếp tục chạy ngầm.

**3. `get()` với task lỗi → `ExecutionException`**
```java
try { f2.get(); }
catch (ExecutionException e) {
    e.getCause().getMessage();   // "Task bị lỗi!"
}
```
- `Future.get()` **bọc** exception của task vào `ExecutionException`.
- Phải dùng `e.getCause()` để lấy exception gốc.

**4. `cancel(true)` — hủy task**
```java
boolean cancelled = f3.cancel(true);
```
- Tham số `true` = **interrupt** thread đang chạy.
- Bên trong task phải **kiểm tra** `Thread.interrupted()` hoặc bắt `InterruptedException` thì mới dừng sớm được.
- Nếu task đã xong → `cancel()` trả `false` (không hủy được).

### ⚠️ Bẫy thường gặp

| Bẫy | Cách tránh |
|---|---|
| `future.get()` treo vô hạn | Luôn dùng `get(timeout, unit)` |
| Quên `shutdown()` → JVM không thoát | Luôn gọi `shutdown()` trong `finally` |
| `cancel(true)` nhưng task không dừng | Task phải check `Thread.interrupted()` |
| `get()` ném `ExecutionException` nhưng không xử lý | Luôn catch và `e.getCause()` |

---

# PHẦN 5: VÍ DỤ TỔNG HỢP — ỨNG DỤNG THỰC TẾ

**Bài toán:** Gọi 3 API song song (giả lập), tổng hợp kết quả.

```java
import java.util.*;
import java.util.concurrent.*;

public class RealWorldExample {

    // Giả lập gọi API — trả về sau `delayMs`
    static Callable<String> fakeApi(String name, int delayMs) {
        return () -> {
            Thread.sleep(delayMs);
            return name + " OK";
        };
    }

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        try {
            // 1. Submit 3 API song song
            List<Callable<String>> tasks = List.of(
                fakeApi("API-A", 1000),
                fakeApi("API-B", 500),
                fakeApi("API-C", 1500)
            );
            List<Future<String>> futures = pool.invokeAll(tasks);

            // 2. Thu kết quả
            for (Future<String> f : futures) {
                System.out.println(f.get());   // API-B OK, API-A OK, API-C OK
            }

            // 3. invokeAny — chỉ lấy task NHANH NHẤT
            List<Callable<String>> raceTasks = List.of(
                fakeApi("Server-1", 2000),
                fakeApi("Server-2", 300),
                fakeApi("Server-3", 800)
            );
            String fastest = pool.invokeAny(raceTasks);
            System.out.println("Server nhanh nhất: " + fastest); // Server-2 OK

        } finally {
            pool.shutdown();
        }
    }
}
```

### 🔍 Điểm đáng chú ý

- **`invokeAll(tasks)`**: chạy tất cả, **block** cho tới khi tất cả xong → trả `List<Future>` **theo đúng thứ tự** task đầu vào.
- **`invokeAny(tasks)`**: chạy tất cả, trả về kết quả của task **xong đầu tiên**, **hủy** các task còn lại → dùng cho pattern "race" (gọi nhiều mirror, lấy cái nhanh nhất).
- **`finally { pool.shutdown(); }`** — pattern chuẩn để đảm bảo dọn dẹp.

---

# PHẦN 6: TÓM TẮT BẰNG BẢNG

| Khái niệm | Vai trò | Ví dụ ẩn dụ |
|---|---|---|
| **ExecutorService** | Quản lý thread pool, nhận task | Quầy giao dịch có sẵn nhân viên |
| **Callable** | Task có trả kết quả + ném exception | Phiếu yêu cầu "tính tổng 1..100" |
| **Runnable** | Task không trả kết quả | Phiếu yêu cầu "in log" |
| **Future** | Handle để lấy/hủy/theo dõi kết quả | Biên lai nhận kết quả sau |
| **`submit()`** | Giao task, trả `Future` | Nộp phiếu, nhận biên lai |
| **`execute()`** | Giao task, **không** trả `Future` | Nộp phiếu "fire and forget" |
| **`get()`** | Block lấy kết quả | Đưa biên lai đổi kết quả |
| **`cancel()`** | Hủy task | Hủy yêu cầu trước khi xong |

---

## 📌 Nếu bạn có hình ảnh cụ thể

Vui lòng **gửi lại hình** (screenshot slide/bài giảng). Tôi sẽ:
1. Trích xuất nội dung từng hình.
2. Giải thích chi tiết nội dung hình đó.
3. Liên kết với code mẫu phù hợp ở trên (hoặc viết code mới bám sát hình).

Bạn cũng có thể cho tôi biết hình đó thuộc slide nào (ví dụ: "sơ đồ ThreadPoolExecutor", "bảng so sánh Runnable vs Callable", "sequence diagram của Future.get()") — tôi sẽ đào sâu đúng phần đó.


# Giải thích chi tiết từng file — Nhóm chủ đề *ExecutorService, Callable & Future*

Tôi sẽ đi qua **từng file** theo nhóm chức năng, mỗi file gồm: **Mục đích → Lý thuyết → Phân tích code → Output dự kiến → Lưu ý/bẫy**.

---

# 📦 NHÓM A — CƠ BẢN: Callable, execute() vs submit()

## 1️⃣ `CallableDemo.java`

### 🎯 Mục đích
Minh họa **cơ bản nhất** về `Callable` + `Future` + `get(timeout)`.

### 📚 Lý thuyết nền
- `Callable<V>` = task **có trả kết quả** kiểu `V`.
- `submit(Callable)` trả về `Future<V>` ngay lập tức (không block).
- `Future.get(timeout, unit)` → block tối đa `timeout`, sau đó ném `TimeoutException` nếu chưa xong.

### 🔍 Phân tích code

```java
ExecutorService es = Executors.newCachedThreadPool();
```
→ Tạo pool **cached**: core=0, max=Integer.MAX_VALUE, thread nhàn rỗi bị hủy sau **60 giây**.

```java
Future<Integer> future = es.submit(() -> 1 + 1);
```
- Lambda `() -> 1+1` tự động trở thành `Callable<Integer>` vì **có return**.
- `submit()` **không block** — task chạy trên thread khác, main thread chạy tiếp ngay.

```java
System.out.println(future.get(10, TimeUnit.SECONDS));
```
- Block main thread đến khi có kết quả (task `1+1` xong gần như tức thời).
- In ra `2`.
- `TimeoutException` được khai báo ở `throws` để compiler chấp nhận (dù thực tế không xảy ra).

### 📤 Output
```
2
```

### ⚠️ Bẫy lớn: **thiếu `shutdown()`**
- Cached pool dùng **non-daemon thread** → JVM **không thoát** ngay.
- Sau khi in `2`, JVM đợi **~60 giây** cho thread idle tự chết → chương trình "treo" 1 phút.
- Đây là **lỗi rất phổ biến trong tutorial**. Sửa: thêm `es.shutdown();` cuối `main`.

### 📌 Điểm kiến thức rút ra
| Đối tượng | Kiểu trả về của `submit` |
|---|---|
| `submit(Runnable)` | `Future<?>` — `get()` trả `null` |
| `submit(Callable<V>)` | `Future<V>` — `get()` trả `V` |

---

## 2️⃣ `ExecuteWithExecutorServicesDemo.java`

### 🎯 Mục đích
So sánh **`execute()`** vs **`submit()`** — đây là câu hỏi phỏng vấn rất hay gặp.

### 📚 Lý thuyết nền
| | `execute(Runnable)` | `submit(...)` |
|---|---|---|
| Trả về | `void` | `Future` |
| Nhận | Chỉ `Runnable` | `Runnable` **hoặc** `Callable` |
| Exception của task | **Ném lên `UncaughtExceptionHandler`** | **Được "nuốt" vào Future** — chỉ thấy khi `get()` |
| Dùng khi | "Fire and forget" | Cần kết quả / theo dõi / hủy |

### 🔍 Phân tích code

```java
ExecutorService es = Executors.newCachedThreadPool();
es.execute(() -> System.out.println("Hello from thread " + Thread.currentThread().getName()));
es.execute(() -> System.out.println("Hello from thread " + Thread.currentThread().getName()));
```

- Cached pool: **tạo thread khi cần**.
- Hai task gần như submit cùng lúc → **có thể** chạy trên **2 thread khác nhau**, hoặc **cùng thread** nếu task 1 kịp xong trước khi task 2 được submit (race condition của scheduler).

### 📤 Output (một khả năng điển hình)
```
Hello from thread pool-1-thread-2
Hello from thread pool-1-thread-1
```
Thứ tự **không xác định**, tên thread có thể **giống hoặc khác**.

### ⚠️ Lưu ý
- Vì dùng `execute()`, **không nhận được `Future`** → không kiểm soát được task.
- Vẫn **thiếu `shutdown()`** → JVM treo ~60s (giống file 1).
- Nếu task `execute()` ném exception → không ai bắt, chỉ in stacktrace qua default handler.

---

# 📦 NHÓM B — CÁC LOẠI THREAD POOL PHỔ BIẾN

## 3️⃣ `CachedThreadPoolDemo.java`

### 🎯 Mục đích
Minh họa **cached thread pool** và **các thuộc tính nội bộ** của `ThreadPoolExecutor`.

### 📚 Lý thuyết: `newCachedThreadPool()` bên trong là gì?

```java
// Tương đương (Java 8+):
new ThreadPoolExecutor(
    0,                       // corePoolSize
    Integer.MAX_VALUE,       // maximumPoolSize
    60L, TimeUnit.SECONDS,   // keepAliveTime
    new SynchronousQueue<>() // queue KHÔNG lưu — handoff trực tiếp
);
```

| Thuộc tính | Ý nghĩa |
|---|---|
| `corePoolSize = 0` | Không giữ thread "thường trú" |
| `maximumPoolSize = MAX` | Tạo thread **không giới hạn** khi có task |
| Queue = `SynchronousQueue` | **Không chứa** task — task đến là phải có thread nhận ngay, nếu không sẽ tạo thread mới |
| `keepAliveTime = 60s` | Thread idle > 60s → chết |

### 🔍 Phân tích code

```java
ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newCachedThreadPool();
```
→ **Cast** để truy cập method nội bộ (`getPoolSize`, `getQueue`) — interface `ExecutorService` **không có** các method này.

```java
IntStream.range(0, 4).forEach(i -> {
    es.submit(() -> { TimeUnit.SECONDS.sleep(1); ... });
});
```
→ Submit **4 task**, mỗi task ngủ 1 giây.
→ Vì cached pool **không queue**, 4 task gần như đồng thời → **tạo 4 thread** (mỗi task 1 thread).

```java
es.shutdown();  // chỉ ngừng nhận task mới — KHÔNG chờ task chạy xong
System.out.println("Pool size: " + es.getPoolSize());   // 4
System.out.println("Queue size: " + es.getQueue().size()); // 0
```

### 📤 Output
```
Pool size: 4
Queue size: 0
```

### ⚠️ Lưu ý quan trọng
- `shutdown()` **không block** — 4 task vẫn đang sleep ở thời điểm in thông số.
- Vì đọc `getPoolSize()` **ngay sau `shutdown`** → số liệu có thể **lệch nhẹ** nếu task nhanh/chậm bất thường. Nhưng với `sleep(1s)` thì gần như chắc chắn 4/0.
- Nếu thay `newCachedThreadPool` bằng `newFixedThreadPool(3)`: pool=3, queue=1 (xem file tiếp).

### 📌 Bảng so sánh cached vs fixed
| | Cached | Fixed(3) |
|---|---|---|
| Core / Max | 0 / MAX | 3 / 3 |
| Queue | `SynchronousQueue` (không chứa) | `LinkedBlockingQueue` (chứa vô hạn) |
| Task khi bận | Tạo thread mới | **Xếp vào queue** |
| Nguy cơ | **OOM** nếu submit ồ ạt | Task chờ lâu |

---

## 4️⃣ `FixedThreadPoolDemo.java`

### 🎯 Mục đích
Minh họa **fixed thread pool** — giới hạn thread cố định + queue giữ task chờ.

### 📚 Lý thuyết: `newFixedThreadPool(n)`

```java
new ThreadPoolExecutor(
    n, n,                    // core = max = n (thread KHÔNG BAO GIỜ bị hủy)
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>()  // queue KHÔNG GIỚI HẠN
);
```

→ Khi task vượt quá `n` → **xếp hàng** trong `LinkedBlockingQueue`.

### 🔍 Phân tích code

```java
ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
IntStream.range(0, 4).forEach(i -> es.submit(() -> sleep(1)));
```
- 4 task, pool chỉ có 3 thread → **3 task chạy song song**, **1 task xếp hàng**.
- Sau `sleep(1)` task đầu tiên xong → task thứ 4 mới được chạy.

```java
es.shutdown();
System.out.println("Pool size: " + es.getPoolSize());   // 3
System.out.println("Queue size: " + es.getQueue().size()); // 1
```

### 📤 Output
```
Pool size: 3
Queue size: 1
Available processors: <số core máy>
```

### 📌 Hai dòng cuối

```java
int numOfCores = Runtime.getRuntime().availableProcessors();
System.out.println("Available processors: " + numOfCores);
var es2 = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
```
- Lấy số core CPU. Pattern phổ biến: **pool size ≈ số core** cho task **CPU-bound**.
- `es2` được tạo nhưng **không dùng, không shutdown** — chương trình vẫn thoát vì không có thread nào được spawn (fixed pool chỉ tạo thread **khi có task submit**).

### ⚠️ Lưu ý thực chiến
| Loại task | Pool size khuyến nghị |
|---|---|
| CPU-bound (tính toán) | `= số core` hoặc `core + 1` |
| I/O-bound (DB, HTTP) | `= core * (1 + wait_time / cpu_time)` — thường **2× đến 10× core** |

---

# 📦 NHÓM C — LIFECYCLE: shutdown, awaitTermination, shutdownNow

## 5️⃣ `ExecutorServiceInterruptionDemo.java`

### 🎯 Mục đích
Minh họa **vòng đời** của pool và **cách interrupt task** đang chạy.

### 📚 Lý thuyết: 3 method "chết chóc" của vòng đời

| Method | Hành vi |
|---|---|
| `shutdown()` | Ngừng nhận task mới. **Chờ task đang chạy xong**. Queue vẫn được xử lý. |
| `awaitTermination(timeout)` | **Block** main thread chờ pool đóng hẳn. Trả `true` nếu đóng xong trong timeout. |
| `shutdownNow()` | **Interrupt** các task đang chạy + **trả về** danh sách task chưa chạy. |

### 🔍 Phân tích code

```java
IntStream.range(0, 10).forEach((i) -> {
    es.submit(() -> {
        try {
            TimeUnit.SECONDS.sleep(i);   // task i ngủ i giây
            System.out.println("Task #" + i + " is completed");
        } catch (InterruptedException e) {
            System.out.println("Task #" + i + " is interrupted");
        }
    });
});
```
→ 10 task chạy song song trên cached pool, task `i` ngủ `i` giây.
→ Task 0 xong ngay, task 1 sau 1s, task 2 sau 2s... task 9 sau 9s.

```java
es.shutdown();
```
→ Ngừng nhận task mới. **Task đang chạy vẫn tiếp tục**.

```java
es.awaitTermination(2, TimeUnit.SECONDS);
```
→ Main **chờ 2 giây** cho pool đóng hoàn toàn. Trong 2s đó:
- Task #0 xong (0s)
- Task #1 xong (1s)
- Task #2 xong (2s — **sát nút**)

```java
finally {
    es.shutdownNow();   // interrupt các task còn lại (3→9)
}
```
→ Task #3..#9 đang `sleep` bị **interrupt** → ném `InterruptedException` → in **"is interrupted"**.

### 📤 Output dự kiến
```
Shutting down
Task #0 is completed
Task #1 is completed
Task #2 is completed       ← có thể chậm 1 nhịp → thành interrupted
Task #3 is interrupted
Task #4 is interrupted
...
Task #9 is interrupted
```
*(Thứ tự 3-9 lộn xộn vì song song)*

### 📌 Dòng comment rất hay

```java
// Uncomment line below to see RejectedExecutionException
// es.submit(() -> System.out.println("new task will throw exception after shutdown"));
```
→ Sau `shutdown()`, gọi `submit()` sẽ ném **`RejectedExecutionException`** (unchecked). Đây là cơ chế "khóa cửa" của pool.

### ⚠️ Lưu ý
- `sleep(2)` của task #2 **cạnh tranh với awaitTermination(2s)** — không deterministic.
- Import thừa: `Callable`, `List`, `Arrays`, `CannotStubVoidMethodWithReturnValue` (Mockito) — code ví dụ thôi, không ảnh hưởng.
- Pattern `try { awaitTermination } finally { shutdownNow }` là **chuẩn production** để đảm bảo **không leak task** khi thoát app.

---

# 📦 NHÓM D — INVOKE ALL / ANY

## 6️⃣ `InvokeAllDemo.java`

### 🎯 Mục đích
Minh họa **`invokeAll`** — submit **nhiều Callable cùng lúc**, chờ **TẤT CẢ** xong.

### 📚 Lý thuyết

```java
<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
```
- **Block** đến khi **TẤT CẢ** task hoàn thành.
- Trả về `List<Future>` **cùng thứ tự** với list tasks đầu vào.
- Task có exception **không** làm hỏng các task khác — exception nằm trong từng `Future`.

### 🔍 Phân tích code

```java
List<Callable<String>> tasks = Arrays.asList(
    () -> "task #1", () -> "task #2", () -> "task #3"
);
List<Future<String>> futures = es.invokeAll(tasks);
```
→ 3 task chạy song song, main block chờ cả 3 xong.

```java
futures.stream()
    .map(future -> {
        try { return future.get(); }
        catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        return "";
    })
    .forEach(System.out::println);
```
→ Map mỗi Future → giá trị String (đã xong nên `get()` **không block nữa**).

### 📤 Output
```
task #1
task #2
task #3
```
**Đúng thứ tự** vì `invokeAll` giữ thứ tự input — dù task nào xong trước.

### ⚠️ Lưu ý
- **Thiếu `shutdown()`** → JVM treo ~60s (cached pool).
- Nếu 1 task ném exception → `get()` của nó ném `ExecutionException`, các task khác vẫn OK.
- Muốn **giới hạn thời gian chờ tất cả**: dùng `invokeAll(tasks, timeout, unit)` — sau timeout, các task chưa xong bị **cancel**.

---

## 7️⃣ `InvokeAnyDemo.java`

### 🎯 Mục đích
Minh họa **`invokeAny`** — kiểu "**race**": lấy kết quả của **task NHANH NHẤT**.

### 📚 Lý thuyết

```java
<T> T invokeAny(Collection<? extends Callable<T>> tasks)
```
- **Block** đến khi **task ĐẦU TIÊN** hoàn thành **thành công**.
- **Trả về giá trị `T`** trực tiếp (không phải `Future`).
- **HỦY** các task còn lại (gọi `cancel(true)`).
- Nếu **tất cả** fail → ném `ExecutionException`.

### 🔍 Phân tích code

```java
Arrays.asList(
    () -> { sleep(500); return "task #1"; },
    () -> { sleep(100); return "task #2"; },  // ← nhanh nhất
    () -> { sleep(300); return "task #3"; }
);
```
Timeline:
- `t=0`      : 3 task bắt đầu
- `t=100ms`  : task #2 xong → `invokeAny` **return "task #2"** ngay
- `t=100ms`  : task #1, #3 bị **interrupt** (nếu đang sleep → ném `InterruptedException`, bị bỏ qua)

### 📤 Output
```
task #2
```

### 📌 Use case thực tế của `invokeAny`
- Gọi **nhiều mirror server**, lấy phản hồi **nhanh nhất** → giảm latency.
- **Hedged request** (như Google): gửi 2 request song song, hủy cái chậm.
- Tính toán song song nhiều cách, lấy kết quả cách nào xong trước.

### ⚠️ Lưu ý
- Kết quả **KHÔNG deterministic** nếu các task timing gần nhau (ví dụ 100, 105, 110ms → có thể lấy #1 hoặc #2 tùy scheduler).
- Vẫn **thiếu `shutdown()`**.
- Exception của task nhanh-nhất-nhưng-fail → `invokeAny` **bỏ qua**, tiếp tục chờ task khác.

---

# 📦 NHÓM E — SCHEDULED THREAD POOL

## 8️⃣ `ScheduledThreadPoolDemo.java`

### 🎯 Mục đích
Minh họa **`schedule`** — chạy task **1 lần sau delay**.

### 📚 Lý thuyết

```java
ScheduledExecutorService es = Executors.newScheduledThreadPool(4);

// Chạy 1 lần sau 1 giây
es.schedule(Runnable, 1, TimeUnit.SECONDS);

// Chạy định kỳ
es.scheduleAtFixedRate(task, initialDelay, period, unit);
es.scheduleWithFixedDelay(task, initialDelay, delay, unit);
```

### 🔍 Phân tích code

```java
var es = Executors.newScheduledThreadPool(4);
es.schedule(() -> System.out.println("Schedule example: " + 
        Thread.currentThread().getName()), 1, TimeUnit.SECONDS);
es.shutdown();
```
- `schedule(...)`: chạy **1 lần**, **sau 1s**.
- `shutdown()`: KHÔNG hủy task đã schedule — **task vẫn chạy** theo Javadoc.
- JVM chờ task chạy xong rồi mới thoát.

### 📤 Output (sau 1s)
```
Schedule example: pool-1-thread-1
```

### 📌 Nếu muốn **hủy** task đã schedule
```java
ScheduledFuture<?> sf = es.schedule(...);
sf.cancel(false);  // false = không interrupt nếu đang chạy
```

---

## 9️⃣ `ScheduledThreadPoolDemo2.java`

### 🎯 Mục đích
So sánh **`scheduleAtFixedRate`** vs **`scheduleWithFixedDelay`** — điểm cực kỳ hay được hỏi.

### 📚 Lý thuyết — Sự khác biệt CỐT LÕI

| | `scheduleAtFixedRate` | `scheduleWithFixedDelay` |
|---|---|---|
| Công thức | `t(n+1) = t(n) + period` | `t(n+1) = end(t(n)) + delay` |
| Ý nghĩa | **Chu kỳ cố định** giữa các **lần BẮT ĐẦU** | **Khoảng nghỉ cố định** giữa **KẾT THÚC** và **BẮT ĐẦU** kế tiếp |
| Nếu task chậm hơn period | **Task chạy liên tục**, không nghỉ | Vẫn nghỉ đúng `delay` sau khi xong |

### 🔍 Phân tích code

```java
es.scheduleAtFixedRate(() -> {
    System.out.println("Hello");
    try { TimeUnit.MILLISECONDS.sleep(500); } catch (...) {}
}, 500, 1000, TimeUnit.MILLISECONDS);
```
- `initialDelay = 500ms`, `period = 1000ms`, task chạy 500ms.

**Timeline `scheduleAtFixedRate`:**
```
t=500ms    → start, in "Hello", sleep 500
t=1000ms   → end
t=1500ms   → start (500 + 1000)
...
```
→ Cứ **1000ms** có 1 lần "Hello" → trong 7s: chạy lúc **500, 1500, 2500, 3500, 4500, 5500, 6500** → **~7 lần**.

```java
es.awaitTermination(7, TimeUnit.SECONDS);
es.shutdownNow();
```
- Vì task lặp **không bao giờ tự kết thúc** → `awaitTermination(7s)` **luôn trả false** sau đúng 7 giây.
- `shutdownNow()` → interrupt task đang `sleep` → task kết thúc.

```java
var es2 = Executors.newScheduledThreadPool(4);
es2.scheduleWithFixedDelay(..., 500, 1000, TimeUnit.MILLISECONDS);
```
- `initialDelay = 500ms`, `delay = 1000ms`, task chạy 500ms.

**Timeline `scheduleWithFixedDelay`:**
```
t=500ms    → start, in "Hello", sleep 500
t=1000ms   → end
t=2000ms   → start (end + 1000)
t=2500ms   → end
t=3500ms   → start
...
```
→ Chu kỳ thực tế **1500ms** (task 500 + delay 1000) → trong 7s: chạy lúc **500, 2000, 3500, 5000, 6500** → **~5 lần**.

### 📤 Output dự kiến
```
# scheduleAtFixedRate
Hello   (500ms)
Hello   (1500ms)
Hello   (2500ms)
Hello   (3500ms)
Hello   (4500ms)
Hello   (5500ms)
Hello   (6500ms)
====================================
# scheduleWithFixedDelay
Hello   (500ms)
Hello   (2000ms)
Hello   (3500ms)
Hello   (5000ms)
Hello   (6500ms)
```

### ⚠️ Bẫy kinh điển
- **Nếu task chạy > period** ở `scheduleAtFixedRate`:
  - Task kế tiếp chạy **ngay** khi task trước xong — không có khoảng nghỉ.
  - Ví dụ: period=1s, task chạy 3s → các lần chạy **chồng chéo** (thực tế chạy back-to-back).
- **Nếu exception ném ra** ở bất kỳ lần chạy nào → **toàn bộ chu kỳ dừng** (im lặng). Bọc `try-catch` bên trong task.
- `awaitTermination` ở đây trả **false** vì task lặp không tự dừng. Pattern đúng: dùng `ScheduledFuture.cancel()`.

---

# 📦 NHÓM F — THREAD FACTORY

## 🔟 `ThreadFactoryDemo.java`

### 🎯 Mục đích
Minh họa **custom `ThreadFactory`** để đặt tên thread, set daemon, đặt priority, uncaught handler...

### 📚 Lý thuyết

`ThreadFactory` = nhà máy tạo thread. Cho phép **can thiệp vào mọi thread** do pool sinh ra.

```java
public interface ThreadFactory {
    Thread newThread(Runnable r);
}
```

**Use case thực tế:**
- Đặt **tên thread có ý nghĩa** (`order-worker-1`, `payment-worker-2`) → dễ debug log.
- Set **`setDaemon(true)`** → thread không giữ JVM sống.
- Set **`setPriority`**, **`setUncaughtExceptionHandler`**.

### 🔍 Phân tích code

```java
int threadNum = 4;
var es = Executors.newFixedThreadPool(threadNum, new DefaultThreadFactory());
```
→ Fixed pool 4 thread, **mỗi thread** được tạo bởi `DefaultThreadFactory`.

```java
IntStream.range(0, 4).forEach(i -> {
    es.submit(() -> System.out.println(Thread.currentThread().getName()));
});
```
→ 4 task, mỗi task in tên thread → sẽ thấy **tên custom**.

```java
class DefaultThreadFactory implements ThreadFactory {
    private AtomicInteger counter = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "Custom Thread Factory | Thread #" + counter.getAndIncrement());
    }
}
```

- `AtomicInteger`: **thread-safe** — nhiều thread pool có thể **gọi `newThread` đồng thời**.
- `counter.getAndIncrement()`: trả giá trị **hiện tại** rồi mới tăng → thread đầu là **#0**, sau đó **#1**, **#2**, **#3**.

### 📤 Output
```
Custom Thread Factory | Thread #0
Custom Thread Factory | Thread #1
Custom Thread Factory | Thread #2
Custom Thread Factory | Thread #3
```
*(Thứ tự có thể lộn xộn, nhưng tập tên là 0..3)*

### 📌 Block comment rất hay

```java
// Future<Integer> future = es.submit(() -> {
//     System.out.println(Thread.currentThread().getName());
//     return i;
// });
// System.out.println("Result: " + future.get());
```
→ Nhấn mạnh: **`ThreadFactory` áp dụng cho cả `Runnable` và `Callable`** — vì bên trong `ThreadPoolExecutor` luôn **bọc task** thành `Runnable` (dù là `Callable`) trước khi giao cho `ThreadFactory`.

### ⚠️ Lưu ý
- Trong production nên dùng `ThreadFactoryBuilder` (Guava) hoặc tự viết như trên.
- Đừng quên **`setDaemon`** nếu không muốn thread giữ JVM sống.

---

# 📦 NHÓM G — FORK/JOIN FRAMEWORK

## 1️⃣1️⃣ `ForkJoinPoolDemo.java` — Khởi tạo pool

### 🎯 Mục đích
So sánh **các cách tạo `ForkJoinPool`** và chạy **`RecursiveAction`** (không trả kết quả).

### 📚 Lý thuyết: Fork/Join là gì?

**Divide and conquer** — chia task lớn thành task nhỏ, chạy song song, ghép kết quả.

```
Task(32) ──fork──> Task(16), Task(16)
                       │           │
                       └── leaf ───┘  (workload < threshold → tự làm)
```

**Các pool có sẵn:**

| Cách | Đặc điểm |
|---|---|
| `ForkJoinPool.commonPool()` | Pool **dùng chung** cho parallel streams, CompletableFuture. Size = `cores - 1`. |
| `new ForkJoinPool(n)` | Pool riêng `n` thread. |
| `new ForkJoinPool()` | Pool riêng, size = **số core CPU**. |

### 🔍 Phân tích code

```java
ForkJoinPool commonPool = ForkJoinPool.commonPool();
ForkJoinPool forkJoinPool = new ForkJoinPool(4);
ForkJoinPool forkJoinPool2 = new ForkJoinPool();
```
→ Tạo 3 pool. **Chỉ dùng `forkJoinPool2`**. Hai pool kia → resource leak nhẹ (nhưng chưa có thread nào được spawn nên OK khi exit).

```java
forkJoinPool2.invoke(new DefaultRecursiveAction(32));
```
- `invoke()`: chạy task + **block** chờ xong (dùng cho `RecursiveAction`).
- Workload = 32 → xem file `DefaultRecursiveAction` bên dưới.

### 📤 Output
```
Splitting workLoad in thread ForkJoinPool-1-worker-N with workload: 32
Doing workLoad myself in thread ForkJoinPool-1-worker-A with workload: 16
Doing workLoad myself in thread ForkJoinPool-1-worker-B with workload: 16
```
*(Có thể `A` và `B` trùng nhau — worker tự xử lý lại sau khi fork).*

---

## 1️⃣2️⃣ `ForkJoinPoolDemo2.java` — Chạy `RecursiveTask`

### 🎯 Mục đích
Chạy **`RecursiveTask`** (có trả kết quả) và in **kết quả**.

### 🔍 Phân tích code

```java
var pool = new ForkJoinPool();
var result = pool.invoke(new DefaultRecursiveTask(40));
System.out.println("Result: " + result);
```
- `invoke()` trả về **kết quả `Integer`** (vì `RecursiveTask<Integer>`).
- Xem tính toán ở file `DefaultRecursiveTask`.

### 📤 Output
```
Result: 80
```

---

## 1️⃣3️⃣ `DefaultRecursiveAction.java` — Action không trả kết quả

### 🎯 Mục đích
Cài đặt **divide & conquer** với `RecursiveAction` (không return).

### 📚 Lý thuyết

```java
abstract class RecursiveAction extends ForkJoinTask<Void> {
    protected abstract void compute();  // KHÔNG return
}
```

**Threshold** (ngưỡng): nếu `workload < threshold` → **tự làm**, ngược lại → **chia nhỏ**.

### 🔍 Phân tích code

```java
private int workload = 0;
```
→ "Khối lượng công việc" mô phỏng (số đơn vị task).

```java
protected void compute() {
    if (this.workload < 18) {
        // LEAF: tự xử lý
        System.out.println("Doing workLoad myself in thread " + 
            Thread.currentThread().getName() + " with workload: " + this.workload);
    } else {
        // SPLIT
        System.out.println("Splitting workLoad in thread " + 
            Thread.currentThread().getName() + " with workload: " + this.workload);
        List<DefaultRecursiveAction> subtasks = new ArrayList<>(createSubtasks());
        for (RecursiveAction subtask : subtasks) {
            subtask.fork();   // ← push vào deque của thread hiện tại
        }
        // invokeAll(subtasks) là cách khác — fork tất cả + join tất cả
    }
}
```

**Điểm cực kỳ quan trọng — `fork()` là gì?**

| Method | Ý nghĩa |
|---|---|
| `fork()` | **Push** task vào **work-stealing deque** của thread hiện tại. Task có thể bị **worker khác "lấy trộm"** chạy. |
| `join()` | **Chờ** task xong + trả kết quả. |
| `invokeAll(tasks)` | Gọi `fork()` tất cả + `join()` tất cả. Tiện hơn. |

> ⚠️ Trong `RecursiveAction`, **không cần `join()`** vì không cần kết quả — chỉ cần `fork()` là đủ.

```java
private List<DefaultRecursiveAction> createSubtasks() {
    // workload / 2, workload / 2
}
```
→ Chia **đôi** workload.

### 🔍 Trace với `workload = 32`

```
compute(32)
  32 >= 18 → split
  ├── fork(16)
  └── fork(16)

compute(16) [thread A]
  16 < 18 → leaf, in "Doing workLoad myself ... 16"

compute(16) [thread B or same]
  16 < 18 → leaf, in "Doing workLoad myself ... 16"
```

→ **Chỉ 1 lần split + 2 lần leaf**.

### 📤 Output
```
Splitting workLoad in thread ...workload: 32
Doing workLoad myself in thread ...workload: 16
Doing workLoad myself in thread ...workload: 16
```

---

## 1️⃣4️⃣ `DefaultRecursiveTask.java` — Task có trả kết quả

### 🎯 Mục đích
Cài đặt **`RecursiveTask<Integer>`** — chia nhỏ + **ghép kết quả**.

### 📚 Lý thuyết

```java
abstract class RecursiveTask<V> extends ForkJoinTask<V> {
    protected abstract V compute();  // CÓ return V
}
```

Khác `RecursiveAction` ở chỗ: phải **`join()`** để lấy kết quả từng subtask và **tổng hợp**.

### 🔍 Phân tích code

```java
protected Integer compute() {
    if (this.workload < 18) {
        return workload * 2;   // ← kết quả leaf
    } else {
        // split
        for (RecursiveTask<Integer> subtask : subtasks) subtask.fork();

        int result = 0;
        for (DefaultRecursiveTask subtask : subtasks) {
            result += subtask.join();   // ← BLOCK chờ kết quả từng subtask
        }
        return result;
    }
}
```

**Điểm cốt lõi:** mỗi task cha **join()** để **cộng dồn** kết quả con → kết quả lan truyền từ lá lên gốc.

### 🔍 Trace chi tiết với `workload = 40`

**Cây đệ quy:**
```
                  compute(40)
                 /          \
        compute(20)         compute(20)
        /       \           /        \
    (10)        (10)     (10)        (10)
     return 20  return 20 return 20  return 20
        \       /           \        /
        20+20=40            20+20=40
              \              /
              40 + 40 = 80  ← kết quả cuối
```

**Tính toán:**
| Task | Loại | Kết quả |
|---|---|---|
| compute(40) | split → 20, 20 | 40 + 40 = **80** |
| compute(20) | split → 10, 10 | 20 + 20 = **40** |
| compute(10) | leaf | 10 × 2 = **20** |

→ Kết quả cuối = **80**.

### 📤 Output
```
Splitting workLoad in thread ...workload: 40
Splitting workLoad in thread ...workload: 20
Splitting workLoad in thread ...workload: 20
Doing workLoad myself in thread ...workload: 10
Doing workLoad myself in thread ...workload: 10
Doing workLoad myself in thread ...workload: 10
Doing workLoad myself in thread ...workload: 10
Result: 80
```

### 📌 So sánh `RecursiveAction` vs `RecursiveTask`

| | `RecursiveAction` | `RecursiveTask<V>` |
|---|---|---|
| Kết quả | `void` | `V` |
| `compute()` trả về | `void` | `V` |
| Sau `fork()` | **Không** cần `join()` | **Phải** `join()` để lấy kết quả |
| Dùng khi | Side-effect (in, ghi file...) | Cần **tổng hợp kết quả** |

---

# 🎯 TỔNG KẾT TOÀN BỘ 14 FILE

## Sơ đồ phân loại

```
java.util.concurrent
│
├── ExecutorService (interface)
│   ├── ThreadPoolExecutor
│   │   ├── newCachedThreadPool()      → File 3
│   │   ├── newFixedThreadPool(n)      → File 4
│   │   └── newScheduledThreadPool(n)  → File 8, 9
│   │
│   └── ForkJoinPool                    → File 11, 12
│       ├── RecursiveAction            → File 13
│       └── RecursiveTask<V>           → File 14
│
├── Callable<V>                        → File 1
├── Future<V>                          → File 1, 6, 7
│
├── ThreadFactory                       → File 10
│
├── execute() vs submit()              → File 2
├── invokeAll() / invokeAny()          → File 6, 7
└── Lifecycle (shutdown…)              → File 5
```

## ⚠️ Các lỗi "ẩn" trong code mẫu — nhớ sửa khi làm thật

| File | Vấn đề | Sửa |
|---|---|---|
| `CallableDemo` | Thiếu `shutdown()` → JVM treo 60s | `finally { es.shutdown(); }` |
| `ExecuteWithExecutorServicesDemo` | Thiếu `shutdown()` + không bắt exception | Thêm try-finally |
| `InvokeAllDemo` | Thiếu `shutdown()` | `finally { es.shutdown(); }` |
| `InvokeAnyDemo` | Thiếu `shutdown()` | `finally { es.shutdown(); }` |
| `ForkJoinPoolDemo` | Tạo 3 pool không dùng hết | Chỉ tạo pool cần dùng |
| `FixedThreadPoolDemo` | `es2` tạo không shutdown | Bỏ hoặc `es2.shutdown()` |
| `ScheduledThreadPoolDemo2` | `awaitTermination` trả false nhưng bỏ qua | Kiểm tra return / dùng `Future.cancel()` |
| `ExecutorServiceInterruptionDemo` | `sleep(2)` + `awaitTermination(2s)` boundary | Tăng await lên 3s cho chắc |

## 🧠 Câu hỏi ôn tập nhanh

1. `submit()` khác `execute()` chỗ nào? → File 2.
2. Cached pool tạo thread khi nào, hủy khi nào? → File 3.
3. `invokeAll` vs `invokeAny` khác nhau ra sao? → File 6 & 7.
4. `scheduleAtFixedRate` khác `scheduleWithFixedDelay` thế nào? → File 9.
5. `fork()` làm gì? Tại sao cần `join()` ở `RecursiveTask` mà không ở `RecursiveAction`? → File 13, 14.
6. `shutdown` vs `shutdownNow` khác nhau chỗ nào? → File 5.

Nếu bạn cần tôi **chạy từng file trong đầu mô phỏng trace chi tiết hơn** (ví dụ: timeline từng ms của `ScheduledThreadPoolDemo2`) hoặc **viết unit test** cho từng file, cứ nói nhé!


Đã nhận được hình ảnh bạn gửi. Đây là một slide bài giảng trên nền tảng **Udemy**, thuộc khóa học *"Java from Zero to First Job, Practical Guide, 2000+ examples"* của giảng viên (thường là IT-BULLS).

Dưới đây là giải thích chi tiết từng thành phần trong hình ảnh và cách nó liên kết trực tiếp với 14 file code bạn vừa gửi ở lượt trước.

---

# 🖼️ PHÂN TÍCH CHI TIẾT HÌNH ẢNH

## 1. Tổng quan bố cục (Layout)
*   **Bên trái (Nền trắng):** Nội dung chính của slide - Danh sách các chủ đề sẽ học hôm nay (Agenda).
*   **Bên phải (Nền xanh):** Logo của Java (Duke - chú linh vật ly cà phê bốc khói).
*   **Phía dưới (Footer):** Logo và thông tin bản quyền của trung tâm đào tạo **IT-BULLS** (`CONFIDENTIAL | © IT-Bulls.com`), kèm logo Udemy ở góc phải.
*   **Phụ đề (Subtitle):** Dòng chữ đang chạy *"You will learn what the threat is today."* (Lưu ý: Đây là lỗi auto-caption của Udemy, từ đúng phải là **thread** - luồng, không phải *threat* - mối đe dọa).

## 2. Nội dung chính: AGENDA (Lịch trình bài học)

Slide này liệt kê **8 chủ đề cốt lõi**. Tôi sẽ giải thích từng mục và chỉ ra file code tương ứng mà bạn đã gửi ở lượt trước:

### 🔹 1. Executor interface
*   **Lý thuyết:** Là interface gốc rễ nhất trong gói `java.util.concurrent`. Nó chỉ có duy nhất một method: `void execute(Runnable command)`. Nó định nghĩa khái niệm "giao việc cho một cái gì đó chạy" mà không cần quan tâm nó chạy thế nào.
*   **Liên kết file code:** Bạn chưa gửi file nào code trực tiếp `Executor` (thường ít dùng trực tiếp), nhưng khái niệm này được kế thừa trong file `ExecuteWithExecutorServicesDemo.java`.

### 🔹 2. Executor service interface
*   **Lý thuyết:** Là interface con của `Executor`, cung cấp nhiều tính năng quản lý vòng đời (lifecycle) và theo dõi task hơn như: `submit()`, `invokeAll()`, `shutdown()`, `awaitTermination()`.
*   **Liên kết file code:** Hầu hết các file bạn gửi đều dùng `ExecutorService` (ví dụ: `CallableDemo.java`, `FixedThreadPoolDemo.java`).

### 🔹 3. What is a thread pool (Thread pool là gì?)
*   **Lý thuyết:** Thay vì tạo mới và hủy thread liên tục (rất tốn tài nguyên), ta tạo sẵn một "bể chứa" các thread. Khi có task, lấy thread ra dùng, xong việc thì trả lại bể để tái sử dụng.
*   **Liên kết file code:**
    *   `CachedThreadPoolDemo.java` (Bể linh hoạt, tạo thread khi cần).
    *   `FixedThreadPoolDemo.java` (Bể cố định số lượng thread).

### 🔹 4. How to start thread with executor service (Cách chạy thread bằng executor service)
*   **Lý thuyết:** Có 2 cách chính: dùng `execute()` (không trả kết quả) hoặc `submit()` (có trả về `Future`).
*   **Liên kết file code:** `ExecuteWithExecutorServicesDemo.java` (dùng `execute()`) và `CallableDemo.java` (dùng `submit()`).

### 🔹 5. What is Callable
*   **Lý thuyết:** Giống như `Runnable` nhưng có thể trả về kết quả (`return V`) và có thể ném ra checked exception.
*   **Liên kết file code:** `CallableDemo.java` (dòng `Future<Integer> future = es.submit(() -> 1 + 1);` - lambda này chính là một Callable).

### 🔹 6. What is Future
*   **Lý thuyết:** Là một "biên lai" (handle) đại diện cho kết quả của một tác vụ bất đồng bộ. Bạn dùng nó để kiểm tra task xong chưa (`isDone()`), lấy kết quả (`get()`), hoặc hủy task (`cancel()`).
*   **Liên kết file code:** `CallableDemo.java` (dùng `future.get()`), `InvokeAllDemo.java` (dùng `List<Future>`).

### 🔹 7. invokeAll() & invokeAny()
*   **Lý thuyết:**
    *   `invokeAll()`: Nộp một danh sách các task, đợi **TẤT CẢ** chạy xong và trả về danh sách kết quả.
    *   `invokeAny()`: Nộp một danh sách các task, chỉ cần **MỘT task chạy xong đầu tiên** là nó lấy kết quả và tự động hủy các task còn lại.
*   **Liên kết file code:** `InvokeAllDemo.java` và `InvokeAnyDemo.java`.

### 🔹 8. Thread interruption with executor service (Ngắt thread với executor service)
*   **Lý thuyết:** Làm sao để dừng các thread đang chạy khi ta không cần nữa. Các method quan trọng: `shutdown()`, `shutdownNow()`, và cách bắt `InterruptedException` bên trong task.
*   **Liên kết file code:** `ExecutorServiceInterruptionDemo.java`.

---

# 🎯 KẾT LUẬN: MỐI LIÊN HỆ GIỮA HÌNH ẢNH VÀ CODE

Slide này chính là **"mục lục" (Table of Contents)** cho toàn bộ 14 file code mà bạn đã gửi. 

| Chủ đề trong Slide (Agenda) | File code thực hành tương ứng |
| :--- | :--- |
| 1 & 2. Executor / ExecutorService | Tất cả các file |
| 3. Thread Pool | `CachedThreadPoolDemo`, `FixedThreadPoolDemo`, `ScheduledThreadPoolDemo` |
| 4. Start thread với Executor | `ExecuteWithExecutorServicesDemo` |
| 5 & 6. Callable & Future | `CallableDemo` |
| 7. invokeAll & invokeAny | `InvokeAllDemo`, `InvokeAnyDemo` |
| 8. Thread Interruption | `ExecutorServiceInterruptionDemo` |

*(Lưu ý: Slide này chưa đề cập đến **ForkJoinPool** và **ThreadFactory**, nhưng bạn đã gửi code của 2 phần nâng cao này. Có thể chúng nằm ở bài giảng tiếp theo trong khóa học).*

Nếu bạn muốn tôi đi sâu hơn vào bất kỳ mục nào trong 8 mục này (ví dụ: vẽ sơ đồ tư duy hoặc phân tích sâu hơn về cơ chế bên trong), hãy cho tôi biết nhé!