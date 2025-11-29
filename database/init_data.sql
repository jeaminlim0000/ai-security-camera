-- AI 방범 카메라 시스템 초기 데이터
-- 테스트 및 개발용 샘플 데이터

USE security_camera;

-- 테스트 사용자 (비밀번호: 평문으로 저장되어 있음 - 실제 운영 시 암호화 필요)
INSERT INTO user_infoo (id, pwd, name, email, birth, reg_date) VALUES
('admin', 'admin123', '관리자', 'admin@example.com', '1990-01-01', NOW()),
('testuser', 'test123', '테스트유저', 'test@example.com', '1995-05-15', NOW());

-- 샘플 게시글
INSERT INTO board_post (title, content, writer, created_at) VALUES
('AI 방범 카메라 시스템 안내', 'YOLOv5 기반 실시간 객체 감지 시스템입니다.', 'admin', NOW()),
('총기 감지 기능 테스트', '커스텀 학습된 모델로 총기를 실시간 감지합니다.', 'admin', NOW()),
('영상 편집 기능 사용법', 'PotoCapcher를 이용하여 녹화된 영상을 편집할 수 있습니다.', 'testuser', NOW());
