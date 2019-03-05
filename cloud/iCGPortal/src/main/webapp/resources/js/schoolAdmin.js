$(function() {
	$('#schoolAdminContact').on('change keyup', function() {
		// Remove invalid characters
		var sanitized = $(this).val().replace(/[^0-9]/g, '');
		$(this).val(sanitized);
	});

	function contactStartCheck(contact) {
		return /^[0][9]$/.test(contact);
	}
	function contactCheck(contact) {
		return /^[0-9]{9}$/.test(contact);
	}
	var conatinsNumber = false;
	var conatinsLetter = false;
	var conatins8Char = false;
	$("#schoolAdminChangePwd").focus(function() {
		var newPwd = $(this).val();
		passwordCheck(newPwd);
	});
	$("#schoolAdminChangePwd").keyup(function() {
		var key = event.keyCode || event.charCode;
		if (key == 8 || key == 46) {
		}
		var newPwd = $(this).val();
		passwordCheck(newPwd);

	});

	function passwordCheck(newPwd) {
		/*
		 * to check number in password exists when the backspace and delete is
		 * pressed
		 */
		if (hasNumber(newPwd)) {
			conatinsNumber = true;
			$("#schoolAdminCheckNumber").css("display", "block");
			$("#schoolAdminWarningNumber").css("display", "none");
			var checkClass = $("#schoolAdminNumberCheck").hasClass(
					"text-danger");
			if (checkClass) {
				$("#schoolAdminNumberCheck").removeClass("text-danger");
			}
			$("#schoolAdminNumberCheck").addClass("text-success");
		} else {
			$("#schoolAdminCheckNumber").css("display", "none");
			$("#schoolAdminWarningNumber").css("display", "block");
			$("#schoolAdminNumberCheck").addClass("text-danger");
		}

		/* to check characters in password exists */
		if (hasLetter(newPwd)) {
			conatinsLetter = true;
			$("#schoolAdminCheckLetter").css("display", "block");
			$("#schoolAdminWarningLetter").css("display", "none");
			var checkClass = $("#schoolAdminLetterCheck").hasClass(
					"text-danger");
			if (checkClass) {
				$("#schoolAdminLetterCheck").removeClass("text-danger");
			}
			$("#schoolAdminLetterCheck").addClass("text-success");

		} else {
			$("#schoolAdminCheckLetter").css("display", "none");
			$("#schoolAdminWarningLetter").css("display", "block");
			$("#schoolAdminLetterCheck").addClass("text-danger");
		}

		/* to check 8 characters in password exists */
		if (newPwd.length >= 8) {
			conatins8Char = true;
			$("#schoolAdminCheckChar").css("display", "block");
			$("#schoolAdminWarningChar").css("display", "none");
			var checkClass = $("#schoolAdminCharLongCheck").hasClass(
					"text-danger");
			if (checkClass) {
				$("#schoolAdminCharLongCheck").removeClass("text-danger");
			}
			$("#schoolAdminCharLongCheck").addClass("text-success");
		} else {
			$("#schoolAdminCheckChar").css("display", "none");
			$("#schoolAdminWarningChar").css("display", "block");
			$("#schoolAdminCharLongCheck").addClass("text-danger");
		}

	}

	$("#submitbutton")
			.on(
					"click",
					function() {
						$("#mobilemsg").text("");
						$("#passmsg").text("");
						$("#confmsg").text("");
						var contactnumber = $("#mobileNumber").val()
						var password = $("#pass").val()
						var confpassword = $("#cpass").val()

						if (!$.trim($('#name').val())
								|| $.trim($('#name').val()).length < 1
								|| $.trim($('#name').val()).length > 45) {
							$("#pnamemsg")
									.text(
											"Name cannot be blank. It should be more than 1 character");
							return false;
						}

						if ($(mobileNumber).val()) {
							if (!phoneRegEx.test(contactnumber)) {
								$("#mobilemsg")
										.text(
												"Invalid Phone Number. Enter 10 numbers with 09 start");
								return false;
							}
						}
						if (!$(pass).val() || !$.trim($(pass).val())) {
							// $("#passmsg").text("Password cannot be empty");
							// return false;
						}
						if (!$(pass).val()) {
						} else {
							if (!$(cpass).val() || !$.trim($(cpass).val())) {
								$("#confmsg").text(
										"Confirm Password cannot be empty");
								return false;
							}
						}
						if (!$(cpass).val()) {
						} else {
							if (password != confpassword) {
								$("#confmsg")
										.text(
												"Password and Confirm Password Should be Equal");
								return false;
							}
						}
						var check = validatePassword();
						if (check == false) {
							document.getElementById("passmsg").innerHTML = "Invalid Password";
							return false;
						}
					});

	function validatePassword() {
		var pass = document.getElementById("pass");
		var passregu = document.getElementById("pass").value;
		var minNumberofChars = 8;
		var maxNumberofChars = 45;
		var regularExpression = new RegExp(
				"^(?=.*[a-z])(?=.*[0-9])(?=.*[~!@#$%^&*_])");
		if (pass.value.length > 0) {
			if ((pass.value.length < minNumberofChars)
					|| (pass.value.length > maxNumberofChars)) {
				return false;
			}
			if (!regularExpression.test(passregu)) {
				return false;
			}
		}
	}

	function validate() {
		var pass = document.userForm.pass.value;
		var minNumberofChars = 6;
		var maxNumberofChars = 45;
		var regularExpression = new RegExp("/^[a-zA-Z]$/");
		if (pass.length > 0) {
			if (pass == null || pass == " " || (pass.length < minNumberofChars)
					|| (pass.length > maxNumberofChars)) {
				alert("Password cannot be blank/It should be between 1 to 45 characters");
				return false;
			}
			if (regularExpression.test(pass)) {
				alert("password should contain atleast one number and one special character");
				return false;
			}
		}
	}

	var SPLIT_ID = ':';
	var JOIN_ID = ':00';

	$("#date_close").attr('readonly', 'readonly');
	$("#date_close").datepicker({
		dateFormat : 'yy-mm-dd',
		changeMonth : true,
		changeYear : true,
		minDate : '-12Y',
		maxDate : '-3Y'
	});

	$("#date_reopen").attr('readonly', 'readonly');
	$("#date_reopen").datepicker({
		dateFormat : 'yy-mm-dd',
		changeMonth : true,
		changeYear : true,
		minDate : '-12Y',
		maxDate : '-3Y'
	});

	var scheduleList;
	var userList;

	$(document)
			.ready(
					function() {
						
						if($('#csvupload').attr('data-success') != ''){
							$('#csvupload').css("display", "block");
						}
						if($('#invalidcsverror').attr('data-error') != ''){
							$('#invalidcsverror').css("display", "block");
						}
						
						$('input[type=number]').on('mousewheel', function(e) {
							$(this).blur();
						});
						// Disable keyboard scrolling
						$('input[type=number]').on('keydown', function(e) {
							var key = e.charCode || e.keyCode;
							// Disable Up and Down Arrows on Keyboard
							if (key == 38 || key == 40) {
								e.preventDefault();
							} else {
								return;
							}
						});
						
						$('input:submit').attr('disabled', true).css('opacity', '0.6');
						$('input:file').change(function() {
							var fileExtension = 'csv';
							if ($(this).val().split('.').pop().toLowerCase() == fileExtension) {
								fileContentsCheck($("#csvFileUpload")[0].files[0], 'holidays', 'holidays_csverror', 'holidayUpload');
								$("#invalidcsverror").css({"display":"none"});
								$("#holidayErrorDetails").css({"display":"none"});
								$("#holidayUploadCountMsg").css({"display":"none"});
								$("#csvupload").css({"display":"none"});
							}else{
								$('#holidayUpload').attr('disabled', true).css('opacity', '0.6');
							}
						});
						var token_id = $("#token_id").val();

						if (userList) {
							onSuccessUserDetails(scheduleList);
						} else {
							makeGetAjaxRequest("web/user/getUserDetails/"
									+ token_id, false, onSuccessUserDetails,
									onFailureUserDetails);
						}

						if (scheduleList) {
							onSuccessScheduleDetails(scheduleList);
						} else {
							makeGetAjaxRequest("scheduleList/" + token_id,
									false, onSuccessScheduleDetails,
									onFailureScheduleDetails);
						}

					});

	function onSuccessUserDetails(responseData) {
		if (!isDataNull(responseData) && !isDataNull(responseData.Return)
				&& !isDataNull(responseData.Return.Results)) {
			userList = responseData;

			if (!isDataNull(responseData.Return.Results.username)) {
				username = responseData.Return.Results.username;
				$('#schoolAdminUserName').val(username);
			} else {
				username = '';
				$('#schoolAdminUserName').val(username);
			}

			if (!isDataNull(responseData.Return.Results.name)) {
				name = responseData.Return.Results.name;
				$('#schoolAdminProfileName').val(name);
			} else {
				name = '';
				$('#schoolAdminProfileName').val(name);
			}

			if (!isDataNull(responseData.Return.Results.mobileNumber)) {
				usermobileNumber = responseData.Return.Results.mobileNumber;
				$('#schoolAdminContact').val(usermobileNumber);
			} else {
				usermobileNumber = '';
				$('#schoolAdminContact').val(usermobileNumber);
			}
			$('#schoolAdminChangePwd').val('');
			$('#schoolAdminConfirmPwd').val('');
		}

	}

	function onFailureUserDetails(responseData) {
		console.log("Inside onFailureUserDetails");
	}

	function onSuccessScheduleDetails(responseData) {
		if (!isDataNull(responseData) && !isDataNull(responseData.Return)
				&& !isDataNull(responseData.Return.Results)) {
			scheduleList = responseData;

			if (!isDataNull(responseData.Return.Results.schoolinstart)) {
				schoolinstart = responseData.Return.Results.schoolinstart;
				var splittedfields = schoolinstart.split(SPLIT_ID);
				$('#school_in_start_min').val(splittedfields[0]);
				$('#school_in_start_sec').val(splittedfields[1]);
			} else {
				schoolinstart = '00';
				$('#school_in_start_min').val(schoolinstart);
				$('#school_in_start_sec').val(schoolinstart);
			}

			if (!isDataNull(responseData.Return.Results.schoolinend)) {
				schoolinend = responseData.Return.Results.schoolinend;
				var splittedfields = schoolinend.split(SPLIT_ID);
				$('#school_in_end_min').val(splittedfields[0]);
				$('#school_in_end_sec').val(splittedfields[1]);
			} else {
				schoolinend = '00';
				$('#school_in_end_min').val(schoolinend);
				$('#school_in_end_sec').val(schoolinend);
			}

			if (!isDataNull(responseData.Return.Results.schooloutstart)) {
				schooloutstart = responseData.Return.Results.schooloutstart;
				var splittedfields = schooloutstart.split(SPLIT_ID);
				$('#school_out_start_min').val(splittedfields[0]);
				$('#school_out_start_sec').val(splittedfields[1]);
			} else {
				schooloutstart = '00';
				$('#school_out_start_min').val(schooloutstart);
				$('#school_out_start_sec').val(schooloutstart);
			}

			if (!isDataNull(responseData.Return.Results.schooloutend)) {
				schooloutend = responseData.Return.Results.schooloutend;
				var splittedfields = schooloutend.split(SPLIT_ID);
				$('#school_out_end_min').val(splittedfields[0]);
				$('#school_out_end_sec').val(splittedfields[1]);

			} else {
				schooloutend = '00';
				$('#school_out_end_min').val(schooloutend);
				$('#school_out_end_sec').val(schooloutend);
			}

		}

	}

	function onFailureScheduleDetails(responseData) {
		console.log("Inside onFailureScheduleDetails");
	}

	function validateUser() {
		$("#usersubmitMsg").empty();
		$("#usersubmit_success").css({
			"display" : "none"
		});
		$("#schooltimingssubmit_success").css({
			"display" : "none"
		});
		$("#schoolAdminPwdDoesntMatch_err").css({
			"display" : "none"
		});
		$("#schoolAdminChangePwd_invaliderr").css({
			"display" : "none"
		});
		$("#schoolAdminChangePwd_err").css({
			"display" : "none"
		});
		$("#schoolAdminProfileName_err").css({
			"display" : "none"
		});
		$("#schoolAdminProfileName_invaliderr").css({
			"display" : "none"
		});
		$("#schoolAdminContact_err").css({
			"display" : "none"
		});
		$("#schoolAdminContactInValidContactNo").css({
			"display" : "none"
		});

		var password = $("#schoolAdminChangePwd").val();
		var confpassword = $("#schoolAdminConfirmPwd").val();
		var contactnumber = $("#schoolAdminContact").val();

		if (($("#schoolAdminChangePwd").val() != "")
				|| ($("#schoolAdminConfirmPwd").val() != "")) {
			if (!hasNumber(password) || !hasLetter(password)
					|| password.length < 8) {
				$("#schoolAdminChangePwd_invaliderr").css({
					"display" : "block"
				});
				$("#schoolAdminPwdDoesntMatch_err").css({
					"display" : "none"
				});
				return false;
			}
		}
		if (($("#schoolAdminChangePwd").val() != "")
				|| ($("#schoolAdminConfirmPwd").val() != "")) {
			if ($("#schoolAdminChangePwd").val() !== $("#schoolAdminConfirmPwd")
					.val()) {
				$("#schoolAdminPwdDoesntMatch_err").css({
					"display" : "block"
				});
				$("#schoolAdminChangePwd_invaliderr").css({
					"display" : "none"
				});
				return false;
			}
		}
		if ($("#schoolAdminProfileName").val() == "") {
			$("#schoolAdminProfileName_err").css({
				"display" : "block"
			});
			return false;
		}

		if ($("#schoolAdminContact").val() == "") {
			$("#schoolAdminContact_err").css({
				"display" : "block"
			});
			return false;
		}

		if (!phoneRegEx.test(contactnumber)) {
			$("#schoolAdminContactInValidContactNo").css({
				"display" : "block"
			});
			return false;
		}

		return true;
	}
	;

	$('#schoolAdminSave').on(
			"click",
			function() {
				if (validateUser()) {
					showSpinner();
					var userpassword = $('#schoolAdminChangePwd').val();
					var username = $('#schoolAdminProfileName').val();
					var usermobileNumber = $('#schoolAdminContact').val();
					var token_id = $("#token_id").val();

					if (($("#schoolAdminChangePwd").val() != "")
							&& ($("#schoolAdminConfirmPwd").val() != "")) {
						var requestData = {
							"name" : username,
							"mobile_number" : usermobileNumber,
							"password" : userpassword

						};
					} else {
						var requestData = {
							"name" : username,
							"mobile_number" : usermobileNumber

						};
					}
					makePutAjaxRequest("mobile/UserUpdate/" + token_id,
							requestData, onSuccessUserUpdate,
							onFailureUserUpdate);
				} else {
					$("#usersubmitMsg").empty();
					$("#usersubmit_success").css({
						"display" : "none"
					});
					$("#schooltimingssubmit_success").css({
						"display" : "none"
					});

				}
			});

	function onSuccessUserUpdate(responseData) {

		var responseCode = '';
		var responseMessage = '';
		$("#usersubmitMsg").empty();
		$("#usersubmit_success").css({
			"display" : "none"
		});
		$("#schooltimingssubmit_success").css({
			"display" : "none"
		});
		var token_id = $("#token_id").val();

		if (!isDataNull(responseData) && !isDataNull(responseData.Return)
				&& !isDataNull(responseData.Return.ResponseSummary)
				&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {

			responseCode = responseData.Return.ResponseSummary.StatusCode;

			if (responseCode === 'SUC01') {
				$("#usersubmit_success").css({
					"display" : "block"
				});
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
		$("#usersubmit_success").css({
			"display" : "none"
		});
		$("#schooltimingssubmit_success").css({
			"display" : "none"
		});
		responseMessage = getValueByLanguageKey('icglabel_updatefail');
		$("#usersubmitMsg").append(' <b>' + responseMessage + '</b>');
	}

	$('#schoolAdminCancel').on("click", function() {
		$("#usersubmitMsg").empty();
		$("#usersubmit_success").css({
			"display" : "none"
		});
		$("#schooltimingssubmit_success").css({
			"display" : "none"
		});
		onSuccessUserDetails(userList);
	});

	$('#scheduleSubmitButton')
			.on(
					"click",
					function() {
						showSpinner();
						$("#usersubmit_success").css({
							"display" : "none"
						});
						$("#schooltimingssubmit_success").css({
							"display" : "none"
						});

						var token_id = $("#token_id").val();
						var schoolinstart = $('#school_in_start_min').val()
								+ SPLIT_ID + $('#school_in_start_sec').val()
								+ JOIN_ID;
						var schoolinend = $('#school_in_end_min').val()
								+ SPLIT_ID + $('#school_in_end_sec').val()
								+ JOIN_ID;
						var schooloutstart = $('#school_out_start_min').val()
								+ SPLIT_ID + $('#school_out_start_sec').val()
								+ JOIN_ID;
						var schooloutend = $('#school_out_end_min').val()
								+ SPLIT_ID + $('#school_out_end_sec').val()
								+ JOIN_ID;

						var school_in_start_min = parseInt($(
								'#school_in_start_min').val());
						var school_in_end_min = parseInt($('#school_in_end_min')
								.val());
						var school_in_start_sec = parseInt($(
								'#school_in_start_sec').val());
						var school_in_end_sec = parseInt($('#school_in_end_sec')
								.val());
						var school_out_start_min = parseInt($(
								'#school_out_start_min').val());
						var school_out_end_min = parseInt($(
								'#school_out_end_min').val());
						var school_out_start_sec = parseInt($(
								'#school_out_start_sec').val());
						var school_out_end_sec = parseInt($(
								'#school_out_end_sec').val());

						if (!validateTimeRange(school_in_start_min,
								school_in_end_min, school_in_start_sec,
								school_in_end_sec)) {
							$("#submitMsg").empty();
							$("#submitMsg")
									.append(
											'School In Start Time must be lesser than School In End Time ');
							hideSpinnerNow();
						} else if (!validateTimeRange(school_in_end_min,
								school_out_start_min, school_in_end_sec,
								school_out_start_sec)) {
							$("#submitMsg").empty();
							$("#submitMsg")
									.append(
											'School In End Time must be lesser than School Out Start Time ');
							hideSpinnerNow();
						} else if (!validateTimeRange(school_out_start_min,
								school_out_end_min, school_out_start_sec,
								school_out_end_sec)) {
							$("#submitMsg").empty();
							$("#submitMsg")
									.append(
											'School Out Start Time must be lesser than School Out End Time ');
							hideSpinnerNow();
						} else {

							var requestData = {
								"schoolinstart" : schoolinstart,
								"schoolinend" : schoolinend,
								"schooloutstart" : schooloutstart,
								"schooloutend" : schooloutend
							};

							makePostAjaxRequest("saveSchedule/" + token_id,
									requestData, onSuccessScheduleUpdate,
									onFailureScheduleUpdate);
						}

					});

	function validateTimeRange(school_in_start_min, school_in_end_min,
			school_in_start_sec, school_in_end_sec) {

		if (school_in_start_min <= school_in_end_min) {
			if (school_in_start_min == school_in_end_min) {
				if (school_in_start_sec < school_in_end_sec) {
					return true;
				} else {
					return false;
				}

			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	function onSuccessScheduleUpdate(responseData) {

		var responseCode = '';
		var responseMessage = '';
		$("#submitMsg").empty();
		$("#usersubmit_success").css({
			"display" : "none"
		});
		$("#schooltimingssubmit_success").css({
			"display" : "none"
		});
		var token_id = $("#token_id").val();

		if (!isDataNull(responseData) && !isDataNull(responseData.Return)
				&& !isDataNull(responseData.Return.ResponseSummary)
				&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {

			responseCode = responseData.Return.ResponseSummary.StatusCode;

			if (responseCode === 'SUC01') {
				$("#schooltimingssubmit_success").css({
					"display" : "block"
				});
			} else if (responseCode === 'ERR01') {
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#submitMsg").append(' <b>' + responseMessage + '</b>');
				responseMessage = '';
			} else if (responseCode === 'ERR03') {
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#submitMsg").append(' <b>' + responseMessage + '</b>');
				responseMessage = '';
			} else if (responseCode === 'ERR04') {
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#submitMsg").append(' <b>' + responseMessage + '</b>');
				responseMessage = '';
			} else if (responseCode === 'ERR05') {
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#submitMsg").append(' <b>' + responseMessage + '</b>');
				responseMessage = '';
			} else {
				responseMessage = getValueByLanguageKey('icglabel_updatefail');
				$("#submitMsg").append(' <b>' + responseMessage + '</b>');
			}
		} else {
			responseMessage = getValueByLanguageKey('icglabel_updatefail');
			$("#submitMsg").append(' <b>' + responseMessage + '</b>');
		}
	}

	function onFailureScheduleUpdate(responseData) {
		responseMessage = 'OOPS !! Something went wrong';
		$("#usersubmit_success").css({
			"display" : "none"
		});
		$("#schooltimingssubmit_success").css({
			"display" : "none"
		});
		$("#submitMsg").empty();
		$("#submitMsg").append(' <b>' + responseMessage + '</b>.');
	}

	$('#scheduleCancelButton').on("click", function() {
		$("#submitMsg").empty();
		$("#usersubmit_success").css({
			"display" : "none"
		});
		$("#schooltimingssubmit_success").css({
			"display" : "none"
		});
		onSuccessScheduleDetails(scheduleList);

	});

});
