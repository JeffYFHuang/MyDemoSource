var avgScore ={};
var compared= false;
var st_name = sessionStorage.getItem("nick_name");
var loading = true;
$(document).ready(function() {
	$('.activity').removeClass("treeview").addClass("active");
	$('.activity').removeClass("font-medium-roboto").addClass("font-bold-roboto");
	$("#sActivityIcon").attr("src","resources/images/sidemenu_icon/white/activities_w.png");

	google.charts.load('current', {
		'packages' : [ 'corechart', 'bar', 'line' ]
	});
	google.charts.setOnLoadCallback(drawStudentActivityCharts);
});

function drawStudentActivityCharts(){
	$("#acticitySName, #activityKidName").text(st_name);
	var weekCount = 0, monthCount = 0;
	var currentDuration = 'week'; //Default week is selected
	var monthDisplay, weekDisplay = '';
	$('#activityweek').attr('class', 'selected-date ' + $('#activityweek').attr('class'));
	weekDisplay = navigateWeek(weekCount);
	var jsonData = {};
	var pfi = "",stress = "", activity = "",calories = "",sleep = "", heartRate = "", emotion = "", steps = "";
	init();
	function init(){
		showSpinner();
		loadReportData(weekDisplay,currentDuration);
	}
	$(".duration .fc-button").click(function(){
		loading = false;
		compared= false;
		jsonData = {},avgScore ={};
		pfi = "",stress = "", activity = "",calories = "",sleep = "", heartRate = "", emotion = "", steps = "";
		$(".duration .fc-button").removeClass("selected-date");
		$(this).addClass("selected-date");
		if($(this).hasClass("weekDate")){
			currentDuration = 'week';
			weekDisplay = navigateWeek(weekCount);
			showSpinner();
			loadReportData(weekDisplay,currentDuration);
		} else {
			currentDuration = 'month';
			if(isDataNull(monthDisplay)) monthDisplay = navigateMonth(monthCount);
			showSpinner();
			loadReportData(monthDisplay,currentDuration);
		}
	});

	$(".navigation .fc-button").click(function(){
		loading = false;
		compared= false;
		jsonData = {},avgScore ={};
		pfi = "",stress = "", activity = "",calories = "",sleep = "", heartRate = "", emotion = "", steps = "";
		if(currentDuration == 'week'){
			if($(this).hasClass("fc-prev-button")){
				weekCount--;
			}else{
				if(weekCount == 0){
					return false;
				}else{
					weekCount++;
				}
			} 
			weekDisplay = navigateWeek(weekCount);
			showSpinner();
			loadReportData(weekDisplay,currentDuration);
		}else{
			if($(this).hasClass("fc-prev-button")){
				monthCount--;
			}else{
				if(monthCount == 0){
					return false;
				}else{
					monthCount++;
				}
			}
			monthDisplay = navigateMonth(monthCount);
			showSpinner();
			loadReportData(monthDisplay,currentDuration);
		}			
	});
	var compare=[],k=-1;
	compare[0] = "pfi",compare[1] ="sleep";
	$(".check").attr("checked", "checked");
	$('input:checkbox[name=compareChart]').change(function() {
		if($(this).is(":checked")){
			k++;
			if(k<2){
				compare[k] = $(this).val();
				$(this).removeClass('enabled').addClass('check');
				$(".check").attr("checked", "checked");
			}
			if(k==1){
				$('.enabled').removeClass('enabled').addClass('disabled');
				$('.disabled').attr('disabled','disabled');
				checkActivityToBeCompared(compare[0],compare[1]);
			}
		}
		if($(this).is(":not(:checked)")){
			$(this).removeClass('check').addClass('enabled');
			if($(this).val() === compare[0]){
				compare[0] = compare[1];
			}
			k--;
			$('.disabled').removeClass('disabled').addClass('enabled');
			$('.enabled').removeAttr('disabled');
		} 
	});
	function checkActivityToBeCompared(compareFirst,compareSecond){
		debugLogs("compareFirst: " + compareFirst);
		debugLogs("compareSecond: " + compareSecond);
		var first = jsonData[compareFirst];
		var second = jsonData[compareSecond]; 
		var firstscore = 0,secondcore = 0;
		if(!isDataNull(avgScore[compareFirst])){
			firstscore = avgScore[compareFirst];
		}
		if(!isDataNull(avgScore[compareSecond])){
			secondcore = avgScore[compareSecond];
		}
		$("#firstscore").text(roundToTwo(firstscore));
		$("#secondcore").text(roundToTwo(secondcore));
		var firstDataForSuggestion= compareFirst.charAt(0).toUpperCase() + compareFirst.slice(1);
		var secondDataForSuggestion= compareSecond.charAt(0).toUpperCase() + compareSecond.slice(1);
		suggestion(firstDataForSuggestion,secondDataForSuggestion);
		debugLogs("first: " + first);
		debugLogs("second: " + second);
		debugLogs("jsonData: " + jsonData);
		if(currentDuration == 'week'){
			weekDisplay = navigateWeek(weekCount);
			drawCompareChart(first,second,weekDisplay,compareFirst,compareSecond);
		}else{
			monthDisplay = navigateMonth(monthCount);
			drawCompareChart(first,second,monthDisplay,compareFirst,compareSecond);
		}
	}
	function loadReportData(input,currentDuration){
		var token = sessionStorage.getItem("token_id") 
		var student_id = sessionStorage.getItem("student_id") 

		/****************Start of Fitness********************/
		var requestDataFitness = {
			"measure_type":"fitness",
			"start_date": input[0],
			"end_date": input[1],
			"student_id": student_id
		};
		makePostAjaxRequest("web/StudentActivity/"+token, requestDataFitness, onSuccessFitnessReportData, onFailureFitnessReportData);
		function onSuccessFitnessReportData(responseData){
			if(!isDataNull(responseData.Return.Results)){
				pfi = responseData.Return.Results.fitness;
				obj = jsonSort(pfi, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				debugLogs("pfi",pfi);
				jsonData["pfi"] = pfi;
			}
			count = weekCount;
			lastWeekDisplay = navigateWeek(--count);
			var requestDataFitnessLastWeek = {
					"measure_type":"fitness",
					"start_date": lastWeekDisplay[0],
					"end_date": lastWeekDisplay[1],
					"student_id": student_id
			};
			makePostAjaxRequest("web/StudentActivity/"+token, requestDataFitnessLastWeek, onSuccessFitnessLastWeek, onFailureFitnessLastWeek);
		}

		function onFailureFitnessReportData(responseData){
			debugLogs("onFailureFitnessReportData");
		}
		function onSuccessFitnessLastWeek(responseData){
			debugLogs("responseData : "+responseData.status);
			if(!isDataNull(responseData.Return.Results)){
				pfiLastWeek = responseData.Return.Results.fitness;
				obj = jsonSort(pfiLastWeek, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				drawPFIChart(pfi,pfiLastWeek,input,lastWeekDisplay);
			}
		}
		function onFailureFitnessLastWeek(responseData){
			debugLogs("onFailureFitnessLastWeek");
		}
		/****************End of Fitness********************/

		/****************Start of Steps********************/
		var requestDataSteps = {
				"measure_type":"steps",
				"start_date": input[0],
				"end_date": input[1],
				"student_id": student_id
		};
		makePostAjaxRequest("web/StudentActivity/"+token, requestDataSteps, onSuccessStepsReportData, onFailureStepsReportData);

		function onSuccessStepsReportData(responseData){
			if(!isDataNull(responseData.Return.Results)){
				steps = responseData.Return.Results.steps;
				obj = jsonSort(steps, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				debugLogs("steps" + steps);
				jsonData["steps"] = steps;
			}
			count = weekCount;
			lastWeekDisplay = navigateWeek(--count);
			var requestDataStepsLastWeek = {
					"measure_type":"steps",
					"start_date": lastWeekDisplay[0],
					"end_date": lastWeekDisplay[1],
					"student_id": student_id
			};
			makePostAjaxRequest("web/StudentActivity/"+token, requestDataStepsLastWeek, onSuccessStepsLastWeek, onFailureStepsLastWeek);
		}

		function onFailureStepsReportData(responseData){
			debugLogs("onFailureStepsReportData");
		}
		function onSuccessStepsLastWeek(responseData){
			if(!isDataNull(responseData.Return.Results)){
				stepsLastWeek = responseData.Return.Results.steps;
				obj = jsonSort(stepsLastWeek, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				drawStepsChart(steps,stepsLastWeek,input,lastWeekDisplay);
			}
		}
		function onFailureStepsLastWeek(responseData){
			debugLogs("onFailureStepsLastWeek");
		}
		/****************End of Steps********************/

		/****************Start of Activity********************/
		var requestDataActivity = {
				"measure_type":"activity",
				"start_date": input[0],
				"end_date": input[1],
				"student_id": student_id
		};
		makePostAjaxRequest("web/StudentActivity/"+token, requestDataActivity, onSuccessActivityReportData, onFailureActivityReportData);

		function onSuccessActivityReportData(responseData){
			if(!isDataNull(responseData.Return.Results)){
				activity = responseData.Return.Results.activity;
				obj = jsonSort(activity, "date", DATATYPE.NUMBER, SORTORDER.ASC, null);
				debugLogs("activity" + activity);
				jsonData["activity"] = activity;
			}
			count = weekCount;
			lastWeekDisplay = navigateWeek(--count);
			var previousRequestDataActivity = {
					"measure_type":"activity",
					"start_date": lastWeekDisplay[0],
					"end_date": lastWeekDisplay[1],
					"student_id": student_id
			};
			makePostAjaxRequest("web/StudentActivity/"+token, previousRequestDataActivity, onSuccessPreviousActivity, onFailurePreviousActivity);
		}

		function onFailureActivityReportData(responseData){
			debugLogs("onFailureActivityReportData");
		}
		function onSuccessPreviousActivity(responseData){
			if(!isDataNull(responseData.Return.Results)){
				activityLastWeek = responseData.Return.Results.activity;
				obj = jsonSort(activityLastWeek, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				drawActivitiesChart(activity,activityLastWeek,input,lastWeekDisplay);
			}
		}
		function onFailurePreviousActivity(responseData){
			debugLogs("onFailurePreviousActivity");
		}
		/****************End of Activity********************/

		/****************Start of Calories********************/
		var requestDataCalories = {
				"measure_type":"calories",
				"start_date": input[0],
				"end_date": input[1],
				"student_id": student_id
		};
		makePostAjaxRequest("web/StudentActivity/"+token, requestDataCalories, onSuccessCaloriesReportData, onFailureCaloriesReportData);

		function onSuccessCaloriesReportData(responseData){
			if(!isDataNull(responseData.Return.Results)){
				calories = responseData.Return.Results.calories;
				obj = jsonSort(calories, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				debugLogs("calories",calories);
				jsonData["calories"] = calories;
			}
			count = weekCount;
			lastWeekDisplay = navigateWeek(--count);
			var requestDataCaloriesLastWeek = {
					"measure_type":"calories",
					"start_date": lastWeekDisplay[0],
					"end_date": lastWeekDisplay[1],
					"student_id": student_id
			};
			makePostAjaxRequest("web/StudentActivity/"+token, requestDataCaloriesLastWeek, onSuccessCaloriesLastWeek, onFailureCaloriesLastWeek);
		}
		function onFailureCaloriesReportData(responseData){
			debugLogs("onFailureCaloriesReportData");
		}
		function onSuccessCaloriesLastWeek(responseData){
			if(!isDataNull(responseData.Return.Results)){
				caloriesLastWeek = responseData.Return.Results.calories;
				obj = jsonSort(caloriesLastWeek, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				drawCaloriesBurntChart(calories,caloriesLastWeek,input,lastWeekDisplay);
			}
		}
		function onFailureCaloriesLastWeek(responseData){
			debugLogs("onFailureCaloriesLastWeek");
		}
		/****************End of Calories********************/

		/****************Start of Sleep********************/
		var requestDataSleep = {
				"measure_type":"sleep",
				"start_date": input[0],
				"end_date": input[1],
				"student_id": student_id
		};
		makePostAjaxRequest("web/StudentActivity/"+token, requestDataSleep, onSuccessSleepReportData, onFailureSleepReportData);

		function onSuccessSleepReportData(responseData){
			if(!isDataNull(responseData.Return.Results)){
				sleep = responseData.Return.Results.sleep;
				obj = jsonSort(sleep, "date", DATATYPE.NUMBER, SORTORDER.ASC, null);
				debugLogs("sleep",sleep);
				jsonData["sleep"] = sleep;
			}
			count = weekCount;
			lastWeekDisplay = navigateWeek(--count);
			var requestDataSleepLastWeek = {
					"measure_type":"sleep",
					"start_date": lastWeekDisplay[0],
					"end_date": lastWeekDisplay[1],
					"student_id": student_id
			};
			makePostAjaxRequest("web/StudentActivity/"+token, requestDataSleepLastWeek, onSuccessSleepLastWeek, onFailureSleepLastWeek);
		}

		function onFailureSleepReportData(responseData){
			debugLogs("onFailureSleepReportData");
		}
		function onSuccessSleepLastWeek(responseData){
			if(!isDataNull(responseData.Return.Results)){
				sleepLastWeek = responseData.Return.Results.sleep;
				obj = jsonSort(sleepLastWeek, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				drawSleepChart(sleep,sleepLastWeek,input,lastWeekDisplay);
			}
		}
		function onFailureSleepLastWeek(responseData){
			debugLogs("onFailureSleepLastWeek");
		}
		/****************End of Sleep********************/

		/****************Start of Heart********************/
		var requestDataHeart = {
				"measure_type":"heartrate",
				"start_date": input[0],
				"end_date": input[1],
				"student_id": student_id
		};
		makePostAjaxRequest("web/StudentActivity/"+token, requestDataHeart, onSuccessHeartReportData, onFailureHeartReportData);

		function onSuccessHeartReportData(responseData){
			if(!isDataNull(responseData.Return.Results)){
				heartRate = responseData.Return.Results.heartrate;
				obj = jsonSort(heartRate, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				debugLogs("heartRate",heartRate);
				jsonData["heartRate"] = heartRate;
			}
			count = weekCount;
			lastWeekDisplay = navigateWeek(--count);
			var requestDataHeartlast = {
					"measure_type":"heartrate",
					"start_date": lastWeekDisplay[0],
					"end_date": lastWeekDisplay[1],
					"student_id": student_id
			};
			makePostAjaxRequest("web/StudentActivity/"+token, requestDataHeartlast, onSuccessHeartLastWeek, onFailureHeartLastWeek);
		}

		function onFailureHeartReportData(responseData){
			debugLogs("onFailureHeartReportData");
		}
		function onSuccessHeartLastWeek(responseData){
			if(!isDataNull(responseData.Return.Results)){
				hrmLastWeek = responseData.Return.Results.heartrate;
				obj = jsonSort(hrmLastWeek, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				drawHeartRateChart(heartRate,hrmLastWeek,input,lastWeekDisplay);
			}
		}
		function onFailureHeartLastWeek(responseData){
			debugLogs("onFailureHeartLastWeek");
		}
		/****************End of Heart********************/

		/****************Start of Emotion********************/
		var requestDataEmotion = {
				"source": "web",
				"measure_type":"emotion",
				"start_date": input[0],
				"end_date": input[1],
				"student_id": student_id
		};
		makePostAjaxRequest("web/StudentActivity/"+token, requestDataEmotion, onSuccessEmotionReportData, onFailureEmotionReportData);

		function onSuccessEmotionReportData(responseData){
			if(!isDataNull(responseData.Return.Results)){
				emotion = responseData.Return.Results.emotion;
				obj = jsonSort(emotion, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				jsonData["emotion"] = emotion;
				drawEmotionChart(emotion,input);
			}
		}

		function onFailureEmotionReportData(responseData){
			debugLogs("onFailureEmotionReportData");
		}
		/****************End of Emotion********************/

		/****************Start of Stress********************/
		var requestDataStress = {
				"source": "web",
				"measure_type":"stress",
				"start_date": input[0],
				"end_date": input[1],
				"student_id": student_id
		};
		makePostAjaxRequest("web/StudentActivity/"+token, requestDataStress, onSuccessStressReportData, onFailureStressReportData);

		function onSuccessStressReportData(responseData){
			if(!isDataNull(responseData.Return.Results)){
				stress = responseData.Return.Results.stress;
				obj = jsonSort(stress, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
				jsonData["stress"] = stress;
				drawStressChart(stress,input);
			}
		}

		function onFailureStressReportData(responseData){
			debugLogs("onFailureStressReportData");
		}
		/****************End of Stress********************/

		/****************Start of CompareChart on load********************/
		function compareChart(){
			if(loading){
				k = 1;
			}
			checkActivityToBeCompared(compare[0],compare[1]);
		}
		/****************End of CompareChart on load********************/

		debugLogs(input);
		$('#duration').text(input[2]);
		$( document ).ajaxStop(function() {
			if(compared == false){
				compared= true;
				debugLogs("Triggered ajaxStop handler.");
				compareChart();
				hideSpinnerNow();
			}
		});
	}
	function suggestion(firstData,secondData){
		var firstSuggestionMsg = "",secondSuggestionMsg="";
		firstSuggestionMsg = getMessage(firstData,avgScore[firstData]);
		secondSuggestionMsg = getMessage(secondData,avgScore[secondData]);
		$("#firstSuggestionMsg").text(firstSuggestionMsg);
		$("#secondSuggestionMsg").text(secondSuggestionMsg);
	} 

	function getMessage(type,typeAvg){
		switch(type){
		case "Pfi":{
			if(typeAvg >=100){
				return getValueByLanguageKey('icglabel_pfigthundred');
			}else if(typeAvg >=80 && typeAvg < 100){
				return getValueByLanguageKey('icglabel_pfigteighty');
			}else if(typeAvg >=60 && typeAvg < 80){
				return getValueByLanguageKey('icglabel_pfigtsixty');
			}else{
				return getValueByLanguageKey('icglabel_pfiltsixty');
			}
			break;	
		}
		case "Sleep":{
			if(typeAvg >=9){
				return getValueByLanguageKey('icglabel_sleepgtnine');
			}else{
				return getValueByLanguageKey('icglabel_sleepltnine');
			}
			break;	
		}
		case "Steps":{
			if(typeAvg >=8000){
				return getValueByLanguageKey('icglabel_stepsgteightthousand');
			}else{
				return getValueByLanguageKey('icglabel_stepslteightthousand');
			}
			break;	
		}
		case "Activity":{
			if(typeAvg >=60){
				return getValueByLanguageKey('icglabel_activitygtsixty');
			}else{
				return getValueByLanguageKey('icglabel_activityltsixty');
			}
			break;	
		}
		default:{
			return "";
		}
		}
	}
}

function drawPFIChart(currentData,previousData,currentInputDates,previousInputDates) {
	var sum = 0, best = 0,avg = 0,startLoop =0,previousSum=0,pfiCompare=0;
	var pfiData = new Array();
	if(currentInputDates[3] == 'week'){
		var head = new Array('Day of Week', 'Previous Week', 'Current Week', {
			'type' : 'string',
			'role' : 'style'
		});
		$("#pfi-legend").addClass('pfi-legend-week');
	}else{
		var head = new Array('Day','Current Month', {
			'type' : 'string',
			'role' : 'style'
		});
		$("#pfi-legend").removeClass('pfi-legend-week');
	}
	pfiData.push(head);
	var previousDates = new Array();
	var currentDates = new Array();
	var startCurrentDates = moment(currentInputDates[0]).unix();
	if(currentInputDates[3] == 'week'){
		var endloop = 7;
	}else{
		var endloop = new Date(currentInputDates[1]).getDate();
	}
	for(var i=startLoop;i<endloop;i++){
		currentDates[i] = formatDate(startCurrentDates,"MM/DD");
		if(currentInputDates[3] == 'week'){
			pfiData.push(new Array(currentDates[i],0,0,'point { fill-color: #39B54A; }'));
		}else{
			pfiData.push(new Array(currentDates[i],0,'point { fill-color: #39B54A; }'));
		}
		startCurrentDates += 86400;
	}
	var startPreviousDates = moment(previousInputDates[1]).unix();
	for(var i=endloop-1;i>=startLoop;i--){
		previousDates[i] = formatDate(startPreviousDates,"MM/DD");
		startPreviousDates -= 86400;
	}
	var lastRecordDateForPreviousData = "",lastRecordValueForPreviousData =0;
	for(var i=0; i < previousData.length; i++){
		if(!isDataNull(previousData[i].date) && !isDataNull(previousData[i].value)){
			var value = previousData[i].value;
			var epochdate = previousData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				lastRecordDateForPreviousData = date;
			}
			if(lastRecordDateForPreviousData != date){
				previousSum += lastRecordValueForPreviousData;
			}
			lastRecordDateForPreviousData = date;
			lastRecordValueForPreviousData = value;
			if(currentInputDates[3] == 'week'){
				var index = previousDates.indexOf(date);
				if(index != -1){
					pfiData[++index][1] = value;
				}
			}
		}
	}
	previousSum += lastRecordValueForPreviousData;
	var lastRecordDate = "",lastRecordValue =0;
	var prevDate = "";
	var avgCounter = 0;
	for(var i=0; i < currentData.length; i++){
		if(!isDataNull(currentData[i].date) && !isDataNull(currentData[i].value)){
			var value = currentData[i].value;
			var epochdate = currentData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				best = value;
				lastRecordDate = date;
				// for determining divisor factor for avg calculation
				prevDate = date;
				avgCounter++;
			}else if(best < value){
				best = value;
			}
			if(lastRecordDate != date){
				sum += lastRecordValue;
			}
			lastRecordDate = date;
			var index = currentDates.indexOf(date);
			if(index != -1){
				lastRecordValue = value;
				if(currentInputDates[3] == 'week'){
					pfiData[++index][2] = value;
				}else{
					pfiData[++index][1] = value;
				}
				// for determining divisor factor for avg calculation
				if(date != prevDate) {
					prevDate = date;
					avgCounter++;
				}
			}
		}
	}
	sum += lastRecordValue;
	pfiCompare = sum - previousSum;

	if(pfiCompare < 0){
		$("#pfi_last").text(pfiCompare);
		if(currentInputDates[3] == 'week'){
			$("#pfiCompare").text("Decrease over last week");
		}else{
			$("#pfiCompare").text("Decrease over last month");
		}
	}else{
		$("#pfi_last").text("+"+pfiCompare);
		if(currentInputDates[3] == 'week'){
			$("#pfiCompare").text("Increase over last week");
		}else{
			$("#pfiCompare").text("Increase over last month");
		}
	}
	debugLogs("pfi, avgCounter: " + avgCounter);
	if(avgCounter > 0) {
		avg = sum / avgCounter;
	}
	avgScore["pfi"]= avg;
	$("#pfi_avg").text(roundToTwo(avg));
	$("#pfi_best").text(best);
	debugLogs("chart pfiData length: " + pfiData.length);
	var data = new google.visualization.arrayToDataTable(pfiData);

	var options = {
			tooltip : { isHtml : true },
			hAxis: {title : ''},
			legend : 'none',
			colors : [ '#B3B3B3' ],
			lineWidth : 1,
			pointSize : 6
	};

	var chart = new google.visualization.LineChart(document
			.getElementById('pfi-chart'));

	chart.draw(data, options);
}

function drawStepsChart(currentData,previousData,currentInputDates,previousInputDates){
	var sum = 0, best = 0,avg = 0,startLoop =0,previousSum=0,stepsCompare=0;
	var stepsData = new Array();
	var colorArr = [];
	if(currentInputDates[3] == 'week'){
		var head = new Array('Day of Week', 'Last Week', 'Current Week');
		colorArr = ['#CCCCCC', '#FFAA00'];
		$("#steps-legend").addClass('steps-legend-week');
	}
	else{
		var head = new Array('Day of Week', 'Current Month');
		colorArr = ['#FFAA00'];
		$("#steps-legend").removeClass('steps-legend-week');
	}
	stepsData.push(head);
	var previousDates = new Array();
	var currentDates = new Array();
	var startCurrentDates = moment(currentInputDates[0]).unix();
	if(currentInputDates[3] == 'week'){
		var endloop = 7;
	}else{
		var endloop = new Date(currentInputDates[1]).getDate();
	}
	
	for(var i=startLoop;i<endloop;i++){
		currentDates[i] = formatDate(startCurrentDates,"MM/DD");
		if(currentInputDates[3] == 'week'){
			stepsData.push(new Array(currentDates[i],0,0));
		}else{
			stepsData.push(new Array(currentDates[i],0));
		}
		startCurrentDates += 86400;
	}
	var startPreviousDates = moment(previousInputDates[1]).unix();
	for(var i=endloop-1;i>=startLoop;i--){
		previousDates[i] = formatDate(startPreviousDates,"MM/DD");
		startPreviousDates -= 86400;
	}

	var previousDataDayValue = 0;
	var previousDataPrevDate = "";
	for(var i=0; i < previousData.length; i++){
		if(!isDataNull(previousData[i].date) && !isDataNull(previousData[i].value)){
			var value = previousData[i].value;
			var epochdate = previousData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				previousDataPrevDate = date;
			}

			if(date == previousDataPrevDate) {
				previousDataDayValue += value;
			} else {
				previousDataDayValue = value;
			}
			
			if(currentInputDates[3] == 'week'){
				var index = previousDates.indexOf(date);
				if(index != -1){
					stepsData[++index][1] = previousDataDayValue;
					
				}
			}
			if(date != previousDataPrevDate) {
				previousDataPrevDate = date;
			}
			// update previousSum
			previousSum += value;
		}
	}

	var prevDate = "";
	var avgCounter = 0;
	var currentDataDayValue = 0;
	for(var i=0; i < currentData.length; i++){
		if(!isDataNull(currentData[i].date) && !isDataNull(currentData[i].value)){
			var value = currentData[i].value;
			var epochdate = currentData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				// for determining divisor factor for avg calculation
				prevDate = date;
				avgCounter++;
			}
			
			if(date == prevDate) {
				currentDataDayValue += value;
			} else {
				currentDataDayValue = value;
			}

			var index = currentDates.indexOf(date);
			if(index != -1){
				if(currentInputDates[3] == 'week'){
					stepsData[++index][2] = currentDataDayValue;
				}else{
					stepsData[++index][1] = currentDataDayValue;
				}
			}
			// for determining divisor factor for avg calculation
			if(date != prevDate) {
				prevDate = date;
				avgCounter++;
			}
			// calculating best value
			if(i == 0) {
				best = currentDataDayValue;
			}
			
			if(best < currentDataDayValue) {
				best = currentDataDayValue;
			}
			// incrementing sum
			sum += value;
		}
	}

	stepsCompare = sum - previousSum;

	if(stepsCompare < 0){
		$("#steps_last").text(stepsCompare);
		if(currentInputDates[3] == 'week'){
			$("#stepsCompare").text("Decrease over last week");
		}else{
			$("#stepsCompare").text("Decrease over last month");
		}
	}else{
		$("#steps_last").text("+"+stepsCompare);
		if(currentInputDates[3] == 'week'){
			$("#stepsCompare").text("Increase over last week");
		}else{
			$("#stepsCompare").text("Increase over last month");
		}
	}

	if(avgCounter > 0) {
		avg = sum / avgCounter;
	}
	avgScore["steps"]= avg;
	$("#steps_avg").text(roundToTwo(avg));
	$("#steps_best").text(best);

	var data = new google.visualization.arrayToDataTable(stepsData);

	var options = {
			tooltip : {
				isHtml : true
			},
			bars : 'vertical',
			vAxis : {
				format : 'decimal'
			},
			hAxis:{title:''},
			height : 240,
			colors : colorArr,
			legend : {
				position : 'none'
			}
	};

	var chart = new google.charts.Bar(document
			.getElementById('steps-chart'));
	chart.draw(data, google.charts.Bar.convertOptions(options));
}

function drawActivitiesChart(currentData,previousData,currentInputDates,previousInputDates) {
	var sum = 0,daySum=0, best = 0,avg = 0, walk = 2, run = 3, cycle =4,temp_walk = 0,temp_cycle =0, temp_run =0,previousSum=0,activityCompare=0; 
	var walkSet = false,runSet = false, cycleSet= false,actualPreviousSum=0,startLoop=0;
	var activityData = new Array();
	var head = new Array('Day of Week', 'Walk', 'Run', 'Cycle');
	activityData.push(head);
	var previousDates = new Array();
	var currentDates = new Array();
	var startCurrentDates = moment(currentInputDates[0]).unix();
	debugLogs("currentInputDates[3] => " + currentInputDates[3]);
	if(currentInputDates[3] == 'week'){
		var endloop = 7;
	}else{
		var endloop = new Date(currentInputDates[1]).getDate();
	}
	for(var i=startLoop;i<endloop;i++){
		currentDates[i] = formatDate(startCurrentDates,"MM/DD");
		activityData.push(new Array(currentDates[i],0,0,0));
		startCurrentDates += 86400;
	}
	var startPreviousDates = moment(previousInputDates[1]).unix();
	for(var i=endloop-1;i>=startLoop;i--){
		previousDates[i] = formatDate(startPreviousDates,"MM/DD");
		startPreviousDates -= 86400;
	}
	var previousdate = "";
	var avgCounter = 0;
	for(var i=0; i < currentData.length; i++){
		if(!isDataNull(currentData[i].date) && !isDataNull(currentData[i].duration) && !isDataNull(currentData[i].situation)){
			var duration = currentData[i].duration / 3600; // duration in Hrs
			var situation = currentData[i].situation;
			var epochdate = currentData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				previousdate = date;
				best = daySum;
			}
			if(previousdate != date){
				var index = currentDates.indexOf(previousdate);
				if(index != -1){
					activityData[++index][1] = temp_walk;
					activityData[index][2] = temp_run;
					activityData[index][3] = temp_cycle;
				}
				temp_walk = 0,temp_cycle =0, temp_run =0;
				walkSet = false,runSet = false, cycleSet= false;
				sum += daySum;
				if(best < daySum)
					best = daySum;
				daySum = 0;
			}
			previousdate = date;
			if(situation == walk && !walkSet){
				temp_walk = duration;
				walkSet = true;
				daySum += duration;
			}
			if(situation == run && !runSet){
				temp_run = duration;
				runSet = true;
				daySum += duration;
			}
			if(situation == cycle && !cycleSet){
				temp_cycle = duration;
				cycleSet = true;
				daySum += duration;
			}
			if (situation == walk || situation == run 
					|| situation == cycle) {
				avgCounter++;
			}
		}
	}
	sum += daySum;
	debugLogs("sum: " + sum);
	debugLogs("daySum: " + daySum);
	if(avgCounter > 0) {
		debugLogs("avgCounter: " + avgCounter);
		// divide avgCounter by 3 as three activities
		// (walk, run, cycle) per day are summed together
		avgCounter = avgCounter / 3;
		avg = sum / avgCounter;
	}
	avgScore["activity"]= avg;
	$("#activity_avg").text(roundToTwo(avg) + " Hrs");
	$("#activity_best").text(roundToTwo(best) + " Hrs");
	var index = currentDates.indexOf(previousdate);
	if(index != -1){
		activityData[++index][1] = temp_walk;
		activityData[index][2] = temp_run;
		activityData[index][3] = temp_cycle;
	}
	temp_walk = 0,temp_cycle =0, temp_run =0;
	walkSet = false,runSet = false, cycleSet= false,previousdate = "";
	for(var i=0; i < previousData.length; i++){
		if(!isDataNull(previousData[i].date) && !isDataNull(previousData[i].duration) && !isDataNull(previousData[i].situation)){
			var duration = previousData[i].duration / 3600; // duration in Hrs
			var situation = previousData[i].situation;
			var epochdate = previousData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				previousdate = date;
			}
			if(previousdate != date){
				var index = currentDates.indexOf(previousdate);
				if(index != -1){
					actualPreviousSum += previousSum;
				}
				temp_walk = 0,temp_cycle =0, temp_run =0;
				walkSet = false,runSet = false, cycleSet= false;
			}
			previousdate = date;
			if(situation == walk && !walkSet){
				temp_walk = duration;
				walkSet = true;
				previousSum += duration;
			}
			if(situation == run && !runSet){
				temp_run = duration;
				runSet = true;
				previousSum += duration;
			}
			if(situation == cycle && !cycleSet){
				temp_cycle = duration;
				cycleSet = true;
				previousSum += duration;
			}
		}
	}
	activityCompare = sum - actualPreviousSum;

	if(activityCompare < 0){
		$("#activity_last").text(roundToTwo(activityCompare) + " Hrs");
		if(currentInputDates[3] == 'week'){
			$("#activityCompare").text("Decrease over last week");
		}else{
			$("#activityCompare").text("Decrease over last month");
		}
	}else{
		$("#activity_last").text("+" + roundToTwo(activityCompare) + " Hrs");
		if(currentInputDates[3] == 'week'){
			$("#activityCompare").text("Increase over last week");
		}else{
			$("#activityCompare").text("Increase over last month");
		}
	}
	debugLogs("chart activityData length: " + activityData.length)
	debugLogs("activityData: " + activityData);
	var data = new google.visualization.arrayToDataTable(activityData);

	var options_fullStacked = {
			tooltip : {
				isHtml : true
			},
			isStacked : true,
			height : 240,
			colors : [ '#9BDAF1', '#F9DA95', '#D1F498' ],
			legend : {
				position : 'none',
				maxLines : 3
			},
			bar : {
				groupWidth : '25%'
			},
			hAxis: {title : ''}
	};

	var chart = new google.visualization.ColumnChart(document
			.getElementById('activities-chart'));
	chart.draw(data, options_fullStacked);
}

function drawCaloriesBurntChart(currentData,previousData,currentInputDates,previousInputDates) {
	var sum = 0, best = 0,avg = 0,previousSum =0,caloriesCompare = 0,startLoop =0;
	var caloriesData = new Array();
	var head = new Array('Day of Week', 'Calories Burnt');
	caloriesData.push(head);
	var previousDates = new Array();
	var currentDates = new Array();
	var startCurrentDates = moment(currentInputDates[0]).unix();
	if(currentInputDates[3] == 'week'){
		var endloop = 7;
	}else{
		var endloop = new Date(currentInputDates[1]).getDate();
	}
	for(var i=startLoop;i<endloop;i++){
		currentDates[i] = formatDate(startCurrentDates,"MM/DD");
		caloriesData.push(new Array(currentDates[i],0));
		startCurrentDates += 86400;
	}
	var startPreviousDates = moment(previousInputDates[1]).unix();
	for(var i=endloop-1;i>=startLoop;i--){
		previousDates[i] = formatDate(startPreviousDates,"MM/DD");
		startPreviousDates -= 86400;
	}

	var prevDate = "";
	var avgCounter = 0;
	var currentDataDayValue = 0;
	for(var i=0; i < currentData.length; i++){
		if(!isDataNull(currentData[i].date) && !isDataNull(currentData[i].value)){
			var value = currentData[i].value;
			var epochdate = currentData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				// for determining divisor factor for avg calculation
				prevDate = date;
				avgCounter++;
			}
			
			if(date == prevDate) {
				currentDataDayValue += value;
			} else {
				currentDataDayValue = value;
			}
			
			var index = currentDates.indexOf(date);
			if(index != -1){
				caloriesData[++index][1] = currentDataDayValue;
			}
			// for determining divisor factor for avg calculation
			if(date != prevDate) {
				prevDate = date;
				avgCounter++;
			}
			// calculating best value
			if(i == 0) {
				best = currentDataDayValue;
			}
			
			if(best < currentDataDayValue) {
				best = currentDataDayValue;
			}
			// incrementing sum
			sum += value;
		}
	}

	var previousDataDayValue = 0;
	var previousDataPrevDate = "";
	for(var i=0; i < previousData.length; i++){
		if(!isDataNull(previousData[i].date) && !isDataNull(previousData[i].value)){
			var value = previousData[i].value;
			var epochdate = previousData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				previousDataPrevDate = date;
			}
			if(date == previousDataPrevDate) {
				previousDataDayValue += value;
			} else {
				previousDataDayValue = value;
			}

			if(date != previousDataPrevDate) {
				previousDataPrevDate = date;
			}
			// update previousSum
			previousSum += value;
		}
	}

	caloriesCompare = sum - previousSum;

	if(caloriesCompare < 0){
		$("#calories_last").text(caloriesCompare);
		if(currentInputDates[3] == 'week'){
			$("#caloriesCompare").text("Decrease over last week");
		}else{
			$("#caloriesCompare").text("Decrease over last month");
		}
	}else{
		$("#calories_last").text("+"+caloriesCompare);
		if(currentInputDates[3] == 'week'){
			$("#caloriesCompare").text("Increase over last week");
		}else{
			$("#caloriesCompare").text("Increase over last month");
		}
	}
	debugLogs("calories, avgCounter: " + avgCounter);
	if(avgCounter > 0) {
		avg = sum / avgCounter;
	}
	avgScore["calories"]= avg;
	$("#calories_avg").text(roundToTwo(avg));
	$("#calories_best").text(best);
	debugLogs("chart caloriesData length: " + caloriesData.length)
	var data = new google.visualization.arrayToDataTable(caloriesData);
	var options = {
			tooltip: {isHtml: true},
			hAxis: {title : ''},
			vAxis: {minValue: 0},
			colors: ['#F15A24'],
			legend: { position: 'none'}
	};

	var chart = new google.visualization.AreaChart(document
			.getElementById('calories-burnt-chart'));
	chart.draw(data, options);
}

function drawSleepChart(currentData,previousData,currentInputDates,previousInputDates) {
	var sum = 0, best = 0,avg = 0,sleepCompare=0,previousSum=0,startLoop=0;
	var sleepData = new Array();
	var head = new Array('Day of Week', 'Sleep Hours', 'Standard' );
	sleepData.push(head);
	var previousDates = new Array();
	var currentDates = new Array();
	var startCurrentDates = moment(currentInputDates[0]).unix();

	if(currentInputDates[3] == 'week'){
		var endloop = 7;
	}else{
		var endloop = new Date(currentInputDates[1]).getDate();
	}
	for(var i=startLoop;i<endloop;i++){
		currentDates[i] = formatDate(startCurrentDates,"MM/DD");
		sleepData.push(new Array(currentDates[i],0, 9));
		startCurrentDates += 86400;
	}
	var startPreviousDates = moment(previousInputDates[1]).unix();
	for(var i=endloop-1;i>=startLoop;i--){
		previousDates[i] = formatDate(startPreviousDates,"MM/DD");
		startPreviousDates -= 86400;
	}
	var lastRecordDate = "",lastRecordValue =0;
	var prevDate = "";
	var avgCounter = 0;
	for(var i=0; i < currentData.length; i++){
		if(!isDataNull(currentData[i].date) && !isDataNull(currentData[i].value)){
			var value = currentData[i].value / (3600);
			var epochdate = currentData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				best = value;
				lastRecordDate = date;
				// for determining divisor factor for avg calculation
				prevDate = date;
				avgCounter++;
			} else if(best < value){
				best = value;
			}
			if(lastRecordDate != date){
				sum += lastRecordValue;
			}
			lastRecordDate = date;
			var index = currentDates.indexOf(date);
			if(index != -1){
				lastRecordValue = value;
				sleepData[++index][1] = value;
				// for determining divisor factor for avg calculation
				if(date != prevDate) {
					prevDate = date;
					avgCounter++;
				}
			}			
		}
	}
	sum += lastRecordValue;
	var lastRecordDateForPreviousData = "",lastRecordValueForPreviousData =0;
	for(var i=0; i < previousData.length; i++){
		if(!isDataNull(previousData[i].date) && !isDataNull(previousData[i].value)){
			var value = previousData[i].value / (3600);
			var epochdate = previousData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				lastRecordDateForPreviousData = date;
			}
			if(lastRecordDateForPreviousData != date){
				previousSum += lastRecordValueForPreviousData;
			}
			lastRecordDateForPreviousData = date;
			var index = previousDates.indexOf(date);
			if(index != -1){
				lastRecordValueForPreviousData = value;
			}
		}
	}
	previousSum += lastRecordValueForPreviousData;
	sleepCompare = sum - previousSum;

	if(sleepCompare < 0){
		$("#sleep_last").text(roundToTwo(sleepCompare) + " Hrs");
		if(currentInputDates[3] == 'week'){
			$("#sleepCompare").text("Decrease over last week");
		}else{
			$("#sleepCompare").text("Decrease over last month");
		}
	}else{
		$("#sleep_last").text("+" + roundToTwo(sleepCompare) + " Hrs");
		if(currentInputDates[3] == 'week'){
			$("#sleepCompare").text("Increase over last week");
		}else{
			$("#sleepCompare").text("Increase over last month");
		}
	}
	debugLogs("sleep, avgCounter: " + avgCounter);
	if(avgCounter > 0) {
		avg = sum / avgCounter;
	}
 	avgScore["sleep"]= avg;
	$("#sleep_avg").text(roundToTwo(avg) + " Hrs");
	$("#sleep_best").text(roundToTwo(best) + " Hrs");
	debugLogs("chart sleepData length: " + sleepData.length)
	var data = new google.visualization.arrayToDataTable(sleepData);

	var options = {
			tooltip : { isHtml : true },
			bar: {groupWidth: "35%"},
			height : 240,
			colors : [ '#4C4B56' ],
			legend : {
				position : 'none'
			},
			hAxis:{title:''},
			seriesType: 'bars',
			series: {1: {type: "line",
				lineStyle: "dotted",
				lineOpacity: 1,
				color: "green",
				lineWidth: 1}}
	};

	var chart = new google.visualization.ComboChart(document
			.getElementById('sleep-chart'));
	chart.draw(data, options);
}

function drawHeartRateChart(currentData,previousData,currentInputDates,previousInputDates) {
	var sum = 0, best = 0,avg = 0,previousSum=0,hrmCompare=0,startLoop =0;
	var heartData = new Array();
	var head = new Array('Day of Week', 'Heart Rate');
	heartData.push(head);
	var previousDates = new Array();
	var currentDates = new Array();
	var startCurrentDates = moment(currentInputDates[0]).unix();
	if(currentInputDates[3] == 'week'){
		var endloop = 7;
	}else{
		var endloop = new Date(currentInputDates[1]).getDate();
	}
	for(var i=startLoop;i<endloop;i++){
		currentDates[i] = formatDate(startCurrentDates,"MM/DD");
		heartData.push(new Array(currentDates[i],0));
		startCurrentDates += 86400;
	}
	debugLogs("currentDates: " + currentDates);
	var startPreviousDates = moment(previousInputDates[1]).unix();
	for(var i=endloop-1;i>=startLoop;i--){
		previousDates[i] = formatDate(startPreviousDates,"MM/DD");
		startPreviousDates -= 86400;
	}
	debugLogs("previousDates: " + previousDates);
	var lastRecordDate = "",lastRecordValue =0;
	var prevDate = "";
	var avgCounter = 0;
	for(var i=0; i < currentData.length; i++){
		if(!isDataNull(currentData[i].date) && !isDataNull(currentData[i].value)){
			var value = currentData[i].value;
			var epochdate = currentData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			debugLogs("hrm, date: " + date + ", value: " + value);
			if(i == 0){
				best = value;
				lastRecordDate = date;
				// for determining divisor factor for avg calculation
				prevDate = date;
				avgCounter++;
			}else if(best < value){
				best = value;
			}
			if(lastRecordDate != date){
				sum += lastRecordValue;
			}
			lastRecordDate = date;
			var index = currentDates.indexOf(date);
			if(index != -1){
				lastRecordValue = value;
				heartData[++index][1] = value;
				// for determining divisor factor for avg calculation
				if(date != prevDate) {
					prevDate = date;
					avgCounter++;
				}
			}
		}
	}
	sum += lastRecordValue;
	var lastRecordDateForPreviousData = "",lastRecordValueForPreviousData =0;
	for(var i=0; i < previousData.length; i++){
		if(!isDataNull(previousData[i].date) && !isDataNull(previousData[i].value)){
			var value = previousData[i].value;
			var epochdate = previousData[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				lastRecordDateForPreviousData = date;
			}
			if(lastRecordDateForPreviousData != date){
				previousSum += lastRecordValueForPreviousData;
			}
			lastRecordDateForPreviousData = date;
			var index = previousDates.indexOf(date);
			if(index != -1){
				lastRecordValueForPreviousData = value;
			}
		}
	}
	previousSum += lastRecordValueForPreviousData;
	hrmCompare = sum - previousSum;

	if(hrmCompare < 0){
		$("#heart_last").text(hrmCompare  + " bpm");
		if(currentInputDates[3] == 'week'){
			$("#hrmCompare").text("Decrease over last week");
		}else{
			$("#hrmCompare").text("Decrease over last month");
		}
	}else{
		$("#heart_last").text("+" + hrmCompare + " bpm");
		if(currentInputDates[3] == 'week'){
			$("#hrmCompare").text("Increase over last week");
		}else{
			$("#hrmCompare").text("Increase over last month");
		}
	}
	debugLogs("hrm, avgCounter: " + avgCounter);
	if(avgCounter > 0) {
		avg = sum / avgCounter;
	}
	avgScore["heartRate"]= avg;
	$("#heart_avg").text(roundToTwo(avg) + " bpm");
	$("#heart_best").text(best + " bpm");
	debugLogs("chart heartData length: " + heartData.length)
	var data = new google.visualization.arrayToDataTable(heartData);

	var options = {
			tooltip : { isHtml : true },		
			hAxis: {title : ''},
			legend : 'none',
			colors : [ '#E31C3D' ],
			lineWidth : 1,
			pointSize: 10
	};

	var chart = new google.visualization.LineChart(document
			.getElementById('heart-rate-chart'));
	chart.draw(data, options);
}

function roundToTwo(num) {    
	return +(Math.round(num + "e+2")  + "e-2");
}

function drawEmotionChart(data,currentInputDates) {
	var happy_range = 1, sad_range = 2, fear_range = 4, angry_range = 3;
	var sum = 0, happySum = 0, fearSum =0, angrySum = 0, sadSum =0, best = 0,temp_happy = 0,temp_angry =0, temp_fear =0,temp_sad =0; 
	var happySet = false,sadSet = false, fearSet= false,angrySet= false,startLoop=0;
	var emotionData = new Array();
	var head = new Array('Day of Week', 'Fear', { role: 'annotation' }, 'Angry', { role: 'annotation' }, 'Sad', { role: 'annotation' }, 'Happy', {
		role : 'annotation'
	});
	emotionData.push(head);
	var noOfDates = new Array();
	var startDates = moment(currentInputDates[0]).unix();
	if(currentInputDates[3] == 'week'){
		var endloop = 7;
	}else{
		var endloop = new Date(currentInputDates[1]).getDate();
	}
	for(var i=startLoop;i<endloop;i++){
		noOfDates[i] = formatDate(startDates,"MM/DD");
		emotionData.push(new Array(noOfDates[i],0,0,0,0,0,0,0,0));
		startDates += 86400;
	}
	var previousdate = "";
	for(var i=0; i < data.length; i++){
		if(!isDataNull(data[i].date) && !isDataNull(data[i].duration) && !isDataNull(data[i].situation)){
			var duration = data[i].duration;
			var situation = data[i].situation;
			var epochdate = data[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0){
				previousdate = date;
			}

			if(previousdate != date){
				var index = noOfDates.indexOf(previousdate);
				if(index != -1){
					emotionData[++index][1] = temp_fear;
					emotionData[index][2] = temp_fear;
					emotionData[index][3] = temp_angry;
					emotionData[index][4] = temp_angry;
					emotionData[index][5] = temp_sad;
					emotionData[index][6] = temp_sad;
					emotionData[index][7] = temp_happy;
					emotionData[index][8] = temp_happy;
				}
				temp_happy = 0,temp_angry =0, temp_fear =0,temp_sad =0;
				happySet = false,sadSet = false, fearSet= false,angrySet= false;
			}
			previousdate = date;
			if(situation == happy_range && !happySet){
				temp_happy = duration;
				happySet = true;
				happySum += duration;
			}
			if(situation == sad_range && !sadSet){
				temp_sad = duration;
				sadSet = true;
				sadSum += duration;
			}
			if(situation == angry_range && !angrySet){
				temp_angry = duration;
				angrySet = true;
				angrySum += duration;
			}
			if(situation == fear_range && !fearSet){
				temp_fear = duration;
				fearSet = true;
				fearSum += duration;
			}
		}
	}
	var index = noOfDates.indexOf(previousdate);
	if(index != -1){
		emotionData[++index][1] = temp_fear;
		emotionData[index][2] = temp_fear;
		emotionData[index][3] = temp_angry;
		emotionData[index][4] = temp_angry;
		emotionData[index][5] = temp_sad;
		emotionData[index][6] = temp_sad;
		emotionData[index][7] = temp_happy;
		emotionData[index][8] = temp_happy;
	}
	sum = happySum + sadSum + angrySum + fearSum;
	if(happySum > 0){
		$("#happy_per").text(roundToTwo(happySum*100/sum)+"%");
	}else{
		$("#happy_per").text("0%");
	}
	if(sadSum > 0){
		$("#sad_per").text(roundToTwo(sadSum*100/sum)+"%");
	}else{
		$("#sad_per").text("0%");
	}
	if(angrySum > 0){
		$("#angry_per").text(roundToTwo(angrySum*100/sum)+"%");
	}else{
		$("#angry_per").text("0%");
	}
	if(fearSum > 0){
		$("#fear_per").text(roundToTwo(fearSum*100/sum)+"%");
	}else{
		$("#fear_per").text("0%");
	}
	var data = new google.visualization.arrayToDataTable(emotionData);

	var options_fullStacked = {
			tooltip : { isHtml : true },
			hAxis: {title : ''},
			isStacked : true,
			height : 240,
			colors : [ '#2980B9', '#E74C3C', '#F39C12', '#27AE60' ],
			legend : { position : 'none' },
			bar : {
				groupWidth : '25%'
			}
	};

	var chart = new google.visualization.ColumnChart(document
			.getElementById('emotion-chart'));
	chart.draw(data, options_fullStacked);
}

function drawStressChart(data,currentInputDates) {
	var normal_range = 1, abnormal_range = 2;
	var sum = 0,normalSum = 0,stressSum =0, best = 0,avg = 0, temp_normal = 0,temp_abnormal =0; 
	var normalSet = false,abnormalSet = false,stressedDates ="",startLoop=0;
	var stressData = new Array();
	var head = new Array('Day of Week', 'Under Stress', { role: 'annotation' }, 'Normal Stress', {
		role : 'annotation'
	});
	stressData.push(head);
	var noOfDates = new Array();
	var startDates = moment(currentInputDates[0]).unix();
	if(currentInputDates[3] == 'week'){
		var endloop = 7;
	}else{
		var endloop = new Date(currentInputDates[1]).getDate();
	}
	for(var i=startLoop;i<endloop;i++){
		noOfDates[i] = formatDate(startDates,"MM/DD");
		stressData.push(new Array(noOfDates[i],0,0,0,0));
		startDates += 86400;
	}
	var previousdate = "";
	for(var i=0; i < data.length; i++){
		if(!isDataNull(data[i].date) && !isDataNull(data[i].duration) && !isDataNull(data[i].situation)){
			var duration = data[i].duration;
			var situation = data[i].situation;
			var epochdate = data[i].date;
			var date = formatDate(epochdate,"MM/DD");
			if(i == 0)
				previousdate = date;
			if(previousdate != date){
				var index = noOfDates.indexOf(previousdate);
				if(index != -1){
					stressData[++index][1] = temp_abnormal;
					stressData[index][2] = temp_abnormal;
					stressData[index][3] = temp_normal;
					stressData[index][4] = temp_normal;
				}
				if(temp_normal > temp_abnormal){
					stressedDates += " "+previousdate;
				}
				temp_abnormal = 0,temp_normal =0;
				normalSet = false,abnormalSet = false;
			}
			previousdate = date;
			if(situation == normal_range && !normalSet){
				temp_normal = duration;
				normalSet = true;
				normalSum += duration;
			}
			if(situation == abnormal_range && !abnormalSet){
				temp_abnormal = duration;
				abnormalSet = true;
				stressSum += duration;
			}
		}
	}
	var index = noOfDates.indexOf(previousdate);
	if(index != -1){
		stressData[++index][1] = temp_abnormal;
		stressData[index][2] = temp_abnormal;
		stressData[index][3] = temp_normal;
		stressData[index][4] = temp_normal;
	}
	$("#stressedDates").text(stressedDates);
	sum = normalSum + stressSum;
	if(stressSum > 0){
		$("#stressfull").text("Stressfull(Over "+roundToTwo(stressSum*100/sum)+"%"+")");
	}else{
		$("#stressfull").text("Stressfull(Over 0%)");
	}

	if(normalSum < stressSum){
		$("#stressStatus").text("Under Stress");
		$("#stressStatusImg").addClass('img-activity-stress');
	}else{
		$("#stressStatus").text("Normal Stress");
		$("#stressStatusImg").addClass('img-activity-normal');

	}
	var data = new google.visualization.arrayToDataTable(stressData);
	var options_fullStacked = {
			tooltip : { isHtml : true },			
			hAxis: {title : ''},
			isStacked : true,
			height : 240,
			colors : [ '#E74C3C', '#27AE60' ],
			legend : { position : 'none' },
			bar : {
				groupWidth : '25%'
			}
	};

	var chart = new google.visualization.ColumnChart(document
			.getElementById('stress-chart'));
	chart.draw(data, options_fullStacked);
}

function drawCompareChart(firstData,secondData,inputDates,firstActivity,secondActivity){
	var sum = 0, best = 0,avg = 0,startLoop =0;
	var compareData = new Array();
	if(inputDates[3] == 'week'){
		var head = new Array('Day of Week', firstActivity, secondActivity);
		compareData.push(head);
	}else{
		var head = new Array('Day of Month', firstActivity, secondActivity);
		compareData.push(head);
	}
	var noOfDates = new Array();
	var startDatesForFirstData = moment(inputDates[0]).unix();
	if(inputDates[3] == 'week'){
		var endloop = 7;
	}else{
		var endloop = new Date(inputDates[1]).getDate();
	}
	for(var i=startLoop;i<endloop;i++){
		noOfDates[i] = formatDate(startDatesForFirstData,"MM/DD");
		compareData.push(new Array(noOfDates[i],0,0));
		startDatesForFirstData += 86400;
	}
	if(firstActivity == 'activity'){
		var  walk = 2, run = 3, cycle =4;
		var previousdate ="";
		var value = 0,walkSet = false,runSet = false, cycleSet= false;
		if(!isDataNull(firstData) && firstData.length > 0){
			for(var i=0; i < firstData.length; i++){
				if(!isDataNull(firstData[i].date) && !isDataNull(firstData[i].duration) && !isDataNull(firstData[i].situation)){
					var duration = firstData[i].duration / 3600; // duration in hours
					var situation = firstData[i].situation;
					var epochdate = firstData[i].date;
					var date = formatDate(epochdate,"MM/DD");
					if(i == 0){
						var previousdate = date;
					}
					if(previousdate != date){
						var index = noOfDates.indexOf(date);
						if(index != -1){
							compareData[++index][1] = value;
						}
						value = 0;
						walkSet = false,runSet = false, cycleSet= false;
					}
					previousdate = date;
					if(situation == walk && !walkSet){
						walkSet = true;
						value += duration;
					}
					if(situation == run && !runSet){
						runSet = true;
						value += duration;
					}
					if(situation == cycle && !cycleSet){
						cycleSet = true;
						value += duration;
					}
				}
			}
			var index = noOfDates.indexOf(date);
			if(index != -1){
				compareData[++index][1] = value;
			}
		}
	}else{
		//alert(firstData.length);
		if(!isDataNull(firstData) && firstData.length > 0){
			for(var i=0; i < firstData.length; i++){
				if(!isDataNull(firstData[i].date) && !isDataNull(firstData[i].value)){
					var value;
					switch (firstActivity) {
					case "sleep":
						value = firstData[i].value / 3600;
						break;
					default:
						value = firstData[i].value;
					break;
					}
					var epochdate = firstData[i].date;
					var date = formatDate(epochdate,"MM/DD");
					var index = noOfDates.indexOf(date);
					if(index != -1){
						compareData[++index][1] = value;
					}
				}
			}
		}
	}
	if(secondActivity == 'activity'){
		var  walk = 2, run = 3, cycle =4;
		var previousdate ="";
		var value = 0,walkSet = false,runSet = false, cycleSet= false;
		if(!isDataNull(secondData) && secondData.length > 0){
			for(var i=0; i < secondData.length; i++){
				if(!isDataNull(secondData[i].date) && !isDataNull(secondData[i].duration) && !isDataNull(secondData[i].situation)){
					var duration = secondData[i].duration / 3600; // duration in hours
					var situation = secondData[i].situation;
					var epochdate = secondData[i].date;
					var date = formatDate(epochdate,"MM/DD");
					if(i == 0){
						previousdate = date;
					}
					if(previousdate != date){
						var index = noOfDates.indexOf(previousdate);
						if(index != -1){
							compareData[++index][2] = value;
						}
						value = 0;
						walkSet = false,runSet = false, cycleSet= false;
					}
					previousdate = date;
					if(situation == walk && !walkSet){
						walkSet = true;
						value += duration;
					}
					if(situation == run && !runSet){
						runSet = true;
						value += duration;
					}
					if(situation == cycle && !cycleSet){
						cycleSet = true;
						value += duration;
					}
				}
			}
			var index = noOfDates.indexOf(previousdate);
			if(index != -1){
				compareData[++index][2] = value;
			}
		}
	}else{
		if(!isDataNull(secondData) && secondData.length > 0){
			for(var i=0; i < secondData.length; i++){
				if(!isDataNull(secondData[i].date) && !isDataNull(secondData[i].value)){
					var value;
					switch (secondActivity) {
					case "sleep":
						value = secondData[i].value / 3600;
						break;
					default:
						value = secondData[i].value;
					break;
					}
					var epochdate = secondData[i].date;
					var date = formatDate(epochdate,"MM/DD");
					var index = noOfDates.indexOf(date);
					if(index != -1){
						compareData[++index][2] = value;
					}
				}
			}
		}
	}

	// code to format pfi to "PFI"
	switch (firstActivity) {
	case "pfi": {
		$("#first_compare_score").text(firstActivity.toUpperCase())+ " Avg.";
		$("#fist_compare").text(firstActivity.toUpperCase());
	}
	break;
	default: {
		$("#first_compare_score").text(firstActivity.charAt(0).toUpperCase() + firstActivity.slice(1)+ " Avg.");
		$("#fist_compare").text(firstActivity.charAt(0).toUpperCase() + firstActivity.slice(1));
	}
	break;
	}

	// code to format pfi to "PFI"
	switch (secondActivity) {
	case "pfi": {
		$("#second_compare_score").text(secondActivity.toUpperCase()) + " Avg.";
		$("#second_compare").text(secondActivity.toUpperCase());
	}
	break;
	default: {
		$("#second_compare_score").text(secondActivity.charAt(0).toUpperCase() + secondActivity.slice(1)+ " Avg.");
		$("#second_compare").text(secondActivity.charAt(0).toUpperCase() + secondActivity.slice(1));
	}
	break;
	}

	debugLogs("compareData" + compareData);
	var data = new google.visualization.arrayToDataTable(compareData);

	var options = {
			tooltip : {
				isHtml : true
			},
			hAxis:{title:''},
			bars : 'vertical',
			vAxis : {
				format : 'decimal'
			},
			height : 240,
			colors : [ '#8CC63F', '#4C4B56' ],
			legend : {
				position : 'none'
			}
	};

	var chart = new google.charts.Bar(document
			.getElementById('compare-chart'));
	chart.draw(data, google.charts.Bar.convertOptions(options));
}