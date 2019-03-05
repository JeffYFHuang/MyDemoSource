<%@include file="includes/taglib.jsp"%>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script src="resources/js/lib/moment.js"></script>
<script src="resources/js/date-formatter.js"></script>
<script src="resources/js/json-sort.js"></script>
<script src="resources/js/reward.js"></script>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_reward"></h1>
			<ol class="breadcrumb-new" id="bread">
				<li><a href="schooldashboard?token=${sessionID}"
					id="school_dashboard"><span class="icglabel_dashboard"></span></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li><span class="icglabel_reward"></span><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li><span class="icglabel_rewardranking dynamicbread"></span></li>
			</ol>
		</section>
		<section>
			<div class="navbar navbar-default">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle"
						data-target="#school-navbar" data-toggle="collapse">
						<span class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
				</div>
				<div class="navbar-collapse collapse" id="school-navbar">
					<ul class="nav navbar-nav">
						<li><a class="tabs active icglabel_rewardranking"
							id="rewardRanking" href="#"></a></li>
						<li><a class="tabs icglabel_rewardmanagement" id="rewardMgt"
							href="#"></a></li>
					</ul>
				</div>
			</div>
		</section>
		<!-- body  content -->
		<section class="content">
			<!-- 	Reward Ranking starts here -->
			<section id="ranking-section">
				<section class="content-header-parent sub_title dates-section">
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12"
							style="padding-left: 21px;">
							<div class="bread_heading_parent">
								<div class="col-md-7 navigation pull-left">
									<span class="previousDates content-flaot"><img
										src="resources/images/grey_drop_down.png"
										class="left-navigate img-responsive fc-button fc-prev-button hand" /></span><span
										class="pull-left selectedDateRange" id="duration"></span><span
										class="nextDates content-flaot"><img
										src="resources/images/grey_drop_down.png"
										class="right-navigate img-responsive fc-button hand" /></span>
								</div>
								<div class="col-md-5 duration">
									<span class="pull-right"><span
										style="padding-right: 35px;"><a id="rewardweek"
											class="weekDate fc-button selected-date hand icglabel_week"></a></span><span
										class="pull-right"><a id="rewardmonth"
											class="monthDate fc-button fc-next-button hand icglabel_month"></a></span></span>
								</div>
							</div>
						</div>
					</div>
				</section>
				<section id="fitness-filter" class="search-header">
					<div class="row">
						<div class="col-lg-10 col-md-9 col-sm-6 col-xs-12">
							<strong><span class="font-reg-roboto" style="padding-left: 15px;font-size: 54px !important;"
								id="total_rewrad_count"></span></strong>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-6 col-xs-12">
							<select class="grade fitness-header"
								id="schoolRewardRankingGrade" tabindex="1">
							</select>
						</div>
						<hr class="col-lg-12 col-md-12 col-xs-12 col-sm-12 search-header-divider" />
					</div>
				</section>
				<section class="user_account_mangmet">
					<div class="row"
						style="height: 220px; overflow-y: hidden; white-space: nowrap; overflow-x: auto;">
						<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12"
							id="rewardCatStatistics"></div>
					</div>
				</section>
				<br />
				<section class="user_account_mangmet" style="display: none"
					id="rewardListContent">
					<div class="row"
						style="overflow-y: hidden; white-space: nowrap; overflow-x: auto;">
						<table class="table">
							<thead>
								<tr id="rewardCatListHeading">
								</tr>
							</thead>
							<tbody id="rewardCatListContent">
							</tbody>
						</table>
					</div>
				</section>
			</section>
			<!-- 	Reward Ranking ends here -->
			<!-- 	Reward Management starts here -->
			<section id="management-section" style="display: none;">
				<section class="content-header-parent">
					<p
						class="bread_heading_parent sub_title icglabel_addrewardcategory"></p>
				</section>
				<section class="user_account_mangmet">
					<div id="rewardsCategory_add_success"
						style="text-align: center; display: none">
						<strong><font color="green"
							class="txt-12 icglabel_rewardsCategory_add_success"></font></strong>
					</div>
					<div id="rewardsCategory_add_failure"
						style="text-align: center; display: none">
						<strong><font color="red"
							class="txt-12 icglabel_rewardsCategory_add_failure"></font></strong>
					</div>
					<div id="rewardsCategory_update_success"
						style="text-align: center; display: none">
						<strong><font color="green"
							class="txt-12 icglabel_rewardsCategory_update_success"></font></strong>
					</div>
					<div id="rewardsCategory_update_failure"
						style="text-align: center; display: none">
						<strong><font color="red"
							class="txt-12 icglabel_rewardsCategory_update_failure"></font></strong>
					</div>
					<div id="rewardsCategory_delete_success"
						style="text-align: center; display: none">
						<strong><font color="green"
							class="txt-12 icglabel_rewardsCategory_delete_success"></font></strong>
					</div>
					<div id="rewardsCategory_delete_failure"
						style="text-align: center; display: none">
						<strong><font color="red"
							class="txt-12 icglabel_rewardsCategory_delete_failure"></font></strong>
					</div>
					<div id="maxFileSize_formMsg"
						style="text-align: center; display: none">
						<strong><font color="red"
							class="txt-12 icglabel_maxFileSize_formMsg"></font></strong>
					</div>
					<div id="maxCategoryIn_formMsg"
						style="text-align: center; display: none">
						<strong><font color="red"
							class="txt-12 icglabel_maxCategoryIn_formMsg"></font></strong>
					</div>
					<p class="txt-12"
						style="text-align: center; color: red; font-weight: bold;"
						id="rewardCategory_formMsg"></p>
					<p class="txt-12"
						style="text-align: center; color: red; font-weight: bold;"
						id="editrewardCategory_formMsg"></p>
					<p class="txt-12"
						style="text-align: center; color: red; font-weight: bold;"
						id="deleterewardCategory_formMsg"></p>
					<p class="txt-12"
						style="text-align: center; color: red; font-weight: bold;"
						id="maxCategory_formMsg"></p>
					<p class="txt-12"
						style="text-align: center; color: red; font-weight: bold;"
						id="maxFileSize_formMsg"></p>
					<div class="row">
						<form id="rewardCategory_form">
							<div class="form-group col-lg-3 col-md-4 col-sm-12 col-xs-12">
								<label for="addRewardname"
									class="control-label icglabel_namecolon"></label> <input
									type="text" class="form-control" id="addRewardname"
									maxlength="45" placeholder="Reward Name" name="addRewardname"
									tabindex="1"
									onKeyPress="if(this.value.length==45) return false;"><span
									class="error-block icglabel_rewardCategoryName_err"
									id="rewardCategoryName_err"></span>
							</div>
							<div class="form-group col-lg-3 col-md-4 col-sm-12 col-xs-12">
								<label for="addRewardIcon"
									class="control-label icglabel_iconcolon"></label>
								<div class="input-group">
									<input id="file_attachment" name="pdf" accept=".png,.jpeg,.jpg"
										type="file"><span
										class="error-block icglabel_rewardCategoryIcon_err"
										id="rewardCategoryIcon_err"></span>
								</div>
							</div>
							<!-- Button -->
							<div class="form-group col-lg-3 col-md-3 col-sm-6 col-xs-12">
								<button class="save form-group btn-addCat icglable_addcategory"
									value="Add Category" id="rewardCategorysubmit"
									name="rewardCategorysubmit" tabindex="7"></button>
							</div>
						</form>
					</div>
				</section>
				<section class="content-header-parent">
					<p
						class="bread_heading_parent sub_title icglabel_rewardcategorylist"></p>
				</section>
				<section class="user_account_mangmet">
					<div class="row"
						style="overflow-y: hidden; white-space: nowrap; overflow-x: auto;">
						<table class="table table-bordered" id="rewardCategoryList">
							<thead>
								<tr>
									<th class="icglabel_srno"></th>
									<th class="icglabel_name"></th>
									<th class="icglabel_icon"></th>
									<th class="icglabel_edit"></th>
									<th class="icglabel_delete"></th>
								</tr>
							</thead>
							<tbody id="rewardCategoryWebSection">
							</tbody>
						</table>
					</div>
				</section>
				<section id="reward_category_tabs">
					<section class="content-header-parent">
						<p class="bread_heading_parent sub_title icglabel_reward"></p>
					</section>
					<section>
						<div class="navbar navbar-default">
							<div class="navbar-header">
								<button type="button" class="navbar-toggle"
									data-target="#reward-navbar" data-toggle="collapse">
									<span class="icon-bar"></span> <span class="icon-bar"></span> <span
										class="icon-bar"></span>
								</button>
							</div>
							<div class="navbar-collapse collapse" id="reward-navbar">
								<ul class="nav navbar-nav">
								</ul>
							</div>
						</div>
					</section>
					<section class="user_account_mangmet">
						<div class="row"
							style="overflow-y: hidden; white-space: nowrap; overflow-x: auto;">
							<table class="table table-bordered" id="rewardType">
								<thead>
									<tr>
										<th class="icglabel_srno"></th>
										<th class="icglabel_name"></th>
										<th class="icglabel_icon"></th>
										<th class="icglabel_edit"></th>
										<th class="icglabel_delete"></th>
									</tr>
								</thead>
								<tbody id="rewardWebSection">
								</tbody>
							</table>
						</div>
						<br />
						<hr />
						<div id="rewardsCategoryIn_add_success"
							style="text-align: center; display: none">
							<strong><font color="green"
								class="txt-12 icglabel_rewardsCategoryIn_add_success"></font></strong>
						</div>
						<div id="rewardsCategoryIn_add_failure"
							style="text-align: center; display: none">
							<strong><font color="red"
								class="txt-12 icglabel_rewardsCategoryIn_add_failure"></font></strong>
						</div>
						<div id="rewardsCategoryIn_update_success"
							style="text-align: center; display: none">
							<strong><font color="green"
								class="txt-12 icglabel_rewardsCategoryIn_update_success"></font></strong>
						</div>
						<div id="rewardsCategoryIn_update_failure"
							style="text-align: center; display: none">
							<strong><font color="red"
								class="txt-12 icglabel_rewardsCategoryIn_update_failure"></font></strong>
						</div>
						<div id="rewardsCategoryIn_delete_success"
							style="text-align: center; display: none">
							<strong><font color="green"
								class="txt-12 icglabel_rewardsCategoryIn_delete_success"></font></strong>
						</div>
						<div id="rewardsCategoryIn_delete_failure"
							style="text-align: center; display: none">
							<strong><font color="red"
								class="txt-12 icglabel_rewardsCategoryIn_delete_failure"></font></strong>
						</div>
						<div id="maxFileSizeIn_formMsg"
							style="text-align: center; display: none">
							<strong><font color="red"
								class="txt-12 icglabel_maxFileSizeIn_formMsg"></font></strong>
						</div>
						<p style="text-align: center; color: red; font-weight: bold;"
							id="rewardCategoryIn_formMsg"></p>
						<p style="text-align: center; color: red; font-weight: bold;"
							id="editrewardCategoryIn_formMsg"></p>
						<p style="text-align: center; color: red; font-weight: bold;"
							id="deleterewardCategoryIn_formMsg"></p>
						<div class="row">
							<form id="rewardCategoryIn_form">
								<div class="form-group col-lg-3 col-md-4 col-sm-12 col-xs-12">
									<label for="addRewardname"
										class="control-label icglabel_namecolon"></label> <input
										type="text" class="form-control" id="addRewardInname"
										placeholder="Reward Name" name="addRewardInname" tabindex="1"
										onKeyPress="if(this.value.length==45) return false;"><span
										class="error-block icglabel_rewardCategoryInName_err"
										id="rewardCategoryInName_err"></span>
								</div>
								<div class="form-group col-lg-3 col-md-4 col-sm-12 col-xs-12">
									<label for="addRewardIcon"
										class="control-label icglabel_iconcolon"></label>
									<div class="input-group">
										<input id="fileIn_attachment" name="pdfIn"
											accept=".png,.jpeg,.jpg" type="file"><span
											class="error-block icglabel_rewardCategoryInIcon_err"
											id="rewardCategoryInIcon_err"></span>
									</div>
								</div>
								<!-- Button -->
								<div class="form-group col-lg-3 col-md-3 col-sm-6 col-xs-12">
									<button class="save form-group btn-addCat icglable_addreward"
										value="Add Reward" id="rewardCategoryInsubmit"
										name="rewardCategoryInsubmit" tabindex="7"></button>
								</div>
							</form>
						</div>
					</section>
				</section>
			</section>
			<!-- 	Reward Management ends here -->
		</section>
	</div>
	<div class="modal fade" id="deleteCategoryDetails" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<!--  <button type="button" class="close" data-dismiss="modal">&times;</button> -->
					<h4 class="modal-title"></h4>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-md-12">
							<span class="ui-icon ui-icon-alert"
								style="float: left; margin: 12px 12px 20px 0;"></span><span
								class="icglabel_deletedrewardcategoryconirmation"></span>
						</div>
					</div>
					<div class="row" style="padding: 15px;">
						<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
							<div class="form-group">
								<button type="button" class="confirm pull-right icglabel_ok"
									data-dismiss="modal" id="rewardCDeleteId"></button>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
							<div class="form-group">
								<button type="button"
									class="modal-Cancel pull-left icglabel_cancel"
									data-dismiss="modal"></button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="deleteCategoryInDetails" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<!--  <button type="button" class="close" data-dismiss="modal">&times;</button> -->
					<h4 class="modal-title"></h4>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-md-12">
							<span class="ui-icon ui-icon-alert"
								style="float: left; margin: 12px 12px 20px 0;"></span><span
								class="icglabel_deletedrewardconirmation"></span>
						</div>
					</div>
					<div class="row" style="padding: 15px;">
						<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
							<div class="form-group">
								<button type="button" class="confirm pull-right icglabel_ok"
									data-dismiss="modal" id="rewardCInDeleteId"></button>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
							<div class="form-group">
								<button type="button"
									class="modal-Cancel pull-left icglabel_cancel"
									data-dismiss="modal"></button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="editCategoryDetails" tabindex='-1'
		role="dialog">
		<div class="modal-dialog">
			<p hidden="hidden">
				<input type="text" id="editcategory_id">
			</p>
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title"></h4>
				</div>
				<div class="modal-body">
					<form id="editrewardCategory_form">
						<div class="row">
							<div class="col-md-12">
								<div class="form-group col-md-3">
									<label for="addRewardname"
										class="control-label icglabel_namecolon"></label> <input
										type="text" class="form-control" id="editRewardname"
										placeholder="Reward Name" name="editRewardname" tabindex="1"
										maxlength="45"
										onKeyPress="if(this.value.length==45) return false;"><span
										class="error-block icglabel_rewardCategoryName_err"
										id="editrewardCategoryName_err"></span>
								</div>
								<div class="form-group col-md-3">
									<label for="addRewardIcon"
										class="control-label icglabel_iconcolon"></label>
									<div class="input-group">
										<input id="editfile_attachment" name="pdf"
											accept=".png,.jpeg,.jpg" type="file"><span
											class="error-block icglabel_rewardCategoryIcon_err"
											id="editrewardCategoryIcon_err"></span>
									</div>
								</div>
								<div class=" form-group col-md-2">
									<button class="save form-group btn-addCat icglable_addcategory"
										value="Edit Category" id="editrewardCategorysubmit"
										name="editrewardCategorysubmit" tabindex="7"
										style="display: none;"></button>
								</div>
							</div>
						</div>
						<div class="row" style="padding: 15px;">
							<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
							<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
								<div class="form-group">
									<button type="button" class="confirm icglabel_update"
										data-dismiss="modal" id="updateCategoryDetails"></button>
								</div>
							</div>
							<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
								<div class="form-group">
									<button type="button" class="modal-Cancel icglabel_cancel"
										data-dismiss="modal" id="cancelCategoryDetails"></button>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="editCategoryInDetails" tabindex='-1'
		role="dialog">
		<div class="modal-dialog">
			<p hidden="hidden">
				<input type="text" id="editcategoryIn_id">
			</p>
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title"></h4>
				</div>
				<div class="modal-body">
					<form id="editrewardCategoryIn_form">
						<div class="row">
							<div class="col-md-12">
								<div class="form-group col-md-3">
									<label for="addRewardname"
										class="control-label icglabel_namecolon"></label> <input
										type="text" class="form-control" id="editRewardInname"
										placeholder="Reward Name" name="editRewardInname" tabindex="1"
										maxlength="45"
										onKeyPress="if(this.value.length==45) return false;"><span
										class="error-block icglabel_rewardCategoryInName_err"
										id="editrewardCategoryInName_err"></span>
								</div>
								<div class="form-group col-md-3">
									<label for="addRewardIcon"
										class="control-label icglabel_iconcolon"></label>
									<div class="input-group">
										<input id="editfileIn_attachment" name="pdf"
											accept=".png,.jpeg,.jpg" type="file"><span
											class="error-block icglabel_rewardCategoryInIcon_err"
											id="editrewardCategoryInIcon_err"></span>
									</div>
								</div>
								<div class=" form-group col-md-2">
									<button class="save form-group btn-addCat icglable_addreward"
										value="Edit Reward" id="editrewardCategoryInsubmit"
										name="editrewardCategoryInsubmit" tabindex="7"
										style="display: none;"></button>
								</div>
							</div>
						</div>
						<div class="row" style="padding: 15px;">
							<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2"></div>
							<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
								<div class="form-group">
									<button type="button"
										class="confirm pull-right icglabel_update"
										data-dismiss="modal" id="updateCategoryInDetails"></button>
								</div>
							</div>
							<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
								<div class="form-group">
									<button type="button"
										class="modal-Cancel pull-left icglabel_cancel"
										data-dismiss="modal" id="cancelCategoryInDetails"></button>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	<!-- Modal for editable Reward list -->
</div>