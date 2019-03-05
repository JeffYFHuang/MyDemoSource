<%@include file="includes/taglib.jsp"%>
<link rel="stylesheet" href="resources/css/signUp.css" />
<section id="signUpPage">
	<div class="row" id="signUpPage">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content " style="height: auto !important;">
				<div class="alert alert-danger icglabel_contact10digits"
					id="inValidContact"></div>
				<div class="alert alert-danger icglabel_invalidemail"
					id="inValidEmail"></div>
				<div class="alert alert-danger icglabel_userexists" id="userAccount"></div>
				<div class="alert alert-danger icglabel_empty_profilename"
					id="profileDetails"></div>
				<div class="alert alert-danger icglabel_empty_password"
					id="emptyPassword"></div>
				<div class="alert alert-danger icglabel_passwordmismatch"
					id="inValidSignUpPassword"></div>
				<div class="alert alert-danger icglabel_password_criteria_error"
					id="inValidPasswordFormat"></div>
				<div class="alert alert-danger icglabel_tou_criteria_error"
					id="acceptTOU"></div>
                <div class="alert alert-danger icglabel_unauthoriseusermessage" id="unauthorizedLogin"></div>
				<div class="alert alert-danger icglabel_errormsgactivation" id="errorBlock" data-success="${errorMsg}"></div>
				<div class="alert alert-danger icglabel_userexists" id="errorBlockExist" data-success="${ERR24}"></div>
				<div class="alert alert-success icglabel_signup_success"
					id="ValidSignUp"></div>
				<div class="modal-header">
					<h4 class="modal-title loginHeader icglabel_signuphere"></h4>
					<hr style="border-top: 1px solid #6A6363;"></hr>
				</div>
				<div class="modal-body">
					<div class="form-group col-md-6"
						style="text-align: left; margin-top: auto;">
						<label for="username" class="loginLabel icglabel_username"></label>
						<input placeholder="Username/Email" type="email"
							class="form-control inputLogin" id="username" data-minlength="10"
							maxlength="145" required tabindex="1" onkeyup="return forceLower(this);">
					</div>
					<div class="form-group col-md-6" style="text-align: left;">
						<label for="password" class="loginLabel icglabel_password"></label>
						<input type="password" placeholder="Password"
							class="form-control inputLogin" id="password" data-minlength="10"
							maxlength="15" required tabindex="4">
					</div>
					<div class="form-group col-md-6"
						style="text-align: left; margin-top: auto;">
						<label for="accountname" class="loginLabel icglabel_profile"></label>
						<input placeholder="Eg: John Smith" type="text"
							class="form-control inputLogin" id="accountname"
							data-minlength="8" maxlength="45" required tabindex="2">
					</div>
					<div class="form-group col-md-6" style="text-align: left;">
						<label for="confpassword" class="loginLabel icglabel_confirm"></label>
						<input type="password" data-minlength="10" maxlength="15"
							placeholder="Password" class="form-control inputLogin"
							id="confpassword" required tabindex="5">
					</div>
					<div class="form-group col-md-6" style="text-align: left;">
						<label for="contactnumber" class="loginLabel icglabel_contact"></label>
						<input type="number" maxlength="10" placeholder="09493848373"
							class="form-control inputLogin contactnumber" id="contactnumber" required
							tabindex="3">
					</div>
					<div id="resetting" class="form-group col-md-6">
						<table class="text-left table-font">
							<tbody>
								<tr class="text-muted" id="charLongCheck">
									<td><i class="fa fa-check" aria-hidden="true"
										id="checkChar"></i> <i class="fa fa-exclamation-triangle"
										aria-hidden="true" style="display: none;" id="warningChar"></i></td>
									<td class="icglabel_contain8char"></td>
								</tr>
								<tr class="text-muted" id="letterCheck">
									<td><i class="fa fa-check" aria-hidden="true"
										id="checkLetter"></i> <i class="fa fa-exclamation-triangle"
										aria-hidden="true" style="display: none;" id="warningLetter"></i></td>
									<td class="icglabel_containletter"></td>
								</tr>
								<tr class="text-muted" id="numberCheck">
									<td><i class="fa fa-check" aria-hidden="true"
										id="checkNumber"></i> <i class="fa fa-exclamation-triangle"
										aria-hidden="true" style="display: none;" id="warningNumber"></i></td>
									<td class="icglabel_containnumber"></td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="checkbox col-md-12">
						<label class="col-xs-12"> <input type="checkbox"
							value="remember-me" id="remember_me"
							style="float: left !important;" tabindex="6"> <span
							class="loginLabel"><span class="icglabel_agree"></span><a
								href="#" class="text-primary icglabel_termsofuse"></a> &amp; <a
								href="#" class="text-primary icglabel_privacypolicy"></a>. </span>
						</label>
					</div>
					<div class="form-group row">
						<button type="submit"
							class="btn btn btn-primary btn-block btn-lg signInBtn"
							id="signupbt">Sign up</button>
					</div>
				</div>
				<br />
				<div class="modal-footer row" style="text-align: center;">
					<a class="btn backtoLoginBtn icglabel_backtologin" id="backBtn"
						href="login"> <!-- <span class="glyphicon glyphicon-backward"></span> -->
						<i class="fa fa-angle-double-left" aria-hidden="true"></i>
					</a>
					<div style="float: left; width: 100%;">
						<hr class="style17" />
					</div>
					<div class="row">
						<div class="col-md-6 ">
							<img src="resources/images/FB_login_button.png" alt="facebook"
								class="img-responsive" style="width: 100%;" id="loginButton"
								onclick="authUser();" />
						</div>
						<div class="col-md-6 col-google">
							<img id="customBtn"
								src="resources/images/google_login_button.png" alt="google"
								class="img-responsive" style="width: 100%;" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
var google_id;
var facebook_id;
$(document).ready(function(){
	google_id = "<%=request.getAttribute("googleId")%>";
	facebook_id = "<%=request.getAttribute("facebookId")%>";
	facebookAPIVersion = "<%=request.getAttribute("facebookAPIVersion")%>";
	});
</script>
<script src="https://apis.google.com/js/api:client.js"></script>
<script src="resources/js/signUp.js"></script>
<script src="resources/js/federated-login.js"></script>