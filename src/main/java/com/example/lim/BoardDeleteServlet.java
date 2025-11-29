package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/BoardDeleteServlet")
public class BoardDeleteServlet extends HttpServlet {

    // DB 접속 정보 (BoardEditServlet 등과 동일하게 맞춰야 함)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "YOUR_DB_USER";  // 실제 사용자명
    private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";   // 실제 비밀번호

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) 글 번호 파라미터
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect("BoardListServlet");
            return;
        }
        int postId = Integer.parseInt(idParam);

        // 2) 로그인 사용자 체크
        HttpSession session = request.getSession();
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 3) DB DELETE: DELETE FROM board_post WHERE id=? AND writer=?
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // 드라이버 로드
            Class.forName("com.mysql.jdbc.Driver");
            // DB 연결
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "DELETE FROM board_post WHERE id=? AND writer=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            pstmt.setString(2, loggedInUser);

            int rows = pstmt.executeUpdate();
            System.out.println("삭제된 행 수: " + rows);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 자원 정리
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }

        // 4) 목록으로 리다이렉트
        response.sendRedirect("BoardListServlet");
    }
}
