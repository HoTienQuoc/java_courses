### PHẦN 0: PHÂN BIỆT `Object` (viết hoa) VÀ `object` (viết thường)

Trước khi đi sâu, hãy chốt lại sự khác biệt cốt lõi:
*   **`object` (viết thường):** Là một **thực thể (instance)** được tạo ra từ bất kỳ class nào. (Ví dụ: `User user = new User();` thì `user` là một `object`).
*   **`Object` (viết hoa):** Là tên của một **lớp (class) cụ thể** trong Java (`java.lang.Object`). Nó là "ông tổ" của mọi class. (Ví dụ: `Object obj = new Object();` thì `obj` là một `object` được tạo từ class `Object`).

---

### PHẦN 1: TỔNG QUAN LỚP `Object` (Cội nguồn của vạn vật)

Trong Java, mọi class (kể cả mảng) đều ngầm kế thừa từ `java.lang.Object`. Do đó, mọi đối tượng đều có sẵn các phương thức của lớp này.

#### 1. `toString()`
*   **Giải thích:** Trả về chuỗi đại diện cho đối tượng. Mặc định là `TênClass@MãHashCode`.
*   **Ví dụ & Giải thích bên cạnh:**
```java
class User {
    String name = "Nam";
}

public class Main {
    public static void main(String[] args) {
        User u = new User();
        System.out.println(u.toString()); 
        // KẾT QUẢ: User@1a2b3c (Mặc định, không có ý nghĩa)
        
        // Nếu ta GHI ĐÈ (Override) lại:
        // @Override public String toString() { return "User: " + name; }
        // KẾT QUẢ: User: Nam (Dễ đọc hơn nhiều)
    }
}
```

#### 2. `equals(Object obj)` và `hashCode()`
*   **Giải thích:** 
    *   `equals`: Mặc định so sánh địa chỉ bộ nhớ (giống `==`). Ta thường ghi đè để so sánh giá trị.
    *   `hashCode`: Trả về mã băm (số nguyên). **Quy tắc:** Nếu `equals` trả về `true`, thì `hashCode` bắt buộc phải giống nhau.
*   **Ví dụ:**
```java
class User {
    int id;
    User(int id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id; // So sánh dựa trên id
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id); // Bắt buộc phải override cùng equals
    }
}
```

#### 3. `getClass()`
*   **Giải thích:** Trả về đối tượng `Class` mô tả class thực tế của đối tượng tại runtime. (Không thể ghi đè vì là `final`).
*   **Ví dụ:**
```java
User u = new User(1);
System.out.println(u.getClass().getName()); // KẾT QUẢ: "User" (Hoặc package.User)
```

#### 4. `clone()`
*   **Giải thích:** Tạo bản sao của đối tượng (Shallow copy - sao chép nông). Class muốn dùng phải implements `Cloneable`.
*   **Ví dụ:**
```java
class User implements Cloneable {
    String name = "Nam";
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); // Gọi hàm clone của lớp Object
    }
}
// Sử dụng:
User u1 = new User();
User u2 = (User) u1.clone(); // u2 là bản sao của u1
```

#### 5. `wait()`, `notify()`, `notifyAll()`
*   **Giải thích:** Dùng trong lập trình đa luồng (Multi-threading). Phối hợp các luồng chờ nhau trên cùng một đối tượng.

---

### PHẦN 2: JNI (Java Native Interface) LÀ GÌ?

*   **Giải thích:** JNI là một **cầu nối** cho phép code Java gọi các thư viện viết bằng C/C++ (hoặc ngược lại). Nó cần thiết khi bạn muốn:
    1.  Tương tác với phần cứng (ví dụ: đọc cảm biến, giao tiếp USB).
    2.  Tái sử dụng thư viện C/C++ đã có sẵn.
    3.  Tối ưu hiệu năng (những tác vụ tính toán nặng có thể viết bằng C++ chạy nhanh hơn).

#### Ví dụ về JNI:
**Bước 1: Khai báo hàm `native` trong Java**
```java
public class HelloJNI {
    // Khai báo hàm nhưng KHÔNG CÓ phần thân (body)
    public native void sayHello(); 

    static {
        // Tải thư viện C/C++ (file .so hoặc .dll)
        System.loadLibrary("hello"); 
    }

    public static void main(String[] args) {
        new HelloJNI().sayHello(); // Gọi hàm C
    }
}
```
**Bước 2: Viết code C/C++ tương ứng**
```c
#include <jni.h>
#include "HelloJNI.h"

// Quy ước đặt tên: Java_TênClass_tênHàm
JNIEXPORT void JNICALL Java_HelloJNI_sayHello(JNIEnv *env, jobject obj) {
    printf("Hello from C/C++!\n");
}
```
*   **Giải thích bên cạnh:** Con trỏ `JNIEnv *env` chính là công cụ để code C có thể thao tác với các đối tượng Java (như tạo chuỗi, gọi phương thức Java, ném ngoại lệ...).

---

### PHẦN 3: MỐI LIÊN HỆ GIỮA `Object` VÀ JNI

Tại sao một số hàm của lớp `Object` lại cần đến JNI? Bởi vì **Java thuần không thể tự thao tác trực tiếp với bộ nhớ của JVM**. Các hàm như `hashCode()`, `clone()`, `getClass()` phụ thuộc vào cấu trúc bộ nhớ bên dưới, nên chúng được khai báo là `native`.

#### Bằng chứng từ mã nguồn JDK:
Nếu bạn mở file nguồn của lớp `Object` trong JDK, bạn sẽ thấy đoạn code này:
```java
public class Object {
    // 1. Khai báo một hàm native
    private static native void registerNatives();
    
    // 2. Static block chạy ngay khi class Object được nạp vào bộ nhớ
    static {
        registerNatives();
    }

    // 3. Các hàm native khác
    public final native Class<?> getClass();
    public native int hashCode();
    protected native Object clone() throws CloneNotSupportedException;
    // ...
}
```
*   **Giải thích chi tiết:**
    *   **`registerNatives()`**: Hàm này khi được gọi sẽ kết nối (link) các hàm C/C++ (được viết sẵn trong JVM) với các phương thức `native` khai báo ở trên (như `hashCode`, `clone`, `getClass`).
    *   **Tại sao phải làm vậy?** Vì `hashCode` cần địa chỉ bộ nhớ thật, `clone` cần copy vùng nhớ thật. Java không có con trỏ, nên nó phải "nhờ" JNI làm việc này.
    *   **Static block `static { registerNatives(); }`**: Đảm bảo ngay khi lớp `Object` (lớp gốc của vạn vật) được nạp, các hàm native lập tức được đăng ký để sẵn sàng sử dụng.

---
**Tóm lại:**
*   **`Object`**: Là class gốc. Cung cấp các hàm cơ bản nhất cho mọi đối tượng.
*   **`object`**: Là một thực thể cụ thể đang sống trong bộ nhớ.
*   **JNI**: Là công cụ để Java "nói chuyện" với C/C++.
*   **Mối liên hệ**: Lớp `Object` sử dụng JNI thông qua các hàm `native` để thực hiện các tác vụ sâu dưới đáy bộ nhớ mà Java không thể tự làm.