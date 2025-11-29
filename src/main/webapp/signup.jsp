<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>회원가입</title>
</head>
<body>
<h1>회원가입 페이지</h1>
<form action="${pageContext.request.contextPath}/SignupServlet" method="post">
    <label for="userId">아이디:</label>
    <input type="text" name="userId" id="userId" required>
    <br><br>

    <label for="password">비밀번호:</label>
    <input type="password" name="password" id="password" required>
    <br><br>

    <label for="userName">이름:</label>
    <input type="text" name="userName" id="userName" required>
    <br><br>

    <label for="email">이메일:</label>
    <input type="email" name="email" id="email" required>
    <br><br>

    <label for="birth">생년월일(YYYY-MM-DD):</label>
    <input type="text" name="birth" id="birth" placeholder="1990-01-01" required>
    <br><br>

    <input type="submit" value="회원가입">
</form>
<br>
<a href="index.jsp">로그인 페이지로 돌아가기</a>
</body>
</html>
