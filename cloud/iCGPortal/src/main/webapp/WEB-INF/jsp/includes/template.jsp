<%@include file="taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title><tiles:insertAttribute name="title" ignore="true" /></title>
<tiles:insertAttribute name="head" />
</head>
<body class="hold-transition skin-green" onload="">
<div id="overlay"></div>
<div id="spinner"><img src='resources/images/icons/ajax-loader.gif'/></div>
	<!-- wrapper open -->
	<div class="wrapper" style="height: auto !important;">
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="nav" />
		<tiles:insertAttribute name="main" />
		<tiles:insertAttribute name="footer" />
	</div>
</body>
</html>