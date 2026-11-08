Chào bạn, theo yêu cầu của bạn, tôi sẽ giải thích chi tiết hai hình ảnh, sau đó đi sâu vào từng lý thuyết trong lộ trình học kèm ví dụ code và giải thích chi tiết, tất cả bằng tiếng Việt.

---

### 🖼️ Phân tích chi tiết hai hình ảnh

**Hình 1: Lộ trình học (Agenda)**

Hình ảnh là slide mở đầu cho bài học về **Lock API** trong Java, với logo Java ở bên phải. Nội dung liệt kê các chủ đề sẽ được đề cập:
- **What is Lock API**: Định nghĩa Lock API là gì.
- **Advantages of Lock API**: Những ưu điểm so với cơ chế `synchronized` truyền thống.
- **Synchronized blocks VS Locks**: So sánh chi tiết giữa hai cơ chế.
- **Lock interface overview**: Tổng quan về interface `Lock`.
- **ReentrantLock**: Lớp triển khai phổ biến nhất của `Lock`.
- **Lock fairness**: Cơ chế công bằng (fairness) của khóa.
- **Conditions**: Các đối tượng `Condition` để quản lý luồng chờ.
- **Lock interface**: (Nhắc lại) Các phương thức của interface `Lock`.
- **ReadWriteLock interface**: Interface cho phép nhiều luồng đọc cùng lúc.
- **ReentrantReadWriteLock class**: Lớp triển khai `ReadWriteLock`.
- **StampedLock**: Lớp khóa được giới thiệu từ Java 8, tối ưu cho hiệu năng cao.

Phụ đề trong hình nhấn mạnh: *"And we'll discuss such important interfaces as Lock and with Right Look, once we learned interfaces"* (sẽ thảo luận về các interface quan trọng như `Lock` và `ReadWriteLock` sau khi đã học về interface).

**Hình 2: Ưu điểm của Lock API**

Hình ảnh là slide trình bày chi tiết về **Ưu điểm của Lock API**. Bên trái là hình minh họa những người chạy đua và một người đi xe đạp, tượng trưng cho sự linh hoạt và hiệu quả. Bên phải là danh sách các ưu điểm:
1.  **Lock may be obtained and released in different places**: Khóa có thể được lấy và giải phóng ở những nơi khác nhau (không bắt buộc trong cùng một khối lệnh).
2.  **Time limit to acquire lock to avoid deadlocks**: Có thể đặt giới hạn thời gian để lấy khóa, giúp tránh deadlock.
3.  **Work with conditions**: Có thể hoạt động với các điều kiện (`Condition`).
4.  **Fairness for threads that are waiting for lock longer than other threads**: Có cơ chế công bằng cho các luồng chờ khóa lâu hơn.
5.  **Lock API allows us to interrupt thread when it's waiting for the lock**: Cho phép ngắt luồng khi nó đang chờ khóa.

Phụ đề trong hình: *"To put it simple, it is easy to explain why we might want to use look API sometimes and how it is different"* (Nói một cách đơn giản, rất dễ giải thích lý do tại sao đôi khi chúng ta có thể muốn sử dụng Lock API và nó khác biệt như thế nào).

---

### 📚 Lý thuyết và Ví dụ chi tiết

#### 1. Lock API là gì? Tổng quan Interface `Lock`

**Lý thuyết:** `Lock` là một interface trong gói `java.util.concurrent.locks`. Nó cung cấp các thao tác khóa linh hoạt hơn nhiều so với từ khóa `synchronized`. `synchronized` hoạt động theo cơ chế "block-structured" (có cấu trúc khối): khóa được lấy khi bắt đầu khối và tự động giải phóng khi thoát khối. `Lock` cho phép bạn kiểm soát hoàn toàn việc lấy và giải phóng khóa, mở ra nhiều khả năng mới.

**Các phương thức cốt lõi của `Lock`:**
- `void lock()`: Lấy khóa. Nếu khóa đang bị giữ bởi luồng khác, luồng hiện tại sẽ bị block cho đến khi lấy được.
- `void lockInterruptibly() throws InterruptedException`: Lấy khóa nhưng có thể bị ngắt bởi luồng khác.
- `boolean tryLock()`: Thử lấy khóa ngay lập tức. Trả về `true` nếu thành công, `false` nếu thất bại (không block).
- `boolean tryLock(long time, TimeUnit unit) throws InterruptedException`: Thử lấy khóa trong một khoảng thời gian nhất định.
- `void unlock()`: Giải phóng khóa. **Bắt buộc phải gọi trong khối `finally`**.
- `Condition newCondition()`: Tạo một đối tượng `Condition` liên kết với khóa này.

**Ví dụ code cơ bản với `ReentrantLock`:**

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockExample {
    private final Lock lock = new ReentrantLock();
    private int count = 0;

    public void increment() {
        lock.lock(); // Lấy khóa
        try {
            count++;
            System.out.println(Thread.currentThread().getName() + " tăng count lên: " + count);
        } finally {
            lock.unlock(); // Giải phóng khóa trong finally để đảm bảo luôn được thực thi
        }
    }
}
```

**Giải thích code:**
- `private final Lock lock = new ReentrantLock();`: Tạo một đối tượng khóa. `ReentrantLock` là lớp triển khai phổ biến nhất.
- `lock.lock()`: Yêu cầu lấy khóa. Nếu một luồng khác đang giữ khóa, luồng này sẽ đợi.
- Khối `try-finally`: Đây là **mẫu chuẩn** khi dùng `Lock`. Các thao tác trên tài nguyên chung (như `count++`) được đặt trong `try`. Việc giải phóng khóa `lock.unlock()` **phải** nằm trong `finally` để đảm bảo khóa luôn được trả lại dù có ngoại lệ xảy ra.

---

#### 2. Ưu điểm của Lock API so với `synchronized`

**Lý thuyết:** Interface `Lock` khắc phục những hạn chế của `synchronized` bằng cách cung cấp các tính năng linh hoạt hơn.

**Ví dụ minh họa các ưu điểm:**

**a) Lấy và giải phóng khóa ở nơi khác nhau (Ưu điểm 1)**

```java
public class LockFlexibility {
    private final Lock lock = new ReentrantLock();

    public void methodA() {
        lock.lock(); // Lấy khóa ở đây
        // ... làm việc gì đó ...
    }

    public void methodB() {
        // ... làm việc gì đó ...
        lock.unlock(); // Giải phóng khóa ở một phương thức khác
    }
}
```
**Giải thích:** Với `synchronized`, bạn không thể làm điều này vì khóa gắn chặt với cấu trúc khối. `Lock` cho phép bạn lấy khóa ở `methodA` và giải phóng ở `methodB`, rất hữu ích cho các thuật toán phức tạp như "chain locking" (khóa nối tiếp).

**b) Giới hạn thời gian chờ để tránh Deadlock (Ưu điểm 2)**

```java
public class TryLockExample {
    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();

    public void doWork() throws InterruptedException {
        // Thử lấy lock1 trong 1 giây
        if (lock1.tryLock(1, TimeUnit.SECONDS)) {
            try {
                System.out.println(Thread.currentThread().getName() + " đã lấy được lock1");
                // Thử lấy lock2 trong 1 giây
                if (lock2.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " đã lấy được lock2");
                        // ... thực hiện công việc ...
                    } finally {
                        lock2.unlock();
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + " không lấy được lock2, thoát ra");
                }
            } finally {
                lock1.unlock();
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " không lấy được lock1, thoát ra");
        }
    }
}
```
**Giải thích:** Phương thức `tryLock(time, unit)` sẽ trả về `false` nếu không lấy được khóa trong thời gian quy định. Điều này cho phép luồng "từ bỏ" và giải phóng những khóa nó đang giữ, phá vỡ điều kiện "chờ vòng tròn" – nguyên nhân gây ra deadlock.

**c) Cho phép ngắt luồng đang chờ (Ưu điểm 5)**

```java
public class InterruptibleLockExample {
    private final Lock lock = new ReentrantLock();

    public void performTask() {
        try {
            lock.lockInterruptibly(); // Có thể bị ngắt khi đang chờ
            try {
                // ... công việc ...
                System.out.println(Thread.currentThread().getName() + " đang làm việc...");
                Thread.sleep(5000); // Giả sử công việc mất thời gian
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " đã bị ngắt khi đang chờ khóa!");
        }
    }
}
```
**Giải thích:** Nếu một luồng đang bị block tại `lock.lockInterruptibly()`, một luồng khác có thể gọi `thread.interrupt()` để "đánh thức" nó dậy. Luồng bị ngắt sẽ nhận được `InterruptedException` và có thể thoát ra một cách an toàn. Với `lock.lock()`, luồng sẽ bị block và không thể bị ngắt.

---

#### 3. ReentrantLock và Lock Fairness

**Lý thuyết:**
- **ReentrantLock (Khóa tái nhập):** Là lớp triển khai chính của `Lock`. "Tái nhập" có nghĩa là một luồng đã giữ khóa có thể lấy lại khóa đó nhiều lần mà không bị block. Mỗi lần lấy khóa, một bộ đếm nội bộ sẽ tăng lên. Luồng phải gọi `unlock()` tương ứng với số lần đã `lock()`.
- **Lock Fairness (Tính công bằng):** `ReentrantLock` có thể được cấu hình là **fair** (công bằng) hoặc **non-fair** (không công bằng).
    - **Fair Lock (`new ReentrantLock(true)`):** Các luồng đang chờ sẽ được cấp khóa theo thứ tự FIFO (vào trước ra trước). Điều này đảm bảo không có luồng nào bị "đói" (starvation), nhưng hiệu suất tổng thể thường thấp hơn.
    - **Non-fair Lock (`new ReentrantLock(false)` - mặc định):** Cho phép "chen ngang" (barging). Một luồng mới đến có thể lấy được khóa ngay cả khi có luồng khác đang chờ lâu hơn. Điều này giúp tăng thông lượng (throughput) nhưng có nguy cơ gây đói luồng.

**Ví dụ code:**

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FairnessExample {
    // Tạo một Fair Lock
    private final Lock fairLock = new ReentrantLock(true);

    public void accessResource() {
        fairLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " đã lấy được khóa (fair).");
            // Giả lập thời gian xử lý
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            fairLock.unlock();
        }
    }

    public static void main(String[] args) {
        FairnessExample example = new FairnessExample();
        // Tạo 5 luồng cùng tranh nhau khóa
        for (int i = 0; i < 5; i++) {
            new Thread(example::accessResource, "Thread-" + i).start();
        }
    }
}
```
**Giải thích code:**
- `new ReentrantLock(true)`: Tham số `true` kích hoạt chế độ fair. Các luồng sẽ lần lượt lấy được khóa theo đúng thứ tự chúng gọi `lock()`. Nếu dùng `new ReentrantLock()` (mặc định là `false`), thứ tự có thể bị thay đổi.

---

#### 4. Conditions (Điều kiện)

**Lý thuyết:** `Condition` là một công cụ mạnh mẽ để quản lý việc chờ đợi giữa các luồng, tương tự như `wait()`, `notify()`, `notifyAll()` của `Object` nhưng linh hoạt hơn nhiều. Mỗi `Condition` được liên kết với một `Lock`. Nó cho phép một luồng tạm thời nhường khóa và chờ cho đến khi một điều kiện nào đó được thỏa mãn bởi luồng khác.

**Các phương thức chính:**
- `await()`: Luồng hiện tại giải phóng khóa và chờ cho đến khi được "đánh thức". Tương tự `wait()`.
- `signal()`: "Đánh thức" một luồng đang chờ trên `Condition` này. Tương tự `notify()`.
- `signalAll()`: "Đánh thức" tất cả các luồng đang chờ. Tương tự `notifyAll()`.

**Ví dụ code (Mô hình Producer-Consumer):**

```java
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionExample {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int CAPACITY = 5;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition(); // Điều kiện hàng đợi chưa đầy
    private final Condition notEmpty = lock.newCondition(); // Điều kiện hàng đợi không trống

    public void produce(int value) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == CAPACITY) {
                System.out.println("Hàng đợi đầy, Producer đang chờ...");
                notFull.await(); // Chờ cho đến khi hàng đợi chưa đầy
            }
            queue.add(value);
            System.out.println("Producer đã thêm: " + value);
            notEmpty.signal(); // Đánh thức Consumer đang chờ hàng đợi có hàng
        } finally {
            lock.unlock();
        }
    }

    public void consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                System.out.println("Hàng đợi trống, Consumer đang chờ...");
                notEmpty.await(); // Chờ cho đến khi hàng đợi có hàng
            }
            int value = queue.poll();
            System.out.println("Consumer đã lấy: " + value);
            notFull.signal(); // Đánh thức Producer đang chờ hàng đợi chưa đầy
        } finally {
            lock.unlock();
        }
    }
}
```
**Giải thích code:**
- `notFull` và `notEmpty` là hai đối tượng `Condition` được tạo từ cùng một `lock`.
- **Producer:** Trước khi thêm hàng, nó kiểm tra nếu hàng đợi đầy (`queue.size() == CAPACITY`), nó sẽ gọi `notFull.await()` để nhường khóa và chờ. Sau khi thêm hàng, nó gọi `notEmpty.signal()` để báo cho Consumer.
- **Consumer:** Tương tự, nếu hàng đợi trống, nó chờ trên `notEmpty`. Sau khi lấy hàng, nó gọi `notFull.signal()` để báo cho Producer.
- Việc sử dụng `while` thay vì `if` là để chống lại "spurious wakeups" (thức giấc giả) – một hiện tượng hiếm gặp nhưng có thể xảy ra.

---

#### 5. ReadWriteLock và ReentrantReadWriteLock

**Lý thuyết:** `ReadWriteLock` là một interface duy trì một cặp khóa liên quan: một cho thao tác đọc (read-only) và một cho thao tác ghi (write).
- **Read Lock:** Nhiều luồng có thể giữ read lock cùng một lúc, miễn là không có luồng nào giữ write lock.
- **Write Lock:** Chỉ một luồng có thể giữ write lock, và khi đó, không luồng nào có thể giữ read lock hay write lock.

Điều này đặc biệt hiệu quả trong các ứng dụng **đọc nhiều, ghi ít** (read-heavy), vì nó cho phép nhiều luồng đọc song song, tăng đáng kể hiệu năng so với khóa độc quyền thông thường.

**Ví dụ code:**

```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private String data = "Dữ liệu ban đầu";

    // Thao tác ĐỌC: nhiều luồng có thể vào cùng lúc
    public String readData() {
        rwLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " đang đọc: " + data);
            Thread.sleep(500); // Giả lập thời gian đọc
            return data;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    // Thao tác GHI: độc quyền, chỉ một luồng được vào
    public void writeData(String newData) {
        rwLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " đang GHI...");
            Thread.sleep(1000); // Giả lập thời gian ghi
            data = newData;
            System.out.println(Thread.currentThread().getName() + " đã ghi xong: " + data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
```
**Giải thích code:**
- `readLock().lock()` được sử dụng cho thao tác đọc. Nhiều luồng có thể gọi phương thức này cùng lúc mà không bị block lẫn nhau (trừ khi có luồng đang ghi).
- `writeLock().lock()` được sử dụng cho thao tác ghi. Nó sẽ block tất cả các luồng khác, dù là đọc hay ghi, cho đến khi hoàn tất.

**Lưu ý:** `ReentrantReadWriteLock` cũng hỗ trợ **lock downgrading** (hạ cấp khóa): một luồng có thể lấy write lock, sau đó lấy read lock, rồi giải phóng write lock. Tuy nhiên, **không thể nâng cấp** từ read lock lên write lock (sẽ gây deadlock).

---

#### 6. StampedLock (Java 8+)

**Lý thuyết:** `StampedLock` là một lớp khóa được giới thiệu trong Java 8, cung cấp hiệu năng vượt trội so với `ReentrantReadWriteLock` trong nhiều trường hợp. Nó giới thiệu một khái niệm mới: **Optimistic Read (Đọc lạc quan)**.

- **Cơ chế hoạt động:** Mỗi thao tác lấy khóa sẽ trả về một `stamp` (một giá trị `long`). `stamp` này được dùng để giải phóng khóa hoặc để kiểm tra tính hợp lệ.
- **Ba chế độ:**
    1.  **Writing:** Tương tự write lock, độc quyền hoàn toàn. `long stamp = lock.writeLock();` và `lock.unlockWrite(stamp);`.
    2.  **Reading:** Tương tự read lock, chia sẻ. `long stamp = lock.readLock();` và `lock.unlockRead(stamp);`.
    3.  **Optimistic Reading (Đọc lạc quan):** Đây là điểm mạnh nhất. Phương thức `long stamp = lock.tryOptimisticRead();` **không** thực sự lấy khóa mà chỉ trả về một `stamp`. Bạn đọc dữ liệu, sau đó kiểm tra lại bằng `lock.validate(stamp)`. Nếu `validate` trả về `true`, dữ liệu bạn đọc là nhất quán (không có ai ghi trong lúc bạn đọc). Nếu trả về `false`, bạn cần thử lại bằng cách lấy read lock thực sự.

**Đặc điểm quan trọng:** `StampedLock` **không phải là reentrant** (không thể tái nhập). Nếu một luồng đã giữ khóa mà cố gắng lấy lại, nó sẽ bị deadlock. Nó cũng không hỗ trợ `Condition`.

**Ví dụ code (Đọc lạc quan):**

```java
import java.util.concurrent.locks.StampedLock;

public class StampedLockExample {
    private double x, y;
    private final StampedLock sl = new StampedLock();

    // Ghi độc quyền
    public void move(double deltaX, double deltaY) {
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    // Đọc lạc quan
    public double distanceFromOrigin() {
        long stamp = sl.tryOptimisticRead(); // 1. Đọc lạc quan
        double currentX = x;
        double currentY = y;

        // 2. Kiểm tra xem có ai đã ghi dữ liệu trong lúc đọc không
        if (!sl.validate(stamp)) {
            // Nếu có, phải lấy read lock thực sự và đọc lại
            stamp = sl.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        // 3. Tính toán trên dữ liệu đã đảm bảo nhất quán
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
```
**Giải thích code:**
1.  `tryOptimisticRead()` trả về một `stamp` mà không block.
2.  Đọc các biến `x` và `y` vào các biến cục bộ.
3.  `validate(stamp)` kiểm tra xem trong khoảng thời gian từ lúc lấy `stamp` đến giờ, có luồng nào đã ghi (lấy write lock) không.
4.  Nếu `validate` trả về `false`, nghĩa là dữ liệu có thể đã bị thay đổi, ta phải lấy `readLock()` một cách "bi quan" (pessimistic) để đảm bảo an toàn, rồi đọc lại.
5.  Nếu `validate` trả về `true`, dữ liệu đáng tin cậy và ta có thể sử dụng ngay mà không cần lấy khóa thực sự.

Hy vọng những giải thích và ví dụ này giúp bạn nắm rõ lộ trình và các khái niệm quan trọng về Lock API trong Java! Nếu cần đi sâu hơn vào phần nào, bạn cứ hỏi nhé.

Chào bạn, tôi đã xem qua 5 file code bạn gửi. Đây là những ví dụ rất kinh điển và tuyệt vời để hiểu sâu về **Lock API** trong Java. 

Tôi sẽ đi qua từng file, kết hợp giải thích lý thuyết, phân tích code chi tiết và chỉ ra những điểm cần lưu ý (đặc biệt là một lỗi tiềm ẩn trong file số 3).

---

### 1. `ReentrantLockDemo.java` - Cơ bản về ReentrantLock và so sánh với Synchronized

**Lý thuyết:**
*   **ReentrantLock** là một cơ chế khóa độc quyền (mutual exclusion) giống như `synchronized`. Tại một thời điểm, chỉ có một luồng được phép thực thi đoạn code được bảo vệ.
*   Khác với `synchronized` (tự động mở khóa khi ra khỏi block), `ReentrantLock` yêu cầu bạn phải **tự gọi `unlock()` trong khối `finally`** để đảm bảo khóa luôn được giải phóng ngay cả khi có Exception xảy ra.

**Giải thích Code:**
```java
public void incrementWithLock() {
    try {
        lock.lock(); // 1. Lấy khóa
        counter++;   // 2. Thao tác trên tài nguyên chung
    } finally {
        lock.unlock(); // 3. Luôn giải phóng khóa
    }
}
```
*   Ở hàm `main`, tạo một Thread Pool gồm 4 luồng. 
*   Sử dụng `IntStream.range(0, 10000)` để giao 10.000 tác vụ tăng biến `counter` cho 4 luồng này.
*   Nhờ có `lock.lock()`, dù 4 luồng chạy song song, tại một thời điểm chỉ có 1 luồng được tăng `counter`, ngăn chặn hoàn toàn hiện tượng **Race Condition** (tranh chấp dữ liệu). Kết quả in ra cuối cùng chắc chắn là `10000`.
*   Hàm `terminateExecutorService` thực hiện việc tắt Thread Pool một cách "lịch sự" (chờ tối đa 3 giây cho các task hoàn thành rồi mới ép buộc hủy).

---

### 2. `ReentrantLockDemo2.java` - Tính "Tái nhập" (Reentrancy)

**Lý thuyết:**
*   **Reentrancy (Tính tái nhập)** là khả năng một luồng đã giữ khóa có thể lấy lại chính khóa đó thêm nhiều lần nữa mà không bị block (kẹt).
*   `ReentrantLock` duy trì một bộ đếm (hold count). Mỗi lần gọi `lock()`, bộ đếm tăng lên 1. Mỗi lần gọi `unlock()`, bộ đếm giảm đi 1. Khóa thực sự được giải phóng khi bộ đếm về 0.

**Giải thích Code:**
```java
public double calculate(String operation, double operand1, double operand2) {
    try {
        lock.lock(); // Lần lấy khóa thứ 1
        // ...
        switch (operation) {
            case "+": return add(operand1, operand2); // Gọi hàm add
            // ...
        }
    } finally {
        lock.unlock();
    }
}

public double add(double value1, double value2) {
    try {
        lock.lock(); // Lần lấy khóa thứ 2 (cùng một luồng)
        // ...
        return value1 + value2;
    } finally {
        lock.unlock(); // Nhả khóa lần 2
    }
}
```
*   Khi luồng gọi hàm `calculate("+", ...)`, nó lấy được khóa.
*   Hàm `calculate` gọi hàm `add`. Hàm `add` lại gọi `lock.lock()`. Nếu không có tính tái nhập, luồng này sẽ bị block mãi mãi (Deadlock) vì nó đang đợi chính nó nhả khóa.
*   Nhờ tính tái nhập, luồng này được phép đi tiếp vào hàm `add`. Khi ra khỏi `add`, bộ đếm khóa giảm từ 2 về 1. Khi ra khỏi `calculate`, bộ đếm về 0, khóa được nhả hoàn toàn.

---

### 3. `ReentrantLockDemo3.java` - tryLock() và giới hạn thời gian (Có lỗi cần sửa)

**Lý thuyết:**
*   Phương thức `tryLock(time, unit)` cố gắng lấy khóa trong một khoảng thời gian nhất định. 
*   Nếu lấy được, trả về `true`. Nếu hết thời gian mà chưa lấy được, trả về `false`. Điều này giúp tránh việc luồng bị treo vô hạn (giảm thiểu nguy cơ Deadlock).

**Giải thích Code & CẢNH BÁO LỖI:**
Trong code của bạn:
```java
private static void doSomethingLong() {
    try {
        if (lock.tryLock(2, TimeUnit.SECONDS)) {
            System.out.println("lock is acquired: " + lock.isLocked());
            TimeUnit.SECONDS.sleep(3); // Giả lập xử lý lâu
        } else {
            System.out.println("Thread didn't acquired a lock: " + Thread.currentThread().getName());
        }
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        lock.unlock(); // <--- LỖI TIỀM ẨN Ở ĐÂY!
    }
}
```
*   **Kịch bản:** Luồng 1 lấy được khóa và ngủ 3 giây. Luồng 2 gọi `tryLock(2s)`. Sau 2 giây, luồng 2 không lấy được khóa, trả về `false`, in ra dòng chữ "Thread didn't acquired...".
*   **Lỗi:** Sau đó, luồng 2 chạy vào khối `finally` và gọi `lock.unlock()`. Tuy nhiên, **luồng 2 không hề sở hữu khóa này**. Việc gọi `unlock()` khi bạn không giữ khóa sẽ ném ra ngoại lệ **`IllegalMonitorStateException`**.
*   **Cách sửa:** Chỉ gọi `unlock()` khi bạn đã lấy được khóa thành công. Nên viết lại như sau:
```java
private static void doSomethingLong() {
    boolean isLocked = false;
    try {
        // Cố gắng lấy khóa
        isLocked = lock.tryLock(2, TimeUnit.SECONDS);
        if (isLocked) {
            System.out.println("lock is acquired...");
            TimeUnit.SECONDS.sleep(3);
        } else {
            System.out.println("Thread didn't acquired a lock...");
        }
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        if (isLocked) { // Chỉ nhả khóa nếu đã lấy được
            lock.unlock();
        }
    }
}
```

---

### 4. `ConditionDemo.java` - Biến điều kiện (Condition)

**Lý thuyết:**
*   `Condition` cung cấp khả năng một luồng tạm dừng thực thi (`await`) và nhường khóa cho luồng khác, cho đến khi một điều kiện nào đó được đáp ứng và được đánh thức (`signal` / `signalAll`).
*   Nó giống như mô hình **Producer - Consumer** (Sản xuất - Tiêu thụ).

**Giải thích Code:**
*   `stackEmptyCondition`: Điều kiện "Stack đang trống". Luồng Pop sẽ chờ điều kiện này.
*   `stackFullCondition`: Điều kiện "Stack đang đầy". Luồng Push sẽ chờ điều kiện này.
*   **Hàm `pushToStack` (Producer):** Nếu stack đầy (`size == CAPACITY`), nó gọi `stackFullCondition.await()` để nhường khóa và chờ. Khi có phần tử được thêm vào, nó gọi `stackEmptyCondition.signalAll()` để báo cho các luồng đang Pop rằng "này, có hàng rồi, đừng chờ nữa".
*   **Hàm `popFromStack` (Consumer):** Nếu stack trống (`size == 0`), nó gọi `stackEmptyCondition.await()` để chờ. Sau khi lấy được hàng, trong khối `finally`, nó gọi `stackFullCondition.signalAll()` để báo cho các luồng Push rằng "này, tôi vừa lấy 1 phần tử, có chỗ trống rồi, push tiếp đi".
*   *Lưu ý:* Sử dụng `while` thay vì `if` để kiểm tra điều kiện là một best-practice nhằm chống lại hiện tượng "spurious wakeup" (luồng tự thức giấc dù chưa có signal).

---

### 5. `ReentrantReadWriteLockDemo.java` - Khóa Đọc/Ghi

**Lý thuyết:**
*   Trong nhiều ứng dụng, tần suất **Đọc (Read)** lớn hơn rất nhiều so với **Ghi (Write)**. Nếu dùng khóa độc quyền (ReentrantLock) cho tất cả, các thao tác đọc sẽ bị block lẫn nhau, gây lãng phí tài nguyên.
*   `ReadWriteLock` giải quyết việc này bằng cách cung cấp 2 loại khóa:
    *   **Read Lock:** Nhiều luồng có thể đọc CÙNG LÚC (miễn là không có luồng nào đang Ghi).
    *   **Write Lock:** Độc quyền hoàn toàn. Khi một luồng đang Ghi, không ai được Đọc hay Ghi.

**Giải thích Code:**
```java
private Lock writeLock = readWriteLock.writeLock();
private Lock readLock = readWriteLock.readLock();
```
*   Các hàm thay đổi dữ liệu (`put`, `remove`) sử dụng `writeLock.lock()`. Điều này đảm bảo tính toàn vẹn dữ liệu của `HashMap` khi bị sửa đổi.
*   Các hàm chỉ đọc dữ liệu (`get`, `containsKey`) sử dụng `readLock.lock()`. 
*   **Ưu điểm:** Giả sử có 10 luồng cùng gọi hàm `get()`, cả 10 luồng này sẽ được chạy đồng thời cùng lúc (vì dùng readLock). Hiệu năng tăng vọt so với việc dùng ReentrantLock thông thường. Nếu có 1 luồng gọi `put()`, nó sẽ phải đợi tất cả các luồng đọc xong, và block các luồng đọc mới cho đến khi nó ghi xong.

---
**Tổng kết:** Bộ code bạn gửi bao quát toàn bộ các tính năng mạnh mẽ nhất của Lock API. Hãy chú ý sửa lại lỗi ở file `ReentrantLockDemo3.java` để code chạy an toàn nhé! Nếu bạn cần giải thích thêm phần nào, hãy cho tôi biết.