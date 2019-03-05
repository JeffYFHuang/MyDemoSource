var token_id = $("#token_id").val();

$('.search_device').removeClass("treeview").addClass("active");
$('.search_device').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$("#supportStaffSearchDeviceIcon").attr("src",
		"resources/images/SuperAdminSideBarIcons/White_Icon/Device_managment.png");

function getDeviceDetails() {

	var uuid = $('#adminDeviceUUIDforFind').val();
	if (uuid.length > 0)
		makeGetAjaxRequest("getDeviceDetails?token=" + token_id + "&uuid="
				+ uuid, false, onDeviceSuccess, onDeviceFailure);

}

function onDeviceSuccess(data) {
	$('#deviceDataSection').empty();
	var nodataText = getValueByLanguageKey('icglabel_nodata');
	var rowData = '<tr><td align="center" colspan="6"><strong>' + nodataText + '</strong></td></tr>';
	
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		var device = data.Return.Results.finalList;

		if(device.length > 0){
			var uuid = device[0].uuid;
			var schoolName = device[0].schoolName;
			var firmWareVersion = device[0].firmWareVersion;
			var deviceModel = device[0].deviceModel;
			var addedDate = device[0].addedDate;
			var status = device[0].status;
		
			if(status == ""){
				status ='un-assigend';
			}
			
			if (uuid != undefined) {
				rowData = '<tr>' + '<td>'
						+ uuid
						+ '</td>'
						+ '<td>'
						+ schoolName
						+ '</td>'
						+ '<td>'
						+ firmWareVersion
						+ '</td>'
						+ '<td>'
						+ deviceModel
						+ '</td>'
						+ '<td>'
						+ addedDate
						+ ' </td>'
						+ '<td>'
						+ status
						+ ' </td>'
						+ '</tr>';			
			} 
			$("#deviceDataSection").append(rowData);
		}else{
			$("#deviceDataSection").append(rowData);
		}
	}else{
		$("#deviceDataSection").append(rowData);
	}
}
function onDeviceFailure(data) {
	debugLogs('failed');
}

