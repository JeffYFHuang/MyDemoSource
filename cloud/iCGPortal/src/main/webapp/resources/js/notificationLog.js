$(document).ready(
		function() {
			var token_id = $("#token_id").val();
			var page_id = 1;
			
			$('.notificationLogView').removeClass("treeview").addClass("active");
			$('.notificationLogView').removeClass("font-medium-roboto").addClass("font-bold-roboto");
			$("#systemAdminProfileManagementIcon2")
					.attr("src",
							"resources/images/sidemenu_icon/white/notification_log.png");
			
			debugLogs(token_id);
			makeGetAjaxRequest("web/ActivityNotificationLogs/notification/"
					+ token_id + "/" + page_id, false,
					onNotificationLogSuccess, onNotificationLogFailure);
		});

function onNotificationLogSuccess(data) {
	debugLogs('Response on Success URL Call' + "\t" + data);

	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$("#nLogDetails").text('');

		var len = data.Return.Results.notification.length;
		if (len > 0) {
			$("#pagination").css({
				"display" : "block"
			});
			for (var count = 0; count < len; count++) {
				var profile_name = data.Return.Results.notification[count].profile_name;
				var username = data.Return.Results.notification[count].username;
				var role = data.Return.Results.notification[count].role;
				var action = data.Return.Results.notification[count].action;
				var ipaddress = data.Return.Results.notification[count].ipaddress;
				var created_date = data.Return.Results.notification[count].created_date;

				assignedend = data.Return.Results.notification[count].noofPages;
				assignedstart = data.Return.Results.notification[count].currentPage;

				var rowData = '<tr>' + '<td>' + profile_name + '</td>' + '<td>'
						+ username + '</td>' + '<td>' + role + '</td>' + '<td>'
						+ action + '</td>' + '<td>' + ipaddress + '</td>'
						+ '<td>' + created_date + '</td>' + '</tr>';

				debugLogs('rowData' + '\n' + rowData);
				$("#nLogDetails").append(rowData);
			}
			document.getElementById('assigned_startPage').innerHTML = assignedstart;
			document.getElementById('assigned_endPage').innerHTML = assignedend;
		} else {
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			$('#nLogDetails').append(
					'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
			return false;
		}
	} else {
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		$('#nLogDetails').append(
				'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
		return false;
	}
}
function onNotificationLogFailure(data) {
	debugLogs('Response on Failure URL Call' + "\t" + data);

	$('#nLogDetails').append(
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
		makeGetAjaxRequest("web/ActivityNotificationLogs/notification/"
				+ token_id + "/" + currentpage, false,
				onNotificationLogSuccess, onNotificationLogFailure);
	}
}

function decreaseassigendHref() {
	debugLogs('Into decreaseassigendHref()');
	var currentpage = assignedstart;
	currentpage--;
	var token_id = $("#token_id").val();
	if (currentpage > 0) {
		assignedstart = currentpage;
		makeGetAjaxRequest("web/ActivityNotificationLogs/notification/"
				+ token_id + "/" + currentpage, false,
				onNotificationLogSuccess, onNotificationLogFailure);
	}
}
