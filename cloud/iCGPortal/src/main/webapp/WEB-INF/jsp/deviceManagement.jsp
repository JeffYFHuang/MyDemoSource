<%@include file="includes/taglib.jsp"%>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/fmt"%>
<input type="hidden" id="page_id"
	value="<%=request.getParameter("pageid")%>">
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_devicemanagement"></h1>
			<ol class="breadcrumb-new" id="bread">
				<li><a href="schooldashboard?token=${sessionID}"
					id="school_dashboard" class="hand"><span
						class="icglabel_dashboard"></span></a><i class="fa fa-chevron-right"
					aria-hidden="true"></i></li>
				<li><a href="deviceManagement?token=${sessionID}"
					id="school_acctmgt" class="hand"><span
						class="icglabel_devicemanagement"></span></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
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
						<li><a class="active tabs hand icglabel_assigneddevices"
							id="assignedDevice"> </a></li>
						<li><a class="tabs hand icglabel_unassigneddevices"
							id="unassignedDevice"></a></li>
					</ul>
				</div>
			</div>
		</section>
		<!-- body  content -->
		<section class="content">
			<section id="assigned_device_section">
				<section class="user_account_mangmet">
					<form id="scheduleForm" method="POST" action="/schdeuleSearch/"
						novalidate="novalidate">
						<div class="row">
							<div class="col-md-3">
								<div class="form-group">
									<label for="scheduleGrade" class="control-label icglabel_grade"></label>
									<select class="form-control-user select-grade" id="scheduleGrade"
										name="scheduleGrade" onChange="setStudentClass()" required
										title="Please select Grade" tabindex="1">
									</select> <span class="error-block" id="scheduleGrade_err"></span>
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-groups">
									<label for="scheduleClassroom"
										class="control-label icglabel_class"></label> <select
										class="form-control-user select-grade" id="scheduleClassroom"
										name="scheduleClassroom" required
										title="Please select Classroom" tabindex="2">
									</select> <span class="error-block" id="scheduleClassroom_err"></span>
								</div>
							</div>
							<div class="col-md-2">
								<button type="button"
									class="save form-group btn-classSearch btn-actMgt icglabel_find"
									value="Find" id="findDevice" tabindex="3"></button>
							</div>
						</div>
					</form>
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th class="icglabel_uuid"></th>
									<th class="icglabel_studentname"></th>
									<th class="icglabel_grade"></th>
									<th class="icglabel_class"></th>
									<th class="icglabel_startdate"></th>
								</tr>
							</thead>
							<tbody id="assignedSection">
							</tbody>
						</table>
					</div>
					<span id="pagination" class="pagination" style="display: none">
						<span class="col-md-8"> <span class="icglabel_page"></span>&nbsp;<span
							id="assigned_startPage" style="text-align: left;"></span> <span
							class="icglabel_of" style="text-align: left;"></span> <span
							id="assigned_endPage" style="text-align: left;"></span>
					</span> <span class="col-md-4"><span class="pull-right"><span
								class="disabled pull-left"> <a id="decrease"
									onClick="decreaseassigendHref()" href="#"><img
										src="resources/images/grey_drop_down.png"
										class="left-navigate img-responsive pull-left"></a></span> <span
								class="disabled pull-right"><a id="increase"
									onClick="increaseassigendHref()" href="#"><img
										src="resources/images/grey_drop_down.png"
										class="right-navigate img-responsive"></a></span></span> </span>
					</span>
				</section>
			</section>
			<!--end of teacher-->
			<!--Start of Student-->
			<section id="unassigned_device_section" style="display: none">
				<section class="content-header-parent">
					<div class="row sub-title">
						<div
							class="col-lg-4 col-md-4 col-sm-6 col-xs-12 col-md-offset-3 filter-device-tabs"
							style="">
							<p class="bread_heading_parent">
								<input type="text" class="form-control-user input-bottom"
									id="adminDeviceUUIDforFind" name="adminDeviceUUIDFilter"
									title="Please enter UUID" tabindex="9" />
							</p>
						</div>
						<div class="col-lg-1 col-md-1 col-sm-6 col-xs-6" style="">
							<p class="bread_heading_parent">
								<input type="button"
									class="save form-group text-center btn-actMgt" value="Find"
									id="adminDevicefindTeacher" tabindex="14"
									onclick="getDeviceDetails()" />
							</p>
						</div>
					</div>
				</section>
				<form:form name="updateForm" method="POST" align="center">
					<section id="unassigned_device_section">
						<section class="user_account_mangmet">
							<div class="table-responsive">
								<table class="table">
									<thead>
										<tr>
											<th width="50%" class="icglabel_uuid"></th>
											<th width="50%" class="icglable_currentstatus"></th>
										</tr>
									</thead>
									<tbody id="unAssignedSection" style="text-align: left;">
									</tbody>
								</table>
							</div>
							<span id="pagination_deviceunassign" class="pagination"
								style="display: none"> <span class="col-md-8"><span class="pull-left"><span
									class="icglabel_page"></span>&nbsp;<span
									id="unassigned_startPage" style="text-align: left;"></span><span
									class="icglabel_of" style="text-align: left;"></span> <span
									id="unassigned_endPage" style="text-align: left;"></span></span></span> <span
								class="col-md-4"><span
									class="pull-right"><span class="disabled pull-left">
											<a id="undecrease" onClick="decreaseunassigendHref()"
											href="#"><img src="resources/images/grey_drop_down.png"
												class="left-navigate img-responsive pull-left"></a>
									</span> <span class="disabled pull-right"><a id="unincrease"
											onClick="increaseunassigendHref()" href="#"><img
												src="resources/images/grey_drop_down.png"
												class="right-navigate img-responsive"></a></span></span> </span></span>
						</section>
					</section>

					<section class="submit-button-section">
						<div class="row">
							<div style="padding-top: 20px;">
								<p class="bread_heading_parent text-center">
									<button type="submit" class="save form-group icglabel_save"
										value="Save" id="submit"
										onClick='this.form.action="updateUnassignedDeviceStatus?token=${sessionID}";'
										tabindex="14"></button>
									&nbsp;
									<button type="submit" class="save form-group icglabel_download"
										value="Download" id="download"
										onClick='this.form.action="downloadCsv?token=${sessionID}"; hideSpinner();'
										tabindex="14"></button>
								</p>
							</div>
						</div>
					</section>
				</form:form>
			</section>
		</section>
	</div>
</div>
<script src="resources/js/deviceManagement.js"></script>
<script src="resources/js/lib/moment.js"></script>
<script src="resources/js/date-formatter.js"></script>
