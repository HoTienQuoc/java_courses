Dựa vào các hình ảnh bạn cung cấp (từ khóa học Udemy và tài liệu Java SE 27), tôi sẽ tổng hợp và hệ thống lại toàn bộ kiến thức về **Lập trình Hàm (Functional Programming - FP) trong Java**. 

Bài viết này sẽ đi đúng theo lộ trình (Agenda) trong hình 2 của bạn: từ khái niệm cốt lõi, so sánh mô hình, cho đến các công cụ thực thi trong Java (Lambda, Method Reference, Stream API, và package `java.util.function`).

---

## 1. Lập trình hàm là gì? (What is Functional Programming?)

### Lý thuyết (Từ Hình 2 & 3)
Lập trình hàm là một **paradigm (mô hình)** mà ở đó chương trình được xây dựng bằng cách **áp dụng và kết hợp các hàm (functions)**. Khác với OOP (lấy đối tượng làm trung tâm), FP coi **hàm là công dân hạng nhất (First-class function)**.

**5 Khái niệm cốt lõi (Main Concepts):**
1. **First-class function:** Hàm có thể được gán cho biến, truyền làm tham số, hoặc trả về từ hàm khác.
2. **Pure functions (Hàm thuần khiết):** Hàm không thay đổi trạng thái bên ngoài (no side-effects) và với cùng một input luôn trả về cùng một output.
3. **Recursion (Đệ quy):** Kỹ thuật hàm tự gọi lại chính nó (dùng thay cho vòng lặp `for/while` trong FP thuần túy).
4. **Referential Transparency (Tính tham chiếu trong suốt):** Một biểu thức có thể được thay thế bằng kết quả của nó mà không làm thay đổi hành vi của chương trình.
5. **Lazy Evaluation (Đánh giá lười):** Chỉ tính toán khi thực sự cần thiết.

---

## 2. Ưu và Nhược điểm (Advantages & Disadvantages) (Từ Hình 4)

| Ưu điểm (Advantages) | Nhược điểm (Disadvantages) |
| :--- | :--- |
| **Simplicity (Đơn giản):** Code ngắn gọn, dễ hiểu (declarative). | **Immutable values impact performance:** Việc tạo ra quá nhiều đối tượng bất biến (immutable) có thể ảnh hưởng đến bộ nhớ và hiệu năng. |
| **Suitable for concurrent programs:** Rất tốt cho lập trình đa luồng vì không có trạng thái chia sẻ (shared mutable state). | **Hard to read:** Đôi khi các hàm lồng nhau (nested lambdas) hoặc pipeline phức tạp rất khó đọc. |
| | **Hard to combine pure functions:** Việc kết hợp các pure functions trong Java (do không có cú pháp compose tự nhiên như Haskell) đôi khi gặp khó khăn. |

---

## 3. So sánh: Declarative VS Imperative (Từ Hình 5)

### Lý thuyết
*   **Imperative (Mệnh lệnh):** Bạn phải chỉ rõ máy tính **LÀM NHƯ THẾ NÀO** (How). Bạn tự quản lý vòng lặp, biến đếm, điều kiện.
*   **Declarative (Khai báo):** Bạn chỉ cần nói **LÀM CÁI GÌ** (What). Máy tính tự lo cách thực hiện.

### Ví dụ & Giải thích

```java
import java.util.List;

public class ImperativeVsDeclarative {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // 1. IMPERATIVE (Cách cũ)
        int sum1 = 0;
        for (int num : numbers) {
            if (num % 2 == 0) { // Tự kiểm tra số chẵn
                sum1 += num;      // Tự cộng dồn
            }
        }

        // 2. DECLARATIVE (Cách dùng Functional Programming)
        int sum2 = numbers.stream()
                .filter(n -> n % 2 == 0) // Nói: "Lọc số chẵn"
                .mapToInt(n -> n)        // Nói: "Chuyển sang int"
                .sum();                  // Nói: "Tính tổng"

        System.out.println(sum1 == sum2); // true (6)
    }
}
```
> 💡 **Giải thích lý thuyết:** Ở cách 1, bạn phải quản lý state (`sum1`), vòng lặp. Ở cách 2, bạn chỉ mô tả luồng dữ liệu (pipeline). Java Streams đảm nhận việc lặp. Đây là tư duy cốt lõi của Declarative.

---

## 4. Function VS Method (Từ Hình 6)

### Lý thuyết
*   **Method (Phương thức):** Là một khối code gắn liền với một **Object hoặc Class** (thuộc về OOP). Nó có thể truy cập vào trạng thái của object (`this`), có thể thay đổi state.
*   **Function (Hàm):** Là một khối code độc lập, không phụ thuộc vào ngữ cảnh (context) nào. Trong Java (vốn là ngôn ngữ OOP), chúng ta không có "hàm" thực sự thuần túy, mà chúng ta mô phỏng hàm bằng **Lambda Expression** và **Functional Interface**.

---

## 5. Functional Programming trong Java (Java 8+)

Để đưa FP vào Java, đội ngũ phát triển đã thêm 3 tính năng chính: **Functional Interface**, **Lambda Expression**, và **Method Reference**.

### 5.1. Functional Interface (Giao diện chức năng)
**Lý thuyết:** Là interface có **đúng 1 abstract method** (phương thức trừu tượng). Nó đóng vai trò là "khuôn mẫu" để Java có thể hiểu và gán các Lambda vào.

```java
@FunctionalInterface
interface MathOperation {
    int operate(int a, int b); // 1 abstract method duy nhất
}
```
> 💡 **Giải thích lý thuyết:** `@FunctionalInterface` không bắt buộc, nhưng nếu bạn cố tình thêm abstract method thứ 2, compiler sẽ báo lỗi. Nó giúp code an toàn và rõ ràng.

### 5.2. Lambda Expression
**Lý thuyết:** Lambda là cách viết tắt của Anonymous Class. Nó biểu diễn một **hành vi (behavior)**. Cú pháp: `(parameters) -> expression` hoặc `(parameters) -> { statements; }`.

```java
public class LambdaDemo {
    public static void main(String[] args) {
        // Cách cũ: Anonymous class
        MathOperation addOld = new MathOperation() {
            @Override
            public int operate(int a, int b) { return a + b; }
        };

        // Cách FP: Lambda
        MathOperation addNew = (a, b) -> a + b;
        MathOperation multiply = (a, b) -> a * b;

        System.out.println(addNew.operate(5, 3));      // 8
        System.out.println(multiply.operate(5, 3));    // 15
    }
}
```
> 💡 **Giải thích lý thuyết:** Lambda `(a, b) -> a + b` được Java ánh xạ vào `MathOperation`. Nhờ có Lambda, ta có thể truyền "hành vi" này như một tham số cho hàm khác (First-class function).

### 5.3. Method Reference (`::`)
**Lý thuyết:** Là cú pháp viết tắt của Lambda khi Lambda **chỉ gọi duy nhất một method có sẵn**. Giúp code gọn hơn.
Có 4 loại: `ClassName::staticMethod`, `object::instanceMethod`, `ClassName::instanceMethod` (unbound), `ClassName::new` (constructor).

```java
import java.util.function.Supplier;
import java.util.function.Function;

public class MethodRefDemo {
    public static void main(String[] args) {
        // Lambda thông thường
        Function<String, Integer> parse1 = s -> Integer.parseInt(s);
        
        // Method Reference (Static method)
        Function<String, Integer> parse2 = Integer::parseInt;

        // Lambda cho Constructor
        Supplier<String> create1 = () -> new String();
        // Method Reference cho Constructor
        Supplier<String> create2 = String::new;

        System.out.println(parse2.apply("123")); // 123
    }
}
```
> 💡 **Giải thích lý thuyết:** `Integer::parseInt` hoàn toàn tương đương với `s -> Integer.parseInt(s)`. Dùng Method Reference khi code đã quá rõ nghĩa, giúp tăng tính "Declarative".

---

## 6. Gói `java.util.function` (Từ Hình 1)

Trong Java, bạn không cần phải tự tạo ra các Functional Interface (như `MathOperation` ở trên) cho mọi trường hợp. Java 8 cung cấp sẵn package **`java.util.function`** chứa các interface phổ biến nhất. Dưới đây là 4 "hình mẫu" cơ bản nhất (Basic Shapes):

| Interface | Input | Output | Method | Ý nghĩa |
| :--- | :--- | :--- | :--- | :--- |
| **`Function<T, R>`** | 1 (T) | 1 (R) | `R apply(T t)` | Biến đổi dữ liệu (Map). |
| **`Consumer<T>`** | 1 (T) | Không (void)| `void accept(T t)` | Tiêu thụ dữ liệu (In ra, lưu DB). |
| **`Predicate<T>`** | 1 (T) | boolean | `boolean test(T t)` | Kiểm tra điều kiện (Filter). |
| **`Supplier<T>`** | Không | 1 (T) | `T get()` | Cung cấp dữ liệu. |

**Các biến thể (theo chuẩn đặt tên của Java - Hình 1):**
*   **`Bi` prefix:** Nhận 2 tham số (VD: `BiFunction<T, U, R>`, `BiConsumer<T, U>`).
*   **`Unary` / `Binary`:** Tham số và kết quả cùng kiểu (VD: `UnaryOperator<T>` là `Function<T, T>`, `BinaryOperator<T>` là `BiFunction<T, T, T>`).
*   **Primitive types:** Tránh boxing/unboxing (VD: `IntFunction<R>`, `ToIntFunction<T>`, `DoubleConsumer`).

```java
import java.util.function.*;

public class JavaUtilFunctionDemo {
    public static void main(String[] args) {
        // 1. Function: Nhận String, trả về Integer (độ dài)
        Function<String, Integer> lengthFn = String::length;
        System.out.println(lengthFn.apply("Hello")); // 5

        // 2. Consumer: Nhận String, in ra màn hình
        Consumer<String> printFn = System.out::println;
        printFn.accept("Functional Programming");

        // 3. Predicate: Kiểm tra chuỗi rỗng
        Predicate<String> isEmpty = String::isEmpty;
        System.out.println(isEmpty.test("")); // true

        // 4. Supplier: Cung cấp một số ngẫu nhiên
        Supplier<Double> random = Math::random;
        System.out.println(random.get()); // 0.xxx
    }
}
```
> 💡 **Giải thích lý thuyết:** Việc sử dụng các interface có sẵn này giúp code của bạn tương thích hoàn toàn với **Stream API** (như phương thức `.filter(Predicate)`, `.map(Function)`, `.forEach(Consumer)`).

---

## 7. Áp dụng tổng hợp: Stream API & Lazy Evaluation

**Lý thuyết:** Stream API là "trái tim" của FP trong Java. Kết hợp `java.util.function`, Lambda và Stream, ta có thể xử lý dữ liệu cực kỳ mạnh mẽ. Tính **Lazy Evaluation (Đánh giá lười)** có nghĩa là các phép toán trung gian (`filter`, `map`) chưa chạy cho đến khi gặp phép toán kết thúc (`collect`, `forEach`).

```java
import java.util.List;
import java.util.stream.Collectors;

public class StreamUltimateDemo {
    public static void main(String[] args) {
        List<String> names = List.of("An", "Binh", "Cuong", "Dung", "Em");

        List<String> result = names.stream()
                // Intermediate (Lazy): Chưa chạy ngay
                .filter(name -> {
                    System.out.println("Checking: " + name); // Sẽ in ra khi có Terminal
                    return name.length() > 2;
                })
                .map(String::toUpperCase)
                // Terminal (Eager): Kích hoạt toàn bộ pipeline
                .collect(Collectors.toList());

        System.out.println(result); 
    }
}
```
> 💡 **Giải thích lý thuyết:** 
> * Nếu không có `.collect(...)` ở cuối, đoạn code trên sẽ **không làm gì cả** (vì Lazy).
> * Nhờ tính năng này, Stream có thể tối ưu hóa: nếu bạn dùng `.findFirst()` thay vì `.collect()`, nó sẽ dừng vòng lặp ngay khi tìm thấy phần tử đầu tiên, không cần duyệt hết danh sách.

---
**Tóm lại:** Lập trình hàm trong Java không thay thế hoàn toàn OOP, mà nó là một bổ sung mạnh mẽ. Nó giúp code ngắn gọn hơn, dễ test hơn (nhờ pure functions), và an toàn hơn trong môi trường đa luồng. Việc nắm vững **`java.util.function`**, **Lambda**, và **Stream** là chìa khóa để viết code Java hiện đại.