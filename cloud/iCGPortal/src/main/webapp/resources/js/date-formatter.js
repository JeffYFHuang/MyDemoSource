function getDate(pastDay) {
	var today = new Date();
	var yesterday = new Date(today);
	if(pastDay > 0){
		yesterday.setDate(today.getDate() - pastDay);
	}else{
		yesterday.setDate(today.getDate());
	}

	var date = yesterday.getDate(), month = "January,February,March,April,May,June,July,August,September,October,November,December"
			.split(",")[yesterday.getMonth()];

	function nth(d) {
		if (d > 3 && d < 21)
			return 'th'; 
		switch (d % 10) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}
	var yesterday = date + nth(date) + " " + month + " "
			+ yesterday.getFullYear();

	return yesterday;
} //End of getDate method

function dbDateFormat(inputDate){
	//This method is to convert display date(DD-MM-YYYY) to db date format (YYYY-MM-DD)
	if(isDataNull(inputDate)){
		var today = new Date();
		var dd = today.getDate();
		var mm = today.getMonth()+1; //January is 0!
		var yyyy = today.getFullYear();

		if(dd<10) {
		    dd = '0'+dd
		} 

		if(mm<10) {
		    mm = '0'+mm
		} 

		inputDate = dd + '-' + mm + '-' + yyyy;
	}
	var initial = inputDate.split(/-/);
	var output = ([ initial[2], initial[1], initial[0] ].join('-')); //=> 'yyyy-mm-dd'
	
	return output;
} //End of formatDate method

function navigateWeek(jumpto){
	if(jumpto <= 0){
		var output = [];
		var sDate = moment().add(jumpto, "week").startOf("week").format("YYYY-MM-DD");
		var eDate = moment().add(jumpto, "week").endOf("week").format("YYYY-MM-DD");
		
		var sDay = moment().add(jumpto, "week").startOf("week").format("YYYY-MM-DD"); //"Jul 16th"
		var eDay = moment().add(jumpto, "week").endOf("week").format("YYYY-MM-DD"); //"22nd, 2017"
		var week = sDay + ' - ' + eDay;
		
		output.push(sDate);
		output.push(eDate);
		output.push(week);
		output.push('week');
		
		return output;
	}
	return false;
}

function navigateMonth(jumpto){
	if(jumpto <= 0){
		var output = [];
		var sDate = moment().add(jumpto, "month").startOf("month").format("YYYY-MM-DD");
		var eDate = moment().add(jumpto, "month").endOf("month").format("YYYY-MM-DD");
		
		var smonth = moment().add(jumpto, "month").startOf("month").format("YYYY-MM-DD"); //"June 2017"
		var emonth = moment().add(jumpto, "month").endOf("month").format("YYYY-MM-DD");
		var month = smonth + ' - ' +emonth;
		
		output.push(sDate);
		output.push(eDate);
		output.push(month);
		output.push('month');
		
		return output;
	}
	return false;
}

function formatDate(epochTime, outputformat) {
	if(epochTime.toString().length == 10) 
		epochTime = epochTime*1000;
	
	var  epocTimeInput = moment(epochTime);
	var convertedDateFormat = epocTimeInput.format(outputformat);
	return convertedDateFormat;

}