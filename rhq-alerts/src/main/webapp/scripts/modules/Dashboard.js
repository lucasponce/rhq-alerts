/*
    Module to manage Dashboard component
 */
var Dashboard = (function() {
    
    var _initialized = false;
    var _chartPlaceholder;
    var _width;
    var _margin;
    var _startTime;
    var _endTime;
    var _color;
    
    /*
       Global data of the graph:
       - It is a list of arrays.
       - Each array has:
            a) first element: a text containing name of the series.
            b) other elements: date objects representing events.
       - This array is passed to d3 object as input for raw data.
     */
    var _data = [];
    
    /*
        Aux associative array to map a name of series with the index on _data[] array.
        If we have a series name, we can acces to its series data using:
        
        _data[_seriesIndexes['MySeries']]
                
     */
    var _seriesIndexes = [];
    
    /*
        Associative array to store "events".
        - First level is an associative array based on event.name.
        - Second level is an associative array based on event.date.
        
        _storage[event.name][event.date] = event
          
     */
    var _storage = [];
    
    /*
        Global graph object.  
     */
    var _graph;
    var _element;
    
    /*
        Initialize visual parameters of graph
     */
    var init = function(dashboardId, width, margin, startTime, endTime, hoverCallback) {
        _initialized = true;
        _chartPlaceholder = document.getElementById(dashboardId);
        _width = width;
        _margin = margin;
        _startTime = startTime;
        _endTime = endTime;

        _color = d3.scale.category10();
        _graph = d3.chart.eventDrops()
            .start(new Date(_startTime))
            .end(new Date(_endTime))
            .eventColor(function (datum, index) {
                return _color(index);
            })
            .width(_width)
            .margin(_margin)
            .axisFormat(function(xAxis) {
                xAxis.ticks(5);
            })
            .eventHover(function(el) {
                var series = el.parentNode.firstChild.innerHTML;
                var timestamp = d3.select(el).data()[0]

                if (typeof hoverCallback === 'function') {
                    hoverCallback(series, timestamp);
                }
            });

        _element = d3.select(_chartPlaceholder).append('div').datum(_data);
        _data = _element.datum();

        _graph(_element);
    };
    
    /*
        "event" parameter is an object with these mandatory attributes:
         - name: text used for series
         - date: date object used to add this object into the timeline
         - the object will be store internally in a map with key (name, date)
     */
    var addEvent = function (event) {
        if (_initialized) {
            if (event.name == undefined || event.date == undefined) {
                console.log("addEvent(): event object has not name or date properties");
                return;
            }

            if (_storage[event.name] == undefined) {
                // Update _storage
                var newSeries = [];
                newSeries[event.date] = event;
                _storage[event.name] = newSeries;

                // Update _data
                _seriesIndexes.push(event.name);
                var newLine = {};
                newLine.name = event.name;
                newLine.dates = [ event.date ];
                _data.push(newLine);
                
                // Redraw
                _graph(_element);                
            } else {
                if (_storage[event.name][event.date] == undefined) {
                    // Update _storage
                    _storage[event.name][event.date] = event;
                    
                    // Update _data
                    var i = _seriesIndexes.indexOf(event.name);
                    _data[i].dates.push(event.date);
                    
                    // Redraw
                    _graph(_element);
                } else {
                    console.log("addEvent(): event is already in the Dashboard");
                }
            }
        }
    };
    
    /*
        Return the object with key (name, date)
     */
    var getEvent = function(name, date) {
        if (_initialized) {
            if (_storage[name] != undefined) {
                if (_storage[name][date] != undefined) {
                    return _storage[name][date]
                } else {
                    console.log("getEvent(): name " + name + " found but date: " + date + " not found");
                }
            } else {
                console.log("getEvent(): name " + name + " not found ");
            }
        }
    };
    
    /*
        Return public methods
     */
    return {
        init: init,
        addEvent: addEvent,
        getEvent: getEvent
    };
    
})();
