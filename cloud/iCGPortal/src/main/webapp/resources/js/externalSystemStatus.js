$(document).ready(
		function() {
			var token_id = $("#token_id").val();
			var page_id = 1;
			
			$('.externalSystemStatusView').removeClass("treeview").addClass("active");
			$('.externalSystemStatusView').removeClass("font-medium-roboto").addClass("font-bold-roboto");
			$("#systemAdminProfileManagementIcon4")
					.attr("src",
							"resources/images/sidemenu_icon/white/external_system_status.png");
			
			debugLogs(token_id);
			makeGetAjaxRequest("web/ExternalSystemStatus/" + token_id + "/"
					+ page_id, false, onExternalSystemStatusSuccess,
					onExternalSystemStatusFailure);
		});

function onExternalSystemStatusSuccess(data) {
	debugLogs('Response on Success URL Call' + "\t" + data);

	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$("#beaconStatus").text('');
		var len = data.Return.Results.externalsystemstatus.length;

		if (len > 0) {
			$("#pagination").css({
				"display" : "block"
			});
			for (var count = 0; count < len; count++) {
				var ips_receiver_mac = data.Return.Results.externalsystemstatus[count].ips_receiver_mac;
				var ips_receiver_name = data.Return.Results.externalsystemstatus[count].ips_receiver_name;
				var ips_receiver_version = data.Return.Results.externalsystemstatus[count].ips_receiver_version;
				var ips_receiver_status = data.Return.Results.externalsystemstatus[count].ips_receiver_status;
				var device_count = data.Return.Results.externalsystemstatus[count].device_count;
				var school_name = data.Return.Results.externalsystemstatus[count].school_name;
				assignedend = data.Return.Results.externalsystemstatus[count].noofPages;
				assignedstart = data.Return.Results.externalsystemstatus[count].currentPage;

				if (typeof device_count === 'undefined') {
					device_count = 0;
				}
				
				var rowData = '<tr>' + '<td>' + ips_receiver_mac + '</td>'
						+ '<td>' + ips_receiver_name + '</td>' + '<td>'
						+ ips_receiver_version + '</td>' + '<td>'
						+ device_count + '</td>' + '<td>' + ips_receiver_status
						+ '</td>' + '<td>' + school_name + '</td>' + '</tr>';

				debugLogs('rowData' + '\n' + rowData);
				$("#beaconStatus").append(rowData);
			}
			document.getElementById('assigned_startPage').innerHTML = assignedstart;
			document.getElementById('assigned_endPage').innerHTML = assignedend;
		} else {
			var nodataText = getValueByLanguageKey('icglabel_nodata');
			$('#beaconStatus').append(
					'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
			return false;
		}
	} else {
		var nodataText = getValueByLanguageKey('icglabel_nodata');
		$('#beaconStatus').append(
				'<tr><td colspan="6"><strong>' + nodataText + '</strong></td></tr>');
		return false;
	}
}
function onExternalSystemStatusFailure(data) {
	debugLogs('Response on Failure URL Call' + "\t" + data);

	$('#beaconStatus').append(
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
		makeGetAjaxRequest("web/ExternalSystemStatus/" + token_id + "/"
				+ currentpage, false, onExternalSystemStatusSuccess,
				onExternalSystemStatusFailure);
	}
}

function decreaseassigendHref() {
	debugLogs('Into decreaseassigendHref()');
	var currentpage = assignedstart;
	currentpage--;
	var token_id = $("#token_id").val();
	if (currentpage > 0) {
		assignedstart = currentpage;
		makeGetAjaxRequest("web/ExternalSystemStatus/" + token_id + "/"
				+ currentpage, false, onExternalSystemStatusSuccess,
				onExternalSystemStatusFailure);
	}
}
