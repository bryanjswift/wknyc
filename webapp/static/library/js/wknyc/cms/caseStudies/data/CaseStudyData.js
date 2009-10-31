/**
 * Handles the data for a single Case Study.
 * 
 * @author joseph.schmitt
 */
wknyc.cms.caseStudies.CaseStudyData = new Class({
	//Class vars
	data: {},
	media: {},
	
	//Media
	mediaPath: '/',
	thumbFull: null,
	thumbLarge: null,
	thumbMedium: null,
	thumbSmall: null,
	thumbTiny: null,
	
	//Data
	id: null,
	title: null,
	description: null,
	client: null,
	published: null,
	updated: null,
	status: null,
	
	
	//Constructor
	/**
	 * Class constructor.
	 * @param {Object} data - JSON object containing all of the data for a single
	 * case study.
	 * @param {Object} media - JSON object containing the media data for a single
	 * case study.
	 * for this model.
	 */
	initialize: function(data, media) {
		this.data = data;
		this.media = media;
		
		this.parseData();
	},
	
	
	//Methods
	/**
	 * Parses through the JSON and saves the relevant data into class variables
	 * that can be accessed through getters and setters.
	 */
	parseData: function() {
		//Thumbnails
		if (this.media) {
			this.thumbFull = this.media.images.image[0].attributes.src;
			this.thumbLarge = this.media.images.image[1].attributes.src;
			this.thumbMedium = this.media.images.image[2].attributes.src;
			this.thumbSmall = this.media.images.image[3].attributes.src;
			this.thumbTiny = this.media.images.image[4].attributes.src;
		}
		
		if (this.data) {
			this.id = this.data.id;
			this.title = this.data.title;
			this.description = this.data.description;
			this.client = this.data.client;
			this.published = this.data.month + '/' + this.data.year;
			this.updated = this.published;
			this.status = "Unknown";
		}
		
		this.mediaPath = _workRoot + this.id + '/images/';
		wknyc.utils.log(this.data);
	},
	
	
	//API
	/**
	 * Retrieves the url to a thumbnail, based on the requested size.
	 * @param {String} size - Size of the thumbnail. Defaults to 'large'.
	 */
	getThumbnail: function(size) {
		var thumbsize;
		
		switch( size ) {
			case 'full': 	thumbsize = this.thumbFull; break;
			case 'large': 	thumbsize = this.thumbLarge; break;
			case 'medium': 	thumbsize = this.thumbMedium; break;
			case 'small': 	thumbsize = this.thumbSmall; break;
			case 'tiny': 	thumbsize = this.thumbTiny; break;
			default: 		thumbsize = this.thumbLarge;
		}
		
		return this.mediaPath + thumbsize;
	},
	
	getTitle: function() {
		return this.title;
	},
	
	getDescription: function() {
		return this.description;
	},
	
	getClient: function() {
		return this.client;
	},
	
	getPublishedDate: function() {
		return this.published;
	},
	
	getUpdatedDate: function() {
		return this.updated;
	},
	
	getStatus: function() {
		return this.status;
	}
});
