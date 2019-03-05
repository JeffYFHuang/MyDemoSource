<link rel="stylesheet" href="resources/css/parentMgt.css" />
<link rel="stylesheet" href="resources/css/schoolDashboard.css" />
<input type="hidden" id="token_id" value="${sessionID}">
<%
	String currentUser = (String) session.getAttribute("currentUser");
	currentUser = "";
	if (currentUser.equals("school_admin"))
		currentUser = "School Admin";
%>

<!-- header open -->
<header class="main-header">
	<div class="row">
		<div class="col-md-12">
			<a href="#" class="sidebar-toggle visible-xs" data-toggle="offcanvas"
				role="button"> <span class="sr-only">Toggle navigation</span></a> <span
				class=""><a href="schooldashboard?token=${sessionID}"><img
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
					<br />${firstName}<br /> <span style="color: #525252"
						class="txt-16 font-bold-roboto">${user_name}</span>
				</p>
			</li>
			<li class="treeview dashboard txt-14 font-medium-roboto"><a
				href="schooldashboard?token=${sessionID}"><img
					id="sdashboardIcon"
					src="resources/images/SchoolAdmin_sideBarIcons/grey/dashboard.png"
					class="image_white" /><span class="icglabel_dashboard"></span> </a></li>
			<li class="treeview account_mgmt txt-14 font-medium-roboto"
				id="accountManagement"><a
				href="schoolAccountManagement?token=${sessionID}"><img
					id="sAccountIcon"
					src="resources/images/SchoolAdmin_sideBarIcons/grey/account_managment.png"
					class="image_white" /> <span class="icglabel_accountmanagement"></span></a></li>

			<li class="treeview device_mgmt txt-14 font-medium-roboto"
				id="deviceManagement"><a
				href="deviceManagement?token=${sessionID}"><img id="sDeviceIcon"
					src="resources/images/sidemenu_icon/grey/Device_managment.png"
					class="image_white" /> <span class="icglabel_devicemanagement"></span></a></li>

			<li class="treeview fitness txt-14 font-medium-roboto"><a
				href="schoolFitness?token=${sessionID}"><img id="sFitnessIcon"
					src="resources/images/SchoolAdmin_sideBarIcons/grey/fitness.png"
					class="image_white" /> <span class="icglabel_fitness"></span> </a></li>
			<li class="treeview rewards txt-14 font-medium-roboto"><a
				href="manageRewardsCategory?token=${sessionID}"><img
					id="sRewardsIcon"
					src="resources/images/SchoolAdmin_sideBarIcons/grey/reward.png"
					class="image_white" /><span class="icglabel_rewards"></span></a></li>
			<li class="treeview class_schedule txt-14 font-medium-roboto"><a
				href="listofclass?token=${sessionID}"> <img
					id="sClassScheduleIcon"
					src="resources/images/SchoolAdmin_sideBarIcons/grey/class_schedule.png"
					class="image_white" /> <span class="icglable_classschedule"></span>
			</a></li>
			<li class="treeview announcement txt-14 font-medium-roboto"><a
				href="editDeleteannouncement?token=${sessionID}"><img
					id="sAnnouncementIcon"
					src="resources/images/SchoolAdmin_sideBarIcons/grey/announcement.png"
					class="image_white" /><span class="icglabel_announcement"></span></a></li>
			<li class="treeview search_student txt-14 font-medium-roboto"><a
				href="schoolSearchStudent?token=${sessionID}"><img
					id="sSearchIcon"
					src="resources/images/SchoolAdmin_sideBarIcons/grey/search_student.png"
					class="image_white sSearchIcon" /><span
					class="icglable_searchstudent"></span></a></li>
			<li class="treeview account txt-14 font-medium-roboto"><a
				href="schoolAdminProfile?token=${sessionID}"><img
					id="sSchoolAdminIcon"
					src="resources/images/SchoolAdmin_sideBarIcons/grey/school_admin_account.png"
					class="image_white" /><span class="icglabel_schooladminaccount"></span></a></li>
			<li class="treeview txt-14 font-medium-roboto"><a
				href="logout?token=${sessionID}"><img
					src="resources/images/SchoolAdmin_sideBarIcons/grey/sign_out.png"
					class="image_white" /> <span class="icglabel_signout"></span></a></li>
		</ul>
	</section>
</aside>
<!-- left side  collapse & expand bar closed -->
