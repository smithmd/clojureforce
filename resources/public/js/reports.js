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
    $('#chart').html('');
}

function loadReport(reportId) {
    $.get('/reports/' + reportId, function (data) {
        $('#getResult').html( function () {
            var output = "<p>" + generateReportListLink() + "</p>";
            return output;
        });
        formatReport($.parseJSON(data));
    });
}

function formatReport(jsonData) {
    var w = 750;
    var h = 100;

    var dataset = prepData(jsonData);

    var svg = d3.select("#chart")
                .append("svg")
                .attr("width", w)
                .attr("height", h);

    var circles = svg.selectAll("circle")
                     .data(dataset)
                     .enter()
                     .append("circle");

    circles.attr("cx", function (d,i) {
                return (i * 50) + 30;
            })
            .attr("cy", h/2)
            .attr("r", function(d) {
                return Math.ceil(d.aggregates[0].value / 1000) + 1;
            });
}

function prepData(jsonData) {
    var jsonArray = [];
    for (var key in jsonData.factMap) {
        if (jsonData.factMap.hasOwnProperty(key)) {
            jsonArray.push(jsonData.factMap[key]);
        }
    }

    return jsonArray;
}
