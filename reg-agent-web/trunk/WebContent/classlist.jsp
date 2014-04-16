<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>View Schedule</title>
</head>
<body>
<%@ page language="java" import="jach.msthesis.scheduler.*" %>
<%@ page language="java" import="jach.msthesis.registration.model.*" %>
<%@ page language="java" import="java.util.*" %>

<%
	Map classlists;

	classlists =(Map)session.getAttribute("classlists-myown");		
	String type = request.getParameter("type");
	if (type != null){
		if (type.equals("regist")){
			classlists =(Map)session.getAttribute("classlists-regist");		
		}
	}else type="";
	

	String subject = request.getParameter("subject");
	String section = request.getParameter("section");
	
	if (section == null && subject == null){
		section = "WX-1L";
		subject = "CMSC 123";
	}
	
	Classlist cl = (Classlist)classlists.get(subject+":"+section);
	out.println("<b>"+subject +" "+section+"</b><br/>");
	
	int allowed = cl.getSection().getClassSize();
	int count=1;
	Iterator ite = cl.getStudents().iterator();
	while (count <= allowed){
		String stdNum="";
		if (ite.hasNext())
	stdNum = (String)ite.next();
		out.println((count)+") ");
		
		if (!stdNum.equals("")){
	if (type.equals("regist"))
		out.println("<a href='current.jsp?type=regist&stdnum="+stdNum+"'>"+stdNum+"<a><br/>");
	else
		out.println("<a href='form5sked.jsp?stdnum="+stdNum+"'>"+stdNum+"<a><br/>");
		}else{
	out.println("<br/>");
		}
		count++;
	}
%>

</body>
</html>