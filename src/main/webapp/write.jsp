<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<html>
<head>
    <title>글쓰기</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
        }
        .write-container {
            width: 600px;
            margin: 50px auto;
            background: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.2);
        }
        h1 {
            text-align: center;
            margin-bottom: 20px;
        }
        .form-group {
            margin: 15px 0;
        }
        label {
            display: inline-block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], textarea {
            width: 90%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        textarea {
            resize: vertical; /* 수직 크기 조절만 가능 */
        }
        input[type="file"] {
            margin-top: 5px;
        }
        .btn-submit {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 1rem;
            transition: background-color 0.3s ease;
        }
        .btn-submit:hover {
            background-color: #0056b3;
        }
        .readonly-field {
            background-color: #e9ecef;
            color: #495057;
        }
    </style>
</head>
<body>
<%
    // 로그인 정보 세션에서 가져오기
    session = request.getSession(false);
    String loggedInUser = "";
    if (session != null) {
        loggedInUser = (String) session.getAttribute("loggedInUser");
    }
%>

<div class="write-container">
    <h1>글쓰기 페이지</h1>
    <!-- 파일 업로드 시 반드시 enctype="multipart/form-data" 설정 -->
    <!-- 작성하기 버튼을 누르면 BoardWriteServlet에서 동영상 변환까지 자동 처리 -->
    <form action="BoardWriteServlet" method="post" enctype="multipart/form-data">

        <div class="form-group">
            <label>제목:</label><br>
            <input type="text" name="title" required>
        </div>

        <div class="form-group">
            <label>작성자:</label><br>
            <input type="text" name="writer" value="<%= loggedInUser %>"
                   readonly class="readonly-field">
        </div>

        <div class="form-group">
            <label>내용:</label><br>
            <textarea name="content" rows="5" cols="50" required></textarea>
        </div>

        <div class="form-group">
            <label>이미지 선택:</label><br>
            <input type="file" name="imageFile" accept="image/*">
        </div>

        <div class="form-group">
            <label>동영상 선택 (자동 변환):</label><br>
            <!-- name="videoFile" → 서버에서 Part로 받아 ffmpeg 변환 처리 -->
            <input type="file" name="videoFile" accept="video/*">
        </div>

        <div class="form-group" style="text-align:center;">
            <input type="submit" value="작성하기" class="btn-submit">
        </div>
    </form>
</div>
</body>
</html>
