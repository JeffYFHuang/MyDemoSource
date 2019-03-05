var schoolstart = 0;
var schoolend = 0;
$(function() {
	var token_id = $("#token_id").val();
	$('.parentmanage').removeClass("treeview").addClass("active");
	$('.parentmanage').removeClass("font-medium-roboto").addClass(
			"font-bold-roboto");
	$("#sParentManageIcon")
			.attr(
					"src",
					"resources/images/SuperAdminSideBarIcons/White_Icon/Parent_account_managment.png");
	$("#searchCriteriaError").css({
		"display" : "none"
	});
	function loadStudentSearchResults() {
		if (validate()) {
			var page_id = 1;
			var profileName = $("#searchParentProfileName").val().trim();
			var contactNo = $("#searchParentContact").val().trim();
			var emailId = $("#searchParentUsername").val().trim();
			var uuid = $("#searchParentUuid").val().trim();

			profileName = (profileName.length <= 0) ? 0 : profileName;
			contactNo = (contactNo.length <= 0) ? 0 : contactNo;
			emailId = (emailId.length <= 0) ? 0 : emailId;
			uuid = (uuid.length <= 0) ? 0 : uuid;

			if (profileName == 0 && contactNo == 0 && emailId == 0 && uuid == 0) {
				debugLogs('At least one search criteria must be entered');
				$("#searchCriteriaError").css({
					"display" : "block"
				});
				$("#stDetails").empty();
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				var class_students = '<tr class="box-body"><td colspan="10" align="center"><b>'+ nodataText +'<b></td> </tr>';
				$("#stDetails").append(class_students);
			} else {
				$("#searchCriteriaError").css({
					"display" : "none"
				});
				profileName = encodeURIComponent(profileName);
				profileName = profileName.replace(/%25/g, '%2525');
				makeGetAjaxRequest("SearchParentAccountManagement/" + token_id
						+ "/" + profileName + "/"
						+ contactNo + "/" + emailId + "/" + uuid + "/"
						+ page_id, true, onSearchSuccess, onSearchFailure);
			}
		} else {
			console.log("validation failed::::");
		}
	}
	function validate() {
		var filter = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;
		var contactnumber = $("#searchParentContact").val();
		var email = $("#searchParentUsername").val();
		$("#searchCriteriaError").css({
			"display" : "none"
		});
		$("#searchParentContactInValid").css({
			"display" : "none"
		});
		if (!isDataNull(email) && (!filter.test(email))) {
			$("#searchParentUsername_incorrect").css({
				"display" : "block"
			});
			return false;
		} else {
			$("#searchParentUsername_incorrect").css({
				"display" : "none"
			});
		}
		if (!isDataNull(contactnumber) && (!phoneRegEx.test(contactnumber))) {
			$("#searchParentContactInValid").css({
				"display" : "block"
			});
			return false;
		} else {
			$("#searchParentContactInValid").css({
				"display" : "none"
			});
		}
		return true;
	}
	;
	$("#searchParentSearch").click(function() {
		$("#searchCriteriaError, #adminParent_update_success, #adminParent_update_failure, #adminParent_delete_success, #adminParent_delete_failure").css({
			"display" : "none"
		});
		loadStudentSearchResults();
		return false;
	});

	function onSearchFailure(data) {
		if (isDataNull(data)) {
			var class_students = '<tr class="box-body"><td colspan="9" align="center"><b class="icglabel_nodata"></b></td> </tr>';
			$("#stDetails").append(class_students);
		}
	}

	function onSearchSuccess(data) {
		if (!isDataNull(data) && !isDataNull(data.Return)
				&& !isDataNull(data.Return.Results)) {
			$("#searchResultView").css({
				"display" : "block"
			});
			$("#stDetails").empty();

			if (!isDataNull(data.Return.Results.parents)) {
				var len = data.Return.Results.parents.length;
				if (len > 0) {
					$("#pagination").css({
						"display" : "block"
					});
				}
				schoolend = data.Return.Results.noofPages;
				schoolstart = data.Return.Results.currentPage;
				for (var count = 0; count < len; count++) {
					var icglabel_user_id;
					var icglabel_profileName = "";
					var icglabel_emailId = "";
					var icglabel_contactNo = "";
					var icglabel_userType = "";
					var icglabel_userSource = "";
					var icglabel_device_uuid = "";
					var icglabel_lastlogin = "";
					var icglabel_device_status = "";

					icglabel_user_id = data.Return.Results.parents[count].user_id;
					icglabel_profileName = data.Return.Results.parents[count].profileName;
					icglabel_emailId = data.Return.Results.parents[count].emailId;
					icglabel_contactNo = data.Return.Results.parents[count].contactNo;
					icglabel_userType = data.Return.Results.parents[count].userType;
					icglabel_userSource = data.Return.Results.parents[count].userSource;
					icglabel_device_uuid = data.Return.Results.parents[count].device_uuid;
					icglabel_lastlogin = data.Return.Results.parents[count].lastlogin;
					icglabel_device_status = data.Return.Results.parents[count].device_status;

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
							+ icglabel_userSource
							+ '</td>'
							+ '<td>'
							+ icglabel_device_status
							+ '</td>'
							+ '<td>'
							+ icglabel_device_uuid
							+ '</td>'
							+ '<td>'
							+ icglabel_lastlogin
							+ '</td>'
							+ '<td class="editIcon"><img onclick="mergeParentDetails($(this))" '
							+ 'src="resources/images/unselected_edit_icon.png" class="hand" data-toggle="modal" '
							+ 'data-target="#editAdminParentDetails" data-keyboard="true" '
							+ 'user_id="'
							+ icglabel_user_id
							+ '" profileName="'
							+ escape(icglabel_profileName)
							+ '" emailId="'
							+ icglabel_emailId
							+ '" contactNo="'
							+ icglabel_contactNo
							+ '" source="'
							+ icglabel_userSource
							+ '" /></td> '
							+ '<td class="deleteIcon"><a style="color: black" '
							+ 'data-toggle="modal" data-target="#deleteParentDetails" >'
							+ '<img class="hand" onclick="deleteParentDetails($(this))" user_id="'
							+ icglabel_user_id
							+ '" src="resources/images/Delete_icon.png" /></a></td> '
							+ '</tr>';

					document.getElementById('school_startPage').innerHTML = schoolstart;
					document.getElementById('school_endPage').innerHTML = schoolend;

					$("#stDetails").append(rowData);
				}
			} else {
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				var rowData = '<tr><td colspan="9" class="box-body text-center"><b>'
						+ nodataText + '</b></td></tr></tbody>';
				$("#stDetails").append(rowData);
			}
		} else {
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			var rowData = '<tr><td colspan="9" class="box-body text-center"><b>'
					+ nodataText + '</b></td></tr></tbody>';
			$("#stDetails").append(rowData);
		}
	}

	function validateUser() {
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

		var contactnumber = $("#editAdminParentContact").val();
		var phoneRegEx = /((?=(09))[0-9]{10})$/g;
		var username = $("#editAdminParentUsername").val();
		var filter = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;

		if ($("#editAdminParentProfileName").val() == "") {
			$("#editAdminParentProfileName_err").css({
				"display" : "block"
			});
			return false;
		}

		if ($("#editAdminParentContact").val() == "") {
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

		if (!filter.test(username)) {
			$("#editAdminParentUsername_err").css("display", "block");
			return false;
		}
		return true;
	}
	;

	$(function() {
		$("#editAdminParentSave")
				.on(
						"click",
						function(e) {
							console
									.log("requestData editAdminParentSave inside:::::: ");
							var profileName = '';
							var user_id = '';
							profileName = $("#editAdminParentProfileName")
									.val();
							user_id = $('#edituser_id').val();

							var isValidForm = true;

							if (!validateUser()) {
								e.preventDefault();
								isValidForm = false;
							}

							if (validateUser()) {
								console.log("editAdminParentSave inside call "
										+ profileName);
								var token_id = $("#token_id").val();
								var profileName = $(
										'#editAdminParentProfileName').val();
								var contactNo = $('#editAdminParentContact')
										.val();
								var emailId = $('#editAdminParentUsername')
										.val();

								var requestData = {
									"name" : profileName,
									"mobile_number" : contactNo,
									"username" : emailId
								};
								makePostAjaxRequest(
										"SaveParentAccountManagement/"
												+ token_id + "/" + user_id,
										requestData,
										onSuccessParentAccountUpdate,
										onFailureParentAccountUpdate);
							} else {
								e.preventDefault();
								return false;
							}
						});
	});

	function onSuccessParentAccountUpdate(responseData) {
		$("#adminParent_update_success").css({
			"display" : "none"
		});
		$("#adminParent_update_failure").css({
			"display" : "none"
		});
		$("#adminParent_delete_success").css({
			"display" : "none"
		});
		$("#adminParent_delete_failure").css({
			"display" : "none"
		});
		$("#editAdminParentProfileName").val("");
		$("#editAdminParentContact").val("");
		$('#editAdminParentUsername').val("");

		var responseCode = '';
		var token_id = $("#token_id").val();
		if (!isDataNull(responseData) && !isDataNull(responseData.Return)
				&& !isDataNull(responseData.Return.ResponseSummary)
				&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {
			responseCode = responseData.Return.ResponseSummary.StatusCode;

			if (responseCode === 'SUC01') {
				$("#searchCriteriaError, #adminParent_update_failure, #adminParent_delete_success, #adminParent_delete_failure").css({
					"display" : "none"
				});
				$("#adminParent_update_success").css({
					"display" : "block"
				});
				loadStudentSearchResults();
			} else {
				$("#searchCriteriaError, #adminParent_update_success, #adminParent_delete_success, #adminParent_delete_failure").css({
					"display" : "none"
				});
				$("#adminParent_update_failure").css({
					"display" : "block"
				});
			}
		} else {
			$("#searchCriteriaError, #adminParent_update_success, #adminParent_delete_success, #adminParent_delete_failure").css({
				"display" : "none"
			});
			$("#adminParent_update_failure").css({
				"display" : "block"
			});
		}
	}

	function onFailureParentAccountUpdate(responseData) {
		$("#adminParent_update_success").css({
			"display" : "none"
		});
		$("#adminParent_update_failure").css({
			"display" : "none"
		});
		$("#adminParent_delete_success").css({
			"display" : "none"
		});
		$("#adminParent_delete_failure").css({
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
		$("#editAdminParentProfileName").val("");
		$("#editAdminParentContact").val("");
		$('#editAdminParentUsername').val("");
		$("#adminParent_update_failure").css({
			"display" : "block"
		});
	}

	$(function() {
		$("#editAdminParentCancel").on("click", function(e) {
			console.log("requestData editAdminParentCancel inside:::::: ");
			$("#adminParent_update_success").css({
				"display" : "none"
			});
			$("#adminParent_update_failure").css({
				"display" : "none"
			});
			$("#adminParent_delete_success").css({
				"display" : "none"
			});
			$("#adminParent_delete_failure").css({
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
			$("#editAdminParentProfileName").val("");
			$("#editAdminParentContact").val("");
			$('#editAdminParentUsername').val("");
		});
	});

	$("#increaseassigendHref").click(
			function() {
				var currentpage = schoolstart;
				currentpage++;
				if (currentpage <= schoolend) {
					schoolstart = currentpage;
					var profileName = $("#searchParentProfileName").val()
							.trim();
					var contactNo = $("#searchParentContact").val().trim();
					var emailId = $("#searchParentUsername").val().trim();
					var uuid = $("#searchParentUuid").val().trim();

					profileName = (profileName.length <= 0) ? 0 : profileName;
					contactNo = (contactNo.length <= 0) ? 0 : contactNo;
					emailId = (emailId.length <= 0) ? 0 : emailId;
					uuid = (uuid.length <= 0) ? 0 : uuid;
					profileName = encodeURIComponent(profileName);
					profileName = profileName.replace(/%25/g, '%2525');
					makeGetAjaxRequest("SearchParentAccountManagement/"
							+ token_id + "/" + profileName
							+ "/" + contactNo + "/" + emailId + "/" + uuid
							+ "/" + currentpage, true, onSearchSuccess,
							onSearchFailure);
				}
			});

	$("#decreaseassigendHref").click(
			function() {
				var currentpage = schoolstart;
				currentpage--;
				if (currentpage > 0) {
					schoolstart = currentpage;
					var profileName = $("#searchParentProfileName").val()
							.trim();
					var contactNo = $("#searchParentContact").val().trim();
					var emailId = $("#searchParentUsername").val().trim();
					var uuid = $("#searchParentUuid").val().trim();

					profileName = (profileName.length <= 0) ? 0 : profileName;
					contactNo = (contactNo.length <= 0) ? 0 : contactNo;
					emailId = (emailId.length <= 0) ? 0 : emailId;
					uuid = (uuid.length <= 0) ? 0 : uuid;
					profileName = encodeURIComponent(profileName);
					profileName = profileName.replace(/%25/g, '%2525');
					makeGetAjaxRequest("SearchParentAccountManagement/"
							+ token_id + "/" + profileName
							+ "/" + contactNo + "/" + emailId + "/" + uuid
							+ "/" + currentpage, true, onSearchSuccess,
							onSearchFailure);
				}
			});

	$("#adminParentDeleteId").click(
			function() {
				var user_id = '';
				user_id = $('#edituser_id').val();
				var token_id = $("#token_id").val();
				console.log("delete user_id " + user_id);
				console.log("delete token_id " + token_id);
				makeGetAjaxRequest("DeleteParentAccountManagement/" + token_id
						+ "/" + user_id, false, onDeleteParentDeleteSuccess,
						onDeleteParentDeleteFailure);
			});

	function onDeleteParentDeleteSuccess(responseData) {
		$("#adminParent_update_success").css({
			"display" : "none"
		});
		$("#adminParent_update_failure").css({
			"display" : "none"
		});
		$("#adminParent_delete_success").css({
			"display" : "none"
		});
		$("#adminParent_delete_failure").css({
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
		$("#editAdminParentProfileName").val("");
		$("#editAdminParentContact").val("");
		$('#editAdminParentUsername').val("");

		var responseCode = '';
		var token_id = $("#token_id").val();

		if (!isDataNull(responseData) && !isDataNull(responseData.Return)
				&& !isDataNull(responseData.Return.ResponseSummary)
				&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {
			responseCode = responseData.Return.ResponseSummary.StatusCode;

			if (responseCode === 'SUC01') {
				console.log("onDeleteParentDeleteSuccess ON> ");
				$("#searchCriteriaError, #adminParent_update_success, #adminParent_update_failure, #adminParent_delete_failure").css({
					"display" : "none"
				});
				$("#adminParent_delete_success").css({
					"display" : "block"
				});
				loadStudentSearchResults();
			} else {
				$("#searchCriteriaError, #adminParent_update_success, #adminParent_update_failure, #adminParent_delete_success").css({
					"display" : "none"
				});
				$("#adminParent_delete_failure").css({
					"display" : "block"
				});
			}
		} else {
			$("#searchCriteriaError, #adminParent_update_success, #adminParent_update_failure, #adminParent_delete_success").css({
				"display" : "none"
			});
			$("#adminParent_delete_failure").css({
				"display" : "block"
			});
		}
	}

	function onDeleteParentDeleteFailure(responseData) {
		console.log("requestData editAdminParentCancel inside:::::: ");
		$("#adminParent_update_success").css({
			"display" : "none"
		});
		$("#adminParent_update_failure").css({
			"display" : "none"
		});
		$("#adminParent_delete_success").css({
			"display" : "none"
		});
		$("#adminParent_delete_failure").css({
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
		$("#editAdminParentProfileName").val("");
		$("#editAdminParentContact").val("");
		$('#editAdminParentUsername').val("");
		$("#adminParent_delete_failure").css({
			"display" : "block"
		});
	}
});

function mergeParentDetails(usrObj) {
	var profileName = unescape(usrObj.attr('profileName'));
	var user_id = usrObj.attr('user_id');
	var contactNo = usrObj.attr('contactNo');
	var emailId = usrObj.attr('emailId');
	var source = usrObj.attr('source');
	var token_id = $("#token_id").val();

	$("#editAdminParentProfileName").val(profileName);
	$("#editAdminParentContact").val(contactNo);
	$("#editAdminParentUsername").val(emailId);
	$("#edituser_id").val(user_id);

	if (source == 'External') {
		$("#editAdminParentUsername").attr('disabled', true)
	} else {
		$("#editAdminParentUsername").attr('disabled', false)
	}
}

function deleteParentDetails(usrObj) {
	console.log("success  deleteParentDetails");
	var user_id = usrObj.attr('user_id');
	$("#edituser_id").val(user_id);
}

function increaseassigendHref() {
	var currentpage = schoolstart;
	currentpage++;
	if (currentpage <= schoolend) {
		schoolstart = currentpage;
		var profileName = $("#searchParentProfileName").val().trim();
		var contactNo = $("#searchParentContact").val().trim();
		var emailId = $("#searchParentUsername").val().trim();
		var uuid = $("#searchParentUuid").val().trim();

		profileName = (profileName.length <= 0) ? 0 : profileName;
		contactNo = (contactNo.length <= 0) ? 0 : contactNo;
		emailId = (emailId.length <= 0) ? 0 : emailId;
		uuid = (uuid.length <= 0) ? 0 : uuid;
		profileName = encodeURIComponent(profileName);
		profileName = profileName.replace(/%25/g, '%2525');
		makeGetAjaxRequest("SearchParentAccountManagement/" + token_id + "/"
				+ profileName + "/" + contactNo + "/"
				+ emailId + "/" + uuid + "/" + currentpage, true,
				onSearchSuccess, onSearchFailure);
	}
}

function decreaseassigendHref() {
	var currentpage = schoolstart;
	currentpage--;
	if (currentpage > 0) {
		schoolstart = currentpage;
		var profileName = $("#searchParentProfileName").val().trim();
		var contactNo = $("#searchParentContact").val().trim();
		var emailId = $("#searchParentUsername").val().trim();
		var uuid = $("#searchParentUuid").val().trim();

		profileName = (profileName.length <= 0) ? 0 : profileName;
		contactNo = (contactNo.length <= 0) ? 0 : contactNo;
		emailId = (emailId.length <= 0) ? 0 : emailId;
		uuid = (uuid.length <= 0) ? 0 : uuid;
		profileName = encodeURIComponent(profileName);
		profileName = profileName.replace(/%25/g, '%2525');
		makeGetAjaxRequest("SearchParentAccountManagement/" + token_id + "/"
				+ profileName + "/" + contactNo + "/"
				+ emailId + "/" + uuid + "/" + currentpage, true,
				onSearchSuccess, onSearchFailure);
	}
}
