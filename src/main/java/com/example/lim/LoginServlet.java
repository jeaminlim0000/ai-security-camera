package com.example.lim;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {

    // DB 접속 정보 (useSSL=false 추가)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC&useSSL=false";
    private static final String DB_USER = "YOUR_DB_USER";
    private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 한글 인코딩
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 로그인 폼 파라미터
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe"); // "아이디 기억하기" 체크 여부

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            //구형 드라이버명
            Class.forName("com.mysql.jdbc.Driver");
            // DB 연결
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // 아이디/비번 확인 (user_infoo 테이블 예시)
            String sql = "SELECT * FROM user_infoo WHERE id=? AND pwd=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 로그인 성공 시, 세션에 사용자 정보 저장
                HttpSession session = request.getSession();
                session.setAttribute("loggedInUser", username);

                // "아이디 기억하기" 체크 시 쿠키 저장 (편의 기능)
                if (rememberMe != null) {
                    Cookie userCookie = new Cookie("username", username);
                    userCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
                    userCookie.setPath("/");
                    response.addCookie(userCookie);
                } else {
                    // 체크 안 했으면 기존 쿠키 삭제
                    Cookie userCookie = new Cookie("username", "");
                    userCookie.setMaxAge(0);
                    userCookie.setPath("/");
                    response.addCookie(userCookie);
                }

                // 로그인 성공 후 menu.jsp로 이동
                response.sendRedirect("menu.jsp");
            } else {
                // 로그인 실패
                response.sendRedirect("index.jsp?error=1");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("드라이버 로드 실패");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("DB 에러: " + e.getMessage());
        } finally {
            // 자원 정리
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
