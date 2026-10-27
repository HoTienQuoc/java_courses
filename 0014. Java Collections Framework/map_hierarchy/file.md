Chào bạn, dựa trên 3 bức ảnh bạn cung cấp, bài học này đánh dấu một bước ngoặt quan trọng: Chúng ta rời khỏi gia đình `Collection` (List, Set, Queue) để bước sang một hệ thống hoàn toàn mới: **Map (Bản đồ ánh xạ)**.

Đây là cấu trúc dữ liệu được sử dụng nhiều bậc nhất trong lập trình thực tế (đặc biệt là khi làm việc với Database, JSON, Cache...). Hãy cùng bóc tách chi tiết nhé.

---

### 1. Tổng quan về `Map` (Giao diện gốc)

**Lý thuyết:**
*   **Điểm khác biệt sống còn:** `Map` **KHÔNG** phải là con của `Collection`. Nó là một hệ thống phân cấp riêng biệt.
*   **Cấu trúc dữ liệu:** `Collection` lưu trữ từng phần tử đơn lẻ (như một danh sách). Còn `Map` lưu trữ dữ liệu theo dạng **Cặp Khóa - Giá trị (Key - Value)**. 
*   **Ẩn dụ:** Giống như một cuốn từ điển. `Key` là từ cần tra cứu (không được trùng lặp), `Value` là ý nghĩa của từ đó.
*   **Đặc điểm:** 
    *   **Khóa (Key) là duy nhất:** Không thể có 2 Key giống hệt nhau trong cùng một Map. Nếu bạn `put` một Key đã tồn tại, Value cũ sẽ bị ghi đè.
    *   **Giá trị (Value) có thể trùng lặp:** Nhiều Key khác nhau có thể trỏ đến cùng một Value.

---

### 2. Hệ thống phân cấp (Map Hierarchy - Ảnh 2)

Dựa vào sơ đồ UML trong ảnh 2, đây là các nhánh chính:

*   **`Map` (Interface gốc):** Định nghĩa các quy tắc cơ bản.
*   **`SortedMap` (Interface):** Cam kết các cặp Key-Value sẽ được **sắp xếp theo thứ tự của Key** (tăng dần hoặc theo Comparator).
*   **`NavigableMap` (Interface):** Mở rộng của `SortedMap`, cung cấp thêm các phương thức tìm kiếm nâng cao (ví dụ: tìm Key nhỏ nhất lớn hơn một giá trị cho trước).
*   **`ConcurrentMap` (Interface):** Thiết kế để an toàn trong môi trường đa luồng (Multithreading).

**Các Lớp triển khai (Implementations) phổ biến:**
1.  **`HashMap`:** Vua của mọi loại Map. Không sắp xếp thứ tự, hiệu năng cực nhanh (O(1)). Cho phép 1 Key `null` và nhiều Value `null`.
2.  **`LinkedHashMap`:** Kế thừa từ `HashMap`, nhưng **giữ nguyên thứ tự chèn** vào. Dùng khi cần Map nhưng muốn duyệt theo thứ tự đã thêm.
3.  **`TreeMap`:** Triển khai của `NavigableMap`. Tự động sắp xếp các Key theo thứ tự. Không cho phép Key `null`.
4.  **`ConcurrentHashMap`:** Bản nâng cấp của `Hashtable` dùng cho đa luồng. Hiệu năng chia khóa (segment locking) cực tốt.
5.  **`Dictionary` & `Hashtable`:** Cổ điển, có từ Java 1.0. `Hashtable` là thread-safe nhưng chậm. `Dictionary` đã bị "deprecated" (không khuyến khích dùng nữa).

---

### 3. Các phương thức cơ bản của Map và `Map.Entry` (Từ Agenda)

**Các hàm thường dùng:**
*   `put(K key, V value)`: Thêm cặp Key-Value.
*   `get(Object key)`: Lấy Value dựa vào Key (trả về `null` nếu không thấy).
*   `containsKey(Object key)`: Kiểm tra Key có tồn tại không.
*   `remove(Object key)`: Xóa cặp Key-Value.
*   `keySet()`: Trả về một `Set` chứa tất cả các Key.
*   `values()`: Trả về một `Collection` chứa tất cả các Value.

**`Entry` là gì?**
Bên trong mỗi Map là các đối tượng `Map.Entry`. Nó đại diện cho chính cái cặp (Key - Value) đó. Khi duyệt Map, người ta thường dùng `entrySet()` để duyệt qua từng `Entry`, giúp lấy được cả Key và Value cùng lúc (hiệu năng tốt hơn là gọi `get(key)` trong vòng lặp).

---

### 4. Hashtable VS HashMap (Ảnh 3)

Bảng so sánh này là câu hỏi phỏng vấn kinh điển:

| Tiêu chí | `Hashtable` (Cổ điển) | `HashMap` (Hiện đại) |
| :--- | :--- | :--- |
| **Đồng bộ (Synchronized)** | **Có** (Thread-safe, nhưng chậm vì khóa toàn bộ Map) | **Không** (Nhanh hơn nhiều, dùng tốt trong 1 luồng) |
| **Khóa null (Null Keys)** | **Cấm** (Ném lỗi NullPointerException) | **Cho phép 1** Key null |
| **Giá trị null (Null Values)**| **Cấm** | **Cho phép** nhiều Value null |
| **Ra đời** | Từ Java 1 (Legacy) | Từ Java 2 |

*Lời khuyên:* Ngày nay, hầu như không ai dùng `Hashtable`. Nếu cần an toàn đa luồng, hãy dùng `ConcurrentHashMap`. Nếu không cần, hãy dùng `HashMap`.

---

### 5. Code Ví dụ Tổng hợp

Dưới đây là code minh họa đầy đủ từ cách dùng cơ bản, duyệt Map bằng Entry, đến việc chứng minh bảng so sánh Hashtable vs HashMap.

```java
import java.util.*;

public class MapDemo {
    public static void main(String[] args) {
        // --- 1. SỬ DỤNG HASHMAP CƠ BẢN ---
        Map<String, Integer> tuDien = new HashMap<>();
        
        tuDien.put("Táo", 10);       // Key: Táo, Value: 10
        tuDien.put("Cam", 5);
        tuDien.put("Chuối", 20);
        tuDien.put("Táo", 15);       // GHI ĐÈ Value của "Táo" từ 10 thành 15
        
        System.out.println("Giá của Táo: " + tuDien.get("Táo")); // 15
        System.out.println("Có nho không? " + tuDien.containsKey("Nho")); // false

        // --- 2. DUYỆT MAP BẰNG Map.Entry ---
        System.out.println("\nDuyệt Map bằng Entry:");
        for (Map.Entry<String, Integer> entry : tuDien.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " - Value: " + entry.getValue());
        }

        // --- 3. SỬ DỤNG TREEMAP (TỰ ĐỘNG SẮP XẾP THEO KEY) ---
        Map<Integer, String> treemap = new TreeMap<>();
        treemap.put(5, "Năm");
        treemap.put(1, "Một");
        treemap.put(3, "Ba");
        System.out.println("\nTreeMap (Tự động sắp xếp Key tăng dần): " + treemap); 
        // Kết quả: {1=Một, 3=Ba, 5=Năm}

        // --- 4. CHỨNG MINH HASHTABLE CẤM NULL ---
        Map<String, String> hashtable = new Hashtable<>();
        try {
            hashtable.put(null, "Giá trị"); // Sẽ văng lỗi ngay lập tức
        } catch (NullPointerException e) {
            System.out.println("\nHashtable không cho phép Key null! (Đúng như lý thuyết)");
        }

        // --- 5. HASHMAP CHO PHÉP NULL ---
        Map<String, String> hashmap = new HashMap<>();
        hashmap.put(null, "Giá trị 1"); 
        hashmap.put("Key", null);
        System.out.println("HashMap với null: " + hashmap); // {null=Giá trị 1, Key=null}
    }
}
```

**Tóm lại:**
Map là cấu trúc lưu trữ Key-Value cực kỳ mạnh mẽ. `HashMap` là lựa chọn mặc định số 1. Nếu cần sắp xếp, dùng `TreeMap`. Nếu cần đa luồng, dùng `ConcurrentHashMap`. Tuyệt đối tránh xa `Hashtable` và `Dictionary` trừ khi bạn đang bảo trì hệ thống Java cổ đại.