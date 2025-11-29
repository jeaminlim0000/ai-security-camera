package com.example.lim;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 2) 쿠키 삭제 (아이디 기억하기 초기화)
        Cookie userCookie = new Cookie("username", "");
        userCookie.setMaxAge(0);  // 즉시 삭제
        userCookie.setPath("/");  // 모든 경로에서 쿠키 삭제 적용
        response.addCookie(userCookie);

        // 3) 로그아웃 후 로그인 페이지(index.jsp)로 이동
        //    컨텍스트 경로가 /ch33라면 => /ch33/index.jsp 로 이동
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}
