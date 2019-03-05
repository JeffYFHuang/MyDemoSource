<%@include file="includes/taglib.jsp"%>
<link rel="stylesheet" href="resources/css/forgotPassword.css" />
<section id="login-page">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content" style="height: auto;">
			<div class="alert alert-danger icglabel_mailnotsentmessage"
					id="pwdChangeError" data-error="${error}"></div>
			<div class="alert alert-success icglabel_passwordactivationmessage"
					id="pwdChangeSuccess" data-success="${message}"></div>
			<div class="alert alert-danger icglabel_forgoterr" id="inValidEmail"></div>
			<div class="modal-header">
				<h4 class="modal-title loginHeader icglabel_resetPassword"></h4>
				<hr style="border-top: 1px solid #6A6363;"></hr>
			</div>
			<div class="modal-body">
				<span class="text-center reset-font icglabel_resettxt"></span>
				<form method="POST" name="resetForm" action="adminResetPassword"
					class="form-signin">
					<div class="form-group col-md-12" style="">
						<label for="username" class="loginLabel"></label> <input
							type="text" placeholder="test@mail.com"
							class="form-control inputLogin" id="emailid" name="username"
							data-minlength="10" maxlength="145" onkeyup="return forceLower(this);">
					</div>
					<div class="form-group col-md-12">
						<button type="submit"
							class="btn btn btn-primary btn-block btn-lg signInBtn icglabel_recovery_email"
							id="resetPassword" value="resetPassword"></button>
					</div>
				</form>
			</div>
			<div class="modal-footer row" style="text-align: center;">
				<a class="btn backtoLoginBtn icglabel_backtologin" id="backBtn"
					href="adminlogin"></a>
			</div>
		</div>
	</div>
	<!-- Bootstrap 3.3.6 -->
</section>
<script src="resources/js/forgotPassword.js"></script>
