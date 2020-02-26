/* jshint undef: true, unused: true */
/* globals $, Coral, document, _, window*/
(function(){

    /**
     * Add column item to column view element
     * @param {Coral.Select}
     * @param {HTML} title item title HTML
     * @param {string} id item id
     * @returns the newly added item
     */
    function addColumnItem(columnViewEl, title, id){
    console.log("\n\n\nInside columnViewEl....\n",columnViewEl);
	 console.log("\n\n\nInside title....\n",title);
	  console.log("\n\n\nInside id....\n",id);
        // deactivate all items, new item should be active
		
        /* _.forEach(columnViewEl.items, function(colItem) {
          
        }); */

        // create new item and add it to list
        var newItem =({
                   
            content: {innerHTML:title},
           
        });
        /* newItem.set({
                   
            content: {innerHTML:title},
           
        }); */
        console.log("       newItem : ",newItem);
        columnViewEl.items.add(newItem);
        console.log("Exiting addColumnItem....");
        return newItem;
    }

    /**
     * Get the item that has innerHtml
     * @param {*} columnViewEl 
     * @param {*} innerHtml 
     */
    function findItem(columnViewEl, innerHtml){
         return _.find(columnViewEl.items.getAll(), function(item) { return item.content.innerHTML ===  innerHtml;});
    }
    
    /**
     * Finds the first item with innerHTML===name in column then triggers a click on it.
     * @param {Coral.ColumnView.Column} column the column that contains the item to click
     * @param {string} name the item's name (innerHTML)
     * @returns {HTMLElement} the item content element that was clicked
     */
    function clickColumnItemContent(column, name){
    console.log("\n\n\nInside clickColumnItemContent....\n");
        var current = name ? findItem(column, name) : name;
        var toClick;
        if(current) toClick = current.content;
        else {
            var first = column.items.first();
            if(first) toClick = first.content;
        }
       $(toClick).trigger('click');
        return toClick;
    }

    /**
     * Loads all datasources and adds them to the left sidebar column;
     * @param {Coral.ColumnView.Column} the column to load sources into
     * @param {Function} cb to be executed with current element name 
     */
    function loadDataSources($dsColumn, cb){
    console.log("\n\n\nInside loadDataSources....\n");
        var currentItem = $dsColumn.activeItem ? $dsColumn.activeItem : $dsColumn.selectedItem;
        console.log("       currentItem : ",currentItem);
        var currentItemName = currentItem ? $dsColumn.value : undefined;
        console.log("       currentItemName : ",currentItemName);
        var selectoHttpSrv =  window.awsCSConnector.registry.get('http');
        console.log("       selectoHttpSrv : ",selectoHttpSrv);
        var toastSrv = window.awsCSConnector.registry.get('toast');
        console.log("       toastSrv : ",toastSrv);
         console.log("       dsColumn : ",$dsColumn);
        // addColumnItem($dsColumn); // add wait element
        return selectoHttpSrv
        .get()
        .done(function(data){
          
			console.log("              dataNew: ",data);
            _.forEach(data, function(ds, name){
				 console.log("               name : ",name);
                var item  = addColumnItem($dsColumn, name);
				
                console.log("               item : ",item);
                $(item.content).on('click', function(){
                   
                    console.log("               ds.options : ",ds.options);
                    $.publish('selecto-view-ds', [name, ds.options]); // publish event with options
                });
                $(item.thumbnail).on('click', function(){
                    $.publish('selecto-delete-ds', [name, ds.options]); // publish event with options
                });
            });
            toastSrv.success('Sucess', 'All DataSources loaded');
            var clickedContent = clickColumnItemContent($dsColumn, currentItemName);
            console.log("       clickedContent : ",clickedContent);
            if(cb) cb(clickedContent ? clickedContent.innerHTML : undefined);
        })
        .fail(function(err){
            var parsedErr = parseJson(err);
            if(parsedErr) toastSrv.error(parsedErr.title, parsedErr.text);
            else toastSrv.error('Server error', err);
        });
        console.log("Exiting loadDataSources....");
    }

    /**
     * Adds a new item to the multifield
     * @param {*} multifield multifield to add item to
     * @param {*} itemInnerHtml  the html to be added to item
     */
    function addMultifieldItem(multifield, itemInnerHtml){
    console.log("\n\n\nInside addMultifieldItem....\n");
        var newItem = new Coral.Multifield.Item().set({
            content: {innerHTML: itemInnerHtml}
        });
        multifield.items.add(newItem);
        console.log("       newItem : ",newItem);
        console.log("Exiting addMultifieldItem....");
    }

    /**
     * Checks if passed string is json string
     * @param {string} str 
     */
    function parseJson(str){
    console.log("\n\n\nInside parseJson....\n");
        var parsed;
        try {
            parsed = JSON.parse(str);
            console.log("       parsed : ",parsed);
        }
        catch(e){ return false;}
        console.log("Exiting parseJson....");
        return parsed;
    }

    /**
     * Validates an input element
     * @param {HTMLElement} el 
     */
    function validate(el){
        var validation = $(el).adaptTo('foundation-validation');
        var valid = validation.checkValidity();
        validation.updateUI();
    console.log("\n\n\nInside validate....\n");
         return valid;
    }

    /**
     * Gets the outer html of a jquery element
     */
    $.fn.outerHtml = function(){
        console.log("\n\n\nInside fn.outerHtml....\n");
       // console.log("          ",this.clone().wrap('<div>').parent().html());
        console.log("Exiting fn.outerHtml....");
        return this.clone().wrap('<div>').parent().html();
    };

    /**
     * Get's the innerHtml of element then createa a new element from that
     * sort of like a jQuery.clone but different impl
     */
    $.fn.cloneInnerHtml = function(){
        console.log("\n\n\nInside fn.cloneInnerHtml....\n");
        //console.log("          ",this.html());
        console.log("Exiting fn.cloneInnerHtml....");
        return $(this.html());
    };

    /**
     * Creates and adds a Coral.Multifield to the page
     * @param {*} templateSelector the selector for multifield template
     * @param {*} appendToSelector the selector under which to append the multifield 
     * @param {*} addBtnTxt the 'add' button text
     */
    function createMultifield(templateSelector, appendToSelector, addBtnTxt){
    console.log("\n\n\nInside createMultifield....\n");
        /**
         * add the multifield to the selecto form
         */
        var multifield = new Coral.Multifield();
        // have to do it this way to support ie11
        multifield.template.content.appendChild($(document.querySelector(templateSelector).innerHTML).get(0));
        var add = new Coral.Button();
        add.label.textContent = addBtnTxt;
        add.setAttribute('coral-multifield-add', '');
        add.setAttribute('type', 'button'); // prevent form submit
        multifield.appendChild(add);
        document.querySelector(appendToSelector).appendChild(multifield);
        console.log("       multifield : ",multifield);
        console.log("Exiting createMultifield....");
        return multifield;
    }

    $(function(){
        // ========= SERVICES
        console.log("\n\n\n========= SERVICES....\n");
        var selectoHttpSrv =  window.awsCSConnector.registry.get('http');
		var analysisSchemeDetail =  window.awsCSConnector.registry.get('analysisSchemeDetail');
        console.log("       selectoHttpSrv : ",selectoHttpSrv);
        var toastSrv = window.awsCSConnector.registry.get('toast');
        console.log("       toastSrv : ",toastSrv);
        var progressSrv = window.awsCSConnector.registry.get('progress');
        console.log("       progressSrv : ",progressSrv);

        // ========= REUSABLE SELECTORS
        console.log("\n\n\n========= REUSABLE SELECTORS....\n");
        var selector = {
            dialodAddBtn: '#addButton',
            multifieldTemplate: '#selecto-option-template',
            optionText: '.selecto-txt',
            optionVal: '.selecto-val'
        };

        // ========= PRE_SELECT ELEMENTS
        console.log("\n\n\n========= PRE_SELECT ELEMENTS....\n");
        var $dsAddBtn = document.querySelector('#ds-add-new-btn');
        var $dsColumn = document.querySelector('#ds-column');
        var $dsAddDialog = document.querySelector('#ds-dialog');
        var $dsId = $dsAddDialog.querySelector('#ds-id');
        var $dsDialogAddBtn = $dsAddDialog.querySelector(selector.dialodAddBtn);
        var $optionsForm = document.querySelector('#selecto-option-form');
        var $saveOptionsBtn = document.querySelector('#save-options');
        var $title = document.querySelector('#selecto-selected-title');
        var $name = document.querySelector('#selcto-datasource-name');

        console.log("       $dsAddBtn : ",$dsAddBtn);
        console.log("       $dsColumn : ",$dsColumn);
        console.log("       $dsAddDialog : ",$dsAddDialog);
        console.log("       $dsId : ",$dsId);
        console.log("       $dsDialogAddBtn : ",$dsDialogAddBtn);
        console.log("       $optionsForm : ",$optionsForm);
        console.log("       $saveOptionsBtn : ",$saveOptionsBtn);
        console.log("       $title : ",$title);
        console.log("       $name : ",$name);

        // ========= HELPERS

        console.log("\n\n\n========= HELPERS....\n");
        
        // validates both id and title inputs
        function validateBoth(){
             var valid = validate($dsId);
            $($dsDialogAddBtn).attr('disabled', valid ? null : 'disabled');
            console.log(" validateBoth....");
            return valid;          
        }

        // adds a new datasource option to multifield
        function addOptionToMultifield(multifield, text, value){
        console.log("\n\n\nInside addOptionToMultifield....\n");
            var $template = $(selector.multifieldTemplate).cloneInnerHtml();
            $template.find(selector.optionText).attr('value', text);
            $template.find(selector.optionVal).attr('checked', value);
            addMultifieldItem(multifield, $template.outerHtml());
            //console.log("       multifield : ",multifield);
            //console.log("       $template.outerHtml() : ",$template.outerHtml());
            console.log("Exiting addOptionToMultifield....");
        }


        // gets the options array from multifield
        function getOptions(multifield){
         console.log("\n\n\nInside getOptions....\n");
         console.log("       multifield.items.getAll() : ",multifield.items.getAll());
         console.log("Exiting getOptions....");
            return _.map(multifield.items.getAll(), function(item){
            console.log("               item.querySelector(selector.optionText).value : ",item.querySelector(selector.optionText).value);
            console.log("               item.querySelector(selector.optionVal).value : ",item.querySelector(selector.optionVal).value);
              console.log(item.querySelector(selector.optionVal).value);

                return {
                    'text': item.querySelector(selector.optionText).value,
                    'value': item.querySelector(selector.optionVal).checked
                };
            });
        }


        // loads all datasources then toggels form visibility
        function loadDataSourcesToggleForm(){
        console.log("\n\n\nInside loadDataSourcesToggleForm....\n");
            return loadDataSources($dsColumn, function(activeItemName){
            console.log("       activeItemName : ",activeItemName);
                // hide form if no current active element
                if(activeItemName) $optionsForm.style.display = '';
                else $optionsForm.style.display = 'none';
            });
        }


         // ========= MAIN
         console.log("\n\n\n========= MAIN....\n");
        // show/hide progress indicator when requests are inprogress/complete
        selectoHttpSrv.progress(function(){progressSrv.show();});
        selectoHttpSrv.complete(function(){progressSrv.hide();});


        // load all datasources, first time
        loadDataSourcesToggleForm();



        // Get the datasources path and set it on the page
        selectoHttpSrv.get('data-source-selector')
        .done(function(data){
            $(function(){$('#selcto-datasource-path').html(data.path);});
        })
        .fail(function(){
            toastSrv.error('Server Error', 'Could not get DataSources path');
        });



        // add events to validate inputs
        $dsId.on('input', validateBoth);



        // handle add new data source
        $dsAddBtn.on('click', function(){
            $dsAddDialog.show();
        });




        // handle adding a new datasource from dialog.
        $dsAddDialog.on('click', selector.dialodAddBtn, function() {
        console.log("\n\n\nInside $dsAddDialog on click ....\n");
            var valid = validateBoth();
            if(!valid) return;
            // TODO
            selectoHttpSrv.create($dsId.value, []) // creates new empty datasource
            .done(function(){
                loadDataSourcesToggleForm()
                .done(function(){
                    var newlyAdded = findItem($dsColumn, $dsId.value);
                    console.log("       newlyAdded : ",newlyAdded);
                    $(newlyAdded.content).trigger('click');
                    toastSrv.success('Success', 'Added DataSource '+$dsId.value);
                    $dsAddDialog.hide();
                    $dsId.value='';
                });
            })
            .fail(function(){
                toastSrv.error('Server Error', 'Could not add datasource');
            });
            console.log("Exiting $dsAddDialog on click ....");
        });
        
        // create the multifield and add it to form
        var multifield = createMultifield(selector.multifieldTemplate, '#selecto-option-fieldset', 'Add Option');
        
        $.subscribe("selecto-view-ds", function showOptions(e, dsName, dsOptions){
        console.log("\n\n\nInside $.subscribe selecto-view-ds ....\n");
            $title.innerHTML = dsName;
            $name.innerHTML = dsName;
            multifield.items.clear(); // remove current items
            _.forEach(dsOptions, function(opt){
            console.log("       opt.text : ",opt.text);
            console.log("       opt.value : ",opt.value);
                addOptionToMultifield(multifield, opt.text, opt.value);
            });
            console.log("Exiting $.subscribe selecto-view-ds ....");
        });
        $.subscribe("selecto-delete-ds", function(e, id){
         console.log("\n\n\nInside $.subscribe selecto-delete-ds ....\n");
            // delete wrapped in ['*'] because of an issue with YUI compressor (https://github.com/yui/yuicompressor/issues/189)
            selectoHttpSrv['delete'](id)
            .always(loadDataSourcesToggleForm)
            .done(function(){
                toastSrv.success('Success', 'Deleted DataSource '+$dsId.value);
            }).fail(function(data){
                toastSrv.error('Error', data && data.text ? data.text : 'Could not delete DataSource');
            });
            console.log("Exiting $.subscribe selecto-delete-ds ....");
        });

        $saveOptionsBtn.on('click', function(){
        console.log("\n\n\nInside $saveOptionsBtn on click ....\n",$dsColumn.value);
		console.log("\n\n\nInside $saveOptionsBtn on click ....\n",multifield);
            selectoHttpSrv.update($dsColumn.value)
            .always(loadDataSourcesToggleForm)
            .done(function(){
                toastSrv.success('Success', 'Updated DataSource '+$dsId.value);
            })
            .fail(function(data){
                toastSrv.error('Error', data && data.text ? data.text : 'Could not update DataSource');
            });
            console.log("Exiting $saveOptionsBtn on click ....");
        });
        

    });
})();