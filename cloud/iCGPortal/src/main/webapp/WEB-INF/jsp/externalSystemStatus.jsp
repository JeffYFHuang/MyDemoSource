<%@include file="includes/taglib.jsp"%>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script src="resources/js/externalSystemStatus.js"></script>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
	<section class="content-header">
			<h1 class="icglable_externalsystemstatus"></h1>
		</section>
		<!-- body  content -->
		<section class="content">
			<!--Start of Student PROFILE List-->
			<section class="user_account_mangmet">
				<div class="table-responsive">
					<table class="table">
						<thead>
							<tr>
								<th class="icglable_beaconmac"></th>
								<th class="icglable_beaconname"></th>
								<th class="icglable_beaconversionnum"></th>
								<th class="icglable_noofbeacons"></th>
								<th class="icglable_beacondevicestatus"></th>
								<th class="icglable_beaconschoolname"></th>
							</tr>
						</thead>
						<tbody id="beaconStatus">
						</tbody>
					</table>
				</div>
				<span id="pagination" class="pagination" style="display:none">
				<span class="col-md-8"> <span class="icglabel_page"></span>&nbsp;<span
					id="assigned_startPage" style="text-align: left;"></span> <span
					class="icglabel_of" style="text-align: left;"></span> <span
					id="assigned_endPage" style="text-align: left;"></span>
				</span> <span class="col-md-4"><span class="pull-right"><span class="disabled pull-left">  <a
						id="decrease" onClick="decreaseassigendHref()"><img
							src="resources/images/grey_drop_down.png"
							class="left-navigate img-responsive pull-left"></a></span> <span
					class="disabled pull-right"><a id="increase"
						onClick="increaseassigendHref()"><img
							src="resources/images/grey_drop_down.png"
							class="right-navigate img-responsive"></a></span></span>
				</span></span>
			</section>
		</section>
	</div>
</div>
