package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/FindPasswordServlet")
public class FindPasswordServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "YOUR_DB_USER";
    private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";  // 실제 비밀번호

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) 파라미터 받기
        request.setCharacterEncoding("UTF-8");
        String userId = request.getParameter("userId");
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        // 2) DB SELECT: SELECT pwd FROM user_infoo WHERE id=? AND name=? AND email=?
        // (테이블/컬럼명은 실제 DB 구조에 맞게 수정: user_infoo, di, name, email, pwd 등)
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String passwordFound = null; // 찾은 비밀번호

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT pwd FROM user_infoo WHERE id=? AND name=? AND email=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, name);
            pstmt.setString(3, email);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                // 비밀번호 찾음
                passwordFound = rs.getString("pwd");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }

        // 3) 결과에 따라 메시지 세팅
        if (passwordFound != null) {
            // 비밀번호 찾기 성공
            request.setAttribute("message", "해당 계정의 비밀번호는: " + passwordFound);
        } else {
            // 일치하는 계정 정보 없음
            request.setAttribute("message", "일치하는 정보가 없습니다. 다시 확인하세요.");
        }

        // 4) 다시 findPassword.jsp로 forward
        request.getRequestDispatcher("findPassword.jsp").forward(request, response);
    }
}
