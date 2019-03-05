var token_id = $("#token_id").val();
$(document).ready(function() {
	$('.class_schedule').removeClass("treeview").addClass("active");
	$('.class_schedule').removeClass("font-medium-roboto").addClass("font-bold-roboto");
	$("#sClassScheduleIcon")
			.attr("src",
					"resources/images/SchoolAdmin_sideBarIcons/White/class_schedule.png");
	
	if($('#csvupload').attr('data-success') != ''){
		$('#csvupload').css("display", "block");
	}
	if($('#invalidcsverror').attr('data-error') != ''){
		$('#invalidcsverror').css("display", "block");
	}
	makeGetAjaxRequest("web/getGradeClass/"
			+ token_id, false,
			onGradeSuccess, onGradeFailure);

	$("#scheduleGo").click(function(){
		showSpinner();
		var grade = document.getElementById('scheduleGrade').value;
		var studentClass = document.getElementById('scheduleClassroom').value;
		if(!isDataNull(grade) && !isDataNull(studentClass)){
			makeGetAjaxRequest("getTimeTable?token="+token_id+"&studentClass="+studentClass+"&grade="+grade, false,
					onSuccess, onFailure);
		}else{
			hideSpinnerNow();
			debugLogs("please select grade and class")
		}
		return false;
	});
	function onSuccess(data){
		if (!isDataNull(data) && !isDataNull(data.Return)
				&& !isDataNull(data.Return.Results)) {
			$('#dataSection').text('');

			var len = data.Return.Results.finalList.length;
			if(len==0){
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				$('#dataSection').append('<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
				return false;
			}

			for (var count = 0; count < len; count++) {
				var monday = data.Return.Results.finalList[count].MON;
				var tuesday = data.Return.Results.finalList[count].TUE;
				var wednesday = data.Return.Results.finalList[count].WED;
				var thursday = data.Return.Results.finalList[count].THR;
				var friday = data.Return.Results.finalList[count].FRI;
				var tempcount = count;
				tempcount++;
				var sub='sub';

				var rowData = '<tr>' +
				'<td>'+ sub+tempcount+'</td>' +
				'<td>' + monday + '</td>' +
				'<td>' + tuesday + '</td>' +
				'<td>' + wednesday + '</td>' +
				'<td>' + thursday + '</td>' +
				'<td>' + friday + '</td>' +
				'</tr>';
				$("#dataSection").append(rowData);
			}
		} else{
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			$('#dataSection').append('<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
			return false;
		}
	}
	
	$('#timetableUpload').attr('disabled', true).css('opacity', '0.6');
	$('input:file').change(function() {
		var fileExtension = 'csv';
		if ($(this).val().split('.').pop().toLowerCase() == fileExtension) {
			fileContentsCheck($("#csvFileUpload")[0].files[0], 'timetable', 'timetable_csverror', 'timetableUpload');
			$("#invalidcsverror").css({"display":"none"});
			$("#timetableignoredList").css({"display":"none"});
			$("#timetableRecordCount").css({"display":"none"});
			$("#csvupload").css({"display":"none"});
		}else{
			$('#timetableUpload').attr('disabled', true).css('opacity', '0.6');
		}
	});
});

function onFailure(){
	$('#dataSection').text('');
}

function onGradeSuccess(userData) {
	debugLogs('Success');
	aGrades = new Array();
	aClass = new Array();
	$('#scheduleGrade').html('');
	var grades = userData.Return.Results.Grades;
	var len = grades.length;
	$('#scheduleGrade').append($('<option>', {value:"", text:'Please select a Grade'}));
	$('#scheduleClassroom').append($('<option>', {value:"", text:'Please select a Class'}));
	if (len > 0){
		for (var count = 0; count < len; count++) {
			var cGrade = grades[count].grade;
			var cClass = grades[count].class;

			if(aGrades.indexOf(cGrade) < 0) {
				aGrades.push(cGrade);
				$('#scheduleGrade').append($('<option>', {value:cGrade, text:cGrade}));
			}
			if(typeof aClass[cGrade] === 'undefined') {
				aClass[cGrade] = new Array();
			}
			aClass[cGrade].push(cClass);

		}
	}
}

function setStudentClass() {
	classList = aClass[$('#scheduleGrade').val()];

	$('#scheduleClassroom').html('');
	$('#scheduleClassroom').append($('<option>', {value:"", text:'Please select a Class'}));
	if(!isDataNull(classList)){
		for (i=0; i<classList.length; i++) {
			$('#scheduleClassroom').append($('<option>', {value:classList[i], text:classList[i]}));
		}
	}
}

function onGradeFailure(userData) {debugLogs('Failures');}

function mergeCategoryInDetails(usrObj) {
	var eLog = JSON.parse(usrObj.attr('error_details'));
	var finalError = '';
	if(!isDataNull(eLog) && !isDataNull(eLog.error_log) && eLog.error_log.length > 0){
		for (i = 0; i < eLog.error_log.length; i++){
			var rowId = (eLog.error_log[i].row *1) + 1;
		    finalError += 'Row:' + rowId + ' >> ' + eLog.error_log[i].error + '\n';
		}
	}
	
	$("#editcategoryIn_id").val('').val(finalError);
}