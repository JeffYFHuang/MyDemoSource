$(document)
		.ready(
				function() {
					if($('#pwdChangeError').attr('data-error') != ''){
						$('#pwdChangeError').css("display", "block");
					}
					if($('#pwdChangeSuccess').attr('data-success') != ''){
						$('#pwdChangeSuccess').css("display", "block");
					}
					$('#signupbt')
							.click(
									function() {
										if (!conatinsNumber || !conatinsLetter
												|| !conatins8Char) {
											$("#inValidSignUpPassword").css(
													"display", "block");
											return false;
										} else if ($("#password").val() !== $(
												"#cpassword").val()) {
											$(".modal-header")
													.addClass("error");
											$("#inValidSignUpPassword").css(
													"display", "block");
											return false;
										}
									});
					var conatinsNumber = false;
					var conatinsLetter = false;
					var conatins8Char = false;

					$("#password").focus(function() {
						var newPwd = $(this).val();
						passwordCheck(newPwd);

					});
					$("#password").keyup(function() {
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
							$("#checkNumber").css("display", "block");
							$("#warningNumber").css("display", "none");
							var checkClass = $("#numberCheck").hasClass(
									"text-danger");
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
							var checkClass = $("#letterCheck").hasClass(
									"text-danger");
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
							var checkClass = $("#charLongCheck").hasClass(
									"text-danger");
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
					
					getValueByLanguageKey('icglabel_activationsuccess');
					getValueByLanguageKey('icglabel_activationnotrequired');
					getValueByLanguageKey('icglabel_activationfailed');
					getValueByLanguageKey('icglabel_passwordactivationmessage');
				});