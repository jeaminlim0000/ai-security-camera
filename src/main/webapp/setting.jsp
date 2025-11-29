<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<html>
<head>
    <title>설정 페이지</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            margin: 0;
            padding: 0;
        }
        /* 오른쪽 상단 메뉴 컨테이너 */
        .top-right-menu {
            text-align: right;
            margin: 10px;
        }
        /* 공통 버튼 스타일 */
        .menu-btn {
            margin-left: 5px;
            padding: 10px 15px;
            background-color: #007bff;
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 0.9rem;
        }
        .menu-btn:hover {
            background-color: #0056b3;
        }
        /* 로그아웃 버튼(붉은색) */
        .logout-btn {
            margin-left: 5px;
            padding: 10px 15px;
            background-color: #dc3545; /* 빨간색 */
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 0.9rem;
        }
        .logout-btn:hover {
            background-color: #c82333;
        }

        /* 설정 컨테이너 */
        .setting-container {
            width: 600px;
            margin: 50px auto;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.2);
            text-align: center;
            padding: 30px;
        }
        h1 {
            color: #333;
            margin-bottom: 20px;
        }
        .btn {
            margin: 10px;
            padding: 12px 20px;
            font-size: 1rem;
            color: #fff;
            background-color: #007bff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
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
        .form-group {
            margin: 15px 0;
            text-align: left;
        }
        label {
            display: inline-block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], input[type="password"] {
            width: 90%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        .message {
            margin: 15px;
            color: red; /* 메시지 시 빨간색 */
            font-weight: bold;
        }
    </style>
    <script>
        function confirmDelete() {
            return confirm("정말 회원 탈퇴하시겠습니까? 탈퇴하면 되돌릴 수 없습니다.");
        }
    </script>
</head>
<body>

<%
    // 로그인 사용자 체크
    session = request.getSession(false);
    if (session == null || session.getAttribute("loggedInUser") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>

<!-- 오른쪽 상단 메뉴 -->
<div class="top-right-menu">
    <!-- 메뉴로 돌아가기 -->
    <button class="menu-btn"
            onclick="location.href='<%= request.getContextPath() %>/menu.jsp'">
        메뉴로 돌아가기
    </button>

    <!-- 게시판 이동 -->
    <button class="menu-btn"
            onclick="location.href='<%= request.getContextPath() %>/BoardListServlet'">
        게시판
    </button>

    <!-- 방범 카메라 -->
    <button class="menu-btn"
            onclick="location.href='<%= request.getContextPath() %>/CameraServlet'">
        방범 카메라
    </button>

    <!-- 방범 카메라 편집 -->
    <button class="menu-btn"
            onclick="location.href='<%= request.getContextPath() %>/CameraEditServlet'">
        방범 카메라 편집
    </button>

    <!-- 로그아웃 버튼 (POST로 logout 서블릿) -->
    <form action="<%= request.getContextPath() %>/logout" method="post" style="display:inline;">
        <button type="submit" class="logout-btn">로그아웃</button>
    </form>
</div>

<div class="setting-container">
    <h1>설정 페이지</h1>
    <p>여기에서 원하는 설정을 변경할 수 있습니다.</p>

    <!-- 아이디/비밀번호 변경 폼 -->
    <form action="<%= request.getContextPath() %>/UserUpdateServlet" method="post">
        <div class="form-group">
            <label>새 아이디:</label><br>
            <input type="text" name="newId" placeholder="새 아이디를 입력" required>
        </div>
        <div class="form-group">
            <label>새 비밀번호:</label><br>
            <input type="password" name="newPwd" placeholder="새 비밀번호를 입력" required>
        </div>
        <button type="submit" class="btn">정보 변경</button>
    </form>

    <hr style="margin: 30px 0;">

    <!-- 회원 탈퇴 버튼 (POST 전송) -->
    <form action="<%= request.getContextPath() %>/UserDeleteServlet" method="post"
          onsubmit="return confirmDelete()">
        <button type="submit" class="btn delete-btn">회원 탈퇴</button>
    </form>

    <%
        // UserUpdateServlet 등에서 setAttribute("message", "...")로 전달 가능
        String message = (String) request.getAttribute("message");
        if (message != null) {
    %>
    <p class="message"><%= message %></p>
    <%
        }
    %>
</div>
</body>
</html>
