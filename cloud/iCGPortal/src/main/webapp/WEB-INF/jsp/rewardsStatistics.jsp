<%@include file="includes/taglib.jsp"%>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="resources/css/lib/fullcalendar.css">
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_rewards"></h1>
			<ol class="breadcrumb-new" id="school-bread">
				<li><a href="parentdashboard?token=${sessionID}"
					class="icglabel_dashboard"></a><i class="fa fa-chevron-right"
					aria-hidden="true"></i></li>
				<li><a href="#" class="kidName" id="sname"></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li class="active"><a href="#" class="icglabel_rewards"></a></li>
			</ol>
		</section>
		<!-- body  content -->
		<section class="content">
			<section class="user_account_mangmet total-rewad">
				<div class="row">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
						<div class="col-lg-10 col-md-10 col-sm-12 col-xs-12">
							<img src="resources/images/ParentDashboard/Rewards.png"
								alt="total reward" class="img-responsive pull-left" /> <span
								class="header-bread font-light-roboto txt-24 icglabel_totalcollectedreward"
								style="position: relative; top: 15px;"> </span>
						</div>
						<div class="col-lg-2 col-md-2 col-sm-12 col-xs-12">
							<span
								class="parentRewardCount font-light-roboto txt-48"
								id="totalRewardCount"></span>
						</div>
					</div>
				</div>

			</section>
			<section class="">
				<div class="row rewards-scroll">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12"
						id="parentRewardList"></div>
				</div>
			</section>
			<section class="content-header-parent">
				<div class="row txt-18 font-norm-roboto">
					<div class="col-lg-3 col-md-5 col-sm-8 col-xs-10">
						<div
							class='input-group date form-group has-feedback has-feedback-left'
							data-date-format="YYYY-MM-DD">
							<input type='text' name="parentRewardCurrentDate"
								id='parentRewardCurrentDate' class="form-control-user dateInput"
								tabindex="1" readonly /><img
								src="resources/images/calender.png"
								class="form-control-feedback calender-img" alt="" /> <span
								class="input-group-btn">
								<button type="button" class="save calenderGo icglabel_gobutton"
									value="Go" id="findbasedOnDateforParentReward" tabindex="2"></button>
							</span>
						</div>
					</div>
				</div>
			</section>

			<section class="search-header">
				<div class="row">
					<div class="col-md-12">
						<p class="health-heading sub_title icglabel_rewardoftheday"></p>

					</div>
					<hr class="col-md-12 hr-divider" />
					<!-- <div class="col-md-12">
						<p class="notification-header" id="parentRewardCategory">Sports reward</p>
					</div> -->
					<div class="col-md-11 col-lg-11 col-sm-11 col-xs-11">
						<div class="table-responsive">
							<table class="table table-bordered">
								<thead>
									<tr>
										<th class="icglabel_srno"></th>
										<th class="icglabel_rewardcategory"></th>
										<th class="icglabel_rewards"></th>
										<th class="icglabel_totalreward"></th>
										<th class="icglabel_teacher"></th>
										<th class="icglabel_time"></th>
									</tr>
								</thead>
								<tbody id="parentRewardOftheDayList">
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</section>
		</section>
	</div>
</div>
<!-- 	body content ends here -->
<!-- page script -->
<script src="resources/js/json-sort.js"></script>
<script src="resources/js/date-formatter.js"></script>
<script src="resources/js/rewardsStatistics.js"></script>