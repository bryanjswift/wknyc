/**
 * @author joseph.schmitt
 */
wknyc.site.work.WorkCaseStudy = new Class({
	//Class vars
	imgPath: '',
	gallery: [],
	curIndex: 0,
	data: {},
	
	/**
	 * Constructor
	 */
	initialize: function() {
		var id = window.location.search.split("?id=");
	    wknyc.section.currentSection = "work";
	    wknyc.swf.src.flash = "library/flash/VideoPlayer_668x418.swf";
	    wknyc.swf.src.xml = "library/data/work/" + id[1] + ".xml";
	    wknyc.swf.src.assets = "library/work/" + id[1] + "/";
		
		$('lightboxImg').setStyles({display: 'block', padding: '20px'});
		wknyc.swf.init(wknyc.swf.src.flash, 'video', 668, 418, wknyc.swf.src.xml, wknyc.swf.src.assets);
		wknyc.swf.embed();
		
		this.getPageData();
	},
	
	
	//Methods
	getPageData: function(){
		var req = new Request({url:wknyc.swf.src.xml, onComplete: function(response){
			wknyc.json = new MooTools.Utilities.XmlParser(response).json();
			this.refresh();
		}.bind(this)}).send();	
	},
	
	refresh: function(){
		this.data = wknyc.json.workcase;
		this.setContent();
		this.setInlineGallery();
	},
	
	setContent: function(){
		$('projectLabel').set('html', this.data.title.value);
		$('heroLabel').set('html', this.data.client.value);
		$('heroType').set('html', this.data.client.value);
	},
	
	setInlineGallery: function(){
		this.imgPath = _workRoot + this.data.id.value + "/images/";
		
		if(this.data.gallery.media.length>1){
			gallery = this.data.gallery.media;
			gallery = gallery.reverse();
		}
		else{
			gallery.push(this.data.gallery.media);
		}
		
		var inlineRow = [];
		
		if(gallery.length>5){
			var curSet = "1 - 5";
			this.activatePager(curSet);
			inlineRow = gallery.slice(0,5);
		}
		else{
			inlineRow = gallery;
		}
		
		this.renderInlineGallery(inlineRow);
	},
	
	activatePager: function(curSet) {
		$('pagerWide').setStyle('display', 'block');
		$('pagerWide').getElement('.currentSet').set('html', curSet);
		$('pagerWide').getElement('.totalCount').set('html', gallery.length);
		$('pagerWide').getElement('.grayArrowLeft').setStyle('cursor', 'pointer');
		$('pagerWide').getElement('.grayArrowRight').setStyle('cursor', 'pointer');
		$('pagerWide').getElement('.grayArrowRight').addEvent('click', this.onGalleryNext.bindWithEvent(this));
		$('pagerWide').getElement('.grayArrowLeft').addEvent('click', this.onGalleryPrev.bindWithEvent(this));
	},
	
	renderInlineGallery: function(inlineRow){
		$$('.lightBoxThumb').each(function(el){
			el.destroy();
		});
		var _init = '<td id="imgGridBottom"></td>';
		$$('tr').set('html', _init);
		var _html = '<img src="" alt="" width="104" height="92" class="workCaseThumb" /><div class="linkDescriptionContainer" style="margin-left:3px;"><p class="link"><a></a></p><p class="description"></p></div>';
		var step = 250;
		for(i=0;i<inlineRow.length;i++){
			var index = i;
			t = new Element('td', {'html':_html, 'class':'lightBoxThumb'});
			t.injectBefore('imgGridBottom');
			t.getElement('.workCaseThumb').set('src', this.imgPath + inlineRow[index].images.image[3].attributes.src);
			t.getElement('.link').getElement('a').set('html', inlineRow[index].attributes.title);
			t.getElement('.description').set('html', inlineRow[index].attributes.description);
			t.fade('hide');
			t.tween.delay(step, t,['opacity', 1]);
			t.addEvent('click', this.thumbClick.bindWithEvent(this, [t, index]));
			step += 250;
		}
	},
	
	thumbClick: function(e, thumb, index) {
		var client = gallery[index].attributes.client;
		var title = gallery[index].attributes.title;
		var desc = gallery[index].attributes.description;
		var obj = {
			'imgPath':imgPath,
			'imgArray':gallery,
			'index':index + this.curIndex,
			'client':client,
			'title':title,
			'desc':desc
		};
		
		wknyc.utils.lightBox.preload("media", obj);	
	},
	
	onGalleryNext: function() {
		this.curIndex = this.curIndex+5;
		if(this.curIndex > gallery.length-1) this.curIndex = 0;
		var curSet = this.curIndex+1 + " - " + gallery.length;
		$('pagerWide').getElement('.currentSet').set('html', curSet);
		var row = gallery.slice(this.curIndex, this.curIndex+5); 
		this.renderInlineGallery(row);
	},
	
	onGalleryPrev: function() {
		this.curIndex = this.curIndex-5;
		if(this.curIndex < 0) this.curIndex = gallery.length-1;
		var curSet = this.curIndex+1 + " - " + gallery.length;
		$('pagerWide').getElement('.currentSet').set('html', curSet);		
		var row = gallery.slice(this.curIndex, this.curIndex+5); 
		this.renderInlineGallery(row);		
	}
});
