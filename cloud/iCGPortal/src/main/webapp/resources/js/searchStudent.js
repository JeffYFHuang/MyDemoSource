$(function(){
	$('.searchEditbtn').click(function () {
		var crctName,crctContact = false;
		var currentStudTD = $(this).parents('tr').find('td');
		if ($(this).html() == '<img src="../resources/images/unselected_edit_icon.png">') {
			currentStudTD = $(this).parents('tr').find('td');
			$.each(currentStudTD, function (val) {
				if(currentStudTD[val].className == "sregno" || currentStudTD[val].className == "suuid" || currentStudTD[val].className == "seditBtn" ||currentStudTD[val].className == "sdeleteBtn"){
					
				}else{
					$(this).find(".noneditable").css({"display":"none"});
					$(this).find(".editable").css({"display":"block"});
				}
			});
		}  else {
			var crctName=false;
			var crctContact = false;
			if(hasNumber($("#editedSearchsName_1").val())){
				crctName = true;
				$("#editedSearchsName_1err").css({"display" :"block"});
			}
			if(contactStartCheck($("#editedSearchsContact_1").val())){
				crctContact = true;
				$("#editedSearchsContact_1err").css({"display" :"block"});
			}
			if(!crctName && !crctContact){
			$.each(currentStudTD, function (val) {
				if(!currentStudTD[val].className == "sregno" || currentStudTD[val].className == "srollno" || currentStudTD[val].className == "suuid" ||currentStudTD[val].className == "sedit" || currentStudTD[val].className == "sdelete"){
					
				}else{
					$(this).find(".editable").css({"display":"none"});
					$("#editedSearchsContact_1err").css({"display" :"none"});
					$("#editedSearchsName_1err").css({"display" :"none"});
					$(this).find(".noneditable").text($(this).find(".editable").val());
					$(this).find(".noneditable").css({"display":"block"});
				}
			});
		}else{
			$.each(currentStudTD, function (val) {
				$(this).find(".noneditable").css({"display":"none"});
				$(this).find(".editable").css({"display":"block"});
				
			});
			$(this).html('<img src="../resources/images/unselected_edit_icon.png">');
		}
	}

		$(this).html($(this).html() == '<img src="../resources/images/unselected_edit_icon.png">' ? '<img src="../resources/images/selected_edit_icon.png">' : '<img src="../resources/images/unselected_edit_icon.png">')

	});
});

