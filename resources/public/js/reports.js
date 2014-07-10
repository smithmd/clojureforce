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
    var h = 200;
    var barPadding = 3;

    var data_set = prepData(jsonData);

    var svg = d3.select("#chart")
                .append("svg")
                .attr("width", w)
                .attr("height", h);

    var circles = svg.selectAll("circle")
                     .data(data_set)
                     .enter()
                     .append("circle");

    circles.attr({
        cx: function (d) {
            return Math.floor(d.aggregates[0].value / 100) + 25;
        },
        cy: function (d) {
            return d.aggregates[1].value;
        },
        r: 5
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
