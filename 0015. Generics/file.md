Chào bạn, đây là một bài học vô cùng quan trọng: **Generics (Lập trình tổng quát)** trong Java. 

Dựa trên bức ảnh Agenda và 5 file code bạn gửi, tôi sẽ hệ thống lại toàn bộ lý thuyết và giải thích chi tiết từng dòng code để bạn hiểu sâu bản chất của Generics.

---

# PHẦN 1: LÝ THUYẾT GENERICS (Dựa trên Agenda)

### 1. Tại sao chúng ta cần Generics? (Why do we need generics)
Trước Java 5, các Collection (như `ArrayList`) mặc định chứa kiểu `Object`. Điều này dẫn đến 2 vấn đề lớn:
*   **Lỗi Runtime (ClassCastException):** Bạn có thể vô tình thêm một `String` vào danh sách dự định chỉ chứa `Integer`. Khi lấy ra ép kiểu, chương trình sẽ sập.
*   **Phải ép kiểu thủ công:** Mỗi lần lấy đồ ra, bạn phải viết `(Integer) list.get(0)`. Rất phiền phức và dễ sai.

**Generics ra đời để giải quyết:** Nó cho phép bạn chỉ định rõ kiểu dữ liệu ngay từ đầu (VD: `List<Integer>`). Trình biên dịch sẽ kiểm tra lỗi ngay lúc code (Compile-time) thay vì đợi đến lúc chạy (Runtime).

### 2. Các khái niệm cốt lõi
*   **Generic Class/Method:** Cho phép tham số hóa kiểu dữ liệu (dùng các ký tự như `<T>`, `<E>`, `<K, V>`).
*   **Diamond Operator (`<>`):** Từ Java 7, bạn có thể viết `new ArrayList<>()` thay vì `new ArrayList<String>()`. Trình biên dịch sẽ tự suy luận kiểu dữ liệu.
*   **Bounded Generics (Giới hạn kiểu):** Dùng từ khóa `extends` để giới hạn kiểu dữ liệu (VD: `<T extends Number>` chỉ cho phép Integer, Double, Float...).
*   **Multiple Bounds (Đa giới hạn):** Có thể giới hạn bởi 1 class và nhiều interface (VD: `<T extends ClassA & InterfaceB>`).
*   **Wildcards (`?` - Ký tự đại diện):** Dùng khi bạn không biết chính xác kiểu dữ liệu là gì.
    *   **Upper Bounded (`? extends T`):** Chấp nhận T hoặc các lớp con của T. (Dùng để **ĐỌC**).
    *   **Lower Bounded (`? super T`):** Chấp nhận T hoặc các lớp cha của T. (Dùng để **GHI**).
*   **Raw Types (Kiểu thô):** Là việc dùng Collection mà không chỉ định kiểu (VD: `List list = new ArrayList()`). Cách này đã lỗi thời và mất đi tính an toàn của Generics.
*   **Type Erasure (Xóa kiểu):** Đây là cơ chế "hậu trường" của Java. Generics chỉ tồn tại ở lúc code (Compile-time). Khi chương trình chạy (Runtime), Java sẽ **xóa** các thông tin `<T>`, `<E>` đi và thay bằng `Object` (hoặc class giới hạn) để đảm bảo tương thích với các phiên bản Java cũ.

---

# PHẦN 2: GIẢI THÍCH CHI TIẾT CODE

### File 1: `GenericDemo1.java` - Generic Method (Phương thức tổng quát)

```java
public class GenericDemo1 {
    public static void main(String[] args) {
        String[] strings = {"one", "two", "three"};
        printArray(strings); // OK

        Integer[] ints = {1, 2, 3};
        printArray(ints); // OK nhờ Generics
    }

    // Nếu dùng cách cũ (bị comment):
    // private static void printArray(String[] arr) -> Chỉ in được mảng String
    
    // Dùng Generic Method: <E> đại diện cho một kiểu dữ liệu bất kỳ
    private static <E> void printArray(E[] arr) {
        for (E element : arr) {
            System.out.println("Element: " + element);
        }
    }
}
```
**Giải thích:** 
*   Chữ `<E>` (viết tắt của Element) khai báo một kiểu dữ liệu tổng quát cho phương thức này.
*   Nhờ có `<E>`, hàm `printArray` có thể nhận vào mảng `String`, mảng `Integer`, mảng `Double`... mà không cần phải viết nhiều hàm trùng tên (overloading).

---

### File 2: `GenericDemo2.java` - Bounded Generics (Giới hạn kiểu)

```java
public class GenericDemo2 {
    public static void main(String args[]) {
        // Tìm số lớn nhất
        System.out.println(maxValue(1, 2, 3)); 
        // Tìm chuỗi lớn nhất (theo bảng chữ cái)
        System.out.println(maxValue("Audi", "Acura", "Aston Martin"));
    }

    // Giới hạn: T phải là lớp implements Comparable (có thể so sánh được)
    public static <T extends Comparable<T>> T maxValue(T x, T y, T z) {
        T max = x; 
        if (y.compareTo(max) > 0) max = y; 
        if (z.compareTo(max) > 0) max = z; 
        return max; 
    }
}
```
**Giải thích:**
*   Tại sao không viết `<T>` bình thường? Vì nếu viết `<T>`, trình biên dịch không biết `T` có hàm `compareTo()` hay không (lỡ truyền vào một Object không thể so sánh thì sao?).
*   `<T extends Comparable<T>>` ép buộc: "Truyền kiểu gì cũng được, miễn là kiểu đó có khả năng so sánh (implements Comparable)".
*   *(Chú thích trong code có nhắc đến Multiple bounds: `<T extends Comparable<T> & Comparator<T>>`. Nghĩa là T phải vừa Comparable, vừa Comparator. Rất ít dùng).*

---

### File 3: `GenericDemo3.java` - Generic Class & Diamond Operator

```java
public class GenericDemo3<T> { // Class tổng quát, T là kiểu dữ liệu
    private T t;

    public void set(T t) { this.t = t; }
    public T get() { return t; }

    public static void main(String[] args) {
        // Diamond Operator <>
        GenericDemo3<Integer> integerBox = new GenericDemo3<>(); 
        GenericDemo3<String> stringBox = new GenericDemo3<>();

        integerBox.set(10);
        // integerBox.set("ten"); // LỖI NGAY LÚC COMPILE (Bảo vệ dữ liệu)
        
        stringBox.set("Hello World");

        Integer integer = integerBox.get(); // Không cần ép kiểu (Integer)
        String string = stringBox.get();    // Không cần ép kiểu (String)
    }
}
```
**Giải thích:**
*   `GenericDemo3<T>`: Biến class thành một cái khuôn linh hoạt. Khi khởi tạo `new GenericDemo3<Integer>()`, toàn bộ chữ `T` trong class sẽ biến thành `Integer`.
*   **Diamond Operator `<>`**: `new GenericDemo3<>()` thay vì `new GenericDemo3<Integer>()`. Code gọn hơn rất nhiều.

---

### File 4: `GenericDemo4.java` - Wildcards (Ký tự đại diện) - **QUAN TRỌNG NHẤT**

Đây là phần khó nhất, liên quan đến 3 class: `Parent`, `Child` (kế thừa Parent), `Grandchild` (kế thừa Child).

**Trường hợp 1: Upper Bounded Wildcard (`? extends Parent`)**
```java
public static void processElements(List<? extends Parent> elements) {
    Parent parent = elements.get(0); // ĐỌC OK: Chắc chắn ít nhất là Parent
    // elements.add(new Parent()); // LỖI: Không thể thêm!
    // elements.add(new Child());  // LỖI: Không thể thêm!
}
```
*   **Ý nghĩa:** `elements` có thể là `List<Parent>`, `List<Child>`, hoặc `List<Grandchild>`.
*   **Lý do không thể add:** Trình biên dịch không biết chính xác danh sách này đang chứa kiểu gì. Lỡ nó là `List<Child>` mà bạn `add(new Parent())` thì danh sách sẽ bị phá vỡ quy tắc (Parent không phải Child). Do đó, Java cấm add (trừ `null`).

**Trường hợp 2: Lower Bounded Wildcard (`? super Child`)**
```java
public static void processElements2(List<? super Child> elements) {
    Object element = elements.get(0); // ĐỌC: Chỉ chắc chắn là Object
    // Child child = elements.get(0); // LỖI
    
    elements.add(new Child());      // GHI OK
    elements.add(new Grandchild()); // GHI OK
}
```
*   **Ý nghĩa:** `elements` có thể là `List<Child>`, `List<Parent>`, hoặc `List<Object>`.
*   **Lý do add được:** Dù danh sách là kiểu gì (Child, Parent hay Object), thì một đối tượng `Child` chắc chắn đều "lọt" vào được (vì Child là con của Parent/Object).
*   **Lý do không đọc được ra Child:** Lỡ danh sách là `List<Parent>` hoặc `List<Object>`, lấy ra có thể là một con vật khác, không phải Child.
*   *Mẹo nhớ:* **PECS** (Producer Extends, Consumer Super). Dùng `extends` khi bạn chỉ muốn lấy dữ liệu ra (Producer). Dùng `super` khi bạn chỉ muốn nhét dữ liệu vào (Consumer).

---

### File 5: `GenericDemo5.java` - Raw Types & Lỗi kinh điển

```java
public class GenericDemo5 {
    public static void main(String[] args) {
        List<Integer> integers = new ArrayList<>(); // List chuẩn, chỉ chứa số

        // Raw Type (Kiểu thô - Không có <T>)
        List list = new ArrayList(); 
        
        // Gán list thô = list chuẩn (Hợp lệ về mặt cú pháp, chỉ bị Warning)
        list = integers; 
        
        // Thêm một String vào list thô
        list.add("some string"); 

        // Lấy ra phần tử đầu tiên (Đang bị ngầm hiểu là Integer)
        Integer integer = integers.get(0); 
        
        // CHƯƠNG TRÌNH SẼ SẬP TẠI ĐÂY VỚI LỖI: ClassCastException
        System.out.println(integer);
    }
}
```
**Giải thích:**
*   Đây chính là lý do **tại sao chúng ta cần Generics**. 
*   Vì `list` là Raw Type, trình biên dịch không kiểm tra chặt chẽ. Nó cho phép bạn nhét `"some string"` vào. 
*   Nhưng `integers` vẫn tin rằng nó chứa `Integer`. Khi bạn lấy ra ép kiểu ngầm định, Java cố gắng biến `"some string"` thành `Integer` -> **Sập chương trình (ClassCastException)**.
*   Ngày nay, hãy **tuyệt đối tránh sử dụng Raw Types**.

**Tổng kết:** Generics giúp code an toàn (bắt lỗi sớm), tái sử dụng tốt hơn (viết 1 hàm dùng cho nhiều kiểu), và không cần ép kiểu thủ công. Hãy luôn cố gắng chỉ định rõ kiểu dữ liệu `<...>` khi làm việc với Collection!

**Generics** (dịch ra tiếng Việt là **Lập trình tổng quát**) là một tính năng cực kỳ mạnh mẽ được giới thiệu từ Java 5. 

Hiểu một cách đơn giản nhất: **Generics cho phép bạn tham số hóa kiểu dữ liệu (Type Parameters).** Nghĩa là thay vì viết code cứng cho một kiểu dữ liệu cụ thể (như `String` hay `Integer`), bạn viết một "khuôn mẫu" code có thể hoạt động an toàn với *bất kỳ* kiểu dữ liệu nào mà người dùng truyền vào.

Để dễ hiểu, hãy xem ẩn dụ và sự so sánh "Trước và Sau" khi có Generics dưới đây:

---

### 1. Ẩn dụ thực tế: Chiếc hộp có dán nhãn

*   **Trước khi có Generics (Raw Type):** Bạn có một chiếc hộp carton không dán nhãn. Bạn nhét quần áo, sách vở, và cả đồ ăn vào đó. Khi muốn lấy quần áo ra, bạn phải thò tay vào mò mẫm, có thể lấy nhầm phải đồ ăn (gây ra lỗi chương trình).
*   **Khi có Generics:** Bạn có một chiếc hộp được dán nhãn rõ ràng: `Hộp<Quần Áo>`. Trình biên dịch Java (Compiler) sẽ đóng vai trò như một người bảo vệ: Nếu bạn cố tình nhét một cuốn sách vào hộp này, nó sẽ báo lỗi ngay lập tức. Khi lấy đồ ra, bạn chắc chắn 100% đó là quần áo, không cần phải kiểm tra lại.

---

### 2. Tại sao chúng ta cần Generics? (Vấn đề nó giải quyết)

Hãy nhìn vào sự khác biệt giữa việc dùng `ArrayList` không có Generics và có Generics:

**Cách cũ (Không dùng Generics):**
```java
List list = new ArrayList(); // Hộp không dán nhãn
list.add("Hello"); 
list.add(123); // Nhét số vào chung với chuỗi -> Không báo lỗi lúc code

// Khi lấy ra:
String s = (String) list.get(0); // Phải ép kiểu thủ công
String s2 = (String) list.get(1); // SẬP CHƯƠNG TRÌNH! (ClassCastException) vì phần tử 1 là Integer
```

**Cách mới (Dùng Generics):**
```java
List<String> list = new ArrayList<>(); // Hộp dán nhãn <String>
list.add("Hello");
// list.add(123); // LỖI NGAY LẬP TỨC KHI ĐANG CODE (Compile-time error)

String s = list.get(0); // Lấy ra dùng luôn, không cần ép kiểu (String)
```

---

### 3. 3 Lợi ích to lớn của Generics

1.  **An toàn kiểu (Type Safety):** Lỗi sai kiểu dữ liệu bị phát hiện ngay khi bạn đang viết code (Compile-time) thay vì đợi đến lúc chạy chương trình (Runtime). Điều này giúp giảm thiểu tối đa lỗi `ClassCastException` (như trong file `GenericDemo5.java` bạn đã gửi).
2.  **Loại bỏ ép kiểu (Elimination of Casts):** Bạn không cần phải viết `(String)` hay `(Integer)` mỗi khi lấy dữ liệu ra khỏi Collection nữa. Code sạch và dễ đọc hơn rất nhiều.
3.  **Tái sử dụng code (Code Reusability):** Bạn chỉ cần viết một lớp hoặc một phương thức (như hàm `printArray` trong `GenericDemo1.java`), nó có thể hoạt động hoàn hảo với mảng `String`, mảng `Integer`, mảng `Double`... mà không cần phải viết lại nhiều lần.

---

### 4. Ký hiệu và cú pháp cơ bản

Khi học Generics, bạn sẽ thường thấy các chữ cái trong dấu ngoặc nhọn `< >`. Đây là các quy ước đặt tên (Type Parameter) phổ biến:
*   `<T>`: Type (Kiểu dữ liệu chung chung).
*   `<E>`: Element (Dùng trong các Collection như List, Set).
*   `<K, V>`: Key và Value (Dùng trong Map).
*   `<N>`: Number (Số).

**Cú pháp:**
*   **Generic Class:** `class TênLớp<T> { ... }` (Ví dụ: `GenericDemo3.java`)
*   **Generic Method:** `public <T> void tenHam(T thamSo) { ... }` (Ví dụ: `GenericDemo1.java`)
*   **Diamond Operator (`<>`):** Viết tắt khi khởi tạo: `List<String> list = new ArrayList<>();`

**Tóm lại:** Generics không làm cho chương trình của bạn chạy nhanh hơn, nhưng nó làm cho code của bạn **an toàn hơn, sạch hơn và dễ bảo trì hơn** rất nhiều. Nó là tiêu chuẩn bắt buộc phải dùng khi làm việc với Java hiện đại.