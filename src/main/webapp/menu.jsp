<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<html>
<head>
    <title>메뉴 목록</title>
    <style>
        /* 배경 색깔  */
        body {
            margin: 0;
            padding: 0;
            font-family: 'Noto Sans', sans-serif;
            background: linear-gradient(120deg, #f6d365 0%, #fda085 100%);
        }
        .menu-container {
            max-width: 500px;
            margin: 100px auto;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.2);
            text-align: center;
            padding: 40px;
        }
        h1 {
            margin-bottom: 20px;
            color: #333;
            font-size: 1.8rem;
        }
        .menu-btn {
            display: inline-block;
            margin: 10px;
            padding: 12px 20px;
            font-size: 1rem;
            color: #fff;
            background-color: #007bff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            transition: background-color 0.3s ease, box-shadow 0.3s ease;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }
        .menu-btn:hover {
            background-color: #0056b3;
            box-shadow: 0 4px 6px rgba(0,0,0,0.3);
        }
        .menu-btn:active {
            background-color: #003d82;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }
        .logout-btn {
            background-color: #dc3545; /* 빨간색 */
        }
        .logout-btn:hover {
            background-color: #c82333;
        }
        #alertMsg {
            margin-top: 20px;
            color: red;
            font-weight: bold;
            font-size: 1.1rem;
        }
    </style>
    <script>
        // 5초마다 pistol_flag.txt 확인
        setInterval(checkPistolFlag, 5000);

        function checkPistolFlag() {
            fetch('<%= request.getContextPath() %>/pistol_flag.txt')
                .then(res => res.text())
                .then(data => {
                    data = data.trim();
                    if (data === "detected") {
                        alert("총기를 가진 사람 발견!");
                        document.getElementById("alertMsg").textContent = "총기를 가진 사람 발견!";
                        //여기서 파일을 다시 "none"으로 리셋해서 계속 뜨는거 방지
                        fetch('<%= request.getContextPath() %>/resetFlag.jsp');
                    }
                })
                .catch(err => console.log("파일 읽기 오류:", err));
        }
    </script>
</head>
<body>

<%
    // 로그인 세션 확인
    session = request.getSession(false);
    if (session == null || session.getAttribute("loggedInUser") == null) {
        // 세션 없거나 loggedInUser가 없으면 → 로그인 페이지로
        response.sendRedirect("index.jsp");
        return;
    }
%>

<div class="menu-container">
    <h1>메뉴 목록</h1>

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

    <!-- 설정 버튼: 클릭 시 setting.jsp로 이동 -->
    <button class="menu-btn"
            onclick="location.href='<%= request.getContextPath() %>/setting.jsp'">
        설정
    </button>

    <!-- 로그아웃 버튼 (POST 전송) -->
    <form action="<%= request.getContextPath() %>/logout" method="post" style="display:inline;">
        <button type="submit" class="menu-btn logout-btn">로그아웃</button>
    </form>

    <div id="alertMsg"></div>
</div>
</body>
</html>
