Chào bạn, chúng ta đã đi được nửa chặng đường của SOLID. Tiếp theo là nguyên lý thứ tư: **Chữ I - Interface Segregation Principle (Nguyên lý Phân tách Interface)**.

Đây là nguyên lý dễ hiểu nhất nhưng lại rất hay bị vi phạm, đặc biệt khi các lập trình viên có xu hướng "gộp" mọi thứ vào một Interface cho tiện.

---

### 1. ĐỊNH NGHĨA

> *"Không nên ép các lớp (client) phải phụ thuộc vào những Interface mà chúng không sử dụng. Thay vì dùng một Interface lớn (Fat Interface), hãy chia nhỏ thành nhiều Interface chuyên biệt."*

**Nói một cách dễ hiểu:** 
Đừng tạo ra một Interface "mập mạp" chứa hàng chục phương thức. Nếu một class chỉ cần dùng 2 phương thức trong số đó, nhưng bị bắt buộc phải implement (triển khai) cả 10 phương thức (trong đó có 8 phương thức rỗng hoặc ném ra ngoại lệ), thì Interface đó đã vi phạm nguyên lý này. 

Hãy chia nhỏ Interface thành các "hợp đồng" nhỏ hơn để class nào cần gì thì "ký" (implement) nấy.

---

### 2. VÍ DỤ: VI PHẠM ISP (Bad Example)

Hãy tưởng tượng bạn thiết kế một hệ thống cho các thiết bị văn phòng. Bạn tạo ra một Interface tên là `MultiFunctionDevice` (Thiết bị đa năng).

```java
// ❌ INTERFACE QUÁ "MẬP" (Fat Interface)
public interface MultiFunctionDevice {
    void print();
    void scan();
    void fax();
    void staple(); // Đóng ghim tài liệu
}
```

Bây giờ, bạn cần tạo 2 class đại diện cho 2 loại máy khác nhau:

```java
// Máy in đời mới, đa năng: Làm được hết
public class AllInOnePrinter implements MultiFunctionDevice {
    public void print() { System.out.println("Printing..."); }
    public void scan() { System.out.println("Scanning..."); }
    public void fax() { System.out.println("Faxing..."); }
    public void staple() { System.out.println("Stapling..."); }
}

// Máy in cũ (chỉ biết in đen trắng)
public class BasicPrinter implements MultiFunctionDevice {
    public void print() { System.out.println("Printing..."); }
    
    // Phải implement nhưng không dùng được
    public void scan() { throw new UnsupportedOperationException(); }
    public void fax() { throw new UnsupportedOperationException(); }
    public void staple() { throw new UnsupportedOperationException(); }
}
```
*   **Vấn đề ở đâu?** 
    *   `BasicPrinter` bị ép buộc phải viết code cho các phương thức nó **không hề sử dụng** (`scan`, `fax`, `staple`). 
    *   Việc ném ra `UnsupportedOperationException` là một "mùi hôi" (code smell) cho thấy thiết kế Interface đang sai.
    *   Nếu sau này bạn thêm hàm `doubleSidedPrint()` vào `MultiFunctionDevice`, bạn lại phải sửa lại tất cả các class con, kể cả `BasicPrinter`.

---

### 3. VÍ DỤ: TUÂN THỦ ISP (Good Example)

Cách khắc phục là chia nhỏ Interface "mập" thành các Interface chuyên biệt, mỗi Interface chỉ đảm nhận một chức năng (tuân thủ luôn cả SRP).

```java
// ✅ CHIA NHỎ THÀNH CÁC INTERFACE CHUYÊN BIỆT
public interface Printer {
    void print();
}

public interface Scanner {
    void scan();
}

public interface Fax {
    void fax();
}

public interface Stapler {
    void staple();
}
```

Bây giờ, các class sẽ chỉ implement những gì chúng thực sự cần:

```java
// Máy in cũ: Chỉ ký hợp đồng Printer
public class BasicPrinter implements Printer {
    public void print() { System.out.println("Printing..."); }
}

// Máy đa năng: Ký nhiều hợp đồng cùng lúc
public class AllInOnePrinter implements Printer, Scanner, Fax, Stapler {
    public void print() { System.out.println("Printing..."); }
    public void scan() { System.out.println("Scanning..."); }
    public void fax() { System.out.println("Faxing..."); }
    public void staple() { System.out.println("Stapling..."); }
}
```
*   **Giải thích bên cạnh:** 
    *   `BasicPrinter` giờ đây cực kỳ gọn gàng, không còn các hàm rỗng hay ngoại lệ vô nghĩa.
    *   `AllInOnePrinter` vẫn có thể làm mọi thứ bằng cách implement nhiều Interface (đây là sức mạnh của Interface trong Java mà chúng ta đã học).
    *   Code tuân thủ nguyên lý **Đơn trách nhiệm (SRP)** và **Phân tách Interface (ISP)**.

---

### 4. MỘT VÍ DỤ THỰC TẾ KHÁC (Người và Robot)

Giả sử bạn có hệ thống quản lý nhân sự.

**Vi phạm ISP:**
```java
interface IWorker {
    void work();
    void eat();
    void sleep();
}
```
*   Nếu bạn tạo class `Robot implements IWorker`, robot không cần ăn và ngủ, nhưng nó buộc phải implement `eat()` và `sleep()` (dẫn đến code rỗng hoặc ném lỗi).

**Tuân thủ ISP:**
```java
interface IWorkable { void work(); }
interface IFeedable { void eat(); }
interface ISleepable { void sleep(); }

class Human implements IWorkable, IFeedable, ISleepable { ... }
class Robot implements IWorkable { ... } // Robot chỉ cần làm việc
```

---

### 5. DẤU HIỆU NHẬN BIẾT VI PHẠM ISP

1.  **Interface có quá nhiều phương thức** (từ 10 phương thức trở lên) phục vụ cho nhiều mục đích khác nhau.
2.  **Bạn thấy mình đang viết các hàm rỗng `{ }`** trong class implement Interface.
3.  **Bạn thấy mình đang ném ngoại lệ `throw new UnsupportedOperationException()`** trong các hàm của class con vì class con không hỗ trợ tính năng đó.
4.  Tên của Interface quá chung chung, ví dụ: `IAnimal`, `IWorker`, `IShape` chứa hàng chục method khác nhau.

**Tóm lại:** 
Interface Segregation Principle nhắc nhở chúng ta rằng: **"Thà nhiều Interface nhỏ còn hơn một Interface lớn"**. Hãy thiết kế các Interface giống như một menu ăn uống: Người ăn chay chỉ nên gọi món chay, đừng bắt họ phải trả tiền cho món bò bít tết mà họ không ăn. Lớp nào cần gì thì implement nấy, giúp code linh hoạt, sạch sẽ và dễ bảo trì hơn rất nhiều.