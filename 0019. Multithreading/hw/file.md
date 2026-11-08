Mình hiểu bạn đã gửi **6 file Java** (`Task1.java` → `Task5_2.java`), không phải hình ảnh. Mình sẽ giải thích từng file như từng “hình ảnh” nội dung: **lý thuyết → code minh họa có chú thích → giải thích chi tiết → lưu ý**. Toàn bộ dùng tiếng Việt.

---

# 0. Lý thuyết nền tảng cần nắm trước

## 0.1 Thread là gì?
- **Process**: chương trình đang chạy, có bộ nhớ riêng.
- **Thread**: luồng thực thi bên trong process, chia sẻ bộ nhớ với các thread khác.
- Tạo thread trong Java có nhiều cách:
  1. `class X implements Runnable` → `new Thread(new X())`
  2. `class X extends Thread` → `new X()`
  3. Lambda: `new Thread(() -> {...})`
  4. Method reference: `new Thread(ClassName::method)`

## 0.2 `start()` vs `run()`
- `start()`: tạo thread mới, JVM gọi `run()` trong thread mới.
- `run()`: chỉ là lời gọi hàm bình thường, không tạo thread mới.

## 0.3 `sleep`, `interrupt`
- `Thread.sleep(ms)`: thread ngủ, có thể bị đánh thức bởi `interrupt()`.
- `interrupt()`: đặt cờ interrupt; nếu thread đang `sleep/wait/join` thì ném `InterruptedException`.

## 0.4 Race condition – critical section
- Nhiều thread cùng đọc/ghi biến chia sẻ → kết quả sai.
- Đoạn code truy cập tài nguyên chia sẻ gọi là **critical section**.
- Công cụ bảo vệ: `synchronized`, `Lock`, `AtomicInteger`, `volatile` (chỉ visibility, không đủ cho `++`).

## 0.5 `wait/notifyAll`, `Condition`
- `wait()`: nhả lock, chờ đến khi được `notify/notifyAll`.
- `await()/signalAll()`: tương tự với `ReentrantLock` + `Condition`.
- Luôn gọi trong khi giữ lock; nên kiểm tra điều kiện trong vòng `while`.

## 0.6 `ExecutorService`, `Future`, `CountDownLatch`
- `ExecutorService`: quản lý pool thread.
- `Callable<T>`: như Runnable nhưng trả về `T`.
- `Future<T>`: đại diện kết quả tương lai; `get()` sẽ block đến khi có kết quả.
- `CountDownLatch`: bộ đếm; thread gọi `await()` chờ đến khi bộ đếm về 0; mỗi thread gọi `countDown()`.

---

# 1. `Task1.java` – Tạo và chạy thread in tên mỗi giây

## 1.1 Nội dung file
Tạo 4 thread in tên thread mỗi giây:
1. `Task implements Runnable`
2. `DefaultThread extends Thread`
3. Lambda truyền vào `Thread`
4. Method reference `Task1::printThreadName`

Main ngủ 5 giây rồi `t1.interrupt()`.

## 1.2 Code minh họa có chú thích lý thuyết

```java
// Cách 1: Runnable object
class Task implements Runnable {
    @Override
    public void run() {
        while (true) {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(1); // ngủ 1 giây
            } catch (InterruptedException e) {
                break; // bị interrupt thì thoát vòng lặp
            }
        }
    }
}

// Cách 2: kế thừa Thread
class DefaultThread extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

public class Task1 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Task());          // Runnable
        Thread t2 = new DefaultThread();             // extends Thread

        // Cách 3: lambda cho Runnable
        Thread t3 = new Thread(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        // Cách 4: method reference, printThreadName() không tham số, void
        Thread t4 = new Thread(Task1::printThreadName);

        t1.start(); // tạo thread thật
        t2.start();
        t3.start();
        t4.start();

        TimeUnit.SECONDS.sleep(5); // main ngủ 5 giây
        t1.interrupt();            // chỉ đánh thức t1
    }

    private static void printThreadName() {
        while (true) {
            System.out.println(Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
```

## 1.3 Giải thích chi tiết
- `new Thread(new Task())`: `Task` là `Runnable`, `Thread` sẽ gọi `Task.run()`.
- `DefaultThread extends Thread`: ghi đè `run()`, bản thân object là thread.
- Lambda: `Runnable` là functional interface (chỉ 1 method `run()`), nên lambda dùng được.
- Method reference: `Task1::printThreadName` tương đương `() -> Task1.printThreadName()`.
- `Thread.currentThread().getName()`: lấy tên thread hiện tại, ví dụ `Thread-0`.
- `t1.interrupt()` chỉ tác động `t1`. Trong file gốc, `t2`, `t3`, `t4` vẫn chạy vô hạn → chương trình không kết thúc. Muốn dừng hết phải giữ tham chiếu và interrupt tất cả, hoặc đặt `setDaemon(true)`.

## 1.4 Lưu ý
- `sleep` ném `InterruptedException`; catch và `break` để kết thúc thread.
- Không gọi `t1.run()` vì sẽ chạy trên main thread, không tạo thread mới.

---

# 2. `Task2.java` – Spam message, dừng khi nhấn Enter

## 2.1 Nội dung
- `Spam` nhận 2 mảng: `latency[]` (ms) và `messages[]`.
- Mỗi lần: ngủ `latency[i]` rồi in `messages[i]`.
- Một thread `Listener` đọc `System.in`; nếu đọc được `'\n'` thì `interrupt()` thread Spam.
- Main tạo `ByteArrayInputStream` chứa `'\n'`, sau 5 giây `System.setIn(bais)` để giả lập nhấn Enter.

## 2.2 Code minh họa

```java
private static class Spam implements Runnable {
    private int[] latency;
    private String[] messages;

    public Spam(int[] latency, String[] messages) {
        if (latency.length != messages.length) {
            throw new IllegalArgumentException("Arrays length mismatch");
        }
        this.latency = latency;
        this.messages = messages;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < messages.length; i++) {
                TimeUnit.MILLISECONDS.sleep(latency[i]); // ngủ theo ms
                System.out.println(messages[i]);         // in message
            }
        } catch (InterruptedException ex) {
            // bị listener interrupt -> dừng
        }
    }

    static class Listener implements Runnable {
        private Thread thread;
        public static final InputStream IN_DEFAULT = System.in;

        public Listener(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // available() > 0: có dữ liệu trong input
                    if (System.in.available() > 0 && System.in.read() == '\n') {
                        System.setIn(IN_DEFAULT); // khôi phục System.in
                        thread.interrupt();       // dừng Spam
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    public void startSpam() {
        Thread spam = new Thread(this);
        Thread listener = new Thread(new Listener(spam));

        spam.start();
        listener.setDaemon(true); // listener là daemon
        listener.start();
    }
}
```

Main:

```java
ByteArrayInputStream bais = new ByteArrayInputStream(new byte[]{'\n'});
int[] latency = {1000, 1000, 1000, 1000, 1000, 1000};
String[] messages = {"First", "Second", "Third", "Fourth", "Fifth", "Sixth"};

Spam spam = new Spam(latency, messages);
spam.startSpam();

TimeUnit.SECONDS.sleep(5);
System.setIn(bais); // giả lập nhấn Enter sau 5 giây
```

## 2.3 Giải thích
- `latency.length != messages.length` → không biết in message nào với delay nào → ném exception.
- `Spam.run()` ngủ trước rồi in; bị interrupt trong `sleep` → thoát.
- `Listener` chạy song song, kiểm tra `System.in.available() > 0`; nếu có ký tự `'\n'` thì interrupt Spam.
- `System.setIn(bais)` thay đổi input stream toàn cục; listener đọc từ stream mới.
- `listener.setDaemon(true)`: listener không giữ JVM sống; khi Spam kết thúc, main kết thúc, JVM có thể thoát.
- Nhược điểm: vòng `while(true)` + `available()` là busy-wait, tốn CPU. Có thể dùng `BufferedReader.readLine()`.

---

# 3. `Task3.java` – Hai counter, synchronized vs không synchronized

## 3.1 Nội dung
- `Task3 implements Runnable`, có `counter1`, `counter2`.
- `run()`: in `counter1 == counter2`, `counter1++`, ngủ 10ms, `counter2++`, ngủ 10ms.
- Main tạo 2 thread dùng chung 1 object `Task3`, chạy 3 giây rồi interrupt.
- So sánh khi có `synchronized(this)` và không.

## 3.2 Code minh họa

```java
public class Task3 implements Runnable {
    public int counter1 = 0;
    public int counter2 = 0;

    @Override
    public void run() {
        try {
            // synchronized (this) { // mở comment để bảo vệ critical section
            while (true) {
                System.out.println(counter1 + " == " + counter2);
                counter1++;                         // ghi biến chia sẻ
                TimeUnit.MILLISECONDS.sleep(10);    // nhường CPU
                counter2++;                         // ghi biến chia sẻ
                TimeUnit.MILLISECONDS.sleep(10);
            }
            // }
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Task3 obj = new Task3();
        Thread t1 = new Thread(obj);
        Thread t2 = new Thread(obj); // cùng object -> cùng counter

        t1.start();
        t2.start();

        TimeUnit.SECONDS.sleep(3);
        t1.interrupt();
        t2.interrupt();
    }
}
```

## 3.3 Giải thích
- Hai thread cùng dùng `obj` → cùng `counter1`, `counter2`.
- Không synchronized: có thể xảy ra:
  - Thread A in `0 == 0`
  - A `counter1++` → 1
  - B in `1 == 0`
  - B `counter1++` → 2
  - A `counter2++` → 1
  → console có thể in `1 == 0`, `2 == 1`…
- Đây là **race condition** vì `++` không atomic (đọc → cộng → ghi).
- `synchronized(this)` tạo monitor lock trên object. Chỉ 1 thread vào block tại một thời điểm.
- **Lưu ý trong file gốc**: nếu bọc cả `while(true)` trong `synchronized(this)`, thread đầu giữ lock mãi, thread 2 không bao giờ vào. Chỉ nên sync đoạn nhỏ:
  ```java
  synchronized (this) {
      counter1++;
      counter2++;
  }
  ```
  Nhưng nếu có `sleep` giữa 2 lần tăng, muốn luôn bằng nhau phải sync cả cặp tăng không sleep bên trong.
- `interrupt()` đánh thức `sleep`, ném `InterruptedException`, thoát.

---

# 4. `Task4_1.java` – Tìm max ma trận bằng `Future`

## 4.1 Nội dung
- Tạo ma trận `4 x 100` số nguyên ngẫu nhiên.
- Mỗi hàng do 1 thread tìm max; mỗi phép so sánh ngủ 1ms để mô phỏng so sánh object lớn.
- So sánh thời gian multithreading và single-thread.
- Dùng `Callable<Integer>` + `ExecutorService.invokeAll()` + `Future`.

## 4.2 Lý thuyết
- `Callable<T>` có `call()` trả về `T`, có thể ném exception.
- `Future<T>`: `get()` block đến khi có kết quả.
- `invokeAll(Collection<Callable>)`: chạy tất cả, trả về `List<Future>`, chờ tất cả hoàn thành.
- 4 hàng chạy song song: mỗi hàng 100 cột, delay 1ms/cột → ~100ms/hàng. Tuần tự ~400ms.

## 4.3 Code chính

```java
public int getMaxElementInMatrixMultiThreading() throws InterruptedException, ExecutionException {
    Callable<Integer>[] arrFinders = createFindersForArray(matrix);

    long start = System.nanoTime();

    ExecutorService es = Executors.newCachedThreadPool();
    List<Future<Integer>> futures = es.invokeAll(Arrays.asList(arrFinders));
    this.futuresList.addAll(futures);
    es.shutdown();

    int max = futures.get(0).get(); // lấy kết quả hàng 0
    for (Future<Integer> future : futures) {
        if (future.get() > max) {   // get() block đến khi có kết quả
            max = future.get();
        }
    }

    long finish = System.nanoTime();
    timeOfMultiThreadingSearch = (finish - start) / 1_000_000;
    return max;
}

private int findMaxInRow(int row) throws InterruptedException {
    int maxInRow = matrix[row][0];
    for (int j = 1; j < matrix[row].length; j++) {
        TimeUnit.MILLISECONDS.sleep(1); // mô phỏng so sánh tốn thời gian
        if (maxInRow < matrix[row][j]) {
            maxInRow = matrix[row][j];
        }
    }
    return maxInRow;
}

private class Finder implements Callable<Integer> {
    private int threadId;

    public Finder(int threadId) {
        this.threadId = threadId;
    }

    @Override
    public Integer call() throws Exception {
        return findMaxInRow(threadId); // mỗi thread tìm max 1 hàng
    }
}
```

## 4.4 Giải thích
- `createFindersForArray` tạo 4 `Finder`, mỗi `Finder` ứng với 1 hàng.
- `invokeAll` chạy 4 `Callable` song song trên pool.
- `future.get()` lấy max từng hàng; so sánh để ra max toàn ma trận.
- Thời gian multithreading ~ thời gian hàng chậm nhất (~100ms) + overhead.
- Single-thread gọi `findMaxInRow` lần lượt 4 hàng → ~400ms.
- `es.shutdown()` giải phóng pool.

---

# 5. `Task4_2.java` – Tìm max ma trận bằng `CountDownLatch`

## 5.1 Nội dung
Cùng bài toán Task4_1 nhưng dùng:
- `Runnable` thay `Callable`
- `CountDownLatch` thay `Future`
- `CopyOnWriteArrayList` để lưu max từng hàng.

## 5.2 Code chính

```java
private CountDownLatch countDownLatch;
private List<Integer> maximumNumbersInEachRow;

public SearchEngine(int[][] matrix) {
    this.matrix = matrix;
    maximumNumbersInEachRow = new CopyOnWriteArrayList<>(); // thread-safe list
    countDownLatch = new CountDownLatch(matrix.length);     // đếm = số hàng
}

public int getMaxElementInMatrixMultiThreading() throws InterruptedException {
    Runnable[] arrFinders = createFindersForArray(matrix);

    long start = System.nanoTime();
    runFinders(arrFinders);

    countDownLatch.await(10, TimeUnit.SECONDS); // chờ đến khi count = 0

    int max = maximumNumbersInEachRow.stream().mapToInt(i -> i).max().getAsInt();
    long finish = System.nanoTime();
    timeOfMultiThreadingSearch = (finish - start) / 1_000_000;
    return max;
}

private class Finder implements Runnable {
    private int threadId;

    public Finder(int threadId) {
        this.threadId = threadId;
    }

    @Override
    public void run() {
        int maxOfThread = Integer.MIN_VALUE;
        try {
            maxOfThread = findMaxInRow(threadId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        maximumNumbersInEachRow.add(maxOfThread); // thêm max hàng
        countDownLatch.countDown();               // giảm bộ đếm
    }
}
```

## 5.3 Giải thích
- `CountDownLatch(matrix.length)` tạo bộ đếm 4.
- Mỗi `Finder` chạy xong: thêm max vào list, gọi `countDown()`.
- Main `await(10, SECONDS)` chờ đến khi cả 4 thread xong hoặc timeout 10s.
- `CopyOnWriteArrayList` an toàn khi nhiều thread cùng `add`.
- So với `Future`: `Future` lấy kết quả trực tiếp qua `get()`, còn `CountDownLatch` chỉ báo “đã xong”, kết quả lưu ở list chia sẻ.
- `CountDownLatch` không reset; dùng 1 lần.

---

# 6. `Task5_1.java` – Reader/Writer với `synchronized` + dummy object

## 6.1 Nội dung
- 1 Writer, 3 Reader.
- Buffer `StringBuilder` dài 5 ký tự.
- Writer ghi message; khi buffer đầy, writer chờ readers đọc hết.
- Reader đọc cùng một message; khi chưa có message thì chờ.
- 3 chu kỳ.
- Dùng `synchronized(MONITOR)` + `wait/notifyAll`.

## 6.2 Lý thuyết
- `MONITOR` là object “dummy” dùng làm lock.
- `wait()`: nhả lock, chờ notify.
- `notifyAll()`: đánh thức tất cả thread đang wait trên MONITOR.
- Biến chia sẻ: `isWrittenToBuffer`, `readersRead` (`volatile` để đảm bảo visibility).
- `readersRead`: số reader chưa đọc? Trong code là đếm số reader đã đọc; writer reset = 0 sau khi ghi.

## 6.3 Code chính

```java
private static final Object MONITOR = new Object();
private static boolean isWrittenToBuffer;
private static volatile int readersRead = READERS_NUMBER;

// Writer
@Override
public void run() {
    int tact = 0;
    while (!stop) {
        try {
            synchronized (MONITOR) {
                write(); // ghi buffer
                while (isWrittenToBuffer) {
                    MONITOR.wait(); // chờ readers đọc hết
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (++tact == ITERATION_NUMBER) stop = true;
        }
    }
}

// Reader
@Override
public void run() {
    while (!stop) {
        if (readersRead == 0) { // chưa có reader nào đọc message hiện tại
            try {
                synchronized (MONITOR) {
                    read(getName()); // đọc buffer
                    if (readersRead == READERS_NUMBER) {
                        isWrittenToBuffer = false;
                        MONITOR.notifyAll(); // báo writer
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

`read`/`write`:

```java
private static void read(String threadName) throws InterruptedException {
    System.out.printf("Reader %s:", threadName);
    for (int j = 0; j < BUFFER_LENGTH; j++) {
        Thread.sleep(PAUSE);
        System.out.print(BUFFER.charAt(j));
    }
    System.out.println();
    readersRead++; // tăng số reader đã đọc
}

private static void write() throws InterruptedException {
    BUFFER.setLength(0); // xóa buffer
    System.err.print("Writer writes:");
    Random random = new Random();
    for (int j = 0; j < BUFFER_LENGTH; j++) {
        Thread.sleep(PAUSE);
        char ch = (char) ('A' + random.nextInt(26));
        System.err.print(ch);
        BUFFER.append(ch);
    }
    System.err.println();
    isWrittenToBuffer = true;
    readersRead = 0; // reset số reader đã đọc
}
```

## 6.4 Giải thích
- Writer vào `synchronized(MONITOR)`, ghi buffer, đặt `isWrittenToBuffer = true`, `readersRead = 0`.
- Sau đó writer `while(isWrittenToBuffer) MONITOR.wait()` → nhả lock, chờ.
- Reader vào `synchronized(MONITOR)`, đọc buffer, `readersRead++`.
- Khi `readersRead == 3`, reader cuối đặt `isWrittenToBuffer = false` và `notifyAll()` → writer tỉnh, tiếp tục chu kỳ sau.
- Vì tất cả reader đọc cùng `BUFFER` nên in cùng message.
- `volatile readersRead` giúp các thread thấy giá trị mới.
- **Hạn chế**: điều kiện `if (readersRead == 0)` nằm ngoài synchronized; nên kiểm tra lại trong `while` để tránh spurious wakeup/race. Code demo mức cơ bản.

---

# 7. `Task5_2.java` – Reader/Writer với `ReentrantLock` + `Condition`

## 7.1 Nội dung
Cùng bài toán Task5_1 nhưng dùng:
- `ReentrantLock`
- `Condition notEmpty`, `Condition isRead`
- `lock()/unlock()`, `await()/signalAll()`

## 7.2 Code chính

```java
private static Lock lock = new ReentrantLock();
private static Condition notEmpty = lock.newCondition();
private static Condition isRead = lock.newCondition();

// Reader
@Override
public void run() {
    while (!stop) {
        try {
            lock.lock();
            read(getName()); // đọc buffer
            if (++readersRead == READERS_NUMBER) {
                notEmpty.signalAll(); // báo writer: readers đã đọc xong
            }
            isRead.await(); // chờ writer ghi message mới
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock(); // luôn mở lock
        }
    }
}

// Writer
@Override
public void run() {
    int tact = 0;
    while (!stop) {
        try {
            lock.lock();
            write(); // ghi buffer
            readersRead = 0;
            isRead.signalAll(); // báo readers có message mới
            notEmpty.await();   // chờ readers đọc hết
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (++tact == ITERATION_NUMBER) {
                isRead.signalAll();
                stop = true;
            }
            lock.unlock();
        }
    }
}
```

## 7.3 Giải thích
- `lock.lock()`: giữ lock; `lock.unlock()` trong `finally` để tránh deadlock.
- `Condition.await()`: thread nhả lock và chờ; khi được `signal/signalAll`, nó phải giành lại lock rồi mới tiếp tục.
- `notEmpty` dùng cho Writer chờ “readers đọc xong” (dù tên hơi ngược).
- `isRead` dùng cho Reader chờ “writer ghi message mới”.
- Writer: ghi → `readersRead=0` → `isRead.signalAll()` đánh thức reader → `notEmpty.await()` chờ.
- Reader: đọc → tăng `readersRead`; nếu đủ 3 → `notEmpty.signalAll()` đánh thức writer → `isRead.await()` chờ message tiếp.
- **Lưu ý**: nên dùng `while` kiểm tra điều kiện thay vì `if`; cần đảm bảo writer ghi trước reader đọc (trong main writer start trước nhưng không đảm bảo scheduling). Code demo có thể gặp race nếu reader vào trước khi buffer có dữ liệu.
- `stop` nên `volatile` để đảm bảo visibility.

---

# 8. Bảng so sánh nhanh

| Nội dung | Cách 1 | Cách 2 |
|---|---|---|
| Task1 | Runnable/Thread/lambda/method ref | — |
| Task2 | Spam + Listener đọc `System.in` | — |
| Task3 | Không sync → race | `synchronized(this)` → mutual exclusion |
| Task4 | `Future` + `invokeAll` | `CountDownLatch` + list thread-safe |
| Task5 | `synchronized` + `wait/notifyAll` | `ReentrantLock` + `Condition` |

---

# 9. Kết luận ngắn
- `Task1` minh họa 4 cách tạo thread; nhớ `start()` không phải `run()`.
- `Task2` minh họa thread phụ theo dõi input và `interrupt` thread khác.
- `Task3` minh họa race condition và vai trò `synchronized`.
- `Task4` minh họa lợi ích multithreading khi mỗi phần tử có delay; `Future` và `CountDownLatch` là 2 cách đồng bộ kết quả.
- `Task5` minh họa producer–consumer: Writer/Reader, buffer, `wait/notifyAll` và `Lock/Condition`.

Nếu bạn muốn, mình có thể tiếp tục **giải thích từng dòng chi tiết của một file cụ thể** (ví dụ `Task5_2.java`) hoặc **sửa các lỗi tiềm ẩn** trong code demo để chạy an toàn hơn.