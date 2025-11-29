package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/UserUpdateServlet")
public class UserUpdateServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "YOUR_DB_USER";
    private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) 로그인 사용자 체크
        HttpSession session = request.getSession(false);
        String loggedInUser = (session != null) ? (String) session.getAttribute("loggedInUser") : null;
        if (loggedInUser == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 2) 파라미터(새 아이디, 새 비밀번호)
        request.setCharacterEncoding("UTF-8");
        String newId = request.getParameter("newId");
        String newPwd = request.getParameter("newPwd");

        if (newId == null || newPwd == null) {
            request.setAttribute("message", "입력이 올바르지 않습니다.");
            request.getRequestDispatcher("setting.jsp").forward(request, response);
            return;
        }

        // 3) DB UPDATE: user_infoo 테이블에서 di=?(현재 로그인 ID)인 행을 찾아
        // di=newId, pwd=newPwd 로 수정
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "UPDATE user_infoo SET id=?, pwd=? WHERE id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newId);
            pstmt.setString(2, newPwd);
            pstmt.setString(3, loggedInUser);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // 성공 시 세션에 저장된 loggedInUser도 갱신
                session.setAttribute("loggedInUser", newId);

                // 설정 페이지로 메시지 전달
                request.setAttribute("message", "아이디/비밀번호가 변경되었습니다.");
            } else {
                // rows=0 → 조건 불일치(혹은 이미 삭제된 사용자)
                request.setAttribute("message", "정보 변경 실패. 다시 시도해주세요.");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("message", "드라이버 로드 실패");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "DB 에러: " + e.getMessage());
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }

        // 4) 다시 설정 페이지로 forward
        request.getRequestDispatcher("setting.jsp").forward(request, response);
    }
}
