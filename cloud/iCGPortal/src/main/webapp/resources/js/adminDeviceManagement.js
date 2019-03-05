var token_id = $("#token_id").val();
var deviceConfigstart = 0;
var deviceConfigend = 0;

$(document)
.ready(
		function() {
			if($('#create_device_success').attr('data-success') != ''){
				$('#create_device_success').css("display", "block");
			}
			if($('#device_invalidcsverror').attr('data-error') != ''){
				$('#device_invalidcsverror').css("display", "block");
			} 


			$('.devicemanage').removeClass("treeview").addClass(
			"active");
			$('.devicemanage').removeClass("font-medium-roboto").addClass("font-bold-roboto");
			$("#sdeviceManageIcon")
			.attr("src",
			"resources/images/SuperAdminSideBarIcons/White_Icon/Device_managment.png");

			$("#configDetialsSuccess").css("display", "none");
			$("#configDetialsFailure").css("display", "none");
			$("#fotaSuccess").css("display", "none");
			$("#fotaFailure").css("display", "none");
			$("#fota_delete_success").css({"display":"none"});
			$("#fota_delete_failure").css({"display":"none"});
			$("#editDeviceSuccess").css("display", "none");
			$("#editDeviceFailure").css("display", "none");
			$("#addDeviceSuccess").css("display", "none");
			$("#addDeviceFailure").css("display", "none");
			$("#searchDevice_update_success").css({"display":"none"});
			$("#searchDevice_update_failure").css({"display":"none"});
			$("#searchDevice_delete_success").css({"display":"none"});
			$("#searchDevice_delete_failure").css({"display":"none"});
			$("#brokenDevice_delete_success").css({"display":"none"});
			$("#brokenDevice_delete_failure").css({"display":"none"});
			$("#returnedDevice_delete_success").css({"display":"none"});
			$("#returnedDevice_delete_failure").css({"display":"none"});
			//getDeviceConfigList();

			$('#adminDeviceUpload').attr('disabled', true).css('opacity', '0.6');
			$('input:file').change(function() {
				var fileExtension = 'csv';
				if ($(this).val().split('.').pop().toLowerCase() == fileExtension) {
					fileContentsCheck($("#csvFileUpload")[0].files[0], 'devices', 'devices_csverror', 'adminDeviceUpload');
					$("#create_device_success").css({"display":"none"});
					$("#deviceRecordCount").css({"display":"none"});
					$("#device_invalidcsverror").css({"display":"none"});
					$("#deviceignoredList").css({"display":"none"});
				}else{
					$('#adminDeviceUpload').attr('disabled', true).css('opacity', '0.6');
				}
			});
		});

$(function() {
	$("#adminManageDevices").click(function() {
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		$("#adminDeviceManageDevices").css({
			"display" : "block"
		});
		$("#adminDeviceConfig-section").css({
			"display" : "none"
		});
		$("#adminDeviceFota-section").css({
			"display" : "none"
		});
		$("#configDetialsSuccess").css("display", "none");
		$("#configDetialsFailure").css("display", "none");
		$("#addDeviceSuccess").css("display", "none");
		$("#addDeviceFailure").css("display", "none");
		$("#searchDevice_update_success").css({"display":"none"});
		$("#searchDevice_update_failure").css({"display":"none"});
		$("#searchDevice_delete_success").css({"display":"none"});
		$("#searchDevice_delete_failure").css({"display":"none"});
		$("#brokenDevice_delete_success").css({"display":"none"});
		$("#brokenDevice_delete_failure").css({"display":"none"});
		$("#returnedDevice_delete_success").css({"display":"none"});
		$("#returnedDevice_delete_failure").css({"display":"none"});
		$("#fotaSuccess").css("display", "none");
		$("#fotaFailure").css("display", "none");
		$("#fota_delete_success").css({"display":"none"});
		$("#fota_delete_failure").css({"display":"none"});
	});
	$("#adminDeviceConfig").click(function() {
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		$("#adminDeviceManageDevices").css({
			"display" : "none"
		});
		$("#adminDeviceConfig-section").css({
			"display" : "block"
		});
		$("#adminDeviceFota-section").css({
			"display" : "none"
		});
		$("#configDetialsSuccess").css("display", "none");
		$("#configDetialsFailure").css("display", "none");
		$("#addDeviceSuccess").css("display", "none");
		$("#addDeviceFailure").css("display", "none");
		$("#searchDevice_update_success").css({"display":"none"});
		$("#searchDevice_update_failure").css({"display":"none"});
		$("#searchDevice_delete_success").css({"display":"none"});
		$("#searchDevice_delete_failure").css({"display":"none"});
		$("#brokenDevice_delete_success").css({"display":"none"});
		$("#brokenDevice_delete_failure").css({"display":"none"});
		$("#returnedDevice_delete_success").css({"display":"none"});
		$("#returnedDevice_delete_failure").css({"display":"none"});
		$("#fotaSuccess").css("display", "none");
		$("#fotaFailure").css("display", "none");
		$("#fota_delete_success").css({"display":"none"});
		$("#fota_delete_failure").css({"display":"none"});
	});
	$("#adminFota").click(function() {
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		$("#adminDeviceFota-section").css({
			"display" : "block"
		});
		$("#adminDeviceConfig-section").css({
			"display" : "none"
		});
		$("#adminDeviceManageDevices").css({
			"display" : "none"
		});
		$("#configDetialsSuccess").css("display", "none");
		$("#configDetialsFailure").css("display", "none");
		$("#addDeviceSuccess").css("display", "none");
		$("#addDeviceFailure").css("display", "none");
		$("#searchDevice_update_success").css({"display":"none"});
		$("#searchDevice_update_failure").css({"display":"none"});
		$("#searchDevice_delete_success").css({"display":"none"});
		$("#searchDevice_delete_failure").css({"display":"none"});
		$("#brokenDevice_delete_success").css({"display":"none"});
		$("#brokenDevice_delete_failure").css({"display":"none"});
		$("#returnedDevice_delete_success").css({"display":"none"});
		$("#returnedDevice_delete_failure").css({"display":"none"});
		$("#fotaSuccess").css("display", "none");
		$("#fotaFailure").css("display", "none");
		$("#fota_delete_success").css({"display":"none"});
		$("#fota_delete_failure").css({"display":"none"});
	});
	$("#choosefile").click(function(){
		$("#adminDeviceFotaFile").trigger('click');
	});

	$('.upgrade_icon').bind("click", function(event) {
		displayUpgrade($(this));
	});

	function displayUpgrade(assetObj) {
		document.getElementById("editAdminDeviceUUID").disabled = false;
		$("#editAdminDeviceModal").val(assetObj.attr('assetAdminDeviceModal'));
		$("#editAdminDeviceFirmware").val(
				assetObj.attr('assetAdminDeviceFirmware'));
		$("#editAdminDeviceUUID").val(assetObj.attr('assetAdminDeviceUUID'));
		$("#editDeviceId").val(assetObj.attr('assetAdminDeviceID'));
		$("#editDeviceConfigId").val(assetObj.attr('assetAdminDeviceConfigID'));
		var status = assetObj.attr('assetAdminDeviceStatus');
		if(status =='assigned')
			document.getElementById("editAdminDeviceUUID").disabled = true;
	}

});

function displayUpgrade(assetObj) {
	document.getElementById("editAdminDeviceUUID").disabled = false;
	$("#editAdminDeviceModal").val(assetObj.attr('assetAdminDeviceModal'));
	$("#editAdminDeviceFirmware")
	.val(assetObj.attr('assetAdminDeviceFirmware'));
	$("#editAdminDeviceUUID").val(assetObj.attr('assetAdminDeviceUUID'));
	$("#editDeviceId").val(assetObj.attr('assetAdminDeviceID'));
	$("#editDeviceConfigId").val(assetObj.attr('assetAdminDeviceConfigID'));
	var status = assetObj.attr('assetAdminDeviceStatus');
	if(status =='assigned')
		document.getElementById("editAdminDeviceUUID").disabled = true;
}

$(function() {
	$("#editAdminDeviceUpdate")
	.on(
			"click",
			function(e) {

				var deviceModel = $('#editAdminDeviceModal').val();
				var firmWareVersion = $('#editAdminDeviceFirmware')
				.val();
				var uuid = $('#editAdminDeviceUUID').val();
				var deviceconfigId = $("#editDeviceConfigId").val();
				var deviceId = $("#editDeviceId").val();

				var isValidForm = true;

				if (deviceModel.trim() === ''
					|| deviceModel.trim().length == 0) {

					$("#editAdminDeviceModal_err").css("display",
					"block");
					e.preventDefault();
					isValidForm = false;
				}

				if (firmWareVersion.trim() == ''
					|| firmWareVersion.trim().length === 0) {

					$("#editAdminDeviceFirmware_err").css("display",
					"block");
					e.preventDefault();
					isValidForm = false;
				}

				if (uuid.trim() == '' || uuid.trim().length === 0) {

					$("#editAdminDeviceUUID_err").css("display",
					"block");
					e.preventDefault();
					isValidForm = false;
				}

				if (isValidForm) {

					var requestData = {
							"deviceModel" : deviceModel,
							"firmWareVersion" : firmWareVersion,
							"uuid" : uuid,
							"deviceid" : deviceId,
							"deviceConfigurationID" : deviceconfigId
					};

					makePostAjaxRequest("updateDeviceDetails/"
							+ token_id, requestData,
							onSuccessDeviceUpdate,
							onFailureDeviceUpdate);
				} else {
					e.preventDefault();
					return false;
				}
			});
	function onSuccessDeviceUpdate(successResponseData) {
		//window.location.reload();
		$("#addDeviceSuccess").css({"display":"none"});
		$("#addDeviceFailure").css({"display":"none"});
		$("#searchDevice_update_success").css({"display":"block"});
		$("#searchDevice_update_failure").css({"display":"none"});
		$("#searchDevice_delete_success").css({"display":"none"});
		$("#searchDevice_delete_failure").css({"display":"none"});
		$("#brokenDevice_delete_success").css({"display":"none"});
		$("#brokenDevice_delete_failure").css({"display":"none"});
		$("#returnedDevice_delete_success").css({"display":"none"});
		$("#returnedDevice_delete_failure").css({"display":"none"});
		getDeviceDetails();
	}

	function onFailureDeviceUpdate(failureResponseData) {
		$("#addDeviceSuccess").css({"display":"none"});
		$("#addDeviceFailure").css({"display":"none"});
		$("#searchDevice_update_success").css({"display":"none"});
		$("#searchDevice_update_failure").css({"display":"block"});
		$("#searchDevice_delete_success").css({"display":"none"});
		$("#searchDevice_delete_failure").css({"display":"none"});
		$("#brokenDevice_delete_success").css({"display":"none"});
		$("#brokenDevice_delete_failure").css({"display":"none"});
		$("#returnedDevice_delete_success").css({"display":"none"});
		$("#returnedDevice_delete_failure").css({"display":"none"});
		debugLogs(failureResponseData);
	}
});

$(function() {
	$("#adminDeviceCreate")
	.on(
			"click",
			function(e) {
				$("#adminDeviceModal_err").css("display", "none");
				$("#adminDeviceFirmware_err").css("display", "none");
				$("#adminSchoolName_err").css("display", "none");
				$("#adminDeviceUUID_err").css("display", "none");
				$("#adminSchoolCounty_err").css("display", "none");

				var deviceModel = $('#adminDeviceModal').val();
				var firmWareVersion = $('#adminDeviceFirmware').val();
				var uuid = $('#adminDeviceUUID').val();
				var county = $('#adminDeviceCountyFilter1').val();
				var schoolId = $("#adminDeviceSchoolId").val();

				var isValidForm = true;

				if (deviceModel.trim() === ''
					|| deviceModel.trim().length == 0) {

					$("#adminDeviceModal_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				} else {
					if (deviceModel.trim().length > 45) {
						$("#adminDeviceModal_err1").css("display",
						"block");
						e.preventDefault();
						isValidForm = false;
					}
				}

				if (firmWareVersion.trim() == ''
					|| firmWareVersion.trim().length === 0) {

					$("#adminDeviceFirmware_err").css("display",
					"block");
					e.preventDefault();
					isValidForm = false;
				}
				
				if (county.trim() == ''
					|| county.trim().length === 0 || county == 0) {

					$("#adminSchoolCounty_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (schoolId.trim() == ''
					|| schoolId.trim().length === 0 || schoolId == 0) {

					$("#adminSchoolName_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (uuid.trim() == '' || uuid.trim().length === 0) {

					$("#adminDeviceUUID_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (isValidForm) {

					var requestData = {
							"deviceModel" : deviceModel,
							"firmWareVersion" : firmWareVersion,
							"uuid" : uuid,
							"schoolId" : schoolId

					};

					makePostAjaxRequest("createDeviceDetails/"
							+ token_id, requestData,
							onSuccessDeviceCreate,
							onFailureDeviceCreate);
				} else {
					e.preventDefault();
					return false;
				}
			});
	function onSuccessDeviceCreate(successResponseData) {
		//window.location.reload();
		var msg =  successResponseData.Return.ResponseSummary.StatusCode;
		if(msg== "ERR35")
		{
			$("#addDeviceFailure").css("display", "block");
			$("#addDeviceSuccess").css("display", "none");
			$("#searchDevice_update_success").css({"display":"none"});
			$("#searchDevice_update_failure").css({"display":"none"});
			$("#searchDevice_delete_success").css({"display":"none"});
			$("#searchDevice_delete_failure").css({"display":"none"});
			$("#brokenDevice_delete_success").css({"display":"none"});
			$("#brokenDevice_delete_failure").css({"display":"none"});
			$("#returnedDevice_delete_success").css({"display":"none"});
			$("#returnedDevice_delete_failure").css({"display":"none"});
		}
		else
		{
			$("#addDeviceFailure").css("display", "none");
			$("#addDeviceSuccess").css("display", "block");
			$("#searchDevice_update_success").css({"display":"none"});
			$("#searchDevice_update_failure").css({"display":"none"});
			$("#searchDevice_delete_success").css({"display":"none"});
			$("#searchDevice_delete_failure").css({"display":"none"});
			$("#brokenDevice_delete_success").css({"display":"none"});
			$("#brokenDevice_delete_failure").css({"display":"none"});
			$("#returnedDevice_delete_success").css({"display":"none"});
			$("#returnedDevice_delete_failure").css({"display":"none"});
		}
		$('#adminDeviceModal').val("");
		$('#adminDeviceFirmware').val("");
		$('#adminDeviceUUID').val("");
		$('#adminDeviceCountyFilter1').val("Select County to list Schools(tc)");
		$("#adminDeviceSchoolId").val("0");

	}

	function onFailureDeviceCreate(failureResponseData) {
		debugLogs(failureResponseData);
		$("#addDeviceFailure").css("display", "block");
	}
});

function getDeviceStatDetails(bool) {

	var county = $('#adminDeviceCountyFilter').val();

	var schoolName = $('#adminDeviceSchoolNameFilter').val();
	if (schoolName > 0) {
		if(!isDataNull(bool)){
			$("#brokenDevice_delete_success").css({"display":"none"});
			$("#brokenDevice_delete_failure").css({"display":"none"});
			$("#returnedDevice_delete_success").css({"display":"none"});
			$("#returnedDevice_delete_failure").css({"display":"none"});
		}

		makeGetAjaxRequest("getDeviceStatDetails?token=" + token_id
				+ "&county=" + county + "&schoolName=" + schoolName, false,
				onSuccess, onFailure);
	}
}

function onSuccess(data) {
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#deviceStats').text('');
		var len = data.Return.Results.finalList.length;

		for (var count = 0; count < len; count++) {
			var SchoolName = data.Return.Results.finalList[count].SchoolName;
			var schoolId = data.Return.Results.finalList[count].schoolId;
			var assigend = data.Return.Results.finalList[count].assigend;
			var unassigend = data.Return.Results.finalList[count].unassigend;
			var broken = data.Return.Results.finalList[count].broken;
			var returned = data.Return.Results.finalList[count].returned;
			var rowData = '';

			if (SchoolName != undefined) {
				rowData = '<tr>' + '<td>' + SchoolName + '</td>' + '<td>'
				+ assigend + '</td>' + '<td>' + unassigend + '</td>';
				if (broken > 0)
					rowData +='<td class="deleteIcon">'
						+ broken+ '&nbsp;<img title="Download Broken Device UUID\'s" class="hand" src="resources/images/Download.png" onClick="document.location.href=\'downloadBrokenDeviceUUIDs?token=' + token_id + '&school_id=' + schoolId + '\';" />' 
						+ '<a title="Delete Broken Device UUID\'s" style="color: black" data-toggle="modal" data-target="#deleteDeviceBrokenDetails">'
						+ '<img onclick="deleteBrokenDevice($(this))" school_AccountId="'
						+ schoolId
						+ '"  src="resources/images/Delete_icon.png" class="hand" /></a></td>';
				else
					rowData += '<td>' + broken + '</td>';
				if (returned > 0)
					rowData +='<td class="deleteIcon">'
						+ returned + '&nbsp;<img title="Download Returned Device UUID\'s" class="hand" src="resources/images/Download.png" onClick="document.location.href=\'downloadReturnedDeviceUUIDs?token=' + token_id + '&school_id=' + schoolId + '\';" />' 
						+ '<a title="Delete Returned Device UUID\'s" style="color: black" data-toggle="modal" data-target="#deleteDeviceReturnedDetails">'
						+ '<img onclick="deleteReturnedDevice($(this))" school_DeviceAccountId="'
						+ schoolId
						+ '"  src="resources/images/Delete_icon.png" class="hand" /></a></td>';
				else
					rowData += '<td>' + returned + '</td>';

				rowData += '</tr>';
			} else {
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				rowData = '<tr><td align="center" colspan="6"><strong>' + nodataText + '</strong></td></tr>';
			}
			$("#deviceStats").append(rowData);
		}

	} else {

	}
}

function onFailure(data) {
	debugLogs('failed');
}

function disableNotification(){
	$("#searchDevice_update_success").css({"display":"none"});
	$("#searchDevice_update_failure").css({"display":"none"});
	$("#searchDevice_delete_success").css({"display":"none"});
	$("#searchDevice_delete_failure").css({"display":"none"});
}
function getDeviceDetails() {
	var uuid = $('#adminDeviceUUIDforFind').val();
	if (uuid.length > 0){
		makeGetAjaxRequest("getDeviceDetails?token=" + token_id + "&uuid="
				+ uuid, false, onDeviceSuccess, onDeviceFailure);
	}else{
		$('#searchDeviceUuid').text($("#editAdminDeviceUUID").val());
		$('#searchDeviceFirmware').text($("#editAdminDeviceFirmware").val());
		$('#searchDeviceModel').text($("#editAdminDeviceModal").val());
	}
}

function onDeviceSuccess(data) {
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#deviceDataSection').text('');
		var len = data.Return.Results.finalList.length;

		for (var count = 0; count < len; count++) {
			var uuid = data.Return.Results.finalList[count].uuid;
			var schoolName = data.Return.Results.finalList[count].schoolName;
			var firmWareVersion = data.Return.Results.finalList[count].firmWareVersion;
			var deviceModel = data.Return.Results.finalList[count].deviceModel;
			var addedDate = data.Return.Results.finalList[count].addedDate;
			var status = data.Return.Results.finalList[count].status;
			var deviceid = data.Return.Results.finalList[count].deviceid;
			var deviceConfigid = data.Return.Results.finalList[count].deviceconfigid;

			if(status == ""){
				status ='unAssigend';
			}
			var rowData = '';
			if (uuid != undefined) {
				rowData = '<tr>' + '<td id="searchDeviceUuid">'
				+ uuid
				+ '</td>'
				+ '<td>'
				+ schoolName
				+ '</td>'
				+ '<td id="searchDeviceFirmware">'
				+ firmWareVersion
				+ '</td>'
				+ '<td id="searchDeviceModel">'
				+ deviceModel
				+ '</td>'
				+ '<td>'
				+ addedDate
				+ ' </td>'
				+ '<td>'
				+ status
				+ ' </td>'
				+ '<td class="editIcon"><a class="teacherEditbtn"'
				+ 'style="color: black"><img onclick=""'
				+ 'assetAdminDeviceModal="'
				+ deviceModel
				+ '"'
				+ 'assetAdminDeviceFirmware="'
				+ firmWareVersion
				+ '"'
				+ 'assetAdminDeviceUUID="'
				+ uuid
				+ '" '
				+ 'assetAdminDeviceID="'
				+ deviceid
				+ '"'
				+ 'assetAdminDeviceStatus="'
				+ status
				+ '"'
				+ 'assetAdminDeviceConfigID="'
				+ deviceConfigid
				+ '"'
				+ 'class="upgrade_icon hand"'
				+ 'src="resources/images/unselected_edit_icon.png"'
				+ 'data-toggle="modal" data-keyboard="true"'
				+ 'data-target="#editTeacherDetails" /></a></td>';
				if(status != 'assigned'){
					rowData += '<td class="deleteIcon"><a style="color: black" data-toggle="modal" data-target="#deleteDeviceDetails"> '
						+ '<img onclick="deleteDevice($(this))" device_Id="'
						+ deviceid
						+ '"  src="resources/images/Delete_icon.png" class="hand" /></a></td>';
				}else{
					rowData += '<td class="deleteIcon">-</td>';
				}
				rowData += '</tr>';			
			} else {
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				rowData = '<tr><td align="center" colspan="8"><strong>' + nodataText + '</strong></td></tr>';
			}
			$("#deviceDataSection").append(rowData);
		}

		$('.upgrade_icon').bind("click", function(event) {
			displayUpgrade($(this));
		});

	} else {

	}
}
function onDeviceFailure(data) {
	debugLogs('failed');
}

function loadSchoolNames() {
	var county = $('#adminDeviceCountyFilter').val();

	makeGetAjaxRequest("getSchoolDetails?token=" + token_id + "&county="
			+ county, false, onSchoolSuccess, onSchoolFailure);

}

function onSchoolSuccess(data) {

	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#adminDeviceSchoolNameFilter').html('');
		$('#adminDeviceSchoolNameFilter').append($('<option>', {
			value : 0,
			text : 'Select School'
		}));
		var len = data.Return.Results.finalList.length;

		for (var count = 0; count < len; count++) {
			var school_id = data.Return.Results.finalList[count].school_id;
			var schoolName = data.Return.Results.finalList[count].schoolName;

			if (school_id != undefined) {
				$('#adminDeviceSchoolNameFilter').append($('<option>', {
					value : school_id,
					text : schoolName
				}));
			}
		}

	} else {

	}
}
function onSchoolFailure(data) {
	debugLogs('failed');
}

function loadSchoolNames1() {
	var county = $('#adminDeviceCountyFilter1').val();

	makeGetAjaxRequest("getSchoolDetails?token=" + token_id + "&county="
			+ county, false, onSchoolDeviceSuccess, onSchoolDeviceFailure);

}

function onSchoolDeviceSuccess(data) {
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#adminDeviceSchoolId').html('');
		$('#adminDeviceSchoolId').append($('<option>', {
			value : 0,
			text : 'Select School'
		}));

		var len = data.Return.Results.finalList.length;

		for (var count = 0; count < len; count++) {
			var school_id = data.Return.Results.finalList[count].school_id;
			var schoolName = data.Return.Results.finalList[count].schoolName;

			if (school_id != undefined) {
				$('#adminDeviceSchoolId').append($('<option>', {
					value : school_id,
					text : schoolName
				}));
			}
		}

	} else {

	}
}
function onSchoolDeviceFailure(data) {
	debugLogs('failed');
}

function loadDeviceConfigDetails() {
	if($('#adminDeviceThreshholdDeviceFilter')[0].selectedIndex == ""){
		$('#adminDeviceConfigSyncFrequency')[0].selectedIndex = "";
		$('#adminDeviceConfigBattery')[0].selectedIndex = "";
		$('#adminDeviceConfigSelfTesting')[0].selectedIndex = "";
		$('#adminDeviceConfigReportFrequency')[0].selectedIndex = "";
		resetDevice();
	}else{
		$('#adminDeviceConfigBattery').removeAttr('disabled');
		$('#adminDeviceConfigSelfTesting').removeAttr('disabled');
		$('#adminDeviceConfigReportFrequency').removeAttr('disabled');
		$('#adminDeviceConfigSyncFrequency').removeAttr('disabled');
		$('#adminDeviceConfigSave').removeAttr('disabled');
		var deviceConfigId = $('#adminDeviceThreshholdDeviceFilter').val();

		makeGetAjaxRequest("getDeviceConfigDetails?token=" + token_id
				+ "&deviceConfigId=" + deviceConfigId, false,
				onDeviceConfigSuccess, onDeviceConfigFailure);
	}
}
function deviceFOTADetails(){
	if($('#adminDeviceFirmwareDeviceFilter')[0].selectedIndex == ""){
		$('#adminDeviceFotaName').val('');
		$('#adminDeviceFotaVersion').val('');
		$('#adminDeviceFotaFile').val('');
		$('#choosedfile').val('');
		resetFOTA();
	}else{
		$('#adminDeviceFotaName').removeAttr('disabled');
		$('#adminDeviceFotaVersion').removeAttr('disabled');
		$('#choosedfile').removeAttr('disabled');
		$('#choosefile').removeAttr('disabled');
		$('#deviceFOTASave').removeAttr('disabled');
		$("#fotaFailure").css({"display":"none"});
		$("#fotaSuccess").css({"display":"none"});
		$("#fota_delete_success").css({"display":"none"});
		$("#fota_delete_failure").css({"display":"none"});
		$("#adminDeviceFotaName_err").css({"display" : "none"});
		$("#adminDeviceFotaVersion_err").css({"display" : "none"});
		$("#adminDeviceFotaFile_err").css({"display" : "none"});
		getDeviceConfigList();
	}

}
function resetFOTA(){
	$("#fotaFailure").css({"display":"none"});
	$("#fotaSuccess").css({"display":"none"});
	$("#fota_delete_success").css({"display":"none"});
	$("#fota_delete_failure").css({"display":"none"});
	$('#adminDeviceFirmwareDeviceFilter')[0].selectedIndex = '';
	$('#adminDeviceFotaName').attr('disabled','disabled');
	$('#adminDeviceFotaVersion').attr('disabled','disabled');
	$('#choosefile').attr('disabled','disabled');
	$('#choosedfile').attr('disabled','disabled');
	$('#deviceFOTASave').attr('disabled','disabled');
	$("#adminDeviceFotaName_err").css({"display" : "none"});
	$("#adminDeviceFotaVersion_err").css({"display" : "none"});
	$("#adminDeviceFotaFile_err").css({"display" : "none"});
	$("#adminDeviceFotaFile_NOT_supported_err").css({
		"display" : "none"
	});
	$("#deviceConfigurationList").empty();
	$("#pagination").css({
		"display" : "none"
	});
	
	var nodataText = getValueByLanguageKey('icglabel_nodata');
	rowData = '<tr><td align="center" colspan="6"><strong>' + nodataText + '</strong></td></tr>';
	$("#deviceConfigurationList").append(rowData);
}

function resetDevice(){
	$("#configDetialsSuccess").css({"display":"none"});
	$("#configDetialsFailure").css({"display":"none"});
	$('#adminDeviceThreshholdDeviceFilter')[0].selectedIndex = "";
	$('#adminDeviceConfigBattery').attr('disabled','disabled');
	$('#adminDeviceConfigSelfTesting').attr('disabled','disabled');
	$('#adminDeviceConfigReportFrequency').attr('disabled','disabled');
	$('#adminDeviceConfigSyncFrequency').attr('disabled','disabled');
	$('#adminDeviceConfigSave').attr('disabled','disabled');
}

function onDeviceConfigSuccess(data) {
	$("#configDetialsSuccess").css("display", "none");
	$("#configDetialsFailure").css("display", "none");
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		var len = data.Return.Results.finalList.length;
		for (var count = 0; count < len; count++) {
			var lowbattery = data.Return.Results.finalList[count].lowbaatery;
			var gpsreportfreequency = data.Return.Results.finalList[count].gpsreportfreequency;
			var wearablefreequency = data.Return.Results.finalList[count].wearablefreequency;
			var deviceselftesting = data.Return.Results.finalList[count].deviceselftesting;
			var deviceConfigID = data.Return.Results.finalList[count].deviceConfigId;

			$("#adminDeviceConfigBattery").val(lowbattery);
			$("#adminDeviceConfigReportFrequency").val(gpsreportfreequency);
			$("#adminDeviceConfigSyncFrequency").val(wearablefreequency);
			$("#adminDeviceConfigSelfTesting").val(deviceselftesting);
			$("#adminDeviceConfigID").val(deviceConfigID);
		}
	} 
}
function onDeviceConfigFailure(data) {
	debugLogs('Inside onDeviceConfigFailure');
}

$(function() {
	$("#adminDeviceConfigSave")
	.on(
			"click",
			function(e) {
				$("#adminDeviceConfigBattery_err").css("display",
				"none");
				$("#adminDeviceConfigReportFrequency_err").css(
						"display", "none");
				$("#adminDeviceConfigSyncFrequency_err").css("display",
				"none");
				$("#adminDeviceConfigSelfTesting_err").css("display",
				"none");

				var lowBattery = $("#adminDeviceConfigBattery").val()
				.trim();
				var reportFreequency = $(
				"#adminDeviceConfigReportFrequency").val()
				.trim();
				var dataSyncFreequency = $(
				"#adminDeviceConfigSyncFrequency").val().trim();
				var deviceselfTesting = $(
				"#adminDeviceConfigSelfTesting").val().trim();
				var configID = $("#adminDeviceConfigID").val().trim();

				var isValidForm = true;

				if (lowBattery === '' || lowBattery.length == 0) {
					$("#adminDeviceConfigBattery_err").css("display",
					"block");
					e.preventDefault();
					isValidForm = false;
				}

				if (reportFreequency == ''
					|| reportFreequency.length === 0) {

					$("#adminDeviceConfigReportFrequency_err").css(
							"display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (dataSyncFreequency == ''
					|| dataSyncFreequency.length === 0) {

					$("#adminDeviceConfigSyncFrequency_err").css(
							"display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (deviceselfTesting == ''
					|| deviceselfTesting.length === 0) {

					$("#adminDeviceConfigSelfTesting_err").css(
							"display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (configID == '' || configID.length === 0) {
					debugLogs('Model ID Not Found');
					e.preventDefault();
					isValidForm = false;
				}

				if (isValidForm) {
					var requestData = {
							"lowBattery" : lowBattery,
							"gepReportFreequency" : reportFreequency,
							"dataSyncFreequency" : dataSyncFreequency,
							"deviceselftesting" : deviceselfTesting,
							"deviceConfigId" : configID

					};
					makePostAjaxRequest("updateDeviceConfigDetails/"
							+ token_id, requestData,
							onSuccessDeviceConfigupdate,
							onFailureDeviceConfigUpdate);
				}
			});

	function onSuccessDeviceConfigupdate(successResponseData) {
		$("#configDetialsSuccess").css("display", "block");
	}
	function onFailureDeviceConfigUpdate(failureResponseData) {
		$("#configDetialsFailure").css("display", "block");
	}
});

function increaseassigendHref() {
	var currentpage = deviceConfigstart;
	currentpage++;
	var selectedModelValue = $( "#adminDeviceFirmwareDeviceFilter" ).val();
	if (currentpage <= deviceConfigend) {
		deviceConfigstart = currentpage;
		makeGetAjaxRequest("getDeviceConfigListDetails?selectedModelValue="+ selectedModelValue + "&token=" + token_id+"&pageid=" + currentpage, false, ongetDeviceConfigListDetailsSuccess, ongetDeviceConfigListDetailsFailure);
	}
}

function decreaseassigendHref() {
	var currentpage = deviceConfigstart;
	currentpage--;
	var selectedModelValue = $( "#adminDeviceFirmwareDeviceFilter" ).val();
	if (currentpage > 0) {
		deviceConfigstart = currentpage;
		makeGetAjaxRequest("getDeviceConfigListDetails?selectedModelValue="+ selectedModelValue + "&token=" + token_id+"&pageid=" + currentpage, false, ongetDeviceConfigListDetailsSuccess, ongetDeviceConfigListDetailsFailure);
	}

}

function getDeviceConfigList() {
	var page_id = 1;
	var selectedModelValue = $( "#adminDeviceFirmwareDeviceFilter" ).val();
	console.log("selectedVal :::::::::::: "+selectedModelValue);
	makeGetAjaxRequest("getDeviceConfigListDetails?selectedModelValue="+selectedModelValue+"&token=" + token_id+"&pageid=" + page_id, false, ongetDeviceConfigListDetailsSuccess, ongetDeviceConfigListDetailsFailure);

}

function ongetDeviceConfigListDetailsSuccess(data) {
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$("#fotaFailure").css("display", "none");
		$('#deviceConfigurationList').text('');
		$("#adminDeviceFotaVersion").val('');
		$("#adminDeviceFotaFile").val('');
		$("#adminDeviceFotaName").val('');
		$('#choosedfile').val('')
		var len = data.Return.Results.finalList.length;
		if(len > 0){
			$("#pagination").css({
				"display" : "block"
			});
		}
		if(len === 0){
			$("#pagination").css({
				"display" : "none"
			});
			
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			rowData = '<tr><td align="center" colspan="6"><strong>' + nodataText + '</strong></td></tr>';
			$("#deviceConfigurationList").append(rowData);
			
		}else{
		for (var count = 0; count < len; count++) {
			var config_id = data.Return.Results.finalList[count].config_id;
			var firmwareVersion = data.Return.Results.finalList[count].firmwareVersion;
			var description = data.Return.Results.finalList[count].description;
			var relasedDate = data.Return.Results.finalList[count].relasedDate;
			var Size = data.Return.Results.finalList[count].Size;
			var filename = data.Return.Results.finalList[count].fileMame;
			var associatedDecviceCount = data.Return.Results.finalList[count].associatedDecviceCount;
			deviceConfigend = data.Return.Results.finalList[count].noofPages;
			deviceConfigstart = data.Return.Results.finalList[count].currentPage;

			var rowData = '';

			rowData = '<tr>'
				+ '<td>'
				+ firmwareVersion
				+ '</td>'
				+ '<td>'
				+ description
				+ '</td>'
				+ '<td>'
				+ relasedDate
				+ '</td>'
				+ '<td>'
				+ Size
				+ '</td>';
			if(!isDataNull(filename))
				rowData	+='<td><span class="noneditable hand"><a href="'+filename+'" ><img src="resources/images/Download.png" /></a></span></td>';
			else
				rowData	+='<td><span class="noneditable hand"><img src="resources/images/Download.png" /></span></td>';
			if(isDataNull(associatedDecviceCount) || associatedDecviceCount == 0)
				rowData	+='<td class="deleteIcon"><a style="color: black" data-toggle="modal" data-target="#deleteDeviceConfigDetails"> '
					+ '<img onclick="deleteDeviceConfig($(this))" device_ConfigId="'
					+ config_id
					+ '" src="resources/images/Delete_icon.png" class="hand" /></a></td></tr>';
			else
				rowData	+='<td> - </td></tr>';

			$("#deviceConfigurationList").append(rowData);
		}
		document.getElementById('deviceConfig_startPage').innerHTML = deviceConfigstart;
		document.getElementById('deviceConfig_endPage').innerHTML = deviceConfigend;
		}

	} else {
		$("#pagination").css({
			"display" : "none"
		});
		
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		rowData = '<tr><td align="center" colspan="6"><strong>' + nodataText + '</strong></td></tr>';
		$("#deviceConfigurationList").append(rowData);

	}
}
function ongetDeviceConfigListDetailsFailure(data) {
	debugLogs('failed');
}

$(function() {
	$("#addFOTAForm").submit(
			function(e) {
				e.preventDefault();
				$("#fotaSuccess").css({"display":"none"});
				$("#fotaFailure").css({"display":"none"});
				var ver = $("#adminDeviceFotaVersion").val();
				var file = $("#adminDeviceFotaFile").val();
				var desc = $("#adminDeviceFotaName").val();
				var model = $("#adminDeviceFirmwareDeviceFilter").val();
				var validData = true;

				$("#adminDeviceFotaName_err").css({
					"display" : "none"
				});
				$("#adminDeviceFotaVersion_err").css({
					"display" : "none"
				});
				$("#adminDeviceFotaFile_err").css({
					"display" : "none"
				});
				$("#adminDeviceFotaFile_NOT_supported_err").css({
					"display" : "none"
				});

				if (isDataNull(desc)) {
					$("#adminDeviceFotaName_err").css({
						"display" : "block"
					});
					validData = false;
				}
				if (isDataNull(ver)) {
					$("#adminDeviceFotaVersion_err").css({
						"display" : "block"
					});
					validData = false;
				}
				if (isDataNull(file)) {
					$("#adminDeviceFotaFile_err").css({
						"display" : "block"
					});
					validData = false;
				}else{
					var requestData = new FormData($(this)[0]);
					var fileObj = $(this)[0].adminDeviceFotaFile.files[0];
					var sFileName = fileObj.name.toLowerCase();
					var sFileExtension = sFileName.split('.')[sFileName.split('.').length - 1].toLowerCase();
					if(fileObj.size > FOTAImgMaximumUploadSize || sFileExtension != "zip"){
						$("#adminDeviceFotaFile_NOT_supported_err").css({
							"display" : "block"
						});
						validData = false;
					}
				}

				if(validData){
					var requestData = new FormData(this);
					makePostFileAjaxRequest("DeviceConfigCreate/" + token_id
							+ "/" + model, requestData,
							onSuccessDeviceConfigCreate,
							onFailureDeviceConfigCreate);
				}else{
					hideSpinnerNow();
				}
			});

	function onSuccessDeviceConfigCreate(data) {

		var msg =  data.Return.ResponseSummary.StatusCode;
		if(msg== "ERR19")
		{
			$("#adminDeviceFotaFile_NOT_supported_err").css({
				"display" : "block"
			});
		}
		else
		{
			$("#adminDeviceFotaName_err").css({
				"display" : "none"
			});
			$("#adminDeviceFotaVersion_err").css({
				"display" : "none"
			});
			$("#adminDeviceFotaFile_err").css({
				"display" : "none"
			});
			$("#adminDeviceFotaFile_NOT_supported_err").css({
				"display" : "none"
			});
			$("#fotaFailure").css("display", "none");
			$("#fotaSuccess").css("display", "block");
			$("#fota_delete_success").css({"display":"none"});
			$("#fota_delete_failure").css({"display":"none"});
			$('#adminDeviceFotaFile').val('');
			$('#choosedfile').val('');
			$('#adminDeviceFotaVersion').val('');
			$("#adminDeviceFotaName").val('');
			getDeviceConfigList();
		}

		
	}
	function onFailureDeviceConfigCreate(data) {
		$("#fotaSuccess").css("display", "none");
		$("#fotaFailure").css("display", "block");
		$("#fota_delete_success").css({"display":"none"});
		$("#fota_delete_failure").css({"display":"none"});
	}
});

function deleteDevice(usrObj) {
	var device_id = usrObj.attr('device_Id');
	$("#device_id").val(device_id);
}

function deleteDeviceDetails() {
	var device_id = $('#device_id').val();
	makeGetAjaxRequest("deleteDeviceApi/" + token_id + "/" + device_id,
			false, onDeleteDeviceSuccess, onDeleteDeviceFailure);
}
function onDeleteDeviceSuccess(){
	$("#addDeviceSuccess").css({"display":"none"});
	$("#addDeviceFailure").css({"display":"none"});
	$("#searchDevice_update_success").css({"display":"none"});
	$("#searchDevice_update_failure").css({"display":"none"});
	$("#searchDevice_delete_success").css({"display":"block"});
	$("#searchDevice_delete_failure").css({"display":"none"});
	$("#brokenDevice_delete_success").css({"display":"none"});
	$("#brokenDevice_delete_failure").css({"display":"none"});
	$("#returnedDevice_delete_success").css({"display":"none"});
	$("#returnedDevice_delete_failure").css({"display":"none"});
	$('#deviceDataSection').text('');
}

function onDeleteDeviceFailure(){
	$("#addDeviceSuccess").css({"display":"none"});
	$("#addDeviceFailure").css({"display":"none"});
	$("#searchDevice_update_success").css({"display":"none"});
	$("#searchDevice_update_failure").css({"display":"none"});
	$("#searchDevice_delete_success").css({"display":"none"});
	$("#searchDevice_delete_failure").css({"display":"block"});
	$("#brokenDevice_delete_success").css({"display":"none"});
	$("#brokenDevice_delete_failure").css({"display":"none"});
	$("#returnedDevice_delete_success").css({"display":"none"});
	$("#returnedDevice_delete_failure").css({"display":"none"});
}

function deleteBrokenDevice(usrObj) {
	var school_id = usrObj.attr('school_AccountId');
	$("#schoolAccount_id").val(school_id);
}

function deleteDeviceFromAccount() {
	var school_id = $('#schoolAccount_id').val();
	makeGetAjaxRequest("deleteBrokenDeviceApi/" + token_id + "/" + school_id,
			false, onDeleteBrokenDeviceSuccess, onDeleteBrokenDeviceFailure);
}
function onDeleteBrokenDeviceSuccess(){ 
	$("#addDeviceSuccess").css({"display":"none"});
	$("#addDeviceFailure").css({"display":"none"});
	$("#searchDevice_update_success").css({"display":"none"});
	$("#searchDevice_update_failure").css({"display":"none"});
	$("#searchDevice_delete_success").css({"display":"none"});
	$("#searchDevice_delete_failure").css({"display":"none"});
	$("#brokenDevice_delete_success").css({"display":"block"});
	$("#brokenDevice_delete_failure").css({"display":"none"});
	$("#returnedDevice_delete_success").css({"display":"none"});
	$("#returnedDevice_delete_failure").css({"display":"none"});
	getDeviceStatDetails(); 
}

function onDeleteBrokenDeviceFailure(){
	$("#addDeviceSuccess").css({"display":"none"});
	$("#addDeviceFailure").css({"display":"none"});
	$("#searchDevice_update_success").css({"display":"none"});
	$("#searchDevice_update_failure").css({"display":"none"});
	$("#searchDevice_delete_success").css({"display":"none"});
	$("#searchDevice_delete_failure").css({"display":"none"});
	$("#brokenDevice_delete_success").css({"display":"none"});
	$("#brokenDevice_delete_failure").css({"display":"block"});
	$("#returnedDevice_delete_success").css({"display":"none"});
	$("#returnedDevice_delete_failure").css({"display":"none"});
}

function deleteReturnedDevice(usrObj) {
	var school_id = usrObj.attr('school_DeviceAccountId');
	$("#schooldeviceAccount_id").val(school_id);
}

function deleteDeviceFromAccountReturned() {
	var school_id = $('#schooldeviceAccount_id').val();
	makeGetAjaxRequest("deleteReturndeDeviceApi/" + token_id + "/" + school_id,
			false, onDeleteReturnedDeviceSuccess, onDeleteReturnedDeviceFailure);
}
function onDeleteReturnedDeviceSuccess(){ 
	$("#addDeviceSuccess").css({"display":"none"});
	$("#addDeviceFailure").css({"display":"none"});
	$("#searchDevice_update_success").css({"display":"none"});
	$("#searchDevice_update_failure").css({"display":"none"});
	$("#searchDevice_delete_success").css({"display":"none"});
	$("#searchDevice_delete_failure").css({"display":"none"});
	$("#brokenDevice_delete_success").css({"display":"none"});
	$("#brokenDevice_delete_failure").css({"display":"none"});
	$("#returnedDevice_delete_success").css({"display":"block"});
	$("#returnedDevice_delete_failure").css({"display":"none"});
	getDeviceStatDetails(); }

function onDeleteReturnedDeviceFailure(){
	$("#addDeviceSuccess").css({"display":"none"});
	$("#addDeviceFailure").css({"display":"none"});
	$("#searchDevice_update_success").css({"display":"none"});
	$("#searchDevice_update_failure").css({"display":"none"});
	$("#searchDevice_delete_success").css({"display":"none"});
	$("#searchDevice_delete_failure").css({"display":"none"});
	$("#brokenDevice_delete_success").css({"display":"none"});
	$("#brokenDevice_delete_failure").css({"display":"none"});
	$("#returnedDevice_delete_success").css({"display":"none"});
	$("#returnedDevice_delete_failure").css({"display":"block"});
}

function deleteDeviceConfig(usrObj) {
	var config_id = usrObj.attr('device_ConfigId');
	$("#deviceConfig_id").val(config_id);
}

function deleteDeviceConfigDetails() {
	var config_id = $('#deviceConfig_id').val();
	makeGetAjaxRequest("deleteDeviceConfigApi/" + token_id + "/" + config_id,
			false, onDeleteDeviceConfigSuccess, onDeleteDeviceConfigFailure);
}
function onDeleteDeviceConfigSuccess(){ 
	$("#fotaFailure").css({"display":"none"});
	$("#fotaSuccess").css({"display":"none"});
	$("#fota_delete_success").css({"display":"block"});
	$("#fota_delete_failure").css({"display":"none"});
	getDeviceConfigList(); 
}

function onDeleteDeviceConfigFailure(){
	$("#fotaFailure").css({"display":"none"});
	$("#fotaSuccess").css({"display":"none"});
	$("#fota_delete_success").css({"display":"none"});
	$("#fota_delete_failure").css({"display":"block"});
}

function mergeCategoryInDetails(usrObj) {
	var eLog = JSON.parse(usrObj.attr('error_details'));
	var finalError = '';
	if(!isDataNull(eLog) && !isDataNull(eLog.error_log) && eLog.error_log.length > 0){
		for (i = 0; i < eLog.error_log.length; i++){
			var rowId = (eLog.error_log[i].row *1) + 1;
			finalError += 'Row:' + rowId + ' >> ' + eLog.error_log[i].error + '\n';
		}
	}

	$("#editcategoryIn_id").val('').val(finalError);
}
