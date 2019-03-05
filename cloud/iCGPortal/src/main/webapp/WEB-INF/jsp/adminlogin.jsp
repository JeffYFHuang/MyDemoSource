<%@include file="includes/taglib.jsp"%>
<!-- Login -->
<link rel="stylesheet" href="resources/css/login.css" />
<section id="login-page">
	<div class="row" id="login-page">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content" style="height: auto;">
				<div class="alert alert-danger ${error}" id="errorBlock" data-errorBlock="${error}"></div>
				<div class="alert alert-danger icglabel_mailnotsent"
					id="pwdResetError" data-pwdResetError="${mailNotSent}"></div>
				<div class="alert alert-success" id="pwdResetSuccess" data-pwdResetSuccess="${message}"></div>
				<div class="alert alert-success icglabel_accountactivationsuccess" id="errorBlockSuc" data-errorBlockSuc="${successMsg}"></div>
				<div class="alert alert-success icglabel_loginsuccess"
					id="pwdChangeBlock" data-pwdChangeBlock="${passwordChangeSuccess}"></div>
				<div class="alert alert-danger icglabel_loginerr" id="invalidLogin"></div>
				<div class="modal-header">
					<h4 class="modal-title loginHeader icglabel_login"></h4>
					<hr style="border-top: 1px solid #6A6363;"></hr>
				</div>
				<div class="modal-body">
					<form method="POST" id="login" action="adminlogin" class="form-signin">
						<div class="form-group col-md-6"
							style="text-align: left; margin-top: auto;">
							<label for="emailid" class="loginLabel icglabel_username"></label>
							<input placeholder="test@mail.com" type="text"
								class="form-control inputLogin" name="username" id="emailid"
								data-minlength="10" maxlength="145" required onkeyup="return forceLower(this);">
						</div>
						<div class="form-group col-md-6" style="text-align: left;">
							<label for="password" class="loginLabel icglabel_password"></label>
							<input type="password" placeholder="********"
								class="form-control inputLogin" name="password" id="password"
								data-minlength="10" maxlength="15" required>
						</div>
						<div class="checkbox col-md-12">
							<label class="col-md-6"> <input type="checkbox"
								id="remember_me"> <span
								class="loginLabel icglabel_remember"> </span>
							</label> <label class="forgotLink col-md-6" style="text-align: right;">
								<a href="adminResetPassword?resetPassword=resetPassword"
								class="loginLabel icglabel_forgotPassword" href="resetPassword"></a>
							</label>
						</div>
						<div class="form-group row">
							<button type="submit"
								class="btn btn btn-primary btn-block btn-lg signInBtn icglabel_login"
								id="loginBtn" name="login" value="login"></button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	<!-- /.login-box -->
</section>
<script src="resources/js/login.js"></script>
