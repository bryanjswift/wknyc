/**
 * @author joseph.schmitt
 */
wknyc.site.work.WorkListing = new Class({
	Implements: Options,
	
	//Options
	options: {
		
	},
	
	//Class vars
	cases: {},
	curCases: {},
	curStartIndex: 0,
	clients: [],
	curFilter: 'All',
	type: 'blog',
	
	/**
	 * Class constructor.
	 */
	initialize: function(options) {
		this.setOptions(options);
		this.getPageData();
	},
	
	getPageData: function(){
		var req = new Request({ url:wknyc.swf.src.xml, onComplete: function(response){
			console.log(wknyc.swf.src.xml);
			wknyc.json = new MooTools.Utilities.XmlParser(response).json();
			this.refresh();
		}.bind(this) }).send();
	},
	
	refresh: function() {
		this.cases = wknyc.json.work.workcase;
		this.clients = wknyc.json.work.clients.client.reverse();
		this.cases = this.cases.reverse();
		
		this.buildFilters();
		this.setGrid(this.curFilter);
	},
	
	buildFilters: function() {
		var clientFilterMenu = new wknyc.common.gui.DropDown($('cssdropdown').getElement('li.headlink'), {
			body: 'ul.filterMenu',
			itemClass: 'filterMid cell',
			injectTarg: 'ul.filterMenu li.filterBottom',
			injectLoc: 'before'
		});
		
		clientFilterMenu.setData(this.clients);
		clientFilterMenu.addMouseEvents(
			//click
			function(item){
				this.doFilter(item.get('html'));
			}.bind(this) 
		);
		
		var allOption = $('cssdropdown').getElement('ul.filterMenu .filterMid.cell.all');
		allOption.addEvent('click', function(){
			this.doFilter(allOption.get('html'));
		}.bind(this));
	},
	
/*
	setFilters: function() {
		$$('.filterMenu').each(function(filterMenu){
			filterMenu.fade('hide');
			this.clients.each(function(item, index, array){
				var el = new Element('li', {'html':array[index].value, 'class':'filterMid'});
				el.addClass('cell');
				el.injectBefore(filterMenu.getElement('.filterBottom'));
			});
		}, this);
		
		var tabs = [$('clientFilterToggle')];
		tabs.each(function(el){
			el.addEvent('mouseenter', function(){
				el.getParent().getElement('.filterMenu').fade('in');
			});
			el.getParent().addEvent('mouseleave', function(){
				el.getParent().getElement('.filterMenu').fade('out');
			});
		});
		
		$$('.filterMenu .cell').each(function(el){
			el.setStyle('cursor', 'pointer');
			el.addEvent('mouseenter', function(){
				el.setStyle('color', '#96b79e');
			});
			el.addEvent('mouseleave', function(){
				el.setStyle('color', '#171716');
			});
			el.addEvent('click', function(){
				this.doFilter(el.get('html'));
			}.bind(this));
		}, this);
	},
*/

	doFilter: function(value){
		this.curFilter = value;
		
		$('workContentContainer').set('tween', {onComplete:function(){
			$('workContentContainer').fade('show');
			$$('#workContentContainer .caseContent').each(function(item){
				item.destroy();
			});
			
			if(this.type==="grid"){
				this.setGrid(value);
			}
			else{
				this.setBlog(value);
			}	
		}.bind(this)});
		
		$('workContentContainer').fade('out');
	},
	
	showPagination: function(cases){
		var html = '<a style="margin:0 2px 0 0;"></a><span></span>';
		
		$$('.workPager').each(function(el){
			var initHtml = '<span><a href="#" class="prevGreenBtn"><img src="library/images/btnsIcons/greenArrowLeft.png" alt="" width="6" height="7" style="margin-bottom:1px" /></a></span><span style="margin-left:5px;"><a href="#" class="nextGreenBtn"><img src="library/images/btnsIcons/greenArrowRight.png" alt="" width="5" height="7" style="margin-left:5px;margin-bottom:1px" /></a></span>';
			el.set('html', initHtml);
			el.setStyle('display', 'block');
			this.cases.each(function(item, index, array){
				var _html = index+1;
				if(_html < 10) _html = "0" + _html;
				if(index+1 !== array) _html = _html + " | "; 
				var link = new Element('a', {
					'styles' : {margin: '0 2px 0 0'},
					'html' : _html,
					'href' : 'javascript:workMain.pageTo('+ (index+1) +');'
				});	
				var anchor = el.getElement('.nextGreenBtn');
				link.injectBefore(anchor);
			});
			
		}, this);
	},
	
	hidePagination: function(){
		$$('.workPager').each(function(el){
			el.setStyle('display', 'none');
		});		
	},
	
	pageTo: function(set){
		$('workContentContainer').set('tween', {onComplete:function(){
			$('workContentContainer').fade('show');
			$$('#workContentContainer .caseContent').each(function(item){
				item.destroy();
			});
			
			if(this.type==="grid"){
				this.curStartIndex = 6 * (set-1);
				this.setGrid(this.curFilter, this.curStartIndex);
			}
			else{
				this.curStartIndex = 3 * (set-1);
				this.setBlog(this.curFilter, this.curStartIndex);	
			}	
		}.bind(this)});
		$('workContentContainer').fade('out');
	},
	
	pageNext: function(){},
	
	pagePrev: function(){},
	
	setToggles: function(type){
		if(type === 'grid'){
			this.type = 'grid';
			$('tabWorkGrid').addClass('on');
			$('tabWorkGrid').setStyle('cursor', 'default');
			$('tabWorkGrid').removeEvents();
			
			$('tabWorkBlog').removeClass('on');
			$('tabWorkBlog').setStyle('cursor', 'pointer');
			$('tabWorkBlog').addEvent('click', function(){
				$('workContentContainer').set('tween', {onComplete:function(){
					$('workContentContainer').fade('show');
					$$('#workContentContainer .caseContent').each(function(item){
						item.destroy();
					});
					
					this.setBlog(this.curFilter);
						
				}.bind(this)});
				
				$('workContentContainer').fade('out');
				
			}.bind(this));
		}
		else{
			this.type = 'blog';
			$('tabWorkBlog').addClass('on');
			$('tabWorkBlog').setStyle('cursor', 'default');
			$('tabWorkBlog').removeEvents();
			
			$('tabWorkGrid').removeClass('on');
			$('tabWorkGrid').setStyle('cursor', 'pointer');
			$('tabWorkGrid').addEvent('click', function(){
				$('workContentContainer').set('tween', {onComplete:function(){
					$('workContentContainer').fade('show');
					$$('#workContentContainer .caseContent').each(function(item){
						item.destroy();
					});
					
					this.setGrid(this.curFilter);
					
				}.bind(this)});
				
				$('workContentContainer').fade('out');
				
			}.bind(this));
		}
	},
	
	setGrid: function(filterValue, startIndex){
		this.setToggles('grid');
		var _delay = 100;
		var _html = $('gridMarkup').get('html');
		var el, link, desc, img;
		this.curCases = [];
		
		if(typeof filterValue !== 'undefined'){
			this.cases.each(function(caseStudy){
				if(caseStudy.client.value === filterValue){
					this.curCases.push(caseStudy);
				}
			}, this);
			
			if(filterValue === "All") this.curCases = this.cases;
		}
		else{
			this.curCases = this.cases;
		}

		if(typeof startIndex === 'undefined'){
			this.curStartIndex = 0;
		}
		
		if(this.curCases.length > 6){
			var pages = Math.ceil(this.curCases.length/6);
			this.showPagination(pages);
		}
		else{
			this.hidePagination();
		}
		
		this.curCases = this.curCases.slice(this.curStartIndex, this.curStartIndex+6);
		
		for(i=0;i<this.curCases.length;i++){
			_delay += 250;
			el = new Element('div', {'html':_html, 'class':'caseContent'});
			
			//set content
			el.getElement('.grayArrow').set('html', this.curCases[i].title.value);
			el.getElement('.workGridDescription').set('html', this.curCases[i].description.value);
			var _href = this.curCases[i].link.value + "?id=" + this.curCases[i].id.value;
			el.getElement('.plusBtn').set('href', _href);
			
			//get images
			var imgPath = _workRoot + this.curCases[i].id.value + "/images/";
			var gallery = [];
			if(this.curCases[i].gallery.media.length>1){
				gallery = this.curCases[i].gallery.media;
				gallery = gallery.reverse();
			}else{
				gallery.push(this.curCases[i].gallery.media);
			}
			
			
			//set main img
			img = el.getElement('.workGridThumb');
			img.fade('hide');
			img.set('src', imgPath + gallery[0].images.image[2].attributes.src);
			img.fade('in');
			el.getElement('.imgLink').set('href', _href);
			
			//set thumbnails
			link = el.getElement('.link');
			desc = el.getElement('.description');

			var tHtml, t, firstThumb;
			tHtml = $$('.initThumbRow1')[0].get('html');

			var row1 = gallery.slice(0,4);
			var step = _delay+100;
			for(ii=0;ii<row1.length;ii++){
				var index = ii;
				t = new Element('td', {'html':tHtml, 'class':'thumbnailContainer'});
				firstThumb = el.getElement('.initThumbRow1');
				t.injectBefore(firstThumb);
				var imgT = t.getElement('.workSqThumb');
				imgT.set('src', imgPath + row1[index].images.image[4].attributes.src);
				imgT.setStyle('opacity', .5);
				t.fade('hide');
				t.tween.delay(step, t,['opacity', 1]);
				t.addEvent('mouseenter', this.thumbOver.bindWithEvent(this, [imgT, el, row1[index].attributes.title, row1[index].attributes.description]));
				t.addEvent('mouseleave', this.thumbOut.bindWithEvent(this, [imgT, el]));
				t.addEvent('click', this.thumbClick.bindWithEvent(this, [t, img, imgPath + row1[index].images.image[2].attributes.src]));
				step += 100;
			}

			var row2 = gallery.slice(4,8);
			for(iii=0;iii<row2.length;iii++){
				var index = iii;
				t = new Element('td', {'html':tHtml, 'class':'thumbnailContainer'});
				firstThumb = el.getElement('.initThumbRow2');
				t.injectBefore(firstThumb);
				var imgT = t.getElement('.workSqThumb');
				imgT.set('src', imgPath + row2[index].images.image[4].attributes.src);
				imgT.setStyle('opacity', .5);
				t.fade('hide');
				t.tween.delay(step, t,['opacity', 1]);
				t.addEvent('mouseenter', this.thumbOver.bindWithEvent(this, [imgT, el, row2[index].attributes.title, row2[index].attributes.description]));
				t.addEvent('mouseleave', this.thumbOut.bindWithEvent(this, [imgT, el]));
				t.addEvent('click', this.thumbClick.bindWithEvent(this, [t, img, imgPath + row2[index].images.image[2].attributes.src]));
				step += 100;
			}
			
			el.injectBefore('workContentAnchor');
			
			el.fade('hide');
			el.set('tween', {duration:300, transition:Fx.Transitions.linear.easeIn});
			el.tween.delay(_delay, el,['opacity', 1]);
		}
	},
	
	setBlog: function(filterValue, startIndex){
		this.setToggles('blog');
		var _delay = 100;
		var el, link, desc, img;
		var _html = $('blogMarkup').get('html');
		
		this.curCases = [];
		if(typeof filterValue !== 'undefined'){
			this.cases.each(function(caseStudy){
				if(caseStudy.client.value === filterValue){
					this.curCases.push(caseStudy);
				}
			}.bind(this));
			if(filterValue === "All") this.curCases = this.cases;
		}else{
			this.curCases = this.cases;
		}
		
		if(typeof startIndex === 'undefined'){
			this.curStartIndex = 0;
		}
		
		if(this.curCases.length > 3){
			var pages = Math.ceil(this.curCases.length/3);
			this.showPagination(pages);
		}else{
			this.hidePagination();
		}
		
		this.curCases = this.curCases.slice(this.curStartIndex, this.curStartIndex+3);		
		
		for(i=0;i<this.curCases.length;i++){
			_delay += 250;
			
			el = new Element('div', {'html':_html, 'class':'caseContent'});
			
			//set content
			el.getElement('.grayArrow').set('html', this.curCases[i].title.value);
			el.getElement('.workBlogDescription').set('html', this.curCases[i].description.value);
			var _href = this.curCases[i].link.value + "?id=" + this.curCases[i].id.value;
			el.getElement('.plusBtn').set('href', _href)
			//el.getElement('.link').set('html', workMain.curCases[i].type.value);
			//el.getElement('.description').set('html', workMain.curCases[i].month.value + '.' + workMain.curCases[i].year.value);
			
			//get images
			var imgPath = _workRoot + this.curCases[i].id.value + "/images/";
			var gallery = [];
			if(this.curCases[i].gallery.media.length>1){
				gallery = this.curCases[i].gallery.media;
				gallery = gallery.reverse();
			}else{
				gallery.push(this.curCases[i].gallery.media);
			}
			
			//set main img
			img = el.getElement('.workBlogThumb');
			img.fade('hide');
			img.set('src', imgPath + gallery[0].images.image[1].attributes.src);
			img.fade('in');
			el.getElement('.imgLink').set('href', _href);
			
			//set thumbnails
			var tHtml, t, firstThumb;
			tHtml = $$('.initThumbRow1')[0].get('html');

			var row1 = gallery.slice(0,6);
			var step = _delay+100;
			for(ii=0;ii<row1.length;ii++){
				var index = ii;
				t = new Element('td', {'html':tHtml, 'class':'thumbnailContainer'});
				firstThumb = el.getElement('.initThumbRow1');
				t.injectBefore(firstThumb);
				var imgT = t.getElement('.workSqThumb');
				imgT.set('src', imgPath + row1[index].images.image[4].attributes.src);
				imgT.setStyle('opacity', .5);
				t.fade('hide');
				t.tween.delay(step, t,['opacity', 1]);
				t.addEvent('mouseenter', this.thumbOver.bindWithEvent(this, [imgT, el, row1[index].attributes.title, row1[index].attributes.description]));
				t.addEvent('mouseleave', this.thumbOut.bindWithEvent(this, [imgT, el, true]));
				t.addEvent('click', this.thumbClick.bindWithEvent(this, [t, img, imgPath + row1[index].images.image[1].attributes.src]));
				step += 100;
			}

			var row2 = gallery.slice(6,12);
			for(iii=0;iii<row2.length;iii++){
				var index = iii;
				t = new Element('td', {'html':tHtml, 'class':'thumbnailContainer'});
				firstThumb = el.getElement('.initThumbRow2');
				t.injectBefore(firstThumb);
				var imgT = t.getElement('.workSqThumb');
				imgT.set('src', imgPath + row2[index].images.image[4].attributes.src);
				imgT.setStyle('opacity', .5);
				t.fade('hide');
				t.tween.delay(step, t,['opacity', 1]);
				t.addEvent('mouseenter', this.thumbOver.bindWithEvent(this, [imgT, el, row2[index].attributes.title, row2[index].attributes.description]));
				t.addEvent('mouseleave', this.thumbOut.bindWithEvent(this, [imgT, el, true]));
				t.addEvent('click', this.thumbClick.bindWithEvent(this, [t, img, imgPath + row2[index].images.image[1].attributes.src]));
				step += 100;
			}
			
			el.injectBefore('workContentAnchor');
			el.fade('hide');
			el.set('tween', {duration:300, transition:Fx.Transitions.linear.easeIn});
			el.tween.delay(_delay, el,['opacity', 1]);
		}
	},
	
	thumbOver: function(e, thumb, target, link, desc) {
		(function(){
			target.getElement('.link').set('html', link);
			target.getElement('.description').set('html', desc);			
		}).delay(200);
		thumb.set('tween', {duration:300});
		thumb.tween('opacity', 1);
	},
	
	thumbOut: function(e, thumb, target, blogView) {
		if(!blogView){
			target.getElement('.link').set('html', '');
			target.getElement('.description').set('html', '');
		}
		thumb.tween('opacity', .5);		
	},
	
	thumbClick: function(e, thumb, target, src) {
		target.fade('hide');
		target.set('src', src);
		target.fade('in');
	}
});
