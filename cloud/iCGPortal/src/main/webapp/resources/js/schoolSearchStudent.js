var studentstart = 0;
var studentend = 0;
var studentname = "";
var registationNo = "";
var uuid = "";

var token_id = $("#token_id").val();

$('.search_student').removeClass("treeview").addClass("active");
$('.search_student').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$(".sSearchIcon").attr("src",
"resources/images/SchoolAdmin_sideBarIcons/White/search_student.png");

$('div#locationMap').dialog({
	autoOpen : false
});
function loadStudentSearchResults() {
	studentname = $("#searchStudentName").val().trim();
	registationNo = $("#searchStudentRegId").val().trim();
	uuid = $("#searchStudentUUID").val().trim();

	studentname = (studentname.length <= 0) ? 0 : studentname;
	registationNo = (registationNo.length <= 0) ? 0 : registationNo;
	uuid = (uuid.length <= 0) ? 0 : uuid;

	if (studentname == 0 && registationNo == 0 && uuid == 0) {
		debugLogs('At least one search criteria must be entered');
		$("#searchCriteriaError").css({"display":"block"});
		hideSpinnerNow();
	} else {
		var page_id = 1;
		$("#searchCriteriaError").css({"display":"none"});
		makeGetAjaxRequest("web/SearchStudents/" + token_id + "/" + page_id
				+ "/" + studentname + "/" + registationNo + "/" + uuid, false,
				onSearchSuccess, onSearchFailure);
	}
}

$("#findStudent").click(function() {
	showSpinner();
	loadStudentSearchResults();
	return false;
});

function onSearchFailure(data) {
	if (isDataNull(data)) {
		var class_students = '<tr class="box-body"><td colspan="7" align="center"><b class="icglabel_nodata"></b></td> </tr>';
		$("#stDetails").append(class_students);
	}
}

function onSearchSuccess(data) {
	var currentLat = "", currentLong = "", currentStudent = "";

	$("tbody#stDetails tr").remove();
	$("#stDetails").empty();
	$("#searchDetails").empty();
	var nodataText = getValueByLanguageKey('icglabel_nodata');
	var rowData = '<tr><td align="center" colspan="7"><strong>' + nodataText + '</strong></td></tr>';
	$("#searchResultView").css({
		"display" : "block"
	});
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)
			&& !isDataNull(data.Return.Results.students)) {
		var student_ids = new Array();
		var len = data.Return.Results.students.length;
		if (len > 0) {
			$("#pagination").css({
				"display" : "block"
			});
		} else {
			$("#pagination").css({
				"display" : "none"
			});
		}
		for (i in data.Return.Results.students) {
			var student_name = "", device_status = "", student_grade = "", student_class = "", registartion_no = "", gps_location = "", roll_no = "", search_gps_location_data = "", student_id = "", device_uuid = "", emergency_contact = "";
			var latval = 0.0, longval = 0.0, class_students = "";
			var timeStamp = "";

			var arr = new Array();
			var current = data.Return.Results.students[i];
			
			if (!isDataNull(current.device_uuid)) {
				device_uuid = current.device_uuid;
			}
			
			if (!isDataNull(current.device_status)) {
				device_status = current.device_status;
			}
			
			if (!isDataNull(current.student_id)) {
				student_id = current.student_id;
				if (($.inArray(student_id, student_ids) !== -1)) {
					$('.suuid_' + student_id).append(
							"<br>" + device_uuid + " (" + device_status + ")");
					continue;
				} else {
					student_ids.push(student_id);
				}
			}
			
			if (!isDataNull(current.student_name)) {
				student_name = current.student_name;
			}
			
			if (!isDataNull(current.student_grade)) {
				student_grade = current.student_grade;
			}
			
			if (!isDataNull(current.student_class)) {
				student_class = current.student_class;
			}
			
			if (!isDataNull(current.registartion_no)) {
				registartion_no = current.registartion_no;
			}
			
			if (!isDataNull(current.gps_location_data)) {
				var lastKnownLocation = current.gps_location_data.split(',');
				latval = lastKnownLocation[0];
				longval = lastKnownLocation[1].trim();
				timeStamp = lastKnownLocation[2];
			}
			
			if (!isDataNull(current.roll_no)) {
				roll_no = current.roll_no;
			}
			
			if (!isDataNull(current.emergency_contact)) {
				emergency_contact = current.emergency_contact;
			}
			
			studentend = data.Return.Results.noofPages;
			studentstart = data.Return.Results.currentPage;
			class_students = '<tr><td><span class="noneditable">'
				+ student_name
				+ '</span></td>'
				+ '<td class="sregno">'
				+ registartion_no
				+ '</td>'
				+ '<td><span class="noneditable">'
				+ student_grade
				+ '</span></td> '
				+ '<td><span class="noneditable">'
				+ student_class
				+ '</span></td> '
				+ '<td class="suuid_'
				+ student_id
				+ '">'
				+ device_uuid
				+ " ("
				+ device_status
				+ ")"
				+ '</td> '
				+ '<td><span class="noneditable">'
				+ emergency_contact
				+ '</span></td>'
				+ '<td class="sdeleteBtn"><input type="image" src="resources/images/selected_location.png" '
				+ 'latitude=' + latval + ' longitude=' + longval
				+ ' student_name=' + student_name
				+ ' class="pButton" id="searchmap_' + i + '"/>'
				+ '</td></tr>';

			$("#stDetails").append(class_students);
			if (isDataNull(longval) || isDataNull(latval)) {
				$('#searchmap_' + i).attr('disabled', true);
				$('#searchmap_' + i).addClass("blur");
			}
		}
		document.getElementById('student_startPage').innerHTML = studentstart;
		document.getElementById('student_endPage').innerHTML = studentend;
		$(".pButton").click(function() {
			currentLat = Number($(this).attr('latitude'));
			currentLong = Number($(this).attr('longitude'));
			currentStudent = $(this).attr('student_name');
			$('body').css('overflow', 'hidden');
			$('div#locationMap').dialog('open');
		});

		$("div#locationMap").dialog({
			resizable : false,
			autofocus : true,
			height : "auto",
			width : 700,
			draggable : false,
			modal : true,
			closeOnEscape : true,
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close", ui.dialog | ui).show();
				initMap();
			}
		});
		function initMap() {
			var studentLatLng = {
					lat : currentLat,
					lng : currentLong
			};
			var map = new google.maps.Map(document
					.getElementById('locationView'), {
				zoom : 14,
				center : studentLatLng
			});

			var contentString = '<div id="content"><p>Last known location of '
				+ currentStudent + '</p>' + "<p> Time: " + timeStamp
				+ '</p> </div>';

			var infowindow = new google.maps.InfoWindow({
				content : contentString
			});

			var marker = new google.maps.Marker({
				position : studentLatLng,
				map : map
			});
			marker.addListener('click', function() {
				infowindow.open(map, marker);
			});
			infowindow.open(map, marker);
		}
		$("#close_locationMap").click(function() {
			$('body').css('overflow', 'scroll');
			$('#locationView').empty();
			$('div#locationMap').dialog('close');
		});
		$("#locationMap").keydown(function(e) {
			if (e.keyCode == 27) {
				$("#close_locationMap").click();
			}
		});
	} else {
		$("#stDetails").append(rowData);
	}
}

function increasestudentHref() {
	var currentpage = studentstart;
	studentname = $("#searchStudentName").val().trim();
	registationNo = $("#searchStudentRegId").val().trim();
	uuid = $("#searchStudentUUID").val().trim();
	studentname = (studentname.length <= 0) ? 0 : studentname;
	registationNo = (registationNo.length <= 0) ? 0 : registationNo;
	uuid = (uuid.length <= 0) ? 0 : uuid;
	if (studentname == 0 && registationNo == 0 && uuid == 0) {
		debugLogs('At least one search criteria must be entered');
	} else {
		currentpage++;
		if (currentpage <= studentend) {
			studentstart = currentpage;
			makeGetAjaxRequest("web/SearchStudents/" + token_id + "/"
					+ currentpage + "/" + studentname + "/" + registationNo
					+ "/" + uuid, false, onSearchSuccess, onSearchFailure);

		}
	}
}

function decreasestudentHref() {
	var currentpage = studentstart;
	studentname = $("#searchStudentName").val().trim();
	registationNo = $("#searchStudentRegId").val().trim();
	uuid = $("#searchStudentUUID").val().trim();
	studentname = (studentname.length <= 0) ? 0 : studentname;
	registationNo = (registationNo.length <= 0) ? 0 : registationNo;
	uuid = (uuid.length <= 0) ? 0 : uuid;
	if (studentname == 0 && registationNo == 0 && uuid == 0) {
		debugLogs('At least one search criteria must be entered');
	} else {
		currentpage--;
		if (currentpage > 0) {
			studentstart = currentpage;
			makeGetAjaxRequest("web/SearchStudents/" + token_id + "/"
					+ currentpage + "/" + studentname + "/" + registationNo
					+ "/" + uuid, false, onSearchSuccess, onSearchFailure);
		}
	}

}
