<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>총기 감지 테스트</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: 'Noto Sans', sans-serif;
            background-color: #f0f0f0;
        }
        .container {
            width: 600px;
            margin: 80px auto;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.2);
            text-align: center;
            padding: 30px;
        }
        h1 {
            margin-bottom: 20px;
            color: #333;
        }
        .info {
            margin: 20px 0;
            color: #555;
        }
        .btn {
            display: inline-block;
            margin: 10px;
            padding: 12px 20px;
            font-size: 1rem;
            color: #fff;
            background-color: #007bff; /* 파란색 */
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .alert-message {
            margin-top: 20px;
            color: #ff0000;
            font-weight: bold;
        }
    </style>
    <script>
        // 3초마다 자동 체크
        setInterval(checkPistolFlag, 3000);

        // 수동 체크 버튼 클릭 시
        function manualCheck() {
            checkPistolFlag();
        }

        // pistol_flag.txt를 읽어와 "detected"면 알림 표시
        function checkPistolFlag() {
            fetch('<%= request.getContextPath() %>/pistol_flag.txt')
                .then(res => res.text())
                .then(data => {
                    data = data.trim();
                    if (data === "detected") {
                        // 1) 자바스크립트 alert
                        alert("총기를 가진 사람 발견!");

                        // 2) 화면에 메시지 표시 (예시)
                        var msgDiv = document.getElementById("alertMsg");
                        msgDiv.innerHTML = "총기를 가진 사람 발견!";
                        // 필요한 경우 resetFlag.jsp 등으로 플래그 초기화 가능
                        // fetch('<%= request.getContextPath() %>/resetFlag.jsp');
                    }
                })
                .catch(err => console.log("파일 읽기 오류:", err));
        }
    </script>
</head>
<body>
<div class="container">
    <h1>총기 감지 테스트 페이지</h1>
    <p class="info">3초마다 <strong>pistol_flag.txt</strong>를 확인하여 "detected"면 알림을 띄웁니다.</p>

    <!-- 수동 체크 버튼 -->
    <button class="btn" onclick="manualCheck()">수동으로 체크하기</button>

    <!-- 감지 메시지 표시 영역 -->
    <div id="alertMsg" class="alert-message"></div>
</div>
</body>
</html>
