function formatReport(jsonData) {
    var w = 750;
    var h = 200;
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
            return h - Math.ceil(d.aggregates[0].value / 1000) * 3 - 25;
        },
        width: (w / data_set.length) - barPadding,
        height: function(d) {
                    return Math.ceil(d.aggregates[0].value / 1000) * 3 + 25;
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
                return h - (Math.ceil(d.aggregates[0].value / 1000) * 3) + 14 - 25;
            },
            "font-family": "sans-serif",
            "font-size": "11px",
            fill: "white",
            "text-anchor": "middle"
          });

}