/* jshint undef: true, unused: true */
/* globals $, window */
(function(){

    /**
     * Validator for required fields
     */
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-selecto-required]",
        validate: function (el) {
            if(!el.value) return "This field is required";
        }
    });

    /**
     * validator for input field to require alphanumeric and dashes only 
     */
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "[data-validation=scheme-name]",
        validate: function (el) {
            var value = el.value;
            var regexp = /^[a-z0-9_]+$/;
            if (value.search(regexp) == -1) return "Analysis Scheme names must start with a lower case letter. Specify a analysis scheme name using the following characters: a-z (lower case letters), 0-9, and _ (underscore).";
            else return; // valid
        }
    });

})();