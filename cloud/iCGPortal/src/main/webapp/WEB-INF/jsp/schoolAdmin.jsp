<%@include file="includes/taglib.jsp"%>
<%@ page import="com.liteon.icgwearable.util.PropertiesUtil"%>
<%@ page import="java.util.Properties"%>
<script>
	
<%Properties prop = PropertiesUtil.getProperties();%>
	
</script>
<!-- body content starts here -->
<div class="content-wrapper">
	<div id="dialog-map" style="display: none" title="Student Location Map">
		<div class="map" style="height: 370px;"></div>
	</div>
	<div>
		<section class="content-header">
			<h1 class="icglabel_dashboard"></h1>
			<ol class="breadcrumb-new" id="school-bread">
				<li><a class="icglabel_dashboard"
					href="schooldashboard?token=${sessionID}"></a> <i
					id="dashboard_arrow" class="fa fa-chevron-right" aria-hidden="true"></i></li>

			</ol>
		</section>
		<section>
			<div class="navbar navbar-default">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle"
						data-target="#school-navbar" data-toggle="collapse">
						<span class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
				</div>
				<div class="navbar-collapse collapse" id="school-navbar">
					<ul class="nav navbar-nav">
						<li><a class="tabs active icglabel_schoolinout" id="inOutTab"
							href="#"></a></li>
						<li><a class="tabs icglabel_sosalert" id="alertTab" href="#"></a></li>
						<li><a class="tabs icglabel_geofencing" id="fencingTab"
							href="#"></a></li>
					</ul>
				</div>
			</div>
		</section>
		<!-- body  content -->
		<section class="content">
			<!-- 	Student in out info ends here -->
			<section id="school-in-out">
				<section id="school-header">
					<section id="schooldashboard-Inout-filter">
						<div class="row txt-18 font-norm-roboto">
							<div
								class="col-md-3 col-sm-12 col-xs-12 col-lg-6 form-group todays-dateSchool"
								id="calSection">

								<div class='input-group date has-feedback has-feedback-left'
									data-date-format="YYYY-MM-DD">
									<input type='text' name="schoolCurrentDate"
										id='schoolCurrentDate' class="form-control-user dateInput"
										tabindex="1" readonly /><img
										src="resources/images/calender.png"
										class="form-control-feedback calender-img" alt="" /> <span
										class="input-group-btn">
										<button type="button"
											class="save calenderGo icglabel_gobutton" value="Go"
											id="findbasedOnDateforInOut" tabindex="2"></button>
									</span>
								</div>
							</div>
							<div
								class="col-md-offset-7 col-lg-offset-4 col-lg-2 col-md-2 col-sm-12 col-xs-12 form-group todays-dateSchool">
								<select class="grade" id="schoolDashboardInOutgrade"
									tabindex="3">
								</select>
							</div>
						</div>
					</section>
					<section id="schoolSection">
						<div class="row content-header-school">

							<div class="sectionStatistics schoolSectionStatistics blue-border">
								<span class="txt-24" id="schoolInoutTotal"></span>
								<p class="txt-18 icglable_totalstudents"></p>
							</div>
							<div class="sectionStatistics schoolSectionStatistics green-border">
								<span class="txt-24" id="schoolInTotal"></span>
								<p class="txt-18 icglable_totalin"></p>
							</div>
							<div class="sectionStatistics schoolSectionStatistics green-border">
								<span class="txt-24" id="schoolOutTotal"></span>
								<p class="txt-18 icglable_totalout"></p>
							</div>
							<div class="sectionStatistics schoolSectionStatistics red-border">
								<span class="txt-24" id="schoolAbsentTotal"></span>
								<p class="txt-18 icglable_absentstudent"></p>
							</div>
							<div class="sectionStatistics schoolSectionStatistics red-border">
								<span class="txt-24" id="schoolAbnormalTotal"></span>
								<p class="txt-18 icglable_abnormalstudent"></p>
							</div>
						</div>
					</section>
				</section>
				<section id="student-info">
					<section class="content-header-parent">
						<div class="row font-norm-roboto">
							<div class="col-md-5" style="padding-right: 2px;">
								<div class="col-md-12" style="padding-left: 21px;">
									<p class="bread_heading_parent sub-title icglabel_absentstudentlist"
										style="padding-bottom: 15px;"></p>
								</div>
								<div class="col-md-12 col-remove-padding-left">
									<div class="studentlists table-responsive">
										<table class="table table-hover">
											<thead>
												<tr>
													<th class="icglabel_studentname"></th>
													<th class="icglabel_class"></th>
												</tr>
											</thead>
											<tbody id="school_absent">
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<div class="col-md-7">
								<div class="col-md-12">
									<p class="bread_heading_parent sub-title icglabel_abnormalstudentlist"
										style="padding-bottom: 15px;"></p>
								</div>
								<div class="col-md-12 col-remove-padd pad-rt">
									<div class="studentlists table-responsive">
										<table class="table table-hover">
											<thead>
												<tr>
													<th class="icglabel_studentname"></th>
													<th class="icglabel_class"></th>
													<th class="icglabel_reason"></th>
													<th class="icglabel_timestamp"></th>
												</tr>
											</thead>
											<tbody id="school_abnormals">
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</section>
				</section>
				<section id="student-filter" style="display: none">
					<section class="content-header-parent" id="class_list">
					</section>
				</section>
			</section>
			<!-- 	Student in out info ends here -->

			<!-- 	SOS Alert starts here -->
			<section id="sosAlert" style="display: none">
				<section>
					<div class="row txt-18 font-norm-roboto">
						<div class="col-md-3 col-sm-4 col-xs-6 todays-dateSchool"
							id="sos-calSection">
							<div
								class='input-group date form-group has-feedback has-feedback-left'
								data-date-format="YYYY-MM-DD">
								<input type='text' name="schoolsosCurrentDate"
									id='schoolsosCurrentDate'
									class="form-control-user dateInput" tabindex="1"
									readonly /><img src="resources/images/calender.png"
									class="form-control-feedback calender-img" alt="" /> <span
									class="input-group-btn">
									<button type="button" class="save calenderGo icglabel_gobutton"
										value="Go" id="findbasedOnDateforSos" tabindex="2"></button>
								</span>
							</div>
						</div>
					</div>
				</section>
				<section>
					<div class="row content-header-school">
						<div class="sectionStatistics blue-border sectionsosStatistics">
								<span class="txt-24" style="color: blue" id="sos_totalAlert_count"></span>
								<p class="txt-18 icglable_totalsosalert"></p>
						</div>
						<div class="sectionStatistics red-border sectionsosStatistics">
								<span class="txt-24 red-text" id="sos_onAlert_count"></span>
								<p class="txt-18 icglable_onalert"></p>
						</div>
						<div class="sectionStatistics green-border sectionsosStatistics">
								<span class="txt-24" style="color: green" id="sos_closed_count"></span>
								<p class="txt-18 icglable_closed"></p>
						</div>
					</div>
				</section>
				<section class="content-header-parent">
					<div class="row font-norm-roboto">
						<div class="">
							<p class="bread_heading_parent sub_title icglabel_sosalertlists"
								style="padding-left: 35px;"></p>
						</div>
					</div>
				</section>
				<section>
					<div class="row mobile-sosAlert">
						<div class="sosAlertLists"></div>
					</div>
				</section>
			</section>
			<!-- 	SOS Alert ends here -->

			<!-- Geofencing starts here -->
			<section id="geofencing" style="display: none">
				<section>
					<div class="row txt-18 font-norm-roboto">
						<div class="col-md-3 col-sm-4 col-xs-6 todays-dateSchool"
							id="geo-calSection">
							<div
								class='input-group date form-group has-feedback has-feedback-left'
								data-date-format="YYYY-MM-DD">
								<input type='text' name="schoolgeoCurrentDate"
									id='schoolgeoCurrentDate'
									class="form-control-user dateInput" tabindex="1"
									readonly /><img src="resources/images/calender.png"
									class="form-control-feedback calender-img" alt="" /> <span
									class="input-group-btn">
									<button type="button" class="save calenderGo icglabel_gobutton"
										value="Go" id="findbasedOnDateforGeo" tabindex="2"></button>
								</span>
							</div>
						</div>
					</div>
				</section>
				<section id="school_geofencingList"></section>
			</section>
			<!-- Geofencing ends here -->
		</section>
	</div>
</div>

<!-- Modal for SOS Map -->
<div class="modal" id="sosMap" role="dialog">
	<div class="modal-dialog  modal-lg">

		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" id="close_mapView"
					style="font-size: 30px;">&#10005;</button>
				<h4 class="modal-title">
					<span class="h2 icglabel_sosmapview"></span>
				</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-lg-12 img-height" id="mapView"></div>
				</div>
			</div>

		</div>

	</div>
</div>
<script src="resources/js/json-sort.js"></script>
<script src="resources/js/schoolDashboard.js"></script>
<script src='<%=prop.getProperty("Map_Url")%>'></script>
<script type="text/javascript">
	$('.dashboard').removeClass("treeview").addClass("active");
	$('.dashboard').removeClass("font-medium-roboto").addClass("font-bold-roboto");
	$("#sdashboardIcon").attr("src",
			"resources/images/SchoolAdmin_sideBarIcons/White/dashboard.png");
</script>
<!-- end of Modal for SOS Map -->