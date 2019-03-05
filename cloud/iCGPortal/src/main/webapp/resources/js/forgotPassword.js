$(document).ready(function() {
	$("#inValidEmail").css("display", "none");
	if($('#pwdChangeError').attr('data-error') != ''){
		$('#pwdChangeError').css("display", "block");
	}
	if($('#pwdChangeSuccess').attr('data-success') != ''){
		$('#pwdChangeSuccess').css("display", "block");
	}
	
	$( document ).ajaxStop(function() {
		getValueByLanguageKey('icglabel_activationsuccess');
		getValueByLanguageKey('icglabel_activationnotrequired');
		getValueByLanguageKey('icglabel_activationfailed');
		getValueByLanguageKey('icglabel_passwordactivationmessage');
	});
});

$("#resetPassword").click(function(e) {
	var user = $("#emailid").val();
	$("#inValidEmail").css("display", "none");
	filter = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;
	//filter = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
	if (user.length == 0 || !filter.test(user)) {
		  $("#inValidEmail").css("display", "block");
		  $("#emailid").val('');
		  return false;
	}
});