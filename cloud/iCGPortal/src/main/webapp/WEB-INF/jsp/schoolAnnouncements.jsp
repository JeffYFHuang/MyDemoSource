<%@include file="includes/taglib.jsp" %>
<%@ page import="com.liteon.icgwearable.hibernate.entity.Accounts"%>
<!-- 	body content starts here -->
		<div class="content-wrapper">
			<div>
				<section class="content-header">
					<h1 class="icglabel_announcements"></h1>
					<ol class="breadcrumb-new">
						<li><a href="parentdashboard?token=${sessionID}" class="icglabel_dashboard"></a><i class="fa fa-chevron-right"
							aria-hidden="true"></i></li>
						<li><a href="#" class="kidName" id="sname"></a><i
							class="fa fa-chevron-right" aria-hidden="true"></i></li>
						<li class="active"><a href="#" class="icglabel_announcements"></a></li>
					</ol>
				</section>
				<!-- body  content -->
				<section class="content" id="announcements"></section>
			</div>
		</div>
		<!-- 	body content ends here -->
<script src="resources/js/schoolAnnouncements.js"></script>