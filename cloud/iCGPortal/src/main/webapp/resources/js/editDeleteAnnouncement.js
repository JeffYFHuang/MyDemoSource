function mergeAnnouncementDetails(usrObj) {
	var announcement_name = usrObj.attr('announcement_name');
	var announcement_desc = usrObj.attr('announcement_desc');
	var announcement_id = usrObj.attr('announcement_id');

	$("#editAnntName").val(announcement_name);
	$("#editAnntDesc").val(announcement_desc);
	$("#ann_id").val(announcement_id);
}

function mergeAnnouncementDetailsForDelete(usrObj) {
	var announcement_id = usrObj.attr('announcement_id');
	$("#hiddenAnnouncementId").val(announcement_id);
}

$(function() {
	$(".collapse").on('shown.bs.collapse', function() {
		$(this).parent().find(".down-navigate").removeClass("down-navigate");
	});
	$(".collapse").on('hidden.bs.collapse', function() {
		$(this).parent().find(".panelup").addClass("down-navigate");
	});

	$("#popupCancelButton").click(function() {
		$("#editAnntName_err").css("display", "none");
		$("#editAnntName_err2").css("display", "none");
	});

	$("#updatebutton").click(
			function(e) {
				debugLogs('Into updateAnnouncementDetails() ');
				var announcement_name = $("#editAnntName").val();
				var announcement_desc = $("#editAnntDesc").val();

				$("#editAnntName_err").css("display", "none");
				$("#editAnntName_err2").css("display", "none");

				if (announcement_name.trim() === ''
						|| announcement_name.trim().length === 0) {
					$("#editAnntName_err").css("display", "block");
					$("#editAnntName_err2").css("display", "none");
					e.preventDefault();
					return false;

				}
				if (announcement_desc.trim() === ''
						|| announcement_desc.trim().length === 0) {
					$("#editAnntName_err").css("display", "none");
					$("#editAnntName_err2").css("display", "block");
					e.preventDefault();
					return false;

				} else {
					var announcement_id = $("#ann_id").val();
					var token_id = $("#token_id").val();
					var requestData = {
						"name" : announcement_name,
						"description" : announcement_desc,
						"announcementId" : announcement_id
					};

					makePostAjaxRequest("updateAnnouncement/" + token_id,
							requestData, onSuccessAnnouncementUpdate,
							onFailureAnnouncementUpdate);
				}

				$("#editAnntName_err").css("display", "none");
				$("#editAnntName_err2").css("display", "none");
			});

	function onSuccessAnnouncementUpdate() {
		window.location.reload();
	}

	function onFailureAnnouncementUpdate() {
		debugLogs("error occurred during announcement update ::::::");
	}
});

$(document)
		.ready(
				function() {
					$('.announcement').removeClass("treeview").addClass(
							"active");
					$('.announcement').removeClass("font-medium-roboto").addClass("font-bold-roboto");
					$("#sAnnouncementIcon")
							.attr("src",
									"resources/images/SchoolAdmin_sideBarIcons/White/announcement.png");
					$("#schoolAnnouncementTitle_err").css({
						"display" : "none"
					});
					$("#schoolAnnouncementDescription_err").css({
						"display" : "none"
					});
					
					$("#schoolAnnouncementsSave").click(
							function() {
								showSpinner();
								var isValidForm = true;
								var title = $("#schoolAnnouncementTitle").val()
										.trim();
								var desc = $("#schoolAnnouncementDescription")
										.val().trim();
								$("#schoolAnnouncementDescription_err").hide();
								$("#schoolAnnouncementTitle_err").hide();

								if (title.length <= 0) {
									$("#schoolAnnouncementTitle_err").show();
									isValidForm = false;
								}
								if (desc.length <= 0) {
									$("#schoolAnnouncementDescription_err")
											.show();
									isValidForm = false;
								} 
								
								if(isValidForm){
									var token_id = $("#token_id").val();
									var requestData = {
										"name" : title,
										"description" : desc
									};

									makePostAjaxRequest("addAnnouncement/"
											+ token_id, requestData,
											onSuccessAddAnnouncement,
											onFailureAddAnnouncement);
								}else{
									hideSpinnerNow();
								}
							});

					$("#schoolAnnouncementsCancel").click(function() {
						$("#schoolAnnouncementTitle").val("");
						$("#schoolAnnouncementDescription").val("");
					});

					$(document)
							.on(
									"keypress",
									'#editAnntDesc, #schoolAnnouncementDescription',
									function(event) {
										var ignore = [ 8, 9, 13, 33, 34, 35,
												36, 37, 38, 39, 40, 46, 69 ], $this = $(this), maxlength = $this
												.attr('maxlength'), code = event.keyCode
												|| event.which;
										// check if maxlength has a value.
										// The value must be greater than 0
										if (maxlength && maxlength > 0) {
											if ($.inArray(code, ignore))
												return ($this.val().length < maxlength || $
														.inArray(code, ignore) !== -1)
										}
									})
					$(document).on(
							"paste",
							'textarea[maxlength]',
							function() {
								var $this = $(this), maxlength = $this
										.attr('maxlength')
								setTimeout(function() {
									$this.val($this.val().substr(0, maxlength))
								}, 100)
							})

				});

function onSuccessAddAnnouncement(responseData) {
	window.location.reload();
}

function onFailureAddAnnouncement(responseData) {
	debugLogs("Inside onFailureAddAnnouncement")
}

function deleteAnnouncement() {
	var hiddenAnnouncementId = $("#hiddenAnnouncementId").val();
	var requestData = {
		"announcementId" : hiddenAnnouncementId
	};
	makePostAjaxRequest("deleteAnnouncement", requestData,
			onSuccessAnnouncementDelete, onFailureAnnouncementDelete);
}

function onSuccessAnnouncementDelete() {
	window.location.reload();
}

function onFailureAnnouncementDelete() {
	debugLogs("error occurred during announcement delete ::::::onFailureAnnouncementDelete");
}
