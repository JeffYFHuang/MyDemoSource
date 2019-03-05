<%@include file="includes/taglib.jsp"%>
<link rel="stylesheet" href="resources/css/login.css">
<section id="login-page">
	<div class="row" id="login-page">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content" style="height: auto;">
				<div class="modal-header">
					<h4 class="modal-title loginHeader icglabel_icgtitle"></h4>
					<hr style="border-top: 1px solid #6A6363;">
				</div>
				<div class="modal-body">
					<div class="text" style="color: #333;">
						<p class="txt-16 text-bold icglabel_icgsubtitle"></p>
						<br />
						<p class="txt-12 icglabel_icgdescription"></p>
					</div>
				</div>
				<div class="modal-footer login-footer">
					<div class="row">
						<div class="col-md-6 col-lg-6 col-xs-12 col-sm-12  ">
							<a href="adminlogin"
								class="btn btn-primary btn-block btn-lg btn-login icglabel_schoollogin"></a>
						</div>
						<div class="col-md-6 col-lg-6 col-xs-12 col-sm-12  col-google">
							<a href="login"
								class="btn btn-primary btn-block btn-lg btn-login icglabel_parentlogin"></a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- /.login-box -->
</section>