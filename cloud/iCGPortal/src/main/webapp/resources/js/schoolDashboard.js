$(document)
.ready(
		function() {
			$('.dashboard').removeClass("treeview").addClass("active");
			$('.dashboard').removeClass("font-medium-roboto").addClass("font-bold-roboto");
			$("#sdashboardIcon")
			.attr("src",
			"resources/images/SchoolAdmin_sideBarIcons/White/dashboard.png");
			$("#school-bread").css({
				"display" : "none"
			});
			var token_id = $("#token_id").val();
			var daySelected = "";
			var gradeSelected = "";
			var d = new Date();
			var month = d.getMonth() + 1;
			var day = d.getDate();
			var output = d.getFullYear() + '-'
			+ (month < 10 ? '0' : '') + month + '-'
			+ (day < 10 ? '0' : '') + day;
			$(".dateInput").val(output);
			init();
			$('div#sosMap').dialog({
				autoOpen : false
			});
			function init() {
				makeGetAjaxRequest("web/getGradeClass/" + token_id,
						false, onGradeSuccess, onFailure);
				return false;
			}
			/* InOUt Datepicker */
			var date_input = $('input[name="schoolCurrentDate"]');
			var container = $('.bootstrap-iso form').length > 0 ? $(
			'.bootstrap-iso form').parent() : "body";
			var options = {
					container : container,
			};

			date_input.datepicker(options);
			$("#schoolCurrentDate").datepicker({
				maxDate : 0,
				format : 'yyyy-mm-dd',
				todayHighlight : true,
				todayBtn : true,
				autoclose : true,
				pickerPosition : "bottom-left",
				orientation : "bottom-right"
			}).on(
					'show',
					function(e) {
						if ($(".datepicker").hasClass(
						"datepicker-orient-bottom")) {
							$(".datepicker").removeClass(
							'datepicker-orient-bottom')
							.addClass("datepicker-orient-top");
						}
						$(".datepicker").addClass("school-datepicker");
					});
			/* End of InOUt Datepicker */
			/* SOS Datepicker */
			var date_input = $('input[name="schoolsosCurrentDate"]');
			var container = $('.bootstrap-iso form').length > 0 ? $(
			'.bootstrap-iso form').parent() : "body";
			var options = {
					container : container,
			};

			date_input.datepicker(options);
			$("#schoolsosCurrentDate").datepicker({
				maxDate : 0,
				format : 'yyyy-mm-dd',
				todayHighlight : true,
				todayBtn : true,
				autoclose : true,
				pickerPosition : "bottom-left",
				orientation : "bottom-right"
			}).on(
					'show',
					function(e) {
						if ($(".datepicker").hasClass(
						"datepicker-orient-bottom")) {
							$(".datepicker").removeClass(
							'datepicker-orient-bottom')
							.addClass("datepicker-orient-top");
						}
						$(".datepicker").addClass("sos-datepicker");
					});
			/* End of SOS Datepicker */
			/* Geo Datepicker */
			var date_input = $('input[name="schoolgeoCurrentDate"]');
			var container = $('.bootstrap-iso form').length > 0 ? $(
			'.bootstrap-iso form').parent() : "body";
			var options = {
					container : container,
			};

			date_input.datepicker(options);
			$("#schoolgeoCurrentDate").datepicker({
				maxDate : 0,
				format : 'yyyy-mm-dd',
				todayHighlight : true,
				todayBtn : true,
				autoclose : true,
				pickerPosition : "bottom-left",
				orientation : "bottom-right"
			}).on(
					'show',
					function(e) {
						if ($(".datepicker").hasClass(
						"datepicker-orient-bottom")) {
							$(".datepicker").removeClass(
							'datepicker-orient-bottom')
							.addClass("datepicker-orient-top");
						}
						$(".datepicker").addClass("geo-datepicker");
					});
			/* End of GEo Datepicker */
			function loadSchoolInOutData() {
				daySelected = $("#schoolCurrentDate").val();
				gradeSelected = $("#schoolDashboardInOutgrade").val();

				if (gradeSelected == 0) {
					makeGetAjaxRequest("web/SchoolInOut/" + token_id
							+ "/" + daySelected + "/" + gradeSelected,
							false, onSuccessLoadSchoolData, onFailure);
				} else {
					makeGetAjaxRequest("web/SchoolInOut/" + token_id
							+ "/" + daySelected + "/" + gradeSelected,
							false, onGoSuccessLoadGradeData, onFailure);
				}
			}
			$("#findbasedOnDateforInOut").click(function() {
				showSpinner();
				loadSchoolInOutData();
				return false;
			});
			$("#findbasedOnDateforSos").click(function() {
				showSpinner();
				initSos();
				return false;
			});
			$("#findbasedOnDateforGeo").click(function() {
				showSpinner();
				initGeo();
				return false;
			});

			function onGradeSuccess(data) {
				aGrades = new Array();
				aClass = new Array();
				$('#schoolDashboardInOutgrade').html('');
				$('#schoolDashboardInOutgrade')
				.append(
				'<option class="icglabel_allgrade" value="0">All Grade</option>');
				if (!isDataNull(data.Return.Results)) {
					var grades = data.Return.Results.Grades;
					var len = grades.length;
					if (len > 0) {
						for (var count = 0; count < len; count++) {
							var cGrade = grades[count].grade;
							var cClass = grades[count].class;

							if (aGrades.indexOf(cGrade) < 0) {
								aGrades.push(cGrade);
								$('#schoolDashboardInOutgrade').append(
										$('<option>', {
											value : cGrade,
											text : cGrade
										}));
							}
							if (typeof aClass[cGrade] === 'undefined') {
								aClass[cGrade] = new Array();
							}
							aClass[cGrade].push(cClass);

						}
					}
					loadSchoolInOutData();
				}
			}
			function loadstatistics(data) {
				if (!isDataNull(data)) {
					$("#schoolInoutTotal").text(
							(data.total_count) ? data.total_count
									: 'n/a');
					$("#schoolInTotal")
					.text(
							(data.school_in_count) ? data.school_in_count
									: 'n/a');
					$("#schoolOutTotal")
					.text(
							(data.school_out_count) ? data.school_out_count
									: 'n/a');
					$("#schoolAbsentTotal").text(
							(data.absent_count) ? data.absent_count
									: 'n/a');
					$("#schoolAbnormalTotal").text(
							(data.abnormal_count) ? data.abnormal_count
									: 'n/a');
				}
			}
			function onFailure(data) {
				debugLogs(data);
			}
			function onSuccessLoadSchoolData(data) {
				$("#schoolSection").css({
					"display" : "block"
				});
				$("#student-info").css({
					"display" : "block"
				});
				$("#student-filter").css({
					"display" : "none"
				});
				loadstatistics(data.Return.Results.school_statistics);
				$("tbody#school_absent tr").remove();
				/* loading absent data */
				var absent_students = "", student_name = "", student_class = "", student_abnormalTime = "", student_abnormalReson = "";
				if (!isDataNull(data)
						&& !isDataNull(data.Return.Results.absent_students)) {
					for (i in data.Return.Results.absent_students) {
						if (!isDataNull(data.Return.Results.absent_students[i].student_name)) {
							student_name = data.Return.Results.absent_students[i].student_name;
						}
						if (!isDataNull(data.Return.Results.absent_students[i].student_class)) {
							student_class = data.Return.Results.absent_students[i].student_class;
						}

						absent_students = '<tr><td>' + student_name
						+ '</td><td>' + student_class
						+ '</td></tr>';

						$('#school_absent').append(absent_students);

						absent_students = "", student_name = "",
						student_class = "";
					}
				} else {
					var nodataText = getValueByLanguageKey('icglabel_nodata');
					absent_students = '<tr><td colspan="2" class="box-body text-center"><b>' + nodataText + '</b></td></tr>';
					$('#school_absent').append(absent_students);
					absent_students = "";
				}
				/* end of loading absent data */
				$("tbody#school_abnormals tr").remove();
				/* loading abnormal data */
				var abnormal_students = "", abnormal_student_name = "", abnormal_student_class = "";
				if (!isDataNull(data)
						&& !isDataNull(data.Return.Results.abnormal_students)) {
					var abnormal = data.Return.Results.abnormal_students;
					for (i in abnormal) {
						if (!isDataNull(abnormal[i].student_name)) {
							student_name = abnormal[i].student_name;
						}
						if (!isDataNull(abnormal[i].student_class)) {
							student_class = abnormal[i].student_class;
						}
						if (!isDataNull(abnormal[i].student_abnormalTime)) {
							student_abnormalTime = abnormal[i].student_abnormalTime;
						}
						if (!isDataNull(abnormal[i].student_abnormalReason)) {
							student_abnormalReson = abnormal[i].student_abnormalReason;
						}
						abnormal_students = '<tr><td>' + student_name
						+ '</td><td>' + student_class
						+ '</td><td>' + student_abnormalReson
						+ '</td><td>' + student_abnormalTime
						+ '</td></tr>';
						$('#school_abnormals')
						.append(abnormal_students);
						abnormal_students = "", student_name = "",
						student_class = "";
					}
				} else {
					var nodataText = getValueByLanguageKey('icglabel_nodata');
					abnormal_students = '<tr><td colspan="4" class="box-body text-center"><b>' + nodataText + '</b></td></tr></tbody>';
					$('#school_abnormals').append(abnormal_students);
					abnormal_students = "";
				}
				/* end of loading abnormal data */
			}
			function onGoSuccessLoadGradeData(data) {
				$("#schoolSection").css({
					"display" : "block"
				});
				$("#student-info").css({
					"display" : "none"
				});
				$("#student-filter").css({
					"display" : "block"
				});
				loadstatistics(data.Return.Results.grade_statistics);
				$("section#class_list div").remove();
				if (!isDataNull(data) && !isDataNull(data.Return)
						&& !isDataNull(data.Return.Results)
						&& !isDataNull(data.Return.Results.classes)) {
					for (i in data.Return.Results.classes) {
						var class_students = "", student_class = "", class_total_count = "n/a", class_school_out_count = "n/a", class_school_in_count = "n/a", absent_students_count = "n/a", abnormal_students_count = "n/a";
						var current = data.Return.Results.classes[i];
						if (!isDataNull(current.student_class)) {
							student_class = current.student_class;
						}
						if (!isDataNull(current.class_statistics.total_count)) {
							class_total_count = current.class_statistics.total_count;
						}
						if (!isDataNull(current.class_statistics.school_out_count)) {
							class_school_out_count = current.class_statistics.school_out_count;
						}
						if (!isDataNull(current.class_statistics.school_in_count)) {
							class_school_in_count = current.class_statistics.school_in_count;
						}
						if (!isDataNull(current.class_statistics.absent_count)) {
							absent_students_count = current.class_statistics.absent_count;
						}
						if (!isDataNull(current.class_statistics.abnormal_count)) {
							abnormal_students_count = current.class_statistics.abnormal_count;
						}
						if(i%2==0){
							class_students ='<div class="row font-norm-roboto" id="rowList_'
								+ i
								+ '"></div>';
							$("#class_list").append(class_students);
						}
						class_students = '<div class="col-md-6 col-remove-padding-left">'
							+ '<div class="col-md-12">'
							+ '<p class="bread_heading_parent sub-title content-header-parent">Class <span>'
							+ student_class
							+ '</span></p>'
							+ '</div>'
							+ '<div class="col-md-4 col-remove-padding-left">'
							+ '<div class="col-md-12 gradeClass font-reg-roboto">'
							+ '<p class="txt-20"><span id="classTotalCount_'
							+ i
							+ '">'
							+ class_total_count
							+ '</span></p><p class="icglable_allstudent">All Student</p>'
							+ '</div>'
							+ '</div>'
							+ '<div class="col-md-4 col-remove-padding-left">'
							+ '<div class="col-md-12 gradeClass font-reg-roboto">'
							+ '<p class="txt-20"><span id="classSchoolInCount_'
							+ i
							+ '">'
							+ class_school_in_count
							+ '</span></p><p class="icglable_instudent">In Student</p>'
							+ '</div>'
							+ '</div>'
							+ '<div class="col-md-4 col-remove-padding-left">'
							+ '<div class="col-md-12 gradeClass font-reg-roboto">'
							+ '<p class="txt-20"><span id="classSchoolOutCount_'
							+ i
							+ '">'
							+ class_school_out_count
							+ '</span></p><p class="icglable_outstudent">Out Student</p>'
							+ '</div>'
							+ '</div>'
							+ '<div class="col-md-12 col-remove-padding-left">'
							+ '<div class="col-md-12 gradeInfo ">'
							+ '<div class="row">'
							+ '<div class="col-md-6 font-medium-roboto txt-16" id="absent_'
							+ i
							+ '">'
							+ '<p class="sectionHeading icglabel_absentstudents">'
							+ 'Absent Student : <span id="absentNo"><span id="classAbsentCount_'
							+ i
							+ '">'
							+ absent_students_count
							+ '</span></span>'
							+ '</p>'
							+ '<hr/>'
							+ '</div>'
							+ '<div class="col-md-6 font-medium-roboto txt-16" id="abnormal_'
							+ i
							+ '">'
							+ '<p class="sectionHeading icglabel_abnormalstudents">'
							+ 'Abnormal Student : <span id="abnormalNo"><span id="classAbnormalCount_'
							+ i
							+ '">'
							+ abnormal_students_count
							+ '</span></span>'
							+ '</p>'
							+ '<hr/>'
							+ '</div>'
							+ '</div>'
							+ '</div>'
							+ '</div>' + '</div>';
						if(i%2==0){
							$("#rowList_"+i).append(class_students);
						}else{
							var j = i-1;
							$("#rowList_"+j).append(class_students);
						}

						class_students = "", student_class = "",
						class_total_count = "";
						class_school_out_count = "",
						class_school_in_count = "",
						absent_students_count = "",
						class_absent_student_name = "";
						for (j in data.Return.Results.classes[i].absent_students) {
							var class_absent_student_name = "", class_absent_students_list = "";
							if (!isDataNull(data.Return.Results.classes[i].absent_students[j])) {
								class_absent_student_name = data.Return.Results.classes[i].absent_students[j].student_name;
							}
							class_absent_students_list = '<div class="row"><div class="col-md-12 font-reg-roboto txt-16"><p>'
								+ class_absent_student_name
								+ '</p><hr class="sectionHr"></div></div>';
							$("div#absent_" + i).append(
									class_absent_students_list);
							class_absent_students_list = "",
							abnormal_students_count = "";
						}
						for (k in data.Return.Results.classes[i].abnormal_students) {
							var class_abnormal_student_name = "", class_abnormal_students_list = "";
							if (!isDataNull(data.Return.Results.classes[i].abnormal_students[k])) {
								class_abnormal_student_name = data.Return.Results.classes[i].abnormal_students[k].student_name;
							}
							class_abnormal_students_list = '<div class="row"><div class="col-md-12 font-reg-roboto txt-16"><p>'
								+ class_abnormal_student_name
								+ '</p><hr class="sectionHr"></div></div>';
							$("div#abnormal_" + i).append(
									class_abnormal_students_list);
							class_abnormal_student_name = "",
							class_abnormal_students_list = "";
						}
					}
				} else {
					console.log("Empty, nothing to display?");
				}
			}
			$("#inOutTab").click(
					function() {
						if ($("#calSection").hasClass(
						"todays-datealertGeo")) {
							$("#calSection").removeClass(
							"todays-datealertGeo").addClass(
							"todays-dateSchool");
						}
						$(this).parent().parent().find('.active')
						.removeClass("active");
						$(this).addClass("active");
						$("#school-bread").css({
							"display" : "none"
						});
						$("#schooldashboard-Inout-filter").css({
							"display" : "block"
						});
						$("#schoolDashboardInOutgrade").css({
							"display" : "block"
						});
						$("#schoolSection").css({
							"display" : "block"
						});

						$("#student-info").css({
							"display" : "block"
						});
						$("#student-filter").css({
							"display" : "none"
						});

						$("#school-in-out").css({
							"display" : "block"
						});
						$("#sosAlert").css({
							"display" : "none"
						});
						$("#geofencing").css({
							"display" : "none"
						});
						init();
					});
			$("#alertTab").click(
					function() {
						if ($("#calSection").hasClass(
						"todays-dateSchool")) {
							$("#calSection").removeClass(
							"todays-dateSchool").addClass(
							"todays-datealertGeo");
						}
						$(this).parent().parent().find('.active')
						.removeClass("active");
						$(this).addClass("active");
						$("#school-bread").css({
							"display" : "block"
						});
						var text = $(this).context.innerHTML;
						if ($("ol#school-bread")
								.has('#school_geobread')) {
							$("ol#school-bread .tabs").parent()
							.remove();
							$("ol#school-bread .active").parent()
							.remove();
							$("ol#school-bread").append(
									'<li><a class="tabs" href="#" id="school_sosbread">'
									+ text + '</a></li>');
						}
						if ($("ol#school-bread")
								.has('#school_sosbread')) {
						} else {
							$("ol#school-bread").append(
									'<li><a class="tabs" href="#" id="school_sosbread">'
									+ text + '</a></li>');
						}

						$("#school-in-out").css({
							"display" : "none"
						});
						$("#sosAlert").css({
							"display" : "block"
						});
						$("#geofencing").css({
							"display" : "none"
						});
						initSos();
					});
			function initSos() {
				daySelected = $("#schoolsosCurrentDate").val();
				makeGetAjaxRequest("web/SOSAlerts/" + token_id + "/"
						+ daySelected, false, onSuccessLoadSosAlert,
						onFailure);
			}
			function onSuccessLoadSosAlert(data) {
				if (!isDataNull(data)
						&& !isDataNull(data.Return)
						&& !isDataNull(data.Return.Results)
						&& !isDataNull(data.Return.Results.school_statistics)) {
					loadSosStatstics(data.Return.Results.school_statistics);
					if (data.Return.Results.sos_alerts.length > 0) {
						loadSosAlerts(data.Return.Results.sos_alerts);
					} else {
						$("div.sosAlertLists div").remove();
						var nodataText = getValueByLanguageKey('icglabel_nodata');
						var sosnodata = '<div class="row">'
							+ '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">'
							+ '<section class="sos-nodata geo-nodata text-center"><strong>' + nodataText + '</strong></section>'
							+ '</div>' + '</div>';
						$("div.sosAlertLists").append(sosnodata);
					}
				}else{
					hideSpinnerNow();
				}
			}
			function loadSosAlerts(data) {
				var arr = [ 2 ];
				var latval = 0.0, longval = 0.0;
				$("div.sosAlertLists div").remove();
				obj = jsonSort(data, "sos_datetime", DATATYPE.DATE,
						SORTORDER.DESC, null);
				for (var i = 0; i < data.length; i++) {
					var sos_parent_name = "", sos_staff_name = "", sos_student_name = "", sos_student_class = "", sos_admin_name = "", sos_gps_location_data = "", sos_abnormal = "", sos_start_time = "", sos_wearable_name = "", sos_duration = "", sos_device_event_id = "";
					var parentName = "", adminName = "", staffName = "", wearableName = "";

					if (!isDataNull(data[i].admin_name)) {
						sos_admin_name = data[i].admin_name;
						adminName = '<span class="wearableIconGreen icglabel_a">A</span> <span title="'
							+ sos_admin_name
							+ '">'
							+ sos_admin_name + '</span>';
					} else {
						adminName = '<span class="wearableIconGrey icglabel_a">A</span> <span></span>';
					}
					if (!isDataNull(data[i].gps_location_data)) {
						sos_gps_location_data = data[i].gps_location_data;
						arr[0] = sos_gps_location_data.split(',');
						latval = arr[0][0];
						longval = arr[0][1];
					}
					if (!isDataNull(data[i].parent_name)) {
						sos_parent_name = data[i].parent_name;
						parentName = '<span class="wearableIconGreen icglabel_p">P</span> <span title="'
							+ sos_parent_name
							+ '">'
							+ sos_parent_name + '</span>';
					} else {
						parentName = '<span class="wearableIconGrey icglabel_p">P</span> <span></span>';
					}
					if (!isDataNull(data[i].abnormal)) {
						sos_abnormal = data[i].abnormal;
					}
					if (!isDataNull(data[i].sos_start_time)) {
						sos_start_time = data[i].sos_start_time;
					}
					if (!isDataNull(data[i].staff_name)) {
						sos_staff_name = data[i].staff_name;
						staffName = '<span class="wearableIconGreen icglabel_s">S</span> <span title="'
							+ sos_staff_name
							+ '">'
							+ sos_staff_name + '</span>';
					} else {
						staffName = '<span class="wearableIconGrey icglabel_s">S</span> <span></span>';
					}
					if (!isDataNull(data[i].student_class)) {
						sos_student_class = data[i].student_class;
					}
					if (!isDataNull(data[i].student_name)) {
						sos_student_name = data[i].student_name;
					}
					if (!isDataNull(data[i].wearable_name)) {
						sos_wearable_name = data[i].wearable_name;
						wearableName = '<span class="wearableIconGreen icglabel_w">W</span> <span title="'
							+ sos_wearable_name
							+ '">'
							+ sos_wearable_name + '</span>';
					} else {
						wearableName = '<span class="wearableIconGrey icglabel_w">W</span> <span></span>';
					}
					if (!isDataNull(data[i].sos_duration)) {
						sos_duration = data[i].sos_duration;
						if (sos_duration == 0) {
							sos_duration = '<1';
						}else{
							sos_duration +=" Minutes";
						}

					}
					if (!isDataNull(data[i].device_event_id)) {
						sos_device_event_id = data[i].device_event_id;
					}
					sosAlerts_list = '<div class="col-md-6 right2padding">'
						+ '<div class="sos" id="sos_'
						+ i
						+ '">'
						+ '<div class="row">'
						+ '<div class=" col-lg-8 col-md-12 col-sm-12 col-xs-12">'
						+ '<div class="row">'
						+ '<div class="col-md-6 col-xs-6 col-sm-6" style="border-right: 1px solid black;">'
						+ '<div class="row">'
						+ '<div class="col-md-12">'
						+ '<p class="icglabel_namecolon">Name:<span>'
						+ sos_student_name
						+ '</span></p>'
						+ '</div>'
						+ '<div class="col-md-12">'
						+ '<p class="icglable_classcolon">	Class:<span>'
						+ sos_student_class
						+ '</span></p>'
						+ '</div>'
						+ '<div class="col-md-12">'
						+ '<p class="icglable_startscolon">Starts:<span>'
						+ sos_start_time
						+ '</span></p>'
						+ '</div>'
						+ '<div class="col-md-12">'
						+ '<p class="icglable_durationcolon">Duration:<span>'
						+ sos_duration
						+ '</span></p>'
						+ '</div>'
						+ '</div>'
						+ '</div>'
						+ '<div class="col-md-6 col-xs-6 col-sm-6">'
						+ '<div class="row">'
						+ '<div class="col-md-12">'
						+ '<p class="wearableIcon text-elipse" id="wearableIcon_w'
						+ i
						+ '">'
						+ '</p>'
						+ '</div>'
						+ '<div class="col-md-12">'
						+ '<p class="parentIcon text-elipse" id="wearableIcon_p'
						+ i
						+ '">'
						+ '</p>'
						+ '</div>'
						+ '<div class="col-md-12">'
						+ '<p class="staffIcon text-elipse" id="wearableIcon_s'
						+ i
						+ '">'
						+ '</p>'
						+ '</div>'
						+ '<div class="col-md-12">'
						+ '<p class="adminIcon text-elipse" id="wearableIcon_a'
						+ i
						+ '">'
						+ '</p>'
						+ '</div>'
						+ '</div>'
						+ '</div>'
						+ '</div>'
						+ '</div>'
						+ '<div class="col-lg-4 col-md-12 col-sm-12 col-xs-12">'
						+ '<div class="row">'
						+ '<div class="col-md-12">'
						+ '<button type="button" latitude='
						+ latval
						+ ' longitude='
						+ longval
						+ ' class="btn btn-lg mapButton icglable_sosmap" '
						+ 'id="map_'
						+ i
						+ '" value="SOS Map" '
						+ '>SOS Map</button>'
						+ '</div>'
						+ '<div class="col-md-12">'
						+ '<button type="button"  deviceid='
						+ sos_device_event_id
						+ ' class="btn btn-lg alertButton icglable_closealert"'
						+ 'id="closeAlert_'
						+ i
						+ '" value="Close Alert" >Close Alert</button>'
						+ '</div>'
						+ '</div>'
						+ '</div>'
						+ '</div>'
						+ '</div>' + '</div>';
					$("div.sosAlertLists").append(sosAlerts_list);
					if (sos_abnormal.toUpperCase() == "YES") {
						$('#sos_' + i).css({
							"border-left-color" : "red"
						});
					} else {
						$('#sos_' + i).css({
							"border-left-color" : "green"
						});
					}
					if (isDataNull(longval) && isDataNull(latval)) {
						$('#map_' + i).attr('disabled', true);
					}
					if (!isDataNull(sos_admin_name)) {
						$('#closeAlert_' + i).attr('disabled', true);
					}
					$("p#wearableIcon_w" + i).append(wearableName);
					$("p#wearableIcon_p" + i).append(parentName);
					$("p#wearableIcon_s" + i).append(staffName);
					$("p#wearableIcon_a" + i).append(adminName);
					sos_parent_name = "", sos_staff_name = "",
					sos_student_name = "",
					sos_student_class = "",
					sos_admin_name = "",
					sos_gps_location_data = "",
					sos_start_time = "",
					sos_wearable_name = "", sos_duration = "",
					sos_device_event_id = "";
					parentName = "", adminName = "", staffName = "",
					wearableName = "";
				}
				$(".icglable_closealert").click(
						function() {
							currentdeviceid = Number($(this).attr(
							'deviceid'));
							currentid = $(this).attr('id');
							closeObject = $(this).parent().parent()
							.parent().parent().find(
							'.adminIcon');
							makePostAjaxRequest("mobile/CloseSOSAlert/"
									+ token_id + "/" + currentdeviceid,
									true, onSuccessLoadCloseAlert,
									onFailure);
						});
				function onSuccessLoadCloseAlert(data) {
					if (!isDataNull(data)
							&& !isDataNull(data.Return)
							&& !isDataNull(data.Return.ResponseSummary)
							&& !isDataNull(data.Return.ResponseSummary.StatusMessage)) {
						initSos();
					}
				}
				$(".mapButton").click(function() {
					currentLat = 0.0, currentLong = 0.0;
					currentLat = Number($(this).attr('latitude'));
					currentLong = Number($(this).attr('longitude'));
					$('body').css('overflow', 'hidden');
					$('div#sosMap').dialog('open');
				});

				$("div#sosMap").dialog(
						{
							resizable : false,
							autofocus : true,
							height : "auto",
							width : 700,
							draggable : false,
							modal : true,
							closeOnEscape : true,
							open : function(event, ui) {
								$(".ui-dialog-titlebar-close",
										ui.dialog | ui).show();
								initMap();
							}
						});
				function initMap() {
					var taiwan = {
							lat : currentLat,
							lng : currentLong
					};
					var map = new google.maps.Map(document
							.getElementById('mapView'), {
						zoom : 18,
						center : taiwan
					});
					var contentString = '<div id="content">'
						+ '<p>Last found location of the student <student_name></p>'
						+ '</div>';
					var infowindow = new google.maps.InfoWindow({
						content : contentString
					});
					var marker = new google.maps.Marker({
						position : taiwan,
						map : map,
						title : 'Taipei Japanese School'
					});
					marker.addListener('click', function() {
						infowindow.open(map, marker);
					});
					infowindow.open(map, marker);
				}
				$("#close_mapView").click(function() {
					$('body').css('overflow', 'scroll');
					$('#mapView').empty();
					$('div#sosMap').dialog('close');
				});
				$("#sosMap").keydown(function(e) {
					if (e.keyCode == 27) {
						$("#close_mapView").click();
					}
				});
			}
			function loadSosStatstics(data) {
				$('#sos_totalAlert_count').text(
						(data.total_count) ? data.total_count : 'n/a');
				$('#sos_onAlert_count').text(
						(data.open_count) ? data.open_count : 'n/a');
				$('#sos_closed_count')
				.text(
						(data.closed_count) ? data.closed_count
								: 'n/a');
			}
			$("#fencingTab").click(
					function() {
						if ($("#calSection").hasClass(
						"todays-dateSchool")) {
							$("#calSection").removeClass(
							"todays-dateSchool").addClass(
							"todays-datealertGeo");
						}
						$(this).parent().parent().find('.active')
						.removeClass("active");
						$(this).addClass("active");
						$("#school-bread").css({
							"display" : "block"
						});
						var text = $(this).context.innerHTML;
						if ($("ol#school-bread")
								.has('#school_sosbread')) {
							$("ol#school-bread .tabs").parent()
							.remove();
							$("ol#school-bread .active").parent()
							.remove();
							$("ol#school-bread").append(
									'<li><a class="tabs" id="school_geobread">'
									+ text + '</a></li>');
						}
						if ($("ol#school-bread")
								.has('#school_geobread')) {
						} else {
							$("ol#school-bread").append(
									'<li><a class="tabs" href="#" id="school_geobread">'
									+ text + '</a></li>');
						}

						$("#school-in-out").css({
							"display" : "none"
						});
						$("#sosAlert").css({
							"display" : "none"
						});
						$("#geofencing").css({
							"display" : "block"
						});
						initGeo();
					});
			function initGeo() {
				daySelected = $("#schoolgeoCurrentDate").val();
				var today = getToday();
				var cacheContent = true;
				if (daySelected == today)
					cacheContent = false;
				makeGetAjaxRequest("web/BeaconGeofenceEventsList/"+ token_id + "/" + daySelected, cacheContent,onSuccessLoadGeofencing, onFailure);
			}
			function onSuccessLoadGeofencing(geo) {
				var geo_zone_name = "", geo_total_entry = 0, geo_zone_view = "";
				$("section#school_geofencingList section").remove();
				$('#school_geofencingList').empty();
				if (!isDataNull(geo) && !isDataNull(geo.Return.Results.school_geofencing)) {
					data = geo.Return.Results.school_geofencing;
					for (i in data) {
						if (!isDataNull(data[i].zone_name)) {
							geo_zone_name = data[i].zone_name;
						}
						if (!isDataNull(data[i].building_name)) {
							geo_zone_name += ': '
								+ data[i].building_name;
						}
						if (!isDataNull(data[i].floor_number)) {
							geo_zone_name += ': ' + data[i].floor_number;
						}
						if (!isDataNull(data[i].students)) {
							geo_total_entry = data[i].students.length;
						}
						if (!isDataNull(data[i].zone_view)) {
							geo_zone_view = data[i].zone_view;
						}
						var geolist = '<section>'
							+ '<section class="content-header-box">'
							+ '<div class="row">'
							+ '<div class="col-md-12">'
							+ '<p class="bread_heading_parent font-reg-roboto txt-22">'
							+ geo_zone_name
							+ '</p>'
							+ '</div>'
							+ '<div class="col-md-12">'
							+ '<p class="bread_heading_parent font-reg-roboto txt-16 icglable_totalstudentsenter">'
							+ 'Total Students Enter: <span class="text-bold txt-20 font-bold-roboto">'
							+ geo_total_entry
							+ '</span>'
							+ '</p>'
							+ '</div>'
							+ '</div>'
							+ '</section>'
							+ '<section id="geo_architecturalMap_'
							+ i
							+ '" style="display:none">'
							+ '<section class="content-header-parent">'
							+ '<div class="row">'
							+ '<div class="col-md-12">'
							+ '<p class="col-md-12 bread_heading_parent sub-title icglable_mapview">Map View</p>'
							+ '</div>'
							+ '</div>'
							+ '</section>'
							+ '<section class="content-map-view">'
							+ '<div class="mapView">'
							+ '<img src="'
							+ geo_zone_view
							+ '" alt="architectural map" class="img-responsive" />'
							+ '</div>'
							+ '</section>'
							+ '</section>'
							+ '<section class="detail-section">'
							+ '<div class="row"><div class="col-md-12" id="geofencing_'
							+ i + '">' +
							'</div></div>' + '</section>' + '</section>';
						$("#school_geofencingList").append(geolist);
						var element = "geo_architecturalMap_" + i;
						isImageExists(geo_zone_view, element,
						"geofence");
						if (!isDataNull(data[i].students)) {
							var geo_student_name = "", geo_student_class = "", geo_duration = "", geo_in_time = "", geo_out_time = "";
							for (j in data[i].students) {
								if (!isDataNull(data[i].students[j].student_name)) {
									geo_student_name = data[i].students[j].student_name;
								}
								if (!isDataNull(data[i].students[j].student_class)) {
									geo_student_class = data[i].students[j].student_class;
								}
								if (!isDataNull(data[i].students[j].duration)) {
									geo_duration = data[i].students[j].duration;
								}
								if (!isDataNull(data[i].students[j].in_time)) {
									geo_in_time = data[i].students[j].in_time;
								}
								if (!isDataNull(data[i].students[j].out_time)) {
									geo_out_time = data[i].students[j].out_time;
									abnormal = false;
								} else {
									geo_out_time = "-:-";
									abnormal = true;
								}
								var schoolGeoList = '<div class="geo-student-list">'
									+ '<div class="detail-div" id="schoolGeo_'
									+ i
									+ ''
									+ j
									+ '">'
									+ '<div class="row font-reg-roboto txt-14">'
									+ '<div class="col-md-12">'
									+ '<div class="row">'
									+ '<div class="col-md-12">'
									+'<div class="geo_name">'
									+ '<span class="txt-16">'
									+ geo_student_name
									+ '</span></div>'
									+ '<div class="geo_info">'
									+ '<span class="red-text icglable_in">In : <span>'
									+ geo_in_time
									+ '</span></span>'
									+'</div>'
									+ '<div class="geo_info">'
									+ '<span>Out : <span>'
									+ geo_out_time
									+ '</span></span>'
									+'</div>'
									+ '</div>'
									+ '</div>'
									+ '<div class="row">'
									+ '<div class="col-md-12">'
									+'<div class="">'
									+ '<span>Class : <span class="icglable_classcolon">'
									+ geo_student_class
									+ '</span></span>'
									+ '</div>'
									+ '</div>'
									+ '</div>'
									+ '<div class="row">'
									+ '<div class="col-md-12">'
									+'<div class="">'
									+ '<span>Duration : <span class="icglable_durationcolon">'
									+ geo_duration
									+ '</span></span>'
									+ '</div>'
									+ '</div>'
									+ '</div>'
									+ '</div>'
									/*+ '<div class="col-md-6">'*/
									/*+ '<div class="col-md-6 col-remove-padd">'
									+ '<div class="col-md-12">'

									+ '</div>'
									+ '</div>'*/
									/*+ '<div class="col-md-6 col-remove-padd">'
									+ '<div class="col-md-12">'

									+ '</div>'
									+ '</div>'
									+ '</div>'*/
									+ '</div>'
									+ '</div>'
									+ '</div>';
								$("#geofencing_" + i).append(
										schoolGeoList);
								if (abnormal) {
									$('#schoolGeo_' + i + j).css({
										"border-left-color" : "red"
									});
								} else {
									$('#schoolGeo_' + i + j).css({
										"border-left-color" : "green"
									});
								}
								geo_student_name = "",
								geo_student_class = "",
								geo_duration = "",
								geo_in_time = "",
								geo_out_time = "";
							}
						}
						geo_zone_name = "", geo_total_entry = 0,
						geo_zone_view = "";
					}
				} else {
					$('#school_geofencingList').empty();
					var nodataText = getValueByLanguageKey('icglabel_nodata');
					schoolGeoList = '<div class="row">'
						+ '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">'
						+ '<section class="sos-nodata text-center geo-nodata icglabel_nodatafordate">' + nodataText + '</section>'
						+ '</div>' + '</div>';
					$('#school_geofencingList').append(schoolGeoList);
					schoolGeoList = "";
				}
			}

			$("#schoolDashboardInOutgrade").change(function() {
				showSpinner();
				loadSchoolInOutData();
				return false;
			});

		});
