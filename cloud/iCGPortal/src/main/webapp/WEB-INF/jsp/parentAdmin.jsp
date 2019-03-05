<style>
* {
	text-rendering: optimizeLegibility;
	font-size: 100%;
}

svg g g {
	font-size: 0px;
}

svg g {
	font-size: 0px;
}

#activity-legend {
	background: url(resources/images/activity-legend.png) no-repeat center
		center;
	height: 42px;
}

#calorie-heart {
	background: url(resources/images/Heart_rate_icon_102x102.png) no-repeat
		center center;
}
</style>
<%@include file="includes/taglib.jsp"%>
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_dashboard"></h1>
		</section>
		<section>
			<div class="navbar navbar-default minlen-li">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle"
						data-target="#kidsnavbar" data-toggle="collapse">
						<span class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
				</div>
				<div class="navbar-collapse collapse" id="kidsnavbar">
					<ul class="nav navbar-nav" id="student">
						<c:if test="${!empty teacherStudentsList}">
							<c:forEach items="${teacherStudentsList}" var="students">
								<li><a class="tabs hand student_details"
									href="#${students.studentId}"
									data-student="${students.studentId}"
									data-studentDeviceUUID="${students.deviceUuid}"
									data-studentDeviceId="${students.deviceId}"
									data-nickName="${students.nickName}"><c:out
											value="${students.nickName}" /></a></li>
							</c:forEach>
						</c:if>
					</ul>
				</div>
			</div>
		</section>
		<!-- body  content -->
		<section class="content">
			<p class="bread_heading_parent">
				<span class="header-bread sub_title icglabel_notifications"></span><span
					class="pull-right"><span
					class="header-light-text icglabel_today"> </span> <span
					class="header-light-text parentDashboardCurrentDate today">
				</span></span>
			</p>
			<section class="">
				<div class="row">
					<div class="col">
						<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol notification-dashboard">
								<img src="resources/images/ParentDashboard/school_in_out.png"
									class="img-responsive pull-left" />
								<p class="notification-header icglabel_schooltime"></p>
								<p class="notification-text">
									<span id="enter_time"></span><br /> <span id="exit_time"></span>
								</p>
							</div>
						</div>
						<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol notification-dashboard">
								<img src="resources/images/ParentDashboard/geofencing.png"
									class="img-responsive pull-left" />
								<p class="notification-header icglabel_geofencinginout"></p>
								<p class="notification-text">
									<span id="geo_entertime"> </span><br /> <span id="geo_exittime"></span>
								</p>
							</div>
						</div>
						<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol notification-dashboard">
								<img src="resources/images/ParentDashboard/Alerts.png"
									class="img-responsive pull-left" />
								<p class="notification-header icglabel_alerts"></p>
								<p class="notification-text">
									<span id="alert_data"></span>
								</p>
							</div>
						</div>
					</div>
					<div class="col">
						<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol notification-dashboard">
								<img src="resources/images/ParentDashboard/reminders.png"
									class="img-responsive pull-left" />
								<p class="notification-header icglabel_reminder"></p>
								<p class="notification-text notification-ellipse"
									id="reminder_data"></p>
								<p>
									<a class="notification-link icglabel_viewmore"
										href="reminderslist?token=${sessionID}"></a>
								</p>
							</div>
						</div>
						<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol notification-dashboard">
								<img src="resources/images/ParentDashboard/Rewards.png"
									class="img-responsive pull-left" />
								<p class="notification-header icglabel_reward"></p>
								<p class="notification-text notification-ellipse"
									id="rewards_data"></p>
								<p>
									<a class="notification-link icglabel_viewmore"
										href="rewardStatistics?token=${sessionID}"></a>
								</p>
							</div>
						</div>
						<div class="col-lg-4 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol notification-dashboard">
								<img src="resources/images/ParentDashboard/announcements.png"
									class="img-responsive pull-left" />
								<p class="notification-header icglabel_announcement"></p>
								<p class="notification-text notification-ellipse"
									id="announcement_data"></p>
								<p>
									<a class="notification-link icglabel_viewmore"
										href="schoolAnnouncements?token=${sessionID}"></a>
								</p>
							</div>
						</div>
					</div>
				</div>
			</section>
			<section class="content-header-parent">
				<div class="row">
					<div class="col-md-12">
						<p class="bread_heading_parent">
							<span class="header-bread sub_title icglabel_health"></span><span
								class="pull-right"><span
								class="header-light-text icglabel_averageason"> </span> <span
								class="header-light-text parentDashboardPreviousDate yesterday">
							</span></span>
						</p>
					</div>
				</div>
			</section>
			<section class="">
				<div class="row">
					<div class="col-lg-12">
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class="section-layout health-dashboard">
								<div class="row">
									<div
										class="col-md-12 col-lg-12 col-xs-12 col-sm-12 health-heading">
										<span
											class="pull-left third_subtitle icglabel_physicalfitnessindex"></span>
										<span class="pull-right"> <img
											src="resources/images/ParentDashboard/Health_PFI.png" />
										</span>
									</div>
									<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
										<div class="chart tab-pane" id="donutchart_pfi"
											style="position: relative; height: 255px;"></div>
										<div class=" col-md-12 col-lg-12 col-xs-12 col-sm-12">
											<div class="text-center font-reg-roboto txt-18"
												style="color: #39B54A;display:none" id="fitness_info">
												<div class="legend-pfi legend-pfi-poor"></div>
												<div class="pfi icglabel_poor"></div>
												<div class="legend-pfi legend-pfi-avg"></div>
												<div class="pfi icglabel_average"></div>
												<div class="legend-pfi legend-pfi-good"></div>
												<div class="pfi icglabel_good"></div>
												<div class="legend-pfi legend-pfi-excellent"></div>
												<div class="pfi icglabel_excellent"></div>
											</div>
											<div class="text-center font-reg-roboto txt-36"
												style="color: #39B54A;display:none" id="fitness_infoNA">
												
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class="section-layout health-dashboard">
								<div class="row">
									<div
										class="col-md-12 col-lg-12 col-xs-12 col-sm-12 health-heading">
										<span class="pull-left third_subtitle icglabel_steps"></span>
										<span class="pull-right"><img
											src="resources/images/ParentDashboard/Health_steps.png" /></span>
									</div>
									<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
										<div class="chart tab-pane" id="donutchart_steps"
											style="position: relative; height: 240px;"></div>
										<div class=" col-md-12 col-lg-12 col-xs-12 col-sm-12">
											<p class="text-center font-reg-roboto txt-36"
												style="color: #FFAA00" id="steps_data"></p>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class="section-layout health-dashboard">
								<div class="row">
									<div
										class="col-md-12 col-lg-12 col-xs-12 col-sm-12 health-heading">
										<span class="pull-left third_subtitle icglabel_activity"></span>
										<span class="pull-right"><img
											src="resources/images/ParentDashboard/Health_activity.png" /></span>
									</div>
									<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
										<div class="chart" id="activity_chart" style="height: 288px;">
											<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12"
												style="padding: 20px 0px 0px 10px;">
												<div class="col-md-4 col-lg-4 col-sm-4 col-xs-4 acivity-div">
													<img
														src="resources/images/ParentDashboard/Activity_steps.png"
														alt="walking" class="img-responsive middle" />
													<p class="text-center font-reg-roboto"
														style="margin-top: 65px;">
														<span id="walking_time" style="word-break: break-word;"></span>
													</p>
												</div>
												<div class="col-md-4 col-lg-4 col-sm-4 col-xs-4 acivity-div">
													<img
														src="resources/images/ParentDashboard/Activity_running.png"
														alt="walking" class="img-responsive middle" />
													<p class="text-center font-reg-roboto"
														style="margin-top: 65px;">
														<span id="running_time" style="word-break: break-word;"></span>
													</p>
												</div>
												<div class="col-md-4 col-lg-4 col-sm-4 col-xs-4 acivity-div">
													<img
														src="resources/images/ParentDashboard/Activity_cycling.png"
														alt="walking" class="img-responsive middle" />
													<p class="text-center font-reg-roboto"
														style="margin-top: 65px;">
														<span id="cycling_time" style="word-break: break-word;"></span>
													</p>
												</div>
											</div>
										</div>
										<div class=" col-md-12 col-lg-12 col-xs-12 col-sm-12">
											<p class="text-center font-reg-roboto txt-36"
												style="color: #FFAA00" id="activity_data"></p>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-12">
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class="section-layout health-dashboard">
								<div class="row">
									<div
										class="col-md-12 col-lg-12 col-xs-12 col-sm-12 health-heading">
										<span class="pull-left third_subtitle icglabel_heartrate"></span>
										<span class="pull-right"><img
											src="resources/images/ParentDashboard/Health_heart_rate.png" /></span>
									</div>
									<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
										<div id="calorie-heart" class="tab-pane"
											style="position: relative; height: 240px;"></div>
										<div class=" col-md-12 col-lg-12 col-xs-12 col-sm-12">
											<p class="text-center font-reg-roboto txt-36"
												style="color: red" id="heartrate_data"></p>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class="section-layout health-dashboard">
								<div class="row">
									<div
										class="col-md-12 col-lg-12 col-xs-12 col-sm-12 health-heading">
										<span class="pull-left third_subtitle icglabel_caloriesburnt"></span>
										<span class="pull-right"><img
											src="resources/images/ParentDashboard/Health_calories.png" /></span>
									</div>
									<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
										<div class="chart tab-pane " id="donutchart_calories"
											style="position: relative; height: 240px;"></div>
										<div class=" col-md-12 col-lg-12 col-xs-12 col-sm-12">
											<p class="text-center font-reg-roboto txt-36"
												style="color: #F15A24" id="calories_data"></p>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class="section-layout health-dashboard sleep-dashboard">
								<div class="row">
									<div
										class="col-md-12 col-lg-12 col-xs-12 col-sm-12 health-heading">
										<span class="pull-left third_subtitle icglabel_sleep"></span>
										<span class="pull-right"><img
											src="resources/images/ParentDashboard/Health_sleep.png" /></span>
									</div>
									<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
										<div class="chart tab-pane" id="donutchart_sleep"
											style="position: relative; height: 229px;"></div>
										<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
											<div class="text-center" style="color: #1B1464;"
												id="sleep_data">
												<div class="col-md-4 col-lg-4 col-xs-6 col-sm-4">
													<span
														class="text-left font-norm-roboto txt-14 icglabel_bedtime"></span><br />
													<span class="text-center font-reg-roboto" id="bed_time"></span>
												</div>
												<div class="col-md-4 col-lg-4 col-xs-6 col-sm-4">
													<span
														class="text-center font-norm-roboto txt-14 icglabel_awaketime"></span><br />
													<span class="font-reg-roboto" id="awake_time"></span>
												</div>
												<div class="col-md-4 col-lg-4 col-xs-6 col-sm-4">
													<span
														class="text-right font-norm-roboto txt-14 icglabel_sleepquality"></span><br />
													<span class="font-reg-roboto" id="sleep_quality"></span>
												</div>
											</div>
										</div>
										<div class="centervalue">
											<p class="sleeptext"></p>
											<p class="sleeptime"></p>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</section>
			<section class="content-header-parent">
				<div class="row">
					<div class="col-md-12">
						<p class="bread_heading_parent">
							<span
								class="header-bread third_subtitle icglabel_healthpsychology">
							</span> <span class="pull-right txt-18"><span
								class="header-light-text icglabel_averageason"> </span> <span
								class="header-light-text parentDashboardPreviousDate yesterday">
							</span></span>
						</p>
					</div>
				</div>
			</section>
			<section class="">
				<div class="row">
					<div class="col-lg-12">
						<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class="section-layout physcology-dashboard ">
								<div class="row">
									<div
										class="col-md-12 col-lg-12 col-xs-12 col-sm-12 health-heading">
										<span class="pull-left third_subtitle icglabel_emotion"></span>
										<span class="pull-right"><img
											src="resources/images/ParentDashboard/Healthpsychology_emotion.png" /></span>
									</div>
									<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
										<div class="chart tab-pane physcology-height"
											id='columnchart_cl'>
											<div class="table-responsive">
												<table class="table borderless">
													<tbody id="emotionList">
														<tr>
															<th class="icglabel_morning"></th>
															<td><div class="middle">
																	<span>07-08</span>
																	<div class="img-health" id="emotion-8">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>08-09</span>
																	<div class="img-health" id="emotion-9">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>09-10</span>
																	<div class="img-health" id="emotion-10">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>10-11</span>
																	<div class="img-health" id="emotion-11">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>11-12</span>
																	<div class="img-health" id="emotion-12">N/A</div>
																</div></td>
														</tr>
														<tr>
															<th class="icglabel_afternoon"></th>
															<td><div class="middle">
																	<span>12-13</span>
																	<div class="img-health" id="emotion-13">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>13-14</span>
																	<div class="img-health" id="emotion-14">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>14-15</span>
																	<div class="img-health" id="emotion-15">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>15-16</span>
																	<div class="img-health" id="emotion-16">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>16-17</span>
																	<div class="img-health" id="emotion-17">N/A</div>
																</div></td>
														</tr>
														<tr>
															<th class="icglabel_evening"></th>
															<td><div class="middle">
																	<span>17-18</span>
																	<div class="img-health" id="emotion-18">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>18-19</span>
																	<div class="img-health" id="emotion-19">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>19-20</span>
																	<div class="img-health" id="emotion-20">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>20-21</span>
																	<div class="img-health" id="emotion-21">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>21-22</span>
																	<div class="img-health" id="emotion-22">N/A</div>
																</div></td>
														</tr>
														<tr>
															<th colspan="6">
																<div style="float: left;">
																	<span style="float: left;"><img
																		src="resources/images/ParentDashboard/Emotion_happy.png"
																		alt="emotion" class="img-responsive"
																		style="margin-right: 10px;"></span>
																	<p class="font-light-roboto phys-icons icglabel_happy"></p>
																</div>
																<div style="float: left;">
																	<span style="float: left;"><img
																		src="resources/images/ParentDashboard/Emotion_sad.png"
																		alt="emotion" class="img-responsive"
																		style="margin-right: 10px;"></span>
																	<p class="font-light-roboto phys-icons icglabel_sad"></p>
																</div>
																<div style="float: left;">
																	<span style="float: left;"><img
																		src="resources/images/ParentDashboard/Emotion_angry.png"
																		alt="emotion" class="img-responsive"
																		style="margin-right: 10px;"></span>
																	<p class="font-light-roboto phys-icons icglabel_angry"></p>
																</div>
																<div style="float: left;">
																	<span style="float: left;"><img
																		src="resources/images/ParentDashboard/Fear.png"
																		alt="emotion" class="img-responsive"
																		style="margin-right: 10px;"></span>
																	<p class="font-light-roboto phys-icons icglabel_fear"></p>
																</div>
															</th>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 safety-padding">
							<div class="section-layout physcology-dashboard ">
								<div class="row">
									<div
										class="col-md-12 col-lg-12 col-xs-12 col-sm-12  health-heading">
										<span class="pull-left third_subtitle icglabel_stress"></span>
										<span class="pull-right"><img
											src="resources/images/ParentDashboard/Healthpsychology_stress.png" /></span>
									</div>
									<div class="col-md-12 col-lg-12 col-xs-12 col-sm-12">
										<div class="chart tab-pane physcology-height"
											id="gaugechart_stress" style="text-align: -webkit-center;">
											<div class="table-responsive">
												<table class="table borderless">
													<tbody id="emotionList">
														<tr>
															<th class="icglabel_morning"></th>
															<td><div class="middle">
																	<span>07-08</span>
																	<div class="img-health" id="stress-8">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>08-09</span>
																	<div class="img-health" id="stress-9">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>09-10</span>
																	<div class="img-health" id="stress-10">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>10-11</span>
																	<div class="img-health" id="stress-11">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>11-12</span>
																	<div class="img-health" id="stress-12">N/A</div>
																</div></td>
														</tr>
														<tr>
															<th class="icglabel_afternoon"></th>
															<td><div class="middle">
																	<span>12-13</span>
																	<div class="img-health" id="stress-13">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>13-14</span>
																	<div class="img-health" id="stress-14">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>14-15</span>
																	<div class="img-health" id="stress-15">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>15-16</span>
																	<div class="img-health" id="stress-16">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>16-17</span>
																	<div class="img-health" id="stress-17">N/A</div>
																</div></td>
														</tr>
														<tr>
															<th class="icglabel_evening"></th>
															<td><div class="middle">
																	<span>17-18</span>
																	<div class="img-health" id="stress-18">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>18-19</span>
																	<div class="img-health" id="stress-19">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>19-20</span>
																	<div class="img-health" id="stress-20">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>20-21</span>
																	<div class="img-health" id="stress-21">N/A</div>
																</div></td>
															<td><div class="middle">
																	<span>21-22</span>
																	<div class="img-health" id="stress-22">N/A</div>
																</div></td>
														</tr>
														<tr>
															<th colspan="6">
																<div style="float: left;">
																	<span style="float: left;"><img
																		src="resources/images/ParentDashboard/Stress_normal.png"
																		alt="emotion" class="img-responsive"
																		style="margin-right: 10px;"></span>
																	<p
																		class="font-light-roboto phys-icons icglabel_normalstress"></p>
																</div>
																<div style="float: left;">
																	<span style="float: left;"><img
																		src="resources/images/ParentDashboard/Stress_understress.png"
																		alt="emotion" class="img-responsive"
																		style="margin-right: 10px;"></span>
																	<p
																		class="font-light-roboto phys-icons icglabel_understress"></p>
																</div>
															</th>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</section>
		</section>
	</div>
</div>
<!-- page script -->
<script type="text/javascript"
	src="https://www.gstatic.com/charts/loader.js"></script>
<script src="resources/js/lib/moment.js"></script>
<script src="resources/js/date-formatter.js"></script>
<script src="resources/js/json-sort.js"></script>
<script src="resources/js/parentAdmin.js"></script>