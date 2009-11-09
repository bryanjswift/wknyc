/**
 * Controls the behavior of generic help text popups for any case study form elements. 
 * 
 * Parameters:
 * 	el:					Element to wrap with the popup. Also used to read the title 
 * 						tag for the content if the titleTagTarget isn't set.
 * Options:
 * 	focusBlurTarget:	Element that should be targeted when adding the 'focus' 
 * 						and 'blur' events. If none is provided, then the 'focus' 
 * 						and 'blur' events aren't added.
 * 	titleTagTarget: 	Element that should be targeted that has a title tag with 
 * 						the content	for the popup. Defaults to the 'el' parameter 
 * 						in the initialize function. 
 * 
 * @author joseph.schmitt
 */
wknyc.cms.newCaseStudy.HelpPopup = new Class({
	Implements: Options,
	
	options: {
		focusBlurTarget: null,
		titleTagTarget: null
	},
	
	//Class vars
	element: $empty,
	wrapper: $empty,
	focusBlurTarget: null,
	titleTagTarget: null,
	popup: $empty,
	
	/**
	 * Class constructor
	 */
	initialize: function(el, options) {
		this.setOptions(options);
		
		this.element = $(el);
		this.focusBlurTarget = $(this.options.focusBlurTarget);
		this.titleTagTarget = $(this.options.titleTagTarget)
		if( !this.options.titleTagTarget ) this.titleTagTarget = this.element;
		
		this.setupAssets();
		this.hide(true);
	},
	
	setupAssets: function(){
		this.wrapper = new Element('div').addClass('popup-wrapper').wraps(this.element);
		this.popup = new Element('div').addClass('popup').inject(this.wrapper);
		var text = new Element('div').inject(this.popup).appendText(this.titleTagTarget.get('title'));
		this.popup.set('tween', {duration: 200, transition: new Fx.Transition(Fx.Transitions.Quad.easeOut)});
		
		this.element.erase('title');
		
		if( this.focusBlurTarget ) {
			this.focusBlurTarget.addEvent('focus', function(){
				this.show();
			}.bind(this));
			
			this.focusBlurTarget.addEvent('blur', function(){
				this.hide();
			}.bind(this));
		}
	},
	
	show: function(skipTween){
		if(skipTween) this.popup.fade('show');
		else this.popup.fade('in');
	},
	
	hide: function(skipTween){
		if(skipTween) this.popup.fade('hide');
		else this.popup.fade('out');
	},
	
	destroy: function() {
		wknyc.cms.newCaseStudy.HelpPopup.Unwrap(this.element);
		this.popup.erase('tween');
		
		if( this.focusBlurTarget ) this.focusBlurTarget.removeEvents();
		
		this.element = null;
		this.wrapper = null;
		this.focusBlurTarget = null;
		this.titleTagTarget = null;
		this.popup = null;
	}
});

wknyc.cms.newCaseStudy.HelpPopup.Unwrap = function(element) {
	var wrapper = element.getParent('.popup-wrapper');
	if(!wrapper) return;
	
	element.inject(wrapper, 'before');
	wrapper.dispose();
};