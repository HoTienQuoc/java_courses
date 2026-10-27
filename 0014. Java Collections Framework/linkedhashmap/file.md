Chào bạn, 3 bức ảnh này cung cấp một bài học rất thực tế và thú vị về **`LinkedHashMap`**. Nếu `HashMap` nổi tiếng với tốc độ tìm kiếm cực nhanh (nhưng thứ tự lộn xộn), thì `LinkedHashMap` là sự kết hợp hoàn hảo giữa tốc độ của `HashMap` và khả năng **duy trì thứ tự** của một danh sách liên kết.

Điều đặc biệt nhất: `LinkedHashMap` chính là "vũ khí bí mật" để bạn tự xây dựng một bộ nhớ đệm (**LRU Cache**) cực kỳ dễ dàng.

Dưới đây là phần giải thích chi tiết từng bức ảnh, kết hợp lý thuyết và code ví dụ.

---

### 1. Cấu trúc bên trong của `LinkedHashMap` (Dựa trên ảnh 2)

**Lý thuyết:**
*   `LinkedHashMap` kế thừa từ `HashMap`. Do đó, nó vẫn giữ nguyên cấu trúc mảng (Array) và cơ chế băm (hashing) để tìm kiếm với tốc độ **O(1)**.
*   **Điểm khác biệt:** Nó thêm một **Danh sách liên kết đôi (Doubly Linked List)** chạy xuyên qua tất cả các phần tử (entry).
*   Hãy nhìn vào ảnh 2:
    *   Bạn thấy các ô `table` từ `0` đến `14` (đây là mảng của HashMap).
    *   Mỗi `entry` (phần tử) có thêm 2 con trỏ: `before` (trỏ đến phần tử phía trước) và `after` (trỏ đến phần tử phía sau).
    *   Có một node đặc biệt gọi là **`header`** (sentinel node). Nó không chứa dữ liệu thực (key/value đều là `null`), nhưng nó giữ vị trí đầu và cuối của danh sách liên kết để quản lý thứ tự.
*   **Thuộc tính `accessOrder` (Rất quan trọng):**
    *   Mặc định là **`false`**. Nghĩa là danh sách liên kết sẽ giữ thứ tự **lúc bạn thêm (Insertion Order)** vào Map.
    *   Nếu bạn đặt nó thành **`true`**. Thứ tự sẽ thay đổi mỗi khi bạn **truy cập (Access Order)** vào một phần tử.

---

### 2. Insertion-Order vs. Access-Order (Trọng tâm Agenda)

**Code minh họa sự khác biệt:**

```java
import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapDemo {
    public static void main(String[] args) {
        // 1. INSERTION ORDER (Mặc định - accessOrder = false)
        Map<String, Integer> insertionMap = new LinkedHashMap<>();
        insertionMap.put("A", 1);
        insertionMap.put("B", 2);
        insertionMap.put("C", 3);
        
        // Truy cập vào B (get)
        insertionMap.get("B");
        
        System.out.println("Insertion Order: " + insertionMap); 
        // Kết quả: {A=1, B=2, C=3} -> Thứ tự vẫn giữ nguyên như lúc thêm.

        // 2. ACCESS ORDER (accessOrder = true)
        // Cú pháp: new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder)
        Map<String, Integer> accessMap = new LinkedHashMap<>(16, 0.75f, true);
        accessMap.put("A", 1);
        accessMap.put("B", 2);
        accessMap.put("C", 3);
        
        System.out.println("Ban đầu: " + accessMap); // {A=1, B=2, C=3}
        
        // Truy cập vào B (get)
        accessMap.get("B");
        
        System.out.println("Sau khi get B: " + accessMap); 
        // Kết quả: {A=1, C=3, B=2} -> B bị đẩy xuống cuối vì vừa được truy cập!
    }
}
```

---

### 3. Cache là gì và các chính sách thay thế (Ảnh 3)

**Lý thuyết:**
*   **Cache (Bộ nhớ đệm):** Là một vùng lưu trữ dữ liệu có tốc độ truy xuất cực nhanh (thường nằm trong RAM), dùng để lưu lại kết quả của những tính toán tốn kém. Khi cần lại dữ liệu đó, hệ thống lấy từ Cache ra thay vì phải tính toán lại từ đầu.
*   **Vấn đề:** Bộ nhớ Cache là có hạn. Khi đầy, ta phải xóa bớt một phần tử để nhường chỗ cho phần tử mới. Xóa cái nào? Đó là lúc cần đến **Cache Replacement Policies** (Chính sách thay thế):
    *   **FIFO (First-In-First-Out):** Xóa cái nào vào trước thì xóa trước.
    *   **LIFO (Last-In-First-Out):** Xóa cái mới nhất.
    *   **LRU (Least Recently Used):** **Xóa phần tử lâu không được sử dụng nhất**. Đây là chính sách phổ biến và hiệu quả nhất trong thực tế.
    *   **MRU (Most Recently Used):** Xóa phần tử vừa mới được sử dụng.
    *   **LFU (Least Frequently Used):** Xóa phần tử ít được sử dụng nhất (đếm số lần dùng).
    *   **Random:** Xóa ngẫu nhiên.

---

### 4. Cách giới hạn số lượng phần tử & Xây dựng LRU Cache (Homework)

Làm sao để `LinkedHashMap` tự động xóa phần tử cũ khi đạt giới hạn? 
Câu trả lời nằm ở việc ghi đè (Override) phương thức **`removeEldestEntry(Map.Entry eldest)`**.

Mặc định phương thức này trả về `false` (không bao giờ xóa). Nếu bạn trả về `true`, nó sẽ xóa phần tử đầu tiên (phần tử "già nhất" trong danh sách liên kết) mỗi khi có phần tử mới được thêm vào.

**Code thực chiến: Xây dựng bộ nhớ đệm LRU (Least Recently Used) chỉ với vài dòng code!**

```java
import java.util.LinkedHashMap;
import java.util.Map;

// Tạo một class LRUCache kế thừa LinkedHashMap
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        // BẮT BUỘC: accessOrder = true để khi get() nó đẩy phần tử xuống cuối
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // Nếu số lượng phần tử vượt quá sức chứa -> Trả về true để tự động xóa thằng đầu tiên
        return size() > capacity;
    }
}

public class CacheDemo {
    public static void main(String[] args) {
        // Tạo Cache chỉ chứa tối đa 3 phần tử
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        System.out.println("Cache đầy: " + cache); // {A=1, B=2, C=3}

        // Truy cập vào A -> A được đẩy xuống cuối danh sách
        cache.get("A"); 
        
        // Thêm D vào, vì vượt quá capacity (3), nó sẽ xóa thằng "già nhất".
        // Thằng già nhất lúc này là B (vì A vừa được dùng nên xuống cuối, C đứng trước B)
        cache.put("D", 4); 
        
        System.out.println("Sau khi thêm D: " + cache); 
        // Kết quả: {C=3, A=1, D=4} -> B đã bị đá ra ngoài!
    }
}
```

### Tóm lại:
*   Dùng `LinkedHashMap` khi bạn cần một `Map` có **thứ tự** (chèn vào hoặc truy cập).
*   Nó chậm hơn `HashMap` một chút xíu (do phải duy trì con trỏ `before`/`after`), nhưng đổi lại tính năng cực kỳ hữu ích.
*   Thuộc tính `accessOrder = true` kết hợp với `removeEldestEntry` là công thức hoàn hảo để tạo ra **LRU Cache** (Bộ nhớ đệm loại bỏ phần tử lâu không dùng) chỉ trong vài dòng code.