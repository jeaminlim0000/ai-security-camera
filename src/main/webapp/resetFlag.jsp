<%@ page import="java.io.*" %>
<%
    String flagFilePath = application.getRealPath("/pistol_flag.txt");


    try (FileWriter fw = new FileWriter(flagFilePath, false)) {
        fw.write("none\n");
    } catch (Exception e) {
        out.print("리셋 중 오류: " + e.getMessage());
    }
%>
<%--피스톨 감지 파일 리셋용도--%>