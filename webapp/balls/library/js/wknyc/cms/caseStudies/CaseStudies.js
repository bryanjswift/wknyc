/**
 * Combined view & controller for the Case Studies section.
 * 
 * @author joseph.schmitt
 */
wknyc.cms.caseStudies.CaseStudies = new Class({
	//Class vars
	sectionTabs : {},
	cases: {},
	clients: {},
	curSection: 'allCaseSection',
	
	/**
	 * Class constructor
	 */
	initialize: function() {
		this.sectionTabs = new wknyc.common.gui.SectionTabs($('sectionTabs'),{
			callback: function(curSection){
				this.curSection = curSection;
				
				if( curSection == 'awardsSection') this.showAwardEntries();
				else this.showCaseStudies();
			}.bind(this)
		});
		
		this.sectionTabs.showOnState(this.curSection, true);
		this.getPageData();
	},
	
	/**
	 * Creates a request to load the data and saves it to the global JSON.
	 */
	getPageData: function() {
		var req = new Request({url:wknyc.swf.src.xml, onComplete: function(response){
			wknyc.json = new MooTools.Utilities.XmlParser(response).json();
			this.refresh();
		}.bind(this)}).send();
	},
	
	/**
	 * Refreshes the view after retrieving new data.
	 */
	refresh: function(){
		this.cases = wknyc.json.work.workcase;
		this.clients = wknyc.json.work.clients.client.reverse();
		this.cases = this.cases.reverse();
		
		this.buildSidebar();
		this.showCaseStudies();
	},
	
	/**
	 * Builds out the filters sidebar
	 */
	buildSidebar: function() {
		var data = new wknyc.cms.caseStudies.FiltersData(this.cases);
		
		//Search bar
		var searchField = new wknyc.common.gui.InputField($('caseFilter'));
		
		//Populate Clients Filter
		var clientsFilter = new wknyc.common.gui.MenuItem( 
			$('clientFilter').getElement('ul'),
			{
				itemCallback: this.onFiltersUpdated.bind(this),
				multiSelect: true,
				allOption: 'All'
			} 
		);
		clientsFilter.setData(data.getAllClients());
		
		
		//Populate Updated Date Filter
		var updatedSlider = new Slider(
			$('updated_minmax_slider'),
			$('updated_slider_minmax_minKnobA'),
			$('updated_slider_bkg_img'),
			{
				start: 0,
				end: data.getUpdatedDates().length-1,
				snap:false,
				offset: 9,
				onChange: function(pos){
					$('updated_slider_minmax_min').set('html', data.getUpdatedDateByIndex( Math.max(pos.minpos,0) ));
					$('updated_slider_minmax_max').set('html', data.getUpdatedDateByIndex( pos.maxpos ));
				}
			},
			$('updated_slider_minmax_maxKnobA')
		);
		$('updated_slider_minmax_minKnobA').addEvent('mouseup', this.onFiltersUpdated.bind(this));
		$('updated_slider_minmax_maxKnobA').addEvent('mouseup', this.onFiltersUpdated.bind(this));
		
		
		//Populate Published Date Filter
		var publishedSlider = new Slider(
			$('published_minmax_slider'),
			$('published_slider_minmax_minKnobA'),
			$('published_slider_bkg_img'),
			{
				start: 0,
				end: data.getPublishedDates().length-1,
				snap:false,
				offset: 9,
				onChange: function(pos){
					$('published_slider_minmax_min').set('html', data.getPublishedDateByIndex( Math.max(pos.minpos,0) ));
					$('published_slider_minmax_max').set('html', data.getPublishedDateByIndex( pos.maxpos ));
				}
			},
			$('published_slider_minmax_maxKnobA')
		);
		$('published_slider_minmax_minKnobA').addEvent('mouseup', this.onFiltersUpdated.bind(this));
		$('published_slider_minmax_maxKnobA').addEvent('mouseup', this.onFiltersUpdated.bind(this));
		
		
		//Populate Status Filter
		var statusFilterList = $('statusFilter').getElement('ul');
		var statusFilter;
		data.getStatuses(function(status, key, hash){
			statusFilter = new Element('li').appendText(status);
		});
		
		
		//Populate Status Filter
		var statusFilter = new wknyc.common.gui.MenuItem( 
			$('statusFilter').getElement('ul'),
			{
				itemCallback: this.onFiltersUpdated.bind(this),
				multiSelect: true,
				allOption: 'All'
			} 
		);
		statusFilter.setData(data.getStatuses());
	},
	
	/**
	 * Common function to be called when the filters have been updated to update the 
	 * table view.
	 */
	onFiltersUpdated: function() {
		this.refreshTableRows();
	},
	
	refreshTableRows: function() {
		$('allCaseStudiesTable').getElements('tbody tr').fade('hide');
		$('allCaseStudiesTable').getElements('tbody tr').each(function(el, i){
			(function() {
				el.fade('show');
			}).delay(20*(i+6));
		}, this);
	},
	
	/**
	 * Shows the "All Case Studies" section. If the markup doesn't exist, it builds
	 * the markup. If it does exist, it simply displays it.
	 */
	showCaseStudies: function() {
		//Checks to see if case studies already built
		if ($$('#allCaseStudiesTable tbody tr').length > 0) {
			$('allCaseContent').fade('in');
			$('awardsContent').fade('out');
		}
		else {
			this.buildCaseStudies();
		}
	},
	
	/**
	 * Handles building the markup based off of the data in the CaseStudyData model.
	 */
	buildCaseStudies: function() {
		//Render out Case Studies Table
		var table = $('allCaseStudiesTable');
		var tr, td, data;
		
		this.cases.each(function(item, index, array){
			data = new wknyc.cms.caseStudies.CaseStudyData( item, item.gallery.media[index]);
			
			tr = new Element('tr');
			table.getElement('tbody').grab(tr);
			
			//Order
			td = new Element('td', {
					'html': '<span class="order">'+(index+1)+'</span><img src="'+data.getThumbnail('tiny')+'"/>'
			}).inject(tr);
			
			//Name
			td = new Element('td',  {'html': '<a href="#">'+data.getTitle()+'</a>', 'class': 'name'});
			td.appendText(data.getDescription()).inject(tr);
			
			//Client
			td = new Element('td');
			td.appendText(data.getClient()).inject(tr);
			
			//Published
			td = new Element('td');
			td.appendText(data.getPublishedDate()).inject(tr);
			
			//Updated
			td = new Element('td');
			td.appendText(data.getPublishedDate()).inject(tr);
			
			//Status
			td = new Element('td');
			td.appendText(data.getStatus()).inject(tr);
		});
		
		var sortTable = new SortingTable( 'allCaseStudiesTable', {
			zebra: false,
			defaultSort: 'thead th:first-child',
			onSorted: function(){
				this.refreshTableRows();
			}.bind(this)
		});
	},
	
	/**
	 * Shows the "Award Entries" section. If the markup doesn't exist, it builds
	 * the markup. If it does exist, it simply displays it.
	 */
	showAwardEntries: function() {
		$('allCaseContent').fade('out');
		$('awardsContent').fade('in');
	},
	
	/**
	 * Handles building the markup based off of the data in the CaseStudyData model.
	 */
	buildAwardEntries: function() {
		
	}
});