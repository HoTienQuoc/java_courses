# So sánh Primitive Types và Reference Types trong Java

> Ghi chú: "Conference type" chắc là gõ nhầm của **Reference type** (kiểu tham chiếu). Mình sẽ so sánh **primitive (nguyên thủy)** với **reference (tham chiếu)**.

---

## 1. Java có bao nhiêu kiểu? Nhìn tổng quan

```
Java Types
├── Primitive (8 kiểu, do ngôn ngữ định nghĩa sẵn)
│   ├── Số nguyên: byte, short, int, long, char
│   ├── Số thực:   float, double
│   └── Logic:     boolean
│
└── Reference (vô số, do lập trình viên/JDK tạo ra)
    ├── Class:     String, BigDecimal, Integer, Person...
    ├── Interface: List, Runnable...
    ├── Array:     int[], String[]...
    ├── Enum
    └── Record (Java 14+)
```

---

## 2. Bảng so sánh chi tiết

| Tiêu chí | **Primitive** | **Reference** |
|---|---|---|
| **Số lượng** | 8 kiểu cố định | Không giới hạn |
| **Lưu ở đâu** | Biến chứa **giá trị trực tiếp** (stack nếu là local, trong object nếu là field) | Biến chứa **địa chỉ** (reference) trỏ tới object nằm ở **heap** |
| **Kích thước** | Cố định (`int` = 4 byte, `long` = 8 byte...) | Object trên heap có kích thước tùy ý; biến tham chiếu thường 4–8 byte (tùy JVM) |
| **Giá trị mặc định** (field) | `0`, `0.0`, `false`, `'\u0000'` | `null` |
| **Giá trị mặc định** (local var) | ❌ **Bắt buộc khởi tạo** trước khi dùng, nếu không → compile error | Cũng bắt buộc khởi tạo (có thể gán `null`) |
| **Có thể `null`?** | ❌ Không | ✅ Có → nguy cơ `NullPointerException` |
| **Có method?** | ❌ Không | ✅ Có (gọi được `.equals()`, `.toString()`...) |
| **Kế thừa** | ❌ Không | ✅ Có (trừ `final` class, record, enum) |
| **So sánh `==`** | So sánh **giá trị** | So sánh **địa chỉ** (có trỏ cùng object hay không) |
| **Truyền vào method** | **Pass by value** — truyền bản sao giá trị | **Pass by value of reference** — truyền bản sao địa chỉ |
| **Hiệu năng** | ⚡ Nhanh, không cấp phát heap | 🐢 Chậm hơn (cấp phát heap, GC) |
| **Generic** | ❌ Không dùng được (`List<int>` sai) | ✅ Dùng được (`List<Integer>` đúng) |
| **Wrapper class** | Có lớp bao tương ứng: `Integer`, `Long`, `Double`, `Boolean`... | — |

---

## 3. Giải thích từng điểm khác biệt cốt lõi

### 3.1. Bộ nhớ: **giá trị trực tiếp** vs **địa chỉ tới object**

```java
int a = 10;              // biến a chứa số 10
String s = "hello";      // biến s chứa địa chỉ, ví dụ 0x1A2B → object "hello" ở heap
```

Minh họa:

```
Stack                          Heap
┌───────────────┐             ┌──────────────────┐
│ a = 10        │             │                  │
├───────────────┤             │  "hello" @0x1A2B │◄────┐
│ s = 0x1A2B ───┼─────────────┼──────────────────┘     │
└───────────────┘
```

Với primitive, stack chứa **toàn bộ dữ liệu**. Với reference, stack chỉ chứa **con trỏ**, dữ liệu thật nằm trên heap.

---

### 3.2. Gán biến — điểm khác nhau **quan trọng nhất**

**Primitive → copy giá trị độc lập:**

```java
int x = 10;
int y = x;      // copy giá trị 10 sang y
y = 20;
System.out.println(x); // 10  ← x KHÔNG đổi
```

**Reference → copy địa chỉ, cùng trỏ tới một object:**

```java
int[] arr1 = {1, 2, 3};
int[] arr2 = arr1;         // arr2 trỏ tới CÙNG mảng
arr2[0] = 999;
System.out.println(arr1[0]); // 999  ← arr1 cũng đổi theo!
```

```
arr1 ──┐
       ├──► [999, 2, 3]
arr2 ──┘
```

Đây là nguồn gốc của rất nhiều bug "sửa cái này tự nhiên cái kia đổi".

---

### 3.3. So sánh `==`

```java
int a = 5, b = 5;
System.out.println(a == b);          // true   (so sánh giá trị)

String s1 = new String("hi");
String s2 = new String("hi");
System.out.println(s1 == s2);        // false  (khác object, khác địa chỉ!)
System.out.println(s1.equals(s2));   // true   (so sánh nội dung)
```

> Quy tắc: với reference, **`==` so sánh địa chỉ**, muốn so sánh nội dung phải dùng `.equals()`.  
> Ngoại lệ: string literal được "intern" nên `"hi" == "hi"` có thể là `true`, nhưng đừng dựa vào đó.

---

### 3.4. Truyền vào method

Cả hai đều **pass by value** — nhưng giá trị được copy khác nhau.

```java
void changePrimitive(int n) { n = 999; }

void changeArray(int[] arr) { arr[0] = 999; }
void reassignArray(int[] arr) { arr = new int[]{7,8,9}; } // không ảnh hưởng bên ngoài
```

```java
int x = 10;
changePrimitive(x);
System.out.println(x);        // 10  (không đổi — bản sao của 10)

int[] a = {1,2,3};
changeArray(a);
System.out.println(a[0]);     // 999 (đổi được — vì cùng trỏ tới 1 mảng)

reassignArray(a);
System.out.println(a[0]);     // 999 (KHÔNG đổi — chỉ đổi con trỏ local)
```

👉 **Điểm tinh tế:** Java **không có pass by reference**. Nó luôn pass by value; với reference thì "value" là **bản sao địa chỉ**. Vì bản sao trỏ cùng object nên ta sửa được nội dung object, nhưng không đổi được biến gốc trỏ đi chỗ khác.

---

### 3.5. `null` và `NullPointerException`

```java
int x = null;         // ❌ compile error
String s = null;      // ✅
s.length();           // 💥 NullPointerException
```

Primitive **không thể** null → an toàn hơn về mặt này.  
Đây là lý do **Optional** hoặc **primitive-first** design đôi khi được ưa chuộng.

---

### 3.6. Wrapper class — cầu nối giữa hai thế giới

Java cung cấp **wrapper** để biến primitive thành object:

| Primitive | Wrapper |
|---|---|
| `byte` | `Byte` |
| `short` | `Short` |
| `int` | `Integer` |
| `long` | `Long` |
| `float` | `Float` |
| `double` | `Double` |
| `char` | `Character` |
| `boolean` | `Boolean` |

#### Autoboxing / Unboxing

```java
Integer boxed = 42;       // autoboxing: int → Integer
int unboxed = boxed;      // unboxing: Integer → int
```

#### 💣 Bẫy kinh điển

```java
Integer a = 1000, b = 1000;
System.out.println(a == b);       // false  (2 object khác nhau)
System.out.println(a.equals(b));  // true

Integer c = 100, d = 100;
System.out.println(c == d);       // true 😲
```

Vì sao `100 == 100` là `true` mà `1000 == 1000` là `false`?

→ Java có **Integer cache** cho giá trị **−128…127**. Trong khoảng này, `valueOf` trả về **cùng một object**, ngoài khoảng thì tạo mới.

```java
Integer c = Integer.valueOf(100); // lấy từ cache
Integer d = Integer.valueOf(100); // cùng object → == true
```

👉 Trong code thực tế **đừng bao giờ dùng `==` để so sánh wrapper**; luôn dùng `.equals()` hoặc (tốt hơn) unbox trước.

---

## 4. Hiệu năng — khi nào primitive hơn hẳn?

| Trường hợp | Nên dùng |
|---|---|
| Vòng lặp nặng, tính toán số học | **Primitive** (`int`, `double`) — tránh boxing/unboxing |
| Field trong class nhỏ, tạo hàng triệu object | **Primitive** — tiết kiệm bộ nhớ, tránh GC |
| Cần lưu vào Collection/Generic | **Wrapper** (`List<Integer>`) |
| Cần biểu diễn "không có giá trị" | **Wrapper** (null) hoặc **OptionalInt** |
| Tính tiền tệ chính xác | **BigDecimal** (reference class chuyên dụng) — như đã bàn |

Boxing mỗi lần lặp trong vòng lớn có thể tạo ra hàng triệu object rác:

```java
Long sum = 0L;
for (long i = 0; i < 10_000_000; i++) {
    sum += i;   // 🐢 mỗi lần + đều unbox rồi box lại
}

long sum2 = 0L;
for (long i = 0; i < 10_000_000; i++) {
    sum2 += i;  // ⚡ nhanh hơn nhiều lần
}
```

---

## 5. Bảng tóm tắt "một dòng"

| | Primitive | Reference |
|---|---|---|
| **Chứa gì** | Giá trị | Địa chỉ |
| **Null được không** | Không | Có |
| **`==` nghĩa là** | So sánh giá trị | So sánh địa chỉ |
| **Có method không** | Không | Có |
| **Dùng trong Generic** | Không | Có |
| **Hiệu năng** | Nhanh, nhẹ | Chậm hơn, cần heap + GC |
| **Rủi ro chính** | Tràn số, mất chính xác (float/double) | NPE, aliasing (2 biến cùng object) |

---

## 6. Lời khuyên thực chiến

1. **Tính toán hiệu năng cao, số học đơn giản** → dùng primitive.
2. **Lưu trong collection, cần null, cần method tiện ích** → dùng wrapper hoặc class chuyên dụng.
3. **So sánh wrapper/reference** → `.equals()` hoặc `Objects.equals(a, b)`, **không dùng `==`**.
4. **Sao chép object** → hiểu rõ shallow copy vs deep copy (vì reference chỉ copy địa chỉ).
5. **API công khai** có thể cân nhắc wrapper để biểu diễn "không có giá trị"; hoặc dùng `OptionalInt`/`Optional<T>`.
6. **Tiền tệ, số thập phân chính xác** → `BigDecimal` (như bài trước).



# `equals()` Method trong Java — Giải thích chi tiết

---

## 1. `equals()` là gì?

`equals()` là method khai báo trong `java.lang.Object`:

```java
public boolean equals(Object obj)
```

→ **Mọi class trong Java đều có `equals()`** vì mọi class đều kế thừa `Object`.

**Mục đích:** định nghĩa **"hai object được coi là bằng nhau"** theo nghĩa logic/nghiệp vụ — chứ không phải cùng một vùng nhớ.

---

## 2. `equals()` mặc định — kế thừa từ `Object`

```java
// Trong Object.class
public boolean equals(Object obj) {
    return (this == obj);   // so sánh ĐỊA CHỈ
}
```

👉 Nếu không override, `equals()` **giống hệt `==`**: chỉ trả về `true` khi hai biến trỏ tới **cùng một object**.

```java
class Person {
    String name;
    Person(String name) { this.name = name; }
}

Person p1 = new Person("An");
Person p2 = new Person("An");

System.out.println(p1 == p2);       // false
System.out.println(p1.equals(p2));  // false ❗ (vì chưa override)
```

Dù `p1` và `p2` có cùng tên, `equals()` vẫn `false` vì chúng là 2 object khác nhau.

---

## 3. `==` vs `equals()` — phân biệt rạch ròi

|                       | `==`                                    | `equals()`                                                                       |
| --------------------- | --------------------------------------- | -------------------------------------------------------------------------------- |
| **Bản chất**          | Toán tử của ngôn ngữ                    | Method của `Object`                                                              |
| **Với primitive**     | So sánh **giá trị**                     | ❌ Không gọi được (primitive không có method)                                     |
| **Với reference**     | So sánh **địa chỉ** (cùng object không) | **Mặc định** cũng so sánh địa chỉ, **nhưng có thể override** để so sánh nội dung |
| **Có override được?** | Không                                   | Có                                                                               |
| **Dùng khi nào**      | Kiểm tra 2 biến trỏ cùng object         | Kiểm tra 2 object "bằng nhau về mặt logic"                                       |

```java
String s1 = new String("hello");
String s2 = new String("hello");

s1 == s2;        // false  (2 object khác nhau)
s1.equals(s2);   // true   (String đã override để so sánh nội dung)
```

---

## 4. Hợp đồng (contract) của `equals()` — **rất quan trọng**

Theo Javadoc của `Object.equals`, khi override phải đảm bảo **5 tính chất**:

| Tính chất      | Ý nghĩa                                                |
| -------------- | ------------------------------------------------------ |
| **Reflexive**  | `x.equals(x)` luôn `true`                              |
| **Symmetric**  | `x.equals(y)` ⇔ `y.equals(x)`                          |
| **Transitive** | `x.equals(y)` và `y.equals(z)` ⇒ `x.equals(z)`         |
| **Consistent** | Gọi nhiều lần cho cùng kết quả (nếu dữ liệu không đổi) |
| **Non-null**   | `x.equals(null)` luôn `false` (không ném NPE)          |

Vi phạm contract → hành vi bất thường trong `HashMap`, `HashSet`, `List.contains()`...

---

## 5. Cách override `equals()` đúng chuẩn

### Template chuẩn

```java
@Override
public boolean equals(Object obj) {
    // 1. Kiểm tra cùng tham chiếu (nhanh, tối ưu)
    if (this == obj) return true;

    // 2. Kiểm tra null và kiểm tra kiểu
    if (obj == null || getClass() != obj.getClass()) return false;

    // 3. Ép kiểu
    Person other = (Person) obj;

    // 4. So sánh từng field quan trọng
    return Objects.equals(this.name, other.name)
        && this.age == other.age;
}
```

### Ví dụ đầy đủ

```java
import java.util.Objects;

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person other = (Person) obj;
        return age == other.age
            && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
```

```java
Person p1 = new Person("An", 20);
Person p2 = new Person("An", 20);

System.out.println(p1.equals(p2)); // true ✅
```

---

## 6. Tại sao **luôn override `hashCode()` khi override `equals()`**?

Đây là **contract bắt buộc**:

> Nếu `x.equals(y)` là `true` → **`x.hashCode() == y.hashCode()` phải là `true`**.

Nếu vi phạm → `HashMap`, `HashSet`, `Hashtable` sẽ hoạt động sai.

### Ví dụ bug kinh điển

```java
class Person {
    String name;
    Person(String name) { this.name = name; }
    // chỉ override equals, KHÔNG override hashCode
    @Override public boolean equals(Object o) { ... }
}

Set<Person> set = new HashSet<>();
set.add(new Person("An"));

System.out.println(set.contains(new Person("An"))); // false ❗
```

**Vì sao?** `HashSet` tìm bằng cách:

1. Tính `hashCode()` → tìm "bucket"
2. Trong bucket, dùng `equals()` để so sánh

Nếu `hashCode()` khác nhau → rơi vào bucket khác → **không bao giờ gọi `equals()`** → trả về `false` dù 2 object "bằng nhau".

### Cách override `hashCode()` chuẩn

```java
@Override
public int hashCode() {
    return Objects.hash(name, age);
}
```

Hoặc thủ công (nhanh hơn chút):

```java
@Override
public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + age;
    return result;
}
```

> Số **31** được dùng phổ biến vì là số nguyên tố lẻ, `31 * i == (i << 5) - i` — JVM tối ưu tốt.

---

## 7. Các cách viết `equals()` — ưu nhược điểm

### Cách 1: `instanceof`

```java
if (!(obj instanceof Person)) return false;
Person other = (Person) obj;
```

✅ Cho phép subclass so sánh được (linh hoạt)
❌ Có thể **vi phạm symmetry** nếu subclass override `equals()` khác đi

### Cách 2: `getClass()` — **an toàn hơn**

```java
if (obj == null || getClass() != obj.getClass()) return false;
```

✅ Đảm bảo symmetry, không bị subclass "phá"
❌ Không cho phép so sánh giữa class cha và con

> **Khuyến nghị:** dùng `getClass()` khi class có thể bị kế thừa, hoặc class là `final`. Dùng `instanceof` nếu bạn thiết kế hệ thống phân cấp chặt chẽ.

### Cách 3: Dùng IDE generate

IntelliJ/Eclipse đều có **Generate → equals() and hashCode()** → sinh code chuẩn, an toàn.

### Cách 4: `Objects.equals()` (Java 7+)

```java
import java.util.Objects;

if (!Objects.equals(this.name, other.name)) return false;
```

`Objects.equals(a, b)` tự xử lý `null` an toàn (không ném NPE):

```java
public static boolean equals(Object a, Object b) {
    return (a == b) || (a != null && a.equals(b));
}
```

### Cách 5: Record (Java 14+) — **tự động**

```java
public record Person(String name, int age) {}
```

Record **tự sinh** `equals()`, `hashCode()`, `toString()` dựa trên tất cả field. Không cần override trừ khi muốn tùy biến.

### Cách 6: Lombok

```java
@EqualsAndHashCode
public class Person { ... }
```

---

## 8. Những bẫy thường gặp

### 8.1. Quên override `hashCode()`

→ Bug ngầm trong `HashMap`/`HashSet` như đã nói ở mục 6.

### 8.2. Override sai signature

```java
// ❌ SAI: overload, không phải override
public boolean equals(Person other) { ... }
```

Compiler không báo lỗi, nhưng `Object.equals(Object)` vẫn được gọi → không hoạt động đúng.

**Giải pháp:** luôn thêm `@Override` — compiler sẽ báo nếu sai.

### 8.3. So sánh `String` bằng `==` trong `equals()`

```java
// ❌ SAI
return this.name == other.name;   // so địa chỉ!

// ✅ ĐÚNG
return Objects.equals(this.name, other.name);
```

### 8.4. So sánh số thực bằng `==`

```java
class Point {
    double x, y;
    // ❌ SAI vì sai số nhị phân
    return this.x == other.x && this.y == other.y;
    
    // ✅ ĐÚNG
    return Double.compare(this.x, other.x) == 0
        && Double.compare(this.y, other.y) == 0;
}
```

Hoặc với `BigDecimal`, dùng `compareTo`:

```java
// ❌ BigDecimal.equals() so cả scale → 3.0 != 3.00
// ✅ compareTo() == 0 so giá trị
return this.amount.compareTo(other.amount) == 0;
```

### 8.5. Không kiểm tra `null`

```java
// ❌ Ném NPE
public boolean equals(Object obj) {
    Person other = (Person) obj;  // obj null → NPE
    ...
}
```

### 8.6. Kế thừa `equals()` gây rối

```java
class Animal {
    String name;
    @Override public boolean equals(Object o) {
        if (!(o instanceof Animal)) return false;
        return ((Animal)o).name.equals(this.name);
    }
}

class Dog extends Animal {
    String breed;
    @Override public boolean equals(Object o) {
        if (!(o instanceof Dog)) return false;
        Dog d = (Dog) o;
        return super.equals(d) && this.breed.equals(d.breed);
    }
}
```

```java
Animal a = new Animal("Kiki");
Dog d = new Dog("Kiki", "Husky");

a.equals(d); // true  (Animal.equals chấp nhận Dog)
d.equals(a); // false (Dog.equals đòi instance of Dog)
// ❌ Vi phạm SYMMETRY!
```

**Cách khắc phục:** dùng `getClass()` thay vì `instanceof`, hoặc cấu trúc lại class.

---

## 9. `equals()` với các class thông dụng

| Class                | `equals()` so sánh gì?                                                            |
| -------------------- | --------------------------------------------------------------------------------- |
| `String`             | Nội dung ký tự                                                                    |
| `Integer`, `Long`... | Giá trị số                                                                        |
| `BigDecimal`         | **Cả giá trị + scale** (`3.0` ≠ `3.00`) → muốn so giá trị dùng `compareTo() == 0` |
| `Arrays` (`int[]`)   | **Địa chỉ** (mảng không override) → dùng `Arrays.equals(a, b)`                    |
| `List`, `Set`, `Map` | Nội dung (phần tử bên trong)                                                      |
| `Object` (mặc định)  | Địa chỉ                                                                           |

### Lưu ý về `BigDecimal`

```java
BigDecimal a = new BigDecimal("1.0");
BigDecimal b = new BigDecimal("1.00");

a.equals(b);            // ❌ false (scale khác nhau: 1 vs 2)
a.compareTo(b) == 0;    // ✅ true  (giá trị bằng nhau)
```

### Lưu ý về mảng

```java
int[] a = {1, 2, 3};
int[] b = {1, 2, 3};

a.equals(b);              // ❌ false (so địa chỉ)
Arrays.equals(a, b);      // ✅ true
Arrays.deepEquals(aa, bb); // cho mảng 2 chiều
```

---

## 10. Tóm tắt nhanh

| Điểm                              | Ghi nhớ                                                           |
| --------------------------------- | ----------------------------------------------------------------- |
| **`equals()` mặc định**           | So sánh địa chỉ (giống `==`)                                      |
| **Khi nào override**              | Khi muốn 2 object khác nhau về địa chỉ nhưng "bằng nhau" về logic |
| **Bắt buộc đi kèm**               | `hashCode()` — luôn override cùng nhau                            |
| **An toàn**                       | Luôn thêm `@Override`, dùng `Objects.equals()`, xử lý `null`      |
| **Chọn `instanceof`/`getClass`**  | `getClass()` an toàn hơn khi có kế thừa                           |
| **Không override `equals()` cho** | Class mutable được dùng làm key `HashMap` (rủi ro hash thay đổi)  |
| **Ưu tiên hiện đại**              | Record → tự sinh; Lombok → `@EqualsAndHashCode`                   |
| **BigDecimal**                    | Dùng `compareTo() == 0`, không dùng `equals()`                    |
| **Mảng**                          | Dùng `Arrays.equals()` / `Arrays.deepEquals()`                    |