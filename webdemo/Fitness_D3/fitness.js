/**
 * @author Jeff Huang
 * @email  gsmcci58@gmail.com
 */

var host = "192.168.0.123";  //"172.18.161.100";
var port = 3001;
var sid_url = "http://" + host + ":" + port + "/schoolids";
var uuid_url = "http://" + host + ":" + port + "/schooluuids/";
var context_url = "http://" + host + ":" + port + "/contextdata/";
var sleep_url = "http://" + host + ":" + port + "/sleepdata/";
var hrm_url = "http://" + host + ":" + port + "/hrmdata/";
var step_url = "http://" + host + ":" + port + "/stepdata/";

var default_width = 760;
var default_height = 200;

var url = context_url; //default
var contenttype = "context"; //default
var contenttitle = "Fitness Index";

var select_sid = d3.select("#school")
               .append('select')
             .attr('class','select')
               .attr("name", "select_sid")
               .on('change', updatesiduuids);

var select_uuid = d3.select("#uuid")
               .append('select')
               .attr('class','select')
               .attr("name", "select_uuid")
               .on('change', updatedatagraphic)
               .on('click', updatedatagraphic);

var select_content = d3.select("#content")
               .append('select')
               .attr('class','select')
               .attr("name", "select_content")
               .on('change', updatecontenturl);

var select_index = d3.select("#index")
               .append('select')
               .attr('class','select')
               .attr("name", "select_index")
               .on('change', updatehistogram);

function setupcontenttype() {
   var data = ["context", "sleep", "hrm", "step"];

   var appending = select_content
                   .selectAll('option')
                   .data(data).enter()
                   .append('option')
                   .text(function (d) { return d; });
}

function setupindextype() {
   var data = ["fitness index", "sleep quailty", "avg hr", "step count"];

   var appending = select_index
                   .selectAll('option')
                   .data(data).enter()
                   .append('option')
                   .text(function (d) { return d; });
}

setupindextype();
setupcontenttype();

function updatecontenturl() {
   contenttype = d3.select('[name="select_content"]').property('value');

   switch(contenttype) {
       case 'context':
            url = context_url;
            contenttitle = "Fitness Index";
            break;
       case 'sleep':
            url = sleep_url;
            contenttitle = "Sleep Quality";
            break;
       case 'hrm':
            url = hrm_url;
            contenttitle = "Heart Rate";
            break;
       case 'step':
            url = step_url;
            contenttitle = "Step Count";
            break;
   }

   d3.select("#demo .value").text(contenttitle);
   updatedatagraphic();
}

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

   //clean all graphics
   d3.select("#demo .value").text("");
   d3.select("#subjectchart").selectAll("svg").remove();
}

function updatesiduuids() {
   updateuuids();
   plothistogram();
}

function updatehistogram() {
   updateuuids();
   plothistogram();
}

var i = 0;
function plothistogram() {
   if (i == 0)
      d3.select("#hischart").append("svg");
   i = 1;

   var sid = d3.select('[name="select_sid"]').property('value');
   var indextype = d3.select('[name="select_index"]').property('value');

   var data = ["fitness index", "sleep quailty", "avg hr", "step count"];

   var url = "";
   var field = "";

   switch(indextype) {
      case data[0]:
           url = context_url;
          field = "activeindex";
          break;
      case data[1]:
           url = sleep_url;
          field = "ratio";
          break;
      case data[2]:
           url = hrm_url;
          field = "mean";
          break;
      case data[3]:
           url = step_url;
           field = "count";
          break;
   };

   //histogram
   d3.json(url + sid, function (error, json) {
       d3.select("#hischart").selectAll("g").remove();
       plotHistormGraphic(json, field);
   });
}

d3.json(sid_url, function (error, json) {
   select_sid
        .selectAll('option')
        .data(json).enter()
        .append('option')
        .text(function (d) { return d.sid; });

   console.log("test!");
   updatesiduuids();
});

function updatedatagraphic() {
   var sid = d3.select('[name="select_sid"]').property('value');
   var uuid = d3.select('[name="select_uuid"]').property('value');
   //alert(url);
   d3.json(url + sid + "?uuid=" + uuid, function (error, json) {
          //alert(json);
          updategraphics(json);
   });
}

function plotHistormGraphic(json, field) {
// set the dimensions and margins of the graph
  var margin = {top: 10, right: 60, bottom: 50, left: 60},
  width = default_width - margin.left - margin.right,
  height = default_height - margin.top - margin.bottom;

  var tsFn = function(d) { return new Date(d.ts*1000); };
  var yFn = function(d) { return d.value; };
  var formatCount = d3.format(",.0f");

  //data = json.slice();
  var data = d3.nest()
     .key(function(d) { return d.uuid;})
     .rollup(function(g) {
        if (field == "mean")
           return d3.sum(g, function(d) { return d[field]; }) / g.length;
        else if (field == "ratio") {
           quality_ratio = d3.sum(g, function(d) { return d.duration; }) / (8 * 60 * 60);
           return quality_ratio * d3.sum(g, function(d) { if (d.status == 3) return d[field]; else return 0;});
        } else {
           return d3.sum(g, function(d) { return d[field]; });
        }
     })
     .entries(json.slice());

  var data2 = new Array();
  data.forEach(function(d) {
      d.ts = d.key;
      data2.push(d.value);
  });

  var svg = d3.select("#hischart").selectAll("svg")
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
      .append("g")
      .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var x = d3.scaleLinear()
    .domain(d3.extent(data, yFn))
    .nice(20)
    .rangeRound([0, width]);

var bins = d3.histogram()
    .domain(x.domain())
    .thresholds(x.ticks(20))
    (data2);

var y = d3.scaleLinear()
    .domain([0, d3.max(bins, function(d) { return d.length; })])
    .range([height, 0]);

var bar = g.selectAll(".bar")
  .data(bins)
  .enter().append("g")
    .attr("class", "bar")
    .attr("transform", function(d) {return "translate(" + x(d.x0) + "," + y(d.length) + ")"; });

bar.append("rect")
    .attr("x", 1)
    .attr("width", x(bins[0].x1) - x(bins[0].x0) - 3)
    .attr("height", function(d) { return height - y(d.length); })
     .on("click", function(d) {
        min = d3.min(d);
        max = d3.max(d);

        select_uuid.selectAll('option').remove();

         var appending = select_uuid
                      .selectAll('option')
                      .data(data.filter(function(d) { return d.value >= min && d.value <= max; }));

         // add new elements
         appending.enter().append('option').text(function (d) { return d.key; });

         appending.exit().remove();
         d3.select("#subjectchart").selectAll("svg").remove();
         //d3.select("#barchart .value").text(d3.min(d) + ", " + d3.max(d));
      })
     .on("mouseout", function(d) {
        //d3.select("#barchart .value").text(contenttitle);
     });

bar.append("text")
    .attr("dy", ".75em")
    .attr("y", -10)
    .attr("x", (x(bins[0].x1) - x(bins[0].x0)) / 2)
    .attr("text-anchor", "middle")
    .style("fill", "#000000")
    .text(function(d) { return formatCount(d.length); });

g.append("g")
    .attr("class", "axis axis--x")
    .attr("transform", "translate(0," + height + ")")
    .call(d3.axisBottom(x));
}

function Fn(d, contenttype) {
    switch (contenttype) {
           case "context":
                return d.activeindex;
           case "sleep":
                if (d.status == 3)
                   return d.ratio;
                else
                   return 0;
           case "hrm":
                return d.mean * d.count;
           case "step":
                return d.count;
    }
}

var contextsituation = {1: "static", 2: "step", 3: "running", 4: "cycling", 5: "sleep", 0: "other"};
var sleepstatus = {1: "進入睡眠", 2: "淺層睡眠", 3: "深度睡眠", 4: "狀態切換或起床", 5: "結束睡眠"};

var indexMap = new Map();
indexMap.set("context", [1 ,2 ,3 ,4]);
indexMap.set("sleep", [1, 2, 3, 4, 5]);
indexMap.set("hrm", [0, 1, 2, 3, 4 ,5]);
indexMap.set("step", [2, 3]);

function plotMultiLine(json, contenttype, field, groupfield) { //20170519
  var tsFn = function(d) { return new Date(d.ts*1000); };
  var yFn = function(d) { return d.value; };

  var situation = contextsituation;
  if (contenttype == "sleep") {
   situation = sleepstatus;
  }

  //data = json.slice();
  var data = d3.nest()
     .key(function(d) { return d.ts;})
     .rollup(function(g) {
      return d3.nest().
      key(function(gd) {return gd[groupfield];})
      .rollup(function(gg) {return gg[0][field];})
      .entries(g);
     })
     .entries(json.slice());

    data.forEach(function(d) {
      d.date = new Date(d.key*1000);
      d.max = 0;
      d.value.forEach(function (k) { // keyname is field name of table.
         d[situation[k.key]] = k.value;
         d.max = Math.max(d.max, k.value); //get max value of d.value.
      });
    });

    var naomitFn = function (d, key) {if (typeof d[key] != 'undefined') {
         return d[key];
      } else {
         return 0;
      }
    };

    // set the dimensions and margins of the graph
    var margin = {top: 10, right: 30, bottom: 30, left: 120},
    width = default_width - margin.left - margin.right,
    height = default_height - margin.top - margin.bottom;
    var legendRectSize = 16;                                  // NEW
    var legendSpacing = 2;                                    // NEW

    // set the ranges
    var x = d3.scaleTime().range([0, width]);
    var y = d3.scaleLinear().range([height, 0]);

    var svg = d3.select("#mixchart").append("svg")
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
      .append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    x.domain(d3.extent(data, function (d) {
      return d.date;
    }));

    y.domain([0, d3.max(data, function (d) {
      return d.max;
    })]);

    // define the line
    var line = d3.line()
      .x(function (d) {
        return x(d.date);
      });

    var color = d3.scaleOrdinal(d3.schemeCategory20);

    indexMap.get(contenttype).forEach ( function (i) {
      key = situation[i];
      line.y(function (d) {
        return y(naomitFn(d, key));
      });
      
      // Add the line path.
      svg.append("path")
         .attr("class", "line")
         .style("fill", "none")
         .attr("d", line(data))
         .style("stroke", color(key));

      // Add sales to the scatterplot
      svg.selectAll("." + key + "-circle")
         .data(data)
         .enter().append("circle")
         .attr('class', key + "-circle")
         .attr("r", 5)
         .attr("cx", function(d) { return x(d.date); })
         .attr("cy", function(d) { return y(naomitFn(d, key)); })
         .style("fill", color(key));
    });

    //Create Title
 /*   svg.append("text")
       .attr("x", width / 2)
       .attr("y", 0 - margin.top / 2)
       .style("text-anchor", "middle")
       .style("font-size", "14px") 
       .text(field);*/

    // Add the X Axis
    svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x));

  // text label for the x axis
  /*svg.append("text")
      .attr("transform",
            "translate(" + (width/2) + " ," +
                           (height + margin.top + 5) + ")")
      .style("text-anchor", "middle")
      .text("Date");*/

  // add the y Axis
  svg.append("g")
      .call(d3.axisLeft(y));

  // text label for the y axis
  svg.append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 0 - margin.left*0.6)
      .attr("x",0 - (height / 2))
      .attr("dy", "1em")
      .style("text-anchor", "middle")
      .text(field);
/*
        var legend = svg.selectAll('.legend')                     // NEW
          .data(color.domain())                                   // NEW
          .enter()                                                // NEW
          .append('g')                                            // NEW
          .attr('class', 'legend')                                // NEW
          .attr('transform', function(d, i) {                     // NEW
            var height = legendRectSize + legendSpacing;          // NEW
            var offset =  height * color.domain().length / 2;     // NEW
            var horz = width + 10;                                 // NEW
            var vert = i * height;                       // NEW
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
          .style("font-size", "12px")
          .text(function(d) { return d; });
*/
}

function plotBarGraphic(json, contenttype) {
// set the dimensions and margins of the graph
  var margin = {top: 10, right: 60, bottom: 30, left: 120},
  width = default_width - margin.left - margin.right,
  height = default_height - margin.top - margin.bottom;

  // parse the date / time
  var parseDate = d3.timeFormat("%d-%m-%Y");

  var getUTCDate = function (ts) {return new Date(ts*1000);};
  var tsFn = function(d) {return getUTCDate(d.ts); };
  var yFn = function(d) { return d.value; };

  //data = json.slice();
  var data = d3.nest()
     .key(function(d) { return d.ts;})
//.rollup(function(g) { return d3.sum(g, function(d) {return d.activeindex; });})
     .rollup(function(g) {
         var count = 1;
         var qulity_ratio = 1;
         if (contenttype == "hrm")
            count = d3.sum(g, function(d) { return d.count; });
         else if (contenttype == "sleep") {
            total_duration = d3.sum(g, function(d) { return d.duration; });
            qulity_ratio = total_duration / (8 * (60 * 60));
         }

         return qulity_ratio * d3.sum(g, function(d) { return Fn(d, contenttype); }) / count;
     })
     .entries(json.slice());

  data.forEach(function(d) {
      d.ts = d.key;
      if (contenttype == "hrm")
         d.value = Math.round(d.value);
      else if (contenttype == "sleep")
         d.value = d3.format(",.2f")(d.value);
  });

  // set the ranges
  var x = d3.scaleTime()
          .domain(d3.extent(data, tsFn))
          .rangeRound([0, width]);

  var y = d3.scaleLinear()
          .range([height, 0])
          .domain([0, d3.max(data, yFn)]);

  var svg = d3.select("#barchart").append("svg")
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
      .append("g")
      .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

  svg.selectAll("rect").data(data)
     .enter()
     .append("rect")
     .attr("class", "bar")
     .attr("x", 1)
     .attr("transform", function(d) {
  return "translate(" + x(tsFn(d)) + "," + y(yFn(d)) + ")"; })
     .attr("width", function(d) { return x(getUTCDate(parseInt(d.ts) + 86400)) - x(getUTCDate(d.ts)) -1 ; })
     .attr("height", function(d) { return height - y(d.value); })
     .attr("style", "cursor: pointer;")
     .on("click", function(d) {
        d3.select("#barchart .value").text("Date: " +  parseDate(new Date(d.ts*1000)) + " " + contenttitle + ": " + d.value);
     })
     .on("mouseout", function(d) {
        d3.select("#barchart .value").text("");
     });

  // add the x Axis
  svg.append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x));

  // text label for the x axis
  /*svg.append("text")
      .attr("transform",
            "translate(" + (width/2) + " ," +
                           (height + margin.top + 5) + ")")
      .style("text-anchor", "middle")
      .text("Date");*/

  // add the y Axis
  svg.append("g")
      .call(d3.axisLeft(y));

  // text label for the y axis
  svg.append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 0 - margin.left * 0.6)
      .attr("x",0 - (height / 2))
      .attr("dy", "1em")
      .style("text-anchor", "middle")
      .text(contenttitle);
}

function contextFeature (d, field) {
/*  fieldsMap.set("sleep", ["duration", "ratio"]);
  fieldsMap.set("hrm", ["count", "mean", "max", "min"]);
  fieldsMap.set("step", ["cal", "count", "distance"]);
*/
   switch (field) {
          case 'activeindex':
               return d.activeindex;
          case 'met':
               return d.met;
          case 'duration':
               return d.duration;
          case 'avghrm':
               return d.avghrm;
          case 'ratio':
               return d.ratio;
          case 'count':
               return d.count;
          case 'mean':
               return d.mean;
          case 'max':
               return d.max;
          case 'min':
               return d.min;
          case 'cal':
               return d.cal;
          case 'distance':
               return d.distance;
          case 'sd':
               return d.sd;
   }
}

function setupdata (json, contenttype, field) {
     var dataset = d3.nest()
         .key ( function(d) {
                               switch (contenttype) {
                                  case "context":
                                      return d.situation;
                                  case "sleep":
                                      return d.status;
                                  case "hrm":
                                      return d.situation;
                                  case "step":
                                      return d.type;
                               }
                            })
         .rollup(function(g) { return d3.sum(g, function(d) {return contextFeature(d, field); })/g.length;})
         .entries(json.slice());

// 1 : 進入睡眠, 2 : 淺層睡眠, 3 : 深度睡眠, 4 : 狀態切換或起床, 5 : 結束睡眠
     dataset.forEach(function(d) {
         d.label = 'other';
         switch(d.key) {
             case '1':
                 if (contenttype == "sleep")
                    d.label = '進入睡眠';
                 else
                    d.label = 'static';
                 break;
             case '2':
                 if (contenttype == "sleep")
                    d.label = '淺層睡眠';
                 else
                    d.label = 'step';
                 break;
             case '3':
                 if (contenttype == "sleep")
                    d.label = '深度睡眠';
                 else
                    d.label = 'running';
                 break;
             case '4':
                 if (contenttype == "sleep")
                    d.label = '狀態切換或起床';
                 else
                    d.label = 'cycling';
                 break;
             case '5':
                 if (contenttype == "sleep")
                    d.label = '結束睡眠';
                 else
                    d.label = 'sleep';
         }
     });

     return dataset;
}

function percentGraphic (json, contenttype, field, schemecategory) {
     var dataset = setupdata(json, contenttype, field);

     var total = 0;
     dataset.forEach(function(d) {
         total = total + d.value;
     });

        var margin = {top: 20, right: 20, bottom: 20, left: 60},
        width = default_height + 80 - margin.left - margin.right,
        height = default_height + 80 - margin.top - margin.bottom;

       // var width = 250;
       // var height = 250;
        var radius = Math.min(width, height) / 2;
        var donutWidth = 30;
        var legendRectSize = 18;                                  // NEW
        var legendSpacing = 4;                                    // NEW

        var color = d3.scaleOrdinal(schemecategory);

        var svg = d3.select('#mixchart')
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
    .style("font-size", "12px")
    .text(function(d){
                if (field != "avghrm" && contenttype != "hrm")
                   return Math.round(d.data.value/total*100)+"%" ;
                else
                   return Math.round(d.data.value);
    });

    //Create Title
    /*svg.append("text")
    .attr("x", 0)
    .attr("y", margin.top - height/2)
    .style("text-anchor", "middle")
    .text(field);*/

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
          .style("font-size", "12px")
          .text(function(d) { return d; });
}

var fieldsMap = new Map();
fieldsMap.set("context", ["activeindex", "met", "duration", "avghrm"]);
fieldsMap.set("sleep", ["duration"]);
fieldsMap.set("hrm", ["mean", "min", "max", "sd"]);
fieldsMap.set("step", ["cal", "count", "distance"]);

function pieCharts (json, contenttype) {
  fieldsMap.get(contenttype).forEach( function (d) {
      percentGraphic(json, contenttype, d, d3.schemeCategory20);
  });
}

function lineCharts (json, contenttype) {
  groupfield = "situation";
  if (contenttype == "sleep")
     groupfield = "status";
  else if (contenttype == "step")
     groupfield = "type";

  fieldsMap.get(contenttype).forEach( function (d) {
      plotMultiLine(json, contenttype, d, groupfield);
  });
}

function mixCharts (json, contenttype) {
  groupfield = "situation";
  if (contenttype == "sleep")
     groupfield = "status";
  else if (contenttype == "step")
     groupfield = "type";

  fieldsMap.get(contenttype).forEach( function (d) {
      plotMultiLine(json, contenttype, d, groupfield);
      percentGraphic(json, contenttype, d, d3.schemeCategory20);
  });
}

function updategraphics(json) {
  d3.select("#subjectchart").selectAll("svg").remove();

  plotBarGraphic(json, contenttype);
  mixCharts (json, contenttype);
  //lineCharts(json, contenttype);
  //plotMultiLine(json, contenttype, "hrm", "situation");
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
