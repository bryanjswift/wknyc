/**
 * @author joseph.schmitt
 */
wknyc.common.gui.SkinnedFileInput = new Class({
	Implements: Options,
	
	//Options
	options: {
		skinnedElement: null,
		reuploadText: ''
	},
	
	//Class vars
	input: $empty,
	skinnedElement: $empty,
	filenameElement: $empty,

	
	/**
	 * Class constructor
	 * @param {Element} element - The file input element to use.
	 * @param {Object} options
	 */
	initialize: function(element, options) {
		this.setOptions(options);
		this.input = $(element);
		this.skinnedElement = $(this.options.skinnedElement);
		
		this.setupAssets();
		this.updateFilename();
	},
	
	setupAssets: function() {
		this.filenameElement = new Element('label').addClass('fileinput-filename');
		var wrapper = new Element('div').addClass('fileinput-wrapper').wraps(this.input).grab(this.filenameElement).grab(this.skinnedElement);
		
		this.input.addEvent('change', this.updateFilename.bind(this));
		this.input.addEvent('mouseout', this.updateFilename.bind(this));
	},
	
	updateFilename: function(e){
		this.filenameElement.empty();
		
		if( this.input.value != "" ) {
			this.filenameElement.appendText(this.input.value);
			
			if( this.options.reuploadText && this.options.reuploadText != '') {
				this.skinnedElement.set('html', this.options.reuploadText);
			}
		}
	}
});
