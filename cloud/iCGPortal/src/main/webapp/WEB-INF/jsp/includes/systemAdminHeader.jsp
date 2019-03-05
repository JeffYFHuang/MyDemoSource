<link rel="stylesheet" href="resources/css/parentMgt.css" />
<link rel="stylesheet" href="resources/css/schoolDashboard.css" />
<input type="hidden" id="token_id" value="${sessionID}">
<%
	String currentUser = (String) session.getAttribute("currentUser");
	currentUser = "";
	if (currentUser.equals("system_admin"))
		currentUser = "System Admin";
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
			
			<li class="treeview servicefunctionstatusView txt-14 font-medium-roboto"><a
				href="admindashboard?token=${sessionID}"><img
					id="servicefunctionstatusIcon"
					src="resources/images/sidemenu_icon/grey/service_funtion_status.png"
					class="image_white" /> <span
					class="icglable_servicefunctionstatus"></span></a></li>
			<li class="treeview externalSystemStatusView txt-14 font-medium-roboto"><a
				href="externalSystemStatus?token=${sessionID}"><img
					id="systemAdminProfileManagementIcon4"
					src="resources/images/sidemenu_icon/grey/external_system_status.png"
					class="image_white" /> <span
					class="icglable_externalsystemstatus"></span></a></li>
			<li class="treeview backupLogView txt-14 font-medium-roboto"><a
				href="backupLog?token=${sessionID}"><img
					id="systemAdminProfileManagementIcon3"
					src="resources/images/sidemenu_icon/grey/backup_log.png"
					class="image_white" /> <span
					class="icglable_backuplog"></span></a></li>			
			<li class="treeview activityLogView txt-14 font-medium-roboto"><a
				href="activityLog?token=${sessionID}"><img
					id="systemAdminProfileManagementIcon1"
					src="resources/images/sidemenu_icon/grey/activity_log.png"
					class="image_white" /> <span
					class="icglable_activitylog"></span></a></li>
			<li class="treeview notificationLogView txt-14 font-medium-roboto"><a
				href="notificationLog?token=${sessionID}"><img
					id="systemAdminProfileManagementIcon2"
					src="resources/images/sidemenu_icon/grey/notification_log.png"
					class="image_white" /> <span
					class="icglable_notificationlog"></span></a></li>
			<li class="treeview accountmanage txt-14 font-medium-roboto"><a
				href="adminProfileManagement?token=${sessionID}"><img
					id="sAccountManageIcon"
					src="resources/images/sidemenu_icon/grey/user_account_managment.png"
					class="image_white" /> <span
					class="icglable_accountmanagement"></span></a></li>
			<li class="treeview txt-14 font-medium-roboto"><a href="logout?token=${sessionID}"><img
					src="resources/images/sidemenu_icon/grey/signout_g.png"
					class="image_white" /> <span class="icglabel_signout"></span></a></li>
		</ul>
	</section>
</aside>
<!-- left side  collapse & expand bar closed -->
