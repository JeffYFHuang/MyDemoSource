
<%@include file="includes/taglib.jsp"%>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/fmt"%>
<input type="hidden" id="generic_allergies" value="${generic_allergies}">
<script src="resources/js/kidsProfile.js"></script>
<div class="content-wrapper">
	<p hidden="hidden">
		<input type="text" id="student_id">
	</p>
	<section class="content-header">
		<h1 class="icglabel_kidsprofile"></h1>
		<ol class="breadcrumb-new" id="bread">
			<li><a href="parentdashboard?token=${sessionID}" id="dashboard"
				class="icglabel_dashboard"></a><i class="fa fa-chevron-right"
				aria-hidden="true"></i></li>
			<li><a id="kidsSlected"></a><i class="fa fa-chevron-right"
				aria-hidden="true"></i></li>
			<li><a href="#" class="icglabel_kidsprofile"></a></li>
		</ol>
	</section>
	<section>
		<div class="navbar navbar-default minlen-li">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle"
					data-target="#kidsnavbar" data-toggle="collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
			</div>
			<div class="navbar-collapse collapse" id="kidsnavbar">
				<ul class="nav navbar-nav">
					<li><a class="addKid" id="kid0" href="#"><span
							class="icglabel_addkid"></span></a></li>
				</ul>
			</div>
		</div>
	</section>

	<!-- body  content -->

	<section class="content" id="unlink_success"
		style="display: none; min-height: 20px !important">
		<div class="alert alert-dismissible"
			style="background-color: #d2d6de !important">
			<label class="control-label icglabel_unlink_success"></label>
		</div>
	</section>
	<section class="content" id="link_success"
		style="display: none; min-height: 20px !important">
		<div class="alert alert-dismissible"
			style="background-color: #d2d6de !important">
			<label class="control-label icglabel_link_success"></label>
		</div>
	</section>
	<section class="content" id="deviceSectionLink"
		style="display: none; min-height: 185px !important">
		<div class="content_switchedon_wrapper">
			<label for="deviceUuidLink"
				class="control-label sub_title icglabel_deviceuuid"></label>
			<div class="row">
				<div class="col-md-6">
					<div class="form-groups">
						<input type="text" class="form-control-user" id="deviceUuidLink"
							name="deviceUuidLink" required title="Please enter device UUID"
							tabindex="10" maxlength="36" /> <span class="help-block"></span><span
							class="error-block txt-12 icglabel_invalidUUID" id="invalidUUID"></span><span
							class="success-block txt-12 icglabel_validUUID" id="validUUID"></span>
					</div>
				</div>
				<div class="col-md-6">
					<div id="form-groups">
						<input type="button" class="btn btn-md btn-unlink" value="Link"
							id="addLinkBtn">
					</div>
				</div>
			</div>
		</div>
	</section>
	<section class="content" style="display: block" id="kidssection"
		style="display:none">
		<section class="user_account_mangmet">
			<div class="row" id="studentsRow">
				<div class="col-md-4">
					<span class="third_subtitle icglabel_studentname"></span>
					<hr />
					<span class="font-light-roboto txt-14" id="name"></span>
				</div>
				<div class="col-md-4">
					<span class="third_subtitle icglabel_classrollnumber"></span>
					<hr />
					<span class="font-light-roboto txt-14" id="classRollNo"></span>
				</div>
				<div class="col-md-4">
					<span class="third_subtitle icglabel_registrationnumber"></span>
					<hr />
					<span class="font-light-roboto txt-14" id="regNo"></span>
				</div>
			</div>
		</section>
		<section class="content-header-parent">
			<p class="bread_heading_parent"></p>
		</section>
		<section class="user_account_mangmet">
			<div class="row text-center txt-12 icglabel_updatesuccess"
				style="display: none; color: green !important; font-weight: bold;"></div>
			<div class="row text-center txt-12 icglabel_updatefail"
				style="display: none; color: red !important; font-weight: bold;"></div>
			<div class="row">
				<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
					<div class="form-groups">
						<label for="kidsname"
							class="control-label icglabel_studentnickname"></label> <input
							tabindex="1" type="text" class="form-control-user" id="kidsname"
							name="studentname" required title="Please enter kids name" /> <span
							class="error-block icglabel_kidname_err" id="kidname_err"></span>
					</div>
				</div>
				<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
					<div class="form-groups">
						<label for="kidsheight" class="control-label icglabel_heightcm"></label>
						<input type="number" maxlength="3" class="form-control-user"
							id="kidsheight" name="kidsweight" required
							title="Please enter kids height" tabindex="2" /><span
							class="error-block icglabel_kidheight_err" id="kidheight_err"></span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
					<div class="form-groups">
						<label for="kidsDob" class="control-label icglabel_dateofbirth"></label>
						<div class='input-group kidsprofile-date' id='date'
							data-date-format="YYYY-MM-DD">
							<input type='text' class="form-control-user dateInput"
								tabindex="6" id="inputDate" name="inputDate" /><img
								src="resources/images/calender.png"
								class="form-control-feedback calender-img" alt="" /> <span
								class="error-block icglabel_kidDob_err" id="kidDob_err"></span>
						</div>
					</div>
				</div>
				<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
					<div class="form-group">
						<label for="kidsContact"
							class="control-label icglabel_emergencycontact"></label> <input
							class="form-control-user contactnumber" type="number"
							maxlength="10" required id="kidsContact" name="kidsContact"
							tabindex="7" /><span class="error-block icglabel_kidcontact_err"
							id="kidcontact_err"></span><span
							class="error-block icglabel_kidcontact_formaterr"
							id="startOfContactInvalid"></span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
					<div class="form-group">
						<label for="kidsgender" class="control-label icglabel_gender"></label>
						<select tabindex="5" class="form-control-user" id="kidsgender">
							<option value="Select the Gender" class="icglabel_selectgender"></option>
							<option value="MALE" class="icglabel_malegender"></option>
							<option value="FEMALE" class="icglabel_femalegender"></option>
						</select> <span class="error-block icglabel_kidgender_err"
							id="kidgender_err"></span>
					</div>
				</div>
				<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12"
					style="z-index: 100; height: 90px;">
					<div class="form-group">
						<label for="kidsAllergy"
							class="control-label icglabel_allergiesforiwps"></label>
						<div class="multiselect" style="background-color: white;">
							<div tabindex="4" class="selectBox">
								<select class="form-control-user up_arrow" id="kidsAllergy">
									<option class="icglabel_select"></option>
								</select>
								<div class="overSelect" id="multiOverSelect"></div>
							</div>
							<div id="checkboxes"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
					<div class="form-group">
						<label for="kidsweight" class="control-label icglabel_weightkg"></label>
						<input type="number" maxlength="3" class="form-control-user"
							id="kidsweight" name="kidsweight" required
							title="Please enter kids weight" tabindex="3" /><span
							class="error-block icglabel_kidweight_err" id="kidweight_err"></span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
					<input type="button" class="save form-group icglabel_save"
						value="Save" id="save" tabindex="8" />
				</div>
				<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
					<input type="button" id="reset"
						class="cancel form-group icglabel_cancel" value="Cancel"
						tabindex="9" />
				</div>
			</div>
		</section>
		<section class="content-header-parent">
			<p class="bread_heading_parent"></p>
		</section>
	</section>
	<section class="content" id="deviceSectionUnlink">
		<div class="content_switchedon_wrapper">
			<label for="deviceUuidUnlink"
				class="control-label sub_title icglabel_deviceuuid"></label>
			<div class="row">
				<div class="col-md-6">
					<div class="form-groups">
						<input type="text" class="form-control-user" id="deviceUuidUnlink"
							name="deviceUuidUnlink" required title="Please enter device UUID"
							tabindex="10" readonly />
					</div>
				</div>
				<div class="col-md-6">
					<div id="form-groups">
						<input type="button" class="btn btn-md btn-unlink" value="Unlink"
							id="unlinkBtn" data-toggle="modal" data-target="#myModal"
							tabindex="11" />
					</div>
				</div>
			</div>
		</div>
	</section>
	<!-- Modal -->
	<div class="modal fade" id="myModal" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">
						<img src="resources/images/alert_icon.png" class="img-responsive"
							alt="alert" style="float: left;" /><span
							class="heading font-reg-roboto txt-24 icglabel_unlinkdeviceuuid"></span>
					</h4>
				</div>
				<div class="modal-body">
					<p class="font-medium-roboto txt-14 icglabel_unlinkmsg"></p>
				</div>
				<div class="">
					<div class="row" style="padding: 15px !important">
						<div class="col-lg-2 col-md-2 col-sm-1 col-xs-1"></div>
						<div class="col-lg-4 col-md-4 col-sm-5 col-xs-5">
							<button type="submit" class="confirm icglabel_confirm"
								data-dismiss="modal" id="cnfrmUnlinkBtn"></button>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-5 col-xs-5">
							<button type="button" class="modal-Cancel icglabel_cancel"
								data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- content_switchedon_wrapper closed -->
</div>