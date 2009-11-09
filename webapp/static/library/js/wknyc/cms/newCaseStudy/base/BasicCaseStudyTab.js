/**
 * @author joseph.schmitt
 */
wknyc.cms.newCaseStudy.BasicCaseStudyTab = new Class({
	//Class vars
	canvas: $empty,
	form: $empty,
	validator: null,
	
	/**
	 * Class constructor
	 */
	initialize: function(canvas) {
		this.canvas = canvas;
		this.setupAssets();
		this.setupValidation();
	},
	
	setupAssets: function() {
		this.form = this.canvas.getElement('form');
	},
	
	setupValidation: function() {
		this.validator = new Form.Validator(this.form);
		
		this.validator.setOptions({
			onFormValidate: function(passed, form, event) {
				
			},
			
			onElementFail: function(element, validatorNames) {
				console.log("PARENT");
				var errorText;
				validatorNames.each(function(validatorName, index){
					errorText = element.get('validator').getValidator(validatorName).getError(element)
				});
				
				element.set('title', errorText);
				
				wknyc.cms.newCaseStudy.HelpPopup.Unwrap(element);
				var popup = new wknyc.cms.newCaseStudy.HelpPopup(element);
				popup.show();
			},
			
			onElementPass: function(element) {
				
			}
		});
		
	},
	
	submit: function() {
		this.form.getElement('input[type=submit]').click();
	},
	
	destroy: function() {
		this.canvas = null;
		this.form = null;
		this.validator.setOptions({
			onFormValidate: null,
			onElementFail: null,
			onElementPass: null
		});
		this.validator = null;
	}
});