<%@include file="includes/taglib.jsp"%>
<%@ page import="com.liteon.icgwearable.util.PropertiesUtil"%>
<%@ page import="java.util.Properties"%>
<%
	Properties prop = PropertiesUtil.getProperties();
%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_safety"></h1>
			<ol class="breadcrumb-new">
				<li><a href="parentdashboard?token=${sessionID}"
					class="icglabel_dashboard"></a><i class="fa fa-chevron-right"
					aria-hidden="true"></i></li>
				<li><a href="#" class="kidName" id="sname"></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li class="active"><a href="#" class="icglabel_safety"></a></li>
			</ol>
		</section>
		<section class="datefilter">
			<div class="row txt-18 font-norm-roboto">
				<div class="col-md-3 col-sm-4 col-xs-6 todays-dateSchool"
					id="safetyCalSection">
					<div
						class='input-group date safety-date form-group has-feedback has-feedback-left'
						 data-date-format="YYYY-MM-DD">
						<input type='text' class="form-control-user dateInput"
							tabindex="1" id='safetyCurrentDate' name="safetyCurrentDate" readonly/><img
							src="resources/images/calender.png"
							class="form-control-feedback calender-img" alt="" /> <span
							class="input-group-addon">
							<button type="button"
								class="safety-data save calenderGo safety-cal icglabel_gobutton"
								tabindex="1"></button>
						</span>
					</div>
				</div>
			</div>
		</section>
		<!-- body  content -->
		<section class="content">
			<section class="bread_heading_parentuser_account_mangmet">
				<p class="sub_title icglabel_notifications"></p>
			</section>
			<section class="">
				<div class="row">
					<div class="col-lg-12">
						<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class="safetyCol notification-dashboard">
								<img src="resources/images/ParentDashboard/school_in_out.png"
									class="img-responsive pull-left" />
								<p class="notification-header icglabel_schooltime"></p>
								<p class="notification-text">
									<span class="icglabel_enter"></span>&nbsp;<span
										class="enter_time"></span><br /> <span class="icglabel_exit"></span>&nbsp;<span
										class="exit_time"></span>
								</p>
							</div>
						</div>
						<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class="safetyCol notification-dashboard">
								<img src="resources/images/ParentDashboard/geofencing.png"
									class="img-responsive pull-left" />
								<p class="notification-header icglabel_geofencinginout"></p>
								<p class="notification-text">
									<span class="icglabel_enter"></span>&nbsp;<span
										class="geo_entertime"></span><br /> <span
										class="icglabel_exit"></span>&nbsp;<span class="geo_exittime"></span>
								</p>
							</div>

						</div>
						<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class="safetyCol notification-dashboard">
								<img src="resources/images/ParentDashboard/Alerts.png"
									class="img-responsive pull-left" />
								<p class="notification-header icglabel_alerts"></p>
								<p class="notification-text">
									<span class="alert_data"></span><br /> <span>&nbsp;</span>
								</p>
							</div>

						</div>
					</div>
				</div>
			</section>
			<section class="content-header-parent">
				<div class="row">
					<div class="col-md-12">
						<p class="bread_heading_parent sub_title icglabel_mapview"></p>
					</div>
				</div>
			</section>
			<section class="content-header-parent">
				<div id="map-canvas"></div>
			</section>
			<section class="content-header-parent">
				<div class="row">
					<div class="col-md-12">
						<span class="bread_heading_parent sub_title icglabel_markerslegend"></span>
					</div>
				</div>
			</section>
			<section class="content-header-parent">
				<div class="row">
					<div class="col-lg-12 third_subtitle">
						<div>
							<span class="font-light-roboto icglabel_alerts"></span>&nbsp;-&nbsp;<img
								src="http://maps.gstatic.com/intl/en_ALL/mapfiles/marker_orange.png">
							<span class="font-light-roboto icglabel_gpslocation"></span>&nbsp;-&nbsp;<img
								src="http://maps.gstatic.com/intl/en_ALL/mapfiles/marker_green.png">
						</div>
						<!-- /.info-box-content -->
					</div>
				</div>
			</section>
		</section>
	</div>
</div>
<!-- 	body content ends here -->
<!-- page script -->
<script src="resources/js/lib/moment.js"></script>
<script src="resources/js/date-formatter.js"></script>
<script src="resources/js/safety.js"></script>
<script src='<%=prop.getProperty("Map_Url")%>'></script>
<style>
#map-canvas {
	height: 500px;
	margin: 0px;
	padding: 0px
}

#results {
	text-align: center;
}

.error {
	font-color: red;
}
</style>