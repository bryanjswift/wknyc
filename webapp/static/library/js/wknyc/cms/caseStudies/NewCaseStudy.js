/**
 * Combined view & controller for the Case Studies section.
 * 
 * @author joseph.schmitt
 */
wknyc.cms.caseStudies.NewCaseStudy = new Class({
	//Class vars
	tabs: null,
	tabLoadedCallback: null,
	
	/**
	 * Class constructor
	 */
	initialize: function() {
		this.setupAssets();
	},
	
	setupAssets: function(){
		//Tab Navigation
		this.tabs = new wknyc.common.gui.Tabs(
			$('steps_tabs'), 
			$('steps_content'), 
			{
				useAjax: true,
				defaultTab: 1
			}
		).addEvent('change', this.onTabChange.bind(this));
		
		this.tabnav = new wknyc.common.gui.Tabs(
			$('steps_nav').getElement('ol'),
			$('steps_content'),
			{
				useAjax: true,
				loadDefaultTab: false
			}
		).addEvent('change', this.onTabNav.bind(this));
	},
	
	onTabChange: function(tabindex, tab) {
		this.tabLoadedCallback.call();
		this.updateNav();
	},
	
	/**
	 * Sets the function that should be called once the new tab content has been
	 * loaded.
	 * @param {Function} fnc - Function to set the callback to.
	 */
	setTabLoadedCallback: function(fnc) {
		this.tabLoadedCallback = fnc;
	},
	
	onTabNav: function(tabindex, tab) {
		this.tabs.selectTabByIndex(tabindex);
	},
	
	updateNav: function() {
		$('steps_nav').getElements('ol li:not(.hide)').each(function(item, index){
			item.addClass('hide');
		});
		
		var prev = this.tabs.currentIndex - 1;
		var next = this.tabs.currentIndex + 1;
		
		if( prev >= 0 ) 
			$('steps_nav').getElements('ol li')[prev].removeClass('hide');
		
		if( next < $('steps_nav').getElements('ol li').length )
			$('steps_nav').getElements('ol li')[next].removeClass('hide');
	},
	
	/**
	 * Function to be called once the Basic Info section is loaded.
	 */
	setupBasicInfoSection: function() {
		var basicInfo = new wknyc.cms.caseStudies.BasicInfoTab($('basic_info'));
	},
	
	setupAssetsSection: function() {
		var assets = new wknyc.cms.caseStudies.AssetsTab($('assets'));
	},
	
	setupCreditsSection: function() {
		console.log('Credits');
	},
	
	setupPressSection: function() {
		console.log('Press');
	},
	
	setupAwardsSection: function() {
		console.log('Awards');
	}
});