<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="jach.msthesis.registration.model.datasource.RegistCurriculumSource"%>
<%@page import="jach.msthesis.registration.model.datasource.MyTranscriptSource"%>
<%@page import="jach.msthesis.courselector.CourseSelector"%>
<%@page import="jach.msthesis.scheduler.constraints.Constraint"%>
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
	RegistCurriculumSource curriculumSource=new RegistCurriculumSource(prefix+"bscs-1996.cur");		
	RegistOffering offering=new RegistOffering(prefix+"2002-1.ofr");
	MyTranscriptSource transcriptSource=new MyTranscriptSource(prefix+"2001-56024.trn",curriculumSource.getCurriculum());
		
	CourseSelector sel=new CourseSelector(transcriptSource.getTranscript(),offering);
	
	AllScheduleOptionGenerator skeduler=new AllScheduleOptionGenerator(sel.getRankedSuggestedSubjects(),offering);
	out.println("Student Number: "+transcriptSource.getTranscript().getStudentNumber()+"<br/>");
	out.println("Curriculum: "+curriculumSource.getCurriculum().getName()+"<br/>");
	out.println("Total Units in Curriculum:"+curriculumSource.getCurriculum().getTotalUnits()+"<br/>");
	out.println("Total Units (2nd Yr):"+curriculumSource.getCurriculum().getTotalUnits(2)+"<br/>");
	out.println("Units earned:"+transcriptSource.getTranscript().getTotalUnits()+"<br/>");
	out.println("Classification:"+transcriptSource.getTranscript().getClassification().getDescription()+"<br/>");

	out.println("Recommended Subjects:<br/>");
	Iterator iter=sel.getRankedSuggestedSubjects().iterator();
	while(iter.hasNext()){
		SuggestedSubject s=(SuggestedSubject)iter.next();
		out.println(s.getName()+":"+s.getRank()+"<br/>");
	}

	
	
	skeduler.setMinUnits(15);
	skeduler.addConstraint(Constraint.NOFRIDAYS);
//	skeduler.addConstraint(Constraint.NOMONDAYS);
	skeduler.addConstraint(Constraint.NOLUNCH);
//	skeduler.addConstraint(Constraint.NOSEVENTOTEN);
	
	
	List skeds = skeduler.getAllSked();

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