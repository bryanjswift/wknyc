/**
 * @author joseph.schmitt
 */
wknyc.cms.caseStudies.CaseStudyCapsule = new Class({
	Implements: Options,
	
	options: {
		isRequired: false,
		cloneMarkup: false
	},
	
	//Class vars
	canvas: null,
	hasExpanded: false,
	
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
		if( !this.options.isRequired ) {
			//Order Picker
			var orderpickerField = new wknyc.common.gui.InputField(this.canvas.getElement('.order input'), {prefillElement:null});
			var orderPicker = new wknyc.common.gui.SpinBox(
				this.canvas.getElement('.order input'),
				{
					minValue: 1,
					maxValue: 10
				}
			);
		}
		
		//Input Fields
		var assetTitleField = new wknyc.common.gui.InputField(this.canvas.getElements('.title input')[0], {prefillElement:'label.prefill'});
		var urlField = new wknyc.common.gui.InputField(this.canvas.getElements('.title input')[1], {prefillElement:'label.prefill'});
		var launchDateField;
		this.canvas.getElements('.launchDate input').each(function(input){
			launchDateField = new wknyc.common.gui.InputField(input, {prefillElement:'label.prefill'});
		});
		
		//Expand/Collapse
		var expandCollapse = this.canvas.getElements('a.expand-collapse');
		expandCollapse.each(function(button){
			button.addEvent('click', function(e){
				this.expandCollapseToggle($(e.target));
				return false;
			}.bind(this));
		}.bind(this));
	},
	
	expandCollapseToggle: function(button) {
		if( button.hasClass('open') ) {
			//Needs to update all the expand/collapse buttons in the capsule
			this.canvas.getElements('a.expand-collapse').each(function(but){
				but.removeClass('open')
			});
			
			this.canvas.getElement('.content').addClass('hide');
		}
		else {
			//Needs to update all the expand/collapse buttons in the capsule
			this.canvas.getElements('a.expand-collapse').each(function(but){
				but.addClass('open')
			});
			
			this.canvas.getElement('.content').removeClass('hide');
			
			if( !this.hasExpaned ) this.setupExpandedContent();
		}
	},
	
	setupExpandedContent: function() {
		this.hasExpanded = true;
		
		var addVideo = new wknyc.common.gui.SkinnedFileInput($('select_video'), {
				skinnedElement: $('select_video').getNext('a.plus'),
				reuploadText: 'select new video'
		});
	},
	
	inject: function(target, position) {
		this.canvas.inject(target, position);
	}
});