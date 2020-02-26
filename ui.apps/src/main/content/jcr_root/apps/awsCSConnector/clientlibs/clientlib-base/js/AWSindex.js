
$(document).ready(function () {

(function($, $document) {

    "use strict";

    $document.on("dialog-ready", function() {

        var pathfieldVar = $('foundation-autocomplete[name="./indexPagePath"]')

        if (typeof pathfieldVar !== 'undefined' || pathfieldVar !== null) {

            if (pathfieldVar.attr("disabled") == "disabled") {

                pathfieldVar.removeAttr("disabled");

                pathfieldVar.attr("disabled", "disabled");

            }

        }

    });

})($, $(document));

    $("#btnAddSubmit").click(function (event) {
 $(".result").html('');
          var submitFlag=false;
    var indexPagePath = $("#indexPagePath").val();

    if(indexPagePath=="" || indexPagePath==undefined || indexPagePath==null){ 
         $(".result").html('<p class="fail"><span></span>Please choose path.</p>');	
                	$(".result").show();	
            submitFlag=false;
            $('#schemeName').focus();
            return false;
    }  else{
            submitFlag=true;
            $('#message').next().next(".errMsg").html("").fadeOut();
        }
 $(".result label").html("");
            // Get form
            var form = $('#fileUploadForm')[0];
 
            // Create an FormData object
        var data ={'indexPagePath': $("#indexPagePath").val(),'operation':'add','includeChildren':$("#includeChildren").prop('checked')};
 $("body").append('<div class="loaderOverlay"><span class="loaderBox"></span></div>');	
  $(".loaderOverlay").show();
 if(submitFlag == true){
            $.ajax({
                type: "POST",
                 url: "/bin/aws/awssearchindexservlet",
                data:  data,
                cache: false,
                success: function (data) {
					$('.loaderOverlay').remove();

                    $(".result label").show();
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    $("#btnAddSubmit").prop("disabled", false);
                      if(data.status == "failure"){	
					$(".result").html('<p class="fail"><span></span> Failure, please try again!</p>');	
                	$(".result").show();	
                }else{	
					$(".result").html('<p class="success"><span></span> Indexed successfully!</p>');	
                	$(".result").show();	
                }
                },
                error: function (e) {

                    $(".result label").text(e.responseText);
                    $(".result label").show();
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    $("#btnAddSubmit").prop("disabled", false);

                }
            });
    }

    });



   $("#btnDeleteSubmit").click(function (event) {
 $(".result").html('');
        var submitFlag=false;
    var indexPagePath = $("#indexPagePath").val();

    if(indexPagePath=="" || indexPagePath==undefined || indexPagePath==null){ 
         $(".result").html('<p class="fail"><span></span>Please choose path.</p>');	
                	$(".result").show();	
            submitFlag=false;
            $('#schemeName').focus();
            return false;
    }  else{
            submitFlag=true;
            $('#message').next().next(".errMsg").html("").fadeOut();
        }
            // Get form
            var form = $('#fileUploadForm')[0];

            // Create an FormData object
        var data ={'indexPagePath': $("#indexPagePath").val(),'operation':'delete','includeChildren':$("#includeChildren").prop('checked')};
 $("body").append('<div class="loaderOverlay"><span class="loaderBox"></span></div>');	
  $(".loaderOverlay").show();
        if(submitFlag == true){
            $.ajax({
                type: "POST",
                enctype: 'multipart/form-data',
                url: "/bin/aws/awssearchindexservlet",
                data: data,
                cache: false,
                success: function (data) {
					$('.loaderOverlay').remove();

                    $(".result label").show();
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    $("#btnDeleteSubmit").prop("disabled", false);
                        if(data.status == "failure"){	
					$(".result").html('<p class="fail"><span></span> Failure, please try again!</p>');	
                	$(".result").show();	
                }else{	
					$(".result").html('<p class="success"><span></span> Deleted successfully!</p>');	
                	$(".result").show();	
                }

                },
                error: function (e) {

                    $(".result label").text(e.responseText);
                    $(".result label").show();
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    $("#btnDeleteSubmit").prop("disabled", false);

                }
            });
        }

    });

    });