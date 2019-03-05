<%@page contentType="text/html; charset=UTF-8"%>
<link rel="stylesheet" href="resources/css/Custom.css" />
<link rel="stylesheet" href="resources/css/common.css" />
<!-- header closed-->
<header class="main-header">
	<div class="pull-left">
		<a href="home"><img src="resources/images/logo.png" alt="logo"
			class="img-responsive" /></a>
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