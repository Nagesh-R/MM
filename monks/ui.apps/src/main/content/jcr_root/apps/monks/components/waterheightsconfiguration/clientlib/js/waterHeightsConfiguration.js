(function(){
    function CanalDashboard(){
        const self = this;
        const Constants = {
            FETCH: 'fetch',
            ADD: 'add'
        }

        this.addData = function() {
            let properties = {};
            properties.URL = self.getApiURL(Constants.ADD);
            self.fetchDataFromForm(properties);
            if(properties.date && properties.height) {
                let httpResponse = self.postData(properties);
            } else {
                console.log('Mandatory parameters missing');
            }
        }

        this.fetchDataFromForm = function(properties) {
            properties.date = $('#monks-dashboard-date').val();
            properties.height = $('#monks-dashboard-waterHeight').val();
            properties.id = new Date().getTime();
        }

        this.postData = function(properties) {
            $.ajax({
              method: "POST",
              url: properties.URL,
              data: properties
            }).done(function( data ) {
                self.paintIncrementalData(data);
            }).fail(function(){
                console.log("Error During Data Insertion ");
            });
        }

        this.fetchData = function() {
            let properties = {};
            properties.URL = self.getApiURL(Constants.FETCH);
            $.ajax({
              method: "GET",
              url: properties.URL
            }).done(function( data ) {
                self.paintData(data);
            }).fail(function(){
                console.log("Error During Data Insertion ");
            });
        }

        this.paintIncrementalData =  function(data) {
           let template = '<tr class="row100 body"><td class="cell100 column1">'+data.date+'</td><td class="cell100 column2">'+data.height+'</td><td class="cell100 column3">'+data.createdDate+'</td></tr>';
           $('.table-data tr:last').after(template);
        }

        this.paintData = function (data) {
            if(data.length){
                data.forEach(function(waterLevel){
                    $('.table-data tr:last').after('<tr class="row100 body"><td class="cell100 column1">'+waterLevel.date+'</td><td class="cell100 column2">'+waterLevel.height+'</td><td class="cell100 column3">'+waterLevel.createdDate+'</td></tr>');
                })
            }
        }

        this.getApiURL = function(action) {
            let apiURL;
            let path = $('#monks-dashboard').data().path;
            if(action){
                switch(action) {
                    case Constants.FETCH:
                        apiURL = path.concat('.fetch.json');
                        break;
                    case Constants.ADD:
                        apiURL = path.concat('.add.json');
                        break;
                    default:
                        apiURL = path.concat('.fetch.json');
                        break;
                }
            }
            return apiURL;
        }

        this.registerEvents = function() {
            $('.weather-dashboard-add').on('click', function(){
                event.stopImmediatePropagation();
                event.stopPropagation();
                self.addData();
            });
        }
    }

    window.hasOwnProperty('Monks') ?  Monks.CanalDashboard = new CanalDashboard() : window.Monks = {},Monks.CanalDashboard = new CanalDashboard();
})();

$(document).ready(function(){
  if(document.querySelector('#monks-dashboard')) {
     Monks.CanalDashboard.registerEvents();
     Monks.CanalDashboard.fetchData();
  }
});
