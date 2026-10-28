Dựa trên Agenda trong hình ảnh (bài học về **Function & BiFunction**) và file code `FunctionsDemoCollections.java` bạn cung cấp, đây là sự kết hợp hoàn hảo giữa lý thuyết và thực hành (Practice with Map implementations).

Dưới đây là phần giải thích chi tiết từng khái niệm, song song với việc bóc tách code của bạn.

---

## 1. Function Interface (`Function<T, R>`)

### Lý thuyết
*   **Định nghĩa:** `Function<T, R>` là một functional interface có sẵn trong `java.util.function`. Nó đại diện cho một hàm nhận **một tham số đầu vào kiểu `T`** và trả về **một kết quả kiểu `R`**.
*   **Phương thức chính:** `R apply(T t)`
*   **Ứng dụng:** Thường dùng để biến đổi (transform) dữ liệu từ dạng này sang dạng khác. Trong Collections, nó xuất hiện trong các hàm như `Map.computeIfAbsent()`.

### Ví dụ & Giải thích lý thuyết bên cạnh

**Đoạn code 1: Sử dụng `computeIfAbsent` (Function)**
```java
Map<Integer, String> map = new HashMap<>();
// ... put(1, "one"), put(2, "two"), put(3, "three")

// 1. Dùng Lambda function
map.computeIfAbsent(4, key -> Integer.toString(key)); 

// 2. Dùng Method Reference
map.computeIfAbsent(5, FunctionsDemoCollections::convertIntToString);
```
> 💡 **Giải thích lý thuyết:**
> *   `Map.computeIfAbsent(K key, Function<K, V> mappingFunction)`: Nếu key `4` chưa có trong map, nó sẽ gọi `Function` để tính toán value.
> *   **Input (T):** `Integer` (chính là cái `key`).
> *   **Output (R):** `String` (value trả về).
> *   **Lambda `key -> Integer.toString(key)`:** Nhận vào 1 tham số, trả về 1 giá trị.
> *   **Method Reference `FunctionsDemoCollections::convertIntToString`:** Trỏ tới hàm `public static String convertIntToString(Integer integer)`. Hàm này có chữ ký (signature) khớp hoàn toàn với `apply(T t)` của `Function`.

---

## 2. BiFunction Interface (`BiFunction<T, U, R>`)

### Lý thuyết
*   **Định nghĩa:** `BiFunction<T, U, R>` là một functional interface nhận **hai tham số đầu vào (`T` và `U`)** và trả về **một kết quả (`R`)**.
*   **Phương thức chính:** `R apply(T t, U u)`
*   **Ứng dụng:** Dùng khi cần kết hợp 2 nguồn dữ liệu lại với nhau. Trong Collections, nó xuất hiện trong `Map.computeIfPresent()` hoặc `Map.replaceAll()`.

### Ví dụ & Giải thích lý thuyết bên cạnh

**Đoạn code 2: Sử dụng `computeIfPresent` (BiFunction)**
```java
// 1. Dùng Lambda function
map.computeIfPresent(3, (key, value) -> key + " : " + value);

// 2. Dùng Method Reference
map.computeIfPresent(2, FunctionsDemoCollections::combineIntAndString);
```
> 💡 **Giải thích lý thuyết:**
> *   `Map.computeIfPresent(K key, BiFunction<K, V, V> remappingFunction)`: Nếu key `3` có tồn tại, nó sẽ tính toán lại value dựa trên key và value cũ.
> *   **Input (T, U):** `Integer` (key) và `String` (value hiện tại).
> *   **Output (R):** `String` (value mới).
> *   **Lambda `(key, value) -> key + " : " + value`:** Nhận 2 tham số, trả về 1 kết quả. Kết quả map sẽ thành `{3="3 : three"}`.
> *   **Method Reference `FunctionsDemoCollections::combineIntAndString`:** Trỏ tới `public static String combineIntAndString(Integer integer, String string)`. Chữ ký hàm này khớp với `BiFunction<Integer, String, String>`.

---

## 3. Compose Functions: `andThen()` và `compose()`

### Lý thuyết
`Function` hỗ trợ kết hợp (chaining) các hàm lại với nhau để tạo ra một hàm mới. Giả sử bạn có hàm `f` và hàm `g`:
*   **`f.andThen(g)`:** Chạy `f` trước, lấy kết quả ném vào `g` (Toán học: `g(f(x))`).
*   **`g.compose(f)`:** Cũng tương đương `g(f(x))`, nhưng cách viết ngược lại. Nó nghĩa là "chuẩn bị hàm `g`, sau đó ghép nó vào sau hàm `f`.

### Ví dụ & Giải thích lý thuyết bên cạnh

**Đoạn code 3: Kết hợp Function**
```java
Function<Product, Double> getPriceFunction = product2 -> product2.getPrice();
Function<Double, Double> getDiscountFunction = price -> price * DISCOUNT_RATE;

// Cách 1: Dùng andThen()
Function<Product, Double> getPriceAndThenDiscountFunction 
        = getPriceFunction.andThen(getDiscountFunction);

// Cách 2: Dùng compose()
Function<Product, Double> getPriceAndThenDiscountFunction2 
        = getDiscountFunction.compose(getPriceFunction);

// Ứng dụng: tính tiền giảm giá cho từng sản phẩm
productDiscountMap.computeIfAbsent(product, getPriceAndThenDiscountFunction);
```
> 💡 **Giải thích lý thuyết:**
> *   `getPriceFunction`: Product -> Double (Lấy giá).
> *   `getDiscountFunction`: Double -> Double (Tính chiết khấu).
> *   **`andThen`:** Đọc từ trái sang phải: "Lấy giá" -> "Tính chiết khấu". (Product -> Double -> Double).
> *   **`compose`:** Đọc từ phải sang trái: "Tính chiết khấu" *được ghép sau* "Lấy giá". Kết quả cuối cùng vẫn là một `Function<Product, Double>`.
> *   **Ứng dụng Map:** `computeIfAbsent` chấp nhận một `Function`. Thay vì truyền một Lambda đơn lẻ, ta truyền cả một **chuỗi hàm (pipeline)** đã được compose sẵn. Kết quả map sẽ lưu `Product` và `Giá trị chiết khấu (Double)`.

---

## 4. `identity()` Method (Có trong Agenda nhưng chưa có trong code)

### Lý thuyết
`Function.identity()` là một hàm đặc biệt: **Đầu vào là gì thì trả về y nguyên cái đó** (`x -> x`). 
Nó thường được dùng làm giá trị mặc định trong các hàm thu thập dữ liệu (Collectors) hoặc khi bạn muốn bỏ qua một bước biến đổi.

### Ví dụ (bổ sung thêm để đủ Agenda)
```java
// Thay vì viết: product -> product
Function<Product, Product> identityFunc = Function.identity();
```

---

## 5. `Comparator.comparing()`

### Lý thuyết
`Comparator.comparing()` là một static method cực kỳ mạnh mẽ. Nó nhận vào một **`Function`** (để trích xuất ra thuộc tính cần so sánh) và trả về một **`Comparator`**.

### Ví dụ & Giải thích lý thuyết bên cạnh

**Đoạn code 4: Sắp xếp danh sách**
```java
// 1. Dùng Method Reference
products.sort(Comparator.comparing(Product::getPrice));

// 2. Dùng Lambda function
products.sort(Comparator.comparing(product -> product.getPrice()));
```
> 💡 **Giải thích lý thuyết:**
> *   `Comparator.comparing(Function<? super T, ? extends U> keyExtractor)`.
> *   Thay vì phải viết cả một `Comparator` dài dòng (như `(p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice())`), ta chỉ cần nói: "Hãy so sánh dựa trên kết quả của hàm này".
> *   Cả Lambda và Method Reference đều cung cấp một `Function<Product, Double>`. `Comparator.comparing` sẽ tự động lo phần so sánh 2 số `Double` với nhau.

---

### Tóm tắt theo đúng Agenda của bài học:
1.  **Function:** Nhận 1 trả 1 (Dùng trong `computeIfAbsent`).
2.  **BiFunction:** Nhận 2 trả 1 (Dùng trong `computeIfPresent`).
3.  **Lambda & Method Reference:** Hai cách để hiện thực hóa các interface trên (ví dụ: `key -> ...` vs `Class::method`).
4.  **andThen / compose:** Cách nối các hàm lại với nhau (ví dụ: Tính giá -> Tính giảm giá).
5.  **Comparator.comparing:** Nhận vào một `Function` trích xuất, trả về `Comparator` để sắp xếp (ví dụ: `Product::getPrice`).
6.  **Practice with Map:** Áp dụng tất cả các khái niệm trên vào `HashMap` để thao tác dữ liệu gọn gàng, không cần dùng `if (map.containsKey(...))` thủ công.