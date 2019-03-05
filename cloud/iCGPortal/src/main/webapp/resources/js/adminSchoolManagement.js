var token_id = $("#token_id").val();
var schoolstart = 0;
var schoolend = 0;


$(document)
.ready(
		function() {
			$("#schoolCreateSuccess").css("display", "none");
			$('.schoolmanage').removeClass("treeview").addClass(
			"active");
			$('.schoolmanage').removeClass("font-medium-roboto").addClass("font-bold-roboto");
			$("#sSchoolManageIcon")
			.attr("src",
			"resources/images/SuperAdminSideBarIcons/White_Icon/School_managment.png");
			getschoolListDetails();
			makeGetAjaxRequest("web/getCountyList/" + token_id, false,
					onCountyListSuccess, onCountyListFailure);
		});

$('#editTeacherDetails').on('show.bs.modal', function() {
	clearErrorMessages();
})

function clearErrorMessages() {
	$("#editAddSchoolName_err").css("display", "none");
	$("#editAddSchoolContact_err").css("display", "none");
	$("#editAddSchoolContactStartOfContactInvalid").css("display", "none");
	$("#editAddSchoolCity_err").css("display", "none");
	$("#editAccMgtClass_err").css("display", "none");
	$("#editAddSchoolUsername_err").css("display", "none");
	$("#editAddSchoolUsername_incorrect").css("display", "none");
	$("#editAddSchoolZipcode_err").css("display", "none");
	$("#editAddSchoolAddress_err").css("display", "none");
	$("#editAddSchoolState_err").css("display", "none");
}
function onCountyListSuccess(userData) {
	$('#addSchoolCounty').html('');
	$('#editAddSchoolCounty').html('');
	var counties = userData.Return.Results.counties;
	var len = counties.length;
	$('#addSchoolCounty').append($('<option>', {
		value : "",
		text : 'Select County'
	}));
	$('#editAddSchoolCounty').append($('<option>', {
		value : "",
		text : 'Select County'
	}));
	if (len > 0) {
		for (var count = 0; count < len; count++) {
			var county = counties[count].trim();
			$('#addSchoolCounty').append($('<option>', {
				value : county,
				text : county
			}));
			$('#editAddSchoolCounty').append($('<option>', {
				value : county,
				text : county
			}));
		}
	}

}

function onCountyListFailure(userData) {
	debugLogs("Inside onCountyListFailure");
}

$(function() {
	$('.upgrade_icon').bind("click", function(event) {
		displayUpgrade($(this));
	});

	function selectOptionByValue(elem, value) {
		var opts = elem.options;
		for (var opt, j = 1; opt = opts[j]; j++) {

			opt = opts[j];
			if (opt.value == value) {
				elem.selectedIndex = j;
				break;
			}
		}
	}

	function displayUpgrade(assetObj) {
		var selectOptions = document.getElementById('editAddSchoolCounty');
		var selectedOption = assetObj.attr('assetAdminCounty');

		$("#editAddSchoolName").val(assetObj.attr('assetAdminSchoolName'));
		$("#editAddSchoolCity").val(assetObj.attr('assetAdminCity'));
		$("#editAddSchoolContact").val(assetObj.attr('assetAdminContact'));
		$("#editAddSchoolCounty").val(assetObj.attr('assetAdminCounty'));
		$("#editAddSchoolUsername").val(assetObj.attr('assetAdminName'));
		$("#editAddSchoolZipcode").val(assetObj.attr('assetAdminZipcode'));
		$("#editAddSchoolAddress").val(assetObj.attr('assetAdminAddress'));
		$("#editAddSchoolState").val(assetObj.attr('assetAdminState'));
		$("#editAccountId").val(assetObj.attr('assetAdminAccountId'));
		var selectedOption = $("#editAddSchoolCounty").val();
		selectOptionByValue(selectOptions, selectedOption);
	}
});
$(function() {
	$("#editAdminSchoolUpdate").on(
			"click",
			function(e) {
				clearErrorMessages();
				var accountId = $('#editAccountId').val();
				var accountName = $('#editAddSchoolName').val();
				var city = $('#editAddSchoolCity').val();
				var county = $('#editAddSchoolCounty').val();
				var mobileNumber = $('#editAddSchoolContact').val();
				var zipcode = $('#editAddSchoolZipcode').val();
				var address = $('#editAddSchoolAddress').val();
				var adminUsername = $('#editAddSchoolUsername').val();
				var state = $('#editAddSchoolState').val();
				var emailValidation = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;

				var isValidForm = true;
				if (accountName.trim() === ''
					|| accountName.trim().length === 0) {
					$("#editAddSchoolName_err").css("display", "block");
					isValidForm = false;
				}
				if (adminUsername.trim() === ''
					|| adminUsername.trim().length === 0) {
					$("#editAddSchoolUsername_err").css("display", "block");
					isValidForm = false;
				} else if (adminUsername.length > 0
						&& !emailValidation.test(adminUsername)) {
					$("#editAddSchoolUsername_incorrect").css("display",
					"block");
					isValidForm = false;
				}

				if (mobileNumber.trim() == ''
					|| mobileNumber.trim().length === 0) {
					$("#editAddSchoolContact_err").css("display", "block");
					isValidForm = false;
				} else {
					if (!phoneRegEx.test(mobileNumber)) {
						$("#editAddSchoolContactStartOfContactInvalid").css(
								"display", "block");
						isValidForm = false;
					}
				}
				if (city.trim() === '' || city.trim().length === 0) {
					$("#editAddSchoolCity_err").css("display", "block");
					isValidForm = false;
				}
				if (county.trim() === '' || county.trim().length === 0) {
					$("#editAccMgtClass_err").css("display", "block");
					isValidForm = false;
				}
				if (zipcode.trim() === '' || zipcode.trim().length === 0) {
					$("#editAddSchoolZipcode_err").css("display", "block");
					isValidForm = false;
				}
				if (address.trim() === '' || address.trim().length === 0) {
					$("#editAddSchoolAddress_err").css("display", "block");
					isValidForm = false;
				}
				if (state.trim() === '' || state.trim().length === 0) {
					$("#editAddSchoolState_err").css("display", "block");
					isValidForm = false;
				}

				if (isValidForm) {
					var requestData = {
							"accountID" : accountId,
							"accountName" : accountName,
							"city" : city,
							"county" : county,
							"mobileNumber" : mobileNumber,
							"zipcode" : zipcode,
							"streetAddress" : address,
							"state" : state,
							"schoolAdmin" : adminUsername
					};

					makePostAjaxRequest("updateSchoolDetails/" + token_id + "/"
							+ accountId, requestData, onSuccessSchoolUpdate,
							onFailureSchoolUpdate);
				} else {
					e.preventDefault();
					return false;
				}
			});
	function onSuccessSchoolUpdate(successResponseData) {
		var msg =  successResponseData.Return.ResponseSummary.StatusCode;
		$("#schoolCreateSuccess").css("display", "none");
		$("#schoolCreateFailure").css("display", "none");
		$("#editDetialsFailure").css({"display" : "none"});
		$("#schoolDeleteSuccess").css({"display" : "none"});
		$("#schoolDeleteFailure").css({"display" : "none"});
		if(msg== "ERR24")
		{
			$("#editDetialsFailureWithMailID").css("display", "block");
			$("#editDetialsSuccess").css("display", "none");
		}
		else
		{
			$("#editDetialsSuccess").css({"display" : "block"});
			$("#editDetialsFailureWithMailID").css("display", "none");
			getschoolListDetails();
		}
	}

	function onFailureSchoolUpdate(failureResponseData) {
		$("#schoolCreateSuccess").css("display", "none");
		$("#schoolCreateFailure").css("display", "none");
		$("#editDetialsSuccess").css({"display" : "none"});
		("#editDetialsFailureWithMailID").css("display", "none");
		$("#schoolDeleteSuccess").css({"display" : "none"});
		$("#schoolDeleteFailure").css({"display" : "none"});
		$("#editDetialsFailure").css({"display" : "block"});
	}
});

$(function() {
	$("#addSchoolCreate")
	.on(
			"click",
			function(e) {
				$("#addSchoolNamee_err").css("display", "none");
				$("#addSchoolUsername_incorrect")
				.css("display", "none");
				$("#addSchoolContact_err").css("display", "none");
				$("#addSchoolContactStartOfContactInvalid").css(
						"display", "none");
				$("#addSchoolCity_err").css("display", "none");
				$("#accMgtClass_err").css("display", "none");
				$("#addSchoolUsername_err").css("display", "none");
				$("#addSchoolUsername_incorrect")
				.css("display", "none");
				$("#addSchoolZipcode_err").css("display", "none");
				$("#addSchoolAddress_err").css("display", "none");
				$("#addSchoolState_err").css("display", "none");

				var accountName = $('#addSchoolName').val();
				var city = $('#addSchoolCity').val();
				var county = $('#addSchoolCounty').val();
				var mobileNumber = $('#addSchoolContact').val();
				var zipcode = $('#addSchoolZipcode').val();
				var address = $('#addSchoolAddress').val();
				var state = $('#addSchoolState').val();
				var adminUsername = $('#addSchoolUsername').val();
				var emailValidation = /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;

				var isValidForm = true;
				if (accountName.trim() === ''
					|| accountName.trim().length === 0) {
					$("#addSchoolNamee_err").css("display", "block");
					isValidForm = false;
				}
				if (adminUsername.trim() === ''
					|| adminUsername.trim().length === 0) {
					$("#addSchoolUsername_err").css("display", "block");
					isValidForm = false;
				} else if (adminUsername.length > 0
						&& !emailValidation.test(adminUsername)) {
					$("#addSchoolUsername_incorrect").css("display",
					"block");
					isValidForm = false;
				}
				if (mobileNumber.trim() == ''
					|| mobileNumber.trim().length === 0) {
					$("#addSchoolContact_err").css("display", "block");
					isValidForm = false;
				} else {
					if (!phoneRegEx.test(mobileNumber)) {
						$("#addSchoolContactStartOfContactInvalid")
						.css("display", "block");
						e.preventDefault();
						isValidForm = false;
					}
				}
				if (city.trim() === '' || city.trim().length === 0) {
					$("#addSchoolCity_err").css("display", "block");
					isValidForm = false;
				}
				if (county.trim() === '' || county.trim().length === 0) {
					$("#accMgtClass_err").css("display", "block");
					isValidForm = false;
				}
				if (zipcode.trim() === ''
					|| zipcode.trim().length === 0) {
					$("#addSchoolZipcode_err").css("display", "block");
					isValidForm = false;
				}
				if (address.trim() === ''
					|| address.trim().length === 0) {
					$("#addSchoolAddress_err").css("display", "block");
					isValidForm = false;
				}
				if (state.trim() === '' || state.trim().length === 0) {
					$("#addSchoolState_err").css("display", "block");
					isValidForm = false;
				}

				if (isValidForm) {
					showSpinner();
					var requestData = {
							"accountName" : accountName,
							"city" : city,
							"county" : county,
							"mobileNumber" : mobileNumber,
							"zipcode" : zipcode,
							"state" : state,
							"address" : address,
							"schoolAdmin" : adminUsername
					};
					makePostAjaxRequest("addSchoolDetails/" + token_id,
							requestData, onSuccessSchoolCreate,
							onFailureSchoolCreate);
				} else {
					e.preventDefault();
					return false;
				}
			});
	function onSuccessSchoolCreate(successResponseData) {
		hideSpinnerNow();
		$("#editDetialsFailureWithMailID").css("display", "none");
		$("#editDetialsSuccess").css("display", "none");
		$("#schoolDeleteSuccess").css({"display" : "none"});
		$("#schoolDeleteFailure").css({"display" : "none"});
		$("#editDetialsFailure").css({"display" : "none"});
		var msg =  successResponseData.Return.ResponseSummary.StatusCode;
		if(msg== "ERR13")
		{
			var responseMessage = successResponseData.Return.ResponseSummary.StatusMessage;
			$("#schoolCreateSuccess").css("display", "none");
			$("#schoolCreateFailure").css("display", "block");
			$(".icglabel_school_failure").append('<br />' + responseMessage);
		}
		else
		{
			getschoolListDetails();
			$('#addSchoolName').val('');
			$('#addSchoolCity').val('');
			$('#addSchoolCounty').val('');
			$('#addSchoolContact').val('');
			$('#addSchoolZipcode').val('');
			$('#addSchoolAddress').val('');
			$('#addSchoolState').val('');
			$('#addSchoolUsername').val('');
			$("#schoolCreateSuccess").css("display", "block");
			$("#schoolCreateFailure").css("display", "none");
		}
	}

	function onFailureSchoolCreate(failureResponseData) {
		hideSpinnerNow();
		$("#editDetialsFailureWithMailID").css("display", "none");
		$("#editDetialsSuccess").css("display", "none");
		$("#schoolDeleteSuccess").css({"display" : "none"});
		$("#schoolDeleteFailure").css({"display" : "none"});
		$("#editDetialsFailure").css({"display" : "none"});
		$("#schoolCreateSuccess").css("display", "none");
		$("#schoolCreateFailure").css("display", "block");
	}
});

function getschoolListDetails() {
	var page_id = 1;
	makeGetAjaxRequest("getSchoolList?token=" + token_id+"&pageid=" + page_id, false,
			onSchoolListSuccess, onSchoolListFailure);

}

function onSchoolListSuccess(data) {
	if (!isDataNull(data) && !isDataNull(data.Return)
			&& !isDataNull(data.Return.Results)) {
		$('#schoolListDetailsBody').text('');
		var len = data.Return.Results.finalList.length;
		if(len > 0){
			$("#pagination").css({
				"display" : "block"
			});
		}
		for (var count = 0; count < len; count++) {
			var accountName = data.Return.Results.finalList[count].accountName;

			var accountId = data.Return.Results.finalList[count].accountId;
			var contactNumber = data.Return.Results.finalList[count].contactNumber;
			var schoolAdmin = data.Return.Results.finalList[count].schoolAdmin;
			var lastlogin = data.Return.Results.finalList[count].lastlogin;
			var allocatedDevices = data.Return.Results.finalList[count].allocatedDevices;
			var city = data.Return.Results.finalList[count].city;
			var zipCode = data.Return.Results.finalList[count].zipCode;
			var county = data.Return.Results.finalList[count].county;
			var state = data.Return.Results.finalList[count].state;
			var address = data.Return.Results.finalList[count].address;
			schoolend = data.Return.Results.finalList[count].noofPages;
			schoolstart = data.Return.Results.finalList[count].currentPage;

			var rowData = '';

			if (accountName != undefined) {
				rowData = '<tr>' + '<td>' + accountName + '</td>' + '<td>'
				+ accountId + '</td>' + '<td>' + contactNumber
				+ '</td>' + '<td>' + schoolAdmin + '</td>' ;

				if(lastlogin == undefined){
					rowData+= '<td>' + '-' + '</td>' + '<td>' + allocatedDevices+ '</td>';
				}
				else
				{
					rowData+= '<td>' + lastlogin + '</td>' + '<td>' + allocatedDevices+ '</td>';
				}


				rowData += '<td class="editIcon"><a class="teacherEditbtn"'
					+ 'style="color: black"><img  onclick="displayUpgrade($(this))"'
					+ 'assetAdminSchoolName="'
					+ escape(accountName)
					+ '"'
					+ 'assetAdminContact="'
					+ contactNumber
					+ '"'
					+ 'assetAdminCity="'
					+ city
					+ '" '
					+ 'assetAdminCounty="'
					+ county
					+ '"'
					+ 'assetAdminName="'
					+ schoolAdmin
					+ '"'
					+ 'assetAdminZipcode="'
					+ zipCode
					+ '"'
					+ 'assetAdminAddress="'
					+ address
					+ '"'
					+ 'assetAdminState="'
					+ state
					+ '"'
					+ 'assetAdminAccountId="'
					+ accountId
					+ '"'
					+ 'class="upgrade_icon hand"'
					+ 'src="resources/images/unselected_edit_icon.png"'
					+ 'data-toggle="modal" data-keyboard="true"'
					+ 'data-target="#editTeacherDetails" /></a></td>'

					rowData += '<td class="deleteIcon"><a style="color: black" data-toggle="modal" data-target="#deleteSchoolDetails"> '
						+ '<img onclick="deleteSchool($(this))" account_Id="'
						+ accountId
						+ '"  src="resources/images/Delete_icon.png" class="hand" /></a></td>'

						'<td>'
						+ ' <span class="hand"> <img src="resources/images/Delete_icon.png" /></span></td>';

				rowData += '</tr>';
				document.getElementById('school_startPage').innerHTML = schoolstart;
				document.getElementById('school_endPage').innerHTML = schoolend;
				if(schoolstart === schoolend){
					$("#increase").attr("href", "javascript: void(0)");
					$("#decrease").attr("href", "javascript: void(0)");
				}

			} else {
				var nodataText = getValueByLanguageKey('icglabel_nodata');
				rowData = '<tr><td align="center" colspan="5"><strong>' + nodataText + '</strong></td></tr>';
			}
			$("#schoolListDetailsBody").append(rowData);
		}

	} else {

	}
}
function onSchoolListFailure(data) {
	debugLogs('failed');
}

function deleteSchool(usrObj) {
	var schoolAccount_id = usrObj.attr('account_Id');
	$("#school_id").val(schoolAccount_id);
}

function deleteSchoolDetails() {
	debugLogs('Into deleteSchoolDetails()');
	var account_id = $('#school_id').val();
	debugLogs('school_id' + account_id);
	showSpinner();
	makeGetAjaxRequest("deleteSchoolApi/" + token_id + "/" + account_id, false,
			onDeleteSchoolSuccess, onDeleteSchoolFailure);
}

function onDeleteSchoolSuccess(responseData) {
	$("#editDetialsFailureWithMailID").css("display", "none");
	$("#editDetialsSuccess").css("display", "none");
	$("#editDetialsFailure").css({"display" : "none"});
	$("#schoolCreateSuccess").css("display", "none");
	$("#schoolCreateFailure").css("display", "none");
	$("#schoolDeleteFailure").css({"display" : "none"});
	$("#schoolDeleteSuccess").css({"display" : "none"});
	hideSpinnerNow();
	if (!isDataNull(responseData) && !isDataNull(responseData.Return)
			&& !isDataNull(responseData.Return.ResponseSummary)
			&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {
		responseCode = responseData.Return.ResponseSummary.StatusCode;
		if (responseCode === 'SUC01') {
			$("#schoolDeleteSuccess").css({"display" : "block"});
			getschoolListDetails();
		} else {
			$("#schoolDeleteFailure").css({"display" : "block"});
		}
	} else {
		$("#schoolDeleteFailure").css({"display" : "block"});
	}
}

function onDeleteSchoolFailure(userData) {
	$("#schoolDeleteSuccess").css({"display" : "none"});
	$("#editDetialsFailure").css({"display" : "none"});
	$("#schoolCreateSuccess").css("display", "none");
	$("#schoolCreateFailure").css("display", "none");
	$("#editDetialsFailureWithMailID").css("display", "none");
	$("#editDetialsSuccess").css("display", "none");
	$("#schoolDeleteFailure").css({"display" : "block"});
	hideSpinnerNow();
}

function selectOptionByValue(elem, value) {
	var opts = elem.options;
	for (var opt, j = 1; opt = opts[j]; j++) {

		opt = opts[j];
		if (opt.value == value) {
			elem.selectedIndex = j;
			break;
		}
	}
}

function displayUpgrade(assetObj) {
	var selectOptions = document.getElementById('editAddSchoolCounty');
	var selectedOption = assetObj.attr('assetAdminCounty');
	$("#editAddSchoolName").val(unescape(assetObj.attr('assetAdminSchoolName')));
	$("#editAddSchoolCity").val(assetObj.attr('assetAdminCity'));
	$("#editAddSchoolContact").val(assetObj.attr('assetAdminContact'));
	$("#editAddSchoolCounty").val(assetObj.attr('assetAdminCounty'));
	$("#editAddSchoolUsername").val(assetObj.attr('assetAdminName'));
	$("#editAddSchoolZipcode").val(assetObj.attr('assetAdminZipcode'));
	$("#editAddSchoolAddress").val(assetObj.attr('assetAdminAddress'));
	$("#editAddSchoolState").val(assetObj.attr('assetAdminState'));
	$("#editAccountId").val(assetObj.attr('assetAdminAccountId'));
	var selectedOption = $("#editAddSchoolCounty").val();
	selectOptionByValue(selectOptions, selectedOption);
}

function increaseassigendHref() {
	var currentpage = schoolstart;
	currentpage++;
	if (currentpage <= schoolend) {
		schoolstart = currentpage;
		makeGetAjaxRequest("getSchoolList?token=" + token_id+"&pageid=" + currentpage, false,
				onSchoolListSuccess, onSchoolListFailure);

	}else{
		$("#increase").attr("href", "javascript: void(0)");
	}
}

function decreaseassigendHref() {
	var currentpage = schoolstart;
	currentpage--;
	if (currentpage > 0) {
		schoolstart = currentpage;
		makeGetAjaxRequest("getSchoolList?token=" + token_id+"&pageid=" + currentpage, false,
				onSchoolListSuccess, onSchoolListFailure);

	}else{
		$("#decrease").attr("href", "javascript: void(0)");
	}

}