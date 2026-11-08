Chào bạn, theo yêu cầu của bạn, mình sẽ giải thích chi tiết nội dung trong 3 bức ảnh bạn gửi, sau đó đi sâu vào lý thuyết, cung cấp ví dụ code và giải thích cặn kẽ từng phần. Tất cả sẽ bằng tiếng Việt để bạn dễ theo dõi nhé.

---

### 📸 1. Giải thích chi tiết nội dung 3 hình ảnh

Dưới đây là tóm tắt nội dung của từng hình ảnh trong khóa học Java của bạn:

#### Hình 1: Lộ trình học tập (Agenda)
Bức ảnh này là một slide tổng quan về các chủ đề sẽ được học trong phần xử lý ngày giờ của Java:
*   **Khái niệm cơ bản:** Tìm hiểu về giờ GMT, UTC và Unix Epoch (mốc thời gian Unix).
*   **Các lớp cốt lõi:** Làm quen với `java.util.Calendar` và `java.util.Date`.
*   **Chuyển đổi:** Cách chuyển đổi qua lại giữa `Date` và `Calendar`.
*   **Định dạng:** Học cách phân tích (parse) và định dạng (format) ngày tháng, sử dụng lớp `java.text.SimpleDateFormat`.
*   **Múi giờ:** Tìm hiểu lớp `java.util.TimeZone` để xử lý múi giờ.
*   **Tổng kết:** Điểm qua những hạn chế (drawbacks) của `Date` và `Calendar`.

#### Hình 2: Tiêu chuẩn thời gian (Time Standards)
Hình này minh họa bản đồ thế giới với các múi giờ được biểu diễn dưới dạng số âm và dương so với múi giờ gốc (múi số 0). Nó giải thích khái niệm:
*   **UTC (Coordinated Universal Time):** Là tiêu chuẩn thời gian quốc tế.
*   **Các múi giờ (Time Zones):** Được biểu diễn dưới dạng `UTC+` hoặc `UTC-`. Ví dụ, Việt Nam là `UTC+7`, nghĩa là thời gian ở Việt Nam đi trước giờ UTC 7 tiếng.

#### Hình 3: Hạn chế của Date và Calendar
Bức ảnh này liệt kê những nhược điểm chính của hai lớp `Date` và `Calendar` trong Java:
*   **Thread safety (An toàn luồng):** Đây là vấn đề lớn nhất. Các lớp này **không an toàn** khi được sử dụng trong môi trường đa luồng (multi-threaded). Nếu nhiều luồng cùng truy cập và thay đổi một đối tượng `Calendar`, dữ liệu có thể bị sai lệch.
*   **API design and ease of understanding (Thiết kế API và tính dễ hiểu):** API của chúng được đánh giá là khó sử dụng và dễ gây nhầm lẫn. Ví dụ, tháng trong `Calendar` bắt đầu từ `0` (tháng 1 là `0`), trong khi `Date` lại có những quy ước khác.
*   **Zoned date and time (Xử lý múi giờ):** Việc xử lý múi giờ với các lớp này rất phức tạp và dễ xảy ra lỗi.

---

### 📚 2. Lý thuyết chi tiết và Ví dụ minh họa

Phần này sẽ đào sâu vào từng khái niệm trong lộ trình học, kèm theo ví dụ code cụ thể.

#### 2.1. GMT, UTC và Unix Epoch

*   **GMT (Greenwich Mean Time):** Giờ trung bình tại Đài thiên văn Hoàng gia Greenwich, Anh. Đây là một tiêu chuẩn thời gian cũ, dựa trên chuyển động quay của Trái Đất.
*   **UTC (Coordinated Universal Time):** Tiêu chuẩn thời gian hiện đại, chính xác hơn GMT. Nó dựa trên đồng hồ nguyên tử nhưng có thêm giây nhuận để đồng bộ với chuyển động quay của Trái Đất. Ngày nay, UTC được dùng làm múi giờ gốc (múi số 0) trên toàn thế giới.
*   **Unix Epoch (Thời gian Unix):** Là một hệ thống mô tả một thời điểm cụ thể, được tính bằng **số giây** (hoặc mili-giây) đã trôi qua kể từ **00:00:00 UTC ngày 1 tháng 1 năm 1970**. Đây là một mốc thời gian cố định, không phụ thuộc vào múi giờ.

Trong Java, lớp `java.util.Date` thực chất **lưu trữ một giá trị `long` chính là số mili-giây kể từ Unix Epoch**.

#### 2.2. Lớp `java.util.Date`

*   **Lý thuyết:** Đại diện cho một thời điểm cụ thể (một mốc thời gian), chính xác đến mili-giây. Tuy nhiên, nó không chứa thông tin về múi giờ.
*   **Ví dụ và giải thích code:**

```java
import java.util.Date;

public class DateExample {
    public static void main(String[] args) {
        // 1. Tạo đối tượng Date cho thời điểm hiện tại
        Date now = new Date();
        System.out.println("Thời gian hiện tại: " + now.toString()); // Hiển thị theo múi giờ mặc định của máy

        // 2. Tạo đối tượng Date từ một mốc thời gian (Unix Epoch)
        // 0 mili-giây kể từ 01/01/1970 00:00:00 UTC
        Date epoch = new Date(0L);
        System.out.println("Thời điểm gốc (Epoch): " + epoch.toString()); // Ví dụ: Thu Jan 01 07:00:00 ICT 1970 (nếu múi giờ là UTC+7)

        // 3. Lấy số mili-giây kể từ Unix Epoch của đối tượng hiện tại
        long currentTimeMillis = now.getTime();
        System.out.println("Số mili-giây từ Epoch đến hiện tại: " + currentTimeMillis);
    }
}
```

**Giải thích:**
*   `new Date()`: Tạo một đối tượng `Date` chứa thời điểm hiện tại.
*   `new Date(0L)`: Tạo một đối tượng `Date` từ một giá trị `long` (0), tương ứng với mốc thời gian gốc (Unix Epoch). Khi in ra, nó sẽ hiển thị theo múi giờ hệ thống của bạn.
*   `getTime()`: Phương thức quan trọng nhất, trả về số mili-giây kể từ Unix Epoch.

#### 2.3. Lớp `java.util.Calendar`

*   **Lý thuyết:** Là một lớp trừu tượng cung cấp các phương thức để làm việc với các thành phần của ngày tháng (năm, tháng, ngày, giờ, phút, giây) và thực hiện các phép tính toán (cộng, trừ ngày tháng).
*   **Ví dụ và giải thích code:**

```java
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarExample {
    public static void main(String[] args) {
        // 1. Lấy một đối tượng Calendar cho thời điểm hiện tại
        Calendar cal = Calendar.getInstance();

        // 2. Lấy các thành phần của ngày tháng
        int year = cal.get(Calendar.YEAR);
        // LƯU Ý: Tháng trong Calendar bắt đầu từ 0 (tháng 1 là 0)
        int month = cal.get(Calendar.MONTH) + 1; 
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY); // Định dạng 24h
        System.out.printf("Hôm nay là: %d/%d/%d %d giờ\n", day, month, year, hour);

        // 3. Thay đổi các thành phần (ví dụ: đặt lại ngày)
        cal.set(Calendar.YEAR, 2025);
        cal.set(Calendar.MONTH, Calendar.JANUARY); // Hoặc 0
        cal.set(Calendar.DAY_OF_MONTH, 1);
        System.out.println("Ngày sau khi thay đổi: " + cal.getTime());

        // 4. Thực hiện phép toán cộng/trừ ngày tháng
        cal.add(Calendar.MONTH, 1); // Cộng thêm 1 tháng
        System.out.println("Sau khi cộng 1 tháng: " + cal.getTime());

        // 5. Chuyển đổi Calendar sang Date
        java.util.Date dateFromCal = cal.getTime();
    }
}
```

**Giải thích:**
*   `Calendar.getInstance()`: Tạo một đối tượng `Calendar` (thực chất là `GregorianCalendar`) với thời gian hiện tại.
*   `get(field)`: Lấy giá trị của một trường (ví dụ: `YEAR`, `MONTH`).
*   `set(field, value)`: Đặt giá trị cho một trường.
*   `add(field, amount)`: Cộng hoặc trừ một lượng thời gian vào một trường cụ thể.
*   `getTime()`: Chuyển đổi đối tượng `Calendar` hiện tại thành một đối tượng `java.util.Date`.

#### 2.4. Chuyển đổi giữa Date và Calendar

*   **Từ `Calendar` sang `Date`:** Dùng phương thức `cal.getTime()` (như ví dụ trên).
*   **Từ `Date` sang `Calendar`:** Dùng phương thức `cal.setTime(date)`.

#### 2.5. Định dạng và Phân tích ngày tháng với `SimpleDateFormat`

*   **Lý thuyết:** `SimpleDateFormat` là một lớp cụ thể để định dạng (chuyển `Date` thành `String`) và phân tích (chuyển `String` thành `Date`) ngày tháng theo một mẫu (pattern) nhất định.
*   **Ví dụ và giải thích code:**

```java
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class SimpleDateFormatExample {
    public static void main(String[] args) throws ParseException {
        // Định dạng: yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // 1. Định dạng (Format): Date -> String
        Date now = new Date();
        String formattedDate = sdf.format(now);
        System.out.println("Chuỗi ngày tháng đã định dạng: " + formattedDate); // Ví dụ: 08/11/2026 10:41:00

        // 2. Phân tích (Parse): String -> Date
        String dateString = "25/12/2025 08:00:00";
        Date christmas = sdf.parse(dateString);
        System.out.println("Đối tượng Date sau khi parse: " + christmas);
    }
}
```

**Giải thích:**
*   `new SimpleDateFormat("pattern")`: Tạo một đối tượng định dạng với mẫu mong muốn. Các ký tự như `dd` (ngày), `MM` (tháng), `yyyy` (năm), `HH` (giờ 24h), `mm` (phút), `ss` (giây) là các trường định dạng.
*   `sdf.format(date)`: Chuyển đổi một đối tượng `Date` thành một `String` theo mẫu.
*   `sdf.parse(string)`: Chuyển đổi một `String` thành một đối tượng `Date`. Phương thức này có thể ném ra ngoại lệ `ParseException` nếu chuỗi không đúng định dạng.

#### 2.6. Xử lý múi giờ với `TimeZone`

*   **Lý thuyết:** Lớp `TimeZone` đại diện cho một múi giờ cụ thể. Nó rất hữu ích khi bạn muốn hiển thị hoặc làm việc với thời gian ở các khu vực khác nhau trên thế giới.
*   **Ví dụ và giải thích code:**

```java
import java.util.TimeZone;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class TimeZoneExample {
    public static void main(String[] args) {
        // Lấy múi giờ của Việt Nam
        TimeZone vnTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        // Lấy múi giờ của New York
        TimeZone nyTimeZone = TimeZone.getTimeZone("America/New_York");

        // Tạo một đối tượng Calendar và đặt múi giờ
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(vnTimeZone);
        System.out.println("Giờ hiện tại ở Việt Nam: " + cal.getTime());

        // Tạo một SimpleDateFormat và đặt múi giờ
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(nyTimeZone);
        System.out.println("Giờ hiện tại ở New York: " + sdf.format(new java.util.Date()));
    }
}
```

**Giải thích:**
*   `TimeZone.getTimeZone("ID")`: Lấy một đối tượng `TimeZone` dựa trên ID của múi giờ (ví dụ: "Asia/Ho_Chi_Minh").
*   `cal.setTimeZone(tz)`: Đặt múi giờ cho đối tượng `Calendar`. Khi gọi `cal.getTime()`, nó sẽ trả về một `Date` (vẫn là mốc thời gian tuyệt đối), nhưng khi `toString()` sẽ hiển thị theo múi giờ đã đặt.
*   `sdf.setTimeZone(tz)`: Đặt múi giờ cho đối tượng `SimpleDateFormat`. Điều này ảnh hưởng đến cách `format()` hiển thị thời gian.

---

### ⚠️ 3. Phân tích chi tiết các hạn chế (Disadvantages)

Như đã đề cập trong Hình 3, `Date` và `Calendar` có những nhược điểm lớn, khiến chúng không được khuyến khích sử dụng trong các dự án hiện đại.

#### 3.1. Vấn đề Thread Safety (An toàn luồng)
Đây là vấn đề nghiêm trọng nhất. Cả `java.util.Date` và `java.util.Calendar` đều **không an toàn** khi được sử dụng trong môi trường đa luồng.
*   **Nguyên nhân:** Các lớp này có thể thay đổi trạng thái (mutable). Ví dụ, `Calendar` có các phương thức như `add()` hoặc `set()` để thay đổi giá trị bên trong nó. Nếu nhiều luồng cùng gọi các phương thức này trên **cùng một đối tượng**, dữ liệu có thể bị hỏng hoặc sai lệch.
*   **Hệ quả:** Các lớp như `SimpleDateFormat` cũng không an toàn luồng. Nếu bạn chia sẻ một đối tượng `SimpleDateFormat` giữa nhiều luồng, kết quả định dạng có thể bị sai. Đây là một lỗi rất khó phát hiện.
*   **Giải pháp:** Để an toàn, bạn phải đồng bộ hóa (synchronize) các đoạn code truy cập, hoặc tạo một đối tượng mới cho mỗi luồng. Điều này làm giảm hiệu năng và tăng độ phức tạp của code.

#### 3.2. Thiết kế API và Tính dễ hiểu
*   **Tháng bắt đầu từ 0:** Trong `Calendar`, tháng `JANUARY` có giá trị là `0`. Đây là một nguồn gốc của nhiều lỗi lập trình.
*   **Sự nhầm lẫn giữa các lớp:** `Date` đại diện cho một mốc thời gian (timestamp), không phải là một ngày trên lịch. Trong khi đó, `Calendar` lại được thiết kế để làm việc với các thành phần của ngày tháng. Việc chuyển đổi qua lại giữa hai lớp này và các lớp định dạng là một quá trình phức tạp và dễ sai sót.

#### 3.3. Vấn đề về Múi giờ (Zoned Date and Time)
Mặc dù có lớp `TimeZone`, việc xử lý múi giờ với `Date` và `Calendar` vẫn rất cồng kềnh. `Date` không chứa thông tin múi giờ, và `Calendar` yêu cầu bạn phải cấu hình múi giờ một cách thủ công. Việc thực hiện các phép tính phức tạp như chuyển đổi giữa các múi giờ, xử lý giờ mùa hè (DST) thường rất khó khăn.

> **Giải pháp hiện đại:** Kể từ Java 8, **`java.time` API** (JSR-310) đã được giới thiệu để khắc phục tất cả những vấn đề trên. Nó cung cấp các lớp **bất biến (immutable)** và **an toàn luồng (thread-safe)** như `LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime`, `Instant`, v.v. Đây là những công cụ được khuyến nghị sử dụng thay thế cho `Date` và `Calendar` trong các dự án mới.

Hy vọng với những giải thích và ví dụ chi tiết này, bạn đã nắm rõ được nội dung của các hình ảnh cũng như hiểu sâu hơn về cách xử lý ngày giờ trong Java. Nếu còn bất kỳ điểm nào chưa rõ, bạn cứ thoải mái đặt câu hỏi nhé

Chào bạn, cảm ơn bạn đã gửi một bộ tài liệu rất chi tiết! Đây chính là các ví dụ code sử dụng **Java 8 Date/Time API (`java.time`)** – "vị cứu tinh" được sinh ra để thay thế hoàn toàn cho `java.util.Date` và `java.util.Calendar` mà chúng ta đã thảo luận ở trên.

Bộ code này bao quát toàn bộ các lớp cốt lõi của `java.time`. Mình sẽ chia chúng thành các nhóm chủ đề, giải thích lý thuyết, trích xuất code mẫu và giải thích chi tiết từng dòng code để bạn nắm bản chất nhé.

---

### 1. Thời gian máy móc (Machine Time): `Instant` và `Clock`

**Lý thuyết:**
*   **`Instant`**: Đại diện cho một mốc thời gian chính xác trên dòng thời gian (timeline), tính bằng giây và nano-giây kể từ Unix Epoch (01/01/1970). Nó luôn hoạt động ở múi giờ **UTC**. Lớp này là **bất biến (immutable)** và **an toàn luồng (thread-safe)**.
*   **`Clock`**: Cung cấp quyền truy cập vào thời gian hiện tại, ngày và giờ sử dụng múi giờ. Rất hữu ích cho việc kiểm thử (testing) vì bạn có thể tạo một "đồng hồ cố định" (fixed clock).

#### File: `InstantDemo.java`
**Code trích xuất & Giải thích:**
```java
// 1. Khởi tạo Instant
Instant instant1 = Instant.now(); // Lấy thời gian hiện tại theo UTC
long nowMillis = System.currentTimeMillis();
Instant instant3 = Instant.ofEpochMilli(nowMillis); // Tạo từ mili-giây
Instant instant4 = Instant.ofEpochSecond(nowMillis / 1000); // Tạo từ giây

// 2. Parse từ chuỗi (Định dạng ISO-8601)
Instant instant6 = Instant.parse("1994-07-08T11:16:42.08Z"); // Chữ 'Z' nghĩa là UTC (Zulu time)

// 3. Chuyển đổi và lấy thông số
long millis = instant6.toEpochMilli(); // Lấy số mili-giây
long seconds = instant1.getEpochSecond(); // Lấy số giây
int nano = instant1.getNano(); // Lấy số nano-giây

// 4. Cộng/Trừ thời gian (Trả về một đối tượng Instant mới do tính bất biến)
Instant minus15s = instant.minusSeconds(15);
Instant plus10Days = instant.plus(10, ChronoUnit.DAYS); // Dùng ChronoUnit

// 5. Chuyển đổi sang các lớp khác (Cần cung cấp ZoneId)
LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Tokyo"));
```
**Giải thích:** `Instant` không biết gì về năm, tháng, ngày, giờ của con người. Nó chỉ biết nó là "một điểm trên trục thời gian". Để biến nó thành ngày tháng con người đọc được, ta phải cung cấp thêm Múi giờ (`ZoneId`).

#### File: `ClockDemo.java`
**Code trích xuất & Giải thích:**
```java
Clock clock = Clock.systemDefaultZone(); // Đồng hồ theo múi giờ hệ thống
Instant instant = clock.instant(); // Lấy Instant từ đồng hồ này

Clock clock2 = Clock.systemUTC(); // Đồng hồ theo UTC
Clock clock3 = Clock.system(ZoneId.of("America/Los_Angeles")); // Đồng hồ theo múi giờ cụ thể

// Đồng hồ cố định (Rất hữu ích khi viết Unit Test)
Clock fixedClock = Clock.fixed(Instant.parse("2000-07-25T11:16:24.00Z"), ZoneId.of("America/Los_Angeles"));

// Đồng hồ bị lệch (offset)
Clock baseClock = Clock.systemDefaultZone();
Clock clock6 = Clock.offset(baseClock, Duration.ofHours(72)); // Đồng hồ chạy nhanh hơn 72 giờ
```
**Giải thích:** `Clock` giúp bạn tách biệt logic lấy thời gian ra khỏi code. Trong môi trường production, bạn dùng `Clock.systemDefaultZone()`. Trong môi trường test, bạn dùng `Clock.fixed()` để kết quả test luôn ổn định, không bị phụ thuộc vào giờ chạy thực tế.

---

### 2. Thời gian con người (Human Time): `LocalDateTime`

**Lý thuyết:** `LocalDateTime` biểu diễn ngày và giờ **không có múi giờ** (ví dụ: 1981-10-18T10:20:45). Nó giống như việc bạn nhìn vào lịch treo tường và đồng hồ treo tường ở nhà bạn, nó không nói cho bạn biết người ở Nhật Bản lúc đó là mấy giờ.

#### File: `LocalDateTimeDemo.java`
**Code trích xuất & Giải thích:**
```java
// 1. Khởi tạo
LocalDateTime localDT1 = LocalDateTime.now(); // Lấy giờ máy hiện tại
LocalDateTime localDT4 = LocalDateTime.of(1980, 4, 9, 20, 15); // Năm, tháng, ngày, giờ, phút
LocalDateTime localDT9 = LocalDateTime.parse("1945-08-17T10:20:45"); // Từ chuỗi ISO-8601

// 2. Lấy thông tin
int year = localDT.getYear();
int month = localDT.getMonthValue(); // Trả về 1-12 (Khác với Calendar trả về 0-11)
DayOfWeek dow = localDT.getDayOfWeek(); // Trả về Enum (MONDAY, TUESDAY...)

// 3. Cộng/Trừ thời gian (Trả về đối tượng mới)
LocalDateTime plus15Days = localDT.plusDays(15);
LocalDateTime minus4Months = localDT.minusMonths(4);
LocalDateTime plus5Years = localDT.plus(5, ChronoUnit.YEARS);
LocalDateTime minus60Days = localDT.minus(Duration.ofDays(60)); // Dùng Duration
```
**Giải thích:** API của `LocalDateTime` cực kỳ rõ ràng. Các hàm `plus...` và `minus...` trả về một đối tượng **mới** (vì nó immutable). Bạn không bao giờ phải lo lắng về việc đối tượng bị thay đổi ngo ý muốn.

---

### 3. Thời gian có múi giờ: `ZonedDateTime` và `OffsetDateTime`

**Lý thuyết:**
*   **`ZonedDateTime`**: Chứa đầy đủ ngày, giờ và **Múi giờ (ZoneId)** (ví dụ: `Asia/Singapore`). Nó tự động xử lý các quy tắc phức tạp như Giờ mùa hè (DST - Daylight Saving Time).
*   **`OffsetDateTime`**: Chứa ngày, giờ và **Độ lệch (ZoneOffset)** so với UTC (ví dụ: `+07:00`). Nó không xử lý quy tắc DST như `ZonedDateTime`. Thường dùng để lưu trữ trong cơ sở dữ liệu (Database) vì tính chính xác tuyệt đối của nó.

#### File: `ZonedDateTimeDemo.java` & `OffsetDateTimeDemo.java`
**Code trích xuất & Giải thích:**
```java
// --- ZonedDateTime ---
// Tạo từ chuỗi có kèm múi giờ
ZonedDateTime zonedDT = ZonedDateTime.parse("1984-08-13T18:30:15+08:00[Asia/Singapore]");
ZonedDateTime nowInJakarta = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));

// Chuyển đổi
Instant instant = zonedDT.toInstant(); // Về mốc thời gian tuyệt đối
LocalDateTime local = zonedDT.toLocalDateTime(); // Bỏ múi giờ

// --- OffsetDateTime ---
OffsetDateTime offsetDT = OffsetDateTime.parse("1983-07-12T06:30:15+07:00");
OffsetDateTime nowWithOffset = OffsetDateTime.now(ZoneId.of("Asia/Jakarta")); // Trả về +07:00
ZoneOffset offset = offsetDT.getOffset(); // Lấy độ lệch (ví dụ: +07:00)
```
**Giải thích:** `ZonedDateTime` nặng hơn `OffsetDateTime` một chút vì nó phải tra cứu cơ sở dữ liệu múi giờ (tz database) để biết quy tắc DST. Do đó, giao tiếp với DB thường dùng `OffsetDateTime`, còn hiển thị cho người dùng cuối thường dùng `ZonedDateTime`.

---

### 4. Khoảng thời gian: `Duration` và `Period`

**Lý thuyết:**
*   **`Duration`**: Đo lường thời gian dựa trên thời gian máy móc (giây, nano-giây). Ví dụ: 60 giây, 1.5 giờ.
*   **`Period`**: Đo lường thời gian dựa trên ngày tháng con người (năm, tháng, ngày). Ví dụ: 2 năm 3 tháng 5 ngày.

#### File: `DurationDemo.java`
**Code trích xuất & Giải thích:**
```java
Instant start = Instant.parse("1995-04-11T12:27:42.00Z");
Instant end = Instant.parse("2000-03-18T04:37:12.00Z");
Duration duration = Duration.between(start, end);
long seconds = duration.getSeconds(); // Tổng số giây

Duration fromDays = Duration.ofDays(1); // 1 ngày = 86400 giây
Duration fromMinutes = Duration.ofHours(60); // 60 giờ = 216000 giây

// Parse từ chuỗi định dạng ISO-8601 (P = Period, T = Time)
Duration fromChar = Duration.parse("P1DT1H10M10.5S"); // 1 Ngày, 1 Giờ, 10 Phút, 10.5 Giây
```

#### File: `PeriodDemo.java`
**Code trích xuất & Giải thích:**
```java
LocalDate startDate = LocalDate.of(1995, 4, 11);
LocalDate endDate = LocalDate.of(2000, 3, 18);
Period period = Period.between(startDate, endDate);
// Kết quả: 4 năm, 11 tháng, 7 ngày
System.out.println("Years: " + period.getYears() + ", Months: " + period.getMonths() + ", Days: " + period.getDays());

Period fromUnits = Period.of(3, 10, 10); // 3 năm, 10 tháng, 10 ngày
Period years = Period.parse("P5Y2M10D"); // 5 Năm, 2 Tháng, 10 Ngày
```

---

### 5. Đơn vị thời gian: `ChronoUnit`

**Lý thuyết:** `ChronoUnit` là một Enum cung cấp các đơn vị thời gian tiêu chuẩn (NANOS, MICROS, MILLIS, SECONDS, MINUTES, HOURS, HALF_DAYS, DAYS, WEEKS, MONTHS, YEARS, DECADES, CENTURIES, MILLENNIA, ERAS). Nó rất hữu ích để tính toán khoảng cách giữa hai mốc thời gian.

#### File: `ChronoUnitDemo.java`
**Code trích xuất & Giải thích:**
```java
LocalDateTime oldDate = LocalDateTime.of(1982, Month.AUGUST, 31, 10, 20, 55);
LocalDateTime newDate = LocalDateTime.of(2016, Month.NOVEMBER, 9, 10, 21, 56);

long years = ChronoUnit.YEARS.between(oldDate, newDate); // 34 năm
long months = ChronoUnit.MONTHS.between(oldDate, newDate); // 410 tháng
long weeks = ChronoUnit.WEEKS.between(oldDate, newDate); // 1784 tuần
long days = ChronoUnit.DAYS.between(oldDate, newDate); // 12488 ngày
long hours = ChronoUnit.HOURS.between(oldDate, newDate); // 299713 giờ
```
**Giải thích:** Phương thức `between(start, end)` sẽ tính toán xem có bao nhiêu **đơn vị hoàn chỉnh** nằm giữa 2 mốc thời gian. Ví dụ: từ 31/8 đến 9/11 là chưa đủ 1 năm, nó sẽ tính là 0 năm (nếu tính từ 1982 đến 2016 thì là 34 năm).

---

### 6. Tương tác với Code cũ (Legacy): `Demo.java`

File này của bạn chủ yếu sử dụng API cũ (`Date`, `Calendar`), nhưng có một hàm rất quan trọng ở cuối là `convertDateCalender` và cách chuyển đổi giữa cũ và mới.

**Code trích xuất & Giải thích:**
```java
public static void convertDateCalender() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
    String dateInString = "22-01-2015 10:20:56";
    
    // 1. String -> Date (API cũ)
    Date date = sdf.parse(dateInString); 
    
    // 2. Date -> Calendar (API cũ)
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    
    // 3. Calendar -> Date (API cũ)
    date = calendar.getTime();
}
```

**Cách chuyển đổi giữa API cũ và mới (Bổ sung kiến thức):**
Trong thực tế, nếu bạn phải làm việc với code cũ, bạn sẽ cần chuyển đổi qua lại:
```java
// Từ Date (cũ) sang Instant (mới)
Date oldDate = new Date();
Instant newInstant = oldDate.toInstant();

// Từ Instant (mới) sang Date (cũ)
Date newDate = Date.from(newInstant);

// Từ Calendar (cũ) sang ZonedDateTime (mới)
Calendar oldCal = Calendar.getInstance();
ZonedDateTime newZoned = ZonedDateTime.ofInstant(oldCal.toInstant(), oldCal.getZone().toZoneId());
```

### Tổng Kết
Bộ code bạn gửi là một "bách khoa toàn thư" thu nhỏ về **Java 8 Date/Time API**. 
*   Nếu bạn muốn lưu mốc thời gian tuyệt đối -> Dùng **`Instant`**.
*   Nếu bạn muốn làm việc với ngày giờ không cần múi giờ (như lịch hẹn nội bộ) -> Dùng **`LocalDateTime`**.
*   Nếu bạn muốn làm việc với múi giờ quốc tế -> Dùng **`ZonedDateTime`**.
*   Nếu bạn cần đo khoảng thời gian bằng giây/phút -> Dùng **`Duration`**.
*   Nếu bạn cần đo khoảng thời gian bằng năm/tháng/ngày -> Dùng **`Period`**.

Tất cả các lớp này đều **bất biến (immutable)** và **an toàn luồng (thread-safe)**, giải quyết triệt để những nhược điểm chết người của `java.util.Date` và `Calendar` mà chúng ta đã phân tích ở hình ảnh trước đó.

Chào bạn, đây là một chủ đề cực kỳ quan trọng trong Java hiện đại. Từ Java 8 trở đi, gói `java.time` (được chuẩn hóa qua **JSR-310**) đã ra đời để thay thế cho các lớp cũ (`java.util.Date`, `java.util.Calendar`) vốn dĩ rất cồng kềnh, dễ gây lỗi và không an toàn trong môi trường đa luồng (thread-safe).

Dưới đây, tôi sẽ giải thích chi tiết từng hình ảnh bạn gửi, đi kèm lý thuyết dễ hiểu và các ví dụ code chi tiết.

---

### PHẦN 1: GIẢI THÍCH CHI TIẾT TỪNG HÌNH ẢNH

**Hình 1: AGENDA (Lộ trình bài học)**
*   Đây là slide giới thiệu tổng quan nội dung khóa học sẽ đề cập.
*   Các khái niệm chính bao gồm: Tổng quan gói `java.time`, chuẩn **JSR-310**, phân loại các lớp, lớp `Instant`, gói `temporal`, so sánh ngày tháng, `LocalDateTime`, `ZonedDateTime`, `OffsetDateTime`, `Clock`, `Duration`, và `Period`.

**Hình 2: java.time Package Overview (Tổng quan gói java.time)**
*   **Bản đồ thế giới:** Minh họa các múi giờ (Time Zones) với đường cơ bản là **UTC** (UTC- và UTC+). Việt Nam nằm ở múi giờ UTC+7.
*   **API INCLUDES (Những gì API cung cấp):** 
    *   Date and Time (Ngày và giờ)
    *   Date without Time (Chỉ có ngày, không có giờ - VD: 2023-10-25)
    *   Time without Date (Chỉ có giờ, không có ngày - VD: 14:30)
    *   Offset from UTC (Độ lệch so với UTC - VD: +07:00)
    *   Time Zone (Múi giờ đầy đủ quy tắc - VD: Asia/Ho_Chi_Minh)
    *   Durations & Periods (Khoảng thời gian)
    *   Formatting and Parsing (Định dạng và phân tích chuỗi ngày giờ)
    *   Calendar systems (Hệ thống lịch - VD: Lịch Thái Lan, Lịch Hồi giáo).

**Hình 3: Class Classification (Phân loại các lớp)**
Slide này chia các lớp trong `java.time` thành 2 nhóm chính:
1.  **Machine-Scale Time (Thời gian cho máy tính):**
    *   `Instant`: Một điểm trên dòng thời gian (tính bằng nano-giây kể từ 1970-01-01T00:00:00Z).
    *   `Duration`: Một khoảng thời gian dựa trên thời gian (VD: 5 giây, 3 giờ).
2.  **Human-Scale Time (Thời gian cho con người):**
    *   `LocalDate`: Chỉ có ngày (không có giờ, không có múi giờ).
    *   `LocalTime`: Chỉ có giờ (không có ngày).
    *   `LocalDateTime`: Có cả ngày và giờ (nhưng không có múi giờ).
    *   `OffsetTime` / `OffsetDateTime`: Ngày/giờ kèm theo độ lệch múi giờ (VD: +07:00).
    *   `ZonedDateTime`: Ngày/giờ kèm theo múi giờ đầy đủ quy tắc (VD: Europe/Paris - có tính đến giờ mùa hè).
    *   `ZoneOffset` / `ZoneId`: Đại diện cho độ lệch và ID múi giờ.

**Hình 4: API Naming Convention (Quy ước đặt tên hàm)**
`java.time` sử dụng các tiền tố cực kỳ nhất quán để bạn dễ nhớ:
*   `get`: Lấy ra một giá trị (VD: `getYear()`).
*   `with`: Trả về một bản sao (copy) của đối tượng với một giá trị bị thay đổi (VD: `withHour(5)`). **Lưu ý: Các lớp này là Immutable (bất biến), nên hàm `with` không thay đổi đối tượng gốc mà trả về đối tượng mới.**
*   `plus/minus`: Trả về bản sao có cộng/trừ một khoảng thời gian (VD: `plusDays(1)`).
*   `multipliedBy/dividedBy/negated`: Nhân/chia/đảo dấu khoảng thời gian.
*   `to`: Chuyển đổi sang một kiểu dữ liệu liên quan (VD: `toLocalDate()`).
*   `at`: Kết hợp đối tượng này với đối tượng khác (VD: `atZone(zone)`).
*   `from`: Phương thức tĩnh (static) chuyển đổi từ đối tượng khác sang.

---

### PHẦN 2: LÝ THUYẾT VÀ VÍ DỤ CODE CHI TIẾT

Dưới đây là các ví dụ code chi tiết cho từng nội dung, đi kèm giải thích và lý thuyết.

#### 1. Machine-Scale Time: `Instant` và `Duration`
*   **Lý thuyết:** `Instant` đại diện cho một mốc thời gian chính xác trên dòng thời gian (thường dùng để ghi log, lưu database dưới dạng timestamp). `Duration` đại diện cho khoảng thời gian tính bằng giây/nano-giây (dùng để đo thời gian code chạy, thời gian chờ).
*   **Code ví dụ:**

```java
import java.time.Instant;
import java.time.Duration;

public class MachineTimeExample {
    public static void main(String[] args) {
        // Lý thuyết: Instant.now() lấy thời điểm hiện tại theo chuẩn UTC
        Instant start = Instant.now();
        System.out.println("Bắt đầu: " + start);

        // Giả lập code chạy mất 2 giây
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        Instant end = Instant.now();
        
        // Lý thuyết: Duration.between() tính khoảng cách giữa 2 Instant
        Duration duration = Duration.between(start, end);
        
        // Lý thuyết: toMillis() chuyển đổi sang mili-giây
        System.out.println("Thời gian chạy (mili-giây): " + duration.toMillis());
        System.out.println("Thời gian chạy (giây): " + duration.getSeconds());
    }
}
```
*   **Giải thích code:** `Instant` hoàn toàn không biết gì về múi giờ hay lịch (calendar), nó chỉ là con số nguyên (epoch time) tính từ năm 1970. `Duration` chuyên dùng để đo các khoảng thời gian ngắn như giây, phút, giờ.

#### 2. Human-Scale Time: `LocalDate`, `LocalTime`, `LocalDateTime`
*   **Lý thuyết:** Đây là những lớp phổ biến nhất khi làm việc với ngày tháng cho người dùng. Chúng **không có múi giờ** (time-zone). Rất an toàn vì là Immutable.
*   **Code ví dụ:**

```java
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Month;

public class LocalTimeExample {
    public static void main(String[] args) {
        // 1. LocalDate: Chỉ có ngày
        LocalDate today = LocalDate.now();
        LocalDate specificDate = LocalDate.of(2023, Month.OCTOBER, 25);
        System.out.println("Hôm nay: " + today); // VD: 2023-10-25

        // 2. LocalTime: Chỉ có giờ
        LocalTime now = LocalTime.now();
        LocalTime meetingTime = LocalTime.of(14, 30); // 14:30
        System.out.println("Giờ hiện tại: " + now);

        // 3. LocalDateTime: Kết hợp cả hai
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime specificDateTime = LocalDateTime.of(2023, 10, 25, 14, 30);
        System.out.println("Thời gian cụ thể: " + specificDateTime);

        // Lý thuyết: Tính bất biến (Immutability) - các hàm plus/minus/with trả về đối tượng MỚI
        LocalDate tomorrow = today.plusDays(1); // Không làm thay đổi biến 'today'
        System.out.println("Ngày mai: " + tomorrow);
        
        // Lý thuyết: Hàm 'with' thay đổi 1 thuộc tính cụ thể
        LocalDate nextYearSameDay = today.withYear(2024);
        System.out.println("Ngày này năm sau: " + nextYearSameDay);
    }
}
```
*   **Giải thích code:** Bạn không thể dùng `today.setYear(2024)` (như các ngôn ngữ khác) vì `java.time` là bất biến. Bạn phải dùng `today.withYear(2024)` và gán lại hoặc dùng biến mới.

#### 3. Human-Scale Time có múi giờ: `ZonedDateTime` & `OffsetDateTime`
*   **Lý thuyết:**
    *   `ZoneId`: Đại diện cho múi giờ (VD: `Asia/Ho_Chi_Minh`). Nó chứa các quy tắc phức tạp như giờ mùa hè (DST).
    *   `ZoneOffset`: Chỉ là độ lệch so với UTC (VD: `+07:00`). Không có quy tắc DST.
    *   `OffsetDateTime`: Ngày, giờ + độ lệch (VD: `2023-10-25T14:30+07:00`). Thường dùng cho database SQL.
    *   `ZonedDateTime`: Ngày, giờ + Múi giờ đầy đủ (VD: `2023-10-25T14:30+07:00[Asia/Ho_Chi_Minh]`). Dùng khi cần tính toán chính xác các quy tắc thời gian phức tạp.
*   **Code ví dụ:**

```java
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ZonedTimeExample {
    public static void main(String[] args) {
        // Lấy múi giờ Việt Nam
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vnTime = ZonedDateTime.now(vietnamZone);
        System.out.println("Giờ VN (Zoned): " + vnTime);
        // Kết quả: 2023-10-25T14:30:00+07:00[Asia/Ho_Chi_Minh]

        // Lấy giờ hiện tại ở New York
        ZonedDateTime nyTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        System.out.println("Giờ New York: " + nyTime);

        // Sử dụng OffsetDateTime
        ZoneOffset offset = ZoneOffset.of("+07:00");
        OffsetDateTime offsetTime = OffsetDateTime.now(offset);
        System.out.println("Giờ VN (Offset): " + offsetTime);
        // Kết quả: 2023-10-25T14:30:00+07:00

        // Lý thuyết: Chuyển đổi giữa các vùng
        ZonedDateTime convertedTime = vnTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        System.out.println("Giờ VN chuyển sang NY (cùng lúc): " + convertedTime);
    }
}
```
*   **Giải thích code:** `withZoneSameInstant` giữ nguyên thời điểm trên dòng thời gian (Instant) nhưng hiển thị theo múi giờ mới.

#### 4. Khoảng thời gian (Periods & Durations)
*   **Lý thuyết:**
    *   `Duration`: Đo bằng **thời gian** (Giây, Nano-giây). VD: 2 tiếng 30 phút.
    *   `Period`: Đo bằng **ngày tháng** (Năm, Tháng, Ngày). VD: 1 năm 2 tháng 5 ngày. Không bị ảnh hưởng bởi múi giờ.
*   **Code ví dụ:**

```java
import java.time.LocalDate;
import java.time.Period;
import java.time.Duration;
import java.time.LocalTime;

public class PeriodExample {
    public static void main(String[] args) {
        // 1. Period: Đo thời gian giữa 2 ngày
        LocalDate startDate = LocalDate.of(1990, 5, 20);
        LocalDate endDate = LocalDate.of(2023, 10, 25);
        
        // Tính khoảng cách (Tuổi)
        Period period = Period.between(startDate, endDate);
        System.out.println("Khoảng cách: " + period.getYears() + " năm " + 
                           period.getMonths() + " tháng " + 
                           period.getDays() + " ngày");

        // 2. Duration: Đo thời gian giữa 2 mốc giờ
        LocalTime startTime = LocalTime.of(8, 30);
        LocalTime endTime = LocalTime.of(17, 45);
        
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Thời gian làm việc (giờ): " + duration.toHours());
        System.out.println("Thời gian làm việc (phút): " + duration.toMinutes());
    }
}
```
*   **Giải thích code:** Phân biệt rõ `Period` dùng để tính tuổi, số ngày làm việc... còn `Duration` dùng để đo hiệu năng code, thời gian chạy của một tiến trình.

#### 5. API Naming Convention (Quy ước đặt tên)
*   **Lý thuyết:** Áp dụng vào bất kỳ class nào ở trên. Hãy nhớ các tiền tố: `get`, `with`, `plus`, `minus`, `to`, `at`.
*   **Code ví dụ:**

```java
import java.time.LocalDateTime;
import java.time.LocalDate;

public class NamingConventionExample {
    public static void main(String[] args) {
        LocalDateTime dt = LocalDateTime.of(2023, 10, 25, 14, 30);

        // GET: Lấy giá trị
        int year = dt.getYear(); 
        System.out.println("Năm: " + year); // 2023

        // WITH: Thay đổi 1 giá trị (Trả về object mới)
        LocalDateTime nextYear = dt.withYear(2024);
        System.out.println("Năm sau: " + nextYear);

        // PLUS/MINUS: Cộng/Trừ
        LocalDateTime nextWeek = dt.plusWeeks(1);
        LocalDateTime previousMonth = dt.minusMonths(1);

        // TO: Chuyển đổi sang kiểu dữ liệu khác (VD: chỉ lấy ngày)
        LocalDate dateOnly = dt.toLocalDate();
        System.out.println("Chỉ lấy ngày: " + dateOnly);

        // AT: Kết hợp (VD: Gắn múi giờ vào LocalDateTime)
        // Giả sử ta có ZoneId, ta có thể dùng: dt.atZone(zone)
    }
}
```
*   **Giải thích code:** Ví dụ này minh họa sự tiện lợi của `java.time`. Bạn có thể đọc code như đọc tiếng Anh: `dt.plusWeeks(1)` (cộng thêm 1 tuần), `dt.toLocalDate()` (chuyển thành LocalDate).

### TỔNG KẾT
Gói `java.time` giải quyết triệt để các vấn đề về ngày tháng năm. 
*   Nếu làm việc với **máy tính** -> Dùng `Instant` và `Duration`.
*   Nếu làm việc với **người dùng** (không quan tâm múi giờ) -> Dùng `LocalDate`, `LocalTime`, `LocalDateTime`.
*   Nếu làm việc với **hệ thống toàn cầu** -> Dùng `ZonedDateTime` hoặc `OffsetDateTime`.
*   Luôn nhớ tính **Immutable** (bất biến) và các quy tắc đặt tên (`get`, `with`, `plus`, `minus`, `to`, `at`).

Hy vọng phần giải thích này giúp bạn nắm vững kiến thức trong khóa học! Nếu có phần nào chưa rõ, bạn cứ hỏi nhé.


Chào bạn, đây là một bộ tài liệu cực kỳ đầy đủ và chi tiết về gói `java.time` (JSR-310). Các file bạn gửi bao gồm từ việc so sánh với API cũ (`Demo.java`) cho đến các lớp cốt lõi của API mới (`Instant`, `Clock`, `LocalDateTime`, `ZonedDateTime`, `OffsetDateTime`, `Duration`, `Period`, `ChronoUnit`).

Dưới đây, tôi sẽ hệ thống lại toàn bộ kiến thức theo từng nhóm chủ đề, giải thích chi tiết lý thuyết, trích xuất code từ các file bạn gửi, và giải thích chi tiết từng đoạn code.

---

### PHẦN 1: API CŨ (LEGACY) VS API MỚI (`Demo.java`)

File `Demo.java` chứa các ví dụ về API ngày tháng **cũ** (`java.util.Date`, `java.util.Calendar`, `java.text.SimpleDateFormat`). Việc hiểu API cũ giúp bạn biết lý do tại sao Java 8 phải tạo ra gói `java.time` mới.

**Lý thuyết cơ bản về API cũ (Cần tránh sử dụng trong dự án mới):**
*   **Không an toàn đa luồng (Not thread-safe):** `SimpleDateFormat` và `Calendar` có thể bị lỗi nếu nhiều luồng cùng truy cập.
*   **Tháng bắt đầu từ 0:** Tháng 1 là `0`, tháng 12 là `11` (Rất dễ nhầm lẫn).
*   **Thiết kế rối rắm:** Việc tính toán cộng/trừ ngày tháng rất cồng kềnh.

**Giải thích code trong `Demo.java`:**

```java
// Trích xuất từ hàm calendarExample() và timeZoneExample()
Calendar calendar = new GregorianCalendar(1988, 1, 28, 13, 24, 56); 
// Lý thuyết: Tham số thứ 2 là tháng (1 tương ứng với tháng 2, vì tính từ 0)

// Lấy thông tin
int month = calendar.get(Calendar.MONTH); // Kết quả: 1 (Tháng 2)

SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
Date date = new Date(1090215021581L); // Tạo từ số mili-giây (epoch)
System.out.println(sdf.format(date)); 
// Lý thuyết: SimpleDateFormat dùng để định dạng (format) Date thành String hoặc ngược lại (parse).
// setTimeZone dùng để hiển thị giờ theo múi giờ mong muốn.
```
*Giải thích:* Đoạn code trên cho thấy sự phức tạp khi phải tạo Calendar, lấy từng trường, rồi dùng SimpleDateFormat để định dạng. Từ đây, chúng ta chuyển sang API mới (`java.time`).

---

### PHẦN 2: THỜI GIAN DÀNH CHO MÁY TÍNH (MACHINE-SCALE TIME)

#### 1. Lớp `Instant` (`InstantDemo.java`)
**Lý thuyết:**
*   `Instant` đại diện cho một mốc thời gian chính xác trên dòng thời gian (timeline), tính bằng số giây và nano-giây kể từ **Epoch** (01-01-1970 00:00:00 UTC).
*   Nó hoàn toàn **không có khái niệm** về múi giờ, năm, tháng, ngày (mặc dù có thể chuyển đổi sang).
*   Thường dùng để lưu trữ database (timestamp), ghi log, đo lường hiệu năng.

**Ví dụ code & Giải thích chi tiết:**

```java
// Lý thuyết: Lấy Instant hiện tại (theo UTC)
Instant instant1 = Instant.now(); 

// Lý thuyết: Tạo Instant từ số mili-giây
long now = System.currentTimeMillis();
Instant instant3 = Instant.ofEpochMilli(now);

// Lý thuyết: Parse một chuỗi theo chuẩn ISO-8601 (có chữ Z ở cuối biểu thị UTC)
Instant instant6 = Instant.parse("1994-07-08T11:16:42.08Z"); 

// Lý thuyết: Cộng/trừ thời gian (trả về object mới vì Instant là Immutable)
System.out.println("15 seconds before : " + instant6.minusSeconds(15));
System.out.println("3 hours before    : " + instant6.minus(3, ChronoUnit.HOURS));

// Lý thuyết: Chuyển đổi Instant sang các lớp khác (cần cung cấp múi giờ để biết là mấy giờ)
ZonedDateTime zonedDateTime = instant6.atZone(ZoneId.of("Asia/Tokyo")); 
OffsetDateTime offsetDateTime = instant6.atOffset(ZoneOffset.UTC);
```
*Giải thích:* `Instant` là cốt lõi của thời gian. Bạn có thể cộng trừ trực tiếp, nhưng khi muốn biết "Đối với người ở Tokyo thì lúc đó là mấy giờ", bạn phải dùng `atZone()` hoặc `atOffset()`.

#### 2. Lớp `Clock` (`ClockDemo.java`)
**Lý thuyết:**
*   `Clock` cung cấp quyền truy cập vào `Instant` hiện tại, dựa trên một múi giờ cụ thể.
*   **Ứng dụng quan trọng nhất:** Dùng trong viết Unit Test (Kiểm thử). Bạn có thể tạo một `Clock` cố định (fixed) để test các logic liên quan đến thời gian mà không phụ thuộc vào thời gian thực tế của máy tính.

**Ví dụ code & Giải thích chi tiết:**

```java
// Lý thuyết: Lấy đồng hồ theo múi giờ mặc định của hệ thống
Clock clock = Clock.systemDefaultZone();
Instant instant = clock.instant();

// Lý thuyết: Lấy đồng hồ theo múi giờ cụ thể
Clock clock3 = Clock.system(ZoneId.of("America/Los_Angeles"));

// Lý thuyết: Tạo một đồng hồ ĐỨNG YÊN (Fixed) mãi mãi ở một thời điểm
Clock fixedClock = Clock.fixed(Instant.parse("2000-07-25T11:16:24.00Z"), ZoneId.of("America/Los_Angeles"));

// Lý thuyết: Tạo đồng hồ bị lệch (offset) so với đồng hồ gốc
Clock baseClock = Clock.systemDefaultZone();
Clock clock6 = Clock.offset(baseClock, Duration.ofHours(72)); // Nhanh hơn 72 giờ
Clock clock7 = Clock.offset(baseClock, Duration.ofHours(-72)); // Chậm hơn 72 giờ
```
*Giải thích:* `Clock.fixed` cực kỳ hữu ích khi bạn muốn giả lập thời gian để test tính năng "khóa tài khoản sau 3 ngày".

---

### PHẦN 3: THỜI GIAN DÀNH CHO CON NGƯỜI (HUMAN-SCALE TIME)

#### 1. Lớp `LocalDateTime`, `LocalDate`, `LocalTime` (`LocalDateTimeDemo.java`)
**Lý thuyết:**
*   Đại diện cho ngày/giờ **không có múi giờ**. (Ví dụ: "Ngày mai lúc 8h sáng họp" - không quan tâm múi giờ).
*   Cung cấp rất nhiều hàm tiện ích: `get`, `with`, `plus`, `minus`.

**Ví dụ code & Giải thích chi tiết:**

```java
// 1. Khởi tạo
LocalDateTime localDT = LocalDateTime.of(1980, 4, 9, 20, 15); // 1980-04-09T20:15
LocalDateTime parsedDT = LocalDateTime.parse("1981-10-18T10:20:45");

// 2. Lấy thông tin (Dễ đọc hơn Calendar rất nhiều)
int day = localDT.getDayOfMonth();
int month = localDT.getMonthValue(); // Trả về 4 (Tháng 4), không phải 3
Month monthEnum = localDT.getMonth(); // Trả về APRIL

// 3. Cộng/Trừ thời gian
LocalDateTime nextWeek = localDT.plusWeeks(1);
LocalDateTime minusYears = localDT.minus(12, ChronoUnit.YEARS);

// 4. Thay đổi thuộc tính (trả về object mới)
LocalDateTime nextYear = localDT.withYear(2024);
```
*Giải thích:* Đây là lớp bạn sẽ dùng nhiều nhất trong các ứng dụng nghiệp vụ (quản lý sự kiện, lịch làm việc). Nó khắc phục hoàn toàn lỗi đánh số tháng từ 0 của `Calendar`.

#### 2. Lớp `ZonedDateTime` và `OffsetDateTime` (`ZonedDateTimeDemo.java`, `OffsetDateTimeDemo.java`)
**Lý thuyết:**
*   **`ZoneOffset`:** Chỉ là khoảng chênh lệch so với UTC (VD: `+07:00`). Không có quy tắc giờ mùa hè.
*   **`ZoneId`:** Mã vùng địa lý (VD: `Asia/Ho_Chi_Minh`, `Europe/Paris`). Chứa các quy tắc phức tạp về thay đổi giờ mùa hè (DST).
*   **`OffsetDateTime`:** `LocalDateTime` + `ZoneOffset`. Dùng để lưu vào CSDL SQL (kiểu `TIMESTAMP WITH TIME ZONE`).
*   **`ZonedDateTime`:** `LocalDateTime` + `ZoneId`. Dùng khi bạn cần tính toán chính xác theo quy tắc múi giờ của một quốc gia cụ thể.

**Ví dụ code & Giải thích chi tiết:**

```java
// --- OFFSET DATE TIME ---
// Khởi tạo với độ lệch cố định +07:00
OffsetDateTime offsetDT = OffsetDateTime.of(1980, 4, 9, 20, 15, 45, 0, ZoneOffset.of("+07:00"));

// Parse chuỗi có offset
OffsetDateTime parsedOffset = OffsetDateTime.parse("2019-08-31T15:20:30+08:00");

// Lấy thông tin offset
ZoneOffset offset = offsetDT.getOffset(); // +07:00

// --- ZONED DATE TIME ---
// Khởi tạo với ZoneId cụ thể
ZonedDateTime zonedDT = ZonedDateTime.of(1980, 4, 9, 20, 15, 45, 0, ZoneId.systemDefault());

// Parse chuỗi có ZoneId
ZonedDateTime parsedZoned = ZonedDateTime.parse("2017-10-10T18:30:45+01:00[Europe/London]");

// Chuyển đổi qua lại
LocalDateTime local = zonedDT.toLocalDateTime();
Instant instant = zonedDT.toInstant();
```
*Giải thích:* Sự khác biệt lớn nhất là `ZonedDateTime` có thêm `[Europe/London]` ở cuối. Khi tính toán cộng/trừ ngày tháng trên `ZonedDateTime`, nó sẽ tự động xử lý việc chuyển đổi giờ mùa hè (ví dụ: cộng 1 ngày qua thời điểm đổi giờ mùa hè, kết quả có thể không phải là 24 giờ).

---

### PHẦN 4: ĐO LƯỜNG KHOẢNG THỜI GIAN

#### 1. Lớp `Duration` (`DurationDemo.java`)
**Lý thuyết:**
*   Đo khoảng thời gian dựa trên **thời gian thực** (Time-based): Giây, Nano-giây, Mili-giây.
*   Thường dùng để đo thời gian thực thi code, thời gian hết hạn của session.
*   Định dạng chuẩn ISO-8601: `PnDTnHnMn.nS` (Ví dụ: `P1DT1H10M` = 1 ngày, 1 giờ, 10 phút).

**Ví dụ code & Giải thích chi tiết:**

```java
// Lý thuyết: Tính khoảng thời gian giữa 2 Instant
Instant start = Instant.parse("1995-04-11T12:27:42.00Z");
Instant end = Instant.parse("2000-03-18T04:37:12.00Z");
Duration duration = Duration.between(start, end);
System.out.println("Seconds: " + duration.getSeconds());

// Lý thuyết: Tạo Duration từ các đơn vị có sẵn
Duration fromDays = Duration.ofDays(1); // 86400 giây

// Lý thuyết: Parse từ chuỗi
Duration fromChar = Duration.parse("P1DT1H10M10.5S"); // 1 ngày 1 giờ 10 phút 10.5 giây
```
*Giải thích:* `Duration.between` rất hữu ích khi bạn bấm giờ một tiến trình. Nó không quan tâm đến tháng/năm (vì tháng/năm có số ngày khác nhau).

#### 2. Lớp `Period` (`PeriodDemo.java`)
**Lý thuyết:**
*   Đo khoảng thời gian dựa trên **ngày tháng** (Date-based): Năm, Tháng, Ngày.
*   Thường dùng để tính tuổi, tính số ngày làm việc.
*   Định dạng chuẩn ISO-8601: `PnYnMnD` (Ví dụ: `P5Y2M10D` = 5 năm, 2 tháng, 10 ngày).

**Ví dụ code & Giải thích chi tiết:**

```java
// Lý thuyết: Tính khoảng cách giữa 2 LocalDate
LocalDate startDate = LocalDate.of(1995, 4, 11);
LocalDate endDate = LocalDate.of(2000, 3, 18);
Period period = Period.between(startDate, endDate);
// Kết quả: 4 năm, 11 tháng, 7 ngày

// Tạo Period
Period fromYears = Period.ofYears(10);
Period fromWeeks = Period.ofWeeks(40); // Tương đương 280 ngày
```
*Giải thích:* `Period.between` trả về `(năm, tháng, ngày)` chứ không trả về tổng số ngày. Nếu bạn muốn biết tổng số ngày, hãy dùng `ChronoUnit.DAYS.between`.

#### 3. Enum `ChronoUnit` (`ChronoUnitDemo.java`)
**Lý thuyết:**
*   Là một enum cực mạnh dùng để đo khoảng cách giữa 2 đối tượng thời gian (Temporal) theo một đơn vị cụ thể.
*   Các hằng số: `NANOS`, `MICROS`, `MILLIS`, `SECONDS`, `MINUTES`, `HOURS`, `HALF_DAYS`, `DAYS`, `WEEKS`, `MONTHS`, `YEARS`, `DECADES`, `CENTURIES`, `MILLENNIA`, `ERAS`, `FOREVER`.

**Ví dụ code & Giải thích chi tiết:**

```java
LocalDateTime oldDate = LocalDateTime.of(1982, Month.AUGUST, 31, 10, 20, 55);
LocalDateTime newDate = LocalDateTime.of(2016, Month.NOVEMBER, 9, 10, 21, 56);

// Lý thuyết: Tính tổng số năm giữa 2 mốc thời gian
long years = ChronoUnit.YEARS.between(oldDate, newDate);
// Kết quả: 34

// Lý thuyết: Tính tổng số tháng
long months = ChronoUnit.MONTHS.between(oldDate, newDate);
// Kết quả: 410

// Lý thuyết: Tính tổng số ngày
long days = ChronoUnit.DAYS.between(oldDate, newDate);
// Kết quả: 12488
```
*Giải thích:* `ChronoUnit` cực kỳ linh hoạt. Nếu bạn muốn biết 2 mốc thời gian cách nhau bao nhiêu tuần, chỉ cần gọi `ChronoUnit.WEEKS.between()`. Nó tự động quy đổi ra đơn vị bạn muốn.

---

### TỔNG KẾT LẠI TOÀN BỘ BỘ CODE CỦA BẠN

1.  **`Demo.java`**: Minh họa API cũ (Date, Calendar) -> Nên tránh dùng trong dự án mới.
2.  **`InstantDemo.java`**: Đại diện cho mốc thời gian tuyệt đối (UTC) -> Dùng để lưu DB.
3.  **`ClockDemo.java`**: Đồng hồ -> Dùng để test (Fixed Clock) hoặc lấy thời gian theo vùng cụ thể.
4.  **`LocalDateTimeDemo.java`**: Ngày/giờ không múi giờ -> Dùng cho lịch sự kiện, hẹn hò nội bộ.
5.  **`ZonedDateTimeDemo.java`**: Ngày/giờ + Múi giờ đầy đủ (có xử lý DST) -> Dùng cho ứng dụng toàn cầu.
6.  **`OffsetDateTimeDemo.java`**: Ngày/giờ + Độ lệch cố định -> Tương thích tốt với database SQL.
7.  **`DurationDemo.java`**: Đo khoảng thời gian ngắn (giây, mili-giây) -> Đo hiệu năng, timeout.
8.  **`PeriodDemo.java`**: Đo khoảng thời gian dài (năm, tháng, ngày) -> Tính tuổi, tính kỳ hạn hợp đồng.
9.  **`ChronoUnitDemo.java`**: Đo lường theo nhiều đơn vị khác nhau -> Dùng khi cần con số tổng (VD: Tổng số ngày).

Hy vọng bản giải thích chi tiết này giúp bạn làm chủ hoàn toàn gói `java.time` trong Java!