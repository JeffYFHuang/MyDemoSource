<%@include file="includes/taglib.jsp"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglable_useraccountmanagement"></h1>
		</section>
		<!-- body  content -->
		<section class="content">
			<section class="content-header-parent">
				<p class="bread_heading_parent sub_title icglable_addprofile"></p>
			</section>
			<section>
				<div class="row">
					<div class="col-md-12">
						<section class="user_account_mangmet">
							<div id="userAccount_create_success" class="txt-12 text-center"
								style="display: none">
								<strong><font color="green" class="icglabel_useraccountupdate_success"></font></strong>
							</div>
							<div id="userAccount_create_failure" class="txt-12 text-center"
								style="display: none">
								<strong><font color="red" class="icglabel_userAccount_create_failure"></font></strong>
							</div>
							<p class="text-center" id="usersubmitMsg"></p>
							<form id="addUserProfileForm" method="POST"
								action="/addUserProfile/" novalidate="novalidate">
								<div class="row">
									<div class="col-md-4">
										<div class="form-groups">
											<label for="addUserRole" class="control-label icglable_role">
											</label> <select class="form-control-user" id="addUserRole"
												name="addUserRole" required title="Please enter Role"
												tabindex="1"><option value="system_admin"
													class="icglabel_system_admin"></option>
												<option value="support_staff" class="icglabel_support_staff"></option></select>
											<span class="error-block icglabel_role_empty"
												id="addUserRole_err"></span>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="addUserUsername"
												class="control-label icglable_usernameemailid">
												Username / Email id </label> <input type="text"
												class="form-control-user" id="addUserUsername"
												name="addUserUsername" required
												title="Please enter Username / Email id" tabindex="3"
												onkeyup="return forceLower(this);" /><span
												class="error-block icglabel_email_error"
												id="addUserUsername_err"></span><span
												class="error-block icglabel_email_error"
												id="addUserUsername_incorrect"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-4">
										<div class="form-groups">
											<label for="addUserProfileName"
												class="control-label icglable_profilename"></label> <input
												type="text" class="form-control-user"
												id="addUserProfileName" name="addUserProfileName" required
												title="Please enter Profile Name" tabindex="2"
												maxlength="45" /> <span
												class="error-block icglabel_addUserProfileName_err"
												id="addUserProfileName_err"></span>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-groups">
											<label for="addUserContact"
												class="control-label icglabel_contactno"> </label> <input
												type="number" maxlength="10" class="form-control-user"
												id="addUserContact" name="addUserContact"
												onKeyPress="if(this.value.length==10) return false;"
												required title="Please enter Contact" tabindex="4" /><span
												class="error-block icglabel_contact_empty"
												id="addUserContact_err"></span><span
												class="error-block icglabel_contact_error"
												id="addUserContactStartOfContactInvalid"></span><span
												class="error-block icglabel_contact10digits"
												id="addUserContactInValidContactNo"></span>
										</div>
									</div>
								</div>
							</form>
							<div class="row">
								<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
									<input type="button" class="save form-group create"
										value="Create" id="addUserCreate" tabindex="5" />
								</div>
								<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
									<input type="reset" class="cancel form-group" value="Cancel"
										id="addUserCancel" tabindex="6" />
								</div>
							</div>
						</section>
					</div>
				</div>
				<section class="content-header-parent">
					<div class="row sub_title">
						<div class=" col-lg-4 col-md-4 col-sm-12 col-xs-12"
							style="padding-left: 21px;">
							<p class="bread_heading_parent icglable_useraccountlists"></p>
						</div>
						<div
							class="col-lg-2 col-md-2 col-sm-6 col-xs-6 col-lg-offset-5 col-md-offset-5 filter-user-tabs">
							<p class="bread_heading_parent">
								<select tabindex="13" class="grade"
									id="adminUserAccountListFilter">
									<option value="0" class="icglable_userAccount_role"></option>
									<option value="1" class="icglabel_system_admin"></option>
									<option value="2" class="icglabel_support_staff"></option>
								</select>
							</p>
						</div>
						<div class="col-lg-1 col-md-1 col-sm-4 col-xs-4" style="">
							<p class="bread_heading_parent">
								<input type="button"
									class="save form-group text-center btn-actMgt" value="Go"
									id="adminUserGo" tabindex="14" />
							</p>
						</div>
					</div>
				</section>
				<section class="user_account_mangmet">
					<div id="userAccount_update_success" class="txt-12 text-center"
						style="display: none">
						<strong><font color="green" class="icglabel_userAccount_update_success"></font></strong>
					</div>
					<div id="userAccount_update_failure" class="txt-12 text-center"
						style="display: none"><font color="red">
						<strong class="icglabel_userAccount_update_failure"></strong><br />
						<strong class="text-center" id="userUpdateMsg"></strong></font>
					</div>
					<div id="userAccount_delete_success" class="txt-12 text-center"
						style="display: none">
						<strong><font color="green" class="icglabel_userAccount_delete_success"></font></strong>
					</div>
					<div id="userAccount_delete_failure" class="txt-12 text-center"
						style="display: none">
						<strong><font color="red" class="icglabel_userAccount_delete_failure"></font></strong>
					</div>
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th class="icglabel_name"></th>
									<th class="icglabel_usernameemailid"></th>
									<th class="icglabel_contactnumber"></th>
									<th class="icglabel_role"></th>
									<th class="icglable_lastlogin"></th>
									<th class="icglabel_edit"></th>
									<th class="icglabel_delete"></th>
								</tr>
							</thead>
							<tbody id="stDetails">
							</tbody>
						</table>
					</div>
					<span id="pagination" class="pagination" style="display: none">
						<span class="col-md-8"> <span class="icglabel_page"></span>&nbsp;<span
							id="assigned_adminstartPage" style="text-align: left;"></span> <span
							class="icglabel_of" style="text-align: left;"></span> <span
							id="assigned_adminendPage" style="text-align: left;"></span>
					</span> <span class="col-md-4"><span class="pull-right"><span
								class="disabled pull-left"> <a id="decrease"
									onClick="decreaseUserListHref()" href="#"><img
										src="resources/images/grey_drop_down.png"
										class="left-navigate img-responsive pull-left"></a></span> <span
								class="disabled pull-right"><a id="increase"
									onClick="increaseUserListHref()" href="#"><img
										src="resources/images/grey_drop_down.png"
										class="right-navigate img-responsive"></a></span></span> </span>
					</span>
				</section>
			</section>
			<!--end of teacher-->
		</section>
	</div>
</div>
<!-- Modal for editable teacher Accpunt list -->
<div class="modal fade" id="editAdminUserDetails" tabindex='-1'
	role="dialog">
	<div class="modal-dialog">
		<p hidden="hidden">
			<input type="text" id="edituser_id">
		</p>
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<!--  <button type="button" class="close" data-dismiss="modal">&times;</button> -->
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<form id="editNewSchoolForm" method="POST" action="/editNewSchool/"
					novalidate="novalidate">
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAdminUserRole"
										class="control-label icglable_role"></label> <select
										class="form-control-user" id="editAdminUserRole"
										name="editAdminUserRole" required title="Please enter Role"
										tabindex="1"><option value="system_admin"
											class="icglabel_system_admin"></option>
										<option value="support_staff" class="icglabel_support_staff"></option></select>
									<span class="error-block icglabel_role_empty"
										id="editAdminUserRole_err"></span>
								</div>
							</div>

							<div class="col-md-6">
								<div class="form-group">
									<label for="editUserUsername"
										class="control-label icglable_usernameemailid"> </label> <input
										type="text" class="form-control-user" id="editUserUsername"
										name="editUserUsername" required
										title="Please enter Profile Name" tabindex="2"
										onkeyup="return forceLower(this);" /> <span
										class="error-block icglabel_email_error"
										id="editAdminParentUsername_err"> </span><span
										class="error-block icglabel_email_error"
										id="searchParentUsername_incorrect"> </span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAdminUserProfileName"
										class="control-label icglable_profilename"> </label> <input
										type="text" class="form-control-user"
										id="editAdminUserProfileName" name="editAdminUserProfileName"
										required title="Please enter Profile Name" tabindex="2"
										maxlength="45" /> <span
										class="error-block icglabel_addUserProfileName_err"
										id="editAdminParentProfileName_err"></span>
								</div>
							</div>

							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAdminUserContact"
										class="control-label icglabel_contactno"></label> <input
										type="number" class="form-control-user"
										id="editAdminUserContact" name="editAdminUserContact"
										onKeyPress="if(this.value.length==10) return false;" required
										title="Please enter Contact" tabindex="2" /><span
										class="error-block icglabel_contact_empty"
										id="editAdminParentContact_err"></span><span
										class="error-block icglabel_contact_error"
										id="editAdminParentContactStartOfContactInvalid"></span><span
										class="error-block icglabel_contact10digits"
										id="editAdminParentContactInValidContactNo"></span>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="row">
				<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
					<div class="form-group pull-left">
						<button type="button" class="confirm icglabel_update"
							data-dismiss="modal" id="editAdminUserUpdate"></button>
					</div>
				</div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
					<div class="form-group pull-right">
						<button type="button" class="modal-Cancel icglabel_cancel"
							data-dismiss="modal" id="editAdminUserCancel"></button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="deleteParentDetails" role="dialog">
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
							class="icglabel_deletedadminaccountconfirmation"></span>
					</div>

				</div>
				<div class="row" style="padding: 15px !important;">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group pull-left">
							<button type="button" class="confirm icglabel_ok"
								data-dismiss="modal" id="adminParentDeleteId"></button>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group pull-right">
							<button type="button" class="modal-Cancel icglabel_cancel"
								data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script src="resources/js/adminUserAccountManagement.js"></script>
