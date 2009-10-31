/**
 * @author joseph.schmitt
 */

 wknyc.common.gui.InputField = new Class({
 	Implements: [Events, Options],
	
	//Options
	options: {
		prefillElement: 'label',
		wrapperName: 'input-wrap'
	},
	
	//Class vars
	input: null,
	prefill: null,
	
	/**
	 * Class constructor
	 * @param {Object} el
	 * @param {Object} options
	 */
	initialize: function(el, options) {
		this.setOptions(options);
		
		this.input = el;
		if( $defined(this.options.prefillElement) && this.options.prefillElement != '')
			this.prefill = this.input.getPrevious(this.options.prefillElement).removeClass('hide');
		
		this.addEvents();
		
		if (this.prefill) {
			this.wrapElements();
			
			//Handles case for browsers that save form data between refreshes
			if (this.input.get('value') != "") this.prefill.addClass('hide');
		}
	},
	
	wrapElements: function() {
		var wrapper = new Element('div').addClass(this.options.wrapperName);
		wrapper.wraps(this.prefill).wraps(this.input);
	},
	
	addEvents: function() {
		this.input.addEvent('focus', function(e){
			//Checks to see if the input is empty
			if( this.input.get('value') == "" && this.prefill ) {
				if( !this.prefill.hasClass('hide') ) this.prefill.addClass('hide');
			}
			
			$(e.target).addClass('active');
			this.fireEvent('focus');
		}.bind(this));
		
		if( this.prefill ){
			//Sets the focus to the input field if the prefill text is clicked
			this.prefill.addEvent('click', function(){
				this.input.focus();
			}.bind(this));
		}
		
		this.input.addEvent('blur', function(e){
			//Only shows the prefill text if the field is empty
			if (this.input.get('value') == "" && this.prefill) {
				this.prefill.removeClass('hide');
			}
			
			$(e.target).removeClass('active');
			this.fireEvent('blur');
		}.bind(this));
	}
 });