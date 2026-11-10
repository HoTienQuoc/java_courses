Chào bạn, đây là một chủ đề rất thú vị và quan trọng trong Java hiện đại (đặc biệt khi làm việc với Spring WebFlux, Microservices, hay xử lý dữ liệu lớn). Tôi sẽ giải thích chi tiết toàn bộ nội dung bạn yêu cầu bằng tiếng Việt, đi từng bước một từ hình ảnh, lý thuyết cho đến code thực tế.

---

### PHẦN 1: GIẢI THÍCH CHI TIẾT TỪNG HÌNH ẢNH

**Hình 1: Agenda (Chương trình học)**
*   Đây là slide giới thiệu lộ trình bài học. Giảng viên sẽ đi qua các khái niệm: Lập trình phản ứng (Reactive Programming) là gì, các thư viện hỗ trợ trong Java, API `Flow`, khái niệm `Backpressure`, các interface chính, khi nào nên dùng và ví dụ thực tế.

**Hình 2: Reactive Programming Approach (Tiếp cận lập trình phản ứng)**
*   **Định nghĩa:** Lập trình phản ứng là một mô hình (paradigm) thiết kế để xử lý các **luồng dữ liệu bất đồng bộ (asynchronous data streams)** và quản lý các thay đổi trong hệ thống.
*   **Đặc điểm chính:**
    *   **Asynchronous and Non-blocking:** Bất đồng bộ và không chặn (không làm tắc nghẽn luồng xử lý).
    *   **Data Streams:** Xem dữ liệu như một dòng chảy liên tục các sự kiện/giá trị theo thời gian.
    *   **Event-Driven:** Hướng sự kiện, hệ thống phản hồi lại các thay đổi.
    *   **Backpressure:** Cơ chế "ép ngược" - các thành phần phía sau (downstream) báo hiệu cho phía trước biết khả năng xử lý của mình để tránh bị quá tải.
    *   **Responsive and Scalable:** Tạo ra các ứng dụng phản hồi nhanh và dễ dàng mở rộng.
    *   **Functional Programming:** Kết hợp với lập trình hàm (tính bất biến, pure functions).
*   **Sơ đồ:** Mô hình Non-Blocking Request. Thay vì mỗi request chiếm 1 thread, các request được đưa vào **Event Loop**, xử lý qua một số lượng nhỏ thread (Fixed/Small Number of threads) và tương tác với DB qua Event Handler. Khi DB trả về Event, Event Loop mới tiếp tục xử lý.

**Hình 3: Libraries for Reactive Programming (Các thư viện)**
*   Giới thiệu các thư viện phổ biến để thực thi lập trình phản ứng trong Java:
    *   **RxJava:** Rất phổ biến, nền tảng của nhiều thư viện khác.
    *   **Project Reactor:** Thư viện lõi của Spring WebFlux (rất phổ biến hiện nay).
    *   **Mutiny:** Thư viện của framework Quarkus.
    *   **Reactive Streams:** Đây là tiêu chuẩn (Specification). Java 9 đã tích hợp sẵn nó dưới dạng `Flow API`.

**Hình 4: Flow API**
*   **Giới thiệu:** Java 9 giới thiệu `Flow API` (còn gọi là Reactive Streams) để giải quyết các thách thức trong lập trình bất đồng bộ.
*   **Mục đích:** Thiết lập một tiêu chuẩn cho việc xử lý luồng bất đồng bộ, ưu tiên cơ chế **non-blocking backpressure**.
*   **Lợi ích:** Xử lý hiệu quả, dễ mở rộng, kiểm soát luồng dữ liệu tốt hơn giữa các thành phần.

**Hình 5: Key Types in Flow API (Các Interface chính)**
*   **Publisher (Nhà sản xuất):** Phát ra một chuỗi các phần tử (có thể vô hạn). Phân phối dữ liệu cho các Subscriber.
*   **Subscriber (Người đăng ký/Nhận):** Tiêu thụ dữ liệu từ Publisher. Gửi tín hiệu khi nó có thể xử lý thêm dữ liệu.
*   **Subscription (Sự đăng ký):** Cầu nối giữa Publisher và Subscriber. Cho phép Subscriber yêu cầu thêm dữ liệu (`request`) hoặc hủy bỏ (`cancel`).
*   **Processor (Bộ xử lý):** Đóng vai trò kép, vừa là Subscriber, vừa là Publisher. Dùng để biến đổi dữ liệu trong luồng.

**Hình 6: Why Flow API is Needed (Tại sao cần Flow API)**
*   **Vấn đề của Stream truyền thống:** Thiếu tiêu chuẩn xử lý bất đồng bộ, dễ gây tràn bộ đệm (buffer overflow) hoặc tiêu tốn tài nguyên nếu Publisher bắn dữ liệu quá nhanh mà Subscriber xử lý không kịp.
*   **Giải pháp:** `Backpressure`. Subscriber sẽ báo cho Publisher biết nó sẵn sàng nhận bao nhiêu dữ liệu, ngăn chặn việc "dội bom" dữ liệu làm sập hệ thống phía sau.

**Hình 7: When to Use Flow API (Khi nào nên dùng)**
*   **Xử lý bất đồng bộ:** Các tác vụ I/O, sự kiện, dữ liệu thời gian thực.
*   **Kiểm soát Backpressure:** Khi tốc độ tạo dữ liệu (Producer) khác biệt lớn so với tốc độ xử lý (Consumer).
*   **Ứng dụng phản hồi & mở rộng:** Xây dựng web services cần phản hồi nhanh, không bị nghẽn.

---

### PHẦN 2: GIẢI THÍCH LÝ THUYẾT DỄ HIỂU BẰNG VÍ DỤ ĐỜI SỐNG

Để dễ hiểu, chúng ta hãy dùng **Ví dụ quán Cà phê**:

1.  **Reactive Programming (Lập trình phản ứng):** Giống như bạn đặt món trên ứng dụng GrabFood. Bạn không cần đứng đợi (Non-blocking). Khi tài xế giao tới (Event), bạn mới ra nhận. Hệ thống tự động cập nhật trạng thái đơn hàng liên tục cho bạn (Data Stream).
2.  **Publisher (Người bán/Barista):** Người pha chế cà phê. Họ có thể pha rất nhanh.
3.  **Subscriber (Khách hàng):** Bạn, người uống cà phê.
4.  **Subscription (Giao kèo):** Bạn nói với Barista: *"Tôi chỉ uống được 2 ly một lúc thôi, pha xong 2 ly thì đưa tôi, đừng đưa 10 ly cùng lúc tôi không cầm kịp"*. Đây chính là **`request(n)`**.
5.  **Backpressure (Ép ngược):** Nếu Barista pha 100 ly/giây, nhưng bạn chỉ uống được 1 ly/giây. Nếu không có Backpressure, cà phê sẽ tràn ngập bàn (Buffer Overflow, tràn RAM). Backpressure là việc bạn nói: *"Từ từ thôi, tôi uống hết ly này rồi hẵng đưa ly tiếp theo"*.
6.  **Processor (Bộ xử lý):** Giống như một nhân viên pha chế kiêm giao hàng. Nhận order từ quầy (Subscriber), pha chế (xử lý), rồi đưa cho khách (Publisher).

---

### PHẦN 3: VÍ DỤ CODE CHI TIẾT VÀ GIẢI THÍCH (Sử dụng Java Flow API)

Dưới đây là một ví dụ hoàn chỉnh sử dụng `Flow API` của Java 9+. Chúng ta sẽ xây dựng một hệ thống đơn giản: **Publisher phát ra các số từ 1 đến 5**, và **Subscriber sẽ nhận và in ra màn hình**. Subscriber sẽ yêu cầu (request) từng số một để mô phỏng Backpressure.

#### 1. Code ví dụ (Java)

```java
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

// 1. Định nghĩa Subscriber (Khách hàng)
class MySubscriber implements Flow.Subscriber<Integer> {
    private Flow.Subscription subscription;
    private String name;

    public MySubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        // Được gọi đầu tiên khi đăng ký thành công
        this.subscription = subscription;
        System.out.println(name + " đã đăng ký nhận dữ liệu.");
        // Yêu cầu Publisher gửi 1 phần tử trước (Backpressure: Tôi chỉ nhận 1 cái lúc này)
        subscription.request(1); 
    }

    @Override
    public void onNext(Integer item) {
        // Được gọi mỗi khi Publisher gửi 1 phần tử mới
        System.out.println(name + " nhận được: " + item);
        
        // Xử lý xong thì yêu cầu tiếp 1 phần tử nữa
        // Nếu bỏ dòng này, Publisher sẽ không gửi tiếp (đây chính là Backpressure)
        subscription.request(1); 
    }

    @Override
    public void onError(Throwable throwable) {
        // Xử lý khi có lỗi
        System.err.println(name + " gặp lỗi: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        // Được gọi khi Publisher không còn dữ liệu để gửi
        System.out.println(name + " đã nhận xong toàn bộ dữ liệu.");
    }
}

// 2. Chương trình chính (Main)
public class FlowApiExample {
    public static void main(String[] args) throws InterruptedException {
        // Tạo một Publisher (Sử dụng SubmissionPublisher có sẵn của Java)
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        // Tạo Subscriber
        MySubscriber subscriber = new MySubscriber("Khách Hàng A");

        // Đăng ký Subscriber vào Publisher
        publisher.subscribe(subscriber);

        System.out.println("Publisher bắt đầu phát dữ liệu...");
        // Publisher phát dữ liệu
        for (int i = 1; i <= 5; i++) {
            publisher.submit(i);
        }

        // Đóng Publisher (báo hiệu không còn dữ liệu)
        publisher.close();

        // Đợi một chút để luồng bất đồng bộ chạy xong trước khi tắt chương trình
        Thread.sleep(1000); 
    }
}
```

#### 2. Giải thích Code chi tiết kèm Lý thuyết

**A. Class `MySubscriber` (Kế thừa `Flow.Subscriber<Integer>`)**
*   **Lý thuyết:** Subscriber là người tiêu thụ dữ liệu. Nó phải triển khai 4 phương thức.
*   **Code `onSubscribe(Flow.Subscription subscription)`**:
    *   *Lý thuyết:* Đây là nơi thiết lập mối liên kết. `Subscription` chính là cái cầu nối để bạn điều khiển luồng dữ liệu.
    *   *Code:* `subscription.request(1);`
    *   *Giải thích:* **Backpressure cốt lõi nằm ở đây.** Thay vì bảo Publisher gửi hết, Subscriber nói: *"Tôi sẵn sàng nhận 1 phần tử thôi"*. Publisher sẽ **chờ** cho đến khi có lệnh `request()` tiếp theo mới gửi dữ liệu mới.
*   **Code `onNext(Integer item)`**:
    *   *Lý thuyết:* Hàm nhận dữ liệu.
    *   *Code:* `subscription.request(1);` nằm ở cuối hàm.
    *   *Giải thích:* Khi nhận được 1 item và in ra xong, Subscriber lại báo: *"Tôi đã xử lý xong, cho tôi xin thêm 1 cái nữa"*. Nếu bạn comment dòng `request(1)` này đi, chương trình sẽ chỉ in ra số `1` duy nhất rồi dừng lại vì Subscriber "quên" đòi thêm dữ liệu.
*   **Code `onError` & `onComplete`**:
    *   *Lý thuyết:* Xử lý khi luồng kết thúc có lỗi hoặc thành công.

**B. Class `FlowApiExample` (Hàm `main`)**
*   **Lý thuyết:** Chúng ta dùng `SubmissionPublisher`. Đây là class có sẵn trong Java, triển khai sẵn interface `Flow.Publisher`. Nó tự động quản lý việc gửi dữ liệu bất đồng bộ bằng `Executor` (mặc định là `ForkJoinPool`).
*   **Code `publisher.subscribe(subscriber);`**:
    *   *Giải thích:* Gắn kết Publisher và Subscriber. Ngay lập tức, hàm `onSubscribe` bên Subscriber sẽ được kích hoạt.
*   **Code `publisher.submit(i);`**:
    *   *Lý thuyết:* Đưa dữ liệu vào luồng (Stream).
    *   *Giải thích:* Publisher đẩy số 1, 2, 3, 4, 5 vào. Tuy nhiên, nhờ cơ chế Backpressure ở trên, dù vòng lặp `for` chạy rất nhanh đẩy hết 5 số, nhưng Subscriber cứ nhận 1 số, xử lý in ra xong, mới xin số tiếp theo.
*   **Code `publisher.close();`**:
    *   *Giải thích:* Báo cho Subscriber biết là Publisher đã đóng, không còn gì để gửi nữa. Lúc này hàm `onComplete()` bên Subscriber sẽ chạy.
*   **Code `Thread.sleep(1000);`**:
    *   *Giải thích:* Lập trình phản ứng là **bất đồng bộ (Asynchronous)**. Hàm `submit()` chạy trên một luồng (thread) khác. Nếu không có `Thread.sleep()`, luồng `main` sẽ kết thúc ngay lập tức và tiến trình Java bị hủy trước khi Subscriber kịp in ra màn hình.

**Kết quả khi chạy:**
```text
Publisher bắt đầu phát dữ liệu...
Khách Hàng A đã đăng ký nhận dữ liệu.
Khách Hàng A nhận được: 1
Khách Hàng A nhận được: 2
Khách Hàng A nhận được: 3
Khách Hàng A nhận được: 4
Khách Hàng A nhận được: 5
Khách Hàng A đã nhận xong toàn bộ dữ liệu.
```

### TỔNG KẾT
Khóa học này đang dạy bạn nền tảng cốt lõi của **Reactive Streams** trong Java. Khi bạn đã hiểu `Flow API` (Publisher, Subscriber, Subscription, Backpressure), việc học các thư viện cao cấp hơn như **Project Reactor** (với `Mono` và `Flux`) hay **RxJava** sẽ trở nên cực kỳ dễ dàng, vì chúng đều được xây dựng dựa trên tiêu chuẩn này.

Bạn có muốn tôi viết thêm ví dụ về `Processor` (bộ xử lý trung gian) không?

Chào bạn, đây là hai file code rất cụ thể để minh họa cho lý thuyết **Flow API** mà chúng ta đã thảo luận ở trên. 

Tôi sẽ giải thích chi tiết từng file, từng dòng code, và chỉ ra cách nó liên kết trực tiếp với lý thuyết (đặc biệt là cơ chế **Backpressure**).

---

### 1. File `FlowApiExample.java` (Đóng vai trò là Publisher / Main)

File này chứa hàm `main`, nơi khởi tạo luồng dữ liệu và kích hoạt quá trình.

```java
package reactive_programming_in_java_flow_api_reactive_streams;

import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

public class FlowApiExample {

    public static void main(String[] args) throws InterruptedException {
        // 1. Tạo một SubmissionPublisher
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();

        // 2. Tạo một custom Subscriber (thực chất ở file 2 là Subscriber)
        MySubscriber subscriber = new MySubscriber();

        // 3. Đăng ký Subscriber vào Publisher
        publisher.subscribe(subscriber);
        
        // 4. Đợi 1 giây
        TimeUnit.SECONDS.sleep(1);
        
        // 5. Phát (emit) dữ liệu từ 1 đến 5
        for (int i = 1; i <= 5; i++) {
            System.out.println("Publisher: Emitting item " + i);
            publisher.submit(i);
        }

        // 6. Đóng Publisher để báo hiệu hoàn thành
        publisher.close();
    }
}
```

**Giải thích chi tiết & Lý thuyết:**
*   **`SubmissionPublisher`**: Đây là class có sẵn trong Java (từ Java 9), implements sẵn interface `Flow.Publisher`. Nó tự động quản lý việc gửi dữ liệu **bất đồng bộ (asynchronous)**.
*   **`publisher.subscribe(subscriber)`**: Lúc này, **Sự đăng ký (Subscription)** được thiết lập. Ngay lập tức, phương thức `onSubscribe` bên trong `MySubscriber` sẽ được kích hoạt tự động.
*   **`TimeUnit.SECONDS.sleep(1)`**: Trong lập trình phản ứng, quá trình subscribe diễn ra bất đồng bộ. Dòng này (dù hơi thủ công) giúp đảm bảo Subscriber đã kết nối xong xuôi trước khi Publisher bắt đầu "bắn" dữ liệu.
*   **`publisher.submit(i)`**: Đưa các số 1, 2, 3, 4, 5 vào luồng dữ liệu (Stream). 
    *   *Lưu ý quan trọng:* Vòng lặp `for` này chạy **rất nhanh**, nó đẩy hết 5 số vào gần như ngay lập tức. Tuy nhiên, nhờ có cơ chế Backpressure ở file 2, Subscriber sẽ nhận từ từ từng số một.
*   **`publisher.close()`**: Báo hiệu cho Subscriber biết rằng "Tôi không còn dữ liệu nào để gửi nữa đâu". Lúc này, hàm `onComplete()` bên Subscriber sẽ được gọi.

---

### 2. File `MySubscriber.java` (Đóng vai trò là Subscriber)

File này định nghĩa cách tiếp nhận và xử lý dữ liệu.

```java
package reactive_programming_in_java_flow_api_reactive_streams;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

// Custom Subscriber implementing the Subscriber interface
class MySubscriber implements Subscriber<Integer> {

    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println("Subscriber: Subscribed");
        System.out.println(subscription);
        this.subscription = subscription;
        subscription.request(1); // Yêu cầu phần tử đầu tiên ngay khi đăng ký
    }

    @Override
    public void onNext(Integer item) {
        System.out.println("Subscriber: Received item " + item);
        
        // Chuyển đổi số nguyên thành chuỗi (nhân đôi giá trị) và xử lý tiếp
        submit(String.valueOf(item * 2));
        
        subscription.request(1); // Yêu cầu phần tử tiếp theo
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Subscriber: Error - " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Subscriber: Completed");
        subscription.cancel(); // Hủy đăng ký khi xong việc
    }

    // Phương thức phụ (helper) để xử lý item sau khi biến đổi
    private void submit(String item) {
        System.out.println("Subscriber: Emitting item " + item);
    }
}
```

**Giải thích chi tiết & Lý thuyết:**
*(Lưu ý nhỏ: Comment trong code ghi là "Custom Processor" nhưng thực tế class này chỉ `implements Subscriber<Integer>`. Nó đóng vai trò như một Subscriber có thêm logic biến đổi dữ liệu ở giữa, chứ chưa phải là một Processor thực thụ vì Processor phải implements cả `Subscriber` và `Publisher`.)*

*   **`implements Subscriber<Integer>`**: Khẳng định class này là một "người tiêu thụ" dữ liệu kiểu `Integer`.
*   **`onSubscribe(Subscription subscription)`**:
    *   *Lý thuyết:* Được gọi đầu tiên khi kết nối thành công. `Subscription` chính là cầu nối để điều khiển luồng.
    *   *Code:* Lưu lại `subscription` và gọi `subscription.request(1)`.
    *   *Giải thích:* Đây là **cốt lõi của Backpressure**. Subscriber nói với Publisher: *"Tôi đã sẵn sàng, hãy gửi cho tôi đúng 1 phần tử thôi nhé!"*. Publisher sẽ **dừng lại và chờ** cho đến khi có lệnh `request` tiếp theo.
*   **`onNext(Integer item)`**:
    *   *Lý thuyết:* Được gọi mỗi khi Publisher gửi một phần tử mới đến.
    *   *Code:* Nhận `item` (ví dụ: 1). In ra màn hình. Gọi hàm `submit(String.valueOf(item * 2))` (tức là in ra số 2). Sau đó gọi `subscription.request(1)`.
    *   *Giải thích:* Khi Subscriber đã xử lý xong phần tử hiện tại, nó mới "xin" thêm phần tử tiếp theo. Nếu bạn xóa dòng `subscription.request(1)` ở đây đi, chương trình sẽ chỉ in ra số 1 (và số 2) rồi dừng lại mãi mãi, dù Publisher có đẩy 4 số còn lại vào. Đây chính là việc **ngăn chặn quá tải (Buffer Overflow)**.
*   **`onError(Throwable throwable)`**: Xử lý khi có lỗi xảy ra trong luồng.
*   **`onComplete()`**: 
    *   *Lý thuyết:* Được gọi khi Publisher gọi `close()`.
    *   *Code:* In ra thông báo hoàn thành và gọi `subscription.cancel()` để dọn dẹp tài nguyên.
*   **`private void submit(String item)`**: Đây không phải là hàm chuẩn của Flow API, mà là hàm tự viết thêm để mô phỏng việc "biến đổi dữ liệu" (giống như một bước xử lý trung gian).

---

### 3. Luồng thực thi thực tế (Kết quả khi chạy)

Khi bạn chạy chương trình, màn hình sẽ in ra theo thứ tự như sau:

```text
Publisher: Emitting item 1
Subscriber: Subscribed
java.util.concurrent.SubmissionPublisher$...
Publisher: Emitting item 2
Subscriber: Received item 1
Subscriber: Emitting item 2
Publisher: Emitting item 3
Publisher: Emitting item 4
Publisher: Emitting item 5
Subscriber: Received item 2
Subscriber: Emitting item 4
Subscriber: Received item 3
Subscriber: Emitting item 6
Subscriber: Received item 4
Subscriber: Emitting item 8
Subscriber: Received item 5
Subscriber: Emitting item 10
Subscriber: Completed
```

**Phân tích kết quả:**
1.  Vòng lặp `for` ở `main` chạy rất nhanh, in ra "Emitting item 1 -> 5" gần như ngay lập tức. Điều này chứng minh `publisher.submit()` là **non-blocking** (không chặn luồng main).
2.  Tuy nhiên, Subscriber chỉ nhận được item 1, xử lý in ra (Emitting item 2), rồi mới nhận item 2, xử lý in ra (Emitting item 4)... Cứ thế cho đến hết.
3.  Khi Publisher đóng (`close()`), Subscriber mới nhận được tín hiệu `onComplete`.

### Tổng kết
Đoạn code này là một minh chứng tuyệt vời cho **Reactive Streams**:
*   **Bất đồng bộ:** Publisher và Subscriber chạy trên các luồng (thread) khác nhau.
*   **Backpressure:** Thể hiện rõ qua việc Subscriber gọi `request(1)` sau mỗi lần nhận dữ liệu, đảm bảo nó không bị "ngộp" bởi tốc độ phát của Publisher.

Bạn đã nắm rất rõ phần này rồi đấy! Nếu cần ví dụ về **Processor** (đóng vai trò vừa nhận vừa phát) hay chuyển sang thư viện **Project Reactor** (Mono/Flux) thì cứ báo tôi nhé.

Chào bạn, chúng ta tiếp tục với slide tiếp theo trong khóa học. Slide lần này nói về **Processor Interface** – một thành phần cực kỳ quan trọng để xây dựng nên các "đường ống" (pipeline) xử lý dữ liệu phức tạp.

Dưới đây là phần giải thích chi tiết, lý thuyết, ví dụ đời sống và code thực tế cho slide này.

---

### PHẦN 1: GIẢI THÍCH CHI TIẾT HÌNH ẢNH (Slide "Processor Interface")

Slide này cung cấp cái nhìn tổng quan về Interface `Flow.Processor` trong Java:

*   **Overview (Tổng quan):**
    *   Cho phép một class đóng vai trò **vừa là Publisher, vừa là Subscriber**. (Nó nhận dữ liệu từ phía trước và phát dữ liệu ra phía sau).
    *   Rất hữu ích để xây dựng các **chuỗi xử lý (processing chains)** trong reactive streams.
*   **Use Cases (Các trường hợp sử dụng):**
    1.  **Transforming or Processing Data:** Áp dụng các logic biến đổi, lọc dữ liệu trước khi giao cho Subscriber cuối cùng (ví dụ: nhân đôi số, chuyển chữ thường thành chữ hoa).
    2.  **Connecting Different Parts of a Reactive Pipeline:** Kết nối các giai đoạn lại với nhau. Nó nhận dữ liệu từ "thượng nguồn" (upstream) và phát dữ liệu đã xử lý xuống "hạ nguồn" (downstream).
    3.  **Implementing a Custom Flow Control Mechanism:** Quản lý Backpressure và kiểm soát tốc độ tiêu thụ dữ liệu.
    4.  **Aggregating or Combining Data Streams:** Gộp hoặc kết hợp dữ liệu từ nhiều Publisher trước khi gửi đi.
*   **Thông tin Interface (JavaDoc):**
    *   `Interface Flow.Processor<T,R>`
    *   **T**: Kiểu dữ liệu mà nó nhận vào (Subscribed item type).
    *   **R**: Kiểu dữ liệu mà nó phát ra (Published item type).
    *   **Superinterfaces:** Nó kế thừa từ cả `Flow.Publisher<R>` và `Flow.Subscriber<T>`.

---

### PHẦN 2: LÝ THUYẾT VÀ VÍ DỤ ĐỜI SỐNG

**Ví dụ đời sống: Dây chuyền sản xuất nước cam**

Hãy tưởng tượng một dây chuyền:
1.  **Publisher (Nông dân):** Cung cấp cam tươi (Kiểu dữ liệu `T` - Cam).
2.  **Processor (Máy ép nước cam):** Nhận cam từ nông dân (đóng vai Subscriber), ép thành nước cam (biến đổi dữ liệu), rồi bơm nước cam ra bình (đóng vai Publisher phát ra kiểu dữ liệu `R` - Nước cam).
3.  **Subscriber (Khách hàng):** Nhận nước cam và uống.

**Điểm mấu chốt:**
*   Máy ép (Processor) đứng ở giữa. Nó phải "kết nối" với nông dân (upstream) để lấy cam, và "kết nối" với khách hàng (downstream) để đưa nước cam.
*   Nếu khách hàng uống chậm (Backpressure), máy ép cũng phải ép chậm lại và báo cho nông dân giao cam ít đi.

---

### PHẦN 3: VÍ DỤ CODE VÀ GIẢI THÍCH CHI TIẾT

*Lưu ý: Trong bài trước, file `MySubscriber` của bạn có comment ghi là "Custom Processor" nhưng thực chất nó chỉ là Subscriber. Lần này, chúng ta sẽ viết một **Processor thực thụ** nhé.*

**Mục tiêu:** Viết một `TransformProcessor` nhận vào số `Integer`, nhân nó với 10, chuyển thành chuỗi `String` (ví dụ: "Giá trị: 20") và gửi cho Subscriber cuối cùng.

#### 1. Code ví dụ

```java
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

// 1. Tạo một Processor tùy chỉnh (Vừa nhận Integer, vừa phát String)
class TransformProcessor implements Flow.Processor<Integer, String> {
    
    private Flow.Subscription upstreamSubscription; // Cầu nối với Publisher phía trên
    private Flow.Subscriber<? super String> downstreamSubscriber; // Subscriber phía dưới

    // --- PHẦN PUBLISHER (Phát dữ liệu xuống dưới) ---
    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {
        this.downstreamSubscriber = subscriber;
        
        // Cung cấp một Subscription cho Subscriber phía dưới
        // Để khi Subscriber request(n), Processor sẽ chuyển yêu cầu đó lên trên
        subscriber.onSubscribe(new Flow.Subscription() {
            @Override
            public void request(long n) {
                if (upstreamSubscription != null) {
                    upstreamSubscription.request(n);
                }
            }
            @Override
            public void cancel() {
                if (upstreamSubscription != null) {
                    upstreamSubscription.cancel();
                }
            }
        });
    }

    // --- PHẦN SUBSCRIBER (Nhận dữ liệu từ trên xuống) ---
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.upstreamSubscription = subscription;
        // Không request vội, đợi Subscriber phía dưới request thì mới kéo dữ liệu
    }

    @Override
    public void onNext(Integer item) {
        // BƯỚC BIẾN ĐỔI DỮ LIỆU (Transforming)
        System.out.println("Processor: Nhận " + item + " từ Upstream. Đang xử lý...");
        String processedItem = "Giá trị đã x10: " + (item * 10);
        
        // Phát dữ liệu đã biến đổi xuống Downstream
        if (downstreamSubscriber != null) {
            downstreamSubscriber.onNext(processedItem);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (downstreamSubscriber != null) downstreamSubscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        if (downstreamSubscriber != null) downstreamSubscriber.onComplete();
    }
}

// 2. Subscriber cuối cùng (Chỉ nhận String)
class FinalSubscriber implements Flow.Subscriber<String> {
    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        System.out.println("FinalSubscriber: Đã đăng ký.");
        subscription.request(1); // Yêu cầu 1 phần tử đầu tiên
    }

    @Override
    public void onNext(String item) {
        System.out.println("FinalSubscriber: Nhận được -> " + item);
        // Xử lý xong thì xin thêm 1 phần tử (Backpressure)
        subscription.request(1); 
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Lỗi: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("FinalSubscriber: Đã nhận xong toàn bộ dữ liệu.");
    }
}

// 3. Chương trình chạy chính
public class ProcessorExample {
    public static void main(String[] args) throws InterruptedException {
        // Tạo Publisher gốc
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();
        
        // Tạo Processor
        TransformProcessor processor = new TransformProcessor();
        
        // Tạo Subscriber cuối
        FinalSubscriber finalSubscriber = new FinalSubscriber();

        // NỐI CHUỖI: Publisher -> Processor -> Subscriber
        publisher.subscribe(processor);
        processor.subscribe(finalSubscriber);

        System.out.println("Bắt đầu phát dữ liệu...");
        // Publisher phát dữ liệu số nguyên
        for (int i = 1; i <= 3; i++) {
            publisher.submit(i);
        }

        // Đóng publisher
        publisher.close();
        
        // Đợi bất đồng bộ chạy xong
        Thread.sleep(1000);
    }
}
```

#### 2. Giải thích Code chi tiết và Liên kết Lý thuyết

**A. Class `TransformProcessor implements Flow.Processor<Integer, String>`**
*   **Lý thuyết (Từ Slide):** Kế thừa cả `Publisher<String>` và `Subscriber<Integer>`.
*   **Code `subscribe(Flow.Subscriber<? super String> subscriber)`**: 
    *   Đây là hành vi của **Publisher**.
    *   *Giải thích:* Khi Subscriber cuối cùng đăng ký với Processor, Processor lưu lại `downstreamSubscriber`. Đồng thời, nó tạo ra một `Subscription` giả để đưa cho Subscriber. Khi Subscriber cuối gọi `request(n)`, Processor sẽ chuyển tiếp lệnh đó lên `upstreamSubscription` (tức là báo cho Publisher gốc gửi dữ liệu).
*   **Code `onNext(Integer item)`**:
    *   Đây là hành vi của **Subscriber**.
    *   *Lý thuyết (Từ Slide):* "Transforming or Processing Data".
    *   *Giải thích:* Nhận `item` (số nguyên). Thực hiện phép nhân `item * 10`. Sau đó gọi `downstreamSubscriber.onNext(processedItem)` để đẩy chuỗi kết quả xuống dưới. Đây chính là lúc "kết nối các phần của đường ống" (Connecting pipeline).

**B. Luồng chạy của chương trình `main`**
*   **Code `publisher.subscribe(processor);`**: Nối Đầu (Publisher) với Trạm trung chuyển (Processor).
*   **Code `processor.subscribe(finalSubscriber);`**: Nối Trạm trung chuyển với Đích (Subscriber).
*   **Code `publisher.submit(i);`**: Publisher gửi số 1, 2, 3.

**C. Kết quả khi chạy (Bạn sẽ thấy rõ sự biến đổi):**
```text
FinalSubscriber: Đã đăng ký.
Bắt đầu phát dữ liệu...
Processor: Nhận 1 từ Upstream. Đang xử lý...
FinalSubscriber: Nhận được -> Giá trị đã x10: 10
Processor: Nhận 2 từ Upstream. Đang xử lý...
FinalSubscriber: Nhận được -> Giá trị đã x10: 20
Processor: Nhận 3 từ Upstream. Đang xử lý...
FinalSubscriber: Nhận được -> Giá trị đã x10: 30
FinalSubscriber: Đã nhận xong toàn bộ dữ liệu.
```

### TỔNG KẾT
Slide này giải thích về **Processor** - trái tim của các đường ống Reactive. Nhờ có Processor, bạn có thể xếp chồng các bước xử lý: `Lọc -> Biến đổi -> Gộp -> ...` giống như các trạm trên một dây chuyền lắp ráp mà vẫn đảm bảo cơ chế **Backpressure** (Subscriber cuối cùng không bị ngộp dữ liệu).

Trong thực tế khi làm việc với **Project Reactor** (Spring WebFlux), các hàm như `.map()`, `.filter()`, `.flatMap()` bản chất chính là các `Processor` được viết sẵn dưới nắp ca-pô, giúp bạn không phải viết những đoạn code dài dòng như trên.

Bạn có muốn tôi demo thêm cách làm điều tương tự (biến đổi dữ liệu) bằng **Project Reactor** (Mono/Flux) để thấy nó ngắn gọn và mạnh mẽ như thế nào không?