<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.lim.Post" %>
<html>
<head>
    <title>게시글 수정</title>
</head>
<body>
<%
    String message = (String) request.getAttribute("message");
    Post post = (Post) request.getAttribute("post");

    if (message != null) {
%>
<p><%= message %></p>
<a href="BoardListServlet">목록으로</a>
<%
} else if (post == null) {
%>
<p>수정할 게시글이 없습니다.</p>
<a href="BoardListServlet">목록으로</a>
<%
} else {
%>
<h1>게시글 수정</h1>
<form action="BoardEditServlet" method="post">
    <input type="hidden" name="id" value="<%= post.getId() %>">
    제목: <input type="text" name="title" value="<%= post.getTitle() %>"><br><br>
    내용:<br>
    <textarea name="content" rows="10" cols="50"><%= post.getContent() %></textarea><br><br>
    <input type="submit" value="수정하기">
</form>
<%
    }
%>
</body>
</html>
