<%@page contentType="text/html; charset=UTF-8"%>
<footer class="main-footer">
	<div class="row">
		<div class="col-md-4">
			<p class="copyrights icglabel_allcopyright"></p>
		</div>
		<div class="col-md-8">
			<ul class="footer_navigation">
				<li class="terms_condt"><a href="#" data-toggle="modal"
					data-target="#TnCModal" class="icglabel_termCondition"></a></li>
				<li><a href="#" data-toggle="modal" data-target="#PrivacyModal"
					class="icglabel_privacy"></a></li>
				<li><a href="#" data-toggle="modal"
					data-target="#CopyrightModal" class="icglabel_copyright"></a></li>
			</ul>
		</div>
	</div>
</footer>
</div>
<!-- #TnCModal Start -->
<div class="modal fade" id="TnCModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">
					<img src="resources/images/alert_icon.png" class="img-responsive"
						alt="alert" style="float: left;" /><span
						class="heading icglabel_termCondition"> </span>
				</h4>
			</div>
			<div class="modal-body">
				<p class="icglabel_personalinfo"></p>
				<p class="icglabel_personalinfoans"></p>
			</div>
			<div class="modal-footer">
				<button type="button" class="modal-Close icglabel_close"
					data-dismiss="modal"></button>
			</div>
		</div>
	</div>
</div>
<!-- #TnCModal End -->
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
<!-- #CopyrightModal Start -->
<div class="modal fade" id="CopyrightModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">
					<img src="resources/images/alert_icon.png" class="img-responsive"
						alt="alert" style="float: left;" /><span
						class="heading icglabel_copyright"></span>
				</h4>
			</div>
			<div class="modal-body">
				<p class="icglabel_copyrightcontent"></p>
			</div>
			<div class="modal-footer">
				<button type="button" class="modal-Close icglabel_close"
					data-dismiss="modal"></button>
			</div>
		</div>
	</div>
</div>
<!-- #CopyrightModal End -->
<script src="resources/js/common.js"></script>
<script src="resources/js/client-storage.js"></script>
<script type="text/javascript">
	var userRole = '<%=session.getAttribute("currentUser")%>';
</script>