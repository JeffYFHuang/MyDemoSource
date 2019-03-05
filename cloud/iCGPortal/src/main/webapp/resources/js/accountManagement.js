$('.account_mgmt').removeClass("treeview").addClass("active");
$('.account_mgmt').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$("#sAccountIcon")
		.attr("src",
				"resources/images/SchoolAdmin_sideBarIcons/White/account_managment.png");
var token_id = $("#token_id").val();

onload = function() {
	var idx, foo = document.getElementById('accMgtRole');
	foo.selectedIndex = (idx = self.name.split('fooidx')) ? idx[1] : 0;
}

$(function() {
	var emailValidation = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;
	var text = $('#teacherAccount')[0].innerHTML;

	$("ol#bread").append(
			'<li class="tabs icglabel_teacherstaff">' + text + '</li>');

	$("#teacherAccount")
			.click(
					function() {
						sessionStorage.setItem("tabClick", "teacherTab");
						$(this).parent().parent().find('.active').removeClass(
								"active");
						console.log($(this));
						$(this).addClass("active");
						$("#teacher_section").css({
							"display" : "block"
						});
						$("#student_section").css({
							"display" : "none"
						});
						var text = $(this).context.innerHTML;
						$("ol#bread .tabs").remove();
						$("ol#bread .active").parent().remove();

						$("ol#bread").append(
								'<li class="tabs icglabel_teacherstaff">'
										+ text + '</li>');
						$("#teacher_invalidcsverror").attr('data-error','');
					});

	$("#studentAccount")
			.click(
					function() {
						sessionStorage.setItem("tabClick", "studentTab");
						$(this).parent().parent().find('.active').removeClass(
								"active");
						console.log($(this));
						$(this).addClass("active");
						$("#teacher_section").css({
							"display" : "none"
						});
						$("#student_section").css({
							"display" : "block"
						});
						var text = $(this).context.innerHTML;
						// $("ol#bread li .right").parent().remove();
						$("ol#bread .tabs").remove();
						$("ol#bread .active").parent().remove();

						$("ol#bread").append(
								'<li class="tabs icglabel_student">' + text
										+ '</li>');

						studentInit();
					});

	$("#accMgtCreate").click(
			function(e) {
				var isValidForm = true;
				$("#accMgtRole_err").css({
					"display" : "none"
				});
				$("#accMgtGrade_err").css({
					"display" : "none"
				});
				$("#accMgtClass_err").css({
					"display" : "none"
				});
				$("#accMgtName_err").css({
					"display" : "none"
				});
				$("#accMgtEmail_err").css({
					"display" : "none"
				});
				$("#accMgtContact_err").css({
					"display" : "none"
				});
				$("#accMgtContactStartOfContactInvalid").css({"display":"none"});

				if ($("#accTeacherStaffRole").val() == "") {
					isValidForm = false;
					$("#accMgtRole_err").css({
						"display" : "block"
					});
				}

				if ($("#accTeacherStaffRole").val() == "school_teacher") {
					if ($("#accMgtTeacherGrade").val() == "") {
						isValidForm = false;
						$("#accMgtGrade_err").css({
							"display" : "block"
						});
					}
					if ($("#accMgtTeacherClass").val() == "") {
						isValidForm = false;
						$("#accMgtClass_err").css({
							"display" : "block"
						});
					}
				}
				if ($("#accMgtName").val() == "") {
					isValidForm = false;
					$("#accMgtName_err").css({
						"display" : "block"
					});
				}
				if ($("#accMgtEmail").val() == "") {
					isValidForm = false;
					$("#accMgtEmail_err").css({
						"display" : "block"
					});
				} else {
					if (!emailValidation.test($("#accMgtEmail").val())) {
						$("#accMgtEmail_err").css("display", "block");
						isValidForm = false;
					}
				}
				if ($("#accMgtContact").val() == "") {
					isValidForm = false;
					$("#accMgtContact_err").css({
						"display" : "block"
					});
				} else {
					if (!phoneRegEx.test($("#accMgtContact").val())) {
						$("#accMgtContactStartOfContactInvalid").css("display",
								"block");
						isValidForm = false;
					}
				}

				if (!isValidForm) {
					e.preventDefault();
				}
			});
	$("#accMgtStudentCreate")
			.click(
					function(e) {
						var isValidForm = true;
						$("#accMgtStudentName_err").css({
							"display" : "none"
						});
						$("#accMgtStudentGrade_err").css({
							"display" : "none"
						});
						$("#accMgtStudentId_err").css({
							"display" : "none"
						});
						$("#accMgtStudentClassroom_err").css({
							"display" : "none"
						});
						$("#accMgtStudentAppno_err").css({
							"display" : "none"
						});
						$("#accMgtStudentContact_err").css({
							"display" : "none"
						});
						$("#accMgtStudentContactStartOfContactInvalid").css({
							"display" : "none"
						});
						$("#accMgtStudentRollno_err").css({
							"display" : "none"
						});

						if ($("#accMgtStudentName").val() == "") {
							isValidForm = false;
							$("#accMgtStudentName_err").css({
								"display" : "block"
							});
						}

						if ($("#accMgtStudentGrade").val() == "") {
							isValidForm = false;
							$("#accMgtStudentGrade_err").css({
								"display" : "block"
							});
						}
						if ($("#accMgtStudentId").val() == "") {
							isValidForm = false;
							$("#accMgtStudentId_err").css({
								"display" : "block"
							});
						}
						if ($("#accMgtStudentClassroom").val() == "") {
							isValidForm = false;
							$("#accMgtStudentClassroom_err").css({
								"display" : "block"
							});
						}
						if ($("#accMgtStudentAppno").val() == "") {
							isValidForm = false;
							$("#accMgtStudentAppno_err").css({
								"display" : "block"
							});
						}
						if ($("#accMgtStudentContact").val() == "") {
							isValidForm = false;
							$("#accMgtStudentContact_err").css({
								"display" : "block"
							});
						} else {
							if (!phoneRegEx
									.test($("#accMgtStudentContact").val())) {
								$("#accMgtStudentContactStartOfContactInvalid")
										.css("display", "block");
								isValidForm = false;
							}
						}
						if ($("#accMgtStudentRollno").val() == "") {
							isValidForm = false;
							$("#accMgtStudentRollno_err").css({
								"display" : "block"
							});
						}

						if (!isValidForm) {
							e.preventDefault();
						}
						studentInit();
					});
	/* collapse panel */
	$('.collapse').on(
			'shown.bs.collapse',
			function() {
				$(this).parent().find('.fa-arrow-circle-o-up').removeClass(
						'fa-arrow-circle-o-up').addClass(
						'fa-arrow-circle-o-down');
			}).on(
			'hidden.bs.collapse',
			function() {
				$(this).parent().find('.fa-arrow-circle-o-down').removeClass(
						'fa-arrow-circle-o-down').addClass(
						'fa-arrow-circle-o-up');
			});

	$('.studentEditnBtn').click(function() {

	});
	$("#schoolTeacherBrowse").click(function() {
		$("#csvFileUpload").click();
	});
	$("#schoolStudentBrowse").click(function() {
		$("#studentCsvFileUpload").click();
	});
});

var teacherGrade = '';
var teacherClass = '';
var student_grde = '';
var student_class = '';
function mergeTeacherDetails(usrObj) {
	$("#editAccMgtName_err").css("display", "none");
	$("#editAccMgtContact_err").css("display", "none");
	var teacher_id = usrObj.attr('teacher_id');
	var profile_name = unescape(usrObj.attr('profile_name'));
	teacherGrade = usrObj.attr('teacherStaff_grade');
	teacherClass = usrObj.attr('teacherStaff_class');
	var teacherContact = usrObj.attr('teacherStaff_contact');
	var teacherUsername = usrObj.attr('teacherStaff_username');
	var teacherRole = usrObj.attr('teacherStaff_role');
	
	debugLogs('profile_name' + profile_name);
	debugLogs('teacherGrade' + teacherGrade);

	debugLogs('teacherRole'+'\t'+teacherRole);
	if(teacherRole == 'Staff'){
		$("#accMgtEditTeacherGrade,#accMgtTeacherEditClass").prop("disabled",
				true);
	}else{
		$("#accMgtEditTeacherGrade,#accMgtTeacherEditClass").prop("disabled",
				false);
	}
	
	makeGetAjaxRequest("web/displayUnassinedGradeClass/" + token_id + "/"
			+ teacher_id, false, onUnassinedEditGradeClassSuccess,
			onUnassinedEditGradeClassFailure);

	$("#editAccMgtRole").val(teacherRole);
	$("#editAccMgtName").val(profile_name);
	$("#editAccMgtGrade").val(teacherGrade);
	$("#editAccMgtClass").val(teacherClass);
	$("#editAccMgtContact").val(teacherContact);
	$("#teacher_id").val(teacher_id);
}

function deleteTSDetails(usrObj) {
	var teacher_id = usrObj.attr('teacher_id');
	$("#teacher_id").val(teacher_id);
}

function deleteStudent(usrObj) {
	var student_id = usrObj.attr('student_id');
	$("#student_id").val(student_id);
}

function mergeStudentDetails(usrObj) {
	$("#editAccMgtStudentName_err").css("display", "none");
	$("#editAccMgtStudentGrade_err").css("display", "none");
	$("#editAccMgtStudentClass_err").css("display", "none");
	$("#editAccMgtStudentContact_err").css("display", "none");
	$("#editAccMgtStudentContactStartOfContactInvalid").css("display", "none");

	var student_id = usrObj.attr('student_id');
	var student_name = unescape(usrObj.attr('student_name'));
	var student_regno = usrObj.attr('student_regno');
	student_grade = usrObj.attr('student_grade');
	student_class = usrObj.attr('student_class');
	var student_rollno = usrObj.attr('student_rollno');
	var student_deviceuuid = usrObj.attr('student_deviceuuid');
	var student_contact = usrObj.attr('student_contact');

	makeGetAjaxRequest("web/getGradeClass/" + token_id, false,
			onEditStudentGradeSuccess, onEditStudentGradeFailure);

	debugLogs(student_name);
	debugLogs(student_grade);
	debugLogs(student_class);
	debugLogs(student_contact);

	$("#editAccMgtStudentName").val(student_name);
	$("#editAccMgtStudentGrade").val(student_grade);
	$("#editAccMgtStudentClass").val(student_class);
	$("#editAccMgtStudentContact").val(student_contact);
	$("#student_id").val(student_id);
}

var aGrades = '';
var aClass = '';

var unGrades = '';
var unClass = '';

var unEditGrades = '';
var unEditClass = '';

var avilStudentGrades = '';
var avilStudentClass = '';

var unEditStudentGrades = '';
var unEditStudentClass = '';

$(document).ready(
		function() {
			if($('#teacher_account_success_msg').attr('data-success') != ''){
				$('#teacher_account_success_msg').css("display", "block");
			}
			if($('#teacher_account_exists').attr('data-error') != ''){
				$('#teacher_account_exists').css("display", "block");
			}
			if($('#teacher_account_fail').attr('data-error') != ''){
				$('#teacher_account_fail').css("display", "block");
			}
			if($('#teacher_csvupload').attr('data-success') != ''){
				$('#teacher_csvupload').css("display", "block");
			}
			if($('#teacher_invalidcsverror').attr('data-error') != ''){
				$('#teacher_invalidcsverror').css("display", "block");
			}
			if($('#student_account_exists').attr('data-error') != ''){
				$('#student_account_exists').css("display", "block");
			}
			if($('#student_account_fail').attr('data-error') != ''){
				$('#student_account_fail').css("display", "block");
			}
			if($('#student_device_map_error').attr('data-error') != ''){
				$('#student_device_map_error').css("display", "block");
			}
			if($('#student_uuid_error').attr('data-error') != ''){
				$('#student_uuid_error').css("display", "block");
			}
			if($('#student_csvupload').attr('data-success') != ''){
				$('#student_csvupload').css("display", "block");
			}
			if($('#student_invalidcsverror').attr('data-error') != ''){
				$('#student_invalidcsverror').css("display", "block");
			}
			if($('#student_account_success_msg').attr('data-success') != ''){
				$('#student_account_success_msg').css("display", "block");
			}
			
			teacherInit();
			var tabClick = '';
			debugLogs($("#tabClick").val());
			tabClick = sessionStorage.getItem("tabClick");
			if (!isDataNull(tabClick) && tabClick == 'studentTab'){
				$("#studentAccount").click();
			}
			/*function showFileName(data){
				$('input:file').change();
			}*/
			$('#accMgtUpload').attr('disabled', true).css('opacity', '0.6');
			$('#accMgtStudentUpload').attr('disabled', true).css('opacity', '0.6');
			$('input:file')
					.change(
							function() {
								var fileExtension = 'csv';
								if ($(this).val().split('.').pop()
										.toLowerCase() == fileExtension) {
									if($(this).attr('id') == 'csvFileUpload'){
										fileContentsCheck($("#csvFileUpload")[0].files[0], 'teachers', 'teacher_csverror', 'accMgtUpload');
										$("#teacher_csvupload").css({"display":"none"});
										$("#teacherStaffRecordCount").css({"display":"none"});
										$("#teachersignoredList").css({"display":"none"});
										$("#teacher_invalidcsverror").css({"display":"none"});
									}else{
										fileContentsCheck($("#studentCsvFileUpload")[0].files[0], 'students', 'student_csverror', 'accMgtStudentUpload');
										$("#student_csvupload").css({"display":"none"});
										$("#studentsRecordCount").css({"display":"none"});
										$("#student_invalidcsverror").css({"display":"none"});
										$("#studentsIgnoredList").css({"display":"none"});
									}
								}else{
									if($(this).attr('id') == 'csvFileUpload'){
										$('#accMgtUpload').attr('disabled', true).css('opacity', '0.6');
									}else{
										$('#accMgtStudentUpload').attr('disabled', true).css('opacity', '0.6');
									}
								}
							});

			debugLogs(token_id);
			makeGetAjaxRequest("web/getGradeClass/" + token_id, false,
					onGradeSuccess, onGradeFailure);
			makeGetAjaxRequest("web/displayUnassinedGradeClass/" + token_id
					+ "/0	", false, onUnassinedGradeClassSuccess,
					onUnassinedGradeClassFailure);
		});

$("#studentAccount").click(function() {
	studentInit();
});

function onGradeSuccess(userData) {
	debugLogs('Success');
	aGrades = new Array();
	aClass = new Array();
	$('#student_grade').html('');
	$('#accMgtStudentGrade').html('');
	$('#accMgtStudentGradeFilter').html('');
	var grades = userData.Return.Results.Grades;
	var len = grades.length;
	$('#student_grade').append($('<option>', {
		value : "ALL",
		text : 'Grade'
	}));
	$('#accMgtStudentGrade').append($('<option>', {
		value : "",
		text : 'Select Grade'
	}));
	$('#accMgtStudentGradeFilter').append($('<option>', {
		value : "",
		text : 'Grade'
	}));

	if (len > 0) {
		for (var count = 0; count < len; count++) {
			var cGrade = grades[count].grade;
			var cClass = grades[count].class;

			if (aGrades.indexOf(cGrade) < 0) {
				aGrades.push(cGrade);
				$('#student_grade').append($('<option>', {
					value : cGrade,
					text : cGrade
				}));
				$('#accMgtStudentGrade').append($('<option>', {
					value : cGrade,
					text : cGrade
				}));
				$('#accMgtStudentGradeFilter').append($('<option>', {
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

function setStudentClass() {
	classList = aClass[$('#student_grade').val()];
	if (!isDataNull(classList)) {
		debugLogs('classList.length' + classList.length);
		$('#student_class').html('');
		for (i = 0; i < classList.length; i++) {
			$('#student_class').append($('<option>', {
				value : classList[i],
				text : classList[i]
			}));
		}
	}
}

function setStudentDefaultClass() {
	var classList1 = aClass[$('#accMgtStudentGrade').val()];
	$('#accMgtStudentClassroom').html('');
	$('#accMgtStudentClassroom').append($('<option>', {
		value : "",
		text : 'Select Class'
	}));
	if (!isDataNull(classList1)) {
		for (i = 0; i < classList1.length; i++) {
			$('#accMgtStudentClassroom').append($('<option>', {
				value : classList1[i],
				text : classList1[i]
			}));
		}
	}
}
function setFilterStudentClass() {
	$('#accMgtStudentClassFilter').html('');
	$('#accMgtStudentClassFilter').append($('<option>', {
		value : "",
		text : 'Class'
	}));

	var classList2 = aClass[$('#accMgtStudentGradeFilter').val()];
	if (!isDataNull(classList2)) {
		for (i = 0; i < classList2.length; i++) {
			$('#accMgtStudentClassFilter').append($('<option>', {
				value : classList2[i],
				text : classList2[i]
			}));
		}
	}
}
function onUnassinedGradeClassSuccess(userData) {
	debugLogs('Success');
	unGrades = new Array();
	unClass = new Array();
	$('#accMgtTeacherGrade').html('');
	var grades = userData.Return.Results.Grades;
	var len = grades.length;
	$('#accMgtTeacherGrade').append($('<option>', {
		value : "",
		text : 'Select Grade'
	}));
	if (len > 0) {
		for (var count = 0; count < len; count++) {
			var cGrade = grades[count].grade;
			var cClass = grades[count].class;

			if (unGrades.indexOf(cGrade) < 0) {
				unGrades.push(cGrade);
				$('#accMgtTeacherGrade').append($('<option>', {
					value : cGrade,
					text : cGrade
				}));
			}
			if (typeof unClass[cGrade] === 'undefined') {
				unClass[cGrade] = new Array();
			}
			unClass[cGrade].push(cClass);

		}
	}
}

function onUnassinedGradeClassFailure(userData) {
	debugLogs(userData);
}

function onUnassinedEditGradeClassSuccess(userData) {
	debugLogs('Into onUnassinedEditGradeClassSuccess() {');
	unEditGrades = new Array();
	unEditClass = new Array();
	$('#accMgtEditTeacherGrade').html('');
	var grades = userData.Return.Results.Grades;
	var len = grades.length;
	if (len > 0) {
		for (var count = 0; count < len; count++) {
			var cGrade = grades[count].grade;
			var cClass = grades[count].class;

			if (unEditGrades.indexOf(cGrade) < 0) {
				unEditGrades.push(cGrade);
				$('#accMgtEditTeacherGrade').append($('<option>', {
					value : cGrade,
					text : cGrade
				}));
			}
			if (typeof unEditClass[cGrade] === 'undefined') {
				unEditClass[cGrade] = new Array();
			}
			unEditClass[cGrade].push(cClass);
		}
		if (!isDataNull(teacherGrade)) {
			$('#accMgtEditTeacherGrade option[value="' + teacherGrade + '"]')
					.attr("selected", true);
		}
		setTeacherEditClass(userData);
	}
	debugLogs('Exiting onUnassinedEditGradeClassSuccess() }');
}

function onEditStudentGradeSuccess(userData) {
	debugLogs('userData' + userData);
	debugLogs('Into onEditStudentGradeSuccess() {');
	unEditStudentGrades = new Array();
	unEditStudentClass = new Array();
	$('#editAccMgtStudentGrade').html('');
	var grades = userData.Return.Results.Grades;
	var len = grades.length;
	if (len > 0) {
		for (var count = 0; count < len; count++) {
			var cGrade = grades[count].grade;
			var cClass = grades[count].class;

			if (unEditStudentGrades.indexOf(cGrade) < 0) {
				unEditStudentGrades.push(cGrade);
				$('#editAccMgtStudentGrade').append($('<option>', {
					value : cGrade,
					text : cGrade
				}));
			}
			if (typeof unEditStudentClass[cGrade] === 'undefined') {
				unEditStudentClass[cGrade] = new Array();
			}
			unEditStudentClass[cGrade].push(cClass);
		}
		if (!isDataNull(student_grade)) {
			$('#editAccMgtStudentGrade option[value="' + student_grade + '"]')
					.attr("selected", true);
		}
		setStudentEditClass(userData);
	}
	debugLogs('Exiting onEditStudentGradeSuccess() }');
}

function setStudentEditClass(userData) {
	debugLogs('student_class in setStudentEditClass()' + student_class);
	classList = unEditStudentClass[$('#editAccMgtStudentGrade').val()];
	debugLogs('classList.length' + classList.length);
	$('#editAccMgtStudentClass').html('');
	for (i = 0; i < classList.length; i++) {
		$('#editAccMgtStudentClass').append($('<option>', {
			value : classList[i],
			text : classList[i]
		}));
	}
	if (!isDataNull(student_class)) {
		$('#editAccMgtStudentClass option[value="' + student_class + '"]')
				.attr("selected", true);
	}
}

function onEditStudentGradeFailure(userData) {
	debugLogs(userData);
}

function onUnassinedEditGradeClassFailure(userData) {
	debugLogs('Into onUnassinedEditGradeClassFailure()');
	debugLogs(userData);
}

function setTeacherEditClass(userData) {
	debugLogs('Into setTeacherEditClass()');
	classList = unEditClass[$('#accMgtEditTeacherGrade').val()];
	debugLogs('classList.length' + classList.length);
	$('#accMgtTeacherEditClass').html('');
	for (i = 0; i < classList.length; i++) {
		$('#accMgtTeacherEditClass').append($('<option>', {
			value : classList[i],
			text : classList[i]
		}));
	}
	if (!isDataNull(teacherClass)) {
		$('#accMgtTeacherEditClass option[value="' + teacherClass + '"]').attr(
				"selected", true);
	}
}

function roleChange(userData) {
	debugLogs('roleChange' + userData.val());
	var role = userData.val();
	if (role == 'Staff')
		$("#accMgtEditTeacherGrade,#accMgtTeacherEditClass").prop("disabled",
				true);
	else
		$("#accMgtEditTeacherGrade,#accMgtTeacherEditClass").prop("disabled",
				false);
}

function roleChangeForTS(userData) {
	debugLogs('roleChange' + userData.val());
	var role = userData.val();
	if (role == 'school_staff')
		$("#accMgtTeacherGrade,#accMgtTeacherClass").prop("disabled",
				true);
	else
		$("#accMgtTeacherGrade,#accMgtTeacherClass").prop("disabled",
				false);
}

function setStudentUnassignedClass() {
	classList = unClass[$('#accMgtTeacherGrade').val()];

	$('#accMgtTeacherClass').html('');
	$('#accMgtTeacherClass').append($('<option>', {
		value : "",
		text : 'Select Class'
	}));
	for (i = 0; i < classList.length; i++) {
		$('#accMgtTeacherClass').append($('<option>', {
			value : classList[i],
			text : classList[i]
		}));
	}
}
function onGradeFailure(userData) {
	debugLogs('Failures');
}

$(function() {
	$("#updateTeacherDetails").on(
			"click",
			function(e) {
				$("#editAccMgtContact_err").css({
					"display" : "none"
				});
				$("#editAccMgtContactStartOfContactInvalid").css({
					"display" : "none"
				});

				var user_role = $('#editAccMgtRole').val();
				var name = $('#editAccMgtName').val();
				var grade = $('#accMgtEditTeacherGrade').val();
				var studentClass = $('#accMgtTeacherEditClass').val();
				var mobileNumber = $('#editAccMgtContact').val();
				var teacher_id = $('#teacher_id').val();

				var isValidForm = true;
				if (name.trim() === '' || name.trim().length === 0) {
					$("#editAccMgtName_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (mobileNumber.trim() == ''
						|| mobileNumber.trim().length === 0) {
					$("#editAccMgtContact_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				} else {
					if (!phoneRegEx.test(mobileNumber)) {
						$("#editAccMgtContactStartOfContactInvalid").css(
								"display", "block");
						e.preventDefault();
						isValidForm = false;
					}
				}
					
				if (isValidForm) {
					var requestData = {
						"user_role" : user_role,
						"name" : name,
						"grade" : grade,
						"studentClass" : studentClass,
						"mobileNumber" : mobileNumber
					};

					makePostAjaxRequest("updateTeacherStaff/" + teacher_id,
							requestData, onSuccessTeacherStaffUpdate,
							onFailureTeacherStaffUpdate);
				} else {
					e.preventDefault();
					return false;
				}
			});
	function onSuccessTeacherStaffUpdate(successResponseData) {
		debugLogs(successResponseData);
		teacherInit('update');
	}

	function onFailureTeacherStaffUpdate(failureResponseData) {
		debugLogs(failureResponseData);
	}
});

$(function() {
	$("#updateStudentDetails").on(
			"click",
			function(e) {
				$("#editAccMgtStudentContact_err").css({
					"display" : "none"
				});
				$("#editAccMgtStudentContactStartOfContactInvalid").css({
					"display" : "none"
				});

				debugLogs('Into updateStudentDetails() {');
				var studentName = $('#editAccMgtStudentName').val();
				var studentGrade = $('#editAccMgtStudentGrade').val();
				var studentClass = $('#editAccMgtStudentClass').val();
				var studentContactNo = $('#editAccMgtStudentContact').val();
				var student_id = $('#student_id').val();
				debugLogs(studentName);
				debugLogs(studentGrade);
				debugLogs(studentClass);
				debugLogs(studentContactNo);
				var isValidForm = true;

				if (studentName.trim() === ''
						|| studentName.trim().length === 0) {
					debugLogs('2');
					$("#editAccMgtStudentName_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (studentGrade.trim() == ''
						|| studentGrade.trim().length === 0) {
					debugLogs('2');
					$("#editAccMgtStudentGrade_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (studentClass.trim() == ''
						|| studentClass.trim().length === 0) {
					debugLogs('2');
					$("#editAccMgtStudentClass_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				}

				if (studentContactNo.trim() == ''
						|| studentContactNo.trim().length === 0) {
					debugLogs('2');
					$("#editAccMgtStudentContact_err").css("display", "block");
					e.preventDefault();
					isValidForm = false;
				} else {
					if (!phoneRegEx.test(studentContactNo)) {
						$("#editAccMgtStudentContactStartOfContactInvalid")
								.css("display", "block");
						e.preventDefault();
						isValidForm = false;
					}
				}

				if (isValidForm) {
					var requestData = {
						"name" : studentName,
						"grade" : studentGrade,
						"studentClass" : studentClass,
						"emergency_contact" : studentContactNo
					};
					makePostAjaxRequest("updateStudentApi/" + token_id + "/"
							+ student_id, requestData, onSuccessStudentUpdate,
							onFailureStudentUpdate);
					debugLogs('Exiting updateStudentDetails() }');
				} else {
					e.preventDefault();
					return false;
				}
			});
	function onSuccessStudentUpdate(successResponseData) {
		debugLogs(successResponseData);
		studentInit("update");
	}
	function onFailureStudentUpdate(failureResponseData) {
		debugLogs(failureResponseData);
	}
});

function studentInit(status) {
	$(".student_update_success").css({
		"display" : "none"
	});
	$(".student_delete_success").css({
		"display" : "none"
	});
	
	var page_id = 1;
	var studentGrade = $('#accMgtStudentGradeFilter').val();
	var studentClass = $('#accMgtStudentClassFilter').val();
	
	/*if(studentGrade == null)
		studentGrade = 'Grade';*/
	
	if(studentGrade == '' || studentGrade == null)
		studentGrade = 'Grade';
	if(studentClass == '')
		studentClass ='Class';
		
	if (!isDataNull(status) && status == 'update') {
		$('.student_update_success').css("display", "block");
		hideStudentMsg();
	}

	if (!isDataNull(status) && status == 'delete') {
		$('.student_delete_success').css("display", "block");
		hideStudentMsg();
	}
	
	/*if (isDataNull(studentUrl)) {
		studentUrl = "studentsWebListAPI/" + token_id+"/"+studentGrade+"/"+studentClass+"/"+page_id; 
	}*/
	studentUrl = "studentsWebListAPI/" + token_id+"/"+studentGrade+"/"+studentClass+"/"+page_id;
	makeGetAjaxRequest(studentUrl, false,
			onWebStudentsSuccess, onWebStudentsFailure);
}
function hideStudentMsg(){
	$('#student_device_map_error').attr("data-error", "");
	$('#student_device_map_error').css("display", "none");
	
	$('#student_account_fail').attr("data-error", "");
	$('#student_account_fail').css("display", "none");
	
	$('#student_account_exists').attr("data-error", "");
	$('#student_account_exists').css("display", "none");
	
	$('#student_account_success_msg').attr("data-success", "");
	$('#student_account_success_msg').css("display", "none");
}
function hideTeacherMsg(){
	$('#teacher_account_success_msg').attr("data-success", "");
	$('#teacher_account_success_msg').css("display", "none");
	
	$('#teacher_account_exists').attr("data-error", "");
	$('#teacher_account_exists').css("display", "none");
	
	$('#teacher_account_fail').attr("data-error", "");
	$('#teacher_account_fail').css("display", "none");
}
function teacherInit(status) {
	$(".teacher_update_success").css("display", "none");
	$(".teacher_delete_success").css("display", "none");
	$(".teacher_delete_failure").css("display", "none");
	
	if (!isDataNull(status) && status == 'update') {
		$('.teacher_update_success').css("display", "block");
		hideTeacherMsg();
	}

	if (!isDataNull(status) && status == 'deletesuccess') {
		$('.teacher_delete_success').css("display", "block");
		hideTeacherMsg();
	}

	if (!isDataNull(status) && status == 'deletefailure') {
		$('.teacher_delete_failure').css("display", "block");
		hideTeacherMsg();
	}
	var page_id = 1;
	var teacherRole = $('#accMgtRole').val();
	var grade = $('#student_grade').val();
	debugLogs('teacherRole'+'\t'+teacherRole);
	if(teacherRole == '')
		teacherRole = 'ALL';
	if(grade == '')
		grade ='ALL';
	
	var findTeacherUrl ="teachersStaffWebListAPI/"+token_id+"/"+teacherRole+"/"+grade+"/"+page_id ;
	makeGetAjaxRequest(findTeacherUrl, false,
			onTeacherStaffWebSuccess, onTeacherStaffWebFailure);
}

function onTeacherStaffWebSuccess(data) {
	debugLogs('Into onTeacherStaffWebSuccess ()');
	debugLogs(data);
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#teacherStaffWebSection').text('');

		var len = data.Return.Results.teacherstaff.length;

		if(len > 0){
			$("#pagination").css({
				"display" : "block"
			});
			for (var count = 0; count < len; count++) {
				var teacherstaff_id = data.Return.Results.teacherstaff[count].teacherstaff_id;
				var teacherstaff_name = data.Return.Results.teacherstaff[count].teacherstaff_name;
				var teacherstaff_status = (data.Return.Results.teacherstaff[count].teacherstaff_status == 'y') ? 'Active'
						: 'In-Active';
				var teacherstaff_role = data.Return.Results.teacherstaff[count].teacherstaff_role;
				var teacherstaff_class = data.Return.Results.teacherstaff[count].teacherstaff_class;
				var teacherstaff_grade = data.Return.Results.teacherstaff[count].teacherstaff_grade;
				var teacherstaff_contactno = data.Return.Results.teacherstaff[count].teacherstaff_contactno;
				var teacherstaff_username = data.Return.Results.teacherstaff[count].teacherstaff_username;

				assignedend = data.Return.Results.teacherstaff[count].noofPages;
				assignedstart = data.Return.Results.teacherstaff[count].currentPage;
				
				if (typeof teacherstaff_grade === 'undefined') {
					teacherstaff_grade = 'n/a';
				}

				if (typeof teacherstaff_class === 'undefined') {
					teacherstaff_class = 'n/a';
				}

				var rowData = '<tr>' + '<td>'
						+ teacherstaff_name
						+ '</td>'
						+ '<td>'
						+ teacherstaff_grade
						+ '</td>'
						+ '<td>'
						+ teacherstaff_class
						+ '</td>'
						+ '<td>'
						+ teacherstaff_contactno
						+ '</td>'
						+ '<td>'
						+ teacherstaff_username
						+ '</td>'
						+ '<td>'
						+ teacherstaff_status
						+ '</td>'
						+ '<td>'
						+ teacherstaff_role
						+ '</td>'
						+ '<td class="editIcon"><img onclick="mergeTeacherDetails($(this))" '
						+ 'src="resources/images/unselected_edit_icon.png" data-toggle="modal" data-target="#editTeacherDetails" data-keyboard="true" data-backdrop="static" '
						+ 'teacher_id="'
						+ teacherstaff_id
						+ '"  profile_name="'
						+ escape(teacherstaff_name)
						+ '" teacherStaff_grade="'
						+ teacherstaff_grade
						+ '" teacherStaff_class ="'
						+ teacherstaff_class
						+ '" '
						+ 'teacherStaff_contact="'
						+ teacherstaff_contactno
						+ '" teacherStaff_username="'
						+ teacherstaff_username
						+ '" teacherStaff_role="'
						+ teacherstaff_role
						+ '" class="hand" /></td> '
						+ '<td class="deleteIcon"><a style="color: black" '
						+ 'data-toggle="modal" data-target="#deleteDetails" ><img onclick="deleteTSDetails($(this))" teacher_id="'
						+ teacherstaff_id
						+ '" src="resources/images/Delete_icon.png" class="hand" /></a></td> '
						+ '</tr>';
				$("#teacherStaffWebSection").append(rowData);
			}
			document.getElementById('assigned_startPage').innerHTML = assignedstart;
			document.getElementById('assigned_endPage').innerHTML = assignedend;
			var assignedendConvertToInt = parseInt(assignedend);
			if(assignedstart === assignedendConvertToInt){
				$("#increase").attr("href", "javascript: void(0)");
				$("#decrease").attr("href", "javascript: void(0)");
			}
		}else {
			$("#pagination").css({
				"display" : "none"
			});
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			
			$('#teacherStaffWebSection').append(
			'<tr><td colspan="9"><strong>' + nodataText + '</strong></td></tr>');
			return false;
		}
	} else {
		$("#pagination").css({
			"display" : "none"
		});
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		
		$('#teacherStaffWebSection').append(
				'<tr><td colspan="9"><strong>' + nodataText + '</strong></td></tr>');
		return false;
	}
}

function onTeacherStaffWebFailure(data) {
	debugLogs(data);
}

function increaseassigendHref() {
	debugLogs('Into increaseassigendHref()');
	debugLogs('assignedstart' + '\t' + assignedstart);

	var teacherRole = $('#accMgtRole').val();
	var grade = $('#student_grade').val();
	debugLogs('teacherRole In increaseassigendHref()'+'\t'+teacherRole);
	if(teacherRole == '')
		teacherRole = 'ALL';
	if(grade == '')
		grade ='ALL';
	
	var currentpage = assignedstart;
	debugLogs('currentpage' + "\t" + currentpage);
	currentpage++;
	var token_id = $("#token_id").val();
	if (currentpage <= assignedend) {
		debugLogs('Getting into if loop');
		assignedstart = currentpage;
		makeGetAjaxRequest("teachersStaffWebListAPI/"+token_id+"/"+teacherRole+"/"+grade+"/"+currentpage, false, onTeacherStaffWebSuccess,
				onTeacherStaffWebFailure);
	}else{
		$("#increase").attr("href", "javascript: void(0)");
	}
}

function decreaseassigendHref() {
	debugLogs('Into decreaseassigendHref()');
	var teacherRole = $('#accMgtRole').val();
	var grade = $('#student_grade').val();
	debugLogs('teacherRole In decreaseassigendHref()'+'\t'+teacherRole);
	if(teacherRole == '')
		teacherRole = 'ALL';
	if(grade == '')
		grade ='ALL';
	
	var currentpage = assignedstart;
	currentpage--;
	var token_id = $("#token_id").val();
	if (currentpage > 0) {
		assignedstart = currentpage;
		makeGetAjaxRequest("teachersStaffWebListAPI/"+token_id+"/"+teacherRole+"/"+grade+"/"+currentpage, false, onTeacherStaffWebSuccess,
				onTeacherStaffWebFailure);
	}else{
		$("#decrease").attr("href", "javascript: void(0)");
	}
}

function onWebStudentsSuccess(data) {
	debugLogs(data);
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#stdntSection').text('');

		var len = data.Return.Results.students.length;
		if(len > 0){
			$("#studentspagination").css({
				"display" : "block"
			});
			for (var count = 0; count < len; count++) {
				var student_id = data.Return.Results.students[count].student_id;
				var student_name = data.Return.Results.students[count].student_name;
				var student_reg_no = data.Return.Results.students[count].student_reg_no;
				var student_grade = data.Return.Results.students[count].student_grade;
				var student_class = data.Return.Results.students[count].student_class;
				var student_rollno = data.Return.Results.students[count].student_rollno;
				var student_uuid = data.Return.Results.students[count].student_uuid;
				var student_emergency_contact = data.Return.Results.students[count].student_emergency_contact;

				assignedend = data.Return.Results.students[count].noofPages;
				assignedstart = data.Return.Results.students[count].currentPage;
				
				if (typeof student_uuid === 'undefined') {
					student_uuid = 'n/a';
				}
				var rowData = '<tr>' + '<td>'
						+ student_name
						+ '</td>'
						+ '<td>'
						+ student_reg_no
						+ '</td>'
						+ '<td>'
						+ student_grade
						+ '</td>'
						+ '<td>'
						+ student_class
						+ '</td>'
						+ '<td>'
						+ student_rollno
						+ '</td>'
						+ '<td>'
						+ student_uuid
						+ '</td>'
						+ '<td>'
						+ student_emergency_contact
						+ '</td>'
						+ '<td class="sedit"><a class="studentEditnBtn"'
						+ 'style="color: black"><img onclick="mergeStudentDetails($(this))"'
						+ 'src="resources/images/unselected_edit_icon.png" data-toggle="modal" data-target="#editStudentDetails" data-keyboard="true" '
						+ 'student_name="'
						+ escape(student_name)
						+ '" student_regno="'
						+ student_reg_no
						+ '" student_grade="'
						+ student_grade
						+ '" '
						+ 'student_class="'
						+ student_class
						+ '" student_rollno="'
						+ student_rollno
						+ '"'
						+ 'student_deviceuuid="'
						+ student_uuid
						+ '"'
						+ 'student_contact="'
						+ student_emergency_contact
						+ '" student_id="'
						+ student_id
						+ '" class="hand" /></a></td>'
						+ '<td class="deleteIcon"><a style="color: black" data-toggle="modal" data-target="#deleteStDetails"> '
						+ '<img onclick="deleteStudent($(this))" student_id="'
						+ student_id
						+ '"  src="resources/images/Delete_icon.png" class="hand" /></a></td>'
						+ '</tr>';
				$("#stdntSection").append(rowData);
			}
			document.getElementById('students_startPage').innerHTML = assignedstart;
			document.getElementById('students_endPage').innerHTML = assignedend;
			var assignedendConvertToInt = parseInt(assignedend);
			if(assignedstart === assignedendConvertToInt){
				$("#stincrease").attr("href", "javascript: void(0)");
				$("#stdecrease").attr("href", "javascript: void(0)");
			}
			
		}else{
			$("#studentspagination").css({
				"display" : "none"
			});
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			
			$('#stdntSection').append(
			'<tr><td colspan="9"><strong>' + nodataText + '</strong></td></tr>');
			return false;
		}
	} else {
		$("#studentspagination").css({
			"display" : "none"
		});
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		
		$('#stdntSection').append(
				'<tr><td colspan="9"><strong>' + nodataText + '</strong></td></tr>');
		return false;
	}
}

function onWebStudentsFailure(data) {
	debugLogs(data);
}

function increaseStudentsHref() {
	debugLogs('Into increaseStudentsHref()');
	debugLogs('assignedstart' + '\t' + assignedstart);
	
	var studentGrade = $('#accMgtStudentGradeFilter').val();
	var studentClass = $('#accMgtStudentClassFilter').val();
	
	if(studentGrade == '')
		studentGrade = 'Grade';
	if(studentClass == '')
		studentClass ='Class';
	
	var currentpage = assignedstart;
	debugLogs('currentpage' + "\t" + currentpage);
	currentpage++;
	var token_id = $("#token_id").val();
	if (currentpage <= assignedend) {
		debugLogs('Getting into if loop');
		assignedstart = currentpage;
		makeGetAjaxRequest("studentsWebListAPI/" + token_id+"/"+studentGrade+"/"+studentClass+"/"+currentpage, false, onWebStudentsSuccess,
				onWebStudentsFailure);
	}else{
		$("#stincrease").attr("href", "javascript: void(0)");
	}
}

function decreaseStudentsHref() {
	debugLogs('Into decreaseStudentsHref()');
	
	var studentGrade = $('#accMgtStudentGradeFilter').val();
	var studentClass = $('#accMgtStudentClassFilter').val();
	
	if(studentGrade == '')
		studentGrade = 'Grade';
	if(studentClass == '')
		studentClass ='Class';
	
	var currentpage = assignedstart;
	currentpage--;
	var token_id = $("#token_id").val();
	if (currentpage > 0) {
		assignedstart = currentpage;
		makeGetAjaxRequest("studentsWebListAPI/" + token_id+"/"+studentGrade+"/"+studentClass+"/"+currentpage, false, onWebStudentsSuccess,
				onWebStudentsFailure);
	}else{
		$("#stdecrease").attr("href", "javascript: void(0)");
	}
}

function deleteTeacherStaffDetails() {
	var teacher_id = $('#teacher_id').val();

	debugLogs('teacher_id' + teacher_id);
	makeGetAjaxRequest("deleteTeacherStaff/" + token_id + "/" + teacher_id,
			false, onDeleteTeacherStaffSuccess, onDeleteTeacherStaffFailure);
}

function onDeleteTeacherStaffSuccess(userData) {
	debugLogs('Into onDeleteTeacherStaffSuccess');
	debugLogs(userData);
	teacherInit('deletesuccess');
}

function onDeleteTeacherStaffFailure(userData) {
	debugLogs(userData);
	teacherInit('deletefailure');
}

function deleteStudentDetails() {
	debugLogs('Into deleteStudentDetails()');
	var student_id = $('#student_id').val();
	debugLogs('student_id' + student_id);
	makeGetAjaxRequest("deleteStudentApi/" + token_id + "/" + student_id,
			false, onDeleteStudentSuccess, onDeleteStudentFailure);
}

function onDeleteStudentSuccess(userData) {
	debugLogs(userData);
	studentInit('delete');
}

function onDeleteStudentFailure(userData) {
	debugLogs(userData);
}

$(function() {
	$("#findStudent").on(
			"click",
			function(e) {
				showSpinner();
				studentInit();
			});
});

$(function() {
	$("#findTeacher").on(
			"click",
			function(e) {
				showSpinner();
				$('#teacherStaffWebSection').text('');
				teacherInit();
			});
});

function showErrorsInTextArea(usrObj) {
	var eLog = JSON.parse(usrObj.attr('error_details'));
	var finalError = '';
	if(!isDataNull(eLog) && !isDataNull(eLog.error_log) && eLog.error_log.length > 0){
		for (i = 0; i < eLog.error_log.length; i++){
			var rowId = (eLog.error_log[i].row *1) + 1;
		    finalError += 'Row:' + rowId + ' >> ' + eLog.error_log[i].error + '\n';
		}
	}
	
	$("#errorDetails_id").val('').val(finalError);
}

function showStudentsErrorsInTextArea(usrObj) {
	var eLog = JSON.parse(usrObj.attr('student_error_details'));
	var finalError = '';
	if(!isDataNull(eLog) && !isDataNull(eLog.error_log) && eLog.error_log.length > 0){
		for (i = 0; i < eLog.error_log.length; i++){
			var rowId = (eLog.error_log[i].row *1) + 1;
		    finalError += 'Row:' + rowId + ' >> ' + eLog.error_log[i].error + '\n';
		}
	}
	
	$("#student_errorDetails_id").val('').val(finalError);
}