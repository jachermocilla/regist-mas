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

	List skeds;

	skeds =(List)session.getAttribute("skeds-myown");		
	String type = request.getParameter("type");
	if (type != null){
		if (type.equals("regist")){
			skeds =(List)session.getAttribute("skeds-regist");		
		}
	}
	
	
	

	String matrix[]=new String[72];

	int skedid=Integer.parseInt(request.getParameter("skedid"));

	List sked=(List)skeds.get(skedid);
	
	Iterator ite=sked.iterator();
	while(ite.hasNext()){
		SkedNode node=(SkedNode)ite.next();
		Iterator slots=node.getSection().getTimeSlots().iterator();
		while(slots.hasNext()){
			TimeSlot slot=(TimeSlot)slots.next();
			//out.println(slot.getSlot());
			//matrix[slot.getSlot()]=node.getName();
			ISection section = (Section)node.getSection();
			if (type!=null)
				matrix[slot.getSlot()]="<a href='classlist.jsp?type=regist&subject="+section.getSubject().getName()+"&section="+section.getSectionName()+"'>"+section.getSubject().getName()+" "+section.getSectionName()+"</a>";
			else				
				matrix[slot.getSlot()]="<a href='classlist.jsp?subject="+section.getSubject().getName()+"&section="+section.getSectionName()+"'>"+section.getSubject().getName()+" "+section.getSectionName()+"</a>";
		}
	}
	
	//out.println("<table border='1'>");
	
%>
	<table border="1">
	<tr>
		<td><b>&nbsp;</b></td>
		<td><b>Monday</b></td>
		<td><b>Tuesday</b></td>
		<td><b>Wednesday</b></td>
		<td><b>Thursday</b></td>
		<td><b>Friday</b></td>
		<td><b>Saturday</b></td>
	</tr>
<%
	
	for (int i=0;i<12;i++){
		out.println("<tr>");
		int t=(i+7)%12;
		int u=(t+1);
		if (t==0)
			t=12;
		out.println("<td><b>"+(t)+"-"+(u)+"</b></td>");
		for (int j=0;j<6;j++){
			String val=matrix[j+(i*6)];
			if (val !=null){
				out.println("<td>"+val+"</td>");
			}else{
				out.println("<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");			
			}
		}
		out.println("</tr>");
	}

%>
	</table>
<%
		
	out.println("<a href='viewsked.jsp?skedid="+(0)+"'>[START]</a>");
	if (skedid > 0){
		out.println("<a href='viewsked.jsp?skedid="+(skedid-1)+"'>[PREV]</a>");
	}
	if (skedid < skeds.size()-1){
		out.println("<a href='viewsked.jsp?skedid="+(skedid+1)+"'>[NEXT]</a>");
	}
	out.println("<a href='viewsked.jsp?skedid="+(skeds.size()-1)+"'>[END]</a>");

	
%>

</body>
</html>