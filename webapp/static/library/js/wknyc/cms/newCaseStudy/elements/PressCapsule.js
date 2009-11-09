/**
 * @author joseph.schmitt
 */
wknyc.cms.newCaseStudy.PressCapsule = new Class({
	Implements: Options,
	
	options: {
		isRequired: false,
		cloneMarkup: false
	},
	
	//Class vars
	canvas: null,
	hasExpanded: false,
	
	//Validators
	isEmpty: null,
	isNumeralsOnly: null,
	isMinLength: null,
	
	/**
	 * Class constructor
	 */
	initialize: function(canvas, options) {
		this.setOptions(options);
		
		if( this.options.cloneMarkup ) this.canvas = canvas.clone();
		else this.canvas = canvas;
		
		this.setupAssets();
	},
	
	setupAssets: function() {
		//Input Fields
		var itemNameField = new wknyc.common.gui.InputField(this.canvas.getElement('.itemName input'), {prefillElement:null});
		
		var pressDateField;
		this.canvas.getElements('.pressDate input[type=text]:not(.dateValue)').each(function(input){
			pressDateField = new wknyc.common.gui.InputField(input, {prefillElement:'label.prefill'});
		});
		
		var pressUrlField = new wknyc.common.gui.InputField(this.canvas.getElement('.pressUrl input'), {prefillElement:'label.prefill'});
		var pressSourceField = new wknyc.common.gui.InputField(this.canvas.getElement('.pressSource input'), {prefillElement:null});
		var pressBylineField = new wknyc.common.gui.InputField(this.canvas.getElement('.pressByline input'), {prefillElement:null});
	},
	
	inject: function(target, position) {
		this.canvas.inject(target, position);
	}
});