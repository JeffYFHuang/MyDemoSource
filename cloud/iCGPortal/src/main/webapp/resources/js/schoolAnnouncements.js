$('.announcement').removeClass("treeview").addClass("active");
$('.announcement').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$("#sAnnouncementIcon").attr("src","resources/images/sidemenu_icon/white/annoucements_w.png");

	var st_name = sessionStorage.getItem("nick_name");
	$( document ).ready(function() {
	    $("#sname").text(st_name);
	});
	
	$( document ).ready(function() {
		var token_id = $("#token_id").val();
		sessionStorage.setItem("token_id", token_id);
		var student_id = sessionStorage.getItem("student_id");
		if(!isDataNull(token_id)){
			makeGetAjaxRequest("mobile/AnnouncementsList/"+token_id+"/"+student_id , false, 
	    			onSuccessAnnouncements, onFailureAnnouncements);
		}else{
			debugLogs("Oops!! no token found yet, can't load the page content!!");
		}

		$(".collapse").on('shown.bs.collapse', function() {
			$(this).parent().find(".down-navigate").removeClass("down-navigate");
		});
		$(".collapse").on('hidden.bs.collapse', function() {
			$(this).parent().find(".panelup").addClass("down-navigate");
		});
	});
	
	
	function onSuccessAnnouncements(responseData) {
		var announcementId = 0, name = "", start_date = "", description="", announcement="";
	
		if (!isDataNull(responseData) && !isDataNull(responseData.Return) && !isDataNull(responseData.Return.Results) && !isDataNull(responseData.Return.Results.announcements)) {
			for (i in responseData.Return.Results.announcements) {
				announcementId++;
				if (!isDataNull(responseData.Return.Results.announcements[i].name)) {
					name = responseData.Return.Results.announcements[i].name;
				}
				if (!isDataNull(responseData.Return.Results.announcements[i].updatedDate)) {
					start_date = '<font color="#cccccc">Date :' + responseData.Return.Results.announcements[i].updatedDate + '</font>';
				}
				if (!isDataNull(responseData.Return.Results.announcements[i].description)) {
					description = responseData.Return.Results.announcements[i].description;
				}
				
				announcement = '<section class="">' +
						'<div class="panel">' +
							'<div class="panel-heading">' +
								'<div class="panel-title">' +
									'<div class="row">' +
										'<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">' +
											'<span class="third_subtitle" style="float: left;">' + name + '</span>' +
											'<div class="pull-right">' +
												'<span class="header-icons">' +
												'<a style="color: black" href="#announcementPanel_'+ announcementId +'" data-toggle="collapse">' +
													'<img class="panelup" src="resources/images/arrow_up.png" />' +
														'</a></span>' +
											'</div>' +
										'</div>' +
										'<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 txt-12">' + start_date + '</div>' +
									'</div><hr class="panelhr">' +
								'</div>' +
							'</div>' +
							'<div class="panel-body collapse in" id="announcementPanel_'+ announcementId +'">' +
								'<div class="panelContent"><pre class="panel-pre font-arial">' + description + '</pre></div>' +
							'</div>' +
						'</div>' +
					'</section>';
						
				$('#announcements').append(announcement);
				name = ""; start_date = ""; description = "";
			}
		}else{
			announcement = '<div class="box-body font-light-roboto txt-14"> <b> No Announcements Available </b> </div>';
			$('#announcements').append(announcement);
		}
	}

	function onFailureAnnouncements(responseData) {
		debugLogs("responseData" + responseData.status);
	}