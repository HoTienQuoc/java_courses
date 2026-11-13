# Chào bạn! 👋

Trước tiên, tôi cần thông báo một điều: **Trong cuộc trò chuyện này, tôi chưa nhận được hình ảnh nào từ bạn cả**. Có thể bạn quên đính kèm, hoặc hệ thống chưa tải lên thành công. Bạn hãy gửi lại hình ảnh (nếu cần tôi phân tích chi tiết từng ảnh), nhé!

Trong khi chờ, tôi sẽ giải thích **chi tiết, đầy đủ** về **TDD, BDD, ATDD** — cả lý thuyết, ví dụ đời thường, và code minh họa có giải thích từng dòng. Tất cả bằng tiếng Việt.

---

## 🎯 Tổng quan: TDD, BDD, ATDD là gì?

Cả 3 đều là **phương pháp phát triển phần mềm hướng kiểm thử (Test-Driven / Behavior-Driven / Acceptance-Driven Development)**. Điểm chung:

- **Viết test TRƯỚC khi viết code** (test-first).
- **Test đóng vai trò "đặc tả" (specification)** — thay vì viết tài liệu dài dòng, ta viết test.
- Vòng lặp phát triển ngắn: viết test → viết code → refactor.

Điểm khác biệt cốt lõi nằm ở **ai viết test** và **test được viết ở mức độ nào**:

| Tiêu chí | **TDD** | **BDD** | **ATDD** |
|---|---|---|---|
| Tên đầy đủ | Test-Driven Development | Behavior-Driven Development | Acceptance Test-Driven Development |
| Ai viết? | Lập trình viên | Dev + QA + BA + PO | Dev + QA + Business (PO/Khách hàng) |
| Viết ở đâu? | Unit test (hàm/class) | Scenario (hành vi) | Acceptance test (nghiệm thu) |
| Ngôn ngữ | Code (Java, Python...) | Ngôn ngữ tự nhiên (Given-When-Then) | Ngôn ngữ tự nhiên + code |
| Trả lời câu hỏi | "Code này có đúng không?" | "Hệ thống có hành xử đúng không?" | "Khách hàng có chấp nhận không?" |
| Công cụ | JUnit, PyTest, NUnit | Cucumber, SpecFlow, Behave | Cucumber, FitNesse, Robot Framework |

---

## 1️⃣ TDD — Test-Driven Development

### 📖 Lý thuyết

TDD là vòng lặp 3 bước nổi tiếng **Red → Green → Refactor**:

1. **RED**: Viết một test cho chức năng **chưa tồn tại** → chạy → test **FAIL** (vì code chưa có).
2. **GREEN**: Viết **code tối thiểu** để test PASS (không cần đẹp, chỉ cần pass).
3. **REFACTOR**: Dọn dẹp code (đổi tên, tách hàm...) mà **vẫn giữ test PASS**.

Lặp lại chu trình này cho từng đơn vị chức năng nhỏ.

### 🎯 Ví dụ đời thường

Bạn muốn làm món **trứng chiên**:
- **RED**: Viết "công thức kiểm tra" trước: "Trứng phải chín, không cháy, có lòng đỏ vàng". Bạn chưa nấu → công thức "fail".
- **GREEN**: Đập trứng, chiên sơ → đạt tiêu chí tối thiểu (chín, không cháy).
- **REFACTOR**: Trang trí đĩa, thêm hành ngò — món ngon hơn mà vẫn đạt tiêu chí.

### 💻 Code minh họa (Python + PyTest)

**Bối cảnh**: Viết hàm `calculate_discount(price, percent)` tính giá sau giảm.

#### Bước 1 (RED) — Viết test TRƯỚC

```python
# File: test_discount.py
import pytest
from discount import calculate_discount   # ❌ Chưa có file discount.py → import lỗi → test FAIL

def test_giam_gia_10_phan_tram():
    # TEST CASE: giảm 10% của 100.000 phải còn 90.000
    assert calculate_discount(100_000, 10) == 90_000

def test_giam_gia_0_phan_tram():
    # TEST CASE: giảm 0% thì giá không đổi
    assert calculate_discount(100_000, 0) == 100_000
```

**Giải thích từng dòng:**
- `import pytest`: import framework test PyTest.
- `from discount import calculate_discount`: cố import hàm — **hàm chưa tồn tại** → lỗi `ModuleNotFoundError`.
- Mỗi `def test_xxx()` là **1 test case** — PyTest tự phát hiện hàm có prefix `test_`.
- `assert <điều kiện>`: nếu điều kiện sai → test FAIL. Đây chính là **yêu cầu chức năng được mã hóa**.

👉 Chạy `pytest` → **FAIL** toàn bộ. Đây là bước RED.

#### Bước 2 (GREEN) — Viết code tối thiểu

```python
# File: discount.py
def calculate_discount(price, percent):
    """
    Tính giá sau khi giảm giá.
    - price: giá gốc
    - percent: phần trăm giảm (0-100)
    - Trả về: giá sau giảm
    """
    discount_amount = price * percent / 100   # số tiền được giảm
    return price - discount_amount            # giá cuối cùng
```

**Giải thích từng dòng:**
- `def calculate_discount(price, percent)`: định nghĩa hàm nhận 2 tham số.
- `discount_amount = price * percent / 100`: ví dụ 100.000 × 10 / 100 = 10.000 (số tiền giảm).
- `return price - discount_amount`: 100.000 − 10.000 = 90.000 → khớp test.

👉 Chạy `pytest` → **PASS** cả 2 test. Bước GREEN xong.

#### Bước 3 (REFACTOR) — Dọn dẹp (nếu cần)

```python
# File: discount.py — refactor
def calculate_discount(price: float, percent: float) -> float:
    """Tính giá sau giảm. Raise ValueError nếu percent ngoài [0,100]."""
    if not 0 <= percent <= 100:
        raise ValueError("percent phải nằm trong [0, 100]")
    return price * (1 - percent / 100)   # gộp công thức, gọn hơn
```

**Giải thích:**
- Thêm **type hints** (`: float`) → dễ đọc, IDE gợi ý tốt hơn.
- Thêm **validate** `0 <= percent <= 100` → phòng lỗi đầu vào.
- Gộp công thức: `price - price*percent/100` = `price * (1 - percent/100)`.
- **Vẫn giữ nguyên logic** → các test cũ **vẫn PASS** ✅.

#### ⚠️ TDD yêu cầu thêm test cho case mới

Vì refactor thêm `ValueError`, TDD "chuẩn" khuyên thêm test:

```python
def test_giam_gia_am_raises_error():
    with pytest.raises(ValueError):
        calculate_discount(100_000, -5)   # percent âm → phải raise lỗi

def test_giam_gia_tren_100_raises_error():
    with pytest.raises(ValueError):
        calculate_discount(100_000, 150)  # percent > 100 → phải raise lỗi
```

**Giải thích:**
- `with pytest.raises(ValueError)`: khẳng định rằng đoạn code bên trong **phải ném ra** `ValueError`, nếu không test FAIL.

### ✅ Ưu / ❌ Nhược điểm TDD

- **Ưu**: Code sạch, độ phủ test cao, refactor an toàn, giảm bug.
- **Nhược**: Đòi hỏi kỷ luật; dev dễ "viết test cho có"; không đảm bảo hiểu đúng yêu cầu khách hàng (vì test do dev viết).

---

## 2️⃣ BDD — Behavior-Driven Development

### 📖 Lý thuyết

BDD là **sự tiến hóa của TDD**, tập trung vào **hành vi (behavior)** của hệ thống từ **góc nhìn người dùng**, dùng ngôn ngữ tự nhiên để **Dev + QA + Business hiểu nhau**.

Cú pháp kinh điển **Given – When – Then**:

- **Given** (Bối cảnh): Trạng thái ban đầu.
- **When** (Hành động): Người dùng làm gì.
- **Then** (Kỳ vọng): Kết quả mong đợi.

Công cụ: **Cucumber** (Java/Ruby/JS), **Behave** (Python), **SpecFlow** (.NET).

### 🎯 Ví dụ đời thường

Rút tiền ATM:
- **Given** tôi có 500.000đ trong tài khoản
- **When** tôi rút 200.000đ
- **Then** số dư còn 300.000đ
- **And** máy nhả 200.000đ tiền mặt

Ai đọc cũng hiểu — đó là sức mạnh của BDD.

### 💻 Code minh họa (Python + Behave)

**Bối cảnh**: Chức năng **rút tiền ATM**.

#### Bước 1 — Viết file feature (ngôn ngữ tự nhiên)

```gherkin
# File: features/rut_tien.feature
Feature: Rút tiền ATM
  Là khách hàng
  Tôi muốn rút tiền từ tài khoản
  Để sử dụng cho sinh hoạt

  Scenario: Rút tiền khi đủ số dư
    Given tài khoản của tôi có 500000 đồng
    When tôi rút 200000 đồng
    Then số dư còn lại là 300000 đồng

  Scenario: Rút tiền vượt số dư
    Given tài khoản của tôi có 100000 đồng
    When tôi rút 200000 đồng
    Then tôi nhận được thông báo "Số dư không đủ"
    And số dư vẫn là 100000 đồng
```

**Giải thích từng dòng:**
- `Feature:` mô tả **tính năng lớn**; 3 dòng tiếp là mô tả mục đích dạng **user story**.
- `Scenario:` = **1 kịch bản test cụ thể**.
- `Given/When/Then/And`: cú pháp **Gherkin** — mỗi dòng là **1 bước (step)**.
- `And`: nối thêm điều kiện cùng loại với bước trước.

⚠️ Lưu ý: file này **Business/QA đọc được** — đó chính là "ngôn ngữ chung".

#### Bước 2 — Viết step definitions (map ngôn ngữ tự nhiên → code)

```python
# File: features/steps/rut_tien_steps.py
from behave import given, when, then
from atm import Account   # class tự viết

@given('tài khoản của tôi có {so_du:d} đồng')
def step_tai_khoan_co_so_du(context, so_du):
    # {so_du:d} → Behave tự parse số nguyên và truyền vào biến so_du
    context.account = Account(balance=so_du)   # tạo tài khoản mới mỗi scenario

@when('tôi rút {so_tien:d} đồng')
def step_rut_tien(context, so_tien):
    # gọi hàm withdraw, bắt lỗi để dùng ở bước Then
    try:
        context.account.withdraw(so_tien)
        context.thong_bao = None
    except ValueError as e:
        context.thong_bao = str(e)

@then('số dư còn lại là {so_du_con_lai:d} đồng')
def step_kiem_tra_so_du(context, so_du_con_lai):
    assert context.account.balance == so_du_con_lai

@then('tôi nhận được thông báo "{thong_bao}"')
def step_kiem_tra_thong_bao(context, thong_bao):
    assert context.thong_bao == thong_bao

@then('số dư vẫn là {so_du:d} đồng')
def step_kiem_tra_so_du_khong_doi(context, so_du):
    assert context.account.balance == so_du
```

**Giải thích từng dòng:**
- `@given/@when/@then(...)`: **decorator** đăng ký hàm là step tương ứng với câu trong file feature.
- `{so_du:d}`: **placeholder** — `:d` nghĩa là **số nguyên (digit)**; Behave truyền giá trị đã parse vào biến.
- `context`: object chia sẻ dữ liệu giữa các step trong **cùng một Scenario** (mỗi scenario có context mới).
- `try/except`: nếu `withdraw` raise lỗi → lưu message để kiểm tra ở bước sau, thay vì để test fail ngay.
- `assert`: kiểm tra kỳ vọng (giống TDD).

#### Bước 3 — Code chính (production code)

```python
# File: atm.py
class Account:
    def __init__(self, balance: int):
        self.balance = balance

    def withdraw(self, amount: int) -> int:
        """Rút tiền. Raise ValueError nếu số dư không đủ."""
        if amount > self.balance:
            raise ValueError("Số dư không đủ")
        self.balance -= amount
        return amount
```

**Giải thích:**
- `__init__`: khởi tạo tài khoản với số dư ban đầu.
- `withdraw`: nếu rút > số dư → raise `ValueError("Số dư không đủ")` — **khớp chính xác** chuỗi `"Số dư không đủ"` ở feature.
- Trừ tiền nếu hợp lệ.

#### Bước 4 — Chạy

```bash
behave features/rut_tien.feature
```

Output sẽ báo **2 scenarios passed** nếu code đúng. Nếu code sai (ví dụ không raise "Số dư không đủ"), Behave sẽ báo **failed** ngay tại step Then tương ứng.

### ✅ Ưu / ❌ Nhược điểm BDD

- **Ưu**: Cải thiện giao tiếp Dev–QA–Business; test "đọc được"; giảm hiểu nhầm yêu cầu.
- **Nhược**: Tốn công viết step definitions; dễ viết scenario quá chi tiết (biến thành "TDD trá hình").

---

## 3️⃣ ATDD — Acceptance Test-Driven Development

### 📖 Lý thuyết

ATDD còn gọi là **Story Test-Driven Development** — mở rộng TDD/BDD bằng cách **viết "Acceptance Test" TRƯỚC KHI CODE**, với sự tham gia của **3 vai (3 amigos)**: **Business (PO/Khách hàng) + Developer + Tester**.

Quy trình 3 bước chính:
1. **Discuss (Thảo luận)**: 3 amigos họp bàn, viết ra **tiêu chí nghiệm thu (Acceptance Criteria)** — khi nào khách hàng "OK" tính năng này.
2. **Distill (Chưng cất)**: Biến tiêu chí thành **acceptance test** cụ thể (dạng Given-When-Then hoặc bảng).
3. **Develop (Phát triển)**: Dev viết code + TDD bên trong để pass các acceptance test đó.

Khác biệt chính với BDD:
- **BDD** = **phương pháp luận** (mindset) → tập trung vào **hành vi**.
- **ATDD** = **quy trình kỹ thuật** (process) → tập trung vào **tiêu chí nghiệm thu (acceptance)** — kiểm tra "đã đủ để giao hàng chưa?".

### 🎯 Ví dụ đời thường

Khách đặt món **"Cơm tấm sườn bì chả"**:
- **Acceptance Criteria** (thỏa thuận trước): có sườn nướng + bì + chả + cơm tấm + nước mắm, không quá 15 phút, giá 45k.
- **Dev (đầu bếp)** nấu → **Tester (bồi bàn)** đối chiếu từng tiêu chí → **Khách (PO)** ăn thử OK → giao hàng.
- Nếu **thiếu chả** → test FAIL → chưa giao được.

Điểm mấu chốt: **tiêu chí được CHỐT TRƯỚC**, mọi bên đồng thuận.

### 💻 Code minh họa (Python + PyTest, mô phỏng ATDD)

**Bối cảnh**: Chức năng **Đăng nhập** trên web.

#### Bước 1 — Acceptance Criteria (thảo luận 3 amigos)

```
US-01: Đăng nhập
Acceptance Criteria:
  AC1. Đúng username/password → đăng nhập thành công, trả về token.
  AC2. Sai password → thông báo "Sai thông tin đăng nhập", không trả token.
  AC3. Tài khoản bị khóa → thông báo "Tài khoản đã bị khóa".
```

#### Bước 2 — Viết Acceptance Test TRƯỚC (dạng code)

```python
# File: test_acceptance_login.py
import pytest
from auth import AuthService, User, UserRepository

@pytest.fixture
def auth_service():
    """Fixture: tạo AuthService với dữ liệu mẫu dùng chung các test."""
    repo = UserRepository()
    repo.save(User(username="alice", password="123456", locked=False))
    repo.save(User(username="bob",   password="abcdef", locked=True))
    return AuthService(repo)

def test_ac1_dang_nhap_thanh_cong(auth_service):
    """AC1: đúng username/password → có token."""
    result = auth_service.login("alice", "123456")
    assert result.success is True
    assert result.token is not None and len(result.token) > 0

def test_ac2_sai_mat_khau(auth_service):
    """AC2: sai password → thông báo lỗi, không token."""
    result = auth_service.login("alice", "sai_mat_khau")
    assert result.success is False
    assert result.message == "Sai thông tin đăng nhập"
    assert result.token is None

def test_ac3_tai_khoan_bi_khoa(auth_service):
    """AC3: tài khoản bị khóa → thông báo khóa."""
    result = auth_service.login("bob", "abcdef")
    assert result.success is False
    assert result.message == "Tài khoản đã bị khóa"
```

**Giải thích từng dòng:**
- `@pytest.fixture`: setup dữ liệu **chung cho nhiều test** — ở đây tạo 2 user (1 bình thường, 1 bị khóa).
- `UserRepository`, `User`, `AuthService`: **CHƯA TỒN TẠI** → test FAIL ngay từ import (đúng tinh thần ATDD: test viết trước).
- Mỗi `test_acX_*` ứng với **1 Acceptance Criterion** — đây là "hợp đồng" giữa 3 bên.
- `LoginResult` (sẽ viết) cần có `.success`, `.message`, `.token` — như vậy **test định nghĩa luôn API cần có** (rất mạnh).

#### Bước 3 — Viết code để pass acceptance test

```python
# File: auth.py
from dataclasses import dataclass

@dataclass
class User:
    username: str
    password: str
    locked: bool = False

@dataclass
class LoginResult:
    success: bool
    message: str = ""
    token: str | None = None

class UserRepository:
    """Lưu user đơn giản bằng dict (thay cho DB thật)."""
    def __init__(self):
        self._users: dict[str, User] = {}
    def save(self, user: User): self._users[user.username] = user
    def find(self, username: str) -> User | None: return self._users.get(username)

class AuthService:
    def __init__(self, repo: UserRepository):
        self.repo = repo

    def login(self, username: str, password: str) -> LoginResult:
        user = self.repo.find(username)
        # Trường hợp không tồn tại hoặc sai mật khẩu → cùng thông báo (bảo mật)
        if user is None or user.password != password:
            return LoginResult(success=False, message="Sai thông tin đăng nhập")
        if user.locked:
            return LoginResult(success=False, message="Tài khoản đã bị khóa")
        return LoginResult(success=True, token=f"token-{username}")
```

**Giải thích từng dòng:**
- `@dataclass`: decorator tự sinh `__init__`, `__repr__`... → gọn code.
- `token: str | None = None`: cho phép `None` (Python 3.10+).
- `UserRepository`: dict đóng vai "DB" trong bộ nhớ → dễ test.
- `AuthService.login`:
  - Tìm user. Nếu **không tồn tại HOẶC sai password** → cùng trả `"Sai thông tin đăng nhập"` (**AC2**).
  - Nếu **locked** → `"Tài khoản đã bị khóa"` (**AC3**).
  - Ngược lại → trả token (**AC1**).
- Thứ tự kiểm tra **quan trọng**: password **trước** locked (bảo mật — không để lộ user bị khóa nếu chưa xác thực đúng mật khẩu).

#### Bước 4 — Chạy

```bash
pytest test_acceptance_login.py -v
```

→ **3 passed** = đáp ứng đủ 3 acceptance criteria → **sẵn sàng demo cho PO**.

> 💡 Trong thực tế, **ATDD thường dùng Cucumber/Behave** (giống BDD) để cả business đọc được. Code thuần PyTest ở trên chỉ để bạn thấy **bản chất**: test viết trước, dựa trên acceptance criteria đã thống nhất.

### ✅ Ưu / ❌ Nhược điểm ATDD

- **Ưu**: Đảm bảo **đúng yêu cầu khách hàng ngay từ đầu**; giảm rework; ai cũng hiểu "Done" là gì.
- **Nhược**: Cần khách hàng/PO **tham gia thật sự**; viết acceptance test tốn thời gian; nếu viết quá nhiều → chậm.

---

## 🔄 So sánh & liên hệ

**Chúng không loại trừ nhau** — một team giỏi thường dùng **cả 3**:

```
ATDD (vĩ mô)  →  "Cần giao cái gì? (acceptance criteria)"
     ↓
BDD (trung mô) →  "Hành vi từng scenario ra sao? (Given-When-Then)"
     ↓
TDD (vi mô)    →  "Từng hàm/class viết thế nào? (unit test)"
     ↓
     Code
```

**Ví dụ liên kết**: Chức năng "Rút tiền ATM"
- **ATDD**: PO + Dev + QA họp → chốt "rút tiền phải kiểm tra số dư, phải trả tiền mặt, phải in biên lai".
- **BDD**: Viết `Scenario: Rút tiền khi đủ số dư` + `Scenario: Rút vượt số dư`.
- **TDD**: Bên trong `Account.withdraw()`, dev viết unit test cho edge cases (amount=0, amount âm, số dư âm...).

---

## 📌 Tóm tắt 1 câu

| | **1 câu tóm tắt** |
|---|---|
| **TDD** | Dev viết **unit test trước**, code sau — *"Code đúng chưa?"* |
| **BDD** | Cả team viết **hành vi bằng ngôn ngữ tự nhiên** trước — *"Hệ thống cư xử đúng chưa?"* |
| **ATDD** | 3 bên chốt **tiêu chí nghiệm thu trước**, test theo tiêu chí — *"Khách có chấp nhận không?"* |

---

## 🖼️ Về hình ảnh bạn gửi

Vì hiện tại tôi **chưa nhận được ảnh nào**, bạn hãy **gửi lại** (hoặc dán mô tả nội dung ảnh). Tôi sẽ:
1. Mô tả chi tiết từng hình ảnh.
2. Liên hệ nội dung ảnh với lý thuyết TDD/BDD/ATDD ở trên.
3. Giải thích code (nếu ảnh có code) từng dòng.

Bạn muốn tôi đi sâu thêm phần nào? (ví dụ: **so sánh TDD vs BDD bằng bảng chi tiết hơn**, **quy trình áp dụng trong team thực tế**, **công cụ Cucumber/Behave cụ thể**, hay **bài tập thực hành từng bước**?) 😊