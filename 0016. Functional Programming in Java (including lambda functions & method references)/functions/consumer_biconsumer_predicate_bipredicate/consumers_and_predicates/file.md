Dựa trên hình ảnh Agenda của bài học và 2 file code bạn cung cấp, bài học này tập trung vào nhóm các **Functional Interface dùng để xử lý hành động (Consumer/BiConsumer)** và **kiểm tra điều kiện (Predicate/BiPredicate)**. 

Đặc biệt, bài học minh họa rất rõ cách các interface này được tích hợp vào các Collection có sẵn trong Java như `List`, `Map`, và `Iterator`.

Dưới đây là phần giải thích chi tiết lý thuyết song song với code thực tế của bạn.

---

## 1. Consumer Interface & BiConsumer Interface

### Lý thuyết
*   **`Consumer<T>`:** Đại diện cho một hành động (operation) nhận **1 tham số đầu vào** và **không trả về kết quả** (`void`). Phương thức chính là `void accept(T t)`. Nó thường được dùng để in ấn, thay đổi trạng thái (mutate), hoặc tiêu thụ dữ liệu.
*   **`BiConsumer<T, U>`:** Tương tự nhưng nhận **2 tham số đầu vào**, không trả về kết quả. Phương thức chính là `void accept(T t, U u)`.

### Ví dụ & Giải thích lý thuyết bên cạnh

**File: `ConsumerDemo.java`**

**Đoạn 1: `Consumer` kết hợp với `Iterator.forEachRemaining()`**
```java
public static void increasePriceForProductList(List<? extends Product> products, double priceToIncrease) {
    // Iterator.forEachRemaining(Consumer<? super E> action)
    products.iterator().forEachRemaining(
        product -> product.setPrice(product.getPrice() + priceToIncrease)
    );
}
```
> 💡 **Giải thích lý thuyết:**
> *   Hàm `forEachRemaining` của `Iterator` nhận vào một `Consumer`. 
> *   Lambda `product -> product.setPrice(...)` chính là một `Consumer<Product>`. Nó nhận vào `product`, thực hiện hành động (thay đổi giá) nhưng **không trả về gì cả**. Đây là cách viết gọn hơn so với vòng lặp `while(iterator.hasNext())`.

**Đoạn 2: `BiConsumer` kết hợp với `Map.forEach()`**
```java
public static void increasePriceForProductMap(Map<Integer, ? extends Product> idProductMap, double priceToIncrease) {
    // Map.forEach(BiConsumer<? super K, ? super V> action)
    idProductMap.forEach(
        (id, product) -> product.setPrice(product.getPrice() + priceToIncrease)
    );
}
```
> 💡 **Giải thích lý thuyết:**
> *   `Map.forEach` không dùng `Consumer` mà dùng `BiConsumer` vì mỗi phần tử trong Map gồm 2 phần: **Key (id)** và **Value (product)**.
> *   Lambda `(id, product) -> ...` chính là một `BiConsumer<Integer, Product>`. Nó nhận vào 2 tham số, thực hiện hành động nhưng không trả về gì.

---

## 2. Predicate Interface & `Collection.removeIf()`

### Lý thuyết
*   **`Predicate<T>`:** Đại diện cho một hàm kiểm tra điều kiện. Nhận **1 tham số** và luôn trả về **`boolean`**. Phương thức chính là `boolean test(T t)`.
*   **`Collection.removeIf(Predicate)`:** Là một method mặc định của Collection. Nó sẽ duyệt qua các phần tử và xóa phần tử đó nếu `Predicate` trả về `true`.

### Ví dụ & Giải thích lý thuyết bên cạnh

**File: `PredicateDemo.java`**

**Đoạn 3: `Predicate` cơ bản với `removeIf()`**
```java
public static void removeProductsIfPriceIsMoreThan(List<? extends Product> products, double price) {
    // removeIf(Predicate<? super E> filter)
    products.removeIf(product -> product.getPrice() > price);
}
```
> 💡 **Giải thích lý thuyết:**
> *   Lambda `product -> product.getPrice() > price` là một `Predicate<Product>`.
> *   Nếu giá > mức cho phép, nó trả về `true` -> `removeIf` sẽ xóa sản phẩm đó. Cách viết này thay thế hoàn toàn việc dùng vòng lặp kết hợp `Iterator.remove()` rất dễ gây lỗi (`ConcurrentModificationException`).

---

## 3. Kết hợp các Predicate (`and`, `or`, `negate`, `not`, `isEqual`)

### Lý thuyết
Các `Predicate` có thể được nối lại với nhau để tạo ra các điều kiện phức tạp hơn (giống như toán tử logic `&&`, `||`, `!`):
*   **`.and(other)`**: Trả về `true` nếu cả 2 đều `true`. (Tương đương `&&`)
*   **`.or(other)`**: Trả về `true` nếu 1 trong 2 `true`. (Tương đương `||`)
*   **`.negate()`**: Đảo ngược kết quả. (Tương đương `!`)
*   **`Predicate.not(predicate)`**: Phương thức static (từ Java 11), tương tự như `negate()` nhưng giúp code dễ đọc hơn.
*   **`Predicate.isEqual(target)`**: Trả về một Predicate kiểm tra xem đối tượng truyền vào có `.equals()` với `target` hay không (an toàn với cả giá trị `null`).

### Ví dụ & Giải thích lý thuyết bên cạnh

**File: `PredicateDemo.java`**

**Đoạn 4: `Predicate.and()`**
```java
public static void removeProductsIfPriceIsMoreThanAndCategoryIsEqualTo(...) {
    Predicate<Product> isPriceIsMoreThanPredicate = product -> product.getPrice() > price;
    Predicate<Product> isCategoryIsEqualTo = product -> product.getCategoryName().equals(categoryName);
    
    // Kết hợp 2 Predicate
    products.removeIf(isPriceIsMoreThanPredicate.and(isCategoryIsEqualTo));
    
    // negate() demo (Đảo ngược điều kiện)
    // products.removeIf(isPriceIsMoreThanPredicate.and(isCategoryIsEqualTo).negate());
}
```
> 💡 **Giải thích lý thuyết:**
> *   `isPriceIsMoreThanPredicate.and(isCategoryIsEqualTo)` tạo ra một `Predicate` mới: "Giá > X **VÀ** Danh mục = Y".
> *   Nếu bạn bỏ comment dòng `.negate()`, nó sẽ đảo ngược lại: "Xóa những sản phẩm **KHÔNG PHẢI** (Giá > X VÀ Danh mục = Y)".

**Đoạn 5: `Predicate.isEqual()` và `Predicate.not()`**
```java
System.out.println("========== Predicate.isEqual() demo ==========");
User criteriaUser = new UserForHashTables(1, "John", "Smith", "password", "john.smith@email.com");

// users.removeIf(Predicate.not(Predicate.isEqual(criteriaUser)));
users.removeIf(Predicate.not(Predicate.isEqual(criteriaUser)));
```
> 💡 **Giải thích lý thuyết:**
> *   `Predicate.isEqual(criteriaUser)` trả về một Predicate kiểm tra xem user đó có giống hệt `criteriaUser` không (dựa trên hàm `.equals()`).
> *   `Predicate.not(...)` (Java 11+) đảo ngược lại điều kiện đó. Nghĩa là: "Hãy xóa những user **KHÔNG BẰNG** criteriaUser". 
> *   Cách viết này cực kỳ Declarative (khai báo) và dễ đọc. Ta giữ lại duy nhất 1 user khớp với điều kiện.

---

## 4. Bổ sung: BiPredicate Interface (Có trong Agenda)

Mặc dù không xuất hiện trực tiếp trong 2 file code trên, nhưng Agenda có nhắc đến **`BiPredicate<T, U>`**.
*   **Lý thuyết:** Giống như `BiFunction`, nó nhận **2 tham số** và trả về **`boolean`**. Phương thức là `boolean test(T t, U u)`.
*   **Ví dụ minh họa:**
    ```java
    BiPredicate<String, Integer> isLengthGreaterThan = (str, num) -> str.length() > num;
    System.out.println(isLengthGreaterThan.test("Hello", 3)); // true
    ```
*   Nó cũng hỗ trợ `.and()`, `.or()`, `.negate()` tương tự như `Predicate`.

---

### Tổng kết theo đúng Agenda:
1.  **Consumer & BiConsumer**: Dùng để thao tác (mutate) dữ liệu mà không cần trả về (như tăng giá sản phẩm).
2.  **Iterator.forEachRemaining & Map.forEach**: Các hàm có sẵn của Java nhận vào `Consumer` / `BiConsumer`.
3.  **Predicate & Collection.removeIf**: Dùng để lọc và xóa dữ liệu một cách an toàn và ngắn gọn.
4.  **Predicate.and(), or(), negate(), not(), isEqual()**: Các công cụ để xây dựng các biểu thức điều kiện phức tạp bằng cách kết hợp các hàm (Functional Composition).