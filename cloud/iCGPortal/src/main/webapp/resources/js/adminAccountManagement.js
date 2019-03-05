$(document).ready(
		function() {
			var conatinsNumber = false;
			var conatinsLetter = false;
			var conatins8Char = false;
			var removedChar = false;
			$("#adminAccChangePassword").keyup(function() {
				removedChar = false;
				var key = event.keyCode || event.charCode;
				if (key == 8 || key == 46) {
					removedChar = true;
				}
				var newPwd = $(this).val();
				passwordCheck(newPwd);

			});

			function passwordCheck(newPwd) {
				/*
				 * to check number in password exists when the backspace and
				 * delete is pressed
				 */
				if (hasNumber(newPwd)) {
					conatinsNumber = true;
					$("#adminAccCheckNumber").css("display", "block");
					$("#adminAccWarningNumber").css("display", "none");
					var checkClass = $("#adminAccNumberCheck").hasClass(
							"text-danger");
					if (checkClass) {
						$("#adminAccNumberCheck").removeClass("text-danger");
					}
					$("#adminAccNumberCheck").addClass("text-success");
				} else {
					if (removedChar) {
						$("#adminAccCheckNumber").css("display", "none");
						$("#adminAccWarningNumber").css("display", "block");
						$("#adminAccNumberCheck").addClass("text-danger");
					}
				}

				/* to check characters in password exists */
				if (hasLetter(newPwd)) {
					conatinsLetter = true;
					/* $("#schoolAdminLetterCheck").css("display","block"); */
					$("#adminAccCheckLetter").css("display", "block");
					$("#adminAccWarningLetter").css("display", "none");
					var checkClass = $("#adminAccLetterCheck").hasClass(
							"text-danger");
					if (checkClass) {
						$("#adminAccLetterCheck").removeClass("text-danger");
					}
					$("#adminAccLetterCheck").addClass("text-success");

				} else {
					if (removedChar) { /*
										 * when the backspace and delete is
										 * pressed
										 */
						$("#adminAccCheckLetter").css("display", "none");
						$("#adminAccWarningLetter").css("display", "block");
						$("#adminAccLetterCheck").addClass("text-danger");
					}
				}

				/* to check 8 characters in password exists */
				if (newPwd.length >= 8) {
					conatins8Char = true;
					$("#adminAccCheckChar").css("display", "block");
					$("#adminAccWarningChar").css("display", "none");
					var checkClass = $("#adminAccCharLongCheck").hasClass(
							"text-danger");
					if (checkClass) {
						$("#adminAccCharLongCheck").removeClass("text-danger");
					}
					$("#adminAccCharLongCheck").addClass("text-success");
				} else {
					if (removedChar) {/*
										 * when the backspace and delete is
										 * pressed
										 */
						$("#adminAccCheckChar").css("display", "none");
						$("#adminAccWarningChar").css("display", "block");
						$("#adminAccCharLongCheck").addClass("text-danger");
					}
				}
			}
		});