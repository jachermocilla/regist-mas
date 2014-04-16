<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="jach.msthesis.scheduler.statistics.Statistics"%>
<%@page import="jach.msthesis.registration.model.datasource.RegistWriteInSource"%>
<%@page import="jach.msthesis.registration.model.datasource.RegistOffering"%>
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
	ScheduleValidator scheduler;
	String prefix=application.getRealPath("/")+"data/";
	RegistWriteInSource wis = new RegistWriteInSource(prefix+"WRITEIN-2004-SECOND");
	RegistOffering offering=new RegistOffering(prefix+"CLASSES-2004-SECOND");
	
	wis.load();
	offering.load();
	
	scheduler = new ScheduleValidator(offering,wis.getWriteIns());
	scheduler.loadForm5(prefix+"FORM5-2004-SECOND");
	
	Statistics stat=new Statistics(scheduler);
	stat.display();
	
	Set studs = scheduler.getStudents(); 
	int randStud = new Random().nextInt(studs.size());
	String stdNum;
	
	stdNum = request.getParameter("stdnum");
	if (stdNum == null)
		stdNum=(String)studs.toArray()[randStud];
	//stdNum = "2001-56024";
	
	out.println("<h3>Student Number: " + stdNum + "</h3>");
	IWriteIn writein = scheduler.getWriteIn(stdNum);
	Schedule skedule = (Schedule)scheduler.getSchedule(stdNum);
	
	out.println("Writein: "+writein.getUnitsAllowed() +" units allowed<br/>");
	float writeinTotal=0;
	Iterator ite1=writein.getSubjects().iterator();
	while (ite1.hasNext()){
		ISubject s=(ISubject)ite1.next();
		writeinTotal += s.getUnitCredit();
		out.println(s.getName()+" (  "+ s.getUnitCredit() +" )<br/>");
	}
	
	out.println("Form 5: "+skedule.getUnitsObtained() +" units obtained<br/>");
	double p=stat.getPercentageLoad(stdNum);
	ite1=skedule.getSectionAssignments().iterator();
	while (ite1.hasNext()){
		SectionAssignment sectAss=(SectionAssignment)ite1.next();
		out.println(sectAss.getLecture().getSubject().getName()+" "+sectAss.getLecture().getSectionName()+" ");
		if (sectAss.getLab() != null){
			out.println(sectAss.getLab().getSectionName()+"<br/>");
		}else{
			out.println("<br/>");
		}
	}
	
	out.println("Percentage Load [ "+stdNum+" ] :"+stat.getPercentageLoad(stdNum)+"<br/>");
	
	List skeds = new Vector();
	List sk = new Vector();
	
	
	
	SkedNode root = skedule.getRoot();
	root = root.getChild(0);
	//sk.add(root);
	while(root.getChildren().size() != 0){
		sk.add(root);
		root = root.getChild(0);
	}
	sk.add(root);
	
	skeds.add(sk);
	
	session.setAttribute("skeds-regist",skeds);
	session.setAttribute("classlists-regist",scheduler.getClasslists());
	
	int count=0;
	Iterator ite=skeds.iterator();
	while(ite.hasNext()){
	out.print("<a href='viewsked.jsp?type=regist&skedid=");
	out.println(count+"'>view</a>");
	count++;
	List sked=(List)ite.next();
	Iterator ite2=sked.iterator();
	while (ite2.hasNext()){
		SkedNode section=(SkedNode)ite2.next();
		//out.println(section.getSection().getSubject().getName()+":"+section.getSection().getSectionName()+"<br/>");
		out.print("<a href='classlist.jsp?type=regist&subject="+section.getSection().getSubject().getName()+"&section="+section.getSection().getSectionName()+"'>"+"["+section.getName()+"]</a>"+" -> ");		
	}
	out.println("<br>");	

	}
	out.println("Total Schedules:"+count);
%>

</body>
</html>