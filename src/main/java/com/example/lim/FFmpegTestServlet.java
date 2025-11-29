package com.example.lim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/FFmpegTestServlet")
public class FFmpegTestServlet extends HttpServlet {

    // ffmpeg 실행 파일 절대 경로
    private static final String FFMPEG_PATH = "C:/ffmpeg-7.0.2-full_build/bin/ffmpeg.exe";

    // 변환할 원본 MP4
    private static final String INPUT_MP4 =
            "YOUR_USER_PATH/Desktop/bb camara/2025-03-18/2025-03-18 19-26-39.mp4";

    // 결과 파일을 저장할 폴더 (파일명 아님)
    private static final String OUTPUT_FOLDER =
            "YOUR_USER_PATH/Desktop/bb camara/2025-03-18";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 1) 입력 파일 확인
        File inputFile = new File(INPUT_MP4);
        if (!inputFile.exists()) {
            response.getWriter().println("원본 MP4 파일이 존재하지 않습니다: " + INPUT_MP4);
            return;
        }

        // 2) 출력 폴더 생성
        File outputDir = new File(OUTPUT_FOLDER);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 3) 결과 파일명 만들기
        //    예: "2025-03-18 19-26-39.mp4" → "2025-03-18 19-26-39_h264.mp4"
        String inputName = inputFile.getName();
        String baseName = inputName.replace(".mp4", "");
        String outputName = baseName + "_h264.mp4";
        File outputFile = new File(outputDir, outputName);

        // 4) ffmpeg 명령어 준비
        ProcessBuilder pb = new ProcessBuilder(
                FFMPEG_PATH,
                "-y",
                "-i", inputFile.getAbsolutePath(),
                "-vcodec", "libx264",
                "-acodec", "aac",
                outputFile.getAbsolutePath()
        );
        // 표준 에러를 표준 출력으로 합침
        pb.redirectErrorStream(true);

        int exitCode = -1;
        Process process = null;

        try {
            // 5) 프로세스 시작
            process = pb.start();

            // 6) ffmpeg 로그 스트림을 final 변수로 선언 (Java 6에서 익명 클래스 사용 시 필수)
            final BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8"));

            // 7) 로그를 소진하기 위한 스레드
            Thread readerThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        String line;
                        while ((line = br.readLine()) != null) {
                            // 필요 시 System.out.println(line); 로 찍어서 로그 확인 가능
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            readerThread.start();

            // 8) ffmpeg 프로세스가 종료될 때까지 대기
            exitCode = process.waitFor();

            // 9) 로그 스레드도 끝날 때까지 join
            readerThread.join();

            // 10) BufferedReader 닫기
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
            response.getWriter().println("IOException 발생: " + e.getMessage());
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            response.getWriter().println("InterruptedException 발생: " + e.getMessage());
            return;
        }

        // 11) 결과 확인
        if (exitCode == 0) {
            response.getWriter().println("변환 완료!");
            response.getWriter().println("원본 파일: " + inputFile.getAbsolutePath());
            response.getWriter().println("결과 파일: " + outputFile.getAbsolutePath());
        } else {
            response.getWriter().println("변환 실패 (exit code = " + exitCode + ")");
        }
    }
}
