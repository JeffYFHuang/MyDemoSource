<%@include file="includes/taglib.jsp"%>
<script src="resources/js/classrommSchedule.js"></script>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglable_classschedule"></h1>
			<ol class="breadcrumb-new" id="bread">
				<li><a href="schooldashboard?token=${sessionID}"
					id="school_dashboard"><span class="icglabel_dashboard"></span></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i>
				<li class="icglable_classschedule"></li>
			</ol>
		</section>
		<!-- body  content -->
		<section class="content">
			<!-- 	<section class="user_account_man gmet"></section> -->
			<!--Start of Student-->
			<section id="student_section">
				<div class="row">
					<div class="col-md-8">
						<section class="user_account_mangmet">
							<form id="scheduleForm" method="POST" action="/schdeuleSearch/"
								novalidate="novalidate">
								<div class="row">
									<div class="col-lg-5 col-md-6 col-sm-12 col-xs-12">
										<div class="form-group">
											<label for="scheduleGrade"
												class="control-label icglabel_grade"></label> <select
												class="form-control-user select-grade" id="scheduleGrade"
												name="scheduleGrade" onChange="setStudentClass()" required
												title="Please select Grade" tabindex="1">
											</select> <span class="error-block" id="scheduleGrade_err"></span>
										</div>
									</div>
									<div class="col-lg-5 col-md-6 col-sm-12 col-xs-12">
										<div class="form-groups">
											<label for="scheduleClassroom"
												class="control-label icglabel_class"></label> <select
												class="form-control-user select-grade"
												id="scheduleClassroom" name="scheduleClassroom" required
												title="Please select Classroom" tabindex="2">
											</select> <span class="error-block" id="scheduleClassroom_err"></span>
										</div>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 col-xs-12">
										<button type="button"
											class="save form-group btn-classSearch btn-actMgt icglabel_find"
											value="Find" id="scheduleGo" tabindex="3"></button>
									</div>
								</div>
							</form>
							<p class="sub_title icglable_classschedule"></p>
							<div class="table-responsive">
								<table class="table table-bordered table-hover">
									<thead>
										<tr>
											<th class="icglable_periods"></th>
											<th class="icglable_monday"></th>
											<th class="icglable_tuesday"></th>
											<th class="icglable_wednesday"></th>
											<th class="icglable_thursday"></th>
											<th class="icglable_friday"></th>
										</tr>
									</thead>
									<tbody id="dataSection">
										<tr>
											<td colspan="6"><strong class="icglabel_nodata"></strong></td>
										</tr>
									</tbody>
								</table>
							</div>
						</section>
					</div>
					<div class="col-md-4">
						<section class="">
							<section class="small_section">
								<p
									class="section-header third_subtitle icglabel_downloadtemplates"></p>
								<hr style="border-top: 1px solid black;" />
								<div class="user_account_mangmet font-light-roboto student-body">
									<p class="text-center">
										<a href="resources/templates/timetable.csv" tabindex="9"
											class="font-medium-roboto txt-14 icglable_downloadhere"></a>
										<span class="icglable_toclassscheduletemplate"></span>
									</p>
								</div>
							</section>
							<section class="small_section">
								<p class="section-header third_subtitle icglabel_upload"></p>
								<hr style="border-top: 1px solid black;" />
								<form method="POST" action="timeTablesUploadFile"
									enctype="multipart/form-data">
									<input type="hidden" name="token" value="${sessionID}">
									<div class="text-center font-light-roboto">
										<div class="text-center">
											<strong><font color="green" class="txt-12"> <span id="csvupload"
												class="icglabel_csvuploadmsg text-center"
												style="display: none;" data-success="${uploadsuccess}"></span>
											</font></strong>
										</div>
										<strong><font color="green" id="timetableRecordCount"
											class="text-center txt-12">${timetableRecordCount}</font></strong>
										<c:if test="${fn:length(ignoredList) > 1 }">
											<div class="text-center">
												<a href="#" data-toggle="modal" id="timetableignoredList"
													data-target="#ignoredDetailsModal"
													class="icglabel_errorDetails"
													onclick="mergeCategoryInDetails($(this))"
													data-keyboard="true" error_details='${ignoredList}'></a>
											</div>
										</c:if>
										<strong><font color="red" class="txt-12 text-center"> <span
												id="invalidcsverror"
												class="text-center icglabel_invalidcsverrormsg"
												style="display: none;" data-error="${csvInvalidMsg}"></span>
										</font></strong>
										<strong><font color="red" class="text-center txt-12"> <span
												id="timetable_csverror"
												class="icglabel_csverrormsg" style="display: none;"></span>
											</font></strong>

										<div class="user_account_mangmet"
											style="padding: 20px 20px 0px 19px;">
											<p class="text-center">
												<input id="csvFileUpload" type="file" name="file"
													accept=".csv" tabindex="10" class="inputfile"
													onchange="showFileName(this)"> <label
													for="csvFileUpload"><span
													class="font-medium-roboto txt-14 icglable_browsehere"></span><label
													class="icglable_uploadclassschedulecsv"></label></label>
											</p>
										</div>
										<div class="text-center">
											<button type="submit"
												class="save form-group text-center icglabel_upload"
												style="padding: 9px 15px;" tabindex="11"
												id="timetableUpload" disabled></button>
										</div>
									</div>
								</form>
							</section>
						</section>
					</div>
				</div>
				<!-- <section class="user_account_mangmet">
						</section> -->
			</section>
		</section>
	</div>
</div>

<div class="modal fade" id="ignoredDetailsModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">
					<span class="heading icglabel_errorDetails"></span>
				</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<section class="user_account_mangmet">
						<textarea class="form-control" id="editcategoryIn_id" rows="10"
							name="schoolAnnouncementDescription" tabindex="2" readonly> </textarea>
					</section>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="modal-Cancel icglabel_close"
					data-dismiss="modal"></button>
			</div>
		</div>
	</div>
</div>
