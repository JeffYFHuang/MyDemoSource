$(document).ready(
		function() {
			var token_id = $("#token_id").val();
			var page_id = 1;
			
			$('.activityLogView').removeClass("treeview").addClass("active");
			$('.activityLogView').removeClass("font-medium-roboto").addClass("font-bold-roboto");
			$("#systemAdminProfileManagementIcon1")
					.attr("src",
							"resources/images/sidemenu_icon/white/activity_log.png");
			
			debugLogs(token_id);
			makeGetAjaxRequest("web/ActivityNotificationLogs/activity/"
					+ token_id + "/" + page_id, false,
					onDisplayActivityLogSuccess, onDisplayActivityLogFailure);
		});

function onDisplayActivityLogSuccess(data) {
	debugLogs('Response on Success URL Call' + "\t" + data);
	
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		var len = data.Return.Results.activity.length;
		debugLogs('List Length'+'\t'+len);
		$("#aLogDetails").text('');
		if (len) {
			$("#pagination").css({
				"display" : "block"
			});
			for (var count = 0; count < len; count++) {
				var profile_name = data.Return.Results.activity[count].profile_name;
				var username = data.Return.Results.activity[count].username;
				var role = data.Return.Results.activity[count].role;
				var action = data.Return.Results.activity[count].action;
				var ipaddress = data.Return.Results.activity[count].ipaddress;
				var created_date = data.Return.Results.activity[count].created_date;

				assignedend = data.Return.Results.activity[count].noofPages;
				assignedstart = data.Return.Results.activity[count].currentPage;
				var rowData = '<tr>' + '<td>' + profile_name + '</td>' + '<td>'
						+ username + '</td>' + '<td>' + role + '</td>' + '<td>'
						+ action + '</td>' + '<td>' + ipaddress + '</td>'
						+ '<td>' + created_date + '</td>' + '</tr>';

				debugLogs('rowData' + '\n' + rowData);
				$("#aLogDetails").append(rowData);
			}
			document.getElementById('assigned_startPage').innerHTML = assignedstart;
			document.getElementById('assigned_endPage').innerHTML = assignedend;
		} else {
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			$('#aLogDetails').append(
					'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
			return false;
		}
	} else {
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		$('#aLogDetails').append(
				'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
		return false;
	}
}
function onDisplayActivityLogFailure(data) {
	debugLogs('Response on Failure URL Call' + "\t" + data);

	$('#aLogDetails').append(
			'<tr><td colspan="6"><strong>Error Occured</strong></td></tr>');
	return false;
}

function increaseassigendHref() {
	debugLogs('Into increaseassigendHref()');
	debugLogs('assignedstart' + '\t' + assignedstart);

	var currentpage = assignedstart;
	debugLogs('currentpage' + "\t" + currentpage);
	currentpage++;
	var token_id = $("#token_id").val();
	if (currentpage <= assignedend) {
		debugLogs('Getting into if loop');
		assignedstart = currentpage;
		makeGetAjaxRequest("web/ActivityNotificationLogs/activity/" + token_id
				+ "/" + currentpage, false, onDisplayActivityLogSuccess,
				onDisplayActivityLogFailure);
	}
}

function decreaseassigendHref() {
	debugLogs('Into decreaseassigendHref()');
	var currentpage = assignedstart;
	currentpage--;
	var token_id = $("#token_id").val();
	if (currentpage > 0) {
		assignedstart = currentpage;
		makeGetAjaxRequest("web/ActivityNotificationLogs/activity/" + token_id
				+ "/" + currentpage, false, onDisplayActivityLogSuccess,
				onDisplayActivityLogFailure);
	}
}
