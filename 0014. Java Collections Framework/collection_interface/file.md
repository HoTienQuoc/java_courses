### 1. Tổng quan: Collection là gì? (Dựa trên ảnh 2)

**Lý thuyết:**
*   Hãy nhìn vào ảnh chiếc túi xách (Tote bag). Trong Java, **Collection** chính là một chiếc túi như vậy.
*   Nó là một **Interface** (Giao diện) đại diện cho một nhóm các đối tượng (Objects).
*   Chiếc túi này không quan tâm bạn bỏ vào đó cái gì (hình người, xe đẩy, hộp quà). Nó chỉ cung cấp không gian để **lưu trữ** và các phương thức để **thêm, xóa, tìm kiếm, đếm** các đồ vật bên trong.
*   *Lưu ý:* Vì `Collection` là Interface, bạn **không thể** viết `Collection c = new Collection();`. Bạn phải dùng các lớp triển khai (Implementations) của nó như `ArrayList`, `HashSet`, `LinkedList`, v.v.

---

### 2. Sơ đồ UML và Mối quan hệ (Dựa trên ảnh 3)

**Lý thuyết:**
*   Nhìn vào sơ đồ bên trái ảnh 3:
    *   `<<interface>> Iterable<T>`: Đây là giao diện cha cao nhất. Nó cho phép bạn dùng vòng lặp `for-each` để duyệt qua các phần tử. Nó có phương thức `iterator()`.
    *   `<<interface>> Collection<E>`: Kế thừa từ `Iterable<T>`.
    *   Ký tự `<T>` và `<E>` là **Generics** (Lập trình tổng quát), đại diện cho kiểu dữ liệu mà Collection sẽ chứa (ví dụ: `Collection<String>` chỉ chứa chuỗi).

```java
import java.util.Collection;
import java.util.ArrayList;

public class CollectionDemo {
    public static void main(String[] args) {
        // Khởi tạo một Collection chứa các Chuỗi (String) bằng lớp ArrayList
        Collection<String> shoppingBag = new ArrayList<>();
        System.out.println("Đã khởi tạo chiếc túi rỗng!");
    }
}
```

---

### 3. Các phương thức Abstract (Abstract Methods) - Danh sách Methods list (Ảnh 3)

Đây là các phương thức cốt lõi. Mình chia thành các nhóm để bạn dễ học và kèm theo code ví dụ.

#### Nhóm 1: Thêm, xóa và kiểm tra cơ bản (Dành cho 1 phần tử)
*   `add(E element)`: Thêm 1 phần tử vào túi.
*   `remove(Object o)`: Xóa 1 phần tử.
*   `contains(Object o)`: Kiểm tra xem món đồ đó có trong túi không.
*   `size()`: Đếm số lượng đồ vật trong túi.
*   `isEmpty()`: Kiểm tra túi có rỗng không.

```java
shoppingBag.add("Táo");
shoppingBag.add("Cam");
shoppingBag.add("Sữa");

System.out.println("Số lượng: " + shoppingBag.size()); // Kết quả: 3
System.out.println("Có Táo không? " + shoppingBag.contains("Táo")); // Kết quả: true

shoppingBag.remove("Cam");
System.out.println("Sau khi xóa Cam: " + shoppingBag); // Kết quả: [Táo, Sữa]
System.out.println("Túi có rỗng không? " + shoppingBag.isEmpty()); // Kết quả: false
```

#### Nhóm 2: Thao tác hàng loạt (Bulk Operations - Dành cho 1 Collection khác)
*   `addAll(Collection c)`: Thêm toàn bộ túi khác vào túi hiện tại.
*   `removeAll(Collection c)`: Xóa tất cả các phần tử trùng với túi truyền vào.
*   `retainAll(Collection c)`: **Giữ lại** các phần tử trùng với túi truyền vào (giao của 2 tập hợp).
*   `containsAll(Collection c)`: Kiểm tra xem túi hiện tại có chứa *tất cả* các món của túi kia không.
*   `clear()`: Đổ bỏ toàn bộ đồ trong túi.

```java
Collection<String> bag2 = new ArrayList<>();
bag2.add("Bánh mì");
bag2.add("Sữa");

// Thêm toàn bộ bag2 vào shoppingBag
shoppingBag.addAll(bag2); 
System.out.println(shoppingBag); // Kết quả: [Táo, Sữa, Bánh mì, Sữa]

// Giữ lại những món CÓ TRONG bag2 (Tức là xóa Táo, Sữa bị trùng thì giữ lại 1)
shoppingBag.retainAll(bag2);
System.out.println(shoppingBag); // Kết quả: [Sữa, Bánh mì, Sữa]

// Xóa sạch
shoppingBag.clear();
System.out.println("Đã clear: " + shoppingBag.isEmpty()); // Kết quả: true
```

#### Nhóm 3: Chuyển đổi và Tiện ích
*   `toArray()`: Chuyển Collection thành mảng Array thông thường.
*   `equals(Object o)` / `hashCode()`: Dùng để so sánh 2 Collection với nhau.
*   `iterator()`: Trả về một đối tượng Iterator để duyệt từng phần tử (thường dùng trong các vòng lặp while).

```java
shoppingBag.add("Nước ngọt");
shoppingBag.add("Kẹo");

// Chuyển sang mảng
Object[] arrayItems = shoppingBag.toArray();
System.out.println("Phần tử đầu tiên trong mảng: " + arrayItems[0]); // Nước ngọt
```

---

### 4. Phương thức Default (Default Methods) - Mục số 4 trong Agenda (Ảnh 1)

Từ Java 8 trở đi, `Collection` Interface được bổ sung các **Default Methods** (phương thức có sẵn phần thân code, không bắt buộc các lớp con phải viết lại). Ảnh 3 không liệt kê nhưng đây là phần cực kỳ quan trọng bạn sẽ học:

*   `removeIf(Predicate)`: Xóa các phần tử thỏa mãn điều kiện.
*   `forEach(Consumer)`: Duyệt qua các phần tử (thay cho vòng lặp for).
*   `stream()`: Chuyển thành Stream để xử lý dữ liệu (lọc, map, v.v.).

```java
Collection<Integer> numbers = new ArrayList<>();
numbers.add(10);
numbers.add(15);
numbers.add(20);
numbers.add(25);

// Sử dụng Default Method: removeIf để xóa các số lớn hơn 18
numbers.removeIf(n -> n > 18);
System.out.println(numbers); // Kết quả: [10, 15]

// Sử dụng Default Method: forEach để in ra các số còn lại
numbers.forEach(n -> System.out.println("Số còn lại: " + n));
```

---

### Tóm lại (Source code)
Thay vì phải tự viết code để quản lý một mảng động, tự viết hàm thêm, xóa, tìm kiếm, bạn chỉ cần sử dụng `Collection`. 
Khi lập trình thực tế, người ta thường khai báo kiểu `Collection` (hoặc `List`, `Set`) ở bên trái, và khởi tạo bằng một lớp cụ thể (như `ArrayList`) ở bên phải. Điều này giúp code của bạn linh hoạt, dễ bảo trì (Interface-based programming).