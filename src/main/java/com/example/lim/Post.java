package com.example.lim;

import java.sql.Timestamp;

public class Post {
    private int id;
    private String title;
    private String content;
    private String writer;
    private Timestamp createdAt;

    // 로컬 경로 또는 업로드 경로를 저장할 수 있는 필드
    private String imagePath;
    private String videoPath;

    // Getter/Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer;
    }
    public void setWriter(String writer) {
        this.writer = writer;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // --- 추가/수정된 부분 ---

    public String getImagePath() {
        return imagePath;
    }

    /**
     * Windows 경로의 '\'를 '/'로 치환해 저장.
     * 예: "C:\Users\..." -> "C:/Users/..."
     */
    public void setImagePath(String imagePath) {
        if (imagePath != null) {
            this.imagePath = imagePath.replace("\\", "/");
        } else {
            this.imagePath = null;
        }
    }

    public String getVideoPath() {
        return videoPath;
    }

    /**
     * Windows 경로의 '\'를 '/'로 치환해 저장.
     */
    public void setVideoPath(String videoPath) {
        if (videoPath != null) {
            this.videoPath = videoPath.replace("\\", "/");
        } else {
            this.videoPath = null;
        }
    }
}
