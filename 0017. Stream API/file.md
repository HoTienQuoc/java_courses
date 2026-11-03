Chào bạn, dựa trên tài liệu PDF về `java.util.stream` (Java SE 27) và hai file mã nguồn `StreamsDemo.java`, `StreamsDemo2.java` bạn cung cấp, mình sẽ tổng hợp và giải thích chi tiết lý thuyết Stream API, song song với việc phân tích các ví dụ mã nguồn để bạn dễ hiểu nhất.

---

# TỔNG QUAN VỀ JAVA STREAM API

**Stream API** (gói `java.util.stream`) được giới thiệu từ Java 8, hỗ trợ các thao tác theo phong cách hàm (functional-style) trên các luồng phần tử (như biến đổi map-reduce trên collections).

**Định nghĩa cốt lõi:** Stream không phải là một cấu trúc dữ liệu lưu trữ phần tử. Nó là một **pipeline (đường ống)** truyền tải các phần tử từ một nguồn (source) qua các bước tính toán.

### 1. Các đặc điểm cốt lõi của Stream (Theo PDF)
*   **No storage (Không lưu trữ):** Stream không lưu dữ liệu, nó chỉ truyền tải dữ liệu từ nguồn.
*   **Functional in nature (Tính hàm):** Các thao tác trên Stream không sửa đổi nguồn ban đầu. Ví dụ: `filter` trả về một Stream mới chứa các phần tử thỏa mãn, không xóa phần tử khỏi collection gốc.
*   **Laziness-seeking (Tính lười biếng):** Các thao tác trung gian (như filter, map) không thực thi ngay lập tức. Chúng chỉ được thực thi khi có thao tác kết thúc (terminal operation).
*   **Possibly unbounded (Có thể vô hạn):** Stream có thể xử lý dữ liệu vô hạn (nhờ các thao tác short-circuiting như `limit()`, `findFirst()`).
*   **Consumable (Chỉ dùng một lần):** Mỗi Stream chỉ được duyệt một lần. Sau khi thực hiện terminal operation, Stream coi như bị "tiêu thụ" (consumed) và không thể tái sử dụng.

---

# CHI TIẾT LÝ THUYẾT KÈM VÍ DỤ TỪ MÃ NGUỒN

## 1. Các cách tạo Stream (Stream Construction)
**Lý thuyết (Trang 2 PDF):** Bạn có thể tạo Stream từ Collection, Array, các hàm static factory (`Stream.of`, `Stream.iterate`), từ file (`Files.lines`), hoặc từ các lớp tiện ích khác.

**Ví dụ từ `StreamsDemo2.java`:**
```java
// 1. Từ giá trị (Values)
Stream<String> streamFromValues = Stream.of("a1", "a2", "a3");

// 2. Từ mảng (Array)
String[] array = {"a1", "a2", "a3"};
Stream<String> streamFromArrays = Arrays.stream(array);

// 3. Từ File
Stream<String> streamFromFiles = Files.lines(Paths.get(file.getAbsolutePath()));

// 4. Từ Collection
Collection<String> collection = Arrays.asList("a1", "a2", "a3");
Stream<String> streamFromCollection = collection.stream();

// 5. Stream vô hạn (Infinite Stream) - Kết hợp với limit() để cắt ngắn
Stream<Integer> streamFromIterate = Stream.iterate(1, n -> n + 2); // 1, 3, 5, 7,...
streamFromIterate.limit(3).collect(Collectors.toList()); // Kết quả: [1, 3, 5]

// 6. Tạo Stream song song (Parallel Stream)
Stream<String> parallelStream = collection.parallelStream();
```
*Giải thích:* Stream có thể được sinh ra từ bất kỳ nguồn nào. Với `Stream.iterate`, nó tạo ra một luồng vô hạn, nhưng nhờ tính "Laziness" và thao tác `limit(3)`, chương trình chỉ tính toán đúng 3 phần tử rồi dừng.

## 2. Pipeline của Stream: Intermediate vs Terminal Operations
**Lý thuyết (Trang 2 PDF):** Một Stream Pipeline gồm:
*   **Source (Nguồn)**
*   **Intermediate Operations (Thao tác trung gian):** `filter`, `map`, `flatMap`, `sorted`... Trả về một Stream mới. Chúng luôn là *lazy* (lười biếng). Việc duyệt dữ liệu không bắt đầu cho đến khi Terminal Operation được gọi.
*   **Terminal Operations (Thao tác kết thúc):** `collect`, `forEach`, `reduce`, `sum`... Tạo ra kết quả hoặc side-effect. Chúng thường là *eager* (háo hức/thực thi ngay). Sau khi chạy, Stream bị tiêu thụ (consumed).

**Ví dụ từ `StreamsDemo.java`:**
```java
List<Product> modifiedProducts = products.stream() // Source
    .filter(product -> product.getItemsInWarehouse() > 1000) // Intermediate
    .map(product -> { // Intermediate (Lưu ý: đây là side-effect, giải thích ở mục 5)
        product.setPrice(product.getPrice() - (product.getPrice() * 0.1));
        return product;
    })
    .collect(Collectors.toList()); // Terminal
```
*Giải thích:* Ở đây, lệnh `filter` và `map` không chạy ngay. Chỉ khi gọi `.collect()`, toàn bộ pipeline mới bắt đầu duyệt các phần tử trong `products` từ đầu đến cuối, lọc, biến đổi và gom vào một List mới. Lợi ích: Dữ liệu chỉ được duyệt qua một lần (single pass), tối ưu hiệu năng.

## 3. Stateless vs Stateful Intermediate Operations
**Lý thuyết (Trang 3 PDF):** 
*   **Stateless (Không trạng thái):** `filter`, `map`. Xử lý từng phần tử độc lập, không cần nhớ phần tử trước đó.
*   **Stateful (Có trạng thái):** `distinct`, `sorted`. Phải nhớ dữ liệu của các phần tử trước đó để xử lý phần tử mới. Do đó, trong parallel processing, chúng có thể cần buffer dữ liệu hoặc duyệt nhiều lần.

**Ví dụ từ `StreamsDemo2.java`:**
```java
// Stateless
collection.stream().map((s) -> s + "_1").collect(Collectors.toList());

// Stateful
List<String> distinctOrdered = ordered.stream()
    .distinct() // Cần lưu trạng thái các phần tử đã thấy để loại bỏ trùng lặp
    .collect(Collectors.toList());

List<String> sorted = collection.stream().sorted() // Cần duyệt toàn bộ dữ liệu, sắp xếp rồi mới trả về kết quả
    .collect(Collectors.toList());
```
*Giải thích:* Với `distinct()`, bộ nhớ phải ghi nhớ các phần tử đã xuất hiện. Với `sorted()`, nó phải chờ thu thập toàn bộ phần tử (đến hết nguồn), sắp xếp, rồi mới đẩy kết quả tiếp cho các bước sau.

## 4. Thao tác Short-circuiting (Cắt ngắn)
**Lý thuyết (Trang 3 PDF):** Đây là các thao tác mà khi gặp Stream vô hạn, nó có thể trả về kết quả hữu hạn trong thời gian hữu hạn. Intermediate: `limit()`. Terminal: `findFirst()`, `anyMatch()`.

**Ví dụ từ `StreamsDemo2.java`:**
```java
// findFirst() trên Stream vô hạn sẽ dừng lại ngay khi tìm thấy
String first = collection.stream().findFirst().orElse("1");

// limit() cắt ngắn Stream
List<String> limit = collection.stream().limit(2).collect(Collectors.toList());

// anyMatch() dừng ngay khi có 1 phần tử thỏa mãn
boolean isAnyOneTrue = collection.stream().anyMatch("a1"::equals);
```
*Giải thích:* Nếu `collection` có 1 triệu phần tử, `anyMatch("a1"::equals)` sẽ dừng lại ngay khi gặp "a1" đầu tiên, không cần duyệt hết 1 triệu phần tử, giúp tiết kiệm tài nguyên.

## 5. Non-interference, Stateless behaviors và Side-effects (Rất quan trọng)
**Lý thuyết (Trang 4-6 PDF):**
*   **Non-interference (Không can thiệp):** Không được sửa đổi nguồn dữ liệu (source) trong khi pipeline đang chạy. (Trừ khi nguồn là `ConcurrentHashMap`).
*   **Stateless behaviors (Hành vi phi trạng thái):** Tham số lambda truyền vào Stream không nên phụ thuộc vào trạng thái bên ngoài có thể thay đổi.
*   **Side-effects (Tác dụng phụ):** Hạn chế tối đa việc thay đổi trạng thái bên ngoài bên trong Stream (ví dụ: dùng `forEach` để add vào một List bên ngoài).
    *   *Lưu ý đặc biệt từ mã nguồn:* Trong `StreamsDemo.java`, đoạn `.map(product -> { product.setPrice(...); return product; })` thực chất đang tạo ra **side-effect** (thay đổi trực tiếp đối tượng Product gốc). Điều này đi ngược lại khuyến nghị của Java (mặc dù nó vẫn chạy được). Java khuyên bạn nên trả về một đối tượng mới hoàn toàn.

**Ví dụ về cách làm SAI (Side-effect) từ `StreamsDemo2.java`:**
```java
// SAI: Tạo side-effect bằng cách add vào List bên ngoài (đặc biệt nguy hiểm nếu dùng parallelStream)
ArrayList<String> results = new ArrayList<>();
stream.filter(s -> pattern.matches().matches())
      .forEach(s -> results.add(s)); // Nguy cơ lỗi Thread-safe nếu chạy song song!
```
**Cách làm ĐÚNG (Không side-effect, an toàn):**
```java
// ĐÚNG: Dùng Collectors.toList() để thu thập kết quả
List<String> results = stream.filter(s -> pattern.matches().matches())
                             .collect(Collectors.toList());
```

## 6. Parallelism (Xử lý song song)
**Lý thuyết (Trang 3 PDF):** Bạn có thể chuyển Stream tuần tự thành Stream song song bằng `.parallel()` hoặc `parallelStream()`. Hệ thống sẽ tự động chia nhỏ công việc cho các luồng (thread) xử lý.
*   **Lưu ý:** Chỉ dùng khi hàm xử lý (lambda) là *stateless* và *associative* (có tính kết hợp). Không phải lúc nào parallel cũng nhanh hơn sequential.

**Ví dụ từ `StreamsDemo.java`:**
```java
int sumOfWeights = widgets.parallelStream() // Tạo luồng song song
    .filter(b -> b.getColor() == RED)
    .mapToInt(b -> b.getWeight())
    .sum(); // Tính tổng
```
*Giải thích:* `.sum()` là một thao tác kết thúc có tính kết hợp (phép cộng), nên có thể chia nhỏ mảng `widgets` ra, tính tổng từng phần song song rồi cộng lại kết quả cuối. Tốc độ sẽ nhanh hơn nhiều nếu danh sách cực lớn.

## 7. Reduction và Mutable Reduction (Rút gọn)
**Lý thuyết (Trang 7-9 PDF):**
*   **Reduction (reduce):** Kết hợp các phần tử thành một kết quả duy nhất (ví dụ: tính tổng, tìm max/min). Cần hàm `accumulator` (tích lũy) và `combiner` (kết hợp kết quả các luồng con khi chạy parallel).
*   **Mutable Reduction (collect):** Tích lũy các phần tử vào một vùng chứa có thể thay đổi (như `List`, `Map`, `StringBuilder`).

**Ví dụ từ `StreamsDemo2.java`:**
```java
// REDUCTION
// Tính tổng
Integer sum = collection.stream().reduce((s1, s2) -> s1 + s2).orElse(0);
// Tìm max
Integer max = collection.stream().reduce(Integer::max).orElse(0);
// Tìm min
Integer min = collection.stream().reduce((s1, s2) -> s1 < s2 ? s1 : s2).orElse(0);

// MUTABLE REDUCTION (COLLECT)
// Gom thành List
List<String> distinct = strings.stream().distinct().collect(Collectors.toList());
// Nối chuỗi
String join = strings.stream().collect(Collectors.joining(" : ", "<b> ", " </b>"));
// Nhóm theo khóa
Map<String, List<String>> groups = strings.stream().collect(Collectors.groupingBy((p) -> p.substring(0, 1)));
```
*Giải thích:* `reduce` thường dùng để tính toán các giá trị đơn lẻ (int, double). `collect` linh hoạt hơn, thường dùng với lớp tiện ích `Collectors` để gom nhóm, phân vùng (`partitioningBy`), tính toán thống kê (`summarizingInt`).

## 8. Associativity (Tính kết hợp)
**Lý thuyết (Trang 12 PDF):** Một phép toán là kết hợp nếu `(a op b) op c == a op (b op c)`. Điều này cực kỳ quan trọng cho Parallel Stream. Phép cộng, phép nhân, min, max, nối chuỗi đều có tính kết hợp. 
*   *Ví dụ:* `1 + 2 + 3 = (1 + 2) + 3 = 1 + (2 + 3)`. Nhờ đó Stream có thể tính `(1+2)` ở thread 1 và `(3+4)` ở thread 2, sau đó cộng lại.

## 9. Ordering (Thứ tự)
**Lý thuyết (Trang 6 PDF):** Stream có thể có hoặc không có thứ tự gặp gỡ (encounter order). Nguồn như `List` hoặc Array có thứ tự. Nguồn như `HashSet` thì không.
*   **Ví dụ từ `StreamsDemo2.java`:**
    ```java
    Collection<String> ordered = Arrays.asList("a1", "a2", "a2", "a3", "a1", "a2", "a2");
    Collection<String> nonOrdered = new HashSet<>(ordered);
    
    // Kết quả distinct của List sẽ giữ nguyên thứ tự xuất hiện: [a1, a2, a3]
    // Kết quả distinct của HashSet có thể ra thứ tự ngẫu nhiên.
    ```
*   Khi dùng parallel stream, nếu bạn không quan tâm đến thứ tự, có thể gọi `.unordered()` để tăng hiệu năng tính toán.

---

# TỔNG KẾT TỪ HAI FILE MÃ NGUỒN MẪU

1.  **`StreamsDemo.java`**: Minh họa sự kết hợp cơ bản (Basic Pipeline) với `filter`, `map`, `collect`. Đặc biệt minh họa `flatMap` (làm phẳng danh sách lồng nhau - lấy sản phẩm từ nhiều kho) và `mapToInt` (chuyển đổi sang Stream số nguyên để dùng `.sum()`).
2.  **`StreamsDemo2.java`**: Là một bộ sưu tập toàn diện (Cheat Sheet) các hàm Stream: Từ khởi tạo, lọc (`filter`), biến đổi (`map`, `flatMap`), sắp xếp (`sorted`), so khớp (`anyMatch`, `allMatch`), rút gọn (`reduce`), và thu thập nâng cao (`collect` với `groupingBy`, `partitioningBy`). 

**Lời khuyên khi làm việc với Stream:**
*   Luôn ưu tiên dùng `collect()` thay vì `forEach()` để gom dữ liệu (tránh side-effect).
*   Cẩn thận khi dùng `parallelStream()` với các Collection không an toàn luồng (như `ArrayList`), hãy dùng `Collectors.toList()` thay vì tự add phần tử.
*   Hạn chế việc thay đổi giá trị của đối tượng gốc trong hàm `map()` (như trong `StreamsDemo.java`), hãy tạo đối tượng mới hoặc sử dụng các hàm thuần túy (pure functions).

Dựa trên các hình ảnh bạn vừa cung cấp từ khóa học **"Java from Zero to First Job"** trên Udemy, mình sẽ tổng hợp và giải thích chi tiết lại toàn bộ lý thuyết cốt lõi của **Stream API** mà giảng viên đang truyền đạt. 

Đặc biệt, mình sẽ liên kết trực tiếp các lý thuyết trên slide với những đoạn code thực tế trong file `StreamsDemo.java` và `StreamsDemo2.java` mà bạn đã gửi ở lượt trước để bạn thấy được tính ứng dụng ngay lập tức.

---

### 1. Khái niệm cốt lõi: Stream API là gì?
*(Dựa trên Slide 2)*
Theo tài liệu chính thức của Oracle (được trích dẫn trên slide), **Stream API** là một tập hợp các interface cho phép thực hiện các thao tác theo phong cách lập trình hàm (functional-style) trên các luồng phần tử, ví dụ như biến đổi map-reduce trên collections.

*   **Ví dụ trên slide:**
    ```java
    int sum = widgets.stream()
        .filter(b -> b.getColor() == RED)
        .mapToInt(b -> b.getWeight())
        .sum();
    ```
*   **Giải thích:** Đoạn code trên lấy một danh sách `widgets`, lọc ra những cái màu đỏ (`filter`), chuyển đổi chúng thành trọng lượng (`mapToInt`), và tính tổng (`sum`).

### 2. Phép ẩn dụ "Băng tải" (Conveyor Belt Analogy)
*(Dựa trên Slide 3 & 4)*
Đây là cách giải thích trực quan nhất về Stream:
*   **Nguồn (Source):** Giống như một thùng chứa hàng lớn (List of Products / List of Warehouses).
*   **Băng tải (Stream Pipeline):** Các phần tử (sản phẩm) lần lượt được đưa lên một băng chuyền.
*   **Các trạm xử lý (Intermediate Operations):** Trên băng chuyền có các trạm như:
    *   *Extract products from warehouse:* Lấy sản phẩm ra khỏi kho (ví dụ: `flatMap`).
    *   *Filter:* Trạm gác, chỉ cho những sản phẩm thỏa mãn điều kiện đi tiếp.
    *   *Modify price:* Trạm dán lại giá tiền (ví dụ: `map`).
*   **Đầu ra (Terminal Operation):** Sản phẩm hoàn thiện đi ra khỏi băng chuyền và được đóng gói (ví dụ: `collect`, `forEach`).

> **Liên kết với code `StreamsDemo.java`:** Đoạn code sau chính là một băng tải hoàn chỉnh:
> ```java
> List<Product> modifiedProducts = products.stream() // 1. Nguồn (Source)
>     .filter(product -> product.getItemsInWarehouse() > 1000) // 2. Trạm lọc
>     .map(product -> { // 3. Trạm biến đổi (Modify price)
>         product.setPrice(product.getPrice() - (product.getPrice() * 0.1));
>         return product;
>     })
>     .collect(Collectors.toList()); // 4. Trạm đóng gói (Terminal)
> ```

### 3. Các cách tạo Stream (How to Create a Stream?)
*(Dựa trên Slide 5)*
Có rất nhiều cách để khởi tạo một Stream tùy thuộc vào nguồn dữ liệu:

| Cách thức | Cú pháp (Pattern) | Ví dụ (Từ `StreamsDemo2.java`) |
| :--- | :--- | :--- |
| **Từ Collection** | `collection.stream()` | `collection.stream()` |
| **Từ Mảng (Array)** | `Arrays.stream(array)` | `Arrays.stream(array)` |
| **Từ các giá trị rời** | `Stream.of(...)` | `Stream.of("a1", "a2", "a3")` |
| **Từ File** | `Files.lines(path)` | `Files.lines(Paths.get(file.getAbsolutePath()))` |
| **Từ String** | `"string".chars()` | `"123".chars()` (Trả về `IntStream`) |
| **Từ Stream Builder** | `Stream.builder().add(...)` | `Stream.builder().add("a1").add("a2").build()` |
| **Chạy song song** | `collection.parallelStream()`| `collection.parallelStream()` |
| **Vô hạn (Iterate)** | `Stream.iterate(...)` | `Stream.iterate(1, n -> n + 2)` |
| **Vô hạn (Generate)** | `Stream.generate(...)` | `Stream.generate(() -> "a1")` |

### 4. Các thao tác trung gian (Intermediate Operations)
*(Dựa trên Slide 6)*
Các thao tác này trả về một Stream mới. Chúng được gọi là **"Lazy" (lười biếng)**, nghĩa là chúng chưa thực thi ngay mà chỉ ghi nhớ lại để chờ lệnh Terminal kích hoạt.

| Phương thức | Mô tả | Ví dụ (Từ file code) |
| :--- | :--- | :--- |
| **`filter`** | Lọc các phần tử thỏa mãn điều kiện. | `.filter((p) -> p.getAge() >= 18)` |
| **`map`** | Biến đổi từng phần tử (1-1). | `.map((s) -> s + "_1")` |
| **`mapToInt/Double/Long`**| Chuyển đổi sang Stream số học nguyên thủy. | `.mapToInt(People::getAge)` |
| **`flatMap`** | Biến đổi và **làm phẳng** (1-Nhiều). Ví dụ: từ 1 Warehouse lấy ra nhiều Product. | `.flatMap(warehouse -> warehouse.getProducts().stream())` |
| **`skip`** | Bỏ qua N phần tử đầu tiên. | `.skip(2)` |
| **`distinct`** | Loại bỏ các phần tử trùng lặp (dựa vào `equals`). | `.distinct()` |
| **`peek`** | Xem các phần tử đang đi qua băng tải (thường dùng để debug). | `.peek(e -> System.out.print(e + ","))` |
| **`limit`** | Giới hạn số lượng phần tử (dùng để cắt Stream vô hạn). | `.limit(3)` |
| **`sorted`** | Sắp xếp các phần tử (tự nhiên hoặc dùng Comparator). | `.sorted((o1, o2) -> -o1.compareTo(o2))` |

### 5. Các thao tác kết thúc (Terminal Operations)
*(Dựa trên Slide 7)*
Đây là lệnh "kích hoạt" băng tải chạy. Sau khi lệnh này chạy xong, Stream sẽ bị **"tiêu thụ" (consumed)** và không thể dùng lại được nữa.

| Phương thức | Mô tả | Ví dụ (Từ file code) |
| :--- | :--- | :--- |
| **`collect`** | Thu thập kết quả vào List, Set, Map... | `.collect(Collectors.toList())` |
| **`forEach`** | Duyệt qua từng phần tử (tạo side-effect). | `.forEach(System.out::println)` |
| **`toArray`** | Chuyển Stream thành Mảng. | `.toArray(Product[]::new)` |
| **`findFirst`** | Lấy phần tử đầu tiên. | `.findFirst().orElse("1")` |
| **`findAny`** | Lấy một phần tử bất kỳ (rất hữu ích khi chạy parallel). | `.findAny().orElse("1")` |
| **`count`** | Đếm số lượng phần tử. | `.count()` |
| **`anyMatch/noneMatch/allMatch`**| Kiểm tra điều kiện (trả về boolean). | `.anyMatch("a1"::equals)` |
| **`min` / `max`** | Tìm phần tử nhỏ nhất / lớn nhất. | `.max(String::compareTo).get()` |
| **`forEachOrdered`** | Giống `forEach` nhưng đảm bảo đúng thứ tự (khi parallel). | `.forEachOrdered((p) -> p.append("_new"))` |
| **`reduce`** | Kết hợp các phần tử thành 1 kết quả duy nhất (tính tổng, max...).| `.reduce((s1, s2) -> s1 + s2).orElse(0)` |

---

### 💡 Tổng kết Lộ trình học Stream API từ các Slide:
1.  Hiểu Stream là một **"băng tải"** xử lý dữ liệu, không lưu trữ dữ liệu.
2.  Biết cách **tạo Stream** từ List, Array, File, hoặc tạo Stream vô hạn.
3.  Sử dụng thành thạo các **Intermediate Ops** (`filter`, `map`, `flatMap`) để lắp ráp các trạm xử lý trên băng tải. (Nhớ kỹ tính chất *Lazy* của chúng).
4.  Dùng **Terminal Ops** (`collect`, `reduce`, `count`) để lấy thành phẩm cuối cùng. 

*Mẹo nhỏ khi làm bài tập:* Khi bạn có một yêu cầu như *"Từ danh sách các kho, hãy lấy ra tên các sản phẩm có giá > 100 và in ra màn hình"*. Hãy dịch nó sang Stream: `warehouses.stream()` -> Lấy sản phẩm (`flatMap`) -> Lọc giá (`filter`) -> Lấy tên (`map`) -> In ra (`forEach`). Cứ đi từng bước theo đúng sơ đồ băng tải này là code sẽ cực kỳ rõ ràng!