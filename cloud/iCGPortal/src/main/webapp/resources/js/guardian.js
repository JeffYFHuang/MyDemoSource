$(document).ready(function(){
	var emailValidation = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;
	var phoneValidation = /((?=(09))[0-9]{10})$/g;
	var token_id = $("#token_id").val();
	sessionStorage.setItem("subscriptionSave",'');
	sessionStorage.setItem("guardianCreate",'');
	sessionStorage.setItem("guardianEdit",'');
	sessionStorage.setItem("guardianDelete",'');
	guradianTab = null;
	init();
	function init(){
		var studentselectedBread = sessionStorage.getItem("nick_name");
		$("#studentselectedBread").text(studentselectedBread);
		guardianId = 0;
		guardiansStudentId = 0;
		studentCountForMember = [];
		/*var subscriptionSave="",guardianCreate="",guardianEdit="",guardianDelete="";
		subscriptionSave = sessionStorage.getItem("subscriptionSave");
		messageDisplay(subscriptionSave,"subscriptionMessage");

		guardianCreate = sessionStorage.getItem("guardianCreate");
		messageDisplay(guardianCreate,"responseMessageForCreate");

		guardianEdit = sessionStorage.getItem("guardianEdit");
		messageDisplay(guardianEdit,"guardianEdit");

		guardianDelete = sessionStorage.getItem("guardianDelete");
		messageDisplay(guardianDelete,"guardianDelete");*/
		subscriptionSave = sessionStorage.getItem("subscriptionSave");
		if(!isDataNull(subscriptionSave)){
			var subscriptionMessage = sessionStorage.getItem("subscriptionSave");
			$("#subscriptionMessage").text('subscriptionMessage');
			if(subscriptionMessage === "Guradian Subscription updated Successfully"){
				$("#subscriptionMessage").css({"color":"green"});
			}else{
				$("#subscriptionMessage").css({"color":"red"});
			}
		}else{
			$("#subscriptionMessage").text('');
		}
		guardianCreate = sessionStorage.getItem("guardianCreate");
		if(!isDataNull(guardianCreate)){
			var guardianCreate = sessionStorage.getItem("guardianCreate");
			$("#responseMessageForCreate").text(guardianCreate);
			if(guardianCreate === "Guardian Created Successfully"){
				$("#responseMessageForCreate").css({"color":"green"});
			}else{
				$("#responseMessageForCreate").css({"color":"red"});
			}
		}else{
			$("#responseMessageForCreate").text('');
		}
		guardianEdit = sessionStorage.getItem("guardianEdit");
		if(!isDataNull(guardianEdit)){
			var guardianEdit = sessionStorage.getItem("guardianEdit");
			$("#guardianEdit").text(guardianEdit);
			if(guardianEdit === "Guardian Edited Successfully"){
				$("#guardianEdit").css({"color":"green"});
			}else{
				$("#guardianEdit").css({"color":"red"});
			}
		}else{
			$("#guardianEdit").text('');
		}
		guardianDelete = sessionStorage.getItem("guardianDelete");
		if(!isDataNull(guardianDelete)){
			var guardianDelete = sessionStorage.getItem("guardianDelete");
			$("#guardianDelete").text(guardianDelete);
			if(guardianDelete === "Guardian Deleted Successfully"){
				$("#guardianDelete").css({"color":"green"});
			}else{
				$("#guardianDelete").css({"color":"red"});
			}
		}else{
			$("#guardianDelete").text('');
		}
		makeGetAjaxRequest("mobile/StudentList/"
				+ token_id, false,
				onStdListSuccess, onStdListFailure);

		makeGetAjaxRequest("mobile/GuardianList/"
				+ token_id, false,
				onSuccessGuardianLists, onFailureGuardianLists);
	}
	/*function messageDisplay(action,id){
		if(!isDataNull(action)){
			var message = sessionStorage.getItem("action");
			$("#"+id).text(message);
		}else{
			$("#"+id).text('');
		}
	}*/
	function onSuccessGuardianLists(data){
		$("tbody#guardianLists tr").remove();
	/*	$("#guardianBread").text(' ');*/
		$("ul#guardianNav li").remove();
		$("#guardianStudentSubscription div").remove();
		$("#navbar-section").css({"display":"none"});
		//$("li#liBread i").remove();
		$("#addUUID").css({"display":"none"});
		var guardian_user_id="", guradian_username="", guradian_name="", guradian_status="", guradian_mobile_number="";
		if(!isDataNull(data.Return.Results.GuardianNamesWithIds)){
			jsonGuardianObj = data.Return.Results.GuardianNamesWithIds;
			obj = jsonSort(jsonGuardianObj, "name", DATATYPE.STRING, SORTORDER.ASC, null); 
			var li_id = 0;
			for(var j = 0; j < jsonGuardianObj.length; j++){

				if(!isDataNull(jsonGuardianObj[j].status)){
					guradian_status = jsonGuardianObj[j].status;
				}
				if(!isDataNull(jsonGuardianObj[j].user_id)){
					guardian_user_id = jsonGuardianObj[j].user_id;
					guardianId = guardian_user_id;
				}
				if(!isDataNull(jsonGuardianObj[j].name)){
					guradian_name = jsonGuardianObj[j].name;
				}
				if(!isDataNull(jsonGuardianObj[j].username)){
					guradian_username = jsonGuardianObj[j].username;
				}
				if(!isDataNull(jsonGuardianObj[j].mobile_number)){
					guradian_mobile_number = jsonGuardianObj[j].mobile_number;
				}
				if(guradian_status == 'Active'){
					li_id++;
					if(li_id == 1){
						$("#navbar-section").css({"display":"block"});
					/*	$("li#liBread").append('<i class="fa fa-chevron-right" aria-hidden="true"></i>');*/
						$("#addUUID").css({"display":"block"});
					}
					if(isDataNull(guradian_name)){
						name = guradian_username;
					}else{
						name = guradian_name;
					}
					$("div#kidsnavbar ul").prepend(
							'<li><a class="tabs" guardianId="'+guardian_user_id+'" id="guradian'+li_id+'" href="#">'+name+'</a></li>');
				}
				var listsofGuardian ='<tr>'+
				'<td><span class="noneditable" id="guardianName">'+guradian_name+'</span></td>'+
				'<td><span class="noneditable">'+guradian_username+'</span></td>'+
				'<td id="guardianKidslist'+guardianId+'"></td>'+
				'<td><span class="noneditable" id="guardianContact">'+guradian_mobile_number+'</span></td>'+
				'<td><span id="status'+guardianId+'">'+guradian_status+'</span></td>'+
				'<td class="sedit"><a class="studentEditnBtn"'+
				'style="color: black"><img onclick="mergeGuardianDetails($(this))"'+
				'class="upgrade_icon edit_img'+guardianId+' hand"'+
				'src="resources/images/unselected_edit_icon.png"'+
				'data-toggle="modal" data-keyboard="true"'+
				'data-target="#editGuardianDetails" guardian_name="'+escape(guradian_name)+'" guradian_username="'+guradian_username+'" guradian_mobile_number="'+guradian_mobile_number+'"  guardian_id="' + guardian_user_id + '"  /></a></td>'+
				'<td class="sdelete"><a style="color: black"  data-toggle="modal" data-target="#deleteGuardian"><img class="hand" '+
				'onclick="deleteGuardian($(this))" guardian_id="' + guardian_user_id + '" src="resources/images/Delete_icon.png" /></a></td>'+
				'</tr>';
				$("tbody#guardianLists").append(listsofGuardian);
				if(guradian_status == 'Inactive'){
					$("#status"+guardianId).css({"color":"red"});
				}else{
					$("#status"+guardianId).css({"color":"green"});
				}
				makeGetAjaxRequestSync("web/StudentListByMenberId/"
						+guardian_user_id, false,
						onSuccessGuardianKids, onFailureGuardianKid);
				guardian_user_id="", guradian_username="", guradian_name="", guradian_status="", guradian_mobile_number="";
			}
			if(li_id > 0){
				var id;
				if(isDataNull(guradianTab)){
					$('#guradian'+li_id).addClass("active");
				/*	var text = $('#guradian'+li_id)[0].innerHTML;
					$("#guardianBread").text(text);*/
					id = $('#guradian'+li_id).attr('guardianid');
					guardianId = id;
					makeGetAjaxRequestSync("web/StudentListByMenberId/"
							+id, false,
							onSuccessGuardianKidsLists, onFailureGuardianKidsLists);
				}else{
					$('#'+guradianTab).addClass("active");
					/*var text = $('#'+guradianTab)[0].innerHTML;
					$("#guardianBread").text(text);*/
					id = $('#'+guradianTab).attr('guardianid');
					guardianId = id;
					makeGetAjaxRequestSync("web/StudentListByMenberId/"
							+id, false,
							onSuccessGuardianKidsLists, onFailureGuardianKidsLists);
				}
			}
		}else{
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			var	listsofGuardian ='<tr><td colspan="7" class="box-body text-center"><b>' + nodataText + '</b></td> </tr>';
			$("tbody#guardianLists").append(listsofGuardian);
		}
		$(".tabs").click(function(){ 
			showSpinner();
			$(this).parent().parent().find('.active').removeClass("active");
			$(this).addClass("active");
			guardianId = $(this).attr('guardianId');
			guradianTab =  $(this).attr('id');
		/*	var text = $(this).context.innerHTML;
			$("#guardianBread").text(text);*/
			$("#addKidsDeviceSection").css({'display':'none'});
			$("#kidssection").css({'display':'block'});

			sessionStorage.setItem("subscriptionSave",'');
			sessionStorage.setItem("guardianCreate",'');
			sessionStorage.setItem("guardianEdit",'');
			sessionStorage.setItem("guardianDelete",'');

			var subscriptionSave="",guardianCreate="",guardianEdit="",guardianDelete="";
			subscriptionSave = sessionStorage.getItem("subscriptionSave");
			if(!isDataNull(subscriptionSave)){
				var subscriptionMessage = sessionStorage.getItem("subscriptionSave");
				$("#subscriptionMessage").text(subscriptionMessage);
				if(subscriptionMessage === "Guradian Subscription updated Successfully"){
					$("#subscriptionMessage").css({"color":"green"});
				}else{
					$("#subscriptionMessage").css({"color":"red"});
				}
			}else{
				$("#subscriptionMessage").text('');
			}
			guardianCreate = sessionStorage.getItem("guardianCreate");
			if(!isDataNull(guardianCreate)){
				var guardianCreate = sessionStorage.getItem("guardianCreate");
				$("#responseMessageForCreate").text(guardianCreate);
				if(guardianCreate === "Guardian Created Successfully"){
					$("#responseMessageForCreate").css({"color":"green"});
				}else{
					$("#responseMessageForCreate").css({"color":"red"});
				}
			}else{
				$("#responseMessageForCreate").text('');
			}
			guardianEdit = sessionStorage.getItem("guardianEdit");
			if(!isDataNull(guardianEdit)){
				var guardianEdit = sessionStorage.getItem("guardianEdit");
				$("#guardianEdit").text(guardianEdit);
				if(guardianEdit === "Guardian Edited Successfully"){
					$("#guardianEdit").css({"color":"green"});
				}else{
					$("#guardianEdit").css({"color":"red"});
				}
			}else{
				$("#guardianEdit").text('');
			}
			guardianDelete = sessionStorage.getItem("guardianDelete");
			if(!isDataNull(guardianDelete)){
				var guardianDelete = sessionStorage.getItem("guardianDelete");
				$("#guardianDelete").text(guardianDelete);
				if(guardianDelete === "Guardian Deleted Successfully"){
					$("#guardianDelete").css({"color":"green"});
				}else{
					$("#guardianDelete").css({"color":"red"});
				}
			}else{
				$("#guardianDelete").text('');
			}
			/*makeGetAjaxRequest("mobile/GuardianList/"
					+ token_id, false,
					onSuccessGuardianLists, onFailureGuardianLists);*/
			makeGetAjaxRequestSync("web/StudentListByMenberId/"
					+guardianId, false,
					onSuccessGuardianKidsLists, onFailureGuardianKidsLists);
			//init();
		});
	}
	function onSuccessGuardianKids(data){
		var student_id="", student_nickname="";
		if(!isDataNull(data.Return.Results.students)){
			var removekidifexist ="guardianKidslist"+guardianId+" span";
			$("#"+removekidifexist).remove();
			jsonStudentObj = data.Return.Results.students;
			obj = jsonSort(jsonStudentObj, "name", DATATYPE.STRING, SORTORDER.ASC, null); 
			var guradian_kids = "";
			for(var j = 0; j < jsonStudentObj.length; j++){
				if(!isDataNull(jsonStudentObj[j].student_id)){
					student_id = jsonStudentObj[j].student_id;
					//studentCountForMember[j] = student_id;
					guardiansStudentId = student_id;
					guradian_kids += student_id + ",";
				}
				if(!isDataNull(jsonStudentObj[j].nickname)){
					student_nickname = jsonStudentObj[j].nickname;
				}
				if(j == 0){
					var kids = '<span>'+student_nickname+'</span>';
				}else{
					var kids = '<span>, '+student_nickname+'</span>';
				}
				$("#guardianKidslist"+guardianId).append(kids);
			}
			$(".edit_img"+guardianId).attr('guradian_kids',guradian_kids);
		}
	}
	function onFailureGuardianKid(data){
		console.log(data);
	}
	var student_uuid= "";
	function onSuccessGuardianKidsLists(data){
		var student_id="", student_nickname="", student_name="";
		if(!isDataNull(data.Return.Results.students)){
			$("#guardianStudentSubscription div").remove();
			$("#guardiansave").css({"display":"none"});
			/*var removekidifexist ="guardianKidslist"+guardianId+" span";
			$("#"+removekidifexist).remove();*/
			jsonStudentObj = data.Return.Results.students;
			obj = jsonSort(jsonStudentObj, "name", DATATYPE.STRING, SORTORDER.ASC, null); 
			var guradian_kids = "";
			for(var j = 0; j < jsonStudentObj.length; j++){
				if(!isDataNull(jsonStudentObj[j].student_id)){
					student_id = jsonStudentObj[j].student_id;
					studentCountForMember[j] = student_id;
					guardiansStudentId = student_id;
					guradian_kids += student_id + ",";
				}
				if(!isDataNull(jsonStudentObj[j].nickname)){
					student_nickname = jsonStudentObj[j].nickname;
				}
				if(!isDataNull(jsonStudentObj[j].uuid)){
					student_uuid = jsonStudentObj[j].uuid;
				}


				var subsctiption ='<div class="col-md-4 col-lg-4 col-sm-12 col-xs-12 font-light-roboto boder-line">'+
				'<div class="col-md-12 third_subtitle guardianKids">'+
				'<p>'+
				''+student_nickname+' <span><img '+
				'src="resources/images/ParentDashboard/Kids_band.png"'+
				'height="34" width="34" class="pull-right" /></span>'+
				'</p>'+
				'</div>'+
				'<div class="col-md-12 top-border">'+
				'<p>'+
				'<span><img '+
				'src="resources/images/ParentDashboard/school_in_out_icon.png" height=34 '+
				'width=34 class="pull-left" /></span> School In/Out '+
				'<label class="switch pull-right"> <input type="checkbox" id="guardianSchoolIn'+student_id+'">'+
				'<span class="slider round"></span>'+
				'</label>'+
				'</p>'+
				'</div>'+
				'<div class="col-md-12 top-border">'+
				'<p>'+
				'<span><img src="resources/images/ParentDashboard/SOS.png"'+
				'height=34 width=34 class="pull-left" /></span> SOS Alerts '+
				'<label class="switch pull-right"> <input type="checkbox" id="guardianSosAlert'+student_id+'">'+
				'<span class="slider round"></span>'+
				'</label>'+
				'</p>'+
				'</div>'+
				'<div class="col-md-12 top-border">'+
				'<p>'+
				'<span><img '+
				'src="resources/images/ParentDashboard/Band_removal.png" height=34'+
				'width=34 class="pull-left" /></span> Band Removal'+
				'<label class="switch pull-right"> <input type="checkbox" id="guardianBand'+student_id+'">'+
				'<span class="slider round"></span>'+
				'</label>'+
				'</p>'+
				'</div>'+
				'<p class="hidden">'+
				'<input type="text" id="subscriptionUuid'+student_id+'"/>'+
				'<input type="text" id="subscriptionStudentId'+student_id+'"/>'+
				'<input type="text" id="subscriptionGuardianId'+student_id+'"/>'+
				'</p>'+
				'</div>';
				$("#guardianStudentSubscription").append(subsctiption);
				if(j == 0){
					$("#guardiansave").css({"display":"block"});
				}
				makeGetAjaxRequestSync("mobile/GuardianSubscriptionsList/"+token_id+
						"/"+guardianId+"/"+student_id,false,
						onSuccessGuardianSubscriptionsList,onFailureGuardianSubscriptionsList);
				student_id="", student_nickname="", student_name="";
			}

		}
	}
	function onSuccessGuardianSubscriptionsList(data){
		if(!isDataNull(data.Return.Results.School_Entry.value)) {
			var schoolEntrySubscribed = data.Return.Results.School_Entry.value;
			if(schoolEntrySubscribed === 'yes'){
				$('#guardianSchoolIn'+guardiansStudentId).attr('checked','checked')
			}else {
				$('#guardianSchoolIn'+guardiansStudentId).prop('unchecked')
			}
		}
		if(!isDataNull(data.Return.Results.SOS.value)) {
			var SOSSubscribed = data.Return.Results.SOS.value;
			if(SOSSubscribed === 'yes'){
				$('#guardianSosAlert'+guardiansStudentId).attr('checked','checked')
			}else {
				$('#guardianSosAlert'+guardiansStudentId).prop('unchecked')
			}
		}
		if(!isDataNull(data.Return.Results.BandRemoval.value)) {
			var bandRemovalSubscribed = data.Return.Results.BandRemoval.value;
			if(bandRemovalSubscribed === 'yes'){
				$('#guardianBand'+guardiansStudentId).attr('checked','checked')
			}else {
				$('#guardianBand'+guardiansStudentId).prop('unchecked')
			}
		}

		if(!isDataNull(student_uuid)) {
			$("#subscriptionUuid"+guardiansStudentId).val(student_uuid);
		}
		$("#subscriptionStudentId"+guardiansStudentId).val(guardiansStudentId);
		$("#subscriptionGuardianId"+guardiansStudentId).val(guardianId);
	}
	$("#guardiansave").click(function(){
		showSpinner();
		var School_Enter_Event = 1;
		var School_Exit_Event = 2;
		var SOS_Alert_Event = 13;
		var Band_Removal_Alert_Event = 20;
		var School_Exit;
		for(var i = 0; i < studentCountForMember.length;i++){
			var School_Enter = $('#guardianSchoolIn'+studentCountForMember[i]).is(':checked');
			if(School_Enter){
				School_Enter = 'yes';
				School_Exit='yes';
			}else{
				School_Enter = 'no';
				School_Exit='no';
			}
			var SOS = $('#guardianSosAlert'+studentCountForMember[i]).is(':checked');
			if(SOS){
				SOS = 'yes';
			}else{
				SOS = 'no';
			}
			var BandRemoval = $('#guardianBand'+studentCountForMember[i]).is(':checked');
			if(BandRemoval){
				BandRemoval = 'yes';
			}else{
				BandRemoval = 'no';
			}
			var uuid = $("#subscriptionUuid"+studentCountForMember[i]).val();
			var memberID = $("#subscriptionGuardianId"+studentCountForMember[i]).val();
			var studentID = $("#subscriptionStudentId"+studentCountForMember[i]).val();
			var requestData = [{
				"alertId" : School_Enter_Event,
				"alertValue": School_Enter
			},{
				"alertId" : School_Exit_Event,
				"alertValue": School_Exit
			},{
				"alertId" : SOS_Alert_Event,
				"alertValue": SOS
			},{
				"alertId" : Band_Removal_Alert_Event,
				"alertValue": BandRemoval
			}];

			makePostAjaxRequestSync("mobile/GuardianSubscriptionsUpdate/"+
					token_id+"/"+uuid+"/"+memberID+"/"+studentID,requestData,
					onSuccessGuardianSubscriptionsUpdate,onFailureGuardianSubscriptionsUpdate);
		}
	});
	function onSuccessGuardianSubscriptionsUpdate(data){
		console.log(data);
		responseCode = data.Return.ResponseSummary.StatusCode;
		var message = "";
		$("#subscriptionMessage").css({"color":"red"});
		if(responseCode === 'SUC01'){
			message = "Guradian Subscription updated Successfully"
			$("#subscriptionMessage").css({"color":"green"});
		}else if(responseCode === 'ERR03'){
			message = "Unauthorised User";
		}
		else if(responseCode === 'ERR05'){
			message = "Invalid Input, please provide valid details to update";
		}else{
			message = "Something went wrong";
		}
		sessionStorage.setItem("subscriptionSave",message);
		sessionStorage.setItem("guardianCreate",'');
		sessionStorage.setItem("guardianEdit",'');
		sessionStorage.setItem("guardianDelete",'');
		$("#subscriptionMessage").text(message);
		//init();

	}
	function onFailureGuardianSubscriptionsUpdate(data){
		var message =data.Return.ResponseSummary.StatusMessage;
		sessionStorage.setItem("subscriptionSave",message);
		console.log(data);
		//init();
	}
	function onFailureGuardianSubscriptionsList(data){
		console.log(data);
	}
	function onFailureGuardianKidsLists(data){
		console.log(data);
	}
	function onFailureGuardianLists(data){
		console.log(data);
	}
	function onStdListSuccess(data){
		var kids = data.Return.Results.students;
		$('#checkboxes').html('');
		var li_id =0;
		var kidName= '',std_name ="",student_id = "",std_nickname="",guardian_count="";

		for (i = kids.length-1; i >=0 ; i--) { 
			li_id = kids[i].student_id;
			if(!isDataNull(kids[i].name)){
				std_name = kids[i].name;
			}
			if(!isDataNull(kids[i].student_id)){
				student_id   =kids[i].student_id;
			}
			if(!isDataNull(kids[i].nickname)){
				std_nickname = kids[i].nickname;
			}
			if(!isDataNull(kids[i].guardian_count)){
				guardian_count = kids[i].guardian_count;
			}
			if(guardian_count < 3){
				var chekboxrow = '<label for="addKid'+student_id+'"> <input type="checkbox" value="'+student_id+'" name="kidsname" id="addKid'+student_id+'" />'+std_nickname+'';
				$('#checkboxes').append(chekboxrow);
			}
			std_name ="",student_id = "",std_nickname="",guardian_count="";
		}
	}
	function onStdListFailure(data){
		debugLogs(data);
	}

	$("#editContactDetails").keyup(function(){
		$("#editContactDetails_err").css({"display":"none"});	
		var contact = $(this).val();
		if(contact.length == 2){
			if(!contactStartCheck(contact)){
				$("#editContactDetailsStartOfContactInvalid").css({"display":"block"});
				$(this).val("")
			}else{
				$("#editContactDetailsStartOfContactInvalid").css({"display":"none"});
			}

		}else if(contact.length > 10){
			if(!contactCheck(contact)){
				$("#editContactDetailsInValidContactNo").css({"display":"block"});
			}else {
				$("#editContactDetailsInValidContactNo").css({"display":"none"});
			}

		}else{
			$("#editContactDetailsStartOfContactInvalid").css({"display":"none"});
			$("#editContactDetailsInValidContactNo").css({"display":"none"});
		}
	});

	function contactStartCheck(contact){
		return /^[0][9]$/.test(contact);
	}
	function contactCheck(contact){
		return /^[0-9]{9}$/.test(contact);
	}
	$("#guardiancreate").click(function(e){
		showSpinner();
		$("#responseMessageForCreate").text(' ');
		isValidName = true;
		isValidKids = true;
		var arr = [], i =0;
		if(isDataNull($("#guardianUserName").val())) {
			$("#guardianUserName_err").css("display", "block");
			$("#guardianUserNameValid_err").css("display", "none");
			isValidName = false;
		}else{
			$("#guardianUserName_err").css("display", "none");
			isValidName = true;
			if(!emailValidation.test($("#guardianUserName").val())) {
				$("#guardianUserNameValid_err").css("display", "block");
				isValidName = false;
			}else{
				$("#guardianUserNameValid_err").css("display", "none");
				isValidName = true;
			}
		}
		var checkbox = document.querySelector('input[name="kidsname"]:checked');
		$('input:checkbox[name=kidsname]:checked').each(function() {
			arr[i] = $(this).val();
			i++;
		});
		if(arr.length <= 0){
			isValidKids = false;
			$("#guardianAccess_err").css({"display":"block"});
		}else{
			isValidKids = true;
			$("#guardianAccess_err").css({"display":"none"});
		}
		if((!isValidName) || (!isValidKids)){
			e.preventDefault();
			hideSpinnerNow();
		}else{
			var playload = {
					"guardianUserName"	:$("#guardianUserName").val(),
					"kidsname"			:arr
			};
			makePostAjaxRequest("createGuradian/"+token_id, playload,
					onSuccessGuardianCreate, onFailureGuardianCreate);
			
		}

	}) 
	function onSuccessGuardianCreate(data){
		$("#guardianUserName").val('');
		var message="";
		var responseCode = data.Return.ResponseSummary.StatusCode;
		if(responseCode == "SUC01"){
			message = "Guardian Created Successfully"
		}else if(responseCode == "ERR24"){
			message = "User account already exist";
		}else if(responseCode == "ERR03"){
			message = "Unauthorised User";
		}else{
			message = "Something went wrong";
		}
		sessionStorage.setItem("guardianCreate",message);
		sessionStorage.setItem("subscriptionSave",'');
		sessionStorage.setItem("guardianEdit",'');
		sessionStorage.setItem("guardianDelete",'');
		hideSpinnerNow();
		init();
	}
	function onFailureGuardianCreate(data){
		$("#guardianUserName").val('');
		console.log(data);
		sessionStorage.setItem("guardianCreate",data.Return.ResponseSummary.StatusMessage);
		hideSpinnerNow();
		init();
	}
	$("#updateGuradian").click(function(e){
		$("#editGuardianContactDetails_err").css({"display":"none"});
		$("#editGuardianContactDetailsStartOfContactInvalid").css({"display":"none"});

		var guardianId = $("#guardian_user_id").val();
		var name = $("#editguardianName").val();
		var username = $("#editguardianUserName").val();
		var mobileNumber = $("#editGuardianContactDetails").val();
		var phoneValidation = /((?=(09))[0-9]{10})$/g;

		iseditValidName = true;
		isValidUsername = true;
		isValidContact = true;
		isValidKids = true;

		var editarr = [], i =0;
		if(name.trim() === '' || name.trim().length === 0){
			$("#editguardianName_err").css("display", "block");
			e.preventDefault();
			iseditValidName = false;
		}else{
			$("#editguardianName_err").css("display", "none");
			iseditValidName = true;
		}

		if(username.trim() == '' || username.trim().length === 0){
			$("#editguardianUserName_err").css("display", "block");
			$("#editguardianUserNameValid_err").css("display", "none");
			e.preventDefault();
			isValidUsername = false;
		}else if(!emailValidation.test(username)) {
			$("#editguardianUserNameValid_err").css("display", "block");
			e.preventDefault();
			isValidUsername = false;
		}else{
			$("#editguardianUserName_err").css("display", "none");
			$("#editguardianUserNameValid_err").css("display", "none");
			isValidUsername = true;
		}

		if(mobileNumber.trim() == '' || mobileNumber.trim().length === 0){
			debugLogs('2');
			$("#editGuardianContactDetails_err").css("display", "block");
			$("#editGuardianContactDetailsStartOfContactInvalid").css("display", "none");
			e.preventDefault();
			isValidContact = false;
		}else if(!phoneValidation.test(mobileNumber)) {
			$("#editGuardianContactDetailsStartOfContactInvalid").css("display", "block");
			e.preventDefault();
			isValidContact = false;
		}else{
			$("#editGuardianContactDetails_err").css("display", "none");
			$("#editGuardianContactDetailsStartOfContactInvalid").css("display", "none");
			isValidContact = true;
		}

		var editcheckbox = document.querySelector('input[name="editkidsname"]:checked');
		$('input:checkbox[name=editkidsname]:checked').each(function() {
			editarr[i] = $(this).val();
			i++;
		});
		if(editarr.length <= 0){
			$("#editGuardianKids_err").css("display", "block");
			e.preventDefault();
			isValidKids = false;
		}else{
			$("#editGuardianKids_err").css("display", "none");
			isValidKids = true;
		}
		if((!iseditValidName) || (!isValidUsername) || (!isValidContact) || (!isValidKids)){
			e.preventDefault();
			return false;
		}else{
			var playload = {
					"name"              : name,
					"guardianUserName"	: username,
					"kidsname"			: editarr,
					"mobileNumber"		: mobileNumber
			};
			makePutAjaxRequest("editGuardian/"+token_id+"/"+guardianId, playload,
					onSuccessGuardianUpdate, onFailureGuardianUpdate);
		}
	});
	function onSuccessGuardianUpdate(data){
		var message="";
		var responseCode = data.Return.ResponseSummary.StatusCode;
		if(responseCode == "SUC01"){
			message = "Guardian Edited Successfully"
		}else if(responseCode == "404"){
			message = "User Not Found";
		}else if(responseCode == "ERR03"){
			message = "Unauthorised User";
		}else if(responseCode == "ERR24"){
			message = "User account already exist";
		}else{
			message = "Something went wrong";
		}
		sessionStorage.setItem("guardianEdit",message);
		sessionStorage.setItem("subscriptionSave",'');
		sessionStorage.setItem("guardianCreate",'');
		sessionStorage.setItem("guardianDelete",'');
		init();
	}
	function onFailureGuardianUpdate(data){
		console.log(data);
		sessionStorage.setItem("guardianEdit",data.Return.ResponseSummary.StatusMessage);
		init();
	}
	$("#deleteGuardianDetails").click(function(){
		debugLogs('Into deleteGuardiantDetails()');
		var guardian_id = $("#deletGuardian_id").val();
		debugLogs('guardian_id'+guardian_id);
		var token_id = $("#token_id").val();
		makeDeleteAjaxRequest("deleteGuardian/"+token_id+"/"+guardian_id, false,
				onDeleteStudentSuccess, onDeleteStudentFailure);
	});

	function onDeleteStudentSuccess(userData){
		debugLogs(userData);
		var message="";
		var responseCode = userData.Return.ResponseSummary.StatusCode;
		if(responseCode == "SUC01"){
			message = "Guardian Deleted Successfully"
		}else if(responseCode == "404"){
			message = "User Not Found";
		}else if(responseCode == "ERR03"){
			message = "Unauthorised User";
		}else{
			message = "Something went wrong";
		}
		sessionStorage.setItem("guardianDelete",message);
		sessionStorage.setItem("subscriptionSave",'');
		sessionStorage.setItem("guardianCreate",'');
		sessionStorage.setItem("guardianEdit",'');
		init();
	}
	function onDeleteStudentFailure(userData){
		debugLogs(userData);
		sessionStorage.setItem("guardianDelete",userData.Return.ResponseSummary.StatusMessage);
		init();
	}
});
var expanded = false;
var expandedEdit = false;
$('html').click(function (e) {
	if (e.target.id == 'multiOverSelect' || e.target.name == 'kidsname') {
		if (!expanded) {
			$("#checkboxes").css({"display":"block"});
			expanded = true;
		}else if(expanded && e.target.name == 'kidsname'){
			$("#checkboxes").css({"display":"block"});
			expanded = true;
		}else{
			$("#checkboxes").css({"display":"none"});
			expanded = false;
		}
	} else {
		$("#checkboxes").css({"display":"none"});
		expanded = false;
	}
	if (e.target.id == 'editmultiOverSelect' || e.target.name == 'editkidsname') {
		if (!expandedEdit) {
			$("#editcheckboxes").css({"display":"block"});
			expandedEdit = true;
		}else if(expandedEdit && e.target.name == 'editkidsname'){
			$("#editcheckboxes").css({"display":"block"});
			expandedEdit = true;
		}else{
			$("#editcheckboxes").css({"display":"none"});
			expandedEdit = false;
		}
	} else {
		$("#editcheckboxes").css({"display":"none"});
		expandedEdit = false;
	}
});
var guardianLinkedStudentId = "";
function mergeGuardianDetails(usrObj){
	$("#editguardianName_err").css("display", "none");
	$("#editguardianUserName_err").css("display", "none");
	$("#editGuardianKids_err").css("display", "none");
	$("#editGuardianContactDetails_err").css("display", "none");
	$("#editGuardianContactDetailsStartOfContactInvalid").css("display", "none");
	$("#editGuardianContactDetailsInValidContactNo").css("display", "none");
	var guardian_user_id = usrObj.attr('guardian_id');
	var guardian_name = unescape(usrObj.attr('guardian_name'));
	var guradian_username = usrObj.attr('guradian_username');
	var guardian_kids = usrObj.attr('guradian_kids');
	guardianLinkedStudentId = guardian_kids;
	var guradian_mobile_number = usrObj.attr('guradian_mobile_number');
	//student_grade = usrObj.attr('student_grade');

	var token_id = $("#token_id").val();
	makeGetAjaxRequest("mobile/StudentList/"
			+ token_id, false,
			onEditStdListSuccess, onEditStdListFailure);
	$("#editguardianName").val(guardian_name);
	$("#editguardianUserName").val(guradian_username);
	$("#editGuardianContactDetails").val(guradian_mobile_number);
	$("#guardian_user_id").val(guardian_user_id);
	$("#guardian_kids_ids").val(guardian_kids);

}
function onEditStdListSuccess(data){
	var kids = data.Return.Results.students;
	$("#editcheckboxes label").remove();
	var li_id =0;
	var kidName= '',std_name="",student_id="",std_nickname="",editchekboxrow="",guardian_count="";
	var guardian_kids = guardianLinkedStudentId;
	var arr = [];
	if(!isDataNull(guardian_kids)) arr = guardian_kids.split(',');
	for (i = kids.length-1; i >=0 ; i--) { 
		if(!isDataNull(kids[i].student_id)){
			li_id = kids[i].student_id;
		}
		if(!isDataNull(kids[i].name)){
			std_name=kids[i].name;
		}
		if(!isDataNull(kids[i].student_id)){
			student_id =kids[i].student_id;
		}
		if(!isDataNull(kids[i].nickname)){
			std_nickname=kids[i].nickname;
		}
		if(!isDataNull(kids[i].guardian_count)){
			guardian_count=kids[i].guardian_count;
		}
		if(guardian_count < 3){
			editchekboxrow = '<label for="editKid'+student_id+'"> <input type="checkbox" value="'+student_id+'" name="editkidsname" id="editKid'+student_id+'" />'+std_nickname+'';
			$('#editcheckboxes').append(editchekboxrow);
		}else{
			if(arr.indexOf(student_id.toString()) !== -1){
				editchekboxrow = '<label for="editKid'+student_id+'"> <input type="checkbox" value="'+student_id+'" name="editkidsname" id="editKid'+student_id+'" />'+std_nickname+'';
				$('#editcheckboxes').append(editchekboxrow);
			}
		}
		std_name="",student_id="",std_nickname="",editchekboxrow="",guardian_count="";
	}

	for(var i=0;i<arr.length-1;i++){
		$("#editKid"+arr[i]).attr('checked','checked');
	}
}
function onEditStdListFailure(data){
	console.log(data);
}
function deleteGuardian(obj){
	$("#deletGuardian_id").val(obj.attr('guardian_id'));
}
