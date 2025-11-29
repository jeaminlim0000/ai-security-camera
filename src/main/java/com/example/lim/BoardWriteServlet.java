package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/BoardWriteServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1MB
        maxFileSize = 1024 * 1024 * 50,      // 파일 하나당 최대 50MB
        maxRequestSize = 1024 * 1024 * 100   // 요청 전체 용량 최대 100MB
)
public class BoardWriteServlet extends HttpServlet {

    // 업로드된 파일을 저장할 외부 경로
    private static final String EXTERNAL_UPLOAD_PATH = "C:/upload_files";

    // ★ ffmpeg 실행 파일 절대 경로 (실제 환경에 맞게 수정)
    private static final String FFMPEG_PATH = "C:/ffmpeg-7.0.2-full_build/bin/ffmpeg.exe";

    // DB 정보
    private static final String DB_URL = "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "YOUR_DB_USER";
    private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 1) 폼 파라미터
        String title = request.getParameter("title");
        String writer = request.getParameter("writer");
        String content = request.getParameter("content");

        Part imagePart = request.getPart("imageFile");
        Part videoPart = request.getPart("videoFile");

        // 이미지/동영상 파일명 추출
        String imageFileName = null;
        if (imagePart != null && imagePart.getSize() > 0) {
            imageFileName = getFileName(imagePart);
        }

        String videoFileName = null;
        if (videoPart != null && videoPart.getSize() > 0) {
            videoFileName = getFileName(videoPart);
        }

        // 2) 업로드 폴더 준비
        File uploadDir = new File(EXTERNAL_UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 3) 이미지 파일 저장
        if (imageFileName != null) {
            File imageFile = new File(uploadDir, imageFileName);
            savePartToFile(imagePart, imageFile);
        }

        // 4) 동영상 파일 저장 + ffmpeg 변환
        String finalVideoFileName = null; // DB에 기록할 최종 파일명
        if (videoFileName != null) {
            File originalVideoFile = new File(uploadDir, videoFileName);
            // (a) 원본 파일 저장
            savePartToFile(videoPart, originalVideoFile);

            // (b) ffmpeg 변환된 파일명 → "원본이름_h264.mp4"
            //    예: example.mp4 → example_h264.mp4
            String baseName = videoFileName.replaceAll("\\.mp4$", "");
            String convertedName = baseName + "_h264.mp4";
            File convertedVideoFile = new File(uploadDir, convertedName);

            // (c) 변환 실행
            int exitCode = convertWithFFmpeg(originalVideoFile, convertedVideoFile);
            if (exitCode == 0) {
                // 변환 성공
                finalVideoFileName = convertedName;
                // 원본 파일을 삭제할지 여부는 선택
                // originalVideoFile.delete();
            } else {
                // 변환 실패 시, DB에는 기록하지 않음
                finalVideoFileName = null;
            }
        }

        // 5) DB INSERT
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); // Java 6 + MySQL 5.x
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // DB에는 변환된 파일명을 기록
            // 이미지 파일은 imageFileName 그대로
            // 동영상은 finalVideoFileName(변환 성공 시 _h264.mp4)
            String sql = "INSERT INTO board_post (title, content, writer, image_path, video_path, created_at) "
                    + "VALUES (?, ?, ?, ?, ?, NOW())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, writer);
            pstmt.setString(4, imageFileName);
            pstmt.setString(5, finalVideoFileName);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                response.sendRedirect("BoardListServlet");
            } else {
                response.getWriter().println("글쓰기 실패! 다시 시도하세요.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("드라이버 로드 실패");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("DB 에러: " + e.getMessage());
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Servlet 3.0 (Tomcat 7)에서는 getSubmittedFileName()이 없으므로
     * content-disposition 헤더를 직접 파싱해 파일명 추출.
     */
    private String getFileName(Part part) {
        // 예: Content-Disposition: form-data; name="videoFile"; filename="test.mp4"
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            String[] tokens = contentDisp.split(";");
            for (int i = 0; i < tokens.length; i++) {
                String cd = tokens[i].trim();
                if (cd.startsWith("filename")) {
                    // filename="test.mp4" → test.mp4
                    String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                    return fileName;
                }
            }
        }
        return null;
    }

    /**
     * Part → File 복사 (Java 6 호환)
     */
    private void savePartToFile(Part part, File file) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = part.getInputStream();
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } finally {
            if (is != null) try { is.close(); } catch (IOException e) {}
            if (fos != null) try { fos.close(); } catch (IOException e) {}
        }
    }

    /**
     * ffmpeg 변환 (원본 mp4 → _h264.mp4), Java 6 호환
     */
    private int convertWithFFmpeg(File inputFile, File outputFile) {
        ProcessBuilder pb = new ProcessBuilder(
                FFMPEG_PATH, "-y",
                "-i", inputFile.getAbsolutePath(),
                "-vcodec", "libx264",
                "-acodec", "aac",
                outputFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);

        int exitCode = -1;
        try {
            Process process = pb.start();

            final BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8")
            );

            Thread logThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        String line;
                        while ((line = br.readLine()) != null) {
                            // 필요하다면 System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            logThread.start();

            exitCode = process.waitFor();
            logThread.join();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return exitCode;
    }
}
