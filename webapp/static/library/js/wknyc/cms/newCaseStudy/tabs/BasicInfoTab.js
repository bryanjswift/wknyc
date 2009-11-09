/**
 * @author joseph.schmitt
 */
wknyc.cms.newCaseStudy.BasicInfoTab = new Class({
	Extends: wknyc.cms.newCaseStudy.BasicCaseStudyTab,
	
	//Class vars
	
	/**
	 * Class constructor
	 */
	initialize: function(canvas) {
		this.parent(canvas);
	},
	
	setupAssets: function() {
		this.parent();
		
		//Input Fields
		var nameField = new wknyc.common.gui.InputField($('nameField'), {prefillElement:null});
		var nameFieldPopup = new wknyc.cms.newCaseStudy.HelpPopup($('nameField'), {focusBlurTarget:'nameField'});
		
		var shortDescField = new wknyc.common.gui.InputField($('shortDescField'), {prefillElement:null});
		var shortDescPopup = new wknyc.cms.newCaseStudy.HelpPopup($('shortDescField'), {focusBlurTarget:'shortDescField'});
		
		var longDescField = new wknyc.common.gui.InputField($('longDescField'), {prefillElement:null});
		var longDescPopup = new wknyc.cms.newCaseStudy.HelpPopup($('longDescField'), {focusBlurTarget:'longDescField'});
		
		var launchDateMonth = new wknyc.common.gui.InputField($('launchDateMonth'));
		var launchDateMonthPopup = new wknyc.cms.newCaseStudy.HelpPopup($('launchDateMonth').getParent(), {focusBlurTarget:'launchDateMonth', titleTagTarget: 'launchDateMonth'});
		
		var launchDateYear = new wknyc.common.gui.InputField($('launchDateYear'));
		var launchDateYearPopup = new wknyc.cms.newCaseStudy.HelpPopup($('launchDateYear').getParent(), {focusBlurTarget:'launchDateYear', titleTagTarget: 'launchDateYear'});
		
		
		//Client Selector
		var clientSelectorPopup = new wknyc.cms.newCaseStudy.HelpPopup($('clientSelect'), {focusBlurTarget:'clientSelect'});
		
		
		//Order Picker
		var orderpickerField = new wknyc.common.gui.InputField($('orderpicker'), {prefillElement:null});
		var orderPicker = new wknyc.common.gui.SpinBox(
			$('orderpicker'),
			{
				minValue: 1,
				maxValue: 10
			}
		);
		var orderpickerPopup = new wknyc.cms.newCaseStudy.HelpPopup($('orderpicker'), {focusBlurTarget:'orderpicker'});
		
		
		//Select PDF
		var select_PDF = new wknyc.common.gui.SkinnedFileInput($('select_PDF'), {
				skinnedElement: $('select_PDF').getNext('a.file_upload'),
				reuploadText: 'select new PDF'
		});
		
		//Draggable Toggle
		var onSiteToggle = new wknyc.common.gui.DraggableToggle(
			$('displayOnSiteRadio-on'),
			$('displayOnSiteRadio-off')
		);
	}
});