package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/CameraServlet")
public class CameraServlet extends HttpServlet {

    // 파이썬 실행 파일 경로
    private static final String PYTHON_EXE = "python";
    // 파이썬 스크립트 경로
    private static final String PYTHON_SCRIPT = "YOUR_USER_PATH/Desktop/python/bbcamara.py";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // HTML인데 애매하네
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head>");
        sb.append("<title>방범 카메라 실행 결과</title>");
        sb.append("<style>");
        sb.append("body { font-family: Arial, sans-serif; background-color: #f0f0f0; margin:0; padding:0; }");
        sb.append(".camera-result { width: 80%; margin: 50px auto; background:#fff; padding:20px; border-radius:8px; }");
        sb.append("h1 { color:#333; }");
        sb.append(".msg { margin-top:10px; color:#333; }");
        sb.append(".log-box { background:#eee; padding:10px; border-radius:5px; white-space:pre-wrap; margin-top:10px; }");
        sb.append(".menu-btn { margin-top:20px; padding:10px 15px; background-color:#007bff; color:#fff; border:none; border-radius:5px; cursor:pointer; }");
        sb.append(".menu-btn:hover { background-color:#0056b3; }");
        sb.append("</style>");
        sb.append("</head><body>");
        sb.append("<div class='camera-result'>");
        sb.append("<h1>방범 카메라 실행 결과</h1>");

        // 파이썬 스크립트 실행
        ProcessBuilder pb = new ProcessBuilder(PYTHON_EXE, PYTHON_SCRIPT);
        Process process = null;
        try {
            process = pb.start();
        } catch (Exception e) {
            sb.append("<p class='msg' style='color:red;'>파이썬 스크립트 실행 중 오류: ").append(e.getMessage()).append("</p>");
            sb.append("<button class='menu-btn' onclick=\"location.href='").append(request.getContextPath()).append("/menu.jsp'\">메뉴로 돌아가기</button>");
            sb.append("</div></body></html>");
            response.getWriter().print(sb.toString());
            return;
        }

        // 표준 출력, 에러 읽기
        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
        BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

        StringBuilder stdoutContent = new StringBuilder();
        StringBuilder stderrContent = new StringBuilder();

        String line;
        while ((line = stdOut.readLine()) != null) {
            stdoutContent.append(line).append("\n");
        }
        while ((line = stdErr.readLine()) != null) {
            stderrContent.append(line).append("\n");
        }

        int exitVal = 0;
        try {
            exitVal = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 결과 처리 로직
        if (exitVal == 0) {
            // 정상 종료
            sb.append("<p class='msg'>방범 카메라가 정상적으로 실행 및 종료되었습니다.</p>");
            sb.append("<p class='msg'>촬영된 영상은 YOUR_USER_PATH/Desktop/bb camara/ 폴더에 저장됩니다.</p>");

        } else {
            // 비정상 종료 → 에러 로그 표시
            sb.append("<p class='msg' style='color:red;'>방범 카메라 실행 중 오류가 발생했습니다. (exit code = ")
                    .append(exitVal).append(")</p>");
            sb.append("<p>아래 로그를 참고하세요:</p>");
            sb.append("<div class='log-box'>");
            sb.append("<strong>STDOUT:</strong>\n").append(stdoutContent.toString());
            sb.append("\n\n<strong>STDERR:</strong>\n").append(stderrContent.toString());
            sb.append("</div>");
        }

        sb.append("<button class='menu-btn' onclick=\"location.href='").append(request.getContextPath()).append("/menu.jsp'\">메뉴로 돌아가기</button>");

        sb.append("</div></body></html>");
        response.getWriter().print(sb.toString());
    }
}
