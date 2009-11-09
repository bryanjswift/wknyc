/**
 * @author joseph.schmitt
 */
wknyc.common.gui.DropDown = new Class({
	Implements: Options,
	
	//Options
	options: {
		tab: 'a',
		body: 'ul',
		itemClass: '',
		injectTarg: 'ul',
		injectLoc: 'bottom'
	},
	
	//Class vars
	element: null,
	tab: null,
	body: null,
	items: [],
	data: {},
	
	initialize: function(element, options) {
		this.setOptions(options);
		
		this.element = $(element);
		this.tab = this.element.getElement(this.options.tab);
		this.body = this.element.getElement(this.options.body);
		this.items = [];
	},
	
	setData: function(data, refreshFnc) {
		this.data = data;
		this.refresh(refreshFnc);
	},
	
	refresh: function(fnc) {
		//Build items
		if( !fnc ) {
			var el, targ = this.element.getElement(this.options.injectTarg);
			this.data.each(function(item){
				this.buildItem(item, targ);
			}, this);
		}
		else {
			this.data.each(fnc);
		}
		
		//Add tab events
		this.element.addEvent('mouseenter', function(element){
			this.body.fade('in');
		}.bind(this));
		this.element.addEvent('mouseleave', function(element){
			this.body.fade('out');
		}.bind(this));
	},
	
	buildItem: function(item, targ) {
		var el = new Element('li', {'class': this.options.itemClass});
		el.appendText(item).inject(targ, this.options.injectLoc);
		this.items.push(el)
	},
	
	addMouseEvents: function(click, enter, leave) {
		this.items.each(function(item, index, array){
			//click
			if (click) {
				item.addEvent('click', function(){
					click.pass([item, index, array]).call();
				});
			}
			//mouseenter
			if (enter) {
				item.addEvent('mouseenter', function(){
					enter.pass([item, index, array]).call();
				});
			}
			//mouseleave
			if (leave) {
				item.addEvent('mouseleave', function(){
					leave.pass([item, index, array]).call();
				});
			}
		});
	}
});
