/**
 * @author jayjo
 */
wknyc.common.gui.SectionTabs = new Class({
	Implements: Options,
	
	//Class vars
	options: {},
	tabsElement: null,
	displayOn: false,
	callback: null,
	
	//Options
	options: {
		callback: null
	},
	
	//Methods
	/**
	 * Class constructor
	 * @param {Element} tabsElement - Reference to the elements that holds the tabs,
	 * usually a UL.
	 * @param {Object} options - Optional properties, listed above under "Options"
	 */
	initialize: function(tabsElement, options){
		this.setOptions(options);
		
		//Assign options to class variables
		this.tabsElement = tabsElement;
		this.callback = options.callback;
		
		this.createOnStates();
	},
	
	/**
	 * Creates a duplicate item for "on" state so that the font can be rendered
	 */
	createOnStates: function(){
		var onState, offState, $this = this;

		this.tabsElement.getElements('li').each(function(element){
			offState = element;
			offState.addEvent('click', function(){
				$this.toggleState(this);
				$this.callback.pass(this.get('id')).call();
			});
			
			onState = element.clone(true, true);
			onState.addClass('on').inject(offState, 'after').addClass('hide');
		});
	},
	
	/**
	 * Toggles the state of an element between "on" and "off"
	 * @param {Element} element - LI element 
	 */
	toggleState: function(element){
		//Hides all the "on" state tabs and displays all the "off" state tabs
		this.tabsElement.getElements('li').each(function(tab){
			if( tab.hasClass('on') && !tab.hasClass('hide') ) 
				tab.addClass('hide');
			
			if( !tab.hasClass('on') ) tab.removeClass('hide');
		});
		
		//Displays the "on" state of the submitted element
		this.showOnState(element, !this.displayOn);
	},
	
	/**
	 * Determines whether or not to show the "on" state.
	 * @param {Boolean} showOn - If true, displays the on state. Else, displays the "off" state.
	 */
	showOnState: function(element, showOn){
		element = $(element);
		
		// if the state is the same as the current state, do nothing
		if( showOn == this.displayOn ) return false;
		
		//Display the on state
		if( showOn ) {
			element.addClass('hide').getNext('.on.hide').removeClass('hide');
		}
		//Display the off state
		else {
			element.removeClass('hide').getNext('.on').addClass('hide');
		}
	}
});