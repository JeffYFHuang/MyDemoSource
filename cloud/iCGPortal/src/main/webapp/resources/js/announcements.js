$(function(){
	$("#schoolAnnouncementsSave").click(function(){ 

		if($("#schoolAnnouncementTitle").val() == ""){
			$("#schoolAnnouncementTitle_err").css({"display":"block"});
		}else{
			$("#schoolAnnouncementTitle_err").css({"display":"none"});
		}
		if($("textarea#schoolAnnouncementDescription").val() == " "){
			$("#schoolAnnouncementDescription_err").css({"display":"block"});
		}else {
			$("#schoolAnnouncementDescription_err").css({"display":"none"});
		}
	});

	$(".collapse").on('shown.bs.collapse', function(){
		$(this).parent().find(".down-navigate").removeClass("down-navigate");
	});
	$(".collapse").on('hidden.bs.collapse', function(){
		$(this).parent().find(".panelup").addClass("down-navigate");
	});

	$('.announcementEditBtn').click(function () {
		var currentPanel = $(this).parents().find('.panelContent');
		if ($(this).html() == '<img src="../resources/images/unselected_edit_icon.png">') {
			currentPanel = $(this).parents().find('.panelContent');
			if(currentPanel[0].className == "panelContent" ){
				$(this).parents().find('.noneditable').css({"display":"none"});
				$(this).parents().find(".editabletextarea").val($(this).parents().find(".noneditable").text());
				$(this).parents().find('.editabletextarea').css({"display":"block"});
			}
		} else {
			if(currentPanel[0].className == "panelContent" ){
				$(this).parents().find(".editabletextarea").css({"display":"none"});
				$(this).parents().find(".noneditable").text($(this).parents().find(".editabletextarea").val());
				$(this).parents().find(".noneditable").css({"display":"block"});
			}
		}

		$(this).html($(this).html() == '<img src="../resources/images/unselected_edit_icon.png">' ? '<img src="../resources/images/selected_edit_icon.png">' : '<img src="../resources/images/unselected_edit_icon.png">')


	});

});

