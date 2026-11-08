Chào bạn, tôi đã nhận được yêu cầu của bạn. Tuy nhiên, tôi **không thấy bất kỳ hình ảnh nào** được đính kèm trong cuộc trò chuyện này. Có thể bạn đã quên gửi, hoặc có lỗi khi tải lên. Nếu bạn gửi lại hình ảnh, tôi sẽ rất vui lòng giải thích chi tiết từng hình.

Trong khi chờ đợi, tôi sẽ giải thích cặn kẽ 5 công cụ đồng bộ hóa (synchronizers) quan trọng nhất trong Java `java.util.concurrent`: **CountDownLatch, CyclicBarrier, Semaphore, Exchanger và Phaser**. Tôi sẽ đi từ lý thuyết cốt lõi, đến ví dụ code chi tiết, và giải thích từng dòng code để bạn nắm chắc bản chất.

---

### 🧭 Tổng quan: Khi nào dùng cái nào?

Trước khi đi sâu, đây là bảng tóm tắt nhanh để bạn định hình vai trò của từng công cụ:

| Công cụ | Mục đích chính | Đặc điểm cốt lõi |
| :--- | :--- | :--- |
| **CountDownLatch** | Chờ đợi sự kiện | **Một lần**. Chờ cho đến khi bộ đếm về 0. |
| **CyclicBarrier** | Đồng bộ điểm hẹn | **Có thể tái sử dụng**. Chờ đủ `N` luồng rồi tất cả cùng chạy tiếp. |
| **Semaphore** | Giới hạn truy cập | **Đếm số lượng "giấy phép"**. Giới hạn số luồng được làm việc cùng lúc. |
| **Exchanger** | Trao đổi dữ liệu | **Điểm hẹn của 2 luồng**. Hai luồng gặp nhau và hoán đổi dữ liệu. |
| **Phaser** | Đồng bộ đa giai đoạn | **Linh hoạt nhất**. Như CyclicBarrier nhưng số luồng có thể thay đổi. |

---

### 1. CountDownLatch: "Người gác cổng chờ tín hiệu"

#### 📖 Lý thuyết dễ hiểu
Hãy tưởng tượng bạn là một **nhóm trưởng** cần chờ 5 thành viên trong nhóm hoàn thành báo cáo trước khi bạn có thể tổng hợp lại. Bạn không cần biết ai làm nhanh ai làm chậm, chỉ cần biết **khi nào cả 5 người báo "Xong"**.

`CountDownLatch` hoạt động y hệt vậy. Nó có một **bộ đếm (counter)** được khởi tạo với một con số `N`. 
- Một hoặc nhiều luồng gọi `await()` để **chặn (block)** lại, chờ đợi.
- Các luồng khác, sau khi hoàn thành công việc, gọi `countDown()` để **giảm bộ đếm đi 1**.
- Khi bộ đếm về `0`, "cánh cổng mở", tất cả các luồng đang chờ ở `await()` được đánh thức và tiếp tục chạy.

**Điểm mấu chốt:** `CountDownLatch` là **dùng một lần**. Một khi bộ đếm về 0, nó không thể reset lại được. Nếu muốn lặp lại, bạn phải tạo một đối tượng mới.

#### 💻 Ví dụ code: Cuộc đua ngựa chờ trọng tài

Đoạn code dưới đây mô phỏng 5 chú ngựa phải chờ 3 trọng tài vào vị trí trước khi cuộc đua bắt đầu.

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchDemo {
    public static void main(String[] args) {
        // Khởi tạo bộ đếm là 3 (chờ 3 trọng tài)
        CountDownLatch latch = new CountDownLatch(3); 

        // Tạo 5 luồng "Ngựa"
        for (int i = 0; i < 5; i++) {
            new Thread(new Horse("Ngựa-" + i, latch)).start();
        }
        // Tạo 3 luồng "Trọng tài"
        for (int i = 0; i < 3; i++) {
            new Thread(new Judge("Trọng tài-" + i, latch)).start();
        }
    }

    static class Horse implements Runnable {
        private final String name;
        private final CountDownLatch latch;
        Horse(String name, CountDownLatch latch) { this.name = name; this.latch = latch; }

        @Override
        public void run() {
            try {
                System.out.println(name + " đã sẵn sàng, chờ trọng tài...");
                latch.await(); // Chặn tại đây cho đến khi bộ đếm về 0
                System.out.println(name + " XUẤT PHÁT!");
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    static class Judge implements Runnable {
        private final String name;
        private final CountDownLatch latch;
        Judge(String name, CountDownLatch latch) { this.name = name; this.latch = latch; }

        @Override
        public void run() {
            try {
                Thread.sleep((long) (Math.random() * 3000)); // Giả lập thời gian vào vị trí
                System.out.println(name + " đã vào vị trí.");
                latch.countDown(); // Giảm bộ đếm đi 1
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
```

#### 🔍 Giải thích code và liên hệ lý thuyết
- `CountDownLatch latch = new CountDownLatch(3);`: Tạo "cánh cổng" cần **3 tín hiệu** để mở. Đây là con số `N` trong lý thuyết.
- Trong `Horse.run()`, `latch.await()` chính là hành động **chặn**. Tất cả 5 chú ngựa sẽ đứng yên ở đây, không chạy tiếp cho đến khi nhận đủ 3 tín hiệu.
- Trong `Judge.run()`, `latch.countDown()` là hành động **gửi tín hiệu**. Mỗi trọng tài khi vào vị trí sẽ giảm bộ đếm đi 1.
- **Liên hệ:** Khi trọng tài thứ 3 gọi `countDown()`, bộ đếm từ 1 về 0. Lập tức, cả 5 luồng `Horse` đang bị chặn ở `await()` đồng loạt được "thả" ra và in ra "XUẤT PHÁT!".

---

### 2. CyclicBarrier: "Điểm hẹn đồng đội có thể tái sử dụng"

#### 📖 Lý thuyết dễ hiểu
Hãy tưởng tượng một nhóm bạn đi du lịch. Họ quy ước: **"Cứ đủ 4 người thì cả nhóm mới được lên xe buýt đi tiếp"**. Nếu mới có 3 người đến điểm hẹn, người thứ 3 phải đứng chờ. Khi người thứ 4 đến, **cả 4 người cùng lên xe**. Điều đặc biệt là sau chặng đó, họ lại có thể lặp lại quy tắc này cho chặng tiếp theo.

`CyclicBarrier` (rào chắn chu kỳ) hoạt động như vậy. Nó chặn các luồng tại một **điểm hẹn (barrier point)** cho đến khi đủ một số lượng luồng `N` đã đăng ký. Khi luồng cuối cùng đến, **tất cả các luồng được giải phóng cùng lúc** và barrier được **reset** để có thể dùng lại cho chu kỳ sau. Bạn thậm chí có thể gắn một hành động (`Runnable`) để chạy ngay khi barrier "vỡ" (đủ người).

#### 💻 Ví dụ code: Các luồng cùng chạy song song theo giai đoạn

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    public static void main(String[] args) {
        int numberOfThreads = 3;
        // Tạo barrier cho 3 luồng, kèm hành động chạy khi barrier vỡ
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads, 
            () -> System.out.println("--- TẤT CẢ ĐÃ ĐẾN ĐIỂM HẸN, TIẾP TỤC! ---"));

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Task(barrier, "Luồng-" + i)).start();
        }
    }

    static class Task implements Runnable {
        private final CyclicBarrier barrier;
        private final String name;

        Task(CyclicBarrier barrier, String name) { this.barrier = barrier; this.name = name; }

        @Override
        public void run() {
            try {
                System.out.println(name + " đang làm việc giai đoạn 1...");
                Thread.sleep((long) (Math.random() * 2000)); // Giả lập làm việc
                
                System.out.println(name + " đã xong giai đoạn 1, đang chờ...");
                barrier.await(); // Chờ những người khác

                System.out.println(name + " bắt đầu giai đoạn 2...");
                Thread.sleep((long) (Math.random() * 2000));
                System.out.println(name + " đã hoàn thành!");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
```

#### 🔍 Giải thích code và liên hệ lý thuyết
- `new CyclicBarrier(3, Runnable)`: Tạo một điểm hẹn yêu cầu **3 luồng** phải đến. Tham số thứ hai là **hành động tùy chọn** sẽ được thực thi bởi luồng cuối cùng khi nó bước vào barrier.
- `barrier.await()`: Đây là điểm chặn. Mỗi luồng khi làm xong việc sẽ gọi phương thức này và bị "giữ" lại.
- **Liên hệ:** Khi luồng thứ 3 gọi `await()`, barrier "vỡ". Ngay lập tức, hành động in ra dòng `--- TẤT CẢ... ---` được chạy, và sau đó **cả 3 luồng** được thả ra để bắt đầu giai đoạn 2. Nếu sau này có thêm 3 luồng khác, barrier này vẫn có thể dùng lại được (tính chu kỳ).

---

### 3. Semaphore: "Tấm vé vào cửa có giới hạn"

#### 📖 Lý thuyết dễ hiểu
Hãy nghĩ đến một **bãi đỗ xe** chỉ có **3 chỗ trống**. Khi bãi còn chỗ, xe vào được. Khi bãi hết chỗ, xe phải xếp hàng chờ bên ngoài. Khi một xe rời đi, một chỗ trống được giải phóng và xe đang chờ tiếp theo mới được vào.

`Semaphore` (đèn hiệu) quản lý một số lượng **"giấy phép" (permits)** nhất định. 
- Luồng muốn truy cập tài nguyên phải gọi `acquire()` để **lấy một giấy phép**. Nếu hết giấy phép, luồng bị chặn.
- Khi dùng xong, luồng gọi `release()` để **trả lại giấy phép**, mở đường cho luồng khác.

**Công dụng chính:** Kiểm soát số luồng tối đa được phép truy cập một tài nguyên (ví dụ: giới hạn số kết nối đến database, số luồng xử lý file...).

#### 💻 Ví dụ code: Giới hạn 2 luồng được làm việc cùng lúc

```java
import java.util.concurrent.Semaphore;

public class SemaphoreDemo {
    public static void main(String[] args) {
        // Chỉ cho phép tối đa 2 luồng vào khu vực tài nguyên
        Semaphore semaphore = new Semaphore(2); 

        for (int i = 1; i <= 5; i++) {
            new Thread(new Worker(i, semaphore)).start();
        }
    }

    static class Worker implements Runnable {
        private final int id;
        private final Semaphore semaphore;

        Worker(int id, Semaphore semaphore) { this.id = id; this.semaphore = semaphore; }

        @Override
        public void run() {
            try {
                System.out.println("Luồng " + id + " đang chờ vé...");
                semaphore.acquire(); // Lấy vé
                System.out.println("Luồng " + id + " ĐÃ VÀO (còn " + semaphore.availablePermits() + " vé).");
                
                Thread.sleep(2000); // Giả lập thời gian xử lý
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Luồng " + id + " RA, trả vé.");
                semaphore.release(); // Trả vé trong finally để đảm bảo luôn được thực thi
            }
        }
    }
}
```

#### 🔍 Giải thích code và liên hệ lý thuyết
- `new Semaphore(2)`: Tạo một semaphore với **2 giấy phép**. Điều này có nghĩa là tối đa 2 luồng có thể vượt qua `acquire()` cùng một lúc.
- `semaphore.acquire()`: Nếu còn vé, luồng lấy 1 vé và tiếp tục chạy. Nếu đã hết 2 vé, luồng thứ 3 sẽ **bị chặn** ngay tại đây, xếp hàng chờ.
- `semaphore.release()`: Khi một luồng làm xong, nó trả lại vé. Ngay lập tức, một luồng đang chờ trong hàng đợi sẽ được đánh thức để nhận vé và tiếp tục công việc.
- **Lưu ý quan trọng:** `release()` phải được đặt trong khối `finally` để đảm bảo vé luôn được trả, ngay cả khi luồng gặp lỗi (exception), tránh tình trạng "rò rỉ" giấy phép.

---

### 4. Exchanger: "Điểm hẹn trao đổi của hai người"

#### 📖 Lý thuyết dễ hiểu
Đây là công cụ đơn giản nhất về mặt ý tưởng. Nó giống như một **điểm hẹn của 2 người** để trao đổi đồ vật. Người thứ nhất đến điểm hẹn và phải **đứng chờ**. Khi người thứ hai đến, họ trao đổi đồ cho nhau và cả hai cùng rời đi.

`Exchanger` tạo ra một **điểm đồng bộ (synchronization point)** mà tại đó **chính xác hai luồng** có thể gặp nhau và **hoán đổi (swap) dữ liệu** cho nhau. Luồng nào đến trước sẽ bị chặn lại chờ luồng kia. Khi cả hai đã đến, phương thức `exchange()` sẽ trả về đối tượng mà luồng kia đã gửi đến.

#### 💻 Ví dụ code: Hai luồng trao đổi dữ liệu

```java
import java.util.concurrent.Exchanger;

public class ExchangerDemo {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        new Thread(new Producer(exchanger), "Producer").start();
        new Thread(new Consumer(exchanger), "Consumer").start();
    }

    static class Producer implements Runnable {
        private final Exchanger<String> exchanger;
        Producer(Exchanger<String> exchanger) { this.exchanger = exchanger; }

        @Override
        public void run() {
            String dataToSend = "Hàng đã sản xuất xong!";
            try {
                System.out.println("Producer: Chuẩn bị gửi dữ liệu...");
                Thread.sleep(1000); // Giả lập thời gian sản xuất
                // Trao đổi và nhận lại dữ liệu từ Consumer
                String received = exchanger.exchange(dataToSend); 
                System.out.println("Producer: Đã nhận lại từ Consumer -> " + received);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    static class Consumer implements Runnable {
        private final Exchanger<String> exchanger;
        Consumer(Exchanger<String> exchanger) { this.exchanger = exchanger; }

        @Override
        public void run() {
            String dataToSend = "Đã tiêu thụ hết hàng!";
            try {
                System.out.println("Consumer: Chuẩn bị gửi dữ liệu...");
                Thread.sleep(3000); // Giả lập thời gian tiêu thụ, đến chậm hơn
                String received = exchanger.exchange(dataToSend);
                System.out.println("Consumer: Đã nhận được từ Producer -> " + received);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
```

#### 🔍 Giải thích code và liên hệ lý thuyết
- `Exchanger<String> exchanger = new Exchanger<>();`: Tạo một điểm trao đổi cho kiểu dữ liệu `String`.
- `exchanger.exchange(dataToSend)`: Đây là phương thức cốt lõi. Nó vừa **gửi** dữ liệu `dataToSend` đi, vừa **chờ nhận** dữ liệu từ luồng kia.
- **Liên hệ:** Trong ví dụ, `Producer` chạy nhanh hơn, đến `exchange()` trước và bị chặn lại, chờ đợi. Sau đó 2 giây, `Consumer` mới đến `exchange()`. Lúc này, cuộc trao đổi diễn ra: `Producer` nhận được chuỗi `"Đã tiêu thụ hết hàng!"` và `Consumer` nhận được `"Hàng đã sản xuất xong!"`. Cả hai sau đó được giải phóng và tiếp tục chạy.

---

### 5. Phaser: "Người điều phối đa giai đoạn linh hoạt"

#### 📖 Lý thuyết dễ hiểu
`Phaser` giống như một phiên bản **nâng cấp và linh hoạt hơn** của `CyclicBarrier`. Nó cũng là một điểm hẹn, nhưng cho phép **số lượng luồng tham gia có thể thay đổi** một cách linh động qua từng giai đoạn (phase).

Hãy tưởng tượng một dự án có nhiều giai đoạn:
- **Giai đoạn 1:** Có 3 người tham gia. Họ phải chờ nhau.
- **Giai đoạn 2:** Chỉ còn 2 người tiếp tục, 1 người đã xong việc và rời đi. 2 người còn lại chờ nhau.
- **Giai đoạn 3:** Có thêm 2 người mới tham gia. Tổng cộng 4 người phải chờ nhau.

`Phaser` xử lý được kịch bản này. Các luồng có thể `register()` để tham gia, `arriveAndDeregister()` để rời đi, và `arriveAndAwaitAdvance()` để chờ những người khác trước khi sang giai đoạn tiếp theo.

#### 💻 Ví dụ code: Đồng bộ theo nhiều giai đoạn

```java
import java.util.concurrent.Phaser;

public class PhaserDemo {
    public static void main(String[] args) {
        // Khởi tạo Phaser với 1 "bên" (chính là luồng main)
        Phaser phaser = new Phaser(1); 

        // Giai đoạn 1: 3 luồng tham gia
        System.out.println("--- GIAI ĐOẠN 1 ---");
        for (int i = 1; i <= 3; i++) {
            new Thread(new PhasedTask(phaser, "Task-" + i)).start();
        }
        phaser.arriveAndAwaitAdvance(); // Main chờ 3 task trên hoàn thành giai đoạn 1

        // Giai đoạn 2: 2 luồng mới tham gia, các luồng cũ tự động deregister sau khi xong
        System.out.println("--- GIAI ĐOẠN 2 ---");
        for (int i = 4; i <= 5; i++) {
            new Thread(new PhasedTask(phaser, "Task-" + i)).start();
        }
        phaser.arriveAndAwaitAdvance(); // Main chờ 2 task mới hoàn thành giai đoạn 2

        phaser.arriveAndDeregister(); // Main rời đi, kết thúc
        System.out.println("--- KẾT THÚC ---");
    }

    static class PhasedTask implements Runnable {
        private final Phaser phaser;
        private final String name;

        PhasedTask(Phaser phaser, String name) {
            this.phaser = phaser;
            this.name = name;
            phaser.register(); // Đăng ký tham gia ngay khi tạo
        }

        @Override
        public void run() {
            try {
                System.out.println(name + " bắt đầu làm việc...");
                Thread.sleep((long) (Math.random() * 1000));
                System.out.println(name + " xong việc, chờ đồng đội.");
                
                // Đánh dấu đã đến và chờ, sau đó tự động rời khỏi phaser
                phaser.arriveAndDeregister(); 
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
```

#### 🔍 Giải thích code và liên hệ lý thuyết
- `new Phaser(1)`: Con số `1` đại diện cho **luồng main** đăng ký tham gia với tư cách là người điều phối. Phase ban đầu là 0.
- `phaser.register()`: Được gọi trong constructor của `PhasedTask`. Mỗi khi một task mới được tạo, nó sẽ **tăng số lượng "bên" (party)** đang tham gia Phaser lên 1.
- `phaser.arriveAndAwaitAdvance()` (được gọi từ main): Main thread "đến" barrier và **chờ** cho đến khi tất cả các bên đã đăng ký đều đến. Khi đủ, phase tăng lên và tất cả được giải phóng.
- `phaser.arriveAndDeregister()` (được gọi từ task): Task "đến" barrier và **hủy đăng ký** của mình. Điều này có nghĩa là ở giai đoạn sau, nó không còn được tính vào số lượng cần chờ nữa. Đây chính là điểm mạnh so với `CyclicBarrier`.
- **Liên hệ:** Ở giai đoạn 1, có tổng cộng 4 bên (main + 3 task). Khi cả 4 gọi `arrive...`, phase 1 kết thúc. Các task 1-3 tự deregister. Sang giai đoạn 2, chỉ còn main (1 bên). Khi tạo thêm 2 task mới, tổng số bên là 3. Khi cả 3 đến, phase 2 kết thúc. Cuối cùng main deregister, số bên về 0, Phaser kết thúc.

---

### 💎 Tóm kết
- **CountDownLatch** và **CyclicBarrier** đều là "điểm hẹn", nhưng `CountDownLatch` là **một lần** (chờ sự kiện), còn `CyclicBarrier` là **chu kỳ** (chờ nhau).
- **Semaphore** không phải là điểm hẹn, mà là **"cửa quay có kiểm soát"** số lượng người vào cùng lúc.
- **Exchanger** là điểm hẹn **đặc biệt của đúng 2 người** để trao đổi hàng hóa.
- **Phaser** là phiên bản **linh hoạt nhất**, cho phép "điểm hẹn" của bạn thay đổi thành viên theo từng giai đoạn.

Hy vọng phần giải thích chi tiết này hữu ích cho bạn. Nếu bạn gửi lại hình ảnh, tôi sẽ phân tích chúng ngay.


Tuyệt vời! Bạn đã gửi 5 file code mẫu. Tôi sẽ giải thích **chi tiết từng file**, đi kèm lý thuyết ở bên cạnh từng đoạn code, và chỉ ra những điểm quan trọng / cạm bẫy. Vì bạn muốn "chi tiết nội dung", tôi sẽ mổ xẻ từng dòng có ý nghĩa.

---

## 📁 File 1: `CountDownLatchDemo.java`

### 🎯 Mục đích
Mô phỏng việc **main thread chờ 4 worker** hoàn thành công việc trước khi tiếp tục. Điểm đặc biệt: pool chỉ có **2 thread**, nhưng có **4 task** → minh họa rằng `CountDownLatch` đếm theo **số lần countDown**, không quan tâm task nào chạy.

### 💻 Code + giải thích từng dòng

```java
package synchronizers_cyclicbarrier_countdownlatch_semaphore_exchanger_phaser;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class CountDownLatchDemo {

    private static final int COUNT = 4;
```
> **Lý thuyết:** `COUNT = 4` là **số tín hiệu** cần thiết để "mở cổng". Trong `CountDownLatch`, con số này chính là bộ đếm ban đầu, và nó **chỉ giảm, không tăng**.

```java
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(COUNT);
```
> **Lý thuyết:** `new CountDownLatch(4)` tạo latch với bộ đếm = 4. Không có cơ chế reset — đây là **one-shot synchronizer**. Nếu muốn dùng lại, phải tạo object mới.

```java
        var es = Executors.newFixedThreadPool(2);
```
> **Quan trọng:** Pool chỉ có **2 thread** nhưng ta submit **4 task**. Nghĩa là 4 task phải **xếp hàng**: 2 chạy trước, xong thì 2 chạy sau. Đây là điểm hay của ví dụ — nó chứng minh latch không phụ thuộc vào số luồng.

```java
        IntStream.range(0, COUNT).forEach(i -> es.submit(new Worker(countDownLatch)));
```
> Submit 4 `Worker` vào pool. Vì pool chỉ có 2 thread, các task sẽ được thực thi theo **batch 2-2**.

```java
        countDownLatch.await();
        System.out.println("Latch is released");
```
> **Lý thuyết cốt lõi:** `await()` làm **main thread bị block** cho đến khi bộ đếm về 0. Trong lúc block, main thread **không chiếm CPU** — nó ở trạng thái `WAITING`. Khi bộ đếm = 0, main được đánh thức và in dòng "Latch is released".

```java
        es.shutdown();
    }
```
> **Lý thuyết:** `shutdown()` không kill thread ngay, mà từ chối task mới và để task hiện tại chạy xong. Vì ta đã `await()` xong 4 task rồi, nên lúc này pool không còn task nào.

```java
    private static class Worker implements Runnable {
        private CountDownLatch countDownLatch;

        public Worker(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        public void run() {
            System.out.println("doing some work...");
            System.out.println("Counted down -1");
            countDownLatch.countDown();
        }
    }
}
```
> **Lý thuyết:** `countDown()` giảm bộ đếm đi 1 (thread-safe, dùng CAS bên trong `AbstractQueuedSynchronizer`). Khi bộ đếm về 0, **tất cả các thread đang chờ ở `await()`** được đánh thức đồng thời.
>
> **Cạm bẫy cần nhớ:** Nếu task bị exception trước khi gọi `countDown()` → **bộ đếm không bao giờ về 0** → main thread treo vĩnh viễn. Trong thực tế nên đặt `countDown()` trong `finally`.

### 📊 Kết quả chạy dự kiến (thứ tự có thể xen kẽ)

```
doing some work...
Counted down -1
doing some work...
Counted down -1
doing some work...        ← 2 task batch 2 bắt đầu
Counted down -1
doing some work...
Counted down -1
Latch is released         ← main in ra khi bộ đếm về 0
```

> **Điểm mấu chốt:** "Latch is released" **luôn in cuối cùng**, sau khi 4 lần `countDown` xảy ra. Đây là minh chứng cho tính chất "chờ đủ sự kiện" của `CountDownLatch`.

---

## 📁 File 2: `CyclicBarrierDemo.java`

### 🎯 Mục đích
Mô phỏng **4 luồng** cùng sinh số ngẫu nhiên, gặp nhau ở **điểm hẹn (barrier)**, và khi cả 4 đến đủ thì **hành động callback** được chạy để tính max.

### 💻 Code + giải thích từng dòng

```java
    private static final int NUMBER_OF_THREADS = 4;
    private static final int NUMBERS_TO_GENERATE = 3;
    private static final int MAXIMUM_RANDOM_NUMBER = 100;

    private static CyclicBarrier barrier;
    private List<Integer> generatedNumbers = new CopyOnWriteArrayList<>();
    private Random r = new Random();
```
> **Lý thuyết quan trọng — `CopyOnWriteArrayList`:** Đây là list **thread-safe** cho trường hợp **đọc nhiều, ghi ít**. Mỗi lần `add()`, nó tạo một **bản copy mới** của mảng bên trong. Phù hợp ở đây vì 4 thread cùng `add` song song mà không cần khóa thủ công.
>
> ⚠️ Nếu dùng `ArrayList` thường → có thể mất dữ liệu do **race condition** khi nhiều thread cùng add.

```java
    public static void main(String[] args) {
        var demo = new CyclicBarrierDemo();
        barrier = new CyclicBarrier(NUMBER_OF_THREADS, demo::numberGenerationCallback);
```
> **Lý thuyết cốt lõi:** `new CyclicBarrier(4, callback)`:
> - `4` = số "bên" (parties) cần đến để barrier vỡ.
> - `demo::numberGenerationCallback` = **hành động chạy khi barrier vỡ** (chạy bởi **thread đến cuối cùng**, và chạy **trước khi** các thread được thả ra).
> - Điểm khác biệt với `CountDownLatch`: barrier này **tự reset** sau khi vỡ → có thể tái sử dụng cho chu kỳ tiếp theo.

```java
        var ex = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        IntStream.range(0, NUMBER_OF_THREADS).forEach(i -> ex.submit(demo::generateNumbers));
        System.out.println("getParties(): " + barrier.getParties());
```
> **Lý thuyết:** `getParties()` trả về **số bên đã đăng ký** = 4 (cố định, không thay đổi như Phaser). Dòng này in ra `4` bất kể barrier đang ở trạng thái nào.

```java
        ex.shutdown();
    }
```

```java
    private void numberGenerationCallback() {
        int max = generatedNumbers.stream()
                .mapToInt(i -> i)
                .max().getAsInt();
        System.out.println("All generated numbers: " + generatedNumbers);
        System.out.println("Maximum: " + max);
    }
```
> **Lý thuyết:** Callback này chạy **một lần mỗi chu kỳ**, ngay sau khi thread thứ 4 gọi `await()`. Lúc này cả 4 thread vẫn đang bị block (barrier chưa mở), nên callback có thể **đọc an toàn** dữ liệu mà 4 thread vừa ghi.

```java
    private void generateNumbers() {
        for (int i = 0; i < NUMBERS_TO_GENERATE; i++) {
            generatedNumbers.add(r.nextInt(MAXIMUM_RANDOM_NUMBER));
        }
        try {
            System.out.println("await(): " + barrier.await());
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
```
> **Lý thuyết quan trọng:**
> - Mỗi thread sinh **3 số** rồi gọi `await()`.
> - **`await()` trả về `int`** — đó là **"arrival index"**: số thứ tự của thread khi đến barrier, tính từ 0. Thread đầu tiên đến → trả về `0`, thread cuối → trả về `3`. Dòng `System.out.println("await(): " + barrier.await())` sẽ in ra các số 0,1,2,3 (theo thứ tự ngẫu nhiên tùy thread nào đến trước).
> - `await()` ném **2 loại exception**:
>   - `InterruptedException`: thread bị interrupt khi đang chờ.
>   - `BrokenBarrierException`: barrier bị "vỡ" bất thường — xảy ra khi **một thread khác bị interrupt hoặc timeout**, làm barrier không thể hoàn thành chu kỳ đó. Đây là cơ chế lan truyền lỗi của `CyclicBarrier`.

### ⚠️ Cạm bẫy của `CyclicBarrier`
- Nếu **một thread chết** trước khi gọi `await()` → 3 thread còn lại **treo mãi mãi** (barrier không bao giờ đủ 4). Đây là nhược điểm lớn so với `Phaser` (có thể deregister).
- Barrier có thể dùng lại (tính "cyclic") — sau chu kỳ 1 vỡ, chu kỳ 2 bắt đầu lại với **cùng 4 bên**. Trong ví dụ này chỉ có 1 chu kỳ vì mỗi thread chỉ gọi `await()` một lần.

### 📊 Kết quả chạy dự kiến

```
getParties(): 4
await(): 0                       ← thread đến đầu
await(): 1
await(): 3                       ← thread đến thứ 3 (index 3?? có thể lộn xộn)
await(): 2
All generated numbers: [12, 45, 78, 3, 99, 21, 56, 34, 88, 7, 65, 42]
Maximum: 99
```
> **Điểm mấu chốt:** 4 dòng `await():` xuất hiện trước dòng `All generated numbers:` — **chứng minh callback chỉ chạy sau khi cả 4 thread đã đến barrier**.

---

## 📁 File 3: `ExchangerDemo.java`

### 🎯 Mục đích
Mô phỏng 2 thread **trao đổi dữ liệu** cho nhau qua `Exchanger`.

### 💻 Code + giải thích từng dòng

```java
    public static void main(String[] args) throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();
```
> **Lý thuyết:** `Exchanger<T>` là điểm đồng bộ dành cho **chính xác 2 thread**. Kiểu generic `T` là kiểu dữ liệu trao đổi. `Exchanger` không giới hạn số lần dùng — có thể trao đổi nhiều lần nếu 2 thread gọi `exchange()` liên tiếp.

```java
        Runnable task1 = () -> {
            try {
                String msg = exchanger.exchange("Message from Task #1");
                System.out.println("Received from another thread in task #1: " + msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
```
> **Lý thuyết cốt lõi:** `exchange("Message from Task #1")` có **2 hành vi trong một**:
> 1. **Gửi** chuỗi `"Message from Task #1"` cho thread đối tác.
> 2. **Chờ** (block) cho đến khi thread đối tác đến, rồi **nhận** dữ liệu của họ.
>
> Nếu `task1` đến trước → bị block tại đây. Khi `task2` đến → cuộc trao đổi diễn ra, cả 2 được thả ra.

```java
        Runnable task2 = () -> {
            try {
                String msg = exchanger.exchange("Message from Task #2");
                System.out.println("Received from another thread in task #2: " + msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
```

```java
        var es = Executors.newCachedThreadPool();
        es.submit(task1);
        es.submit(task2);
        es.shutdown();
    }
```
> **Lý thuyết:** `newCachedThreadPool()` tạo thread **khi cần**, phù hợp với 2 task ngắn. Không quan trọng pool loại gì — `Exchanger` hoạt động ở tầng thread, không phụ thuộc executor.

### ⚠️ Cạm bẫy
- Nếu chỉ có **1 thread** gọi `exchange()` → nó **treo vĩnh viễn** vì không có đối tác.
- Nếu có **3 thread** cùng gọi → thread thứ 3 sẽ chờ một đối tác mới. `Exchanger` ghép cặp **từng đôi một**.
- Không có cơ chế timeout mặc định, nhưng có overload `exchange(x, timeout, unit)` để tránh treo.

### 📊 Kết quả chạy dự kiến

```
Received from another thread in task #1: Message from Task #2
Received from another thread in task #2: Message from Task #1
```
> **Điểm mấu chốt:** Mỗi task **nhận được message của task kia** — đúng nghĩa "trao đổi". Thứ tự 2 dòng có thể đảo, tùy thread nào được đánh thức trước.

---

## 📁 File 4: `PhaserDemo.java`

### 🎯 Mục đích
Mô phỏng **2 thread** trải qua **2 giai đoạn (phase)**, mỗi giai đoạn đều phải chờ nhau. Sau khi xong, cả 2 **deregister** khỏi Phaser.

### 💻 Code + giải thích từng dòng

```java
    private static final int NUMBER_OF_THREADS = 2;
    private static Phaser phaser = new Phaser();
```
> **Lý thuyết cực quan trọng:** `new Phaser()` **không có tham số** → số parties ban đầu = **0**. Điều này khác với `new Phaser(1)` trong ví dụ lý thuyết trước (main đăng ký). Ở đây **main thread không tham gia Phaser** — chỉ 2 task con đăng ký.
>
> Đây là lý do `phaser.arriveAndAwaitAdvance()` trong task hoạt động đúng: chỉ cần 2 task cùng đến là phase advance.

```java
    public static void main(String[] args) throws InterruptedException {
        var es = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        System.out.println("Phase before tasks execution: " + phaser.getPhase());
```
> **Lý thuyết:** `getPhase()` trả về **số phase hiện tại**, bắt đầu từ `0`. Dòng này in ra `0`.

```java
        IntStream.range(0, NUMBER_OF_THREADS).forEach(i -> es.submit(new Task(phaser)));
```
> Khi `new Task(phaser)` được tạo, constructor gọi `phaser.register()` → mỗi task tăng số parties lên 1. Sau khi tạo 2 task → `phaser` có **2 parties**.

```java
        es.shutdown();

        es.awaitTermination(1, TimeUnit.SECONDS);
```
> **Lý thuyết:** `awaitTermination` **không đăng ký** main vào Phaser. Nó chỉ đơn thuần **chờ pool kết thúc** (tất cả task chạy xong). Đây là chờ ở tầng `ExecutorService`, không phải tầng `Phaser`.

```java
        System.out.println("Parties after all threads are de-registered: "
                + phaser.getRegisteredParties());
    }
```
> **Lý thuyết:** `getRegisteredParties()` trả về **số parties hiện tại**. Sau khi 2 task `arriveAndDeregister()`, con số này = **0**. Nếu task quên deregister, con số này ≠ 0.

```java
    private static class Task implements Runnable {
        private Phaser phaser;

        public Task(Phaser phaser) {
            this.phaser = phaser;
            this.phaser.register();
        }
```
> **Lý thuyết:** `register()` **tăng số parties lên 1**. Đây là cơ chế "đăng ký tham gia" — thread nào muốn được tính vào phase phải register. Lưu ý: `register()` **không block**, chỉ tăng counter.

```java
        @Override
        public void run() {
            System.out.println("Arrived in thread: " + Thread.currentThread().getName());
            System.out.println("Arrival phase number: " + phaser.arriveAndAwaitAdvance());
```
> **Lý thuyết cốt lõi — `arriveAndAwaitAdvance()`:**
> - Đánh dấu thread đã "đến" phase hiện tại (giảm số "chưa đến").
> - **Block** cho đến khi **tất cả parties** đều đến.
> - Khi đủ, phase **tăng lên 1** và tất cả thread được thả ra.
> - **Trả về số phase vừa hoàn thành** — lần đầu gọi sẽ trả về `0` (vì phase 0 vừa xong), lần 2 trả về `1`.
>
> ⚠️ **Khác với `CyclicBarrier.await()`:** `await()` trả về **arrival index** (0..N-1), còn `arriveAndAwaitAdvance()` trả về **phase number**. Đây là câu hỏi phỏng vấn rất hay.

```java
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println("getPhase(): " + phaser.getPhase());
```
> Lúc này phase đã = 1. In ra `getPhase(): 1`.

```java
                System.out.println("Arrived in thread: " + Thread.currentThread().getName());
                System.out.println("Arrival phase number: " + phaser.arriveAndAwaitAdvance());
```
> Lần gọi thứ 2: đánh dấu đến phase 1, chờ đủ 2 thread → phase tăng lên 2 → trả về `1`.

```java
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println("getPhase(): " + phaser.getPhase());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            phaser.arriveAndDeregister();
        }
    }
}
```
> **Lý thuyết — `arriveAndDeregister()`:**
> - Đánh dấu thread đã "đến" phase hiện tại.
> - **Giảm số parties đi 1** (hủy đăng ký).
> - **Không block** — thread rời đi ngay.
> - Thread này sẽ **không còn được tính** vào các phase sau.
>
> **Tại sao đây là vũ khí của Phaser?** Vì số parties có thể **thay đổi linh động** giữa các phase — điều `CyclicBarrier` không làm được. Trong ví dụ này, sau khi deregister, parties = 0 → Phaser "kết thúc" (terminated), nhưng không ai kiểm tra điều đó.

### ⚠️ Điểm cần chú ý
- Nếu quên `register()` → parties không đủ → `arriveAndAwaitAdvance()` **treo**.
- Nếu quên `arriveAndDeregister()` → parties không bao giờ về 0 → các phase sau **không bao giờ advance** đúng.
- `getPhase()` sau lần `arriveAndAwaitAdvance()` thứ nhất = **1**, không phải 0.

### 📊 Kết quả chạy dự kiến

```
Phase before tasks execution: 0
Arrived in thread: pool-1-thread-1
Arrival phase number: 0
Arrived in thread: pool-1-thread-2
Arrival phase number: 0
getPhase(): 1
getPhase(): 1
Arrived in thread: pool-1-thread-1
Arrival phase number: 1
Arrived in thread: pool-1-thread-2
Arrival phase number: 1
getPhase(): 2
getPhase(): 2
Parties after all threads are de-registered: 0
```

> **Điểm mấu chốt:** `Arrival phase number` lần 1 = `0`, lần 2 = `1`; `getPhase()` tương ứng `1` rồi `2`; cuối cùng parties = `0`.

---

## 📁 File 5: `SemaphoreDemo.java`

### 🎯 Mục đích
Mô phỏng **hệ thống login giới hạn 10 slot**. 10 user cùng login, sau đó main thử login lần nữa (thất bại vì hết slot), rồi logout để giải phóng 1 slot.

### 💻 Code + giải thích từng dòng

```java
    public static void main(String[] args) throws InterruptedException {
        int loginPermits = 10;
        var es = Executors.newFixedThreadPool(4);
        var demo = new SemaphoreDemo(loginPermits);
```
> **Lý thuyết:** `loginPermits = 10` → `new Semaphore(10)` tạo **10 giấy phép**. Chỉ 10 luồng được "login" cùng lúc.
>
> **Lưu ý pool 4 thread:** submit 10 task nhưng chỉ 4 thread. Vì task `tryLogin` **rất nhanh** (chỉ acquire + print), 10 task sẽ chạy gần như tuần tự. Kết quả cuối: **10 permit bị lấy hết** → còn 0.

```java
        IntStream.range(0, loginPermits).forEach(user -> es.submit(demo::tryLogin));
        es.shutdown();
        es.awaitTermination(2, TimeUnit.SECONDS);
```
> **Lý thuyết:** `awaitTermination` chờ tối đa 2s để pool kết thúc. Vì task rất nhanh, 2s là quá đủ. Nếu quá 2s mà chưa xong → trả về `false` (ở đây bỏ qua kết quả).

```java
        System.out.println("availablePermitsForLogin:\t" + demo.availablePermitsForLogin());
```
> Lúc này in ra `0` — vì 10 task đã acquire hết 10 permit.

```java
        demo.tryLogin();
```
> **Lý thuyết — `tryAcquire()`:** Đây là phiên bản **non-blocking** của `acquire()`:
> - Nếu còn permit → lấy ngay, trả về `true`.
> - Nếu hết permit → **trả về `false` ngay lập tức**, không chờ.
>
> Vì permit = 0 → in ra `"No permits for login available. Try later."`
>
> ⚠️ Phân biệt với `acquire()` (blocking) — nếu dùng `acquire()` ở đây, main thread sẽ **treo** vì không có permit nào được trả.

```java
        demo.logout();
        System.out.println("availablePermitsForLogin:\t" + demo.availablePermitsForLogin());
    }
```
> `logout()` gọi `semaphore.release()` → permit tăng từ 0 lên 1. In ra `1`.

```java
    private Semaphore semaphore;

    public SemaphoreDemo(int slotLimit) {
        semaphore = new Semaphore(slotLimit);
    }
```
> **Lý thuyết:** Constructor nhận `slotLimit` (10). `new Semaphore(10)` tạo **fair = false** mặc định → không đảm bảo thứ tự FIFO khi nhiều thread chờ. Nếu muốn công bằng, dùng `new Semaphore(10, true)` — nhưng hiệu năng giảm.

```java
    private void tryLogin() {
        if (semaphore.tryAcquire()) {
            System.out.println("Congrats! You are logged in!");
        } else {
            System.out.println("No permits for login available. Try later.");
        }
    }
```
> **Lý thuyết:** `tryAcquire()` ở đây dùng **không có timeout** — trả về ngay. Nếu muốn chờ có giới hạn, dùng `tryAcquire(timeout, unit)`.

```java
    private void logout() {
        semaphore.release();
    }
```
> **Lý thuyết cực quan trọng:** `release()` **tăng permit lên 1**, bất kể thread gọi `release()` có phải là thread đã `acquire()` hay không. Đây là **điểm mạnh** (linh hoạt) nhưng cũng là **cạm bẫy** — nếu gọi `release()` nhiều hơn `acquire()`, số permit có thể **vượt quá giới hạn ban đầu**. Ví dụ: khởi tạo 10, gọi `release()` 5 lần mà không acquire → permit = 15 (!!). Đây là lỗi logic phổ biến.
>
> Trong thực tế, luôn đặt `release()` trong `finally` sau `acquire()` thành công.

```java
    private int availablePermitsForLogin() {
        return semaphore.availablePermits();
    }
}
```
> **Lý thuyết:** `availablePermits()` trả về **số permit hiện có**. Hữu ích để debug / monitor, **không nên dùng để quyết định logic** vì có race condition giữa lúc đọc và lúc acquire.

### ⚠️ Cạm bẫy điển hình
- **Quên release** → permit bị "rò rỉ" → các thread khác treo.
- **Release thừa** → permit tăng vượt giới hạn → mất tác dụng giới hạn.
- **`availablePermits()` không atomic với `tryAcquire()`** → đừng viết `if (availablePermits() > 0) acquire()`.

### 📊 Kết quả chạy dự kiến

```
Congrats! You are logged in!       × 10 lần (có thể xen kẽ)
availablePermitsForLogin:    0
No permits for login available. Try later.
availablePermitsForLogin:    1
```
> **Điểm mấu chốt:** 10 dòng "Congrats" (vì 10 task đều acquire thành công), permit còn `0`, main thử login thất bại, sau `release()` → permit = `1`.

---

## 🧩 Bảng so sánh nhanh 5 file

| File | Điểm mấu chốt cần nhớ | Điểm khác biệt so với lý thuyết |
| :--- | :--- | :--- |
| `CountDownLatchDemo` | Pool 2 thread nhưng 4 task → chứng minh latch đếm theo **sự kiện**, không theo thread | Không có `finally` cho `countDown` — cạm bẫy tiềm ẩn |
| `CyclicBarrierDemo` | `await()` trả về **arrival index** (0..3); callback chạy bởi thread cuối | Dùng `CopyOnWriteArrayList` để tránh race |
| `ExchangerDemo` | `exchange()` vừa gửi vừa nhận, block đến khi có đối tác | Không có timeout → có thể treo nếu thiếu đối tác |
| `PhaserDemo` | `arriveAndAwaitAdvance()` trả về **phase number** (0,1); main **không đăng ký** | Parties = 0 ban đầu, khác ví dụ `new Phaser(1)` |
| `SemaphoreDemo` | `tryAcquire()` non-blocking; `release()` tăng permit bất kể ai gọi | `release()` thừa có thể vượt giới hạn — cạm bẫy |

---

## 💡 Câu hỏi tự kiểm tra (để bạn khắc sâu)

1. Trong `CountDownLatchDemo`, nếu pool là `newFixedThreadPool(1)` thì kết quả có thay đổi không? → **Không**, chỉ chậm hơn, vì latch không quan tâm số thread.
2. Trong `CyclicBarrierDemo`, nếu bỏ `CopyOnWriteArrayList` và dùng `ArrayList` thì sao? → **Có thể mất số** do race condition khi 4 thread cùng `add`.
3. Trong `PhaserDemo`, nếu **quên** `register()` trong constructor thì chuyện gì xảy ra? → Parties = 0, `arriveAndAwaitAdvance()` **không block** (vì không có ai để chờ) — phase vẫn tăng nhưng vô nghĩa.
4. Trong `SemaphoreDemo`, nếu main gọi `demo.logout()` **2 lần** thì permit = mấy? → **2**, vượt quá giới hạn ban đầu (10) — minh họa cạm bẫy `release()` thừa.

Bạn muốn tôi đi sâu thêm phần nào (ví dụ: cơ chế bên trong `AbstractQueuedSynchronizer`, cách debug deadlock với các synchronizer này, hay viết thêm ví dụ nâng cao)?