/**
 * Combined view & controller for the Case Studies section.
 * 
 * @author joseph.schmitt
 */
wknyc.cms.newCaseStudy.NewCaseStudy = new Class({
	//Class vars
	tabs: null,
	tabLoadedCallback: null,
	
	currentTab: null,
	
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
				defaultTab: 2
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
		
		var saveDraftButton = $('steps_nav').getElement('a.save').addEvent('click', function(e){
			this.onSaveDraft();
			return false;
		}.bind(this));
		
		var submitButton = $('steps_nav').getElement('a.submit').addEvent('click', function(e){
			this.onFinish();
			return false;
		}.bind(this));
	},
	
	onTabChange: function(tabindex, tab) {
		this.tabLoadedCallback.call();
		this.updateNav();
	},
	
	/**
	 * Saves the current tab section, but doesn't markt he role as finished or publish
	 */
	onSaveDraft: function() {
		this.currentTab.submit();
		
		/*
		 * TODO: Stuf ro ajax call to mark the role as saved.
		 */
	},
	
	/**
	 * Saves the current tab and marks the role as finished.
	 */
	onFinish: function() {
		this.currentTab.submit();
		
		/*
		 * TODO: Stub for ajax call to mark the role as finished.
		 */
	},
	
	/**
	 * Sets the function that should be called once the new tab content has been
	 * loaded.
	 * @param {Function} fnc - Function to set the callback to.
	 */
	setTabLoadedCallback: function(fnc) {
		this.tabLoadedCallback = fnc.bind(this);
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
		if( this.currentTab ) this.currentTab.destroy();
		this.currentTab = new wknyc.cms.newCaseStudy.BasicInfoTab($('basic_info'));
	},
	
	setupAssetsSection: function() {
		if( this.currentTab ) this.currentTab.destroy();
		this.currentTab = new wknyc.cms.newCaseStudy.AssetsTab($('assets'));
	},
	
	setupPressSection: function() {
		if( this.currentTab ) this.currentTab.destroy();
		this.currentTab = new wknyc.cms.newCaseStudy.PressTab($('press'));
	}
});