$('.safety').removeClass("treeview").addClass("active");
$('.safety').removeClass("font-medium-roboto").addClass("font-bold-roboto");
$("#sSafetyIcon").attr("src","resources/images/sidemenu_icon/white/safety_w.png");

var st_name = sessionStorage.getItem("nick_name");
$(document).ready(function() {
	$("#sname").text(st_name);
	var date_input =$('input[name="safetyCurrentDate"]');
	var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
	var options={
			container: container,
	};
	date_input.datepicker(options);
	//$("#datepicker").attr('readonly', 'readonly');
	$('#safetyCurrentDate').datepicker({
		dateFormat : 'YYYY-MM-DD',
		todayHighlight	: true,
		maxDate 		: '+0d'
	}).on('show', function(e){     
		if($(".datepicker").hasClass("datepicker-orient-bottom")){
			$(".datepicker").removeClass('datepicker-orient-bottom').addClass("datepicker-orient-top");
		}
		$(".datepicker").addClass("safety-datepicker");
	}).datepicker('setDate', 'today');
	$(".safety-data").trigger('click');
});

//Date picker
$(".safety-data").click(function(){
	showSpinner();
	var inputDate = $('#safetyCurrentDate').val();
	var student_id = sessionStorage.getItem("student_id");
	var token_id = sessionStorage.getItem("token_id");
	var device_uuid = sessionStorage.getItem("device_uuid");
	
	makeGetAjaxRequest("web/KidsSafetyNotifications/"
			+ token_id + "/" + student_id + "/" + inputDate, false,
			onSafetySuccess, onSafetyFailure);
	return false;
})

/**
 * onSafetySuccess callback function
 * 
 * @param userData -
 *            JSON data response received for Safety HTTP request
 * @returns
 */
function onSafetySuccess(userData) {
	var comma = ',';
	/*
	 * Array variable to store time, location co-ordinates and other information
	 * to drop pins on the map
	 */
	var markers = [];

	if (!isDataNull(userData) && !isDataNull(userData.Return)
			&& !isDataNull(userData.Return.Results)) {

		var not_available = "n/a";

		var enterTime = not_available, displayEnterTime = not_available;
		var exitTime = not_available, displayExitTime = not_available;
		var schooltime_entryGps = not_available;
		var schooltime_exitGps = not_available;

		if (!isDataNull(userData.Return.Results.schooltime.entry)) {
			var len = userData.Return.Results.schooltime.entry.length;
			if (len > 0) {
				displayEnterTime = userData.Return.Results.schooltime.entry[len - 1].time;
				for (var count = 0; count < len; count++) {
					enterTime = userData.Return.Results.schooltime.entry[count].time;
					schooltime_entryGps = userData.Return.Results.schooltime.entry[count].gps;
					if (!isDataNull(schooltime_entryGps)) {
						var schooltimeEntry = schooltime_entryGps.split(comma);

						// push data to markers array
						var schoolEntry = [];
						schoolEntry.push('School Time Entry Location');
						schoolEntry.push(Number(schooltimeEntry[0]));
						schoolEntry.push(Number(schooltimeEntry[1]));
						schoolEntry.push('Entry Time : ' + enterTime);
						schoolEntry.push('school_inout');
						schoolEntry.push(enterTime);
						markers.push(schoolEntry);
					}
				}
			}
		}
		if (!isDataNull(userData.Return.Results.schooltime.exit)) {
			var len = userData.Return.Results.schooltime.exit.length;
			if (len > 0) {
				displayExitTime = userData.Return.Results.schooltime.exit[len - 1].time;
				for (var count = 0; count < len; count++) {
					exitTime = userData.Return.Results.schooltime.exit[count].time;
					schooltime_exitGps = userData.Return.Results.schooltime.exit[count].gps;
					if (!isDataNull(schooltime_exitGps)) {
						var schooltimeExit = schooltime_exitGps.split(comma);

						// push data to markers array
						var schoolExit = [];
						schoolExit.push('School Time Exit Location');
						schoolExit.push(Number(schooltimeExit[0]));
						schoolExit.push(Number(schooltimeExit[1]));
						schoolExit.push('Exit Time : ' + exitTime);
						schoolExit.push('school_inout');
						schoolExit.push(exitTime);
						markers.push(schoolExit);
					}
				}
			}
		}
		$(".enter_time").text(" - " + displayEnterTime);
		$(".exit_time").text(" - " + displayExitTime);

		var geofence_entertime = not_available, displayGeofenceEnterTime = not_available;
		var geofence_exittime = not_available, displayGeofenceExitTime = not_available;
		var geofence_entryGps = not_available;
		var geofence_exitGps = not_available;

		if (!isDataNull(userData.Return.Results.geofence.entry)) {
			var len = userData.Return.Results.geofence.entry.length;

			if (len > 0) {
				displayGeofenceEnterTime = userData.Return.Results.geofence.entry[len - 1].time;
				for (var count = 0; count < userData.Return.Results.geofence.entry.length; count++) {
					geofence_entertime = userData.Return.Results.geofence.entry[count].time;
					geofence_entryGps = userData.Return.Results.geofence.entry[count].gps;
					if (!isDataNull(geofence_entryGps)) {
						var geofenceEntryGps = geofence_entryGps.split(comma);

						// push data to markers array
						var geofenceEntry = [];
						geofenceEntry.push('Geofence Entry Location');
						geofenceEntry.push(Number(geofenceEntryGps[0]));
						geofenceEntry.push(Number(geofenceEntryGps[1]));
						geofenceEntry.push('Entry Time : ' + geofence_entertime);
						geofenceEntry.push('geofence_inout');
						geofenceEntry.push(geofence_entertime);
						markers.push(geofenceEntry);
					}
				}
			}
		}

		if (!isDataNull(userData.Return.Results.geofence.exit)) {
			var len = userData.Return.Results.geofence.exit.length;

			if (len > 0) {
				displayGeofenceExitTime = userData.Return.Results.geofence.exit[len - 1].time;
				for (var count = 0; count < len; count++) {
					geofence_exittime = userData.Return.Results.geofence.exit[count].time;
					geofence_exitGps = userData.Return.Results.geofence.exit[count].gps;
					if (!isDataNull(geofence_exitGps)) {
						var geofenceExitGps = geofence_exitGps.split(comma);

						// push data to markers array
						var geofenceExit = [];
						geofenceExit.push('Geofence Exit Location');
						geofenceExit.push(Number(geofenceExitGps[0]));
						geofenceExit.push(Number(geofenceExitGps[1]));
						geofenceExit.push('Exit Time : ' + geofence_exittime);
						geofenceExit.push('geofence_inout');
						geofenceExit.push(geofence_exittime);
						markers.push(geofenceExit);
					}
				}
			}
		}
		$(".geo_entertime").text("- " + displayGeofenceEnterTime);
		$(".geo_exittime").text("- " + displayGeofenceExitTime);

		var alertType = not_available;
		var alertTime = not_available;
		var alertData = not_available;
		var alert_gps = not_available;
		if (!isDataNull(userData.Return.Results.alert)
				&& userData.Return.Results.alert.length > 0) {
			var len = userData.Return.Results.alert.length;

			if (len > 0) {
				$(".alert_data").text(
						userData.Return.Results.alert[len - 1].type + "-"
						+ userData.Return.Results.alert[len - 1].time);
				for (var count = 0; count < len; count++) {
					alert_gps = userData.Return.Results.alert[count].gps;
					if (!isDataNull(alert_gps)) {
						var alertGps = alert_gps.split(comma);

						alertType = userData.Return.Results.alert[count].type;
						alertTime = userData.Return.Results.alert[count].time;

						var deviceAlert = [];
						deviceAlert.push(alertType);
						deviceAlert.push(Number(alertGps[0]));
						deviceAlert.push(Number(alertGps[1]));
						deviceAlert.push('Alert Time:' + alertTime);
						deviceAlert.push('alerts');
						deviceAlert.push(alertTime);
						markers.push(deviceAlert);
					}
				}
			}
		} else {
			$(".alert_data").text(alertData);
		}

		var location_gps = not_available;
		var location_gps_time = not_available;
		var centerLatLng = '';
		var defaultMarker = 0;

		if (!isDataNull(userData.Return.Results.gpslocation)) {
			if (userData.Return.Results.gpslocation.length > 0) {
				for (var count = 0; count < userData.Return.Results.gpslocation.length; count++) {
					location_gps = userData.Return.Results.gpslocation[count].gps
					if (!isDataNull(location_gps)) {
						location_gps_time = userData.Return.Results.gpslocation[count].time;

						var locationGps = location_gps.split(comma);

						var gpsLocation = [];
						gpsLocation.push('GPS Location');
						gpsLocation.push(Number(locationGps[0]));
						gpsLocation.push(Number(locationGps[1]));
						gpsLocation.push('GPS Location Time:'
								+ location_gps_time);
						gpsLocation.push('location');
						gpsLocation.push(location_gps_time);
						markers.push(gpsLocation);
					}
				}
			}
		}

		/*
		 * sort function to sort markers to center the map focusing at latest
		 * amongst alert, GPS location, Geo Fence Entry, Geo Fency Exit, School
		 * Entry, School Exit
		 */
		markers.sort(function(a, b) { // sort object by retirement date
			var dateA = new Date(a[5]);
			var dateB = new Date(b[5]);

			return (dateA - dateB) // sort by date ascending
		});

		/*
		 * Fix the center of map and the info window display attributes on
		 * the recent event
		 */
		if (markers.length > 0) {
			defaultMarker = markers.length - 1;
			centerLatLng = new google.maps.LatLng(markers[defaultMarker][1],
					markers[defaultMarker][2]);
		}

		var geocoder = new google.maps.Geocoder();
		var country = "Taiwan";
		var map;
		var places;

		var iconBase = '//maps.gstatic.com/intl/en_ALL/mapfiles/';
		var icons = {
				school_inout : {
					icon : iconBase + 'marker_purple.png'
				},
				geofence_inout : {
					icon : iconBase + 'marker_yellow.png'
				},
				alerts : {
					icon : iconBase + 'marker_orange.png'
				},
				location : {
					icon : iconBase + 'marker_green.png'
				}
		};

		// set some default map details, initial center point, zoom and
		// style
		var mapOptions = {
				zoom : 16,
				mapTypeId : google.maps.MapTypeId.ROADMAP
		};

		// create the map and reference the div#map-canvas container
		map = new google.maps.Map(document.getElementById("map-canvas"),
				mapOptions);
		var infowindow = new google.maps.InfoWindow({
			content : ''
		});

		// console.log(markers.length);
		for (i = 0; i < markers.length; i++) {
			var openInfoWindow = false;
			// create gmap latlng obj
			tmpLatLng = new google.maps.LatLng(markers[i][1], markers[i][2]);
			// make and place map maker.
			var marker = new google.maps.Marker({
				map : map,
				position : tmpLatLng,
				title : markers[i][0],
				icon : icons[markers[i][4]].icon
			});

			if (i == defaultMarker)
				openInfoWindow = true;

			bindInfoWindow(marker, map, infowindow, '<b>' + markers[i][0]
			+ "</b><br>" + markers[i][3], openInfoWindow);
		}

		if (centerLatLng == '') {
			debugLogs('centerLatLng Empty :(');
			geocoder.geocode({
				'address' : country
			}, function(results, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					map.setCenter(results[0].geometry.location);
				}
			});
		} else {
			debugLogs('centerLatLng -> ' + centerLatLng);
			map.setCenter(centerLatLng);
		}
	}
}

// binds a map marker and infoWindow together on click
function bindInfoWindow(marker, map, infowindow, html, openInfoWindow) {
	google.maps.event.addListener(marker, 'click', function() {
		infowindow.setContent(html);
		infowindow.open(map, marker);
	});
	if (openInfoWindow) {
		infowindow.setContent(html);
		infowindow.open(map, marker);
	}
};

function onSafetyFailure(data) {

	var not_available = "N/A";
	var enterTime = not_available;
	var exitTime = not_available;

	$(".enter_time").text(not_available);
	$(".exit_time").text(not_available);

	$(".geo_entertime").text(not_available);
	$(".geo_exittime").text(not_available);

	$(".reminder_title").text(not_available);
	$(".reminder_time").text(not_available);

	$(".alert_data").text(not_available);

	$(".announcement_data").text(not_available);
	$(".rewards_data").text(not_available);

	var failureResponse = data.status;
	console.log("failureResponse :  " + failureResponse);
}