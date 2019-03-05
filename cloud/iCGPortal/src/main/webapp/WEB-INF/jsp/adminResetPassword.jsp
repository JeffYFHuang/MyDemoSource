<%@include file="includes/taglib.jsp"%>
<link rel="stylesheet" href="resources/css/passwordReset.css" />
<section id="login-page">
	<div class="modal-dialog modal-lg" role="document">
		<div class="modal-content " style="height: auto !important;">
			<div class="alert alert-danger icglabel_mailnotsentmessage"
					id="pwdChangeError" data-error="${error}"></div>
			<div class="alert alert-success icglabel_passwordactivationmessage"
					id="pwdChangeSuccess" data-success="${message}"></div>
			<div class="alert alert-danger icglabel_passwordmismatch"
				id="inValidSignUpPassword"></div>
			<div class="modal-header">
				<h4 class="modal-title loginHeader icglabel_typenewpassword"></h4>
				<hr style="border-top: 1px solid #6A6363;"></hr>
			</div>
			<div class="modal-body">
				<form method="POST" name="resetForm"
					action="AdminPasswordReset?key=${activationLinkKey}"
					class="form-signin">
					<div class="form-group col-md-6"
						style="text-align: left; margin-top: auto;">
						<label for="password" class="loginLabel icglabel_newpassword"></label>
						<input placeholder="Password001" type="password"
							class="form-control inputLogin" id="password" name="password"
							data-minlength="10" maxlength="15" required>
					</div>
					<div class="form-group col-md-6" style="text-align: left;">
						<label for="cpassword" class="loginLabel icglabel_confirm"></label>
						<input type="password" placeholder="***********"
							class="form-control inputLogin" id="cpassword" name="cpassword"
							data-minlength="10" maxlength="15" required>
					</div>
					<div class="form-group col-md-12">
						<button type="submit"
							class="btn btn btn-primary btn-block btn-lg signInBtn icglabel_resetmypassword"
							id="signupbt"></button>
					</div>
				</form>
			</div>
			<br />
			<br />
			<br />
			<br />
			<div class="modal-footer row" style="text-align: center;">
				<div id="resetting" class="col-md-6"
					style="padding: 20px !important;">
					<div class="">
						<table class="table text-left table-font">
							<tbody>
								<tr class="text-muted" id="charLongCheck">
									<td><i class="fa fa-check" aria-hidden="true"
										id="checkChar"></i> <i class="fa fa-exclamation-triangle"
										aria-hidden="true" style="display: none;" id="warningChar"></i>
									</td>
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
				</div>
			</div>
		</div>
	</div>
</section>
<!-- /.login-box -->
<script src="resources/js/resetPassword.js"></script>