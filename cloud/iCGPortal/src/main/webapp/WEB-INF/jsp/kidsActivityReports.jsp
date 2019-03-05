<%@include file="includes/taglib.jsp"%>
<link rel="stylesheet" href="resources/css/lib/fullcalendar.css">
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_kidsactivityreports"></h1>
			<ol class="breadcrumb-new">
				<li><a href="parentdashboard?token=${sessionID}"
					class="icglabel_dashboard"></a><i class="fa fa-chevron-right"
					aria-hidden="true"></i></li>
				<li><a href="#" class="kidName" id="acticitySName"></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li class="active icglabel_activities"></li>
			</ol>
		</section>
		<!-- body  content -->
		<section class="content">
			<section class="content-header-parent sub_title dates-section">
				<div class="row ">
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="bread_heading_parent">
							<div class="col-md-7 navigation">
								<span class="previousDates"><img
									src="resources/images/grey_drop_down.png"
									class="left-navigate img-responsive pull-left fc-button fc-prev-button hand" style="margin: 1px;" /></span><span
									class="pull-left" id="duration"></span><span class="nextDates"><img
									src="resources/images/grey_drop_down.png"
									class="right-navigate img-responsive pull-left fc-button hand" /></span>
							</div>
							<div class="col-md-5 duration">
								<span class="pull-right"><span
									style="padding-right: 35px;"><a id="activityweek"
										class="weekDate fc-button hand icglabel_week"></a></span><span
									class="pull-right"><a id="activitymonth"
										class="monthDate fc-button hand fc-next-button icglabel_month"></a></span></span>
							</div>
						</div>
					</div>
				</div>
			</section>
			<section class="">
				<div class="row">
					<div class="col-lg-12">
						<div class="col-lg-5 col-md-5 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol">
								<p
									class="notification-header sub_title icglabel_specialAnalysis"></p>
								<div class="row">
									<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
										<div class="analysis-results analysis-avg">
											<p
												class="small-header header-padding icglabel_specialAnalysisResults"></p>
											<hr class="result-hr-color" />
											<div class="row">
												<div
													class="col-lg-12 col-md-12 col-sm-12 col-xs-12 font-norm-roboto txt-14">
													<div
														class="col-lg-6 col-md-6 col-xs-12 col-sm-12 gap-btw-ele">
														<div class="col-lg-12 pfi-score notification-text">
															<span id="first_compare_score"></span> - <span
																class="pfiscore" id="firstscore"></span>
														</div>
													</div>
													<div
														class="col-lg-6 col-md-6 col-xs-12 col-sm-12 gap-btw-ele">
														<div class="col-lg-12 sleep-score notification-text">
															<span id="second_compare_score"></span> - <span
																class="sleepscore" id="secondcore"></span>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
										<div class="suggestion-results suggestion-box">
											<p class="small-header header-padding third_subtitle">
												<span class="icglabel_suggestions"></span><span
													class="icglabel_for"> </span><span id="activityKidName"></span>
											</p>
											<hr class="suggestion-hr-color" />
											<p class="notification-text suggestions"
												id="firstSuggestionMsg"></p>
											<p class="notification-text suggestions"
												id="secondSuggestionMsg"></p>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-7 col-md-7 col-sm-12 col-xs-12 safety-padding"
							style="padding-top: 0px;">
							<div class="analysis-results analysis-checkbox">
								<form class="analysis-middle">
									<div class="row">
										<div class="col-lg-12">
											<div class="col-lg-12">
												<label class="checkbox-inline checkbox-inline-padding">
													<input type="checkbox" value="pfi" name="compareChart"
													class="check"><span
													class="icglabel_physicalfitnessindex"></span>
												</label> <label class="checkbox-inline"> <input
													type="checkbox" value="steps" name="compareChart" disabled
													class="disabled"><span class="icglabel_steps"></span>
												</label> <label class="checkbox-inline"> <input
													type="checkbox" value="activity" name="compareChart"
													disabled class="disabled"><span
													class="icglabel_activity"></span>
												</label> <label class="checkbox-inline"> <input
													type="checkbox" value="calories" name="compareChart"
													disabled class="disabled"><span
													class="icglabel_caloriesburnt"></span>
												</label> <label class="checkbox-inline"> <input
													type="checkbox" value="sleep" name="compareChart"
													class="check"><span class="icglabel_sleep"></span>
												</label> <label class="checkbox-inline"> <input
													type="checkbox" value="heartRate" name="compareChart"
													disabled class="disabled"><span
													class="icglabel_heartrate"></span>
												</label>
											</div>
										</div>
									</div>
								</form>
							</div>
							<!-- 	</div> -->
							<div
								class="col-lg-12 col-md-12 col-xs-12 col-sm-12 safety-padding section-layout graph-height compare-graph">
								<div class="chart tab-pane" id="compare-chart"
									style="position: relative; height: 240px;"></div>
								<div id="compare-legend">
									<div class="first-compare-legend legend-gap"></div>
									<span class="legend-gap" style="width: 10%" id="fist_compare"></span>
									<div class="second-compare-legend legend-gap"></div>
									<span class="legend-gap" id="second_compare"></span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</section>
			<section>
				<div class="row">
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
						<section class="content-header-parent">
							<div class="row">
								<div class="col-md-12">
									<p class="bread_heading_parent">
										<span
											class="pull-left header-font sub_title icglabel_physicalfitnessindex"></span>
									</p>
								</div>
							</div>
						</section>
						<section class="">
							<div class="row">
								<div
									class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reports-padding-right">
									<div class="section-layout graph-height">
										<div class="chart tab-pane" id="pfi-chart"
											style="position: relative; height: 240px;"></div>
										<div id="pfi-legend"></div>
										<div class="avg-small-section">
											<div class="sm-section">
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="pfi_avg"></p>
													<p
														class="notification-text notification-text-margin icglabel_averagepfi"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="pfi_best"></p>
													<p
														class="notification-text notification-text-margin icglabel_bestpfi"></p>
												</div>
												<div class="small-staticts" style="margin-right: 0;">
													<p class="small-section-header small-section-header-margin"
														id="pfi_last"></p>
													<p class="notification-text notification-text-margin"
														id="pfiCompare"></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
						<section class="content-header-parent">
							<div class="row">
								<div class="col-md-12">
									<p class="bread_heading_parent">
										<span class="pull-left header-font sub_title icglabel_steps"></span>
									</p>
								</div>
							</div>
						</section>
						<section class="">
							<div class="row">
								<div
									class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reports-padding-left">
									<div class="section-layout graph-height">
										<div class="chart" id="steps-chart" style="height: 240px;"></div>
										<div id="steps-legend"></div>
										<div class="avg-small-section">
											<div class="sm-section">
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="steps_avg"></p>
													<p
														class="notification-text notification-text-margin icglabel_averagesteps"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="steps_best"></p>
													<p
														class="notification-text notification-text-margin icglabel_beststeps"></p>
												</div>
												<div class="small-staticts" style="margin-right: 0;">
													<p class="small-section-header small-section-header-margin"
														id="steps_last"></p>
													<p class="notification-text notification-text-margin"
														id="stepsCompare"></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
						<section class="content-header-parent">
							<div class="row">
								<div class="col-md-12">
									<p class="bread_heading_parent">
										<span
											class="pull-left header-font sub_title icglabel_activity"></span>
									</p>
								</div>
							</div>
						</section>
						<section class="">
							<div class="row">
								<div
									class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reports-padding-right">
									<div class=" section-layout graph-height">
										<div class="chart" id="activities-chart"
											style="height: 240px;"></div>
										<div id="activity-legend"></div>
										<div class="avg-small-section">
											<div class="sm-section">
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="activity_avg"></p>
													<p
														class="notification-text notification-text-margin icglabel_averageactivities"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="activity_best"></p>
													<p
														class="notification-text notification-text-margin icglabel_bestactivities"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="activity_last"></p>
													<p class="notification-text notification-text-margin"
														id="activityCompare"></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
						<section class="content-header-parent">
							<div class="row">
								<div class="col-md-12">
									<p class="bread_heading_parent">
										<span
											class="pull-left header-font sub_title icglabel_caloriesburnt"></span>
									</p>
								</div>
							</div>
						</section>
						<section class="">
							<div class="row">
								<div
									class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reports-padding-left">
									<div class="section-layout graph-height">
										<div class="chart" id="calories-burnt-chart"
											style="height: 240px;"></div>
										<div id="calories-burnt-legend"></div>
										<div class="avg-small-section">
											<div class="sm-section">
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="calories_avg"></p>
													<p
														class="notification-text notification-text-margin icglabel_averagecaloriesburnt"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="calories_best"></p>
													<p
														class="notification-text notification-text-margin icglabel_bestcaloriesburnt"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="calories_last">+</p>
													<p class="notification-text notification-text-margin"
														id="caloriesCompare"></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
						<section class="content-header-parent">
							<div class="row">
								<div class="col-md-12">
									<p class="bread_heading_parent">
										<span class="pull-left header-font sub_title icglabel_sleep"></span>
									</p>
								</div>
							</div>
						</section>
						<section class="">
							<div class="row">
								<div
									class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reports-padding-right">
									<div class="section-layout graph-height">
										<div class="chart" id="sleep-chart" style="height: 240px;"></div>
										<div id="sleep-legend"></div>
										<div class="avg-small-section">
											<div class="sm-section">
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="sleep_avg"></p>
													<p
														class="notification-text notification-text-margin icglabel_averagesleep"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="sleep_best"></p>
													<p
														class="notification-text notification-text-margin icglabel_bestsleep"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="sleep_last"></p>
													<p class="notification-text notification-text-margin"
														id="sleepCompare"></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
						<section class="content-header-parent">
							<div class="row">
								<div class="col-md-12">
									<p class="bread_heading_parent">
										<span
											class="pull-left header-font sub_title icglabel_heartrate"></span>
									</p>
								</div>
							</div>
						</section>
						<section class="">
							<div class="row">
								<div
									class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reports-padding-left">
									<div class="section-layout graph-height">
										<div class="chart" id="heart-rate-chart"
											style="height: 240px;"></div>
										<div id="heart-rate-legend"></div>
										<div class="avg-small-section">
											<div class="sm-section">
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="heart_avg"></p>
													<p
														class="notification-text notification-text-margin icglabel_averageheartrate"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="heart_best"></p>
													<p class="notification-text notification-text-margin"></p>
												</div>
												<div class="small-staticts">
													<p class="small-section-header small-section-header-margin"
														id="heart_last"></p>
													<p class="notification-text notification-text-margin"
														id="hrmCompare"></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
						<section class="content-header-parent">
							<div class="row">
								<div class="col-md-12">
									<p class="bread_heading_parent">
										<span class="pull-left header-font sub_title icglabel_emotion"></span>
									</p>
								</div>
							</div>
						</section>
						<section class="">
							<div class="row">
								<div
									class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reports-padding-right">
									<div class="section-layout graph-height">
										<div class="chart" id="emotion-chart" style="height: 240px;"></div>
										<div id="emotion-legend"></div>
										<div class="avg-small-section">
											<div class="sm-section">
												<div class="small-staticts emotions-statistics">
													<p class="small-section-header small-section-header-margin"
														id="happy_per"></p>
													<p
														class="notification-text notification-text-margin icglabel_happy"></p>
												</div>
												<div class="small-staticts emotions-statistics">
													<p class="small-section-header small-section-header-margin"
														id="sad_per"></p>
													<p
														class="notification-text notification-text-margin icglabel_sad"></p>
												</div>
												<div class="small-staticts emotions-statistics">
													<p class="small-section-header small-section-header-margin"
														id="angry_per"></p>
													<p
														class="notification-text notification-text-margin icglabel_angry"></p>
												</div>
												<div class="small-staticts emotions-statistics">
													<p class="small-section-header small-section-header-margin"
														id="fear_per"></p>
													<p
														class="notification-text notification-text-margin icglabel_fear"></p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
						<section class="content-header-parent">
							<div class="row">
								<div class="col-md-12">
									<p class="bread_heading_parent">
										<span class="pull-left header-font sub_title icglabel_stress"></span>
									</p>
								</div>
							</div>
						</section>
						<section class="">
							<div class="row">
								<div
									class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reports-padding-left">
									<div class="section-layout graph-height">
										<div class="chart" id="stress-chart" style="height: 240px;"></div>
										<div id="stress-legend"></div>
										<div class="avg-small-section">
											<div class="sm-section">
												<div class="small-staticts">
													<div class="pull-left" style="width: 70%">
														<p
															class="small-section-header small-section-header-margin icglabel_overall"></p>
														<p class="notification-text notification-text-margin"
															id="stressStatus"></p>
													</div>
													<div class="pull-right activity-img-box">
														<div class="img-middle" id="stressStatusImg"></div>
													</div>
												</div>
												<div class="small-staticts stress-statistics">
													<p class="small-section-header small-section-header-margin"
														id="stressfull"></p>
													<p class="notification-text notification-text-margin"
														id="stressedDates">
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</section>
					</div>
				</div>
			</section>
		</section>
	</div>
</div>
<!-- 	body content ends here -->
<script src="//www.gstatic.com/charts/loader.js"></script>
<script src="resources/js/lib/moment.js"></script>
<script src="resources/js/date-formatter.js"></script>
<script src="resources/js/json-sort.js"></script>
<script src="resources/js/kidsActivityReports.js"></script>
