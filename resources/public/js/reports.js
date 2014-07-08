var generateReportListLink = function() {
   return '<a href="javascript:void(0);" onclick="getReportList();">Return to full report list</a>';
};

var generateReportLink = function(report) {
    return '<a href="javascript:void(0);" onclick="loadReport(\'' + report.id + '\');">' + report.name + '</a>'
};

$(document).ready( function () {
    getReportList();
});

function getReportList() {
    $.get('/reports', function (data) {
        $('#getResult').html( function () {
            var jsonArray = $.parseJSON(data);
            var output = '';
            $.each(jsonArray, function (index, obj) {
                output += generateReportLink(obj) + "<br />";
            });
            return output;
        });
    });
}

function loadReport(reportId) {
    $.get('/reports/' + reportId, function (data) {
        $('#getResult').html( function () {
            var output = "<p>" + generateReportListLink() + "</p>";
            output += data;

            return output;
        });
    });
}

function formatReport(jsonData) {


    var data = [
      { "id": 3, "created_at": "Sun May 05 2013", "amount": 12000},
      { "id": 1, "created_at": "Mon May 13 2013", "amount": 2000},
      { "id": 2, "created_at": "Thu Jun 06 2013", "amount": 17000},
      { "id": 4, "created_at": "Thu May 09 2013", "amount": 15000},
      { "id": 5, "created_at": "Mon Jul 01 2013", "amount": 16000}
    ];

    (function() {
      var data = jsonData.slice()
      var format = d3.time.format("%a %b %d %Y")
      var amountFn = function(d) { return d.amount }
      var dateFn = function(d) { return format.parse(d.created_at) }

      var x = d3.time.scale()
        .range([10, 280])
        .domain(d3.extent(data, dateFn))

      var y = d3.scale.linear()
        .range([180, 10])
        .domain(d3.extent(data, amountFn))

      var svg = d3.select("#chart").append("svg:svg")
      .attr("width", 300)
      .attr("height", 200)

      svg.selectAll("circle").data(data).enter()
       .append("svg:circle")
       .attr("r", 4)
       .attr("cx", function(d) { return x(dateFn(d)) })
       .attr("cy", function(d) { return y(amountFn(d)) })
    })();
}


var temporaryData = '';