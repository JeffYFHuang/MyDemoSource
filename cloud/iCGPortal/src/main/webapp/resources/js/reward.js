$(function(){
	var token_id = $("#token_id").val();
	var weekCount = 0, monthCount = 0;
	var currentDuration = 'week'; //Default week is selected
	var monthDisplay, weekDisplay = '';
	$('.weekDate').attr('class', 'selected-date ' + $('.weekDate').attr('class'));
	weekDisplay = navigateWeek(weekCount);

	$('.rewards').removeClass("treeview").addClass("active");
	$('.rewards').removeClass("font-medium-roboto")
			.addClass("font-bold-roboto");
	$("#sRewardsIcon").attr("src",
			"resources/images/SchoolAdmin_sideBarIcons/White/Reward.png");
	
	$(".duration .fc-button").click(function(){
		$(".duration .fc-button").removeClass("selected-date");
		$(this).addClass("selected-date");
		if($(this).hasClass("weekDate")){
			currentDuration = 'week';

			weekDisplay = navigateWeek(weekCount);
			loadRewardRanking(weekDisplay);
		} else {
			currentDuration = 'month';

			if(isDataNull(monthDisplay)) monthDisplay = navigateMonth(monthCount);
			loadRewardRanking(monthDisplay);
		}
	});

	$(".navigation .fc-button").click(function(){
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
			loadRewardRanking(weekDisplay);
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
			loadRewardRanking(monthDisplay);
		}			
	});

	$( "#schoolRewardRankingGrade" ).change(function() {
		if(currentDuration == 'week'){
			weekDisplay = navigateWeek(weekCount);
			loadRewardRanking(weekDisplay);
		}else{
			monthDisplay = navigateMonth(monthCount);
			loadRewardRanking(monthDisplay);
		}
	});
	initRanking();
	function initRanking(){
		makeGetAjaxRequest("web/getGradeClass/"
				+ token_id, false,
				onGradeSuccess, onFailure);
		return false;
	}
	function onGradeSuccess(defaultdata) {
		aGrades = new Array();
		aClass = new Array();
		$('#schoolRewardRankingGrade').html('');
		$('#schoolRewardRankingGrade').append('<option class="icglabel_allgrade" value="0">All Grade</option>');
		var grades = defaultdata.Return.Results.Grades;
		var len = grades.length;
		if (len > 0){
			for (var count = 0; count < len; count++) {
				var cGrade = grades[count].grade;
				var cClass = grades[count].class;

				if(aGrades.indexOf(cGrade) < 0) {
					aGrades.push(cGrade);
					$('#schoolRewardRankingGrade').append($('<option>', {value:cGrade, text:cGrade}));
				}
				if(typeof aClass[cGrade] === 'undefined') {
					aClass[cGrade] = new Array();
				}
				aClass[cGrade].push(cClass);
			}
		}
		loadRewardRanking(weekDisplay);
	}
	function onFailure(data){
		debugLogs(data);
	}
	function loadRewardRanking(dates){
		$("#duration").text(dates[2]);

		gradeSelected = $("#schoolRewardRankingGrade").val();
		if(gradeSelected == 0){
			makeGetAjaxRequest("web/RewardRanking/"
					+ token_id+ "/" +dates[0]+ "/" +dates[1]+ "/" +gradeSelected, false,
					onSuccessLoadDefaultRanking, onFailure);
		}else{
			makeGetAjaxRequest("web/RewardRanking/"
					+ token_id+ "/" +dates[0]+ "/" +dates[1]+ "/" +gradeSelected, false,
					onSuccessLoadGradeRanking, onFailure);
		}
	}
	function onSuccessLoadDefaultRanking(defaultdata){
		$("#rewardListContent").css({'display':'none'});
		$("div#rewardCatStatistics div").remove();
		var reward_url="", reward_name="", reward_count="", reward_total_count=0,imgExists="",rewards_category_id="";
		if(!isDataNull(defaultdata) && !isDataNull(defaultdata.Return) && 
				!isDataNull(defaultdata.Return.Results) && 
				!isDataNull(defaultdata.Return.Results.rewards) && 
				!isDataNull(defaultdata.Return.Results.rewards.reward_statistics)){
			jsonObj = defaultdata.Return.Results.rewards.reward_statistics;
			obj = jsonSort(jsonObj, "name", DATATYPE.STRING, SORTORDER.ASC, null); 

			for(var j = 0; j < jsonObj.length; j++){
				if(!isDataNull(jsonObj[j].rewards_category_id)){
					rewards_category_id = jsonObj[j].rewards_category_id;	
				}
				if(!isDataNull(jsonObj[j].name)){
					reward_name = jsonObj[j].name;	
				}
				if(!isDataNull(jsonObj[j].total_count)){
					reward_count = jsonObj[j].total_count;
					reward_total_count += reward_count;
				}else{
					reward_count = 'n/a';
				}
				var rewardStatisticsList =	'<div class="imageDiv">'+
				'<div style="height:180px">'+
				'<div class="text-center font-reg-roboto rewardstatname"><p class="panel-pre">'+reward_name+'</p></div>'+
				'<div class="rewardranking-img" id="schoolRewards'+j+'"></div>'+
				'<div class="text-center txt-36 font-reg-roboto rewardstat">'+reward_count+'</div>'+
				'</div>'+
				'</div>';
				$("#rewardCatStatistics").append(rewardStatisticsList);
				if(!isDataNull(jsonObj[j].category_icon_url)){
					reward_url = jsonObj[j].category_icon_url;
					imgExists = '<img height="72" width="72" style="border-radius: 10px;" id="adminRewardImg'+rewards_category_id+'">';
					$("#schoolRewards"+j).append(imgExists);
					var element = "adminRewardImg"+rewards_category_id;
					isImageExists(encodeURI(reward_url),element,null);
				}else{
					imgExists ='<div class="text-center h3 rewardimgnotexits">n/a</div>';
					$("#schoolReward"+reward_name).append(imgExists);
				}

				reward_url="", reward_name="", reward_count="",imgExists="",rewards_category_id="";
			}
			$("#total_rewrad_count").text((reward_total_count > 0)? reward_total_count : 'n/a');
		}else{
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			$('#rewardCatStatistics').append('<div style="text-align: center;"><strong>' + nodataText + '</strong></div>');
		}	
	}
	function onSuccessLoadGradeRanking(gradedata){
		$("#rewardListContent").css({'display':'block'});
		$("div#rewardCatStatistics div").remove();
		$("tr#rewardCatListHeading th").remove();
		$("tbody#rewardCatListContent tr").remove();
		var reward_url="", reward_name="", reward_count="", reward_total_count=0, dynamicth = "",imgExists="",rewards_category_id="";
		if(!isDataNull(gradedata.Return.Results.rewards) && !isDataNull(gradedata.Return.Results.rewards.reward_statistics)){
			jsonObj = gradedata.Return.Results.rewards.reward_statistics;
			obj = jsonSort(jsonObj, "name", DATATYPE.STRING, SORTORDER.ASC, null); 

			for(var j = 0; j < jsonObj.length; j++){
				if(!isDataNull(jsonObj[j].rewards_category_id)){
					rewards_category_id = jsonObj[j].rewards_category_id;	
				}
				if(!isDataNull(jsonObj[j].name)){
					reward_name = jsonObj[j].name;	
				}
				if(!isDataNull(jsonObj[j].total_count)){
					reward_count = jsonObj[j].total_count;
					reward_total_count += reward_count;
				}else{
					reward_count = 'n/a';
				}
				var rewardStatisticsList =	'<div class="imageDiv">'+
				'<div style="height:180px">'+
				'<div class="text-center font-reg-roboto rewardstatname"><p class="panel-pre">'+reward_name+'</p></div>'+
				'<div class="rewardranking-img" id="schoolRewards'+j+'"></div>'+
				'<div class="text-center txt-36 font-reg-roboto rewardstat">'+reward_count+'</div>'+
				'</div>'+
				'</div>';
				dynamicth += '<th class="icglabel_"'+reward_name+'>'+reward_name+'<br/><span class="txt-12">(Best Student Reward)</span></th>';
				$("#rewardCatStatistics").append(rewardStatisticsList);
				if(!isDataNull(jsonObj[j].category_icon_url)){
					reward_url = jsonObj[j].category_icon_url;
					imgExists = '<img height="72" width="72" style="border-radius: 10px;" id="adminRewardImg'+rewards_category_id+'"></span>';
					$("#schoolRewards"+j).append(imgExists);
					var element = "adminRewardImg"+rewards_category_id;
					isImageExists(encodeURI(reward_url),element,null);

				}else{
					imgExists ='<div class="text-center h3 rewardimgnotexits">n/a</div>';
					$("#schoolReward"+reward_name).append(imgExists);
				}

				reward_url="", reward_name="", reward_count="",imgExists="",rewards_category_id="";
			}
			$("#total_rewrad_count").text((reward_total_count > 0)? reward_total_count : 'n/a');
		}	
		if(!isDataNull(gradedata.Return.Results.rewards) && !isDataNull(gradedata.Return.Results.rewards.class_rankings)){
			var ranking_class="",ranking_rank="",ranking_reward="",ranking_topStudent="",ranking_topReward="" ;
			var rewardCatHeadingList ='<th class="icglabel_class">Class<br /></th>'+
			'<th class="icglabel_ranking">Ranking<br /></th>'+
			'<th class="icglabel_totalreward">Total Reward<br/><span class="txt-12">(Best Student Reward)</span></th>';
			$("#rewardCatListHeading").append(rewardCatHeadingList);
			$("#rewardCatListHeading").append(dynamicth);
			var noValue = 'n/a';
			var classToppers = new Array();
			jsonRankingObj = gradedata.Return.Results.rewards.class_rankings;
			rankingObj = jsonSort(jsonRankingObj, "total_class_rewards", DATATYPE.NUMBER, SORTORDER.DESC, null); 
			for(var i = 0; i < jsonRankingObj.length; i++){
				if(!isDataNull(jsonRankingObj[i].class)){
					ranking_class = jsonRankingObj[i].class;
				}
				if(!isDataNull(jsonRankingObj[i].class_rank)){
					ranking_rank = jsonRankingObj[i].class_rank;
				}
				if(!isDataNull(jsonRankingObj[i].total_class_rewards)){
					ranking_reward = jsonRankingObj[i].total_class_rewards;
					classToppers.push(ranking_reward);
				}
				if(!isDataNull(jsonRankingObj[i].top_student_name)){
					ranking_topStudent = jsonRankingObj[i].top_student_name;
				}
				if(!isDataNull(jsonRankingObj[i].top_student_rewards)){
					ranking_topReward = jsonRankingObj[i].top_student_rewards;	
				}
				var rewardCatListContentList =	'<tr id="ranks'+i+'"><td>'+ranking_class+'</td>'+
				'<td><span class="badge'+ranking_reward+'"></span></td>'+
				'<td>'+ranking_reward+'<br/>('+ranking_topStudent+' <span> '+ranking_topReward+')</span></td></tr>';
				$("#rewardCatListContent").append(rewardCatListContentList);
				
				ranking_reward ="",ranking_topStudent="",ranking_topReward="";
				if(!isDataNull(jsonRankingObj[i].reward_categories)){
					rankingObj = jsonSort(jsonRankingObj[i].reward_categories, "name", DATATYPE.STRING, SORTORDER.ASC, null); 
					for(var j = 0; j < jsonRankingObj[i].reward_categories.length; j++){
						if(!isDataNull(jsonRankingObj[i].reward_categories[j].total_count)){
							ranking_reward = jsonRankingObj[i].reward_categories[j].total_count;
						}else{
							ranking_reward = 0;
						}
						if(!isDataNull(jsonRankingObj[i].reward_categories[j].top_student_name)){
							ranking_topStudent = jsonRankingObj[i].reward_categories[j].top_student_name;
						}
						if(!isDataNull(jsonRankingObj[i].reward_categories[j].top_student_rewards)){
							ranking_topReward = jsonRankingObj[i].reward_categories[j].top_student_rewards;
						}
						var rewardCatListContentdynamicList = '<td>'+noValue+'</td>';
						if(ranking_reward > 0) rewardCatListContentdynamicList = '<td>'+ranking_reward+'<br/>('+ranking_topStudent+' <span> '+ranking_topReward+')</span></td>';
						$("#ranks"+i).append(rewardCatListContentdynamicList);
						ranking_reward ="",ranking_topStudent="",ranking_topReward="",rewardCatListContentdynamicList="";
					}
				}
				badges="",ranking_class="",ranking_rank="",rankingObj="",rewardCatListContentList="";
			}
			if(classToppers.length){
				classToppers = $.unique(classToppers).sort(function(a, b){return b-a});
				var topReward = ["resources/images/gold_badges.png", "resources/images/Silver_badges.png", "resources/images/Bronze.png", "resources/images/green_badges.png"];
				var topRewardTitle = ['Gold', 'Silver', 'Bronze','Green'];
				var top3 = 0;
				for(var i = 0; i < classToppers.length; i++){
					console.log(classToppers[i]);
					if(classToppers[i] > 0) {
						if(top3 >= 3){
							$('.badge'+classToppers[i]).append('<img src="'+topReward[3]+'" alt="'+topRewardTitle[3]+'" title="' + topRewardTitle[3] + '" />');
						}else{
							$('.badge'+classToppers[i]).append('<img src="'+topReward[top3]+'" alt="'+topRewardTitle[top3]+'" title="' + topRewardTitle[top3] + '" />');
							top3++;
						}
					}
				}
			}
			dynamicth="",rewardCatHeadingList="";
		}else{
			$("#rewardListContent").css({"display":"none"});
		}
	}

	$("#rewardRanking").click(function(){  
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		var text = $(this).context.innerHTML;
		$("ol#bread li .right").parent().remove();
		$("ol#bread .dynamicbread").remove();
		$("ol#bread .active").parent().remove();
		$("ol#bread").append(
				'<li class="icglabel_rewardranking dynamicbread">'+text+'</li>');
		$("#ranking-section").css({"display":"block"});
		$("#management-section").css({"display":"none"});
		initRanking();
	});

	$("#rewardMgt").click(function(){  
		$(this).parent().parent().find('.active').removeClass("active");
		$(this).addClass("active");
		var text = $(this).context.innerHTML;
		$("ol#bread li .right").parent().remove();
		$("ol#bread .dynamicbread").remove();
		$("ol#bread .active").parent().remove();
		$("ol#bread").append(
				'<li class="icglabel_rewardmanagement dynamicbread">'+text+'</li>');

		$("#ranking-section").css({"display":"none"});
		$("#management-section").css({"display":"block"});

		$('.editReward').click(function() {
			var rName = false;
			var currentTD = $(this).parents('tr').find('td');
			if ($(this).html() == '<img class="hand" src="../resources/images/unselected_edit_icon.png">') {
				currentTD = $(this).parents('tr').find('td');
				$.each(currentTD, function (val) {
					if(currentTD[val].className == "name" || currentTD[val].className == "image"){
						$(this).find(".noneditable").css({"display":"none"});
						$(this).find(".editable").css({"display":"block"});
					}else{
					}
				});
			} else {
				var rName=false;
				if(hasNumber($("#catName_0").val())){
					rName = true;
					$("#cname_err").css({"display" :"block"});
				}
				if(!rName){
					$.each(currentTD, function (val) {
						if(currentTD[val].className == "name" || currentTD[val].className == "image"){ 
							$(this).find(".editable").css({"display":"none"});
							$("#cname_err").css({"display" :"none"});
							$(this).find(".noneditable").text($(this).find(".editable").val());
							$(this).find(".noneditable").css({"display":"block"});
						}else{

						}
					});
				}else{
					$.each(currentTD, function (val) {
						$(this).find(".noneditable").css({"display":"none"});
						$(this).find(".editable").css({"display":"block"});
					});
					$(this).html('<img class="hand" src="../resources/images/unselected_edit_icon.png">');
				}
			}
			$(this).html($(this).html() == '<img class="hand" src="../resources/images/unselected_edit_icon.png">' ? '<img class="hand" src="../resources/images/selected_edit_icon.png">' : '<img class="hand" src="../resources/images/unselected_edit_icon.png">')
		});
		$('.editRewardType').click(function() {
			var ctName = false;
			var currentRewardTypeTD = $(this).parents('tr').find('td');
			if ($(this).html() == '<img src="../resources/images/unselected_edit_icon.png">') {
				currentRewardTypeTD = $(this).parents('tr').find('td');
				$.each(currentRewardTypeTD, function (val) {
					if(currentRewardTypeTD[val].className == "name" || currentRewardTypeTD[val].className == "image"){
						$(this).find(".noneditable").css({"display":"none"});
						$(this).find(".editable").css({"display":"block"});
					}else{
					}
				});
			} else {
				var ctName=false;
				if(hasNumber($("#cattypeName_0").val())){
					ctName = true;
					$("#ctname_err").css({"display" :"block"});
				}
				if(!ctName){
					$.each(currentRewardTypeTD, function (val) {
						if(currentRewardTypeTD[val].className == "name" || currentRewardTypeTD[val].className == "image"){ 
							$(this).find(".editable").css({"display":"none"});
							$("#ctname_err").css({"display" :"none"});
							$(this).find(".noneditable").text($(this).find(".editable").val());
							$(this).find(".noneditable").css({"display":"block"});
						}else{

						}
					});
				}else{
					$.each(currentRewardTypeTD, function (val) {
						$(this).find(".noneditable").css({"display":"none"});
						$(this).find(".editable").css({"display":"block"});
					});
					$(this).html('<img class="hand" src="../resources/images/unselected_edit_icon.png">');
				}
			}
			$(this).html($(this).html() == '<img class="hand" src="../resources/images/unselected_edit_icon.png">' ? '<img class="hand" src="../resources/images/selected_edit_icon.png">' : '<img class="hand" src="../resources/images/unselected_edit_icon.png">')
		});
	});

	$(function(){
		$('#rewardCategory_form').submit(function(e) {
			e.preventDefault();
			var rewardname = '';
			rewardname = $("#addRewardname").val();

			var isValidForm=true;

			$("#rewardCategoryName_err").css({"display":"none"});
			$("#rewardCategoryIcon_err").css({"display":"none"});
			if (rewardname == ""  || (rewardname.length > 45)) {
				$("#rewardCategoryName_err").css({"display":"block"});

				e.preventDefault();
				isValidForm = false;
			}
			if(isDataNull($("#file_attachment").val())){
				$("#rewardCategoryIcon_err").css({"display":"block"});

				e.preventDefault();
				isValidForm = false;
			}

			if(isValidForm){
				var requestData = new FormData($(this)[0]);
				var  file = $(this)[0].file_attachment.files[0];
				var sFileName = file.name.toLowerCase();
				var sFileExtension = sFileName.split('.')[sFileName.split('.').length - 1].toLowerCase();
				if(!isDataNull(file) && (file.size<IconImgMaximumUploadSize) && ((sFileExtension === "png") || (sFileExtension === "jpg") || (sFileExtension === "jpeg"))){
					var token_id = $("#token_id").val();
					makePostFileAjaxRequest("mobile/RewardsCategoryAdd/"+token_id+"/"+rewardname, requestData, onSuccessRewardsCategoryUpdate, onFailureRewardsCategoryUpdate);
				}else{
					$("#rewardsCategory_add_success").css({"display":"none"});
					$("#rewardsCategory_add_failure").css({"display":"none"});
					$("#rewardsCategory_update_success").css({"display":"none"});
					$("#rewardsCategory_update_failure").css({"display":"none"});
					$("#rewardsCategory_delete_success").css({"display":"none"});
					$("#rewardsCategory_delete_failure").css({"display":"none"});
					$("#maxFileSize_formMsg").css({"display":"none"});
					var responseMessage = '';
					$("#maxFileSize_formMsg").css({"display":"block"});
					hideSpinner();
					return false;
				}
			}else{
				e.preventDefault();
				hideSpinner();
				return false;
			}

		});
	});

	function onSuccessRewardsCategoryUpdate(responseData){
		$("#rewardsCategory_add_success").css({"display":"none"});
		$("#rewardsCategory_add_failure").css({"display":"none"});
		$("#rewardsCategory_update_success").css({"display":"none"});
		$("#rewardsCategory_update_failure").css({"display":"none"});
		$("#rewardsCategory_delete_success").css({"display":"none"});
		$("#rewardsCategory_delete_failure").css({"display":"none"});

		$("#maxFileSizeIn_formMsg").css({"display":"none"});
		$("#maxFileSize_formMsg").css({"display":"none"});
		$("#rewardCategoryName_err").css({"display":"none"});
		$("#rewardCategoryIcon_err").css({"display":"none"});
		$("#addRewardname").val("");
		$("#file_attachment").val("");
		$("#rewardCategory_formMsg").empty();

		var responseCode = '';
		var responseMessage = '';
		$("#rewardCategory_formMsg").empty();
		var token_id = $("#token_id").val();

		if(!isDataNull(responseData) && !isDataNull(responseData.Return) && !isDataNull(responseData.Return.ResponseSummary) && 
				!isDataNull(responseData.Return.ResponseSummary.StatusCode)){
			responseCode = responseData.Return.ResponseSummary.StatusCode;

			if(responseCode === 'SUC01'){
				$("#rewardsCategory_add_success").css({"display":"block"});
				rewardsCategoryInit("rewardsCategory_add");
			}else if(responseCode === 'ERR01'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>');
				$("#rewardsCategory_add_failure").css({"display":"block"});
			}else if(responseCode === 'ERR02'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>');
				$("#rewardsCategory_add_failure").css({"display":"block"});
			}else if(responseCode === 'ERR03'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>');
				$("#rewardsCategory_add_failure").css({"display":"block"});
			}else if(responseCode === 'ERR04'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>');
				$("#rewardsCategory_add_failure").css({"display":"block"});
			}else if(responseCode === 'ERR19'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>');
				$("#rewardsCategory_add_failure").css({"display":"block"});
			}else{
				responseMessage = 'OOPS !! Something went wrong';
				$("#rewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>');
				$("#rewardsCategory_add_failure").css({"display":"block"});
			}
		}else{
			responseMessage = 'OOPS !! Something went wrong';
			$("#rewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>');
			$("#rewardsCategory_add_failure").css({"display":"block"});
		}
	}

	function onFailureRewardsCategoryUpdate(responseData){
		responseMessage = 'OOPS !! Something went wrong';
		$("#rewardsCategory_add_success").css({"display":"none"});
		$("#rewardsCategory_add_failure").css({"display":"none"});
		$("#rewardsCategory_update_success").css({"display":"none"});
		$("#rewardsCategory_update_failure").css({"display":"none"});
		$("#rewardsCategory_delete_success").css({"display":"none"});
		$("#rewardsCategory_delete_failure").css({"display":"none"});

		$("#maxFileSizeIn_formMsg").css({"display":"none"});
		$("#maxFileSize_formMsg").css({"display":"none"});
		$("#rewardCategoryName_err").css({"display":"none"});
		$("#rewardCategoryIcon_err").css({"display":"none"});
		$("#addRewardname").val("");
		$("#file_attachment").val("");
		$("#rewardCategory_formMsg").empty();

		$("#rewardsCategory_add_failure").css({"display":"block"});
	}

	$(function(){
		$('#rewardCategoryIn_form').submit(function(e) {
			e.preventDefault();
			var rewardsactiveTab='';
			rewardsactiveTab = sessionStorage.getItem("rewardsactiveTab");
			var category_id = '';
			category_id = rewardsactiveTab;
			var rewardname = '';
			rewardname = $("#addRewardInname").val();

			var isValidForm=true;

			$("#rewardCategoryInName_err").css({"display":"none"});
			$("#rewardCategoryInIcon_err").css({"display":"none"});
			if (rewardname == ""  || (rewardname.length > 45)) {
				$("#rewardCategoryInName_err").css({"display":"block"});

				e.preventDefault();
				isValidForm = false;
			}
			if(isDataNull($("#fileIn_attachment").val())){
				$("#rewardCategoryInIcon_err").css({"display":"block"});

				e.preventDefault();
				isValidForm = false;

			}
			if(isValidForm){

				var requestData = new FormData($(this)[0]);
				var  file = $(this)[0].fileIn_attachment.files[0];
				var sFileName = file.name.toLowerCase();
				var sFileExtension = sFileName.split('.')[sFileName.split('.').length - 1].toLowerCase();
				if(!isDataNull(file) && (file.size<IconImgMaximumUploadSize) && ((sFileExtension === "png") || (sFileExtension === "jpg") || (sFileExtension === "jpeg"))){
					var token_id = $("#token_id").val();
					makePostFileAjaxRequest("mobile/RewardsCategoryInAdd/"+token_id+"/"+category_id+"/"+rewardname, requestData, onSuccessRewardsCategoryInUpdate, onFailureRewardsCategoryInUpdate);
				}else{
					$("#maxFileSizeIn_formMsg").css({"display":"none"});
					$("#rewardsCategoryIn_add_success").css({"display":"none"});
					$("#rewardsCategoryIn_add_failure").css({"display":"none"});
					$("#rewardsCategoryIn_update_success").css({"display":"none"});
					$("#rewardsCategoryIn_update_failure").css({"display":"none"});
					$("#rewardsCategoryIn_delete_success").css({"display":"none"});
					$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
					var responseMessage = '';
					$("#maxFileSizeIn_formMsg").css({"display":"block"});
					hideSpinner();
					return false;
					}
			}else{
				e.preventDefault();
				hideSpinner();
				return false;
			}


		});
	});

	function onSuccessRewardsCategoryInUpdate(responseData){
		$("#rewardsCategoryIn_add_success").css({"display":"none"});
		$("#rewardsCategoryIn_add_failure").css({"display":"none"});
		$("#rewardsCategoryIn_update_success").css({"display":"none"});
		$("#rewardsCategoryIn_update_failure").css({"display":"none"});
		$("#rewardsCategoryIn_delete_success").css({"display":"none"});
		$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
		$("#rewardCategoryInName_err").css({"display":"none"});
		$("#rewardCategoryInIcon_err").css({"display":"none"});
		$("#addRewardInname").val("");
		$("#fileIn_attachment").val("");
		$("#rewardCategoryIn_formMsg").empty();


		var responseCode = '';
		var responseMsg = '';
		$("#rewardCategoryIn_formMsg").empty();
		var token_id = $("#token_id").val();

		if(!isDataNull(responseData) && !isDataNull(responseData.Return) && !isDataNull(responseData.Return.ResponseSummary) && 
				!isDataNull(responseData.Return.ResponseSummary.StatusCode)){
			responseCode = responseData.Return.ResponseSummary.StatusCode;

			if(responseCode === 'SUC01'){
				$("#rewardsCategoryIn_add_success").css({"display":"block"});

				rewardsCategoryInit("rewardsCategoryIn_add");

				responseMessage = '';
				$("#rewardCategoryIn_formMsg").append(' <b>'+ responseMessage );
				responseMsg = '';

			}else if(responseCode === 'ERR01'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				responseMsg = '';
				$("#rewardsCategoryIn_add_failure").css({"display":"block"});
			}else if(responseCode === 'ERR02'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				responseMsg = '';
				$("#rewardsCategoryIn_add_failure").css({"display":"block"});
			}else if(responseCode === 'ERR03'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				responseMsg = '';
				$("#rewardsCategoryIn_add_failure").css({"display":"block"});
			}else if(responseCode === 'ERR04'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				responseMsg = '';
				$("#rewardsCategoryIn_add_failure").css({"display":"block"});
			}else if(responseCode === 'ERR19'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#rewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				responseMsg = '';
				$("#rewardsCategoryIn_add_failure").css({"display":"block"});
			}else{
				responseMessage = 'OOPS !! Something went wrong';
				$("#rewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				responseMsg = '';
				$("#rewardsCategoryIn_add_failure").css({"display":"block"});
			}
		}else{
			responseMessage = 'OOPS !! Something went wrong';
			$("#rewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
			responseMsg = '';
			$("#rewardsCategoryIn_add_failure").css({"display":"block"});
		}
	}

	function onFailureRewardsCategoryInUpdate(responseData){
		responseMessage = 'OOPS !! Something went wrong';
		$("#rewardsCategoryIn_add_success").css({"display":"none"});
		$("#rewardsCategoryIn_add_failure").css({"display":"none"});
		$("#rewardsCategoryIn_update_success").css({"display":"none"});
		$("#rewardsCategoryIn_update_failure").css({"display":"none"});
		$("#rewardsCategoryIn_delete_success").css({"display":"none"});
		$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
		$("#rewardCategoryInName_err").css({"display":"none"});
		$("#rewardCategoryInIcon_err").css({"display":"none"});
		$("#addRewardInname").val("");
		$("#fileIn_attachment").val("");
		$("#rewardCategoryIn_formMsg").empty();

		$("#rewardsCategoryIn_add_failure").css({"display":"block"});
	}


	function onSuccessRewardsCategoryEditUpdate(responseData){
		$("#rewardsCategory_add_success").css({"display":"none"});
		$("#rewardsCategory_add_failure").css({"display":"none"});
		$("#rewardsCategory_update_success").css({"display":"none"});
		$("#rewardsCategory_update_failure").css({"display":"none"});
		$("#rewardsCategory_delete_success").css({"display":"none"});
		$("#rewardsCategory_delete_failure").css({"display":"none"});
		$("#maxFileSizeIn_formMsg").css({"display":"none"});
		$("#maxFileSize_formMsg").css({"display":"none"});
		$("#editrewardCategoryName_err").css({"display":"none"});
		$("#editrewardCategoryIcon_err").css({"display":"none"});
		$("#editRewardname").val("");
		$("#editfile_attachment").val("");
		$("#rewardCategory_formMsg").empty();

		var responseCode = '';
		var responseMessage = '';
		$("#editrewardCategory_formMsg").empty();
		var token_id = $("#token_id").val();

		if(!isDataNull(responseData) && !isDataNull(responseData.Return) && !isDataNull(responseData.Return.ResponseSummary) && 
				!isDataNull(responseData.Return.ResponseSummary.StatusCode)){
			responseCode = responseData.Return.ResponseSummary.StatusCode;
			if(responseCode === 'SUC01'){
				$("#rewardsCategory_update_success").css({"display":"block"});
				rewardsCategoryInit("rewardsCategory_update");
			}else if(responseCode === 'ERR01'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_update_failure").css({"display":"block"});
			}else if(responseCode === 'ERR02'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_update_failure").css({"display":"block"});
			}else if(responseCode === 'ERR03'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_update_failure").css({"display":"block"});
			}else if(responseCode === 'ERR04'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_update_failure").css({"display":"block"});
			}else if(responseCode === 'ERR19'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_update_failure").css({"display":"block"});
			}else{
				responseMessage = 'OOPS !! Something went wrong';
				$("#editrewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_update_failure").css({"display":"block"});
			}
		}else{
			responseMessage = 'OOPS !! Something went wrong';
			$("#editrewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
			$("#rewardsCategory_update_failure").css({"display":"block"});
		}
	}

	function onFailureRewardsCategoryEditUpdate(responseData){
		responseMessage = 'OOPS !! Something went wrong';
		$("#rewardsCategory_add_success").css({"display":"none"});
		$("#rewardsCategory_add_failure").css({"display":"none"});
		$("#rewardsCategory_update_success").css({"display":"none"});
		$("#rewardsCategory_update_failure").css({"display":"none"});
		$("#rewardsCategory_delete_success").css({"display":"none"});
		$("#rewardsCategory_delete_failure").css({"display":"none"});
		$("#maxFileSizeIn_formMsg").css({"display":"none"});
		$("#maxFileSize_formMsg").css({"display":"none"});
		$("#editrewardCategoryName_err").css({"display":"none"});
		$("#editrewardCategoryIcon_err").css({"display":"none"});
		$("#editRewardname").val("");
		$("#editfile_attachment").val("");
		$("#rewardsCategory_update_failure").css({"display":"block"});
	}

	function onSuccessRewardsCategoryInEditUpdate(responseData){
		$("#rewardsCategoryIn_add_success").css({"display":"none"});
		$("#rewardsCategoryIn_add_failure").css({"display":"none"});
		$("#rewardsCategoryIn_update_success").css({"display":"none"});
		$("#rewardsCategoryIn_update_failure").css({"display":"none"});
		$("#rewardsCategoryIn_delete_success").css({"display":"none"});
		$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
		$("#editrewardCategoryInName_err").css({"display":"none"});
		$("#editrewardCategoryInIcon_err").css({"display":"none"});
		$("#editRewardInname").val("");
		$("#editfileIn_attachment").val("");
		$("#rewardCategoryIn_formMsg").empty();

		var responseCode = '';
		var responseMessage = '';
		$("#editrewardCategoryIn_formMsg").empty();
		var token_id = $("#token_id").val();

		if(!isDataNull(responseData) && !isDataNull(responseData.Return) && !isDataNull(responseData.Return.ResponseSummary) && 
				!isDataNull(responseData.Return.ResponseSummary.StatusCode)){
			responseCode = responseData.Return.ResponseSummary.StatusCode;
			if(responseCode === 'SUC01'){
				$("#rewardsCategoryIn_update_success").css({"display":"block"});
				rewardsCategoryInit("rewardsCategoryIn_update");
			}else if(responseCode === 'ERR01'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_update_failure").css({"display":"block"});
			}else if(responseCode === 'ERR02'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_update_failure").css({"display":"block"});
			}else if(responseCode === 'ERR03'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_update_failure").css({"display":"block"});
			}else if(responseCode === 'ERR04'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_update_failure").css({"display":"block"});
			}else if(responseCode === 'ERR19'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#editrewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_update_failure").css({"display":"block"});
			}else{
				responseMessage = 'OOPS !! Something went wrong';
				$("#editrewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_update_failure").css({"display":"block"});
			}
		}else{
			responseMessage = 'OOPS !! Something went wrong';
			$("#editrewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
			$("#rewardsCategoryIn_update_failure").css({"display":"block"});
		}
	}

	function onFailureRewardsCategoryInEditUpdate(responseData){
		responseMessage = 'OOPS !! Something went wrong';
		$("#rewardsCategoryIn_add_success").css({"display":"none"});
		$("#rewardsCategoryIn_add_failure").css({"display":"none"});
		$("#rewardsCategoryIn_update_success").css({"display":"none"});
		$("#rewardsCategoryIn_update_failure").css({"display":"none"});
		$("#rewardsCategoryIn_delete_success").css({"display":"none"});
		$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
		$("#editrewardCategoryInName_err").css({"display":"none"});
		$("#editrewardCategoryInIcon_err").css({"display":"none"});
		$("#editRewardInname").empty();
		$("#editfileIn_attachment").empty();
		$("#rewardCategoryIn_formMsg").empty();

		$("#rewardsCategoryIn_update_failure").css({"display":"block"});
	}

	function makePostFileAjaxRequest(requestURL, requestData, onSuccess, onFailure) 
	{
		$.ajax({
			url : requestURL,
			data : requestData,
			type : 'POST',              
			contentType : false,        
			data : requestData,
			processData : false,
			success : function(response) {
				onSuccess(response);
			},
			error : function(jqXHR, exception) {
				onFailure(jqXHR);
			}
		});
	}

	$(document).ready(function() {
		$("#rewardsCategory_add_success").css({"display":"none"});
		$("#rewardsCategory_add_failure").css({"display":"none"});
		$("#rewardsCategory_update_success").css({"display":"none"});
		$("#rewardsCategory_update_failure").css({"display":"none"});
		$("#rewardsCategory_delete_success").css({"display":"none"});
		$("#rewardsCategory_delete_failure").css({"display":"none"});
		$("#maxCategoryIn_formMsg").css({"display":"none"});
		$("#maxFileSizeIn_formMsg").css({"display":"none"});
		$("#maxFileSize_formMsg").css({"display":"none"});
		$("#rewardCategory_formMsg").empty();
		$("#editrewardCategory_formMsg").empty();
		$("#deleterewardCategory_formMsg").empty();
		$("#rewardsCategoryIn_add_success").css({"display":"none"});
		$("#rewardsCategoryIn_add_failure").css({"display":"none"});
		$("#rewardsCategoryIn_update_success").css({"display":"none"});
		$("#rewardsCategoryIn_update_failure").css({"display":"none"});
		$("#rewardsCategoryIn_delete_success").css({"display":"none"});
		$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
		$("#rewardCategoryIn_formMsg").empty();
		$("#editrewardCategoryIn_formMsg").empty();
		rewardsCategoryInit();
		var rewardClick='';

		$('input:file').change(function() {
			var fileType = $(this).val().split('.').pop().toLowerCase();
		});
	});

	function rewardsCategoryInit(status){
		var token_id = $("#token_id").val();
		makeGetAjaxRequest("mobile/SchoolRewardsList/"+token_id+"/0", false,
				onRewardCategoryWebSuccess, onRewardCategoryWebFailure);
	}

	function onRewardCategoryWebSuccess(data){
		var token_id = $("#token_id").val();
		if (!isDataNull(data) && !isDataNull(data.Return)
				&& !isDataNull(data.Return.Results) ) {
			$('#rewardCategoryWebSection').text('');

			if (!isDataNull(data) && !isDataNull(data.Return.Results.categories_rewards)){
				$('#reward_category_tabs').show();
				var len = data.Return.Results.categories_rewards.length;
				for (var count = 0; count < len; count++) {
					var icglabel_categoryId = "";
					var icglabel_srno= "";
					var icglabel_name= "";
					var icglabel_icon= "";
					var rowImg = "";
					icglabel_categoryId = data.Return.Results.categories_rewards[count].category_id;
					icglabel_srno = count+1;
					icglabel_name = data.Return.Results.categories_rewards[count].category_name;

					var rowData = '<tr>' +
					'<td>' + icglabel_srno + '</td>' +
					'<td>' + icglabel_name + '</td>' +
					'<td><span id="rowimg'+icglabel_srno+'"></span></td>' +
					'<td class="editIcon"><img class="hand" onclick="mergeCategoryDetails($(this))" ' +
					'src="resources/images/unselected_edit_icon.png" data-toggle="modal" data-target="#editCategoryDetails" data-keyboard="true" ' +  
					'category_id="' + icglabel_categoryId + '" category_name="' + icglabel_name + '" /></td> ' +
					'<td class="deleteIcon"><a style="color: black" ' + 
					'data-toggle="modal" data-target="#deleteCategoryDetails" ><img class="hand" onclick="deleteCategoryDetails($(this))" category_id="' + icglabel_categoryId + '" src="resources/images/Delete_icon.png" /></a></td> ' +
					'</tr>';

					$("#rewardCategoryWebSection").append(rowData);

					if(!isDataNull(data.Return.Results.categories_rewards[count].category_icon_url)){
						icglabel_icon = data.Return.Results.categories_rewards[count].category_icon_url;
						rowImg ='<img id="rewardmgtIcon'+icglabel_srno+'" alt="icglabel_icon" height="36" width="36" class="img-responsive img-middle"/>';
						$("#rowimg"+icglabel_srno).append(rowImg);
						var element = "rewardmgtIcon"+icglabel_srno;
						isImageExists(encodeURI(icglabel_icon),element,null);
					}else{
						rowImg ='<div class="text-center h3">n/a</div>';
						$("#rowimg"+icglabel_srno).append(rowImg);
					}
				}
			}else{
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				var rowData = '<tr><td colspan="5" class="box-body text-center"><b>' + nodataText + '</b></td></tr></tbody>';
				$("#rewardCategoryWebSection").append(rowData);
				
				$('#reward_category_tabs').hide();
				$("#rewardWebSection").append(rowData);
			}

			if(!$("ul li a").hasClass("smalltabs")){
				var id =0;
				for (i = 0;i < data.Return.Results.categories_rewards.length; i++ ) {
					var cValue = data.Return.Results.categories_rewards[i];
					id++;
					var rowImg = '';
					if(!isDataNull(cValue.category_icon_url)){
						var category_icon = cValue.category_icon_url;
						rowImg ='<img id="rcatIcon'+cValue.category_id+'" class="img-responsive pull-left img-tabs"/>';
						var rCatElm = "rcatIcon"+cValue.category_id;
					}
					$("div#reward-navbar ul").append(
							'<li><a class="smalltabs hand" id="'+cValue.category_id+'"><p class="smalltabs-ellipse" title="'+cValue.category_name+'">'+cValue.category_name+'</p>' + rowImg + '</a></li>');
					isImageExists(encodeURI(category_icon),rCatElm,"rewardSmallTab");
					if(i == 0)
					{
						$('#'+cValue.category_id+'').addClass("active");
						sessionStorage.setItem("rewardsactiveTab",cValue.category_id);
						makeGetAjaxRequest("mobile/SchoolRewardsList/"+token_id+"/"+cValue.category_id, false, onRewardWebSuccess, onRewardWebFailure);
					}
				}
			}else{
				$("li a.smalltabs").parent().remove();
				var rewardsactiveTab='',rowImg = '';;
				rewardsactiveTab = sessionStorage.getItem("rewardsactiveTab");

				var id =0;
				for (i = 0;i < data.Return.Results.categories_rewards.length; i++ ) {
					var cValue = data.Return.Results.categories_rewards[i];
					id++;
					if(!isDataNull(cValue.category_icon_url)){
						var category_icon = cValue.category_icon_url;
						rowImg ='<img id="rcatIcon'+cValue.category_id+'" class="img-responsive pull-left img-tabs"/>';
						var rCatElm = "rcatIcon"+cValue.category_id;
					}
					$("div#reward-navbar ul").append(
							'<li><a class="smalltabs hand" id="'+cValue.category_id+'"><p class="smalltabs-ellipse" title="'+cValue.category_name+'">'+cValue.category_name+'</p>' + rowImg + '</a></li>');
					isImageExists(encodeURI(category_icon),rCatElm,"rewardSmallTab");
					if(isDataNull(rewardsactiveTab)){
						if(i == 0)
						{
							$('#'+cValue.category_id+'').addClass("active");
							makeGetAjaxRequest("mobile/SchoolRewardsList/"+token_id+"/"+cValue.category_id, false,
									onRewardWebSuccess, onRewardWebFailure);
						}
					}else if(rewardsactiveTab == cValue.category_id)
					{
						$('#'+cValue.category_id+'').addClass("active");
						sessionStorage.setItem("rewardsactiveTab",cValue.category_id);
						makeGetAjaxRequest("mobile/SchoolRewardsList/"+token_id+"/"+cValue.category_id, false,
								onRewardWebSuccess, onRewardWebFailure);
					}
				}
			}
			$(".smalltabs").unbind('click').click(function(){ 
				$("#rewardsCategory_add_success").css({"display":"none"});
				$("#rewardsCategory_add_failure").css({"display":"none"});
				$("#rewardsCategory_update_success").css({"display":"none"});
				$("#rewardsCategory_update_failure").css({"display":"none"});
				$("#rewardsCategory_delete_success").css({"display":"none"});
				$("#rewardsCategory_delete_failure").css({"display":"none"});
				$("#maxFileSizeIn_formMsg").css({"display":"none"});
				$("#maxFileSize_formMsg").css({"display":"none"});
				$("#rewardCategory_formMsg").empty();
				$("#editrewardCategory_formMsg").empty();
				$("#deleterewardCategory_formMsg").empty();
				$("#rewardsCategoryIn_add_success").css({"display":"none"});
				$("#rewardsCategoryIn_add_failure").css({"display":"none"});
				$("#rewardsCategoryIn_update_success").css({"display":"none"});
				$("#rewardsCategoryIn_update_failure").css({"display":"none"});
				$("#rewardsCategoryIn_delete_success").css({"display":"none"});
				$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
				$("#rewardCategoryIn_formMsg").empty();
				$("#editrewardCategoryIn_formMsg").empty();
				$(this).parent().parent().find('.active').removeClass("active");
				$(this).addClass("active");
				var activeTab = $(this).parent().parent().find('.active').attr("id");
				sessionStorage.setItem("rewardsactiveTab",activeTab);
				makeGetAjaxRequest("mobile/SchoolRewardsList/"+token_id+"/"+activeTab, false,
						onRewardWebSuccess, onRewardWebFailure);
			});
		}
		if(!isDataNull(data.Return.Results.categories_rewards.length) && (data.Return.Results.categories_rewards.length >= 8)){
			$("#maxCategoryIn_formMsg").css({"display":"block"});
			$('#addRewardname').attr('disabled', true);
			document.getElementById("file_attachment").disabled = true;
			document.getElementById("rewardCategorysubmit").disabled = true;
		}else{
			$("#maxCategoryIn_formMsg").css({"display":"none"});
			$('#addRewardname').attr('disabled', false);
			document.getElementById("file_attachment").disabled = false;
			document.getElementById("rewardCategorysubmit").disabled = false;
		}
	}

	function onRewardCategoryWebFailure(data){
		console.log('Inside onRewardCategoryWebFailure');
	}

	function onRewardWebSuccess(data){
		if (!isDataNull(data) && !isDataNull(data.Return)
				&& !isDataNull(data.Return.Results)) {
			$('#rewardWebSection').text('');

			var categoryCount = 0;
			if (!isDataNull(data.Return.Results.categories_rewards) &&
					data.Return.Results.categories_rewards[categoryCount].rewards.length > 0){
				var len = data.Return.Results.categories_rewards[categoryCount].rewards.length;
				for (var count = 0; count < len; count++) {
					var icglabel_rewardId;
					var icglabel_srno="";
					var icglabel_name="";
					var icglabel_icon="";
					var imgRowCategory= "";
					icglabel_rewardId = data.Return.Results.categories_rewards[categoryCount].rewards[count].reward_id;
					icglabel_srno = count+1;
					icglabel_name = data.Return.Results.categories_rewards[categoryCount].rewards[count].reward_name;
					var rowData = '<tr>' +
					'<td>' + icglabel_srno + '</td>' +
					'<td>' + icglabel_name + '</td>' +
					'<td><span id="imgRowCategory'+icglabel_srno+'"></span></td>' +
					'<td class="editIcon"><img onclick="mergeCategoryInDetails($(this))" ' +
					'src="resources/images/unselected_edit_icon.png" class="hand" data-toggle="modal" data-target="#editCategoryInDetails" data-keyboard="true" ' +  
					'categoryIn_id="' + icglabel_rewardId + '" categoryIn_name="' + icglabel_name + '" /></td> ' +
					'<td class="deleteIcon"><a style="color: black" ' + 
					'data-toggle="modal" data-target="#deleteCategoryInDetails" ><img class="hand" onclick="deleteCategoryInDetails($(this))" categoryIn_id="' + icglabel_rewardId + '" src="resources/images/Delete_icon.png" /></a></td> ' +
					'</tr>';
					$("#rewardWebSection").append(rowData);

					if(!isDataNull(data.Return.Results.categories_rewards[categoryCount].rewards[count].reward_icon_url)){
						icglabel_icon = data.Return.Results.categories_rewards[categoryCount].rewards[count].reward_icon_url;
						imgRowCategory ='<img id="rewardmgtCategoryIcon'+icglabel_srno+'" alt="icglabel_icon" height="36" width="36"/>';
						$("#imgRowCategory"+icglabel_srno).append(imgRowCategory);
						var element = "rewardmgtCategoryIcon"+icglabel_srno;
						isImageExists(encodeURI(icglabel_icon),element,null);
					}else{
						imgRowCategory ='<div class="text-center h3">n/a</div>';
						$("#imgRowCategory"+icglabel_srno).append(imgRowCategory);
					}
				}
			}else{
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				var rowData = '<tr><td colspan="5" class="box-body text-center"><b>' + nodataText + '</b></td></tr></tbody>';
				$("#rewardWebSection").append(rowData);
			}
		}else{
			console.log("failed section");
		}
	}

	function onRewardWebFailure(data){
		console.log("onRewardWebFailure :::::: responseData >>>>>>> ");
	}

	$(function(){
		$('#editrewardCategory_form').submit(function(e) {
			e.preventDefault();
			var rewardname = '';
			var category_id = '';
			rewardname = $("#editRewardname").val();
			category_id = $("#editcategory_id").val();
			var requestData = new FormData($(this)[0]);
			var file = $(this)[0].editfile_attachment.files[0];
			if(!isDataNull(file)){
				var sFileName = file.name.toLowerCase();
				var sFileExtension = sFileName.split('.')[sFileName.split('.').length - 1].toLowerCase();
				if(!isDataNull(file) && file.size<IconImgMaximumUploadSize && ((sFileExtension === "png") || (sFileExtension === "jpg") || (sFileExtension === "jpeg"))){
						var token_id = $("#token_id").val();
						makePostFileAjaxRequest("mobile/RewardsCategoryEdit/"+token_id+"/"+category_id+"/"+rewardname, requestData, onSuccessRewardsCategoryEditUpdate, onFailureRewardsCategoryEditUpdate);
				}else{
					$("#rewardsCategory_add_success").css({"display":"none"});
					$("#rewardsCategory_add_failure").css({"display":"none"});
					$("#rewardsCategory_update_success").css({"display":"none"});
					$("#rewardsCategory_update_failure").css({"display":"none"});
					$("#rewardsCategory_delete_success").css({"display":"none"});
					$("#rewardsCategory_delete_failure").css({"display":"none"});
					$("#maxFileSize_formMsg").css({"display":"none"});
					var responseMessage = '';
					$("#maxFileSize_formMsg").css({"display":"block"});
					return false;
				}
			}else{
				console.log("no file loaded");
				var token_id = $("#token_id").val();
				makePostFileAjaxRequest("mobile/RewardsCategoryEdit/"+token_id+"/"+category_id+"/"+rewardname, requestData, onSuccessRewardsCategoryEditUpdate, onFailureRewardsCategoryEditUpdate);
			}
		});
	});

	$(function(){
		$('#editrewardCategoryIn_form').submit(function(e) {
			e.preventDefault();
			var rewardname = '';
			var rewardsactiveTab='';
			rewardsactiveTab = sessionStorage.getItem("rewardsactiveTab");
			var category_id = '';
			category_id = rewardsactiveTab;
			var reward_id = '';
			rewardname = $("#editRewardInname").val();
			reward_id = $("#editcategoryIn_id").val();
			var requestData = new FormData($(this)[0]);
			var file = $(this)[0].editfileIn_attachment.files[0];
			if(!isDataNull(file) ){
				var sFileName = file.name.toLowerCase();
				var sFileExtension = sFileName.split('.')[sFileName.split('.').length - 1].toLowerCase();
				if(!isDataNull(file) && file.size<IconImgMaximumUploadSize && ((sFileExtension === "png") || (sFileExtension === "jpg") || (sFileExtension === "jpeg"))){
					var token_id = $("#token_id").val();
					makePostFileAjaxRequest("mobile/RewardsCategoryInEdit/"+token_id+"/"+category_id+"/"+reward_id+"/"+rewardname, requestData, onSuccessRewardsCategoryInEditUpdate, onFailureRewardsCategoryInEditUpdate);
				//}
			}else{
				$("#maxFileSizeIn_formMsg").css({"display":"none"});
				$("#rewardsCategoryIn_add_success").css({"display":"none"});
				$("#rewardsCategoryIn_add_failure").css({"display":"none"});
				$("#rewardsCategoryIn_update_success").css({"display":"none"});
				$("#rewardsCategoryIn_update_failure").css({"display":"none"});
				$("#rewardsCategoryIn_delete_success").css({"display":"none"});
				$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
				var responseMessage = '';
				$("#maxFileSizeIn_formMsg").css({"display":"block"});
				return false;
				
			}
			}else{
				console.log("no file loaded");
				var token_id = $("#token_id").val();
				makePostFileAjaxRequest("mobile/RewardsCategoryInEdit/"+token_id+"/"+category_id+"/"+reward_id+"/"+rewardname, requestData, onSuccessRewardsCategoryInEditUpdate, onFailureRewardsCategoryInEditUpdate);
			}
		});
	});

	$("#rewardCDeleteId").click(function(){  
		var category_id = '';
		category_id = $("#editcategory_id").val();
		var token_id = $("#token_id").val();
		makeGetAjaxRequest("mobile/RewardsCategoryDelete/"+token_id+"/"+category_id, false,
				onDeleteRewardsCategoryDeleteSuccess, onDeleteRewardsCategoryDeleteFailure);
	});


	function onDeleteRewardsCategoryDeleteSuccess(responseData){
		$("#rewardsCategory_add_success").css({"display":"none"});
		$("#rewardsCategory_add_failure").css({"display":"none"});
		$("#rewardsCategory_update_success").css({"display":"none"});
		$("#rewardsCategory_update_failure").css({"display":"none"});
		$("#rewardsCategory_delete_success").css({"display":"none"});
		$("#rewardsCategory_delete_failure").css({"display":"none"});
		$("#maxCategoryIn_formMsg").css({"display":"none"});
		$("#maxFileSizeIn_formMsg").css({"display":"none"});
		$("#maxFileSize_formMsg").css({"display":"none"});
		$("#editrewardCategoryName_err").css({"display":"none"});
		$("#editrewardCategoryIcon_err").css({"display":"none"});
		$("#editRewardname").val("");
		$("#editfile_attachment").val("");
		$("#rewardCategoryName_err").css({"display":"none"});
		$("#rewardCategoryIcon_err").css({"display":"none"});
		$("#addRewardname").val("");
		$("#file_attachment").val("");
		$("#rewardCategory_formMsg").empty();
		$("#editrewardCategory_formMsg").empty();
		$("#deleterewardCategory_formMsg").empty();

		var responseCode = '';
		var responseMessage = '';
		$("#deleterewardCategory_formMsg").empty();
		var token_id = $("#token_id").val();

		if(!isDataNull(responseData) && !isDataNull(responseData.Return) && !isDataNull(responseData.Return.ResponseSummary) && 
				!isDataNull(responseData.Return.ResponseSummary.StatusCode)){
			responseCode = responseData.Return.ResponseSummary.StatusCode;
			if(responseCode === 'SUC01'){
				$("#rewardsCategory_delete_success").css({"display":"block"});
				rewardsCategoryInit("rewardsCategory_delete");
			}else if(responseCode === 'ERR01'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_delete_failure").css({"display":"block"});
			}else if(responseCode === 'ERR02'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_delete_failure").css({"display":"block"});
			}else if(responseCode === 'ERR03'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_delete_failure").css({"display":"block"});
			}else if(responseCode === 'ERR04'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_delete_failure").css({"display":"block"});
			}else if(responseCode === 'ERR19'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_delete_failure").css({"display":"block"});
			}else{
				responseMessage = 'OOPS !! Something went wrong';
				$("#deleterewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategory_delete_failure").css({"display":"block"});
			}
		}else{
			responseMessage = 'OOPS !! Something went wrong';
			$("#deleterewardCategory_formMsg").append(' <b>'+ responseMessage +'</b>.');
			$("#rewardsCategory_delete_failure").css({"display":"block"});
		}
	}

	function onDeleteRewardsCategoryDeleteFailure(responseData){
		$("#rewardsCategory_add_success").css({"display":"none"});
		$("#rewardsCategory_add_failure").css({"display":"none"});
		$("#rewardsCategory_update_success").css({"display":"none"});
		$("#rewardsCategory_update_failure").css({"display":"none"});
		$("#rewardsCategory_delete_success").css({"display":"none"});
		$("#rewardsCategory_delete_failure").css({"display":"none"});

		$("#maxFileSizeIn_formMsg").css({"display":"none"});
		$("#maxFileSize_formMsg").css({"display":"none"});
		$("#editrewardCategoryName_err").css({"display":"none"});
		$("#editrewardCategoryIcon_err").css({"display":"none"});
		$("#editRewardname").val("");
		$("#editfile_attachment").val("");
		$("#rewardCategoryName_err").css({"display":"none"});
		$("#rewardCategoryIcon_err").css({"display":"none"});
		$("#addRewardname").val("");
		$("#file_attachment").val("");
		$("#rewardCategory_formMsg").empty();
		$("#editrewardCategory_formMsg").empty();
		$("#deleterewardCategory_formMsg").empty();
		$("#rewardsCategory_delete_failure").css({"display":"block"});
	}

	$("#rewardCInDeleteId").click(function(){  
		var rewardsactiveTab='';
		rewardsactiveTab = sessionStorage.getItem("rewardsactiveTab");
		var category_id = '';
		category_id = rewardsactiveTab;
		var reward_id = '';
		reward_id = $("#editcategoryIn_id").val();
		var token_id = $("#token_id").val();
		makeGetAjaxRequest("mobile/RewardsCategoryInDelete/"+token_id+"/"+category_id+"/"+reward_id, false,
				onDeleteRewardsCategoryInDeleteSuccess, onDeleteRewardsCategoryInDeleteFailure);
	});

	function onDeleteRewardsCategoryInDeleteSuccess(responseData){
		$("#rewardsCategoryIn_add_success").css({"display":"none"});
		$("#rewardsCategoryIn_add_failure").css({"display":"none"});
		$("#rewardsCategoryIn_update_success").css({"display":"none"});
		$("#rewardsCategoryIn_update_failure").css({"display":"none"});
		$("#rewardsCategoryIn_delete_success").css({"display":"none"});
		$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
		$("#editrewardCategoryInName_err").css({"display":"none"});
		$("#editrewardCategoryInIcon_err").css({"display":"none"});
		$("#editRewardInname").val("");
		$("#editfileIn_attachment").val("");
		$("#rewardCategoryInName_err").css({"display":"none"});
		$("#rewardCategoryInIcon_err").css({"display":"none"});
		$("#addRewardInname").val("");
		$("#fileIn_attachment").val("");
		$("#rewardCategoryIn_formMsg").empty();
		$("#editrewardCategoryIn_formMsg").empty();
		$("#deleterewardCategoryIn_formMsg").empty();

		var responseCode = '';
		var responseMessage = '';
		$("#deleterewardCategoryIn_formMsg").empty();
		var token_id = $("#token_id").val();

		if(!isDataNull(responseData) && !isDataNull(responseData.Return) && !isDataNull(responseData.Return.ResponseSummary) && 
				!isDataNull(responseData.Return.ResponseSummary.StatusCode)){
			responseCode = responseData.Return.ResponseSummary.StatusCode;

			if(responseCode === 'SUC01'){
				$("#rewardsCategoryIn_delete_success").css({"display":"block"});
				rewardsCategoryInit("rewardsCategoryIn_delete");
			}else if(responseCode === 'ERR01'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_delete_failure").css({"display":"block"});
			}else if(responseCode === 'ERR02'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_delete_failure").css({"display":"block"});
			}else if(responseCode === 'ERR03'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_delete_failure").css({"display":"block"});
			}else if(responseCode === 'ERR04'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_delete_failure").css({"display":"block"});
			}else if(responseCode === 'ERR19'){
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#deleterewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_delete_failure").css({"display":"block"});
			}else{
				responseMessage = 'OOPS !! Something went wrong';
				$("#deleterewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
				$("#rewardsCategoryIn_delete_failure").css({"display":"block"});
			}
		}else{
			responseMessage = 'OOPS !! Something went wrong';
			$("#deleterewardCategoryIn_formMsg").append(' <b>'+ responseMessage +'</b>.');
			$("#rewardsCategoryIn_delete_failure").css({"display":"block"});
		}
	}

	function onDeleteRewardsCategoryInDeleteFailure(responseData){
		$("#rewardsCategoryIn_add_success").css({"display":"none"});
		$("#rewardsCategoryIn_add_failure").css({"display":"none"});
		$("#rewardsCategoryIn_update_success").css({"display":"none"});
		$("#rewardsCategoryIn_update_failure").css({"display":"none"});
		$("#rewardsCategoryIn_delete_success").css({"display":"none"});
		$("#rewardsCategoryIn_delete_failure").css({"display":"none"});
		$("#editrewardCategoryInName_err").css({"display":"none"});
		$("#editrewardCategoryInIcon_err").css({"display":"none"});
		$("#editRewardInname").val("");
		$("#editfileIn_attachment").val("");
		$("#rewardCategoryInName_err").css({"display":"none"});
		$("#rewardCategoryInIcon_err").css({"display":"none"});
		$("#addRewardInname").val("");
		$("#fileIn_attachment").val("");
		$("#rewardCategoryIn_formMsg").empty();
		$("#editrewardCategoryIn_formMsg").empty();
		$("#deleterewardCategoryIn_formMsg").empty();
		$("#rewardsCategoryIn_delete_failure").css({"display":"block"});
	}

	$(function(){
		$("#updateCategoryDetails").on(
				"click",
				function(e) {
					var rewardname = '';
					var category_id = '';
					rewardname = $("#editRewardname").val();
					category_id = $("#editcategory_id").val();

					var isValidForm=true;

					$("#editrewardCategoryName_err").css({"display":"none"});
					$("#editrewardCategoryIcon_err").css({"display":"none"});
					if (rewardname == "" || (rewardname.length > 45)) {
						$("#editrewardCategoryName_err").css({"display":"block"});

						e.preventDefault();
						isValidForm = false;
					}

					if(isValidForm){
						document.getElementById('editrewardCategorysubmit').click();
					}else{
						e.preventDefault();
						return false;
					}
				});
	});

	$(function(){
		$("#cancelCategoryDetails").on(
				"click",
				function(e) {
					$("#editrewardCategoryName_err").css({"display":"none"});
					$("#editrewardCategoryIcon_err").css({"display":"none"});
					$("#editRewardname").val("");
					$("#editfile_attachment").val("");
				});
	});

	$(function(){
		$("#updateCategoryInDetails").on(
				"click",
				function(e) {
					var rewardname = '';
					var category_id = '';
					rewardname = $("#editRewardInname").val();

					var isValidForm=true;

					$("#editrewardCategoryInName_err").css({"display":"none"});
					$("#editrewardCategoryInIcon_err").css({"display":"none"});
					if (rewardname == "" || (rewardname.length > 45)) {
						$("#editrewardCategoryInName_err").css({"display":"block"});

						e.preventDefault();
						isValidForm = false;
					}

					if(isValidForm){

						document.getElementById('editrewardCategoryInsubmit').click();
					}else{
						e.preventDefault();
						return false;
					}
				});
	});

	$(function(){
		$("#cancelCategoryInDetails").on(
				"click",
				function(e) {
					$("#editrewardCategoryInName_err").css({"display":"none"});
					$("#editrewardCategoryInIcon_err").css({"display":"none"});
					$("#editRewardInname").val("");
					$("#editfileIn_attachment").val("");
				});
	});
});

function mergeCategoryDetails(usrObj){
	var category_id = usrObj.attr('category_id');
	var category_name = unescape(usrObj.attr('category_name'));
	var category_icon = usrObj.attr('category_icon');
	var token_id = $("#token_id").val();

	$("#editRewardname").val(category_name);
	$("#editcategory_id").val(category_id);
}

function mergeCategoryInDetails(usrObj){
	var categoryIn_id = usrObj.attr('categoryIn_id');
	var categoryIn_name = unescape(usrObj.attr('categoryIn_name'));
	var category_icon = usrObj.attr('category_icon');
	var token_id = $("#token_id").val();

	$("#editRewardInname").val(categoryIn_name);
	$("#editcategoryIn_id").val(categoryIn_id);
}

function deleteCategoryDetails(usrObj){
	var category_id = usrObj.attr('category_id');
	$("#editcategory_id").val(category_id);
}

function deleteCategoryInDetails(usrObj){
	var categoryIn_id = usrObj.attr('categoryIn_id');
	$("#editcategoryIn_id").val(categoryIn_id);
}
