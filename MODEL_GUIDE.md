# AI 모델 설정 가이드

## 모델 파일 위치

이 프로젝트는 두 가지 YOLOv5 모델을 사용합니다:

### 1. 일반 객체 감지 모델 (YOLOv5s)
- **자동 다운로드**: 첫 실행 시 자동으로 다운로드됩니다
- 용도: 사람, 동물 등 80개 클래스 감지
- 모델: `yolov5s.pt` (COCO 데이터셋 기반)

### 2. 총기 감지 모델 (커스텀)
- **수동 설치 필요**: 학습된 모델 파일이 필요합니다
- 파일명: `best.pt`
- 용량: 약 51MB
- 위치: `YOUR_USER_PATH/yolov5/runs/train/exp/weights/best.pt`

---

## 총기 감지 모델 설치 방법

### 방법 1: 학습된 모델 파일 배치

1. 학습된 `best.pt` 파일을 준비합니다
2. 다음 경로에 파일을 배치:
   ```
   YOUR_USER_PATH/
   └── yolov5/
       └── runs/
           └── train/
               └── exp/
                   └── weights/
                       └── best.pt
   ```

3. `python/bbcamara.py` 파일에서 경로 확인:
   ```python
   model_pistol = torch.hub.load(
       'ultralytics/yolov5', 
       'custom', 
       path='YOUR_USER_PATH/yolov5/runs/train/exp/weights/best.pt',
       force_reload=True,
       device='cpu'
   )
   ```

### 방법 2: 직접 학습하기

YOLOv5 모델을 직접 학습하려면:

1. YOLOv5 설치:
   ```bash
   git clone https://github.com/ultralytics/yolov5
   cd yolov5
   pip install -r requirements.txt
   ```

2. 학습 데이터 준비:
   - 총기 이미지 수집
   - YOLO 형식으로 라벨링
   - 데이터셋 폴더 구성

3. 학습 실행:
   ```bash
   python train.py --img 640 --batch 16 --epochs 50 --data pistol.yaml --weights yolov5s.pt
   ```

4. 학습 완료 후 `runs/train/exp/weights/best.pt` 파일 생성

---

## 모델 파일 관리

### GitHub에 모델 업로드하지 않는 이유
- 파일 용량이 크기 때문 (50MB+)
- Git LFS 없이는 관리 어려움
- `.gitignore`에 `*.pt` 제외 설정됨

### 권장 방법
1. 모델 파일을 별도로 보관
2. Google Drive, Dropbox 등에 업로드
3. README에 다운로드 링크 추가
4. 프로젝트 클론 후 모델 파일 수동 배치

---

## 모델 없이 실행하는 경우

총기 감지 모델(`best.pt`)이 없으면:
- **일반 객체 감지는 정상 작동**
- 총기 감지 기능만 오류 발생

오류를 방지하려면 `bbcamara.py`에서 총기 모델 로딩 부분을 주석 처리:

```python
# 총기 감지 모델 비활성화
# model_pistol = torch.hub.load(
#     'ultralytics/yolov5', 
#     'custom', 
#     path='YOUR_USER_PATH/yolov5/runs/train/exp/weights/best.pt',
#     force_reload=True,
#     device='cpu'
# )
```

---

## 문제 해결

### 모델 로딩 오류
```
Error: File not found: YOUR_USER_PATH/yolov5/runs/train/exp/weights/best.pt
```
**해결**: 모델 파일 경로 확인 및 파일 존재 여부 확인

### 메모리 부족 오류
```
RuntimeError: CUDA out of memory
```
**해결**: CPU 모드 사용 (`device='cpu'`) 또는 배치 사이즈 줄이기

### 느린 추론 속도
**원인**: CPU 사용
**해결**: GPU 사용 권장 (`device='cuda'`)

---

## 참고

- YOLOv5 공식 문서: https://github.com/ultralytics/yolov5
- PyTorch 설치: https://pytorch.org/
- 커스텀 모델 학습 가이드: https://github.com/ultralytics/yolov5/wiki/Train-Custom-Data
