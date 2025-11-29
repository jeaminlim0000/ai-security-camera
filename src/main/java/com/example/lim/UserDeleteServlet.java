package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/UserDeleteServlet")
public class UserDeleteServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "YOUR_DB_USER";
    private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) 로그인 사용자 확인
        HttpSession session = request.getSession();
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            // 로그인 안 되어 있으면 로그인 페이지로
            response.sendRedirect("index.jsp");
            return;
        }

        // 2) DB에서 회원 정보 삭제
        // 예: user_info 테이블, id(또는 username) 칼럼이 loggedInUser와 매칭
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 예: "DELETE FROM user_info WHERE id=?"
            String sql = "DELETE FROM user_infoo WHERE id=?";
            // ※ 실제 테이블/컬럼명에 맞게 수정
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loggedInUser);

            int rows = pstmt.executeUpdate();
            System.out.println("회원 탈퇴: " + rows + "행 삭제됨.");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 자원 정리
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }

        // 3) 세션 무효화 (로그아웃 효과)
        session.invalidate();

        // 4) 메인 페이지나 로그인 페이지로 이동
        response.sendRedirect("index.jsp");
    }
}
