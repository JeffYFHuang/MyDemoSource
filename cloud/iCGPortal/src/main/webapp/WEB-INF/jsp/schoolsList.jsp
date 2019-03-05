<%@include file="includes/taglib.jsp"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_schoolmanagement"></h1>
		</section>
		<!-- body  content -->
		<section class="content">
			<section class="content-header-parent">
				<p class="bread_heading_parent sub_title icglabel_addnewschool"></p>
			</section>
			<section id="teacher_section">
				<div class="row">
					<div class="col-md-12">
						<form novalidate="novalidate">
							<section class="user_account_mangmet">
								<div class="row">
									<div class="col-md-12">
										<strong style="text-align: center;"> <span
											id="schoolCreateSuccess" style="display: none"> <font
												color="green" class="txt-12 icglabel_school_success"></font>
										</span> <span id="schoolCreateFailure" style="display: none">
												<font color="red" class="txt-12 icglabel_school_failure"></font>
										</span></strong>
									</div>
								</div>
								<div class="row">
									<div class="col-md-4">
										<div class="form-groups">
											<label for="addSchoolName"
												class="control-label icglabel_schoolname"></label> <input
												type="text" class="form-control-user" id="addSchoolName"
												name="addSchoolName" required title="Please enter name"
												tabindex="1" maxlength="45" /> <span
												class="error-block icglabel_schoolname_empty"
												id="addSchoolNamee_err"></span>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="addSchoolCity"
												class="control-label icglabel_city"></label><input
												type="text" class="form-control-user" id="addSchoolCity"
												name="addSchoolCity" required title="Please enter City"
												tabindex="5" maxlength="25" /> <span
												class="error-block icglabel_city_empty"
												id="addSchoolCity_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-4">
										<div class="form-groups">
											<label for="addSchoolContact"
												class="control-label icglabel_contactno"> </label> <input
												type="number" class="form-control-user"
												id="addSchoolContact" name="addSchoolContact" required
												title="Please enter Contact" tabindex="2" maxlength="10" /><span
												class="error-block icglabel_contact_empty"
												id="addSchoolContact_err"></span><span
												class="error-block icglabel_contact10digits"
												id="addSchoolContactStartOfContactInvalid"></span>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-groups">
											<label for="addSchoolCounty"
												class="control-label icglabel_county"> </label> <select
												name="addSchoolCounty" class="form-control-user"
												id="addSchoolCounty" required title="Please enter County"
												tabindex="6"></select> <span
												class="error-block icglabel_county_empty"
												id="accMgtClass_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-4 ">
										<div class="form-group">
											<label for="addSchoolUsername"
												class="control-label icglabel_adminusername"></label> <input
												type="text" class="form-control-user" id="addSchoolUsername"
												name="addSchoolUsername" required
												title="Please enter Username / Email id" tabindex="3"
												onkeyup="return forceLower(this);" maxlength="145" /><span
												class="error-block icglabel_email_empty"
												id="addSchoolUsername_err"></span><span
												class="error-block icglabel_email_error"
												id="addSchoolUsername_incorrect"></span>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="addSchoolState"
												class="control-label icglabel_state"></label> <input
												type="text" maxlength="25" class="form-control-user"
												id="addSchoolState" name="addSchoolState" required
												title="Please enter State" tabindex="7" /><span
												class="error-block icglabel_state_empty"
												id="addSchoolState_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-4 ">
										<div class="form-group">
											<label for="addSchoolAddress"
												class="control-label icglabel_address"></label> <input
												type="text" class="form-control-user" id="addSchoolAddress"
												name="addSchoolAddress" required
												title="Please enter Address" tabindex="4" maxlength="145" />
											<span class="error-block icglabel_address_empty"
												id="addSchoolAddress_err"></span>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="addSchoolZipcode"
												class="control-label icglabel_zipcode"></label> <input
												type="number" class="form-control-user"
												id="addSchoolZipcode" name="addSchoolZipcode" required
												title="Please enter Zipcode" tabindex="7" maxlength="6" /><span
												class="error-block icglabel_zipcode_empty"
												id="addSchoolZipcode_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-3 col-lg-2 col-sm-12 col-xs-12">
										<input type="button" class="save form-group create"
											value="Create" id="addSchoolCreate" tabindex="8" />
									</div>
									<div class="col-md-3 col-lg-2 col-sm-12 col-xs-12">
										<input type="reset" class="cancel form-group" value="Cancel"
											id="addSchoolCancel" tabindex="9" />
									</div>
								</div>
							</section>
						</form>
					</div>
				</div>
			</section>
			<section class="content-header-parent">
				<div class="row">
					<div class="col-md-6 col-sm-12 col-xs-12"
						style="padding-left: 21px;">
						<p class="bread_heading_parent sub_title icglabel_schoollist"></p>
					</div>
				</div>
			</section>
			<section class="user_account_mangmet">
				<div class="row">
					<div class="col-md-12 txt-12 text-center">
						<strong> <span class="alert" id="editDetialsSuccess"
							style="display: none"> <font color="green"
								class="icglabel_update_success"></font>
						</span> <span class="alert" id="editDetialsFailure" style="display: none">
								<font color="red" class="icglabel_update_fail"></font>
						</span><span class="alert" id="schoolDeleteSuccess" style="display: none">
								<font color="green" class="icglabel_delete_successful"></font>
						</span> <span class="alert" id="schoolDeleteFailure"
							style="display: none"> <font color="red"
								class="icglabel_delete_failed"></font>
						</span> <span class="alert" id="editDetialsFailureWithMailID"
							style="display: none"> <font color="red"
								class="icglabel_update_fail_with_mail"></font>
						</span>
						</strong>
					</div>
				</div>
				<div class="table-responsive">
					<table class="table">
						<thead>
							<tr>
								<th class="icglabel_name"></th>
								<th class="icglable_id"></th>
								<th class="icglabel_contactno"></th>
								<th class="icglabel_adminusername"></th>
								<th class="icglable_lastlogin"></th>
								<th class="icglable_allocateddevice"></th>
								<th class="icglabel_edit"></th>
								<th class="icglabel_delete"></th>
							</tr>
						</thead>
						<tbody id="schoolListDetailsBody">
						</tbody>
					</table>
				</div>
				<span id="pagination" class="pagination" style="display: none">
					<span class="col-md-8"> <span class="icglabel_page"></span>&nbsp;<span
						id="school_startPage" style="text-align: left;"></span> <span
						class="icglabel_of" style="text-align: left;"></span> <span
						id="school_endPage" style="text-align: left;"></span>
				</span> <span class="col-md-4"><span class="pull-right"><span
							class="disabled pull-left"> <a id="decrease"
								onClick="decreaseassigendHref()" href="#"><img
									src="resources/images/grey_drop_down.png"
									class="left-navigate img-responsive pull-left"></a></span> <span
							class="disabled pull-right"><a id="increase"
								onClick="increaseassigendHref()" href="#"><img
									src="resources/images/grey_drop_down.png"
									class="right-navigate img-responsive"></a></span> </span></span>
				</span>
			</section>
		</section>
		<!--end of teacher-->
	</div>
</div>
<div class="modal fade" id="deleteSchoolDetails" role="dialog">
	<p hidden="hidden">
		<input type="text" id="school_id">
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
							class="icglabel_deleteSchoolconirmation"></span>
					</div>
				</div>
				<div class="row" style="padding: 15px !important;">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group pull-left">
							<button type="button" class="confirm icglabel_ok"
								data-dismiss="modal" tabindex="1"
								onclick="deleteSchoolDetails()"></button>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group pull-right">
							<button type="button" tabindex="2"
								class="modal-Cancel icglabel_cancel" data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Modal for editable teacher Accpunt list -->
<div class="modal fade" id="editTeacherDetails" tabindex='-1'
	role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<!--  <button type="button" class="close" data-dismiss="modal">&times;</button> -->
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<form name="editNewSchoolForm" id="editNewSchoolForm" method="POST"
					action="" novalidate="novalidate">
					<input type="hidden" class="form-control-user" id="editAccountId"
						name="accountId" />
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAddSchoolName"
										class="control-label icglabel_schoolname"></label> <input
										type="text" class="form-control-user" id="editAddSchoolName"
										name="editAddSchoolName" required title="Please enter name"
										tabindex="1" maxlength="45" /> <span
										class="error-block icglabel_schoolname_empty"
										id="editAddSchoolName_err"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="editAddSchoolCity"
										class="control-label icglabel_city"></label><input type="text"
										class="form-control-user" id="editAddSchoolCity"
										name="editAddSchoolCity" required title="Please enter City"
										tabindex="5" maxlength="25" /> <span
										class="error-block icglabel_city_empty"
										id="editAddSchoolCity_err"></span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAddSchoolContact"
										class="control-label icglabel_contactno"></label> <input
										type="number" maxlength="10" class="form-control-user"
										id="editAddSchoolContact" name="editAddSchoolContact" required
										title="Please enter Contact" tabindex="2" /><span
										class="error-block icglabel_contact_empty"
										id="editAddSchoolContact_err"></span><span
										class="error-block icglabel_contact10digits"
										id="editAddSchoolContactStartOfContactInvalid"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAddSchoolCounty"
										class="control-label icglabel_county"></label> <select
										name="editAddSchoolCounty" class="form-control-user"
										id="editAddSchoolCounty" required title="Please select county"
										tabindex="6"></select><span
										class="error-block icglabel_county_empty"
										id="editAccMgtClass_err"></span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-group">
									<label for="editAddSchoolUsername"
										class="control-label icglabel_adminusername"></label> <input
										type="email" class="form-control-user"
										id="editAddSchoolUsername" name="editAddSchoolUsername"
										required title="Please enter Username / Email id" tabindex="3"
										maxlength="145" /><span
										class="error-block icglabel_email_empty"
										id="editAddSchoolUsername_err"></span><span
										class="error-block icglabel_email_error"
										id="editAddSchoolUsername_incorrect"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="editAddSchoolState"
										class="control-label icglabel_state"></label> <input
										type="text" maxlength="25" class="form-control-user"
										id="editAddSchoolState" name="editAddSchoolState" required
										title="Please enter State" tabindex="7" /><span
										class="error-block icglabel_state_empty"
										id="editAddSchoolState_err"></span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-group">
									<label for="editAddSchoolAddress"
										class="control-label icglabel_address"></label> <input
										type="text" class="form-control-user"
										id="editAddSchoolAddress" name="editAddSchoolAddress" required
										title="Please enter Address" tabindex="4" maxlength="145" />
									<span class="error-block icglabel_address_empty"
										id="editAddSchoolAddress_err"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label for="editAddSchoolZipcode"
										class="control-label icglabel_zipcode"></label> <input
										type="number" maxlength="6" class="form-control-user"
										id="editAddSchoolZipcode" name="editAddSchoolZipcode" required
										title="Please enter Zipcode" tabindex="7" /><span
										class="error-block icglabel_zipcode_empty"
										id="editAddSchoolZipcode_err"></span>
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
							data-dismiss="modal" id="editAdminSchoolUpdate"></button>
					</div>
				</div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
					<div class="form-group pull-right">
						<button type="button" class="modal-Cancel icglabel_cancel"
							data-dismiss="modal" id="editAdminSchoolCancel"></button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script src="resources/js/adminSchoolManagement.js"></script>