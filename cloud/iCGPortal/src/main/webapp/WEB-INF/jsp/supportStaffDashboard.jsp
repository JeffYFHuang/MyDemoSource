<%@include file="includes/taglib.jsp"%>
<section id="main" class="content-wrapper">
	<div align="center">
		<br />
		<h1>
			<font color="green"><span class="txt-28 font-reg-roboto icglabel_greeting"></span> ${firstName}</font>
		</h1>
		<!-- main page content goes in here -->
		<!-- You have successfully logged in. -->


		<h1>
			<font color="green" class="icglabel_welcome"></font>
		</h1>

		<%-- <a href="myProfile?${requestScope.userDetails.id}"/>Logout</a> --%>

		<!-- <a href="logout"/>Logout</a>  -->
	</div>
</section>
<script type="text/javascript">
	$('.dashboard').removeClass("treeview").addClass("active");
	$("#supportStaffDashboardIcon").attr("src","resources/images/SchoolAdmin_sideBarIcons/White/dashboard.png");
</script>