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

function prepData(jsonData) {
    var jsonArray = [];
    for (var key in jsonData.factMap) {
        if (jsonData.factMap.hasOwnProperty(key)) {
            jsonArray.push(jsonData.factMap[key]);
        }
    }

    return jsonArray;
}
