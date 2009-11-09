/**
 * @author joseph.schmitt
 */
wknyc.common.gui.DraggableToggle = new Class({
	Implements: Options,
	
	//Options
	options: {
		
	},
	
	//Class vars
	onElement: $empty,
	onButton: $empty,
	offElement: $empty,
	offButton: $empty,
	handle: $empty,
	frame: $empty,
	onState: true,
	leftLimit: 0,
	rightLimit: 0,
	
	/**
	 * Class constructor
	 * @param {Element} element - Input element to use as the spin box. 
	 * @param {Object} options
	 */
	initialize: function(onElement, offElement) {
		this.onElement = onElement.addClass('hide');
		this.offElement = offElement.addClass('hide');
		
		this.draw();
		this.getStartState();
		this.addEvents();
	},
	
	draw: function() {
		var wrapper = new Element('div').addClass('dragtoggle-wrapper');
		wrapper.wraps(this.onElement).grab(this.offElement);
		
		this.onButton = new Element('a').set('href', "#").appendText('On').addClass('on').inject(wrapper);
		this.offButton = new Element('a').set('href', "#").appendText('Off').addClass('off').inject(wrapper);
		this.frame = new Element('div').addClass('frame').inject(wrapper);
		this.handle = new Element('div').addClass('handle').inject(wrapper).set('tween', {duration: 200, transition: new Fx.Transition(Fx.Transitions.Quint.easeOut)});
		
		this.leftLimit = -this.onButton.getStyle('width').toInt();
		this.rightLimit = 0;
		
		var dragHandle = new Drag(this.handle, {
			handle: this.frame,
			limit: {
				x: [this.leftLimit, this.rightLimit],
				y: [0, 0],
			},
			onComplete: function(el, event) {
				var curX = el.getStyle('left').toInt();
				
				if( curX < this.leftLimit/2 )
					this.setToggleState(false);
				else
					this.setToggleState(true);
			}.bind(this)
		});
	},
	
	getStartState: function() {
		if( this.offElement.get('checked'))
			this.setToggleState(false, true);
		else
			this.setToggleState(true, true);
	},
	
	addEvents: function(){
		this.onButton.addEvent('click', function(e){
			this.setToggleState(false);
			return false;
		}.bind(this));
		
		this.offButton.addEvent('click', function(e){
			this.setToggleState(true);
			return false;
		}.bind(this));
	},
	
	/**
	 * Sets the state to on or off and updates the view accordingly
	 * @param {Boolean} onState - Whether or not the state is 'ON'
	 */
	setToggleState: function(onState, noTween){
		this.onState = onState;
		
		if( onState ) {
			if( noTween ) this.handle.set('styles', {'left': '0'}) 
			else this.handle.tween('left', '0');
			
			this.offButton.addClass('hide');
			this.onButton.removeClass('hide');
			
			//Update the radio button element
			this.onElement.set('checked', 'checked');
			this.offElement.erase('checked');
		}
		else {
			if( noTween ) this.handle.set('styles', {'left': this.leftLimit}) 
			else this.handle.tween('left', this.leftLimit);
			
			this.onButton.addClass('hide');
			this.offButton.removeClass('hide');
			
			//Update the radio button element
			this.offElement.set('checked', 'checked');
			this.onElement.erase('checked');
		}
	},
	
	toggleState: function() {
		this.toggleState(!this.onState);
	}
});
