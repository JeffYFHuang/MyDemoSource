$('.account_mgmt').removeClass("treeview").addClass("active");
$("#sAccountIcon").attr("src",
		"resources/images/sidemenu_icon/white/account_mangmet_w.png");

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
				|| pass.length > maxNumberofChars) {
			return false;
		}
		if (regularExpression.test(pass)) {
			return false;
		}
	}
}

function onSuccessUserDetails(responseData) {
	if (!isDataNull(responseData)) {
		userList = responseData;
		if (!isDataNull(responseData.email)) {
			username = responseData.email;
			$('#parentAdminUserName').val(username);
		} else {
			username = '';
			$('#parentAdminUserName').val(username);
		}

		if (!isDataNull(responseData.name)) {
			name = responseData.name;
			$('#parentAdminProfileName').val(name);
		} else {
			name = '';
			$('#parentAdminProfileName').val(name);
		}

		if (!isDataNull(responseData.mobileNumber)) {
			usermobileNumber = responseData.mobileNumber;
			$('#parentAdminContact').val(usermobileNumber);
		} else {
			usermobileNumber = '';
			$('#parentAdminContact').val(usermobileNumber);
		}
		$('#parentAdminChangePwd').val('');
		$('#parentAdminConfirmPwd').val('');
	}
}

function onFailureUserDetails(responseData) {
	debugLogs("Inside onFailureUserDetails");
}

$('#parentAdminSave').on(
		"click",
		function() {
			showSpinner();
			if (validateUser()) {
				var userpassword = $('#parentAdminChangePwd').val();
				var username = $('#parentAdminProfileName').val();
				var usermobileNumber = $('#parentAdminContact').val();
				var token_id = $("#token_id").val();

				if (($("#parentAdminChangePwd").val() != "")
						&& ($("#parentAdminConfirmPwd").val() != "")) {
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
						requestData, onSuccessUserUpdate, onFailureUserUpdate);
			} else {
				hideSpinnerNow();
				$("#usersubmitMsg").empty();
			}
		});

function validateUser() {
	var phoneRegEx = /((?=(09))[0-9]{10})$/g;
	var filter = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
	var re = /^09[0-9]{8}$/;

	var password = $("#parentAdminChangePwd").val();
	var confpassword = $("#parentAdminConfirmPwd").val();
	var contactnumber = $("#parentAdminContact").val();

	if ($("#parentAdminUserName").val() == "") {
		$("#parentAdminUserName_err").css({
			"display" : "block"
		});
		return false;
	} else {
		$("#parentAdminUserName_err").css({
			"display" : "none"
		});
	}

	if (($("#parentAdminChangePwd").val() != "")
			|| ($("#parentAdminConfirmPwd").val() != "")) {
		if (!hasNumber(password) || !hasLetter(password)
				|| !(password.length >= 8) || !(password.length <= 12)) {
			$("#parentAdminChangePwd_invaliderr").css({
				"display" : "block"
			});
			return false;
		} else {
			$("#parentAdminChangePwd_invaliderr").css({
				"display" : "none"
			});
		}
	}
	if (($("#parentAdminChangePwd").val() != "")
			|| ($("#parentAdminConfirmPwd").val() != "")) {
		if ($("#parentAdminChangePwd").val() !== $("#parentAdminConfirmPwd")
				.val()) {
			$("#parentAdminPwdDoesntMatch_err").css({
				"display" : "block"
			});
			return false;
		} else {
			$("#parentAdminPwdDoesntMatch_err").css({
				"display" : "none"
			});
		}
	}
	if ($("#parentAdminProfileName").val().trim() === "") {
		$("#parentAdminProfileName_err").css({
			"display" : "block"
		});
		return false;
	} else {
		$("#parentAdminProfileName_err").css({
			"display" : "none"
		});
	}
	if ($("#parentAdminContact").val().trim() === "") {
		$("#parentAdminContact_err").css({
			"display" : "block"
		});
		return false;
	} else {
		$("#parentAdminContact_err").css({
			"display" : "none"
		});
	}
	if (!phoneRegEx.test(contactnumber)) {
		$("#parentAdminContactInValidContactNo").css({
			"display" : "block"
		});
		return false;
	} else {
		$("#parentAdminContactInValidContactNo").css({
			"display" : "none"
		});
		return true;
	}
};

function makePutAjaxRequest(requestURL, requestData, onSuccess, onFailure) {
	$.ajax({
		url : requestURL,
		data : JSON.stringify(requestData),
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		type : "PUT",
		crossDomain : true,
		async : true,
		success : function(response) {
			onSuccess(response);
		},
		error : function(jqXHR, exception) {
			onFailure(jqXHR);
		}
	});
}

function onSuccessUserUpdate(responseData) {
	var responseCode = '';
	var responseMsg = '';
	$("#usersubmitMsg").empty();
	var token_id = $("#token_id").val();

	if (!isDataNull(responseData) && !isDataNull(responseData.Return)
			&& !isDataNull(responseData.Return.ResponseSummary)
			&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {
		responseCode = responseData.Return.ResponseSummary.StatusCode;
		if (responseCode === 'SUC01') {
			responseMessage = getValueByLanguageKey('icglabel_updatesuccess');
			$("#usersubmitMsg").append('<strong><font color="green">' + responseMessage + '</font></strong>');
			responseMsg = '';
		} else {
			responseMessage = getValueByLanguageKey('icglabel_updatefail');
			$("#usersubmitMsg").append('<strong><font color="red">' + responseMessage + '</font></strong>');
			responseMsg = '';
		}
	} else {
		responseMessage = getValueByLanguageKey('icglabel_updatefail');
		$("#usersubmitMsg").append('<strong><font color="red">' + responseMessage + '</font></strong>');
		responseMsg = '';
	}
}

function onFailureUserUpdate(responseData) {
	var responseMessage = getValueByLanguageKey('icglabel_updatefail');
	$("#usersubmitMsg").empty();
	$("#usersubmitMsg").append('<strong><font color="red">' + responseMessage + '</font></strong>');
	debugLogs("Inside onFailureUserUpdate");
}

$(document)
		.ready(
				function() {
					if ($('#new_parent').length && $('#new_parent').val() == 1) {
						$("ol#bread, .new_parent").css('display', 'none');
					}

					var token_id = $("#token_id").val();
					var device_uuid = sessionStorage.getItem("device_uuid");

					var cancelData;

					var profileName = $('#parentAdminProfileName').val();
					var usermobileNumber = $('#parentAdminContact').val();
					
					if(!isDataNull(device_uuid)){
						makeGetAjaxRequest("mobile/SupportedEventsList/" + token_id
								+ "/" + device_uuid, false,
								onSuccessParentSubscription,
								onFailureParentSubscription);
					}
					$('#sNickName').text(sessionStorage.getItem("nick_name"));
					$('#parentAdminCancel')
							.on(
									"click",
									function() {
										$("#usersubmitMsg").empty();
										window.location.reload();
									});

					function onSuccessParentSubscription(responseData) {
						if (!isDataNull(responseData)) {
							for ( var i in responseData.Return.Results) {
								if (!isDataNull(responseData.Return.Results[i].event_id)) {
									if (!isDataNull(responseData.Return.Results[i].event_name)) {
										var event_name = responseData.Return.Results[i].event_name;
										event_name = event_name.replace(/ /g,
												"_");
										if (event_name === 'School_Enter') {
											if (!isDataNull(responseData.Return.Results[i].subscribed)) {
												var schoolEnterSubscribed = responseData.Return.Results[i].subscribed;
												if (schoolEnterSubscribed === 'yes') {
													$('#School_Enter').attr(
															'checked',
															'checked')
												} else {
													$('#School_Enter').prop(
															'unchecked')
												}
											}
										} else if (event_name === 'Geofence_Entry_') {
											if (!isDataNull(responseData.Return.Results[i].subscribed)) {
												var geoFenceEntrySubscribed = responseData.Return.Results[i].subscribed;
												if (geoFenceEntrySubscribed === 'yes') {
													$('#Geofence_Entry').attr(
															'checked',
															'checked')
												} else {
													$('#Geofence_Entry').prop(
															'unchecked')
												}
											}
										} else if (event_name === 'SOS_Alert') {
											if (!isDataNull(responseData.Return.Results[i].subscribed)) {
												var sosfAlertSubscribed = responseData.Return.Results[i].subscribed;
												if (sosfAlertSubscribed === 'yes') {
													$('#SOS_Alert').attr(
															'checked',
															'checked')
												} else {
													$('#SOS_Alert').prop(
															'unchecked')
												}
											}
										} else if (event_name === 'Band_Removal_Alert') {
											if (!isDataNull(responseData.Return.Results[i].subscribed)) {
												var bandRemovalAlertSubscribed = responseData.Return.Results[i].subscribed;
												if (bandRemovalAlertSubscribed === 'yes') {
													$('#Band_Removal_Alert')
															.attr('checked',
																	'checked')
												} else {
													$('#Band_Removal_Alert')
															.prop('unchecked')
												}
											}
										} else if (event_name === 'Abnormal_Vital_Sign') {
											if (!isDataNull(responseData.Return.Results[i].subscribed)) {
												var abnormalVitalSignAlertSubscribed = responseData.Return.Results[i].subscribed;
												if (abnormalVitalSignAlertSubscribed === 'yes') {
													$('#Abnormal_Vital_Sign')
															.attr('checked',
																	'checked')
												} else {
													$('#Abnormal_Vital_Sign')
															.prop('unchecked')
												}
											}
										}else if (event_name === 'Report_Summary') {
											if (!isDataNull(responseData.Return.Results[i].subscribed)) {
												var reportSummarySubscribed = responseData.Return.Results[i].subscribed;
												if (reportSummarySubscribed === 'yes') {
													$('#Report_Summary')
															.attr('checked',
																	'checked')
												} else {
													$('#Report_Summary')
															.prop('unchecked')
												}
											}
										}
									}
								}
							}
						}
					}

					function onFailureParentSubscription(responseData) {
						debugLogs("Inside onFailureParentSubscription");
					}

					$('#saveButton')
							.on(
									"click",
									function() {
										showSpinner();
										var token_id = $("#token_id").val();
										var student_id = sessionStorage
												.getItem("student_id");
										var School_Enter_Event = 1;
										var Geofence_Entry_Event = 3;
										var Report_Summary_Event = 12;
										var SOS_Alert_Event = 13;
										var Abnormal_Vital_Sign_Event = 16;
										var Band_Removal_Alert_Event = 20;

										var School_Enter = $('#School_Enter')
												.is(':checked');
										if (School_Enter) {
											School_Enter = 'yes';
										} else {
											School_Enter = 'no';
										}
										var Geofence_Entry = $(
												'#Geofence_Entry').is(
												':checked');

										if (Geofence_Entry) {
											Geofence_Entry = 'yes';
										} else {
											Geofence_Entry = 'no';
										}

										var SOS_Alert = $('#SOS_Alert').is(
												':checked');

										if (SOS_Alert) {
											SOS_Alert = 'yes';
										} else {
											SOS_Alert = 'no';
										}

										var Band_Removal_Alert = $(
												'#Band_Removal_Alert').is(
												':checked');

										if (Band_Removal_Alert) {
											Band_Removal_Alert = 'yes';
										} else {
											Band_Removal_Alert = 'no';
										}

										var Abnormal_Vital_Sign = $(
												'#Abnormal_Vital_Sign').is(
												':checked');

										if (Abnormal_Vital_Sign) {
											Abnormal_Vital_Sign = 'yes';
										} else {
											Abnormal_Vital_Sign = 'no';
										}

										var Report_Summary = $(
												'#Report_Summary').is(
												':checked');

										if (Report_Summary) {
											Report_Summary = 'yes';
										} else {
											Report_Summary = 'no';
										}

										var requestData = [
												{
													"eventId" : School_Enter_Event,
													"eventValue" : School_Enter
												},
												{
													"eventId" : Geofence_Entry_Event,
													"eventValue" : Geofence_Entry
												},
												{
													"eventId" : SOS_Alert_Event,
													"eventValue" : SOS_Alert
												},
												{
													"eventId" : Report_Summary_Event,
													"eventValue" : Report_Summary
												},
												{
													"eventId" : Abnormal_Vital_Sign_Event,
													"eventValue" : Abnormal_Vital_Sign
												},
												{
													"eventId" : Band_Removal_Alert_Event,
													"eventValue" : Band_Removal_Alert
												} ];

										makePostAjaxRequest(
												"mobile/SupportedEventsUpdate/"
														+ token_id + "/"
														+ student_id,
												requestData,
												onSuccessSupportedEventsUpdate,
												onFailureSupportedEventsUpdate);
									});

					function onSuccessSupportedEventsUpdate(responseData) {
						$("#saveButtonMsg").empty();
						if (!isDataNull(responseData)
								&& !isDataNull(responseData.Return)
								&& !isDataNull(responseData.Return.ResponseSummary)
								&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {

							responseCode = responseData.Return.ResponseSummary.StatusCode;
							if (responseCode === 'SUC01') {
								responseMessage = getValueByLanguageKey('icglabel_updatesuccess');
								$("#saveButtonMsg").append('<strong><font color="green">' + responseMessage + '</font></strong>');
							} else {
								responseMessage = getValueByLanguageKey('icglabel_updatefail');
								$("#saveButtonMsg").append('<strong><font color="red">' + responseMessage + '</font></strong>');
							}
						}
					}

					function onFailureSupportedEventsUpdate(responseData) {
						responseMessage = getValueByLanguageKey('icglabel_updatefail');
						$("#saveButtonMsg").append('<strong><font color="red">' + responseMessage + '</font></strong>');
					}

					function onSuccessParentSubscriptionUpdate(responseData) {
						debugLogs("Inside onSuccessParentSubscriptionUpdate");
					}

					function onFailureParentSubscriptionUpdate(responseData) {
						debugLogs("Inside onFailureParentSubscriptionUpdate");
					}

					function onSuccessGuardianList(responseData) {
						var School_Enter_Event = 1;
						var Geofence_Entry_Event = 3;
						var Report_Summary_Event = 12;
						var SOS_Alert_Event = 13;
						var Abnormal_Vital_Sign_Event = 16;
						var Band_Removal_Alert_Event = 20

						var School_Enter = $('#School_Enter').is(':checked');
						if (School_Enter) {
							School_Enter = 'yes';
						} else {
							School_Enter = 'no';
						}
						var Geofence_Entry = $('#Geofence_Entry')
								.is(':checked');

						if (Geofence_Entry) {
							Geofence_Entry = 'yes';
						} else {
							Geofence_Entry = 'no';
						}

						var SOS_Alert = $('#SOS_Alert').is(':checked');

						if (SOS_Alert) {
							SOS_Alert = 'yes';
						} else {
							SOS_Alert = 'no';
						}

						var Band_Removal_Alert = $('#Band_Removal_Alert').is(
								':checked');

						if (Band_Removal_Alert) {
							Band_Removal_Alert = 'yes';
						} else {
							Band_Removal_Alert = 'no';
						}

						var Abnormal_Vital_Sign = $('#Abnormal_Vital_Sign').is(
								':checked');

						if (Abnormal_Vital_Sign) {
							Abnormal_Vital_Sign = 'yes';
						} else {
							Abnormal_Vital_Sign = 'no';
						}

						var Report_Summary = $('#Report_Summary')
								.is(':checked');

						if (Report_Summary) {
							Report_Summary = 'yes';
						} else {
							Report_Summary = 'no';
						}

						var requestData = [ {
							"alertId" : School_Enter_Event,
							"alertValue" : School_Enter
						}, {
							"alertId" : Geofence_Entry_Event,
							"alertValue" : Geofence_Entry
						}, {
							"alertId" : Report_Summary_Event,
							"alertValue" : Report_Summary
						}, {
							"alertId" : SOS_Alert_Event,
							"alertValue" : SOS_Alert
						}, {
							"alertId" : Abnormal_Vital_Sign_Event,
							"alertValue" : Abnormal_Vital_Sign
						}, {
							"alertId" : Band_Removal_Alert_Event,
							"alertValue" : Band_Removal_Alert
						} ];

						// alert(JSON.stringify(requestData));
						var token_id = $("#token_id").val();
						var device_uuid = sessionStorage.getItem("device_uuid");

						if (!isDataNull(responseData)) {
							for ( var i in responseData.Return.Results.GuardianNamesWithIds) {
								if (!isDataNull(responseData.Return.Results.GuardianNamesWithIds[i])) {
									makePostAjaxRequest(
											"mobile/GuardianSubscriptionsUpdate/"
													+ token_id
													+ "/"
													+ device_uuid
													+ "/"
													+ responseData.Return.Results.GuardianNamesWithIds[i].user_id,
											requestData,
											onSuccessParentSubscriptionUpdate,
											onFailureParentSubscriptionUpdate);
								}
							}
							reloadAfterSuccessParentSubscriptionUpdate();
						}
					}

					function reloadAfterSuccessParentSubscriptionUpdate() {
						window.location.reload();
					}

					function onFailureGuardianList(responseData) {
						debugLogs("Inside onFailureGuardianList");
					}

					function onSuccessParentUserUpdate(responseData) {
						debugLogs("Inside onSuccessParentUserUpdate");
					}

					function onFailureParentUserUpdate(responseData) {
						debugLogs("Inside onFailureParentUserUpdate");
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
								$('#parentAdminUserName').val(username);
							} else {
								username = '';
								$('#parentAdminUserName').val(username);
							}

							if (!isDataNull(responseData.Return.Results.name)) {
								name = responseData.Return.Results.name;
								$('#parentAdminProfileName').val(name);
							} else {
								name = '';
								$('#parentAdminProfileName').val(name);
							}

							if (!isDataNull(responseData.Return.Results.mobile_number)) {
								usermobileNumber = responseData.Return.Results.mobile_number;
								$('#parentAdminContact').val(usermobileNumber);
							} else {
								usermobileNumber = '';
								$('#parentAdminContact').val(usermobileNumber);
							}
							$('#parentAdminChangePwd').val('');
							$('#parentAdminConfirmPwd').val('');
						}
					}

					function onFailureParentUserDetails(responseData) {
						debugLogs("Inside onFailureParentUserDetails");
					}

					$("#parentAdminChangePwd").focus(function() {
						var newPwd = $(this).val();
						passwordCheck(newPwd);
					});
					$("#parentAdminChangePwd").keyup(function() {
						var key = event.keyCode || event.charCode;
						if (key == 8 || key == 46) {
						}
						var newPwd = $(this).val();
						passwordCheck(newPwd);

					});

					function passwordCheck(newPwd) {
						if (hasNumber(newPwd)) {
							conatinsNumber = true;
							$("#schoolAdminCheckNumber")
									.css("display", "block");
							$("#schoolAdminWarningNumber").css("display",
									"none");
							var checkClass = $("#schoolAdminNumberCheck")
									.hasClass("text-danger");
							if (checkClass) {
								$("#schoolAdminNumberCheck").removeClass(
										"text-danger");
							}
							$("#schoolAdminNumberCheck").addClass(
									"text-success");
						} else {
							$("#schoolAdminCheckNumber").css("display", "none");
							$("#schoolAdminWarningNumber").css("display",
									"block");
							$("#schoolAdminNumberCheck")
									.addClass("text-danger");
						}

						/* to check characters in password exists */
						if (hasLetter(newPwd)) {
							conatinsLetter = true;
							$("#schoolAdminCheckLetter")
									.css("display", "block");
							$("#schoolAdminWarningLetter").css("display",
									"none");
							var checkClass = $("#schoolAdminLetterCheck")
									.hasClass("text-danger");
							if (checkClass) {
								$("#schoolAdminLetterCheck").removeClass(
										"text-danger");
							}
							$("#schoolAdminLetterCheck").addClass(
									"text-success");

						} else {
							// delete is pressed*/
							$("#schoolAdminCheckLetter").css("display", "none");
							$("#schoolAdminWarningLetter").css("display",
									"block");
							$("#schoolAdminLetterCheck")
									.addClass("text-danger");
						}

						/* to check 8 characters in password exists */
						if (newPwd.length >= 8) {
							conatins8Char = true;
							$("#schoolAdminCheckChar").css("display", "block");
							$("#schoolAdminWarningChar").css("display", "none");
							var checkClass = $("#schoolAdminCharLongCheck")
									.hasClass("text-danger");
							if (checkClass) {
								$("#schoolAdminCharLongCheck").removeClass(
										"text-danger");
							}
							$("#schoolAdminCharLongCheck").addClass(
									"text-success");
						} else {
							// delete is pressed*/
							$("#schoolAdminCheckChar").css("display", "none");
							$("#schoolAdminWarningChar")
									.css("display", "block");
							$("#schoolAdminCharLongCheck").addClass(
									"text-danger");
						}
					}
				});