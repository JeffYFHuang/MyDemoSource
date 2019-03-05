<%@include file="includes/taglib.jsp"%>
<%@ page import="com.liteon.icgwearable.hibernate.entity.Accounts"%>
<style type="text/css">
.clearXbutton::-ms-clear {
	display: none;
}

.table td {
	padding: 6px !important;
}
</style>
<script src="resources/js/schoolAdmin.js"></script>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<%
				if (session.getAttribute("currentUser").equals("school_admin")) {
			%>
			<h1 class="icglabel_schooladminaccount">
				<spring:message code="school_admin.school_admin_account" />
			</h1>
			<ol class="breadcrumb-new" id="bread">
				<li><a href="schooldashboard?token=${sessionID}"
					href="schoolDashboard.html" id="school_dashboard"><span
						class="icglabel_dashboard"> <spring:message
								code="parent_admin.account_management.dashboard" /></span></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li><span class="icglabel_schooladminaccount"><spring:message
							code="school_admin.school_admin_account" /></span></li>
				<%
					} else {
				%>
				<h1 class="icglabel_schooladminaccount">
					<spring:message
						code="parent_admin.account_management.accountmanagement" />
				</h1>
				<ol class="breadcrumb-new" id="bread">
					<li><a href="parentdashboard?token=${sessionID}"
						href="schoolDashboard.html" id="school_dashboard"><span
							class="icglabel_dashboard"> <spring:message
									code="parent_admin.account_management.dashboard" /></span></a><i
						class="fa fa-chevron-right" aria-hidden="true"></i></li>
					<li><span class="icglabel_accountmanagement"><spring:message
								code="parent_admin.account_management.accountmanagement" /></span></li>
					<%
						}
					%>
				</ol>
		</section>
		<!-- body  content -->
		<section class="content">
			<!--Start of Student-->
			<section id="admin_section">
				<section class="user_account_mangmet">
					<div id="usersubmit_success"
						style="text-align: center; display: none">
						<strong><font color="green"
							class="text-center txt-12 icglabel_usersubmit_success"></font></strong>
					</div>
					<p style="text-align: center; color: red;" id="usersubmitMsg"></p>
					<form id="schoolAdminCreateForm" method="POST"
						name="schoolAdminCreateForm" action="/schoolAdminCreate/"
						novalidate="novalidate">
						<div class="row">
							<div class="col-md-4">
								<div class="form-groups">
									<label for="schoolAdminUserName"
										class="control-label icglable_usernameemail"><spring:message
											code="parent_admin.account_management.username" /></label> <input
										type="text" class="form-control-user" id="schoolAdminUserName"
										name="schoolAdminUserName" required
										title="Please enter the Username" tabindex="1"
										Placeholder="xyz@mail.com" disabled />
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="schoolAdminChangePwd"
										class="control-label icglabel_changepassword"> </label><input
										type="password" class="form-control-user"
										id="schoolAdminChangePwd" name="schoolAdminChangePwd"
										onKeyPress="if(this.value.length==15) return false;" required
										data-minlength="10" maxlength="15"
										title="Please enter Change Password" tabindex="4" /> <span
										class="error-block icglabel_schoolAdminPwdDoesntMatch_err"
										id="schoolAdminPwdDoesntMatch_err"></span><span
										class="error-block icglabel_schoolAdminChangePwd_invaliderr"
										id="schoolAdminChangePwd_invaliderr"></span>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-4">
								<div class="form-groups">
									<label for="schoolAdminProfileName"
										class="control-label icglabel_profile"> </label> <input
										type="text" class="form-control-user"
										id="schoolAdminProfileName" name="schoolAdminProfileName"
										required title="Please enter Profile Name" tabindex="2"
										maxlength="45" /><span
										class="error-block icglabel_addUserProfileName_err"
										id="schoolAdminProfileName_err"></span><span
										class="error-block icglabel_schoolAdminProfileName_invaliderr"
										id="schoolAdminProfileName_invaliderr"></span>
								</div>
							</div>
							<div class="col-md-4">
								<div class="form-group">
									<label for="schoolAdminConfirmPwd"
										class="control-label icglabel_confirm"> </label><input
										type="password" class="form-control-user"
										id="schoolAdminConfirmPwd" name="schoolAdminConfirmPwd"
										onKeyPress="if(this.value.length==15) return false;" required
										data-minlength="10" maxlength="15"
										title="Please enter Confirm Password" tabindex="5" />
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-4">
								<div class="form-group">
									<label for="schoolAdminContact"
										class="control-label icglabel_contactno"> </label> <input
										type="number" maxlength="10"
										class="form-control inputLogin contactnumber"
										id="schoolAdminContact" name="schoolAdminContact" required
										title="Please enter Contact" tabindex="3" /><span
										class="error-block icglabel_contact_empty"
										id="schoolAdminContact_err"></span><span
										class="error-block icglabel_contact10digits"
										id="schoolAdminContactInValidContactNo"></span>
								</div>
							</div>
							<div id="schoolAdminPasswordCheck" class="form-group col-md-4">
								<table class="table text-left table-font pswdtable">
									<tbody class="txt-16">
										<tr style="border-top: 2px solid white !important;"
											class="text-muted" id="schoolAdminCharLongCheck">
											<td><i class="fa fa-check" aria-hidden="true"
												id="schoolAdminCheckChar"></i> <i
												class="fa fa-exclamation-triangle" aria-hidden="true"
												style="display: none;" id="schoolAdminWarningChar"></i></td>
											<td class="icglabel_contain8char"></td>
										</tr>
										<tr class="text-muted" id="schoolAdminLetterCheck">
											<td><i class="fa fa-check" aria-hidden="true"
												id="schoolAdminCheckLetter"></i> <i
												class="fa fa-exclamation-triangle" aria-hidden="true"
												style="display: none;" id="schoolAdminWarningLetter"></i></td>
											<td class="icglabel_containletter"></td>
										</tr>
										<tr style="border-bottom: 1px solid #f4f4f4 !important;"
											class="text-muted" id="schoolAdminNumberCheck">
											<td><i class="fa fa-check" aria-hidden="true"
												id="schoolAdminCheckNumber"></i> <i
												class="fa fa-exclamation-triangle" aria-hidden="true"
												style="display: none;" id="schoolAdminWarningNumber"></i></td>
											<td class="icglabel_containnumber"></td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
					</form>
					<div class="row">
						<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
							<button type="submit" class="save form-group icglabel_save"
								id="schoolAdminSave" tabindex="6"></button>
						</div>
						<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
							<button type="reset" class="cancel form-group icglabel_cancel"
								onclick="window.location.reload();" id="schoolAdminCancel"
								tabindex="7"></button>
						</div>
						<br>
					</div>
				</section>
			</section>
			<%
				if (session.getAttribute("currentUser").equals("school_admin")) {
			%>
			<section>
				<section class="content-header-parent">
					<p class="bread_heading_parent sub_title icglabel_schooltimings"></p>
				</section>
				<section class="user_account_mangmet">
					<div id="schooltimingssubmit_success"
						style="text-align: center; display: none">
						<strong><font color="green"
							class="text-center txt-12 icglabel_schooltimingssubmit_success"></font></strong>
					</div>
					<p style="text-align: center; color: red;" id="submitMsg"></p>
					<div class="row">
						<div class="col-lg-2 col-md-3 col-sm-6 col-xs-12">
							<div class="form-group">
								<label for="accMgtStudentGrade"
									class="control-label icglabel_schoolinstart"> </label><select
									class="form-control-user" id="school_in_start_min"
									name="schoolinstartmin">
									<option>00</option>
									<option>01</option>
									<option>02</option>
									<option>03</option>
									<option>04</option>
									<option>05</option>
									<option>06</option>
									<option>07</option>
									<option>08</option>
									<option>09</option>
									<option>10</option>
									<option>11</option>
									<option>12</option>
									<option>13</option>
									<option>14</option>
									<option>15</option>
									<option>16</option>
									<option>17</option>
									<option>18</option>
									<option>19</option>
									<option>20</option>
									<option>21</option>
									<option>22</option>
									<option>23</option>
								</select>
							</div>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-6 col-xs-12">
							<div class="form-group form-control-static">
								<label for="accMgtStudentGrade" class="control-label"></label><select
									class="form-control-user" id="school_in_start_sec"
									name="schoolinstartsec">
									<option>00</option>
									<option>15</option>
									<option>30</option>
									<option>45</option>
								</select>
							</div>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-6 col-xs-12">
							<div class="form-group">
								<label for="accMgtStudentGrade"
									class="control-label icglabel_schoolinend"> </label><select
									class="form-control-user" id="school_in_end_min"
									name="schoolinendmin">
									<option>00</option>
									<option>01</option>
									<option>02</option>
									<option>03</option>
									<option>04</option>
									<option>05</option>
									<option>06</option>
									<option>07</option>
									<option>08</option>
									<option>09</option>
									<option>10</option>
									<option>11</option>
									<option>12</option>
									<option>13</option>
									<option>14</option>
									<option>15</option>
									<option>16</option>
									<option>17</option>
									<option>18</option>
									<option>19</option>
									<option>20</option>
									<option>21</option>
									<option>22</option>
									<option>23</option>
								</select>
							</div>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-6 col-xs-12">
							<div class="form-group form-control-static">
								<label for="accMgtStudentGrade" class="control-label"></label><select
									class="form-control-user" id="school_in_end_sec"
									name="schoolinendsec">
									<option>00</option>
									<option>15</option>
									<option>30</option>
									<option>45</option>
								</select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-2 col-md-3 col-sm-6 col-xs-12">
							<div class="form-group">
								<label for="accMgtStudentGrade"
									class="control-label icglabel_schooloutstart"> </label><select
									class="form-control-user" id="school_out_start_min"
									name="schooloutstartmin">
									<option>00</option>
									<option>01</option>
									<option>02</option>
									<option>03</option>
									<option>04</option>
									<option>05</option>
									<option>06</option>
									<option>07</option>
									<option>08</option>
									<option>09</option>
									<option>10</option>
									<option>11</option>
									<option>12</option>
									<option>13</option>
									<option>14</option>
									<option>15</option>
									<option>16</option>
									<option>17</option>
									<option>18</option>
									<option>19</option>
									<option>20</option>
									<option>21</option>
									<option>22</option>
									<option>23</option>
								</select>
							</div>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-6 col-xs-12">
							<div class="form-group form-control-static">
								<label for="accMgtStudentGrade" class="control-label"></label><select
									class="form-control-user" id="school_out_start_sec"
									name="schooloutstartsec">
									<option>00</option>
									<option>15</option>
									<option>30</option>
									<option>45</option>
								</select>
							</div>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-6 col-xs-12">
							<div class="form-group">
								<label for="accMgtStudentGrade"
									class="control-label icglabel_schooloutend"> </label><select
									class="form-control-user" id="school_out_end_min"
									name="schooloutendmin">
									<option>00</option>
									<option>01</option>
									<option>02</option>
									<option>03</option>
									<option>04</option>
									<option>05</option>
									<option>06</option>
									<option>07</option>
									<option>08</option>
									<option>09</option>
									<option>10</option>
									<option>11</option>
									<option>12</option>
									<option>13</option>
									<option>14</option>
									<option>15</option>
									<option>16</option>
									<option>17</option>
									<option>18</option>
									<option>19</option>
									<option>20</option>
									<option>21</option>
									<option>22</option>
									<option>23</option>
								</select>
							</div>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-6 col-xs-12">
							<div class="form-group form-control-static">
								<label for="accMgtStudentGrade" class="control-label"></label><select
									class="form-control-user" id="school_out_end_sec"
									name="schooloutendsec">
									<option>00</option>
									<option>15</option>
									<option>30</option>
									<option>45</option>
								</select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
							<button type="submit" class="save form-group icglabel_save"
								id="scheduleSubmitButton" tabindex="6"></button>
						</div>
						<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
							<button type="submit" class="cancel form-group icglabel_cancel"
								id="scheduleCancelButton" tabindex="7"></button>
						</div>
						<br>
					</div>
				</section>
			</section>
			<section class="content-header-parent">
				<p class="bread_heading_parent sub_title icglabel_schoolholidays"></p>
			</section>
			<section id="admin_section">
				<div class="row">
					<div class="col-md-8">
						<section class="user_account_mangmet">

							<div class="table-responsive">
								<table class="table">
									<thead>
										<tr>
											<th class="icglabel_holiday"></th>
											<th class="icglabel_startdate"></th>
											<th class="icglabel_enddate"></th>
										</tr>
									</thead>
									<tbody>
										<c:if test="${fn:length(schoolCalendarList) eq 0}">
											<tr>
												<td colspan="3"><strong class="icglabel_nodata"></strong></td>
											</tr>
										</c:if>
										<c:forEach items="${schoolCalendarList}" var="schoolCalendar">
											<tr>
												<td class="content_left"><c:out
														value="${schoolCalendar.name}" /></td>
												<td><c:out value="${schoolCalendar.dateclose}" /></td>
												<td><c:out value="${schoolCalendar.datereopen}" /></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</section>
					</div>
					<div class="col-md-4">
						<section class="">
							<section class="small_section">
								<p
									class="section-header third_subtitle icglabel_downloadholidaystemplate"></p>
								<hr style="border-top: 1px solid black;" />
								<div class="user_account_mangmet font-light-roboto student-body">
									<p class="text-center">
										<a href="resources/templates/holidays.csv" tabindex="9"
											class="font-medium-roboto txt-14 icglabel_clickhere"> </a> <span
											class="icglabel_csv"></span>
									</p>
								</div>
							</section>
							<section class="small_section">
								<p class="section-header third_subtitle icglabel_upload"></p>
								<hr style="border-top: 1px solid black;" />
								<form method="POST" action="schoolAdminProfile"
									enctype="multipart/form-data">
									<input type="hidden" name="token" value="${sessionID}">
									<div class="font-light-roboto">
										<div class="text-center">
											<strong><font color="green"> <span
													id="csvupload"
													class="text-center txt-12 icglabel_csvuploadmsg"
													style="display: none;" data-success="${uploadsuccess}"></span>
											</font></strong>
										</div>
										<c:if test="${not empty holidayUploadCountMsg}">
											<div class="text-center txt-12" id="holidayUploadCountMsg">
												<strong><font color="green"
													class="text-center txt-12"> ${holidayUploadCountMsg}
														&nbsp; </font></strong>
											</div>
										</c:if>
										<c:if test="${not empty fn:trim(rowDetails)}">
											<div class="text-center txt-12">
												<a href="#" data-toggle="modal" id="holidayErrorDetails"
													data-target="#errorDetailsModal"
													class="icglabel_errorDetails"
													onclick="mergeCategoryInDetails($(this))"
													data-keyboard="true" error_details="${rowDetails}"></a>
											</div>
										</c:if>
										<div class="text-center">
											<strong><font color="red" class="txt-12"> <span
													id="invalidcsverror"
													class="text-center icglabel_invalidcsverrormsg"
													style="display: none;" data-error="${csvInvalidMsg}"></span>
											</font></strong>
											<strong><font color="red" class="text-center txt-12"> <span
												id="holidays_csverror"
												class="icglabel_csverrormsg" style="display: none;"></span>
											</font></strong>
										</div>
										<div class="user_account_mangmet">
											<p class="text-center">
												<input id="csvFileUpload" type="file" name="fileUpload"
													accept=".csv" class="inputfile"
													onchange="showFileName(this)"> <label
													for="csvFileUpload"><span
													class="font-medium-roboto txt-14 icglable_browsehere"></span><label
													class="icglabel_uploadcsv"> </label></label>
											</p>
										</div>
										<div class="text-center">
											<input type="submit" id="holidayUpload"
												class="save form-group text-center icglabel_upload"
												style="padding: 9px 15px;" tabindex="11" value="Upload"
												disabled>
										</div>
									</div>
								</form>
							</section>
						</section>
					</div>
				</div>
			</section>
			<%
				}
			%>
		</section>
	</div>
	<div class="modal fade" id="errorDetailsModal" role="dialog">
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
</div>
<script src="resources/js/date-formatter.js"></script>
<script type="text/javascript">
	$('.account').removeClass("treeview").addClass("active");
	$('.account').removeClass("font-medium-roboto")
			.addClass("font-bold-roboto");
	$("#sSchoolAdminIcon")
			.attr("src",
					"resources/images/SchoolAdmin_sideBarIcons/White/school_admin_account.png");
	$("#schoolAdminCancel").click(function() {
		document.getElementById("schoolAdminCreateForm").reset();
	});
	function mergeCategoryInDetails(usrObj) {
		var error_details = usrObj.attr('error_details');
		$("#editcategoryIn_id").val(error_details);
	}
</script>