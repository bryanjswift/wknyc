/**
 * Handles displaying and hiding of Tabbed content.
 * 
 * Parameters:
 * 	tabs:			Element containing all of the tabs. Usually a UL containing LI's.
 * 	content:		Element containing the tab content. Usually a DIV containing more
 * 					DIV's. The content must be in the same order as the tabs.
 * 
 * Options:
 * 	duration:		Number for how long the fade transition for the new content should 
 * 					be. For no fade, pass in a 0.
 * 	defaultTab:		The index of the tab that is loaded at startup if loadDefaultTab
 * 					is set to true.
 * 	loadDefaultTab:	Boolean for whether or not to automatically load the default tab
 * 					when the class is initialized.
 * 	useDeepLinking	Boolean for whether or not the tabs should updated the address
 * 					bar when being selected, enabling Deep Linking.
 * 	activeClass:	Name of the class to add to the currently active tab and content.
 * 	useAjax:		Boolean for whether or not the tabs will be loaded through AJAX
 * 					instead of simply showing/hiding markup.
 * 	urlAttribute:	If you're using ajax loading, then the tab item must have an
 * 					anchor tab with an attribute that holds the url for the content.
 * 					The attribute is usually an 'href', but can be modified through
 * 					this option.
 * 
 * Events:
 * 	change:			Fired every time a new tab is clicked. For ajax tabs, it fires
 * 					after the new content has loaded and has been written to the DOM,
 * 					but before the transition starts.
 * 
 * @author joseph.schmitt
 */
wknyc.common.gui.Tabs = new Class({
	Implements: [Options, Events],
	
	//Options
	options: {
		duration: 250,
		defaultTab: 0,
		loadDefaultTab: true,
		useDeepLinking: false,
		activeClass: 'active',
		useAjax: false,
		urlAttribute: 'href'
	},
	
	//Class vars
	tabs: null,
	content: null,
	currentIndex: -1,
	isFirstLoad: true,
	
	/**
	 * Class constructor
	 */
	initialize: function(tabs, content, options) {
		this.tabs = $(tabs);
		this.content = $(content);
		this.setOptions(options);
		
		this.setupAssets();
		this.addEvents();
		
		if(this.options.useDeepLinking)
			this.setupDeepLinking();
		else if(this.options.loadDefaultTab)
			this.loadDefaultTab();
		else
			return;
	},
	
	setupAssets: function() {
		this.content.getChildren().each(function(item){
			item.set('styles', {'display': 'none', 'opacity': 0});
		});
	},
	
	addEvents: function() {
		this.tabs.getChildren('li').each(function(tab, index, tabslist){
			tab.store('index', index);
			tab.addEvent('click', function(e){
				this.selectTab(tab, false);
				return false;
			}.bind(this));
			
		}.bind(this));
	},
	
	loadDefaultTab: function() {
		if (this.isFirstLoad) {
			this.selectTab(this.tabs.getElements('li')[this.options.defaultTab], true);
			this.isFirstLoad = false;
		}
	},
	
	setupDeepLinking: function() {
		SWFAddress.addEventListener(SWFAddressEvent.CHANGE, this.onAddressUpdated.bind(this));
	},
	
	onAddressUpdated: function(event) {
		var anchor = $(event.path);
		
		//Checks for a valid deep link
		if( anchor ) {
			var tab = anchor.getParent();
			this.doSelectTab(tab.retrieve('index'), this.isFirstLoad, tab);
			this.isFirstLoad = false;
//			SWFAddress.setTitle("Page Title / " + event.path);
		}
		//If the url doesn't match to a valid tab, load the default tab
		else if( this.isFirstLoad && this.options.loadDefaultTab ) {
			this.loadDefaultTab();
		}
	},
	
	selectTabByIndex: function(index, skipFade) {
		var tab = this.tabs.getElements('li')[index];
		this.selectTab(tab, skipFade);
	},
	
	/**
	 * Selects a tab 
	 * @param {Object} index
	 * @param {Object} skipFade
	 * @param {Object} tab
	 */
	selectTab: function(tab, skipFade) {
		if( this.options.useDeepLinking )
			window.location.hash = "#"+tab.getElement('a').get('id');
		else
			this.doSelectTab(tab.retrieve('index'), false, tab);
	},
	
	doSelectTab: function(index, skipFade, tab) {
		var prevContent = this.content.getElement('.'+this.options.activeClass);
		
		if( this.tabs.getChildren('.'+this.options.activeClass) )
			this.tabs.getChildren('.'+this.options.activeClass).removeClass(this.options.activeClass);
		if( this.content.getChildren('.'+this.options.activeClass) ) 
			this.content.getChildren('.'+this.options.activeClass).removeClass(this.options.activeClass);
		
		
		var curTab;
		if( this.tabs.getChildren().length > 0 ) 
			curTab = this.tabs.getChildren()[index].addClass(this.options.activeClass);
		
		
		if( !this.options.useAjax ) {
			var curContent;
			if( this.content.getChildren().length > 0 ) curContent = this.content.getChildren()[index].addClass(this.options.activeClass);
			
			this.fireEvent('change', [index, tab]);
			this.transitionNewContent(curContent, prevContent, skipFade);
		}
		else {
			this.loadAjaxContent(index, tab, skipFade);
		}
		
		this.currentIndex = index;
	},
	
	transitionNewContent: function(curContent, prevContent, skipFade) {
		//Fades out the previous content
		if(prevContent ) {
			prevContent .set('styles', {'display': 'none', 'opacity': 0});
		}
		
		//Fades in the current content
		if(curContent) {
			if( !skipFade ) {
				curContent.set('tween', {onStart:function(){
					curContent.set('styles', {'display': 'block'});
				}, duration: this.options.duration, transition: new Fx.Transition(Fx.Transitions.Quad.easeOut)});
				curContent.set('opacity', 0).fade('in');
			}
			else {
				curContent.set('styles', {'display': 'block'});
				curContent.fade('show');
			}
		}
	},
	
	loadAjaxContent: function(index, tab, skipFade) {
		var myHTMLRequest = new Request.HTML().get(tab.getElement('a').get(this.options.urlAttribute));
		myHTMLRequest.addEvent('success', 
			function(responseTree, responseElements, responseHTML, responseJavaScript){
				this.content.empty().set({'html': responseHTML, 'opacity': 0});
				
				this.fireEvent('change', [index, tab]);
				this.transitionNewContent(this.content, null, skipFade);
			}.bind(this)
		);
	}
});
