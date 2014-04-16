<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="jach.msthesis.registration.model.datasource.RegistWriteInSource"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%@ page language="java" import="jach.msthesis.scheduler.*" %> 
<%@ page language="java" import="jach.msthesis.registration.model.*" %>
<%@ page language="java" import="jach.msthesis.scheduler.regist.*" %>
<%@ page language="java" import="java.util.*" %>

<%
	String prefix=application.getRealPath("/")+"/../resources/";
	RegistWriteInSource wis = new RegistWriteInSource(prefix+"WRITEIN-2004-SECOND-CMSC");
	
	wis.load();

	
	List students = new ArrayList(wis.getWriteIns().keySet());
	Collections.sort(students);
	
	out.println("<h3>Students: </h3>");
	
	int count = 1;
	Iterator ite = students.iterator();
	while (ite.hasNext()){
		String student = (String)ite.next();
		WriteIn wri=(WriteIn)wis.getWriteIns().get(student);
		out.println("<b>"+(count++)+") "+student+"("+wri.getPriority()+")"+"</b>");		
		out.println("<a href='form5sked.jsp?stdnum="+student+"'>[regist]</a> ");
		out.println("<a href='current.jsp?stdnum="+student+"'>[current]</a> ");
		out.println("<br/>");
	}

%>

</body>
</html>