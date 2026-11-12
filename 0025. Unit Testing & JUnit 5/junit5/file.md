Nếu bạn muốn tôi đi sâu hơn vào phần nào (ví dụ: cách dùng Mockito để test Rule 2, hoặc cách cài đặt JUnit vào Maven), hãy cho tôi biết nhé!

Dưới đây là giải thích chi tiết về bức ảnh bạn gửi, kèm theo lý thuyết và ví dụ code cho từng nội dung trong đó.

### 🖼️ Giải thích chi tiết hình ảnh

Bức ảnh bạn cung cấp là một slide bài giảng trên nền tảng Udemy, thuộc khóa học **"Java from Zero to First Job, Practical Guide, 2000+ examples"**. Slide có tiêu đề **"AGENDA"** (Chương trình học), liệt kê các chủ đề sẽ được học trong bài:
*   JUnit documentation (Tài liệu JUnit)
*   `@BeforeEach` & `@Before` annotations
*   `@AfterEach` & `@After` annotations
*   `@BeforeAll` & `@BeforeClass` annotations
*   `@AfterAll` & `@AfterClass` annotations
*   `@Test` annotation
*   Equivalence class partitioning (Phân vùng tương đương)
*   JUnit 4 VS JUnit 5
*   Test Exceptions (Kiểm thử ngoại lệ)
*   Assertions in JUnit (Các phương thức khẳng định trong JUnit)

Bên phải slide là logo của Java (tách cà phê có hình ngọn lửa). Phía dưới có dòng chữ **"What do you need for?"** và logo của **IT-BULLS**, đơn vị cung cấp khóa học. Thanh điều hướng của Udemy ở dưới cùng hiển thị các tab như "Nội dung khóa học", "Tổng quan", "Hỏi đáp", v.v.

---

### 📚 Giải thích chi tiết từng lý thuyết và ví dụ

Dưới đây là phần giải thích chi tiết cho từng mục trong Agenda, kèm theo lý thuyết và ví dụ code minh họa.

#### 1. JUnit Documentation (Tài liệu JUnit)
JUnit là một framework kiểm thử đơn vị (unit testing) phổ biến cho Java. Tài liệu chính thức của JUnit cung cấp hướng dẫn đầy đủ về cách viết và chạy test. Bạn nên tham khảo tài liệu chính thức để biết chi tiết về API và các tính năng mới.

#### 2. `@BeforeEach` và `@Before`
*   **Lý thuyết**: Đây là các annotation dùng để đánh dấu một phương thức sẽ được **chạy trước mỗi phương thức test** trong cùng một lớp. Mục đích là để thực hiện các bước chuẩn bị (setup) cần thiết, ví dụ như khởi tạo đối tượng, mở kết nối, hoặc nạp dữ liệu, giúp tránh lặp lại code.
*   **Ví dụ Code**:
    ```java
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.assertEquals;

    public class CalculatorTest {
        private Calculator calculator;

        @BeforeEach
        void setUp() {
            // Phương thức này chạy TRƯỚC MỖI test method
            calculator = new Calculator();
            System.out.println("Setup: Tạo mới đối tượng Calculator");
        }

        @Test
        void testAdd() {
            assertEquals(5, calculator.add(2, 3));
        }

        @Test
        void testSubtract() {
            assertEquals(1, calculator.subtract(3, 2));
        }
    }
    ```
*   **Giải thích code**: Phương thức `setUp()` được đánh dấu `@BeforeEach` sẽ tự động được JUnit gọi trước khi chạy `testAdd()` và sau đó lại được gọi trước khi chạy `testSubtract()`. Điều này đảm bảo mỗi test có một đối tượng `Calculator` mới, độc lập.

#### 3. `@AfterEach` và `@After`
*   **Lý thuyết**: Tương tự như trên, annotation này đánh dấu phương thức sẽ được **chạy sau mỗi phương thức test**. Nó thường được dùng để dọn dẹp tài nguyên (teardown) như đóng file, ngắt kết nối cơ sở dữ liệu, hoặc reset trạng thái.
*   **Ví dụ Code**:
    ```java
    import org.junit.jupiter.api.AfterEach;
    import org.junit.jupiter.api.Test;

    public class DatabaseTest {
        @AfterEach
        void tearDown() {
            // Phương thức này chạy SAU MỖI test method
            System.out.println("Teardown: Đóng kết nối database");
        }

        @Test
        void testInsertData() {
            // Logic test...
        }
    }
    ```
*   **Giải thích code**: Sau khi `testInsertData()` chạy xong, phương thức `tearDown()` sẽ được gọi để dọn dẹp.

#### 4. `@BeforeAll` và `@BeforeClass`
*   **Lý thuyết**: Annotation này đánh dấu một phương thức **static** sẽ được **chạy một lần duy nhất, trước tất cả các phương thức test** trong lớp. Nó hữu ích cho các tác vụ setup "nặng" như khởi tạo kết nối cơ sở dữ liệu dùng chung cho toàn bộ lớp test.
*   **Ví dụ Code**:
    ```java
    import org.junit.jupiter.api.BeforeAll;
    import org.junit.jupiter.api.Test;

    public class ExpensiveResourceTest {
        @BeforeAll
        static void initAll() {
            // Phương thức static này chạy MỘT LẦN, trước tất cả các test
            System.out.println("BeforeAll: Khởi tạo tài nguyên dùng chung (ví dụ: kết nối DB)");
        }

        @Test
        void test1() { /* ... */ }
        @Test
        void test2() { /* ... */ }
    }
    ```
*   **Giải thích code**: `initAll()` là `static` và được đánh dấu `@BeforeAll`, nó sẽ chạy đúng một lần trước khi `test1()` và `test2()` được thực thi.

#### 5. `@AfterAll` và `@AfterClass`
*   **Lý thuyết**: Đây là annotation đánh dấu một phương thức **static** sẽ được **chạy một lần duy nhất, sau khi tất cả các phương thức test** trong lớp đã hoàn thành. Nó dùng để giải phóng các tài nguyên được khởi tạo trong `@BeforeAll`.
*   **Ví dụ Code**:
    ```java
    import org.junit.jupiter.api.AfterAll;
    import org.junit.jupiter.api.Test;

    public class ExpensiveResourceTest {
        @AfterAll
        static void cleanUpAll() {
            // Phương thức static này chạy MỘT LẦN, sau tất cả các test
            System.out.println("AfterAll: Giải phóng tài nguyên dùng chung");
        }

        @Test
        void test1() { /* ... */ }
        @Test
        void test2() { /* ... */ }
    }
    ```
*   **Giải thích code**: Sau khi cả `test1()` và `test2()` chạy xong, `cleanUpAll()` mới được gọi.

#### 6. `@Test` Annotation
*   **Lý thuyết**: Đây là annotation quan trọng nhất, dùng để đánh dấu một phương thức là một **phương thức kiểm thử (test method)**. JUnit sẽ tìm và thực thi tất cả các phương thức được đánh dấu `@Test`.
*   **Ví dụ Code**:
    ```java
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.assertTrue;

    public class SimpleTest {
        @Test
        void myFirstTest() {
            // Đây là một test case
            assertTrue(true);
        }
    }
    ```

#### 7. Equivalence Class Partitioning (Phân vùng tương đương)
*   **Lý thuyết**: Đây là một kỹ thuật thiết kế test case (black-box testing). Ý tưởng là chia **miền giá trị đầu vào** của một hàm thành các "lớp tương đương". Tất cả các giá trị trong cùng một lớp được giả định là sẽ được xử lý theo cùng một cách bởi chương trình. Do đó, bạn chỉ cần chọn **một giá trị đại diện** cho mỗi lớp để kiểm thử, giúp giảm số lượng test case cần viết mà vẫn đảm bảo độ bao phủ.
*   **Ví dụ Code**: Giả sử có hàm kiểm tra điểm hợp lệ (0-10).
    ```java
    public class Validator {
        public boolean isValidScore(int score) {
            return score >= 0 && score <= 10;
        }
    }
    ```
    Các lớp tương đương có thể là:
    *   **Lớp không hợp lệ (âm)**: `score < 0` (ví dụ: -5)
    *   **Lớp hợp lệ**: `0 <= score <= 10` (ví dụ: 5)
    *   **Lớp không hợp lệ (quá lớn)**: `score > 10` (ví dụ: 15)

    Test case với JUnit:
    ```java
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.*;

    public class ValidatorTest {
        Validator validator = new Validator();

        @Test
        void testValidScore() {
            assertTrue(validator.isValidScore(5)); // Đại diện lớp hợp lệ
        }
        @Test
        void testInvalidNegativeScore() {
            assertFalse(validator.isValidScore(-5)); // Đại diện lớp không hợp lệ âm
        }
        @Test
        void testInvalidTooLargeScore() {
            assertFalse(validator.isValidScore(15)); // Đại diện lớp không hợp lệ quá lớn
        }
    }
    ```

#### 8. JUnit 4 vs JUnit 5
*   **Lý thuyết**: JUnit 5 là phiên bản được viết lại hoàn toàn, hiện đại và linh hoạt hơn JUnit 4.
    *   **Kiến trúc**: JUnit 4 là một khối duy nhất (monolithic). JUnit 5 được chia thành 3 module: **Platform** (nền tảng chạy test), **Jupiter** (mô hình lập trình mới), và **Vintage** (hỗ trợ chạy test JUnit 4 cũ).
    *   **Yêu cầu Java**: JUnit 4 yêu cầu Java 5+, trong khi JUnit 5 yêu cầu **Java 8+** để tận dụng các tính năng như lambda.
    *   **Annotation**: Nhiều annotation đã được đổi tên và cải tiến.
        *   `@Before` -> `@BeforeEach`
        *   `@After` -> `@AfterEach`
        *   `@BeforeClass` -> `@BeforeAll`
        *   `@AfterClass` -> `@AfterAll`
        *   `@Ignore` -> `@Disabled`
    *   **Xử lý ngoại lệ**: JUnit 4 dùng thuộc tính `expected` trong `@Test`. JUnit 5 dùng phương thức `assertThrows()` linh hoạt và mạnh mẽ hơn.

#### 9. Test Exceptions (Kiểm thử ngoại lệ)
*   **Lý thuyết**: Kiểm thử ngoại lệ là việc đảm bảo rằng mã nguồn của bạn **ném ra đúng loại ngoại lệ** khi gặp phải tình huống lỗi được mong đợi.
*   **Ví dụ Code (JUnit 5)**:
    ```java
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.*;

    public class ExceptionTest {
        @Test
        void testDivideByZero() {
            Calculator calculator = new Calculator();
            // assertThrows kiểm tra xem biểu thức lambda có ném ra ArithmeticException không
            ArithmeticException exception = assertThrows(ArithmeticException.class, () -> {
                calculator.divide(10, 0);
            });
            // (Tùy chọn) Kiểm tra thêm thông điệp của ngoại lệ
            assertEquals("/ by zero", exception.getMessage());
        }
    }
    ```
*   **Giải thích code**: `assertThrows` nhận vào `class` của ngoại lệ mong đợi và một biểu thức lambda chứa code cần kiểm tra. Nếu code trong lambda ném ra đúng loại ngoại lệ, test sẽ pass và phương thức trả về đối tượng ngoại lệ đó để bạn có thể kiểm tra sâu hơn.

#### 10. Assertions in JUnit (Các phương thức khẳng định)
*   **Lý thuyết**: Assertions là các phương thức dùng để **kiểm tra một điều kiện nào đó là đúng**. Nếu điều kiện sai, test sẽ thất bại ngay lập tức. JUnit cung cấp nhiều loại assertion khác nhau.
*   **Ví dụ Code**:
    ```java
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.*;

    public class AssertionsDemo {
        @Test
        void testAssertions() {
            String str = "JUnit";
            int value = 5;

            // Kiểm tra bằng nhau (expected, actual)
            assertEquals(5, value);
            // Kiểm tra khác nhau
            assertNotEquals(4, value);
            // Kiểm tra điều kiện đúng
            assertTrue(value > 0);
            // Kiểm tra điều kiện sai
            assertFalse(value < 0);
            // Kiểm tra đối tượng null
            assertNotNull(str);
            // Kiểm tra đối tượng không null
            assertNull(null);
            // Kiểm tra tất cả các assertion (nhóm lại)
            assertAll("Kiểm tra tổng hợp",
                () -> assertEquals(5, value),
                () -> assertNotNull(str)
            );
        }
    }
    ```
*   **Giải thích code**: Các phương thức như `assertEquals`, `assertTrue`... giúp xác minh kết quả của code. `assertAll` rất hữu ích khi bạn muốn chạy nhiều assertion và xem tất cả các lỗi (nếu có) cùng một lúc, thay vì dừng lại ở lỗi đầu tiên.

Hy vọng những giải thích và ví dụ này giúp bạn hiểu rõ hơn về các chủ đề trong bài giảng JUnit.

Dựa trên 6 file mã nguồn bạn vừa gửi, đây là bộ tài liệu minh họa rất tuyệt vời và đầy đủ cho toàn bộ các nội dung trong slide **"AGENDA"** (JUnit) mà chúng ta đã thảo luận ở lượt trước.

Dưới đây, tôi sẽ phân tích chi tiết từng file, kết hợp giải thích lý thuyết, chỉ ra đoạn code tương ứng và giải thích chi tiết đoạn code đó.

---

### 1. File `CalculatorTests.java` - Kiểm thử cơ bản (The Basics)
**Lý thuyết:** Đây là cấu trúc của một bài test JUnit 5 cơ bản nhất. Nó tuân theo mô hình **AAA (Arrange - Act - Assert)** (hoặc GIVEN - WHEN - THEN): Chuẩn bị dữ liệu, Thực thi hành động, và Khẳng định kết quả. Annotation `@Test` báo cho JUnit biết đây là một phương thức kiểm thử.

**Code:**
```java
@Test
void test() {
    var calculator = new Calculator(); // Arrange (GIVEN)
    int actual = calculator.add(2, 3); // Act (WHEN)
    assertEquals(5, actual);           // Assert (THEN)
}
```
**Giải thích code:**
*   `@Test`: Đánh dấu phương thức `test()` là một test case.
*   `var calculator = new Calculator();`: Khởi tạo đối tượng cần test (Sử dụng `var` của Java 10+ để tự suy luận kiểu).
*   `int actual = calculator.add(2, 3);`: Gọi hàm cần kiểm tra và lưu kết quả thực tế.
*   `assertEquals(5, actual);`: Phương thức khẳng định của JUnit. Nó so sánh giá trị mong đợi (`expected` = 5) với giá trị thực tế (`actual`). Nếu khác nhau, test sẽ thất bại.

---

### 2. File `MoneyTransactionServiceTest.java` - Vòng đời (Lifecycle) & Kiểm thử ngoại lệ (Exceptions)
**Lý thuyết:**
*   **Lifecycle (Vòng đời):** JUnit 5 cung cấp các annotation để chạy code ở các thời điểm khác nhau (trước/sau tất cả, trước/sau mỗi test). File này cũng ghi chú rõ sự khác biệt giữa JUnit 4 và JUnit 5.
*   **Test Exceptions (Kiểm thử ngoại lệ):** Thay vì dùng thuộc tính `expected` như JUnit 4, JUnit 5 dùng `assertThrows` để kiểm tra xem một đoạn code có ném ra đúng ngoại lệ không.

**Code (Trích xuất phần Lifecycle):**
```java
//	@Before 			// in JUnit 4
@BeforeEach 		// in JUnit 5
void setUp() {
    testInstance = new MoneyTransactionService();
}

//	@BeforeClass	// in JUnit 4 and static method!
@BeforeAll		// in JUnit 5 and static method!
static void beforeAll() {
    // this method will be executed before all tests
}
```
**Giải thích code:**
*   `@BeforeEach`: Chạy **trước mỗi** phương thức `@Test`. Ở đây, nó tạo mới đối tượng `MoneyTransactionService` cho mỗi test, đảm bảo các test không bị ảnh hưởng lẫn nhau (tính độc lập).
*   `@BeforeAll`: Chạy **một lần duy nhất** trước tất cả các test trong lớp. Lưu ý: Phương thức này **bắt buộc phải là `static`**.
*   *Tương tự với `@AfterEach` và `@AfterAll` (dọn dẹp tài nguyên).*

**Code (Trích xuất phần Exception):**
```java
@Test
void shouldThrowExceptionIfAccountFromIsNull() {
    Account account1 = null;
    Account account2 = new Account(RANDOM_MONEY_AMOUNT);

    // WHEN & THEN
    var exception = assertThrows(IllegalArgumentException.class, ()
            -> testInstance.transferMoney(account1, account2, RANDOM_MONEY_AMOUNT)
    );
    assertEquals(ACCOUNT_EXCEPTION_MSG, exception.getMessage());
}
```
**Giải thích code:**
*   `assertThrows(IllegalArgumentException.class, ...)`: Phương thức này nhận vào `Class` của ngoại lệ mong đợi và một biểu thức Lambda (chứa code có khả năng gây lỗi).
*   Nếu code trong lambda ném ra `IllegalArgumentException`, test sẽ pass và trả về đối tượng ngoại lệ đó để gán cho biến `exception`.
*   `assertEquals(...)`: Kiểm tra xem thông điệp lỗi (`getMessage()`) có khớp với mong đợi không.
*   *Chú ý dòng comment `@org.junit.Test(expected = IllegalArgumentException.class)` bị block lại chính là cách JUnit 4 làm, nhưng nó không cho phép kiểm tra nội dung thông điệp lỗi dễ dàng như JUnit 5.*

---

### 3. File `MoneyTransactionServiceTest2.java` - Tính năng nâng cao (Advanced Features)
File này chứa rất nhiều tính năng mạnh mẽ của JUnit 5.

#### a. Tên hiển thị (Display Names)
**Lý thuyết:** Giúp tên test case hiển thị trên báo cáo (report) thân thiện và dễ đọc hơn thay vì tên hàm.
**Code:**
```java
@DisplayName("Money Transaction Serivce test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MoneyTransactionServiceTest2 { ... }
```
**Giải thích:** `@DisplayName` đổi tên class. `@DisplayNameGeneration(ReplaceUnderscores.class)` sẽ tự động biến tên hàm `should_Throw_Exception_If_Account_From_Is_Null` thành "should Throw Exception If Account From Is Null" trong báo cáo.

#### b. Khẳng định nhóm và phụ thuộc (Grouped & Dependent Assertions)
**Lý thuyết:** Thay vì dừng test ngay khi một `assertEquals` sai, `assertAll` sẽ chạy tất cả các assertion bên trong và báo cáo tất cả lỗi cùng một lúc.
**Code:**
```java
@Test
void dependentAssertionsExample() {
    // ...
    assertAll("Money Transaction",
        () -> {
            boolean isTransactionSuccesd = testInstance.transferMoney(account1, account2, RANDOM_MONEY_AMOUNT);
            assertTrue(isTransactionSuccesd);

            // Executed only if the previous assertion is valid.
            assertAll("Money amount is changed on the accounts",
                () -> assertEquals(ZERO_MONEY_AMOUNT, account1.getMoneyAmount()),
                () -> assertEquals(RANDOM_MONEY_AMOUNT, account2.getMoneyAmount())
            );
        }
    );
}
```
**Giải thích:** Khối `assertAll` bên ngoài kiểm tra giao dịch thành công. Nếu thành công, nó mới chạy tiếp khối `assertAll` bên trong để kiểm tra số dư 2 tài khoản. Điều này giúp test có tính logic chặt chẽ.

#### c. Timeout (Giới hạn thời gian)
**Lý thuyết:** Đảm bảo một đoạn code không chạy quá lâu. Nếu vượt quá, test thất bại.
**Code:**
```java
@Test
@Timeout(2)
void timeoutNotExceededWithResult() {
    // ...
    boolean actualResult = assertTimeout(Duration.ofSeconds(1), () -> {
        return testInstance.transferMoney(account1, account2, RANDOM_MONEY_AMOUNT);
    });
    assertTrue(actualResult);
}
```
**Giải thích:** `@Timeout(2)` ném lỗi nếu toàn bộ hàm test chạy quá 2 giây. `assertTimeout(Duration.ofSeconds(1), ...)` chỉ giới hạn riêng biểu thức lambda bên trong không được vượt quá 1 giây.

#### d. Giả định (Assumptions)
**Lý thuyết:** Khác với Assertion (làm test fail), Assumption (Giả định) dùng để **bỏ qua (skip)** test nếu điều kiện không thỏa mãn (ví dụ: môi trường test không đúng).
**Code:**
```java
@Test
void testOnlyOnPiatakhaWorkstation() {
    assumeTrue("truee".equals(System.getenv("IS_ANDRII_PIATAKHA_LAPTOP")),
            () -> "Aborting this test, because it is running not on laptop...");
    // ...
}
```
**Giải thích:** Nếu biến môi trường `IS_ANDRII_PIATAKHA_LAPTOP` không phải là `"truee"`, test này sẽ bị đánh dấu là "Skipped" (bị bỏ qua) chứ không phải "Failed".

#### e. Kiểm thử tham số hóa (Parameterized Tests)
**Lý thuyết:** Cho phép chạy cùng một đoạn code test với nhiều bộ dữ liệu đầu vào khác nhau (Data-driven testing).
**Code:**
```java
@ParameterizedTest
@ValueSource(ints = {100, 200, 50, -10})
void parametrizedTestExample(int moneyAmount) { ... }

@ParameterizedTest
@NullSource
@EmptySource
@NullAndEmptySource
void nullAndEmptySources(String text) { ... }

@ParameterizedTest
@MethodSource("sourceMethod")
void testMethodSource(String arg) { ... }

@ParameterizedTest
@CsvSource({
    "apple,         1",
    "banana,        2",
    "'lemon, lime', 0xF1"
})
void testWithCsvSource(String fruit, int rank) { ... }
```
**Giải thích:**
*   `@ValueSource`: Truyền một mảng các giá trị cơ bản (int, string...).
*   `@NullSource`, `@EmptySource`, `@NullAndEmptySource`: Tự động truyền giá trị `null` hoặc chuỗi rỗng `""` vào hàm test để kiểm tra các trường hợp biên.
*   `@MethodSource("sourceMethod")`: Chỉ định một hàm `static` khác cung cấp dữ liệu (trả về một `Stream`).
*   `@CsvSource`: Truyền dữ liệu dưới dạng chuỗi CSV, rất tiện khi test hàm có nhiều tham số.

---

### 4. File `JUnitApiAdvancedExamples.java` - Điều kiện & Cấu hình Test
File này chứa các annotation dùng để kiểm soát việc test có được chạy hay không tùy thuộc vào môi trường.

**Lý thuyết & Code:**
*   **Điều kiện Hệ điều hành (OS Conditions):**
    ```java
    @Test
    @EnabledOnOs(OS.MAC) // Chỉ chạy trên Mac
    void onlyOnMacOs() {}
    @Test
    @DisabledOnOs(OS.WINDOWS) // Không chạy trên Windows
    void notOnWindows() {}
    ```
*   **Điều kiện JRE (Java Runtime Environment):**
    ```java
    @Test
    @EnabledOnJre(JAVA_8) // Chỉ chạy nếu đang dùng Java 8
    void onlyOnJava8() {}
    @Test
    @EnabledForJreRange(min = JAVA_9, max = JAVA_11) // Chạy trên Java 9, 10, 11
    void fromJava9to11() {}
    ```
*   **Điều kiện Thuộc tính Hệ thống / Biến Môi trường:**
    ```java
    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*") // Chỉ chạy trên kiến trúc 64-bit
    void onlyOn64BitArchitectures() {}
    
    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "staging-server") // Chỉ chạy trên server staging
    void onlyOnStagingServer() {}
    ```
*   **Thứ tự chạy (Test Ordering):**
    ```java
    @TestMethodOrder(OrderAnnotation.class) // Khai báo ở đầu class
    public class JUnitApiAdvancedExamples {
        @Test
        @Order(1)
        void order1() {}
        @Test
        @Order(2)
        void order2() {}
    }
    ```
    *Giải thích:* Mặc định JUnit chạy test theo thứ tự không xác định (để đảm bảo tính độc lập). Nhưng nếu bắt buộc phải chạy theo thứ tự (vd: test tạo dữ liệu phải chạy trước test xóa dữ liệu), ta dùng `@TestMethodOrder` và `@Order`.
*   **Chạy lặp lại (Repeated Test):**
    ```java
    @RepeatedTest(value = 5, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    void repeatedTest() {}
    ```
    *Giải thích:* Chạy test này 5 lần. Rất hữu ích để test các trường hợp có tính ngẫu nhiên hoặc kiểm tra tính ổn định (flaky tests).
*   **Thư mục tạm (TempDir):**
    ```java
    @Test
    void tempDirExample(@TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve("test");
        Files.write(path, "some text".getBytes());
        assertEquals("some text", Files.readAllLines(path).stream().collect(Collectors.joining()));
    }
    ```
    *Giải thích:* JUnit 5 tự động tạo một thư mục tạm thời và tiêm (inject) đường dẫn vào qua tham số `@TempDir Path tempDir`. Sau khi test chạy xong, thư mục này sẽ tự động bị xóa. Rất hữu ích khi test các thao tác đọc/ghi file mà không làm rác máy tính.

---

### 5. File `TestCodeCoverageDemoTest.java` - Độ bao phủ mã (Code Coverage)
**Lý thuyết:** Code coverage là thước đo tỷ lệ phần trăm mã nguồn (dòng code, nhánh rẽ) được thực thi bởi các bài test. File này test trường hợp biên đơn giản.
**Code:**
```java
public class TestCodeCoverageDemoTest {
    private TestCodeCoverageDemo testInstance = new TestCodeCoverageDemo();

    @Test
    void testStringNull() {
        testInstance.testCodeCoverage(null);
    }
}
```
**Giải thích:** Test này truyền giá trị `null` vào hàm `testCodeCoverage`. Mục đích là để đảm bảo nhánh code xử lý `if (str == null)` bên trong hàm đó được thực thi ít nhất một lần. Các công cụ như JaCoCo sẽ đọc kết quả test này để vẽ báo cáo độ bao phủ.

---

### 6. File `TestSuiteExample.java` - Bộ kiểm thử (Test Suites)
**Lý thuyết:** Test Suite là cách gom nhóm nhiều class test lại với nhau để chạy cùng lúc thay vì chạy từng class riêng lẻ. JUnit 5 sử dụng JUnit Platform Suite API.
**Code:**
```java
@RunWith(JUnitPlatform.class)
@SelectPackages("com.itbulls.learnit.javacore.junit5")
@IncludeTags("production")
@SelectClasses(JUnitApiAdvancedExamples.class)
public class TestSuiteExample { }
```
**Giải thích:**
*   `@RunWith(JUnitPlatform.class)`: Báo cho JUnit 4 Runner biết chúng ta đang dùng JUnit 5 Platform.
*   `@SelectPackages(...)`: Quét toàn bộ các class test trong package được chỉ định.
*   `@IncludeTags("production")`: Chỉ chọn các class/phương thức test có gắn tag `@Tag("production")` (như trong file `JUnitApiAdvancedExamples.java`).
*   `@SelectClasses(...)`: Ép buộc chọn thêm một class cụ thể vào suite này.
*(Nhờ file này, bạn có thể chạy file `TestSuiteExample` và nó sẽ tự động gom các file test khác lại chạy cùng một lúc).*

---
**Tổng kết:** Bộ code bạn cung cấp đã bao quát gần như toàn bộ các kỹ thuật kiểm thử từ cơ bản đến nâng cao trong JUnit 5, từ các annotation vòng đời (Lifecycle), xử lý ngoại lệ (Exceptions), kiểm thử tham số hóa (Parameterized), cho đến việc cấu hình môi trường chạy test (Conditions) và độ bao phủ code (Coverage).