var sid_url = "http://localhost:3000/schoolids";
var uuid_url = "http://localhost:3000/schooluuids/"; 
var context_url = "http://localhost:3000/contextdata/";
var sleep_url = "http://localhost:3000/sleepdata/";

var select_sid = d3.select("#school")
               .append('select')
  	       .attr('class','select')
               .attr("name", "select_sid")
               .on('change', updateuuids);

var select_uuid = d3.select("#uuid")
               .append('select')
               .attr('class','select')
               .attr("name", "select_uuid")
               .on('change', updatefitness);

function updateuuids() {
   var sid = d3.select('[name="select_sid"]').property('value');

   d3.json(uuid_url + sid, function (error, json) {
      // bind data
      var appending = select_uuid
                      .selectAll('option')
                      .data(json);

      // add new elements
      appending.enter().append('option').text(function (d) { return d.uuid; });

      // update existing elements
      appending.transition()
        .duration(0)
        .text(function (d) { return d.uuid; });

      // remove old elements
      appending.exit().remove();
   });
}

d3.json(sid_url, function (error, json) {
   select_sid
        .selectAll('option')
        .data(json).enter()
        .append('option')
        .text(function (d) { return d.sid; })

   updateuuids();
});

function updatefitness() {
   var sid = d3.select('[name="select_sid"]').property('value');
   var uuid = d3.select('[name="select_uuid"]').property('value');
   //alert(uuid);
   d3.json(context_url + sid + "?uuid=" + uuid, function (error, json) {
          //alert(json);
          updategraphics(json);
   });
}

function activeIndexBarGraphic(json) {
// set the dimensions and margins of the graph
  var margin = {top: 10, right: 30, bottom: 30, left: 40},
  width = 960 - margin.left - margin.right,
  height = 250 - margin.top - margin.bottom;

  // parse the date / time
  var parseDate = d3.timeFormat("%d-%m-%Y");

  var activeidxFn = function(d) { return d.activeindex };
  var tsFn = function(d) { return new Date(d.ts*1000) };

  //data = json.slice();

  var data = d3.nest()
     .key(function(d) { return d.ts;})
     .rollup(function(g) { return d3.sum(g, function(d) {return d.activeindex; });})
     .entries(json.slice());

  data.forEach(function(d) {
      d.ts = d.key;
      d.activeindex = d.value;
  });

  // set the ranges
  var x = d3.scaleTime()
          .domain(d3.extent(data, tsFn))
          .rangeRound([0, width]);

  var y = d3.scaleLinear()
          .range([height, 0])
          .domain(d3.extent(data, activeidxFn));

  var svg = d3.select("#demo").append("svg")
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
      .append("g")
      .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

  // format the data
/*  data.forEach(function(d) {
      d.date = parseDate(new Date(d.ts*1000));
  });*/

  svg.selectAll("rect").data(data)
     .enter()
     .append("rect")
     .attr("class", "bar")
     .attr("x", 1)
     .attr("transform", function(d) {
		  return "translate(" + x(tsFn(d)) + "," + y(activeidxFn(d)) + ")"; })
     .attr("width", function(d) { return x(new Date(d.ts*1000+86400000)) - x(new Date(d.ts*1000)) -1 ; })
     .attr("height", function(d) { return height - y(activeidxFn(d)); })
     .attr("style", "cursor: pointer;")
     .on("mouseover", function(d) {
        d3.select("#demo .value").text("Date: " +  parseDate(new Date(d.ts*1000)) + " Active Index: " + activeidxFn(d))
      })
     .on("mouseout", function(d) {
        d3.select("#demo .value").text("Active Index")
      })

  // add the x Axis
  svg.append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x));

  // add the y Axis
  svg.append("g")
      .call(d3.axisLeft(y));
}

function contextFeature (d, type) {
   switch(type) {
             case 'activeindex':
                 return d.activeindex;
             case 'met':
                 return d.met;
             case 'duration':
                 return d.duration;
             case 'hrm':
                 return d.avghrm;
   }
}

function percentContextGraphic (json, type, schemecategory) {
     var dataset = d3.nest()
         .key(function(d) { return d.situation;})
         .rollup(function(g) { return d3.sum(g, function(d) {return contextFeature(d, type); })/g.length;})
         .entries(json.slice());

     total = 0;

     dataset.forEach(function(d) {
         d.label = 'other';
         switch(d.key) {
             case '1':
                 d.label = 'static';
                 break;
             case '2':
                 d.label = 'step';
                 break;
             case '3':
                 d.label = 'running';
                 break;
             case '4':
                 d.label = 'cycling';
                 break;
         }

         total = total + d.value;
     });

        var margin = {top: 20, right: 50, bottom: 30, left: 40},
        width = 320 - margin.left - margin.right,
        height = 360 - margin.top - margin.bottom;

       // var width = 250;
       // var height = 250;
        var radius = Math.min(width, height) / 2;
        var donutWidth = 55;
        var legendRectSize = 18;                                  // NEW
        var legendSpacing = 4;                                    // NEW

        var color = d3.scaleOrdinal(schemecategory);

        var svg = d3.select('#chart')
          .append('svg')
          .attr('width', width)
          .attr('height', height)
          .append('g')
          .attr('transform', 'translate(' + (width / 2) +
            ',' + (height / 2) + ')');

        var arc = d3.arc()
          .innerRadius(radius - donutWidth)
          .outerRadius(radius);

        var pie = d3.pie()
          .value(function(d) { return d.value; })
          .sort(null);

        var path = svg.selectAll('path')
          .data(pie(dataset))
          .enter()
          .append('path')
          .attr('d', arc)
          .attr('fill', function(d, i) {
            return color(d.data.label);
          });

        var text = svg.selectAll('text')
	    .data(pie(dataset))
	    .enter()
	    .append("text")
	    .attr("transform", function (d) {
		return "translate(" + arc.centroid(d) + ")";
            })
	    .attr("text-anchor", "middle")
	    .text(function(d){
                if (type != "hrm")
		   return Math.round(d.data.value/total*100)+"%" ;
                else
                   return Math.round(d.data.value);
	    })
	    .style({
		fill:function(d,i){
			return color(i);
		},
		'font-size':'18px',
	    });

    //Create Title
    svg.append("text")
    .attr("x", 0)
    .attr("y", margin.top - height/2)
    .style("text-anchor", "middle")
    .text(type);

        var legend = svg.selectAll('.legend')                     // NEW
          .data(color.domain())                                   // NEW
          .enter()                                                // NEW
          .append('g')                                            // NEW
          .attr('class', 'legend')                                // NEW
          .attr('transform', function(d, i) {                     // NEW
            var height = legendRectSize + legendSpacing;          // NEW
            var offset =  height * color.domain().length / 2;     // NEW
            var horz = -2 * legendRectSize;                       // NEW
            var vert = i * height - offset;                       // NEW
            return 'translate(' + horz + ',' + vert + ')';        // NEW
          });                                                     // NEW

        legend.append('rect')                                     // NEW
          .attr('width', legendRectSize)                          // NEW
          .attr('height', legendRectSize)                         // NEW
          .style('fill', color)                                   // NEW
          .style('stroke', color);                                // NEW

        legend.append('text')                                     // NEW
          .attr('x', legendRectSize + legendSpacing)              // NEW
          .attr('y', legendRectSize - legendSpacing)              // NEW
          .text(function(d) { return d; });
}

function updategraphics(json) {
  d3.selectAll("svg").remove();
  percentContextGraphic(json, 'activeindex', d3.schemeCategory20);
  percentContextGraphic(json, 'met', d3.schemeCategory10);
  percentContextGraphic(json, 'duration', d3.schemeCategory10);
  percentContextGraphic(json, 'hrm', d3.schemeCategory20);
  activeIndexBarGraphic(json);
}

/*JSONData = [
  { "id": 3, "created_at": "Sun May 05 2013", "amount": 12000},
  { "id": 1, "created_at": "Mon May 13 2013", "amount": 2000},
  { "id": 2, "created_at": "Thu Jun 06 2013", "amount": 17000},
  { "id": 4, "created_at": "Thu May 09 2013", "amount": 15000},
  { "id": 5, "created_at": "Mon Jul 01 2013", "amount": 16000}
]

(function() {
  var data = JSONData.slice()
  var format = d3.time.format("%a %b %d %Y")
  var amountFn = function(d) { return d.amount }
  var dateFn = function(d) { return format.parse(d.created_at) }

  var x = d3.time.scale()
    .range([10, 280])
    .domain(d3.extent(data, dateFn))

  var y = d3.scale.linear()
    .range([180, 10])
    .domain(d3.extent(data, amountFn))
  
  var svg = d3.select("#demo").append("svg:svg")
  .attr("width", 300)
  .attr("height", 200)

  svg.selectAll("circle").data(data).enter()
   .append("svg:circle")
   .attr("r", 4)
   .attr("cx", function(d) { return x(dateFn(d)) })
   .attr("cy", function(d) { return y(amountFn(d)) }) 
})();*/
