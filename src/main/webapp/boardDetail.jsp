<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.lim.Post" %>
<html>
<head>
    <title>게시글 상세</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
        .detail-container {
            width: 80%;
            margin: 20px auto;
            background: #fff;
            padding: 20px;
            border-radius: 5px;
        }
        .btn {
            display: inline-block;
            margin: 5px;
            padding: 8px 12px;
            background-color: #007bff;
            color: #fff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9rem;
            text-decoration: none;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .delete-btn {
            background-color: #dc3545; /* 빨간색 */
        }
        .delete-btn:hover {
            background-color: #c82333;
        }
        img {
            max-width: 400px;
        }
    </style>
</head>
<body>
<%
    Post post = (Post) request.getAttribute("post");
    if (post == null) {
%>
<p>존재하지 않는 게시글입니다.</p>
<a href="<%= request.getContextPath() %>/BoardListServlet" class="btn">목록으로</a>
<%
} else {

    // 로그인 사용자 ID(세션) 가져오기
    String loggedInUser = (String) session.getAttribute("loggedInUser");
    if (loggedInUser == null) {
        loggedInUser = "";
    }
%>
<div class="detail-container">
    <h1>게시글 상세</h1>
    <p>번호: <%= post.getId() %></p>
    <p>제목: <%= post.getTitle() %></p>
    <p>작성자: <%= post.getWriter() %></p>
    <p>작성일: <%= post.getCreatedAt() %></p>
    <hr>
    <p>내용:<br>
        <%= post.getContent().replaceAll("\n", "<br>") %>
    </p>
    <hr>
    <%
        // 첨부 이미지
        String imagePath = post.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
    %>
    <p>첨부 이미지:</p>
    <img src="<%= request.getContextPath() %>/FileDownloadServlet?fileName=<%= imagePath %>"
         alt="이미지">
    <%
        }

        // 첨부 동영상
        String videoPath = post.getVideoPath();
        if (videoPath != null && !videoPath.isEmpty()) {
    %>
    <hr>
    <p>첨부 동영상:</p>
    <video controls width="400">
        <source src="<%= request.getContextPath() %>/FileDownloadServlet?fileName=<%= videoPath %>" type="video/mp4">
        동영상을 재생할 수 없습니다.
    </video>
    <%
        }
    %>
    <hr>
    <!-- 목록 버튼 -->
    <a class="btn" href="<%= request.getContextPath() %>/BoardListServlet">목록으로</a>

    <%
        // 게시글 작성자 == 현재 로그인 사용자면 수정/삭제 버튼 노출
        if (post.getWriter() != null && post.getWriter().equals(loggedInUser)) {
    %>
    <!-- 수정 버튼: id 파라미터로 글 번호 전달 -->
    <a class="btn"
       href="<%= request.getContextPath() %>/BoardEditServlet?id=<%= post.getId() %>">
        수정
    </a>

    <!-- 삭제 버튼: 자바스크립트 confirm -->
    <a class="btn delete-btn"
       href="<%= request.getContextPath() %>/BoardDeleteServlet?id=<%= post.getId() %>"
       onclick="return confirm('정말 삭제하시겠습니까?');">
        삭제
    </a>
    <%
        }
    %>
</div>
<%
    }
%>
</body>
</html>
