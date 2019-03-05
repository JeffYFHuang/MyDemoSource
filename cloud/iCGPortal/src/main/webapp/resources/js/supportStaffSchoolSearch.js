var token_id = $("#token_id").val();

$('.supportstaff_search_school').removeClass("treeview").addClass("active");
$('.supportstaff_search_school').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$("#supportStaffSchoolSearchIcon").attr("src",
		"resources/images/sidemenu_icon/white/search_school.png");

function loadSchoolNames() {
	var county = $('#adminDeviceCountyFilter').val();

	makeGetAjaxRequest("getSchoolDetails?token=" + token_id + "&county="
			+ county, false, onSchoolSuccess, onSchoolFailure);

}

function onSchoolSuccess(data) {

	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#adminDeviceSchoolNameFilter').html('');
		$('#adminDeviceSchoolNameFilter').append($('<option>', {
			value : 0,
			text : 'Select School'
		}));
		var len = data.Return.Results.finalList.length;

		for (var count = 0; count < len; count++) {
			var school_id = data.Return.Results.finalList[count].school_id;
			var schoolName = data.Return.Results.finalList[count].schoolName;

			if (school_id != undefined) {
				$('#adminDeviceSchoolNameFilter').append($('<option>', {
					value : school_id,
					text : schoolName
				}));
			}
		}

	} else {

	}
}
function onSchoolFailure(data) {
	debugLogs('failed');
}

function getDeviceStatDetails() {

	var county = $('#adminDeviceCountyFilter').val();
	var schoolId = $('#adminDeviceSchoolNameFilter').val();
	if (schoolId > 0) {
		makeGetAjaxRequest("web/SearchSchool/ " + token_id + "/" + schoolId,
				false, onSearchSchoolSuccess, onSearchSchoolFailure);
	}
}

function onSearchSchoolSuccess(data) {

	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {

		$('#searchSchoolDetails').text('');

		var len = data.Return.Results.searchstudent.length;
		$("#searchResultView").css({"display":"block"});
		if (len > 0) {
			for (var count = 0; count < len; count++) {
				var id = data.Return.Results.searchstudent[count].id;
				var contact = data.Return.Results.searchstudent[count].contact;
				var username = data.Return.Results.searchstudent[count].username;
				var lastlogin = data.Return.Results.searchstudent[count].lastlogin;
				var allocateddevice = data.Return.Results.searchstudent[count].allocateddevice;
				var address = data.Return.Results.searchstudent[count].address;

				if (typeof lastlogin === 'undefined') {
					lastlogin = '-';
				}

				if (typeof address === 'undefined') {
					address = '-';
				}

				var rowData = '<tr>' + '<td>' + id + '</td>' + '<td>' + address
						+ '<td>' + contact + '</td>' + '<td>' + username
						+ '</td>' + '<td>' + allocateddevice + '<td>'
						+ lastlogin + '</td>' + '</td>' + '</td>' + '</tr>';
				$("#searchSchoolDetails").append(rowData);
			}
		} else {
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			$('#searchSchoolDetails').append(
					'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
			return false;
		}
	} else {
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		$('#searchSchoolDetails').append(
				'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
		return false;
	}
}
function onSearchSchoolFailure(data) {
	debugLogs('failed');
}
