/**
 * @author joseph.schmitt
 */
wknyc.cms.newCaseStudy.AssetsTab = new Class({
	Extends: wknyc.cms.newCaseStudy.BasicCaseStudyTab,
	
	//Class vars
	
	/**
	 * Class constructor
	 */
	initialize: function(canvas) {
		this.parent(canvas);
	},
	
	setupAssets: function() {
		this.parent();
		//Pre-rendered Capsules
		var capsule;
		this.form.getElements('fieldset.capsule').each(function(capsuleMarkup, index){
			capsule = new wknyc.cms.newCaseStudy.CaseStudyCapsule( capsuleMarkup, {isRequired: capsuleMarkup.hasClass('required')} );
		});
		
		//Add more button
		var addMoreButton = $('add_more_assets').addEvent('click', function(e){
			var newCapsule = new wknyc.cms.newCaseStudy.CaseStudyCapsule( $$('.templates fieldset.capsule:not(.required)')[0], {cloneMarkup: true} );
			newCapsule.inject(this.form);
			return false;
		});
	}
});