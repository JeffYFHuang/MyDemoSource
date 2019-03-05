$('.rewards').removeClass("treeview").addClass("active");
$('.rewards').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$("#sRewardsIcon").attr("src","resources/images/sidemenu_icon/white/Rewards_white.png");

var st_name = sessionStorage.getItem("nick_name");
$(document).ready(function() {
	var d = new Date();
	var month = d.getMonth()+1;
	var day = d.getDate();
	var output = d.getFullYear() + '-' +
	(month<10 ? '0' : '') + month + '-' +
	(day<10 ? '0' : '') + day;
	$(".dateInput").val(output);

	/*Parent Reward Datepicker*/
	var date_input =$('input[name="parentRewardCurrentDate"]');
	var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
	var options={
			container: container,
	};

	date_input.datepicker(options);
	$("#parentRewardCurrentDate").datepicker({
		maxDate: 0,
		format: 'yyyy-mm-dd',
		todayHighlight	: true,
		todayBtn		: true,
		autoclose		: true,
		pickerPosition	: "bottom-left",
		orientation 	: "bottom-right"
	}).on('show', function(e){    
		if($(".datepicker").hasClass("datepicker-orient-bottom")){
			$(".datepicker").removeClass('datepicker-orient-bottom').addClass("datepicker-orient-top");
		}else{
			$(".datepicker").removeClass('datepicker-orient-top').addClass("datepicker-orient-bottom");
		}
	});
	/*End of Parent Reward Datepicker*/
	$("#sname").text(st_name);
	$("#datepicker").attr('readonly', 'readonly');

	var student_id = sessionStorage.getItem("student_id");
	var token_id = $("#token_id").val();
	var device_uuid = sessionStorage.getItem("device_uuid");
	init();
	function init(){
		console.log("when loading reward");

		if(!isDataNull(token_id)){
			makeGetAjaxRequest("web/StudentRewards/"+token_id+"/"+student_id,
					false,onSuccessStudentRewards,onFailureStudentRewards);
		}else{
			debugLogs("Oops!! no token found yet, can't load the page content!!");
		}
		return false;
	}
	function onSuccessStudentRewards(data){
		console.log(data);
		obj = jsonSort(data, "category_name", DATATYPE.STRING, SORTORDER.ASC, null); 
		console.log(data);
		$("#parentRewardList div").remove();
		var parentRewardList ='',total_reward_count=0;
		if (!isDataNull(data.Return.Results.rewards)) {
			var reward_id="", category_name="", category_icon_url="", reward_name="", reward_count="", reward_icon_url="", total_category_reward_count=0, check_category="", rewards_category_id="";
			obj = jsonSort(data.Return.Results.rewards, "category_name", DATATYPE.STRING, SORTORDER.ASC, null); 
			for (i in data.Return.Results.rewards){
				if(!isDataNull(data.Return.Results.rewards[i].rewards_category_id)) {
					rewards_category_id = data.Return.Results.rewards[i].rewards_category_id;
				}
				if(!isDataNull(data.Return.Results.rewards[i].reward_id)) {
					reward_id = data.Return.Results.rewards[i].reward_id;
				}
				if(!isDataNull(data.Return.Results.rewards[i].category_name)) {
					category_name = data.Return.Results.rewards[i].category_name;
				}
				if(!isDataNull(data.Return.Results.rewards[i].reward_name)) {
					reward_name = data.Return.Results.rewards[i].reward_name;
				}
				if(check_category == rewards_category_id){
					if(!isDataNull(data.Return.Results.rewards[i].reward_count)) {
						reward_count = data.Return.Results.rewards[i].reward_count;
						total_category_reward_count += reward_count;
						total_reward_count += reward_count;
					}
					var  parentCategoryRewardType ='<div class="safety-padding safety-gap">'+
					'<div class="safetyCol rewardType bord-section">'+
					'<div id="parentRewardTypeIcon'+reward_id+'" class="pull-left"></div>'+
					'<div class="pull-right text-center substats-reward"><span class="notification-header txt-36">'+reward_count+'</span><br/>'+
					'<span class="notification-text txt-18">'+reward_name+'</span></div>'+
					'</div>'+
					'</div>';
					$("#category"+rewards_category_id).append(parentCategoryRewardType);
					$("#totalCatRewardCount"+rewards_category_id).text(total_category_reward_count);
					addRewardTypeIcon(data.Return.Results.rewards[i].reward_icon_url,reward_id);
				}else{
					check_category = rewards_category_id;
					if(!isDataNull(data.Return.Results.rewards[i].reward_count)) {
						reward_count = data.Return.Results.rewards[i].reward_count;
						total_category_reward_count = reward_count;
						total_reward_count += reward_count;
					}
					parentRewardList ='<div class="rewards-section" id="category'+rewards_category_id+'">'+
					'<div class="safety-padding safety-gap">'+
					'<div class="safetyCol rewardsCategory bord-section">'+
					'<div class="reward-icons">'+
					'<p class="text-center reward-padding notification-header">'+category_name+'</p>'+
					'<div id="parentRewardIcon'+category_name+'"></div>'+
					'<p class="text-center reward-padding txt-48" id="totalCatRewardCount'+rewards_category_id+'">'+total_category_reward_count+'</p>'+
					'</div>'+
					'</div>'+
					'</div>'+
					'<div class="safety-padding safety-gap">'+
					'<div class="safetyCol rewardType bord-section">'+
					'<div id="parentRewardTypeIcon'+reward_id+'" class="pull-left"></div>'+
					'<div class="pull-right text-center substats-reward"><span class="notification-header txt-36">'+reward_count+'</span><br/>'+
					'<span class="notification-text txt-18">'+reward_name+'</span></div>'+
					'</div>'+
					'</div>'+
					'</div>';
					$("#parentRewardList").append(parentRewardList);

					if(!isDataNull(data.Return.Results.rewards[i].category_icon_url)) {
						category_icon_url = data.Return.Results.rewards[i].category_icon_url;
						var category_icon_urlifexits ='<span class="reward-padding over h3"><img '+
						'id="categoryimg'+rewards_category_id+'" class="img-responsive" height="75" width="75"></span>';
						$("#parentRewardIcon"+category_name).append(category_icon_urlifexits);
						var element = "categoryimg"+rewards_category_id;
						isImageExists(encodeURI(category_icon_url),element,null);
					}else{
						var category_icon_urlifnotexits ='<div class="text-center h3 img-notexits">n/a</div>';
						$("#parentRewardIcon"+category_name).append(category_icon_urlifnotexits);
					}
					addRewardTypeIcon(data.Return.Results.rewards[i].reward_icon_url,reward_id);
				}
			}
		}/*else{
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			parentRewardList = '<div class="box-body"><b>' + nodataText + '</b> </div>';
			$('#parentRewardList').append(parentRewardList);
		}*/
		$("#totalRewardCount").text((total_reward_count)?total_reward_count:'n/a');
		
		$("#findbasedOnDateforParentReward").trigger('click');
	}
	function onFailureStudentRewards(data){
		console.log(data);
		$("#findbasedOnDateforParentReward").trigger('click');
	}
	function addRewardTypeIcon(reward_icon_url,reward_id){
		if(!isDataNull(reward_icon_url)) {
			var reward_icon_urllifexits ='<img height="75" width="75"'+
			'class="img-responsive pull-left" id="rewardimg'+reward_id+'" />';
			$("#parentRewardTypeIcon"+reward_id).append(reward_icon_urllifexits);
			var element = "rewardimg"+reward_id;
			isImageExists(encodeURI(reward_icon_url),element,null);
		}else{
			var reward_icon_urlifnotexits ='<div class="h3 reward-imgntfound pull-left">n/a</div>';
			$("#parentRewardTypeIcon"+reward_id).append(reward_icon_urlifnotexits);
		}
	}

	$("#findbasedOnDateforParentReward").click(function(){
		showSpinner();
		var inputDate = $('#parentRewardCurrentDate').val(); //dbDateFormat($('#datepicker').val());

		console.log('inputDate'+inputDate);
		if(!isDataNull(token_id)){
			makeGetAjaxRequest("mobile/StudentRewardsByDate/"+token_id+"/"+student_id+"/"+inputDate , false, 
					onSuccessRewards, onFailureRewards);
		}else{
			hideSpinnerNow();
			debugLogs("Oops!! no token found yet, can't load the page content!!");
		}
		return false;
	});
	function onSuccessRewards(responseData) {
		$("#parentRewardOftheDayList tr").remove();
		var parentRewardOftheDay = '';
		var total_reward_count = 0, reward_name = "", reward_description = '', category_name = "",reward_date ="", category_icon_url= "", received_count="", comments="",rewards= "",teacher="";
		if (!isDataNull(responseData.Return.Results.Rewards)) {
			obj = jsonSort(responseData.Return.Results.Rewards, "category_name", DATATYPE.STRING, SORTORDER.ASC, null); 
			var reward_id = 0;
			for (i in responseData.Return.Results.Rewards){
				reward_id++;
				if(!isDataNull(responseData.Return.Results.Rewards[i].category_name)) {
					category_name = responseData.Return.Results.Rewards[i].category_name;
				}
				if(!isDataNull(responseData.Return.Results.Rewards[i].reward_name)) {
					reward_name = responseData.Return.Results.Rewards[i].reward_name;
				}
				if(!isDataNull(responseData.Return.Results.Rewards[i].received_count)) {
					received_count = responseData.Return.Results.Rewards[i].received_count;
					total_reward_count += received_count;
				}
				if(!isDataNull(responseData.Return.Results.Rewards[i].teacher)) {
					teacher = responseData.Return.Results.Rewards[i].teacher;
				}
				if(!isDataNull(responseData.Return.Results.Rewards[i].reward_date)) {
					reward_date = responseData.Return.Results.Rewards[i].reward_date;
				}
				var date = new Date(reward_date);
				var h = date.getHours();
				var m = date.getMinutes();
				var time =  ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2);

				parentRewardOftheDay = '<tr>'+
				'<td>'+reward_id+'</td>'+
				'<td>'+category_name+'</td>'+
				'<td>'+reward_name+'</td>'+
				'<td>'+received_count+'</td>'+
				'<td>'+teacher+'</td>'+
				'<td>'+time+'</td>'+
				'</tr>';
				$("#parentRewardOftheDayList").append(parentRewardOftheDay);
			}
		}else{
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			parentRewardOftheDay = '<tr><td colspan="6" class="box-body text-center"><b>' + nodataText + '</b></td></tr></tbody>';
			$("#parentRewardOftheDayList").append(parentRewardOftheDay);
		}
	}

	function onFailureRewards(responseData) {
		debugLogs("responseData" + responseData.status);
	}
});