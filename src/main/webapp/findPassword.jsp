<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>비밀번호 찾기</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
            margin: 0;
            padding: 0;
        }
        /* 돌아가기 버튼을 화면 왼쪽 위에 배치 */
        .return-btn {
            position: absolute;
            top: 10px;
            left: 10px;
            padding: 8px 12px;
            font-size: 0.9rem;
            background-color: #007bff;
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .return-btn:hover {
            background-color: #0056b3;
        }
        .container {
            width: 400px;
            margin: 50px auto;
            background: #fff;
            padding: 30px;
            border-radius: 8px;
            text-align: center;
        }
        .form-input {
            margin: 10px 0;
        }
        input[type="text"] {
            width: 80%;
            padding: 8px;
        }
        .btn {
            margin: 10px;
            padding: 8px 12px;
            font-size: 1rem;
            background-color: #007bff;
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .message {
            margin: 10px;
            color: red; /* 에러 메시지일 때 빨간색 */
        }
    </style>
</head>
<body>

<!-- 돌아가기 버튼: index.jsp(로그인 페이지)로 이동 -->
<button class="return-btn"
        onclick="location.href='<%= request.getContextPath() %>/index.jsp'">
    돌아가기
</button>

<div class="container">
    <h1>비밀번호 찾기</h1>
    <form action="<%= request.getContextPath() %>/FindPasswordServlet" method="post">
        <div class="form-input">
            <label>아이디: </label><br>
            <input type="text" name="userId" required>
        </div>
        <div class="form-input">
            <label>이름: </label><br>
            <input type="text" name="name" required>
        </div>
        <div class="form-input">
            <label>이메일: </label><br>
            <input type="text" name="email" required>
        </div>
        <button type="submit" class="btn">비밀번호 찾기</button>
    </form>

    <%
        // FindPasswordServlet에서 setAttribute("message", "...") 했을 때 표시
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
