Chào bạn, đây là một chuỗi bài học rất hay về quy trình phát triển phần mềm hiện đại, cụ thể là áp dụng **TDD (Test-Driven Development - Phát triển hướng kiểm thử)**, **BDD (Behavior-Driven Development - Phát triển hướng hành vi)** và **ATDD (Acceptance Test-Driven Development - Phát triển hướng kiểm thử chấp nhận)**. 

Bài toán trong chuỗi ảnh là xây dựng một hệ thống **Template (Mẫu) Email** cho phép cá nhân hóa nội dung (giống như MailChimp hay SendGrid).

Dưới đây, tôi sẽ giải thích chi tiết từng hình ảnh, sau đó đi sâu vào lý thuyết, code và giải thích từng bước theo đúng yêu cầu của bạn.

---

### PHẦN 1: GIẢI THÍCH CHI TIẾT TỪNG HÌNH ẢNH

**Hình 1: Application: Description (Mô tả ứng dụng)**
*   **Nội dung:** Giới thiệu bài toán cần giải quyết. Chúng ta cần xây dựng một hệ thống cộng tác và giao tiếp sử dụng email-template (mẫu email) để cá nhân hóa bản tin (newsletter).
*   **Ví dụ:** Các hệ thống nổi tiếng như MailChimp, SendPulse, SendGrid.

**Hình 2: REQUIREMENTS (Yêu cầu)**
*   **Nội dung:** Định nghĩa các yêu cầu theo cú pháp Gherkin (thuộc BDD).
*   **Chi tiết:**
    *   Template email phải có các biến giữ chỗ (variable placeholders) - ví dụ: `${firstName}`.
    *   Hệ thống phải thay thế các placeholder này bằng giá trị được cung cấp tại thời điểm chạy (runtime).
    *   Nếu có biến không được điền giá trị, hệ thống phải ném ra lỗi (throw an error).

**Hình 3: Scenario #1 - Test (Kịch bản 1 - Viết Test)**
*   **Nội dung:** Bắt đầu viết Unit Test đầu tiên (Giai đoạn RED của TDD). Hàm test có tên `shouldSetValueIntoPlaceholder()`.
*   **Chi tiết:** Khởi tạo template `"Hello, ${firstName}"`, truyền giá trị `"John"` vào biến `firstName`, và kỳ vọng (assert) kết quả trả về là `"Hello, John"`. Lưu ý ở bước này, hàm được gọi là `evaluate()`.

**Hình 4: Original Version of a Template class (Phiên bản gốc của class Template)**
*   **Nội dung:** Tạo một class `Template` rỗng (Skeleton code). Constructor nhận vào một String, hàm `set()` nhận 2 String, và hàm `evaluate()` trả về `null`.
*   **Mục đích:** Tạo đủ cấu trúc để code có thể biên dịch (compile) và chạy test, dù test chắc chắn sẽ fail (trả về null thay vì "Hello, John").

**Hình 5: Run first test – RED Stage (Chạy test đầu tiên - Giai đoạn Đỏ)**
*   **Nội dung:** Nút bấm "FAIL" màu đỏ. Test đã thất bại đúng như dự kiến. Thông điệp: "Chúng ta phải triển khai phiên bản code dễ nhất."

**Hình 6: Scenario #2 - Test**
*   **Nội dung:** Viết thêm một test mới `shouldProcessDifferentValues()`. Lần này truyền vào `"William"`. 
*   **Chi tiết:** Lưu ý rằng tên hàm đã đổi từ `evaluate()` sang `createEmailText()`. Kỳ vọng trả về `"Hello, William"`.

**Hình 7: Scenario #2 - Solution (Giải pháp cho Kịch bản 2)**
*   **Nội dung:** Viết code để test vượt qua (Giai đoạn GREEN). 
*   **Chi tiết:** Thêm biến `variableValue`. Hàm `set()` gán giá trị. Hàm `createEmailText()` trả về `"Hello, " + variableValue`. (Code này rất "ngây thơ", nó chỉ giải quyết đúng trường hợp `"Hello, "` mà thôi).

**Hình 8: Scenario #3 - Test**
*   **Nội dung:** Viết test `shouldProcessDifferentMailTemplates()`. Lần này template là `"Hi, ${firstName}"`. Kỳ vọng trả về `"Hi, William"`.
*   **Mục đích:** Test này sẽ làm sập code ở Hình 7 (vì code đang hardcode chữ `"Hello, "`).

**Hình 9: Scenario #3 - Solution**
*   **Nội dung:** Cập nhật code để vượt qua test. Thay vì hardcode, ta lưu lại `templateText` ban đầu. Sử dụng hàm `replaceAll` với Regex `\\$\\{firstName\\}` để thay thế.
*   **Chi tiết:** Code đã bắt đầu tổng quát hóa (generalize).

**Hình 10: Scenario #4 - Test**
*   **Nội dung:** Test `shouldProcessDifferentPlaceholdersAndDifferentValues()`. Template có 2 biến: `"Hello dear ${firstName} ${lastName}"`.
*   **Mục đích:** Đảm bảo hệ thống có thể xử lý nhiều placeholder cùng lúc.

**Hình 11: Scenario #4 - Solution**
*   **Nội dung:** Nâng cấp code. Sử dụng `Map<String, String>` để lưu trữ danh sách các cặp (Tên biến - Giá trị). Duyệt qua Map, dùng vòng lặp `for` và `replaceAll` cho từng biến.

**Hình 12: Scenario #5 - Test**
*   **Nội dung:** Test `shouldThrowExceptionWhenNotAllVariablesArePopulated()`. Cố tình tạo template có 2 biến nhưng không truyền giá trị nào cả. Kỳ vọng hệ thống sẽ ném ra `RuntimeException` với thông báo `"Not enough values for template"`.

**Hình 13: Scenario #5 - Solution**
*   **Nội dung:** Thêm logic kiểm tra. Sau khi thay thế các biến, nếu chuỗi kết quả vẫn còn chứa `"${"` và `"}"`, nghĩa là có biến chưa được thay thế. Lúc này, ném ra ngoại lệ (Exception).

**Hình 14: Before VS After (Trước và Sau)**
*   **Nội dung:** So sánh class `Template` ban đầu (rỗng, trả về null) và class `Template` hoàn chỉnh (sử dụng Map, vòng lặp, regex, và xử lý ngoại lệ).

---

### PHẦN 2: GIẢI THÍCH LÝ THUYẾT DỄ HIỂU

1.  **TDD (Test-Driven Development):** Lập trình viên **viết Test trước**, sau đó mới viết code để làm cho Test đó chạy đúng.
    *   **Quy trình Red - Green - Refactor:**
        *   🔴 **Red:** Viết test, chạy và thấy nó Fail (vì chưa có code).
        *   🟢 **Green:** Viết code đơn giản nhất có thể để test Pass (chạy đúng).
        *   🔄 **Refactor:** Dọn dẹp lại code cho sạch sẽ, dễ đọc mà không làm hỏng test.
2.  **BDD (Behavior-Driven Development):** Tập trung vào **hành vi** của hệ thống từ góc nhìn của người dùng. Thường sử dụng cú pháp **Gherkin** (Given - When - Then: Biết rằng... - Khi... - Thì...).
3.  **ATDD (Acceptance Test-Driven Development):** Tương tự TDD nhưng viết các bài test ở mức độ chấp nhận (Acceptance). Thường được viết dựa trên các yêu cầu của khách hàng (như Hình 2).

---

### PHẦN 3: CODE VÀ GIẢI THÍCH CHI TIẾT (KẾT HỢP LÝ THUYẾT)

Dưới đây là mã nguồn hoàn chỉnh qua từng kịch bản, kèm theo lý thuyết và giải thích chi tiết.

#### Kịch bản 1 & 2: Khởi tạo và Test đầu tiên (Giai đoạn RED -> GREEN)

**1. Viết Test (RED)**
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateTest {
    @Test
    void shouldSetValueIntoPlaceholder() {
        // GIVEN: Tạo đối tượng với template cho trước
        Template template = new Template("Hello, ${firstName}");
        
        // WHEN: Gán giá trị cho placeholder và tạo email
        template.set("firstName", "John");
        String emailText = template.createEmailText();
        
        // THEN: Kiểm tra kết quả
        assertEquals("Hello, John", emailText);
    }
}
```
*   **Giải thích lý thuyết:** Ở TDD, ta viết Test trước. Cấu trúc `GIVEN - WHEN - THEN` là đặc trưng của BDD, giúp test dễ đọc như một câu chuyện. Lúc này, code chưa có hoặc `createEmailText()` trả về null, nên test sẽ **FAIL (Red)**.

**2. Code giải pháp cơ bản (GREEN)**
```java
public class Template {
    private String variableValue;

    public Template(String templateText) {
        // Lúc này chỉ cần lưu constructor, chưa xử lý gì nhiều
    }

    public void set(String placeholderName, String variableValue) {
        // Lưu lại giá trị người dùng truyền vào
        this.variableValue = variableValue;
    }

    public String createEmailText() {
        // Trả về đúng như kỳ vọng của Test
        return "Hello, " + this.variableValue;
    }
}
```
*   **Giải thích code:** Đây là bước **GREEN** (làm test chạy đúng). Code này cố tình viết rất "ngốc nghếch" (hardcode chữ "Hello, "). Tại sao? Vì nguyên tắc TDD là: *Chỉ viết đủ code để test hiện tại vượt qua, không viết thừa.* 

#### Kịch bản 3: Đa dạng hóa Template (Giai đoạn REFACTOR)

**1. Viết thêm Test**
```java
    @Test
    void shouldProcessDifferentMailTemplates() {
        // GIVEN: Template khác, không phải "Hello"
        Template template = new Template("Hi, ${firstName}");
        
        // WHEN
        template.set("firstName", "William");
        String emailText = template.createEmailText();
        
        // THEN
        assertEquals("Hi, William", emailText);
    }
```
*   **Giải thích:** Test này sẽ làm code ở Kịch bản 2 bị lỗi (vì code đang hardcode chữ `"Hello, "`). Test lại **FAIL (Red)**.

**2. Cập nhật Code (GREEN + REFACTOR)**
```java
public class Template {
    private String variableValue;
    private String templateText; // Thêm biến lưu lại template gốc

    public Template(String templateText) {
        this.templateText = templateText; // Lưu lại template
    }

    public void set(String placeholderName, String variableValue) {
        this.variableValue = variableValue;
    }

    public String createEmailText() {
        // Sử dụng Regex để tìm và thay thế placeholder
        // "\\$\\{firstName\\}" là Regex tương đương với chuỗi "${firstName}"
        return templateText.replaceAll("\\$\\{firstName\\}", variableValue);
    }
}
```
*   **Giải thích code:** Ta không thể hardcode nữa. Ta lưu lại `templateText` ban đầu. Dùng hàm `replaceAll` với biểu thức chính quy (Regex) để thay thế chính xác chuỗi `${firstName}` bằng giá trị thực tế. Bước này gọi là **REFACTOR** (làm cho code thông minh hơn mà vẫn giữ test xanh).

#### Kịch bản 4: Nhiều Placeholder (Giai đoạn Tổng quát hóa)

**1. Viết thêm Test**
```java
    @Test
    void shouldProcessDifferentPlaceholdersAndDifferentValues() {
        // GIVEN: Có 2 biến
        Template template = new Template("Hello dear ${firstName} ${lastName}");
        
        // WHEN: Truyền 2 giá trị
        template.set("firstName", "William");
        template.set("lastName", "Harrison");
        String emailText = template.createEmailText();
        
        // THEN
        assertEquals("Hello dear William Harrison", emailText);
    }
```
*   **Giải thích:** Test này lại tiếp tục **FAIL (Red)** vì code cũ chỉ biết thay thế mỗi `${firstName}` và không lưu được nhiều giá trị.

**2. Cập nhật Code bằng Map (GREEN)**
```java
import java.util.HashMap;
import java.util.Map;

public class Template {
    // Sử dụng Map để lưu nhiều cặp (Tên biến -> Giá trị)
    private Map<String, String> placeholdersValuesMapping;
    private String templateText;

    public Template(String templateText) {
        this.templateText = templateText;
        // Khởi tạo Map
        this.placeholdersValuesMapping = new HashMap<>(); 
    }

    public void set(String placeholderName, String variableValue) {
        // Đưa cặp key-value vào Map
        placeholdersValuesMapping.put(placeholderName, variableValue);
    }

    public String createEmailText() {
        String result = templateText;
        // Duyệt qua tất cả các cặp biến đã được set
        for (Map.Entry<String, String> entry : placeholdersValuesMapping.entrySet()) {
            // Tạo Regex động dựa trên tên biến: ví dụ \$\{firstName\}
            String regex = "\\$\\{" + entry.getKey() + "\\}";
            // Thay thế tất cả các lần xuất hiện của biến bằng giá trị tương ứng
            result = result.replaceAll(regex, entry.getValue());
        }
        return result;
    }
}
```
*   **Giải thích code:** Đây là bước đột phá về thiết kế (Refactor). Thay vì cố định 1 biến, ta dùng `Map` để lưu trữ N biến. Khi tạo email, ta lặp qua Map, tạo Regex động (`"\\$\\{" + entry.getKey() + "\\}"`) và thay thế hàng loạt. Code này giải quyết được mọi trường hợp có nhiều biến.

#### Kịch bản 5: Xử lý ngoại lệ (Edge Case)

**1. Viết Test cho trường hợp lỗi**
```java
import static org.junit.jupiter.api.Assertions.assertThrows;

    @Test
    void shouldThrowExceptionWhenNotAllVariablesArePopulated() {
        // GIVEN
        Template template = new Template("Hello dear ${firstName} ${lastName}");
        
        // WHEN: Cố tình KHÔNG gọi hàm set() nào cả, hoặc chỉ set 1 biến
        // template.set("firstName", "William"); 
        
        // THEN: Kỳ vọng sẽ ném ra RuntimeException với thông báo cụ thể
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            template.createEmailText();
        });
        
        assertEquals("Not enough values for template", exception.getMessage());
    }
```

**2. Cập nhật Code (Hoàn thiện)**
```java
    public String createEmailText() {
        String result = templateText;
        for (Map.Entry<String, String> entry : placeholdersValuesMapping.entrySet()) {
            String regex = "\\$\\{" + entry.getKey() + "\\}";
            result = result.replaceAll(regex, entry.getValue());
        }
        
        // KIỂM TRA LỖI: Nếu sau khi thay thế mà vẫn còn "${" và "}"
        // tức là còn placeholder chưa được cung cấp giá trị
        if (result.contains("${") && result.contains("}")) {
            throw new RuntimeException("Not enough values for template");
        }
        
        return result;
    }
```
*   **Giải thích code:** Bước này tuân thủ yêu cầu thứ 3 của BDD (Hình 2): "Nếu có biến không được điền -> ném lỗi". Sau khi lặp thay thế hết những gì có trong Map, ta kiểm tra lại chuỗi kết quả. Nếu vẫn còn dấu hiệu của placeholder (`${...}`) nghĩa là người dùng quên truyền giá trị, ta chủ động ném ra `RuntimeException` để cảnh báo lập trình viên/người dùng.

---

### TỔNG KẾT
Qua chuỗi bài học này, bạn đã thấy sức mạnh của **TDD**:
1.  Bạn không bao giờ viết thừa code (YAGNI - You Aren't Gonna Need It).
2.  Code được thiết kế từng bước một, từ đơn giản (hardcode) đến phức tạp (dùng Map, Regex).
3.  Bạn luôn có một bộ Test (Unit Test) đi kèm để bảo vệ code. Mỗi khi bạn sửa code (Refactor), bạn chỉ cần chạy lại Test, nếu nó vẫn Xanh (Green) thì bạn biết mình không làm hỏng tính năng cũ.

Hy vọng phần giải thích này giúp bạn nắm rõ bài học! Nếu cần làm rõ thêm phần nào, hãy cho tôi biết nhé.


Chào bạn, file `Template.java` bạn vừa gửi chính là **phiên bản hoàn chỉnh cuối cùng** (Final Solution) sau khi đã trải qua toàn bộ 5 kịch bản TDD (từ Hình 13 & 14 trong bài học). 

Đây là một class rất tốt, thể hiện rõ tư duy Refactor (tái cấu trúc) từ một class rỗng trả về `null` thành một công cụ xử lý chuỗi hoàn chỉnh. 

Dưới đây, tôi sẽ phân tích chi tiết từng dòng code, giải thích các kỹ thuật được sử dụng, và chỉ ra một vài điểm có thể tối ưu thêm (Code Review) để bạn hiểu sâu hơn.

---

### 1. GIẢI THÍCH CHI TIẾT TỪNG PHẦN CỦA CODE

#### Phần 1: Khai báo và Khởi tạo (Dòng 1 - 15)
```java
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Template {

    private Map<String, String> placeholdersValuesMapping;
    private String templateText;

    // Khối khởi tạo Instance (Instance Initializer Block)
    {
        placeholdersValuesMapping = new HashMap<>();
    }

    public Template(String templateText) {
        this.templateText = templateText;
    }
```
*   **Lý thuyết:** 
    *   `Map<String, String>`: Sử dụng cấu trúc dữ liệu Key-Value để lưu tên biến (Key) và giá trị thực tế (Value). Điều này giải quyết bài toán truyền nhiều biến (Kịch bản 4) một cách linh hoạt.
    *   **Khối khởi tạo `{ ... }` (Instance Initializer Block):** Đoạn code này sẽ tự động chạy **trước** khi constructor được gọi. Nó tương đương với việc bạn viết `private Map<String, String> placeholdersValuesMapping = new HashMap<>();` ở trên cùng. 
*   **Giải thích code:** Hàm khởi tạo `Template(String templateText)` chỉ làm một việc duy nhất là lưu lại chuỗi mẫu ban đầu (ví dụ: `"Hello, ${firstName}"`).

#### Phần 2: Hàm `set` (Dòng 17 - 19)
```java
    public void set(String placeholderName, String variableValue) {
        placeholdersValuesMapping.put(placeholderName, variableValue);
    }
```
*   **Giải thích code:** Mỗi khi người dùng gọi hàm này, nó sẽ đưa cặp (Tên biến - Giá trị) vào trong `Map`. Ví dụ: `placeholdersValuesMapping.put("firstName", "John")`.

#### Phần 3: Hàm `createEmailText` - Trái tim của class (Dòng 21 - 32)
```java
    public String createEmailText() {
        String result = templateText; // 1. Tạo bản sao để không làm hỏng template gốc
        
        // 2. Duyệt qua tất cả các biến đã được set
        for (Entry<String, String> entry : placeholdersValuesMapping.entrySet()) {
            // 3. Tạo Regex động
            String regex = "\\$\\{" + entry.getKey() + "\\}"; 
            
            // 4. Thay thế
            result = result.replaceAll(regex, entry.getValue());
        }

        // 5. Kiểm tra ngoại lệ (Yêu cầu từ BDD)
        if (result.contains("${") && result.contains("}")) {
            throw new RuntimeException("Not enough values for template");
        }

        return result;
    }
```
*   **Giải thích chi tiết:**
    *   **Bước 3 (Tạo Regex động):** Đây là kỹ thuật quan trọng nhất. 
        *   Chuỗi `"\\$"` trong Java có nghĩa là ký tự `$` (dấu gạch chéo ngược `\` dùng để escape ký tự đặc biệt). Tương tự `"\\{"` là `{` và `"\\}"` là `}`.
        *   Nếu key là `"firstName"`, biến `regex` sẽ trở thành chuỗi: `\$\{firstName\}`. Hàm `replaceAll` sẽ dùng Regex này để tìm chính xác cụm `${firstName}` trong chuỗi và thay bằng `"John"`.
    *   **Bước 5 (Kiểm tra lỗi):** Sau khi thay thế hết những gì có trong `Map`, nếu chuỗi `result` vẫn còn chứa `"${"` và `"}"`, chứng tỏ trong template gốc có biến nhưng người dùng quên gọi hàm `set()` cho biến đó. Hệ thống lập tức ném ra `RuntimeException` để cảnh báo (Đúng như Kịch bản 5 yêu cầu).

---

### 2. NHẬN XÉT VÀ ĐÁNH GIÁ (CODE REVIEW)

Code của bạn đã hoàn toàn thỏa mãn **3 yêu cầu của BDD** (Hình 2):
1. Template có placeholders (biến `${...}`).
2. Thay thế biến bằng giá trị runtime (dùng vòng lặp Map + Regex).
3. Báo lỗi nếu thiếu biến (dùng `throw new RuntimeException`).

**Tuy nhiên, dưới góc độ của một lập trình viên chuyên nghiệp, có 2 điểm nhỏ bạn có thể cải thiện để code "sạch" (Clean Code) hơn:**

**Điểm 1: Khối khởi tạo (Instance Initializer Block)**
Việc dùng khối `{ ... }` để khởi tạo Map là hợp lệ, nhưng hiếm khi được sử dụng trong thực tế vì nó gây rối mắt. 
*   *Cách viết phổ biến hơn:*
```java
public class Template {
    // Khởi tạo trực tiếp tại nơi khai báo
    private Map<String, String> placeholdersValuesMapping = new HashMap<>();
    private String templateText;
    
    // Constructor bỏ trống hoặc chỉ nhận template
    public Template(String templateText) {
        this.templateText = templateText;
    }
    // ...
}
```

**Điểm 2: Lỗ hổng nhỏ trong logic kiểm tra ngoại lệ**
Đoạn `if (result.contains("${") && result.contains("}"))` có một lỗ hổng (Edge case). 
*   *Kịch bản lỗi:* Nếu tên của người dùng (giá trị truyền vào) có chứa ký tự `${` hoặc `}`, ví dụ: `template.set("firstName", "John ${Doe}");`. Thì khi render ra chuỗi `"Hello, John ${Doe}"`, đoạn code check lỗi của bạn sẽ tưởng nhầm là còn biến chưa thay thế và ném Exception sai sự thật.
*   *Cách sửa tối ưu hơn (Dùng Regex để check):* Thay vì dùng `contains`, ta dùng Regex để tìm xem có pattern `${...}` nào còn sót lại không.
```java
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// ... bên trong hàm createEmailText
// Tìm xem còn cụm ${...} nào không
Matcher matcher = Pattern.compile("\\$\\{.*?\\}").matcher(result);
if (matcher.find()) {
    throw new RuntimeException("Not enough values for template");
}
```

---

### 3. LIÊN HỆ VỚI LÝ THUYẾT TDD (RED - GREEN - REFACTOR)

Nhìn vào file code cuối cùng này, bạn có thể thấy rõ sức mạnh của **TDD**:
*   **Nếu không có TDD:** Bạn sẽ cố gắng viết ngay một class Template phức tạp với đủ thứ Regex, Map, và Exception ngay từ đầu. Rất dễ bị bug và không biết bắt đầu từ đâu.
*   **Với TDD:** 
    *   Bạn bắt đầu bằng một hàm `createEmailText()` trả về `null` (Giai đoạn RED).
    *   Test số 1 pass -> bạn viết `return "Hello, " + value;` (GREEN).
    *   Test số 2 fail -> bạn thêm Regex `replaceAll` (GREEN).
    *   Test số 3 fail -> bạn nâng cấp lên `Map` (REFACTOR/GREEN).
    *   Test số 4 fail -> bạn thêm logic kiểm tra Exception (GREEN).
*   **Kết quả:** Bạn có một file `Template.java` hoàn chỉnh như hiện tại. Mọi dòng code trong file này đều được sinh ra để **phục vụ cho một bài Test cụ thể**, không có dòng code nào là dư thừa!

Chúc bạn học tốt môn TDD/BDD này nhé! Nếu cần giải thích thêm phần nào (ví dụ về cách viết Regex trong Java), bạn cứ hỏi.
