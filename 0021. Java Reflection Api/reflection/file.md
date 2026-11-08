# Java Reflection API - Giải thích chi tiết

> ⚠️ **Lưu ý:** Trong cuộc trò chuyện này tôi **không thấy hình ảnh nào** được gửi kèm. Có thể bạn quên đính kèm hoặc hệ thống chưa tải lên. Bạn hãy **gửi lại hình ảnh** để tôi phân tích từng hình cho bạn nhé.
>
> Trong khi chờ, tôi sẽ giải thích **toàn bộ lý thuyết Java Reflection API** kèm ví dụ code chi tiết bằng tiếng Việt như bạn yêu cầu.

---

## 1. Reflection là gì?

**Định nghĩa:** Reflection là cơ chế cho phép chương trình Java **kiểm tra (introspect)** và **thay đổi (modify)** cấu trúc, hành vi của class, interface, field, method, constructor **tại runtime** (thời điểm chạy), thay vì phải biết trước tại compile-time.

**Ví dụ đời thường:**  
Bình thường khi bạn viết `person.getName()`, bạn đã "biết" đối tượng có method `getName()` ngay từ lúc code. Với Reflection, bạn có thể viết code **không biết trước** tên class, tên method, ví dụ:

```java
Object obj = Class.forName("com.example.Person").getDeclaredConstructor().newInstance();
Method m = obj.getClass().getMethod("getName");
Object result = m.invoke(obj);
```

→ Code trên hoạt động với **bất kỳ class nào** có constructor mặc định và method `getName()`.

---

## 2. Các thành phần cốt lõi của Reflection API

| Thành phần | Package | Vai trò |
|---|---|---|
| `Class<T>` | `java.lang` | Đại diện cho một class/interface tại runtime |
| `Field` | `java.lang.reflect` | Đại diện cho biến thành viên (field) |
| `Method` | `java.lang.reflect` | Đại diện cho phương thức |
| `Constructor<T>` | `java.lang.reflect` | Đại diện cho constructor |
| `Modifier` | `java.lang.reflect` | Đọc modifier (public, private, static...) |
| `Array` | `java.lang.reflect` | Thao tác với mảng bằng reflection |
| `Proxy` | `java.lang.reflect` | Tạo dynamic proxy |
| `Parameter` | `java.lang.reflect` | Thông tin tham số của method/constructor |

---

## 3. Lý thuyết + Code chi tiết từng phần

### 3.1. `Class<T>` – Trung tâm của Reflection

**Lý thuyết:**  
Mọi class trong Java khi được load vào JVM đều có một đối tượng `Class<?>` đại diện. Từ đối tượng này bạn có thể "mở ra" toàn bộ thông tin: tên, package, superclass, interface, field, method, constructor, annotation, modifier...

**Có 3 cách để lấy `Class` object:**

```java
// Cách 1: Dùng class literal (nhanh nhất, an toàn compile-time)
Class<Person> c1 = Person.class;

// Cách 2: Dùng getClass() từ một instance
Person p = new Person();
Class<?> c2 = p.getClass();

// Cách 3: Dùng Class.forName() - hữu ích khi tên class đến từ config/chuỗi
Class<?> c3 = Class.forName("com.example.Person");
```

**Ví dụ đầy đủ:**

```java
package com.example;

public class Person {
    private String name;
    private int age;
    public static int count;

    public Person() {}
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    private void secretMethod() {
        System.out.println("Secret!");
    }
}
```

```java
public class ClassDemo {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("com.example.Person");

        System.out.println("Tên đầy đủ: " + clazz.getName());        // com.example.Person
        System.out.println("Tên ngắn:   " + clazz.getSimpleName());   // Person
        System.out.println("Package:    " + clazz.getPackageName());  // com.example
        System.out.println("Superclass: " + clazz.getSuperclass());   // class java.lang.Object
    }
}
```

**Giải thích:**
- `Class.forName(...)` load class theo tên đầy đủ (kể cả package).
- `getName()` trả về tên fully-qualified (có package).
- `getSimpleName()` trả về chỉ tên class.
- `getSuperclass()` trả về `Class<?>` của lớp cha (mặc định là `Object`).

---

### 3.2. `Field` – Truy cập biến thành viên

**Lý thuyết:**
- `getFields()` → chỉ trả về **public** field (kể cả kế thừa).
- `getDeclaredFields()` → trả về **tất cả** field khai báo trực tiếp trong class (public, private, protected, default), **không gồm kế thừa**.
- `setAccessible(true)` → bỏ qua kiểm tra truy cập (cho phép đọc/ghi field private). Từ Java 9 cần mở module nếu dùng module system.

**Code:**

```java
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldDemo {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Person.class;

        System.out.println("=== getDeclaredFields (tất cả field) ===");
        for (Field f : clazz.getDeclaredFields()) {
            System.out.printf("Field: %-8s | Type: %-10s | Modifier: %s%n",
                    f.getName(),
                    f.getType().getSimpleName(),
                    Modifier.toString(f.getModifiers()));
        }

        System.out.println("\n=== getFields (chỉ public) ===");
        for (Field f : clazz.getFields()) {
            System.out.println("Public field: " + f.getName());
        }

        // Truy cập field private
        Person p = new Person("Alice", 30);

        Field nameField = clazz.getDeclaredField("name");
        nameField.setAccessible(true);              // Bỏ kiểm tra private
        System.out.println("\nTrước khi sửa: " + nameField.get(p));   // Alice
        nameField.set(p, "Bob");
        System.out.println("Sau khi sửa:   " + nameField.get(p));      // Bob

        // Truy cập field static
        Field countField = clazz.getDeclaredField("count");
        countField.setAccessible(true);
        countField.set(null, 100);                 // static → truyền null
        System.out.println("count = " + countField.get(null));
    }
}
```

**Giải thích chi tiết:**
- Vòng `for` đầu liệt kê **tất cả** field khai báo trong `Person` (gồm `name`, `age`, `count`) kèm type và modifier (`private`, `public static`...).
- `f.getType()` trả về `Class<?>` của kiểu dữ liệu field.
- `Modifier.toString(int)` chuyển số modifier thành chuỗi dễ đọc.
- `setAccessible(true)` là **chìa khóa** để thao tác field private — nếu không gọi, sẽ ném `IllegalAccessException`.
- Với field `static`, tham số `obj` khi `get()/set()` phải là `null`.

---

### 3.3. `Method` – Gọi phương thức động

**Lý thuyết:**
- `getMethods()` → **public** method (gồm cả kế thừa).
- `getDeclaredMethods()` → **tất cả** method khai báo trực tiếp trong class.
- `getMethod(name, paramTypes...)` → lấy một public method cụ thể.
- `getDeclaredMethod(...)` → lấy một method cụ thể (kể cả private).
- `Method.invoke(obj, args...)` → gọi method.

**Code:**

```java
import java.lang.reflect.Method;

public class MethodDemo {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Person.class;

        System.out.println("=== Danh sách method public ===");
        for (Method m : clazz.getMethods()) {
            // Lọc bớt method của Object để output gọn
            if (m.getDeclaringClass() == Object.class) continue;
            System.out.println(m.getName() + " - " + m);
        }

        System.out.println("\n=== Danh sách method declared ===");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.printf("Method: %-12s | Return: %-8s | Params: %d%n",
                    m.getName(),
                    m.getReturnType().getSimpleName(),
                    m.getParameterCount());
        }

        // Gọi method bình thường
        Person p = new Person("Alice", 25);
        Method getName = clazz.getMethod("getName");
        Object name = getName.invoke(p);              // gọi p.getName()
        System.out.println("\nGọi getName(): " + name);

        // Gọi method có tham số
        Method setAge = clazz.getMethod("setAge", int.class);
        setAge.invoke(p, 99);                         // p.setAge(99)
        System.out.println("Sau setAge(99): " + ((Person) p).getAge());

        // Gọi method private
        Method secret = clazz.getDeclaredMethod("secretMethod");
        secret.setAccessible(true);
        secret.invoke(p);                             // In ra "Secret!"
    }
}
```

**Giải thích:**
- `getMethod("setAge", int.class)` cần truyền **kiểu tham số chính xác** để phân biệt overload (nếu có nhiều `setAge`).
- `invoke(p, 99)` tương đương `p.setAge(99)`.
- Method private (`secretMethod`) **phải** `setAccessible(true)` mới gọi được.
- `m.getDeclaringClass()` cho biết method được khai báo ở class nào (dùng để lọc method kế thừa).

---

### 3.4. `Constructor<T>` – Tạo đối tượng động

**Lý thuyết:**
- `getConstructors()` → constructor **public**.
- `getDeclaredConstructors()` → **tất cả** constructor.
- `newInstance(args...)` → tạo object (từ Java 9 nên dùng `getDeclaredConstructor().newInstance()` thay vì `Class.newInstance()` đã deprecated).

**Code:**

```java
import java.lang.reflect.Constructor;

public class ConstructorDemo {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Person.class;

        System.out.println("=== Tất cả constructor ===");
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            System.out.println(c + " | Số tham số: " + c.getParameterCount());
        }

        // Tạo object với constructor không tham số
        Constructor<?> noArg = clazz.getDeclaredConstructor();
        Person p1 = (Person) noArg.newInstance();
        System.out.println("\nTạo p1: " + p1.getName() + " - " + p1.getAge());

        // Tạo object với constructor có tham số
        Constructor<?> twoArg = clazz.getDeclaredConstructor(String.class, int.class);
        Person p2 = (Person) twoArg.newInstance("Charlie", 40);
        System.out.println("Tạo p2: " + p2.getName() + " - " + p2.getAge());

        // Nếu constructor private
        // Constructor<?> privateCons = clazz.getDeclaredConstructor();
        // privateCons.setAccessible(true);
        // Person p3 = (Person) privateCons.newInstance();
    }
}
```

**Giải thích:**
- `getDeclaredConstructor(String.class, int.class)` lấy đúng constructor 2 tham số.
- `newInstance("Charlie", 40)` tương đương `new Person("Charlie", 40)`.
- Nếu constructor là `private`, gọi `setAccessible(true)` trước khi `newInstance()`.

---

### 3.5. `Modifier` – Đọc modifier

```java
import java.lang.reflect.Modifier;

public class ModifierDemo {
    public static void main(String[] args) throws Exception {
        var f = Person.class.getDeclaredField("count");   // public static int count

        int mod = f.getModifiers();
        System.out.println("Modifier nguyên: " + mod);
        System.out.println("Dạng chuỗi:      " + Modifier.toString(mod)); // "public static"

        System.out.println("Là public? " + Modifier.isPublic(mod));
        System.out.println("Là static? " + Modifier.isStatic(mod));
        System.out.println("Là final?  " + Modifier.isFinal(mod));
    }
}
```

---

### 3.6. `Array` – Thao tác với mảng bằng Reflection

```java
import java.lang.reflect.Array;

public class ArrayDemo {
    public static void main(String[] args) {
        // Tạo mảng int[] có 5 phần tử
        Object arr = Array.newInstance(int.class, 5);
        Array.set(arr, 0, 10);
        Array.set(arr, 1, 20);
        System.out.println("Length: " + Array.getLength(arr)); // 5
        System.out.println("Phần tử [1]: " + Array.get(arr, 1)); // 20

        // Mảng String[]
        Object strArr = Array.newInstance(String.class, 3);
        Array.set(strArr, 0, "Java");
        System.out.println("Phần tử [0]: " + Array.get(strArr, 0));
    }
}
```

**Ứng dụng:** hữu ích khi viết thư viện **generic** cần tạo mảng có kiểu runtime (vì `new T[n]` không compile được).

---

### 3.7. `Proxy` – Dynamic Proxy (nâng cao)

**Lý thuyết:** Tạo ra một object **giả lập** implements một interface và chặn mọi lời gọi method để xử lý trước/sau. Đây là nền tảng của **Spring AOP**, **Hibernate lazy loading**...

```java
import java.lang.reflect.*;

public class ProxyDemo {
    interface Greeter { String greet(String name); }

    public static void main(String[] args) {
        Greeter real = n -> "Xin chào, " + n;

        Greeter proxy = (Greeter) Proxy.newProxyInstance(
            Greeter.class.getClassLoader(),
            new Class<?>[]{ Greeter.class },
            (proxyObj, method, methodArgs) -> {
                System.out.println("[TRƯỚC] Gọi: " + method.getName());
                Object result = method.invoke(real, methodArgs);   // gọi method thật
                System.out.println("[SAU] Kết quả: " + result);
                return result;
            });

        System.out.println(proxy.greet("An"));
    }
}
```

**Kết quả:**
```
[TRƯỚC] Gọi: greet
[SAU] Kết quả: Xin chào, An
Xin chào, An
```

**Giải thích:** Lambda thứ 3 là `InvocationHandler` — nhận mọi lời gọi tới `proxy`, cho phép chèn logic trước/sau. Đây chính là cách **AOP** hoạt động bên dưới.

---

## 4. Ứng dụng thực tế của Reflection

| Ứng dụng | Cách Reflection được dùng |
|---|---|
| **Spring / Hibernate** | Scan annotation, inject dependency, tạo proxy |
| **Jackson / Gson** | Đọc field để serialize/deserialize JSON |
| **JUnit** | Tìm method có `@Test` rồi invoke |
| **JDBC** | `Class.forName("com.mysql.cj.jdbc.Driver")` để load driver |
| **IDE autocomplete** | Liệt kê method/field của class |
| **Plugin system** | Load class từ file `.jar` bên ngoài |

---

## 5. Ưu / Nhược điểm

**Ưu điểm:**
- Linh hoạt, cho phép code **generic** với mọi class.
- Nền tảng của hầu hết framework Java.
- Cho phép mở rộng mà không cần sửa code cũ (Open/Closed).

**Nhược điểm:**
- **Chậm hơn** gọi trực tiếp (vì phải qua kiểm tra runtime, không inline được).
- **Mất an toàn kiểu** (compile-time) → dễ `ClassCastException`, `NoSuchMethodException`.
- **Phá vỡ encapsulation** (truy cập private).
- Có thể bị **chặn bởi SecurityManager** (Java cũ) hoặc **module system** (Java 9+).

---

## 6. Tổng kết – “Cheat sheet” nhanh

```java
// 1. Lấy Class
Class<?> c = obj.getClass();               // từ instance
Class<?> c = Person.class;                 // class literal
Class<?> c = Class.forName("...Person");   // theo tên

// 2. Field
Field f = c.getDeclaredField("name");
f.setAccessible(true);
Object v = f.get(obj);                     // đọc
f.set(obj, "NewValue");                    // ghi

// 3. Method
Method m = c.getDeclaredMethod("foo", int.class);
m.setAccessible(true);
Object r = m.invoke(obj, 42);

// 4. Constructor
Constructor<?> ctor = c.getDeclaredConstructor(String.class);
Person p = (Person) ctor.newInstance("An");
```

---

📌 **Nhắc lại:** Bạn hãy **gửi lại hình ảnh** để tôi phân tích chi tiết từng hình (có thể là sơ đồ kiến trúc Reflection, class diagram, hay slide lý thuyết). Khi có hình, tôi sẽ giải thích từng thành phần, mũi tên, quan hệ trong hình kèm ví dụ code tương ứng.

Nếu bạn muốn tôi đi sâu thêm phần nào (ví dụ: `Annotation` + Reflection, `ClassLoader`, `MethodHandle` như “Reflection thế hệ mới”), cứ nói nhé!


Chào bạn, tôi đã nhận được **4 hình ảnh** bạn gửi từ khóa học Udemy (Java from Zero to First Job). Tôi sẽ đi vào phân tích chi tiết **từng hình**, giải thích lý thuyết, kèm theo **ví dụ code thực tế** và giải thích code cho bạn dễ hiểu nhất.

Để xuyên suốt các ví dụ được liền mạch, tôi sẽ tạo một class mẫu có tên là `Student` để làm đối tượng thử nghiệm.

---

### 🎓 Class Mẫu (Sẽ dùng cho tất cả các ví dụ bên dưới)
```java
package com.example;

public class Student {
    private String name;
    public int age;
    
    public Student() {}
    
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void sayHello() {
        System.out.println("Xin chào, tôi là " + name);
    }
    
    private void studySecret() {
        System.out.println("Đang học bí kíp võ công...");
    }
}
```

---

### 🖼️ HÌNH 1: AGENDA (Lộ trình bài học)
**Nội dung hình:** Đây là slide lộ trình (Agenda) của bài học Reflection. Nó liệt kê những gì bạn sẽ được học:
1. Reflection API là gì?
2. Mục tiêu của Reflection API.
3. Các ví dụ về Reflection API.
4. Tổng quan về Java Reflection API.
5. Tổng quan về package `java.lang.reflect`.
6. Làm việc với các thực thể của class thông qua Reflection (fields, constructors, methods, interfaces, super classes).
7. Làm việc với các thực thể private (riêng tư) của class.

**Lý thuyết trọng tâm:** 
Slide này cho bạn biết bức tranh toàn cảnh. Reflection không phải là một tính năng bình thường bạn dùng hàng ngày, mà nó là "công cụ của các công cụ" (dùng để chế tạo ra các Framework như Spring, Hibernate). Điểm quan trọng nhất là mục số 7: Reflection có thể **phá vỡ tính đóng gói (Encapsulation)** để truy cập vào các thành phần `private`.

---

### 🖼️ HÌNH 2: ĐỊNH NGHĨA REFLECTION
**Nội dung hình:** Một slide trích dẫn: 
> *"Reflection is commonly used by programs which require the ability to examine or modify the runtime behavior of applications running in the Java virtual machine"*
> (Reflection thường được sử dụng bởi các chương trình yêu cầu khả năng kiểm tra hoặc sửa đổi hành vi thời gian chạy của các ứng dụng đang chạy trong máy ảo Java).

**Lý thuyết trọng tâm:**
*   **Compile-time (Thời gian biên dịch):** Bình thường khi bạn viết code, bạn phải biết rõ class đó tên gì, có method gì. Nếu gõ sai, trình biên dịch báo lỗi ngay.
*   **Runtime (Thời gian chạy):** Reflection cho phép chương trình Java "tự soi gương" (reflect) **ngay lúc nó đang chạy**. Bạn có thể truyền vào một chuỗi `"com.example.Student"`, và lúc chạy, Java mới bắt đầu đi tìm class này, xem nó có những biến gì, hàm gì để thực thi.

**Ví dụ code minh họa Hình 2:**
```java
public class RuntimeDemo {
    public static void main(String[] args) {
        try {
            // Lúc compile-time, ta chỉ truyền vào 1 chuỗi String
            String className = "com.example.Student"; 
            
            // Lúc runtime, JVM mới dùng Reflection để load class này vào bộ nhớ
            Class<?> clazz = Class.forName(className);
            
            System.out.println("Đã load thành công class: " + clazz.getName());
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy class này!");
        }
    }
}
```
**Giải thích code:** 
*   `Class.forName("...")`: Đây là cốt lõi của Reflection. Nó nhận một chuỗi, tìm class tương ứng trong bộ nhớ, và trả về đối tượng `Class`.
*   Nếu bạn gõ sai thành `"com.example.Student123"`, chương trình vẫn **biên dịch thành công**, nhưng khi **chạy** sẽ văng lỗi `ClassNotFoundException`. Đây chính là sự khác biệt giữa compile-time và runtime.

---

### 🖼️ HÌNH 3: USE CASES (Các trường hợp sử dụng)
**Nội dung hình:** Slide nói về "Reflection API - Use Cases". Có hình ảnh một con ma đang soi gương. Các use cases bao gồm:
*   Debugging (Gỡ lỗi)
*   Test Tools (Công cụ kiểm thử)
*   Extensibility Features (Tính năng mở rộng)
*   Virtual Development Environments (Môi trường phát triển ảo)

*(Ghi chú vui: Dòng phụ đề auto-generate ở dưới cùng ghi là "And thus does the bagasse need to be able to ximen private members on glosses" - đây là lỗi nhận diện giọng nói của Udemy, ý giảng viên có lẽ là "And thus does the developer need to be able to examine private members on classes" - tức là lập trình viên cần xem được các thành phần private).*

**Lý thuyết trọng tâm:**
*   **Debugging:** Khi bạn dùng các nút "Step Over", "Step Into" trong IDE (IntelliJ, Eclipse), IDE dùng Reflection để xem giá trị các biến `private` của object đang chạy.
*   **Test Tools:** JUnit dùng Reflection để tìm các hàm có gắn `@Test` và tự động gọi chúng.
*   **Extensibility (Mở rộng):** Spring Framework đọc file cấu hình, dùng Reflection để tạo object và tiêm dependency (Dependency Injection) mà không cần bạn viết hàm `new`.

**Ví dụ code minh họa Hình 3 (Giả lập công cụ Test):**
```java
import java.lang.reflect.Method;

public class MiniTestFramework {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("com.example.Student");
        
        // Giả lập công cụ test: tự động tìm và gọi các method có tên bắt đầu bằng "say"
        Method[] methods = clazz.getDeclaredMethods();
        Object studentObj = clazz.getDeclaredConstructor().newInstance();
        
        for (Method method : methods) {
            if (method.getName().startsWith("say")) {
                System.out.println("Đang tự động chạy test: " + method.getName());
                method.invoke(studentObj); // Thực thi method đó
            }
        }
    }
}
```
**Giải thích code:**
*   `clazz.getDeclaredMethods()`: Lấy ra tất cả các hàm của class `Student`.
*   `clazz.getDeclaredConstructor().newInstance()`: Dùng Reflection để tạo một đối tượng `Student` (tương đương với `new Student()`).
*   Vòng lặp for kiểm tra tên hàm, nếu bắt đầu bằng "say", nó dùng `invoke` để gọi hàm đó. Đây chính là cách các framework Test (như JUnit) hoạt động dưới mui xe.

---

### 🖼️ HÌNH 4: PACKAGE OVERVIEW (Tổng quan gói java.lang.reflect)
**Nội dung hình:** Đây là ảnh chụp tài liệu chính thức của Oracle (Java Docs) cho package `java.lang.reflect`. 
*   Nội dung nói rằng package này cung cấp các class và interface để lấy thông tin phản chiếu về class.
*   Nhấn mạnh 2 class quan trọng: `AccessibleObject` (cho phép bỏ qua kiểm tra quyền truy cập - tức là xem được đồ private) và `Array` (cung cấp các hàm tĩnh để tạo và truy cập mảng động).
*   Có một bảng "Interface Summary" liệt kê các interface như `AnnotatedArrayType`, `AnnotatedElement`... (những cái này liên quan đến việc đọc Annotation - một chủ đề nâng cao khác).

**Lý thuyết trọng tâm:**
Trong package `java.lang.reflect`, bạn sẽ làm việc chủ yếu với 4 class/interface cốt lõi sau:
1.  `Class<T>`: Đại diện cho bản thân cái class.
2.  `Field`: Đại diện cho biến (thuộc tính).
3.  `Method`: Đại diện cho hàm (phương thức).
4.  `Constructor`: Đại diện cho hàm khởi tạo.
Và đặc biệt là khái niệm **`AccessibleObject`** - lớp cha của Field, Method, Constructor, cung cấp hàm `setAccessible(true)` để "hack" quyền truy cập private.

**Ví dụ code minh họa Hình 4 (Truy cập private và tạo mảng động):**

```java
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Array;

public class DeepDiveReflection {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("com.example.Student");
        Student s = new Student("An", 20);

        // 1. LÀM VIỆC VỚI FIELD (Thuộc tính)
        Field nameField = clazz.getDeclaredField("name"); // Lấy biến private 'name'
        // nameField.setAccessible(true); // Nếu bỏ comment dòng này sẽ bị lỗi IllegalAccessException ở dòng dưới
        
        nameField.setAccessible(true); // Cấp quyền truy cập (như hình 4 nói về AccessibleObject)
        System.out.println("Tên private ban đầu: " + nameField.get(s)); // Đọc giá trị
        nameField.set(s, "Bình"); // Ghi giá trị mới
        System.out.println("Tên private sau khi sửa: " + nameField.get(s));

        // 2. LÀM VIỆC VỚI METHOD (Hàm)
        Method privateMethod = clazz.getDeclaredMethod("studySecret");
        privateMethod.setAccessible(true); // Bắt buộc phải có để gọi hàm private
        privateMethod.invoke(s); // Gọi hàm studySecret()

        // 3. LÀM VIỆC VỚI ARRAY (Như tài liệu Java Docs hình 4 đề cập)
        // Tạo một mảng int có 3 phần tử bằng Reflection
        Object intArray = Array.newInstance(int.class, 3);
        Array.set(intArray, 0, 10);
        Array.set(intArray, 1, 20);
        Array.set(intArray, 2, 30);
        System.out.println("Phần tử thứ 2 của mảng: " + Array.get(intArray, 1));
    }
}
```

**Giải thích chi tiết code Hình 4:**
*   `getDeclaredField("name")`: Lấy biến `name` (dù nó là private). Nếu dùng `getField()` thì sẽ báo lỗi vì `name` không phải public.
*   `nameField.setAccessible(true)`: Đây là "phép thuật" của Reflection (chính là `AccessibleObject`). Nó bảo JVM: "Đừng kiểm tra quyền truy cập nữa, cho phép tôi đọc/ghi biến private này".
*   `nameField.get(s)` và `nameField.set(s, "Bình")`: Tương đương với `s.name` và `s.name = "Bình"` (nếu như `name` là public).
*   `getDeclaredMethod("studySecret")`: Lấy ra hàm private.
*   `invoke(s)`: Thực thi hàm đó trên đối tượng `s`.
*   `Array.newInstance(...)`: Lớp `Array` trong Reflection cho phép tạo mảng với kiểu dữ liệu động (biết được kiểu dữ liệu lúc runtime chứ không phải lúc code).

---
**💡 Tổng kết:** 4 bức ảnh của bạn đang đi từ: **Giới thiệu mục tiêu** (H1) -> **Định nghĩa cốt lõi** (H2) -> **Ứng dụng thực tế** (H3) -> **Đi sâu vào package cấu trúc** (H4). Reflection rất mạnh mẽ nhưng cũng chậm và kém an toàn, nên chỉ dùng khi thực sự cần thiết (như viết Framework).

Bạn có muốn tôi giải thích thêm phần nào (ví dụ: cách đọc Annotation bằng Reflection, hoặc cách dùng nó để đọc file JSON) không?

Chào bạn, tôi đã nhận được file `ReflectionDemo.java` của bạn. Đây là một ví dụ **cực kỳ chi tiết và đầy đủ** về cách sử dụng Java Reflection API. Nó bao quát hầu hết các tính năng từ cơ bản đến nâng cao mà chúng ta đã thảo luận ở các hình ảnh trước.

Để bạn dễ hiểu nhất, tôi sẽ chia code của bạn thành **từng phần nhỏ (Block)**. Mỗi phần tôi sẽ đưa ra: **Đoạn code**, **Lý thuyết đằng sau nó**, và **Giải thích chi tiết từng dòng code**.

---

### 🎯 TỔNG QUAN VỀ CLASS `User` (Lớp mục tiêu)
Trước khi đi vào hàm `main`, hãy nhìn vào lớp `User` (lớp con bên trong `ReflectionDemo`). Lớp này được thiết kế cố tình với nhiều thành phần khác nhau để làm "bia đỡ đạn" cho Reflection:
*   Có biến `static private int id`.
*   Có các biến `private` (firstName, lastName, email).
*   Có constructor `public` (không tham số) và constructor `private` (có tham số).
*   Có method `public`, method `private`, và method `private static`.

*(Lưu ý: Vì `User` là lớp con bên trong `ReflectionDemo`, nên tên đầy đủ của nó khi biên dịch sẽ là `ReflectionDemo$User`)*.

---

### KHỐI 1: LẤY THÔNG TIN CƠ BẢN CỦA CLASS (Class Info)

**Đoạn code:**
```java
Class userClass = new User().getClass();
System.out.println("Get class name: " + userClass.getName());

Field[] fields = userClass.getDeclaredFields();
System.out.println("===== Get Fields Names =====");
Arrays.stream(fields).forEach(field -> System.out.println(field.getName()));

System.out.println("===== Get Fields Type =====");
Arrays.stream(fields).forEach(field -> System.out.println(field.getGenericType()));

System.out.println("===== Does Field have private modifier? =====");
Arrays.stream(fields).forEach(field -> System.out.println(Modifier.isPrivate(field.getModifiers())));

System.out.println("Get package name: " + userClass.getPackageName());
System.out.println("Get super class: " + userClass.getSuperclass());

System.out.println("===== Get Interfaces =====");
Class[] interfaces = userClass.getInterfaces();
Arrays.stream(interfaces).forEach(i -> System.out.println(i.getName()));
```

**📘 Lý thuyết:**
*   **`getDeclaredFields()`**: Lấy tất cả các trường (biến) được khai báo trực tiếp trong class này, bất kể là `public`, `private` hay `protected`.
*   **`getGenericType()`**: Lấy kiểu dữ liệu của biến (ví dụ: `String`, `int`).
*   **`Modifier.isPrivate()`**: Kiểm tra xem biến đó có phải là `private` không.
*   **`getInterfaces()`**: Lấy danh sách các Interface mà class này đang thực thi (implement).

**🔍 Giải thích code:**
1.  `new User().getClass()`: Tạo một đối tượng `User` rồi lấy đối tượng `Class` đại diện cho nó.
2.  Lấy mảng các `Field` (biến) bằng `getDeclaredFields()`. Kết quả sẽ trả về 4 biến: `id`, `firstName`, `lastName`, `email`.
3.  Dùng `Arrays.stream()` để duyệt qua từng biến và in ra:
    *   Tên biến (`field.getName()`).
    *   Kiểu dữ liệu (`field.getGenericType()`).
    *   Kiểm tra xem có phải `private` không (`Modifier.isPrivate(field.getModifiers())`).
4.  In ra tên package (ở đây là rỗng vì code không khai báo package) và lớp cha (`java.lang.Object`).
5.  Lấy các interface mà `User` implements (`Serializable`, `Cloneable`) và in tên chúng ra.

---

### KHỐI 2: LẤY THÔNG TIN CONSTRUCTOR (Hàm khởi tạo)

**Đoạn code:**
```java
userClass = Class.forName("ReflectionDemo$User");

System.out.println("===== Get Constructors =====");
Constructor<User>[] constructors = userClass.getDeclaredConstructors();
System.out.println("Number of constructors: " + constructors.length);
Arrays.stream(constructors).forEach(constructor -> System.out.println(constructor.getName()));

System.out.println("===== Get Private Constructor =====");
Constructor<User> privateConstructor = userClass.getDeclaredConstructor(String.class);
System.out.println(privateConstructor);
```

**📘 Lý thuyết:**
*   **`Class.forName()`**: Load class bằng tên chuỗi. Vì `User` là inner class, tên của nó phải dùng dấu `$` (thay vì dấu chấm).
*   **`getDeclaredConstructors()`**: Lấy TẤT CẢ các hàm khởi tạo (kể cả private).
*   **`getDeclaredConstructor(Class... parameterTypes)`**: Lấy một hàm khởi tạo cụ thể dựa vào kiểu tham số truyền vào.

**🔍 Giải thích code:**
1.  `Class.forName("ReflectionDemo$User")`: Tìm và load class `User`.
2.  Lấy tất cả constructor. Lớp `User` có 2 constructor (1 public không tham số, 1 private có 1 tham số String). In ra tên của chúng.
3.  `getDeclaredConstructor(String.class)`: Tìm chính xác cái constructor có 1 tham số kiểu `String` (đây chính là constructor `private User(String defaultEmail)`).

---

### KHỐI 3: LẤY THÔNG TIN METHOD (Phương thức)

**Đoạn code:**
```java
System.out.println("===== Get Methods =====");
Method[] methods = userClass.getDeclaredMethods();
Arrays.stream(methods).forEach(method -> System.out.println(method.getName()));

System.out.println("===== Get Method =====");
Method method = userClass.getMethod("mergeTwoUserAccounts", User.class);
System.out.println(method);
```

**📘 Lý thuyết:**
*   **`getDeclaredMethods()`**: Lấy tất cả các hàm khai báo trong class (public, private, static...).
*   **`getMethod(String name, Class... parameterTypes)`**: Lấy một hàm `public` cụ thể. Nếu hàm đó là `private`, phải dùng `getDeclaredMethod`.

**🔍 Giải thích code:**
1.  Lấy tất cả các hàm của `User` (gồm `printFullNameToConsole`, `mergeTwoUserAccounts`, `doSomething`, `doSomethingStatic`, `toString`...) và in tên ra.
2.  `getMethod("mergeTwoUserAccounts", User.class)`: Tìm hàm tên là `mergeTwoUserAccounts` và có tham số truyền vào là kiểu `User.class`.

---

### KHỐI 4: TẠO ĐỐI TƯỢNG BẰNG REFLECTION (New Instance)

**Đoạn code:**
```java
System.out.println("===== New Instance =====");
Constructor<User> defaultConstructor = userClass.getConstructor();
User newInstance = defaultConstructor.newInstance();
System.out.println(newInstance);

System.out.println(privateConstructor.newInstance("newdefault@email.com"));
```

**📘 Lý thuyết:**
*   **`getConstructor()`**: Chỉ lấy constructor `public` không có tham số.
*   **`newInstance(args...)`**: Tương đương với việc bạn gõ từ khóa `new User(...)`.

**🔍 Giải thích code:**
1.  Lấy constructor public không tham số, gọi `newInstance()` để tạo đối tượng `newInstance`.
2.  Dùng `privateConstructor` (đã lấy ở Khối 2) để tạo một đối tượng khác bằng cách truyền email vào.
    *(Lưu ý nhỏ: Bình thường gọi constructor private từ bên ngoài sẽ lỗi, nhưng vì class `ReflectionDemo` chứa class `User`, nó có quyền truy cập vào private của lớp con bên trong nó. Nếu `User` là class độc lập, bạn sẽ cần gọi `privateConstructor.setAccessible(true)` trước khi `newInstance`).*

---

### KHỐI 5: THAO TÁC VỚI CÁC BIẾN (Field Initialization)

**Đoạn code:**
```java
System.out.println("===== Field initalization =====");
Field firstNameField = userClass.getDeclaredField("firstName");
firstNameField.setAccessible(true); // Phá vỡ đóng gói
firstNameField.set(newInstance, "Sergey");
System.out.println(newInstance);

System.out.println("===== Static Field initalization =====");
Field idField = userClass.getDeclaredField("id");
idField.setAccessible(true);
idField.set(null, 20);
System.out.println(newInstance);

System.out.println("===== Read Value from Field ====");
System.out.println(firstNameField.get(newInstance));
System.out.println(idField.get(null));
```

**📘 Lý thuyết:**
*   **`getDeclaredField("tên_biến")`**: Lấy biến cụ thể (kể cả private).
*   **`setAccessible(true)`**: Cấp quyền "tối cao" để đọc/ghi biến private.
*   **`set(obj, value)`**: Gán giá trị. Đối với biến `static`, truyền `null` vào vị trí `obj`.
*   **`get(obj)`**: Đọc giá trị.

**🔍 Giải thích code:**
1.  Lấy biến `firstName`, gọi `setAccessible(true)` để có thể can thiệp vào nó.
2.  `firstNameField.set(newInstance, "Sergey")`: Gán giá trị `"Sergey"` cho biến `firstName` của đối tượng `newInstance`. (Tương đương `newInstance.firstName = "Sergey"`).
3.  Lấy biến `id` (biến static). Gán giá trị `20`. Vì là static, ta truyền `null` làm đối tượng đích: `idField.set(null, 20)`.
4.  Dùng `get()` để đọc giá trị của `firstName` và `id`.

---

### KHỐI 6: GỌI HÀM BẰNG REFLECTION (Method Invocation)

**Đoạn code:**
```java
System.out.println("===== Method invocation =====");
Method privateMethod = userClass.getDeclaredMethod("doSomething", String.class);
privateMethod.setAccessible(true);
privateMethod.invoke(newInstance, "Test String");

System.out.println("===== Static Method invocation =====");
Method privateStaticMethod = userClass.getDeclaredMethod("doSomethingStatic", String.class);
privateMethod.setAccessible(true); // Lưu ý: Dòng này trong code của bạn bị sai tên biến
privateStaticMethod.invoke(null, "Test String");
```

**📘 Lý thuyết:**
*   **`invoke(Object obj, Object... args)`**: Thực thi hàm. Đối với hàm `static`, truyền `null` vào `obj`.

**🔍 Giải thích code:**
1.  Lấy hàm `private` tên `doSomething` có 1 tham số `String`.
2.  `setAccessible(true)` để có thể gọi hàm private.
3.  `privateMethod.invoke(newInstance, "Test String")`: Gọi hàm `doSomething("Test String")` trên đối tượng `newInstance`.
4.  Lấy hàm `private static` tên `doSomethingStatic`.
5.  **⚠️ Lưu ý phát hiện lỗi trong code của bạn:** Bạn gọi `privateMethod.setAccessible(true);` thay vì `privateStaticMethod.setAccessible(true);`. Dù nó vẫn chạy được (do class ngoài gọi class trong), nhưng về mặt logic nên sửa lại thành `privateStaticMethod`.
6.  `privateStaticMethod.invoke(null, "Test String")`: Gọi hàm static. Vì là static, đối tượng truyền vào là `null`.

---
### 💡 TỔNG KẾT TỪ FILE CODE CỦA BẠN
File code của bạn là một **"bản đồ thu nhỏ"** (cheat sheet) hoàn hảo về Reflection. Nó chứng minh rằng với Reflection, bạn có thể:
1.  Xem cấu trúc của một class (tên, field, method, interface).
2.  Tạo đối tượng mà không cần dùng từ khóa `new` (kể cả constructor private).
3.  Thay đổi giá trị biến private và static.
4.  Gọi các hàm private và static.

**Cảnh báo thực tế:** Trong các dự án thực tế, bạn **hiếm khi** viết code trực tiếp như thế này (trừ khi đang viết Framework, Annotation Processor). Việc lạm dụng `setAccessible(true)` sẽ phá vỡ tính đóng gói của OOP và có thể gây lỗi bảo mật (như bị chặn bởi Java 17+ module system). Tuy nhiên, hiểu rõ cách nó hoạt động sẽ giúp bạn debug cực tốt và hiểu được cách các thư viện lớn (Spring, Hibernate, JUnit) vận hành bên dưới.