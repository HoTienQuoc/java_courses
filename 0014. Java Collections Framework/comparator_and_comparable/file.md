Chào bạn, dựa trên 2 bức ảnh bạn cung cấp, bài học này đang đề cập đến một trong những chủ đề "đau đầu" nhưng cực kỳ quan trọng của Java: **Sắp xếp dữ liệu (Sorting) với `Comparable` và `Comparator`**, đi kèm với thuật toán **Quick Sort**.

Trong Java, khi bạn có một danh sách các số (1, 5, 3) hay chuỗi ("A", "C", "B"), Java tự biết cách sắp xếp vì chúng đã có sẵn quy tắc. Nhưng nếu bạn có một danh sách các đối tượng (ví dụ: `SinhVien`, `SanPham`, `NhanVien`), **Java không biết phải sắp xếp theo tiêu chí nào** (theo tên? theo tuổi? theo giá tiền?). Lúc này, chúng ta cần dùng đến `Comparable` hoặc `Comparator`.

Dưới đây là phần giải thích chi tiết kết hợp code ví dụ.

---

### 1. Interface `Comparable` (Sắp xếp mặc định - Natural Ordering)

**Lý thuyết:**
*   `Comparable` được dùng khi đối tượng của bạn có một **thứ tự sắp xếp tự nhiên, mặc định**. (Ví dụ: Sinh viên mặc định sắp xếp theo Mã Sinh Viên).
*   Lớp của bạn phải `implements Comparable<T>`.
*   Bạn phải ghi đè (override) phương thức **`compareTo(T o)`**.

**Quy tắc hoạt động của `compareTo`:** Trả về một số nguyên (`int`).
*   Trả về **số âm (< 0)**: Đối tượng hiện tại **nhỏ hơn** đối tượng truyền vào (đứng trước).
*   Trả về **số 0**: Hai đối tượng **bằng nhau**.
*   Trả về **số dương (> 0)**: Đối tượng hiện tại **lớn hơn** đối tượng truyền vào (đứng sau).

**Code Ví dụ:**
Giả sử chúng ta có lớp `SinhVien` và muốn sắp xếp mặc định theo `ID` tăng dần.

```java
import java.util.*;

class SinhVien implements Comparable<SinhVien> {
    private int id;
    private String ten;

    public SinhVien(int id, String ten) {
        this.id = id;
        this.ten = ten;
    }

    public int getId() { return id; }

    // Ghi đè phương thức compareTo
    @Override
    public int compareTo(SinhVien svKhac) {
        // Cách viết ngắn gọn: So sánh ID của mình với ID của người ta
        return this.id - svKhac.getId(); 
        
        // Hoặc dùng cách an toàn hơn (tránh tràn số):
        // return Integer.compare(this.id, svKhac.getId());
    }

    @Override
    public String toString() { return "SV(" + id + ", " + ten + ")"; }
}

public class ComparableDemo {
    public static void main(String[] args) {
        List<SinhVien> list = new ArrayList<>();
        list.add(new SinhVien(3, "An"));
        list.add(new SinhVien(1, "Bình"));
        list.add(new SinhVien(2, "Cường"));

        // Chỉ cần gọi sort(), Java sẽ tự dùng compareTo để sắp xếp
        Collections.sort(list); 
        System.out.println(list); // [SV(1, Bình), SV(2, Cường), SV(3, An)]
    }
}
```

---

### 2. Interface `Comparator` (Sắp xếp linh hoạt - Custom Ordering)

**Lý thuyết:**
*   `Comparator` được dùng khi bạn muốn sắp xếp đối tượng theo **nhiều tiêu chí khác nhau**, hoặc bạn **không thể sửa code** của lớp đó (ví dụ: lớp có sẵn trong thư viện Java, hoặc do người khác viết).
*   Bạn tạo một lớp riêng (hoặc dùng Lambda) `implements Comparator<T>`.
*   Bạn phải ghi đè phương thức **`compare(T o1, T o2)`**.
*   **Quy tắc tương tự `compareTo`:** Trả về âm (o1 đứng trước o2), 0 (bằng nhau), dương (o1 đứng sau o2).

**Code Ví dụ:**
Vẫn là lớp `SinhVien` trên, nhưng giờ sếp yêu cầu sắp xếp **theo Tên (A-Z)** thay vì theo ID.

```java
import java.util.*;

public class ComparatorDemo {
    public static void main(String[] args) {
        List<SinhVien> list = new ArrayList<>();
        list.add(new SinhVien(3, "An"));
        list.add(new SinhVien(1, "Bình"));
        list.add(new SinhVien(2, "Cường"));

        // Cách 1: Dùng Anonymous Inner Class (Cách cũ)
        Comparator<SinhVien> theoTen = new Comparator<SinhVien>() {
            @Override
            public int compare(SinhVien sv1, SinhVien sv2) {
                return sv1.getTen().compareTo(sv2.getTen());
            }
        };
        list.sort(theoTen);

        // Cách 2: Dùng Lambda Expression (Cách hiện đại - Khuyên dùng)
        list.sort((sv1, sv2) -> sv1.getTen().compareTo(sv2.getTen()));
        
        // Cách 3: Dùng hàm có sẵn của Comparator (Cực gọn)
        list.sort(Comparator.comparing(SinhVien::getTen));

        System.out.println(list); // [SV(3, An), SV(1, Bình), SV(2, Cường)]
    }
}
```

---

### 3. Sự khác biệt giữa `compareTo` và `compare` (Trọng tâm Agenda)

| Tiêu chí | `Comparable` | `Comparator` |
| :--- | :--- | :--- |
| **Phương thức** | `compareTo(Object o)` | `compare(Object o1, Object o2)` |
| **Số lượng tham số** | 1 (So sánh chính nó với đối tượng khác) | 2 (So sánh 2 đối tượng độc lập) |
| **Vị trí code** | Viết **bên trong** lớp đối tượng (VD: trong class `SinhVien`) | Viết **bên ngoài** lớp đối tượng (thường dùng Lambda) |
| **Tính linh hoạt** | Kém. Chỉ có **1 cách** sắp xếp mặc định duy nhất. | Cao. Có thể tạo **nhiều Comparator** (theo tên, theo tuổi, theo lương...). |
| **Khi nào dùng?** | Khi muốn định nghĩa thứ tự mặc định cho lớp do mình tạo ra. | Khi muốn sắp xếp theo tiêu chí phụ, hoặc sắp xếp lớp không sửa được code. |

*Lưu ý: Nếu một lớp đã `implements Comparable`, bạn vẫn có thể dùng `Comparator` để sắp xếp theo tiêu chí khác. `Comparator` sẽ được ưu tiên sử dụng nếu được truyền vào hàm `sort()`.*

---

### 4. Thuật toán Quick Sort (Dựa trên ảnh 2)

Bức ảnh thứ 2 minh họa thuật toán **Quick Sort (Sắp xếp nhanh)**. Đây là thuật toán mặc định mà Java sử dụng bên dưới hàm `Collections.sort()` hoặc `Arrays.sort()` (cụ thể là Dual-Pivot QuickSort) để sắp xếp các kiểu dữ liệu nguyên thủy và TimSort cho Objects.

**Cách thuật toán hoạt động (Giải thích theo hình):**
1.  **Chọn Pivot (Chốt):** Chọn một phần tử làm mốc (trong ảnh là số **31** màu vàng).
2.  **Phân hoạch (Partitioning):**
    *   Mục tiêu: Đưa tất cả các số **nhỏ hơn Pivot** (31) sang bên trái, và các số **lớn hơn Pivot** sang bên phải.
    *   Dùng 2 con trỏ: `lo` (bắt đầu từ trái) và `hi` (bắt đầu từ phải, ngay trước Pivot).
    *   `lo` di chuyển sang phải cho đến khi tìm thấy số **lớn hơn** Pivot (gặp số 33).
    *   `hi` di chuyển sang trái cho đến khi tìm thấy số **nhỏ hơn** Pivot (gặp số 27).
    *   Đổi chỗ 2 số này cho nhau. Lặp lại đến khi `lo` vượt qua `hi`.
    *   Cuối cùng, đưa Pivot vào đúng vị trí ở giữa.
3.  **Đệ quy:** Chia mảng làm 2 nửa (bên trái Pivot và bên phải Pivot). Tiếp tục gọi lại thuật toán Quick Sort cho từng nửa.

**Liên hệ với bài học:**
Để thuật toán Quick Sort biết được số nào "nhỏ hơn" hay "lớn hơn" Pivot, nó cần sử dụng các phương thức **`compareTo`** hoặc **`compare`** mà chúng ta vừa học. Nếu không có chúng, thuật toán sẽ không biết cách sắp xếp các đối tượng phức tạp.

---
**Tóm lại:**
*   Dùng **`Comparable`** khi bạn muốn định nghĩa "thứ tự gốc" cho đối tượng (VD: Sản phẩm sắp xếp mặc định theo ID).
*   Dùng **`Comparator`** khi bạn muốn sắp xếp linh hoạt theo nhiều góc độ khác nhau (VD: Lúc thì sắp xếp Sản phẩm theo giá tăng dần, lúc thì theo tên A-Z, lúc thì theo số lượng tồn kho). Trong thực tế làm việc, **`Comparator` được sử dụng nhiều hơn rất nhiều** nhờ sự linh hoạt và cú pháp Lambda gọn gàng.