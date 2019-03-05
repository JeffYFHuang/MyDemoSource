$('.reminders').removeClass("treeview").addClass("active");
$('.reminders').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$("#sRemindersIcon").attr("src",
		"resources/images/sidemenu_icon/white/reminders_w.png");
var st_name = sessionStorage.getItem("nick_name");
$(document).ready(function() {
	$("#sname").text(st_name);
	
	$("#datepicker").attr('readonly', 'readonly');
	$('#datepicker').datepicker({
		dateFormat : 'YYYY-MM-DD',
		todayHighlight	: true,
		autoclose		: true,
		pickerPosition	: "bottom-left",
		orientation 	: "bottom-right",
		maxDate 		: '+0d'
	}).on('show', function(e){    
		if($(".datepicker").hasClass("datepicker-orient-bottom")){
			$(".datepicker").removeClass('datepicker-orient-bottom').addClass("datepicker-orient-top");
		}
		$(".datepicker").addClass("reminders-datepicker");
	}).datepicker('setDate', 'today');;
	$(".reminder-data").trigger('click');
});

	//Date picker
	$(".reminder-data").click(function(){
		showSpinner();
		var inputDate = $('#datepicker').val();
		var student_id = sessionStorage.getItem("student_id");
		var token_id = $("#token_id").val();
		var device_uuid = sessionStorage.getItem("device_uuid");

		if (!isDataNull(token_id)) {
				makeGetAjaxRequest(
						"mobile/kidRemindersList/"+token_id+"/"+student_id+"/"+inputDate, false,
						onSuccessReminders, onFailureReminders);
			} else {
				hideSpinnerNow();
				debugLogs("Oops!! no token found yet, can't load the page content!!");
			}
		return false;
	})

	function onSuccessReminders(responseData) {
		var stclass = "", reminder_image = "", reminder_comments = "", created_date = "", reminder = "";
		$('#reminders').text(reminder);
		
		if (!isDataNull(responseData) && !isDataNull(responseData.Return)
				&& !isDataNull(responseData.Return.Results)
				&& !isDataNull(responseData.Return.Results.Reminders)
				) {		
			
		for (var count = 0; count < responseData.Return.Results.Reminders.length; count++) {
			stclass = responseData.Return.Results.Reminders[count].class			
			reminder_comments = responseData.Return.Results.Reminders[count].comments;			
			reminder_image = responseData.Return.Results.Reminders[count].filename;
			created_date = responseData.Return.Results.Reminders[count].created_date;	
			
			if(!isDataNull(reminder_image)){
				var imgObj = new Image();
				imgObj.src = reminder_image;
				imgObj.onerror=imageNotFound;

				if (imgObj.complete) {
					reminder = '<div class="row"><div class="col-md-12 col-lg-12 col-sm-12 col-xs-12 third_subtitle reminder-description">' + (count+1) + '. <img src="'+ reminder_image +'" height="24" width="24">' + reminder_comments + '</div></div>'
					
				} else {
					reminder = '<div class="row"><div class="col-md-12 col-lg-12 col-sm-12 col-xs-12 third_subtitle reminder-description">' + (count+1) + '. ' + reminder_comments + '</div></div>';
				}
			} else {
				reminder = '<div class="row"><div class="col-md-12 col-lg-12 col-sm-12 col-xs-12 third_subtitle reminder-description">' + (count+1) + '. ' + reminder_comments + '</div></div>';
			}

			$('#reminders').append(reminder);
			}
		} else {
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			reminder = '<div class="box-body font-light-roboto txt-14"> <b>' + nodataText + '</b> </div>';
			$('#reminders').append(reminder);
		}
	}

	function onFailureReminders(responseData) {
		debugLogs("onFailureReminders:" + responseData.status);
	}
	
	function imageNotFound(){
		debugLogs("Reminders Image Not found");
	}