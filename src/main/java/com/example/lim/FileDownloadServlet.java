package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/FileDownloadServlet")
public class FileDownloadServlet extends HttpServlet {

    // 외부 폴더 경로 (BoardWriteServlet과 동일)
    private static final String EXTERNAL_UPLOAD_PATH = "C:/upload_files";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // fileName 파라미터로 파일명 전달받음
        String fileName = request.getParameter("fileName");
        if (fileName == null || fileName.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "파일 이름이 없습니다.");
            return;
        }

        // 실제 파일 객체
        File file = new File(EXTERNAL_UPLOAD_PATH, fileName);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "파일을 찾을 수 없습니다.");
            return;
        }

        // MIME 타입 설정 (이미지면 image/png, video면 video/mp4 등)
        // 간단히 application/octet-stream으로 전송해도 되지만, 이미지 표시하려면 정확한 타입이 낫다.
        // 예시) response.setContentType(getServletContext().getMimeType(fileName));
        // 브라우저가 파일을 제대로 해석하도록 MIME 타입 설정
        // 예: "video/mp4", "image/png" 등
        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) {
            // 알 수 없는 타입은 일단 바이너리로
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);

        // 파일을 스트림으로 전송
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            fis = new FileInputStream(file);
            os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } finally {
            if (fis != null) try { fis.close(); } catch (IOException e) {}
            if (os != null) try { os.close(); } catch (IOException e) {}
        }
    }
}
