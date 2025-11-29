-- AI 방범 카메라 시스템 데이터베이스 스키마
-- MySQL 5.x 이상

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS security_camera DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE security_camera;

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS user_infoo (
    id       VARCHAR(30) NOT NULL PRIMARY KEY COMMENT '사용자 ID',
    pwd      VARCHAR(50) NULL COMMENT '비밀번호',
    name     VARCHAR(30) NULL COMMENT '이름',
    email    VARCHAR(30) NULL COMMENT '이메일',
    birth    DATE NULL COMMENT '생년월일',
    reg_date DATETIME NULL COMMENT '가입일'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사용자 정보';

-- 게시판 테이블
CREATE TABLE IF NOT EXISTS board_post (
    id         INT AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 ID',
    title      VARCHAR(255) NOT NULL COMMENT '제목',
    content    TEXT NOT NULL COMMENT '내용',
    writer     VARCHAR(50) NOT NULL COMMENT '작성자',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '작성일',
    image_path VARCHAR(255) NULL COMMENT '이미지 경로',
    video_path VARCHAR(255) NULL COMMENT '동영상 경로',
    INDEX idx_writer (writer),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시판';
