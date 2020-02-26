$(document).ready(function () {
    $("#backButton").hide();
    $("#languageList").hide();
     $("#tabs").hide();
	$("#updateButton").hide();
    $("#saveButton").hide();
    $("#language").hide();

	var languageList = $('#languageList').parent();
	 languageList.find('.coral-Form-fieldlabel').hide();
     var parentschemeName = $('#schemeName').parent();
	 parentschemeName.find('.coral-Form-fieldlabel').hide();
     var parentaliasTextbox = $('#aliasTextbox').parent();
	 parentaliasTextbox.find('.coral-Form-fieldlabel').hide();
     var parentgroupsTextBox = $('#groupsTextBox').parent();
	 parentgroupsTextBox.find('.coral-Form-fieldlabel').hide();
      var language = $('#language').parent();
	 language.find('.coral-Form-fieldlabel').hide();
    var analysisList = $('#analysisList').parent();
    console.log("$#analysisList.value");

    if($("#analysisList").val()=="NoValue"){
console.log($("#analysisList").prop('disabled', true));
    }




$("#btnEditScheme").click(function (event) {
    $("#addNewScheme").hide();
    $("#btnEditScheme").hide();
    $("#aliasTextbox").val("");
	$("#groupsTextBox").val("");
    $("#deleteButtonScheme").hide();

 console.log("---------------btnEditSubmit");
     //Get form
            var form = $("#analysisList").val();
     var data ={'comp': $("#analysisList").val()};


 $("body").append('<div class="loaderOverlay"><span class="loaderBox"></span></div>');	
  $(".loaderOverlay").show();
    $.ajax({
                type: "POST",

                url: "/bin/aws/awsanalysisschemeservlet",
        data: data,
                cache: false,
                success: function (data) {
				$('.loaderOverlay').remove();	
                 console.log("******DATAA");
				console.log(data); 
                   console.log("-----*******---------");
				var obj = JSON.parse(data);
                if(obj.Synonyms == "{}"){
					$("#aliasTextbox").val("");	
                    $("#groupsTextBox").val(""); 
                }else{
                    var synVals = obj.Synonyms.split(',"groups":');	
                    var synGroups = synVals[1];	
                    synGroups = synGroups.split("}");	
                    synGroups = synGroups[0]	
                    var synAlias = synVals[0].split('aliases":');	
                    synAlias = synAlias[1];

                    $("#aliasTextbox").val(JSON.stringify(JSON.parse(synAlias), undefined, 4));	
                    $("#groupsTextBox").val(JSON.stringify(JSON.parse(synGroups), undefined, 4)); 
                }

                 $("#tabs").show();
				var arr = [];
				$.each(obj[0],function(k,v){ arr.push(JSON.stringify(v)) })
				$(".result").hide();
                    $("#language").show();
					 parentaliasTextbox.find('.coral-Form-fieldlabel').show();
                    $("#stopwordsTextBox").val(JSON.stringify(JSON.parse(obj.Stopwords), undefined, 4));
					$("#language").val(obj.Language);
					  $("#language").prop('disabled', true)                    
					$("#language").text(obj.Language);
                     $("#btnNextSubmit").hide();
                language.find('.coral-Form-fieldlabel').show();
                $("#aliasTextbox").show();
                    $("#analysisList").hide();
                    parentgroupsTextBox.find('.coral-Form-fieldlabel').show();
                    $("#groupsTextBox").show();
                    //var obj = JSON.parse(data);
                    $("#updateButton").show();
                     $("#schemeName").show();
					 parentschemeName.find('.coral-Form-fieldlabel').show();
                    $("#schemeName").val($("#analysisList").val());
					  $("#schemeName").prop('disabled', true)
                    $("#backButton").show();
                     analysisList.find('.coral-Form-fieldlabel').hide();

                }

            });


});


$("#deleteButtonScheme").click(function (event) {
  

 console.log("---------------deleteButtonScheme");
     //Get form
            var form = $("#analysisList").val();
     var data ={'comp': $("#analysisList").val()};


 $("body").append('<div class="loaderOverlay"><span class="loaderBox"></span></div>');	
  $(".loaderOverlay").show();
    $.ajax({
                type: "POST",

                url: "/bin/aws/awsdeleteanalysisschemeservlet",
				data: data,
                cache: false,
                success: function (data) {
				$('.loaderOverlay').remove();	
				var response = JSON.parse(data)	
                console.log(response); //return	
                console.log(response)
                 console.log("******DATAA");
				console.log(response); 
                  if(response.status == "failure"){	
					$(".result").html('<p class="fail"><span></span> Failure, please try again!</p>');	
                	$(".result").show();	
                }else if(response.status == "success"){	
					 window.location.reload();	
                }
                    else{
						$(".result").html('<p class="fail"><span></span>'+response.status+'</p>');	
                	$(".result").show();
                }
			
                }

            });


});

function alphaOnly(string) {
var regex = /^[a-z0-9-_]+$/;
if(!regex.test(string)) {
return false;
}else{
return true;
}
}
$("#saveButton").click(function (event) {
    var submitFlag=false;
    var schemeName = $("#schemeName").val();

    if(schemeName=="" || schemeName==undefined || schemeName==null){ 
         $(".result").html('<p class="fail"><span></span>Analysis Scheme Name is required.</p>');	
                	$(".result").show();	
            submitFlag=false;
            $('#schemeName').focus();
            return false;
    }else if(alphaOnly(schemeName)==false){
		         $(".result").html('<p class="fail"><span></span>Analysis Scheme Name is required.</p>');	
                	$(".result").show();
		submitFlag=false;
		$('#schemeName').focus();
return false;
    }  else{
            submitFlag=true;
            $('#message').next().next(".errMsg").html("").fadeOut();
        }
  var aliasVal;
        var groupsVal;
        var stopVal;
if($("#aliasTextbox").val() == ""){
			aliasVal = "";
        }else{
			aliasVal = JSON.stringify(JSON.parse($("#aliasTextbox").val()));
        }

        if($("#groupsTextBox").val() == ""){
			groupsVal = "";
        }else{
			groupsVal = JSON.stringify(JSON.parse($("#groupsTextBox").val()));
        }

        if($("#stopwordsTextBox").val() == ""){
			stopVal = "";
        }else{
			stopVal = JSON.stringify(JSON.parse($("#stopwordsTextBox").val()));
        }
    var data ={'Alias': aliasVal,'Group':groupsVal,'schemeName': $("#schemeName").val(),'stopwords':stopVal ,'language': $("#languageList").val()};
$(".comp").checked=false;

     $("body").append('<div class="loaderOverlay"><span class="loaderBox"></span></div>');	
  $(".loaderOverlay").show();	
    if(submitFlag == true){

      $.ajax({
        type: "POST",
        url: "/bin/aws/awsaveanalysisschemeservlet",
        data: data,
                cache: false,
                success: function (data) {
				$('.loaderOverlay').remove();	
                var response = JSON.parse(data)	
                console.log(response); //return	
                console.log(response); //return	
                console.log("-----*******---------");
                    if(response.status == "failure"){	
					$(".result").html('<p class="fail"><span></span> Failure, please try again!</p>');	
                	$(".result").show();	
                }else if(response.status == "success"){	
					 window.location.reload();	
                }
                    else{
						$(".result").html('<p class="fail"><span></span>'+response.status+'</p>');	
                	$(".result").show();
                }

                }

            });
}
});


    $("#updateButton").click(function (event) {


		var aliasVal;
        var groupsVal;
        var stopVal;
        if($("#aliasTextbox").val() == ""){
			aliasVal = "";
        }else{
			aliasVal = JSON.stringify(JSON.parse($("#aliasTextbox").val()));
        }

        if($("#groupsTextBox").val() == ""){
			groupsVal = "";
        }else{
			groupsVal = JSON.stringify(JSON.parse($("#groupsTextBox").val()));
        }

        if($("#stopwordsTextBox").val() == ""){
			stopVal = "";
        }else{
			stopVal = JSON.stringify(JSON.parse($("#stopwordsTextBox").val()));
        }

        console.log($("#language").val());
    var data ={'Alias': aliasVal,'Group': groupsVal,'comp': $("#schemeName").val(),'stopwords': stopVal,'languageupdate': $("#language").val()};
 console.log("---------------updateButton");

console.log(data);

     $("body").append('<div class="loaderOverlay"><span class="loaderBox"></span></div>');	
  $(".loaderOverlay").show();	

      $.ajax({
        type: "POST",
        url: "/bin/awsupdateanalysisschemeservlet",
        data: data,
                cache: false,
                success: function (data) {
				$('.loaderOverlay').remove();
                    console.log("******DATAA");
				console.log(data); 
                var response = JSON.parse(data)	
                console.log(response); //return	

                console.log("-----*******---------");	
                if(response.status == "failure"){	
					$(".result").html('<p class="fail"><span></span> Failure, please try again!</p>');	
                	$(".result").show();	
                }else if(response.status == "Success"){	
					 window.location.reload();	
                }
                    else{
						$(".result").html('<p class="success"><span></span>'+response.status+'</p>');	
                	$(".result").show();
                }
                }

            });
});



$("#addNewScheme").click(function (event) {
    $("#language").hide();
    	$("#updateButton").hide();
      $("#aliasTextbox").val("");
	$("#groupsTextBox").val('');
    $("#stopwordsTextBox").val('');
    $("#analysisSchemeHeading").val("");
    $("#languageList").show();
     $("#analysisList").hide();
     $("#tabs").show();
	 parentschemeName.find('.coral-Form-fieldlabel').show();
     $("#schemeName").show();
     parentaliasTextbox.find('.coral-Form-fieldlabel').show();
     $("#aliasTextbox").show();
     parentgroupsTextBox.find('.coral-Form-fieldlabel').show();
    languageList.find('.coral-Form-fieldlabel').show();
     $("#groupsTextBox").show();
     $("#addNewScheme").hide();
     $(".result").hide();
     $("#btnEditScheme").hide();
     $("#saveButton").show();
    $("#backButton").show();
     analysisList.find('.coral-Form-fieldlabel').hide();
    $("#deleteButtonScheme").hide();


});


$("#backButton").click(function (event) {

     window.location.reload();

});

});