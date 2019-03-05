$(function(){
	$('.fitness').removeClass("treeview").addClass("active");
	$('.fitness').removeClass("font-medium-roboto").addClass("font-bold-roboto");
	$("#sFitnessIcon").attr("src","resources/images/SchoolAdmin_sideBarIcons/White/fitness.png");

	var token_id = $("#token_id").val();
	var text = $('#fitnessIndex')[0].innerHTML;
	$("ol#bread").append(
			'<li class="tabs icglabel_physicalfitnessindex">'+text+'</li>');

	var weekCount = {'fitnessIndex': 0, 'steps': 0, 'calories': 0};
	var monthCount = {'fitnessIndex': 0, 'steps': 0, 'calories': 0};
	var currentDuration = {'fitnessIndex': 'week', 'steps': 'week', 'calories': 'week'};

	var gradesResponse = '';
	init("findFitnessDataGrades", "fitnessIndex");
	init("findStepsDataGrades", "steps");
	init("findCaloriesDataGrades", "calories");

	$(".duration .fc-button").click(function(){
		var cTab = $('ul.navbar-nav a.active').attr('id');
		var cDur = $(this).parent().parent().parent().attr('id');
		$("#" + cDur + " .fc-button").removeClass("selected-date");
		$(this).addClass("selected-date");
		var dateDisplay = cDuration = '';
		if($(this).hasClass("weekDate")){
			cDuration = 'week';
			dateDisplay = navigateWeek(weekCount[cTab])[2];
		} else {
			cDuration = 'month';
			dateDisplay = navigateMonth(monthCount[cTab])[2];
		}

		fnDateDisplay(dateDisplay, cDuration, cTab);
	});

	$(".navigation .fc-button").click(function(){
		var dateDisplay = cDuration = '';
		var cTab = $('ul.navbar-nav a.active').attr('id');
		if(currentDuration[cTab] == 'week'){
			if($(this).hasClass("fc-prev-button")){
				weekCount[cTab]--;
			}else{
				if(weekCount[cTab] == 0){
					return false;
				}else{
					weekCount[cTab]++;
				}
			} 
			dateDisplay = navigateWeek(weekCount[cTab])[2];
		}else{
			if($(this).hasClass("fc-prev-button")){
				monthCount[cTab]--;
			}else{
				if(monthCount[cTab] == 0){
					return false;
				}else{
					monthCount[cTab]++;
				}
			}
			dateDisplay = navigateMonth(monthCount[cTab])[2];
		}
		fnDateDisplay(dateDisplay, currentDuration[cTab], cTab);		
	});

	function fnDateDisplay(dateDisplay, cDuration, cTab){
		if(cTab == 'fitnessIndex'){
			$('#pfiDateRange').text(dateDisplay);
		}else if(cTab == 'steps'){
			$('#stepsDateRange').text(dateDisplay);
		}else if(cTab == 'calories'){
			$('#calDateRange').text(dateDisplay);
		}
		currentDuration[cTab] = cDuration;
	}
	function init(gradeObjId, cTab){	
		if(gradeObjId == 'findFitnessDataGrades'){
			$('#pfiRewardweek').attr('class', 'selected-date ' + $('#pfiRewardweek').attr('class'));
		}else if(gradeObjId == 'findStepsDataGrades'){
			$('#stepsRewardweek').attr('class', 'selected-date ' + $('#stepsRewardweek').attr('class'));
		}else if(gradeObjId == 'findCaloriesDataGrades'){
			$('#calRewardweek').attr('class', 'selected-date ' + $('#calRewardweek').attr('class'));
		}

		fnDateDisplay(navigateWeek(weekCount[cTab])[2], currentDuration[cTab], cTab);	

		if(!isDataNull(gradesResponse)){
			onGradeSuccess(gradesResponse, gradeObjId)
		}else{
			makeGetAjaxRequest("web/getGradeClass/"
					+ token_id, false, function(data){ 
						onGradeSuccess(data, gradeObjId)
					}, onGradeFailure);
		}
	}

	function loadFitnessData(grade, type, dataObjId, avgObjId){
		var input = [];

		if(type == "pfi" && $('#pfiDateRange').length && !isDataNull($('#pfiDateRange').text())){
			input = $('#pfiDateRange').text().split(' - ');
		}else if(type == "steps" && $('#stepsDateRange').length && !isDataNull($('#stepsDateRange').text())){
			input = $('#stepsDateRange').text().split(' - ');			
		}else if(type == "calories" && $('#calDateRange').length && !isDataNull($('#calDateRange').text())){
			input = $('#calDateRange').text().split(' - ');
		}

		var requestDataFitness = {
				"measure_type":type,
				"start_date": input[0],
				"end_date": input[1],
				"grade": grade
		};

		if(grade != "0"){
			makePostAjaxRequest("web/GradewiseFitness/"+token_id, requestDataFitness, function(data){ 
				onSuccessCalculateFitnessStats(data, dataObjId, avgObjId)
			}, onFailureFitnessReportData);
		}else{
			hideSpinnerNow();
		}
	}

	function onFailureFitnessReportData(responseData){
		debugLogs("onFailureFitnessReportData");
	}

	$("#fitnessIndex").click(function(){  
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		var text = $(this).context.innerHTML;
		$("ol#bread .tabs").remove();
		$("ol#bread .active").parent().remove();

		$("ol#bread").append(
				'<li class="tabs">'+text+'</li>');

		$("#school-pfi-data").css({"display":"block"});
		$("#school-steps-data").css({"display":"none"});
		$("#school-calories-data").css({"display":"none"});
		$('#school_geofencingList').empty();
	});

	$("#steps").click(function(){  
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		var text = $(this).context.innerHTML;
		$("ol#bread .tabs").remove();
		$("ol#bread .active").parent().remove();

		$("ol#bread").append(
				'<li class="tabs">'+text+'</li>');

		$("#school-pfi-data").css({"display":"none"});
		$("#school-steps-data").css({"display":"block"});
		$("#school-calories-data").css({"display":"none"});
	});
	$("#calories").click(function(){  
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		var text = $(this).context.innerHTML;
		$("ol#bread .tabs").remove();
		$("ol#bread .active").parent().remove();

		$("ol#bread").append(
				'<li class="tabs">'+text+'</li>');

		$("#school-pfi-data").css({"display":"none"});
		$("#school-steps-data").css({"display":"none"});
		$("#school-calories-data").css({"display":"block"});
	});

	function onGradeSuccess(data, dataGrades) {
		if(isDataNull(gradesResponse)) gradesResponse = data;
		aGrades = new Array();
		$('#' + dataGrades).html('');
		if(!isDataNull(gradesResponse.Return.Results)){
			var grades = gradesResponse.Return.Results.Grades;
			var len = grades.length;
			if (len > 0){
				for (var count = 0; count < len; count++) {
					var cGrade = grades[count].grade;
					if(aGrades.indexOf(cGrade) < 0) {
						aGrades.push(cGrade);
						var cGradeText = 'Grade ' + grades[count].grade;
						$('#' + dataGrades).append($('<option>', {value:cGrade, text:cGradeText}));
					}
				}

				if(dataGrades == 'findFitnessDataGrades'){
					$("#findFitnessData").trigger("click");
				}else if(dataGrades == 'findStepsDataGrades'){
					$("#findStepsData").trigger("click");
				}else if(dataGrades == 'findCaloriesDataGrades'){
					$("#findCaloriesData").trigger("click");
				}
			}
		}
	}

	function onGradeFailure(data) {
		debugLogs("Inside onGradeFailure");
	}

	$("#findFitnessData").click(function(){
		showSpinner();
		var grade = $("#findFitnessDataGrades").val();
		if(grade != "0") loadFitnessData(grade, "pfi", "pfi-data", "pfi-avg");
		return false;
	});
	$("#findStepsData").click(function(){
		showSpinner();
		var grade = $("#findStepsDataGrades").val();
		if(grade != "0") loadFitnessData(grade, "steps", "steps-data", "steps-avg");
		return false;
	});
	$("#findCaloriesData").click(function(){
		showSpinner();
		var grade = $("#findCaloriesDataGrades").val();
		if(grade != "0") loadFitnessData(grade, "calories", "calories-data", "calories-avg");
		return false;
	});

	function onSuccessCalculateFitnessStats(data, resultData, resultAverage){
		var totalRecords = data.Return.Results.length;
		var gradeTotal = 0;
		$('#' + resultData).empty();
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		if(totalRecords){
			var objects = data.Return.Results;
			var categories = new Array();
			var groupedObjects = new Array();
			var classToppers = new Array();
			var i = 0;

			var maxValue = {};
			var maxStudent = {};
			var classCount = {};

			_.each(objects,function(obj){
				var existingObj;

				if($.inArray(obj.class,categories) >= 0) {
					existingObj = _.find(objects,function(o){return o.class === obj.class; });
					existingObj.value += obj.value;

					var cls = obj.class;

					if (typeof maxValue[cls] === "undefined") {
						maxValue[cls] = obj.value;
						maxStudent[cls] = obj.sname;
					}
					if(obj.value > maxValue[cls]){
						maxValue[cls] = obj.value;
						maxStudent[cls] = obj.sname;
					}
					classCount[cls] = classCount[cls] + 1;

					existingObj.max_value = maxValue[cls];
					existingObj.max_value_student = maxStudent[cls];
					existingObj.class_strength = classCount[cls];
					existingObj.class_avg = (existingObj.value/existingObj.class_strength);
				} else {
					groupedObjects[i] = obj;
					categories[i] = obj.class;
					maxValue[obj.class] = obj.value;
					maxStudent[obj.class] = obj.sname;
					classCount[obj.class] = 1;

					obj.max_value = maxValue[obj.class];
					obj.max_value_student = maxStudent[obj.class];
					obj.class_strength = classCount[obj.class];
					obj.class_avg = (obj.value/obj.class_strength);
					i++;
				}
			});

			groupedObjects = _.sortBy(groupedObjects,function(obj){ return obj.class_avg; }).reverse();

			ranking = 0;
			_.each(groupedObjects,function(obj){
				ranking++;
				var display = '';
				var sname = stClass = max_value_student = '';
				var sid = classTotal = max_value = class_strength = class_avg = 0;
				_.each(obj,function(val,key){
					switch(key){
					case 'studentid':
						sid = val;
						break;
					case 'sname':
						sname = val;
						break;
					case 'class':
						stClass = val;
						break;
					case 'value':
						classTotal = val;
						gradeTotal +=classTotal;
						break;
					case 'max_value':
						max_value = val;
						classToppers.push(max_value);
						break;
					case 'max_value_student':
						sname = val;
						break;
					case 'class_strength':
						class_strength = val;
						break;
					case 'class_avg':
						class_avg = val.toFixed(2);
						break;
					}
				});

				display = '<tr>' +
				'<td>' + ranking + '</td>' + 
				'<td>' + stClass + '</td>' + 
				'<td>' + class_avg + '</td>' + 
				'<td>' + sname + '</td>' + 
				'<td class="' + max_value + '">' + max_value + '</td>' + 
				'</tr>';
				$('#' + resultData).append(display);
			});

			if(classToppers.length){
				classToppers = $.unique(classToppers).sort(function(a, b){return b-a});
				var topReward = ["resources/images/gold_badges.png", "resources/images/Silver_badges.png", "resources/images/Bronze.png", "resources/images/green_badges.png"];
				var topRewardTitle = ['Gold', 'Silver', 'Bronze','Green'];
				var top3 = 0;
				for(var i = 0; i < classToppers.length; i++){
					if(classToppers[i] > 0) {
						if(top3 >= 3){
							$('#' + resultData + ' .' + classToppers[i]).append('&nbsp;&nbsp;&nbsp;&nbsp;<img src="' + topReward[3] + '" alt="' + topRewardTitle[3] + '" title="' + topRewardTitle[3] + '" />');
						}else{
							$('#' + resultData + ' .' + classToppers[i]).append('&nbsp;&nbsp;&nbsp;&nbsp;<img src="' + topReward[top3] + '" alt="' + topRewardTitle[top3] + '" title="' + topRewardTitle[top3] + '" />');
							top3++;
						}
					}
				}
			}

			var gradeAverage = 'n/a';
			if(gradeTotal > 0 && totalRecords > 0){
				gradeAverage = (gradeTotal/totalRecords).toFixed(2);
			}
			$('#' + resultAverage).text(gradeAverage);
		}else{
			$('#' + resultData).append('<tr><td colspan="5"><strong>' + nodataText + '</strong></td></tr>');
			$('#' + resultAverage).text('');
		}		
	}
});
