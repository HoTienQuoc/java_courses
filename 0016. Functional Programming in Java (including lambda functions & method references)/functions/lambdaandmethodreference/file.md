Dựa trên các file code bạn cung cấp và hình ảnh Agenda của khóa học (Udemy), bài học này đang đi đúng vào phần thực hành (Practice) và lý thuyết chuyên sâu về **Custom Functional Interface**, **Lambda Function**, và **Method Reference**. 

Dưới đây là phần giải thích chi tiết, kết hợp lý thuyết song song với từng đoạn code thực tế bạn đã cung cấp.

---

## 1. What is a 'Functional Interface' & How to create a custom one?

### Lý thuyết
Theo định nghĩa: **Functional Interface** là một interface có **duy nhất một phương thức trừu tượng (abstract method)**. 
*   Nó có thể có nhiều phương thức `default` (có thân hàm) và `static` (có thân hàm).
*   Nó đóng vai trò là "khuôn mẫu" (target type) để Java có thể gán các biểu thức Lambda hoặc Method Reference vào.
*   Annotation `@FunctionalInterface` là tùy chọn nhưng rất nên dùng, vì compiler sẽ báo lỗi nếu bạn vô tình thêm abstract method thứ 2.

### Ví dụ & Giải thích lý thuyết bên cạnh

**File: `DistanceCalculator.java`**
```java
@FunctionalInterface
public interface DistanceCalculator {

    // 1. Abstract method duy nhất -> Đủ điều kiện làm Functional Interface
    double calculateDistance(City city1, City city2);

    // 2. Nếu bỏ comment dòng dưới, compiler SẼ BÁO LỖI ngay lập tức
    // double calculateDeliveryTime(); 

    // 3. Default method: Được phép (vì đã có thân hàm)
    default void someDefaultMethod() {
        System.out.println("Default method...");
    }

    // 4. Static method: Được phép (vì đã có thân hàm)
    static void someStaticMethod() {
        System.out.println("Static method...");
    }
}
```
> 💡 **Giải thích lý thuyết:** 
> *   `DistanceCalculator` là một **Custom Functional Interface** (chúng ta tự tạo ra thay vì dùng `Function`, `BiFunction` của Java).
> *   Phương thức trừu tượng `calculateDistance` nhận vào 2 đối tượng `City` và trả về `double`. 
> *   Tại sao có `default` và `static` vẫn được coi là Functional Interface? Vì chúng không phải là *abstract method* (chúng đã được hiện thực hóa). Tính "duy nhất" chỉ áp dụng cho các phương thức trừu tượng chưa có thân hàm.

*(Lưu ý nhỏ: File `City.java` chỉ là một POJO cơ bản chứa `longitude` và `latitude` (bạn có thể thấy tác giả gõ nhầm chữ `Logitude` thay vì `Longitude`), đóng vai trò là tham số đầu vào (input) cho interface trên).*

---

## 2. Lambda Function

### Lý thuyết
Lambda là cách viết tắt của Anonymous Class (lớp ẩn danh). Nó cho phép ta biểu diễn **hành vi (behavior)** một cách ngắn gọn mà không cần tạo class cụ thể. 
Cú pháp: `(tham_số) -> biểu_thức` hoặc `(tham_số) -> { khối_lệnh }`

### Ví dụ & Giải thích lý thuyết bên cạnh

**File: `LambdaAndMethodReferenceDemo.java`**

```java
// === 1. CÁCH CŨ: Anonymous Class (Java 7 trở về trước) ===
orderManagement.setDistanceCalculator(new DistanceCalculator() {
    @Override
    public double calculateDistance(City city1, City city2) {
        // Phải viết đầy đủ tên class, ký hiệu @Override, rất dài dòng
        return city1.getLogitude() - city2.getLogitude(); 
    }
});

// === 2. CÁCH MỚI: Lambda dạng biểu thức (1 dòng) ===
DistanceCalculator dCalculator = (city1, city2) -> city1.getLogitude() - city2.getLogitude();

// === 3. CÁCH MỚI: Lambda dạng khối lệnh (nhiều dòng) ===
DistanceCalculator dCalculator2 = (city1, city2) -> {
    System.out.println("Text inside lambda function");
    return city1.getLogitude() - city2.getLogitude();
};
dCalculator2.calculateDistance(new City(), new City()); // Gọi hàm

// === 4. Truyền Lambda trực tiếp như một tham số ===
orderManagement.setDistanceCalculator(
    (city1, city2) -> city1.getLogitude() - city2.getLogitude()
);
```
> 💡 **Giải thích lý thuyết:**
> *   **Đoạn 1 (Anonymous Class):** Ta phải tạo ra một object không tên, implement `DistanceCalculator`. Rất nặng nề (boilerplate code).
> *   **Đoạn 2 & 3 (Lambda):** Java compiler tự động hiểu `(city1, city2)` là tham số của hàm `calculateDistance`, kiểu dữ liệu (City, City) được **suy luận tự động (Type Inference)** nên không cần khai báo. Phần sau `->` chính là thân hàm.
> *   **Đoạn 4:** Nhờ có Functional Interface, ta có thể truyền thẳng "hành vi" vào hàm `setDistanceCalculator` giống như truyền một biến thông thường. Đây chính là tính **First-class function** (hàm là công dân hạng nhất) của Lập trình hàm.

---

## 3. Method Reference

### Lý thuyết
**Method Reference** là cú pháp viết tắt của Lambda, **chỉ được sử dụng khi Lambda đó chỉ gọi duy nhất một phương thức có sẵn**. 
Cú pháp sử dụng toán tử `::` (double colon). Nó giúp code mang tính "Declarative" (Khai báo) cao hơn, đọc hiểu như ngôn ngữ tự nhiên.

### Ví dụ & Giải thích lý thuyết bên cạnh

Trong file demo của bạn, lớp `GoogleDistanceCalculator` cung cấp 2 hàm:
1. Hàm Instance (không static): `getDistanceBetweenCities(City, City)`
2. Hàm Static: `getDistanceBetweenCitiesStatic(City, City)`

**File: `LambdaAndMethodReferenceDemo.java`**

```java
// === 1. STATIC METHOD REFERENCE ===
// Lambda tương đương: (city1, city2) -> GoogleDistanceCalculator.getDistanceBetweenCitiesStatic(city1, city2)
orderManagement.setDistanceCalculator(
    GoogleDistanceCalculator::getDistanceBetweenCitiesStatic
);

// === 2. INSTANCE METHOD REFERENCE (Trên một đối tượng cụ thể) ===
GoogleDistanceCalculator gdc = new GoogleDistanceCalculator();

// Lambda tương đương: (city1, city2) -> gdc.getDistanceBetweenCities(city1, city2)
orderManagement.setDistanceCalculator(
    gdc::getDistanceBetweenCities
);
```
> 💡 **Giải thích lý thuyết:**
> *   **Toán tử `::`:** Ở đoạn 1, `GoogleDistanceCalculator::getDistanceBetweenCitiesStatic` được hiểu là "Hãy dùng hàm static này làm thân hàm cho `calculateDistance` của interface". Các tham số `(City, City)` được truyền thẳng vào hàm static đó.
> *   **Đoạn 2:** Tương tự, nhưng ta gọi hàm trên một *đối tượng cụ thể* (`gdc`). Java sẽ map 2 tham số `(City, City)` vào hàm `getDistanceBetweenCities` của đối tượng `gdc`.
> *   **Tại sao dùng Method Reference thay vì Lambda?** Để code ngắn gọn và dễ đọc hơn. Nếu lambda của bạn chỉ là `(a, b) -> obj.doSomething(a, b)`, hãy chuyển ngay thành `obj::doSomething`.

---

## 4. Thiết kế tổng thể (Design Pattern ẩn sau code)

**File: `LambdaAndMethodReferenceDemo.java` (Class OrderManagement)**

```java
class OrderManagement {
    private DistanceCalculator distanceCalculator;

    public OrderManagement(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    public void setDistanceCalculator(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }
}
```
> 💡 **Giải thích lý thuyết:**
> *   `OrderManagement` không hề biết cách tính khoảng cách là gì (không hardcode). Nó chỉ giữ một tham chiếu đến `DistanceCalculator`.
> *   Đây là sự kết hợp hoàn hảo giữa **OOP (Strategy Pattern)** và **Functional Programming**. Thay vì phải tạo các class kế thừa (như `DefaultDistanceCalculator`, `GoogleDistanceCalculator`), ta có thể linh hoạt "bơm" (inject) các Lambda hoặc Method Reference khác nhau vào tùy thời điểm thông qua hàm `setDistanceCalculator`.
> *   **Lợi ích:** Tính mở rộng (Open/Closed Principle). Sau này muốn thêm tính năng tính khoảng cách bằng "Grab", "Be", ta không cần sửa code của `OrderManagement`, chỉ cần truyền một Lambda mới vào là xong.

---
**Tổng kết theo đúng Agenda của bài học (Hình ảnh):**
1.  **What is 'Functional Interface':** Là interface có 1 abstract method (như `DistanceCalculator`).
2.  **How to create custom functional interface:** Tự định nghĩa interface với `@FunctionalInterface` (như file `DistanceCalculator.java`).
3.  **Lambda function:** Cách viết `(city1, city2) -> ...` để thay thế Anonymous Class.
4.  **Method reference:** Cách viết `ClassName::staticMethod` hoặc `instance::method` khi Lambda chỉ gọi 1 hàm có sẵn.
5.  **Practice:** Chính là việc áp dụng vào bài toán `OrderManagement` truyền các chiến lược tính khoảng cách khác nhau.