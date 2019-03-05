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
			<h1 class="icglable_servicefunctionstatus"></h1>
		</section>

		<!-- body  content -->
		<section class="content">

			<section class="">
				<div class="row">
					<div class="col-lg-12">
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol service-section">
							<div>
								<img src="resources/images/Open_stack.png"
									class="img-responsive pull-left" />
							</div>
							<div class="service-text">
								<p class="notification-header third_subtitle icglable_openstack"></p>
								
								<p>
									<a class="notification-link icglabel_viewmore"
										href="http://172.31.157.5/horizon/auth/login/" target="_new"></a>
								</p>
								</div>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol service-section">
							<div>
								<img src="resources/images/HDFS.png"
									class="img-responsive pull-left" />
									</div>
									<div class="service-text">
								<p class="notification-header third_subtitle icglable_hdfs"></p>
								
								<p>
									<a class="notification-link icglabel_viewmore"
										href="#"></a>
								</p>
								</div>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol service-section">
							<div>
								<img src="resources/images/Cassandra.png"
									class="img-responsive pull-left" />
									</div>
									<div class="service-text">
								<p class="notification-header third_subtitle icglable_cassandra"></p>
								
								<p>
									<a class="notification-link icglabel_viewmore"
										href="http://172.31.157.64/" target="_new"></a>
								</p>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-12">
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol service-section">
							<div>
								<img src="resources/images/SQL.png"
									class="img-responsive pull-left" />
									</div>
									<div class="service-text">
								<p class="notification-header third_subtitle icglable_mysql"></p>
								
								<p>
									<a class="notification-link icglabel_viewmore"
										href="http://172.31.157.58/phpmyadmin/" target="_new"></a>
								</p>
								</div>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol service-section">
							<div>
								<img src="resources/images/Map_reduce.png"
									class="img-responsive pull-left" />
									</div>
									<div class="service-text">
								<p class="notification-header third_subtitle icglable_mapreduce"></p>
								
								<p>
									<a class="notification-link icglabel_viewmore"
										href="http://172.31.157.69/" target="_new"></a>
								</p>
								</div>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol service-section">
							<div>
								<img src="resources/images/Flume.png"
									class="img-responsive pull-left" />
									</div>
									<div class="service-text">
								<p class="notification-header third_subtitle icglable_flume"></p>
								
								<p>
									<a class="notification-link icglabel_viewmore"
										href="http://172.31.157.55/" target="_new"></a>
								</p>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-12">
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol service-section">
							<div>
								<img src="resources/images/gangalia.png"
									class="img-responsive pull-left" />
									</div>
									<div class="service-text">
								<p class="notification-header third_subtitle icglable_ganglia"></p>
								
								<p>
									<a class="notification-link icglabel_viewmore"
										href="http://172.31.157.68/ganglia/" target="_new"></a>
								</p>
								</div>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 safety-padding">
							<div class=" safetyCol service-section">
							<div>
								<img src="resources/images/Nagios.png"
									class="img-responsive pull-left" />
									</div>
									<div class="service-text">
								<p class="notification-header third_subtitle icglable_nagios"></p>
								
								<p>
									<a class="notification-link icglabel_viewmore"
										href="https://www.nagios.com/" target="_new"></a>
								</p>
								</div>
							</div>
						</div>
					</div>
				</div>
			</section>
		</section>
	</div>
</div>
<script src="resources/js/systemAdminDashboard.js"></script>