var DEBUG_MODE = true;
var loginToken = $('#token_id').val();
var IconImgMaximumUploadSize = 1048576;
var FOTAImgMaximumUploadSize = 31457280;
var phoneRegEx = /^09[0-9]{8}$/;
var LANGUAGE_JSON = '';
var maxAllowedCSVRows = 3001;

if (!isDataNull(loginToken)) {
	makeGetAjaxRequest("web/isTokenValid/" + loginToken, false,
			onSuccessTokenValidorInvalid, onFailureTokenValidorInvalid)
}

function onSuccessTokenValidorInvalid(responseData) {
	if (!isDataNull(responseData) && !isDataNull(responseData.Return)
			&& !isDataNull(responseData.Return.ResponseSummary)
			&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {

		responseCode = responseData.Return.ResponseSummary.StatusCode;

		if (responseCode === 'SUC01') {
			debugLogs("Token is Valid");
		} else if (responseCode === 'ERR01') {
			debugLogs("ERR01 >>>>>>>>>>>>>>>>>>>>>> error :::::::::::::::::: ");
			window.history.forward();
		} else if (responseCode === 'ERR02') {
			debugLogs("ERR02 >>>>>>>>>>>>>>>>>>>> error :::::::::::::::::: ");
			window.history.forward();
		}
	}
}

function onFailureTokenValidorInvalid(responseData) {
	debugLogs("status on failure" + responseData.status)
}

function makeGetAjaxRequest(requestURL, allowCache, onSuccess, onFailure) {
	allowCacheContent = 1;
	if (isDataNull(allowCache)) {
		allowCacheContent = 0;
	}
	$.ajax({
		url : requestURL,
		headers : {
			'allowCacheContent' : allowCacheContent
		},
		dataType : "json",
		type : "GET",
		crossDomain : true,
		async : true,
		success : function(response) {
			onSuccess(response);
		},
		error : function(jqXHR, exception) {
			onFailure(jqXHR);
		}
	});

}
function makeGetAjaxRequestSync(requestURL, allowCache, onSuccess, onFailure) {
	allowCacheContent = 1;
	if (isDataNull(allowCache)) {
		allowCacheContent = 0;
	}
	$.ajax({
		url : requestURL,
		headers : {
			'allowCacheContent' : allowCacheContent
		},
		dataType : "json",
		type : "GET",
		crossDomain : true,
		async : false,
		success : function(response) {
			onSuccess(response);
		},
		error : function(jqXHR, exception) {
			onFailure(jqXHR);
		}
	});

}

function makePostAjaxRequest(requestURL, requestData, onSuccess, onFailure) {
	$.ajax({
		url : requestURL,
		data : JSON.stringify(requestData),
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		type : "POST",
		crossDomain : true,
		async : true,
		success : function(response) {
			onSuccess(response);
		},
		error : function(jqXHR, exception) {
			onFailure(jqXHR);
		}
	});
}
function makePostAjaxRequestSync(requestURL, requestData, onSuccess, onFailure) {
	$.ajax({
		url : requestURL,
		data : JSON.stringify(requestData),
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		type : "POST",
		crossDomain : true,
		async : false,
		success : function(response) {
			onSuccess(response);
		},
		error : function(jqXHR, exception) {
			onFailure(jqXHR);
		}
	});
}

function makeDeleteAjaxRequest(requestURL, requestData, onSuccess, onFailure) {
	$.ajax({
		url : requestURL,
		data : JSON.stringify(requestData),
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		type : "DELETE",
		crossDomain : true,
		async : true,
		success : function(response) {
			onSuccess(response);
		},
		error : function(jqXHR, exception) {
			onFailure(jqXHR);
		}
	});
}

function makePostFileAjaxRequest(requestURL, requestData, onSuccess, onFailure) {
	$.ajax({
		url : requestURL,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		data : requestData,
		type : 'POST',
		contentType : false,
		processData : false,
		success : function(response) {
			onSuccess(response);
		},
		error : function(jqXHR, exception) {
			onFailure(jqXHR);
		}
	});
}

function makePutAjaxRequest(requestURL, requestData, onSuccess, onFailure) {
	$.ajax({
		url : requestURL,
		data : JSON.stringify(requestData),
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		type : "PUT",
		crossDomain : true,
		async : true,
		success : function(response) {
			onSuccess(response);
		},
		error : function(jqXHR, exception) {
			onFailure(jqXHR);
		}
	});
}

function isDataNull(src) {
	if (src == null || src == undefined || src == "" || src == "undefined") {
		return true;
	}
	return false;
} // End of isDataNull method

function debugLogs(input) {
	if (DEBUG_MODE && input != "") {
		console.log(input);
	}
} // End of clientLogs method

function readImageFile(fileName, id) {
	jQuery.support.cors = true;
	$.ajax({
		cache : false,
		url : fileName,
		data : "{}",
		type : 'GET',
		crossDomain : true,
		dataType : 'text',
		async : false,
		success : function(data) {
			$('#' + id).attr('src', data);
			$('#' + id).show();
			$('#' + id + '_label').show();
		},
		error : function() {
			debugLogs('file error:' + fileName);
		}
	});
} // Function to read image file from server - success response in case image
// exists.

function getCookie(cookieName) {
	var cookieValue = null;
	var allcookies = document.cookie;
	var cookiearray = allcookies.split(';');

	for (var i = 0; i < cookiearray.length; i++) {
		var name = (cookiearray[i].split('=')[0]);
		var nameString = $.trim(name);
		if (nameString === cookieName) {
			cookieValue = cookiearray[i].split('=')[1];
			break;
		}
	}
	return cookieValue;
}

function deleteCookie(cookieName) {
	var allcookies = document.cookie;
	var cookiearray = allcookies.split(';');
	for (var i = 0; i < cookiearray.length; i++) {
		var name = cookiearray[i].split('=')[0];
		var nameString = $.trim(name);
		if (nameString == cookieName) {
			var date = new Date();
			date.setDate(date.getDate() - 1);
			document.cookie = nameString
					+ "=GONEcbEndCookie; expires=Monday, 19-Aug-1996 05:00:00 GMT";
			break;
		}
	}
}

function setCookie(cookieName, cookieValue, exdays) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value = escape(cookieValue)
			+ ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
	document.cookie = cookieName + "=" + c_value;
}

function setFocusOnElement(element) {
	$(element).focus();
} // Set focus on specified element

function recurseSearch(parsedJSON, id) {
	var isFound = '';
	if (!isDataNull(parsedJSON)) {
		for ( var key in parsedJSON) {
			if (key == id) {
				isFound = parsedJSON[key];
			} else if (typeof (parsedJSON[key]) == "object") {
				isFound = recurseSearch(parsedJSON[key], id);
			}
			if (isFound) {
				break;
			}
		}
	}
	return isFound;
} // Function to return value of specified key in JSON object if exists else
// empty will be returned.

function displayLabels(language, user_role) {
	// Finds all elements with class name that starts with text-
	var languageLabels;
	switch (user_role) {
	case "common":
		switch (language) {
		case "en":
			languageLabels = "resources/language/common-en.txt";
			break;
		case "cn-simplified":
			languageLabels = "resources/language/common-cn-simplified.txt";
			break;
		case "cn-traditional":
			languageLabels = "resources/language/common-cn-traditional.txt";
			break;
		}
		break;
	case "parent_admin":
		switch (language) {
		case "en":
			languageLabels = "resources/language/parent-en.txt";
			break;
		case "cn-simplified":
			languageLabels = "resources/language/parent-cn-simplified.txt";
			break;
		case "cn-traditional":
			languageLabels = "resources/language/parent-cn-traditional.txt";
			break;
		}
		break;
	case "school_admin":
		switch (language) {
		case "en":
			languageLabels = "resources/language/school-en.txt";
			break;
		case "cn-simplified":
			languageLabels = "resources/language/school-cn-simplified.txt";
			break;
		case "cn-traditional":
			languageLabels = "resources/language/school-cn-traditional.txt";
			break;
		}
		break;
	case "super_admin":
	case "system_admin":
	case "support_staff":
		switch (language) {
		case "en":
			languageLabels = "resources/language/admin-en.txt";
			break;
		case "cn-simplified":
			languageLabels = "resources/language/admin-cn-simplified.txt";
			break;
		case "cn-traditional":
			languageLabels = "resources/language/admin-cn-traditional.txt";
			break;
		}
		break;
	}

	if (!isDataNull(languageLabels)) {
		makeGetAjaxRequest(languageLabels, true, onSuccessShowLabels,
				onFailureShowLabels);
		return true;
	}

	debugLogs("Inside: displayLabels(), Some issue with reading appropriate language labels.language>"
			+ language + ", user_role>" + user_role);
	return false;
}

function onSuccessShowLabels(parsedJSON) {
	LANGUAGE_JSON = parsedJSON;
	$.each( $('*[class*="icglab"]'), function(key, val) {
		getValueFromJSON(parsedJSON, val.classList[val.classList.length-1], val.classList[val.classList.length-1]);
		
	});

	function getValueFromJSON(parsedJSON, key1, val1) {
	      if (!isDataNull(parsedJSON[val1])) {
	    	  $('.' + key1).text(parsedJSON[val1]);
	      }
	}
}

function onFailureShowLabels() {
	debugLogs("Inside: onFailureShowLabels(), Some issue with reading appropriate language labels json file");
}

function changeFunc() {
	var selectName = document.getElementById("selectBox");
	var selectedValue = selectName.value;
	setCookie("selectName", selectedValue, 30);
	displayLabels(selectedValue, "common");
}

function getToday() {
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; // January is 0!
	var yyyy = today.getFullYear();

	if(dd<10) {
	    dd = '0'+dd
	} 

	if(mm<10) {
	    mm = '0'+mm
	} 

	today = yyyy +'-'+ mm +'-'+ dd ;
	return today;
}

$(document).ready(
		function() {
			if ($('#selectBox').length && getCookie('selectName')) {
				$("#selectBox").val(getCookie('selectName'));
			}

			var language = (isDataNull(getCookie("selectName"))) ? 'en'
					: getCookie("selectName");
			var user_role = (isDataNull(userRole)) ? 'common' : userRole;
			displayLabels(language, user_role);

			$(document).on(
					
				"keypress",
				':input[type="number"]',
				function(event) {
					var $this = $(this), maxlength = $this
							.attr('maxlength'), code = event.keyCode
							|| event.which;
					var char = String.fromCharCode(code);
					if (maxlength && maxlength > 0) {
						if((code >= 48 && code <=57)) { 
						}else{
							event.preventDefault();
						}
						return ($this.val().length < maxlength)
					}
				})

			$(':input[type="number"]').bind("cut copy paste", function(e) {
				e.preventDefault();
			});

		});

function validateEmail(email) {
	//var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	var re =/^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/;
	return re.test(email);
}

function resetpassword() {
	$("#resetSuccess").css("display", "block");
}

function isImageExists(image_url, element, source) {
	if (!isDataNull(image_url)) {
		if ($("#" + element).length) {
			$.get(image_url).done(function() {
				if (!isDataNull(source) && (source == "geofence" || source == "rewardSmallTab")) {
					$("#" + element).css({
						"display" : "block"
					});
					if(source == "rewardSmallTab"){
						$("#" + element).attr("src", image_url);
					}
				} else {
					$("#" + element).attr("src", image_url);
				}

			}).fail(
					function() {
						if (!isDataNull(source) && (source == "geofence" || source == "rewardSmallTab")) {
							$("#" + element).css({
								"display" : "none"
							});
						} else {
							$("#" + element).attr("src",
									"resources/images/ImgNotAvailable.png");
						}
					})
		}
	}
}

function contactStartCheck(contact) {
	return /^[0][9]$/.test(contact);
}
function contactCheck(contact) {
	return /^[0-9]{9}$/.test(contact);
}
function hasNumber(pswd) {
	return /\d/.test(pswd);
}
function hasLetter(pswd) {
	return /[a-zA-Z]/.test(pswd);
}
function emailCheck(email) {
	//return /^[-a-z0-9~!$%^&*_=+]+(\.[-a-z0-9~!$%^&*_=+}{?]+)*@[-a-z0-9_~!$%^&*_=+]+(\.[a-z-_0-9]+)*$/i.test(email);
	return /^[A-Za-z0-9]+([_.-][a-zA-Z0-9]+)*@[A-Za-z0-9]+([.-][a-zA-Z0-9]+)*\.[A-Za-z]{2,3}$/i.test(email);
}
// Not in use?
function ValidateEmail(email) {
	if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
		return true;
	} else
		return false;
}

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
}

function forceLower(strInput) 
{
	var position = strInput.selectionStart;
	strInput.value=strInput.value.toLowerCase();
	strInput.selectionEnd = position;
}

function showFileName(input) {
	var fileName = input.value.split( '\\' ).pop();

	if( fileName )
		input.nextElementSibling.querySelector( 'span' ).innerHTML = fileName;
}

function showFileNameInsideInput(input, elementId) {
	var fileName = input.value.split( '\\' ).pop();

	if( fileName )
		$("#" + elementId).val(fileName);
}

function getValueByLanguageKey(labelKey) {
	var parsedJSON = LANGUAGE_JSON;
	var labelValue = sessionStorage.getItem(labelKey);
	if(isDataNull(labelValue)){
	    if (!isDataNull(parsedJSON) && !isDataNull(parsedJSON[labelKey])) {
	    	labelValue = parsedJSON[labelKey];
	    	sessionStorage.setItem(labelKey, labelValue);
	    }
	}
    return labelValue;
}

$("form").submit(function() {
    $("#overlay, #spinner").show();
    return true;
});
$(document).ajaxComplete(function(){
	$("#overlay, #spinner").hide();
 });
function showSpinner(){
	$("#overlay, #spinner").show();
}
function hideSpinner(){
	setInterval(function(){ $("#overlay, #spinner").hide(); }, 2000);
}

function hideSpinnerNow(){
	$("#overlay, #spinner").hide();
}

function fileContentsCheck(fileObject, filetype, errorDisplayId, uploadButtonId){
	var headers = [];
	switch(filetype){
		case 'teachers':
			headers = ["sl_no", "grade", "class", "name", "username", "role", "contact"];
			break;
		case 'students':
			headers = ["srl", "grade", "class", "admission_no", "class_roll_no", "name", "emergency_contact", "device_uuid"];
			break;
		case 'holidays':
			headers = ["sl-no", "title", "start", "end"];
			break;
		case 'timetable':
			headers = ["sl_no", "grade", "class", "week_day", "subject1", "subject2", "subject3", "subject4", "subject5", "subject6", "subject7", "subject8"];
			break;
		case 'devices':
			headers = ["sl-no", "school_id", "device_uuid", "device_model", "firmaware_version"];
			break;
	}
	
	if (typeof (FileReader) != "undefined") {
        var reader = new FileReader();
        reader.onload = function (e) {
            var rows = e.target.result.split("\n");
            if(rows.length > maxAllowedCSVRows){
            	hideSpinnerNow();
            	var error_msg = "CSV file contains more than " + maxAllowedCSVRows + " records";
            	$('#' + errorDisplayId).css("display", "block");
				$('#' + errorDisplayId).text(error_msg);
				return;
            }else if(rows.length <= 1){
            	hideSpinnerNow();
            	var error_msg = "CSV file contains no records";
            	$('#' + errorDisplayId).css("display", "block");
				$('#' + errorDisplayId).text(error_msg);
				return;
            }else{
            	var cells = rows[0].split(",");
            	if(headers.length == cells.length){
            		for (var j = 0; j < cells.length; j++) {
            			if(cells[j].trim() !== headers[j].trim()){
            				hideSpinnerNow();
            				var error_msg = "CSV File Headers Not Matching.";
            				$('#' + errorDisplayId).css("display", "block");
            				$('#' + errorDisplayId).text(error_msg);
            				return;
            			}
                    }
            	}else {
            		hideSpinnerNow();
            		var error_msg = "CSV File Headers Not Matching.";
            		$('#' + errorDisplayId).css("display", "block");
    				$('#' + errorDisplayId).text(error_msg);
    				return;
                }
            }
            $('#' + errorDisplayId).text('');
            $('#' + uploadButtonId).attr('disabled', false).css('opacity', '');
            return;
        }
        reader.readAsText(fileObject);
    } else {
        debugLogs("Inside:fileContentsCheck, This browser does not support HTML5.");
    }
	$('#' + errorDisplayId).val('');
	$('#' + uploadButtonId).attr('disabled', true).css('opacity', '0.6');
	return;
}
