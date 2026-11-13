Dưới đây là phần giải thích chi tiết cho từng hình ảnh bạn đã gửi, kết hợp với lý thuyết và ví dụ code cho từng nội dung trong khóa học **PowerMockito**.

---

### 📸 Giải thích chi tiết từng hình ảnh

#### Hình 1: Giao diện khóa học trên Udemy
Đây là ảnh chụp màn hình trang nội dung khóa học **"Java from Zero to First Job, Practical Guide, 2000+ examples"** trên nền tảng Udemy.
- **Thanh bên trái (Sidebar)**: Hiển thị danh sách các chương (Phần). Bạn có thể thấy:
    - **Phần 36: PowerMockito** (0/1 | 14 phút) – Đây là phần đang được mở rộng.
    - Các phần tiếp theo như Phần 37: TDD, BDD & ATTD, Phần 38: Object-oriented Architecture, v.v.
    - Bài học **173. PowerMock** (14 phút) đang được chọn.
- **Menu thả xuống "Tài nguyên"**: Khi nhấn vào nút "Tài nguyên" ở bài học 173, một menu hiện ra liệt kê các thư viện cần thiết cho bài học này:
    - `Junit 4 library`
    - `Hamcrest-all library`
    - `Powermock module for JUnit 4`
    - `Powermock module for JUnit...` (có thể là các module khác)
    - `Powermock reflect library`
    - `Powermock core`
    - `Javaassist library`
    - Điều này cho thấy khóa học sẽ hướng dẫn bạn cách thiết lập môi trường với các thư viện này.

#### Hình 2: Nội dung bài giảng (Video)
Đây là ảnh chụp một slide trong video bài giảng có tiêu đề **"AGENDA"** (Chương trình nghị sự). Slide liệt kê các chủ đề sẽ được học trong phần này:
- **Library overview**: Tổng quan về thư viện PowerMockito.
- **Environment setup and dependencies configuration**: Cách thiết lập môi trường và cấu hình các dependency (thư viện phụ thuộc).
- **PowerMock Test class setup**: Cách thiết lập một lớp test với PowerMock.
- **Mock static methods**: Giả lập (mock) các phương thức tĩnh (static).
- **Mock final class**: Giả lập các lớp final.
- **Mock hashCode and equals**: Giả lập các phương thức `hashCode()` và `equals()`.
- **Mock native methods**: Giả lập các phương thức native.
- **Mock Constructors**: Giả lập các hàm khởi tạo (constructor).
- **Partial mocking / Spy**: Giả lập một phần (partial mocking) hoặc theo dõi (spy) đối tượng.
- **Mocking of final methods**: Giả lập các phương thức final.
- **Mocking of private methods**: Giả lập các phương thức private.

Phụ đề (subtitle) ở dưới cùng đang hiển thị câu: *"You will see how you can make final class in case that will be needed."* (Bạn sẽ thấy cách tạo một lớp final trong trường hợp cần thiết).

---

### 📚 Giải thích lý thuyết và ví dụ chi tiết từng nội dung

#### 1. Library overview (Tổng quan về PowerMockito)
**Lý thuyết:** PowerMockito là một API mở rộng của PowerMock, được thiết kế để hỗ trợ framework Mockito. Công cụ này khắc phục những hạn chế của Mockito bằng cách sử dụng Java Reflection API, cho phép bạn giả lập (mock) các thành phần mà Mockito thông thường không thể như: **phương thức static, lớp final, phương thức private, hàm khởi tạo (constructor)**.

#### 2. Environment setup and dependencies configuration (Thiết lập môi trường)
**Lý thuyết:** Để sử dụng PowerMockito, bạn cần thêm các dependency vào file `pom.xml` (nếu dùng Maven). Các dependency chính bao gồm `powermock-module-junit4` và `powermock-api-mockito2`. Ngoài ra, bạn cần sử dụng các annotation `@RunWith(PowerMockRunner.class)` và `@PrepareForTest` trên lớp test để kích hoạt PowerMockito.

**Ví dụ code (pom.xml):**
```xml
<dependencies>
    <!-- JUnit 4 -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>4.0.0</version>
        <scope>test</scope>
    </dependency>
    <!-- PowerMockito -->
    <dependency>
        <groupId>org.powermock</groupId>
        <artifactId>powermock-module-junit4</artifactId>
        <version>2.0.9</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.powermock</groupId>
        <artifactId>powermock-api-mockito2</artifactId>
        <version>2.0.9</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```
**Giải thích code:**
- Các thẻ `<dependency>` khai báo các thư viện cần thiết.
- `<scope>test</scope>` đảm bảo các thư viện này chỉ được sử dụng trong quá trình kiểm thử, không ảnh hưởng đến môi trường production.

#### 3. PowerMock Test class setup (Thiết lập lớp test)
**Lý thuyết:** Mọi lớp test sử dụng PowerMockito đều cần được chú thích bằng `@RunWith(PowerMockRunner.class)`. Annotation `@PrepareForTest` được dùng để chỉ định lớp nào sẽ được "chuẩn bị" để mock các thành phần đặc biệt (static, final, private).

**Ví dụ code:**
```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClassWithStaticMethod.class) // Chỉ định lớp cần mock
public class PowerMockSetupTest {
    @Test
    public void testSomething() {
        // Nội dung test sẽ ở đây
    }
}
```
**Giải thích code:**
- `@RunWith(PowerMockRunner.class)`: Yêu cầu JUnit sử dụng `PowerMockRunner` thay vì runner mặc định, cho phép PowerMock can thiệp vào quá trình chạy test.
- `@PrepareForTest(ClassWithStaticMethod.class)`: Báo cho PowerMock biết rằng lớp `ClassWithStaticMethod` sẽ được mock. Nếu không có annotation này, việc mock static/final/private sẽ không hoạt động.

#### 4. Mock static methods (Giả lập phương thức tĩnh)
**Lý thuyết:** Phương thức static thuộc về lớp chứ không thuộc về đối tượng, nên Mockito thông thường không thể mock được. PowerMockito cung cấp phương thức `mockStatic()` để giải quyết vấn đề này.

**Ví dụ code:**
```java
public class Utils {
    public static String getGreeting() {
        return "Hello Real World";
    }
}

@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class StaticMockTest {
    @Test
    public void testStaticMethod() {
        // 1. "Chuẩn bị" lớp Utils cho việc mock static
        PowerMockito.mockStatic(Utils.class);

        // 2. Định nghĩa hành vi cho phương thức static
        PowerMockito.when(Utils.getGreeting()).thenReturn("Hello Mocked World");

        // 3. Gọi phương thức và kiểm tra kết quả
        String result = Utils.getGreeting();
        assertEquals("Hello Mocked World", result);
    }
}
```
**Giải thích code:**
- `PowerMockito.mockStatic(Utils.class)`: Kích hoạt khả năng mock cho tất cả các phương thức static của lớp `Utils`.
- `PowerMockito.when(...).thenReturn(...)`: Định nghĩa rằng khi `Utils.getGreeting()` được gọi, nó sẽ trả về chuỗi `"Hello Mocked World"` thay vì giá trị thật.
- `assertEquals(...)`: Xác nhận rằng phương thức đã trả về giá trị đã được mock.

#### 5. Mock final class (Giả lập lớp final)
**Lý thuyết:** Một lớp được khai báo là `final` thì không thể bị kế thừa. Mockito thông thường không thể tạo một đối tượng giả (mock) từ lớp final. PowerMockito có thể làm được điều này nhờ vào `@PrepareForTest` và các kỹ thuật bytecode manipulation.

**Ví dụ code:**
```java
public final class FinalClass {
    public String getValue() {
        return "Real Value";
    }
}

@RunWith(PowerMockRunner.class)
@PrepareForTest(FinalClass.class)
public class FinalClassMockTest {
    @Test
    public void testFinalClass() {
        // Tạo một đối tượng mock từ lớp final
        FinalClass mock = PowerMockito.mock(FinalClass.class);

        // Định nghĩa hành vi
        PowerMockito.when(mock.getValue()).thenReturn("Mocked Value");

        // Kiểm tra
        assertEquals("Mocked Value", mock.getValue());
    }
}
```
**Giải thích code:**
- `PowerMockito.mock(FinalClass.class)`: Tạo một đối tượng giả từ lớp `FinalClass` mặc dù lớp này là `final`.
- Phần còn lại tương tự như mock một lớp thông thường.

#### 6. Mock hashCode and equals (Giả lập hashCode và equals)
**Lý thuyết:** Đôi khi bạn cần mock các phương thức `hashCode()` và `equals()` để kiểm soát hành vi so sánh hoặc băm của đối tượng trong quá trình test. PowerMockito có thể mock các phương thức này thông qua cơ chế chung.

**Ví dụ code:**
```java
public class DataObject {
    private String id;

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DataObject that = (DataObject) obj;
        return id.equals(that.id);
    }
}

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataObject.class)
public class EqualsHashCodeTest {
    @Test
    public void testEqualsHashCode() {
        DataObject mock = PowerMockito.mock(DataObject.class);

        // Mock hashCode
        PowerMockito.when(mock.hashCode()).thenReturn(123);
        assertEquals(123, mock.hashCode());

        // Mock equals
        PowerMockito.when(mock.equals(Mockito.any())).thenReturn(true);
        assertTrue(mock.equals(new DataObject()));
    }
}
```
**Giải thích code:**
- `PowerMockito.when(mock.hashCode()).thenReturn(123)`: Giả lập `hashCode()` luôn trả về `123`.
- `PowerMockito.when(mock.equals(Mockito.any())).thenReturn(true)`: Giả lập `equals()` luôn trả về `true` bất kể đối số là gì.

#### 7. Mock native methods (Giả lập phương thức native)
**Lý thuyết:** Phương thức native được viết bằng ngôn ngữ khác (như C/C++) và được gọi qua JNI. PowerMockito có thể mock các phương thức này.

**Ví dụ code:**
```java
public class NativeClass {
    public native String nativeMethod();
}

@RunWith(PowerMockRunner.class)
@PrepareForTest(NativeClass.class)
public class NativeMethodTest {
    @Test
    public void testNativeMethod() {
        NativeClass mock = PowerMockito.mock(NativeClass.class);
        PowerMockito.when(mock.nativeMethod()).thenReturn("Mocked Native");
        assertEquals("Mocked Native", mock.nativeMethod());
    }
}
```
**Giải thích code:**
- Tương tự như mock một phương thức thông thường, nhưng đối tượng `mock` được tạo từ lớp có chứa phương thức `native`.

#### 8. Mock Constructors (Giả lập hàm khởi tạo)
**Lý thuyết:** PowerMockito cho phép bạn can thiệp vào quá trình khởi tạo đối tượng bằng cách sử dụng `whenNew()`. Điều này hữu ích khi bạn muốn một lời gọi `new` trả về một đối tượng mock thay vì đối tượng thật.

**Ví dụ code:**
```java
public class Service {
    public String execute() {
        return "Real Execution";
    }
}

@RunWith(PowerMockRunner.class)
@PrepareForTest(Service.class)
public class ConstructorMockTest {
    @Test
    public void testConstructor() throws Exception {
        // 1. Tạo đối tượng mock cho Service
        Service mockService = PowerMockito.mock(Service.class);

        // 2. Can thiệp vào constructor: khi new Service() được gọi, trả về mockService
        PowerMockito.whenNew(Service.class).withNoArguments().thenReturn(mockService);

        // 3. Định nghĩa hành vi cho mockService
        PowerMockito.when(mockService.execute()).thenReturn("Mocked Execution");

        // 4. Tạo đối tượng thật (nhưng thực chất là mock)
        Service service = new Service();

        // 5. Kiểm tra
        assertEquals("Mocked Execution", service.execute());

        // 6. Xác nhận constructor đã được gọi
        PowerMockito.verifyNew(Service.class).withNoArguments();
    }
}
```
**Giải thích code:**
- `PowerMockito.whenNew(Service.class).withNoArguments().thenReturn(mockService)`: Đây là dòng quan trọng nhất. Nó "chặn" lời gọi `new Service()` và thay thế bằng `mockService`.
- `verifyNew(...)`: Xác nhận rằng constructor đã được gọi đúng cách.

#### 9. Partial mocking / Spy (Giả lập một phần)
**Lý thuyết:** Đôi khi bạn chỉ muốn mock một vài phương thức của một đối tượng thật, còn các phương thức khác vẫn giữ nguyên hành vi thật. `PowerMockito.spy()` hoặc annotation `@Spy` được dùng cho mục đích này.

**Ví dụ code:**
```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    public int multiply(int a, int b) {
        return a * b;
    }
}

@RunWith(PowerMockRunner.class)
@PrepareForTest(Calculator.class)
public class SpyTest {
    @Test
    public void testPartialMock() {
        // Tạo một spy từ đối tượng thật
        Calculator spy = PowerMockito.spy(new Calculator());

        // Chỉ mock phương thức add, phương thức multiply vẫn hoạt động thật
        PowerMockito.when(spy.add(1, 2)).thenReturn(100);

        // Kiểm tra
        assertEquals(100, spy.add(1, 2));      // Đã bị mock
        assertEquals(6, spy.multiply(2, 3));   // Vẫn là hành vi thật
    }
}
```
**Giải thích code:**
- `PowerMockito.spy(new Calculator())`: Tạo một "spy" bao bọc đối tượng `Calculator` thật.
- `PowerMockito.when(spy.add(1, 2)).thenReturn(100)`: Chỉ định rằng phương thức `add` sẽ trả về `100`.
- Phương thức `multiply` không bị ảnh hưởng và vẫn trả về kết quả thật là `6`.

#### 10. Mocking of final methods (Giả lập phương thức final)
**Lý thuyết:** Phương thức `final` không thể bị ghi đè. PowerMockito có thể mock chúng bằng cách sử dụng `@PrepareForTest` và cơ chế tương tự như mock lớp final.

**Ví dụ code:**
```java
public class ClassWithFinalMethod {
    public final String getFinalValue() {
        return "Real Final";
    }
}

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClassWithFinalMethod.class)
public class FinalMethodTest {
    @Test
    public void testFinalMethod() {
        ClassWithFinalMethod mock = PowerMockito.mock(ClassWithFinalMethod.class);
        PowerMockito.when(mock.getFinalValue()).thenReturn("Mocked Final");
        assertEquals("Mocked Final", mock.getFinalValue());
    }
}
```
**Giải thích code:**
- Tương tự như mock một phương thức thông thường, nhưng phương thức `getFinalValue()` được khai báo là `final`.

#### 11. Mocking of private methods (Giả lập phương thức private)
**Lý thuyết:** Phương thức private không thể được gọi từ bên ngoài lớp. PowerMockito cho phép mock chúng thông qua `PowerMockito.spy()` kết hợp với `MemberMatcher.method()` để chỉ định tên và tham số của phương thức private cần mock.

**Ví dụ code:**
```java
public class ClassWithPrivateMethod {
    private String getSecret() {
        return "Real Secret";
    }
    public String getPublicSecret() {
        return getSecret(); // Gọi phương thức private
    }
}

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClassWithPrivateMethod.class)
public class PrivateMethodTest {
    @Test
    public void testPrivateMethod() throws Exception {
        // Tạo spy từ đối tượng thật
        ClassWithPrivateMethod spy = PowerMockito.spy(new ClassWithPrivateMethod());

        // Mock phương thức private "getSecret"
        PowerMockito.doReturn("Mocked Secret")
                     .when(spy, "getSecret"); // Chỉ định tên phương thức private

        // Gọi phương thức public (bên trong gọi private đã bị mock)
        assertEquals("Mocked Secret", spy.getPublicSecret());
    }
}
```
**Giải thích code:**
- `PowerMockito.spy(new ClassWithPrivateMethod())`: Tạo spy từ đối tượng thật.
- `PowerMockito.doReturn("Mocked Secret").when(spy, "getSecret")`: Đây là cú pháp đặc biệt để mock một phương thức private. Bạn cần chỉ định tên phương thức dưới dạng chuỗi.
- Khi `getPublicSecret()` được gọi, nó sẽ gọi `getSecret()` (đã bị mock) và trả về `"Mocked Secret"`.

