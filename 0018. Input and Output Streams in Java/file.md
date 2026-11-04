Chào bạn, dựa trên các hình ảnh slide bài giảng (Agenda, IO Overview, Hierarchy) và các file code bạn cung cấp, mình sẽ tổng hợp và giải thích chi tiết lý thuyết về **Input and Output (I/O) Streams trong Java**, kết hợp phân tích trực tiếp các ví dụ code để bạn dễ hiểu nhất.

---

### I. Tổng quan về I/O Streams (Dựa trên slide "Java IO: Overview")

**Stream (Luồng)** trong Java là một khái niệm trừu tượng đại diện cho một chuỗi dữ liệu (sequence of data). 
*   **Input Stream (Luồng vào):** Dùng để đọc dữ liệu từ một nguồn (Data Source) vào chương trình (Program). Ví dụ: Đọc file từ ổ cứng, nhận dữ liệu từ bàn phím.
*   **Output Stream (Luồng ra):** Dùng để ghi dữ liệu từ chương trình ra một đích (Data Source). Ví dụ: Ghi file, in ra màn hình console.

*Hình ảnh slide minh họa:* Dữ liệu (dạng nhị phân `01010101010`) được truyền qua lại giữa Program và Data Source thông qua Stream.

---

### II. Phân cấp hệ thống I/O Streams (Dựa trên slide "Hierarchy")

Java chia Stream làm 2 loại chính dựa trên đơn vị dữ liệu xử lý:

1.  **Byte Streams (Luồng Byte):** Xử lý dữ liệu dưới dạng 8-bit bytes. Thích hợp cho dữ liệu nhị phân (hình ảnh, âm thanh, video, file nén).
    *   Lớp cơ sở: `InputStream` (đọc) và `OutputStream` (ghi).
2.  **Character Streams (Luồng Ký tự):** Xử lý dữ liệu dưới dạng 16-bit Unicode. Thích hợp cho dữ liệu văn bản (text). Tự động xử lý việc mã hóa (encoding) ký tự.
    *   Lớp cơ sở: `Reader` (đọc) và `Writer` (ghi).

*Sơ đồ cây (Hierarchy)* bao gồm các lớp con phổ biến như: `FileInputStream`, `BufferedOutputStream`, `FileReader`, `BufferedWriter`, v.v.

---

### III. Làm việc với File và Path (File & NIO.2)

#### 1. Lớp `java.io.File`
Đại diện cho đường dẫn tới file hoặc thư mục (cả trên Windows và Unix).
**Ví dụ từ `FileDemo.java`:**
```java
File file = new File("testDirectory");
file.mkdir(); // Tạo 1 thư mục đơn

file = new File("testDirectory2\\innerTestDirectory");
file.mkdirs(); // Tạo thư mục kể cả thư mục cha chưa tồn tại

// Sử dụng File.separator để tương thích đa nền tảng (\ trên Windows, / trên Unix)
file = new File("testDirectory3" + File.separator + "innerTestDirectory");
if (file.mkdirs()) {
    System.out.println("Success");
}

System.out.println("File separator: " + File.separator); // Ví dụ: \
System.out.println("Path separator: " + File.pathSeparator); // Ví dụ: ;

file = new File("result.csv");
file.createNewFile(); // Tạo file rỗng

// Lấy danh sách các file .java trong thư mục
File[] listFiles = file.listFiles(pathname -> pathname.getName().endsWith(".java"));
```

#### 2. NIO.2 (`java.nio.file.Path` và `Files`)
Là cách hiện đại hơn (từ Java 7) để làm việc với file, linh hoạt và mạnh mẽ hơn `File`.
**Ví dụ từ `PathDemoNio.java`:**
```java
// Paths.get() tạo đường dẫn
Path path = Paths.get("testDirectoryNio", "innerTestDirectoryNio");

Files.createDirectories(path); // Tạo thư mục (tương tự mkdirs)

Path filePath = Paths.get("testDirectoryNio", "result.csv");
if (!Files.exists(filePath)) {
    Files.createFile(filePath);
}

Files.delete(path); // Xóa file/thư mục
```

---

### IV. Byte Streams (InputStream / OutputStream)

Dùng để đọc/ghi dữ liệu nhị phân. Luôn nhớ đóng luồng (close) sau khi dùng để giải phóng tài nguyên. Java 7+ hỗ trợ **try-with-resources** (tự động đóng luồng) như trong slide Agenda.

**Ví dụ từ `IOStreamsDemo.java`:**
```java
// Ghi file dùng FileOutputStream (Byte Stream)
private static void writeFileToPathFileOutputStream(String path, String textToWrite) {
    // try-with-resources: Tự động close() khi kết thúc block
    try (FileOutputStream fos = new FileOutputStream(path)) {
        byte[] bytes = textToWrite.getBytes(); // Chuyển String thành mảng byte
        fos.write(bytes); // Ghi mảng byte xuống file
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

---

### V. Character Streams (Reader / Writer)

Dùng cho dữ liệu văn bản (Text). Nó xử lý mã hóa ký tự (như UTF-8) tự động, tránh lỗi font chữ.

**Ví dụ từ `IOStreamsDemo.java`:**
```java
// Ghi file dùng FileWriter (Character Stream)
private static void writeFileToPathFileWriter(String path, String textToWrite) throws IOException {
    try (var fw = new FileWriter(path)) {
        fw.write(textToWrite); // Ghi trực tiếp chuỗi String
    }
}

// Đọc file dùng FileReader
private static void printFileWithFileReader(String path) throws IOException {
    try (var fr = new FileReader(path)) {
        int content;
        // Đọc từng ký tự (trả về int), -1 là hết file
        while ((content = fr.read()) != -1) {
            System.out.print((char) content); 
        }
    }
}
```

---

### VI. Buffered Streams (Luồng có bộ đệm)

Đọc/ghi từng byte/ký tự trực tiếp xuống ổ cứng rất chậm. **Buffered Streams** sử dụng một vùng đệm (buffer) trong RAM để gom dữ liệu lại rồi mới ghi/đọc một lần, giúp tăng hiệu suất cực lớn.

**Ví dụ từ `IOStreamsDemo.java`:**
```java
private static void writeFileToPathFileWriterBuffered(String path, String textToWrite) throws IOException {
    // Bọc FileWriter trong BufferedWriter
    try (var bfw = new BufferedWriter(new FileWriter(path))) {
        bfw.write(textToWrite);
        // Nếu không dùng try-with-resources, PHẢI gọi bfw.flush() để đẩy dữ liệu từ RAM xuống file
    }
}

private static void printFileWithBuffer(String path) throws IOException {
    try (var br = new BufferedReader(new FileReader(path))) {
        String line;
        // BufferedReader cung cấp hàm readLine() rất tiện để đọc từng dòng văn bản
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}
```

---

### VII. Java NIO.2 (Gói `java.nio.file`)

Xử lý file theo phong cách hiện đại, tích hợp với Stream API, cực kỳ mạnh mẽ.

**1. Đọc/Ghi file nhanh (`Files.write`, `Files.lines`)** - *từ `IOStreamsDemo.java`*
```java
// Ghi thêm (APPEND) vào file
Files.write(Paths.get(path), textToWrite.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

// Đọc file bằng Stream API (Rất hiệu quả với file lớn)
try (Stream<String> fStream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
    fStream.forEach(System.out::println);
}
```

**2. Đọc file cấu hình thành Map** - *từ `ConfigMapHw.java`*
```java
// Đọc file config dạng "key=value" và đưa vào Map
Map<String, String> collect = Files.lines(configMapFilePath)
    .collect(Collectors.toMap(
        line -> line.split("=")[0], // Key
        line -> line.split("=")[1]  // Value
    ));
return collect.get(keyName);
```

**3. Tìm kiếm file** - *từ `SearchFilesByExtension.java`*
```java
// Tìm tất cả file có đuôi mở rộng (extension) cụ thể bằng đệ quy
try (Stream<Path> stream = Files.find(pathToStartSearch, Integer.MAX_VALUE, 
        (specificPath, attr) -> String.valueOf(specificPath).endsWith(extension))) {
    return stream.count(); // Đếm số lượng file tìm thấy
}
```

**4. Console Text Editor (Kết hợp Scanner và NIO)** - *từ `ConsoleTextEditor.java`*
```java
// Ghi đè nội dung vào file (TRUNCATE_EXISTING xóa nội dung cũ, CREATE tạo mới nếu chưa có)
Files.write(fileToWrite.toPath(), sb.toString().getBytes(), 
    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
```

---

### VIII. Serialization (Tuần tự hóa) và Externalization

Đây là kỹ thuật chuyển đổi trạng thái của một Object (đối tượng) thành một chuỗi byte để lưu vào file hoặc truyền qua mạng, sau đó có thể khôi phục lại (Deserialization).

#### 1. Serialization (Mặc định)
Object phải `implements Serializable`.
**Ví dụ từ `Employee.java` và `SerializationDemo.java`:**
```java
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L; // Dùng để kiểm tra phiên bản khi deserialize
    
    // Các trường sẽ được tự động serialization (trừ static và transient)
    private int id;
    private String name;
    private Map<String, String> props;

    // JVM tự động gọi hàm này khi writeObject() được gọi
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject(); // Thực hiện serialization mặc định
        System.out.println("=============== serialization in-progress");
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        System.out.println("=============== de-serialization in-progress");
    }
}
```
*Cách sử dụng (từ `SerializationDemo.java`):*
```java
// Ghi object ra mảng byte
var byteArrOutputStream = new ByteArrayOutputStream();
var oos = new ObjectOutputStream(byteArrOutputStream);
oos.writeObject(empl); 

// Đọc object từ mảng byte
var ois = new ObjectInputStream(new ByteArrayInputStream(byteArrOutputStream.toByteArray()));
Employee copyEmployee = (Employee) ois.readObject();
```

#### 2. Externalization (Tùy chỉnh)
Object phải `implements Externalizable`. Bạn phải tự viết code để ghi/đọc từng thuộc tính. Thường dùng khi muốn **mã hóa mật khẩu** hoặc **bỏ qua các trường không cần thiết**.
**Ví dụ từ `User.java` và `ExternalizationDemo.java`:**
```java
public class User implements Externalizable {
    private String nickname;
    private String password;

    public User() {} // BẮT BUỘC phải có constructor không tham số

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.nickname);
        out.writeObject("1234"); // Mã hóa mật khẩu thành "1234" trước khi ghi
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.nickname = (String) in.readObject();
        this.password = (String) in.readObject(); // Giải mã mật khẩu ở đây
    }
}
```

---
**Tóm tắt nhanh (Cheat Sheet):**
*   **File/Thư mục:** Dùng `java.io.File` (cũ) hoặc `java.nio.file.Files/Paths` (mới, khuyên dùng).
*   **Đọc/Ghi file văn bản:** Dùng `BufferedReader`/`BufferedWriter` (Character Streams).
*   **Đọc/Ghi file nhị phân:** Dùng `BufferedInputStream`/`BufferedOutputStream` (Byte Streams).
*   **Thao tác nhanh gọn với file nhỏ:** Dùng `Files.readAllLines()`, `Files.write()`, `Files.lines()`.
*   **Lưu Object:** Dùng `Serializable` hoặc `Externalizable`.


Chào bạn, mình xin lỗi vì ở câu trả lời trước mình đã gộp phần này vào mục II và chưa "mổ xẻ" thật chi tiết từng ô trong sơ đồ bạn gửi. 

Bức ảnh bạn vừa gửi là **Sơ đồ phân cấp (Hierarchy) của I/O Streams trong Java**. Đây là "bản đồ" cực kỳ quan trọng để bạn biết khi nào cần dùng lớp nào. 

Hãy cùng mình bóc tách chi tiết từng nhánh trong sơ đồ này, kết nối trực tiếp với các file code bạn đã cung cấp nhé:

---

### Gốc: `Streams`
Mọi thứ bắt đầu từ khái niệm Stream (Dòng dữ liệu). Từ đây, Java chia làm 2 nhánh lớn dựa trên **đơn vị dữ liệu** mà chúng xử lý.

---

### Nhánh Trái: `Byte Streams` (Luồng Byte)
*   **Bản chất:** Xử lý dữ liệu thô dưới dạng 8-bit (0 và 1). Dùng cho file nhị phân (ảnh, âm thanh, video, file nén, hoặc bất kỳ file nào bạn không cần đọc nội dung text).
*   **2 Lớp cơ sở (Trừu tượng):**
    *   `InputStream`: Đại diện cho luồng **ĐỌC** byte.
    *   `OutputStream`: Đại diện cho luồng **GHI** byte.

**Các lớp con cụ thể (Implementations) trong sơ đồ:**
1.  **`FileInputStream` / `FileOutputStream`**: 
    *   *Công dụng:* Đọc/ghi dữ liệu byte trực tiếp từ/ra file trên ổ cứng.
    *   *Liên hệ code:* Trong file `IOStreamsDemo.java`, hàm `writeFileToPathFileOutputStream` đã dùng `FileOutputStream` để ghi text ra file (dưới dạng byte).
2.  **`ByteArrayInputStream` / `ByteArrayOutputStream`**:
    *   *Công dụng:* Đọc/ghi dữ liệu từ một mảng byte trong bộ nhớ (RAM) thay vì ổ cứng. Rất hữu ích khi bạn muốn xử lý dữ liệu tạm thời.
    *   *Liên hệ code:* Trong `SerializationDemo.java`, mình dùng `ByteArrayOutputStream` để hứng dữ liệu của Object `Employee` sau khi serialize.
3.  **`ObjectInputStream` / `ObjectOutputStream`**:
    *   *Công dụng:* Chuyên dùng để đọc/ghi **Object** (Tuần tự hóa - Serialization). Nó tự động chuyển Object thành chuỗi byte và ngược lại.
    *   *Liên hệ code:* Dùng trong `SerializationDemo.java` và `ExternalizationDemo.java` để đọc/ghi đối tượng `Employee` và `User`.
4.  **`FilterInputStream` / `FilterOutputStream`**:
    *   *Công dụng:* Đây là các lớp "Bọc" (Wrapper). Bản thân chúng không đọc/ghi trực tiếp mà dùng để thêm tính năng cho các luồng khác. 
    *   *(Mở rộng)*: Những lớp như `BufferedInputStream` hay `DataInputStream` mà bạn thấy trong code thực tế đều kế thừa từ `FilterInputStream`. Chúng giúp tăng tốc độ (Buffer) hoặc đọc được kiểu dữ liệu nguyên thủy (int, double).

---

### Nhánh Phải: `Character Streams` (Luồng Ký tự)
*   **Bản chất:** Xử lý dữ liệu dưới dạng 16-bit Unicode. Dùng riêng cho văn bản (Text). Điểm mạnh là nó tự động xử lý việc mã hóa ký tự (Encoding - ví dụ: UTF-8) giúp bạn không bị lỗi font chữ.
*   **2 Lớp cơ sở (Trừu tượng):**
    *   `Reader`: Đại diện cho luồng **ĐỌC** ký tự.
    *   `Writer`: Đại diện cho luồng **GHI** ký tự.

**Các lớp con cụ thể (Implementations) trong sơ đồ:**
1.  **`FileReader` / `FileWriter`**:
    *   *Công dụng:* Đọc/ghi file văn bản trực tiếp (tự động chuyển byte thành ký tự).
    *   *Liên hệ code:* Trong `IOStreamsDemo.java`, hàm `writeFileToPathFileWriter` dùng `FileWriter` để ghi chữ (thay vì ghi byte).
2.  **`BufferedReader` / `BufferedWriter`**:
    *   *Công dụng:* Đây là "vũ khí" tối thượng để đọc/ghi file text. Nó tạo ra một bộ đệm (Buffer) trong RAM. Thay vì cứ mỗi ký tự lại chạy xuống ổ cứng, nó gom lại một cục rồi mới ghi, giúp tốc độ nhanh gấp nhiều lần. `BufferedReader` cung cấp hàm `readLine()` cực kỳ tiện lợi để đọc từng dòng.
    *   *Liên hệ code:* Hàm `printFileWithBuffer` trong `IOStreamsDemo.java` dùng `BufferedReader` để đọc từng dòng in ra màn hình.
3.  **`InputStreamReader` / `OutputStreamWriter`**:
    *   *Công dụng (Cầu nối):* Đây là 2 lớp cực kỳ quan trọng. Chúng là "cầu nối" chuyển đổi từ **Byte Stream** sang **Character Stream**. Bạn dùng nó khi muốn đọc một luồng byte nhưng lại muốn chỉ định bảng mã (ví dụ: đọc file bằng bảng mã `windows-1251`).
    *   *Liên hệ code:* Trong `IOStreamsDemo.java`, hàm `printFileToConsoleWithCustomEncoding` dùng `Files.lines` với charset `windows-1251` (bản chất bên dưới NIO cũng dùng `InputStreamReader` để giải mã).
4.  **`CharArrayReader` / `CharArrayWriter`**:
    *   *Công dụng:* Tương tự như ByteArray, nhưng dùng để đọc/ghi dữ liệu ký tự (char) từ mảng trong bộ nhớ RAM.

---

### 💡 Cách ghi nhớ sơ đồ này cực nhanh:
1.  Hễ thấy chữ **`Stream`** (Input/Output) -> Nghĩ ngay đến **Byte** (Xử lý file nhị phân, ảnh, âm thanh).
2.  Hễ thấy chữ **`Reader` / `Writer`** -> Nghĩ ngay đến **Character** (Xử lý file văn bản, text, chuỗi).
3.  Hễ thấy chữ **`File...`** -> Đọc/ghi xuống ổ cứng.
4.  Hễ thấy chữ **`Buffered...`** -> Thêm bộ đệm cho nhanh (Luôn ưu tiên dùng cái này).
5.  Hễ thấy chữ **`Object...`** -> Dùng để lưu Object (Serialization).
6.  Hễ thấy chữ **`InputStreamReader` / `OutputStreamWriter`** -> Điểm chuyển giao từ Byte sang Ký tự (thường dùng để set Encoding như UTF-8).

Hy vọng phần giải thích chi tiết này đã làm rõ bức sơ đồ trong slide bài giảng của bạn!