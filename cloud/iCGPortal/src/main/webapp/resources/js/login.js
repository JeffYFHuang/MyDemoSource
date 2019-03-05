$(document).ready(function(){
	//removeAllItems(); //Remove Old Session Data
	if($('#errorBlock').attr('data-errorBlock') != ''){
		$('#errorBlock').css("display", "block");
	}
	if($('#pwdChangeBlock').attr('data-pwdChangeBlock') != ''){
		$('#pwdChangeBlock').css("display", "block");
	}
	if($('#pwdResetSuccess').attr('data-pwdResetSuccess') != ''){
		var labelKey = '';
		if($('#pwdResetSuccess').attr('data-pwdResetSuccess') == "Successfully Activated."){
			labelKey = "icglabel_activationsuccess";
		}else if($('#pwdResetSuccess').attr('data-pwdResetSuccess') == "Account already activated."){
			labelKey = "icglabel_activationnotrequired";
			
		}else if($('#pwdResetSuccess').attr('data-pwdResetSuccess') == "Activatin code doesn't match."){
			labelKey = "icglabel_activationfailed";
			
		}else if($('#pwdResetSuccess').attr('data-pwdResetSuccess') == "PasswordActivationMessage"){
			labelKey = "icglabel_passwordactivationmessage";
			
		}
		$('#pwdResetSuccess').text(getValueByLanguageKey(labelKey));
		$('#pwdResetSuccess').css("display", "block");
	}
	if($('#pwdResetError').attr('data-pwdResetError') != ''){
		$('#pwdResetError').css("display", "block");
	}
	if($('#errorBlockSuc').attr('data-errorBlockSuc') != ''){
		$('#errorBlockSuc').css("display", "block");
	}

	var username = getCookie("emailid");
	var password = getCookie("password");
		    
	document.getElementById("emailid").value = username;
	document.getElementById("password").value= password;
});
	$("#loginBtn").click(function(e){
		if(validateUser()){
			var email = $("#emailid").val();
			var pwd = $("#password").val();
			if ($('#remember_me').is(":checked"))	{
			 	setCookie("emailid", email);
				setCookie("password", pwd);
			}
			removeAllItems("session");
		}else{
			e.preventDefault();
		}
	});
	
	function validateUser() {
		var user = $("#emailid").val();
		var pass = $("#password").val();
		$("#errorBlock").css("display", "none");
		$("#invalidLogin").css("display", "none");
		
		filter = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;
		if (user.length == 0 || !filter.test(user)) {
			  $("#invalidLogin").css("display", "block");
			  return false;
		}
		if(pass.length == 0) {
			  $("#invalidLogin").css("display", "block");
			return false;
		}
		return true;
	}
