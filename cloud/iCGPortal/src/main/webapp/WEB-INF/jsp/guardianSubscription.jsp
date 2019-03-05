<%@include file="includes/taglib.jsp"%>
<%@ page import="com.liteon.icgwearable.hibernate.entity.Accounts"%>
<%
	String parentName = (String) session.getAttribute("name");
%>
<script src="resources/js/json-sort.js"></script>
<script src="resources/js/guardian.js"></script>
<script src="resources/js/date-formatter.js"></script>
<div class="content-wrapper">
	<section class="content-header">
		<h1 class="icglabel_guardian"></h1>
		<ol class="breadcrumb-new" id="bread">
			<li><a href="parentdashboard?token=${sessionID}" id="dashboard"
				class="icglabel_dashboard"></a><i class="fa fa-chevron-right"
				aria-hidden="true"></i></li>
			<li id="liBread"><a href="#" id="studentselectedBread"></a><i
				class="fa fa-chevron-right" aria-hidden="true"></i></li>
			<li><a href="#" id="guardianBread" class="icglabel_guardiansubscription"></a></li>
		</ol>
	</section>
	<section id="navbar-section">
		<div class="navbar navbar-default minlen-li">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle"
					data-target="#kidsnavbar" data-toggle="collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
			</div>
			<div class="navbar-collapse collapse" id="kidsnavbar">
				<ul class="nav navbar-nav" id="guardianNav">
				</ul>
			</div>
		</div>
	</section>
	<!-- body  content -->
	<section class="content" id="kidssection">
		<section class="user_account_mangmet" id="addUUID">
			<div id="addkidssection">
				<form id="unlinkForm" novalidate="novalidate">
					<div class="row" id="guardianStudentSubscription"></div>
					<div class="row">
						<div class="col-md-3 col-lg-2 col-xs-12 col-sm-12">
							<div class="form-groups form-control-static">
								<button type="button" class="save icglabel_save"
									id="guardiansave" style="display: none" tabindex="1"></button>
							</div>
						</div>
						<div class="col-md-4 col-lg-4 col-xs-12 col-sm-12">
							<div id="subscriptionMessage" class="text-left txt-12"
								style="top: 20px; position: relative;"></div>
						</div>
					</div>
				</form>
			</div>
		</section>
		<section class="content-header-parent">
			<p
				class="bread_heading_parent sub_title icglabel_grantaccesstoguardian"></p>
		</section>
		<section class="user_account_mangmet grantaccess" id="">
			<div id="">
				<div class="col-md-12 userexists">
					<span id="responseMessageForCreate" class=" col-md-6 text-center txt-12"></span>
				</div>
				<div class="row guardianAccessSection">
					<div class="col-md-4">
						<label for="guardianUserName"
							class="control-label icglabel_guardianusername"> </label>
						<div class="row">
							<div class="col-md-12">
								<div class="form-groups">
									<input type="text" class="form-control-user"
										id="guardianUserName" name="guardianUserName" required
										title="Please enter name" onkeyup="return forceLower(this);">
									<span class="error-block icglabel_email_empty"
										id="guardianUserName_err"></span> <span
										class="error-block icglabel_email_error"
										id="guardianUserNameValid_err"></span>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-3 guardianKidSelect">
						<label for="guardianAccess"
							class="control-label icglabel_acesstokids"> </label>
						<div class="row">
							<div class="col-md-12">
								<div class="form-groups">
									<div class="multiselect">
										<div id="guardianAccess" tabindex="2" class="selectBox">
											<select class="form-control-user up_arrow">
												<option class="icglabel_selectKid"></option>
											</select>
											<div class="overSelect" id="multiOverSelect"></div>
										</div>
										<div id="checkboxes"></div>
									</div>
									<span class="error-block icglabel_selectkids"
										id="guardianAccess_err"></span>
								</div>
							</div>

						</div>
					</div>
					<div class="col-md-2">
						<div class="form-groups form-control-static">
							<button type="button"
								class="save form-control-static icglabel_create"
								style="top: 16px; position: relative; padding: 10px 50px;"
								id="guardiancreate" tabindex="3"></button>
						</div>
					</div>
				</div>
			</div>
		</section>
		<section class="content-header-parent">

			<p class="bread_heading_parent sub_title icglabel_guardianlists"></p>
		</section>
		<section class="user_account_mangmet">
			<div class="guardian_edit_delete txt-12" id="guardianDelete"
				style="text-align: center;"></div>
			<div class="guardian_edit_delete txt-12" id="guardianEdit"
				style="text-align: center;"></div>
			<div class="table-responsive">
				<table class="table">
					<thead>
						<tr>
							<th class="icglabel_guardianname"></th>
							<th class="icglabel_username"></th>
							<th class="icglabel_kidnames"></th>
							<th class="icglabel_contactno"></th>
							<th class="icglabel_status"></th>
							<th class="icglabel_edit"></th>
							<th class="icglabel_delete"></th>
						</tr>
					</thead>
					<tbody id="guardianLists">
					</tbody>
				</table>
			</div>
		</section>
	</section>
</div>
<!-- Modal for delete confirmation -->
<div class="modal fade" id="deleteGuardian" role="dialog">
	<p hidden="hidden">
		<input type="text" id="deletGuardian_id">
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
							class="icglabel_guardiandeleteconfirmation"></span>
					</div>
				</div>
				<div class="row" style="padding: 15px;">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group pull-right">
							<button type="button" class="confirm icglabel_ok"
								data-dismiss="modal" id="deleteGuardianDetails"></button>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group pull-left">
							<button type="button" class="modal-Cancel icglabel_cancel"
								data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- End of Modal for delete confirmation -->

<!-- Modal for editing guardian list -->
<div class="modal fade" id="editGuardianDetails" tabindex='-1'
	role="dialog">
	<div class="modal-dialog">
		<p hidden="hidden">
			<input type="text" value="" id="guardian_user_id"><span
				id="guardian_kids_ids"></span>
		</p>
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"></h4>
			</div>
			<div class="modal-body">
				<form id="guardianUpdateForm" novalidate="novalidate">
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editguardianName"
										class="control-label icglabel_guardianname"> </label> <input
										type="text" class="form-control-user" id="editguardianName"
										name="editguardianName" required
										title="Please enter guardian name" tabindex="1" /><span
										class="error-block icglabel_kidname_err"
										id="editguardianName_err"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editguardianUserName"
										class="control-label icglabel_username"> </label> <input
										type="text" class="form-control-user"
										id="editguardianUserName" name="editguardianUserName" required
										title="Please enter user name" tabindex="1"
										onkeyup="return forceLower(this);" /><span
										class="error-block icglabel_email_empty"
										id="editguardianUserName_err"></span><span
										class="error-block icglabel_email_error"
										id="editguardianUserNameValid_err"></span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12">
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editGuardianKids"
										class="control-label icglabel_kidnames"> </label>
									<div class="multiselect">
										<div id="editguardianAccess" tabindex="2" class="selectBox">
											<select class="form-control-user up_arrow">
												<option class="icglabel_selectKid"></option>
											</select>
											<div class="overSelect" id="editmultiOverSelect"></div>
										</div>
										<div id="editcheckboxes"></div>
									</div>
									<span class="error-block icglabel_selectkids"
										id="editGuardianKids_err"></span>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-groups">
									<label for="editGuardianContactDetails"
										class="control-label icglabel_contactno"> </label> <input
										type="number" class="form-control-user"
										id="editGuardianContactDetails"
										name="editGuardianContactDetails"
										onKeyPress="if(this.value.length==10) return false;" required
										title="Please enter Contact" tabindex="2" /><span
										class="error-block icglabel_contact_empty"
										id="editGuardianContactDetails_err"></span><span
										class="error-block icglabel_contact_error"
										id="editGuardianContactDetailsStartOfContactInvalid"></span>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="row">
				<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
					<div class="form-group pull-right">
						<button type="button" class="confirm icglabel_update"
							id="updateGuradian" data-dismiss="modal"></button>
					</div>
				</div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
					<div class="form-group pull-left">
						<button type="button" class="modal-Cancel icglabel_cancel"
							data-dismiss="modal"></button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- page script -->
<script type="text/javascript">
	$('.guardian').removeClass("treeview").addClass("active");
	$('.guardian').removeClass("font-medium-roboto").addClass("font-bold-roboto");
	$("#sGuardianIcon").attr("src",
			"resources/images/sidemenu_icon/white/guardian_w.png");
</script>
