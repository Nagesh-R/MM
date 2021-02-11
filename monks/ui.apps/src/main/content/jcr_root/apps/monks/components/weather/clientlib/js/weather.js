(function(){
       function weather() {
           const self = this;
           let apiPath;

           this.getApiPath  = function() {
               return $('#media-monks-weather-component').data().path.concat('.api.json');
           }

           this.fetchWeatherData = function(apiPath) {
               fetch(apiPath,
               {
                 method: 'get',
                 headers: {
                   'Accept': 'application/json',
                   'Content-Type': 'application/json',
                   'pragma': 'no-cache',
                   'cache-control': 'no-store'
                 },
               }).then(function (response) {
                 self.paintData(response.json());
               }).catch(function (error) {
                 console.error(error);
               });
           }

           this.paintData = function(httpResponse) {
               httpResponse.then(function(data){
                   if(data && data.main && data.visibility) {
                       $('.monk-weather-temprature').text(Math.round(data.main.temp));
                       $('.monk-weather-humidity').text(data.main.humidity);
                       $('.monk-weather-visibility').text((parseFloat(data.visibility)/1000).toFixed(2));
                   }
               })
           }

       }
       window.hasOwnProperty('Monks') ?  Monks.weather = new weather() : window.Monks = {},Monks.weather = new weather();
}());

   document.addEventListener('DOMContentLoaded', (event) => {
     (function(){
       let apiURL;
       if(document.querySelector('#media-monks-weather-component')) {
           apiURL = Monks.weather.getApiPath();
           if(apiURL){
               Monks.weather.fetchWeatherData(apiURL);
           }
       }
     })();
   });