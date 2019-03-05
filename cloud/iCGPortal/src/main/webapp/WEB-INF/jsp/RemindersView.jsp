<%@include file="includes/taglib.jsp"%>
<%@ taglib prefix="fm" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="com.liteon.icgwearable.hibernate.entity.Accounts"%>
<link rel="stylesheet" href="resources/css/lib/fullcalendar.css">
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglabel_reminders"></h1>
			<ol class="breadcrumb-new">
				<li><a href="parentdashboard?token=${sessionID}" class="icglabel_dashboard"></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li><a href="#" class="kidName" id="sname"></a><i
					class="fa fa-chevron-right" aria-hidden="true"></i></li>
				<li class="active"><a href="#" class="icglabel_reminders"></a></li>
			</ol>
		</section>
		<section class="datefilter">
			<div class="row txt-18 font-norm-roboto">
				<div class="col-md-3 col-sm-4 col-xs-6 todays-dateSchool"
					id="calSection">
					<form>
						<div
							class='input-group date form-group has-feedback has-feedback-left'
							id='currentDateParent' data-date-format="YYYY/MM/DD">
							<input type='text' class="form-control-user dateInput"
								tabindex="1" id="datepicker" name="datepicker" /><img
								src="resources/images/calender.png"
								class="form-control-feedback calender-img" alt="" /> <span
								class="input-group-addon">
								<button type="button"
									class="save calenderGo reminder-data icglabel_gobutton" value="Go"
									id="findbasedOnDateParent" tabindex="1"></button>
							</span>
						</div>
					</form>
				</div>
			</div>
		</section>
		<!-- body  content -->
		<section class="content">
			<section class="user_account_mangmet">
				<h4 class="sub_title icglabel_reminderoftheday"></h4>
				<hr />
				<div id="reminders"></div>
			</section>
		</section>
	</div>
</div>
<!-- 	body content ends here -->
<script src="resources/js/date-formatter.js"></script>
<script src="resources/js/RemindersView.js"></script>