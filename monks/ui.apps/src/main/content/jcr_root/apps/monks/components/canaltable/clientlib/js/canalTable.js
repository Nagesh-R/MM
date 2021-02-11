(function(){
    function CanalTable(){
        const self = this;

        this.fetchData = function() {
            let URL = $('.monks-canal-table').data().path.concat('.api.json');
            $.ajax({
              method: "GET",
              url: URL
            }).done(function( data ) {
                self.paintData(data);
            }).fail(function(){
                console.log("Error During Data Insertion ");
            });
        }

        this.paintData = function (data) {
            if(data.length){
                data.forEach(function(waterLevel){
                    $('.table-data tr:last').after('<tr class="row100 body"><td class="cell100 column1">'+waterLevel.date+'</td><td class="cell100 column2">'+waterLevel.height+'</td><td class="cell100 column3">'+waterLevel.createdDate+'</td></tr>');
                })
            }
        }
    }

    window.hasOwnProperty('Monks') ?  Monks.CanalTable = new CanalTable() : window.Monks = {},Monks.CanalTable = new CanalTable();
})();

$(document).ready(function(){
  if(document.querySelector('.monks-canal-table')) {
     Monks.CanalTable.fetchData();
  }
});
