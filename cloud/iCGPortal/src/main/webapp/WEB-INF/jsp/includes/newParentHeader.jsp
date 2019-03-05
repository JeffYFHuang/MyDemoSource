<link rel="stylesheet" href="resources/css/parentMgt.css" />
<input type="hidden" id="token_id" value="${sessionID}">
<input type="hidden" id="new_parent" value="1">
<script type="text/javascript">
	function showKidsProfile(obj, token) {
		var student_id = sessionStorage.getItem("student_id");
		location.href = "newParentKids?token=" + token;

		return false;
	}
</script>
<!-- header open -->
<header class="main-header">
	<div class="row">
		<div class="col-md-12">
			<!-- <span class="header-container">Logo LITEON</span> -->
			<span class=" header-container"><a
				href="newParentProfile?token=${sessionID}"><img
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
			<li class="treeview account_mgmt" id="accountManagement"><a
				href="newParentProfile?token=${sessionID}"><img
					id="sAccountIcon"
					src="resources/images/sidemenu_icon/grey/account_mangmet_g.png"
					class="image_white" /> <span class="icglabel_accountmanagement"></span></a></li>
			<li class="treeview kids"><a href="#"
				onclick="showKidsProfile(this, '${sessionID}'); return false;"><img
					id="sKidsIcon"
					src="resources/images/sidemenu_icon/grey/kids_profile_g.png"
					class="image_white" /><span class="icglabel_kidsprofile"></span></a></li>
			<li class="treeview"><a href="logout?token=${sessionID}"><img
					src="resources/images/sidemenu_icon/grey/signout_g.png"
					class="image_white" /> <span class="icglabel_signout"></span></a></li>
		</ul>
	</section>
	<!-- /.sidebar -->
</aside>
