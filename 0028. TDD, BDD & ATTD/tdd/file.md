Chào bạn, đây là một chủ đề rất quan trọng và thú vị trong phát triển phần mềm: **Test-driven development (TDD - Phát triển hướng kiểm thử)**. 

Dưới đây, mình sẽ chia câu trả lời thành 3 phần đúng theo yêu cầu của bạn: 
1. Giải thích chi tiết 12 bức ảnh.
2. Giải thích lý thuyết và ví dụ dễ hiểu cho từng khái niệm.
3. Viết code Java (kèm giải thích chi tiết và lý thuyết bên cạnh).

---

### PHẦN 1: GIẢI THÍCH CHI TIẾT TỪNG HÌNH ẢNH

*   **Hình 1 (Agenda):** Đây là mục lục của bài học. Giảng viên sẽ giới thiệu: TDD là gì, Chu trình TDD, 3 nguyên tắc (luật) của TDD, Phân biệt Test-first và TDD, Tháp kiểm thử (Testing Pyramid), Chi phí sửa lỗi (Defect Cost), và Ưu điểm của TDD.
*   **Hình 2 (What is TDD?):** Hình ảnh minh họa chu trình cốt lõi của TDD gồm 3 bước lặp đi lặp lại: **Red** (Viết một test bị fail - màu đỏ), **Green** (Viết code để test đó pass - màu xanh lá), **Refactor** (Tối ưu lại code - màu xanh dương).
*   **Hình 3 (Three Laws of TDD):** 3 nguyên tắc bất hủ của TDD (do Uncle Bob đề xuất):
    1. Không được viết code chức năng (production code) trừ khi bạn đã viết một test bị fail.
    2. Không được viết test nhiều hơn mức cần thiết để test đó bị fail.
    3. Không được viết code chức năng nhiều hơn mức cần thiết để test đó pass.
*   **Hình 4 (Quote):** Câu nói: *"Nếu tôi chưa biết phải code nó như thế nào - ít nhất tôi cũng nên có khả năng tạo ra một bài test cho những gì tôi kỳ vọng nó sẽ làm."* (Tư duy cốt lõi: Hiểu rõ yêu cầu đầu ra trước khi bắt tay vào code).
*   **Hình 5 (Test-first vs TDD):** So sánh giữa việc chỉ viết test trước (Test-first) và quy trình TDD thực thụ. Test-first chỉ là một phần; TDD còn giúp định hình **Kiến trúc code (Code Architecture)** tốt hơn vì bạn phải thiết kế code sao cho dễ test.
*   **Hình 6 (Testing Pyramid):** Tháp kiểm thử. Đáy là **Unit Tests** (nhiều nhất, nhanh nhất, rẻ nhất). Giữa là **Integration Tests** (Kiểm thử tích hợp). Đỉnh là **GUI Tests** (Kiểm thử giao diện, chậm và tốn kém nhất).
*   **Hình 7 (Defect Cost Increase):** Biểu đồ cho thấy chi phí sửa một lỗi (bug) tăng theo vòng đời dự án. Sửa lỗi ở khâu Requirements rất rẻ, nhưng càng về sau (Design, Code, Test, đặc biệt là Maintenance - Bảo trì) thì chi phí càng tăng vọt.
*   **Hình 8 (TDD - Advantages):** Ưu điểm: Bao phủ test cao (Test Code Coverage), giảm lỗi trên production, cải thiện chất lượng code, có sẵn test tự động để hồi quy (regression testing), test như một dạng tài liệu code (documentation), giúp refactor code dễ dàng hơn.
*   **Hình 9, 11, 12 (TDD - Disadvantages):** Nhược điểm: Tốn công viết test (Test Code Coverage), cần thời gian bảo trì test, TDD đòi hỏi kỹ năng, cả team phải dùng chung, khó học, và rất khó áp dụng cho các hệ thống code cũ (legacy code).
*   **Hình 10 (Classes of Equivalence):** Phân vùng tương đương. Ví dụ với phép cộng máy tính, ta không cần test mọi số trên đời. Ta chia làm các nhóm: cộng 2 số dương, 2 số âm, dương + âm, số 0, tham số null, giá trị MAX, giá trị MIN. (Test đại diện cho từng nhóm là đủ).

---

### PHẦN 2: LÝ THUYẾT & VÍ DỤ DỄ HIỂU

**1. TDD là gì và Chu trình Red-Green-Refactor?**
*   **Lý thuyết:** Thay vì code trước rồi test sau, TDD bắt bạn **viết Test trước**. Nó giống như việc bạn viết kịch bản phim trước khi quay.
*   **Ví dụ:** Bạn muốn làm chức năng đăng nhập.
    *   *Red:* Viết test kiểm tra xem nhập đúng tài khoản/mật khẩu có đăng nhập được không. Chạy test -> Báo lỗi (vì chưa có code đăng nhập).
    *   *Green:* Viết code đăng nhập đơn giản nhất có thể để test trên pass.
    *   *Refactor:* Xem lại code đăng nhập, tối ưu hóa biến, sửa lại tên hàm cho đẹp mà không làm hỏng test.

**2. 3 Luật của TDD (Three Laws)**
*   **Luật 1:** Chưa có test fail -> Cấm viết code. (Tránh viết code thừa, code không ai dùng).
*   **Luật 2:** Viết test vừa đủ fail thôi. (Ví dụ chỉ cần test lỗi cú pháp thì dừng, đừng viết cả chục test case phức tạp lúc này).
*   **Luật 3:** Viết code vừa đủ để test pass. (Không code thêm các tính năng "tương lai" khi chưa được yêu cầu).

**3. Test-first vs TDD**
*   **Test-first:** Chỉ đơn giản là viết test trước khi code. Code xong test pass là xong.
*   **TDD:** Là một quy trình kỷ luật. Bạn viết test (Red), code cho pass (Green), sau đó **bắt buộc phải Refactor** (tối ưu thiết kế). TDD giúp bạn thiết kế hệ thống (Design) tốt hơn vì code phải dễ để test.

**4. Testing Pyramid (Tháp kiểm thử)**
*   **Unit Test (Đáy):** Test từng hàm, từng class nhỏ lẻ. Viết hàng nghìn cái cũng được, chạy tính bằng mili-giây.
*   **Integration Test (Giữa):** Test sự kết hợp giữa các module (ví dụ: API gọi xuống Database).
*   **GUI Test (Đỉnh):** Test bấm nút, nhập liệu trên giao diện. Rất chậm và dễ "flaky" (lúc pass lúc fail do mạng, do máy). TDD chủ yếu tập trung vào Unit Test.

**5. Defect Cost Increase (Chi phí sửa lỗi)**
*   **Lý thuyết:** Càng phát hiện lỗi muộn, chi phí sửa càng đắt.
*   **Ví dụ:** Lỗi sai yêu cầu phát hiện lúc vẽ thiết kế: sửa 5 phút. Lỗi đó để lọt lên production, khách hàng phàn nàn: mất vài ngày fix, đền bù hợp đồng, mất uy tín. TDD giúp phát hiện lỗi ngay lúc code (rẻ nhất).

**6. Classes of Equivalence (Phân vùng tương đương)**
*   **Lý thuyết:** Không thể test mọi trường hợp (vô hạn), nên chia dữ liệu thành các nhóm có hành vi giống nhau.
*   **Ví dụ:** Chức năng nhập tuổi (từ 18-60).
    *   Nhóm hợp lệ: 18, 30, 60 (chỉ cần test 1-2 giá trị đại diện).
    *   Nhóm không hợp lệ: 17 (nhỏ hơn), 61 (lớn hơn), "abc" (sai định dạng).

---

### PHẦN 3: VÍ DỤ CODE JAVA (KÈM GIẢI THÍCH VÀ LÝ THUYẾT)

Chúng ta sẽ áp dụng chu trình TDD để viết chức năng tính tổng 2 số (dựa trên Hình 10 - Classes of Equivalence). Chúng ta dùng thư viện **JUnit** (thư viện test phổ biến nhất trong Java).

#### Bước 1: RED (Viết test fail)
*   **Lý thuyết:** Tuân thủ Luật 1 (Chưa có test fail thì không được code). Ta chọn nhóm "Cộng 2 số dương".
*   **Code:**
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {

    @Test
    public void testAddTwoPositiveNumbers() {
        // 1. Khởi tạo đối tượng (Chưa hề tồn tại class này)
        Calculator calculator = new Calculator();
        
        // 2. Gọi hàm cần test (Chưa hề tồn tại hàm này)
        int result = calculator.add(2, 3);
        
        // 3. Kiểm tra kết quả mong đợi (2 + 3 = 5)
        assertEquals(5, result, "2 + 3 phải bằng 5");
    }
}
```
*   **Giải thích:** Lúc này, trình biên dịch sẽ báo lỗi đỏ lòm vì chưa có class `Calculator` và hàm `add`. Test đang **FAIL** (Thỏa mãn Luật 1).

#### Bước 2: GREEN (Viết code để test pass)
*   **Lý thuyết:** Tuân thủ Luật 3 (Viết code vừa đủ để pass). Không cần quan tâm số âm, số 0 vội, chỉ cần hàm cộng 2 số thôi.
*   **Code:**
```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```
*   **Giải thích:** Bây giờ chạy lại test ở Bước 1. Test sẽ báo **PASS** (Màu xanh). Chúng ta đã có một chức năng hoạt động đúng cơ bản.

#### Bước 3: REFACTOR (Tối ưu)
*   **Lý thuyết:** Code đã pass, nhưng hãy xem có gì cần sửa không. Hàm `add` hiện tại đã quá đơn giản và tối ưu, nên ta bỏ qua bước này và chuyển sang test case tiếp theo.

#### Bước 4: Lặp lại chu trình với các nhóm (Equivalence Classes) khác
*   **Lý thuyết:** Áp dụng Hình 10. Giờ ta test trường hợp "Cộng số âm", "Cộng số 0", và đặc biệt là "Tràn số" (MAX_VALUE).
*   **Viết thêm Test (RED):**
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {

    Calculator calculator = new Calculator();

    @Test
    public void testAddTwoPositiveNumbers() {
        assertEquals(5, calculator.add(2, 3)); // Đã pass
    }

    @Test
    public void testAddTwoNegativeNumbers() {
        assertEquals(-5, calculator.add(-2, -3), "-2 + -3 phải bằng -5");
    }

    @Test
    public void testAddWithZero() {
        assertEquals(5, calculator.add(5, 0), "5 + 0 phải bằng 5");
    }

    @Test
    public void testOverflow() { // Test trường hợp biên (Edge case)
        int max = Integer.MAX_VALUE;
        // Khi cộng MAX_VALUE với 1, nó sẽ bị tràn số (thành số âm trong Java)
        // Ta kỳ vọng code sẽ ném ra lỗi (Exception) hoặc xử lý được (tùy yêu cầu)
        // Ở đây ta giả sử hàm add của ta phải ném ra lỗi ArithmeticException
        assertThrows(ArithmeticException.class, () -> {
            calculator.add(max, 1); 
        });
    }
}
```
*   **Chạy test:** `testAddTwoNegativeNumbers` và `testAddWithZero` sẽ pass. Nhưng `testOverflow` sẽ **FAIL** (vì code hiện tại `return a + b` sẽ trả về số âm chứ không ném lỗi).
*   **Cập nhật Code (GREEN):**
```java
public class Calculator {
    public int add(int a, int b) {
        // Sử dụng Math.addExact để tự động ném lỗi nếu tràn số
        return Math.addExact(a, b); 
    }
}
```
*   **Giải thích:** Sau khi sửa code, chạy lại toàn bộ test. Tất cả đều **PASS (Xanh)**. Chúng ta lại tiếp tục Refactor nếu cần.

---
**Tổng kết lại:** TDD không phải là việc viết thật nhiều test cho có. TDD là một quy trình thiết kế (Design) phần mềm. Việc viết test trước ép bạn phải suy nghĩ về "Giao diện" của code (API), cách truyền tham số, cách trả về kết quả trước khi bắt tay vào việc code logic phức tạp. Nhờ đó, code của bạn sẽ sạch, ít bug và cực kỳ dễ bảo trì!

