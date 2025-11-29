<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession, java.util.List, com.example.lim.Post" %>
<html>
<head>
    <title>게시판</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #e8e8e8;
            margin: 0;
            padding: 0;
        }
        .board-container {
            width: 80%;
            margin: 20px auto;
            background: #fff;
            padding: 20px;
            border-radius: 5px;
        }
        .top-right-menu {
            text-align: right;
            margin-bottom: 10px;
        }
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
        .logout-btn {
            margin-left: 5px;
            padding: 10px 15px;
            background-color: #dc3545;
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 0.9rem;
        }
        .logout-btn:hover {
            background-color: #c82333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ccc;
            padding: 8px;
        }
        .paging {
            margin-top: 15px;
        }
        .paging a {
            margin: 0 5px;
            text-decoration: none;
            color: #007bff;
        }
        .paging a:hover {
            text-decoration: underline;
        }
        .paging strong {
            margin: 0 5px;
        }
        #alertMsg {
            margin-top: 20px;
            color: red;
            font-weight: bold;
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
                    }
                })
                .catch(err => console.log("파일 읽기 오류:", err));
        }
    </script>
</head>
<body>

<%
    // 세션에서 로그인 사용자 확인
    session = request.getSession(false);
    if (session == null || session.getAttribute("loggedInUser") == null) {
        // 세션에 loggedInUser가 없으면 → 로그인 페이지로
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    String username = (String) session.getAttribute("loggedInUser");

    // BoardListServlet에서 넘겨준 게시글 목록/페이징
    List<Post> postList = (List<Post>) request.getAttribute("postList");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPage = (Integer) request.getAttribute("totalPage");
    Integer totalCount = (Integer) request.getAttribute("totalCount");
    if (currentPage == null) currentPage = 1;
    if (totalPage == null) totalPage = 1;
    if (totalCount == null) totalCount = 0;
%>

<div class="board-container">

    <!-- 오른쪽 상단 메뉴 버튼들 -->
    <div class="top-right-menu">
        <!-- 메뉴 페이지로 돌아가기 버튼 -->
        <button class="menu-btn"
                onclick="location.href='<%= request.getContextPath() %>/menu.jsp'">
            메뉴로 돌아가기
        </button>

        <!-- 방범 카메라 버튼 -->
        <button class="menu-btn"
                onclick="location.href='<%= request.getContextPath() %>/CameraServlet'">
            방범 카메라
        </button>

        <!-- 방범 카메라 편집 버튼 -->
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
            <button type="submit" class="logout-btn">로그아웃</button>
        </form>
    </div>

    <h1>게시판</h1>
    <p>환영합니다, <%= username %>님!</p>
    <hr>

    <!-- 글쓰기 버튼 -->
    <p>
        <a href="write.jsp">글쓰기</a>
    </p>

    <!-- 게시글 목록 -->
    <p>총 게시글 수: <%= totalCount %></p>
    <table>
        <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
        </tr>
        <%
            if (postList != null) {
                for (Post p : postList) {
        %>
        <tr>
            <td><%= p.getId() %></td>
            <td>
                <a href="<%= request.getContextPath() %>/BoardDetailServlet?id=<%= p.getId() %>">
                    <%= p.getTitle() %>
                </a>
            </td>
            <td><%= p.getWriter() %></td>
            <td><%= p.getCreatedAt() %></td>
        </tr>
        <%
                }
            }
        %>
    </table>

    <!-- 페이지 이동 -->
    <div class="paging">
        <%
            // 이전 페이지
            if (currentPage > 1) {
        %>
        <a href="<%= request.getContextPath() %>/BoardListServlet?page=<%= currentPage - 1 %>">이전</a>
        <%
            }

            // 중간 페이지 번호 링크
            for (int i = 1; i <= totalPage; i++) {
                if (i == currentPage) {
        %>
        <strong><%= i %></strong>
        <%
        } else {
        %>
        <a href="<%= request.getContextPath() %>/BoardListServlet?page=<%= i %>"><%= i %></a>
        <%
                }
            }

            // 다음 페이지
            if (currentPage < totalPage) {
        %>
        <a href="<%= request.getContextPath() %>/BoardListServlet?page=<%= currentPage + 1 %>">다음</a>
        <%
            }
        %>
    </div>

    <!-- 감지 알림 메시지 표시 영역 -->
    <div id="alertMsg"></div>
</div>

</body>
</html>
