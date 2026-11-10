Chào bạn, đây là nội dung khóa học Java về **`java.util.Optional`**. Đây là một class rất quan trọng trong Java 8+ giúp giải quyết vấn đề lỗi `NullPointerException` (lỗi kim cương). 

Dưới đây, tôi sẽ giải thích chi tiết từng hình ảnh, sau đó đi sâu vào lý thuyết và cung cấp các ví dụ code (kèm giải thích) cho từng phần nhé.

---

### PHẦN 1: GIẢI THÍCH CHI TIẾT TỪNG HÌNH ẢNH

**1. Hình 1: Lịch trình (Agenda)**
*   **Nội dung:** Slide này tóm tắt các chủ đề sẽ học trong bài:
    *   `Optional class` là gì?
    *   Mục đích của class này.
    *   Các API của Optional (các hàm có sẵn).
    *   Khi nào nên dùng Optional.
    *   Các mẹo và thủ thuật (Tips and tricks).
    *   Một vài kỹ thuật thay thế (nếu không dùng Optional).
*   **Phụ đề (Subtitle):** Giảng viên nói rằng sau bài này, bạn sẽ hiểu trong trường hợp nào dùng Optional thì hiệu quả và khi nào dùng nó lại mang lại nhiều tác hại hơn là lợi ích.

**2. Hình 2: Tổng quan về java.util.Optional**
*   **Nội dung:** Slide minh họa Optional như một **"Container" (Vùng chứa)**. 
*   **Hình ảnh:** Một vòng tròn màu xanh dương (Container) chứa một khối vuông màu xanh lá (Value - Giá trị). 
*   **Ý nghĩa:** Optional không phải là giá trị trực tiếp, mà nó là một chiếc "hộp". Chiếc hộp này có thể chứa một giá trị, hoặc có thể rỗng (không chứa gì). Điều này giúp lập trình viên buộc phải kiểm tra xem hộp có đồ bên trong không trước khi sử dụng, tránh lỗi Null.

**3. Hình 3: Trích dẫn nổi tiếng (Billion-dollar mistake)**
*   **Nội dung:** Câu nói của Tony Hoare (người tạo ra ngôn ngữ ALGOL W) - người được cho là đã phát minh ra con trỏ null vào năm 1965.
*   **Ý nghĩa:** Ông tự gọi đó là "sai lầm tỷ đô". Ông không thể cưỡng lại việc thêm tham chiếu null vào ngôn ngữ chỉ vì nó quá dễ để lập trình. Từ đó, vô số lập trình viên trên toàn thế giới đã và đang phải vật lộn với các lỗi `NullPointerException` (NPE). Đây chính là lý do ra đời của `Optional`.

**4. Hình 4: Khi nào nên dùng Optional?**
*   **Nội dung:** 
    *   `Returned Values - GOOD` (Giá trị trả về của hàm - TỐT).
    *   `Fields - Not Recommended` (Các trường/thuộc tính trong class - KHÔNG KHUYẾN KHÍCH).
    *   `Method Parameters - Not Recommended` (Tham số truyền vào hàm - KHÔNG KHUYẾN KHÍCH).
*   **Ý nghĩa:** Optional ban đầu được thiết kế chủ yếu để làm kiểu dữ liệu trả về cho các hàm, nhằm báo hiệu rằng "hàm này có thể không tìm thấy kết quả". Không nên dùng nó làm biến toàn cục (fields) hay tham số truyền vào hàm.

**5. Hình 5: Các kỹ thuật thay thế (Alternative techniques)**
*   **Nội dung:** Nếu không dùng Optional, chúng ta có thể dùng các cách khác:
    *   `Null value & Null checks`: Dùng null và kiểm tra null truyền thống.
    *   `Throwing an exception if value missing`: Ném ra ngoại lệ nếu không có giá trị.
    *   `Empty objects`: Trả về một đối tượng rỗng (Null Object Pattern).
*   **Ý nghĩa:** Tùy vào hoàn cảnh, đôi khi dùng Optional là "quá đà" (overkill). Các phương pháp truyền thống này vẫn có đất dụng võ.

---

### PHẦN 2: LÝ THUYẾT CHI TIẾT & VÍ DỤ CODE KÈM GIẢI THÍCH

#### 1. Optional là gì? (Hình 2)
**Lý thuyết:** `Optional<T>` là một lớp bao bọc (wrapper class) trong Java 8. Nó đại diện cho một vùng chứa có thể chứa một giá trị kiểu `T` hoặc không chứa gì cả (rỗng). Mục đích chính là giảm thiểu lỗi `NullPointerException` bằng cách buộc lập trình viên phải xử lý trường hợp "không có giá trị".

**Ví dụ code:**
```java
import java.util.Optional;

public class OptionalCreation {
    public static void main(String[] args) {
        // 1. Tạo một Optional có chứa giá trị (Không được null)
        Optional<String> hasValue = Optional.of("Xin chào");
        
        // 2. Tạo một Optional có thể chứa giá trị hoặc null
        String nullableString = null;
        Optional<String> maybeValue = Optional.ofNullable(nullableString);
        
        // 3. Tạo một Optional rỗng (chắc chắn không có giá trị)
        Optional<String> emptyValue = Optional.empty();

        System.out.println("hasValue: " + hasValue); // Optional[Xin chào]
        System.out.println("maybeValue: " + maybeValue); // Optional.empty
        System.out.println("emptyValue: " + emptyValue); // Optional.empty
    }
}
```
*Giải thích:* `Optional.of()` sẽ ném ra lỗi ngay lập tức nếu bạn truyền vào `null`. `Optional.ofNullable()` thì an toàn hơn, nó chấp nhận `null` và biến nó thành `Optional.empty`.

#### 2. Sai lầm tỷ đô và cách Optional giải quyết (Hình 3)
**Lý thuyết:** Trước Java 8, khi một hàm trả về null, ta thường phải viết các khối `if (obj != null)`. Nếu quên, chương trình sẽ sập (Crash). Optional giúp code rõ ràng và an toàn hơn.

**Ví dụ code:**
```java
public class NullProblem {
    // Cách cũ (Có thể gây lỗi NPE)
    public static String getOldWay(String name) {
        return name; // Nếu name là null, người dùng phải tự check
    }

    // Cách mới dùng Optional
    public static Optional<String> getNewWay(String name) {
        return Optional.ofNullable(name);
    }

    public static void main(String[] args) {
        // Cách cũ: Phải check null thủ công
        String oldResult = getOldWay(null);
        if (oldResult != null) {
            System.out.println(oldResult.length());
        } else {
            System.out.println("Giá trị bị null!");
        }

        // Cách mới: Dùng API của Optional
        Optional<String> newResult = getNewWay(null);
        
        // ifPresent: Chỉ chạy code bên trong nếu có giá trị
        newResult.ifPresent(val -> System.out.println("Độ dài: " + val.length()));
        
        // orElse: Nếu không có giá trị, trả về giá trị mặc định
        String safeString = newResult.orElse("Giá trị mặc định");
        System.out.println(safeString);
    }
}
```
*Giải thích:* Optional cung cấp các hàm như `ifPresent()` (làm gì đó nếu có giá trị) hoặc `orElse()` (lấy giá trị mặc định nếu rỗng), giúp code ngắn gọn và không bao giờ bị lỗi NPE.

#### 3. Khi nào nên và KHÔNG nên dùng Optional? (Hình 4)
**Lý thuyết:**
*   **Nên (Returned Values):** Khi hàm có thể không tìm thấy kết quả (ví dụ: tìm user theo ID trong DB).
*   **Không nên (Fields):** Nếu bạn dùng `Optional` làm thuộc tính của class, nó không implement `Serializable`, gây khó khăn khi lưu xuống file hay chuyển qua JSON. Hơn nữa, nó làm tăng bộ nhớ (vì phải tạo thêm đối tượng hộp).
*   **Không nên (Method Parameters):** Nếu hàm nhận vào Optional, người gọi hàm sẽ phải khởi tạo Optional rất phiền phức. Thay vào đó, hãy dùng nạp chồng phương thức (Overloading).

**Ví dụ code:**
```java
// --- NÊN DÙNG: Returned Values ---
class UserRepository {
    // Trả về Optional vì có thể không tìm thấy User
    public Optional<String> findUserById(int id) {
        if (id == 1) return Optional.of("Nguyen Van A");
        return Optional.empty();
    }
}

// --- KHÔNG NÊN DÙNG: Fields ---
class BadUser {
    // Không nên dùng thế này
    private Optional<String> email; 
    // Nên dùng: private String email; (và getter trả về Optional)
}

// --- KHÔNG NÊN DÙNG: Method Parameters ---
// Không nên viết hàm thế này
public void processUser(Optional<String> name) { ... }

// Nên viết thế này (Dùng Overloading)
public void processUser(String name) { ... }
public void processUser() { ... }
```
*Giải thích:* Hãy giữ `Optional` ở ranh giới (boundaries) của ứng dụng, cụ thể là kiểu trả về của các hàm truy vấn dữ liệu.

#### 4. Các kỹ thuật thay thế (Hình 5)
**Lý thuyết:** Đôi khi Optional là không cần thiết. Dưới đây là 3 cách thay thế.

**Ví dụ code:**
```java
public class Alternatives {
    
    // 1. Null value & Null checks (Cách truyền thống)
    public String getTraditional(String value) {
        if (value == null) return "Unknown";
        return value;
    }

    // 2. Throwing exception (Ném lỗi nếu thiếu - Fail Fast)
    // Dùng khi giá trị bắt buộc phải có, nếu không có là lỗi logic
    public String getRequired(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Giá trị không được để trống!");
        }
        return value;
    }

    // 3. Null Object Pattern (Trả về đối tượng rỗng thay vì null)
    // Giả sử có interface Animal
    interface Animal { void makeSound(); }
    static class Dog implements Animal {
        public void makeSound() { System.out.println("Gâu gâu"); }
    }
    // Đối tượng rỗng: Không làm gì cả nhưng an toàn để gọi hàm
    static class NullAnimal implements Animal {
        public void makeSound() { /* Không làm gì cả */ }
    }

    public Animal getAnimal(String type) {
        if ("dog".equals(type)) return new Dog();
        return new NullAnimal(); // Thay vì return null;
    }
}
```
*Giải thích:* Nếu việc thiếu giá trị là một lỗi nghiêm trọng, hãy ném Exception (cách 2). Nếu bạn muốn tránh kiểm tra null ở mọi nơi, hãy dùng Null Object Pattern (cách 3).

#### 5. Một số API quan trọng của Optional (Tips & Tricks - Hình 1)
**Lý thuyết:** Cần phân biệt rõ `orElse` và `orElseGet`, cũng như cách dùng `map` và `flatMap`.

**Ví dụ code:**
```java
public class OptionalApi {
    public static void main(String[] args) {
        Optional<String> optionalName = Optional.ofNullable(null);

        // 1. orElse vs orElseGet
        // orElse: Luôn luôn tính toán giá trị dự phòng (kể cả khi có giá trị)
        String name1 = optionalName.orElse(heavyComputation()); 
        
        // orElseGet: Chỉ tính toán khi Optional rỗng (Tối ưu hiệu năng hơn)
        String name2 = optionalName.orElseGet(() -> heavyComputation());

        // 2. orElseThrow: Ném lỗi nếu rỗng (Java 10+)
        try {
            String name3 = optionalName.orElseThrow(() -> new RuntimeException("Không có tên!"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        // 3. map: Biến đổi giá trị bên trong nếu nó tồn tại
        Optional<String> upperName = Optional.of("hello").map(String::toUpperCase);
        System.out.println(upperName.get()); // HELLO
    }

    static String heavyComputation() {
        System.out.println("Đang tính toán nặng...");
        return "Default";
    }
}
```
*Giải thích:* 
*   Dùng `orElseGet` thay vì `orElse` nếu giá trị dự phòng của bạn được tạo ra bằng một hàm tốn tài nguyên (như truy vấn DB, tính toán nặng). Vì `orElse` luôn chạy hàm đó bất kể Optional có rỗng hay không.
*   `orElseThrow` rất hữu ích khi bạn muốn "văng lỗi" ngay lập tức nếu không có dữ liệu. 

Hy vọng với lời giải thích chi tiết và các ví dụ trên, bạn đã nắm vững toàn bộ nội dung về `Optional` trong khóa học này! Nếu cần giải thích thêm phần nào, bạn cứ hỏi nhé.

Chào bạn, dựa trên 3 file code bạn cung cấp, đây là một bộ ví dụ cực kỳ đầy đủ và thực tế để minh họa cho lý thuyết về `Optional` mà chúng ta vừa tìm hiểu. 

Tôi sẽ chia phần giải thích thành 2 phần chính: 
1. **File `OptionalDemo.java`**: "Từ điển" các hàm của Optional.
2. **Cặp file `ProblemStatement.java` và `Solution.java`**: Bài toán thực tế (Cách cũ vs Cách dùng Optional).

---

### PHẦN 1: GIẢI THÍCH CHI TIẾT `OptionalDemo.java`

File này chứa các hàm `static` minh họa từng API của Optional. Hãy đi từng hàm một:

#### 1. `createOptionalExample()` - Cách khởi tạo
```java
Optional<String> emptyOptional = Optional.empty(); // Tạo hộp rỗng
String string = "Learn IT University";
Optional<String> optional = Optional.of(string); // Tạo hộp có giá trị (ném lỗi nếu string null)
string = null;
Optional<String> opt = Optional.ofNullable(string); // Tạo hộp an toàn (nhận null)
```
*   **Lý thuyết:** Không bao giờ dùng `Optional.of(null)`. Hãy dùng `Optional.ofNullable()` khi bạn không chắc chắn giá trị truyền vào có bị null hay không.

#### 2. `valueRelatedMethodsExample()` - Kiểm tra trạng thái
```java
Optional<String> optional = Optional.of("Learn IT University");
System.out.println("isPresent(): " + optional.isPresent()); // true
System.out.println("isEmpty():" + optional.isEmpty()); // false
```
*   **Lý thuyết:** `isPresent()` tương đương với việc kiểm tra `!= null`. `isEmpty()` (từ Java 11) tương đương với `== null`.

#### 3. `conditionalActionsExample()` - Thực thi có điều kiện
```java
Optional<String> optional = Optional.of("Learn IT University");
optional.ifPresent(string -> System.out.println("ifPresent(): " + string));

optional = Optional.empty();
optional.ifPresentOrElse(
    string -> System.out.println(string), // Nếu có giá trị thì chạy cái này
    () -> System.out.println("ifPresentOrElse(): No value found") // Nếu rỗng thì chạy cái này
);
```
*   **Lý thuyết:** `ifPresent` giúp code gọn hơn `if (optional.isPresent())`. `ifPresentOrElse` (Java 9+) là sự kết hợp của `if-else` truyền thống.

#### 4. `getExample()` - Lấy giá trị (Nguy hiểm)
```java
Optional<String> optional = Optional.of("Learn IT University");
System.out.println("get(): " + optional.get()); // Lấy ra chữ "Learn IT University"

optional = Optional.empty();
try {
    optional.get(); // NÉM LỖI NoSuchElementException vì hộp rỗng
} catch (NoSuchElementException e) {
    System.out.println("get(): no element found");
}
```
*   **Lý thuyết:** Hàm `get()` được xem là "anti-pattern" (phản mẫu) trong Java hiện đại. Nếu hộp rỗng mà bạn gọi `get()`, chương trình sẽ sập ngay lập tức (giống hệt lỗi `NullPointerException`). Khuyên dùng: Thay vì dùng `get()`, hãy dùng `orElse()`, `orElseThrow()`, hoặc `ifPresent()`.

#### 5. `orElseExample()` - Giá trị dự phòng (Cực kỳ quan trọng)
```java
Optional<String> optional = Optional.empty();

// orElse: LUÔN LUÔN gọi hàm getDefaultString() dù hộp có rỗng hay không
System.out.println("orElse(): " + optional.orElse(OptionalDemo.getDefaultString())); 

// orElseGet: CHỈ gọi hàm getDefaultString() khi hộp thực sự rỗng (Lazy evaluation)
System.out.println("orElseGet(): " + optional.orElseGet(OptionalDemo::getDefaultString));

try {
    optional.orElseThrow(); // Ném NoSuchElementException
    optional.orElseThrow(() -> new RuntimeException()); // Ném lỗi tùy chỉnh
} catch (Exception e) { }
```
*   **Lý thuyết:** 
    *   Nếu hàm dự phòng của bạn tốn tài nguyên (ví dụ: gọi API, truy vấn DB), **tuyệt đối không dùng `orElse`**, hãy dùng `orElseGet`. Vì `orElse` sẽ tính toán giá trị đó ngay cả khi Optional đã có dữ liệu (gây lãng phí).
    *   `orElseThrow()` dùng khi thiếu dữ liệu là một lỗi nghiêm trọng (Fail-fast).

#### 6. `filterExample()` - Lọc
```java
Optional<String> optional = Optional.of("Andrey Pyatakha");
// Giữ lại nếu tên chứa "Andrey", ngược lại trả về chuỗi mặc định
optional.filter(fullName -> fullName.contains("Andrey")).orElse("No required name found");
```
*   **Lý thuyết:** `filter` nhận vào một điều kiện (Predicate). Nếu điều kiện đúng, trả về Optional đó. Nếu sai, trả về `Optional.empty()`.

#### 7. `mapExample()` - Biến đổi dữ liệu
```java
Optional<User> optional = Optional.of(new User("Yegor", 1990));
Integer yearOfBirth = optional.map(user -> user.getYearOfBirth()) // Lấy năm sinh từ User
        .filter(year -> year >= 1960)
        .filter(year -> year <= 2000)
        .orElse(null);
```
*   **Lý thuyết:** `map()` giúp bạn "lấy ruột" của hộp ra, xử lý nó (ví dụ: lấy `yearOfBirth`), rồi bỏ lại vào một chiếc hộp mới. Bạn có thể "móc nối" (chain) liên tục `map` và `filter` cực kỳ mượt mà.

#### 8. `flatMapExample()` - Làm phẳng (Xử lý lồng nhau)
```java
Optional<User> personOptional = Optional.of(person);

// CÁCH SAI (Dùng map): Vì User::getName đã trả về Optional rồi
// Nên kết quả bị lồng: Optional<Optional<String>>
Optional<Optional<String>> nameOptionalWrapped = personOptional.map(User::getName);

// CÁCH ĐÚNG (Dùng flatMap): Tự động làm phẳng thành Optional<String>
String name = personOptional.flatMap(User::getName).orElse("");
```
*   **Lý thuyết:** Nếu hàm bên trong `map` **đã trả về `Optional`**, bạn phải dùng `flatMap`. Nó sẽ gỡ bỏ lớp vỏ bọc bên ngoài, giúp bạn không phải làm việc với `Optional<Optional<T>>` (rất khó đọc code).

---

### PHẦN 2: BÀI TOÁN THỰC TẾ (Problem vs Solution)

Đây là ví dụ tuyệt vời nhất để thấy sức mạnh của Optional (Tương ứng với Hình 3 - Sai lầm tỷ đô).

#### File `ProblemStatement.java` (Cách viết truyền thống - Cổ điển)
```java
var computer = new Computer();
var usbDrive = computer.getUsbDrive();

if (usbDrive != null) {
    var writeProtectSwitch = usbDrive.getWriteProtectSwitch();
    if (writeProtectSwitch != null) {
        // some code for write protect switch
    }
}
```
*   **Vấn đề:** Bạn thấy đấy, đây gọi là "Kim tự tháp tử thần" (Pyramid of Doom). Bạn phải check null từng lớp một. Nếu `Computer` có 5 thuộc tính lồng nhau, bạn sẽ có 5 câu lệnh `if` lồng nhau. Chỉ cần quên 1 câu `if`, lỗi NPE sẽ nổ tung.

#### File `Solution.java` (Cách viết dùng Optional)
```java
public static void main(String[] args) {
    var computer = new Computer();
    // Sử dụng ifPresentOrElse thay cho if-else
    computer.getUsbDrive().ifPresentOrElse(
        drive -> { // Nếu có USB
            drive.getWriteProtectSwitch()
                 .ifPresent(switcher -> System.out.println(switcher.isSwitchedOn()));
        }, 
        () -> System.out.println("No USB drive found") // Nếu không có USB
    );
}

// Các lớp được thiết kế lại để trả về Optional
private static class Computer {
    private UsbDrive usbDrive;
    public Optional<UsbDrive> getUsbDrive() {
        return Optional.ofNullable(this.usbDrive); // Bọc lại
    }
}
private static class UsbDrive {
    private WriteProtectSwitch writeProtectSwitch;
    public Optional<WriteProtectSwitch> getWriteProtectSwitch() {
        return Optional.ofNullable(this.writeProtectSwitch);
    }
}
```
*   **Giải thích ưu điểm:** 
    *   Thay vì kiểm tra `usbDrive != null`, ta dùng `ifPresentOrElse`. Code đọc như một câu tiếng Anh: "Lấy USB, nếu có thì (drive) -> lấy công tắc nguồn, nếu công tắc có (switcher) -> in ra trạng thái. Nếu không có USB -> in ra lỗi".
    *   Không còn khối `if` lồng nhau. Code phẳng hơn, an toàn tuyệt đối 100% không bao giờ bị `NullPointerException` ở các dòng này.

### 💡 TỔNG KẾT LỜI KHUYÊN
1. Dùng `Optional` cho **kiểu trả về (return type)** của các hàm (như hàm `getUsbDrive()` ở trên).
2. Hạn chế dùng `get()`. Hãy dùng `orElse`, `orElseGet`, `orElseThrow`, `ifPresent`.
3. Nếu hàm trả về Optional, hãy dùng `flatMap` thay vì `map` để tránh bị lồng `Optional<Optional<T>>`.
4. Không dùng Optional cho các trường (fields) trong class hay tham số truyền vào phương thức (method parameters).