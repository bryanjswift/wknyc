/**
 * @author freelancedeveloper1
 */
wknyc.common.gui.SpinBox = new Class({
	Implements: Options,
	
	//Options
	options: {
		topButton: 'a.top',
		botButton: 'a.bottom',
		minValue: -1,
		maxValue: -1,
		wrap: true
	},
	
	//Class vars
	input: $empty,
	topButton: $empty,
	botButton: $empty,
	maxValue: null,
	minValue: null,
	
	
	/**
	 * Class constructor
	 * @param {Element} element - Input element to use as the spin box. 
	 * @param {Object} options
	 */
	initialize: function(element, options) {
		this.setOptions(options);
		
		this.input = element;
		this.topButton = $(element).getParent().getChildren(this.options.topButton)[0];
		this.botButton = $(element).getParent().getChildren(this.options.botButton)[0];
		
		var wrapper = new Element('div').addClass('spinbox-wrapper').wraps(this.input);
		wrapper.grab(this.topButton, 'top').grab(this.botButton);
		
		this.setMaxValue(this.options.maxValue);
		this.setMinValue(this.options.minValue);
		this.addEvents();
		
		this.modifyValue(0);
	},
	
	addEvents: function(){
		this.topButton.addEvent('click', function(e){
			this.modifyValue(1, this.options.wrap);
			return false;
		}.bind(this));
		
		this.botButton.addEvent('click', function(e){
			this.modifyValue(-1, this.options.wrap);
			return false;
		}.bind(this));
	},
	
	setMaxValue: function(value) {
		this.maxValue = value.toInt() >= 0 ? value.toInt() : null;
	},
	
	setMinValue: function(value) {
		this.minValue = value.toInt() >= 0 ? value.toInt() : null;
	},
	
	modifyValue: function(delta, wrap){
		var value = this.input.get('value').toInt();
		
		if( value.toString() != 'NaN' ) {
			value += delta;
			
			if( $defined(this.minValue) && $defined(this.maxValue) ) {
				//Too high
				if( value > this.maxValue ) {
					if( wrap ) value = this.minValue;
					else value = this.maxValue;
				}
				//Too low
				else if( value < this.minValue ){
					if( wrap ) value = this.maxValue;
					else value = this.minValue;
				} 
			}
			
			this.input.set('value', value);
		}
		else {
			this.input.set('value', '0');
		}
	}
});
