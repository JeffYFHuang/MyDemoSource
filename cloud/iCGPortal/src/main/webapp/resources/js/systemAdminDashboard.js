var token_id = $("#token_id").val();

$(document)
		.ready(
				function() {
					$('.servicefunctionstatusView').removeClass("treeview").addClass(
							"active");
					$("#servicefunctionstatusIcon")
							.attr("src",
									"resources/images/sidemenu_icon/white/service_funtion_status.png");
					
				});