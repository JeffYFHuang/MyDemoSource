
var DATATYPE = {STRING: "str", DATE: "date", INTEGER: "int"};
var SORTORDER = {ASC:"ascending", DESC: "descending"};

function jsonSort(incomingJSON, sortKey, keyDataType, sortingOrder, shallParseJSON){
	if(!isDataNull(incomingJSON) && !isDataNull(sortKey)){
		var jsonObj = incomingJSON;
		if(!isDataNull(shallParseJSON)) {
			jsonObj = $.parseJSON(incomingJSON).data;
		} else{
			debugLogs('In Else1');
		}
		var numberOfElements = jsonObj.length;
		if(numberOfElements > 1) {
			mergeSort(jsonObj, 0, (numberOfElements-1), sortKey, keyDataType, sortingOrder);
		} else{
			debugLogs('In Else2');
		}
	} else{
		debugLogs('In Else3');
	}
	
	return JSON.stringify(jsonObj);
	
} // End of jsonSort method

function mergeSort(jsonObject, start, end, key, dataType, sortingOrder) {
	var middle = (start+end)/2;
	middle = Math.floor(middle);
	if(start < end) {
		mergeSort(jsonObject, start, middle, key, dataType, sortingOrder);
		mergeSort(jsonObject, middle+1, end, key, dataType, sortingOrder);
		merge(jsonObject, start, middle, end, key, dataType, sortingOrder);
	}
} // End of mergeSort method

function merge(jsonObject, start, middle, end, sortKey, dataType, sortingOrder){
	var tempArray = new Array(end-start+1);
	var cPos = 0, lPos = start, rPos = middle+1;
	while(lPos <= middle && rPos <= end){
		switch(dataType){
			case DATATYPE.STRING:
				var x = eval('jsonObject['+lPos+'].'+sortKey);
				var y = eval('jsonObject['+rPos+'].'+sortKey);
				var finalLog = "Value of x: " + x + " and y: " + y + " and sorting order being: " + sortingOrder;
				switch(sortingOrder){
					case SORTORDER.DESC:
						if(x > y){
							tempArray[cPos++] = jsonObject[lPos++];
						} else {
							tempArray[cPos++] = jsonObject[rPos++];
						}
					break;
					default:
						if(x < y){
							tempArray[cPos++] = jsonObject[lPos++];
						} else {
							tempArray[cPos++] = jsonObject[rPos++];
						}
					break;
				}
				finalLog += "\nlPos: " + lPos + "\tmid: " + middle + "\trPos: " + rPos + "\tcPos: " + cPos;
				debugLogs(finalLog);
			break;
			case DATATYPE.DATE:
				var x = new Date(eval('jsonObject['+lPos+'].'+sortKey));
				var y = new Date(eval('jsonObject['+rPos+'].'+sortKey));
				var finalLog = "Value of x: " + x + " and y: " + y + " and sorting order being: " + sortingOrder;
				switch(sortingOrder){
					case SORTORDER.DESC:
						if(x > y){
							tempArray[cPos++] = jsonObject[lPos++];
						} else {
							tempArray[cPos++] = jsonObject[rPos++];
						}
					break;
					default:
						if(x < y){
							tempArray[cPos++] = jsonObject[lPos++];
						} else {
							tempArray[cPos++] = jsonObject[rPos++];
						}
					break;
				}
				finalLog += "\nlPos: " + lPos + "\tmid: " + middle + "\trPos: " + rPos + "\tcPos: " + cPos;
				debugLogs(finalLog);
			break;
			case DATATYPE.NUMBER:
				var x = parseInt(eval('jsonObject['+lPos+'].'+sortKey));
				var y = parseInt(eval('jsonObject['+rPos+'].'+sortKey));
				var finalLog = "Value of x: " + x + " and y: " + y + " and sorting order being: " + sortingOrder;
				switch(sortingOrder){
					case SORTORDER.DESC:
						if(x > y){
							tempArray[cPos++] = jsonObject[lPos++];
						} else {
							tempArray[cPos++] = jsonObject[rPos++];
						}
					break;
					default:
						if(x < y){
							tempArray[cPos++] = jsonObject[lPos++];
						} else {
							tempArray[cPos++] = jsonObject[rPos++];
						}
					break;
				}
				finalLog += "\nlPos: " + lPos + "\tmid: " + middle + "\trPos: " + rPos + "\tcPos: " + cPos;
				debugLogs(finalLog);
			break;
		}
	}

	/* Copy remaining elements of the original array to the temp array */
	while(lPos <= middle) {
		tempArray[cPos++] = jsonObject[lPos++];
	}

	while(rPos <= end){
		tempArray[cPos++] = jsonObject[rPos++];
	}

	/* Copy back the temp array to original json array object */
	for(var index=0; index<cPos; index++) {
		jsonObject[index+start] = tempArray[index];
	}
} // End of merge method
