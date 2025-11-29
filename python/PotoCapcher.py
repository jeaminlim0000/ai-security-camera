import cv2
import os
import numpy as np
import datetime
import time
import sys
import io

# Pillow 임포트
from PIL import Image, ImageDraw, ImageFont

sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8', errors='replace')

# --------------------------------------------------
# 한글 표시용 함수 (OpenCV -> PIL 변환 -> 텍스트 추가 -> OpenCV 복귀)
def put_korean_text(cv2_image, text, pos=(10, 50),
                    font_path="C:/Windows/Fonts/malgun.ttf",  # 실제 존재하는 폰트 경로
                    font_size=24,
                    color=(255, 255, 255)):
    """
    cv2_image: OpenCV 이미지 (numpy 배열, BGR)
    text: 표시할 문자열 (한글 포함 가능)
    pos: (x, y) 위치
    font_path: 한글 폰트(.ttf) 파일 경로
    font_size: 폰트 크기
    color: (B, G, R) 색상
    """
    # 1) OpenCV BGR -> PIL용 RGB 변환
    cv2_image_rgb = cv2.cvtColor(cv2_image, cv2.COLOR_BGR2RGB)
    pil_image = Image.fromarray(cv2_image_rgb)

    # 2) PIL로 텍스트 그리기
    draw = ImageDraw.Draw(pil_image)
    font = ImageFont.truetype(font_path, font_size)
    # BGR → RGB 변환
    r, g, b = color[2], color[1], color[0]
    draw.text(pos, text, font=font, fill=(r, g, b))

    # 3) PIL -> OpenCV BGR 복귀
    result = cv2.cvtColor(np.array(pil_image), cv2.COLOR_RGB2BGR)
    return result

# --------------------------------------------------
# 도움말 이미지를 생성하는 함수
def draw_help_image():
    """
    검은 배경에 키 설명(한글)을 표시해주는 이미지를 생성하여 반환.
    """
    help_img = np.zeros((800, 700, 3), dtype=np.uint8)

    lines = [
        "[메뉴 선택 모드]",
        "  a/d (←/→) : 날짜 폴더 이동",
        "  w/s (↑/↓) : 파일 목록 이동",
        "  Space : 날짜 → 파일 선택, 파일 → 편집 모드",
        "  ESC : 프로그램 종료",
        "",
        "[편집 모드]",
        "  ESC : 편집 모드 종료",
        "  a/d (←/→) : 한 프레임씩 이동",
        "  w/s (↑/↓) : 10 프레임씩 이동",
        "  Space : 시작/종료 프레임 지정 (두 번 누름)",
        "  v : 프레임 및 ROI 설정 초기화",
        "  x : 현재 프레임 스냅샷 저장",
        "  c : 현재 구간(ROI) 동영상 저장",
        "",
        "[ROI 설정]",
        "  마우스 왼쪽 드래그 : ROI 영역 지정",
        "  ROI 지정 후 스페이스바 누르면 ROI 고정",
        "",
    ]

    y_offset = 30
    font_path = "C:/Windows/Fonts/malgun.ttf"
    font_size = 22
    color_white = (255, 255, 255)

    for line in lines:
        help_img = put_korean_text(
            help_img,
            line,
            pos=(20, y_offset),
            font_path=font_path,
            font_size=font_size,
            color=color_white
        )
        y_offset += 35

    return help_img

# --------------------------------------------------
# 전역 변수 설정
MAX_ITEMS_DATE = 5
MAX_ITEMS_FILE = 5

base_video_folder = r"YOUR_USER_PATH\Desktop\bb camara"
edited_base_folder = r"YOUR_USER_PATH\Desktop\PotoCapchers"
if not os.path.exists(edited_base_folder):
    os.makedirs(edited_base_folder)

date_folders = [d for d in os.listdir(base_video_folder) if os.path.isdir(os.path.join(base_video_folder, d))]
if not date_folders:
    print(" 날짜별 폴더가 없습니다. 프로그램을 종료합니다.")
    exit()

current_date_index = 0
current_file_index = 0
video_files = []
cap = None
total_frames = 0
fps = 0

start_frame = 0
end_frame = 0
click_count = 0
roi_locked = False

roi = None
drawing = False
start_x, start_y = -1, -1

mode = "SELECT_DATE"  # "SELECT_DATE" → "SELECT_FILE" → "EDIT"

# --------------------------------------------------
# 마우스로 ROI 설정 시 사용하는 콜백
def draw_rectangle(event, x, y, flags, param):
    global start_x, start_y, roi, drawing, roi_locked
    if roi_locked:
        return
    if event == cv2.EVENT_LBUTTONDOWN:
        start_x, start_y = x, y
        drawing = True
    elif event == cv2.EVENT_MOUSEMOVE:
        if drawing:
            roi = (start_x, start_y, x, y)
    elif event == cv2.EVENT_LBUTTONUP:
        drawing = False
        roi = (start_x, start_y, x, y)

# --------------------------------------------------
def update_video_files():
    global video_files
    selected_date_folder = os.path.join(base_video_folder, date_folders[current_date_index])
    videos = [f for f in os.listdir(selected_date_folder) if f.endswith('.mp4')]
    video_files = videos

def open_video_and_prepare():
    global cap, total_frames, fps, start_frame, end_frame
    selected_date_folder = os.path.join(base_video_folder, date_folders[current_date_index])
    selected_video = video_files[current_file_index]
    video_path = os.path.join(selected_date_folder, selected_video)
    print(f" 선택된 영상: {selected_video}")

    cap = cv2.VideoCapture(video_path)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    fps = cap.get(cv2.CAP_PROP_FPS)
    print(f" 총 프레임 개수: {total_frames}, FPS: {fps}")

    start_frame = 0
    end_frame = total_frames - 1

# --------------------------------------------------
# 초기 셋업
update_video_files()

cv2.namedWindow("Menu Selector", cv2.WINDOW_NORMAL)
cv2.resizeWindow("Menu Selector", 600, 600)  # 세로 크기 확장
cv2.namedWindow("Video Editor", cv2.WINDOW_NORMAL)
cv2.namedWindow("Zoom View", cv2.WINDOW_NORMAL)
cv2.setMouseCallback("Video Editor", draw_rectangle)

# --------------------------------------------------
def draw_menu_image():
    # OpenCV용 빈 이미지 생성
    menu_img = np.zeros((600, 600, 3), dtype=np.uint8)
    
    # 안내 문구
    info_text = "날짜 이동: a/d(왼/오), 파일 이동: w/s(위/아래), Space: 선택, ESC: 종료, h: 도움말"
    menu_img = put_korean_text(
        menu_img,
        info_text,
        pos=(10, 20),
        font_path="C:/Windows/Fonts/malgun.ttf",  # 실제 존재하는 폰트 경로
        font_size=18,
        color=(255,255,255)
    )

    global current_date_index, current_file_index

    # 날짜 폴더 표시 범위 (스크롤)
    start_index_date = max(0, min(current_date_index - 2, len(date_folders) - MAX_ITEMS_DATE))
    end_index_date = min(len(date_folders), start_index_date + MAX_ITEMS_DATE)

    # 비디오 파일 표시 범위 (스크롤)
    start_index_file = max(0, min(current_file_index - 2, len(video_files) - MAX_ITEMS_FILE))
    end_index_file = min(len(video_files), start_index_file + MAX_ITEMS_FILE)

    y_off = 60
    # [DATE FOLDER] 라벨
    menu_img = put_korean_text(
        menu_img,
        "[DATE FOLDER]",
        pos=(10, y_off),
        font_path="C:/Windows/Fonts/malgun.ttf",
        font_size=20,
        color=(0,255,255)
    )
    y_off += 30

    # 날짜 폴더 목록 출력
    for i in range(start_index_date, end_index_date):
        color = (255,255,255)
        if i == current_date_index:
            color = (0,255,0)
        text_line = f"{i+1}. {date_folders[i]}"
        menu_img = put_korean_text(
            menu_img,
            text_line,
            pos=(30, y_off),
            font_path="C:/Windows/Fonts/malgun.ttf",
            font_size=18,
            color=color
        )
        y_off += 25

    y_off += 20
    # [VIDEO FILES] 라벨
    menu_img = put_korean_text(
        menu_img,
        "[VIDEO FILES]",
        pos=(10, y_off),
        font_path="C:/Windows/Fonts/malgun.ttf",
        font_size=20,
        color=(0,255,255)
    )
    y_off += 30

    # 비디오 파일 목록 출력
    if not video_files:
        menu_img = put_korean_text(
            menu_img,
            "No mp4 files found.",
            pos=(30, y_off),
            font_path="C:/Windows/Fonts/malgun.ttf",
            font_size=18,
            color=(255,255,255)
        )
    else:
        for j in range(start_index_file, end_index_file):
            color = (255,255,255)
            if j == current_file_index:
                color = (0,255,0)
            text_line = f"{j+1}. {video_files[j]}"
            menu_img = put_korean_text(
                menu_img,
                text_line,
                pos=(30, y_off),
                font_path="C:/Windows/Fonts/malgun.ttf",
                font_size=18,
                color=color
            )
            y_off += 25

    return menu_img

# --------------------------------------------------
current_pos = 0  # 현재 프레임 인덱스

while True:
    if mode in ["SELECT_DATE", "SELECT_FILE"]:
        menu_img = draw_menu_image()
        cv2.imshow("Menu Selector", menu_img)

        key = cv2.waitKey(50) & 0xFF
        if key == 27:  # ESC
            print(" 프로그램 종료")
            break

        elif key in [ord('h'), ord('H')]:
            # 도움말 창 열기
            help_img = draw_help_image()
            cv2.namedWindow("Help", cv2.WINDOW_NORMAL)
            cv2.resizeWindow("Help", 700, 800)
            cv2.imshow("Help", help_img)
            # 사용자가 아무 키나 누르면 창 닫음
            cv2.waitKey(0)
            cv2.destroyWindow("Help")

        elif mode == "SELECT_DATE":
            if key in [ord('a'), 81]:  # left arrow
                current_date_index = max(0, current_date_index - 1)
                update_video_files()
            elif key in [ord('d'), 83]:  # right arrow
                current_date_index = min(len(date_folders) - 1, current_date_index + 1)
                update_video_files()
            elif key == ord(' '):
                if not video_files:
                    print(" 이 날짜 폴더에는 mp4가 없습니다.")
                else:
                    mode = "SELECT_FILE"
                    print(f" 날짜 폴더 '{date_folders[current_date_index]}' 선택. 파일 선택 모드.")
        elif mode == "SELECT_FILE":
            if not video_files:
                print(" mp4 파일이 없어 선택 불가.")
                mode = "SELECT_DATE"
            else:
                if key in [ord('w'), 82]:  # up arrow
                    current_file_index = max(0, current_file_index - 1)
                elif key in [ord('s'), 84]:  # down arrow
                    current_file_index = min(len(video_files) - 1, current_file_index + 1)
                elif key == ord(' '):
                    open_video_and_prepare()
                    mode = "EDIT"
                    cv2.destroyWindow("Menu Selector")
                    current_pos = 0
                    print(" 파일 선택 완료. 편집 모드로 전환합니다.")

    else:
        # === EDIT 모드 ===
        if not cap or not cap.isOpened():
            print(" cap이 유효하지 않습니다. 편집 모드 종료.")
            break

        cap.set(cv2.CAP_PROP_POS_FRAMES, current_pos)
        ret, frame = cap.read()
        if not ret:
            current_pos = max(0, min(current_pos, total_frames - 1))
            cap.set(cv2.CAP_PROP_POS_FRAMES, current_pos)
            print("범위 밖이거나 프레임을 읽을 수 없음. 인덱스 보정.")
            cv2.waitKey(30)
            continue

        display_frame = frame.copy()
        if roi:
            cv2.rectangle(display_frame, (roi[0], roi[1]), (roi[2], roi[3]), (0,255,0), 2)
            x1, y1, x2, y2 = roi
            x1, x2 = max(0, min(x1, frame.shape[1])), max(0, min(x2, frame.shape[1]))
            y1, y2 = max(0, min(y1, frame.shape[0])), max(0, min(y2, frame.shape[0]))
            if x2 > x1 and y2 > y1:
                zoomed_region = frame[y1:y2, x1:x2]
                zoomed_region = cv2.resize(zoomed_region, (500, 500), interpolation=cv2.INTER_LINEAR)
                cv2.imshow("Zoom View", zoomed_region)

        cv2.imshow("Video Editor", display_frame)
        key = cv2.waitKey(30) & 0xFF

        if key == 27:  # ESC
            print(" 편집 모드 종료")
            break

        elif key in [ord('h'), ord('H')]:
            # 편집 모드에서도 도움말 표시
            help_img = draw_help_image()
            cv2.namedWindow("Help", cv2.WINDOW_NORMAL)
            cv2.resizeWindow("Help", 700, 800)
            cv2.imshow("Help", help_img)
            cv2.waitKey(0)
            cv2.destroyWindow("Help")

        elif key in [ord('a'), 81]:  # 한 프레임 뒤로
            current_pos = max(0, current_pos - 1)

        elif key in [ord('d'), 83]:  # 한 프레임 앞으로
            current_pos = min(total_frames - 1, current_pos + 1)

        elif key in [ord('w'), 82]:  # 10 프레임 앞으로
            current_pos = min(total_frames - 1, current_pos + 10)

        elif key in [ord('s'), 84]:  # 10 프레임 뒤로
            current_pos = max(0, current_pos - 10)

        elif key == ord(' '):
            # 스페이스바로 시작/종료 프레임 지정
            if click_count % 2 == 0:
                start_frame = current_pos
                print(f" 시작 프레임 설정: {start_frame}")
                if roi:
                    roi_locked = True
                    print(" ROI 설정이 고정되었습니다.")
            else:
                old_end_frame = current_pos
                end_frame = current_pos
                if start_frame > end_frame:
                    start_frame, end_frame = end_frame, start_frame
                    print(f" 종료 프레임: {old_end_frame}, 시작/종료 교체.")
                    print(f" 변경된 시작: {start_frame}, 종료: {end_frame}")
                elif start_frame == end_frame:
                    print(" 시작과 종료 지점이 같습니다. 다시 설정하세요.")
                    click_count -= 1
                else:
                    print(f" 종료 프레임 설정: {end_frame}")
            click_count += 1

        elif key in [ord('v'), ord('V')]:
            print(" 프레임 및 ROI 설정이 취소되었습니다.")
            start_frame = 0
            end_frame = total_frames - 1
            roi = None
            roi_locked = False
            click_count = 0
            current_pos = 0

        elif key in [ord('x'), ord('X')]:
            # 스냅샷 저장 (날짜+시간만)
            timestamp = datetime.datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
            date_folder = timestamp[:10]
            save_folder_current = os.path.join(edited_base_folder, date_folder)
            if not os.path.exists(save_folder_current):
                os.makedirs(save_folder_current)

            if roi:
                x1, y1, x2, y2 = roi
                cropped_frame = frame[y1:y2, x1:x2]
                filename = os.path.join(save_folder_current, f"snapshot_roi_{timestamp}.jpg")
            else:
                cropped_frame = frame
                filename = os.path.join(save_folder_current, f"snapshot_full_{timestamp}.jpg")

            cv2.imwrite(filename, cropped_frame)
            print(f" 스냅샷 저장 완료: {filename}", flush=True)

        elif key in [ord('c'), ord('C')]:
            # 동영상 저장 (날짜+시간만)
            timestamp = datetime.datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
            date_folder = timestamp[:10]
            save_folder_current = os.path.join(edited_base_folder, date_folder)
            if not os.path.exists(save_folder_current):
                os.makedirs(save_folder_current)

            if roi:
                x1, y1, x2, y2 = roi
                output_filename = os.path.join(save_folder_current, f"camara_roi_{timestamp}.mp4")
                out_width = x2 - x1
                out_height = y2 - y1
                print(" ROI가 설정되어 있으므로 ROI 영역만 저장합니다.")
            else:
                output_filename = os.path.join(save_folder_current, f"Poto_full_{timestamp}.mp4")
                out_width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
                out_height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
                x1, y1, x2, y2 = 0, 0, out_width, out_height
                print(" ROI가 설정되지 않았으므로 전체 화면을 저장합니다.")

            out = cv2.VideoWriter(
                output_filename,
                cv2.VideoWriter_fourcc(*'mp4v'),
                fps,
                (out_width, out_height)
            )

            cap.set(cv2.CAP_PROP_POS_FRAMES, start_frame)
            for f_idx in range(start_frame, end_frame):
                ret2, frame2 = cap.read()
                if not ret2:
                    break
                cropped_frame = frame2[y1:y2, x1:x2]
                out.write(cropped_frame)

            out.release()
            print(f" 동영상 저장 완료: {output_filename}", flush=True)

cv2.destroyAllWindows()
if cap:
    cap.release()
