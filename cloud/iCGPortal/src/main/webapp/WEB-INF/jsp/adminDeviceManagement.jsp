<%@include file="includes/taglib.jsp"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglable_devicemanagement"></h1>
		</section>
		<section>
			<div class="navbar navbar-default">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle"
						data-target="#super-navbar" data-toggle="collapse">
						<span class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
				</div>
				<div class="navbar-collapse collapse" id="super-navbar">
					<ul class="nav navbar-nav">
						<li><a class="active tabs hand icglabel_managedevices"
							id="adminManageDevices"></a></li>
						<li><a class="tabs hand icglabel_deviceconfiguration"
							id="adminDeviceConfig"></a></li>
						<li><a class="tabs hand icglabel_fota" id="adminFota"></a></li>
					</ul>
				</div>
			</div>
		</section>
		<!-- body  content -->
		<section class="content">
			<section id="adminDeviceManageDevices">
				<section class="content-header-parent">
					<p class="bread_heading_parent sub_title icglabel_adddevice"></p>
				</section>
				<div class="row">
					<div class="col-md-8">
						<section class="user_account_mangmet addDeviceMgtPanel">
							<form id="addNewDeviceForm" method="POST" action="/addNewDevice/"
								novalidate="novalidate">
								<div class="row">
									<div class="col-md-8 text-center txt-12">
										<strong> <span class="alert" id="addDeviceSuccess"
											style="display: none"> <strong><font
													color="green" class="icglabel_create_school_success"></font></strong>
										</span> <span class="alert" id="addDeviceFailure"
											style="display: none"> <strong><font
													color="red" class="icglabel_create_school_fail"></font></strong>
										</span></strong>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-groups">
											<label for="adminDeviceModal"
												class="control-label icglabel_devicemodel"></label> <input
												type="text" class="form-control-user" id="adminDeviceModal"
												name="adminDeviceModal" required title="Please enter modal"
												tabindex="1" maxlength="45" /> <span
												class="error-block icglabel_devicemodel_empty"
												id="adminDeviceModal_err"></span>
										</div>
									</div>
									<div class="col-md-6">
										<div class="form-group">
											<label for="adminDeviceCountyFilter1"
												class="control-label icglabel_county"></label> <select
												tabindex="12" class="form-control-user"
												id="adminDeviceCountyFilter1" onchange="loadSchoolNames1()">
												<option value='' class="icglabel_schoolcounty"></option>
												<c:forEach items="${countyList}" var="county">
													<option>${county}</option>
												</c:forEach>
											</select><span class="error-block icglabel_county_empty"
												id="adminSchoolCounty_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-groups">
											<label for="adminDeviceUUID"
												class="control-label icglabel_uuidno"></label> <input
												type="text" class="form-control-user" id="adminDeviceUUID"
												name="adminDeviceUUID" required title="Please enter UUID"
												tabindex="2" maxlength="36" /><span
												class="error-block icglabel_uuid_empty"
												id="adminDeviceUUID_err"></span>
										</div>
									</div>
									<div class="col-md-6" style="">
										<div class="form-groups">
											<label for="adminDeviceCountyFilter1"
												class="control-label icglabel_schoolname"></label> <select
												tabindex="13" class="form-control-user"
												id="adminDeviceSchoolId">
												<option value='' class="icglabel_schoolname"></option>
											</select><span class="error-block icglabel_schoolname_empty"
												id="adminSchoolName_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="adminDeviceFirmware"
												class="control-label icglabel_firmwareversion"></label><input
												type="text" class="form-control-user"
												id="adminDeviceFirmware" name="adminDeviceFirmware" required
												title="Please enter Firmware Version" tabindex="3"
												maxlength="45" /> <span
												class="error-block icglabel_firmware_version_empty"
												id="adminDeviceFirmware_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
										<input type="button" class="save form-group create"
											value="Create" id="adminDeviceCreate" tabindex="8" />
									</div>
									<div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
										<input type="reset" class="cancel form-group" value="Cancel"
											id="adminDeviceCancel" tabindex="9" />
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
										<a href="resources/templates/devices.csv" tabindex="9"
											class="font-medium-roboto txt-14 icglabel_downloadhere"></a>
										<span class="icglabel_tomanagedevicecreationtemplate"></span>
									</p>
								</div>
							</section>
							<section class="small_section">
								<p class="section-header third_subtitle icglabel_Upload"></p>
								<hr style="border-top: 1px solid black;" />
								<form method="POST" action="deviceUploadFile"
									enctype="multipart/form-data">
									<input type="hidden" name="token" value="${sessionID}">
									<div class="font-light-roboto">
										<div class="text-center txt-12">
											<strong><font color="green"><span
													id="create_device_success" class="icglabel_csvuploadmsg"
													style="display: none;" data-success="${csvuploadmsg}"></span></font></strong>
											<strong><font color="green"><span
													class="txt-12 text-center" id="deviceRecordCount">${deviceRecordCount}</span></font></strong>
											<strong><font color="red"><span
													class="txt-12 icglabel_invalidcsverrormsg"
													id="device_invalidcsverror" style="display: none;"
													data-error="${csvInvalidMsg}"></span></font></strong> <strong><font
												color="red" class="text-center txt-12"> <span
													id="devices_csverror" class="icglabel_csverrormsg"
													style="display: none;"></span>
											</font></strong>
										</div>
										<c:if test="${fn:length(ignoredList) > 1 }">
											<div class="text-center">
												<a href="#" data-toggle="modal" id="deviceignoredList"
													data-target="#ignoredDetailsModal"
													class="text-center txt-12 icglabel_errorDetails"
													onclick="mergeCategoryInDetails($(this))"
													data-keyboard="true" error_details='${ignoredList}'></a>
											</div>
										</c:if>
										<div class="user_account_mangmet">
											<p class="text-center">
												<input id="csvFileUpload" type="file" name="file"
													accept=".csv" class="inputfile"
													onchange="showFileName(this)"> <label
													for="csvFileUpload"><span
													class="font-medium-roboto txt-14 icglabel_browsehere"></span><label
													class="icglabel_uploaddevicecsv"> </label></label>
											</p>
										</div>
										<div class="text-center">
											<button type="submit"
												class="save form-group text-center icglabel_Upload"
												value="Upload" style="padding: 9px 15px;"
												id="adminDeviceUpload" disabled></button>
										</div>
									</div>
								</form>
							</section>
						</section>
					</div>
				</div>
				<section class="content-header-parent">
					<div class="row sub_title">
						<div class="col-md-4 col-sm-4 col-xs-4"
							style="padding-left: 21px;">
							<p class="bread_heading_parent icglabel_devicelist"></p>
						</div>
						<div
							class="col-md-4 col-sm-4 col-xs-4 col-md-offset-3 col-sm-offset-1 filter-device-tabs"
							style="">
							<p class="bread_heading_parent">
								<input type="text" class="form-control-user input-bottom"
									id="adminDeviceUUIDforFind" name="adminDeviceUUIDFilter"
									title="Please enter UUID" tabindex="9" />
							</p>
						</div>
						<div class="col-md-1 col-sm-3 col-xs-3" style="">
							<p class="bread_heading_parent">
								<input type="button"
									class="save form-group text-center btn-actMgt" value="Find"
									id="adminDevicefindTeacher" tabindex="14"
									onclick="disableNotification();getDeviceDetails();" />
							</p>
						</div>
					</div>
				</section>
				<section class="user_account_mangmet">
					<div id="searchDevice_update_success"
						style="text-align: center; display: none">
						<strong><font color="green"
							class=" txt-12 icglabel_searchDevice_update_success"></font></strong>
					</div>
					<div id="searchDevice_update_failure"
						style="text-align: center; display: none">
						<strong><font color="red"
							class="txt-12 icglabel_searchDevice_update_failure"></font></strong>
					</div>
					<div id="searchDevice_delete_success"
						style="text-align: center; display: none">
						<strong><font color="green"
							class="txt-12 icglabel_searchDevice_delete_success"></font></strong>
					</div>
					<div id="searchDevice_delete_failure"
						style="text-align: center; display: none">
						<strong><font color="red"
							class="txt-12 icglabel_searchDevice_delete_failure"></font></strong>
					</div>
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th class="icglabel_uuid"></th>
									<th class="icglabel_schoolname"></th>
									<th class="icglabel_firmwareversion"></th>
									<th class="icglabel_devicemodel"></th>
									<th class="icglabel_addeddate"></th>
									<th class="icglabel_status"></th>
									<th class="icglabel_edit"></th>
									<th class="icglabel_delete"></th>
								</tr>
							</thead>
							<tbody id="deviceDataSection">
								<c:if test="${ empty deviceList || deviceList.size() == 0 }">
									<tr>
										<td align="center" colspan="8"><strong><span
												class="icglabel_nodata"></span></strong></td>
									</tr>
								</c:if>
								<c:forEach items="${deviceList}" var="device">
									<tr>
										<c:if test="${empty device.uuid}">
											<td><span class="icglabel_datamissing"></span></td>
										</c:if>
										<c:if test="${not empty device.uuid}">
											<td><span>${device.uuid}</span></td>
										</c:if>
										<c:if test="${empty device.schoolName}">
											<td><span class="icglabel_datamissing"></span></td>
										</c:if>
										<c:if test="${not empty device.schoolName}">
											<td><span class="noneditable">${device.schoolName}</span></td>
										</c:if>
										<c:if test="${empty device.firmWareVersion}">
											<td><span class="icglabel_datamissing"></span></td>
										</c:if>
										<c:if test="${not empty device.firmWareVersion}">
											<td><span class="noneditable">${device.firmWareVersion}</span></td>
										</c:if>
										<c:if test="${empty device.deviceModel}">
											<td><span class="icglabel_datamissing"></span></td>
										</c:if>
										<c:if test="${not empty device.deviceModel}">
											<td><span class="noneditable">${device.deviceModel}</span></td>
										</c:if>
										<c:if test="${empty device.status}">
											<td><span class="icglabel_datamissing"></span></td>
										</c:if>
										<c:if test="${not empty device.status}">
											<td class="mail">${device.status}</td>
										</c:if>
										<c:if test="${empty device.addedDate}">
											<td><span class="icglabel_datamissing"></span></td>
										</c:if>
										<c:if test="${not empty device.addedDate}">
											<td><span class="nonedita/ble">${device.addedDate}</span></td>
										</c:if>
										<td class="editIcon"><a class="teacherEditbtn"
											style="color: black"><img onclick=""
												assetAdminDeviceModal="${device.deviceModel}"
												assetAdminDeviceFirmware="${device.firmWareVersion}"
												assetAdminDeviceUUID="${device.uuid}"
												assetAdminDeviceID="${device.deviceid}"
												assetAdminDeviceConfigID="${device.deviceConfigurationID}"
												assetAdminDeviceStatus="${device.status}"
												class="upgrade_icon"
												src="resources/images/unselected_edit_icon.png"
												data-toggle="modal" data-keyboard="true"
												data-target="#editTeacherDetails" /></a></td>
										<td class="deleteIcon"><a style="color: black" href=""><img
												src="resources/images/Delete_icon.png" /></a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</section>
				<div class="modal fade" id="deleteDeviceDetails" role="dialog">
					<p hidden="hidden">
						<input type="text" id="device_id">
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
											class="icglabel_deleteDeviceconirmation"></span>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2"></div>
									<div class="col-md-4">
										<div class="form-group">
											<button type="button" class="confirm icglabel_ok"
												data-dismiss="modal" tabindex="1"
												onclick="deleteDeviceDetails()"></button>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<button type="button" tabindex="2"
												class="modal-Cancel icglabel_cancel" data-dismiss="modal"></button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<section class="content-header-parent">
					<div class="row sub_title">
						<div class="col-lg-4 col-md-3 col-sm-12 col-xs-12"
							style="padding-left: 21px;">
							<p class="bread_heading_parent icglabel_devicestatisticslist"></p>
						</div>
						<div
							class="col-lg-2 col-md-2 col-sm-6 col-xs-6 col-lg-offset-2 col-md-offset-1"
							style="">
							<p class="bread_heading_parent">
								<select tabindex="12" class="grade grade-width"
									id="adminDeviceCountyFilter" onchange="loadSchoolNames()">
									<option value='' class="icglabel_schoolcounty"></option>
									<c:forEach items="${countyList}" var="county">
										<option>${county}</option>
									</c:forEach>
								</select>
							</p>
						</div>
						<div
							class="col-lg-2 col-md-2 col-sm-6 col-xs-6 col-lg-offset-0 col-md-offset-1"
							style="">
							<p class="bread_heading_parent">
								<select tabindex="13" class="grade grade-width"
									id="adminDeviceSchoolNameFilter">
									<option value='' class="icglabel_schoolname"></option>
								</select>
							</p>
						</div>
						<div
							class="col-lg-1 col-md-1 col-sm-4 col-xs-4 col-lg-offset-0 col-md-offset-1"
							style="">
							<p class="bread_heading_parent">
								<input type="button"
									class="save form-group text-center btn-actMgt" value="Go"
									id="adminDeviceGo" tabindex="14"
									onclick="getDeviceStatDetails(1)" />
							</p>
						</div>
					</div>
				</section>
				<section class="user_account_mangmet">
					<div id="brokenDevice_delete_success" class="txt-12 text-center"
						style="display: none">
						<strong><font color="green"
							class="icglabel_brokenDevice_delete_success"></font></strong>
					</div>
					<div id="brokenDevice_delete_failure" class="txt-12 text-center"
						style="display: none">
						<strong><font color="red"
							class="icglabel_brokenDevice_delete_failure"></font></strong>
					</div>
					<div id="returnedDevice_delete_success" class="txt-12 text-center"
						style="display: none">
						<strong><font color="green"
							class="icglabel_returnedDevice_delete_success"></font></strong>
					</div>
					<div id="returnedDevice_delete_failure" class="txt-12 text-center"
						style="display: none">
						<strong><font color="red"
							class="icglabel_returnedDevice_delete_failure"></font></strong>
					</div>
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th class="icglabel_schoolname"></th>
									<th class="icglabel_assigned"></th>
									<th class="icglabel_unassigned"></th>
									<th class="icglabel_Broken"></th>
									<th class="icglable_returned"></th>
								</tr>
							</thead>
							<tbody id="deviceStats">
								<tr>
									<td align="center" colspan="5"><strong><span
											class="icglabel_nodata"></span></strong></td>
								</tr>
							</tbody>
						</table>
					</div>
				</section>
			</section>
			<!--end of Manage Device-->
			<!--start of Device Config-->
			<section id="adminDeviceConfig-section" style="display: none">
				<div class="row">
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<p class="bread_heading_parent icglable_thresholdlimit"></p>
					</div>
					<div
						class="col-lg-8 col-md-8 col-sm-8 col-xs-8">
						<p class="bread_heading_parent pull-right">
							<select tabindex="12" class="grade device-filter"
								id="adminDeviceThreshholdDeviceFilter"
								onchange="loadDeviceConfigDetails()">
								<option value="" class="icglabel_devicemodel"></option>
								<c:forEach items="${deviceModelList}" var="deviceModel">
									<option value="${deviceModel.config_id}">${deviceModel.model}</option>
								</c:forEach>
							</select>
						</p>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<section class="user_account_mangmet deviceConfigPanel">
							<form id="addDeviceConfigForm" method="POST"
								action="/addDeviceConfig/" novalidate="novalidate">
								<div class="row">
									<div class="col-md-8 text-center txt-12">
										<strong> <span class="alert"
											id="configDetialsSuccess" style="display: none"> <strong><font
													color="green" class="icglabel_update_success"></font></strong>
										</span> <span class="alert" id="configDetialsFailure"
											style="display: none"> <strong><font
													color="red" class="icglabel_update_fail"></font></strong>
										</span> <input type="hidden" class="form-control-user"
											id="adminDeviceConfigID" name="adminDeviceConfigID" required /></strong>
									</div>
								</div>
								<div class="row">
									<div class="col-lg-4 col-md-5 col-sm-12 col-xs-12">
										<div class="form-groups">
											<label for="adminDeviceConfigBattery"
												class="control-label icglabel_lowbaterry"></label> <select
												tabindex="1" class="form-control-user"
												id="adminDeviceConfigBattery" disabled>
												<option value='' class="icglabel_select"></option>
												<c:forEach items="${lowatteryList}" var="lowbattery">
													<option value="${lowbattery}">${lowbattery}</option>
												</c:forEach>
											</select> <span class="error-block icglabel_lowbaterry_empty"
												id="adminDeviceConfigBattery_err"></span>
										</div>
									</div>
									<div class="col-lg-4 col-md-5 col-sm-12 col-xs-12">
										<div class="form-group">
											<label for="adminDeviceConfigSelfTesting"
												class="control-label icglabel_deviceselftesting"></label> <select
												tabindex="2" class="form-control-user"
												id="adminDeviceConfigSelfTesting" disabled>
												<option value='' class="icglabel_select"></option>
												<c:forEach items="${deviceSelfList}" var="deviceself">
													<option value="${deviceself}">${deviceself}</option>
												</c:forEach>
											</select> <span class="error-block icglabel_deviceselftesting_empty"
												id="adminDeviceConfigSelfTesting_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-lg-4 col-md-5 col-sm-12 col-xs-12">
										<div class="form-groups">
											<label for="adminDeviceConfigReportFrequency"
												class="control-label icglabel_gpsreportfrequency"></label> <select
												tabindex="3" class="form-control-user"
												id="adminDeviceConfigReportFrequency" disabled>
												<option value='' class="icglabel_select"></option>
												<c:forEach items="${gpsReportList}" var="gpsreport">
													<option value="${gpsreport}">${gpsreport}</option>
												</c:forEach>
											</select> <span class="error-block icglabel_gpsreportfrequency_empty"
												id="adminDeviceConfigReportFrequency_err"></span>
										</div>
									</div>
									<div class="col-lg-4 col-md-5 col-sm-12 col-xs-12">
										<div class="form-groups">
											<label for="adminDeviceConfigSyncFrequency"
												class="control-label icglabel_datasyncfrequency"></label> <select
												tabindex="4" class="form-control-user"
												id="adminDeviceConfigSyncFrequency" disabled>
												<option value='' class="icglabel_select"></option>
												<c:forEach items="${dataSyncList}" var="datasync">
													<option value="${datasync}">${datasync}</option>
												</c:forEach>
											</select> <span class="error-block icglabel_datasyncfrequency_empty"
												id="adminDeviceConfigSyncFrequency_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
										<input type="button" class="save form-group" value="Save"
											id="adminDeviceConfigSave" tabindex="5" disabled />
									</div>
									<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
										<input type="reset" class="cancel form-group" value="Cancel"
											id="adminDeviceConfigCancel" tabindex="6"
											onclick="resetDevice()" />
									</div>
								</div>
							</form>
						</section>
					</div>
				</div>
			</section>
			<!--end of Device Config-->
			<!--start of FOTA-->
			<section id="adminDeviceFota-section" style="display: none">
				<div class="row">
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<p class="bread_heading_parent icglabel_uploadnewfirmware"></p>
					</div>
					<div class="col-lg-8 col-md-8 col-sm-8 col-xs-8"
						>
						<p class="bread_heading_parent pull-right">
							<select tabindex="12" class="grade device-filter"
								id="adminDeviceFirmwareDeviceFilter"
								onchange="deviceFOTADetails()">
								<option value='' class="icglabel_devicemodel"></option>
								<c:forEach items="${deviceModelList}" var="deviceModel">
									<option value="${deviceModel.model}">${deviceModel.model}</option>
								</c:forEach>
							</select>
						</p>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<section class="user_account_mangmet">
							<form id="addFOTAForm">
								<div class="row">
									<div class="col-md-8 text-center txt-12">
										<strong> <span class="alert" id="fotaSuccess"
											style="display: none"> <strong><font
													color="green" class="icglabel_upload_success"></font></strong>
										</span> <span class="alert" id="fotaFailure" style="display: none">
												<strong><font color="red"
													class="icglabel_upload_failure"></font></strong>
										</span></strong>
									</div>
								</div>
								<div class="row">
									<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12">
										<div class="form-groups">
											<label for="adminDeviceFotaName"
												class="control-label icglable_firmwarename"></label> <input
												type="text" class="form-control-user"
												id="adminDeviceFotaName" name="adminDeviceFotaName"
												title="Please enter Name" tabindex="1" disabled /> <span
												class="error-block icglabel_firmware_name_empty"
												id="adminDeviceFotaName_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12">
										<div class="form-groups">
											<label for="adminDeviceFotaVersion"
												class="control-label icglable_version"></label> <input
												type="text" class="form-control-user"
												id="adminDeviceFotaVersion" name="adminDeviceFotaVersion"
												title="Please enter Version" disabled tabindex="1" /> <span
												class="error-block icglabel_firmware_version_empty"
												id="adminDeviceFotaVersion_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12">
										<div class="form-groups">
											<label for="adminDeviceFotaFile"
												class="control-label icglable_choosefile"></label>
											<div class='input-group'>
												<input type="text" id="choosedfile"
													class="form-control-user" title="Please Choose the file"
													readonly disabled /><input type="file" class="hidden"
													id="adminDeviceFotaFile" name="adminDeviceFotaFile"
													onchange="showFileNameInsideInput(this, 'choosedfile')"
													accept=".zip"><span class="input-group-btn">
													<button type="button" class="browse-file icglabel_browse"
														id="choosefile" disabled tabindex="3"></button>
												</span>
											</div>
											<span class="error-block icglabel_firmware_file_empty"
												id="adminDeviceFotaFile_err"></span> <span
												class="error-block icglabel_firmware_file_not_supported"
												id="adminDeviceFotaFile_NOT_supported_err"></span>
										</div>
									</div>
								</div>
								<div class="row">
									<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
										<button id="deviceFOTASave" disabled
											class="save form-group icglabel_save" tabindex="5"></button>
									</div>
									<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
										<input type="reset" class="cancel form-group" value="Cancel"
											id="adminDeviceFOTACancel" tabindex="6" onclick="resetFOTA()" />
									</div>
								</div>
							</form>
						</section>
					</div>
				</div>
				<section class="content-header-parent">
					<div class="row">
						<div class="col-md-4 col-sm-4 col-xs-4"
							style="padding-left: 21px;">
							<p class="bread_heading_parent icglable_firmwarelist"></p>
						</div>
					</div>
				</section>
				<section class="user_account_mangmet">
					<div id="fota_delete_success" class="txt-12 text-center"
						style="display: none">
						<strong><font color="green"
							class="icglabel_fota_delete_success"></font></strong>
					</div>
					<div id="fota_delete_failure" class="txt-12 text-center"
						style="display: none">
						<strong><font color="red"
							class="icglabel_fota_delete_failure"></font></strong>
					</div>
					<div class="table-responsive">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th class="icglable_version"></th>
									<th class="icglabel_name"></th>
									<th class="icglable_releasedate"></th>
									<th class="icglable_size"></th>
									<th class="icglabel_download"></th>
									<th class="icglabel_delete"></th>
								</tr>
							</thead>
							<tbody id="deviceConfigurationList">
								<tr>
									<td align="center" colspan="5"><strong><span
											class="icglabel_nodata"></span></strong></td>
								</tr>
							</tbody>
							<tbody id="deviceConfigurationList">
							</tbody>
						</table>
					</div>
					<span id="pagination" class="pagination" style="display: none">
						<span class="col-md-8"> <span class="icglabel_page"></span>&nbsp;<span
							id="deviceConfig_startPage" style="text-align: left;"></span> <span
							class="icglabel_of" style="text-align: left;"></span> <span
							id="deviceConfig_endPage" style="text-align: left;"></span>
					</span> <span class="col-md-4"><span class="pull-right"><span
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
			<!--end of Device Config-->
		</section>
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
			<form id="editAddDeviceForm" method="POST"
				action="/editAddDeviceForm/" novalidate="novalidate">
				<div class="modal-body">
					<div class="row">
						<div class="col-md-8 text-center txt-12">
							<strong> <span class="alert" id="editDeviceSuccess"
								style="display: none"> <strong><font
										color="green" class="icglabel_update_success"></font></strong>
							</span> <span class="alert" id="editDeviceFailure" style="display: none">
									<strong><font color="red" class="icglabel_update_fail"></font></strong>
							</span> <input type="hidden" class="form-control-user" id="editDeviceId"
								name="editDeviceId" /> <input type="hidden"
								class="form-control-user" id="editDeviceConfigId"
								name="editDeviceConfigId" /></strong>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6">
							<div class="form-groups">
								<label for="editAdminDeviceModal"
									class="control-label icglabel_devicemodel"></label> <input
									type="text" class="form-control-user" id="editAdminDeviceModal"
									name="editAdminDeviceModal" required title="Please enter modal"
									tabindex="1" /> <span
									class="error-block icglabel_devicemodel_empty"
									id="editAdminDeviceModal_err"></span>
							</div>
						</div>
						<div class="col-md-6">
							<div class="form-group">
								<label for="editAdminDeviceFirmware"
									class="control-label icglabel_firmwareversion"></label><input
									type="text" class="form-control-user"
									id="editAdminDeviceFirmware" name="editAdminDeviceFirmware"
									required title="Please enter Firmware Version" tabindex="3" />
								<span class="error-block icglabel_firmware_version_empty"
									id="editAdminDeviceFirmware_err"></span>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6">
							<div class="form-groups">
								<label for="editAdminDeviceUUID"
									class="control-label icglabel_uuidno"></label> <input
									type="text" class="form-control-user" id="editAdminDeviceUUID"
									name="editAdminDeviceUUID" required title="Please enter UUID"
									tabindex="2" /><span class="error-block icglabel_uuid_empty"
									id="editAdminDeviceUUID_err"></span>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-2 col-lg-2 col-sm-1 col-xs-1"></div>
					<div class="col-md-4 col-lg-4 col-sm-5 col-xs-4">
						<div class="form-group">
							<button type="button" class="confirm icglabel_update"
								data-dismiss="modal" id="editAdminDeviceUpdate"></button>
						</div>
					</div>
					<div class="col-md-4 col-lg-4 col-sm-5 col-xs-4">
						<div class="form-group">
							<button type="button" class="modal-Cancel icglabel_cancel"
								data-dismiss="modal" id="editAdminDeviceCancel"></button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>


<div class="modal fade" id="deleteDeviceBrokenDetails" role="dialog">
	<p hidden="hidden">
		<input type="text" id="schoolAccount_id">
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
							class="icglabel_deleteDeviceconirmation"></span>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" class="confirm icglabel_ok"
								data-dismiss="modal" tabindex="1"
								onclick="deleteDeviceFromAccount()"></button>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" tabindex="2"
								class="modal-Cancel icglabel_cancel" data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="deleteDeviceReturnedDetails" role="dialog">
	<p hidden="hidden">
		<input type="text" id="schooldeviceAccount_id">
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
							class="icglabel_deleteDeviceconirmation"></span>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" class="confirm icglabel_ok"
								data-dismiss="modal" tabindex="1"
								onclick="deleteDeviceFromAccountReturned()"></button>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" tabindex="2"
								class="modal-Cancel icglabel_cancel" data-dismiss="modal"></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="deleteDeviceConfigDetails" role="dialog">
	<p hidden="hidden">
		<input type="text" id="deviceConfig_id">
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
							class="icglabel_deleteDeviceConfigconirmation"></span>
					</div>
				</div>
				<div class="row">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" class="confirm icglabel_ok"
								data-dismiss="modal" tabindex="1"
								onclick="deleteDeviceConfigDetails()"></button>
						</div>
					</div>
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
						<div class="form-group">
							<button type="button" tabindex="2"
								class="modal-Cancel icglabel_cancel" data-dismiss="modal"></button>
						</div>
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
						<textarea class="form-control" id="editcategoryIn_id" rows="10"
							name="schoolAnnouncementDescription" tabindex="2" disabled> </textarea>
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
<script src="resources/js/adminDeviceManagement.js"></script>