$('.kids').removeClass("treeview").addClass("active");
$('.kids').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$("#sKidsIcon").attr("src",
		"resources/images/sidemenu_icon/white/kids_profile_w.png");
var newStudentData = null;
var token_id = $("#token_id").val();
$(document).ready(
		function() {
			if ($('#new_parent').length && $('#new_parent').val() == 1) {
				$("ol#bread").css('display', 'none');
			}

			makeGetAjaxRequest("mobile/StudentList/" + token_id, false,
					onStdListSuccess, onStdListFailure);

			$("#inputDate").attr('readonly', 'readonly');
			$('#inputDate').datepicker({
				dateFormat : 'YYYY-MM-DD',
				minDate : '-12Y',
				endDate : '-3Y'
			}).on('show', function(e){    
				if($(".datepicker").hasClass("datepicker-orient-bottom")){
					$(".datepicker").removeClass('datepicker-orient-bottom').addClass("datepicker-orient-top");
				}else{
					$(".datepicker").removeClass('datepicker-orient-top').addClass("datepicker-orient-bottom");
				}
			});

		});

function onStdListSuccess(data) {
	console.log('Into onStdListSuccess');
	var kids = data.Return.Results.students;
	console.log('length' + kids.length);
	var li_id = 0;
	var kidName = '';
	for (i = kids.length - 1; i >= 0; i--) {
		li_id = kids[i].student_id;
		var std_name = kids[i].name;
		var std_nickname = kids[i].nickname;
		var std_roll_no = kids[i].roll_no;
		var std_registartion_no = kids[i].registartion_no;
		var std_emergency_contact = kids[i].emergency_contact;
		var height = kids[i].height;
		var weight = kids[i].weight;
		var dob = kids[i].dob;
		var gender = kids[i].gender;
		var allergies = kids[i].allergies;
		var uuid = kids[i].uuid;
		debugLogs('dob' + dob);
		console.log('std_name' + std_name);
		$("div#kidsnavbar ul").prepend(
				'<li><a class="tabs" id="kid' + li_id
						+ '" href="#" student_id="' + li_id
						+ '" std_nickname="' + std_nickname + '" std_name="'
						+ std_name + '" std_roll_no="' + std_roll_no
						+ '" std_registartion_no="' + std_registartion_no
						+ '" ' + ' std_emergency_contact="'
						+ std_emergency_contact + '" height="' + height
						+ '" weight="' + weight + '" dob="' + dob
						+ '" gender="' + gender + '" allergies="' + allergies
						+ '" uuid="' + uuid + '">' + kids[i].nickname
						+ '</a></li>');
	}
	if (li_id == 0) {
		$("#kid0").addClass("active");
	} else {
		$('#kid' + li_id).addClass("active");
		loadStudentData($('#kid' + li_id));
	}

	if ($('#kid' + li_id)[0].className == "tabs"
			|| $('#kid' + li_id)[0].className == "tabs active") {
		$('#kid' + li_id).click();
	} else {
		$("#kid0").click();
	}

	$('#kidsnavbar').on('click', '.tabs', function() {
		loadStudentData($(this));
	});
}

function onStdListFailure(data) {
	debugLogs("Inside: onStdListFailure");
}

function loadStudentData(current) {
	clearErrorMessages();

	current.parent().parent().find('.active').removeClass("active");
	current.addClass("active");
	var text = current.attr('std_nickname');
	/*$("ol#bread li .right").parent().remove();
	$("ol#bread .tabs").remove();
	$("ol#bread .active").parent().remove();*/
	$("#kidsSlected").text(text);
	/*$("ol#bread")
			.append(
					'<li><a class="tabs" href="#">'
							+ text
							+ '</a><i class="fa fa-chevron-right right" aria-hidden="true"></i></li>');*/

	/*$("ol#bread")
			.append('<li><a class="active icglabel_kidsprofile" href="#"></a></li>');*/

	var studentName = current.attr('std_name');
	var studentNickName = current.attr('std_nickname');
	var studentRollNo = current.attr('std_roll_no');
	var studentRegNo = current.attr('std_registartion_no');
	var studentHeight = current.attr('height');
	var studentWeight = current.attr('weight');
	var studentDob = current.attr('dob');
	var studentContact = current.attr('std_emergency_contact');
	var studentGender = current.attr('gender');
	var studentAllergies = current.attr('allergies');
	var studentId = current.attr('student_id');
	var studentDeviceUuid = current.attr('uuid');

	sessionStorage.setItem("studentId", studentId);

	$("#name").text(studentName);
	$("#classRollNo").text(studentRollNo);
	$("#regNo").text(studentRegNo);
	$("#kidsname").val(studentNickName);
	$("#kidsheight").val(studentHeight);
	$("#kidsContact").val(studentContact);
	if(!isDataNull(studentGender))
		$("#kidsgender").val(studentGender);
	else
		$("#kidsgender").val("Select the Gender");
	$("#kidsweight").val(studentWeight);
	$("#student_id").val(studentId);
	$("#deviceUuidUnlink").val(studentDeviceUuid);
	setDOB(studentDob);
	setAllergies(studentAllergies);
	$("#addUUID").css({
		'display' : 'none'
	});
	$("#deviceSectionLink").css({
		'display' : 'none'
	});
	$("#addkidssection").css({
		'display' : 'none'
	});
	$("#profile_section").css({
		'display' : 'block'
	});
	$("#deviceSectionUnlink").css({
		'display' : 'block'
	});
	$("#kidssection").css({
		'display' : 'block'
	});
}

function setDOB(studentDob){
	if (typeof studentDob == 'undefined' || studentDob == 'undefined' || isDataNull(studentDob)) {
		$.fn.datepicker.defaults.defaultViewDate = {
			year : new Date().getFullYear()-3,
			month : 00
		}
		$('#inputDate').datepicker('destroy');
		$('#inputDate').datepicker({
			dateFormat : 'YYYY-MM-DD',
			minDate : '-12Y',
			endDate : '-3Y'
		}).on('show', function(e){    
			if($(".datepicker").hasClass("datepicker-orient-bottom")){
				$(".datepicker").removeClass('datepicker-orient-bottom').addClass("datepicker-orient-top");
			}else{
				$(".datepicker").removeClass('datepicker-orient-top').addClass("datepicker-orient-bottom");
			}
		});
		$("#inputDate").val('');
	} else {
		$('#inputDate').datepicker('setDate', studentDob);
		$("#inputDate").val(studentDob);
	}
}
function setAllergies(studentAllergies) {
	var studentAllergyData = '';
	var studentAllergiesCount = 0;
	if (!isDataNull(studentAllergies)) {
		studentAllergyData = studentAllergies.split(",");
		studentAllergiesCount = studentAllergyData.length;
	}
	var allergyData = $('#generic_allergies').val().split(",");
	$('#checkboxes').html('');
	for (i = allergyData.length - 1; i >= 0; i--) {
		if (studentAllergiesCount > 0
				&& studentAllergyData.indexOf(allergyData[i]) != -1) {
			var chekboxrow = '<label for="' + allergyData[i]
					+ '"> <input type="checkbox" value="' + allergyData[i]
					+ '" name="allergyname" id="' + allergyData[i]
					+ '" checked />' + allergyData[i] + '';
		} else {
			var chekboxrow = '<label for="' + allergyData[i]
					+ '"> <input type="checkbox" value="' + allergyData[i]
					+ '" name="allergyname" id="' + allergyData[i] + '" />'
					+ allergyData[i] + '';
		}

		$('#checkboxes').append(chekboxrow);
	}
}
$(function() {
	$("#kid0").on("click", function(e) {
		$("#deviceUuidLink").val('');
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		$("#kidssection").css({
			'display' : 'none'
		});
		$("#addkidssection").css({
			'display' : 'none'
		});
		$("#deviceSectionLink").css({
			'display' : 'block'
		});
		$("#deviceSectionUnlink").css({
			'display' : 'none'
		});
		$(".icglabel_updatesuccess").css({
			"display" : "none"
		});
	});
});

$(function() {
	$("#cnfrmUnlinkBtn").on(
			"click",
			function(e) {
				var token_id = $("#token_id").val();
				var uuid = $("#deviceUuidUnlink").val();

				makePostAjaxRequest(
						"mobile/ParentUserDeviceUnPair/"
								+ token_id + "/" + uuid, null,
						onUnlinkDeviceSuccess, onUnlinkDeviceFailure);
			});
});

function onUnlinkDeviceSuccess(data) {
	debugLogs('Into onUnlinkDeviceSuccess');
	debugLogs(data);

	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.ResponseSummary)
			&& data.Return.ResponseSummary.StatusCode == 'SUC01') {
		var stndId = sessionStorage.getItem("studentId");
		debugLogs('studentId #onUnlinkDeviceSuccess' + '\t' + stndId);
		sessionStorage.setItem("studentId", 0);
		$("#kid" + stndId).parent().remove();
		$("#kid0").addClass("active");
		$("#kid0").click();
		$("#unlink_success").css({
			'display' : 'block'
		});
	}
}

function onUnlinkDeviceFailure(data) {
	debugLogs(data);
}

$(function() {
	$("#addLinkBtn").on(
			"click",
			function(e) {
				showSpinner();
				var token_id = $("#token_id").val();
				var uuid = $("#deviceUuidLink").val();
				debugLogs('uuid #addLinkBtn' + uuid);
				if (!isDataNull(uuid) && !isDataNull(token_id)) {
					makeGetAjaxRequest(
							"mobile/ParentUserDevicePair/"
									+ token_id + "/" + uuid, false,
							onDevicePairSuccess, onDevicePairFailure);
				}else{
					hideSpinnerNow();
				}
			});
});

function onDevicePairSuccess(data) {
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		newStudentData = data;
		$("#kidssection").css({
			'display' : 'block'
		});
		$("#addkidssection").css({
			'display' : 'block'
		});
		$("#invalidUUID").css({
			"display" : "none"
		});
		$("#unlink_success").css({
			'display' : 'none'
		});
		$("#link_success").css({
			'display' : 'none'
		});
		$("#validUUID").css({
			"display" : "block"
		});
		var current = data.Return.Results;

		$("#name").text(current.student_name);
		$("#classRollNo").text(current.roll_no);
		$("#regNo").text(current.registration_no);
		$("#kidsname").val(current.nickname);
		$("#kidsheight").val(current.height);
		$("#kidsContact").val(current.emergency_contact);
		if(current.gender === undefined){
			$("#kidsgender").val('Select the Gender');
		}else{
			$("#kidsgender").val(current.gender);
		}
		$("#kidsweight").val(current.weight);
		$("#student_id").val(current.student_id);
		
		setDOB(current.dob);
		setAllergies(current.allergies);
	} else {
		$("#invalidUUID").css({
			"display" : "block"
		});
		$("#validUUID").css({
			"display" : "none"
		});
		$("#kidssection").css({
			'display' : 'none'
		});
		$("#addkidssection").css({
			'display' : 'none'
		});
	}
}

function onDevicePairFailure(data) {
	debugLogs('Into onDevicePairFailure');
}

$(function() {
	$("#save")
			.on(
					"click",
					function(e) {
						showSpinner();
						clearErrorMessages();
						var isValid = true;
						var stdHght = $("#kidsheight").val();
						var stdDob = $("#inputDate").val();
						var stdContact = $("#kidsContact").val();
						var stdGender = $("#kidsgender").val();
						var stdNickName = $("#kidsname").val();
						var stdWeight = $("#kidsweight").val();
						var studentId = $("#student_id").val();
						var token_id = $("#token_id").val();

						if (stdNickName == "") {
							$("#kidname_err").css({
								"display" : "block"
							});
							isValid = false;
						}

						if (stdHght == "") {
							$("#kidheight_err").css({
								"display" : "block"
							});
							isValid = false;
						}

						if (stdDob == '') {
							$("#kidDob_err").css({
								"display" : "block"
							});
							isValid = false;
						}

						if (stdWeight == "") {
							$("#kidweight_err").css({
								"display" : "block"
							});
							isValid = false;
						}

						if ($("#kidsContact").val() == "") {
							$("#kidcontact_err").css({
								"display" : "block"
							});
							isValid = false;
						} else {
							if (!phoneRegEx.test($("#kidsContact").val())) {
								$("#startOfContactInvalid").css("display",
										"block");
								isValid = false;
							}
						}

						if (stdGender == "Select the Gender") {
							$("#kidgender_err").css({
								"display" : "block"
							});
							isValid = false;
						}

						if (isValid) {
							var stdAllergies = '', allergies = '';
							var checkbox = document
									.querySelector('input[name="allergyname"]:checked');
							$('input:checkbox[name=allergyname]:checked').each(
									function() {
										allergies += $(this).val() + ',';
									});
							if (allergies.length > 0)
								stdAllergies = allergies.substring(0,
										allergies.length - 1);

							var requestData = {
								"student_id" : studentId,
								"nickname" : stdNickName,
								"height" : stdHght,
								"weight" : stdWeight,
								"emergency_contact" : stdContact,
								"dob" : stdDob,
								"gender" : stdGender,
								"allergies" : stdAllergies
							};
							$("#save").attr('disabled','disabled');
							makePutAjaxRequest("mobile/StudentUpdate/"
									+ token_id, requestData,
									onKidUpdateSuccess, onKidUpdateFailure);
						}else{
							hideSpinnerNow();
						}
					});
});

function onKidUpdateSuccess(data) {
	$("#save").removeAttr('disabled');
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.ResponseSummary)
			&& data.Return.ResponseSummary.StatusCode == 'SUC01') {
		// Update values for tab attributes
		var stdAllergies = '', allergies = '';
		var checkbox = document.querySelector('input[name="allergyname"]:checked');
		$('input:checkbox[name=allergyname]:checked').each(function() {
			allergies += $(this).val() + ',';
		});
		if (allergies.length > 0)
			stdAllergies = allergies.substring(0, allergies.length - 1);
		
		if($('#kidsnavbar').parent().parent().find('.active').attr('id') == "kid0"){
			//If Link Kid Option
			
			$("div#kidsnavbar ul").prepend(
					'<li><a class="tabs" id="kid' + $("#student_id").val()
							+ '" href="#" student_id="' + $("#student_id").val()
							+ '" std_nickname="' + $("#kidsname").val() + '" std_name="'
							+ $("span#name").text() + '" std_roll_no="' + $("span#classRollNo").text()
							+ '" std_registartion_no="' + $("span#regNo").text()
							+ '" ' + ' std_emergency_contact="'
							+ $("#kidsContact").val() + '" height="' + $("#kidsheight").val()
							+ '" weight="' + $("#kidsweight").val() + '" dob="' + $("#inputDate").val()
							+ '" gender="' + $("#kidsgender").val() + '" allergies="' + stdAllergies
							+ '" uuid="' + $("#deviceUuidLink").val() + '">' + $("#kidsname").val()
							+ '</a></li>');
			
			$("#link_success").css({
				'display' : 'block'
			});
			
			$("#deviceUuidLink").val('');
			$("#kidssection").css({
				'display' : 'none'
			});
			$("#addkidssection").css({
				'display' : 'none'
			});
			$("#deviceSectionLink").css({
				'display' : 'block'
			});
			$("#deviceSectionUnlink").css({
				'display' : 'none'
			});
			$(".icglabel_updatesuccess").css({
				"display" : "none"
			});
			
		}else{
			var current = $('#kidsnavbar').parent().parent().find('.active');
			current.attr('std_nickname', $("#kidsname").val());
			current.attr('height', $("#kidsheight").val());
			current.attr('weight', $("#kidsweight").val());
			current.attr('dob', $("#inputDate").val());
			current.attr('std_emergency_contact', $("#kidsContact").val());
			current.attr('gender', $("#kidsgender").val());
			current.attr('allergies', stdAllergies);
			$(".icglabel_updatefail").css({
				"display" : "none"
			});
			$(".icglabel_updatesuccess").css({
				"display" : "block"
			});
		}
	}else{
		$(".icglabel_updatesuccess").css({
			"display" : "none"
		});
		$(".icglabel_updatefail").css({
			"display" : "block"
		});
	}
}

function onKidUpdateFailure(requestData) {
	$("#save").removeAttr('disabled');
	debugLogs(requestData);
	$(".icglabel_updatefail").css({
		"display" : "block"
	});
}

var expanded = false;
var expandedEdit = false;
$('html').click(function(e) {
	if (e.target.id == 'multiOverSelect' || e.target.name == 'allergyname') {
		if (!expanded) {
			$("#checkboxes").css({
				"display" : "block"
			});
			expanded = true;
		} else if (expanded && e.target.name == 'allergyname') {
			$("#checkboxes").css({
				"display" : "block"
			});
			expanded = true;
		} else {
			$("#checkboxes").css({
				"display" : "none"
			});
			expanded = false;
		}
	} else {
		$("#checkboxes").css({
			"display" : "none"
		});
		expanded = false;
	}
});

$(function() {
	$("#reset").on("click", function(e) {
		clearErrorMessages();
		var current = $('#kidsnavbar').parent().parent().find('.active');
		if (current.attr('id') == 'kid0') {
			onDevicePairSuccess(newStudentData);
		} else {
			loadStudentData(current);
		}
	});
});

function clearErrorMessages() {
	$("#unlink_success").css({
		'display' : 'none'
	});
	$("#link_success").css({
		'display' : 'none'
	});
	$("#invalidUUID").css({
		"display" : "none"
	});
	$("#validUUID").css({
		"display" : "none"
	});
	$(".icglabel_updatesuccess").css({
		"display" : "none"
	});
	$("#kidname_err").css({
		"display" : "none"
	});
	$("#kidheight_err").css({
		"display" : "none"
	});
	$("#kidDob_err").css({
		"display" : "none"
	});
	$("#kidweight_err").css({
		"display" : "none"
	});
	$("#kidcontact_err").css({
		"display" : "none"
	});
	$("#startOfContactInvalid").css({
		"display" : "none"
	});
	$("#kidgender_err").css({
		"display" : "none"
	});
}
