# 데이터베이스 설치 가이드

## MySQL 데이터베이스 설정

### 1. MySQL 설치 확인
```bash
mysql --version
```

### 2. MySQL 접속
```bash
mysql -u root -p
```

### 3. 데이터베이스 생성 및 초기화

#### 방법 1: SQL 파일 실행 (권장)
```bash
# 스키마 생성
mysql -u root -p < database/schema.sql

# 초기 데이터 입력 (선택사항)
mysql -u root -p < database/init_data.sql
```

#### 방법 2: MySQL 콘솔에서 직접 실행
```sql
-- 1. schema.sql 내용 복사 & 붙여넣기
-- 2. init_data.sql 내용 복사 & 붙여넣기 (선택)
```

### 4. 데이터베이스 확인
```sql
USE security_camera;
SHOW TABLES;
DESCRIBE user_infoo;
DESCRIBE board_post;
```

---

## 테이블 구조

### user_infoo (사용자 정보)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | VARCHAR(30) | 사용자 ID (Primary Key) |
| pwd | VARCHAR(50) | 비밀번호 |
| name | VARCHAR(30) | 이름 |
| email | VARCHAR(30) | 이메일 |
| birth | DATE | 생년월일 |
| reg_date | DATETIME | 가입일 |

### board_post (게시판)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | INT | 게시글 ID (Auto Increment) |
| title | VARCHAR(255) | 제목 |
| content | TEXT | 내용 |
| writer | VARCHAR(50) | 작성자 |
| created_at | DATETIME | 작성일 (기본값: 현재시간) |
| image_path | VARCHAR(255) | 이미지 경로 |
| video_path | VARCHAR(255) | 동영상 경로 |

---

## 초기 계정

### 관리자 계정
- ID: `admin`
- 비밀번호: `admin123`

### 테스트 계정
- ID: `testuser`
- 비밀번호: `test123`

**⚠️ 주의:** 실제 운영 환경에서는 비밀번호를 반드시 변경하세요!

---

## 데이터베이스 설정 (application.properties)

프로젝트에서 데이터베이스 연결 정보를 수정하세요:

```properties
# MySQL 연결 정보
db.url=jdbc:mysql://localhost:3306/security_camera?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
db.username=root
db.password=YOUR_PASSWORD
```

또는 Java 코드에서:

```java
String url = "jdbc:mysql://localhost:3306/security_camera?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
String username = "root";
String password = "YOUR_PASSWORD";
```

---

## 문제 해결

### MySQL 접속 오류
```
Access denied for user 'root'@'localhost'
```
**해결**: 비밀번호 확인 또는 MySQL 사용자 권한 설정

### 한글 깨짐
```sql
ALTER DATABASE security_camera CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 테이블이 보이지 않음
```sql
SHOW DATABASES;
USE security_camera;
SHOW TABLES;
```

---

## 백업 및 복원

### 백업
```bash
mysqldump -u root -p security_camera > backup.sql
```

### 복원
```bash
mysql -u root -p security_camera < backup.sql
```
