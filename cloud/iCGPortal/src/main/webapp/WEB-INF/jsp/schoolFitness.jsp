<%@include file="includes/taglib.jsp"%>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script src="resources/js/lib/moment.js"></script>
<script src="resources/js/date-formatter.js"></script>
<script src="resources/js/fitness.js"></script>
<script src="resources/js/lib/underscore-min.js"></script>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_fitness"></h1>
			<ol class="breadcrumb-new" id="bread">
				<li><a href="schooldashboard?token=${sessionID}"
					id="school_dashboard"><span class="icglabel_dashboard"></span></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li><a href="schoolFitness?token=${sessionID}"
					id="school_fitness"><span class="icglabel_fitness"></span></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
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
						<li><a class="tabs active icglabel_physicalfitnessindex"
							id="fitnessIndex" href="#"></a></li>
						<li><a class="tabs icglabel_steps" id="steps" href="#"></a></li>
						<li><a class="tabs icglabel_calories" id="calories" href="#"></a></li>
					</ul>
				</div>
			</div>
		</section>
		<!-- body  content -->
		<section class="content">
			<section id="school-pfi-data" class="font-reg-roboto">
				<section class="content-header-parent">
					<section class="content-header-parent sub_title dates-section">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12"
								style="padding-left: 21px;">
								<div class="bread_heading_parent">
									<div class="col-md-7 navigation pull-left">
										<span class="previousDates content-flaot"><img
											src="resources/images/grey_drop_down.png"
											class="left-navigate img-responsive pull-left fc-button fc-prev-button hand" /></span><span
											id="pfiDateRange" class="pull-left selectedDateRange"></span><span
											class="nextDates content-flaot"><img
											src="resources/images/grey_drop_down.png"
											class="right-navigate img-responsive pull-left fc-button hand" /></span>
									</div>
									<div id="pfiDuration" class="col-md-5 duration">
										<span class="pull-right"><span
											style="padding-right: 35px;"><a id="pfiRewardweek"
												class="weekDate fc-button selected-date  hand icglabel_week"></a></span><span
											class="pull-right"><a id="pfiRewardmonth"
												class="monthDate fc-button fc-next-button hand icglabel_month"></a></span></span>
									</div>
								</div>
							</div>
						</div>
					</section>
				</section>
				<section id="fitness-filter" class="user_account_mangmet font-reg-roboto">
					<div class="row">
						<div class="col-lg-9 col-md-6 col-sm-12 col-xs-12">
							<p>
								<strong><span class="font-reg-roboto txt-36" id="pfi-avg"></span></strong>&nbsp;
								<span
									class="txt-26 icglabel_avg"></span>
							</p>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-12 col-xs-12 grade-dropdown school-fitnessGrade">
							<select class="grade select-grade" style="    width: 131px;" id="findFitnessDataGrades" tabindex="3">
							</select>
						</div>
						<div class="col-lg-1 col-md-2 col-sm-12 col-xs-12">
							<span><button type="button"
									class="save form-group btn-find fitness-find icglabel_find" value="Find"
									id="findFitnessData" tabindex="14"></button> </span>
						</div>
					</div>
				</section>
				<br />
				<section class="user_account_mangmet">
					<div class="table-responsive">
						<table class="table table-stripped">
							<thead>
								<tr>
									<th class="icglabel_ranking"></th>
									<th class="icglabel_class"></th>
									<th class="icglabel_pfiavg"></th>
									<th class="icglabel_beststudent"></th>
									<th class="icglabel_beststudentpfi"></th>
								</tr>
							</thead>
							<tbody id="pfi-data">
								<tr>
									<td colspan="5"><strong class="icglabel_nodata"></strong></td>
								</tr>
							</tbody>
						</table>
					</div>
				</section>
			</section>
			<section id="school-steps-data" class="font-reg-roboto" style="display: none">
				<section class="content-header-parent">
					<section class="content-header-parent sub_title dates-section">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12"
								style="padding-left: 21px;">
								<div class="bread_heading_parent">
									<div class="col-md-7 navigation">
										<span class="previousDates"><img
											src="resources/images/grey_drop_down.png"
											class="left-navigate img-responsive pull-left fc-button fc-prev-button hand" /></span><span
											id="stepsDateRange" class="pull-left selectedDateRange"></span><span
											class="nextDates"><img
											src="resources/images/grey_drop_down.png"
											class="right-navigate img-responsive pull-left fc-button hand" /></span>
									</div>
									<div id="stepsDuration" class="col-md-5 duration">
										<span class="pull-right"><span
											style="padding-right: 35px;"><a id="stepsRewardweek"
												class="weekDate fc-button selected-date  hand icglabel_week"></a></span><span
											class="pull-right"><a id="stepsRewardmonth"
												class="monthDate fc-button fc-next-button hand icglabel_month"></a></span></span>
									</div>
								</div>
							</div>
						</div>
					</section>
				</section>
				<section class="user_account_mangmet font-reg-roboto">
					<div class="row">
						<div class="col-lg-9 col-md-6 col-sm-12 col-xs-12">
							<p>
								<strong><span class="font-reg-roboto txt-36" id="steps-avg"></span></strong>&nbsp;
								<span
									class="txt-26 icglabel_avg"></span>
							</p>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-12 col-xs-12 grade-dropdown school-fitnessGrade">
							<select class="grade select-grade" style="width: 131px;" id="findStepsDataGrades" tabindex="3">
							</select>
						</div>
						<div class="col-lg-1 col-md-2 col-sm-12 col-xs-12"> <span><button type="button"
									class="save form-group btn-find fitness-find icglabel_find" value="Find"
									id="findStepsData" tabindex="14"></button> </span>
						</div>
					</div>
				</section>
				<br />
				<section class="user_account_mangmet">
					<div class="table-responsive">
						<table class="table table-stripped">
							<thead>
								<tr>
									<th class="icglabel_ranking"></th>
									<th class="icglabel_class"></th>
									<th class="icglabel_stepsavg"></th>
									<th class="icglabel_beststudent"></th>
									<th class="icglabel_beststudentsteps"></th>
								</tr>
							</thead>
							<tbody id="steps-data">
								<tr>
									<td colspan="5"><strong class="icglabel_nodata"></strong></td>
								</tr>
							</tbody>
						</table>
					</div>
				</section>
			</section>
			<section id="school-calories-data" class="font-reg-roboto" style="display: none">
				<section class="content-header-parent">
					<section class="content-header-parent sub_title dates-section">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12"
								style="padding-left: 21px;">
								<div class="bread_heading_parent">
									<div class="col-md-7 navigation">
										<span class="previousDates"><img
											src="resources/images/grey_drop_down.png"
											class="left-navigate img-responsive pull-left fc-button fc-prev-button hand" /></span><span
											id="calDateRange" class="pull-left selectedDateRange"></span><span
											class="nextDates"><img
											src="resources/images/grey_drop_down.png"
											class="right-navigate img-responsive pull-left fc-button hand" /></span>
									</div>
									<div id="calDuration" class="col-md-5 duration">
										<span class="pull-right"><span
											style="padding-right: 35px;"><a id="calRewardweek"
												class="weekDate fc-button selected-date  hand icglabel_week"></a></span><span
											class="pull-right"><a id="calRewardmonth"
												class="monthDate fc-button fc-next-button hand icglabel_month"></a></span></span>
									</div>
								</div>
							</div>
						</div>
					</section>
				</section>
				<section class="user_account_mangmet font-reg-roboto">
					<div class="row">
						<div class="col-lg-9 col-md-6 col-sm-12 col-xs-12">
							<p>
								<strong><span class="font-reg-roboto txt-36" id="calories-avg"></span></strong>&nbsp;
								<span
									class="txt-26 icglabel_avg"></span>
							</p>
						</div>
						<div class="col-lg-2 col-md-3 col-sm-12 col-xs-12 grade-dropdown school-fitnessGrade">
							<select class="grade select-grade" style="width: 131px;"id="findCaloriesDataGrades" tabindex="3">
							</select>
						</div>
						<div class="col-lg-1 col-md-2 col-sm-12 col-xs-12"> <span><button type="button"
									class="save form-group btn-find fitness-find icglabel_find" value="Find"
									id="findCaloriesData" tabindex="14"></button> </span>
						</div>
					</div>
				</section>
				<br />
				<section class="user_account_mangmet">
					<div class="table-responsive">
						<table class="table table-stripped">
							<thead>
								<tr>
									<th class="icglabel_ranking"></th>
									<th class="icglabel_class"></th>
									<th class="icglabel_caloriesavg"></th>
									<th class="icglabel_beststudent"></th>
									<th class="icglabel_beststudentcalories"></th>
								</tr>
							</thead>
							<tbody id="calories-data">
								<tr>
									<td colspan="5"><strong class="icglabel_nodata"></strong></td>
								</tr>
							</tbody>
						</table>
					</div>
				</section>
			</section>
		</section>
	</div>
</div>