$(document).ready(function(){
	var li_id = 0;
	var kids = ["John Smith", "Adam Riosky", "Xiang June", "harshitha"];

	for (i = kids.length-1; i >=0 ; i--) { 
		li_id++;
		$("div#kidsnavbar ul").prepend(
				'<li><a class="tabs" id="kid'+li_id+'" href="#">'+kids[i]+'</a></li>');

	}
	$('#kid'+li_id).addClass("active");

	var text = $('#kid'+li_id)[0].innerHTML;
	$("ol#bread").append(
			'<li class="tabs">'+text+'<i class="fa fa-chevron-right right" aria-hidden="true"></i></li>');
	$("ol#bread").append(
	'<li><a class="active" href="#">Kids Profile</a></li>');

	/*$("#addKidsheight").val($("#addKidsheight").val()+'0Feet');
	$("#addKidsweight").val($("#addKidsweight").val()+'0K.g');*/

	/*$("#accountManagement").click(function(){
		$("#accountMgt").show();
	});
	 */
	$("#contactno").keyup(function(){
		$("#accMgtContact_err").css({"display":"none"});	
		var contact = $(this).val();
		if(contact.length == 2){
			if(!contactStartCheck(contact)){
				$("#accMgtContactStartOfContactInvalid").css({"display":"block"});
				$(this).val("")
			}else{
				$("#accMgtContactStartOfContactInvalid").css({"display":"none"});
			}

		}else if(contact.length > 10){
			if(!contactCheck(contact)){
				$("#accMgtContactInValidContactNo").css({"display":"block"});
			}else {
				$("#accMgtContactInValidContactNo").css({"display":"none"});
			}

		}else{
			$("#accMgtContactStartOfContactInvalid").css({"display":"none"});
			$("#accMgtContactInValidContactNo").css({"display":"none"});
		}
	});
	function contactStartCheck(contact){
		return /^[0][9]$/.test(contact);
	}
	function contactCheck(contact){
		return /^[0-9]{9}$/.test(contact);
	}
	$(".tabs").click(function(){  


		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		var text = $(this).context.innerHTML;
		$("ol#bread li .right").parent().remove();
		$("ol#bread .tabs").remove();
		$("ol#bread .active").parent().remove();

		$("ol#bread").append(
				'<li class="tabs">'+text+'<i class="fa fa-chevron-right right" aria-hidden="true"></i></li>');
		$("ol#bread").append(
		'<li><a class="active" href="#">Kids Profile</a></li>');


		$("#addKidsDeviceSection").css({'display':'none'});
		$("#addkidssection").css({'display':'none'});
		$("#kidssection").css({'display':'block'});

	});
	
	$("#kid0").click(function(){
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		$("#kidssection").css({'display':'none'});
		$("#addkidssection").css({'display':'none'});
		$("#addKidsDeviceSection").css({'display':'block'});
	});
	$("#addUnlinkBtn").click(function(){
		$("#kidssection").css({'display':'none'});
		$("#addKidsDeviceSection").css({'display':'none'});
		$("#addkidssection").css({'display':'block'});

	});
	date_input=$('input[name="kidsDate"]'); //our date input has the name "date"
	var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
	var options={
			container: container,
	};
	date_input.datepicker(options);
	$("#date").datepicker({

		format: 'yyyy/mm/dd',
		todayHighlight	: true,
		todayBtn		: true,
		autoclose		: true,
		pickerPosition	: "bottom-left",
		orientation 	: "bottom-right"
	});

	date_inputForAddKid=$('input[name="addKidsDate"]'); //our date input has the name "date"
	// var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
	var options2={
         };
	date_inputForAddKid.datepicker(options2);
	$("#addKidsDate").datepicker({

		format: 'yyyy/mm/dd',
		todayHighlight	: true,
		todayBtn		: true,
		autoclose		: true,
		pickerPosition	: "bottom-left",
		orientation 	: "bottom-right"
	});




	$("#addKidSave").click(function(){ 
		debugLogs($("#addKidsDate").datepicker("getDate"));
		var dob = $("#addKidsDate").datepicker("getDate");

		if($("#addKidsname").val() == ""){
			$("#addKidname_err").css({"display":"block"});
		}else{
			$("#addKidname_err").css({"display":"none"});
		}

		if($("#addKidsheight").val() == ""){
			$("#addKidheight_err").css({"display":"block"});
		}else {
			$("#addKidheight_err").css({"display":"none"});
		}

		if(dob == null){
			$("#addKidDob_err").css({"display":"block"});
		}
		else{
			$("#addKidDob_err").css({"display":"none"});
		}

		if($("#addKidsweight").val() == ""){
			$("#addKidweight_err").css({"display":"block"});
		}else{
			$("#addKidweight_err").css({"display":"none"});
		}

		if($("#addKidsContact").val() == ""){
			$("#addKidcontact_err").css({"display":"block"});
		}else{
			$("#addKidcontact_err").css({"display":"none"});
		}
	});
	$("#save").click(function(){ 
		debugLogs($("#date").datepicker("getDate"));
		var dob = $("#date").datepicker("getDate");

		if($("#kidsname").val() == ""){
			$("#kidname_err").css({"display":"block"});
		}else{
			$("#kidname_err").css({"display":"none"});
		}

		if($("#kidsheight").val() == ""){
			$("#kidheight_err").css({"display":"block"});
		}else {
			$("#kidheight_err").css({"display":"none"});
		}

		if(dob == null){
			$("#kidDob_err").css({"display":"block"});
		}
		else{
			$("#kidDob_err").css({"display":"none"});
		}

		if($("#kidsweight").val() == ""){
			$("#kidweight_err").css({"display":"block"});
		}else{
			$("#kidweight_err").css({"display":"none"});
		}

		if($("#kidsContact").val() == ""){
			$("#kidcontact_err").css({"display":"block"});
		}else{
			$("#kidcontact_err").css({"display":"none"});
		}
	});
	$("#kidsContact").keyup(function(){
		$("#kidcontact_err").css({"display":"none"});	
		var contact = $(this).val();
		if(contact.length == 2){
			if(!contactStartCheck(contact)){
				$("#startOfContactInvalid").css({"display":"block"});
				$(this).val("")
			}else{
				$("#startOfContactInvalid").css({"display":"none"});
			}

		}else if(contact.length > 10){
			if(!contactCheck(contact)){
				$("#inValidContactNo").css({"display":"block"});
			}else {
				$("#inValidContactNo").css({"display":"none"});
			}

		}else{
			$("#startOfContactInvalid").css({"display":"none"});
			$("#inValidContactNo").css({"display":"none"});
		}
	});
	$("#addKidsContact").keyup(function(){
		$("#addKidcontact_err").css({"display":"none"});	
		var contact = $(this).val();
		if(contact.length == 2){
			if(!contactStartCheck(contact)){
				$("#addKidStartOfContactInvalid").css({"display":"block"});
			}else{
				$("#addKidStartOfContactInvalid").css({"display":"none"});
			}

		}else if(contact.length > 10){
			if(!contactCheck(contact)){
				$("#addKidInValidContactNo").css({"display":"block"});
			}else {
				$("#addKidInValidContactNo").css({"display":"none"});
			}

		}else{
			$("#addKidStartOfContactInvalid").css({"display":"none"});
			$("#addKidInValidContactNo").css({"display":"none"});
		}
	});
	function contactStartCheck(contact){
		return /^[0][9]$/.test(contact);
	}
	function contactCheck(contact){
		return /^[0-9]{9}$/.test(contact);
	}

});