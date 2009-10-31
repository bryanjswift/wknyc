/**
 * @author joseph.schmitt
 */
wknyc.cms.caseStudies.AssetsTab = new Class({
	Implements: Options,
	
	options: {
	},
	
	//Class vars
	canvas: $empty,
	
	/**
	 * Class constructor
	 */
	initialize: function(canvas, options) {
		this.canvas = canvas;
		this.setupAssets();
	},
	
	setupAssets: function() {
		var assetsForm = this.canvas.getElement('#assetsForm');
		
		//Pre-rendered Capsules
		var capsule;
		assetsForm.getElements('fieldset.capsule').each(function(capsuleMarkup, index){
			capsule = new wknyc.cms.caseStudies.CaseStudyCapsule( capsuleMarkup, {isRequired: capsuleMarkup.hasClass('required')} );
		});
		
		//Add more button
		var addMoreButton = $('add_more_assets').addEvent('click', function(e){
			var newCapsule = new wknyc.cms.caseStudies.CaseStudyCapsule( $$('.templates fieldset.capsule:not(.required)')[0], {cloneMarkup: true} );
			newCapsule.inject(assetsForm);
			return false;
		});
	}
});