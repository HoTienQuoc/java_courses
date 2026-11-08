Hình ảnh bạn gửi là một slide bài giảng trên nền tảng Udemy, có tiêu đề **"AGENDA"** (Chương trình học), liệt kê các chủ đề sẽ được học trong bài về `CompletableFuture` trong Java.

Dưới đây là phần giải thích chi tiết về bức ảnh, sau đó là phần lý thuyết và ví dụ code cho từng mục trong agenda.

---

### 📸 Giải thích chi tiết hình ảnh

Bức ảnh chụp lại một bài giảng trực tuyến về Java. Nội dung chính là một danh sách (agenda) các chủ đề sẽ được giảng viên trình bày trong buổi học này. Cụ thể:

*   **Tiêu đề chính:** "AGENDA" (Chương trình học).
*   **Mục tiêu:** "Today we will learn:" (Hôm nay chúng ta sẽ học:).
*   **Danh sách các chủ đề:**
    1.  Cách thực thi các tác vụ với `CompletableFuture`.
    2.  Các phương thức `runAsync()` và `supplyAsync()`.
    3.  Phương thức `complete()`.
    4.  `Callback` là gì.
    5.  `thenApply()` và `thenApplyAsync()`.
    6.  `thenAccept()` và `thenAcceptAsync()`.
    7.  `thenRun()` và `thenRunAsync()`.
    8.  Kết hợp các `Future`.
    9.  `thenCompose()` và `thenComposeAsync()`.
    10. `thenCombine()` và `thenCombineAsync()`.
    11. `thenAcceptBoth()` và `thenAcceptBothAsync()`.
    12. `allOf()` và `anyOf()`.
    13. Xử lý ngoại lệ với `CompletableFuture`.
*   **Logo:** Ở góc phải là logo của Java (tách cà phê có hình ngọn lửa).
*   **Phụ đề (subtitle):** Ở dưới cùng, có phụ đề tiếng Anh: *"This is something what personally I use very often when I work with MultiThreaded and in my opinion,"* (Đây là thứ mà cá nhân tôi sử dụng rất thường xuyên khi làm việc với Đa luồng và theo ý kiến của tôi,...). Điều này cho thấy giảng viên đang nhấn mạnh tầm quan trọng của `CompletableFuture` trong lập trình đa luồng thực tế.

---

### 📚 Giải thích lý thuyết và ví dụ chi tiết từng mục

`CompletableFuture` (ra mắt từ Java 8) là một công cụ mạnh mẽ để lập trình bất đồng bộ (asynchronous programming). Nó khắc phục nhược điểm của `Future` truyền thống (phải gọi `get()` để lấy kết quả và bị chặn - blocking), cho phép bạn xây dựng các chuỗi tác vụ (pipeline) phức tạp một cách linh hoạt, không chặn và dễ dàng xử lý lỗi.

Dưới đây là giải thích cho từng mục trong agenda:

#### 1. Cách thực thi tác vụ với `CompletableFuture` & 2. `runAsync()`, `supplyAsync()`

*   **Lý thuyết:** Đây là các phương thức tĩnh (static) để khởi tạo một `CompletableFuture`. Chúng sẽ chạy một tác vụ trên một luồng (thread) khác.
    *   `runAsync(Runnable)`: Dùng khi tác vụ **không cần trả về kết quả**. Nó nhận vào một `Runnable` và trả về `CompletableFuture<Void>`.
    *   `supplyAsync(Supplier<U>)`: Dùng khi tác vụ **cần trả về kết quả**. Nó nhận vào một `Supplier` (một hàm không có tham số, trả về giá trị kiểu `U`) và trả về `CompletableFuture<U>`.
    *   **Lưu ý về Thread Pool:** Nếu không truyền vào một `Executor`, chúng sẽ dùng `ForkJoinPool.commonPool()` mặc định. Với các tác vụ I/O (mạng, file), nên dùng một thread pool riêng để tránh làm nghẽn pool chung.

*   **Ví dụ code & Giải thích:**

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreationExample {
    public static void main(String[] args) {
        // --- runAsync: Tác vụ không trả về kết quả ---
        System.out.println("1. Bắt đầu tác vụ không trả về kết quả...");
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            System.out.println("   -> [runAsync] Đã ghi log xong!");
        });

        // --- supplyAsync: Tác vụ có trả về kết quả ---
        // Sử dụng một thread pool riêng cho tác vụ I/O
        ExecutorService ioPool = Executors.newFixedThreadPool(2);
        System.out.println("2. Bắt đầu tác vụ có trả về kết quả...");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            return "Dữ liệu đã được tải";
        }, ioPool); // Chỉ định thread pool

        // Chờ các tác vụ hoàn thành để in kết quả (chỉ dùng cho demo)
        future1.join();
        System.out.println("   -> [supplyAsync] Kết quả: " + future2.join());
        ioPool.shutdown();
    }
}
```
*   **Giải thích:** `runAsync` nhận một lambda không trả về gì. `supplyAsync` nhận một lambda trả về chuỗi `"Dữ liệu đã được tải"`. Cả hai đều chạy bất đồng bộ. Chúng ta dùng `join()` (tương tự `get()` nhưng không ném checked exception) để chờ và lấy kết quả.

#### 3. Phương thức `complete()`

*   **Lý thuyết:** Phương thức này cho phép bạn **chủ động hoàn thành** một `CompletableFuture` bằng cách đặt giá trị kết quả cho nó từ bên ngoài. Điều này hữu ích khi bạn muốn bọc một cơ chế callback (ví dụ: một sự kiện từ UI hoặc một API cũ) vào một `CompletableFuture`. Nếu Future đã hoàn thành, việc gọi `complete()` sẽ không có tác dụng.

*   **Ví dụ code & Giải thích:**

```java
import java.util.concurrent.CompletableFuture;

public class CompleteExample {
    public static void main(String[] args) throws InterruptedException {
        // Tạo một CompletableFuture rỗng, chưa có kết quả
        CompletableFuture<String> future = new CompletableFuture<>();

        // Giả lập một luồng khác sẽ hoàn thành future sau 2 giây
        new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            // Chủ động hoàn thành future với giá trị "Xong!"
            future.complete("Xong!");
        }).start();

        System.out.println("Đang chờ kết quả...");
        // join() sẽ chặn cho đến khi future được complete()
        String result = future.join();
        System.out.println("Kết quả nhận được: " + result); // In ra: Xong!
    }
}
```
*   **Giải thích:** Một luồng mới được tạo để sau 2 giây sẽ gọi `future.complete("Xong!")`. Luồng `main` gọi `future.join()` và bị chặn lại. Khi lệnh `complete()` được thực thi, `future` có kết quả và `join()` trả về giá trị đó.

#### 4. `Callback` là gì?

*   **Lý thuyết:** Trong lập trình bất đồng bộ, **callback** (hàm gọi lại) là một hàm được truyền vào một hàm khác với mục đích sẽ được **gọi lại (thực thi) sau khi một tác vụ nào đó hoàn thành**. `CompletableFuture` sử dụng callback để bạn có thể "gắn" các hành động tiếp theo vào một tác vụ bất đồng bộ mà không cần phải chặn luồng chính để chờ đợi.

#### 5, 6, 7. Nhóm phương thức `then...`: `thenApply`, `thenAccept`, `thenRun`

Đây là các phương thức để "nối" các tác vụ lại với nhau, tạo thành một pipeline. Chúng nhận một callback và thực thi nó khi Future trước đó hoàn thành.

*   **Lý thuyết:**
    *   **`thenApply(Function<T, U>)`**: Nhận kết quả kiểu `T` từ bước trước, **biến đổi** nó thành kiểu `U` và trả về một `CompletableFuture<U>` mới. Nó giống như hàm `map` trong Stream.
    *   **`thenAccept(Consumer<T>)`**: Nhận kết quả kiểu `T` từ bước trước và **sử dụng** nó (ví dụ: in ra màn hình, lưu vào biến) nhưng **không trả về kết quả gì**. Nó trả về `CompletableFuture<Void>`. Nó giống như `forEach`.
    *   **`thenRun(Runnable)`**: **Không quan tâm** đến kết quả của bước trước. Nó chỉ đơn giản là chạy một hành động (Runnable) sau khi bước trước hoàn thành. Nó cũng trả về `CompletableFuture<Void>`.
    *   **Hậu tố `Async`:** Các phiên bản `...Async` (ví dụ: `thenApplyAsync`) sẽ thực thi callback trong một luồng mới (thường từ `ForkJoinPool.commonPool()` hoặc một `Executor` được chỉ định), thay vì chạy trên luồng đã hoàn thành future trước đó.

*   **Ví dụ code & Giải thích:**

```java
import java.util.concurrent.CompletableFuture;

public class ThenExample {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("[1] Tác vụ gốc: Trả về 10");
            return 10;
        })
        .thenApply(x -> {
            // thenApply: Biến đổi 10 -> 20
            System.out.println("[2] thenApply: Nhận " + x + ", nhân đôi thành " + (x * 2));
            return x * 2;
        })
        .thenAccept(result -> {
            // thenAccept: Sử dụng kết quả 20, không trả về gì
            System.out.println("[3] thenAccept: Nhận " + result + ", in ra và kết thúc.");
        })
        .thenRun(() -> {
            // thenRun: Chạy sau khi mọi thứ xong, không cần kết quả
            System.out.println("[4] thenRun: Pipeline đã hoàn tất!");
        }).join(); // Chờ toàn bộ pipeline chạy xong
    }
}
```
*   **Giải thích:** Pipeline thực hiện tuần tự: tạo ra `10`, `thenApply` biến nó thành `20`, `thenAccept` in `20` ra, và cuối cùng `thenRun` in thông báo hoàn tất. Kiểu dữ liệu thay đổi qua từng bước: `Integer` -> `Integer` -> `Void` -> `Void`.

#### 8, 9, 10, 11. Kết hợp các Future: `thenCompose`, `thenCombine`, `thenAcceptBoth`

Khi bạn có nhiều tác vụ bất đồng bộ và cần kết hợp chúng.

*   **Lý thuyết:**
    *   **`thenCompose(Function<T, CompletableFuture<U>>)`**: Dùng khi bạn có một tác vụ bất đồng bộ mà **kết quả của nó lại là một `CompletableFuture` khác**. `thenCompose` sẽ "làm phẳng" (flatten) và trả về `CompletableFuture<U>` thay vì `CompletableFuture<CompletableFuture<U>>`. Nó giống như `flatMap` trong Stream.
    *   **`thenCombine(CompletableFuture<U> other, BiFunction<T, U, V>)`**: Dùng khi bạn có **hai tác vụ bất đồng bộ độc lập** và muốn **kết hợp kết quả của cả hai** khi cả hai đều hoàn thành. Nó nhận kết quả của cả hai và trả về một giá trị mới kiểu `V`.
    *   **`thenAcceptBoth(CompletableFuture<U> other, BiConsumer<T, U>)`**: Tương tự `thenCombine` nhưng **không trả về kết quả** (trả về `CompletableFuture<Void>`). Nó chỉ "tiêu thụ" kết quả của cả hai future khi cả hai hoàn thành.

*   **Ví dụ code & Giải thích:**

```java
import java.util.concurrent.CompletableFuture;

public class CombineExample {
    public static void main(String[] args) {
        // --- thenCompose: Chuỗi phụ thuộc (Sequential) ---
        // Lấy User ID, sau đó dùng ID để lấy thông tin User
        CompletableFuture<String> userInfo = getUserAsync("user123")
            .thenCompose(userId -> getDetailsAsync(userId)); // Trả về CompletableFuture<String>
        System.out.println("Thông tin User: " + userInfo.join());

        // --- thenCombine: Kết hợp hai future độc lập (Parallel) ---
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(() -> "World");
        CompletableFuture<String> combined = futureA.thenCombine(futureB, (a, b) -> a + " " + b);
        System.out.println("Kết quả kết hợp: " + combined.join());
    }

    static CompletableFuture<String> getUserAsync(String username) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("   -> Đang lấy ID cho user: " + username);
            return "id_123"; // Giả lập ID
        });
    }

    static CompletableFuture<String> getDetailsAsync(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("   -> Đang lấy chi tiết cho ID: " + userId);
            return "Nguyễn Văn A"; // Giả lập tên
        });
    }
}
```
*   **Giải thích:** `thenCompose` được dùng để nối hai tác vụ có tính phụ thuộc: phải có `userId` mới lấy được `details`. `thenCombine` được dùng khi `futureA` và `futureB` có thể chạy song song, và ta chỉ cần kết hợp kết quả của chúng khi cả hai xong.

#### 12. `allOf()` và `anyOf()`

Đây là các phương thức tĩnh để làm việc với một danh sách các `CompletableFuture`.

*   **Lý thuyết:**
    *   **`allOf(CompletableFuture<?>... cfs)`**: Trả về một `CompletableFuture<Void>` mới. Future này sẽ **hoàn thành khi TẤT CẢ** các future trong danh sách truyền vào đều hoàn thành. Nếu có bất kỳ future nào lỗi, `allOf` cũng sẽ lỗi.
    *   **`anyOf(CompletableFuture<?>... cfs)`**: Trả về một `CompletableFuture<Object>`. Future này sẽ **hoàn thành ngay khi BẤT KỲ** future nào trong danh sách hoàn thành (bất kể thành công hay thất bại). Kết quả của nó là kết quả của future hoàn thành đầu tiên.

*   **Ví dụ code & Giải thích:**

```java
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AllAnyExample {
    public static void main(String[] args) {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> { sleep(1000); return "F1"; });
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> { sleep(2000); return "F2"; });
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> { sleep(3000); return "F3"; });

        // --- allOf: Chờ tất cả hoàn thành ---
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(f1, f2, f3);
        allFutures.join(); // Chờ đến khi cả 3 xong
        // Sau khi allOf xong, lấy kết quả từng future
        List<String> results = Arrays.asList(f1.join(), f2.join(), f3.join());
        System.out.println("allOf - Tất cả kết quả: " + results);

        // --- anyOf: Chờ cái nào xong trước ---
        CompletableFuture<Object> anyFuture = CompletableFuture.anyOf(f1, f2, f3);
        // anyOf trả về future của cái xong đầu tiên (f1 sau 1 giây)
        System.out.println("anyOf - Kết quả đầu tiên: " + anyFuture.join());
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}
```
*   **Giải thích:** `allOf` hữu ích khi bạn cần đợi một nhóm tác vụ song song hoàn tất trước khi làm việc gì đó tiếp theo. `anyOf` hữu ích khi bạn chỉ cần kết quả nhanh nhất từ một nhóm các tác vụ (ví dụ: gọi nhiều server dự phòng, lấy kết quả từ server phản hồi nhanh nhất).

#### 13. Xử lý ngoại lệ (Exception Handling)

*   **Lý thuyết:** `CompletableFuture` cung cấp các phương thức để xử lý lỗi xảy ra trong bất kỳ giai đoạn nào của pipeline.
    *   **`exceptionally(Function<Throwable, T>)`**: Chỉ được gọi khi có **ngoại lệ xảy ra**. Nó nhận ngoại lệ và trả về một **giá trị thay thế** (fallback).
    *   **`handle(BiFunction<T, Throwable, R>)`**: Được gọi **dù có lỗi hay không**. Nó nhận cả kết quả (nếu thành công) và ngoại lệ (nếu thất bại), cho phép bạn xử lý cả hai trường hợp một cách linh hoạt.

*   **Ví dụ code & Giải thích:**

```java
import java.util.concurrent.CompletableFuture;

public class ExceptionExample {
    public static void main(String[] args) {
        // --- exceptionally ---
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("Lỗi mạng!");
            return "Thành công";
        }).exceptionally(ex -> {
            System.out.println("exceptionally bắt được: " + ex.getMessage());
            return "Giá trị mặc định"; // Fallback
        });
        System.out.println("Kết quả future1: " + future1.join()); // In ra: Giá trị mặc định

        // --- handle ---
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("Lỗi xử lý!");
            return "Thành công";
        }).handle((result, ex) -> {
            if (ex != null) {
                System.out.println("handle bắt được lỗi: " + ex.getMessage());
                return "Đã xử lý lỗi";
            } else {
                return "Kết quả: " + result;
            }
        });
        System.out.println("Kết quả future2: " + future2.join()); // In ra: Đã xử lý lỗi
    }
}
```
*   **Giải thích:** Trong `future1`, `exceptionally` được kích hoạt do có lỗi và trả về `"Giá trị mặc định"`. Trong `future2`, `handle` nhận được `result` (null) và `ex` (ngoại lệ), và trả về `"Đã xử lý lỗi"`.

Hy vọng phần giải thích chi tiết này giúp bạn hiểu rõ về `CompletableFuture`. Nếu cần đi sâu hơn vào bất kỳ phần nào, bạn cứ hỏi nhé.


Chào bạn, cảm ơn bạn đã gửi chuỗi 12 file code Demo từ `Demo1` đến `Demo12`. Đây là những ví dụ minh họa cực kỳ chi tiết và bám sát vào lý thuyết trong slide Udemy mà bạn đã gửi ở tin nhắn trước.

Dưới đây, mình sẽ giải thích chi tiết từng file: **Lý thuyết cốt lõi**, **Code**, và **Giải thích chi tiết từng dòng logic** bằng tiếng Việt.

---

### 📁 Demo1.java - Khởi tạo bất đồng bộ cơ bản (`runAsync`)

*   **Lý thuyết:** `runAsync` nhận vào một `Runnable` (không trả về kết quả). Nó sẽ thực thi tác vụ trên một luồng (thread) khác. Nếu không truyền `Executor`, nó dùng `ForkJoinPool.commonPool()`. Nếu truyền `Executor`, nó dùng thread pool do bạn chỉ định.
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   **Khối 1:** Gọi `CompletableFuture.runAsync(...)`. Nó in ra tên thread hiện tại. Bạn sẽ thấy tên bắt đầu bằng `ForkJoinPool.commonPool-worker-...`. Lệnh `TimeUnit.SECONDS.sleep(2)` được dùng để giữ luồng `main` lại một chút, đảm bảo tác vụ bất đồng bộ kịp in ra màn hình trước khi chương trình kết thúc.
    *   **Khối 2:** Tạo `ExecutorService es = Executors.newCachedThreadPool();`. Lần này, truyền `es` vào làm tham số thứ hai cho `runAsync`. Khi chạy, tên thread in ra sẽ là `pool-X-thread-Y` (do thread pool tạo ra), chứng minh tác vụ chạy trên luồng tùy chỉnh. Cuối cùng gọi `es.shutdown()` để dọn dẹp tài nguyên.

---

### 📁 Demo2.java - Chủ động hoàn thành Future (`complete`)

*   **Lý thuyết:** Phương thức `complete(value)` cho phép bạn tự đặt kết quả cho một `CompletableFuture` từ bên ngoài. Nó hữu ích khi bạn muốn giới hạn thời gian chờ (timeout) hoặc bọc các API callback cũ.
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   Hàm `calculateAsync` tạo một `CompletableFuture` rỗng. Nó dùng `ExecutorService` để chạy một tác vụ giả lập gọi server mất **5 giây**.
    *   Tuy nhiên, ngay sau đó, lệnh `es.awaitTermination(2, TimeUnit.SECONDS)` chỉ đợi tối đa **2 giây**. Vì tác vụ cần 5 giây nên nó chưa xong.
    *   Do `future.isDone()` trả về `false`, khối `else` được chạy. Lệnh `completableFuture.complete(DEFAULT_PRICE)` được gọi để "chốt" kết quả luôn là `9.99` mà không cần đợi tác vụ kia chạy xong. Đây là kỹ thuật cực hay để xử lý timeout trong lập trình mạng.

---

### 📁 Demo3.java - Khởi tạo bất đồng bộ có trả về (`supplyAsync`)

*   **Lý thuyết:** `supplyAsync` nhận vào một `Supplier` (một hàm không tham số, có trả về kết quả) và trả về `CompletableFuture<T>`.
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   Rất đơn giản: `CompletableFuture.supplyAsync(() -> "Hello")` tạo một luồng chạy ngầm trả về chuỗi `"Hello"`. Lệnh `.get()` sẽ chặn luồng `main` cho đến khi có kết quả và in ra `"Hello"`.

---

### 📁 Demo4.java - Biến đổi kết quả (`thenApply` vs `thenApplyAsync`)

*   **Lý thuyết:**
    *   `thenApply(Function)`: Nhận kết quả, biến đổi nó và trả về kết quả mới. Nó chạy **cùng luồng** với tác vụ trước đó (hoặc luồng gọi nó).
    *   `thenApplyAsync(Function)`: Tương tự nhưng chạy trên **một luồng khác** (từ common pool hoặc Executor được chỉ định).
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   **Khối 1:** `supplyAsync` lấy User ID (`1234`). `thenApply` nhận `1234` và trả về `150` (số lượng mua hàng). Cả hai bước này chạy tuần tự. Kết quả in ra là `150`.
    *   **Khối 2:** Tương tự, nhưng dùng `thenApplyAsync` và truyền vào `ExecutorService es`. Bước lấy User ID chạy ở một luồng, bước tính toán số lượng chạy ở một luồng khác trong pool, giúp tận dụng tối đa sức mạnh đa luồng.

---

### 📁 Demo5.java - Tiêu thụ kết quả (`thenAccept`)

*   **Lý thuyết:** `thenAccept(Consumer)` nhận kết quả nhưng **không trả về gì cả** (trả về `CompletableFuture<Void>`). Dùng khi bạn chỉ muốn in ra, lưu vào database, hoặc gửi mail.
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   `supplyAsync` trả về "Some String...". `thenAccept` nhận chuỗi đó và in ra màn hình. Vì không trả về gì, `future.get()` ở ngoài cùng sẽ in ra `null`. Tương tự, bản `Async` sẽ chạy trên luồng của `es`.

---

### 📁 Demo6.java - Chạy tác vụ tiếp theo (`thenRun`)

*   **Lý thuyết:** `thenRun(Runnable)` **không cần biết** kết quả của bước trước là gì. Nó chỉ đơn giản là chạy một hành động (không trả về) sau khi bước trước hoàn thành.
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   Bước 1 dùng `runAsync` (không trả về) để giả lập lưu profile. Bước 2 dùng `thenRun` để in ra "User Profile Saved". Nó không quan tâm bước 1 trả về cái gì (vì bước 1 vốn dĩ không trả về gì). `future.get()` in ra `null`.

---

### 📁 Demo7.java - Làm phẳng Future (`thenCompose` vs `thenApply`)

*   **Lý thuyết:** Khi hàm xử lý của bạn trả về một `CompletableFuture` khác, nếu dùng `thenApply`, bạn sẽ bị lồng `CompletableFuture<CompletableFuture<T>>`. Để làm phẳng (flatten) nó thành `CompletableFuture<T>`, bạn phải dùng `thenCompose` (giống như `flatMap` trong Stream).
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   **Khối 1 (Dùng thenApply):** `getUserDetailById` trả về `CompletableFuture<String>`. `.thenApply(user -> getCreditRating(user))` sẽ trả về `CompletableFuture<CompletableFuture<Double>>`. Do đó, để lấy giá trị thật, bạn phải gọi `result.get().get()` (get 2 lần).
    *   **Khối 2 (Dùng thenCompose):** `thenCompose` nhận kết quả là `String`, nhưng hàm `getCreditRating` trả về `CompletableFuture<Double>`. `thenCompose` sẽ tự động "làm phẳng" để `result2` chỉ là `CompletableFuture<Double>`. Bạn chỉ cần gọi `result2.get()` một lần.

---

### 📁 Demo8.java - Kết hợp hai Future độc lập (`thenCombine`)

*   **Lý thuyết:** `thenCombine(otherFuture, BiFunction)` dùng khi bạn có 2 tác vụ độc lập chạy song song, và bạn chỉ muốn kết hợp kết quả của chúng khi **cả hai** đều hoàn thành.
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   `getWeight()` và `getHeight()` đều giả lập mất 1 giây. Chúng được gọi độc lập, nên chúng chạy **song song** cùng lúc (tổng thời gian ~1 giây, không phải 2 giây).
    *   `thenCombine` chờ cả hai xong, lấy `weightInKg` và `heightInCm`, tính BMI và trả về kết quả.

---

### 📁 Demo9.java - Kết hợp và tiêu thụ (`thenAcceptBoth`)

*   **Lý thuyết:** Giống `thenCombine` (chờ cả 2 hoàn thành), nhưng `thenAcceptBoth` trả về `CompletableFuture<Void>` (chỉ tiêu thụ kết quả, không trả về giá trị mới).
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   Hai tác vụ `supplyAsync` trả về "First" và " Second". `thenAcceptBoth` kết hợp chúng lại (in ra "First Second") và kết thúc. Vì trả về `Void` nên `future.get()` in ra `null`.

---

### 📁 Demo10.java - Chờ tất cả hoàn thành (`allOf`)

*   **Lý thuyết:** `allOf(futures...)` nhận vào nhiều Future. Nó trả về một `CompletableFuture<Void>` chỉ hoàn thành khi **tất cả** các Future truyền vào đều hoàn thành.
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   Tạo danh sách các chuỗi `"a", "b", "c"`. Dùng Stream để biến mỗi chuỗi thành một `CompletableFuture` (in hoa chuỗi đó, nhưng thời gian chờ là ngẫu nhiên từ 0-1000ms).
    *   `CompletableFuture.allOf(...)` sẽ chờ cho đến khi cả 3 tác vụ (với thời gian ngẫu nhiên) chạy xong hết.
    *   Sau khi `allOf` xong, hàm `whenComplete` được gọi. Lúc này chắc chắn 100% các future con đã có kết quả, nên dùng `cf.getNow(null)` để lấy kết quả mà không bị block (chặn).

---

### 📁 Demo11.java - Chờ một cái hoàn thành (`anyOf`)

*   **Lý thuyết:** `anyOf(futures...)` trả về `CompletableFuture<Object>`. Nó hoàn thành ngay khi **bất kỳ** Future nào trong danh sách hoàn thành (nhanh nhất).
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   Tương tự Demo10, nhưng dùng `anyOf`. Nó sẽ không đợi cả 3, mà chỉ đợi cái nào chạy nhanh nhất (ngẫu nhiên 0-1000ms). Kết quả in ra sẽ chỉ là một chữ cái in hoa (A, B, hoặc C) tùy thuộc vào tác vụ nào có `randomSleep` ngắn nhất.

---

### 📁 Demo12.java - Xử lý ngoại lệ (`handle` vs `exceptionally`)

*   **Lý thuyết:**
    *   `handle(BiFunction)`: Được gọi **bất kể** thành công hay thất bại. Nó nhận cả `result` và `exception`.
    *   `exceptionally(Function)`: Chỉ được gọi khi có **lỗi xảy ra**. Nó nhận `exception` và trả về giá trị dự phòng (fallback).
*   **Code:** (Như bạn đã gửi)
*   **Giải thích chi tiết:**
    *   Cả 2 khối đều cố tình gây lỗi `i /= 0` (chia cho 0) bên trong `supplyAsync`, ném ra `ArithmeticException`.
    *   **Khối 1 (`handle`):** Bắt được lỗi, in ra `Exception happend...` và `Exception is caused by...`. Tuy nhiên, nó cố gắng trả về `result`. Vì có lỗi xảy ra nên `result` là `null`. Do đó `future.get()` in ra `null`.
    *   **Khối 2 (`exceptionally`):** Bắt được lỗi, in ra thông báo lỗi, và trả về giá trị mặc định là `10`. Do đó `future2.get()` in ra `10`.

---
**Tổng kết:** Chuỗi 12 file Demo của bạn là một lộ trình hoàn hảo từ cơ bản đến nâng cao của `CompletableFuture`. Bạn có thể thấy sự tiến hóa từ việc tạo luồng đơn giản (`runAsync`, `supplyAsync`), xử lý kết quả (`thenApply`, `thenAccept`, `thenRun`), kết hợp (`thenCompose`, `thenCombine`, `allOf`, `anyOf`) cho đến xử lý lỗi (`handle`, `exceptionally`). Bạn có thắc mắc nào thêm về đoạn code nào không?