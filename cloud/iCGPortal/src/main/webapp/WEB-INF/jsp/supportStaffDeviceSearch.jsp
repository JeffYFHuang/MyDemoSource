<%@include file="includes/taglib.jsp"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglable_searchdevice"></h1>
		</section>
		<!-- body  content -->
		<section class="content">
			<section>
				<section class="content-header-parent">
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
							<p class="bread_heading_parent sub_title icglabel_devicelist"></p>
						</div>
						<div
							class="col-lg-4 col-md-4 col-sm-8 col-xs-8 col-lg-offset-3 col-md-offset-3 filter-device-tabs"
							style="">
							<p class="bread_heading_parent">
								<input type="text" class="form-control-user input-bottom"
									id="adminDeviceUUIDforFind" name="adminDeviceUUIDFilter"
									title="Please enter UUID" tabindex="9" />
							</p>
						</div>
						<div class="col-lg-1 col-md-1 col-sm-4 col-xs-4" style="">
							<p class="bread_heading_parent">
								<input type="button"
									class="save form-group text-center btn-actMgt" value="Find"
									id="adminDevicefindTeacher" tabindex="14"
									onclick="getDeviceDetails()" />
							</p>
						</div>
					</div>
				</section>
				<section class="user_account_mangmet">
					<div class="table-responsive">
						<table class="table">
							<thead>
								<tr>
									<th class="icglabel_uuid"></th>
									<th class="icglabel_schoolname"></th>
									<th class="icglabel_firmwareversion"></th>
									<th class="icglabel_devicemodel"></th>
									<th class="icglabel_addeddate"></th>
									<th class="icglabel_status"></th>
								</tr>
							</thead>
							<tbody id="deviceDataSection">
								<tr>
									<td colspan="6"><strong><span
											class="icglabel_nodata"></span></strong></td>
								</tr>
							</tbody>
						</table>
					</div>
				</section>
			</section>
		</section>
	</div>
</div>
<script src="resources/js/supportStaffDeviceSearch.js"></script>
