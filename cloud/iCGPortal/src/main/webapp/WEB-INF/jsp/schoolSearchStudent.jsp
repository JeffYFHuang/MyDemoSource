<%@include file="includes/taglib.jsp"%>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="com.liteon.icgwearable.util.PropertiesUtil"%>
<%@ page import="java.util.Properties"%>
<%
	Properties prop = PropertiesUtil.getProperties();
%>

<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglable_searchstudent"></h1>
			<ol class="breadcrumb-new" id="bread">
				<li><a href="schooldashboard?token=${sessionID}"
					id="school_dashboard"><span class="icglabel_dashboard"></span></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li><a href="#"><span class="icglable_searchstudent">
					</span></a></li>
			</ol>
			<h1 align="center" id="searchDetails"></h1>
		</section>
		<!-- body  content -->
		<section class="content">
			<section>
			<p class="third_subtitle icglable_studentlist"></p>
				<div class="row">
					<div class="col-md-12">
						<section class="user_account_mangmet">
						<div class="error-block text-center icglabel_searchCriteriaerror" id="searchCriteriaError"></div>
							<form class="form-inline">
								<div class="row txt-16">
									<div class="form-group col-lg-2 col-md-2 col-sm-12 col-xs-12">
										<label class="form-control-static" for="searchStudentName"><b
											class="third_subtitle icglable_searchby"></b></label>
									</div>
									<div class="form-group col-lg-2 col-md-4 col-sm-12 col-xs-12">
										<input type="text" class="form-control input-search-student"
											id="searchStudentName" name="searchStudentName" required
											title="Please enter Student Name" maxlength="45"
											Placeholder="Student Name" />
									</div>
									<div class="form-group col-lg-2 col-md-4 col-sm-12 col-xs-12">
										<input type="number" class="form-control input-search-student"
											id="searchStudentRegId" name="searchStudentRegId" required
											title="Please enter Registration No." maxlength="10"
											Placeholder="Registration No." />
									</div>
									<div class="form-group col-lg-2 col-md-4 col-sm-12 col-xs-12">
										<input type="text" class="form-control input-search-student school-serachInput"
											id="searchStudentUUID" name="searchStudentUUID" required
											title="Please enter UUID" maxlength="45" Placeholder="UUID" />
									</div>
									<div class="form-group col-lg-2 col-md-4 col-sm-12 col-xs-12">
										<button type="button"
											class="save form-group text-center btn-find school-serachInput icglabel_find"
											value="Find" id="findStudent" tabindex="4"></button>
									</div>
								</div>
							</form>
						</section>
					</div>
				</div>
				<section id="searchResultView" style="display: none">
					<section class="content-header-parent">
						<div class="row safari-fix">
							<div class="col-md-4 col-sm-12 col-xs-12"
								style="padding-left: 21px;">
								<p class="bread_heading_parent sub_title icglabel_searchresultview"></p>
							</div>
						</div>
					</section>
					<section class="user_account_mangmet">
						<div class="table-responsive">
							<table class="table">
								<thead>
									<tr>
										<th class="icglabel_name"></th>
										<th class="icglabel_registrationno"></th>
										<th class="icglabel_grade"></th>
										<th class="icglabel_class"></th>
										<th class="icglabel_uuid"></th>
										<th class="icglabel_emergencycontactno"></th>
										<th class="icglable_location"></th>
									</tr>
								</thead>
								<tbody id="stDetails">
								</tbody>
							</table>
						</div>
						<span id="pagination" class="pagination" style="display: none"> <span
							class="col-md-8"> <span class="icglabel_page"></span>&nbsp;<span
								id="student_startPage" style="text-align: left;"></span> <span
								class="icglabel_of" style="text-align: left;"></span> <span
								id="student_endPage" style="text-align: left;"></span>
						</span> <span class="col-md-4"><span class="pull-right"><span class="disabled pull-left"> <a
									id="decrease" onClick="decreasestudentHref()"><img
										src="resources/images/grey_drop_down.png"
										class="left-navigate img-responsive pull-left hand"></a></span> <span
								class="disabled pull-right"><a id="increase"
									onClick="increasestudentHref()"><img
										src="resources/images/grey_drop_down.png"
										class="right-navigate img-responsive hand"></a></span></span>
						</span>
						</span>
					</section>
				</section>
			</section>
		</section>
	</div>
</div>
<!-- Modal for Location Map View -->
<div class="modal" id="locationMap" role="dialog">
	<div class="modal-dialog  modal-lg">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" id="close_locationMap"
					data-dismiss="modal" style="font-size: 30px;">&#10005;</button>
				<h4 class="modal-title">
					<span class="h2 icglabel_location_map"></span>
				</h4>
			</div>
			<div class="modal-body">
				<div class="row safari-fix">
					<div class="col-lg-12 img-height" id="locationView"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- end of Modal for Location lMap View -->
<script src="resources/js/schoolSearchStudent.js"></script>
<script src='<%=prop.getProperty("Map_Url")%>'></script>

