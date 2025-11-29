package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/CameraEditServlet")
public class CameraEditServlet extends HttpServlet {

    // 파이썬 실행 파일 (Windows 환경이라면 "python" 또는 "C:/Python39/python.exe" 등)
    private static final String PYTHON_EXE = "python";

    // PotoCapcher.py 경로
    private static final String PYTHON_SCRIPT = "YOUR_USER_PATH/Desktop/python/PotoCapcher.py";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 파이썬 스크립트를 서버 측에서 실행
        ProcessBuilder pb = new ProcessBuilder(PYTHON_EXE, PYTHON_SCRIPT);

        try {
            Process process = pb.start();

            // 파이썬 표준 출력, 표준 오류를 읽어들인다
            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));

            // 결과를 담을 버퍼
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = stdOut.readLine()) != null) {
                output.append(line).append("<br>");
            }
            while ((line = stdErr.readLine()) != null) {
                output.append("ERR: ").append(line).append("<br>");
            }

            // 파이썬 프로세스 종료 대기
            int exitVal = process.waitFor();
            output.append("<br>파이썬 스크립트 종료 (exit code = ").append(exitVal).append(")");

            // 웹 페이지에 결과 출력
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>방범 카메라 편집 결과</h1>");
            response.getWriter().println(output.toString());
            response.getWriter().println("<br><a href='" + request.getContextPath() + "/menu.jsp'>메뉴로 돌아가기</a>");
            response.getWriter().println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("파이썬 스크립트 실행 중 오류: " + e.getMessage());
        }
    }
}
