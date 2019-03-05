<link rel="stylesheet" href="resources/css/parentMgt.css" />
<link rel="stylesheet" href="resources/css/schoolDashboard.css" />
<input type="hidden" id="token_id" value="${sessionID}">
<script type="text/javascript">
	function showKidsProfile(obj, token) {
		var student_id = sessionStorage.getItem("student_id");
		location.href = "editKids?token=" + token;

		return false;
	}
</script>
<%
	String user_name = (String) session.getAttribute("user_name");
%>
<!-- header open -->
<header class="main-header">
	<div class="row">
		<div class="col-md-12">
			<a href="#" class="sidebar-toggle visible-xs" data-toggle="offcanvas"
				role="button"> <span class="sr-only">Toggle navigation</span></a> <span
				class=""><a href="parentdashboard?token=${sessionID}"><img
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
			<li class="activemail"><p class="txt-14 form-reg-roboto info">${firstName}<br />${user_name}</p></li>
			<li class="treeview dashboard txt-14 font-medium-roboto"><a
				href="parentdashboard?token=${sessionID}"><img
					id="sDashboardIcon"
					src="resources/images/sidemenu_icon/grey/dashboard.png"
					class="image_white" /><span class="icglabel_dashboard"></span> </a></li>
			<li class="treeview safety txt-14 font-medium-roboto"><a
				href="kidsSafety?token=${sessionID}"><img id="sSafetyIcon"
					src="resources/images/sidemenu_icon/grey/safety_g.png"
					class="image_white" /> <span class="icglabel_safety"></span> </a></li>
			<li class="treeview account_mgmt txt-14 font-medium-roboto" id="accountManagement"><a
				href="parentAdminProfile?token=${sessionID}"><img
					id="sAccountIcon"
					src="resources/images/sidemenu_icon/grey/account_mangmet_g.png"
					class="image_white" /> <span class="icglabel_accountmanagement"></span></a></li>
			<li class="treeview guardian txt-14 font-medium-roboto"><a
				href="guardianSubscription?token=${sessionID}"> <img
					id="sGuardianIcon"
					src="resources/images/sidemenu_icon/grey/guardiaan_g.png"
					class="image_white" /> <span class="icglabel_guardiansubscription"></span>
			</a></li>
			<li class="treeview kids txt-14 font-medium-roboto"><a href="#"
				onclick="showKidsProfile(this, '${sessionID}'); return false;"><img
					id="sKidsIcon"
					src="resources/images/sidemenu_icon/grey/kids_profile_g.png"
					class="image_white" /><span class="icglabel_kidsprofile"></span></a></li>
			<li class="treeview reminders txt-14 font-medium-roboto"><a
				href="reminderslist?token=${sessionID}"><img id="sRemindersIcon"
					src="resources/images/sidemenu_icon/grey/reminders_g.png"
					class="image_white" /><span class="icglabel_reminders"></span></a></li>
			<li class="treeview rewards txt-14 font-medium-roboto"><a
				href="rewardStatistics?token=${sessionID}"><img
					id="sRewardsIcon"
					src="resources/images/sidemenu_icon/grey/Rewards_grey.png"
					class="image_white" /><span class="icglabel_rewards"></span></a></li>
			<li class="treeview announcement txt-14 font-medium-roboto"><a
				href="schoolAnnouncements?token=${sessionID}"><img
					id="sAnnouncementIcon"
					src="resources/images/sidemenu_icon/grey/announcement_grey.png"
					class="image_white" /><span class="icglabel_announcements"></span></a></li>
			<li class="treeview activity txt-14 font-medium-roboto"><a
				href="kidsActivityReports?token=${sessionID}"><img
					id="sActivityIcon"
					src="resources/images/sidemenu_icon/grey/activities_g.png"
					class="image_white" /> <span class="icglabel_activityreports"></span>
			</a></li>
			<li class="treeview txt-14 font-medium-roboto"><a href="logout?token=${sessionID}"><img
					src="resources/images/sidemenu_icon/grey/signout_g.png"
					class="image_white" /> <span class="icglabel_signout"></span></a></li>
		</ul>
	</section>
	<!-- /.sidebar -->
</aside>
