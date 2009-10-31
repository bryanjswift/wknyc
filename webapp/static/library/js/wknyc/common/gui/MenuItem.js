/**
 * @author joseph.schmitt
 */
wknyc.common.gui.MenuItem = new Class({
	Implements: Options,
	
	options: {
		selectedClass: 'on',
		itemElement: 'li',
		multiSelect: false,
		allOption: null,
		itemCallback: null
	},
	
	//Class vars
	itemsList: null,
	callback: null,
	allItem: null,
	data: [],
	
	/**
	 * Class Constructor
	 * @param {Element} el - HTML element that holds the menu items.
	 */
	initialize: function(el, options) {
		this.setOptions(options);
		this.itemsList = el;
		this.callback = this.options.itemCallback;
		
		if( this.options.multiSelect && this.options.allOption ) {
			this.createAllOption();
		}
	},
	
	createAllOption: function() {
		this.allItem = new Element(this.options.itemElement, {'html': '<a href="#">'+this.options.allOption+'</a>'}).addClass('all');
		this.allItem.addEvent('click', function(){
			if( !this.allItem.hasClass('on') )
				this.itemsList.getElements( this.options.itemElement ).addClass('on');
			else
				this.itemsList.getElements( this.options.itemElement ).removeClass('on');
			
			if( this.callback )	this.callback.call();
			return false;
		}.bind(this));
		this.itemsList.grab(this.allItem);
	},
	
	setData: function(data) {
		this.data = data;
		this.refresh();
	},
	
	refresh: function() {
		this.data.each(function(item){
			this.createItem(item);
		}.bind(this))
	},
	
	createItem: function(content) {
		var item = new Element(this.options.itemElement, {'html': '<a href="#">'+content+'</a>'});
		item.getElement('a').addEvent('click', function(e) {
			if ( !$(e.target).getParent().hasClass('on') ) {
				if( this.allItem )
					this.allItem.removeClass('on');
				
				if( !this.options.multiSelect )
					this.itemsList.getElements( this.options.itemElement ).removeClass( this.options.selectedClass );
				$(e.target).getParent().addClass( this.options.selectedClass );
				
				if( this.callback )	this.callback.call();
			}
			else if( this.options.multiSelect ) {
				if( this.allItem )
					this.allItem.removeClass('on');
					
				$(e.target).getParent().removeClass('on');
				
				if( this.callback )	this.callback.call();
			}
			
			return false;
		}.bind(this));
		
		this.itemsList.grab(item);
	}
});
