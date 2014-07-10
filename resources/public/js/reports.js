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
    var barPadding = 3;

    var data_set = prepData(jsonData);

    var svg = d3.select("#chart")
                .append("svg")
                .attr("width", w)
                .attr("height", h);

    var rects = svg.selectAll("rect")
                   .data(data_set)
                   .enter()
                   .append("rect");

    rects.attr({
        x: function(d,i) {
            return i * (w / data_set.length);
        },
        y: function(d) {
            return h - Math.ceil(d.aggregates[0].value / 1000);
        },
        width: (w / data_set.length) - barPadding,
        height: function(d) {
                    return Math.ceil(d.aggregates[0].value / 1000) * 4;
        },
        fill: function(d) {
                return "rgb(0,0," + (Math.ceil(d.aggregates[0].value / 1000) * 5) + ")";
        }
    });

    var texts = svg.selectAll("text")
                  .data(data_set)
                  .enter()
                  .append("text");

    texts.text(function(d) {
            return Math.floor(d.aggregates[0].value / 1000);
          })
          .attr({
            x: function(d,i) {
                return i * (w / data_set.length) + (w / data_set.length - barPadding) / 2;
            },
            y: function(d) {
                return h - Math.ceil(d.aggregates[0].value / 1000) - 2 + 14;
            },
            "font-family": "sans-serif",
            "font-size": "11px",
            fill: "white",
            "text-anchor": "middle"
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
