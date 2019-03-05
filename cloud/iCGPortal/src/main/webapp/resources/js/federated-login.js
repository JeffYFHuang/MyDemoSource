	//Federated Login Using Google
	var googleUser = {};
	var startApp = function() {
		gapi.load('auth2',function() {
							// Retrieve the singleton for the GoogleAuth library and set up the client.
							auth2 = gapi.auth2
									.init({
										client_id : google_id,
										cookiepolicy : 'single_host_origin',
										scope : 'profile email'
									});
							attachSignin(document.getElementById('customBtn'));
						});
	};

	function attachSignin(element) {
		auth2.attachClickHandler(element, {}, function(googleUser) {
			onSuccess(googleUser)
		}, function(error) {
			onFailure(error);
		});
	}
	startApp();

	function onSuccess(googleUser) {
			var profile = googleUser.getBasicProfile();
		var useragent = 'WEB';
		if(!isDataNull(profile) && !isDataNull(profile.getEmail())){
			var requestData = {
					"name" : profile.getName(),
					"email" : profile.getEmail().toLowerCase(),
					"token" : profile.getId(),
					"useragent" : useragent
				};

				makePostAjaxRequest("mobile/FederatedLogin/", requestData,
						onSuccessGoogleLogin, onFailureGoogleLogin);
		}else{
			debugLogs('Into Else of onSuccess for googleUser');
		}
	}

	function onSuccessGoogleLogin(responseData) {
		var responseCode = '';
		var responseUrl = '';
		responseUrl = 'parentdashboard?token=';
		$("#message").empty();
		debugLogs("onSuccessGoogleLogin Staus Code"
				+ responseData.Return.ResponseSummary.StatusCode)

		if (!isDataNull(responseData) && !isDataNull(responseData.Return)
				&& !isDataNull(responseData.Return.ResponseSummary)
				&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {
			responseCode = responseData.Return.ResponseSummary.StatusCode;
			responseMessage = responseData.Return.ResponseSummary.StatusMessage;
			if (responseCode === 'SUC01') {
				debugLogs("token received "
						+ responseData.Return.Results.token)
				debugLogs("redirected URL " + responseUrl
						+ responseData.Return.Results.token);
				window.location = responseUrl
						+ responseData.Return.Results.token;
			} else {
				debugLogs("final status code ::::::::  " + responseCode);
				$("#message").append(' <b>' + responseMessage + '</b>.');
				$("#unauthorizedLogin").css("display", "block");
			}
		} else {
			responseMessage = 'Error while authenticate user by External Servers, please try again after sometime';
			$("#message").append(' <b>' + responseMessage + '</b>.');
		}
	}

	function onFailureGoogleLogin(responseData) {
		responseMessage = 'Error while authenticate user by External Servers, please try again after sometime';
		$("#message").empty();
		$("#message").append(' <b>' + responseMessage + '</b>.');
		debugLogs("onFailure :::::::::::: " + responseData.status);
	}

	function onFailure(error) {
		debugLogs('Google Login Authentication Error:' + error);
	}
	
	//Federated Login Using Facebook
	function authUser() {
		FB.login(checkLoginStatus, {
			scope : 'email'
		});
	}

	function checkLoginStatus(response) {
		if (response && response.status == 'connected') {
			testAPI();
			debugLogs('Access Token: ' + response.authResponse.accessToken);
		} else {
			debugLogs('Invalid Facebook Login');
		}
	}

	window.fbAsyncInit = function() {
		FB.init({
			appId : facebook_id,
			cookie : true,
			xfbml : true,
			version : facebookAPIVersion
		});

	};

	// Load the SDK asynchronously
	(function(d, s, id) {
		var js, fjs = d.getElementsByTagName(s)[0];
		if (d.getElementById(id))
			return;
		js = d.createElement(s);
		js.id = id;
		js.src = "//connect.facebook.net/en_US/sdk.js";
		fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk'));

	function testAPI() {
		debugLogs('Welcome!  Fetching your information.... ');
		FB.api('/me', {
			fields : 'name,first_name,last_name,email'
		}, function(response) {
			var useragent = 'WEB';
			
			var requestData = {
				"name" : response.name,
				"email" : response.email,
				"token" : response.id,
				"useragent" : useragent
			};

			debugLogs("requestData :::::: " + JSON.stringify(requestData));
			makePostAjaxRequest("mobile/FederatedLogin/", requestData,
					onSuccessFacebookLogin, onFailureFacebookLogin);
		});
	}

	function onSuccessFacebookLogin(responseData) {
		var responseCode = '';
		var responseUrl = '';
		responseUrl = 'parentdashboard?token=';
		debugLogs("responseUrl is "+ responseUrl);
		$("#message").empty();
		debugLogs("onSuccessFacebookLogin Status Code"
				+ responseData.Return.ResponseSummary.StatusCode)
		if (!isDataNull(responseData) && !isDataNull(responseData.Return)
				&& !isDataNull(responseData.Return.ResponseSummary)
				&& !isDataNull(responseData.Return.ResponseSummary.StatusCode)) {
			responseCode = responseData.Return.ResponseSummary.StatusCode;
			if (responseCode === 'SUC01') {
				debugLogs("token received "
						+ responseData.Return.Results.token)
				window.location = responseUrl
						+ responseData.Return.Results.token;
			} else {
				responseMessage = responseData.Return.ResponseSummary.StatusMessage;
				$("#message").append(' <b>' + responseMessage + '</b>.');
				$("#unauthorizedLogin").css("display", "block");
			} 
		} else {
			responseMessage = 'Error while authenticate user by External Servers, please try again after sometime';
			$("#message").append(' <b>' + responseMessage + '</b>.');
		}
	}

	function onFailureFacebookLogin(responseData) {
		responseMessage = 'OOPS !! Something went wrong';
		$("#message").empty();
		$("#message").append(' <b>' + responseMessage + '</b>.');
		debugLogs("onFailure :::::::::::: " + responseData.status);
	}
