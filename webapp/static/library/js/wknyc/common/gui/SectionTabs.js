/**
 * @author joseph.schmitt
 */
wknyc.common.gui.SectionTabs = new Class({
	Extends: wknyc.common.gui.Tabs,
	
	//Class vars
	
	//Methods
	/**
	 * Class constructor
	 * @param {Element} tabsElement - Reference to the elements that holds the tabs,
	 * usually a UL.
	 * @param {Object} options - Optional properties, listed above under "Options"
	 */
	initialize: function(tabs, content, options){
		this.parent(tabs, content, options);
	},
	
	addEvents: function() {
		var wrapper;
		this.tabs.getChildren('li').each(function(tab, index, tabslist){
			tab.store('index', index);
			tab.addEvent('click', function(e){
				this.selectTab(tab, false);
				return false;
			}.bind(this));
			
			/**
			 * Creates a duplicate item for "on" state so that the font can be rendered
			 */
			onState = tab.getElement('a.univers').clone(true, true);
			onState.inject(tab.getElement('a.univers'), 'after').addClass(this.options.activeClass+' hide');
		}.bind(this));
	},
	
	/**
	 * Selects a tab 
	 * @param {Object} index
	 * @param {Object} skipFade
	 * @param {Object} tab
	 */
	selectTab: function(tab, skipFade) {
		this.toggleState(tab);
		this.parent(tab, skipFade);
	},
	
	/**
	 * Turns all other tabs off and turns the passed in tab on.
	 * @param {Element} tab - LI element 
	 */
	toggleState: function(onTab) {
		//console.log('----------------------------------');
		this.tabs.getChildren('li').each(function(tab){
			this.showOnState(tab, false);
		}.bind(this));
		
		this.showOnState(onTab, true);
	},
	
	/**
	 * Toggles the state of an element between "on" and "off"
	 * @param {Element} tab - LI element 
	 */
	showOnState: function(tab, showActive){
		var offState = tab.getElement('a.univers:not(.'+this.options.activeClass+')');
		var onState = offState.getNext('a.'+this.options.activeClass);
		
		//console.log('showOnState');
		//console.log(offState);
		//console.log(onState);
		
		if( showActive ) {
			onState.removeClass('hide');
			if( !offState.hasClass('hide') ) offState.addClass('hide');
		}
		else {
			offState.removeClass('hide');
			if( !onState.hasClass('hide') ) onState.addClass('hide');
		}
	}
});