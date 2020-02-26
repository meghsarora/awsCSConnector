$(document).ready(function () {
    $("#btnNextStopwords").hide();
    $("#aliasTextbox").hide();
    $("#groupsTextBox").hide();
      $("#saveSynonymButton").hide();

      $("#schemeName").hide();

$("#btnEditScheme").click(function (event) {


});

$("#btnNextStopwords").click(function (event) {});


$("#saveSynonymButton").click(function (event) {
$(".comp").checked=false;
 console.log("---------------btnEditSubmit");
     //Get form
            var form = $("#fileUploadForm")[0];

           // Create an FormData object

console.log("*************");
    console.log(form);
  
});



$("#addNewSynonym").click(function (event) {

     $("#schemeName").show();
     $("#aliasTextbox").show();
     $("#groupsTextBox").show();
    $("#addNewSynonym").hide();
    $(".result").hide();
                    $("#btnEditScheme").hide();
    $("#saveSynonymButton").show();

});



//    $("#btnSynonymSubmit").click(function (event) {
//
//
//            // Get form
//            var form = $('#fileUploadForm')[0];
//
//            // Create an FormData object
//        var data ={'alias': $("#alias").val()}
//console.log("*************");
//console.log(data['alias']);
//
//
//
//            $.ajax({
//                type: "POST",
//                enctype: 'multipart/form-data',
//                url: "/bin/aws/awsanalysisschemeservlet",
//                data: data,
//                cache: false,
//                success: function (data) {
//
//                    $(".result label").text(data);
//                    $(".result label").show();
//                    $(".loading").removeClass("loading--show").addClass("loading--hide");
//                    $("#btnSubmit").prop("disabled", false);
//
//                },
//                error: function (e) {
//
//                    $(".result label").text(e.responseText);
//                    $(".result label").show();
//                    $(".loading").removeClass("loading--show").addClass("loading--hide");
//                    $("#btnSubmit").prop("disabled", false);
//
//                }
//            });

  //  });

});