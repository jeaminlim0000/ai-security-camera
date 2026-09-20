# 데이터베이스 설정

로그인 사용자와 게시글 정보를 MySQL에 저장합니다. 영상과 이미지는 파일로 저장하고, 게시글에는 해당 파일의 경로를 연결했습니다.

## 스키마 적용

프로젝트 루트에서 MySQL 클라이언트를 열고 아래 순서로 실행합니다.

```sql
SOURCE database/schema.sql;
SOURCE database/init_data.sql;
```

`schema.sql`이 `security_camera` 데이터베이스를 생성하고 선택합니다. `init_data.sql`은 로컬 확인용 초기 계정과 게시글을 넣는 용도입니다. 사용할 계정 값은 실행 전에 해당 파일에서 확인합니다.

```sql
USE security_camera;
SHOW TABLES;
DESCRIBE user_infoo;
DESCRIBE board_post;
```

## 테이블

### user_infoo

| 컬럼 | 타입 | 용도 |
| --- | --- | --- |
| `id` | VARCHAR(30), PK | 사용자 ID |
| `pwd` | VARCHAR(50) | 현재 로그인에서 비교하는 비밀번호 값 |
| `name` | VARCHAR(30) | 이름 |
| `email` | VARCHAR(30) | 이메일 |
| `birth` | DATE | 생년월일 |
| `reg_date` | DATETIME | 가입일 |

### board_post

| 컬럼 | 타입 | 용도 |
| --- | --- | --- |
| `id` | INT, PK, AUTO_INCREMENT | 게시글 ID |
| `title` | VARCHAR(255) | 제목 |
| `content` | TEXT | 내용 |
| `writer` | VARCHAR(50) | 작성자 |
| `created_at` | DATETIME | 작성일 |
| `image_path` | VARCHAR(255) | 이미지 경로 |
| `video_path` | VARCHAR(255) | 영상 경로 |

작성자와 작성일에는 각각 인덱스를 두었습니다. 첨부 파일 자체는 DB에 넣지 않고 파일 저장 위치와 게시글을 연결합니다.

## Java 웹의 연결 설정

주요 Servlet은 코드에 정의한 접속 정보로 `DriverManager.getConnection`을 호출합니다. 실행할 때 DB를 사용하는 Servlet·JSP의 주소와 계정을 로컬 환경에 맞춥니다.

```java
private static final String DB_URL =
    "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC&useSSL=false";
private static final String DB_USER = "YOUR_DB_USER";
private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";
```

파일마다 상수 이름이 다를 수 있으므로 JDBC 연결에 전달하는 값을 확인합니다. `application.properties` 파일만 수정하면 모든 Servlet의 연결 정보가 바뀌는 구조는 아닙니다.

## 확인 순서

1. MySQL에 접속해 `security_camera`와 두 테이블이 생성되었는지 확인합니다.
2. 초기 계정 설정과 Servlet의 DB 연결 정보를 맞춥니다.
3. 로그인과 게시글 등록을 확인합니다.
4. 첨부 파일의 저장 위치와 DB에 기록된 경로가 일치하는지 확인합니다.

비밀번호 해시 검증, DB 설정의 공통화와 연결 관리는 다음 개선 항목으로 두었습니다.

[스키마](schema.sql) · [초기 데이터](init_data.sql) · [프로젝트 소개](../README.md)
