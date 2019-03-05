<%@include file="includes/taglib.jsp"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglable_searchparent"></h1>
		</section>
		<!-- body  content -->
		<section class="content">
			<section>
				<div class="row">
					<div class="col-md-12">
						<section class="user_account_mangmet">
							<div id="adminParent_update_success"
								style="text-align: center; display: none">
								<strong><font
									class="txt-12 icglabel_adminParent_update_success"></font></strong>
							</div>
							<div id="adminParent_update_failure"
								style="text-align: center; display: none">
								<strong><font
									class="txt-12 icglabel_adminParent_update_failure"></font></strong>
							</div>
							<div id="adminParent_delete_success"
								style="text-align: center; display: none">
								<strong><font
									class="txt-12 icglabel_adminParent_delete_success"></font></strong>
							</div>
							<div id="adminParent_delete_failure"
								style="text-align: center; display: none">
								<strong><font
									class="txt-12 icglabel_adminParent_delete_failure"></font></strong>
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
												maxlength="45" /> <span
												class="error-block icglabel_addUserProfileName_err"
												id="searchParentProfileName_err"></span>
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
												class="error-block icglabel_contact_empty"
												id="searchParentContact_err"></span><span
												class="error-block icglabel_contact_error"
												id="searchParentContactStartOfContactInvalid"></span><span
												class="error-block icglabel_contact10digits"
												id="searchParentContactInValidContactNo"></span>
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
												id="searchParentUsername_err"> </span><span
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
												tabindex="4" /> <span
												class="error-block icglabel_uuid_empty"
												id="searchParentUuid_err"></span>
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
										<th class="icglabel_uuidorcount"></th>
										<th class="icglable_lastlogin"></th>
									</tr>
								</thead>
								<tbody id="stDetails">
								</tbody>
							</table>
						</div>
						<span id="pagination" class="pagination" style="display: none">
							<span class="col-md-8"> <span class="icglabel_page"></span>&nbsp;<span
								id="school_startPage" style="text-align: left;">1</span> <span
								class="icglabel_of" style="text-align: left;"></span> <span
								id="school_endPage" style="text-align: left;">10</span>
						</span> <span class="col-md-4"><span class="pull-right"><span
									class="disabled pull-left"> <a id="decreaseassigendHref"><img
											src="resources/images/grey_drop_down.png"
											class="left-navigate img-responsive pull-left hand"></a></span> <span
									class="disabled pull-right"><a id="increaseassigendHref"><img
											src="resources/images/grey_drop_down.png"
											class="right-navigate img-responsive hand"></a></span></span> </span>
						</span>
					</section>
				</section>
			</section>
			<!--end of teacher-->
		</section>
	</div>
</div>
<script src="resources/js/supportStaffParentSearch.js"></script>
