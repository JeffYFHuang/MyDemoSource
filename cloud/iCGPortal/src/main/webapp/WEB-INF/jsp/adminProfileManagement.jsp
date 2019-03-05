<%@include file="includes/taglib.jsp"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglable_accountmanagement"></h1>
		</section>

		<!-- body  content -->

		<section class="content">
			<!--Start of Student-->
			<section class="user_account_mangmet">
									<div id="usersubmit_success" class="txt-12 text-center"
						style="display: none"><strong><font color="green"
									class="icglabel_usersubmit_success"></font></strong></div>
									<p style="text-align:center;color:red;" id="usersubmitMsg"></p>
				<form id="adminProfileCreateForm"
					name="adminProfileCreateForm" novalidate="novalidate">
					<div class="row">
						<div class="col-md-4">
							<div class="form-groups">
								<label for="adminUserName"
									class="control-label icglable_usernameemail"><spring:message
										code="parent_admin.account_management.username" /></label> <input
									type="text" class="form-control-user" id="adminUserName"
									name="adminUserName" required
									title="Please enter the username/emailId" tabindex="1"
									 disabled="disabled" /> <span
									class="error-block icglabel_username_err" id="adminUserName_err"></span>
									<span class="error-block icglabel_username_invaliderr" id="adminUserName_invaiderr"></span>
							</div>
						</div>


						<div class="col-md-4">
							<div class="form-group">
								<label for="adminChangePwd"
									class="control-label icglabel_changepassword"> </label><input
									type="password" class="form-control-user"
									id="adminChangePwd" name="adminChangePwd"
									onKeyPress="if(this.value.length==12) return false;" required
									data-minlength="10" maxlength="15"
									title="Please enter the change password" tabindex="4" /> <span
									class="error-block icglabel_passwordmismatch" id="adminChangePwdDoesntMatch_err"></span><span class="error-block icglabel_password_err"
									id="adminChangePwd_invaliderr"></span>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<div class="form-groups">
								<label for="adminProfileName"
									class="control-label icglabel_profile"> </label> <input
									type="text" class="form-control-user"
									id="adminProfileName" name="adminProfileName"
									required title="Please enter the profile name" tabindex="2" maxlength="45"/><span
									class="error-block icglabel_empty_profilename" id="adminProfileName_err"></span><span
									class="error-block icglabel_invalid_profilename" id="adminProfileName_invaliderr"></span>
							</div>
						</div>
						<div class="col-md-4">
							<div class="form-group">
								<label for="adminConfirmPwd"
									class="control-label icglabel_confirm"> </label><input
									type="password" class="form-control-user"
									id="adminConfirmPwd" name="adminConfirmPwd"
									onKeyPress="if(this.value.length==12) return false;" required
									data-minlength="10" maxlength="15"
									title="Please enter the confirm password" tabindex="5" />

							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<div class="form-group">
								<label for="adminContact"
									class="control-label icglabel_contactno"> </label> <input
									type="number" maxlength="10" class="form-control inputLogin"
									id="adminContact" name="adminContact" required
									title="Please enter the contact no." tabindex="3" /><span class="error-block icglabel_contact_empty"
									id="adminContact_err"></span><span class="error-block icglabel_contact10digits"
									id="adminContactInValidContactNo"></span>
							</div>
						</div>
						<div id="adminPasswordCheck" class="form-group col-md-4">
							<table class="table text-left table-font pswdtable">
								<tbody class="txt-14">
									<tr style="border-top: 2px solid white !important;"
										class="text-muted" id="adminCharLongCheck">
										<td><i class="fa fa-check" aria-hidden="true"
											id="adminCheckChar"></i> <i
											class="fa fa-exclamation-triangle" aria-hidden="true"
											style="display: none;" id="adminWarningChar"></i></td>
										<td class="icglabel_contain8char"></td>
									</tr>
									<tr class="text-muted" id="adminLetterCheck">
										<td><i class="fa fa-check" aria-hidden="true"
											id="adminCheckLetter"></i> <i
											class="fa fa-exclamation-triangle" aria-hidden="true"
											style="display: none;" id="adminWarningLetter"></i></td>
										<td class="icglabel_containletter"></td>
									</tr>
									<tr style="border-bottom: 1px solid #f4f4f4 !important;"
										class="text-muted" id="adminNumberCheck">
										<td><i class="fa fa-check" aria-hidden="true"
											id="adminCheckNumber"></i> <i
											class="fa fa-exclamation-triangle" aria-hidden="true"
											style="display: none;" id="adminWarningNumber"></i></td>
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
							id="adminProfileSave" tabindex="6"></button>
					</div>
					<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
						<button type="reset" class="cancel form-group icglabel_cancel"
							id="adminProfileCancel" tabindex="7"></button>
					</div>
				</div>
			</section>
			<!-- content_switchedon_wrapper closed -->
		</section>
	</div>
</div>
<!-- 	body content ends here -->
<script src="resources/js/date-formatter.js"></script>
<script src="resources/js/adminProfileManagement.js"></script>