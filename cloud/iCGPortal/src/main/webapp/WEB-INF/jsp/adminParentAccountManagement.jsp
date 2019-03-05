<%@include file="includes/taglib.jsp"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglable_parentaccountmanagement"></h1>
		</section>
		<!-- body  content -->
		<section class="content">
			<section class="content-header-parent">
				<p class="bread_heading_parent sub_title icglable_searchparent"></p>
			</section>
			<section>
				<div class="row">
					<div class="col-md-12">
						<section class="user_account_mangmet">
							<div id="adminParent_update_success" class="txt-12 text-center"
								style="display: none">
								<strong><font color="green"
									class="icglabel_adminParent_update_success"></font></strong>
							</div>
							<div id="adminParent_update_failure" class="txt-12 text-center"
								style="display: none">
								<strong><font color="red"
									class="icglabel_adminParent_update_failure"></font></strong>
							</div>
							<div id="adminParent_delete_success" class="txt-12 text-center"
								style="display: none">
								<strong><font color="green"
									class="icglabel_adminParent_delete_success"></font></strong>
							</div>
							<div id="adminParent_delete_failure" class="txt-12 text-center"
								style="display: none">
								<strong><font color="red"
									class="icglabel_adminParent_delete_failure"></font></strong>
							</div>
							<div class="txt-12 text-center">
								<span class="error-block icglabel_searchCriteriaerror"
									id="searchCriteriaError" style="text-align: center"></span>
							</div>
							<form id="searchParentForm">
								<div class="row">
									<div class="col-md-4">
										<div class="form-groups">
											<label for="searchParentProfileName"
												class="control-label icglable_profilename"></label> <input
												type="text" class="form-control-user"
												id="searchParentProfileName" name="searchParentProfileName"
												required title="Please enter Profile Name" tabindex="1"
												maxlength="45" />
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-groups">
											<label for="searchParentContact"
												class="control-label icglabel_contactno"> </label> <input
												type="number" maxlength="10" class="form-control-user"
												id="searchParentContact" name="searchParentContact"
												onKeyPress="if(this.value.length==10) return false;"
												required title="Please enter Contact" tabindex="3" /><span
												class="error-block icglabel_contact10digits"
												id="searchParentContactInValid"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-4">
										<div class="form-group">
											<label for="searchParentUsername"
												class="control-label icglabel_usernameemailid"> </label> <input
												type="text" class="form-control-user"
												id="searchParentUsername" name="searchParentUsername"
												required title="Please enter Username / Email id"
												tabindex="2" onkeyup="return forceLower(this);" /><span
												class="error-block icglabel_email_error"
												id="searchParentUsername_incorrect"> </span>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-groups">
											<label for="searchParentUuid"
												class="control-label icglabel_uuid"></label> <input
												type="text" class="form-control-user" id="searchParentUuid"
												name="searchParentUuid" required title="Please enter UUID"
												tabindex="4" />
										</div>
									</div>
								</div>
							</form>
							<div class="row">
								<div class="col-md-2">
									<input type="button" class="save form-group" value="Search"
										id="searchParentSearch" tabindex="5" />
								</div>
							</div>
						</section>
					</div>
				</div>
				<section id="searchResultView" style="display: none">
					<section class="content-header-parent">
						<div class="row">
							<div class="col-md-4 col-sm-12 col-xs-12">
								<p
									class="bread_heading_parent sub_title icglabel_searchresultview"></p>
							</div>
						</div>
					</section>
					<section class="user_account_mangmet">
						<div class="table-responsive">
							<table class="table">
								<thead>
									<tr>
										<th class="icglabel_name"></th>
										<th class="icglabel_usernameemailid"></th>
										<th class="icglabel_contactno"></th>
										<th class="icglabel_usertype"></th>
										<th class="icglabel_usersource"></th>
										<th class="icglabel_status"></th>
										<th class="icglabel_uuidorcount"></th>
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
								id="school_startPage" style="text-align: left;"></span> <span
								class="icglabel_of" style="text-align: left;"></span> <span
								id="school_endPage" style="text-align: left;"></span>
						</span> <span class="col-md-4"><span class="pull-right"><span
									class="disabled pull-left"> <a id="decreaseassigendHref"><img
											src="resources/images/grey_drop_down.png"
											class="left-navigate img-responsive pull-left"></a></span> <span
									class="disabled pull-right"><a id="increaseassigendHref"><img
											src="resources/images/grey_drop_down.png"
											class="right-navigate img-responsive"></a></span></span> </span>
						</span>
					</section>
				</section>
			</section>
			<!--end of teacher-->
		</section>
	</div>
</div>
<!-- Modal for editable teacher Accpunt list -->
<div class="modal fade" id="editAdminParentDetails" tabindex='-1'
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
				<form id="editAdminPArentForm" method="POST"
					action="/editAdminPArent/" novalidate="novalidate">
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAdminParentProfileName"
										class="control-label icglable_profilename"></label> <input
										type="text" class="form-control-user"
										id="editAdminParentProfileName"
										name="editAdminParentProfileName" required
										title="Please enter profile name" tabindex="1" maxlength="45" />
									<span class="error-block icglabel_addUserProfileName_err"
										id="editAdminParentProfileName_err"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editAdminParentContact"
										class="control-label icglabel_contactno"></label> <input
										type="number" class="form-control-user"
										id="editAdminParentContact" name="editAdminParentContact"
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
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-group">
									<label for="editAdminParentUsername"
										class="control-label icglabel_usernameemailid"> </label> <input
										type="text" class="form-control-user"
										id="editAdminParentUsername" name="editAdminParentUsername"
										required title="Please enter Username / Email id" tabindex="2" /><span
										class="error-block icglabel_email_error"
										id="editAdminParentUsername_err"></span><span
										class="error-block icglabel_email_error"
										id="searchParentUsername_incorrect"></span>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="row">
				<div class="col-md-2 col-lg-2 col-sm-1 col-xs-1"></div>
				<div class="col-md-4 col-lg-4 col-sm-5 col-xs-4">
					<div class="form-group pull-left">
						<button type="button" class="confirm icglabel_update"
							data-dismiss="modal" id="editAdminParentSave"></button>
					</div>
				</div>
				<div class="col-md-4 col-lg-4 col-sm-5 col-xs-4">
					<div class="form-group pull-right">
						<button type="button" class="modal-Cancel icglabel_cancel"
							data-dismiss="modal" id="editAdminParentCancel"></button>
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
							class="icglabel_deletedadminparentconfirmation"></span>
					</div>

				</div>
				<div class="row" style="padding: 15px !important;">
					<div class="col-md-2 col-lg-2 col-sm-1 col-xs-1"></div>
					<div class="col-md-4 col-lg-4 col-sm-5 col-xs-4">
						<div class="form-group  pull-left">
							<button type="button" class="confirm icglabel_ok"
								data-dismiss="modal" id="adminParentDeleteId"></button>
						</div>
					</div>
					<div class="col-md-4 col-lg-4 col-sm-5 col-xs-4">
						<div class="form-group  pull-right">
							<button type="button" class="modal-Cancel icglabel_cancel"
								data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script src="resources/js/adminParentAccountManagement.js"></script>
