# Java Collections Framework: Hệ thống hóa từ hợp đồng interface đến việc lựa chọn implementation

Java Collections Framework (JCF) không phải là một tập hợp các lớp tiện ích rời rạc, mà là một hệ thống được xây dựng quanh nguyên tắc **tách biệt interface và implementation**: interface định nghĩa “có thể làm gì”, còn lớp implementation quyết định “làm với chi phí nào, trong ngữ cảnh nào”. Điểm mấu chốt để hiểu framework này không phải là ghi nhớ tất cả tên lớp, mà là nắm được **dưới cùng một hợp đồng hành vi, các cấu trúc dữ liệu khác nhau đánh đổi giữa thời gian và không gian như thế nào**.

---

## 1. Cấu trúc tổng thể: hai interface gốc, bốn loại hợp đồng cơ bản

Thiết kế cấp cao nhất của JCF xoay quanh hai interface gốc: `Collection` và `Map`. `Collection` là tập hợp các phần tử đơn, `Map` là ánh xạ khóa–giá trị; hai interface này không có quan hệ kế thừa với nhau.

Từ góc độ hợp đồng, bên dưới `Collection` tiếp tục được chia thành bốn kiểu cơ bản:

```
Iterable
└── Collection
    ├── List      Có thứ tự, cho phép trùng lặp, có chỉ số
    ├── Set       Không trùng lặp (ngữ nghĩa thứ tự do implementation quyết định)
    ├── Queue     Hàng đợi, thường là FIFO
    │   └── Deque Hàng đợi hai đầu
    └── (SortedSet, NavigableSet là phần mở rộng sắp xếp của Set)

Map (không kế thừa Collection)
├── HashMap
├── LinkedHashMap
├── TreeMap (SortedMap / NavigableMap)
└── Hashtable (legacy, đồng bộ)
```

> **Điểm thiết kế quan trọng**: `Collection` kế thừa `Iterable`, nghĩa là mọi collection trong framework đều mặc nhiên hỗ trợ vòng lặp for tăng cường và duyệt bằng `forEach`. `Map` tuy không kế thừa `Collection`, nhưng các view do `keySet()`, `values()`, `entrySet()` trả về đều implements `Collection`, nên cách duyệt tương thống nhất với collection.

---

## 2. List: hai logic nền tảng của một dãy có thứ tự

Hợp đồng cốt lõi của `List` là: **có thứ tự, cho phép trùng lặp, hỗ trợ truy cập theo chỉ số**. Nhưng “hỗ trợ chỉ số” và “truy cập theo chỉ số hiệu quả” là hai chuyện khác nhau — đây chính là điểm phân biệt giữa `ArrayList` và `LinkedList`.

### 2.1 ArrayList: mảng động

`ArrayList` bên dưới là một mảng `Object[]`. Dung lượng mặc định là 10; khi mở rộng, nó tăng khoảng **1.5 lần** và sao chép sang mảng mới bằng `Arrays.copyOf`.

```java
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add(0, "C");          // Chèn tại chỉ số 0, cần dịch các phần tử phía sau
String s = list.get(1);     // Truy cập ngẫu nhiên O(1)
list.remove(0);             // Sau khi xóa, các phần tử phía sau dịch lên, O(n)
```

**Mô hình chi phí**:
- `get(int index)` → O(1)
- `add(E)` ở cuối → O(1) khấu hao
- `add(int, E)` / `remove(int)` ở giữa → O(n), vì phải dịch phần tử
- `contains(Object)` → O(n)

**Ngữ cảnh phù hợp**: đọc nhiều ghi ít, chủ yếu truy cập ngẫu nhiên và thêm vào cuối.

### 2.2 LinkedList: danh sách liên kết đôi

Mỗi node của `LinkedList` lưu ba trường `prev`, `item`, `next`, đồng thời implements cả `List` và `Deque`.

```java
LinkedList<String> linked = new LinkedList<>();
linked.addFirst("A");       // O(1)
linked.addLast("B");        // O(1)
String first = linked.getFirst();  // O(1)
String mid = linked.get(1);         // O(n): phải duyệt từ đầu hoặc cuối
linked.removeFirst();               // O(1)
```

**Mô hình chi phí**:
- Chèn/xóa ở đầu/cuối → O(1)
- Truy cập theo chỉ số `get(int)` / `set(int)` → O(n)
- Chèn/xóa gần một node đã được định vị → O(1), nhưng bản thân việc định vị có thể là O(n)

**Ngữ cảnh phù hợp**: thường xuyên chèn/xóa ở hai đầu hoặc tại vị trí đã biết, ít có nhu cầu truy cập ngẫu nhiên.

> **Một hiểu nhầm phổ biến**: “`LinkedList` thêm/xóa nhanh” chỉ đúng khi **vị trí chèn/xóa đã được xác định**. Nếu phải định vị qua chỉ số, bản thân việc định vị đã là O(n), nên chi phí tổng hợp không tốt hơn `ArrayList`. Cả hai **đều không thread-safe**; trong môi trường đa luồng cần đồng bộ bên ngoài hoặc dùng lớp thay thế concurrent.

---

## 3. Set: ba con đường hiện thực hóa tính không trùng lặp

Hợp đồng của `Set` là **các phần tử không trùng lặp**. Nhưng “không trùng lặp” được xác định như thế nào và thứ tự duyệt do đâu quyết định lại phụ thuộc hoàn toàn vào cấu trúc bên dưới.

### 3.1 HashSet: bảng băm

`HashSet` bên trong ủy quyền cho `HashMap`, phần tử được lưu như khóa, còn giá trị là một object giữ chỗ. **Thứ tự duyệt không được đảm bảo**, cho phép `null`. `add` trả về `false` nghĩa là phần tử đã tồn tại.

```java
Set<String> set = new HashSet<>();
set.add("Java");     // true
set.add("Python");   // true
set.add("Java");     // false, trùng lặp, bị bỏ qua
```

**Điều kiện tiên quyết**: object tự định nghĩa khi đưa vào `HashSet` bắt buộc phải override đúng `hashCode()` và `equals()`, nếu không ngữ nghĩa loại trùng sẽ mất hiệu lực. Tìm kiếm, thêm, xóa có độ phức tạp trung bình O(1).

### 3.2 LinkedHashSet: bảng băm + danh sách liên kết đôi

`LinkedHashSet` trên nền bảng băm có thêm danh sách liên kết đôi ghi lại **thứ tự chèn**. Thứ tự duyệt trùng với thứ tự chèn. Cái giá là mỗi phần tử phải duy trì thêm hai con trỏ liên kết, không gian tốn hơn một chút, nhưng thứ tự duyệt có thể dự đoán được.

```java
Set<String> orderedSet = new LinkedHashSet<>();
orderedSet.add("C");
orderedSet.add("A");
orderedSet.add("B");
// Thứ tự duyệt: C, A, B (thứ tự chèn)
```

### 3.3 TreeSet: cây đỏ–đen

`TreeSet` dựa trên cây đỏ–đen, các phần tử ở **trạng thái sắp xếp**, hỗ trợ truy vấn theo khoảng (`headSet`, `subSet`, `tailSet`) và các thao tác có thứ tự.

```java
Set<Integer> treeSet = new TreeSet<>(List.of(5, 1, 3));
// Thứ tự duyệt: 1, 3, 5

// Sắp xếp tùy chỉnh: theo độ dài chuỗi, cùng độ dài thì theo thứ tự từ điển
Set<String> byLength = new TreeSet<>(
    Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())
);
byLength.add("bbb");
byLength.add("a");
byLength.add("cc");
// Thứ tự duyệt: a, cc, bbb
```

**Ràng buộc quan trọng**: phần tử trong `TreeSet` phải implements `Comparable`, hoặc phải truyền `Comparator` khi khởi tạo. Nếu không có cả hai, khi thêm phần tử sẽ ném `ClassCastException`. Tìm kiếm có độ phức tạp **O(log n)**, không hỗ trợ `null` (khi dùng natural ordering).

---

## 4. Map: ba cách hiện thực ánh xạ khóa–giá trị

`Map` lưu **ánh xạ từ khóa đến giá trị**: khóa duy nhất, giá trị có thể trùng lặp, một khóa tương ứng tối đa một giá trị.

### 4.1 HashMap: ánh xạ bảng băm

`HashMap` dựa trên bảng băm, cho phép một khóa `null` và nhiều giá trị `null`. Thứ tự khóa không được đảm bảo. `put` khi khóa đã tồn tại sẽ ghi đè giá trị cũ và trả về giá trị cũ; nếu khóa mới, trả về `null`.

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 90);
scores.put("Bob", 85);
scores.put("Alice", 95);      // Ghi đè, trả về 90
int alice = scores.get("Alice"); // 95
boolean hasBob = scores.containsKey("Bob"); // true
```

Giống như `HashSet`, object tự định nghĩa dùng làm khóa phải implement đúng `hashCode` và `equals`.

### 4.2 LinkedHashMap: giữ thứ tự chèn (hoặc thứ tự truy cập)

`LinkedHashMap` duy trì danh sách liên kết trên nền bảng băm; mặc định thứ tự duyệt là **thứ tự chèn**. Khi truyền `accessOrder = true` lúc khởi tạo, thứ tự duyệt chuyển thành **thứ tự truy cập** (phần tử được truy cập gần nhất ở trước, hoặc phần tử lâu nhất chưa được truy cập tùy cách dùng `removeEldestEntry`), nhờ đó có thể dùng trực tiếp để hiện thực cache LRU.

```java
Map<String, Integer> linkedMap = new LinkedHashMap<>();
linkedMap.put("first", 1);
linkedMap.put("second", 2);
// Thứ tự duyệt: first, second (thứ tự chèn)
```

### 4.3 TreeMap: sắp xếp theo khóa

`TreeMap` dựa trên cây đỏ–đen, sắp xếp theo thứ tự tự nhiên của khóa hoặc theo `Comparator` truyền lúc khởi tạo.

```java
Map<String, Integer> treeMap = new TreeMap<>();
treeMap.put("banana", 2);
treeMap.put("apple", 1);
treeMap.put("cherry", 3);
// Thứ tự duyệt (thứ tự tự nhiên của khóa): apple, banana, cherry
```

**Ngữ cảnh phù hợp**: cần duyệt theo thứ tự khóa hoặc cần tìm kiếm theo khoảng (`subMap`, `headMap`, `tailMap`). Cái giá là thêm/tìm có độ phức tạp O(log n), thay vì O(1) khấu hao như `HashMap`.

---

## 5. Queue và Deque: bổ sung ngữ nghĩa hàng đợi

`Queue` định nghĩa hành vi hàng đợi, `Deque` mở rộng thành hàng đợi hai đầu. `ArrayDeque` là implementation hàng đợi hai đầu hiệu quả dựa trên mảng có thể mở rộng, thường được khuyến nghị thay cho `Stack` và hàng đợi thông thường; `PriorityQueue` là hàng đợi ưu tiên dựa trên heap nhị phân, xuất đội theo độ ưu tiên chứ không theo FIFO.

```java
Deque<String> deque = new ArrayDeque<>();
deque.addFirst("B");
deque.addFirst("A");
deque.addLast("C");
// deque: A, B, C
String first = deque.removeFirst(); // A

Queue<Integer> pq = new PriorityQueue<>();
pq.offer(5);
pq.offer(1);
pq.offer(3);
while (!pq.isEmpty()) {
    System.out.print(pq.poll() + " "); // 1 3 5
}
```

`LinkedList` đồng thời implements `List` và `Deque`, nhưng khi dùng làm hàng đợi, `ArrayDeque` trong đa số trường hợp gọn và nhanh hơn `LinkedList`.

---

## 6. Duyệt, comparator và cơ chế fail-fast

### 6.1 Các cách duyệt

Mọi `Collection` đều implements `Iterable`, nên hỗ trợ vòng lặp for tăng cường và `forEach`. Duyệt `Map` được thực hiện qua các view.

```java
// List / Set / Queue
for (String s : list) { ... }
collection.forEach(System.out::println);

// Map: duyệt theo khóa rồi lấy giá trị
for (String key : map.keySet()) {
    System.out.println(key + " -> " + map.get(key));
}

// Map: duyệt Entry (thường hiệu quả hơn, lấy được cả khóa và giá trị một lần)
for (Map.Entry<String, Integer> e : map.entrySet()) {
    System.out.println(e.getKey() + " = " + e.getValue());
}

// Map: forEach (BiConsumer)
map.forEach((k, v) -> System.out.println(k + " = " + v));
```

### 6.2 Comparable và Comparator

`Comparable` là **thứ tự tự nhiên do chính lớp khai báo** (`compareTo`), một lớp thường chỉ có một thứ tự tự nhiên. `Comparator` là **chiến lược so sánh bên ngoài được tùy biến** (`compare`), có thể định nghĩa nhiều cách sắp xếp cho cùng một kiểu.

```java
class Student implements Comparable<Student> {
    String name;
    int age;
    public int compareTo(Student o) {
        return Integer.compare(this.age, o.age); // Thứ tự tự nhiên: theo tuổi
    }
}

// Comparator bên ngoài: theo độ dài tên
Comparator<Student> byNameLength =
    Comparator.comparingInt(s -> s.name.length());

list.sort(byNameLength);
```

**Nguyên tắc lựa chọn**: nếu việc sắp xếp là ngữ nghĩa cố hữu của kiểu, dùng `Comparable`; nếu cần nhiều chiều sắp xếp hoặc không thể sửa mã nguồn của lớp, dùng `Comparator`.

### 6.3 Cơ chế fail-fast

Iterator của các collection không concurrent như `ArrayList`, `HashMap`, `HashSet` là **fail-fast**: trong quá trình duyệt, nếu cấu trúc collection bị sửa **từ bên ngoài** (ví dụ gọi trực tiếp `add`/`remove` của collection thay vì `remove` của iterator), iterator sẽ cố gắng ném `ConcurrentModificationException` càng sớm càng tốt, thay vì tạo ra hành vi không xác định tại thời điểm không xác định.

```java
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (s.equals("B")) {
        list.remove(s); // Ném ConcurrentModificationException
    }
}
```

Cách đúng là dùng `Iterator.remove()`:

```java
while (it.hasNext()) {
    if (it.next().equals("B")) {
        it.remove(); // Xóa qua iterator, không kích hoạt fail-fast
    }
}
```

> **Lưu ý**: fail-fast là cơ chế **cố gắng phát hiện**, không phải bảo đảm an toàn concurrent. Trong môi trường đa luồng nên dùng các collection concurrent trong gói `java.util.concurrent`.

---

## 7. Collection concurrent: thiết kế lại cho ngữ cảnh đa luồng

Các collection truyền thống (`ArrayList`, `HashMap`, v.v.) không thread-safe. Các wrapper `Collections.synchronizedXxx` cung cấp đồng bộ bằng một khóa duy nhất, nhưng mức độ concurrent thấp. Gói `java.util.concurrent` cung cấp các collection concurrent được thiết kế cho từng kiểu truy cập.

| Collection concurrent   | Cách thiết kế                                                           | Ngữ cảnh phù hợp                                    |
| ----------------------- | ----------------------------------------------------------------------- | --------------------------------------------------- |
| `ConcurrentHashMap`     | Khóa phân đoạn / CAS + khóa theo bucket; thao tác đọc thường không khóa | Ánh xạ đọc/ghi concurrent cao                       |
| `CopyOnWriteArrayList`  | Copy-on-write: khi sửa thì sao chép toàn bộ mảng bên dưới               | **Đọc nhiều, ghi rất ít**, ví dụ danh sách listener |
| `ConcurrentLinkedQueue` | Danh sách liên kết không khóa dùng CAS                                  | Hàng đợi không giới hạn concurrent cao              |
| `BlockingQueue`         | Hỗ trợ `put`/`take` có thể block                                        | Mô hình producer–consumer                           |

```java
// ConcurrentHashMap: thao tác khóa–giá trị an toàn concurrent
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("key", 1);
map.merge("key", 1, Integer::sum); // Cập nhật nguyên tử

// CopyOnWriteArrayList: đọc nhiều ghi ít
List<String> cowList = new CopyOnWriteArrayList<>();
cowList.add("A");
for (String s : cowList) { ... } // Không ném CME trong lúc duyệt
```

Iterator của `CopyOnWriteArrayList` dựa trên snapshot của mảng tại thời điểm tạo, nên trong quá trình duyệt sẽ không ném `ConcurrentModificationException`, nhưng thao tác ghi có chi phí cao (mỗi lần sửa đều sao chép mảng). Nó phù hợp với ngữ cảnh **thao tác ghi rất ít, đọc thường xuyên và duyệt nhiều**.

---

## 8. Bảng chọn nhanh

| Nhu cầu                                | Implementation ưu tiên      | Lý do chính                                    |
| -------------------------------------- | --------------------------- | ---------------------------------------------- |
| Đọc theo chỉ số thường xuyên           | `ArrayList`                 | Truy cập ngẫu nhiên trên mảng O(1)             |
| Chèn/xóa thường xuyên ở hai đầu        | `ArrayDeque` / `LinkedList` | Thao tác đầu/cuối O(1)                         |
| Loại trùng, không quan tâm thứ tự      | `HashSet`                   | Tìm kiếm trung bình O(1)                       |
| Loại trùng và giữ thứ tự chèn          | `LinkedHashSet`             | Bảng băm + danh sách liên kết theo thứ tự chèn |
| Loại trùng và cần sắp xếp              | `TreeSet`                   | Cây đỏ–đen, O(log n), truy vấn khoảng          |
| Ánh xạ khóa–giá trị, tìm nhanh         | `HashMap`                   | O(1) khấu hao                                  |
| Ánh xạ khóa–giá trị, giữ thứ tự chèn   | `LinkedHashMap`             | Bảng băm + danh sách liên kết theo thứ tự chèn |
| Ánh xạ khóa–giá trị, sắp xếp theo khóa | `TreeMap`                   | Cây đỏ–đen, duyệt có thứ tự/truy vấn khoảng    |
| Map đa luồng đọc/ghi concurrent cao    | `ConcurrentHashMap`         | Khóa theo bucket / CAS                         |
| List đa luồng đọc nhiều ghi ít         | `CopyOnWriteArrayList`      | Copy-on-write, đọc không khóa                  |

Tính thống nhất của JCF đến từ hợp đồng interface, còn tính thực dụng của nó đến từ **sự khác biệt về chi phí giữa nhiều implementation dưới cùng một hợp đồng**. Khi chọn kiểu collection, bản chất là trả lời ba câu hỏi: **Phần tử có cần duy nhất không? Thứ tự có quan trọng không? Thời gian được dành cho tìm kiếm, chèn hay xóa?** Khi ba câu hỏi này đã có đáp án, việc chọn implementation gần như là xác định.