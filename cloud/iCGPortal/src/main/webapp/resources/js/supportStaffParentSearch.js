$(function() {
	var token_id = $("#token_id").val();

	$('.supportstaff_parent_search').removeClass("treeview").addClass("active");
	$('.supportstaff_parent_search').removeClass("font-medium-roboto").addClass("font-bold-roboto");
	$("#supportStaffParentSearchIcon").attr("src",
			"resources/images/sidemenu_icon/white/search_parent.png");

	function loadStudentSearchResults() {
		var page_id = 1;
		var profileName = $("#searchParentProfileName").val().trim();
		var contactNo = $("#searchParentContact").val().trim();
		var emailId = $("#searchParentUsername").val().trim();
		var uuid = $("#searchParentUuid").val().trim();

		profileName = (profileName.length <= 0) ? 0 : profileName;
		contactNo = (contactNo.length <= 0) ? 0 : contactNo;
		emailId = (emailId.length <= 0) ? 0 : emailId;
		uuid = (uuid.length <= 0) ? 0 : uuid;

		if (profileName == 0 && contactNo == 0 && emailId == 0 && uuid == 0) {
			debugLogs('At least one search criteria must be entered');
			$("#searchCriteriaError").css({
				"display" : "block"
			});
			$("#stDetails").empty();
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			var class_students = '<tr class="box-body"><td colspan="7" align="center"><b>'+ nodataText +'<b></td> </tr>';
			$("#stDetails").append(class_students);
		} else {
			profileName = encodeURIComponent(profileName);
			profileName = profileName = profileName.replace(/%25/g, '%2525');
			makeGetAjaxRequest("SearchParentAccountManagement/" + token_id
					+ "/" + profileName + "/" + contactNo + "/" + emailId + "/"
					+ uuid + "/" + page_id, true, onSearchSuccess,
					onSearchFailure);
		}
	}

	$("#searchParentSearch").click(function() {
		loadStudentSearchResults();
		return false;
	});

	function onSearchFailure(data) {
		if (isDataNull(data)) {
			var class_students = '<tr class="box-body"><td colspan="9" align="center"><b class="icglabel_nodata"></b></td> </tr>';
			$("#stDetails").append(class_students);
		}
	}

	function onSearchSuccess(data) {
		if (!isDataNull(data) && !isDataNull(data.Return)
				&& !isDataNull(data.Return.Results)) {
			$("#searchResultView").css({
				"display" : "block"
			});
			$("#stDetails").empty();

			if (!isDataNull(data.Return.Results.parents)) {
				var len = data.Return.Results.parents.length;
				if (len > 0) {
					$("#pagination").css({
						"display" : "block"
					});
				}
				schoolend = data.Return.Results.noofPages;
				schoolstart = data.Return.Results.currentPage;
				for (var count = 0; count < len; count++) {
					var icglabel_user_id;
					var icglabel_profileName = "";
					var icglabel_emailId = "";
					var icglabel_contactNo = "";
					var icglabel_userType = "";
					var icglabel_userSource = "";
					var icglabel_device_uuid = "";
					var icglabel_lastlogin = "";
					icglabel_user_id = data.Return.Results.parents[count].user_id;
					icglabel_profileName = data.Return.Results.parents[count].profileName;
					icglabel_emailId = data.Return.Results.parents[count].emailId;
					icglabel_contactNo = data.Return.Results.parents[count].contactNo;
					icglabel_userType = data.Return.Results.parents[count].userType;
					icglabel_userSource = data.Return.Results.parents[count].userSource;
					icglabel_device_uuid = data.Return.Results.parents[count].device_uuid;
					icglabel_lastlogin = data.Return.Results.parents[count].lastlogin;
					if (isDataNull(icglabel_lastlogin)) {
						icglabel_lastlogin = '-';
					}
					var rowData = '<tr>' + '<td>' + icglabel_profileName
							+ '</td>' + '<td>' + icglabel_emailId + '</td>'
							+ '<td>' + icglabel_contactNo + '</td>' + '<td>'
							+ icglabel_userType + '</td>' + '<td>'
							+ icglabel_userSource + '</td>' + '<td>'
							+ icglabel_device_uuid + '</td>' + '<td>'
							+ icglabel_lastlogin + '</td>' + '</tr>';

					document.getElementById('school_startPage').innerHTML = schoolstart;
					document.getElementById('school_endPage').innerHTML = schoolend;

					$("#stDetails").append(rowData);
				}
			} else {
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				var rowData = '<tr><td colspan="9" class="box-body text-center"><b>' + nodataText + '</b></td></tr></tbody>';
				$("#stDetails").append(rowData);
			}
		} else {
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			var rowData = '<tr><td colspan="9" class="box-body text-center"><b>' + nodataText + '</b></td></tr></tbody>';
			$("#stDetails").append(rowData);
		}
	}

	$("#increaseassigendHref").click(
			function() {
				var currentpage = schoolstart;
				currentpage++;
				if (currentpage <= schoolend) {
					schoolstart = currentpage;
					var profileName = $("#searchParentProfileName").val()
							.trim();
					var contactNo = $("#searchParentContact").val().trim();
					var emailId = $("#searchParentUsername").val().trim();
					var uuid = $("#searchParentUuid").val().trim();

					profileName = (profileName.length <= 0) ? 0 : profileName;
					contactNo = (contactNo.length <= 0) ? 0 : contactNo;
					emailId = (emailId.length <= 0) ? 0 : emailId;
					uuid = (uuid.length <= 0) ? 0 : uuid;
					profileName = encodeURIComponent(profileName);
					profileName = profileName = profileName.replace(/%25/g, '%2525');
					makeGetAjaxRequest("SearchParentAccountManagement/"
							+ token_id + "/" + profileName + "/" + contactNo
							+ "/" + emailId + "/" + uuid + "/" + currentpage,
							true, onSearchSuccess, onSearchFailure);
				}
			});

	$("#decreaseassigendHref").click(
			function() {
				var currentpage = schoolstart;
				currentpage--;
				if (currentpage > 0) {
					schoolstart = currentpage;
					var profileName = $("#searchParentProfileName").val()
							.trim();
					var contactNo = $("#searchParentContact").val().trim();
					var emailId = $("#searchParentUsername").val().trim();
					var uuid = $("#searchParentUuid").val().trim();

					profileName = (profileName.length <= 0) ? 0 : profileName;
					contactNo = (contactNo.length <= 0) ? 0 : contactNo;
					emailId = (emailId.length <= 0) ? 0 : emailId;
					uuid = (uuid.length <= 0) ? 0 : uuid;
					profileName = encodeURIComponent(profileName);
					profileName = profileName.replace(/%25/g, '%2525');
					makeGetAjaxRequest("SearchParentAccountManagement/"
							+ token_id + "/" + profileName + "/" + contactNo
							+ "/" + emailId + "/" + uuid + "/" + currentpage,
							true, onSearchSuccess, onSearchFailure);
				}
			});
});

function increaseassigendHref() {
	var currentpage = schoolstart;
	currentpage++;
	if (currentpage <= schoolend) {
		schoolstart = currentpage;
		var profileName = $("#searchParentProfileName").val().trim();
		var contactNo = $("#searchParentContact").val().trim();
		var emailId = $("#searchParentUsername").val().trim();
		var uuid = $("#searchParentUuid").val().trim();

		profileName = (profileName.length <= 0) ? 0 : profileName;
		contactNo = (contactNo.length <= 0) ? 0 : contactNo;
		emailId = (emailId.length <= 0) ? 0 : emailId;
		uuid = (uuid.length <= 0) ? 0 : uuid;
		profileName = encodeURIComponent(profileName);
		profileName = profileName.replace(/%25/g, '%2525');
		makeGetAjaxRequest("SearchParentAccountManagement/" + token_id + "/"
				+ profileName + "/" + contactNo + "/" + emailId + "/" + uuid
				+ "/" + currentpage, true, onSearchSuccess, onSearchFailure);
	}
}

function decreaseassigendHref() {
	var currentpage = schoolstart;
	currentpage--;
	if (currentpage > 0) {
		schoolstart = currentpage;
		var profileName = $("#searchParentProfileName").val().trim();
		var contactNo = $("#searchParentContact").val().trim();
		var emailId = $("#searchParentUsername").val().trim();
		var uuid = $("#searchParentUuid").val().trim();

		profileName = (profileName.length <= 0) ? 0 : profileName;
		contactNo = (contactNo.length <= 0) ? 0 : contactNo;
		emailId = (emailId.length <= 0) ? 0 : emailId;
		uuid = (uuid.length <= 0) ? 0 : uuid;
		profileName = encodeURIComponent(profileName);
		profileName = profileName.replace(/%25/g, '%2525');
		makeGetAjaxRequest("SearchParentAccountManagement/" + token_id + "/"
				+ profileName + "/" + contactNo + "/" + emailId + "/" + uuid
				+ "/" + currentpage, true, onSearchSuccess, onSearchFailure);
	}
}