import cv2
import torch
import datetime
import os
import time
import warnings
warnings.filterwarnings("ignore", category=FutureWarning)

# 일반 객체 감지를 위한 YOLOv5s 모델 (COCO 기반)
model_general = torch.hub.load('ultralytics/yolov5', 'yolov5s', device='cpu')

# 피스톨 감지를 위한 커스텀 모델
model_pistol = torch.hub.load(
    'ultralytics/yolov5', 
    'custom', 
    path='YOUR_USER_PATH/yolov5/runs/train/exp/weights/best.pt', 
    force_reload=True,
    device='cpu'
)

# 카메라 열기 (웹캠 사용)
cap = cv2.VideoCapture(0)

# 주석 영상 저장 폴더 설정
base_save_folder_annotated = r"YOUR_USER_PATH\Desktop\bb camara"
if not os.path.exists(base_save_folder_annotated):
    os.makedirs(base_save_folder_annotated)

# 피스톨 감지 상태를 기록할 텍스트 파일 경로
# 톰캣에서 읽을 수 있는 위치여야 함 (예: C:/tomcat/webapps/프로젝트/pistol_flag.txt)
flag_file_path = r"YOUR_TOMCAT_PATH\webapps\ch33\pistol_flag.txt"

# 녹화 관련 변수 설정
recording = False
video_writer_annotated = None
last_motion_time = 0         # 마지막으로 움직임을 감지한 시간
recording_buffer_time = 3    # 움직임이 사라진 후 추가로 녹화할 시간 (초)

# 감지할 대상 클래스 설정
target_objects_general = [
    'person', 'dog', 'cat', 'bird', 'cow', 'horse', 'sheep',
    'elephant', 'bear', 'zebra', 'giraffe', 'bottle'
]
target_objects_pistol = ['pistol']  # 피스톨 모델은 피스톨만 인식

def write_flag(status):
    """
    텍스트 파일에 감지 상태("detected" or "none")를 씀
    """
    try:
        with open(flag_file_path, 'w', encoding='utf-8') as f:
            f.write(status + "\n")
    except Exception as e:
        print("파일 쓰기 오류:", e)

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break

    now_str = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    current_time = time.time()

    # 프레임 우측 하단에 현재 시간 표시
    text_position = (frame.shape[1] - 200, frame.shape[0] - 20)
    cv2.putText(frame, now_str, text_position, cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255,255,255), 1)
    
    # 두 모델로 추론
    results_general = model_general(frame)
    results_pistol = model_pistol(frame)
    
    # 감지된 객체 목록
    detected_general = results_general.pandas().xyxy[0]['name'].tolist()
    detected_pistol = results_pistol.pandas().xyxy[0]['name'].tolist()
    
    # 타겟 객체 감지 여부
    motion_detected = (
        any(obj in target_objects_general for obj in detected_general) or
        any(obj in target_objects_pistol for obj in detected_pistol)
    )

    # 피스톨 감지 여부
    pistol_detected = ('pistol' in detected_pistol)

    if motion_detected:
        # 움직임이 감지되면 시간을 갱신
        last_motion_time = current_time
        
        # 녹화 중이 아니라면 새로 녹화 시작
        if not recording:
            current_date = datetime.datetime.now().strftime("%Y-%m-%d")
            annotated_date_folder = os.path.join(base_save_folder_annotated, current_date)
            if not os.path.exists(annotated_date_folder):
                os.makedirs(annotated_date_folder)
            
            filename_annotated = os.path.join(
                annotated_date_folder,
                now_str.replace(":", "-") + ".mp4"
            )
            fourcc = cv2.VideoWriter_fourcc(*'mp4v')
            video_writer_annotated = cv2.VideoWriter(
                filename_annotated,
                fourcc,
                20.0,
                (frame.shape[1], frame.shape[0])
            )
            recording = True
            print(f"녹화 시작 (주석 있음): {filename_annotated}")
    
    # 객체 감지 박스 그리기
    # 일반 모델 결과 (녹색)
    for _, row in results_general.pandas().xyxy[0].iterrows():
        label = row['name']
        if label in target_objects_general:
            x1, y1, x2, y2 = map(int, (row['xmin'], row['ymin'], row['xmax'], row['ymax']))
            cv2.rectangle(frame, (x1, y1), (x2, y2), (0,255,0), 2)
            cv2.putText(frame, label, (x1, y1 - 10),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0,255,0), 2)
    
    # 피스톨 모델 결과 (빨간색)
    for _, row in results_pistol.pandas().xyxy[0].iterrows():
        label = row['name']
        if label in target_objects_pistol:
            x1, y1, x2, y2 = map(int, (row['xmin'], row['ymin'], row['xmax'], row['ymax']))
            cv2.rectangle(frame, (x1, y1), (x2, y2), (0,0,255), 2)
            cv2.putText(frame, label, (x1, y1 - 10),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0,0,255), 2)
    
    # 피스톨 감지 상태 파일에 쓰기
    if pistol_detected:
        write_flag("detected")
    else:
        write_flag("none")

    # 녹화 중이면 프레임 저장
    if recording:
        video_writer_annotated.write(frame)

        # 마지막 움직임 감지 후 3초가 지났는지 확인
        if (current_time - last_motion_time) > recording_buffer_time:
            # 3초 내에 다시 움직임이 없으므로 녹화 종료
            recording = False
            video_writer_annotated.release()
            print("녹화 종료 (움직임 없음)")

    cv2.imshow("Security Camera", frame)
    if cv2.waitKey(1) & 0xFF == 27:
        break

cap.release()
if recording:
    video_writer_annotated.release()
cv2.destroyAllWindows()