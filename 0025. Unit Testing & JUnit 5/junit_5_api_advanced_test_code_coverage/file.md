Dưới đây là giải thích chi tiết về bức ảnh bạn đã gửi, cùng với hệ thống lý thuyết và ví dụ mã nguồn (code) cho từng nội dung trong chương trình học JUnit 5 nâng cao.

---

### 🖼️ Giải thích chi tiết hình ảnh

Bức ảnh bạn cung cấp là một ảnh chụp màn hình khóa học trực tuyến **"Java from Zero to First Job, Practical Guide, 2000+ examples"** trên nền tảng Udemy. 

Cụ thể:
- **Tiêu đề slide:** "AGENDA" (Chương trình nghị sự) với dòng chữ *"Today we will learn:"* (Hôm nay chúng ta sẽ học).
- **Danh sách nội dung:** Liệt kê 10 chủ đề nâng cao của JUnit 5, từ việc đặt tên hiển thị (`@DisplayName`), nhóm kiểm thử (`Group assertions`), kiểm thử phụ thuộc (`Dependent assertions`), kiểm thử có giới hạn thời gian (`Tests with timeout`), giả định (`Assumptions`), kiểm thử tham số hóa (`Parametrized tests`), vô hiệu hóa kiểm thử (`Disabling tests`), cho đến đo lường độ bao phủ mã nguồn (`Test Code Coverage`) và plugin `Eclemma`.
- **Phần thuyết minh (phụ đề):** *"Also, we'll learn how to display name generation notation to help us improve our test report automatically"* (Ngoài ra, chúng ta sẽ học cách sử dụng ký hiệu tạo tên hiển thị để giúp cải thiện báo cáo kiểm thử của chúng ta một cách tự động).
- **Logo:** Hình ảnh tách cà phê và làn khói - biểu tượng đặc trưng của Java.

Đây là phần giới thiệu tổng quan (Agenda) trước khi đi sâu vào chi tiết từng tính năng mạnh mẽ của JUnit 5.

---

### 📚 Giải thích lý thuyết & Ví dụ Code chi tiết

Dưới đây là phần giải thích chi tiết từng mục trong Agenda, kèm theo lý thuyết và ví dụ mã nguồn Java (sử dụng JUnit 5).

#### 1. @DisplayName & @DisplayNameGeneration (Tên hiển thị tùy chỉnh)

**Lý thuyết:**
- `@DisplayName`: Cho phép đặt tên hiển thị tùy chỉnh cho lớp kiểm thử hoặc phương thức kiểm thử. Tên này có thể chứa dấu cách, ký tự đặc biệt và cả biểu tượng cảm xúc (emoji), giúp báo cáo kiểm thử trở nên dễ đọc và thân thiện hơn với con người.
- `@DisplayNameGeneration`: Được sử dụng ở cấp độ lớp để chỉ định một **bộ tạo tên** (generator). JUnit cung cấp sẵn các generator như `ReplaceUnderscores` (thay dấu gạch dưới bằng dấu cách) hoặc `IndicativeSentences` (tạo câu hoàn chỉnh). 
- **Lưu ý quan trọng:** Nếu một phương thức vừa có `@DisplayName` vừa nằm trong lớp có `@DisplayNameGeneration`, thì `@DisplayName` sẽ được ưu tiên áp dụng.

**Ví dụ Code:**
```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayName("Lớp kiểm thử tính toán")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CalculatorTest {

    // Tên hiển thị sẽ tự động là: "phép cộng hai số nguyên"
    @Test
    void phep_cong_hai_so_nguyen() {
        // ...
    }

    // @DisplayName được ưu tiên, tên hiển thị sẽ là: "Kiểm tra phép chia cho 0"
    @Test
    @DisplayName("Kiểm tra phép chia cho 0")
    void test_divide_by_zero() {
        // ...
    }
}
```
**Giải thích Code:**
- Lớp `CalculatorTest` được đặt tên hiển thị là "Lớp kiểm thử tính toán".
- Annotation `@DisplayNameGeneration` với `ReplaceUnderscores.class` sẽ tự động chuyển đổi tên phương thức `phep_cong_hai_so_nguyen` thành "phép cộng hai số nguyên" khi hiển thị trên báo cáo.
- Phương thức `test_divide_by_zero` có `@DisplayName` riêng, nên nó sẽ hiển thị là "Kiểm tra phép chia cho 0" thay vì tên mặc định.

---

#### 2. Group Assertions (Nhóm khẳng định - `assertAll`)

**Lý thuyết:**
- `assertAll` cho phép thực thi **tất cả** các khẳng định (assertions) bên trong nó, ngay cả khi một vài khẳng định đầu tiên bị lỗi. Tất cả các lỗi sẽ được thu thập và báo cáo cùng một lúc thay vì dừng lại ở lỗi đầu tiên. Điều này giúp bạn có cái nhìn toàn cảnh về những gì đang sai trong một lần chạy kiểm thử.

**Ví dụ Code:**
```java
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class PersonTest {
    @Test
    void testPersonInfo() {
        Person person = new Person("John", "Doe");
        
        assertAll("person info",
            () -> assertEquals("John", person.getFirstName()),
            () -> assertEquals("Doe", person.getLastName()),
            () -> assertEquals(30, person.getAge()) // Giả sử age là 30
        );
    }
}
```
**Giải thích Code:**
- Thay vì viết 3 dòng `assertEquals` riêng lẻ (và nếu dòng đầu sai, 2 dòng sau không chạy), ta gói chúng vào `assertAll`.
- Nếu `firstName` sai, `lastName` vẫn được kiểm tra và `age` cũng vậy. Tất cả các lỗi sẽ được liệt kê trong báo cáo.

---

#### 3. Dependent Assertions (Khẳng định phụ thuộc)

**Lý thuyết:**
- Đây là một dạng nâng cao của `assertAll`. Bên trong một khối `assertAll`, bạn có thể lồng các khối `assertAll` khác. Các khẳng định con chỉ được thực thi nếu khẳng định cha (ngay trước nó) thành công. Điều này hữu ích khi bạn cần đảm bảo một đối tượng không null trước khi kiểm tra các thuộc tính của nó.

**Ví dụ Code:**
```java
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DependentAssertionsTest {
    @Test
    void testPersonProperties() {
        Person person = new Person("Jane", "Doe");
        
        assertAll("properties",
            () -> {
                String firstName = person.getFirstName();
                assertNotNull(firstName); // Nếu firstName là null, khối bên dưới bị bỏ qua
                assertAll("first name",
                    () -> assertTrue(firstName.startsWith("J")),
                    () -> assertTrue(firstName.endsWith("e"))
                );
            },
            () -> {
                String lastName = person.getLastName();
                assertNotNull(lastName);
                assertAll("last name",
                    () -> assertTrue(lastName.startsWith("D")),
                    () -> assertTrue(lastName.endsWith("e"))
                );
            }
        );
    }
}
```
**Giải thích Code:**
- Khối `assertAll` lớn chứa hai khối lambda độc lập (cho `firstName` và `lastName`).
- Trong khối `firstName`, `assertNotNull(firstName)` được kiểm tra trước. Nếu nó thất bại (firstName là null), khối `assertAll("first name", ...)` bên trong sẽ **không được thực thi** để tránh lỗi `NullPointerException`.
- Tương tự với khối `lastName`.

---

#### 4. Tests with Timeout (Kiểm thử với giới hạn thời gian)

**Lý thuyết:**
- JUnit 5 cung cấp các phương thức như `assertTimeout` và `assertTimeoutPreemptively` để đảm bảo một đoạn mã không chạy quá lâu.
- `assertTimeout`: Chờ cho đoạn mã chạy xong rồi mới kiểm tra thời gian. Nếu quá thời gian, test sẽ thất bại.
- `assertTimeoutPreemptively`: Sẽ **hủy bỏ** (abort) đoạn mã ngay khi hết thời gian, không chờ nó chạy xong.

**Ví dụ Code:**
```java
import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TimeoutTest {
    @Test
    void testTimeoutNotExceeded() {
        assertTimeout(ofMillis(500), () -> {
            // Giả lập tác vụ chạy trong 100ms
            Thread.sleep(100);
        });
    }

    @Test
    void testTimeoutExceededPreemptively() {
        // Test này sẽ thất bại vì tác vụ mất 1000ms > 10ms
        assertTimeoutPreemptively(ofMillis(10), () -> {
            Thread.sleep(1000);
        });
    }
}
```
**Giải thích Code:**
- `testTimeoutNotExceeded`: `Thread.sleep(100)` chạy trong 100ms, nhỏ hơn 500ms, nên test thành công.
- `testTimeoutExceededPreemptively`: `Thread.sleep(1000)` vượt quá 10ms. Với `assertTimeoutPreemptively`, tiến trình sẽ bị ngắt ngay lập tức và test báo lỗi.

---

#### 5. Assumptions (Giả định)

**Lý thuyết:**
- `Assumptions` (thuộc lớp `org.junit.jupiter.api.Assumptions`) được dùng để **bỏ qua** một bài kiểm thử nếu một điều kiện nào đó không được đáp ứng, thay vì báo lỗi thất bại. Ví dụ: chỉ chạy test trên môi trường Linux, hoặc chỉ chạy khi một thuộc tính hệ thống được bật.

**Ví dụ Code:**
```java
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.Test;

class AssumptionTest {
    @Test
    void testOnlyOnLinux() {
        // Giả định rằng hệ điều hành là Linux
        assumeTrue(System.getProperty("os.name").toLowerCase().contains("linux"));
        
        // Nếu không phải Linux, đoạn code dưới đây sẽ không chạy và test bị đánh dấu là "skipped"
        System.out.println("Đang chạy trên Linux");
    }
}
```
**Giải thích Code:**
- `assumeTrue(condition)`: Nếu `condition` là `false`, bài test sẽ bị dừng ngay lập tức và được báo cáo là "bị bỏ qua" (skipped), chứ không phải "thất bại" (failed).

---

#### 6. Parametrized Tests (Kiểm thử tham số hóa)

**Lý thuyết:**
- `@ParameterizedTest` cho phép chạy cùng một logic kiểm thử với **nhiều bộ dữ liệu đầu vào** khác nhau. Thay vì viết nhiều phương thức `@Test` trùng lặp, bạn chỉ cần viết một phương thức và cung cấp nguồn dữ liệu (như `@ValueSource`, `@CsvSource`, v.v.).

**Ví dụ Code:**
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterizedTestExample {
    
    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9})
    void testIsOdd(int number) {
        assertTrue(number % 2 != 0);
    }

    @ParameterizedTest
    @CsvSource({"1, one", "2, two", "3, three"})
    void testNumberToWord(int number, String expectedWord) {
        // ... logic kiểm tra
    }
}
```
**Giải thích Code:**
- `@ValueSource(ints = {1, 3, 5, 7, 9})`: Phương thức `testIsOdd` sẽ được chạy **5 lần**, mỗi lần với một số từ mảng.
- `@CsvSource`: Cung cấp dữ liệu dạng bảng (mỗi dòng là một bộ tham số). Phương thức `testNumberToWord` sẽ chạy 3 lần.

---

#### 7. Disabling Tests (Vô hiệu hóa kiểm thử)

**Lý thuyết:**
- `@Disabled`: Đánh dấu một phương thức hoặc cả một lớp kiểm thử là **tạm thời không chạy**. Điều này hữu ích khi bạn đang viết dở một tính năng hoặc một bài test đang bị lỗi và bạn muốn tạm gác lại để xử lý sau.

**Ví dụ Code:**
```java
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class DisabledTestExample {
    
    @Test
    @Disabled("Chờ sửa lỗi logic tính toán")
    void testIncompleteFeature() {
        // Bài test này sẽ không được chạy
    }

    @Test
    void testWorkingFeature() {
        // Bài test này vẫn chạy bình thường
    }
}
```
**Giải thích Code:**
- Phương thức `testIncompleteFeature` có `@Disabled`. JUnit sẽ bỏ qua nó và ghi chú lý do (nếu có) vào báo cáo.

---

#### 8. Test Code Coverage & Eclemma plugin (Độ bao phủ mã nguồn & Plugin EclEmma)

**Lý thuyết:**
- **Test Code Coverage (Độ bao phủ mã nguồn):** Là một phép đo (metric) cho biết có bao nhiêu phần trăm mã nguồn của bạn đã được các bài kiểm thử "chạm" tới (thực thi).
- **Eclemma:** Là một plugin mã nguồn mở phổ biến dành cho môi trường phát triển Eclipse, giúp đo lường và hiển thị trực quan độ bao phủ mã nguồn. EclEmma hoạt động dựa trên thư viện **JaCoCo** (Java Code Coverage).
- **Cách hoạt động:** Khi bạn chạy bài kiểm thử JUnit ở chế độ "Coverage" trong Eclipse, EclEmma sẽ tô màu các dòng code:
    - **Màu xanh lá:** Dòng code đã được kiểm thử thực thi.
    - **Màu vàng:** Dòng code chỉ được thực thi một phần (ví dụ: chỉ đúng trong câu lệnh `if` nhưng chưa bao giờ sai).
    - **Màu đỏ:** Dòng code chưa bao giờ được kiểm thử chạy qua.

**Ví dụ Code minh họa (để thấy độ bao phủ):**
```java
public class Calculator {
    public int divide(int a, int b) {
        if (b == 0) { 
            throw new IllegalArgumentException("Cannot divide by zero"); // Dòng này có thể bị bỏ qua nếu không test trường hợp b=0
        }
        return a / b;
    }
}
```
**Giải thích Code & Liên hệ Eclemma:**
- Nếu bạn chỉ viết bài test `assertEquals(2, calculator.divide(4, 2))`, thì dòng `throw new IllegalArgumentException(...)` sẽ **không được thực thi**. Khi chạy EclEmma, dòng đó sẽ hiện **màu đỏ** (chưa được bao phủ).
- Nếu bạn viết thêm bài test `assertThrows(IllegalArgumentException.class, () -> calculator.divide(1, 0))`, dòng `throw ...` sẽ được thực thi và chuyển sang **màu xanh lá** (đã được bao phủ).

---
Hy vọng phần giải thích chi tiết này giúp bạn nắm rõ toàn bộ nội dung trong bức ảnh và cách áp dụng chúng vào thực tế!


Chào bạn, hai file code bạn vừa gửi là **những ví dụ thực tế tuyệt vời** để minh họa chính xác cho 10 mục trong "AGENDA" ở bức ảnh trước đó. 

Dưới đây là phần giải thích chi tiết từng phần của mã nguồn, kết hợp giữa lý thuyết và phân tích code (theo đúng yêu cầu của bạn).

---

### PHẦN 1: PHÂN TÍCH FILE `MoneyTransactionServiceTest2.java`

File này là một lớp kiểm thử toàn diện, áp dụng hầu hết các tính năng nâng cao của JUnit 5. Tôi sẽ chia nhỏ theo từng chủ đề trong ảnh.

#### 1. @DisplayName & @DisplayNameGeneration (Tên hiển thị)
**Lý thuyết:** Giúp báo cáo test (Test Report) dễ đọc hơn bằng cách thay thế tên hàm (thường viết liền hoặc có dấu gạch dưới) bằng một câu văn mô tả rõ ràng.
**Phân tích Code:**
```java
@DisplayName("Money Transaction Serivce test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MoneyTransactionServiceTest2 { ... }
```
*   `@DisplayName` ở cấp độ lớp: Đặt tên cho cả lớp test là "Money Transaction Serivce test".
*   `@DisplayNameGeneration(ReplaceUnderscores.class)`: Tự động chuyển đổi tên các hàm có dấu gạch dưới thành dấu cách. 
    *   *Ví dụ:* Hàm `should_Throw_Exception_If_Account_From_Is_Null` khi chạy sẽ tự động hiển thị trên báo cáo là `"should Throw Exception If Account From Is Null"`.
*   *Lưu ý trong code:* Hàm `shouldTransferMoneyFromOneAccountToAnother` có thêm `@DisplayName("Verify money transaction from one account to another")`. Annotation này sẽ được ưu tiên hơn quy tắc của `@DisplayNameGeneration`.

#### 2. Vòng đời (Lifecycle) - (Kiến thức nền tảng đi kèm)
**Lý thuyết:** JUnit 5 quản lý vòng đời của bài test qua các annotation. Code của bạn có ghi chú rõ sự khác biệt giữa JUnit 4 và 5.
**Phân tích Code:**
*   `@BeforeAll` (JUnit 5) / `@BeforeClass` (JUnit 4): Chạy **một lần duy nhất** trước tất cả các test. Bắt buộc phải là phương thức `static`.
*   `@BeforeEach` (JUnit 5) / `@Before` (JUnit 4): Chạy **trước mỗi** phương thức `@Test`. Code dùng nó để khởi tạo lại `testInstance = new MoneyTransactionService();` cho mỗi bài test, đảm bảo các bài test không bị ảnh hưởng lẫn nhau.
*   `@AfterEach`, `@AfterAll`: Tương tự nhưng chạy sau khi kết thúc test.

#### 3. Group Assertions & Dependent Assertions (Nhóm khẳng định & Phụ thuộc)
**Lý thuyết:** 
*   `assertAll`: Chạy tất cả các assert bên trong, thu thập tất cả các lỗi (nếu có) thay vì dừng ở lỗi đầu tiên.
*   Phụ thuộc: Chỉ chạy assert con nếu assert cha đúng.

**Phân tích Code:**
*   **Group Assertions:**
```java
@Test
void groupedAssertionsExamples() {
    // ...
    assertAll("money transaction",
            () -> assertEquals(ZERO_MONEY_AMOUNT, account1.getMoneyAmount()),
            () -> assertEquals(RANDOM_MONEY_AMOUNT, account2.getMoneyAmount())
    );
}
```
*Giải thích:* Nếu `account1` sai số dư, JUnit vẫn tiếp tục kiểm tra `account2` và báo cáo cả 2 lỗi cùng lúc.

*   **Dependent Assertions:**
```java
@Test
void dependentAssertionsExample() {
    // ...
    assertAll("Money Transaction",
            () -> {
                boolean isTransactionSuccesd = testInstance.transferMoney(...);
                assertTrue(isTransactionSuccesd); // Assert cha

                // Chỉ chạy nếu assert cha (assertTrue) ở trên là ĐÚNG
                assertAll("Money amount is changed on the accounts",
                        () -> assertEquals(ZERO_MONEY_AMOUNT, account1.getMoneyAmount()),
                        () -> assertEquals(RANDOM_MONEY_AMOUNT, account2.getMoneyAmount())
                );
            }
    );
}
```
*Giải thích:* Nếu việc chuyển tiền thất bại (`isTransactionSuccesd` là false), khối `assertAll` kiểm tra số dư bên trong sẽ bị bỏ qua để tránh báo lỗi sai lệch (vì nếu chưa chuyển tiền thì số dư không đổi, báo lỗi số dư là vô nghĩa).

#### 4. Tests with Timeout (Kiểm thử thời gian)
**Lý thuyết:** Đảm bảo code không bị treo hoặc chạy quá chậm.
**Phân tích Code:**
*   `assertTimeout(Duration.ofSeconds(1), () -> ...)`: Chờ hàm `transferMoney` chạy xong. Nếu quá 1 giây, bài test báo thất bại.
*   `@Timeout(2)` (ở mức method): Đặt giới hạn 2 giây cho toàn bộ hàm `timeoutNotExceededWithResult`.
*   *Lưu ý:* Có một hàm `assertTimeoutPreemptively` (không có trong file) sẽ **ngắt (abort)** ngay lập tức tiến trình nếu quá thời gian, thay vì chờ nó chạy xong như `assertTimeout`.

#### 5. Assumptions (Giả định)
**Lý thuyết:** Dùng để **bỏ qua (skip)** một bài test nếu môi trường không phù hợp, thay vì đánh dấu là thất bại (failed).
**Phân tích Code:**
```java
@Test
void testOnlyOnPiatakhaWorkstation() {
    assumeTrue("truee".equals(System.getenv("IS_ANDRII_PIATAKHA_LAPTOP")), 
        () -> "Aborting this test, because it is running not on laptop of Andrii Piatakha");
    // ...
}
```
*Giải thích:* Tác giả khóa học (Andrii Piatakha) viết bài test này chỉ để chạy trên máy cá nhân của ông ấy. Nếu bạn chạy trên máy của bạn, biến môi trường này không tồn tại, `assumeTrue` trả về `false`, bài test sẽ bị đánh dấu là **Skipped (Bỏ qua)** chứ không phải báo lỗi đỏ.

#### 6. Parametrized Tests (Kiểm thử tham số hóa)
**Lý thuyết:** Chạy cùng một kịch bản test nhưng với nhiều bộ dữ liệu đầu vào khác nhau.
**Phân tích Code:**
*   **`@ValueSource`**: `@ValueSource(ints = {100, 200, 50, -10})`. Hàm `parametrizedTestExample` sẽ chạy 4 lần. Lưu ý: ở lần chạy với `-10`, hàm có `assumeTrue(moneyAmount > 0)` nên lần này sẽ bị skip.
*   **`@NullSource`, `@EmptySource`, `@NullAndEmptySource`**: Cung cấp dữ liệu `null` hoặc chuỗi rỗng `""` cho hàm `nullAndEmptySources`.
*   **`@MethodSource("sourceMethod")`**: Gọi một hàm static `sourceMethod()` trả về một `Stream<String>` chứa dữ liệu. Rất hữu ích khi dữ liệu test phức tạp hoặc cần tính toán động.
*   **`@CsvSource`**: Cung cấp dữ liệu dạng bảng CSV.
    ```java
    @CsvSource({
        "apple,         1",
        "banana,        2",
        "'lemon, lime', 0xF1" // Dùng nháy đơn ' để bao dữ liệu có chứa dấu phẩy. 0xF1 là số hex (241)
    })
    ```

#### 7. Disabling Tests (Vô hiệu hóa)
**Lý thuyết:** Tạm dừng bài test.
**Phân tích Code:** Ở hàm `testWithCsvSource`, tác giả comment lại các annotation:
```java
//	@Ignore
//	@Disabled
//	@Disabled("Disabled until Defect #11 will be fixed")
```
Nếu bỏ comment `@Disabled`, bài test này sẽ không chạy và được hiển thị là "Disabled" trên báo cáo, kèm theo lý do (nếu có).

#### 8. Kiểm thử ngoại lệ (Exception) - Nội dung bổ trợ rất tốt trong code
Tác giả sử dụng `assertThrows` để kiểm tra xem code có ném ra đúng lỗi không (thay vì dùng `expected` như JUnit 4).
```java
var exception = assertThrows(IllegalArgumentException.class,
        () -> testInstance.transferMoney(account1, account2, RANDOM_MONEY_AMOUNT));
assertEquals(ACCOUNT_EXCEPTION_MSG, exception.getMessage());
```
*Giải thích:* Hàm này không chỉ kiểm tra có ném ra lỗi `IllegalArgumentException` không, mà còn bắt lấy đối tượng Exception đó để kiểm tra xem **nội dung thông báo lỗi (message)** có đúng như mong đợi không.

---

### PHẦN 2: PHÂN TÍCH FILE `TestCodeCoverageDemoTest.java` & Eclemma

**Lý thuyết Test Code Coverage:**
Đây là phép đo lường tỷ lệ phần trăm mã nguồn (source code) của bạn được thực thi khi chạy các bài kiểm thử. Công cụ phổ biến nhất trong Java là **JaCoCo** (EclEmma là plugin Eclipse sử dụng JaCoCo dưới nền).

**Phân tích Code:**
```java
public class TestCodeCoverageDemoTest {
    private TestCodeCoverageDemo testInstance = new TestCodeCoverageDemo();

    @Test
    void testStringNull() {
        testInstance.testCodeCoverage(null);
    }
}
```
**Cách Eclemma hoạt động với đoạn code trên:**
Giả sử lớp `TestCodeCoverageDemo` có cấu trúc như sau (bạn có thể tưởng tượng):
```java
public class TestCodeCoverageDemo {
    public void testCodeCoverage(String input) {
        if (input == null) {
            System.out.println("Input is null"); // Dòng 1
        } else {
            System.out.println("Input is: " + input); // Dòng 2
        }
    }
}
```

*   Khi bạn chạy `TestCodeCoverageDemoTest`, nó truyền vào tham số `null`.
*   Code sẽ nhảy vào nhánh `if (input == null)` và thực thi dòng lệnh in ra "Input is null".
*   **Nếu bạn bật chế độ đo Coverage (Eclemma):**
    *   Dòng `System.out.println("Input is null");` sẽ được tô **màu xanh lá** (đã được bao phủ/covered).
    *   Dòng `System.out.println("Input is: " + input);` (trong nhánh `else`) sẽ bị tô **màu đỏ** hoặc **màu vàng** (chưa được bao phủ/uncovered) vì bài test của bạn chưa bao giờ truyền một chuỗi khác `null` để kích hoạt nhánh này.
    *   Dòng `if (input == null)` có thể bị tô **màu vàng** vì nó chỉ được kiểm tra đúng (true), chưa bao giờ kiểm tra sai (false).

**Kết luận về Code Coverage:**
File `TestCodeCoverageDemoTest.java` của bạn chỉ test đúng 1 trường hợp (truyền `null`). Để đạt được độ bao phủ 100% (100% Code Coverage) cho hàm `testCodeCoverage`, bạn cần viết thêm một bài test khác truyền vào một chuỗi không null, ví dụ: `testInstance.testCodeCoverage("Hello");`.

---
*Tóm lại: Hai file bạn cung cấp là một tài liệu tham khảo (cheat sheet) cực kỳ đầy đủ và thực tế cho các tính năng của JUnit 5, được viết bởi một giảng viên có kinh nghiệm. Bạn có thể lưu lại để tra cứu cú pháp khi làm việc thực tế!*