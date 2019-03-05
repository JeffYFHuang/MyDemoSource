var conatinsNumber = false;
var conatinsLetter = false;
var conatins8Char = false;

$(document).ready(function() {
	$("#username").focus();
	if (window.location.pathname.indexOf("SignupActivationRequest") >= 0){
		$("#errorBlock").css("display", "block");
	} else {
		$("#errorBlock").css("display", "none");
	}
	$('#contactnumber').bind('keypress', function(e) {
		if (this.value.length >= 10) {
			return false;
		}
	});

	//var removedChar = false;
	$( "#password" ).focus(function() {
		var newPwd = $(this).val();
		passwordCheck(newPwd);
	});
	$("#password").keyup(function() {
	//	removedChar = false;
		var key = event.keyCode || event.charCode;
		if (key == 8 || key == 46) {
			//removedChar = true;
		}
		var newPwd = $(this).val();
		passwordCheck(newPwd);

	});
});


function passwordCheck(newPwd) {
	conatinsNumber = false;
	conatinsLetter = false;
	conatins8Char = false;
	/*
	 * to check number in password exists when the backspace and delete is
	 * pressed
	 */
	if (hasNumber(newPwd)) {
		conatinsNumber = true;
		$("#checkNumber").css("display", "block");
		$("#warningNumber").css("display", "none");
		var checkClass = $("#numberCheck").hasClass("text-danger");
		if (checkClass) {
			$("#numberCheck").removeClass("text-danger");
		}
		$("#numberCheck").addClass("text-success");
	} else {
		$("#checkNumber").css("display", "none");
		$("#warningNumber").css("display", "block");
		$("#numberCheck").addClass("text-danger");
	}

	/* to check characters in password exists */
	if (hasLetter(newPwd)) {
		conatinsLetter = true;
		$("#checkLetter").css("display", "block");
		$("#warningLetter").css("display", "none");
		var checkClass = $("#letterCheck").hasClass("text-danger");
		if (checkClass) {
			$("#letterCheck").removeClass("text-danger");
		}
		$("#letterCheck").addClass("text-success");

	} else {
		$("#checkLetter").css("display", "none");
		$("#warningLetter").css("display", "block");
		$("#letterCheck").addClass("text-danger");
	}

	/* to check 8 characters in password exists */
	if (newPwd.length >= 8) {
		conatins8Char = true;
		$("#checkChar").css("display", "block");
		$("#warningChar").css("display", "none");
		var checkClass = $("#charLongCheck").hasClass("text-danger");
		if (checkClass) {
			$("#charLongCheck").removeClass("text-danger");
		}
		$("#charLongCheck").addClass("text-success");
	} else {
		$("#checkChar").css("display", "none");
		$("#warningChar").css("display", "block");
		$("#charLongCheck").addClass("text-danger");
	}
}

$("#signupbt").on(
		"click",
		function(e) {
			$("#userAccount").css("display", "none");
			$("#inValidEmail").css("display", "none");
			$("#profileDetails").css("display", "none");
			$("#ValidSignUp").css("display", "none");
			$("#inValidContact").css("display", "none");
			$("#inValidSignUpPassword").css("display", "none");
			$("#emptyPassword").css("display", "none");
			$("#inValidPasswordFormat").css("display", "none");
			$("#acceptTOU").css("display", "none");

			var profilename = $("#accountname").val();
			var username = $("#username").val();
			var contactnumber = $("#contactnumber").val();
			var rem = $('#remember_me').is(':checked');
			var password = $("#password").val();
			var confpassword = $("#confpassword").val();

			var phoneRegEx = /((?=(09))[0-9]{10})$/g;
			var filter = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;
			var re = /^09[0-9]{8}$/;

			if (!filter.test(username)) {
				$("#inValidEmail").css("display", "block");
				return false;
			}
			if (profilename.length == 0) {
				$("#profileDetails").css("display", "block");
				return false;
			}
			if (!phoneRegEx.test(contactnumber)) {
				$("#inValidContact").css("display", "block");
				return false;
			}
			if (password.length == 0) {
				$("#emptyPassword").css("display", "block");
				return false;
			}else{
				var newPwd = $("#password").val();
				passwordCheck(newPwd);
			}
			if (password != confpassword) {
				$("#inValidSignUpPassword").css("display", "block");
				return false;
			}
			if(!conatinsNumber || !conatinsLetter || !conatins8Char){
				$("#inValidPasswordFormat").css("display", "block");
				return false;
			}

			if (!re.test(contactnumber)) {
				$("#inValidContact").css("display", "block");
				return false;
			}
			if(!rem){
				$("#acceptTOU").css("display", "block");
				return false;
			}

			if (profilename !== "" && username !== "" && password !== ""
				&& password === confpassword && contactnumber !== ""
					&& rem !== false) {
				var requestData = {
						"profile_name" : profilename,
						"username" : username,
						"password" : password,
						"account_name" : confpassword,
						"mobile_number" : contactnumber
				};
				makePostAjaxRequest(
						'mobile/ParentUserRegistration',
						requestData, onSuccessPostSubscription,
						onFailurePostSubscription);
			} else {
				e.preventDefault();
			}
		});

function onSuccessPostSubscription(responseData) {
	$("#message").text(responseData.Return.ResponseSummary.StatusMessage);
	var myJSON = JSON.stringify(responseData);
	if (responseData.Return.ResponseSummary.StatusCode === 'ERR24') {
		$("#userAccount").css({
			"display" : "block"
		});
		$("#ValidSignUp").css({
			"display" : "none"
		});
		$('#username').val("");
	} else {
		$("#userAccount").css({
			"display" : "none"
		});
		$("#ValidSignUp").css({
			"display" : "block"
		});
		$('#accountname').val("");
		$('#username').val("");
		$('#contactnumber').val("");
		$('#password').val("");
		$('#confpassword').val("");
	}
}
function onFailurePostSubscription(responseData) {
	debugLogs("Error during ParentUserRegistration");
}