<link rel="stylesheet" href="resources/css/parentMgt.css" />
<link rel="stylesheet" href="resources/css/schoolDashboard.css" />
<input type="hidden" id="token_id" value="${sessionID}">
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
				<p class="txt-14 form-reg-roboto info">
					<br />${firstName}
					<br/>${user_name}</p>
			</li>
			<li class="treeview schoolmanage txt-14 font-medium-roboto"><a
				href="schoolslistview?token=${sessionID}"><img
					id="sSchoolManageIcon"
					src="resources/images/SuperAdminSideBarIcons/Grey_icon/school_managment.png"
					class="image_white" /><span class="icglabel_schoolmanagement"></span>
			</a></li>
			<li class="treeview devicemanage txt-14 font-medium-roboto"><a
				href="adminDeviceManagement?token=${sessionID}"><img
					id="sdeviceManageIcon"
					src="resources/images/SuperAdminSideBarIcons/Grey_icon/Device_maangment.png"
					class="image_white" /> <span class="icglable_devicemanagement"></span></a></li>
			<li class="treeview systemconfig txt-14 font-medium-roboto"><a
				href="adminSystemConfiguration?token=${sessionID}"><img
					id="sSystemConfigIcon"
					src="resources/images/SuperAdminSideBarIcons/Grey_icon/System_configuration.png"
					class="image_white" /> <span class="icglable_systemconfiguration"></span>
			</a></li>
			<li class="treeview usermanage txt-14 font-medium-roboto"><a
				href="adminUserAccountManagement?token=${sessionID}"><img
					id="sUserManageIcon"
					src="resources/images/SuperAdminSideBarIcons/Grey_icon/user_account_managment.png"
					class="image_white" /><span class="icglable_useraccountmanagement"></span></a></li>
			<li class="treeview parentmanage txt-14 font-medium-roboto"><a
				href="adminParentAccountManagement?token=${sessionID}"> <img
					id="sParentManageIcon"
					src="resources/images/SuperAdminSideBarIcons/Grey_icon/Parent_account_managment.png"
					class="image_white" /> <span
					class="icglable_parentaccountmanagement"></span>
			</a></li>
			<li class="treeview accountmanage txt-14 font-medium-roboto"><a
				href="adminProfileManagement?token=${sessionID}"><img
					id="sAccountManageIcon"
					src="resources/images/SuperAdminSideBarIcons/Grey_icon/account_managment.png"
					class="image_white" /><span class="icglable_accountmanagement"></span></a></li>
			<li class="treeview txt-14 font-medium-roboto"><a href="logout?token=${sessionID}"><img
					src="resources/images/SchoolAdmin_sideBarIcons/grey/sign_out.png"
					class="image_white" /> <span class="icglabel_signout"></span></a></li>
		</ul>
	</section>
</aside>
<!-- left side  collapse & expand bar closed -->
