$('.accountmanage').removeClass("treeview").addClass("active");
$('.accountmanage').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$("#sAccountManageIcon")
		.attr("src",
				"resources/images/SuperAdminSideBarIcons/White_Icon/account_managment.png");

$('#adminProfileSave').on(
		"click",
		function() {

			if (validateUser()) {
				var email = $("#adminUserName").val();
				var userpassword = $('#adminChangePwd').val();
				var username = $('#adminProfileName').val();
				var usermobileNumber = $('#adminContact').val();
				var token_id = $("#token_id").val();

				if (($("#adminChangePwd").val() != "")
						&& ($("#adminConfirmPwd").val() != "")) {
					var requestData = {
						"username" : email,
						"name" : username,
						"mobile_number" : usermobileNumber,
						"password" : userpassword
					};
				} else {
					var requestData = {
						"username" : email,
						"name" : username,
						"mobile_number" : usermobileNumber
					};
				}
				makePutAjaxRequest("mobile/UserUpdate/" + token_id,
						requestData, onSuccessUserUpdate, onFailureUserUpdate);
			} else {
				$("#usersubmitMsg").empty();
			}
		});

function validateUser() {
	$("#usersubmit_success").css({"display":"none"});
	$("#usersubmitMsg").empty

	var filter = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;

	var password = $("#adminChangePwd").val();
	var confpassword = $("#adminConfirmPwd").val();
	var contactnumber = $("#adminContact").val();
	var email = $("#adminUserName").val();

	if ($("#adminUserName").val() == "") {
		$("#adminUserName_err").css({
			"display" : "block"
		});
		return false;
	} else {
		$("#adminUserName_err").css({
			"display" : "none"
		});
	}
	if (!filter.test(email)) {
		$("#adminUserName_invaiderr").css({
			"display" : "block"
		});
		return false;
	} else {
		$("#adminUserName_invaiderr").css({
			"display" : "none"
		});
	}

	if (($("#adminChangePwd").val() != "")
			|| ($("#adminConfirmPwd").val() != "")) {
		if (!hasNumber(password) || !hasLetter(password)
				|| !(password.length >= 8) || !(password.length <= 12)) {
			$("#adminChangePwd_invaliderr").css({
				"display" : "block"
			});
			return false;
		} else {
			$("#adminChangePwd_invaliderr").css({
				"display" : "none"
			});
		}
	}
	if (($("#adminChangePwd").val() != "")
			|| ($("#adminConfirmPwd").val() != "")) {
		if ($("#adminChangePwd").val() !== $("#adminConfirmPwd").val()) {
			$("#adminChangePwdDoesntMatch_err").css({
				"display" : "block"
			});
			return false;
		} else {
			$("#adminChangePwdDoesntMatch_err").css({
				"display" : "none"
			});
		}
	}
	if ($("#adminProfileName").val().trim() === "") {
		$("#adminProfileName_err").css({
			"display" : "block"
		});
		return false;
	} else {
		$("#adminProfileName_err").css({
			"display" : "none"
		});
		/*if (hasNumber($("#adminProfileName").val())) {
			$("#adminProfileName_invaliderr").css({
				"display" : "block"
			});
			return false;

		} else {
			$("#adminProfileName_invaliderr").css({
				"display" : "none"
			});
		}*/
	}

	if ($("#adminContact").val().trim() === "") {
		$("#adminContact_err").css({
			"display" : "block"
		});
		return false;
	} else {
		$("#adminContact_err").css({
			"display" : "none"
		});
	}

	if (!phoneRegEx.test(contactnumber)) {
		$("#adminContactInValidContactNo").css({
			"display" : "block"
		});
		return false;
	} else {
		$("#adminContactInValidContactNo").css({
			"display" : "none"
		});
		return true;
	}

};

function onSuccessUserUpdate(responseData) {

	var responseCode = '';
	var responseMessage = '';
	$("#usersubmitMsg").empty();
	var token_id = $("#token_id").val();
	$("#usersubmit_success").css({"display":"none"});

	if (!isDataNull(responseData) && !isDataNull(responseData.Return)
			&& !isDataNull(responseData.Return.ResponseSummary)
			&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {

		responseCode = responseData.Return.ResponseSummary.StatusCode;
		responseMessage = getValueByLanguageKey('icglabel_updatefail');
		
		if (responseCode === 'SUC01') {
			$("#usersubmit_success").css({"display":"block"});
		} else if (responseCode === 'ERR01') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>');
		} else if (responseCode === 'ERR03') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>');
		} else if (responseCode === 'ERR04') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>');
		} else if (responseCode === 'ERR05') {
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>');
		} else {
			responseMessage = getValueByLanguageKey('icglabel_updatefail');
			$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>');
		}
	} else {
		responseMessage = getValueByLanguageKey('icglabel_updatefail');
		$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>');
	}
}

function onFailureUserUpdate(responseData) {
	responseMessage = getValueByLanguageKey('icglabel_updatefail');
	$("#usersubmitMsg").empty();
	$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>');
	$("#usersubmit_success").css({"display":"none"});
}

// for update button clicked :::

$(document)
		.ready(

				function() {
					$("#usersubmit_success").css({"display":"none"});
					$("#usersubmitMsg").empty();
					$('#adminProfileCancel').on("click", function() {
						$("#usersubmitMsg").empty();
						window.location.reload();
					});
					var roleType = $("#roleType").val();
					if (roleType == "super_admin") {
						$("#adminUserName").removeAttr('disabled');
					}
					var token_id = $("#token_id").val();
					makeGetAjaxRequest("mobile/UserDetails/" + token_id, false,
							onSuccessParentUserDetails,
							onFailureParentUserDetails);

					function onSuccessParentUserDetails(responseData) {
						cancelData = responseData;
						var name = responseData.Return.Results.name;
						if (!isDataNull(responseData)) {
							if (!isDataNull(responseData.Return.Results.username)) {
								username = responseData.Return.Results.username;
								$('#adminUserName').val(username);
							} else {
								username = '';
								$('#adminUserName').val(username);
							}

							if (!isDataNull(responseData.Return.Results.name)) {
								name = responseData.Return.Results.name;
								$('#adminProfileName').val(name);
							} else {
								name = '';
								$('#adminProfileName').val(name);
							}

							if (!isDataNull(responseData.Return.Results.mobile_number)) {
								usermobileNumber = responseData.Return.Results.mobile_number;
								$('#adminContact').val(usermobileNumber);
							} else {
								usermobileNumber = '';
								$('#adminContact').val(usermobileNumber);
							}
							$('#adminChangePwd').val('');
							$('#adminConfirmPwd').val('');
						}

					}

					function onFailureParentUserDetails(responseData) {
						debugLogs("Inside onFailureParentUserDetails");
					}
					$("#adminChangePwd").focus(function() {
						var newPwd = $(this).val();
						passwordCheck(newPwd);
					});
					$("#adminChangePwd").keyup(function() {
						var key = event.keyCode || event.charCode;
						if (key == 8 || key == 46) {
						}
						var newPwd = $(this).val();
						passwordCheck(newPwd);

					});

					function passwordCheck(newPwd) {
						/*
						 * to check number in password exists when the backspace
						 * and delete is pressed
						 */
						if (hasNumber(newPwd)) {
							conatinsNumber = true;
							$("#adminCheckNumber").css("display", "block");
							$("#adminWarningNumber").css("display", "none");
							var checkClass = $("#adminNumberCheck").hasClass(
									"text-danger");
							if (checkClass) {
								$("#adminNumberCheck").removeClass(
										"text-danger");
							}
							$("#adminNumberCheck").addClass("text-success");
						} else {
							$("#adminCheckNumber").css("display", "none");
							$("#adminWarningNumber").css("display", "block");
							$("#adminNumberCheck").addClass("text-danger");
						}

						/* to check characters in password exists */
						if (hasLetter(newPwd)) {
							conatinsLetter = true;
							$("#adminCheckLetter").css("display", "block");
							$("#adminWarningLetter").css("display", "none");
							var checkClass = $("#adminLetterCheck").hasClass(
									"text-danger");
							if (checkClass) {
								$("#adminLetterCheck").removeClass(
										"text-danger");
							}
							$("#adminLetterCheck").addClass("text-success");

						} else {
							// if (removedChar) { /*when the backspace and
							// delete is pressed*/
							$("#adminCheckLetter").css("display", "none");
							$("#adminWarningLetter").css("display", "block");
							$("#adminLetterCheck").addClass("text-danger");
							// }
						}

						/* to check 8 characters in password exists */
						if (newPwd.length >= 8) {
							conatins8Char = true;
							$("#adminCheckChar").css("display", "block");
							$("#adminWarningChar").css("display", "none");
							var checkClass = $("#adminCharLongCheck").hasClass(
									"text-danger");
							if (checkClass) {
								$("#adminCharLongCheck").removeClass(
										"text-danger");
							}
							$("#adminCharLongCheck").addClass("text-success");
						} else {
							// delete is pressed*/
							$("#adminCheckChar").css("display", "none");
							$("#adminWarningChar").css("display", "block");
							$("#adminCharLongCheck").addClass("text-danger");
						}

					}
				});