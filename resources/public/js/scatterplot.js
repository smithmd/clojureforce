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