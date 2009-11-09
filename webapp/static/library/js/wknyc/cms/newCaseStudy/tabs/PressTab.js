/**
 * @author joseph.schmitt
 */
wknyc.cms.newCaseStudy.PressTab = new Class({
	Extends: wknyc.cms.newCaseStudy.BasicCaseStudyTab,
	
	//Class vars
	capsules: [],
	
	/**
	 * Class constructor
	 */
	initialize: function(canvas) {
		this.parent(canvas);
	},
	
	setupAssets: function() {
		this.parent();
		
		//Pre-rendered Capsules
		var capsule;
		this.form.getElements('fieldset.capsule').each(function(capsuleMarkup, index){
			capsule = new wknyc.cms.newCaseStudy.PressCapsule( capsuleMarkup, {isRequired: capsuleMarkup.hasClass('required')} );
			this.capsules.push(capsule);
		}.bind(this));
		
		//Add more button
		var addMoreButton = $('add_more_press').addEvent('click', function(e){
			var newCapsule = new wknyc.cms.newCaseStudy.PressCapsule( $$('.templates fieldset.capsule')[0], {cloneMarkup: true} );
			newCapsule.inject(this.form);
			return false;
		});
	},
	
	setupValidation: function() {
		this.validator = new Form.Validator(this.form);
		this.validator.ignoreField(this.canvas.getElement('.pressDate .pressDateDay'));
		this.validator.ignoreField(this.canvas.getElement('.pressDate .pressDateMonth'));
		this.validator.ignoreField(this.canvas.getElement('.pressDate .pressDateYear'));
		
		this.validator.setOptions({
			onFormValidate: function(passed, form, event) {
				
			},
			
			onElementFail: function(element, validatorNames) {
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
				wknyc.cms.newCaseStudy.HelpPopup.Unwrap(element);
			}			
		});
	},
	
	getDateFromFields: function() {
		var month = this.canvas.getElement('.pressDate .pressDateMonth').get('value') != "" ? this.canvas.getElement('.pressDate .pressDateMonth').get('value').toInt() : "";
		var day = this.canvas.getElement('.pressDate .pressDateDay').get('value') != "" ? this.canvas.getElement('.pressDate .pressDateDay').get('value').toInt() : "";
		var year = this.canvas.getElement('.pressDate .pressDateYear').get('value') != "" ? this.canvas.getElement('.pressDate .pressDateYear').get('value').toInt() : "";
		
		if( month+day+year == "" ) return "";
		else return month+'/'+day+'/'+year;
	},
	
	submit: function() {
		this.canvas.getElement('.pressDate .dateValue').set({'value': this.getDateFromFields()});
		console.log(this.canvas.getElement('.pressDate .dateValue'));
		console.log(this.canvas.getElement('.pressDate .dateValue').get('value'));
		this.parent();
	}
});