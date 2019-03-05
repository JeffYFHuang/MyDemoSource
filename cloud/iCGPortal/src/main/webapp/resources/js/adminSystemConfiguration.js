var sysConfigurationList;

$(document)
		.ready(
				function() {
					var token_id = $("#token_id").val();
					$('.systemconfig').removeClass("treeview").addClass(
							"active");
					$('.systemconfig').removeClass("font-medium-roboto").addClass("font-bold-roboto");
					$("#sSystemConfigIcon")
							.attr("src",
									"resources/images/SuperAdminSideBarIcons/White_Icon/System_configuration.png");

					if (sysConfigurationList) {
						onSuccessSysConfigurationListDetails(sysConfigurationList);
					} else {
						makeGetAjaxRequest("sysConfigurationList/" + token_id,
								false, onSuccessSysConfigurationListDetails,
								onFailureSysConfigurationListDetails);
					}
				});

function onSuccessSysConfigurationListDetails(responseData) {
	if (!isDataNull(responseData) && !isDataNull(responseData.Return)
			&& !isDataNull(responseData.Return.Results)) {
		sysConfigurationList = responseData;

		if (!isDataNull(responseData.Return.Results.adminScheduleDataSync)) {
			adminScheduleDataSync = responseData.Return.Results.adminScheduleDataSync;
			$('#adminScheduleDataSync').val(adminScheduleDataSync);
		} else {
			adminScheduleDataSync = '01';
			$('#adminScheduleDataSync').val(adminScheduleDataSync);
		}

		if (!isDataNull(responseData.Return.Results.adminScheduleSessionValidity)) {
			adminScheduleSessionValidity = responseData.Return.Results.adminScheduleSessionValidity;
			$('#adminScheduleSessionValidity')
					.val(adminScheduleSessionValidity);
		} else {
			adminScheduleSessionValidity = '30';
			$('#adminScheduleSessionValidity')
					.val(adminScheduleSessionValidity);
		}

		if (!isDataNull(responseData.Return.Results.adminScheduleUserSessionValidity)) {
			adminScheduleUserSessionValidity = responseData.Return.Results.adminScheduleUserSessionValidity;
			$('#adminScheduleUserSessionValidity').val(
					adminScheduleUserSessionValidity);
		} else {
			adminScheduleUserSessionValidity = '30';
			$('#adminScheduleUserSessionValidity').val(
					adminScheduleUserSessionValidity);
		}

		if (!isDataNull(responseData.Return.Results.adminSchedulePwdLinkValidity)) {
			adminSchedulePwdLinkValidity = responseData.Return.Results.adminSchedulePwdLinkValidity;
			$('#adminSchedulePwdLinkValidity')
					.val(adminSchedulePwdLinkValidity);
		} else {
			adminSchedulePwdLinkValidity = '30';
			$('#adminSchedulePwdLinkValidity')
					.val(adminSchedulePwdLinkValidity);
		}
	}
}

function onFailureSysConfigurationListDetails(responseData) {
	console.log("Inside onFailureSysConfigurationListDetails");
}

$('#adminScheduleSave')
		.on(
				"click",
				function() {
					var token_id = $("#token_id").val();
					var adminScheduleDataSync = parseInt($(
							'#adminScheduleDataSync').val());
					var adminScheduleSessionValidity = parseInt($(
							'#adminScheduleSessionValidity').val());
					var adminScheduleUserSessionValidity = parseInt($(
							'#adminScheduleUserSessionValidity').val());
					var adminSchedulePwdLinkValidity = parseInt($(
							'#adminSchedulePwdLinkValidity').val());

					var requestData = {
						"adminScheduleDataSync" : adminScheduleDataSync,
						"adminScheduleSessionValidity" : adminScheduleSessionValidity,
						"adminScheduleUserSessionValidity" : adminScheduleUserSessionValidity,
						"adminSchedulePwdLinkValidity" : adminSchedulePwdLinkValidity
					};
					makePostAjaxRequest("adminSysConfiguration/" + token_id,
							requestData, onSuccessSysConfigurationUpdate,
							onFailureSysConfigurationUpdate);
				});

function onSuccessSysConfigurationUpdate(responseData) {
	var responseCode = '';
	var responseMsg = '';
	$("#submitMsg").empty();
	var token_id = $("#token_id").val();

	if (!isDataNull(responseData) && !isDataNull(responseData.Return)
			&& !isDataNull(responseData.Return.ResponseSummary)
			&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {

		responseCode = responseData.Return.ResponseSummary.StatusCode;

		if (responseCode === 'SUC01') {
			responseMessage = "System Configuration Updated Successfully";
			$("#submitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#submitMsg").css({"color":"green"});
			responseMsg = '';
		} else if (responseCode === 'ERR01') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#submitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#submitMsg").css({"color":"red"});
			responseMsg = '';
		} else if (responseCode === 'ERR03') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#submitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#submitMsg").css({"color":"red"});
			responseMsg = '';
		} else if (responseCode === 'ERR04') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#submitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#submitMsg").css({"color":"red"});
			responseMsg = '';
		} else if (responseCode === 'ERR05') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#submitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#submitMsg").css({"color":"red"});
			responseMsg = '';
		} else {
			responseMessage = 'OOPS !! Something went wrong';
			$("#submitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#submitMsg").css({"color":"red"});
			responseMsg = '';
		}
	} else {
		responseMessage = 'OOPS !! Something went wrong';
		$("#submitMsg").append(' <b>' + responseMessage + '</b>.');
		$("#submitMsg").css({"color":"red"});
		responseMsg = '';
	}
}

function onFailureSysConfigurationUpdate(responseData) {
	responseMessage = 'OOPS !! Something went wrong';
	$("#submitMsg").empty();
	$("#submitMsg").append(' <b>' + responseMessage + '</b>.');
}

$('#adminScheduleCancel').on("click", function() {
	$("#submitMsg").empty();
	onSuccessSysConfigurationListDetails(sysConfigurationList);

});