$(document).ready(function(){
	var conatinsNumber = false;
	var conatinsLetter = false;
	var conatins8Char = false;
	var removedChar = false;
	$("#resetNewPwd").keyup(function(){
		removedChar = false;
		var key = event.keyCode || event.charCode;
		if( key == 8 || key == 46 ){
			removedChar = true;
		}
		var newPwd = $(this).val();
		passwordCheck(newPwd);
		
	});
	
	function passwordCheck(newPwd) {


		/*to check number in password exists when the backspace and delete is pressed*/
		if(hasNumber(newPwd)){
			conatinsNumber = true;
			$("#checkNumber").css("display", "block");
			$("#warningNumber").css("display", "none");
			var checkClass = $("#numberCheck").hasClass( "text-danger");
			if(checkClass) {
				$("#numberCheck").removeClass("text-danger");
			}
			$("#numberCheck").addClass("text-success");
		}else {
			if (removedChar) {
				$("#checkNumber").css("display", "none");
				$("#warningNumber").css("display", "block");
				$("#numberCheck").addClass("text-danger");
			}
		}

		/*to check characters in password exists*/
		if(hasLetter(newPwd)){
			conatinsLetter = true;
			$("#checkLetter").css("display", "block");
			$("#warningLetter").css("display", "none");
			var checkClass = $("#letterCheck").hasClass( "text-danger");
			if(checkClass) {
				$("#letterCheck").removeClass("text-danger");
			}
			$("#letterCheck").addClass("text-success");
			

		} else {
			if (removedChar) { /*when the backspace and delete is pressed*/
				$("#checkLetter").css("display", "none");
				$("#warningLetter").css("display", "block");
				$("#letterCheck").addClass("text-danger");
			}
		}

		/*to check 8 characters in password exists*/
		if(newPwd.length >=8){
			conatins8Char = true;
			$("#checkChar").css("display", "block");
			$("#warningChar").css("display", "none");
			var checkClass = $("#charLongCheck").hasClass( "text-danger");
			if(checkClass) {
				$("#charLongCheck").removeClass("text-danger");
			}
			$("#charLongCheck").addClass("text-success");
		}else {
			if (removedChar) {/* when the backspace and delete is pressed*/
				$("#checkChar").css("display", "none");
				$("#warningChar").css("display", "block");
				$("#charLongCheck").addClass("text-danger");
			}
		}

	}
	
	$("#resetConfirmPwd").focus(function(){
		if(!conatins8Char) { 
			$("#checkChar").css("display", "none");
			$("#warningChar").css("display", "block");
			$("#charLongCheck").addClass("text-danger"); 
			document.getElementById("resetConfirmPwd").disabled = 'disabled';
		}
		if(!conatinsNumber) { 
			$("#checkNumber").css("display", "none");
			$("#warningNumber").css("display", "block");
			$("#numberCheck").addClass("text-danger"); 
			document.getElementById("resetConfirmPwd").disabled = 'disabled';
		}
		if(!conatinsLetter) { 
			$("#checkLetter").css("display", "none");
			$("#warningLetter").css("display", "block");
			$("#letterCheck").addClass("text-danger"); 
			document.getElementById("resetConfirmPwd").disabled = 'disabled';
		}
	});

	$("#resetMyPassword").click(function(){
		if($("#resetNewPwd").val() !== $("#resetConfirmPwd").val()) {
			/*document.getElementById("resetMyPassword").disabled = 'disabled';*/
			$(".modal-header").addClass("error");	
			$("#resetError").css("display", "block");

		} 

	});
	
});