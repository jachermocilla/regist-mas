<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="jach.msthesis.registration.model.datasource.RegistWriteInSource"%>
<%@page import="jach.msthesis.registration.model.datasource.RegistOffering"%>
<%@page import="jach.msthesis.scheduler.statistics.Statistics"%>
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
	RegistSchedulerImproved scheduler;	
	String prefix=application.getRealPath("/")+"data/";
	RegistOffering offering=new RegistOffering(prefix+"CLASSES-2004-SECOND");
	RegistWriteInSource wis = new RegistWriteInSource(prefix+"WRITEIN-2004-SECOND");
	
	wis.load();
	offering.load();
	
	scheduler=new RegistSchedulerImproved(offering,wis.getWriteIns());
	scheduler.init();
	scheduler.assign();

	String stdNum=request.getParameter("stdnum");
	

	
	Set studs = scheduler.getStudents(); 
	
	if (stdNum == null){
		int randStud = new Random().nextInt(studs.size());
		stdNum=(String)studs.toArray()[randStud];
	}
	
	Statistics stat=new Statistics(scheduler);
	stat.display();
	
	//stdNum = "2001-44659";
	
	out.println("<b>Allocation Statistics</b><br/>");
	out.println("Number of writeins: "+stat.getWriteInCount()+"<br/>");
	out.println("Number of writeins with schedule: "+stat.getWriteInWithScheduleCount()+"<br/>");
	
	out.println("Number of schedules: "+stat.getSchedulesCount()+"<br/>");
	out.println("Number of schedules with writein: "+stat.getScheduleWithWriteInCount()+"<br/>");
	
	out.println("Average Percentage Load: "+stat.getAveragePercentageLoad()+"<br/>");
	out.println("Full Load: "+stat.getFullLoadCount()+"<br/>");
	out.println("Underload: "+stat.getUnderloadCount()+"<br/>");
	out.println("Overload: "+stat.getOverloadCount()+"<br/>");
	out.println("Zero load: "+stat.getZeroLoadCount()+"<br/>");
	out.println("Average Percentage Class Size "+stat.getAveragePercentageClassSize()+"<br/>");
	
	out.println("<h3>Student Number: " + stdNum + "</h3>");
	out.println("<a href='current.jsp?type=regist&stdnum="+stdNum+"'>Click here for original allocation</a><br/>");
	IWriteIn writein = scheduler.getWriteIn(stdNum);
	Schedule skedule = scheduler.getSchedule(stdNum);
	
	out.println("Writein: "+writein.getUnitsAllowed() +" units allowed<br/>");
	float writeinTotal=0;
	Iterator ite1=writein.getSubjects().iterator();
	while (ite1.hasNext()){
		ISubject s=(ISubject)ite1.next();
		writeinTotal += s.getUnitCredit();
		out.println(s.getName()+" (  "+ s.getUnitCredit() +" )<br/>");
	}
	
	out.println("Form 5: "+skedule.getUnitsObtained() +" units obtained<br/>");
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

	//out.println("Percentage Load [ "+stdNum+" ] :"+scheduler.getPercentageLoad(stdNum)+"<br/>");
	
	
	
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
	
	session.setAttribute("skeds-myown",skeds);
	session.setAttribute("classlists-myown",scheduler.getClasslists());

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
			//out.print("["+section.getName()+"]"+" -> ");		
			out.print("<a href='classlist.jsp?subject="+section.getSection().getSubject().getName()+"&section="+section.getSection().getSectionName()+"'>"+"["+section.getName()+"]</a>"+" -> ");		
		}
		out.println("<br>");	
	}
	out.println("Total Schedules:"+count);
%>

</body>
</html>