Chào bạn, dựa trên các hình ảnh bạn cung cấp từ khóa học **"Java from Zero to First Job"** trên Udemy, mình sẽ tổng hợp và giải thích chi tiết toàn bộ chủ đề **Synchronization Basics (Cơ bản về Đồng bộ hóa trong Java)**.

Bài viết sẽ đi sâu vào từng slide, giải thích lý thuyết, đưa ra ví dụ thực tế, kèm theo code minh họa và giải thích logic ngay bên cạnh code để bạn dễ hiểu nhất.

---

### SLIDE 1: AGENDA (Lộ trình bài học)
Slide này liệt kê các chủ đề chính chúng ta sẽ tìm hiểu:
*   **Critical section** (Vùng tới hạn)
*   **Atomic operations** (Hoạt động nguyên tử)
*   **Basic synchronization example** (Ví dụ cơ bản về đồng bộ)
*   **Monitor & Mutex** (Cơ chế giám sát và Khóa loại trừ tương hỗ)
*   **synchronized keyword, blocks** (Từ khóa và khối đồng bộ)
*   **wait(), notify(), notifyAll()** (Các phương thức giao tiếp giữa các luồng)

---

### SLIDE 2: CRITICAL SECTION (Vùng tới hạn)

**Lý thuyết:**
*   **Critical Section** là một đoạn code trong chương trình mà tại đó, các **Thread (luồng)** cùng truy cập và thay đổi một **tài nguyên dùng chung** (shared resource) như biến, file, database...
*   Nếu nhiều thread cùng bước vào vùng này một lúc, dữ liệu sẽ bị sai lệch (Race Condition). Do đó, tại một thời điểm, **chỉ duy nhất một thread được phép thực thi bên trong Critical Section**.
*   *Ví dụ thực tế:* Nhà vệ sinh chỉ có 1 buồng. 3 người (3 threads) cùng muốn vào. Bắt buộc phải xếp hàng, người này ra thì người kia mới được vào. Cái cửa nhà vệ sinh chính là cơ chế đồng bộ để bảo vệ "vùng tới hạn" bên trong.

**Minh họa Code (Race Condition):**
```java
class SharedData {
    int counter = 0;

    // Đây là Critical Section
    public void increment() {
        counter++; 
    }
}

public class CriticalSectionExample {
    public static void main(String[] args) throws InterruptedException {
        SharedData data = new SharedData();

        // Thread 1 tăng counter 10000 lần
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) data.increment();
        });

        // Thread 2 tăng counter 10000 lần
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) data.increment();
        });

        t1.start(); t2.start();
        t1.join(); t2.join();

        // Kết quả mong đợi: 20000. Kết quả thực tế: Thường nhỏ hơn 20000 (VD: 18732)
        System.out.println("Counter: " + data.counter); 
    }
}
```
* **Giải thích bên cạnh code:** Hàm `increment()` là Critical Section vì cả `t1` và `t2` đều gọi nó để thay đổi biến `counter`. Vì `counter++` không phải là thao tác an toàn (xem slide 4), dẫn đến việc ghi đè dữ liệu của nhau, làm mất lượt tăng (Lost Update).

---

### SLIDE 3 & 4 & 5: ATOMIC vs NON-ATOMIC OPERATIONS (Hoạt động Nguyên tử và Phi nguyên tử)

**Lý thuyết:**
*   **Atomic Operation (Hoạt động nguyên tử):** Là thao tác được thực hiện trong **một bước duy nhất, không thể bị ngắt quãng**. Nó thành công hoàn toàn hoặc thất bại hoàn toàn. (Ví dụ: Gán `a = 1;`, đọc giá trị từ mảng, sử dụng các class trong package `java.util.concurrent.atomic`).
*   **Non-Atomic Operation (Hoạt động phi nguyên tử):** Như slide 4 minh họa, phép toán `i++` (tăng i lên 1) **KHÔNG** phải là 1 bước. Nó bao gồm 3-4 bước:
    1. Đọc giá trị của `i` từ bộ nhớ.
    2. Cộng thêm 1.
    3. Lưu kết quả vào bộ nhớ.
    4. Gán giá trị cho biến.
*   *Hậu quả:* Nếu Thread A đang làm bước 2, Thread B chen vào làm bước 1, dữ liệu sẽ bị hỏng.

**Minh họa Code (Giải pháp với Atomic):**
```java
import java.util.concurrent.atomic.AtomicInteger;

class AtomicExample {
    // Sử dụng AtomicInteger thay vì int thường
    AtomicInteger atomicCounter = new AtomicInteger(0); 

    public void increment() {
        // Phương thức này là ATOMIC (nguyên tử) ở mức phần cứng
        atomicCounter.incrementAndGet(); 
    }
}

public class AtomicDemo {
    public static void main(String[] args) throws InterruptedException {
        AtomicExample data = new AtomicExample();
        
        Thread t1 = new Thread(() -> { for (int i = 0; i < 10000; i++) data.increment(); });
        Thread t2 = new Thread(() -> { for (int i = 0; i < 10000; i++) data.increment(); });
        
        t1.start(); t2.start();
        t1.join(); t2.join();
        
        // Kết quả LUÔN LUÔN chính xác là 20000.
        System.out.println("Atomic Counter: " + data.atomicCounter.get()); 
    }
}
```
* **Giải thích bên cạnh code:** Thay vì dùng `int`, ta dùng `AtomicInteger`. Hàm `incrementAndGet()` đảm bảo rằng toàn bộ quá trình "đọc - cộng - ghi" diễn ra tức thời như một khối nguyên tử (nhờ cơ chế CAS - Compare And Swap), không thread nào có thể chen ngang vào giữa được.

---

### SLIDE 6 & 7: MUTEX & MONITOR DEFINITION (Định nghĩa Khóa và Cơ chế giám sát)

**Lý thuyết:**
*   **Mutex (MUTual EXclusion - Loại trừ tương hỗ):** Hiểu đơn giản là một cái **Ổ khóa (Lock)**. Thread nào muốn vào Critical Section phải "chiếm" (capture) khóa, làm xong phải "nhả" (release) khóa. Nếu khóa đang bị thread khác giữ, thread hiện tại phải chờ (block).
*   **Monitor (Cơ chế giám sát):** Là một khái niệm bậc cao hơn. Nó không chỉ chứa Mutex (để đảm bảo độc quyền) mà còn cung cấp thêm khả năng **chờ đợi (wait)** và **đánh thức (signal/notify)** các thread khác khi một điều kiện nào đó thay đổi.
*   **Ví dụ Slide 7 (Chiếc xe Audi):**
    *   Chiếc xe = Tài nguyên chung (Critical Section).
    *   Bạn (Me) và Bạn gái (My girlfriend) = Các Thread.
    *   Chìa khóa xe = **Mutex**. Chỉ một người cầm chìa khóa (Capturing) tại một thời điểm. Người kia phải đợi đến khi chìa khóa được nhả ra (Releasing).
    *   **Monitor** là người quản lý bãi xe thông minh: Quản lý việc giao chìa khóa, và có một phòng chờ (Wait Set) để bảo bạn gái ngồi đợi uống nước cho đến khi bạn trả xe xong thì người quản lý sẽ gọi (Notify) cô ấy ra lấy xe.

**Minh họa Code (Mutex vs Monitor trong Java):**
```java
import java.util.concurrent.locks.ReentrantLock;

class CarSharing {
    private ReentrantLock mutex = new ReentrantLock(); // Đây là MUTEX

    public void drive(String person) {
        mutex.lock(); // Chiếm khóa (Capturing)
        try {
            System.out.println(person + " đang lái xe...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(person + " đã trả xe.");
            mutex.unlock(); // Nhả khóa (Releasing)
        }
    }
}
```
* **Giải thích bên cạnh code:** `ReentrantLock` đóng vai trò là một **Mutex** thuần túy. Bạn phải tự gọi `lock()` và `unlock()`. Nếu không có `unlock()` trong khối `finally`, hệ thống sẽ bị deadlock (kẹt xe mãi mãi).

---

### SLIDE 8: MONITOR VS MUTEX

**Lý thuyết:**
*   **Mutex** thường là cơ chế cấp thấp (Low-level), phụ thuộc vào hệ điều hành (OS), lập trình viên phải quản lý lock/unlock thủ công (dễ quên gây deadlock).
*   **Monitor** là cơ chế cấp cao (High-level), thường được cung cấp bởi chính Ngôn ngữ lập trình hoặc Framework (như Java). Java sử dụng từ khóa `synchronized` để hiện thực hóa Monitor. Lập trình viên không cần tự lock/unlock, JVM sẽ tự động làm việc đó cho bạn.

**Minh họa Code (Sử dụng Monitor qua `synchronized`):**
```java
class SharedMonitor {
    int counter = 0;

    // Từ khóa synchronized biến hàm này thành một Monitor
    public synchronized void increment() {
        counter++; 
        // Không cần gọi lock() hay unlock(). JVM tự động cấp khóa khi thread vào hàm
        // và tự động nhả khóa khi thread chạy xong hàm.
    }
}
```
* **Giải thích bên cạnh code:** Khi một thread gọi hàm `increment()`, JVM sẽ tự động khóa (Monitor Lock) đối tượng `SharedMonitor` lại. Các thread khác gọi hàm này sẽ bị đưa vào trạng thái chờ. Khi thread đầu tiên chạy xong, JVM tự nhả khóa và đánh thức thread tiếp theo.

---

### CÁC CHỦ ĐỀ CÒN LẠI TRONG AGENDA: `synchronized`, `wait()`, `notify()`

**Lý thuyết:**
*   **Synchronized block:** Đôi khi ta không muốn khóa cả hàm (vì hàm có thể dài và có những đoạn không cần khóa), ta chỉ khóa một đoạn code nhỏ.
*   **wait():** Thread đang giữ khóa của Monitor sẽ tạm nhả khóa ra và đi ngủ (chờ đợi).
*   **notify() / notifyAll():** Đánh thức một hoặc tất cả các thread đang ngủ (`wait`) dậy để tiếp tục tranh khóa.

**Minh họa Code (Mô hình Producer - Consumer đơn giản):**
```java
class DataBuffer {
    private int data;
    private boolean hasData = false;

    // Producer: Tạo dữ liệu
    public synchronized void produce(int value) throws InterruptedException {
        while (hasData) {
            wait(); // Nếu có dữ liệu rồi thì nhả khóa và đợi (Monitor quản lý)
        }
        data = value;
        System.out.println("Sản xuất: " + data);
        hasData = true;
        notify(); // Đánh thức Consumer đang đợi
    }

    // Consumer: Tiêu thụ dữ liệu
    public synchronized void consume() throws InterruptedException {
        while (!hasData) {
            wait(); // Nếu chưa có dữ liệu thì nhả khóa và đợi
        }
        System.out.println("Tiêu thụ: " + data);
        hasData = false;
        notify(); // Đánh thức Producer đang đợi
    }
}
```
* **Giải thích bên cạnh code:** Cả 2 hàm đều dùng chung 1 Monitor (từ khóa `synchronized`). Hàm `produce` thấy `hasData = true` thì gọi `wait()` để nhường khóa cho Consumer. Hàm `consume` lấy xong dữ liệu, gọi `notify()` để báo cho Producer biết là "dữ liệu đã được lấy, mày có thể sản xuất tiếp". Đây là cốt lõi của giao tiếp đa luồng trong Java.

---

### TỔNG KẾT
*   **Critical Section:** Nơi nguy hiểm cần bảo vệ.
*   **Atomic:** Làm 1 phát ăn luôn, không bị ngắt.
*   **Mutex:** Cái ổ khóa thủ công.
*   **Monitor:** Cái ổ khóa tự động thông minh (Java dùng `synchronized`).
*   Nếu bạn hiểu rõ các khái niệm này, bạn đã nắm được 80% cốt lõi của lập trình đa luồng (Multithreading) trong Java! Chúc bạn học tốt khóa học trên Udemy.


Chào bạn, dựa trên 4 file code bạn cung cấp (`Demo.java`, `Message.java`, `Reader.java`, `Notifier.java`), đây là một ví dụ kinh điển và cực kỳ trực quan về **Giao tiếp giữa các luồng (Inter-thread Communication)** trong Java, sử dụng các khái niệm vừa học: **Monitor, `synchronized`, `wait()`, và `notifyAll()`**.

Dưới đây là phần phân tích chi tiết lý thuyết song hành cùng code và giải thích luồng chạy của chương trình.

---

### 1. Phân tích chi tiết từng File (Lý thuyết & Code)

#### File 1: `Message.java` (Tài nguyên dùng chung - Shared Resource / Monitor Object)
```java
package synchronization_basics;

public class Message {
    private String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```
*   **Lý thuyết bên cạnh code:** Lớp này đơn giản chỉ là một POJO (Plain Old Java Object) chứa dữ liệu là một chuỗi (`String message`). Tuy nhiên, trong ngữ cảnh đa luồng, đối tượng `Message` này đóng vai trò là **Monitor Object (Đối tượng giám sát)**. Các luồng (Thread) sẽ dùng chính đối tượng này làm "chìa khóa" (khóa nội tại - intrinsic lock) để đồng bộ hóa và giao tiếp với nhau.

#### File 2: `Reader.java` (Luồng chờ đợi - Sử dụng `wait()`)
```java
package synchronization_basics;

public class Reader implements Runnable {
    private Message message;

    public Reader(Message m) {
        this.message = m;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        
        // CRITICAL SECTION: Khóa đối tượng message lại
        synchronized (message) { 
            try {
                System.out.println(name + " waiting to get notified at time:" + System.currentTimeMillis());
                
                // LÝ THUYẾT: message.wait() 
                // 1. Thread hiện tại (READER) sẽ nhả khóa (unlock) của đối tượng message.
                // 2. Thread đi vào trạng thái WAITING (ngủ) trong Wait Set của Monitor.
                // 3. Chờ cho đến khi có thread khác gọi notify/notifyAll trên CÙNG đối tượng message.
                message.wait(); 
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Đoạn code này chỉ chạy SAU KHI thread được đánh thức và giành lại được khóa
            System.out.println(name + " waiter thread got notified at time:" + System.currentTimeMillis());
            System.out.println(name + " processed: " + message.getMessage());
        }
    }
}
```
*   **Lý thuyết bên cạnh code:**
    *   Bắt buộc phải gọi `wait()` bên trong khối `synchronized(message)`. Nếu không, JVM sẽ ném ra lỗi `IllegalMonitorStateException` vì thread chưa sở hữu Monitor thì không có quyền ra lệnh cho Monitor đưa mình vào trạng thái chờ.
    *   Khi gọi `message.wait()`, thread READER **nhả khóa ngay lập tức**. Điều này rất quan trọng, nó cho phép thread NOTIFIER có thể lấy được khóa để vào làm việc.

#### File 3: `Notifier.java` (Luồng đánh thức - Sử dụng `notifyAll()`)
```java
package synchronization_basics;

import java.util.concurrent.TimeUnit;

public class Notifier implements Runnable {
    private Message message;

    public Notifier(Message m) {
        this.message = m;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println(name + " started");
        try {
            // Giả lập thời gian xử lý để đảm bảo các Reader đã vào trạng thái wait() trước
            TimeUnit.MILLISECONDS.sleep(100); 
            
            // CRITICAL SECTION: Khóa đối tượng message lại
            synchronized (message) { 
                message.setMessage(name + " Notifier work done");
                
                // LÝ THUYẾT: notifyAll() đánh thức TẤT CẢ các thread đang nằm trong Wait Set của đối tượng message.
                // (Nếu dùng message.notify(), nó chỉ đánh thức 1 thread ngẫu nhiên).
                message.notifyAll(); 
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Notifier finished work");
    }
}
```
*   **Lý thuyết bên cạnh code:**
    *   Tương tự, `notifyAll()` cũng phải được gọi trong khối `synchronized(message)`.
    *   Khi gọi `notifyAll()`, nó không nhả khóa ngay lập tức. Nó chỉ đánh dấu các thread đang chờ là "đã được đánh thức" (chuyển từ trạng thái WAITING sang BLOCKED). Thread NOTIFIER vẫn tiếp tục giữ khóa cho đến khi chạy ra khỏi khối `synchronized`. Lúc đó, các Reader mới thực sự có cơ hội tranh nhau lấy lại khóa để chạy tiếp.

#### File 4: `Demo.java` (Chương trình chính)
```java
package synchronization_basics;

public class Demo {
    public static void main(String[] args) throws InterruptedException {
        // 1. Tạo đối tượng tài nguyên dùng chung (Monitor)
        Message message = new Message("process it");
        
        // 2. Khởi tạo và chạy 2 luồng Reader
        Reader reader1 = new Reader(message);
        new Thread(reader1, "READER 1").start();

        Reader reader2 = new Reader(message);
        new Thread(reader2, "READER 2").start();

        // 3. Khởi tạo và chạy luồng Notifier
        Notifier notifier = new Notifier(message);
        new Thread(notifier, "NOTIFIER").start();
        
        System.out.println("All the threads are started");
    }
}
```
*   **Lý thuyết:** Tất cả các thread (`READER 1`, `READER 2`, `NOTIFIER`) đều được truyền **cùng một đối tượng `message`**. Đây là điều kiện tiên quyết để chúng có thể giao tiếp được với nhau qua Monitor của đối tượng này.

---

### 2. Kịch bản chạy thực tế (Step-by-Step Timeline)

Hãy tưởng tượng câu chuyện diễn ra như sau:

1.  **Khởi động:** Main thread tạo 3 thread và start. Thứ tự chạy giữa các thread là không xác định (do hệ điều hành quyết định), nhưng thường các Reader sẽ chạy trước vì được start trước.
2.  **READER 1 vào:** `READER 1` chiếm được khóa của `message`, in ra dòng chữ "waiting...", sau đó gọi `message.wait()`. Lập tức `READER 1` nhả khóa và đi ngủ.
3.  **READER 2 vào:** `READER 2` thấy khóa đã được nhả, nó chiếm khóa, in ra dòng chữ "waiting...", gọi `message.wait()`. `READER 2` cũng nhả khóa và đi ngủ.
4.  **NOTIFIER thức giấc:** `NOTIFIER` bị trễ 100ms (do lệnh `sleep`), nên chắc chắn 2 Reader đã ngủ. `NOTIFIER` chiếm khóa của `message`, cập nhật dữ liệu (`setMessage`), và gọi `message.notifyAll()`.
5.  **Đánh thức:** `notifyAll()` đánh thức cả `READER 1` và `READER 2`. Cả hai chuyển từ trạng thái ngủ sang trạng thái "chờ lấy khóa" (Blocked).
6.  **NOTIFIER kết thúc:** `NOTIFIER` chạy ra khỏi khối `synchronized`, nhả khóa. In ra "Notifier finished work".
7.  **Cuộc chiến tranh khóa:** `READER 1` và `READER 2` tỉnh dậy, cả hai lao vào tranh nhau khóa của `message`. Giả sử `READER 1` nhanh chân hơn:
    *   `READER 1` lấy được khóa, in ra thông báo "got notified", in ra thông điệp mới, rồi chạy ra khỏi khối `synchronized` và nhả khóa.
    *   `READER 2` lúc này mới lấy được khóa, in ra thông báo "got notified", in ra thông điệp mới, rồi kết thúc.

**Kết quả in ra màn hình (có thể xê dịch một chút về thời gian):**
```text
All the threads are started
READER 1 waiting to get notified at time:1699...
READER 2 waiting to get notified at time:1699...
NOTIFIER started
Notifier finished work
READER 1 waiter thread got notified at time:1699...
READER 1 processed: NOTIFIER Notifier work done
READER 2 waiter thread got notified at time:1699...
READER 2 processed: NOTIFIER Notifier work done
```

### 3. Tổng kết lý thuyết cốt lõi từ ví dụ này

*   **Tại sao dùng `notifyAll()` thay vì `notify()`?** Trong ví dụ này có 2 Reader cùng chờ. Nếu dùng `notify()`, JVM chỉ đánh thức 1 thread (ví dụ READER 1). READER 2 sẽ bị "kẹt" (ngủ vĩnh viễn) vì không có ai gọi đánh thức nó nữa. Dùng `notifyAll()` đảm bảo an toàn cho mọi thread đang chờ.
*   **Tại sao `wait()` phải nằm trong `synchronized`?** Vì `wait()` thao tác trực tiếp lên Monitor của đối tượng (đưa thread vào Wait Set). Bạn không thể điều khiển một cái khóa mà bạn không sở hữu.
*   **Trạng thái của Thread:**
    *   Khi gọi `wait()`: Thread chuyển sang `WAITING`.
    *   Khi bị gọi `notifyAll()`: Thread chuyển sang `BLOCKED` (chờ lấy lại khóa).
    *   Khi lấy lại được khóa: Thread chuyển sang `RUNNABLE` và tiếp tục chạy các dòng code sau lệnh `wait()`.

Đoạn code của bạn là một ví dụ hoàn hảo để thực hành mô hình **Producer - Consumer** (Sản xuất - Tiêu thụ) ở mức độ cơ bản nhất!