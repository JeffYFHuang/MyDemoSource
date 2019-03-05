$(document).ready(function() {
	$('.dashboard').removeClass("treeview").addClass("active");
	$('.dashboard').removeClass("font-medium-roboto").addClass("font-bold-roboto");
	$("#sDashboardIcon").attr("src","resources/images/sidemenu_icon/white/dashboard.png");
	LoadGoogle();
	$('.today').text(" "+getDate());
	$('.yesterday').text(getDate(1));

	if(sessionStorage.getItem("sid") === null || sessionStorage.getItem("sid") === '' || typeof sessionStorage.getItem("sid") === 'undefined'){
		$(".student_details:first").click();

	}else{

		$('ul.navbar-nav li').find('a').each(function() {
			var currentSid = $(this).attr('data-student');
			if(sessionStorage.getItem("sid") === currentSid){
				$(this).click();
			}
		});
	}
});

$('.student_details').on("click",function(){
	$(this).parent().parent().find('.active').removeClass("active");
	$(this).addClass("active");
	var student_id = $(this).attr('data-student');
	var device_uuid = $(this).attr('data-studentDeviceUUID');
	var nick_name=$(this).attr('data-nickName');

	var token_id = $("#token_id").val();

	sessionStorage.setItem("student_id", student_id);
	sessionStorage.setItem("sid", student_id);
	sessionStorage.setItem("device_uuid", device_uuid);
	sessionStorage.setItem("nick_name", nick_name);
	sessionStorage.setItem("token_id", token_id);

	makeGetAjaxRequest("web/ParentDashboardNotifications/"+token_id+ "/"+student_id+"/dashboard", false, onSuccess, onFailure);

});

$('.student_details').on("click",function(){ 
	google.charts.load('current', {
		'packages' : [ 'gauge', 'corechart', 'bar' ]
	});
	google.charts.setOnLoadCallback(doStats);
});

function LoadGoogle()
{
	if(typeof google != 'undefined' && google && google.load)
	{
		// Now you can use google.load() here...
	}
	else
	{
		// Retry later...
		setTimeout(LoadGoogle, 30);
	}
}

function onSuccess(data) {
	var userData = data;
	if (!isDataNull(data) && !isDataNull(data.Return.Results) && !isDataNull(data.Return.Results[0])) {

		var not_available = "N/A";

		var enterTime = not_available;
		var exitTime = not_available;

		if(!isDataNull(data.Return.Results[0].schooltime)){
			if(!isDataNull(data.Return.Results[0].schooltime.entry_time)){
				enterTime = data.Return.Results[0].schooltime.entry_time;
				$("#enter_time").text("Enter - " + enterTime);
			}else{
				$("#enter_time").text("Enter - " + enterTime);
			}
			if(!isDataNull(data.Return.Results[0].schooltime.exit_time)){
				exitTime = data.Return.Results[0].schooltime.exit_time;
				$("#exit_time").text("Exit - " + exitTime);
			}else{
				$("#exit_time").text("Exit - " + exitTime);
			}
		} else{
			$("#enter_time").text("Enter - " + enterTime);
			$("#exit_time").text("Exit - " + exitTime);
		}

		var geofence_entertime = not_available;
		var geofence_exittime = not_available;

		if(!isDataNull(data.Return.Results[0].geozone)){
			if(!isDataNull(data.Return.Results[0].geozone.entry_time)){
				geofence_entertime = data.Return.Results[0].geozone.entry_time;
				$("#geo_entertime").text("Enter - " + geofence_entertime);
			}else{
				$("#geo_entertime").text("Enter - " + geofence_entertime);
			}
			if(!isDataNull(data.Return.Results[0].geozone.exit_time)){
				geofence_exittime = data.Return.Results[0].geozone.exit_time;
				$("#geo_exittime").text("Exit - " + geofence_exittime);
			}else{
				$("#geo_exittime").text("Exit - " + geofence_exittime);
			}
		} else{
			$("#geo_entertime").text("Enter - " + geofence_entertime);
			$("#geo_exittime").text("Exit - " + geofence_exittime);
		}

		var alertType = not_available;
		var alertTime = not_available;
		var alertData = not_available;

		if(!isDataNull(data.Return.Results[0].alert) && !isDataNull(data.Return.Results[0].alert.type)
				&& !isDataNull(data.Return.Results[0].alert.time)){

			alertType = data.Return.Results[0].alert.type;
			alertTime = data.Return.Results[0].alert.time;
			alertData = alertType + " - " + alertTime;
			$("#alert_data").text(alertData);

		}else{
			$("#alert_data").text(alertData);
		}

		var reminder_title = not_available;
		var reminder_time = not_available;
		var reminder_data = not_available;

		if(!isDataNull(data.Return.Results[0].reminder) && !isDataNull(data.Return.Results[0].reminder.title)
				&& !isDataNull(data.Return.Results[0].reminder.time)){
			reminder_title = data.Return.Results[0].reminder.title;
			reminder_time = data.Return.Results[0].reminder.time;
			reminder_data = reminder_title + ', ' + reminder_time;
			$("#reminder_data").attr("title",reminder_data);
			$("#reminder_data").text(reminder_data);
		}else{
			$("#reminder_data").text(reminder_data);
		}

		var announcementTitle = not_available;
		var announcementTime = not_available;
		var announcementData = not_available;

		if(!isDataNull(data.Return.Results[0].announcement) && !isDataNull(data.Return.Results[0].announcement.title)
				&& data.Return.Results[0].announcement.time){
			announcementTitle = data.Return.Results[0].announcement.title;
			announcementTime = data.Return.Results[0].announcement.time;
			announcementData = announcementTitle + " - " + announcementTime;
			$("#announcement_data").attr("title",announcementData);
			$("#announcement_data").text(announcementData);
		}else{
			$("#announcement_data").text(announcementData);
		}

		var rewardsCategory = not_available;
		var rewardsData = not_available;
		var rewardDetails = not_available;

		if(!isDataNull(data.Return.Results[0].reward) && !isDataNull(data.Return.Results[0].reward.category)
				&& !isDataNull(data.Return.Results[0].reward.reward) ){

			rewardsCategory = data.Return.Results[0].reward.category;
			rewardsData = data.Return.Results[0].reward.reward;
			rewardDetails = rewardsCategory + " - " + rewardsData;
			$("#rewards_data").attr("title",rewardDetails);
			$("#rewards_data").text(rewardDetails);
		}else{
			$("#rewards_data").text(rewardDetails);
		}
	}
}

function onFailure(data){
	var not_available = "N/A";
	var enterTime = not_available;
	var exitTime = not_available;

	$("#enter_time").text(not_available);
	$("#exit_time").text(not_available);

	$("#geo_entertime").text(not_available);
	$("#geo_exittime").text(not_available);

	$("#reminder_title").text(not_available);
	$("#reminder_time").text(not_available);

	$("#alert_data").text(not_available);

	$("#announcement_data").text(not_available);
	$("#rewards_data").text(not_available);

	var failureResponse = data.status;
	debugLogs("Inside onFailure");
}

function doStats() {
	var yd = new Date(Date.now() - 864e5);
	var ymonth = yd.getMonth()+1;
	var yday = yd.getDate();
	var yesterdayDate =yd.getFullYear() + '-' +
	(ymonth<10 ? '0' : '') + ymonth + '-' +
	(yday<10 ? '0' : '') + yday;
	var pfi_resize = "",steps_resize = "", calories_resize = "",sleep_resize = "";
	
	var statisticsOverview = {
			init: function() {
				this.drawFitnessChart();
			},
			drawFitnessChart: function() {
				var token = sessionStorage.getItem("token_id");
				var student_id = sessionStorage.getItem("student_id");
				var requestDataFitness = {
						"source": "web",
						"measure_type":"fitness, steps, activity, heartrate, sleep, calories, emotion, stress",
						"start_date": yesterdayDate,
						"end_date": yesterdayDate,
						"student_id": student_id
				};

				//make post request for all activites : 
				makePostAjaxRequest("web/StudentActivity/"+token, requestDataFitness, onSuccessPostActivity, onFailurePostActivity);
				
				function onSuccessPostActivity(responseData){
					if(!isDataNull(responseData.Return.Results)){
						var results = responseData.Return.Results;
						onSuccessPostFitnessParentActivity(results);
						onSuccessPostStepsParentActivity(results);
						onSuccessPostActivityParentActivity(results);
						onSuccessPostHeartRateParentActivity(results);
						onSuccessPostCaloriesBurntParentActivity(results);
						onSuccessPostSleepParentActivity(results);
						onSuccessPostEmotionParentActivity(results);
						onSuccessPostStressParentActivity(results);
					}
				}
				
				function onFailurePostActivity(responseData){
					debugLogs("Inside onFailurePostActivity");
					var fitness_data = "N/A";
					$("#donutchart_pfi").empty();
					$("#fitness_info").css({"display":"none"});
					$("#fitness_infoNA").css({"display":"block"});
					$('#fitness_infoNA').text(fitness_data);
					pfi_resize = undefined;
				}
				
				function onSuccessPostFitnessParentActivity(responseData){
					var fitness_data = "N/A";
					if(!isDataNull(responseData.fitness) && !isDataNull(responseData.fitness[0].date) && (responseData.fitness[0].value >= 0)){
						fitness_data = responseData.fitness[0].value;
						pfi_resize = fitness_data;
						$("#fitness_infoNA").css({"display":"none"});
						$("#fitness_info").css({"display":"block"});
						drawFitnessIndexChart(fitness_data);
					}else{
						$("#donutchart_pfi").empty();
						pfi_resize = undefined;
						$("#fitness_info").css({"display":"none"});
						$("#fitness_infoNA").css({"display":"block"});
						$('#fitness_infoNA').text(fitness_data);
					}
				}

				function onSuccessPostStepsParentActivity(responseData){
					var steps_data = "N/A";
					if(!isDataNull(responseData.steps) && !isDataNull(responseData.steps[0].date)
							&& !isDataNull(responseData.steps[0].value)){
						steps_data = 0;
						for(var i=0; i < responseData.steps.length; i++){
							steps_data += responseData.steps[i].value;
						}
						steps_resize = steps_data;
						if(steps_data > 0){
							step_data = steps_data+ " Steps";
							$('#steps_data').text(step_data);
							drawStepsChart(steps_data);
						}
					}else{
						$("#donutchart_steps").empty();
						steps_resize = undefined;
						$('#steps_data').text(steps_data);
					}
				}
				function onSuccessPostActivityParentActivity(responseData){
					var activity_duration = "N/A";
					var activity_situation = "N/A";
					var walking_time = "N/A", running_time = "N/A", cycling_time = "N/A";
					var walk = 2, run = 3, cycle =4;
					var walkSet = false,runSet = false, cycleSet= false;
					if(!isDataNull(responseData.activity)){
						jsonActivityObj = responseData.activity;
						for(var j =0; j< jsonActivityObj.length;j++){
							if(!isDataNull(jsonActivityObj[j].duration) && !isDataNull(jsonActivityObj[j].situation)){
								if(jsonActivityObj[j].situation == walk && !walkSet){
									walking_time = ((jsonActivityObj[j].duration/60).toFixed(2))+ " minutes";
									$("#walking_time").text(walking_time);
									walkSet = true;
								}
								if(jsonActivityObj[j].situation == run && !runSet){
									running_time = ((jsonActivityObj[j].duration/60).toFixed(2))+ " minutes";
									$("#running_time").text(running_time);
									runSet = true;
								}
								if(jsonActivityObj[j].situation == cycle && !cycleSet){
									cycling_time = ((jsonActivityObj[j].duration/60).toFixed(2))+ " minutes";
									$("#cycling_time").text(cycling_time);
									cycleSet = true;
								}
							}
						}
						if(!walkSet){
							$("#walking_time").text(walking_time);
						}
						if(!runSet){
							$("#running_time").text(running_time);
						}
						if(!cycleSet){
							$("#cycling_time").text(cycling_time);
						}
					}else{
						$("#walking_time").text(walking_time);
						$("#running_time").text(running_time);
						$("#cycling_time").text(cycling_time);
					}
				}

				function onSuccessPostHeartRateParentActivity(responseData){
					var heartRate_data = "N/A";
					if(!isDataNull(responseData.heartrate) && !isDataNull(responseData.heartrate[0].date) && !isDataNull(responseData.heartrate[0].value)){
						heartRate_data = responseData.heartrate[0].value+ " bpm";
						$('#heartrate_data').text(heartRate_data);
					}else{
						$('#heartrate_data').text(heartRate_data);
					}
				}
				
				function onSuccessPostCaloriesBurntParentActivity(responseData){
					var calories_data = "N/A";
					if(!isDataNull(responseData.calories) && !isDataNull(responseData.calories[0].date) && !isDataNull(responseData.calories[0].value)){
						calories_data = 0;
						for(var i=0; i < responseData.calories.length; i++){
							calories_data += responseData.calories[i].value;
						}
						calories_resize = calories_data;
						if(calories_data > 0){
							cal_data = calories_data+ " Cal";
							$('#calories_data').text(cal_data);
							drawCaloriesChart(calories_data);
						}
					}else{
						$("#donutchart_calories").empty();
						calories_resize = undefined;
						$('#calories_data').text(calories_data);
					}
				}

				function onSuccessPostSleepParentActivity(responseData){
					var sleep_data = "N/A", bed_time = "N/A", awake_time = "N/A" , sleep_quality = "N/A";
					if(!isDataNull(responseData.sleep) && !isDataNull(responseData.sleep[0].date)
							&& !isDataNull(responseData.sleep[0].value)){
						sleep_data = responseData.sleep[0].value;
						sleep_resize = sleep_data;
						if(sleep_data > 0){
							if(!isDataNull(responseData.sleep[0].bed_time)){
								bed_time = responseData.sleep[0].bed_time;
								$('#bed_time').text(bed_time);
							}else{
								$('#bed_time').text(bed_time);
							}
							if(!isDataNull(responseData.sleep[0].awake_time)){
								awake_time = responseData.sleep[0].awake_time;
								$('#awake_time').text(awake_time);
							}else{
								$('#awake_time').text(awake_time);
							}
							if(!isDataNull(responseData.sleep[0].sleep_quality)){
								sleep_quality = responseData.sleep[0].sleep_quality;
								$('#sleep_quality').text(sleep_quality);
							}else{
								$('#sleep_quality').text(sleep_quality);
							}
							drawSleepChart(sleep_data)
						}
					}else{
						$("#donutchart_sleep").empty();
						sleep_resize = undefined;
						$('#bed_time').text(bed_time);
						$('#awake_time').text(awake_time);
						$('#sleep_quality').text(sleep_quality);
					}
				}

				function onSuccessPostEmotionParentActivity(responseData){
					var happy_range = 1, sad_range = 2, fear_range = 4, angry_range = 3;
					jsonObj = responseData.emotion;
					obj = jsonSort(jsonObj, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
					for(var j = 0; j< jsonObj.length;j++){
						if(!isDataNull(jsonObj[j].date)){
							var time = formatDate(jsonObj[j].date,"HH");
							if(!isDataNull(jsonObj[j].situation)){
								var emotionValue = jsonObj[j].situation;
								$("#emotion-"+time).text(' ');
								if(emotionValue == happy_range){
									$("#emotion-"+time).addClass("img-happy");
								}else if(emotionValue == sad_range){
									$("#emotion-"+time).addClass("img-sad");
								}else if(emotionValue == angry_range){
									$("#emotion-"+time).addClass("img-fear");
								}else if(emotionValue == fear_range){
									$("#emotion-"+time).addClass("img-angry");
								}
							}
						}
					}
				}
				
				function onSuccessPostStressParentActivity(responseData){
					var normal_range = 1, abnormal_range = 2;
					jsonObj = responseData.stress;
					obj = jsonSort(jsonObj, "date", DATATYPE.NUMBER, SORTORDER.ASC, null); 
					for(var j = 0; j< jsonObj.length;j++){
						if(!isDataNull(jsonObj[j].date)){
							var time = formatDate(jsonObj[j].date,"HH");
							if(!isDataNull(jsonObj[j].situation)){
								var stressValue = jsonObj[j].situation;
								$("#stress-"+time).text(' ');
								if(stressValue == normal_range){
									$("#stress-"+time).addClass("img-normal");
								}else if(stressValue == abnormal_range){
									$("#stress-"+time).addClass("img-stress");
								}
							}
						}
					}
				}
			}
	};    
	$(window).resize(function(){ 
		$("#donutchart_calories").empty();
		$("#donutchart_pfi").empty();
		$("#donutchart_sleep").empty();
		$("#donutchart_steps").empty();
		if(!isDataNull(pfi_resize)){
			drawFitnessIndexChart(pfi_resize);
		}
		if(!isDataNull(steps_resize)){
			drawStepsChart(steps_resize); 
		}
		if(!isDataNull(calories_resize)){
			drawCaloriesChart(calories_resize);
		}
		if(!isDataNull(sleep_resize)){
			drawSleepChart(sleep_resize);
		}
	});
	statisticsOverview.init()
}

function drawFitnessIndexChart(fitnessData) {
	debugLogs("pfi: " + fitnessData);
	var poorRange = 59;
	var avgRange = 79;
	var goodRange = 99;
	var excellentRange = 100;
	var rest = 100;
	var data;
	var pfiCategoryText = "";
	var pfiValue = 0;
	var poorColor = '#8CC63F';
	var avgColor = '#84B23B';
	var goodColor = '#779B31';
	var excellentColor = '#668221';
	var restColor = '#E6E6E6';
	var donutSlices;

	if(fitnessData >= 0 && fitnessData <= poorRange) {
		pfiCategoryText = 'Poor (0-59)';
		pfiValue = fitnessData;
		rest = excellentRange - fitnessData;
		if(fitnessData == 0) {
			restCategoryText = 'Poor (0-59)';
		} else {
			restCategoryText = 'rest'
		}
		donutSlices = {
				0 : {
					color : poorColor
				},
				1 : {
					color : restColor
				}
		};
	} else if(fitnessData > poorRange && fitnessData <= avgRange){
		pfiCategoryText = 'Average (60-79)';
		pfiValue = fitnessData;
		rest = excellentRange - fitnessData;
		restCategoryText = 'rest'
		donutSlices = {
				0 : {
					color : avgColor
				},
				1 : {
					color : restColor
				}
		};
	} else if(fitnessData > avgRange && fitnessData <= goodRange){
		pfiCategoryText = 'Good (80-99)';
		pfiValue = fitnessData;
		rest = excellentRange - fitnessData;
		restCategoryText = 'rest'
		donutSlices = {
				0 : {
					color : goodColor
				},
				1 : {
					color : restColor
				}
		};
	} else if(fitnessData > goodRange){
		pfiCategoryText = 'Excellent (100+)';
		pfiValue = fitnessData;
		rest = 0;
		restCategoryText = 'rest'
		donutSlices = {
				0 : {
					color : excellentColor
				},
				1 : {
					color : restColor
				}
		};
	}
	var data = google.visualization.arrayToDataTable([
	                                                  [ 'PFI Range', 'PFI Value' ], 
	                                                  [ pfiCategoryText, pfiValue ],
	                                                  [ restCategoryText, rest]
	                                                  ]);

	var options = {
			tooltip : {isHtml: true},
			title : "Excellent",
			pieHole : 0.5,
			pieStartAngle : 0,
			pieSliceText : 'percentage',
			pieSliceTextStyle: {
				color : 'black',
				fontSize : 20,
				fontName : 'Roboto'
			},
			legend : {
				position : "none"
			},
			titleTextStyle : {
				color : '#39B54A',
				fontSize : 20,
				fontName : 'Roboto'
			},
			backgroundColor : '#FFF',
			slices : donutSlices
	};

	var chart = new google.visualization.PieChart(document
			.getElementById('donutchart_pfi'));
	chart.draw(data, options);
}


function drawStepsChart(stepsActual) {
	var pierange = 10000;
	var rest = 0;
	if(stepsActual < pierange){
		rest = pierange - stepsActual;
	}
	var data = google.visualization.arrayToDataTable([
	                                                  [ 'Steps', 'Steps' ],
	                                                  [ 'Steps Actual', stepsActual ],
	                                                  [ 'Steps Remaining', rest ] ]);
	var titleValue = stepsActual + "Steps"
	var options = {
			tooltip : {isHtml: true},
			title : titleValue,
			pieHole : 0.5,
			pieStartAngle : 0,
			pieSliceText : 'percentage',
			pieSliceTextStyle: {
				color : 'black',
				fontSize : 20,
				fontName : 'Roboto'
			},
			legend : {
				position : "none"
			},
			sliceVisibilityThreshold: 1/10000,
			titleTextStyle : {
				color : '#FFAA00',
				fontSize : 20,
				fontName : 'Roboto'
			},
			backgroundColor : '#FFF',
			slices : {
				0 : {
					color : '#FFAA00'
				},
				1 : {
					color : '#E9E9E9'
				}
			}
	};
	var chart = new google.visualization.PieChart(document
			.getElementById('donutchart_steps'));
	chart.draw(data, options);
}

function drawCaloriesChart(calories_burnt) {
	var pierange = 2000;
	var rest = 0;
	if(calories_burnt < pierange){
		rest = pierange - calories_burnt;
	}
	var data = google.visualization.arrayToDataTable([
	                                                  [ 'Calories', 'Calories' ],
	                                                  [ 'Calories Burnt', calories_burnt ],
	                                                  [ 'Calories Actual', rest ] ]);
	var options = {
			tooltip : {isHtml: true},
			title : "9810 Calories",
			pieHole : 0.5,
			pieStartAngle : 0,
			pieSliceText : 'percentage',
			pieSliceTextStyle: {
				color : 'black',
				fontSize : 20,
				fontName : 'Roboto'
			},
			legend : {
				position : "none"
			},
			titleTextStyle : {
				color : '#FFAA00',
				fontSize : 20,
				fontName : 'Roboto'
			},
			backgroundColor : '#FFF',
			slices : {
				0 : {
					color : '#F15A24'
				},
				1 : {
					color : '#E9E9E9'
				}
			}
	};
	var chart = new google.visualization.PieChart(document
			.getElementById('donutchart_calories'));
	chart.draw(data, options);
}

function drawSleepChart(sleep) {
	var pierange = 12;
	var rest = 0;
	if(sleep < pierange){
		rest = pierange - sleep;
	}
	var data = google.visualization.arrayToDataTable([
	                                                  [ 'Sleep', 'Sleep' ], 
	                                                  [ 'Total Sleep', sleep ],
	                                                  [ 'Sleep Remaining', rest ]
	                                                  ]);
	var options = {
			tooltip : {isHtml: true},
			title : "9H 25M",
			pieHole : 0.8,
			pieStartAngle : 0,
			pieSliceText : 'percentage',
			pieSliceTextStyle: {
				color : 'black',
				fontSize : 20,
				fontName : 'Roboto'
			},
			legend : {
				position : "none"
			},
			titleTextStyle : {
				color : '#FFAA00',
				fontSize : 20,
				fontName : 'Roboto'
			},
			backgroundColor : '#FFF',
			slices : {
				0 : {
					color : '#1B1464'
				},
				1 : {
					color : '#E6E6E6'
				}
			}
	};

	var chart = new google.visualization.PieChart(document
			.getElementById('donutchart_sleep'));
	chart.draw(data, options);
	$('.sleeptext').text("Total Sleep");
	var sleepInHour = 0;
	var sleepInMins = 0;
	if(sleep > 0){
		var sleepTime = sleep/3600;
		sleepInHour = Math.floor(sleepTime);
		sleepInMins = Math.floor((sleepTime - sleepInHour) * 60);
	}
	$(".sleeptime").text(sleepInHour+"hr " +sleepInMins+"min");
}

