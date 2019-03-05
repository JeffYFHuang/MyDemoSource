<link rel="stylesheet" href="resources/css/parentMgt.css" />
<link rel="stylesheet" href="resources/css/schoolDashboard.css" />
<input type="hidden" id="token_id" value="${sessionID}">
<%
	String currentUser = (String) session.getAttribute("currentUser");
	currentUser = "";
	if (currentUser.equals("support_staff"))
		currentUser = "Support Staff";
	String user_name = (String) session.getAttribute("user_name");
%>
<!-- header open -->
<header class="main-header">
	<div class="row">
		<div class="col-md-12">
			<a href="#" class="sidebar-toggle visible-xs" data-toggle="offcanvas"
				role="button"> <span class="sr-only">Toggle navigation</span></a>
			<span class=""><a
				href="admindashboard?token=${sessionID}"><img
					src="resources/images/logo.png" alt="logo" class="img-responsive" /></a>
			</span>
		</div>
	</div>
</header>
<!-- header closed -->
<!-- left side collapse &expand bar open -->
<aside class="main-sidebar">
	<section class="sidebar" style="height: auto;">
		<ul class="sidebar-menu">
			<li class="activemail">
				<p class="txt-14 form-reg-roboto info"><%=currentUser%>
					<br />${firstName}
					<br/><%=user_name%></p>
			</li>
			<li class="treeview supportstaff_search_school txt-14 font-medium-roboto"><a
				href="supportStaffSchoolSearch?token=${sessionID}"><img
					id="supportStaffSchoolSearchIcon"
					src="resources/images/sidemenu_icon/grey/search_school.png"
					class="image_white sSearchIcon" /> <span class="icglabel_schoolsearch"></span>
			</a></li>
			<li class="treeview supportstaff_parent_search txt-14 font-medium-roboto"><a
				href="supportStaffParentSearch?token=${sessionID}"><img
					id="supportStaffParentSearchIcon"
					src="resources/images/sidemenu_icon/grey/search_parent.png"
					class="image_white" /> <span class="icglable_searchparent"></span>
			</a></li>
			<%-- <li class="treeview search_student"><a
				href="schoolSearchStudent?token=${sessionID}"><img
					id="supportStaffSearchStudentIcon"
					src="resources/images/SchoolAdmin_sideBarIcons/grey/search_student.png"
					class="image_white" /> <span class="icglable_searchstudent"></span>
			</a></li> --%>
			<li class="treeview search_device txt-14 font-medium-roboto"><a
				href="supportStaffDeviceSearch?token=${sessionID}"><img
					id="supportStaffSearchDeviceIcon"
					src="resources/images/SuperAdminSideBarIcons/Grey_icon/Device_maangment.png"
					class="image_white" /> <span class="icglable_searchdevice"></span>
			</a></li>
			<li class="treeview accountmanage txt-14 font-medium-roboto"><a
				href="adminProfileManagement?token=${sessionID}"><img
					id="sAccountManageIcon"
					src="resources/images/SuperAdminSideBarIcons/Grey_icon/account_managment.png"
					class="image_white" /> <span class="icglable_accountmanagement"></span></a></li>
			<li class="treeview txt-14 font-medium-roboto"><a href="logout?token=${sessionID}"><img
					src="resources/images/SchoolAdmin_sideBarIcons/grey/sign_out.png"
					class="image_white" /> <span class="icglabel_signout"></span></a></li>
		</ul>
	</section>
</aside>
<!-- left side  collapse & expand bar closed -->
