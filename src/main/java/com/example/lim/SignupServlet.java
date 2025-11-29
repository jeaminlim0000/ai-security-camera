package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC";
    private static final String DB_USER = "YOUR_DB_USER";
    private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";  // 실제 비밀번호로 수정

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) 요청(request) 한글 인코딩 설정
        request.setCharacterEncoding("UTF-8");

        // 2) 응답(response) 콘텐츠 타입 및 인코딩 설정
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 폼에서 넘어온 데이터 꺼내기
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String userName = request.getParameter("userName");
        String email = request.getParameter("email");
        String birth = request.getParameter("birth");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "INSERT INTO user_infoo (id, pwd, name, email, birth, reg_date) " +
                    "VALUES (?, ?, ?, ?, ?, NOW())";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            pstmt.setString(3, userName);
            pstmt.setString(4, email);
            pstmt.setString(5, birth);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // 가입 성공 시 로그인 페이지로 이동
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            } else {
                // 가입 실패 시 오류 메시지
                response.getWriter().println("회원가입 실패! 다시 시도해보세요.");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("드라이버 로드 실패");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("DB 에러: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
