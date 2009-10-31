/**
 * alex.nguyen@wk.com
 */
 
wknyc.utils.share = {
	init: function() {
		$('shareOverlay').setStyle('top',(window.getHeight()/2)-($('shareOverlay').offsetHeight/2)+window.getScrollTop());
		$('shareOverlay').setStyle('left',(window.getWidth()/2)-($('shareOverlay').offsetWidth/2));
		
		$('shareOverlay').getElement('#embed').getElement('textarea').set('value', window.location);
		$('shareOverlay').getElement('#email').getElement('#name').set('value', '* Your Email Address');
		$('shareOverlay').getElement('#email').getElement('#address').set('value', '* Friend\'s Email Address');
		$('shareOverlay').getElement('#email').getElement('textarea').set('value', 'Your Message');
		
		$('shareOverlay').getElement('#close').setStyle('cursor', 'pointer');
		$('shareOverlay').getElement('#close').addEvent('click', function(event){
			$('shareOverlay').fade('out');
		});
		
		$('shareOverlay').fade('hide');
		$('shareOverlay').fade('in');
	},
	email: function() {}
}

wknyc.utils.lightBox ={
	dimensions: {},
	content: {},
	curObj: {},
	curArray: [],
	curIndex: 0,
	pre: function() {
		$('modal').fade('hide');
		$('modal').setStyle('visibility', 'visible');
	},
	init: function(c) {
		wknyc.utils.lightBox.content = c;
	},
	preload: function(type, obj) {
		if(type === "media"){
			//load photo or video
			curObj = obj;
			curArray = obj.imgArray;
			curIndex = obj.index;
			this.type = "media";
			wknyc.utils.lightBox.content = "<img id='loadImg' src='' alt=''/>";
			var src = obj.imgPath + obj.imgArray[obj.index].images.image[0].attributes.src;
			$('lightboxImg').setStyles({'background-color':'transparent', 'padding':0});
			wknyc.utils.lightBox.loadContent(src);
			
			//activate nav
			var prev = $('lightboxPager').getElement('.grayArrowLeftTrans');
			var next = $('lightboxPager').getElement('.grayArrowRightTrans');
			prev.addEvent('click', wknyc.utils.lightBox.imgNav.bindWithEvent(this, [t, "prev"]));
			next.addEvent('click', wknyc.utils.lightBox.imgNav.bindWithEvent(this, [t, "next"]));				
		}else{
			//load html
			this.type = "html";
			$('lightboxNfo').setStyle('margin-bottom', '30px');
			wknyc.utils.lightBox.scaleLightBox(580, 600);
		}
		
		$('modal').setStyles({width:'100%', height:'100%'});
		$('modal').set('tween', {duration:150, transition:Fx.Transitions.linear.easeIn, onComplete:function(){
			wknyc.swf.pause();
		}});
		$('modal').tween('opacity', 0.9);
		$('modal').addEvent('click', function(){
			wknyc.utils.lightBox.kill();
		});	
	},
	imgNav: function(el, button, direction) {
		if(direction === "prev"){
			curIndex = curIndex - 1;
		}else{
			curIndex = curIndex + 1;
		}
		if(curIndex === -1) curIndex = curArray.length-1;
		if(curIndex > curArray.length-1) curIndex = 0;
		 
		var src = curObj.imgPath + curArray[curIndex].images.image[0].attributes.src;
		wknyc.utils.lightBox.loadContent(src);
	},
	fadeOut: function() {
		$('modal').set('tween', {duration:75, transition:Fx.Transitions.linear.easeOut, onComplete:function(){
			$('modal').setStyles({width:0, height:0});
		}});
		$('modal').tween('opacity', 0);
	},
	scaleLightBox: function(w,h) {
		$('lightbox').setStyle('width', w+20);
		wknyc.utils.lightBox.centerLightbox();
		$('lightbox').setStyle('width', 0);
		$('lightbox').set('morph', {duration:500, transition:Fx.Transitions.Back.easeOut, onComplete:function(){
			wknyc.utils.lightBox.loadContent('', true);
		}});
		$('lightbox').morph({width: w, height:h, opacity: 1});
	},
	centerLightbox: function() {
		lH = $('lightbox').offsetHeight;
		var wH = (window.getHeight()/2)-(lH/2)+window.getScrollTop();
		if(this.type === "html") $('lightbox').setStyle('top', window.getScrollTop()+50);
		$('lightbox').setStyle('top', wH);
		$('lightbox').setStyle('left',(window.getWidth()/2)-($('lightbox').offsetWidth/2));
	},
	loadContent: function(imgSrc, isHTML){
		if($('lightboxContent') != null){
			$('lightboxContent').destroy();	
		}
		var e = new Element('div', {'id':'lightboxContent', 'html': wknyc.utils.lightBox.content, 'styles': {'visibility':'visible'}});
		e.inject($('lightboxImg'));		
		if(isHTML){
			$('lightboxContent').set('tween', {duration:0});
			$('lightboxContent').fade('hide');
			wknyc.utils.lightBox.fadeIn(true);	
		}else{
			$('lightboxTitle').getElement('h3').set('html', curArray[curIndex].attributes.client);
			$('lightboxDescription').getElement('.link').set('html', curArray[curIndex].attributes.title);
			$('lightboxDescription').getElement('.description').set('html', curArray[curIndex].attributes.description);			
			var	imgLoad = new Image();
			imgLoad.onload = function(){
				var minWidth = window.getWidth() - 200;
				var minHeight = window.getHeight() - 200;
				var ratio;
				$('loadImg').src = imgSrc;
				
				if(imgLoad.width > imgLoad.height){
					if(imgLoad.width > minWidth){
						ratio = minWidth/imgLoad.width;
						$('loadImg').width = $('loadImg').width*ratio;
					}
				}else{
					if(imgLoad.height > minHeight){
						ratio = minHeight/imgLoad.height;
						$('loadImg').height = $('loadImg').height*ratio;
					}						
				}
				
				$('lightboxContent').set('tween', {duration:0});
				$('lightboxContent').fade('hide');
				
				wknyc.utils.lightBox.fadeIn();
				
				$('current').set('html', curIndex+1);
				$('total').set('html', curArray.length);
			}
			imgLoad.src = imgSrc;
		}
	},
	fadeIn: function(isHTML) {
		(function(){
			wknyc.utils.lightBox.centerLightbox();
			$('lightbox').set('tween', {duration:500, onComplete:function(){
				if($('lightboxContent')!=null){
					$('lightboxContent').set('tween', {duration:250});
					$('lightboxContent').fade('in');	
				}
			}});
			$('lightbox').fade('in');	
		}).delay(100);
	},
	kill: function() {
		$('lightboxContent').fade('out');
		$('lightbox').set('morph', {duration:350, transition:Fx.Transitions.Back.easeIn, onComplete:function(){
			wknyc.utils.lightBox.fadeOut();
			$('lightboxContent').destroy();
		}});		
		$('lightbox').morph({opacity:0});
	}
};

window.addEvent('resize', function(){
	wknyc.utils.lightBox.centerLightbox();
});

window.addEvent('scroll',function(){
	var d = window.getScrollTop();
	$('modal').setStyle('top',d);
	//$('lightbox').tween('top',d+100);
});