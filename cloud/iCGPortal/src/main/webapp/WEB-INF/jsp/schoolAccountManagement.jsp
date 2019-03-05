<%@include file="includes/taglib.jsp"%>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_accountmanagement"></h1>
			<ol class="breadcrumb-new" id="bread">
				<li><a href="schooldashboard?token=${sessionID}"
					id="school_dashboard"><span class="icglabel_dashboard"></span></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li><a href="#"><span class="icglabel_accountmanagement">
					</span></a><i class="fa fa-chevron-right" aria-hidden="true"></i></li>
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
					<ul class="nav navbar-nav" id="myTab">
						<li><a class="active tabs icglabel_teacherstaff"
							id="teacherAccount" href="#teacherSection"> </a></li>
						<li><a class="tabs icglabel_student" id="studentAccount"
							href="#studentSection"> </a></li>
					</ul>
				</div>

			</div>
		</section>
		<!-- body  content -->
		<section class="content font-reg-roboto">
			<section class="content-header-parent">
				<p class="bread_heading_parent sub-title icglabel_createaccount"></p>
			</section>
			<section id="teacher_section">
				<div class="row">
					<div class="col-md-8">
						<section class="user_account_mangmet teacher-section">
							<form id="accCreateForm" method="POST"
								action="saveTeacherOrStaff?token=${sessionID}"
								novalidate="novalidate">
								<div class="row">
									<div class="col-md-12">
										<div class="act-error-msgs txt-12 text-center">
											<strong><font color="green"> <span
												id="teacher_account_success_msg"
												class="icglabel_create_account_success_msg"
												style="display: none;"
												data-success="${create_account_success}"></span>
											</font> <font color="red"> <span id="teacher_account_exists"
												class="icglabel_account_exists" style="display: none;"
												data-error="${account_exists}"></span>
											</font> <font color="red"> <span id="teacher_account_fail"
												class="icglabel_account_failed" style="display: none;"
												data-error="${account_failed}"></span>
											</font></strong>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-groups">
											<label for="accMgtRole" class="control-label icglabel_role"></label>
											<select tabindex="1" class="form-control-user select-grade"
												id="accTeacherStaffRole" onchange="roleChangeForTS($(this))"
												required name="acntTeacherStaffRole"
												title="Please select the role">
												<option class="icglabel_selectrole" value=""></option>
												<option class="icglabel_teacher" value="school_teacher"></option>
												<option class="icglabel_staff" value="school_staff"></option>
											</select> <span class="error-block icglabel_accMgtRole_err"
												id="accMgtRole_err"></span>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="accMgtTeacherGrade"
												class="control-label icglabel_grade"></label> <select
												name="accMgtTeacherGrade"
												class="form-control-user select-grade"
												id="accMgtTeacherGrade"
												onChange="setStudentUnassignedClass()" required
												title="Please select the grade" tabindex="4"></select> <span
												class="error-block icglabel_accMgtGrade_err"
												id="accMgtGrade_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-groups">
											<label for="accMgtName" class="control-label icglabel_name">
											</label> <input type="text" class="form-control-user" id="accMgtName"
												name="accTeacherStaffName" required
												title="Please enter the teacher/staff name" tabindex="2"
												maxlength="45" /><span
												class="error-block icglabel_accMgtName_err"
												id="accMgtName_err"></span>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-groups">
											<label for="accMgtClass" class="control-label icglabel_class">
											</label> <select name="accMgtTeacherClass" id="accMgtTeacherClass"
												class="form-control-user select-grade" required
												title="Please select the class" tabindex="5">
												<option value='' class="icglabel_accMgtClass"></option>
											</select> <span class="error-block icglabel_accMgtClass_err"
												id="accMgtClass_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 ">
										<div class="form-group">
											<label for="accMgtEmail"
												class="control-label icglabel_usernameemailid"> </label> <input
												type="email" class="form-control-user" id="accMgtEmail"
												name="accTeacherStaffEmail" required
												title="Please enter the username/emailId" tabindex="3"
												onkeyup="return forceLower(this);" /><span
												class="error-block icglabel_accMgtEmail_err"
												id="accMgtEmail_err"></span>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="accMgtContact"
												class="control-label icglabel_contactno"> </label> <input
												type="number" maxlength="10"
												class="form-control-user contactnumber" id="accMgtContact"
												name="accTeacherStaffContact"
												title="Please enter the contact number" required
												tabindex="6" /> <span
												class="error-block icglabel_accMgtContact_err"
												id="accMgtContact_err"></span><span
												class="error-block icglabel_contact10digits"
												id="accMgtContactStartOfContactInvalid"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-5 col-lg-4 col-sm-4 col-xs-12">
										<button type="submit" class="save form-group icglabel_create"
											id="accMgtCreate" tabindex="7"></button>
									</div>
									<div class="col-md-5 col-lg-4 col-sm-4 col-xs-12">
										<button type="reset" class="cancel form-group icglabel_cancel"
											id="accMgtCancel" tabindex="8"></button>
									</div>
								</div>
							</form>
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
										<a href="resources/templates/teachers.csv" tabindex="9"
											class="font-medium-roboto txt-14 icglable_downloadhere"></a><span
											class="icglable_toteacherstaffaccountcreationtemplate">
										</span>
									</p>
								</div>
							</section>
							<section class="small_section">
								<p class="section-header third_subtitle icglabel_upload"></p>
								<hr style="border-top: 1px solid black;" />
								<form id="teachersForm" method="POST"
									action="teacherStaffUpload?token=${sessionID}"
									enctype="multipart/form-data">
									<div class="font-light-roboto">
										<div class="text-center">
											<strong><font color="green" class="text-center txt-12"> <span
												id="teacher_csvupload" class="icglabel_csvuploadmsg"
												style="display: none;"
												data-success="${teacherUploadsuccess}"></span>
											</font></strong>
											<div class="text-center">
												<strong><font color="green" class="text-center txt-12"><span id="teacherStaffRecordCount"
													class="text-center">${teacherStaffRecordCount}</span></font></strong>
											</div>
											<c:if test="${fn:length(ignoredList) > 1 }">
												<div class="text-center txt-12">
													<a href="#" data-toggle="modal" id="teachersignoredList"
														data-target="#ignoredDetailsModal"
														class="txt-12 icglabel_errorDetails"
														onclick="showErrorsInTextArea($(this))"
														data-keyboard="true" error_details='${ignoredList}'></a>
												</div>
											</c:if>
											<strong><font color="red" class="text-center txt-12"> <span
												id="teacher_invalidcsverror"
												class="icglabel_invalidcsverrormsg" style="display: none;"
												data-error="${teacherCsvInvalidMsg}"></span>
											</font></strong>
											<strong><font color="red" class="text-center txt-12"> <span
												id="teacher_csverror"
												class="icglabel_csverrormsg" style="display: none;"></span>
											</font></strong>
											<div class="user_account_mangmet"
												style="padding: 20px 20px 0px 19px;">
												<p class="text-center">
													<input id="csvFileUpload" type="file" name="fileUpload"
														accept=".csv" tabindex="10" class="inputfile"
														onchange="showFileName(this)"> <label
														for="csvFileUpload"><span
														class="font-medium-roboto txt-14 icglable_browsehere"></span><label
														class="icglable_uploadstaffcsv"> </label></label>
												</p>
											</div>
											<div class="text-center">
												<button type="submit"
													class="save form-group text-center icglabel_upload"
													value="Upload" style="padding: 9px 15px;" id="accMgtUpload"
													tabindex="30" disabled tabindex="11"></button>
											</div>
										</div>
									</div>
								</form>
							</section>
						</section>
					</div>
				</div>
				<section class="content-header-parent">
					<div class="row sub-title">
						<div class="col-md-6 col-sm-12 col-xs-12"
							style="padding-left: 21px;">
							<p class="bread_heading_parent txt-18 icglabel_accountlist"></p>
						</div>
						<div class="col-md-2 col-sm-4 col-xs-6" style="">
							<p class="bread_heading_parent txt-18">
								<select tabindex="12" class="grade" id="accMgtRole"
									name="findAccMgtRole" title="Please select the role">
									<option class="icglabel_role" value="ALL"></option>
									<option class="icglabel_teacher" value="school_teacher"></option>
									<option class="icglabel_staff" value="school_staff"></option>
								</select>
							</p>
						</div>

						<div class="col-md-2 col-sm-4 col-xs-6" style="">
							<p class="bread_heading_parent txt-18">

								<select tabindex="13" class="grade" id="student_grade"
									name="findAccMgtGrade" title="Please the grade">
									<option class="icglabel_allgrade" value="ALL"></option>
								</select>
							</p>
						</div>
						<div class="col-md-2 col-sm-4 col-xs-6" style="">
							<p class="bread_heading_parent">
								<button type="submit"
									class="save form-group text-center btn-actMgt icglabel_find"
									id="findTeacher" tabindex="14"></button>
							</p>
						</div>
					</div>
				</section>
				<!-- </form> -->
				<section class="user_account_mangmet">
					<div class="text-center txt-12">
						<strong><font color="green"><span
							class="teacher_update_success icglabel_updatedsuccessfully"
							style="text-align: center; display: none"></span></font><font
							color="green"> <span
							class="teacher_delete_success icglabel_deletedsuccessfully"
							style="text-align: center; display: none"></span></font></strong>
					</div>
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th class="icglabel_name"></th>
									<th class="icglabel_grade"></th>
									<th class="icglabel_class"></th>
									<th class="icglabel_contactno"></th>
									<th class="icglabel_usernameemailid"></th>
									<th class="icglabel_status"></th>
									<th class="icglabel_role"></th>
									<th class="icglabel_edit"></th>
									<th class="icglabel_delete"></th>
								</tr>
							</thead>
							<tbody id="teacherStaffWebSection">
							</tbody>
						</table>
					</div>
					<span id="pagination" class="pagination" style="display: none">
						<span class="col-md-8"> <span class="icglabel_page"></span>&nbsp;
							<span id="assigned_startPage" style="text-align: left;"></span> <span
							class="icglabel_of" style="text-align: left;"></span> <span
							id="assigned_endPage" style="text-align: left;"></span>
					</span> <span class="col-md-4"><span class="pull-right"> <span
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
			<section id="student_section" style="display: none">
				<div class="row">
					<div class="col-md-8">
						<section class="user_account_mangmet student-section">
							<form id="studentCreateForm" method="POST"
								action="saveWebStudents?token=${sessionID}"
								novalidate="novalidate">
								<p hidden="hidden">
									<input type="text" id="studentCreate" value="stdCreate">
								</p>
								<div class="row">
									<div class="col-md-12 act-error-msgs txt-12">
										<strong><font color="green"> <span
											id="student_account_success_msg"
											class="icglabel_create_account_success_msg"
											style="display: none;"
											data-success="${create_account_success}"></span>
										</font> <font color="red"> <span id="student_account_exists"
											class="icglabel_account_failed" style="display: none;"
											data-error="${account_failed}"></span>
										</font> <font color="red"> <span id="student_account_fail"
											class="icglabel_student_exists" style="display: none;"
											data-error="${studentExist}"></span>
										</font> <font color="red"> <span id="student_device_map_error"
											class="icglabel_device_map_error" style="display: none;"
											data-error="${deviceStudentsError}"></span>
										</font> <font color="red"> <span id="student_uuid_error"
											class="icglabel_uuid_error" style="display: none;"
											data-error="${deviceDoesNotExist}"></span>
										</font></strong>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-groups">
											<label for="accMgtStudentName"
												class="control-label icglabel_name"></label> <input
												type="text" class="form-control-user" id="accMgtStudentName"
												name="accMgtStudentName" required
												title="Please enter the student name" tabindex="1"
												maxlength="45" /> <span
												class="error-block icglabel_accMgtName_err"
												id="accMgtStudentName_err"></span>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="accMgtStudentGrade"
												class="control-label icglabel_grade"></label> <select
												onChange="setStudentDefaultClass()" tabindex="5"
												class="form-control-user select-grade"
												id="accMgtStudentGrade" name="accntMgtStudentGrade" required
												title="Please select the grade">
											</select> <span class="error-block icglabel_accMgtGrade_err"
												id="accMgtStudentGrade_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-groups">
											<label for="accMgtStudentId"
												class="control-label icglabel_uuid"></label> <input
												type="text" class="form-control-user" id="accMgtStudentId"
												name="accMgtStudentUuid" required
												title="Please enter the uuid" tabindex="2" maxlength="36" /><span
												class="error-block icglabel_accMgtStudentId_err"
												id="accMgtStudentId_err"></span>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-groups">
											<label for="accMgtStudentClassroom"
												class="control-label icglabel_class"></label> <select
												tabindex="5" class="form-control-user select-grade"
												id="accMgtStudentClassroom" name="accMgtStudentClass"
												required title="Please select the class" tabindex="6">
												<option value="" class="icglabel_accMgtClass"></option>
											</select> <span class="error-block icglabel_accMgtClass_err"
												id="accMgtStudentClassroom_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 ">
										<div class="form-group">
											<label for="accMgtStudentAppno"
												class="control-label icglabel_registrationno"> </label> <input
												type="number" class="form-control-user" maxlength="10"
												id="accMgtStudentAppno" name="accMgtStudentAppno" required
												title="Please enter the application no." tabindex="3" /><span
												class="error-block icglabel_accMgtStudentAppno_err"
												id="accMgtStudentAppno_err"></span>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="accMgtStudentContact"
												class="control-label icglabel_emergencycontactno"> </label>
											<input type="number" maxlength="10"
												class="form-control-user contactnumber"
												id="accMgtStudentContact" name="accMgtStudentContact"
												tabindex="7" title="Please enter the contact number" /><span
												class="error-block icglabel_accMgtContact_err"
												id="accMgtStudentContact_err"></span><span
												class="error-block icglabel_contact10digits"
												id="accMgtStudentContactStartOfContactInvalid"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 ">
										<div class="form-group">
											<label for="accMgtStudentRollno"
												class="control-label icglabel_rollno"></label> <input
												type="number" maxlength="5" class="form-control-user"
												id="accMgtStudentRollno" name="accMgtStudentRollno" required
												title="Please enter the roll no." tabindex="4" /><span
												class="error-block icglabel_accMgtStudentRollno_err"
												id="accMgtStudentRollno_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-5 col-lg-4 col-sm-4 col-xs-12">
										<button type="submit" class="save form-group icglabel_create"
											id="accMgtStudentCreate" tabindex="8"></button>
									</div>
									<div class="col-md-5 col-lg-4 col-sm-4 col-xs-12">
										<button type="reset" class="cancel form-group icglabel_cancel"
											id="accMgtStudentCancel" tabindex="9"></button>
									</div>
								</div>
							</form>
						</section>
					</div>
					<div class="col-md-4">
						<section class="">
							<section class="small_section student-download">
								<p
									class="section-header third_subtitle icglabel_downloadtemplates"></p>
								<hr style="border-top: 1px solid black;" />
								<div class="user_account_mangmet font-light-roboto student-dwnbody">
									<p class="text-center">
										<a href="resources/templates/students.csv" tabindex="10"
											class="font-medium-roboto txt-14 icglable_downloadhere">
										</a><span class="icglable_tostudentaccountcreationtemplate">
										</span>
									</p>
								</div>
							</section>
							<section class="small_section student-uploadtemplate">
								<p class="section-header third_subtitle icglabel_upload"></p>
								<hr style="border-top: 1px solid black;" />
								<form id="studentsForm" method="POST"
									action="uploadStudents?token=${sessionID}"
									enctype="multipart/form-data">
									<div class="text-center">
											<strong><font color="red" class="text-center txt-12"> <span
												id="student_csverror"
												class="icglabel_csverrormsg" style="display: none;"></span>
											</font></strong>
										<strong><font color="green" class="text-center txt-12"> <span
											id="student_csvupload" class="icglabel_csvuploadmsg"
											style="display: none;" data-success="${studentUploadsuccess}"></span>
										</font></strong>
										<div class="text-center txt-12">
											<strong><font color="green" class="text-center"><span id="studentsRecordCount"
												class="text-center">${studentsRecordCount}</span></font></strong>
										</div>
										<strong><font color="red" class="text-center txt-12"> <span
											id="student_invalidcsverror"
											class="icglabel_invalidcsverrormsg" style="display: none;"
											data-error="${studentCsvInvalidMsg}"></span>
										</font></strong>
										<c:if test="${fn:length(studentsIgnoredList) > 1 }">
											<div class="text-center txt-12">
												<a href="#" data-toggle="modal" id="studentsIgnoredList"
													data-target="#ignoredStudentsDetailsModal"
													class="icglabel_errorDetails"
													onclick="showStudentsErrorsInTextArea($(this))"
													data-keyboard="true"
													student_error_details='${studentsIgnoredList}'></a>
											</div>
										</c:if>
										<div
											class="user_account_mangmet font-light-roboto section-body">
											<p class="text-center">
												<input id="studentCsvFileUpload" type="file"
													name="studentFileUpload" accept=".csv" tabindex="11"
													class="inputfile" onchange="showFileName(this)"> <label
													for="studentCsvFileUpload"><span
													class="font-medium-roboto txt-14 icglable_browsehere"></span><label
													class="icglable_uploadstudentcsv"> </label></label>
											</p>
										</div>
										<div class="text-center">
											<button type="submit"
												class="save form-group text-center icglabel_upload"
												value="UploadStudent" style="padding: 9px 15px;"
												id="accMgtStudentUpload" tabindex="12" disabled></button>
										</div>
									</div>
								</form>
							</section>
						</section>
					</div>
				</div>
				<section class="content-header-parent">
					<div class="row">
						<div class="col-md-6 col-sm-12 col-xs-12"
							style="padding-left: 21px;">
							<p class="bread_heading_parent txt-18 icglabel_accountlist"></p>
						</div>
						<div class="col-md-2 col-sm-4 col-xs-6" style="">
							<p class="bread_heading_parent txt-18">
								<select tabindex="13" onchange="setFilterStudentClass()"
									class="grade" id="accMgtStudentGradeFilter"
									name="accMgtStudentGradeFilter" title="Please select the grade">
								</select>
							</p>
						</div>
						<div class="col-md-2 col-sm-4 col-xs-6" style="">
							<p class="bread_heading_parent txt-18">
								<select tabindex="14" class="grade"
									title="Please select the class" id="accMgtStudentClassFilter"
									name="accMgtStudentClassFilter">
									<option class="icglabel_class" value="Class"></option>
								</select>
							</p>
						</div>
						<div class="col-md-2 col-sm-4 col-xs-6" style="">
							<p class="bread_heading_parent">
								<button type="submit"
									class="save form-group text-center btn-actMgt icglabel_find"
									id="findStudent" tabindex="15"></button>
							</p>
						</div>

					</div>
				</section>
				<!-- </form> -->
				<section class="user_account_mangmet">
					<div class="text-center txt-12">
						<strong><font color="green"> <span
							class="student_update_success icglabel_updatedsuccessfully"
							style="text-align: center; display: none"></span></font> <font
							color="green"> <span
							class="student_delete_success icglabel_deletedsuccessfully"
							style="text-align: center; display: none"></span></font></strong>
					</div>
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th class="icglabel_name"></th>
									<th class="icglabel_registrationno"></th>
									<th class="icglabel_grade"></th>
									<th class="icglabel_class"></th>
									<th class="icglabel_rollno"></th>
									<th class="icglabel_uuid"></th>
									<th class="icglabel_emergencycontactno"></th>
									<th class="icglabel_edit"></th>
									<th class="icglabel_delete"></th>
								</tr>
							</thead>
							<tbody id="stdntSection">
							</tbody>
						</table>
					</div>
					<span id="studentspagination" class="pagination"
						style="display: none"> <span class="col-md-8"> <span
							class="icglabel_page"></span>&nbsp;<span id="students_startPage"
							style="text-align: left;"></span> <span class="icglabel_of"
							style="text-align: left;"></span> <span id="students_endPage"
							style="text-align: left;"></span>
					</span> <span class="col-md-4"><span class="pull-right"><span
								class="disabled pull-left"> <a id="stdecrease"
									onClick="decreaseStudentsHref()" href="#"><img
										src="resources/images/grey_drop_down.png"
										class="left-navigate img-responsive pull-left"></a></span> <span
								class="disabled pull-right"><a id="stincrease"
									onClick="increaseStudentsHref()" href="#"><img
										src="resources/images/grey_drop_down.png"
										class="right-navigate img-responsive"></a></span></span> </span>
					</span>
				</section>
			</section>
		</section>
	</div>
</div>
<div class="modal fade" id="deleteDetails" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<!--  <button type="button" class="close" data-dismiss="modal">&times;</button> -->
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-12">
						<span class="ui-icon ui-icon-alert"
							style="float: left; margin: 12px 12px 20px 0;"></span><span
							class="icglabel_deletedconirmation"></span>
					</div>
				</div>

				<div class="row" style="padding: 15px;">
					<div class=" col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" tabindex="1"
								class="confirm pull-left icglabel_ok" data-dismiss="modal"
								onclick="deleteTeacherStaffDetails()"></button>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" tabindex="2"
								class="modal-Cancel pull-right icglabel_cancel"
								data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="deleteStDetails" role="dialog">
	<p hidden="hidden">
		<input type="text" id="student_id">
	</p>
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-md-12">
						<span class="ui-icon ui-icon-alert"
							style="float: left; margin: 12px 12px 20px 0;"></span><span
							class="icglabel_studentdeleteconirmation"></span>
					</div>
				</div>
				<div class="row" style="padding: 15px;">
					<div class=" col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" class="confirm pull-left icglabel_ok"
								data-dismiss="modal" tabindex="1"
								onclick="deleteStudentDetails()"></button>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" tabindex="2"
								class="modal-Cancel pull-right icglabel_cancel"
								data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- Modal for editable teacher Accpunt list -->
<div class="modal fade" id="editTeacherDetails" role="dialog">
	<div class="modal-dialog">
		<p hidden="hidden">
			<input type="text" id="teacher_id">
		</p>
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<form id="accCreateForm" method="POST" action="/accountCreate/"
					novalidate="novalidate">
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAccMgtRole" class="control-label icglabel_role"></label>
									<select value="school_teacher" tabindex="1"
										title="Please select the role" class="form-control-user"
										id="editAccMgtRole" onchange="roleChange($(this))">
										<option class="icglabel_teacher" value="Teacher"></option>
										<option class="icglabel_staff" value="Staff"></option>
									</select> <span class="error-block icglabel_accMgtRole_err"
										id="editAccMgtRole_err"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAccMgtName" class="control-label icglabel_name"></label>
									<input type="text" class="form-control-user" value="name"
										id="editAccMgtName" name="editAccMgtName" required
										title="Please enter the teacher/staff name" tabindex="4"
										maxlength="45" /><span
										class="error-block icglabel_accMgtName_err"
										id="editAccMgtName_err"></span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-group">
									<label for="accMgtEditTeacherGrade"
										class="control-label icglabel_grade"></label> <select
										name="accMgtEditTeacherGrade" class="form-control-user"
										value="1" id="accMgtEditTeacherGrade"
										onChange="setTeacherEditClass($(this))" required
										title="Please select the grade" tabindex="2"></select> <span
										class="error-block icglabel_accMgtGrade_err"
										id="editAccMgtGrade_err"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAccMgtClass"
										class="control-label icglabel_class"> </label> <select
										name="student_class" id="accMgtTeacherEditClass"
										class="form-control-user" required
										title="Please enter the class" tabindex="5">
										<option class="icglabel_studentclass" value=""></option>
									</select> <span class="error-block icglabel_accMgtClass_err"
										id="editAccMgtClass_err"></span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-group">
									<label for="editAccMgtContact"
										class="control-label icglabel_contactno"> </label> <input
										class="form-control-user contactnumber" type="number"
										maxlength="10" required id="editAccMgtContact"
										name="editAccMgtContact" value="09123456789" tabindex="6" /><span
										class="error-block icglabel_accMgtClass_err"
										id="editAccMgtContact_err"></span><span
										class="error-block icglabel_contact10digits"
										id="editAccMgtContactStartOfContactInvalid"></span>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="row" style="padding: 15px;">
				<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
					<div class="form-group">
						<button type="button" tabindex="7"
							class="confirm pull-right icglabel_update" data-dismiss="modal"
							id="updateTeacherDetails"></button>
					</div>
				</div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
					<div class="form-group">
						<button type="button" tabindex="8"
							class="modal-Cancel pull-left icglabel_cancel"
							data-dismiss="modal"></button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Modal for editable student Accpunt list -->
<div class="modal fade" id="editStudentDetails" role="dialog">
	<div class="modal-dialog">
		<p hidden="hidden">
			<input type="text" id="student_id">
		</p>
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<!--  <button type="button" class="close" data-dismiss="modal">&times;</button> -->
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<form id="accCreateForm" method="POST" action="/accountCreate/"
					novalidate="novalidate">
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAccMgtStudentName"
										class="control-label icglabel_name"> </label> <input
										type="text" class="form-control-user"
										id="editAccMgtStudentName" name="editAccMgtStudentName"
										required title="Please enter the student name" tabindex="1"
										maxlength="45" /><span
										class="error-block icglabel_accMgtName_err"
										id="editAccMgtStudentName_err"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="editAccMgtStudentGrade"
										class="control-label icglabel_grade"></label> <select
										name="editAccMgtStudentGrade" class="form-control-user"
										id="editAccMgtStudentGrade" onChange="setStudentEditClass()"
										required title="Please select the grade" tabindex="3"></select>
									<span class="error-block icglabel_accMgtGrade_err"
										id="editAccMgtStudentGrade_err"></span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAccMgtStudentClass"
										class="control-label icglabel_class"> </label> <select
										class="form-control-user" name="editAccMgtStudentClass"
										id="editAccMgtStudentClass" required
										title="Please select the class" tabindex="2"></select> <span
										class="error-block icglabel_accMgtClass_err"
										id="editAccMgtStudentClass_err"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="editAccMgtStudentContact"
										class="control-label icglabel_contactno"> </label> <input
										class="form-control-user contactnumber" type="number"
										maxlength="10" required id="editAccMgtStudentContact"
										name="editAccMgtStudentContact" tabindex="4" /><span
										class="error-block icglabel_accMgtContact_err"
										id="editAccMgtStudentContact_err"></span><span
										class="error-block icglabel_contact10digits"
										id="editAccMgtStudentContactStartOfContactInvalid"></span>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="row" style="padding: 15px;">
				<div class=" col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
					<div class="form-group">
						<button type="submit" tabindex="5"
							class="confirm pull-right icglabel_update" data-dismiss="modal"
							id="updateStudentDetails"></button>
					</div>
				</div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
					<div class="form-group">
						<button type="button" tabindex="6"
							class="modal-Cancel pull-left icglabel_cancel"
							data-dismiss="modal"></button>
					</div>
				</div>
			</div>
		</div>
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
						<textarea class="form-control" id="errorDetails_id" rows="10"
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

<div class="modal fade" id="ignoredStudentsDetailsModal" role="dialog">
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
						<textarea class="form-control" id="student_errorDetails_id"
							rows="10" name="schoolAnnouncementDescription" tabindex="2"
							readonly> </textarea>
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
<script src="resources/js/accountManagement.js"></script>
