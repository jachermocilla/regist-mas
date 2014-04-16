<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="jach.msthesis.scheduler.regist.RegistScheduler"%>
<%@page import="jach.msthesis.scheduler.regist.RegistSchedulerOld"%>
<%@page import="jach.msthesis.registration.model.datasource.RegistOffering"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Registration Agent (Beta)</title>
</head>
<body>
<%
java.util.Date d = new java.util.Date();
%>
<h3>
Today's date is <%=d.toString()%> and this jsp page worked!
</h3>

<%@ page language="java" import="jach.msthesis.scheduler.*" %>
<%@ page language="java" import="jach.msthesis.registration.model.*" %>
<%@ page language="java" import="java.util.*" %>
<%
	String prefix=application.getRealPath("/")+"data/";
	RegistSchedulerOld regist;
	
	RegistOffering offering=new RegistOffering(prefix+"CLASSES-2002-SECOND");
	//regist=new RegistSchedulerOld(offering);
	//regist.loadWriteIn(prefix+"WRITEIN-2002-SECOND");	
	//regist.assign();
	
	List skeds = new ArrayList();
	List tmp = new ArrayList();
	
	//tmp.add(regist.getRoot("2000-28473"));
	//skeds.add(tmp);
	
	session.setAttribute("skeds",skeds);

	int count=0;
	Iterator ite=skeds.iterator();
	while(ite.hasNext()){
		out.print("<a href='viewsked.jsp?skedid=");
		out.println(count+"'>view</a>");
		count++;
		List sked=(List)ite.next();
		Iterator ite2=sked.iterator();
		while (ite2.hasNext()){
		SkedNode section=(SkedNode)ite2.next();
		out.print("["+section.getName()+"]"+" -> ");		
	}
	out.println("<br>");	
	}
	out.println("Total Schedules:"+count);
%>

</body>
</html>