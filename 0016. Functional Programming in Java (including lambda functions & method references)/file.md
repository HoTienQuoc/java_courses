# Functional Programming trong Java — Lý thuyết chi tiết kèm ví dụ

---

## 1. Lập trình hàm (Functional Programming) là gì?

### Lý thuyết

**Functional Programming (FP)** là một paradigm lập trình mà ở đó **hàm (function)** được coi là "công dân hạng nhất" (first-class citizen) — nghĩa là hàm có thể:

1. **Được gán cho biến**
2. **Được truyền làm tham số** cho hàm khác
3. **Được trả về** từ hàm khác

Các đặc điểm cốt lõi của FP:

| Đặc điểm | Ý nghĩa |
|---|---|
| **Pure function** | Hàm chỉ phụ thuộc vào input, không có side-effect (không thay đổi state bên ngoài) |
| **Immutability** | Dữ liệu không bị thay đổi sau khi tạo |
| **First-class & Higher-order functions** | Hàm là giá trị; hàm nhận/trả về hàm |
| **Declarative** | Mô tả "làm gì" thay vì "làm như thế nào" (như vòng lặp for) |

> 💡 **Trước Java 8**, Java thuần OOP — muốn truyền "hành vi" phải tạo anonymous class rất dài dòng. Từ **Java 8**, Java hỗ trợ FP qua **Lambda**, **Functional Interface**, **Method Reference** và **Stream API**.

---

## 2. Functional Interface — Nền tảng của Lambda

### Lý thuyết

**Functional Interface** là interface có **đúng một abstract method** (phương thức trừu tượng). Nó có thể có nhiều `default`/`static` method, nhưng chỉ **1 abstract method** thì mới dùng được với lambda.

- Annotation `@FunctionalInterface` là **tùy chọn** nhưng nên dùng — compiler sẽ báo lỗi nếu bạn vô tình thêm abstract method thứ 2.

### Ví dụ + giải thích

```java
// Định nghĩa functional interface
@FunctionalInterface
interface Calculator {
    int apply(int a, int b);   // CHỈ 1 abstract method → hợp lệ
    // int subtract(int a, int b);  // ❌ Nếu thêm dòng này, compiler báo lỗi
}

public class Demo1 {
    // Hàm nhận vào một "hành vi" (functional interface) như tham số
    // → Đây là higher-order function
    static int execute(int x, int y, Calculator op) {
        return op.apply(x, y);
    }

    public static void main(String[] args) {
        // Truyền lambda như tham số → FP style
        int sum = execute(3, 4, (a, b) -> a + b);       // 7
        int mul = execute(3, 4, (a, b) -> a * b);       // 12
        System.out.println(sum + " " + mul);
    }
}
```

**Giải thích lý thuyết bên cạnh ví dụ:**

- `Calculator` chỉ có **1 abstract method** → là functional interface → lambda `(a,b) -> a + b` có thể "hiện thực hóa" nó.
- `execute(...)` nhận `Calculator op` như tham số → **higher-order function** (hàm nhận hàm).
- Nhờ vậy, ta thay đổi **hành vi** (`+`, `*`) mà không cần viết nhiều class — đây là tinh thần **declarative**.

### Các functional interface có sẵn trong `java.util.function`

| Interface | Signature | Ý nghĩa |
|---|---|---|
| `Function<T,R>` | `R apply(T t)` | Nhận T, trả R (biến đổi) |
| `BiFunction<T,U,R>` | `R apply(T t, U u)` | Nhận 2 tham số |
| `Predicate<T>` | `boolean test(T t)` | Kiểm tra điều kiện |
| `Consumer<T>` | `void accept(T t)` | Tiêu thụ, không trả về |
| `Supplier<T>` | `T get()` | Cung cấp giá trị, không nhận vào |
| `UnaryOperator<T>` | `T apply(T t)` | Biến đổi cùng kiểu |
| `BinaryOperator<T>` | `T apply(T a, T b)` | Gộp 2 giá trị cùng kiểu |

```java
import java.util.function.*;

public class BuiltInDemo {
    public static void main(String[] args) {
        Function<Integer, Integer> square = x -> x * x;   // biến đổi
        Predicate<Integer> isEven       = x -> x % 2 == 0; // điều kiện
        Consumer<String> printer        = s -> System.out.println(s); // tiêu thụ
        Supplier<Double> random         = () -> Math.random(); // cung cấp
        UnaryOperator<Integer> inc      = x -> x + 1;
        BinaryOperator<Integer> add     = (a, b) -> a + b;

        System.out.println(square.apply(5));     // 25
        System.out.println(isEven.test(10));     // true
        printer.accept("Hello FP");              // in ra
        System.out.println(random.get());        // số ngẫu nhiên
        System.out.println(inc.apply(9));        // 10
        System.out.println(add.apply(2, 3));     // 5
    }
}
```

> 💡 `Predicate<T>` có các method mặc định mạnh: `and()`, `or()`, `negate()` — dùng để **kết hợp điều kiện** theo kiểu hàm.

---

## 3. Lambda Expression

### Lý thuyết

**Lambda** là một **biểu thức ẩn danh** (anonymous) — nó **hiện thực hóa** một functional interface bằng cú pháp ngắn gọn. Lambda **không phải** là hàm độc lập như trong ngôn ngữ FP thuần — nó luôn gắn với một functional interface (compiler tự suy ra kiểu).

### Cú pháp

```
(parameters) -> expression
(parameters) -> { statements; }
```

Các dạng rút gọn:

```java
// Đầy đủ
(int a, int b) -> { return a + b; }

// Bỏ kiểu (compiler tự suy)
(a, b) -> { return a + b; }

// Chỉ 1 biểu thức → bỏ return & {}
(a, b) -> a + b

// 1 tham số → bỏ luôn ()
x -> x * 2

// Không tham số → vẫn giữ ()
() -> System.out.println("Hi")
```

### Ví dụ + giải thích

```java
import java.util.*;

public class LambdaDemo {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("An", "Binh", "Cuong"));

        // ❌ Cách cũ (Java 7): anonymous class — dài dòng, "noise"
        names.sort(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();
            }
        });

        // ✅ Cách mới: lambda — cùng hành vi, ngắn gọn, tập trung vào logic
        names.sort((a, b) -> a.length() - b.length());

        // Thậm chí gọn hơn nhờ Comparator.comparingInt (kết hợp method reference)
        names.sort(Comparator.comparingInt(String::length));

        System.out.println(names);  // [An, Binh, Cuong] (sắp theo độ dài)
    }
}
```

**Giải thích lý thuyết bên cạnh ví dụ:**

- `Comparator<T>` là functional interface (1 abstract method `compare`) → lambda `(a,b) -> ...` thay thế anonymous class.
- Lambda giúp **loại bỏ boilerplate**: `new Comparator<String>() { @Override public int compare ... }` → chỉ còn **phần logic thực sự** (`a.length() - b.length()`). Đây chính là **declarative**.
- `Comparator.comparingInt(String::length)` là **method reference** — ta sẽ học ngay sau đây.

### Biến capture & "effectively final"

```java
public class CaptureDemo {
    public static void main(String[] args) {
        int factor = 10;  // biến local → lambda "bắt" nó

        // Lambda có thể đọc biến local bên ngoài, NHƯNG biến đó phải
        // "effectively final" (không bị gán lại sau khi khởi tạo)
        java.util.function.IntUnaryOperator mult = x -> x * factor;
        System.out.println(mult.applyAsInt(5)); // 50

        // factor = 20;  // ❌ Nếu bỏ comment → compile error
    }
}
```

> 💡 **Effectively final**: biến local được dùng trong lambda không được **gán lại giá trị** sau khi khởi tạo. Lý do: lambda có thể chạy **sau** khi method kết thúc (closure), nên Java "chụp" (capture) giá trị tại thời điểm đó.

---

## 4. Method Reference (`::`)

### Lý thuyết

**Method Reference** là "cú pháp viết tắt" của lambda **khi lambda chỉ gọi đúng một method có sẵn**. Nó giúp code **ngắn và rõ nghĩa** hơn lambda. Có **4 loại**:

| Loại | Cú pháp | Lambda tương đương |
|---|---|---|
| 1. Static method | `ClassName::staticMethod` | `x -> ClassName.staticMethod(x)` |
| 2. Instance method của object cụ thể | `obj::instanceMethod` | `x -> obj.instanceMethod(x)` |
| 3. Instance method của object tùy ý (unbound) | `ClassName::instanceMethod` | `(obj, x) -> obj.instanceMethod(x)` |
| 4. Constructor | `ClassName::new` | `x -> new ClassName(x)` |

### Ví dụ từng loại + giải thích

```java
import java.util.*;
import java.util.function.*;

class Person {
    String name;
    Person(String name) { this.name = name; }
    public String getName() { return name; }
    public String introduce() { return "I'm " + name; }
}

public class MethodRefDemo {
    public static void main(String[] args) {
        // (1) STATIC: Math.abs → x -> Math.abs(x)
        Function<Integer, Integer> abs = Math::abs;
        System.out.println(abs.apply(-5));   // 5

        // (2) INSTANCE của object cụ thể: printer::print → s -> printer.print(s)
        List<String> list = List.of("a", "b");
        list.forEach(System.out::println);   // System.out là object cụ thể

        // (3) INSTANCE của object tùy ý (unbound):
        //     Person::getName → (Person p) -> p.getName()
        List<Person> people = List.of(new Person("An"), new Person("Binh"));
        people.stream().map(Person::getName).forEach(System.out::println);
        // → "An", "Binh"

        // (3b) unbound với 2 tham số: Person::introduce → p -> p.introduce()
        //      Dùng khi cần biến đổi object thành giá trị của chính nó
        Function<Person, String> intro = Person::introduce;
        System.out.println(intro.apply(people.get(0)));  // "I'm An"

        // (4) CONSTRUCTOR: Person::new → s -> new Person(s)
        Function<String, Person> factory = Person::new;
        Person p = factory.apply("Cuong");
        System.out.println(p.getName());     // "Cuong"

        // Kết hợp: tạo List<Person> từ List<String>
        List<String> names = List.of("A", "B");
        List<Person> people2 = names.stream()
                                    .map(Person::new)   // constructor ref
                                    .toList();
        System.out.println(people2.size());  // 2
    }
}
```

**Giải thích lý thuyết bên cạnh ví dụ:**

- **Loại (2)** `System.out::println` hoạt động vì `System.out` là **một object cụ thể** — mỗi lần gọi là `System.out.println(x)` với `x` là phần tử trong list.
- **Loại (3)** `Person::getName` là "unbound" — **object chưa xác định**, nó sẽ là **tham số đầu tiên** của lambda. Compiler suy ra: cần 1 tham số `Person`, gọi `getName()` trên đó.
- **Loại (4)** `Person::new` map với hàm `(String s) -> new Person(s)` — compiler khớp kiểu tham số với constructor.
- **Nguyên tắc chung**: Method reference chỉ dùng khi lambda **chỉ gọi đúng 1 method** — nếu có thêm logic (ví dụ `x -> x.getName() + "!")` thì phải dùng lambda.

---

## 5. Stream API — "Trái tim" của FP trong Java

### Lý thuyết

**Stream** là một **chuỗi các phần tử** cho phép xử lý **theo kiểu khai báo** (declarative). Stream **không lưu dữ liệu**, nó chỉ là **pipeline** gồm:

1. **Source** — nguồn dữ liệu (collection, array, file, ...)
2. **Intermediate operations** (trung gian, lazy): `filter`, `map`, `sorted`, `distinct`, `limit`, `flatMap`, ... → trả về **Stream mới**
3. **Terminal operation** (kết thúc, eager): `collect`, `forEach`, `reduce`, `count`, `anyMatch`, ... → kích hoạt toàn bộ pipeline và trả kết quả

> 💡 **Lazy evaluation**: Các intermediate operation **chưa chạy** cho đến khi gặp terminal. Đây là điểm mạnh giúp **tối ưu** (ví dụ `findFirst()` có thể dừng sớm).

### Ví dụ + giải thích

```java
import java.util.*;
import java.util.stream.*;

public class StreamDemo {
    record Employee(String name, String dept, double salary) {}

    public static void main(String[] args) {
        List<Employee> employees = List.of(
            new Employee("An",   "IT",      1000),
            new Employee("Binh", "IT",      1500),
            new Employee("Cuong","HR",       800),
            new Employee("Dung", "Finance", 2000),
            new Employee("Em",   "IT",      1200)
        );

        // Bài toán: Tính tổng lương phòng IT, sắp xếp tăng dần theo lương,
        //          rồi lấy danh sách tên.
        List<String> result = employees.stream()                       // source
            .filter(e -> e.dept().equals("IT"))                        // intermediate
            .sorted(Comparator.comparingDouble(Employee::salary))      // intermediate + method ref
            .map(Employee::name)                                       // intermediate + method ref
            .collect(Collectors.toList());                             // terminal

        System.out.println(result);   // [An, Em, Binh]

        // Tính tổng lương IT bằng reduce
        double totalItSalary = employees.stream()
            .filter(e -> e.dept().equals("IT"))
            .mapToDouble(Employee::salary)
            .sum();
        System.out.println("Tổng lương IT: " + totalItSalary);  // 3700.0

        // Nhóm theo phòng ban → Map<String, List<Employee>>
        Map<String, List<Employee>> byDept = employees.stream()
            .collect(Collectors.groupingBy(Employee::dept));
        System.out.println(byDept.keySet());  // [IT, HR, Finance]

        // Đếm theo phòng ban
        Map<String, Long> countByDept = employees.stream()
            .collect(Collectors.groupingBy(Employee::dept, Collectors.counting()));
        System.out.println(countByDept);      // {HR=1, Finance=1, IT=3}
    }
}
```

**Giải thích lý thuyết bên cạnh ví dụ:**

- `stream()` là **source** — chuyển collection thành stream.
- `filter(e -> ...)` và `map(Employee::name)` là **intermediate** — chúng **không chạy ngay**; chỉ trả về Stream mới mô tả phép biến đổi.
- `sorted(...)` cũng intermediate — nhưng vì cần sắp xếp toàn bộ, nó **buffering** (giữ dữ liệu lại) rồi mới chạy khi có terminal.
- `collect(Collectors.toList())` là **terminal** — đây mới là chỗ pipeline **thực sự chạy** và duyệt từng phần tử.
- `Collectors` là "công cụ" cho terminal `collect`: `toList`, `toSet`, `groupingBy`, `counting`, `summingDouble`, `joining`, `partitioningBy`, ...
- `mapToDouble(...).sum()` là primitive stream — **tránh boxing** (`double` thay vì `Double`) → hiệu năng tốt hơn.

### Pure function & immutability trong Stream

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);

// Mỗi intermediate tạo ra STREAM MỚI, không sửa `nums`
List<Integer> doubled = nums.stream()
    .map(x -> x * 2)       // pure: không đọc/ghi biến ngoài
    .toList();

System.out.println(nums);      // [1,2,3,4,5] — KHÔNG đổi
System.out.println(doubled);   // [2,4,6,8,10]

// ⚠️ Không nên dùng side-effect trong stream:
// .map(x -> { counter++; return x; })   ← anti-pattern
```

---

## 6. Ví dụ tổng hợp — "Bài toán thực tế"

Cho danh sách `Order` (id, customer, amount, status). Tính:
- Tổng tiền các order `COMPLETED`
- Top 3 khách hàng chi tiêu nhiều nhất
- Map từ status → danh sách id

```java
import java.util.*;
import java.util.stream.*;

public class OrderDemo {
    enum Status { NEW, COMPLETED, CANCELLED }
    record Order(int id, String customer, double amount, Status status) {}

    public static void main(String[] args) {
        List<Order> orders = List.of(
            new Order(1, "An",   100, Status.COMPLETED),
            new Order(2, "An",   250, Status.COMPLETED),
            new Order(3, "Binh", 400, Status.CANCELLED),
            new Order(4, "Binh", 150, Status.COMPLETED),
            new Order(5, "Cuong",300, Status.COMPLETED),
            new Order(6, "Cuong",500, Status.COMPLETED),
            new Order(7, "Dung", 200, Status.NEW)
        );

        // (1) Tổng tiền order COMPLETED
        double total = orders.stream()
            .filter(o -> o.status() == Status.COMPLETED)
            .mapToDouble(Order::amount)
            .sum();
        System.out.println("Total COMPLETED: " + total);   // 1300.0

        // (2) Top 3 khách hàng chi tiêu (chỉ COMPLETED)
        Map<String, Double> spend = orders.stream()
            .filter(o -> o.status() == Status.COMPLETED)
            .collect(Collectors.groupingBy(
                Order::customer,
                Collectors.summingDouble(Order::amount)
            ));

        List<String> top3 = spend.entrySet().stream()
            .sorted(Map.Entry.<String,Double>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)          // method reference loại 3
            .toList();
        System.out.println("Top 3: " + top3);   // [Cuong, An, Binh]

        // (3) Map status → list id
        Map<Status, List<Integer>> byStatus = orders.stream()
            .collect(Collectors.groupingBy(
                Order::status,
                Collectors.mapping(Order::id, Collectors.toList())
            ));
        System.out.println(byStatus);
        // {NEW=[7], COMPLETED=[1,2,4,5,6], CANCELLED=[3]}
    }
}
```

**Giải thích lý thuyết:**

- Bài toán **không dùng vòng lặp for** — ta **mô tả** pipeline ("lọc → gom nhóm → sắp xếp → lấy top") thay vì chỉ dẫn từng bước.
- `Collectors.groupingBy(Order::customer, Collectors.summingDouble(Order::amount))` là **downstream collector**: gom nhóm theo customer, rồi **với mỗi nhóm** tính tổng lương.
- `Collectors.mapping(Order::id, Collectors.toList())` gom **id** thay vì cả object — minh họa việc **kết hợp method reference** trong collector.
- `.sorted(...).limit(3)` kết hợp **sắp xếp + giới hạn** → lazy: chỉ lấy 3 phần tử đầu tiên sau khi sắp.

---

## 7. Tóm tắt — Bảng tra nhanh

| Khái niệm | Vai trò |
|---|---|
| **Functional Interface** | Hợp đồng (interface) có 1 abstract method → lambda/method ref thay thế |
| **Lambda** | Viết tắt anonymous class; biểu thức ẩn danh gắn với functional interface |
| **Method Reference** | Viết tắt lambda khi chỉ gọi 1 method có sẵn (`::`) |
| **Stream** | Pipeline xử lý dữ liệu khai báo: source → intermediate (lazy) → terminal |
| **Pure function / Immutability** | Điều kiện để stream an toàn, dễ song song hóa (`parallelStream()`) |

> 🔑 **Tư duy cốt lõi**: Chuyển từ **"how"** (viết vòng lặp) sang **"what"** (mô tả phép biến đổi). Ví dụ: `filter`, `map`, `reduce`, `collect` — đó chính là ngôn ngữ của FP trong Java.