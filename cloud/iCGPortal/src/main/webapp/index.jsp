<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Welcome to iCG Wearable Cloud Services</title>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>LITE-ON</title>
<!-- Tell the browser to be responsive to screen width -->
<meta
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"
	name="viewport" />
<!-- Bootstrap 3.3.6 -->
<link rel="stylesheet" href="resources/css/lib/bootstrap.css" />
<!-- Font Awesome -->
<link rel="stylesheet"
	href="resources/fonts/font-awesome/font-awesome.min.css" />
<!-- Ionicons -->
<link rel="stylesheet" href="resources/fonts/ionicons/ionicons.min.css" />
<!-- Theme style -->
<link rel="stylesheet" href="resources/css/AdminLTE.css" />
<link rel="stylesheet" href="resources/css/skins/skin-green.css" />
<!-- less & sass css -->
<link rel="stylesheet" href="resources/css/less/normalize.css" />
<link rel="stylesheet" href="resources/css/less/Scaffolding.less.css" />
<link rel="stylesheet"
	href="resources/css/less/mixins/Vendor Prefixes.less.css" />
<link rel="stylesheet" href="resources/css/less/breadcrumbs.less.css" />
<link rel="stylesheet" href="resources/css/less/forms.less.css" />
<link rel="stylesheet" href="resources/css/Roboto.css" />
<link rel="stylesheet" href="resources/fonts/OpenSans-SemiBold.ttf" />
<!-- jQuery 2.2.3 -->
<script src="resources/js/lib/jquery-2.2.3.min.js"></script>
<script src="resources/js/lib/jquery-ui.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="resources/js/lib/bootstrap.js"></script>
<!-- AdminLTE App -->
<script src="resources/js/lib/app.js"></script>
<script src="resources/js/lib/bootstrap-datepicker.js"></script>
<link rel="stylesheet"
	href="resources/css/lib/bootstrap-datetimepicker.min.css" />
<link href="resources/css/lib/datepicker.css" rel="stylesheet"
	type="text/css" />
<link rel="stylesheet" href="resources/css/login.css">
<link rel="stylesheet" href="resources/css/Custom.css" />
</head>
<body class="hold-transition skin-green" onload="">
	<!-- wrapper open -->
	<div class="wrapper" style="height: auto !important;">
		<!-- header closed-->
		<header class="main-header">
			<div class="pull-left">
				<img src="resources/images/logo.png" alt="logo"
					class="img-responsive" />
			</div>
			<!-- 	<span class="text-right" style="font-size: 25px; color: white;">LITEON
				LOGO</span> -->
			<div class="pull-right text-center">
				<form>
					<select id="selectBox" class="form-control selectBox"
						onchange="changeFunc();">
						<option value="en">English</option>
						<option value="cn-simplified">简体中文</option>
						<option value="cn-traditional">繁體中文</option>
					</select>
				</form>
			</div>
		</header>
		<!-- header closed-->
		<section id="index-page">
			<div class="container box-container " style="height: auto;">
				<section id="login-page">
					<div class="row" id="login-page">
						<div class="modal-dialog modal-lg" role="document">
							<div class="modal-content" style="height: auto;padding: 35px 82px;">
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
				<footer class="row text-center">
					<div class="footer-text">
						<p class="pull-left"></p>
						<p class="pull-right">
							<a href="#" data-toggle="modal" data-target="#PrivacyModal"
								class="icglabel_privacyprotection"></a><span
								class="icglabel_allrights"></span>
						</p>
					</div>
				</footer>
			</div>
		</section>
	</div>
	<!-- #PrivacyModal Start -->
	<div class="modal fade" id="PrivacyModal" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<div class="modal-content privacy-content">
				<div class="modal-header">
					<h4 class="modal-title">
						<img src="resources/images/alert_icon.png" class="img-responsive"
							alt="alert" style="float: left;" /><span
							class="heading icglabel_privacypolicycaps"> </span>
					</h4>
				</div>
				<div class="modal-body">
					<p class="icglabel_privacyprotection"></p>
					<p class="icglabel_privacypara1"></p>
					<p class="icglabel_privacyprotection"></p>
					<p>
					<p class="icglabel_privacypara2"></p>
					<p class="icglabel_privacypara3"></p>
					</p>
				</div>
				<div class="modal-footer privacy-footer">
					<button type="button" class="modal-Close icglabel_close"
						data-dismiss="modal"></button>
				</div>
			</div>
		</div>
	</div>
	<!-- #PrivacyModal End -->
	<script src="resources/js/common.js"></script>
	<script src="resources/js/client-storage.js"></script>
	<script type="text/javascript">
		var userRole = 'common';
	</script>
</body>
</html>