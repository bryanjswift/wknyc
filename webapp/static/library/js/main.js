/**
 * alex.nguyen@wk.com
 */

var _siteRoot = "/wknyc/bin/site/";
var _workRoot = _siteRoot + "library/work/"
var _currentSection, _duration, _transition;

//Main namespace
var wknyc = {
	section: {
		currentSection: ""
	},
	colors: {
		inactive: "#7e7e7e",
		active: "#3e3e3e"
	},
	utils: {},
	gui: {},
	cms: {
		caseStudies:{}
	},
	site: {
		work:{}
	},
	common: {
		gui:{}
	},
	swf: {},
	globals: {},
	fx: {
		duration: 350,
		transition: Fx.Transitions.linear.easeIn	
	}
};

wknyc.utils.PngFix = {
	BLANK_GIF_PATH: '../img/blank.gif',
	fixBg: function(el) {
		if (Browser.Engine.trident4) {
			var elSrc = el.getStyle('backgroundImage');
			var bgSrc, alphaFilter, method;
			if (elSrc.contains('.png') && !el.retrieve('pngFixed')) {
				try {
					bgSrc = elSrc.split('url("')[1].split('")')[0];
				} catch (ex) {
					bgSrc = elSrc.split('url(')[1].split(')')[0];
				}
				el.setStyle('backgroundImage', "url(" + wknyc.utils.PngFix.BLANK_GIF_PATH + ")");
				method = 'scale';
				if (el.getStyle('background-repeat') == 'no-repeat') {
					method = 'crop';
				}
				el.style.filter = 'progid:DXImageTransform.Microsoft.AlphaImageLoader(src="' + bgSrc + '", sizingMethod="' + method + '")';
				el.store('pngFixed',true);
			}
		}
	},
	fixSrc: function(el,setDimensions) {
		if (Browser.Engine.trident4) {
			var imgSrc = el.getProperty('src');
			if (imgSrc.contains('.png') && !el.retrieve('pngFixed')) {
				method = "image";
				if (el.retrieve('method')) {
					method = el.retrieve('method');
				}
				el.set('src',wknyc.utils.PngFix.BLANK_GIF_PATH);
				el.setStyles({
					backgroundImage: 'none',
					filter: 'progid:DXImageTransform.Microsoft.AlphaImageLoader(src="' + imgSrc + '", sizingMethod="'+method+'")'
				});
				if (!setDimensions) { return; }
				var img = new Image();
				img.onload = function(loaded) { el.set({width:loaded.width,height:loaded.height}); };
				img.src = imgSrc;
			}
		}
	},
	fixClass: function(clazz, filter) {
		var elements;
		if ($defined(filter) && $type(filter) === 'element') {
			elements = filter.getElements('.' + clazz);
		} else {
			elements = $$('.' + clazz);
		}
		var i = elements.length;
		var el;
		if (i > 0) {
			do {
				i = i - 1;
				el = elements[i];
				wknyc.utils.PngFix.fixElement(el);
			} while (i);
		}
	},
	fixElement: function(el) {
		switch (el.get('tag')) {
			case 'img': wknyc.utils.PngFix.fixSrc(el); break;
			default: wknyc.utils.PngFix.fixBg(el); break;
		}
	},
	fixElements: function(elements) {
		var i = elements.length;
		if (!i) { return; }
		do {
			i = i - 1;
			wknyc.utils.PngFix.fixElement(elements[i]);
		} while (i);
	}
};

wknyc.utils.renderFonts = function() {
	Cufon.replace($$('.univers'));
}

wknyc.utils.initMenu = function() {
	//hide submenus
	$$('.subMenu').fade('hide');
	//enable menu states
	$$('#navList li').addEvent('mouseover', function(){
		if(this!=$(_currentSection)){
			this.getElement('a').set('tween', {duration:200, transition:Fx.Transitions.linear.easeIn});
			this.getElement('a').tween('opacity', 0.4);
		}
		if(this.hasClass('label')){
			if(this.getElement('ul')!=null){
				this.getElement('ul').set('tween', {duration:200, transition:Fx.Transitions.linear.easeIn});
				this.getElement('ul').fade('in');
			}
		}
	});
	$$('#navList li').addEvent('mouseout', function(){
		this.getElement('a').set('tween', {duration:200, transition:Fx.Transitions.linear.easeOut});
		this.getElement('a').tween('opacity', 1);
		if(this.hasClass('label')){
			if(this.getElement('ul')!=null){
				this.getElement('ul').set('tween', {duration:200, transition:Fx.Transitions.linear.easeOut});
				this.getElement('ul').fade('out');
			}
		}
	});
	$$('navList li').set('href', function(){
		_siteRoot + this.get('href');
	});
	//highlight current section
	$(_currentSection).getElement('a').setStyle('color', _active);
};

/**
 * Checks for the existence of the global console object and the log method.
 * Should be used instead of console.log so that IE doesn't throw errors when
 * it doesn't find console.
 * @param {Object} message - Message to be sent to console.log
 */
wknyc.utils.log = function(message) {
	if( console && console.log ) console.log(message);
	else return false;
}

wknyc.swf = {
	obj: {},
	src: {},
	init: function(_url, _id, _w, _h, _config, _assets) {
		wknyc.swf.obj = new Swiff(_url, {
			id: _id,
			width: _w,
			height: _h,
			params: {menu:'false', scale:'noscale', salign:'LT', allowScriptAccess:'always', bgcolor:'#FFFFFF',wmode:'opaque', wMode:'opaque', allowFullScreen:'true'},
			vars: {configPath: _config, assetsPath: _assets}
		});
	},
	embed: function() {
		if($('swf')){
			wknyc.swf.obj.inject($('swf'));
		}
	},
	pause: function() {
		if(typeof wknyc.swf.obj.object !== 'undefined') wknyc.swf.obj.remote('pause');
	}
}

function init() {
	_currentSection = wknyc.section.currentSection;
	_duration = wknyc.fx.duration;
	_transition = wknyc.fx.transition;
	_inactive = wknyc.colors.inactive;
	_active = wknyc.colors.active;
	
	wknyc.utils.initMenu();
	wknyc.utils.renderFonts();
	wknyc.utils.lightBox.pre();
}

window.addEvent("domready", function(){
	pageInit();
	init();
});
window.addEvent("load", function(){
	
});
window.addEvent("resize", function(){
	
});