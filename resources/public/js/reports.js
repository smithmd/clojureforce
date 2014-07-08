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

    var data = temporaryData;

    (function() {
        var prepData = function(d) {
            var arr = [];
            for(var key in d.factMap) {
                if (d.factMap.hasOwnProperty(key)) {
                    arr.push(key);
                }
            }
            return arr;
        };

      var format = d3.time.format("%a %b %d %Y");
      var amountFn = function(d) { return d.amount };
      var dateFn = function(d) { return format.parse(d.created_at) };

      var valFn = function(d) { return d.aggregates[0].value };

      var x = d3.time.scale()
        .range([10, 280])
        .domain(d3.extent(data, dateFn));

      var y = d3.scale.linear()
        .range([180, 10])
        .domain(d3.extent(data, valFn));

      var svg = d3.select("#chart").append("svg:svg")
      .attr("width", 300)
      .attr("height", 200);


      svg.select("body").selectAll("p").data(prepData(data)).enter()
        .append("p")
        .text(function(d) {return d.aggregates[0].value;});

    })();
}

var temporaryData = {
                       "attributes":{
                          "describeUrl":"/services/data/v30.0/analytics/reports/00O50000003QiypEAC/describe",
                          "instancesUrl":"/services/data/v30.0/analytics/reports/00O50000003QiypEAC/instances",
                          "reportId":"00O50000003QiypEAC",
                          "reportName":"Total Revenue",
                          "type":"Report"
                       },
                       "allData":true,
                       "factMap":{
                          "T!T":{
                             "aggregates":[
                                {
                                   "label":"$43,600.00",
                                   "value":43600
                                },
                                {
                                   "label":"278",
                                   "value":278
                                }
                             ]
                          },
                          "0!T":{
                             "aggregates":[
                                {
                                   "label":"$15,400.00",
                                   "value":15400
                                },
                                {
                                   "label":"59",
                                   "value":59
                                }
                             ]
                          },
                          "1!T":{
                             "aggregates":[
                                {
                                   "label":"$28,200.00",
                                   "value":28200
                                },
                                {
                                   "label":"219",
                                   "value":219
                                }
                             ]
                          }
                       },
                       "groupingsAcross":{
                          "groupings":[

                          ]
                       },
                       "groupingsDown":{
                          "groupings":[
                             {
                                "groupings":[

                                ],
                                "key":"0",
                                "label":"VIP",
                                "value":"VIP"
                             },
                             {
                                "groupings":[

                                ],
                                "key":"1",
                                "label":"General Admission",
                                "value":"General Admission"
                             }
                          ]
                       },
                       "hasDetailRows":false,
                       "reportExtendedMetadata":{
                          "aggregateColumnInfo":{
                             "RowCount":{
                                "acrossGroupingContext":null,
                                "dataType":"int",
                                "downGroupingContext":null,
                                "label":"Record Count"
                             },
                             "s!Attendee__c.Total_Spent__c":{
                                "acrossGroupingContext":null,
                                "dataType":"currency",
                                "downGroupingContext":null,
                                "label":"Sum of Total Spent"
                             }
                          },
                          "detailColumnInfo":{
                             "CUST_NAME":{
                                "dataType":"string",
                                "label":"Festival: Festival Name"
                             },
                             "Festival__c.Tickets_Sold__c":{
                                "dataType":"double",
                                "label":"Tickets Sold"
                             },
                             "Festival__c.VIP_Tickets_Sold__c":{
                                "dataType":"double",
                                "label":"VIP Tickets Sold"
                             },
                             "CHILD_NAME":{
                                "dataType":"string",
                                "label":"Attendee: Attendee Name"
                             },
                             "Attendee__c.Purchase_Source__c":{
                                "dataType":"picklist",
                                "label":"Purchase Source"
                             },
                             "Attendee__c.Total_Spent__c":{
                                "dataType":"currency",
                                "label":"Total Spent"
                             },
                             "Attendee__c.Tickets_Purchased__c":{
                                "dataType":"double",
                                "label":"Tickets Purchased"
                             }
                          },
                          "groupingColumnInfo":{
                             "Attendee__c.Ticket_Type__c":{
                                "dataType":"picklist",
                                "groupingLevel":0,
                                "label":"Ticket Type"
                             }
                          }
                       },
                       "reportMetadata":{
                          "aggregates":[
                             "s!Attendee__c.Total_Spent__c",
                             "RowCount"
                          ],
                          "currency":null,
                          "detailColumns":[
                             "CUST_NAME",
                             "Festival__c.Tickets_Sold__c",
                             "Festival__c.VIP_Tickets_Sold__c",
                             "CHILD_NAME",
                             "Attendee__c.Purchase_Source__c",
                             "Attendee__c.Total_Spent__c",
                             "Attendee__c.Tickets_Purchased__c"
                          ],
                          "developerName":"Total_Revenue",
                          "groupingsAcross":[

                          ],
                          "groupingsDown":[
                             {
                                "dateGranularity":"None",
                                "name":"Attendee__c.Ticket_Type__c",
                                "sortAggregate":null,
                                "sortOrder":"Asc"
                             }
                          ],
                          "historicalSnapshotDates":[

                          ],
                          "id":"00O50000003QiypEAC",
                          "name":"Total Revenue",
                          "reportBooleanFilter":null,
                          "reportFilters":[

                          ],
                          "reportFormat":"SUMMARY",
                          "reportType":{
                             "label":"Custom Object with Custom Object",
                             "type":"CustomEntityCustomEntity$Festival__c$Attendee__c"
                          }
                       }
                    };