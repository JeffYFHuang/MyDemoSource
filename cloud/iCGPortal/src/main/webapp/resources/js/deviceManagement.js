var token_id = $("#token_id").val();
var assignedstart = 0;
var assignedend = 0;

var unassignedstart = 0;
var unassignedend = 0;
$(document)
		.ready(
				function() {

					makeGetAjaxRequest("web/getGradeClass/" + token_id, false,
							onGradeSuccess, onGradeFailure);

					$('.device_mgmt').removeClass("treeview")
							.addClass("active");
					$('.device_mgmt').removeClass("font-medium-roboto")
							.addClass("font-bold-roboto");
					$("#sDeviceIcon")
							.attr("src",
									"resources/images/SchoolAdmin_sideBarIcons/White/Device_managment.png");

					$("#findDevice")
							.click(
									function() {
										showSpinner();
										var grade = document
												.getElementById('scheduleGrade').value;
										var studentClass = document
												.getElementById('scheduleClassroom').value;
										if (!isDataNull(grade)
												&& !isDataNull(studentClass)) {
											init();
										} else {
											hideSpinnerNow();
											debugLogs("please select grade and class")
										}
										return false;
									});

					$("#assignedDevice").click(
							function() {
								$(this).parent().parent().find('.active')
										.removeClass("active");
								$(this).addClass("active");
								var text = $('#assignedDevice')[0].innerHTML;
								$("ol#bread li .right").parent().remove();
								$("ol#bread .tabs").remove();
								$("ol#bread .active").parent().remove();

								$("ol#bread").append(
										'<li class="tabs icglabel_assigneddevices">'
												+ text + '</li>');
								$("#unassigned_device_section").css({
									'display' : 'none'
								});
								$("#assigned_device_section").css({
									'display' : 'block'
								});
								// init();
								return false;
							});

					$("#unassignedDevice").click(
							function() {
								var page_id = 1;
								$(this).parent().parent().find('.active')
										.removeClass("active");
								$(this).addClass("active");
								var text = $(this).context.innerHTML;
								$("ol#bread li .right").parent().remove();
								$("ol#bread .tabs").remove();
								$("ol#bread .active").parent().remove();

								$("ol#bread").append(
										'<li class="tabs icglabel_unassigneddevices">'
												+ text + '</li>');
								$("#unassigned_device_section").css({
									'display' : 'block'
								});
								$("#assigned_device_section").css({
									'display' : 'none'
								});
								makeGetAjaxRequest(
										"deviceManagementUnassigendDeviceList?token="
												+ token_id + "&pageid="
												+ page_id, false,
										onUnassignedSuccess,
										onUnassignedFailure);
								return false;
							});

					$(this).parent().parent().find('.active').removeClass(
							"active");
					$(this).addClass("active");
					var text = $('#assignedDevice')[0].innerHTML;
					$("ol#bread li .right").parent().remove();
					$("ol#bread .tabs").remove();
					$("ol#bread .active").parent().remove();
					$("ol#bread").append(
							'<li class="tabs icglabel_assigneddevices">' + text
									+ '</li>');
					$("#unassigned_device_section").css({
						'display' : 'none'
					});
					$("#assigned_device_section").css({
						'display' : 'block'
					});
					var nodataText = getValueByLanguageKey('icglabel_nodata');
					$('#assignedSection').append(
							'<tr><td colspan="6"><strong>' + nodataText
									+ '</strong></td></tr>');

					var pageid = getUrlParameter('pageid');
					if (!isDataNull(pageid)) {
						if (pageid == "1")
							$("#unassignedDevice").click();
					}
				});

$("#update").click(
		function() {

			makePostAjaxRequest("updateUnassignedDeviceStatus?token="
					+ token_id, false, onUpdateSuccess, onUpdateFailure);

		});

function onUpdateSuccess(data) {
	debugLogs('updateUnassignedDeviceStatus is Success');
}

function onUpdateFailure() {

}

function openDeviceManagementpage() {

	$(this).parent().parent().find('.active').removeClass("active");
	$(this).addClass("active");
	var text = $('#assignedDevice')[0].innerHTML;
	$("ol#bread li .right").parent().remove();
	$("ol#bread .tabs").remove();
	$("ol#bread .active").parent().remove();

	$("ol#bread").append('<li class="tabs">' + text + '</li>');
	$("#unassigned_device_section").css({
		'display' : 'none'
	});
	$("#assigned_device_section").css({
		'display' : 'block'
	});
	init();
	return false;
}

function init() {
	var page_id = 1;
	var grade = document.getElementById('scheduleGrade').value;
	var studentClass = document.getElementById('scheduleClassroom').value;
	makeGetAjaxRequest("deviceManagementAssigendDeviceList?token=" + token_id
			+ "&studentClass=" + studentClass + "&grade=" + grade + "&pageid="
			+ page_id, false, onAssignedSuccess, onAssignedFailure);
}
function onAssignedSuccess(data) {
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#assignedSection').text('');

		var len = data.Return.Results.finalList.length;
		if (len > 0) {
			$("#pagination").css({
				"display" : "block"
			});
			for (var count = 0; count < len; count++) {
				var uuid = data.Return.Results.finalList[count].uuid;
				var student_name = data.Return.Results.finalList[count].student_name;
				var student_grade = data.Return.Results.finalList[count].student_grade;
				var student_class = data.Return.Results.finalList[count].student_class;
				var sDate = data.Return.Results.finalList[count].sDate;
				assignedend = data.Return.Results.finalList[count].noofPages;
				assignedstart = data.Return.Results.finalList[count].currentPage;
				var rowData = '<tr>' + '<td>' + uuid + '</td>' + '<td>'
						+ student_name + '</td>' + '<td>' + student_grade
						+ '</td>' + '<td>' + student_class + '</td>' + '<td>'
						+ sDate + '</td>' + '</tr>';
				$("#assignedSection").append(rowData);
			}

			document.getElementById('assigned_startPage').innerHTML = assignedstart;
			document.getElementById('assigned_endPage').innerHTML = assignedend;
			if (assignedstart === assignedend) {
				$("#increase").attr("href", "javascript: void(0)");
				$("#decrease").attr("href", "javascript: void(0)");
			}
		} else {
			document.getElementById('assigned_startPage').innerHTML = "";
			document.getElementById('assigned_endPage').innerHTML = "";

			$("#pagination").css({
				"display" : "none"
			});
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			$('#assignedSection').append(
					'<tr><td colspan="6"><strong>' + nodataText
							+ '</strong></td></tr>');
			return false;
		}
	} else {
		document.getElementById('assigned_startPage').innerHTML = "";
		document.getElementById('assigned_endPage').innerHTML = "";
		$("#pagination").css({
			"display" : "none"
		});
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		$('#assignedSection').append(
				'<tr><td colspan="6"><strong>' + nodataText
						+ '</strong></td></tr>');
		return false;
	}
}

function onAssignedFailure(data) {
	debugLogs("Into AssignFailure");
	debugLogs(data);
}

function getDeviceDetails() {
	showSpinner();
	var page_id = 1;
	var uuid = $('#adminDeviceUUIDforFind').val();
	if (uuid.length > 0){
		makeGetAjaxRequest("deviceManagementUnassigendDevice?token=" + token_id
				+ "&deviceUUID=" + uuid + "&pageid=" + page_id, false,
				onUnassignedSuccess, onUnassignedFailure);
	}else{
		hideSpinnerNow();
	}
		

}

function onUnassignedSuccess(data) {
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#unAssignedSection').text('');
		var len = data.Return.Results.finalList.length;
		if (len) {
			$("#pagination_deviceunassign").css({
				"display" : "block"
			});
			$("#submit").attr("disabled", false).css('opacity', '');
			$("#download").attr("disabled", false).css('opacity', '');
			for (var count = 0; count < len; count++) {
				var uuid = data.Return.Results.finalList[count].uuid;
				unassignedend = data.Return.Results.finalList[count].noofPages;
				unassignedstart = data.Return.Results.finalList[count].currentPage;
				var rowData = '<tr>'
						+ '<td> <span class="noneditable">'
						+ uuid
						+ '</span><input type="text" name="uuid" id="uuid" value= "'
						+ uuid
						+ '" class="editable form-control" style="display: none" /></td>'
						+

						'<td><select name="status" class="form-control-user" id="status">'
						+ '<option value="">unassinged</option>'
						+ '<option value="returned">returned</option>'
						+ '<option value="broken">broken</option>'
						+ '</select></td>' + '</tr>';
				$("#unAssignedSection").append(rowData);
			}
			document.getElementById('unassigned_startPage').innerHTML = unassignedstart;
			document.getElementById('unassigned_endPage').innerHTML = unassignedend;
			if (unassignedstart === unassignedend) {
				$("#unincrease").attr("href", "javascript: void(0)");
				$("#undecrease").attr("href", "javascript: void(0)");
			}
		} else {
			debugLogs('No un-assigned devices found');
			$("#submit").attr("disabled", "disabled").css('opacity', '0.6');
			$("#download").attr("disabled", "disabled").css('opacity', '0.6');

			var nodataText = getValueByLanguageKey('icglabel_nodata');
			$('#unAssignedSection').append(
					'<tr><td colspan="2"><strong>' + nodataText
							+ '</strong></td></tr>');
		}
	} else {
		debugLogs('Error reading un-assigned devices.');
	}
}

function onUnassignedFailure(data) {
	debugLogs("Into onUnassignedFailure()");
	debugLogs(data);

}

function increaseassigendHref() {
	var currentpage = assignedstart;
	currentpage++;
	var grade = document.getElementById('scheduleGrade').value;
	var studentClass = document.getElementById('scheduleClassroom').value;
	if (currentpage <= assignedend) {
		assignedstart = currentpage;
		makeGetAjaxRequest("deviceManagementAssigendDeviceList?token="
				+ token_id + "&studentClass=" + studentClass + "&grade="
				+ grade + "&pageid=" + currentpage, false, onAssignedSuccess,
				onAssignedFailure);
	} else {
		$("#increase").attr("href", "javascript: void(0)");
	}
	// return false;
}

function decreaseassigendHref() {
	var currentpage = assignedstart;
	currentpage--;
	var grade = document.getElementById('scheduleGrade').value;
	var studentClass = document.getElementById('scheduleClassroom').value;
	if (currentpage > 0) {
		assignedstart = currentpage;
		makeGetAjaxRequest("deviceManagementAssigendDeviceList?token="
				+ token_id + "&studentClass=" + studentClass + "&grade="
				+ grade + "&pageid=" + currentpage, false, onAssignedSuccess,
				onAssignedFailure);
	} else {
		$("#decrease").attr("href", "javascript: void(0)");
	}

}

function increaseunassigendHref() {
	var currentpage = unassignedstart;
	currentpage++;
	if (currentpage <= unassignedend) {
		unassignedstart = currentpage;
		makeGetAjaxRequest("deviceManagementUnassigendDeviceList?token="
				+ token_id + "&pageid=" + currentpage, false,
				onUnassignedSuccess, onUnassignedFailure);
	} else {
		$("#unincrease").attr("href", "javascript: void(0)");
	}
}

function decreaseunassigendHref() {
	var currentpage = unassignedstart;
	currentpage--;
	if (currentpage > 0) {
		unassignedstart = currentpage;
		makeGetAjaxRequest("deviceManagementUnassigendDeviceList?token="
				+ token_id + "&pageid=" + currentpage, false,
				onUnassignedSuccess, onUnassignedFailure);
	} else {
		$("#undecrease").attr("href", "javascript: void(0)");
	}

}

function downloadUnassignedList() {
	makeGetAjaxRequest("downloadCsv?token=" + token_id, false,
			onDownloadSuccess, onDownloadFailure);
}

function onDownloadSuccess() {

}
function onDownloadFailure() {

}
function assignedDeviceDetails(usrObj) {
	var page_id = usrObj.attr('pageid');
	makeGetAjaxRequest("deviceManagementAssigendDeviceList?token=" + token_id
			+ "&pageid=" + page_id, false, onAssignedSuccess, onAssignedFailure);
	return false;
}

function unAssignedDeviceDetails(usrObj) {
	var page_id = usrObj.attr('pageid');
	makeGetAjaxRequest("deviceManagementUnassigendDeviceList?token=" + token_id
			+ "&pageid=" + page_id, false, onUnassignedSuccess,
			onUnassignedFailure);
	return false;
}

function setStudentClass() {
	classList = aClass[$('#scheduleGrade').val()];

	$('#scheduleClassroom').html('');
	$('#scheduleClassroom').append($('<option>', {
		value : "",
		text : 'Please select a Class'
	}));
	if (!isDataNull(classList)) {
		for (i = 0; i < classList.length; i++) {
			$('#scheduleClassroom').append($('<option>', {
				value : classList[i],
				text : classList[i]
			}));
		}
	}
}

function onGradeSuccess(userData) {
	debugLogs('Success');
	aGrades = new Array();
	aClass = new Array();
	$('#scheduleGrade').html('');
	var grades = userData.Return.Results.Grades;
	var len = grades.length;
	$('#scheduleGrade').append($('<option>', {
		value : "",
		text : 'Please select a Grade'
	}));
	$('#scheduleClassroom').append($('<option>', {
		value : "",
		text : 'Please select a Class'
	}));
	if (len > 0) {
		for (var count = 0; count < len; count++) {
			var cGrade = grades[count].grade;
			var cClass = grades[count].class;

			if (aGrades.indexOf(cGrade) < 0) {
				aGrades.push(cGrade);
				$('#scheduleGrade').append($('<option>', {
					value : cGrade,
					text : cGrade
				}));
			}
			if (typeof aClass[cGrade] === 'undefined') {
				aClass[cGrade] = new Array();
			}
			aClass[cGrade].push(cClass);

		}
	}
}

function onGradeFailure(userData) {
	debugLogs('Failures');
}