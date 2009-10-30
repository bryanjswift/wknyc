/**
 * @author joseph.schmitt
 */
wknyc.cms.caseStudies.FiltersData = new Class({
	//Class vars
	clients: [],
	updatedDates: [],
	publishedDates: [],
	statuses: {},
	
	/**
	 * Class constructor
	 */
	initialize: function(cases) {
		this.clients = wknyc.json.work.clients.client.reverse();
		
		/**
		 * TODO: Dates are being statically set for now until we get the data. Once
		 * the data is coming in, get the real, dynamic data into the array.
		 */
		this.updatedDates = ['1 hour ago', '1 day ago', '1 week ago', '1 year ago', 'all time'];
		
		//Inserts all unique published dates into the array
//		var date;
//		cases.each(function(item, index, array){
//			date = item.month + '/' + item.year;
//			this.publishedDates.include(date);
//		}.bind(this));
		
		/**
		 * TODO: Dates are being statically set for now until we get the data. Once
		 * the data is coming in, get the real, dynamic data into the array.
		 */
		this.publishedDates = ['1 hour ago', '1 day ago', '1 week ago', '1 year ago', '5 years ago'];
		
		/**
		 * TODO: Statuses are being statically set for now until we get the data. Once
		 * the data is coming in, get the real, dynamic data into the array.
		 */
		this.statuses = new Hash({
			'copy': 'Copywriting Needed',
			'art': 'Art Direction Needed',
			'curation': 'Master Curation Needed',
			'copy_finished': 'Copywriting Finished',
			'art_finished': 'Art Direction Finished',
			'curation_finished': 'Master Curation Finished',
			'draft': 'Locked Draft'
		});
	},
	
	
	//CLIENTS FILTER DATA
	
	/**
	 * Returns an array with all of the clients in it.
	 * 
	 * @param {Function} fnc (Optional) - If present, will loop through the clients
	 * array and execute the function on each item in the array with the following params:
	 * 	item: the client name
	 * 	index: the index in the array
	 * 	array: the reference to the clients array
	 * 
	 * @return {Array} Returns the array of clients.
	 */
	getAllClients: function(fnc) {
		if( fnc ) {
			this.clients.each(function(item, index, array){
				fnc.pass([this.getClientByIndex(index), index, array]).call();
			}.bind(this));
		}
		return this.clients;
	},
	
	getClientByIndex: function(index) {
		return this.clients[index].value;
	},
	
	
	//UPDATED DATES DATA
	/**
	 * Returns an array with all of the updated dates in it.
	 * 
	 * @param {Function} fnc (Optional) - If present, will loop through the updated dates
	 * array and execute the function on each item in the array with the following params:
	 * 	item: the updated date
	 * 	index: the index in the array
	 * 	array: the reference to the updated date array
	 * 
	 * @return {Array} Returns the array of updated dates.
	 */
	getUpdatedDates: function(fnc) {
		if (fnc) {
			this.updatedDates.each(function(date, index, array){
				fnc.pass([this.getUpdatedDateByIndex(index), index, this.updatedDates]).call();
			}.bind(this));
		}
		return this.updatedDates;
	},
	
	getUpdatedDateByIndex: function(index) {
		return this.updatedDates[index];
	},
	
	
	//PUBLISHED DATES DATA
	
	/**
	 * Returns an array with all of the published dates in it.
	 * 
	 * @param {Function} fnc (Optional) - If present, will loop through the published dates
	 * array and execute the function on each item in the array with the following params:
	 * 	item: the published date
	 * 	index: the index in the array
	 * 	array: the reference to the published date array
	 * 
	 * @return {Array} Returns the array of published dates.
	 */
	getPublishedDates: function(fnc) {
		if (fnc) {
			this.publishedDates.each(function(date, index, array){
				fnc.pass([this.getPublishedDateByIndex(index), index, this.publishedDates]).call();
			}.bind(this));
		}
		return this.publishedDates;
	},
	
	getPublishedDateByIndex: function(index) {
		return this.publishedDates[index];
	},
	
	getStatuses: function(fnc) {
		if( fnc ) {
			this.statuses.each(function(status, key, hash){
				fnc.pass([status, key, hash]).call();
			});
		}
		return this.statuses;
	},
	
	getStatusByKey: function(key) {
		statuses.get(key);
	}
});
