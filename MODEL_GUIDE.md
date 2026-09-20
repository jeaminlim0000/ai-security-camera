# 모델 구성과 실행 경로

일반 객체와 총기를 각각 검출하기 위해 두 모델을 사용했습니다. 두 모델의 결과를 합쳐 녹화 여부를 판단하고, 총기 감지 상태는 웹 알림에도 전달합니다.

## 사용 모델

| 구분 | 모델 | 실행 시 동작 |
| --- | --- | --- |
| 일반 객체 | 사전 학습된 YOLOv5s | `torch.hub.load`로 로드하고 선택한 12개 클래스의 결과를 사용 |
| 총기 | `pistol` 커스텀 모델 | 지정한 경로의 `best.pt`를 로드 |

두 모델 모두 현재 코드에서는 `device='cpu'`로 실행합니다. 첫 실행에서는 YOLOv5 코드와 일반 객체 모델을 내려받는 과정이 필요할 수 있습니다.

## best.pt 경로 설정

학습한 가중치는 저장소 루트의 [best.pt](best.pt)에 포함했습니다. 파일 크기는 약 56.7MB입니다.

`python/bbcamara.py`의 총기 모델 경로는 아래 형태의 자리표시자로 남아 있으므로, 로컬에 받은 `best.pt`의 실제 경로를 지정해야 합니다.

```python
model_pistol = torch.hub.load(
    'ultralytics/yolov5',
    'custom',
    path='YOUR_USER_PATH/yolov5/runs/train/exp/weights/best.pt',
    force_reload=True,
    device='cpu'
)
```

모델 로딩은 카메라를 여는 코드보다 먼저 실행됩니다. `best.pt`가 없거나 로드에 실패하면 일반 객체 감지만 자동으로 계속 실행하는 구조가 아니므로, 실행 전에 두 모델을 모두 준비합니다.

## 감지 대상과 출력

일반 모델에서 사용하는 클래스는 `person`, `dog`, `cat`, `bird`, `cow`, `horse`, `sheep`, `elephant`, `bear`, `zebra`, `giraffe`, `bottle`입니다. 커스텀 모델은 `pistol`을 확인합니다.

- 검출된 대상의 이름과 바운딩 박스를 프레임에 표시합니다.
- 선택한 대상이 감지되면 녹화 시각을 갱신합니다.
- 총기 감지 여부를 `pistol_flag.txt`에 `detected` 또는 `none`으로 기록합니다.

## 학습 데이터와 재학습

총기 이미지를 수집하고 Labelme로 라벨링한 뒤 YOLO 형식으로 변환해 학습했습니다. 현재 저장소에는 실행 코드와 가중치를 포함했으며, 학습 데이터셋과 `pistol.yaml`은 포함되어 있지 않습니다.

재학습할 때는 이미지·라벨과 데이터 경로가 정의된 YAML을 별도로 준비합니다. 아래 명령은 학습 설정 예시입니다.

```bash
python train.py --img 640 --batch 16 --epochs 50 --data pistol.yaml --weights yolov5s.pt
```

이미지 크기·배치·epoch 수와 탐지 정확도는 구분해서 확인합니다. 조건별 정밀도·재현율과 오탐·미탐 사례를 정리하는 작업은 후속 과제로 두었습니다.

## 실행 시 확인할 항목

| 증상 | 확인할 항목 |
| --- | --- |
| 모델 파일을 찾지 못함 | `best.pt`의 실제 위치와 `path` 값 |
| 모델 로딩 실패 | PyTorch·YOLOv5 환경과 가중치의 호환 여부, 오류 메시지 |
| 카메라 화면이 열리지 않음 | `VideoCapture(0)`에 해당하는 장치와 다른 프로그램의 카메라 사용 여부 |
| 웹 알림이 보이지 않음 | 플래그 파일 경로와 Tomcat의 컨텍스트 경로, 파일 쓰기 권한 |
| 처리 속도가 느림 | 입력 해상도, CPU 사용량, 두 모델을 순차 실행하는 추론 시간 |

[프로젝트 소개](README.md) · [감지 코드](python/bbcamara.py) · [Python 패키지](python/requirements.txt)
