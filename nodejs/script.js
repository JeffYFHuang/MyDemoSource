var sid_url = "http://localhost:3000/schoolids";
var uuid_url = "http://localhost:3000/schooluuids/"; 
var context_url = "http://localhost:3000/contextdata/";
var sleep_url = "http://localhost:3000/sleepdata/";

var data = ["Option 1", "Option 2", "Option 3"];

var select_sid = d3.select('body')
               .append('select')
  	       .attr('class','select')
               .attr("name", "select_sid")
               .on('change', updateuuids);

var select_uuid = d3.select('body')
               .append('select')
               .attr('class','select')
               .attr("name", "select_uuid")
               .on('change', updatefitness);

function updateuuids() {
   var sid = d3.select('[name="select_sid"]').property('value');
   //alert(sid)
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
        .text(function (d) {return d.uuid; });

      // remove old elements
      appending.exit().remove();
   });
}

d3.json(sid_url, function (error, json) {
   var options = select_sid
                 .selectAll('option')
	         .data(json).enter()
	         .append('option')
		 .text(function (d) { return d.sid; })
   updateuuids();
});

function updatefitness() {
   var sid = d3.select('[name="select_sid"]').property('value');
   var uuid = d3.select('[name="select_uuid"]').property('value');

   d3.json(context_url + sid + "?uuid=" + uuid, function (error, json) {
          //alert(json);
          updategraphics(json);
   });
}

function updategraphics(json) {
  var width = 720,
      height = 240;

  d3.select("svg").remove();
  
  var svg = d3.select("#demo").append("svg:svg")
  .attr("width", 820)
  .attr("height", 300);

  var data = json.slice()
  var activeidxFn = function(d) { return d.activeindex };
  var tsFn = function(d) {
                            var date = new Date(d.ts*1000)
                            return date.getDate()*1000;
                         };

  var scaleX = d3.time.scale()
    .range([0, width])
    .domain(d3.extent(data, tsFn));

  var scaleY = d3.scale.linear()
    .range([height, 0])
    .domain(d3.extent(data, activeidxFn));

  var axisX = d3.svg.axis()
      .scale(scaleX)
      .orient("bottom")
      .ticks(10);

  var axisY = d3.svg.axis()
      .scale(scaleY)
      .orient("left")
      .ticks(10);

  var svgdata = svg.selectAll("circle").data(data);
  svgdata.enter()
   .append("svg:circle")
   .attr("r", 4)
   .attr("cx", function(d) { return scaleX(tsFn(d)) })
   .attr("cy", function(d) { return scaleY(activeidxFn(d)) })
   .attr({
      'fill':'none',
      'stroke':'#000',
      'transform':'translate(35,20)'
     })
   .attr("style", "cursor: pointer;")
   .on("mouseover", function(d) {
      d3.select("#demo .value").text("Date: " + new Date(d.ts*1000) + " activeIndex: " + activeidxFn(d))
   })
   .on("mouseout", function(d) {
      d3.select("#demo .value").text("activeIndex:")
   })

/*  svgdata.transition()
         .duration(0)
         .attr("r", 4)
         .attr("cx", function(d) { return scaleX(tsFn(d)) })
         .attr("cy", function(d) { return scaleY(activeidxFn(d)) })
        .attr({
           'fill':'none',
           'stroke':'#000',
           'transform':'translate(40,20)'
        });*/

  d3.select("svg").append('g')
     .call(axisX)  //call axisX
     .attr({
      'fill':'none',
      'stroke':'#000',
      'transform':'translate(40,'+(height+20)+')' 
     });

  d3.select("svg").append('g')
     .call(axisY)  //call axisY
     .attr({
      'fill':'none',
      'stroke':'#000',
      'transform':'translate(40, 20)' 
     });

  svgdata.exit().remove();
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
