$(function() {
	$('.usermanage').removeClass("treeview").addClass("active");
	$("#sUserManageIcon")
			.attr("src",
					"resources/images/SchoolAdmin_sideBarIcons/White/account_managment.png");
	$('.usermanage').removeClass("font-medium-roboto").addClass(
			"font-bold-roboto");

	$('.upgrade_icon').bind("click", function(event) {
		displayUpgrade($(this));
	});
	function displayUpgrade(assetObj) {
		$("#editAdminUserRole").val(assetObj.attr('assetAdminUserRole'));
		$("#editAdminUserProfileName").val(
				assetObj.attr('assetAdminUserProfileName'));
		$("#editAdminUserContact").val(assetObj.attr('assetAdminUserContact'));
	}

});

var token_id = $("#token_id").val();

$(document).ready(function() {
	debugLogs("on ready function called");

	loadStudentSearchResults("0");
	var rewardClick = '';

});

function loadStudentSearchResults(status) {
	debugLogs("loading data " + status);
	var page_id = 1;
	makeGetAjaxRequest("SearchUserAccountLists/" + token_id + "/" + status
			+ "/" + page_id, true, onSearchSuccess, onSearchFailure);

}

$("#adminUserGo").click(
		function() {
			debugLogs("selected option is  "
					+ $("#adminUserAccountListFilter").val());
			loadStudentSearchResults($("#adminUserAccountListFilter").val());
			return false;
		});

function onSearchFailure(data) {

	if (isDataNull(data)) {
		var class_students = '<tr class="box-body"><td colspan="6" align="center"><b class="icglabel_nodata"></b></td> </tr>';
		$("#stDetails").append(class_students);
	}
}

function onSearchSuccess(data) {
	debugLogs("onSearchSuccess :::::: responseData >>>>>>> "
			+ JSON.stringify(data));
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$("#stDetails").empty();

		if (!isDataNull(data.Return.Results.userAccountLists)) {
			var len = data.Return.Results.userAccountLists.length;
			if (len > 0) {
				$("#pagination").css({
					"display" : "block"
				});
				for (var count = 0; count < len; count++) {
					var icglabel_user_id;
					var icglabel_profileName = "";
					var icglabel_emailId = "";
					var icglabel_contactNo = "";
					var icglabel_userType = "";
					var icglabel_lastlogin = "";
					icglabel_user_id = data.Return.Results.userAccountLists[count].user_id;
					icglabel_profileName = data.Return.Results.userAccountLists[count].profileName;
					icglabel_emailId = data.Return.Results.userAccountLists[count].emailId;
					icglabel_contactNo = data.Return.Results.userAccountLists[count].contactNo;
					icglabel_userType = data.Return.Results.userAccountLists[count].userType;
					icglabel_userType = (icglabel_userType == "system_admin") ? "System Admin"
							: "Support Staff";

					assignedend = data.Return.Results.userAccountLists[count].noofPages;
					assignedstart = data.Return.Results.userAccountLists[count].currentPage;

					if (!isDataNull(data.Return.Results.userAccountLists[count].lastlogin)) {
						icglabel_lastlogin = data.Return.Results.userAccountLists[count].lastlogin;
					}

					var rowData = '<tr>' + '<td>'
							+ icglabel_profileName
							+ '</td>'
							+ '<td>'
							+ icglabel_emailId
							+ '</td>'
							+ '<td>'
							+ icglabel_contactNo
							+ '</td>'
							+ '<td>'
							+ icglabel_userType
							+ '</td>'
							+ '<td>'
							+ icglabel_lastlogin
							+ '</td>'
							+ '<td class="editIcon"><img onclick="mergeUserDetails($(this))" '
							+ 'src="resources/images/unselected_edit_icon.png" class="hand" data-toggle="modal" data-target="#editAdminUserDetails" data-keyboard="true" '
							+ 'user_id="'
							+ icglabel_user_id
							+ '" profileName="'
							+ escape(icglabel_profileName)
							+ '" emailId="'
							+ icglabel_emailId
							+ '" contactNo="'
							+ icglabel_contactNo
							+ '" userType="'
							+ icglabel_userType
							+ '" /></td> '
							+ '<td class="deleteIcon"><a style="color: black" '
							+ 'data-toggle="modal" data-target="#deleteParentDetails" ><img class="hand" onclick="deleteParentDetails($(this))" user_id="'
							+ icglabel_user_id
							+ '" src="resources/images/Delete_icon.png" /></a></td> '
							+ '</tr>';
					$("#stDetails").append(rowData);

				}
				document.getElementById('assigned_adminstartPage').innerHTML = assignedstart;
				document.getElementById('assigned_adminendPage').innerHTML = assignedend;
				if (assignedstart === assignedend) {
					$("#decrease").attr("href", "javascript: void(0)");
					$("#increase").attr("href", "javascript: void(0)");
				}
			} else {
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				var rowData = '<tr><td colspan="6" class="box-body text-center"><b>'
						+ nodataText + '</b></td></tr></tbody>';
				$("#stDetails").append(rowData);
			}
		} else {
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			var rowData = '<tr><td colspan="6" class="box-body text-center"><b>'
					+ nodataText + '</b></td></tr></tbody>';
			$("#stDetails").append(rowData);
		}
	} else {
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		var rowData = '<tr><td colspan="6" class="box-body text-center"><b>'
				+ nodataText + '</b></td></tr></tbody>';
		$("#stDetails").append(rowData);
	}
}

$(function() {
	$("#addUserCancel").on("click", function(e) {
		debugLogs("requestData addUserCancel inside:::::: ");

		$("#userAccount_create_success").css({
			"display" : "none"
		});
		$("#userAccount_create_failure").css({
			"display" : "none"
		});
		$("#userAccount_update_success").css({
			"display" : "none"
		});
		$("#userAccount_update_failure").css({
			"display" : "none"
		});
		$("#userAccount_delete_success").css({
			"display" : "none"
		});
		$("#userAccount_delete_failure").css({
			"display" : "none"
		});

		$("#addUserProfileName_err").css({
			"display" : "none"
		});
		$("#addUserContactStartOfContactInvalid").css({
			"display" : "none"
		});
		$("#addUserContactInValidContactNo").css({
			"display" : "none"
		});
		$("#addUserUsername_incorrect").css({
			"display" : "none"
		});
		$("#addUserContact_err").css({
			"display" : "none"
		});
		$("#addUserUsername_err").css("display", "none");
		$("#usersubmitMsg").empty();

		$("#addUserRole").val("system_admin");
		$("#addUserUsername").val("");
		$('#addUserProfileName').val("");
		$('#addUserContact').val("");

	});

});

function validateUser() {

	$("#addUserProfileName_err").css({
		"display" : "none"
	});
	$("#addUserContactStartOfContactInvalid").css({
		"display" : "none"
	});
	$("#addUserContactInValidContactNo").css({
		"display" : "none"
	});
	$("#addUserUsername_incorrect").css({
		"display" : "none"
	});
	$("#addUserContact_err").css({
		"display" : "none"
	});
	$("#addUserUsername_err").css("display", "none");
	$("#userAccount_create_failure").css({
		"display" : "none"
	});
	$("#usersubmitMsg").empty();

	var contactnumber = $("#addUserContact").val();
	var phoneRegEx = /((?=(09))[0-9]{10})$/g;
	var username = $("#addUserUsername").val();
	var filter = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;

	if (!filter.test(username)) {
		$("#addUserUsername_err").css("display", "block");
		return false;
	}

	if ($("#addUserProfileName").val() == "") {
		$("#addUserProfileName_err").css({
			"display" : "block"
		});
		return false;
	}

	if (contactnumber == "") {
		$("#addUserContact_err").css({
			"display" : "block"
		});
		return false;
	}

	if (!phoneRegEx.test(contactnumber)) {
		$("#addUserContactInValidContactNo").css({
			"display" : "block"
		});
		return false;
	}

	return true;
};

function editvalidateUser() {

	$("#editAdminParentProfileName_err").css({
		"display" : "none"
	});
	$("#editAdminParentContactStartOfContactInvalid").css({
		"display" : "none"
	});
	$("#editAdminParentContactInValidContactNo").css({
		"display" : "none"
	});
	$("#editAdminParentUsername_err").css({
		"display" : "none"
	});
	$("#searchParentUsername_incorrect").css({
		"display" : "none"
	});
	$("#editAdminParentContact_err").css({
		"display" : "none"
	});
	$("#editAdminParentUsername_err").css("display", "none");

	var contactnumber = $("#editAdminUserContact").val();
	var phoneRegEx = /((?=(09))[0-9]{10})$/g;
	var username = $("#editUserUsername").val();
	var filter = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;

	if (!filter.test(username)) {
		$("#editAdminParentUsername_err").css("display", "block");
		return false;
	}

	if ($("#editAdminUserProfileName").val() == "") {
		$("#editAdminParentProfileName_err").css({
			"display" : "block"
		});
		return false;
	}

	if (contactnumber == "") {
		$("#editAdminParentContact_err").css({
			"display" : "block"
		});
		return false;
	}

	if (!phoneRegEx.test(contactnumber)) {
		$("#editAdminParentContactInValidContactNo").css({
			"display" : "block"
		});
		return false;
	}

	return true;
};

$(function() {
	$("#editAdminUserUpdate").on(
			"click",
			function(e) {
				debugLogs("requestData editAdminParentSave inside:::::: ");
				var profileName = '';
				var user_id = '';
				profileName = $("#editAdminParentProfileName").val();
				user_id = $('#edituser_id').val();
				var saveType = '';
				saveType = '1';

				var isValidForm = true;

				if (!editvalidateUser()) {

					e.preventDefault();
					isValidForm = false;
				}

				if (editvalidateUser()) {
					debugLogs("editAdminParentSave inside call "
							+ profileName);
					var token_id = $("#token_id").val();
					var profileName = $('#editAdminUserProfileName').val();
					var contactNo = $('#editAdminUserContact').val();
					var emailId = $('#editUserUsername').val();
					var roleType = $('#editAdminUserRole').val();

					debugLogs("after editing editAdminParentSave "
							+ $("#edituser_id").val() + " "
							+ $("#editAdminUserRole").val() + " "
							+ $("#editAdminUserProfileName").val() + " "
							+ $("#editAdminUserContact").val() + " "
							+ $("#editUserUsername").val());

					var requestData = {
						"name" : profileName,
						"mobile_number" : contactNo,
						"username" : emailId,
						"roleType" : roleType

					};

					debugLogs("request data " + requestData + " for id "
							+ user_id);

					makePostAjaxRequest("SaveUserAccountManagement/" + token_id
							+ "/" + user_id + "/" + saveType, requestData,
							onSuccessUserAccountUpdate,
							onFailureUserAccountUpdate);
				} else {
					e.preventDefault();
					return false;
				}
			});

});

$(function() {
	$("#editAdminUserCancel").on("click", function(e) {
		debugLogs("requestData editAdminParentCancel inside:::::: ");

		$("#userAccount_create_success").css({
			"display" : "none"
		});
		$("#userAccount_create_failure").css({
			"display" : "none"
		});
		$("#userAccount_update_success").css({
			"display" : "none"
		});
		$("#userAccount_update_failure").css({
			"display" : "none"
		});
		$("#userAccount_delete_success").css({
			"display" : "none"
		});
		$("#userAccount_delete_failure").css({
			"display" : "none"
		});

		$("#editAdminParentProfileName_err").css({
			"display" : "none"
		});
		$("#editAdminParentContactStartOfContactInvalid").css({
			"display" : "none"
		});
		$("#editAdminParentContactInValidContactNo").css({
			"display" : "none"
		});
		$("#editAdminParentUsername_err").css({
			"display" : "none"
		});
		$("#searchParentUsername_incorrect").css({
			"display" : "none"
		});
		$("#editAdminParentContact_err").css({
			"display" : "none"
		});
		$("#editAdminParentUsername_err").css("display", "none");

		$('#editAdminUserProfileName').val("");
		$('#editAdminUserContact').val("");
		$('#editUserUsername').val("");
		$('#editAdminUserRole').val("system_admin");
		$("#usersubmitMsg").empty();

	});

});

$(function() {
	$("#addUserCreate").on(
			"click",
			function(e) {
				debugLogs("requestData addUserCreate inside:::::: ");

				var user_id = '';
				user_id = '0';
				var saveType = '';
				saveType = '0';

				if (validateUser()) {
					debugLogs("editAdminParentSave inside call ");
					var token_id = $("#token_id").val();
					var profileName = $('#addUserProfileName').val();
					var contactNo = $('#addUserContact').val();
					var emailId = $('#addUserUsername').val();
					var roleType = $('#addUserRole').val();

					var requestData = {
						"name" : profileName,
						"mobile_number" : contactNo,
						"username" : emailId,
						"roleType" : roleType

					};

					debugLogs("request data " + requestData + " for id "
							+ user_id);

					makePostAjaxRequest("SaveUserAccountManagement/" + token_id
							+ "/" + user_id + "/" + saveType, requestData,
							onSuccessUserAccountCreate,
							onFailureUserAccountCreate);
				} else {
					e.preventDefault();
					return false;
				}
			});

});

function onSuccessUserAccountCreate(responseData) {
	debugLogs('Into onSuccessParentAccountUpdate ()');
	$("#userAccount_create_success").css({
		"display" : "none"
	});
	$("#userAccount_create_failure").css({
		"display" : "none"
	});
	$("#userAccount_update_success").css({
		"display" : "none"
	});
	$("#userAccount_update_failure").css({
		"display" : "none"
	});
	$("#userAccount_delete_success").css({
		"display" : "none"
	});
	$("#userAccount_delete_failure").css({
		"display" : "none"
	});

	$("#addUserRole").val("system_admin");
	$("#addUserUsername").val("");
	$('#addUserProfileName').val("");
	$('#addUserContact').val("");
	$("#usersubmitMsg").empty();

	var token_id = $("#token_id").val();

	if (!isDataNull(responseData) && !isDataNull(responseData.Return)
			&& !isDataNull(responseData.Return.ResponseSummary)
			&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {
		debugLogs("inside the response value");

		var responseCode = responseData.Return.ResponseSummary.StatusCode;
		var responseMessage = '';
		if (responseCode === 'SUC01') {
			debugLogs("onSuccessUserAccountCreate ON> ");
			$("#userAccount_create_success").css({
				"display" : "block"
			});

			loadStudentSearchResults($("#adminUserAccountListFilter").val());
		} else if (responseCode === 'ERR01') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#usersubmitMsg").css({
				"color" : "red"
			});
			$("#userAccount_create_failure").css({
				"display" : "block"
			});
		} else if (responseCode === 'ERR02') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#usersubmitMsg").css({
				"color" : "red"
			});
			$("#userAccount_create_failure").css({
				"display" : "block"
			});
		} else if (responseCode === 'ERR03') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#usersubmitMsg").css({
				"color" : "red"
			});
			$("#userAccount_create_failure").css({
				"display" : "block"
			});
		} else if (responseCode === 'ERR04') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#usersubmitMsg").css({
				"color" : "red"
			});
			$("#userAccount_create_failure").css({
				"display" : "block"
			});
		} else if (responseCode === 'ERR19') {
			debugLogs("222222222 status code ::::::::  " + responseCode);
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#usersubmitMsg").css({
				"color" : "red"
			});
			$("#userAccount_create_failure").css({
				"display" : "block"
			});
		} else {
			responseMessage = 'OOPS !! Something went wrong';
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#usersubmitMsg").css({
				"color" : "red"
			});
			$("#userAccount_create_failure").css({
				"display" : "block"
			});
		}

	} else {
		debugLogs("inside the response value failed");
		responseMessage = 'OOPS !! Something went wrong';
		$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
		$("#usersubmitMsg").css({
			"color" : "red"
		});
		$("#userAccount_create_failure").css({
			"display" : "block"
		});
	}
}

function onFailureUserAccountCreate(responseData) {
	debugLogs("onFailureUserAccountCreate :::::::::::: "
			+ responseData.status);
	$("#userAccount_create_success").css({
		"display" : "none"
	});
	$("#userAccount_create_failure").css({
		"display" : "none"
	});
	$("#userAccount_update_success").css({
		"display" : "none"
	});
	$("#userAccount_update_failure").css({
		"display" : "none"
	});
	$("#userAccount_delete_success").css({
		"display" : "none"
	});
	$("#userAccount_delete_failure").css({
		"display" : "none"
	});

	$("#addUserProfileName_err").css({
		"display" : "none"
	});
	$("#addUserContactStartOfContactInvalid").css({
		"display" : "none"
	});
	$("#addUserContactInValidContactNo").css({
		"display" : "none"
	});
	$("#addUserUsername_incorrect").css({
		"display" : "none"
	});
	$("#addUserContact_err").css({
		"display" : "none"
	});
	$("#addUserUsername_err").css("display", "none");
	$("#usersubmitMsg").empty();

	$("#addUserRole").val("system_admin");
	$("#addUserUsername").val("");
	$('#addUserProfileName').val("");
	$('#addUserContact').val("");

	$("#userAccount_create_failure").css({
		"display" : "block"
	});
}

function onSuccessUserAccountUpdate(responseData) {
	debugLogs('Into onSuccessParentAccountUpdate ()');

	$("#userAccount_create_success").css({
		"display" : "none"
	});
	$("#userAccount_create_failure").css({
		"display" : "none"
	});
	$("#userAccount_update_success").css({
		"display" : "none"
	});
	$("#userAccount_update_failure").css({
		"display" : "none"
	});
	$("#userAccount_delete_success").css({
		"display" : "none"
	});
	$("#userAccount_delete_failure").css({
		"display" : "none"
	});
	$("#usersubmitMsg").empty();

	debugLogs("onSuccessUserAccountUpdate :::::: responseData >>>>>>> "
			+ JSON.stringify(responseData));

	$('#editAdminUserProfileName').val("");
	$('#editAdminUserContact').val("");
	$('#editUserUsername').val("");
	$('#editAdminUserRole').val("system_admin");

	var token_id = $("#token_id").val();

	if (!isDataNull(responseData) && !isDataNull(responseData.Return)
			&& !isDataNull(responseData.Return.ResponseSummary)
			&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {
		debugLogs("inside the response value");
		var responseCode = responseData.Return.ResponseSummary.StatusCode;
		var responseMessage = '';
		if (responseCode === 'SUC01') {
			debugLogs("onSuccessUserAccountUpdate ON> ");
			$("#userAccount_update_success").css({
				"display" : "block"
			});
			loadStudentSearchResults($("#adminUserAccountListFilter").val());
		} else {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#userUpdateMsg").append(responseMessage);
			$("#userAccount_update_failure").css({
				"display" : "block"
			});
		}
	} else {
		debugLogs("inside the response value failed");
		responseMessage = 'OOPS !! Something went wrong';
		$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
		$("#userAccount_update_failure").css({
			"display" : "block"
		});
	}
}

function onFailureUserAccountUpdate(responseData) {
	responseMessage = 'OOPS !! Something went wrong';
	debugLogs("onFailure :::::::::::: " + responseData.status);
	$("#userAccount_create_success").css({
		"display" : "none"
	});
	$("#userAccount_create_failure").css({
		"display" : "none"
	});
	$("#userAccount_update_success").css({
		"display" : "none"
	});
	$("#userAccount_update_failure").css({
		"display" : "none"
	});
	$("#userAccount_delete_success").css({
		"display" : "none"
	});
	$("#userAccount_delete_failure").css({
		"display" : "none"
	});

	debugLogs("onFailureUserAccountUpdate :::::: responseData >>>>>>> "
			+ JSON.stringify(responseData));

	$("#editAdminParentProfileName_err").css({
		"display" : "none"
	});
	$("#editAdminParentContactStartOfContactInvalid").css({
		"display" : "none"
	});
	$("#editAdminParentContactInValidContactNo").css({
		"display" : "none"
	});
	$("#editAdminParentUsername_err").css({
		"display" : "none"
	});
	$("#searchParentUsername_incorrect").css({
		"display" : "none"
	});
	$("#editAdminParentContact_err").css({
		"display" : "none"
	});
	$("#editAdminParentUsername_err").css("display", "none");

	$("#editAdminParentProfileName").val("");
	$("#editAdminParentContact").val("");
	$('#editAdminParentUsername').val("");
	$('#editAdminUserRole').val("system_admin");
	$("#usersubmitMsg").empty();

	$("#adminParent_update_failure").css({
		"display" : "block"
	});
}

$("#adminParentDeleteId").click(
		function() {

			debugLogs("requestData delte inside parent:::::: ");

			var user_id = '';
			user_id = $('#edituser_id').val();
			var token_id = $("#token_id").val();
			debugLogs("delete user_id " + user_id);
			debugLogs("delete token_id " + token_id);
			makeGetAjaxRequest("DeleteUserAccountManagement/" + token_id + "/"
					+ user_id, false, onDeleteParentDeleteSuccess,
					onDeleteParentDeleteFailure);
		});

function onDeleteParentDeleteSuccess(responseData) {
	debugLogs("inside delete onDeleteParentDeleteSuccess ");
	debugLogs("onDeleteParentDeleteSuccess :::::: responseData >>>>>>> "
			+ JSON.stringify(responseData));
	$("#userAccount_create_success").css({
		"display" : "none"
	});
	$("#userAccount_create_failure").css({
		"display" : "none"
	});
	$("#userAccount_update_success").css({
		"display" : "none"
	});
	$("#userAccount_update_failure").css({
		"display" : "none"
	});
	$("#userAccount_delete_success").css({
		"display" : "none"
	});
	$("#userAccount_delete_failure").css({
		"display" : "none"
	});

	$("#editAdminParentProfileName_err").css({
		"display" : "none"
	});
	$("#editAdminParentContactStartOfContactInvalid").css({
		"display" : "none"
	});
	$("#editAdminParentContactInValidContactNo").css({
		"display" : "none"
	});
	$("#editAdminParentUsername_err").css({
		"display" : "none"
	});
	$("#searchParentUsername_incorrect").css({
		"display" : "none"
	});
	$("#editAdminParentContact_err").css({
		"display" : "none"
	});
	$("#editAdminParentUsername_err").css("display", "none");

	$('#editAdminUserProfileName').val("");
	$('#editAdminUserContact').val("");
	$('#editUserUsername').val("");
	$('#editAdminUserRole').val("system_admin");
	$("#usersubmitMsg").empty();

	var token_id = $("#token_id").val();

	if (!isDataNull(responseData) && !isDataNull(responseData.Return)
			&& !isDataNull(responseData.Return.ResponseSummary)
			&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {
		debugLogs("inside the response value");

		var responseCode = responseData.Return.ResponseSummary.StatusCode;
		var responseMessage = '';
		if (responseCode === 'SUC01') {
			debugLogs("onDeleteParentDeleteSuccess ON> ");
			$("#userAccount_delete_success").css({
				"display" : "block"
			});
			loadStudentSearchResults($("#adminUserAccountListFilter").val());
		} else if (responseCode === 'ERR01') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#userAccount_delete_failure").css({
				"display" : "block"
			});
		} else if (responseCode === 'ERR02') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#userAccount_delete_failure").css({
				"display" : "block"
			});
		} else if (responseCode === 'ERR03') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#userAccount_delete_failure").css({
				"display" : "block"
			});
		} else if (responseCode === 'ERR04') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#userAccount_delete_failure").css({
				"display" : "block"
			});
		} else if (responseCode === 'ERR19') {
			debugLogs("222222222 status code ::::::::  " + responseCode);
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#userAccount_delete_failure").css({
				"display" : "block"
			});
		} else {
			responseMessage = 'OOPS !! Something went wrong';
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
			$("#userAccount_delete_failure").css({
				"display" : "block"
			});
		}

	} else {
		debugLogs("inside the response value failed");
		responseMessage = 'OOPS !! Something went wrong';
		$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>.');
		$("#userAccount_delete_failure").css({
			"display" : "block"
		});
	}

}

function onDeleteParentDeleteFailure(responseData) {
	debugLogs("requestData editAdminParentCancel inside:::::: ");
	$("#userAccount_create_success").css({
		"display" : "none"
	});
	$("#userAccount_create_failure").css({
		"display" : "none"
	});
	$("#userAccount_update_success").css({
		"display" : "none"
	});
	$("#userAccount_update_failure").css({
		"display" : "none"
	});
	$("#userAccount_delete_success").css({
		"display" : "none"
	});
	$("#userAccount_delete_failure").css({
		"display" : "none"
	});

	$("#editAdminParentProfileName_err").css({
		"display" : "none"
	});
	$("#editAdminParentContactStartOfContactInvalid").css({
		"display" : "none"
	});
	$("#editAdminParentContactInValidContactNo").css({
		"display" : "none"
	});
	$("#editAdminParentUsername_err").css({
		"display" : "none"
	});
	$("#searchParentUsername_incorrect").css({
		"display" : "none"
	});
	$("#editAdminParentContact_err").css({
		"display" : "none"
	});
	$("#editAdminParentUsername_err").css("display", "none");

	$('#editAdminUserProfileName').val("");
	$('#editAdminUserContact').val("");
	$('#editUserUsername').val("");
	$('#editAdminUserRole').val("system_admin");
	$("#usersubmitMsg").empty();

	$("#userAccount_delete_failure").css({
		"display" : "block"
	});
}
function increaseUserListHref() {
	debugLogs('Into increaseUserListHref()');
	debugLogs('assignedstart In increaseUserListHref' + '\t' + assignedstart);
	var currentpage = assignedstart;
	currentpage++;
	debugLogs('currentpage' + "\t" + currentpage);
	var status = $("#adminUserAccountListFilter").val();
	debugLogs('status' + '\t' + status);
	var token_id = $("#token_id").val();
	if (currentpage <= assignedend) {
		debugLogs('Getting into if loop');
		assignedstart = currentpage;
		makeGetAjaxRequest("SearchUserAccountLists/" + token_id + "/" + status
				+ "/" + currentpage, false, onSearchSuccess, onSearchFailure);
	} else {
		$("#increase").attr("href", "javascript: void(0)");
	}
}

function decreaseUserListHref() {
	debugLogs('Into decreaseUserListHref()');
	var status = $("#adminUserAccountListFilter").val();
	debugLogs('status' + '\t' + status);
	var currentpage = assignedstart;
	currentpage--;
	var token_id = $("#token_id").val();
	if (currentpage > 0) {
		assignedstart = currentpage;
		makeGetAjaxRequest("SearchUserAccountLists/" + token_id + "/" + status
				+ "/" + currentpage, false, onSearchSuccess, onSearchFailure);
	} else {
		$("#decrease").attr("href", "javascript: void(0)");
	}
}

function mergeUserDetails(usrObj) {
	var user_id = usrObj.attr('user_id');
	var userType = usrObj.attr('userType');
	userType = (userType == "System Admin") ? "system_admin" : "support_staff";
	var profileName = unescape(usrObj.attr('profileName'));
	var contactNo = usrObj.attr('contactNo');
	var emailId = usrObj.attr('emailId');

	var token_id = $("#token_id").val();
	debugLogs("appended edit data " + user_id + " " + userType + " "
			+ profileName + " " + contactNo + " " + emailId);

	$("#edituser_id").val(user_id);
	$("#editAdminUserRole").val(userType);
	$("#editAdminUserProfileName").val(profileName);
	$("#editAdminUserContact").val(contactNo);
	$("#editUserUsername").val(emailId);

	debugLogs("after merging " + $("#edituser_id").val() + " "
			+ $("#editAdminUserRole").val() + " "
			+ $("#editAdminUserProfileName").val() + " "
			+ $("#editAdminUserContact").val() + " "
			+ $("#editUserUsername").val());

}

function deleteParentDetails(usrObj) {
	debugLogs("success  deleteParentDetails");
	var user_id = usrObj.attr('user_id');
	$("#edituser_id").val(user_id);
}