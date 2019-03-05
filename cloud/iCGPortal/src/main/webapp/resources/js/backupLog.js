$(document).ready(
		function() {
			var token_id = $("#token_id").val();
			debugLogs(token_id);
			var page_id = 1;
			
			$('.backupLogView').removeClass("treeview").addClass("active");
			$('.backupLogView').removeClass("font-medium-roboto").addClass("font-bold-roboto");
			$("#systemAdminProfileManagementIcon3")
					.attr("src",
							"resources/images/sidemenu_icon/white/backup_log.png");
			
			makeGetAjaxRequest("web/ActivityNotificationLogs/backup/"
					+ token_id + "/" + page_id, false, onBackupLogSuccess,
					onBackupLogFailure);
		});

function onBackupLogSuccess(data) {
	debugLogs('Response on Success URL Call' + "\t" + data);

	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$("#bLogDetails").text('');
		var len = data.Return.Results.backup.length;
		if (len > 0) {
			$("#pagination").css({"display" : "block"});
			
			for (var count = 0; count < len; count++) {
				var profile_name = data.Return.Results.backup[count].profile_name;
				var username = data.Return.Results.backup[count].username;
				var role = data.Return.Results.backup[count].role;
				var action = data.Return.Results.backup[count].action;
				var ipaddress = data.Return.Results.backup[count].ipaddress;
				var created_date = data.Return.Results.backup[count].created_date;

				assignedend = data.Return.Results.backup[count].noofPages;
				assignedstart = data.Return.Results.backup[count].currentPage;
				var rowData = '<tr>' + '<td>' + profile_name + '</td>' + '<td>'
						+ username + '</td>' + '<td>' + role + '</td>' + '<td>'
						+ action + '</td>' + '<td>' + ipaddress + '</td>'
						+ '<td>' + created_date + '</td>' + '</tr>';

				debugLogs('rowData' + '\n' + rowData);
				$("#bLogDetails").append(rowData);
			}
			document.getElementById('assigned_startPage').innerHTML = assignedstart;
			document.getElementById('assigned_endPage').innerHTML = assignedend;
		}else {
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			$('#bLogDetails').append(
			'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
			return false;
		}
	} else {
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		$('#bLogDetails').append(
				'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
		return false;
	}
}
function onBackupLogFailure(data) {
	debugLogs('Response on Failure URL Call' + "\t" + data);

	$('#bLogDetails').append(
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
		makeGetAjaxRequest("web/ActivityNotificationLogs/backup/" + token_id
				+ "/" + currentpage, false, onBackupLogSuccess,
				onBackupLogFailure);
	}
}

function decreaseassigendHref() {
	debugLogs('Into decreaseassigendHref()');
	var currentpage = assignedstart;
	currentpage--;
	var token_id = $("#token_id").val();
	if (currentpage > 0) {
		assignedstart = currentpage;
		makeGetAjaxRequest("web/ActivityNotificationLogs/backup/" + token_id
				+ "/" + currentpage, false, onBackupLogSuccess,
				onBackupLogFailure);
	}
}