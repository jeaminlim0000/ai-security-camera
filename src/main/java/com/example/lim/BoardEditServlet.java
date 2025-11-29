package com.example.lim;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/BoardEditServlet")
public class BoardEditServlet extends HttpServlet {

    // DB 접속 정보 (예시)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/security_camera?serverTimezone=UTC";
    private static final String DB_USER = "YOUR_DB_USER";
    private static final String DB_PASSWORD = "YOUR_DB_PASSWORD";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) 글 번호 파라미터 받기
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect("BoardListServlet");
            return;
        }
        int postId = Integer.parseInt(idParam);

        // 2) DB에서 해당 글 조회
        Post post = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 드라이버 로드 (MySQL 8.x)
            Class.forName("com.mysql.jdbc.Driver");
            // DB 연결
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT * FROM board_post WHERE id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                post = new Post();
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setWriter(rs.getString("writer"));
                // TODO: 필요한 컬럼 추가로 set
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // 드라이버 로드 실패
        } catch (SQLException e) {
            e.printStackTrace(); // DB 에러
        } finally {
            // 자원 정리
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }

        // 3) 게시글이 없는 경우
        if (post == null) {
            request.setAttribute("message", "수정할 게시글이 없습니다.");
            request.getRequestDispatcher("boardEdit.jsp").forward(request, response);
            return;
        }

        // 4) 작성자 == 로그인 사용자 체크 (보안)
        HttpSession session = request.getSession();
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            // 로그인 안 했다면 → 로그인 페이지
            response.sendRedirect("index.jsp");
            return;
        }

        // 작성자와 불일치 시 에러 처리
        if (!post.getWriter().equals(loggedInUser)) {
            request.setAttribute("message", "본인 글만 수정할 수 있습니다.");
            request.getRequestDispatcher("boardEdit.jsp").forward(request, response);
            return;
        }

        // 5) 수정 폼으로 forward
        request.setAttribute("post", post);
        request.getRequestDispatcher("boardEdit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (idParam == null) {
            response.sendRedirect("BoardListServlet");
            return;
        }
        int postId = Integer.parseInt(idParam);

        // 세션에서 로그인 사용자
        HttpSession session = request.getSession();
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // DB UPDATE (id와 writer 둘 다 매칭)
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "UPDATE board_post SET title=?, content=? WHERE id=? AND writer=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, postId);
            pstmt.setString(4, loggedInUser);

            int rows = pstmt.executeUpdate();
            // rows>0 이면 수정 성공
            System.out.println("수정된 행 수: " + rows);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }

        // 수정 후 상세 페이지로
        response.sendRedirect("BoardDetailServlet?id=" + postId);
    }
}
