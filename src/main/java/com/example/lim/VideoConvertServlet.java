package com.example.lim;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/VideoConvertServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 100,  // 100MB
        maxRequestSize = 1024 * 1024 * 200 // 200MB
)
public class VideoConvertServlet extends HttpServlet {

    private static final String FFMPEG_PATH = "C:/ffmpeg-7.0.2-full_build/bin/ffmpeg.exe";
    private static final String OUTPUT_FOLDER = "YOUR_USER_PATH/Desktop/bb camara/2025-03-18";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // 1) 업로드된 동영상 받기
        Part videoPart = request.getPart("videoFile");
        if (videoPart == null || videoPart.getSize() == 0) {
            out.println("동영상 파일이 선택되지 않았습니다.");
            return;
        }

        // 임시 저장
        String originalName = getFileName(videoPart);
        if (originalName == null || originalName.isEmpty()) {
            out.println("파일명이 유효하지 않습니다.");
            return;
        }
        String tempDir = System.getProperty("java.io.tmpdir");
        File tempFile = new File(tempDir, originalName);
        videoPart.write(tempFile.getAbsolutePath());

        // 2) 출력 폴더
        File outDir = new File(OUTPUT_FOLDER);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        // 결과 파일명
        String baseName = originalName.replaceAll("\\.mp4$", "");
        String outputName = baseName + "_h264.mp4";
        File outputFile = new File(outDir, outputName);

        // 3) ffmpeg 명령어
        ProcessBuilder pb = new ProcessBuilder(
                FFMPEG_PATH, "-y",
                "-i", tempFile.getAbsolutePath(),
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
                            // 필요 시 System.out.println(line);
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
            out.println("변환 중 예외 발생: " + e.getMessage());
            return;
        }

        if (exitCode == 0) {
            out.println("동영상 변환 성공!\n원본: " + tempFile.getAbsolutePath()
                    + "\n결과: " + outputFile.getAbsolutePath());
            // 임시 파일 삭제
            tempFile.delete();
        } else {
            out.println("동영상 변환 실패 (exit=" + exitCode + ")");
        }
    }

    private String getFileName(Part part) {
        String cd = part.getHeader("content-disposition");
        if (cd != null) {
            for (String token : cd.split(";")) {
                token = token.trim();
                if (token.startsWith("filename")) {
                    return token.substring(token.indexOf('=') + 1)
                            .replace("\"", "");
                }
            }
        }
        return null;
    }
}
