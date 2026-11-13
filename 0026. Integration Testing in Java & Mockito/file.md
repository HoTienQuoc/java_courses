Chào bạn, dựa trên 9 hình ảnh bạn gửi từ khóa học Udemy về **"Integration Testing trong Java và Mockito"**, mình sẽ giải thích chi tiết từng bức ảnh, sau đó hệ thống lại lý thuyết bằng ví dụ thực tế dễ hiểu, và cuối cùng là cung cấp code Java chi tiết kèm giải thích từng dòng cho bạn nhé.

---

### PHẦN 1: GIẢI THÍCH CHI TIẾT TỪNG HÌNH ẢNH

*   **Hình 1 (Agenda):** Đây là chương trình nghị sự của bài học. Bạn sẽ được học: Kiểm thử tích hợp (Integration testing) là gì, so sánh với Kiểm thử đơn vị (Unit testing), Test Doubles là gì, lợi ích của Test Doubles trong Java, các loại Integration testing, và các Framework phổ biến.
*   **Hình 2 (Định nghĩa):** Trích dẫn định nghĩa: *"Kiểm thử tích hợp là quá trình mà các đơn vị (units) riêng lẻ được kiểm thử cùng nhau để xem chúng có tương tác với nhau đúng như mong đợi hay không."*
*   **Hình 3 (Ví dụ):** Minh họa luồng gửi email. Máy khách (Client) gửi yêu cầu đến Mail Server, Mail Server lấy mẫu từ Mail Template Engine, sau đó gửi đi. Integration Test ở đây là đảm bảo 3 thành phần này "nói chuyện" được với nhau, chứ không chỉ test riêng lẻ từng cái.
*   **Hình 4 (Unit vs Integration):** Bảng so sánh.
    *   **Unit Testing:** Test code của một đơn vị duy nhất, dùng JUnit/Mock, lỗi do code, nằm ở tầng 1 (đáy) của Kim tự tháp kiểm thử.
    *   **Integration Testing:** Test sự tương tác giữa các đơn vị, dùng DB thật/dịch vụ bên thứ 3, lỗi có thể do cấu hình môi trường, nằm ở tầng 2 của Kim tự tháp.
*   **Hình 5 (Test Doubles):** Khái niệm "Đóng thế". Thay vì gọi trực tiếp vào Database hay Cloud (chậm, phức tạp), ta dùng các "Test Double" (đồ giả) để thay thế, giúp code test có thể chạy độc lập.
*   **Hình 6 (Phân loại Test Doubles):** Có 5 loại chính: Dummy object (đối tượng rỗng), Test Stub (trả về dữ liệu giả), Fake Object (có logic thật nhưng đơn giản hơn), Mock object (kiểm tra xem hàm có được gọi không), Test spy (ghi lại các lời gọi).
*   **Hình 7 (Lợi ích của Test Doubles):** Cô lập code cần test, tăng tốc độ chạy test, làm cho kết quả test mang tính quyết định (deterministic - chạy 100 lần vẫn ra 1 kết quả), và cho phép giả lập các điều kiện đặc biệt (ví dụ: giả lập lỗi mạng).
*   **Hình 8 (Các loại Integration Testing):**
    *   **Incremental (Tăng dần):** Test từng phần (Top-Down: từ trên xuống, Bottom-Up: từ dưới lên).
    *   **Non-Incremental (Không tăng dần):** Big-Bang Method (Lắp ráp tất cả mọi thứ lại rồi test một lần).
*   **Hình 9 (Frameworks):** Các công cụ hỗ trợ: JUnit, DbUnit (test DB), EasyMock, Mockito (phổ biến nhất), MockFtpServer.

---

### PHẦN 2: LÝ THUYẾT VÀ VÍ DỤ DỄ HIỂU

Để dễ hiểu, hãy tưởng tượng bạn đang **lắp ráp một chiếc Máy tính (PC)**:

1.  **Unit Testing (Kiểm thử đơn vị):** Bạn mang từng linh kiện ra test riêng. Cắm CPU vào mainboard test xem có chạy không, test thanh RAM xem có lỗi không. (Test 1 hàm, 1 class).
    *   *Ví dụ:* Test hàm `tinhTong(a, b)` xem có trả về đúng `a+b` không.
2.  **Integration Testing (Kiểm thử tích hợp):** Bạn lắp RAM, CPU, VGA lên Mainboard, cắm điện và bật máy. Bạn kiểm tra xem khi có điện, Mainboard có nhận đủ RAM không, VGA có xuất hình ra màn hình không. (Test sự tương tác giữa các class/module).
    *   *Ví dụ:* Test chức năng "Đăng ký tài khoản". Bạn cần test xem `UserController` có gọi đúng `UserService` không, và `UserService` có lưu được vào `Database` không. Cả 3 thành phần này phải chạy với nhau.
3.  **Test Doubles (Kỹ thuật đóng thế):** Khi lắp PC, bạn sợ cắm điện thật vào Mainboard lỡ chập mạch. Bạn dùng một **"Bộ nguồn giả"** (Test Double) để cấp điện thử. Bộ nguồn giả này báo đèn xanh y hệt nguồn thật.
    *   *Ví dụ:* Khi test `UserService`, thay vì kết nối vào Database thật (rất chậm và có thể bị xóa dữ liệu), ta dùng **Mockito** tạo ra một `UserRepository` giả. Khi code gọi `userRepo.save()`, Mockito sẽ giả vờ lưu thành công và trả về kết quả, giúp test chạy cực nhanh mà không cần DB thật.
4.  **Các loại Integration Test:**
    *   **Big-Bang:** Mua tất cả linh kiện về, lắp ráp 1 lần xong bật máy. (Dễ lỗi, khó tìm nguyên nhân).
    *   **Top-Down:** Lắp Mainboard và CPU trước (phần cao nhất), sau đó lắp dần xuống RAM, Ổ cứng.
    *   **Bottom-Up:** Lắp Ổ cứng, RAM trước, sau đó mới gắn lên Mainboard.

---

### PHẦN 3: CODE VÍ DỤ CHI TIẾT & GIẢI THÍCH (JAVA + JUNIT + MOCKITO)

Giả sử ta có bài toán: Quản lý người dùng.
*   `User`: Class chứa thông tin.
*   `UserRepository`: Class giao tiếp với Database.
*   `UserService`: Class chứa logic nghiệp vụ, gọi đến Repository.

#### 1. Code cho Unit Testing với Mockito (Test Doubles - Hình 5, 6, 7)
**Mục đích:** Test logic của `UserService` mà không cần kết nối Database thật.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

// Kích hoạt Mockito
@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    // @Mock: Tạo ra một "Test Double" (đối tượng giả) cho UserRepository
    @Mock
    private UserRepository userRepository;

    // @InjectMocks: Tạo UserService thật và tiêm (inject) cái Mock ở trên vào
    @InjectMocks
    private UserService userService;

    @Test
    public void testRegisterUser_Success() {
        // 1. ARRANGE (Chuẩn bị dữ liệu)
        User user = new User("test@example.com", "123456");
        
        // Lý thuyết: Đây là "Test Stub". Giả lập hàm save() của Repository 
        // luôn trả về user đó, thay vì gọi xuống DB thật.
        when(userRepository.save(any(User.class))).thenReturn(user);

        // 2. ACT (Thực thi hàm cần test)
        User registeredUser = userService.registerUser("test@example.com", "123456");

        // 3. ASSERT (Kiểm tra kết quả)
        assertNotNull(registeredUser);
        assertEquals("test@example.com", registeredUser.getEmail());

        // Lý thuyết: Đây là "Mock object" - verify xem hàm save() có được gọi đúng 1 lần không
        verify(userRepository, times(1)).save(any(User.class));
    }
}
```
*   **Giải thích Code + Lý thuyết bên cạnh:**
    *   `@Mock`: Đóng vai trò là **Test Double**. Ta không dùng DB thật, ta dùng một đối tượng giả `userRepository`.
    *   `when(...).thenReturn(...)`: Đây là kỹ thuật **Test Stub**. Nếu ai gọi hàm `save`, đừng chọc vào DB, cứ trả về `user` này cho tôi. Giúp tăng tốc độ (Advantage 1) và cô lập code (Advantage 2).
    *   `verify(...)`: Đây là **Mock Object**. Nó kiểm tra xem `UserService` có thực sự gọi `userRepository.save()` không, gọi mấy lần. Điều này cực kỳ hữu ích để đảm bảo sự tương tác giữa các class.

#### 2. Code cho Integration Testing (Hình 2, 3, 4)
**Mục đích:** Test sự tương tác giữa `UserService` và `UserRepository` (và Database). Ở đây mình dùng H2 Database (in-memory) để test.

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest: Khởi động toàn bộ môi trường Spring (giống như chạy app thật)
@SpringBootTest
@Transactional // Giúp rollback dữ liệu sau khi test xong, không làm bẩn DB
public class UserServiceIntegrationTest {

    // Tiêm Service và Repository THẬT (không phải Mock)
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testRegisterUser_Integration() {
        // 1. ACT: Gọi hàm Service thật
        User registeredUser = userService.registerUser("integration@test.com", "pass123");

        // 2. ASSERT: Kiểm tra xem user đã được lưu vào DB thật chưa
        assertNotNull(registeredUser.getId()); // Có ID tức là đã lưu vào DB
        
        // Tìm kiếm lại trong Database thật (thông qua Repository)
        User foundUser = userRepository.findByEmail("integration@test.com");
        assertNotNull(foundUser);
        assertEquals("pass123", foundUser.getPassword());
        
        // Lý thuyết: Lỗi ở đây có thể do code (sai logic) HOẶC do môi trường 
        // (VD: DB chưa bật, sai cấu hình kết nối, thiếu driver...). 
        // Đây chính là đặc điểm của Integration Test (Hình 4).
    }
}
```
*   **Giải thích Code + Lý thuyết bên cạnh:**
    *   `@SpringBootTest`: Không còn là test đơn lẻ nữa. Nó khởi tạo Spring Context, kết nối thẳng vào Database (Hình 4: *"Using of real database"*).
    *   `@Autowired`: Lấy các Bean thật. `UserService` sẽ gọi `UserRepository` thật, và `UserRepository` sẽ chạy câu lệnh SQL thật xuống DB (H2).
    *   **Lý thuyết:** Tại sao phải làm bước này? Vì ở Unit Test, ta dùng Mockito giả lập `save()` trả về thành công. Nhưng nhỡ đâu code SQL của bạn bị sai (ví dụ: thiếu cột, sai kiểu dữ liệu), thì Unit Test vẫn Pass, nhưng khi chạy thật (Integration Test) sẽ văng lỗi. Đây là lúc Integration Test phát huy tác dụng (Hình 3: Test luồng Mail gửi đi).

#### 3. Code minh họa các loại Test Doubles khác (Hình 6)

```java
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class TestDoublesExample {

    @Test
    public void testDifferentDoubles() {
        // 1. DUMMY OBJECT: Đối tượng rỗng, chỉ để truyền vào cho đủ tham số
        User dummyUser = new User(); // Không có data, không dùng đến

        // 2. TEST STUB: Trả về dữ liệu giả lập cố định
        UserRepository stubRepo = mock(UserRepository.class);
        when(stubRepo.findById(1L)).thenReturn(new User("stub@gmail.com", "123"));

        // 3. FAKE OBJECT: Có logic thật nhưng đơn giản hơn (VD: Dùng List thay vì DB)
        // FakeUserRepository là một class tự viết, lưu dữ liệu vào Map/List trong bộ nhớ.
        
        // 4. MOCK OBJECT: Kiểm tra xem hàm có được gọi không
        verify(stubRepo, never()).delete(any()); // Đảm bảo hàm delete không bao giờ được gọi

        // 5. TEST SPY: Ghi lại các lời gọi hàm, nhưng vẫn chạy logic thật
        // (Thường dùng khi muốn test 1 phần của class thật)
    }
}
```

---

### TỔNG KẾT

*   **Unit Test:** Test 1 mình, dùng **Mockito** để giả lập mọi thứ xung quanh (Test Doubles). Chạy siêu nhanh, nằm ở đáy kim tự tháp.
*   **Integration Test:** Test sự kết hợp, dùng DB thật (hoặc in-memory DB như H2), test xem các class có "bắt tay" đúng không. Nằm ở giữa kim tự tháp.
*   **Frameworks:** **JUnit** để chạy test, **Mockito** để tạo Test Doubles (Hình 9).

Hy vọng với lời giải thích và các đoạn code trên, bạn đã nắm rõ được toàn bộ nội dung trong 9 bức ảnh của khóa học! Nếu cần giải thích thêm phần nào, cứ phản hồi nhé.

Chào bạn, hai hình ảnh bạn gửi là một phần trong khóa học "Java from Zero to First Job" trên Udemy, giới thiệu về **Mockito** – một thư viện mô phỏng (mocking) phổ biến nhất trong Java để viết unit test hiệu quả. Dưới đây là phần giải thích chi tiết theo đúng yêu cầu của bạn.

---

## 🖼️ Giải thích chi tiết từng hình ảnh

### Hình 1: Slide "AGENDA" – Lộ trình học Mockito
Đây là slide tổng quan về các chủ đề sẽ được học trong phần này:
1.  **Mockito Library: Overview** – Giới thiệu tổng quan về thư viện Mockito.
2.  **Set up environment with Mockito** – Cấu hình môi trường (thêm dependency Maven/Gradle) để sử dụng Mockito.
3.  **Mockito Extension for JUnit** – Tích hợp Mockito với JUnit 5 thông qua `@ExtendWith(MockitoExtension.class)`.
4.  **How to create mocks** – Các cách tạo đối tượng giả lập: dùng `@Mock` hoặc `Mockito.mock()`.
5.  **Verify Behavior on mocks** – Kiểm tra xem một phương thức của mock đã được gọi hay chưa, gọi bao nhiêu lần, với tham số gì.
6.  **@InjectMocks** – Tự động tiêm (inject) các mock vào lớp cần test.
7.  **Argument Matchers in Mockito** – Sử dụng các bộ so khớp tham số linh hoạt như `any()`, `eq()`, `anyString()`.
8.  **Stub exceptions** – Cấu hình cho mock ném ra ngoại lệ (exception) khi được gọi.
9.  **Argument captor** – "Bắt" lại các tham số đã được truyền vào mock để kiểm tra sau đó.
10. **Real-life examples** – Các ví dụ thực tế.

### Hình 2: Trang Maven Repository cho `mockito-core`
- Hình này minh họa bước **"Set up environment"** trong Hình 1.
- Trang web hiển thị mã XML để thêm thư viện `mockito-core` phiên bản `3.7.7` vào file `pom.xml` của dự án Maven:
    ```xml
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>3.7.7</version>
        <scope>test</scope>
    </dependency>
    ```
- **Giải thích:** `groupId` là tổ chức (org.mockito), `artifactId` là tên thư viện (mockito-core), `version` là phiên bản, và `<scope>test</scope>` đảm bảo thư viện này chỉ được dùng trong quá trình kiểm thử, không ảnh hưởng đến mã nguồn chính khi chạy thực tế.

---

## 📚 Lý thuyết và Ví dụ Code chi tiết từng chủ đề

### 1. Tổng quan về Mockito
**Lý thuyết:** Mockito giúp tạo ra các **đối tượng giả (mock object)** để thay thế các dependency thật (như database, API, email service...) trong unit test. Điều này giúp cô lập lớp cần kiểm thử, làm test chạy nhanh hơn, ổn định hơn và không phụ thuộc vào môi trường bên ngoài. Mock object có các phương thức giống hệt đối tượng thật nhưng phần thân phương thức rỗng, trả về giá trị mặc định (`null`, `0`, `false`) trừ khi bạn lập trình cho nó.

### 2. Cấu hình môi trường (Maven)
**Lý thuyết:** Để dùng Mockito với JUnit 5, bạn nên thêm dependency `mockito-junit-jupiter`. Thư viện này bao gồm cả `mockito-core` và extension giúp các annotation như `@Mock`, `@InjectMocks` hoạt động tự động.

**Ví dụ code (pom.xml):**
```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>
<!-- Mockito cho JUnit 5 -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.12.0</version>
    <scope>test</scope>
</dependency>
```
**Giải thích:** Đoạn XML này khai báo hai thư viện cần thiết. `mockito-junit-jupiter` chứa mọi thứ cần thiết để tích hợp Mockito với JUnit 5.

### 3. Mockito Extension cho JUnit
**Lý thuyết:** Để JUnit 5 tự động xử lý các annotation của Mockito, bạn cần gắn `@ExtendWith(MockitoExtension.class)` lên lớp test. Nếu không có extension này, các annotation `@Mock` sẽ không được khởi tạo.

**Ví dụ code:**
```java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    // Các @Mock sẽ hoạt động ở đây
}
```
**Giải thích:** `@ExtendWith` báo cho JUnit 5 rằng hãy dùng `MockitoExtension` để quản lý vòng đời của các mock trong lớp test này.

### 4. Cách tạo Mock (`@Mock` và `Mockito.mock()`)
**Lý thuyết:** Có hai cách tạo mock:
- Dùng annotation `@Mock` (cần extension ở trên).
- Dùng phương thức tĩnh `Mockito.mock(TênClass.class)`.

**Ví dụ code:**
```java
// Cách 1: Dùng annotation
@Mock
private UserRepository userRepository;

// Cách 2: Dùng phương thức
UserRepository anotherMock = Mockito.mock(UserRepository.class);
```
**Giải thích:** `@Mock` tạo một đối tượng giả của `UserRepository`. Mọi lời gọi hàm trên `userRepository` sẽ không thực thi logic thật mà chỉ trả về giá trị mặc định.

### 5. `@InjectMocks` – Tiêm Mock vào lớp cần Test
**Lý thuyết:** `@InjectMocks` tạo một **thể hiện thật** của lớp cần kiểm thử (ví dụ `UserService`) và tự động tiêm các `@Mock` đã khai báo vào nó. Mockito sẽ thử tiêm qua constructor trước, sau đó đến setter, rồi đến field.

**Ví dụ code:**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService; // Đối tượng thật, được tiêm 2 mock trên

    @Test
    void testRegister() {
        // userService đã sẵn sàng với các dependency là mock
    }
}
```
**Giải thích:** `userService` là đối tượng thật của lớp `UserService`. Hai dependency `userRepository` và `emailService` bên trong nó đã được thay bằng mock, giúp cô lập hoàn toàn logic của `UserService` khi test.

### 6. `when().thenReturn()` – Lập trình hành vi (Stubbing)
**Lý thuyết:** Stubbing là hành động "dạy" cho mock biết phải trả về gì khi một phương thức cụ thể được gọi.

**Ví dụ code:**
```java
@Test
void findByIdReturnsUser() {
    User alice = new User(1L, "Alice", "alice@example.com");
    // Khi findById(1L) được gọi -> trả về Optional chứa alice
    when(userRepository.findById(1L)).thenReturn(Optional.of(alice));

    Optional<User> result = userService.findById(1L);
    assertThat(result).isPresent();
}
```
**Giải thích:** Dòng `when(...).thenReturn(...)` nói rằng: "Khi phương thức `findById` của `userRepository` được gọi với tham số `1L`, hãy trả về `Optional.of(alice)` thay vì truy vấn database thật".

### 7. `verify()` – Kiểm chứng hành vi
**Lý thuyết:** `verify()` dùng để khẳng định một phương thức của mock **đã được gọi** với số lần và tham số mong muốn. Đây là cách kiểm tra "hành vi" (behavior verification) thay vì chỉ kiểm tra kết quả trả về.

**Ví dụ code:**
```java
@Test
void verifyEmailSent() {
    userService.register(new User("Bob", "bob@example.com"));

    // Xác nhận emailService.sendWelcomeEmail() đã được gọi đúng 1 lần
    verify(emailService, times(1)).sendWelcomeEmail(any(User.class));
}
```
**Giải thích:** `verify(emailService, times(1))` kiểm tra rằng phương thức `sendWelcomeEmail` của mock `emailService` đã được gọi chính xác một lần. Nếu gọi 0 lần hoặc 2 lần, test sẽ thất bại.

### 8. Argument Matchers – Bộ so khớp tham số
**Lý thuyết:** Khi bạn không muốn chỉ định một giá trị tham số cụ thể, bạn có thể dùng các bộ so khớp linh hoạt như `any()`, `anyString()`, `anyInt()`, `eq()`... trong cả `when()` và `verify()`.

**Ví dụ code:**
```java
// Khi isABigFlower được gọi với bất kỳ số nguyên nào ở tham số thứ 2
when(flowerService.isABigFlower("poppy", anyInt())).thenReturn(true);

// Verify với bộ so khớp
verify(userRepository).insert(any(User.class));
```
**Giải thích:** `anyInt()` khớp với bất kỳ giá trị `int` nào. `any(User.class)` khớp với bất kỳ đối tượng `User` nào được truyền vào. Điều này giúp test linh hoạt hơn khi bạn không quan tâm đến giá trị cụ thể.

### 9. Stub Exceptions – Cấu hình ném ngoại lệ
**Lý thuyết:** Bạn có thể khiến một mock ném ra exception khi được gọi để kiểm tra các nhánh xử lý lỗi của code.

**Ví dụ code:**
```java
@Test
void testFindByIdThrowsException() {
    when(userRepository.findById(99L))
        .thenThrow(new RuntimeException("User not found"));

    assertThrows(RuntimeException.class, () -> {
        userService.findById(99L);
    });
}
```
**Giải thích:** `thenThrow(...)` cấu hình để khi `findById(99L)` được gọi, mock sẽ ném ra `RuntimeException`. Test sau đó dùng `assertThrows` để xác nhận rằng `userService` thực sự ném ra exception này.

### 10. Argument Captor – "Bắt" tham số
**Lý thuyết:** `ArgumentCaptor` cho phép bạn "tóm lấy" đối số mà một phương thức của mock đã nhận, sau đó kiểm tra chi tiết đối số đó bằng `assert`. Nó hữu ích khi bạn muốn xác minh nội dung của một đối tượng phức tạp được truyền vào mock.

**Ví dụ code:**
```java
@Test
void captureEmailArgument() {
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    userService.register(new User("Charlie", "charlie@example.com"));

    // "Bắt" đối số User được truyền vào sendWelcomeEmail
    verify(emailService).sendWelcomeEmail(userCaptor.capture());

    User capturedUser = userCaptor.getValue();
    assertEquals("Charlie", capturedUser.getName());
}
```
**Giải thích:** `userCaptor.capture()` được đặt làm tham số cho `verify`. Sau khi verify chạy, `userCaptor.getValue()` trả về đối tượng `User` thực sự đã được truyền vào phương thức `sendWelcomeEmail`, cho phép bạn kiểm tra tên, email, v.v..

---

## 💎 Tổng kết
Mockito là công cụ không thể thiếu để viết unit test sạch sẽ trong Java. Bằng cách kết hợp `@Mock`, `@InjectMocks`, `when().thenReturn()`, `verify()`, Argument Matchers và Argument Captor, bạn có thể kiểm thử mọi ngóc ngách logic của lớp dịch vụ mà không cần đến database hay các dịch vụ bên ngoài. Hy vọng những giải thích và ví dụ trên giúp bạn nắm vững các khái niệm này. Nếu cần đi sâu vào phần nào, bạn cứ hỏi nhé

Chào bạn, bức ảnh bạn vừa gửi tiếp tục là một slide **"AGENDA" (Chương trình học)** trong khóa học Java trên Udemy, nhưng lần này là phần **Mockito Nâng cao (Advanced Mockito)**. 

Dưới đây là giải thích chi tiết về hình ảnh, lý thuyết, và code ví dụ cho từng chủ đề được liệt kê trong slide.

---

## 🖼️ Giải thích chi tiết hình ảnh
Bức ảnh là một slide bài giảng có tiêu đề **"AGENDA - Today we will learn:"** (Hôm nay chúng ta sẽ học). Slide liệt kê 9 chủ đề nâng cao về Mockito, bao gồm:
1.  **Method invocation verification order**: Kiểm tra thứ tự gọi các phương thức.
2.  **How to check number of invocations**: Cách kiểm tra số lần một phương thức được gọi.
3.  **Verify that there are no more interactions with mock**: Xác minh không còn tương tác nào khác với mock.
4.  **Call real method on mock**: Gọi phương thức thật trên đối tượng mock.
5.  **Mock answers policies**: Các chính sách trả lời mặc định của mock.
6.  **Additional Matchers**: Các bộ so khớp tham số bổ sung.
7.  **How to verify 'void' methods**: Cách kiểm chứng các phương thức không có giá trị trả về (void).
8.  **Mockito limitations**: Những hạn chế của Mockito.
9.  **Spies in Mockito**: Đối tượng Spy (điệp viên) trong Mockito.

Phần phụ đề (subtitle) ở dưới cùng có ghi: *"...your mom and make this part of your verification logic on real examples"* (đây là một câu nói đùa hoặc cắt ngang của giảng viên, ý nói hãy áp dụng phần logic xác minh này vào các ví dụ thực tế).

---

## 📚 Lý thuyết và Ví dụ Code chi tiết từng chủ đề

### 1. Method invocation verification order (Kiểm tra thứ tự gọi)
**Lý thuyết:** Đôi khi trong thực tế, thứ tự thực hiện các hành động rất quan trọng (ví dụ: phải trừ tiền trong tài khoản trước, sau đó mới gửi email xác nhận). Mockito cung cấp lớp `InOrder` để xác minh thứ tự này.

**Ví dụ Code:**
```java
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

@Test
void testMethodOrder() {
    // 1. Khởi tạo InOrder với các mock cần kiểm tra
    InOrder inOrder = inOrder(userRepository, emailService);

    // Thực thi code (giả sử userService.register() gọi 2 hàm dưới)
    userService.register(new User("Alice")); 

    // 2. Xác minh thứ tự
    // Phải lưu user vào DB TRƯỚC
    inOrder.verify(userRepository).save(any(User.class));
    // Sau đó mới gửi email
    inOrder.verify(emailService).sendWelcomeEmail(any(User.class));
}
```
**Giải thích code:** 
- `inOrder(mock1, mock2)` tạo một đối tượng kiểm tra thứ tự.
- Các lệnh `inOrder.verify(...)` phải được viết theo đúng thứ tự bạn mong muốn. Nếu code thật gọi `sendWelcomeEmail` trước `save`, test này sẽ thất bại.

---

### 2. How to check number of invocations (Kiểm tra số lần gọi)
**Lý thuyết:** Mặc định `verify(mock).method()` kiểm tra phương thức được gọi **đúng 1 lần**. Ta có thể dùng các hàm `times()`, `never()`, `atLeast()`, `atMost()` để kiểm tra số lần chính xác hơn.

**Ví dụ Code:**
```java
@Test
void testInvocationCount() {
    userService.doSomethingMultipleTimes();

    // Kiểm tra gọi đúng 3 lần
    verify(userRepository, times(3)).save(any());
    
    // Kiểm tra KHÔNG BAO GIỜ được gọi
    verify(emailService, never()).sendErrorEmail(anyString());
    
    // Kiểm tra gọi ít nhất 2 lần
    verify(logger, atLeast(2)).logInfo(anyString());
}
```
**Giải thích code:** Các bộ kiểm tra số lần rất hữu ích khi bạn muốn chắc chắn rằng vòng lặp hoặc logic bên trong lớp Service hoạt động đúng số lần quy định.

---

### 3. Verify that there are no more interactions with mock
**Lý thuyết:** Sau khi bạn đã xác minh tất cả các lời gọi hàm mong muốn, bạn có thể dùng `verifyNoMoreInteractions(mock)` để đảm bảo rằng **không có lời gọi hàm nào khác** (không mong muốn) bị gọi trên mock đó.

**Ví dụ Code:**
```java
@Test
void testNoMoreInteractions() {
    userService.register(new User("Bob"));

    verify(userRepository).save(any(User.class));
    verify(emailService).sendWelcomeEmail(any(User.class));

    // Đảm bảo không có hàm nào khác của userRepository bị gọi (ví dụ: delete)
    verifyNoMoreInteractions(userRepository);
}
```
**Giải thích code:** Nếu trong `userService.register()` có vô tình gọi `userRepository.delete()`, thì `verifyNoMoreInteractions` sẽ báo lỗi test ngay lập tức. *(Lưu ý: Đừng lạm dụng hàm này quá nhiều vì nó làm test dễ bị "giòn" - dễ vỡ khi code thay đổi dù logic vẫn đúng).*

---

### 4. Call real method on mock
**Lý thuyết:** Mặc định, mock sẽ trả về `null` hoặc `0`. Bạn có thể yêu cầu một phương thức cụ thể trên mock hãy chạy logic thật của nó bằng `thenCallRealMethod()`.

**Ví dụ Code:**
```java
@Test
void testCallRealMethod() {
    // Giả sử User có hàm getFullName() tính toán từ firstName và lastName
    User userMock = mock(User.class);
    
    // Cấu hình để hàm getFullName() chạy code thật
    when(userMock.getFullName()).thenCallRealMethod();
    
    // Cấu hình cho các thuộc tính cần thiết
    when(userMock.getFirstName()).thenReturn("John");
    when(userMock.getLastName()).thenReturn("Doe");

    // Kết quả sẽ là "John Doe" thay vì null
    assertEquals("John Doe", userMock.getFullName());
}
```
**Giải thích code:** `thenCallRealMethod()` cực kỳ hữu ích khi bạn chỉ muốn mock một vài phương thức của lớp, còn những phương thức khác vẫn muốn giữ nguyên logic thật để tính toán.

---

### 5. Mock answers policies (Chính sách trả lời)
**Lý thuyết:** Khi tạo mock, bạn có thể quy định "tính cách" mặc định của nó thông qua tham số `Answer`. Các policy phổ biến:
- `RETURNS_DEFAULTS`: (Mặc định) trả về null/0/false.
- `RETURNS_SMART_NULLS`: Trả về một đối tượng null "thông minh" (không bị lỗi NullPointerException ngay mà báo lỗi chi tiết khi gọi hàm).
- `RETURNS_DEEP_STUBS`: Tự động tạo mock lồng nhau cho các lời gọi hàm liên tiếp (Chain method).

**Ví dụ Code:**
```java
@Test
void testAnswersPolicies() {
    // Deep Stubs: Giải quyết lỗi khi gọi mock.getAddress().getCity()
    User userMock = mock(User.class, RETURNS_DEEP_STUBS);
    Address addressMock = mock(Address.class, RETURNS_DEEP_STUBS);

    // Không cần cấu hình từng bước
    when(userMock.getAddress().getCity()).thenReturn("Hanoi");
    
    assertEquals("Hanoi", userMock.getAddress().getCity());
}
```
**Giải thích code:** `RETURNS_DEEP_STUBS` giúp tránh việc phải viết `when(userMock.getAddress()).thenReturn(addressMock);` trước khi cấu hình `getCity()`.

---

### 6. Additional Matchers (Bộ so khớp bổ sung)
**Lý thuyết:** Ngoài `any()`, `eq()`, Mockito còn cung cấp các matchers cho số học và chuỗi như `gt()` (lớn hơn), `lt()` (nhỏ hơn), `contains()` (chứa chuỗi), `startsWith()`.

**Ví dụ Code:**
```java
@Test
void testAdditionalMatchers() {
    // Nếu điểm > 8 thì xếp loại "Giỏi"
    when(gradingService.getRank(gt(8.0))).thenReturn("Giỏi");
    // Nếu tên chứa chữ "Admin" thì cấp quyền cao
    when(authService.grantAccess(contains("Admin"))).thenReturn(true);

    assertEquals("Giỏi", gradingService.getRank(9.0));
    assertTrue(authService.grantAccess("SuperAdmin"));
}
```
**Giải thích code:** `gt(8.0)` (greater than) sẽ khớp với bất kỳ số thực nào lớn hơn 8.0. `contains("Admin")` khớp với mọi chuỗi có chứa chữ Admin.

---

### 7. How to verify 'void' methods
**Lý thuyết:** Với các hàm `void`, bạn không thể dùng `when(mock.voidMethod())` vì nó không trả về gì cả. Thay vào đó, dùng cú pháp `doThrow()`, `doNothing()`, `doAnswer()`.
Để verify, bạn vẫn dùng `verify(mock).voidMethod()` như bình thường.

**Ví dụ Code:**
```java
@Test
void testVoidMethods() {
    // 1. Cấu hình ném lỗi cho hàm void
    doThrow(new RuntimeException("Lỗi DB")).when(userRepository).deleteById(99L);

    assertThrows(RuntimeException.class, () -> {
        userService.deleteUser(99L);
    });

    // 2. Verify hàm void đã được gọi
    verify(userRepository).deleteById(99L);
}
```
**Giải thích code:** Cú pháp `doThrow(...).when(mock).method()` được thiết kế riêng cho các hàm trả về kiểu `void`.

---

### 8. Mockito limitations (Hạn chế của Mockito)
**Lý thuyết:** Mockito không thể mock mọi thứ một cách hoàn hảo (nếu chỉ dùng thư viện core cơ bản). Nó **không thể** mock:
- Lớp `final` và phương thức `final`.
- Phương thức `static`.
- Phương thức `private`.
- Hàm khởi tạo (Constructor).
*(Lưu ý: Để mock được static và final, bạn cần dùng thư viện bổ trợ `mockito-inline` hoặc framework mạnh hơn như PowerMock).*

---

### 9. Spies in Mockito
**Lý thuyết:** 
- **Mock (`@Mock`):** Tạo ra một đối tượng rỗng hoàn toàn. Mọi phương thức đều trả về mặc định (null/0) trừ khi bạn `when()`.
- **Spy (`@Spy`):** Bọc một **đối tượng thật**. Mặc định, nó sẽ gọi các phương thức thật của đối tượng đó. Bạn chỉ dùng `when()` để thay đổi hành vi của một vài phương thức cụ thể. (Giống như một "điệp viên" nằm vùng trong đối tượng thật).

**Ví dụ Code:**
```java
@Test
void testSpy() {
    // Tạo một đối tượng ArrayList thật
    List<String> realList = new ArrayList<>();
    
    // Tạo Spy bọc quanh đối tượng thật
    List<String> spyList = spy(realList);

    // Gọi hàm add bình thường -> Nó sẽ thực sự thêm vào list
    spyList.add("Hello");
    
    // Kiểm tra xem list thật có phần tử chưa
    assertEquals(1, spyList.size()); // size() cũng chạy thật -> trả về 1

    // Bây giờ ta "giả lập" hàm size() (không chạy thật nữa)
    doReturn(100).when(spyList).size();
    
    assertEquals(100, spyList.size()); // Trả về 100 do đã bị mock
}
```
**Giải thích code:** Với `spy`, khi bạn gọi `spyList.add("Hello")`, phần tử thực sự được thêm vào `realList`. Nhưng khi bạn can thiệp bằng `doReturn(100).when(spyList).size()`, hàm `size()` không còn đếm thật nữa mà trả về giá trị bạn đã gán.
*(Lưu ý: Khi dùng Spy, nên dùng cú pháp `doReturn(...).when(spy).method()` thay vì `when(spy.method()).thenReturn(...)` để tránh việc hàm thật bị gọi ngoài ý muốn trong lúc setup).*

---
Hy vọng phần giải thích chi tiết này giúp bạn làm chủ được các kỹ thuật Mockito nâng cao! Nếu có đoạn code nào chưa rõ, bạn cứ hỏi nhé.

Chào bạn, cảm ơn bạn đã gửi một bộ code rất đầy đủ và thực tế! Đây là sự kết hợp hoàn hảo giữa phần **Lý thuyết cơ bản** và **Nâng cao** mà chúng ta đã thảo luận ở các tin nhắn trước. 

Bộ code này mô phỏng một hệ thống gửi email: Class `Messenger` (lớp cần test) sử dụng `TemplateEngine` để tạo nội dung và `MailServer` để gửi đi. Chúng ta sẽ dùng Mockito để test `Messenger` mà không cần thực sự gửi email hay tạo template thật.

Dưới đây là phần phân tích chi tiết từng file, kết hợp lý thuyết, code và giải thích.

---

## PHẦN 1: CÁC LỚP DOMAIN (MÔ HÌNH DỮ LIỆU)
Trước khi đi vào Test, ta cần hiểu các lớp nghiệp vụ:
- **`Client`, `Email`, `Template`**: Các lớp POJO (đối tượng dữ liệu đơn giản).
- **`TemplateEngine`**: Có hàm `prepareMessage` trả về String (nội dung tin nhắn) và `evaluateTemplate`.
- **`MailServer`**: Có hàm `send(Email)`. Lưu ý: Hàm này gọi đến `Validator` bên trong. Có hàm `getValidator()`.
- **`Validator`**: Ném lỗi `IllegalArgumentException` nếu email không có người nhận.
- **`Messenger`**: **Lớp trung tâm cần test**. Hàm `sendMessage` sẽ gọi `TemplateEngine` để lấy nội dung, tạo đối tượng `Email`, set nội dung/người nhận, và gọi `MailServer` để gửi.

---

## PHẦN 2: FILE `MessengerTest.java` (MOCKITO CƠ BẢN)

### 1. Khởi tạo và Cấu hình (Setup)
```java
@ExtendWith(MockitoExtension.class) // JUnit 5 Integration
public class MessengerTest {
    @Mock
    private TemplateEngine templateEngineMock;
    
    @Mock
    private MailServer mailServerMock;

    @InjectMocks
    private Messenger messenger;

    @Captor
    private ArgumentCaptor<Email> captor;
}
```
**Lý thuyết & Giải thích:**
- `@ExtendWith(MockitoExtension.class)`: Kích hoạt Mockito trong môi trường JUnit 5, giúp các annotation bên dưới hoạt động.
- `@Mock`: Tạo đối tượng giả lập cho `TemplateEngine` và `MailServer`. Mọi hàm gọi trên chúng sẽ không chạy code thật.
- `@InjectMocks`: Tạo một đối tượng `Messenger` thật, và Mockito sẽ tự động tiêm (inject) 2 mock trên vào các biến `templateEngine` và `mailServer` của lớp `Messenger`.
- `@Captor`: Khai báo một `ArgumentCaptor` để "bắt" đối tượng `Email` được truyền vào `MailServer.send()` sau này.

### 2. Test cơ bản: `shouldSendMessage()`
```java
@Test
void shouldSendMessage() {
    // Given (Chuẩn bị)
    var client = new Client(RANDOM_EMAIL);
    var template = new Template();
    
    // Lập trình hành vi (Stubbing)
    when(templateEngineMock.prepareMessage(client, template))
            .thenReturn(RANDOM_MESSAGE);

    // When (Hành động)
    messenger.sendMessage(client, template);

    // Then (Kiểm chứng)
    verify(templateEngineMock).prepareMessage(client, template);
    verify(mailServerMock).send(any(Email.class));
}
```
**Giải thích:**
- `when(...).thenReturn(...)`: "Dạy" cho mock `templateEngineMock` biết rằng: Nếu ai đó gọi hàm `prepareMessage` với đúng `client` và `template` này, hãy trả về `RANDOM_MESSAGE`.
- `messenger.sendMessage(...)`: Gọi hàm thật của lớp `Messenger`. Bên trong hàm này, nó sẽ gọi đến 2 mock kia.
- `verify(...)`: Khẳng định rằng hàm `prepareMessage` đã được gọi, và hàm `send` của `mailServer` đã được gọi với **bất kỳ** đối tượng `Email` nào (`any(Email.class)`).

### 3. Argument Matchers: `shouldSendMessageWithArgumentMatchers()`
```java
@Test
void shouldSendMessageWithArgumentMatchers() {
    var client = new Client(RANDOM_EMAIL);
    var template = new Template();
    
    // Sử dụng bộ so khớp linh hoạt
    when(templateEngineMock.prepareMessage(any(Client.class),
            any(Template.class))).thenReturn(RANDOM_EMAIL);
            
    // Lỗi nếu viết thế này: when(templateEngineMock.prepareMessage(client, any(Template.class)))
    // Phải dùng: when(templateEngineMock.prepareMessage(eq(client), any(Template.class)))
    
    messenger.sendMessage(client, template);
    verify(templateEngineMock).prepareMessage(client, template);
    verify(mailServerMock).send(any(Email.class));
}
```
**Lý thuyết quan trọng:**
- **Quy tắc vàng của Mockito:** Nếu bạn sử dụng Argument Matcher (như `any()`) cho **một** tham số, bạn **bắt buộc** phải dùng matcher cho **tất cả** các tham số còn lại. Do đó, không thể viết `client, any()` mà phải là `eq(client), any()`.

### 4. Ném ngoại lệ: `shouldThrowExceptionWhenTemplateEngineThrowsException()`
```java
@Test
public void shouldThrowExceptionWhenTemplateEngineThrowsException() {
    var client = new Client(RANDOM_EMAIL);
    var template = new Template();
    
    // Cấu hình mock ném lỗi
    when(templateEngineMock.prepareMessage(client, template))
            .thenThrow(new IllegalArgumentException());

    // Kiểm tra xem Messenger có ném lỗi ra ngoài không
    assertThrows(IllegalArgumentException.class, ()
            -> messenger.sendMessage(client, template)
    );
}
```
**Giải thích:** Khi `templateEngine` ném lỗi, `Messenger` cũng sẽ dừng lại và ném lỗi đó ra ngoài. Hàm `assertThrows` của JUnit 5 dùng để bắt và xác nhận lỗi này.

### 5. Argument Captor: `shouldSetClientEmailToAddresseInEmail()`
```java
@Test
public void shouldSetClientEmailToAddresseInEmail() {
    var client = new Client(RANDOM_EMAIL);
    var template = new Template();
    when(templateEngineMock.prepareMessage(client, template))
            .thenReturn(RANDOM_MESSAGE);

    messenger.sendMessage(client, template);

    // Bắt đối tượng Email được truyền vào
    verify(mailServerMock).send(captor.capture());
    
    // Lấy Email ra và kiểm tra thuộc tính bên trong
    assertEquals(client.getEmail(), captor.getValue().getAddressee());
}
```
**Giải thích:** `captor.capture()` đặt vào vị trí tham số để "chụp" lại đối tượng `Email` mà `Messenger` vừa tạo. Sau đó, `captor.getValue()` trả về đối tượng đó để ta kiểm tra xem người nhận (`addressee`) có đúng là email của `client` không.

---

## PHẦN 3: FILE `MessengerTest2.java` (MOCKITO NÂNG CAO)

### 1. Các chính sách trả lời (Answers) & Deep Stubs
```java
@Mock(answer = Answers.RETURNS_DEEP_STUBS)
private MailServer mailServerMock;
// ...
@Test
void shouldNotThrowExceptionWithDeepStub() {
    var client = new Client(RANDOM_EMAIL);
    var template = new Template();

    // Gọi hàm lồng nhau: getValidator() -> validate()
    var validator = mailServerMock.getValidator();
    validator.validate(new Email());
}
```
**Lý thuyết:** Mặc định, `mailServerMock.getValidator()` sẽ trả về `null`, dẫn đến lỗi `NullPointerException` khi gọi `.validate()`. Việc cấu hình `RETURNS_DEEP_STUBS` giúp Mockito tự động tạo một mock cho `Validator` khi hàm `getValidator()` được gọi, giúp ta test các chuỗi gọi hàm lồng nhau dễ dàng.

### 2. Kiểm tra số lần gọi hàm: `shouldSendMessageOnlyOneTime()`
```java
@Test
void shouldSendMessageOnlyOneTime() {
    // ... given ...
    messenger.sendMessage(client, template);

    // Kiểm tra số lần gọi
    verify(templateEngineMock, times(1)).prepareMessage(client, template);
    verify(mailServerMock, times(1)).send(any(Email.class));
    verify(mailServerMock, atLeastOnce()).send(any(Email.class));
    verify(mailServerMock, atLeast(1)).send(any(Email.class));
    verify(mailServerMock, atMost(1)).send(any(Email.class));
    // verify(mailServerMock, never()).send(any(Email.class)); // Dòng này sẽ fail
}
```
**Giải thích:** Các hàm `times(n)`, `atLeast(n)`, `atMost(n)`, `never()` dùng để kiểm tra số lần phương thức được gọi. `never()` sẽ báo lỗi nếu hàm đã được gọi (dù chỉ 1 lần).

### 3. Kiểm tra thứ tự gọi: `shouldSendMessageAndPrepareTemplateBeforeThat()`
```java
@Test
void shouldSendMessageAndPrepareTemplateBeforeThat() {
    // Khởi tạo InOrder
    var inOrder = inOrder(templateEngineMock, mailServerMock);
    // ... given & when ...
    
    // Xác minh thứ tự
    inOrder.verify(templateEngineMock).prepareMessage(client, template);
    inOrder.verify(mailServerMock).send(any(Email.class));
}
```
**Giải thích:** `inOrder` đảm bảo rằng `templateEngine.prepareMessage` phải được gọi **trước** khi `mailServer.send` được gọi. Nếu code thật gọi ngược lại, test sẽ thất bại.

### 4. Không còn tương tác nào khác: `shouldSendMessageAndThereIsNoMoreInteractions...`
```java
@Test
void shouldSendMessageAndThereIsNoMoreInteractionsWithTemplateEngine() {
    // ... inOrder ...
    messenger.sendMessage(client, template);

    inOrder.verify(templateEngineMock).prepareMessage(client, template);
    inOrder.verify(mailServerMock).send(any(Email.class));
    
    // Đảm bảo không có hàm nào khác bị gọi
    verifyNoMoreInteractions(templateEngineMock, mailServerMock);
}
```
**Giải thích:** Nếu trong `Messenger.sendMessage` có vô tình gọi thêm `templateEngineMock.evaluateTemplate()`, hàm `verifyNoMoreInteractions` sẽ phát hiện và báo lỗi test.

### 5. Gọi hàm thật trên Mock: `shouldCallRealMethodOnMockExample()`
```java
@Test
void shouldCallRealMethodOnMockExample() {
    Client client = new Client("");
    Template template = new Template();

    // In ra null (vì là mock)
    System.out.println(templateEngineMock.prepareMessage(client, template));

    // Yêu cầu chạy code thật của hàm
    when(templateEngineMock.prepareMessage(client, template)).thenCallRealMethod();
    
    // In ra "Some template" (kết quả từ code thật của TemplateEngine)
    System.out.println(templateEngineMock.prepareMessage(client, template));
}
```
**Giải thích:** `thenCallRealMethod()` rất hữu ích khi bạn muốn mock một nửa đối tượng: phần lớn là mock, nhưng riêng hàm này phải chạy logic thật.

### 6. Additional Matchers: `additionalMatchersDemo()`
```java
@Test
void additionalMatchersDemo() {
    List<String> listMock = mock(List.class);

    // Bộ so khớp nâng cao
    when(listMock.get(or(eq(1), eq(2)))).thenReturn("more");
    when(listMock.get(gt(2))).thenThrow(new RuntimeException());
    
    // System.out.println(listMock.get(1)); // "more"
    // System.out.println(listMock.get(3)); // Ném RuntimeException
}
```
**Giải thích:** Tương tự như phần lý thuyết, `or()`, `gt()` (greater than), `lt()` (less than) giúp viết các điều kiện kiểm tra tham số phức tạp.

### 7. Verify hàm Void: `verifyVoidMethodExample()`
```java
@Test
void verifyVoidMethodExample() {
    List<String> listMock = mock(List.class);

    // Dùng doAnswer cho hàm void
    doAnswer(methodInvocation -> {
        // Lấy tham số truyền vào hàm add()
        Object arg0 = methodInvocation.getArgument(0); // int index
        Object arg1 = methodInvocation.getArgument(1); // String element

        assertEquals(3, arg0);
        assertEquals("List Element", arg1);
        return null; // Hàm void trả về null
    }).when(listMock).add(any(Integer.class), any(String.class));
    
    // Gọi hàm để kích hoạt doAnswer
    listMock.add(3, "List Element");
}
```
**Lý thuyết:** Với hàm `void`, ta dùng cú pháp `doAnswer().when(mock).method()`. Trong `doAnswer`, ta có thể truy cập vào các tham số thực tế truyền vào thông qua `methodInvocation.getArgument(index)` để viết logic kiểm tra (assert) ngay bên trong đó.

### 8. Hạn chế của Mockito: `shouldThrowExceptionWhenMockFinalClass()`
```java
@Test
public void shouldThrowExceptionWhenMockFinalClass() {
    // Không thể mock final class
    // String string = mock(String.class); // Lỗi!
    
    // Không thể mock static method
    // when(Map.of()).thenReturn(null); // Lỗi!
}
```
**Giải thích:** Như đã học, Mockito core không thể can thiệp vào các lớp `final` (như `String`), các phương thức `static` hay `private`. (Trừ khi dùng thư viện bổ trợ `mockito-inline`).

### 9. Spy: `spyExample()`
```java
@Test
void spyExample() {
    List<Integer> listInts = new ArrayList<>();
    List<Integer> spy = spy(listInts);

    // Nếu dùng when() sẽ gây lỗi IndexOutOfBoundsException
    // Vì when() sẽ GỌI HÀM THẬT spy.get(0) ngay lập tức (mà list đang rỗng)
//  when(spy.get(0)).thenReturn(0); 

    // Phải dùng doReturn() để tránh gọi hàm thật lúc setup
    doReturn(0).when(spy).get(0);
    
    System.out.println(spy.get(0)); // In ra 0
    verify(spy).get(0);
}
```
**Giải thích cực kỳ quan trọng:** Với `Spy`, đối tượng thật được bao bọc bên trong. 
- Cú pháp `when(spy.get(0))` sẽ **thực thi** `spy.get(0)` ngay lập tức để lấy giá trị. Vì list đang rỗng, nó sẽ ném ra `IndexOutOfBoundsException`.
- Cú pháp `doReturn(0).when(spy).get(0)` sẽ **không** gọi hàm thật lúc setup, mà chỉ ghi đè hành vi. Do đó, đây là cú pháp chuẩn khi làm việc với `Spy`.

---
Bộ code này là một kho tàng ví dụ thực tế. Bạn có thể thấy rõ Mockito giúp cô lập hoàn toàn logic của `Messenger` mà không cần phải kết nối database hay server email thật. Nếu bạn muốn chạy thử, hãy đảm bảo file `pom.xml` đã có các dependency `junit-jupiter` và `mockito-junit-jupiter` như hướng dẫn trước đó nhé!