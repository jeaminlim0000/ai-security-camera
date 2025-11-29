# AI 방범 카메라 시스템

YOLOv5 기반 실시간 객체 감지 및 총기 탐지 기능을 갖춘 웹 기반 방범 카메라 시스템

## 프로젝트 소개

공공장소에서 발생하는 범죄 상황을 조금이라도 빠르게 인지하고 대처하기 위해 개발한 AI 기반 방범 시스템입니다.
웹 인터페이스를 통해 방범 카메라를 제어하고, YOLOv5 모델을 활용하여 실시간으로 움직이는 객체와 총기를 감지합니다.

### 개발 배경

자주 이용하던 역에서 발생한 칼부림 사건을 계기로, 범죄 상황을 막지는 못해도 조금이라도 더 빨리 인지하고 대처할 수 있도록 돕고자 하는 마음에서 개발하게 되었습니다.

### 주요 기능

#### 1. 실시간 객체 감지
- 움직이는 물체 자동 인식 및 추적
- 감지 대상: 사람, 동물(dog, cat, bird, cow, horse, sheep, elephant, bear, zebra, giraffe), 물병(bottle)
- 총 12개 클래스 선택적 감지 (YOLOv5 COCO 80개 클래스 중)
- 실시간 바운딩 박스 표시

#### 2. 총기 감지 (커스텀 모델)
- GitHub 수집 데이터 + 라벨미(Labelme) 도구로 직접 라벨링
- YOLOv5 기반 총기 탐지 모델 학습
- 총기 감지 시 웹 페이지 실시간 알림
- 감지 상태 자동 로그 기록

#### 3. 자동 녹화 시스템
- 객체 감지 시 자동 녹화 시작
- 날짜별 폴더 자동 생성 및 정리
- 움직임 종료 3초 후 자동 녹화 중지
- MP4 형식 저장

#### 4. 영상 편집 기능 (PotoCapcher)
- 녹화된 영상 날짜별 검색
- 프레임 단위 이동 (1프레임/10프레임)
- 특정 구간 선택 및 저장
- ROI(관심 영역) 지정 기능
- 스냅샷 저장
- 확대/축소 뷰 제공

#### 5. 게시판 시스템
- 공지사항 및 게시글 작성
- 이미지/동영상 파일 첨부
- 게시글 수정/삭제
- 사용자 인증 시스템

## 기술 스택

### Backend
- Java 6 (JDK 6)
- Spring Framework 3.1.1.RELEASE
- Servlet API 3.0.1
- JSP 2.2
- JSTL 1.2
- MyBatis 3.2.8
- Apache Tomcat 9.0.98

### AI / Computer Vision
- Python 3.10.11
- PyTorch 2.6.0+cpu
- Ultralytics YOLOv5
- OpenCV 4.11.0.86
- NumPy 1.26.4
- Pillow 11.1.0

### Database
- MySQL 5.x (게시판 시스템용)

### Development Tools
- Maven
- IntelliJ IDEA
- Labelme (데이터 라벨링)

### Libraries
- SLF4J 1.6.6
- Log4j 1.2.15
- JUnit 4.7

## 시스템 요구사항

- OS: Windows 10/11, macOS, Linux
- JDK: Java 6 이상
- Python: 3.10 이상 권장
- MySQL: 5.x 이상
- Apache Tomcat: 9.0 이상
- 웹캠 (카메라 기능 사용 시)
- CPU: 다중 코어 권장 (AI 모델 추론용)

## 설치 및 실행

### 1. Python 환경 설정

#### 필수 라이브러리 설치
```bash
pip install torch==2.6.0
pip install opencv-python==4.11.0.86
pip install yolov5
pip install numpy==1.26.4
pip install pillow==11.1.0
pip install pandas
```

또는 requirements.txt 사용:
```bash
cd python
pip install -r requirements.txt
```

### 2. YOLOv5 모델 설정

#### (1) 일반 객체 감지 모델
YOLOv5s 모델은 첫 실행 시 자동으로 다운로드됩니다.

#### (2) 총기 감지 모델 (커스텀)
학습된 모델 파일이 필요합니다:
- 모델 파일: `best.pt` (약 51MB)
- 저장 위치: `YOUR_USER_PATH/yolov5/runs/train/exp/weights/best.pt`

**참고:** 커스텀 모델은 별도로 제공되거나 직접 학습해야 합니다.

### 3. 데이터베이스 설정

MySQL 데이터베이스를 생성하고 테이블을 설정하세요.

#### 데이터베이스 생성
```bash
# MySQL 접속
mysql -u root -p

# 스키마 생성
mysql -u root -p < database/schema.sql

# 초기 데이터 입력 (선택사항)
mysql -u root -p < database/init_data.sql
```

#### 데이터베이스 정보
- 데이터베이스명: `security_camera`
- 테이블: `user_infoo` (사용자), `board_post` (게시판)
- 초기 계정: admin/admin123, testuser/test123

#### Java 코드에서 DB 접속 정보 수정
모든 Servlet 파일에서 다음 정보를 본인 환경에 맞게 수정:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC&useSSL=false";
private static final String DB_USER = "YOUR_DB_USER";  // MySQL 사용자명 (예: root)
private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";  // MySQL 비밀번호
```

**상세 가이드**: `database/README.md` 참고

### 4. 경로 설정

다음 파일들에서 경로를 본인 환경에 맞게 수정하세요:

#### Python 파일 (python/bbcamara.py)
```python
# 17번 줄 - 총기 감지 모델 경로
path='YOUR_USER_PATH/yolov5/runs/train/exp/weights/best.pt'

# 26번 줄 - 영상 저장 경로
base_save_folder_annotated = r"YOUR_USER_PATH/Desktop/bb camara"

# 33번 줄 - 플래그 파일 경로
flag_file_path = r"YOUR_TOMCAT_PATH/webapps/ch33/pistol_flag.txt"
```

#### Python 파일 (python/PotoCapcher.py)
```python
# 96-97번 줄 - 영상 폴더 경로
base_video_folder = r"YOUR_USER_PATH/Desktop/bb camara"
edited_base_folder = r"YOUR_USER_PATH/Desktop/PotoCapchers"
```

#### Java 파일 (CameraServlet.java 등)
```java
private static final String PYTHON_EXE = "python";
private static final String PYTHON_SCRIPT = "YOUR_USER_PATH/Desktop/python/bbcamara.py";
```

### 5. 프로젝트 빌드 및 실행

#### IntelliJ IDEA 사용
1. File → Open → 프로젝트 폴더 선택
2. Run → Edit Configurations
3. '+' 클릭 → Tomcat Server → Local
4. Deployment 탭에서 Artifact 추가
5. Run 버튼 클릭

#### 수동 빌드
```bash
mvn clean package
cp target/limproject.war $TOMCAT_HOME/webapps/
```

### 6. 접속
```
http://localhost:8080/프로젝트명/
```

## 주요 구현 사항

### 객체 감지 시스템
**선택적 클래스 감지**
- YOLOv5 COCO 모델은 80개 클래스를 지원하지만, 실제 방범 목적에 맞게 12개만 선택
- 선택 이유: 움직이는 물체 인식 테스트를 위해 간단한 물병이나 동물 동영상 활용
- 감지 객체: person, dog, cat, bird, cow, horse, sheep, elephant, bear, zebra, giraffe, bottle

**실시간 추론**
- CPU 기반 추론 (PyTorch 2.6.0+cpu)
- 프레임별 객체 감지 및 바운딩 박스 표시
- 감지 결과 실시간 화면 출력

### 총기 감지 모델 학습
**데이터 수집 및 라벨링**
- GitHub에서 총기 이미지 데이터 수집
- Labelme 도구를 이용한 직접 라벨링
- YOLOv5 형식으로 데이터셋 구성

**모델 학습**
- 베이스 모델: YOLOv5s
- 학습 환경: CPU (GPU 미사용)
- 커스텀 클래스: pistol

### 자동 녹화 시스템
- 객체 감지 시 자동 녹화 시작
- 날짜별 폴더 자동 생성 (`YYYY-MM-DD`)
- 움직임 종료 후 3초 버퍼 타임
- OpenCV VideoWriter 사용 (MP4 형식)

### 총기 감지 알림
- 총기 감지 시 `pistol_flag.txt` 파일 갱신
- 웹 페이지에서 5초마다 폴링 방식으로 확인
- 감지 시 JavaScript alert 및 화면 표시

### 영상 편집 시스템 (PotoCapcher)
**기본 기능**
- 날짜별 폴더 탐색
- 프레임 단위 이동 (←/→: 1프레임, ↑/↓: 10프레임)
- 시작/종료 프레임 지정 (Space 키)
- 구간 동영상 저장

**고급 기능**
- ROI(Region of Interest) 마우스 드래그로 지정
- ROI 영역 확대 뷰 제공 (500x500)
- 스냅샷 저장 (전체/ROI 영역)
- 한글 UI 지원 (Pillow + 맑은 고딕)

**단축키**
- h: 도움말 표시
- Space: 시작/종료 프레임 지정
- x: 스냅샷 저장
- c: 동영상 저장
- v: 설정 초기화

## 프로젝트 구조

```
limproject/
├── database/
│   ├── schema.sql            # 데이터베이스 스키마
│   ├── init_data.sql         # 초기 데이터
│   └── README.md             # 데이터베이스 설치 가이드
├── python/
│   ├── bbcamara.py           # 방범 카메라 실행
│   ├── PotoCapcher.py        # 영상 편집기
│   └── requirements.txt      # Python 패키지 목록
├── src/
│   └── main/
│       ├── java/com/example/lim/
│       │   ├── CameraServlet.java
│       │   ├── CameraEditServlet.java
│       │   ├── LoginServlet.java
│       │   ├── BoardListServlet.java
│       │   └── ...
│       ├── webapp/
│       │   ├── WEB-INF/web.xml
│       │   ├── index.jsp
│       │   ├── menu.jsp
│       │   ├── board.jsp
│       │   └── ...
│       └── resources/
│           └── application.properties
├── README.md
├── MODEL_GUIDE.md
└── pom.xml
```

## 사용 방법

### 1. 방범 카메라 실행
1. 메뉴에서 "방범 카메라" 버튼 클릭
2. AI 모델 로딩 대기 (최초 1~2분 소요)
3. 카메라 창 표시
4. 객체 감지 시 자동 녹화 시작
5. ESC 키로 종료

### 2. 영상 편집
1. "방범 카메라 편집" 버튼 클릭
2. 날짜 폴더 선택 (a/d 키)
3. 영상 파일 선택 (w/s 키)
4. Space 키로 편집 모드 진입
5. h 키로 도움말 확인
6. 원하는 구간 지정 및 저장

### 3. 총기 감지 알림
- 게시판 페이지 접속 시 자동 활성화
- 5초마다 자동으로 감지 상태 확인
- 총기 감지 시 alert 팝업 표시

## 개발 과정

### 개발 기간
약 7개월 (2024년 ~ 2025년)

### 주요 개발 과정
1. 프로젝트 기획 및 설계
2. YOLOv5 모델 학습 환경 구축
3. 총기 데이터 수집 및 라벨링
4. 커스텀 모델 학습
5. 웹 인터페이스 개발
6. 실시간 감지 시스템 구현
7. 자동 녹화 기능 구현
8. 영상 편집기 개발
9. 통합 테스트 및 최적화

## 주의 사항

### AI 모델 로딩
- 최초 실행 시 YOLOv5 모델 다운로드로 시간 소요
- CPU 환경에서는 추론 속도가 느릴 수 있음
- GPU 사용 시 성능 향상 (코드 수정 필요: `device='cuda'`)

### 경로 설정
- 모든 파일 경로를 본인 환경에 맞게 수정 필수
- 절대 경로 사용 권장
- Python 경로 확인: `where python` (Windows)

### 성능
- CPU 사용 시 실시간 처리 지연 가능
- 웹캠 해상도가 높을수록 처리 속도 저하
- 메모리 사용량 모니터링 필요

### 보안
- 본 프로젝트는 포트폴리오 목적으로 제작
- 실제 보안 시스템으로 사용 시 추가 보안 강화 필요
- 네트워크 보안, 데이터 암호화 등 고려

## 라이선스

MIT License

## 개발자 정보

- 개발자: 임재민
- 이메일: woals3346@naver.com
- GitHub: https://github.com/jeaminlim0000

## 참고 사항

본 프로젝트는 개인 포트폴리오 및 사회 안전 기여 목적으로 제작되었습니다.
실제 공공장소 배치를 위해서는 법적 검토와 개인정보 보호 조치가 필요합니다.
