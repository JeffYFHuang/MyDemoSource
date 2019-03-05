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
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_profileMgmt"></h1>
			<ol class="breadcrumb-new" id="bread">
				<li><a href="parentdashboard?token=${sessionID}"
					class="icglabel_dashboard"></a><i class="fa fa-chevron-right"
					aria-hidden="true"></i></li>
				<li><a href="#" id="sNickName"></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li class="active"><a href="#" class="icglabel_profileMgmt"></a></li>
			</ol>
		</section>
		<!-- body  content -->
		<section class="content">
			<!--Start of Student-->
			<section class="user_account_mangmet">
				<form id="schoolAdminCreateForm" method="POST"
					name="schoolAdminCreateForm" action="/schoolAdminCreate/"
					novalidate="novalidate">
					<div class="row">
						<div class="text-center">
							<b id="usersubmitMsg"></b>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<div class="form-groups">
								<label for="parentAdminUserName"
									class="control-label icglable_usernameemail"><spring:message
										code="parent_admin.account_management.username" /></label> <input
									type="text" class="form-control-user" id="parentAdminUserName"
									name="parentAdminUserName" required
									title="Please enter the Username" tabindex="1" disabled />
							</div>
						</div>
						<div class="col-md-4">
							<div class="form-group">
								<label for="parentAdminChangePwd"
									class="control-label icglabel_changepassword"> </label><input
									type="password" class="form-control-user"
									id="parentAdminChangePwd" name="parentAdminChangePwd"
									onKeyPress="if(this.value.length==12) return false;" required
									data-minlength="10" maxlength="15"
									title="Please enter Change Password" tabindex="4" /> <span
									class="error-block icglabel_passwordmismatch"
									id="parentAdminPwdDoesntMatch_err"></span><span
									class="error-block icglabel_password_err"
									id="parentAdminChangePwd_invaliderr"></span>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<div class="form-groups">
								<label for="parentAdminProfileName"
									class="control-label icglabel_profile"> </label> <input
									type="text" class="form-control-user"
									id="parentAdminProfileName" name="parentAdminProfileName"
									required title="Please enter Profile Name" tabindex="2"
									maxlength="45" /><span
									class="error-block icglabel_addUserProfileName_err"
									id="parentAdminProfileName_err"></span>
							</div>
						</div>
						<div class="col-md-4">
							<div class="form-group">
								<label for="parentAdminConfirmPwd"
									class="control-label icglabel_confirmpassword"> </label><input
									type="password" class="form-control-user"
									id="parentAdminConfirmPwd" name="parentAdminConfirmPwd"
									onKeyPress="if(this.value.length==12) return false;" required
									data-minlength="10" maxlength="15"
									title="Please enter Confirm Password" tabindex="5" />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<div class="form-group">
								<label for="parentAdminContact"
									class="control-label icglabel_contactno"> </label> <input
									class="form-control-user inputLogin contactnumber" type="number"
									maxlength="10" required id="parentAdminContact"
									name="parentAdminContact" tabindex="3" /><span
									class="error-block icglabel_contact_empty"
									id="parentAdminContact_err"></span><span
									class="error-block icglabel_contact_error"
									id="parentAdminContactInValidContactNo"></span>
							</div>
						</div>
						<div id="schoolAdminPasswordCheck" class="form-group col-md-4">
							<table class="table text-left table-font">
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
							id="parentAdminSave" tabindex="6"></button>
					</div>
					<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
						<button type="reset" class="cancel form-group icglabel_cancel"
							id="parentAdminCancel" tabindex="7"></button>
					</div>
				</div>
			</section>
			<section class="content-header-parent new_parent">
				<p
					class="bread_heading_parent sub_title icglabel_parentsubscription"></p>
			</section>
			<div class="content_switchedon_wrapper new_parent">
				<section class="content_switchedon">
					<div class="box">
						<div class="row">
							<div class="text-center txt-12">
								<b id="saveButtonMsg"></b>
							</div>
						</div>
						<div class="row">
							<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 profile_layout">
								<div class="box_school_In normal-border profile_height">
									<p class="third_subtitle txt-15 icglabel_schooltime"></p>
									<label class="switch"> <input type="checkbox"
										id="School_Enter"> <span class="slider round"></span>
									</label>
									<div class="hr_line clear normal-border-bottom"></div>
									<div class="switch_content">
										<p class="font-light-roboto icglabel_schooltimedesc"></p>
										<img
											src="resources/images/ParentDashboard/school_in_out_icon.png"
											width="34" height="34" class="schoolin_icon" />
									</div>
								</div>
							</div>
							<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 profile_layout">
								<div class="Geofencing_In normal-border profile_height">
									<p class="third_subtitle txt-15 icglabel_geofencing"></p>
									<label class="switch"> <input type="checkbox"
										id="Geofence_Entry"> <span class="slider round"></span>
									</label>
									<div class="hr_line clear normal-border-bottom"></div>
									<div class="switch_content">
										<p class="font-light-roboto icglabel_geofencingdesc"></p>
										<img
											src="resources/images/ParentDashboard/geofencing_icon.png"
											class="schoolin_icon" />
									</div>
								</div>
							</div>
							<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 profile_layout">
								<div class="SOS_In  danger-border profile_height">
									<p class="third_subtitle txt-15 icglabel_sos"></p>
									<label class="switch"> <input type="checkbox"
										id="SOS_Alert"> <span class="slider round"></span>
									</label>
									<div class="hr_line clear danger-border-bottom"></div>
									<div class="switch_content">
										<p class="font-light-roboto icglabel_sosdesc"></p>
										<img src="resources/images/ParentDashboard/SOS.png" width="34"
											height="34" class="schoolin_icon" />
									</div>
								</div>
							</div>
							<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 profile_layout">
								<div class="box_school_In danger-border profile_height">
									<p class="third_subtitle txt-15 icglabel_bandremoval"></p>
									<label class="switch"> <input type="checkbox"
										id="Band_Removal_Alert"> <span class="slider round"></span>
									</label>
									<div class="hr_line clear danger-border-bottom"></div>
									<div class="switch_content">
										<p class="font-light-roboto icglabel_bandremovaldesc"></p>
										<img src="resources/images/ParentDashboard/Band_removal.png"
											width="34" height="34" class="schoolin_icon" />
									</div>
								</div>
							</div>
							<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 profile_layout">
								<div class="Geofencing_In danger-border profile_height">
									<p class="third_subtitle txt-15 icglabel_abnormalvitalsign"></p>
									<label class="switch"> <input type="checkbox"
										id="Abnormal_Vital_Sign"> <span class="slider round"></span>
									</label>
									<div class="hr_line clear danger-border-bottom"></div>
									<div class="switch_content">
										<p class="font-light-roboto icglabel_abnormalvitalsigndesc"></p>
										<img
											src="resources/images/ParentDashboard/abnormal_vital_sign.png"
											class="schoolin_icon" />
									</div>
								</div>
							</div>
							<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 profile_layout">
								<div class="SOS_In success-border profile_height">
									<p class="third_subtitle txt-15 icglabel_reportsummary"></p>
									<label class="switch"> <input type="checkbox"
										id="Report_Summary"> <span class="slider round"></span>
									</label>
									<div class="hr_line clear success-border-bottom"></div>
									<div class="switch_content">
										<p class="font-light-roboto icglabel_reportsummarydesc"></p>
										<img src="resources/images/ParentDashboard/Report_summary.png"
											class="schoolin_icon" />
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="form-group" id="form-group-buttons">
						<input type="button" class="save icglabel_cancel" value="Save"
							id="saveButton">
					</div>
				</section>
			</div>
			<!-- content_switchedon_wrapper closed -->
		</section>
	</div>
</div>
<!-- 	body content ends here -->
<script src="resources/js/date-formatter.js"></script>
<script src="resources/js/profileManagement.js"></script>