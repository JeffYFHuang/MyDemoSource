<%@include file="includes/taglib.jsp"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_schoolsearch"></h1>
		</section>
		<!-- body  content -->
		<section class="content">
			<section>
				<div class="row">
					<div class="col-md-12">
						<section class="user_account_mangmet">
							<form class="form-inline">
								<div class="row txt-16">
									<div
										class="form-group col-lg-2 col-md-3 col-sm-12 col-xs-12 ipad-mrg">
										<label
											class="form-control-static col-lg-12 col-md-12 col-sm-12 col-xs-12"><b
											class="third_subtitle icglabel_schoolfilterby"></b></label>
									</div>
									<div
										class="form-group col-lg-4 col-md-3 col-sm-12 col-xs-12 ipad-mrg">
										<p class="form-group col-lg-12 col-md-12 col-sm-12 col-xs-12">
											<select tabindex="1" class="form-control-user"
												id="adminDeviceCountyFilter" onchange="loadSchoolNames()">
												<option value='' class="icglabel_schoolcounty"></option>
												<c:forEach items="${countyList}" var="county">
													<option>${county}</option>
												</c:forEach>
											</select>
										</p>
									</div>
									<div
										class="form-group col-lg-4 col-md-3 col-sm-12 col-xs-12 ipad-mrg">
										<p class="form-group col-lg-12 col-md-12 col-sm-12 col-xs-12">
											<select tabindex="2" class="form-control-user"
												id="adminDeviceSchoolNameFilter">
												<option value='' class="icglabel_schoolname"></option>
											</select>
										</p>
									</div>
									<div
										class="form-group col-lg-2 col-md-1 col-sm-6 col-xs-6 ipad-mrg">
										<p class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
											<input type="button"
												class="save form-group text-center btn-actMgt" value="Go"
												id="adminDeviceGo" tabindex="14"
												onclick="getDeviceStatDetails()" />
										</p>
									</div>
								</div>
							</form>
						</section>
					</div>
				</div>
				<section id="searchResultView" style="display: none">
					<section class="content-header-parent">
						<div class="row">
							<div class="col-md-4 col-sm-12 col-xs-12">
								<p class="bread_heading_parent icglabel_searchresultview"></p>
							</div>
						</div>
					</section>
					<section class="user_account_mangmet">
						<div class="table-responsive">
							<table class="table">
								<thead>
									<tr>
										<th class="icglable_id"></th>
										<th class="icglabel_address"></th>
										<th class="icglabel_contactno"></th>
										<th class="icglabel_adminusername"></th>
										<th class="icglable_allocateddevice"></th>
										<th class="icglable_lastlogin"></th>
									</tr>
								</thead>
								<tbody id="searchSchoolDetails">
								</tbody>
							</table>
						</div>
					</section>
				</section>
			</section>
		</section>
	</div>
</div>
<script src="resources/js/supportStaffSchoolSearch.js"></script>