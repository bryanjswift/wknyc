/**
 * alex.nguyen@wk.com
 */
 
var _transition = Fx.Transitions.linear.easeIn;
var _logoRightMargin = 120;
var _logoHeight = 60;
var _mainMargin = 0;
var _w, _h, _newH, _xPos, _duration;

function init() {
	Cufon.replace($$('p'));
	
	$('footer').fade('hide');
	$('logo').fade('hide');
	$('main').fade('hide');
	$('contact').fade('hide');
	$('description').fade('hide');
	
	$('email').setStyle('visibility', 'visible');
	$$('.splashImg').setStyle('visibility', 'visible');
	$$('p').setStyle('visibility', 'visible');
}

function setSize() {
	if(Browser.Engine.trident){
		_w = document.documentElement.clientWidth;
		_h = document.documentElement.clientHeight;
	}else{
		_w = window.innerWidth;
		_h = window.innerHeight;
	}

	_xPos = _w/7;
	_newH = _h/2 - 1;
	
	//window.console.log("ratio : " + _w/_h);
}

function reposition(initial) {
	if(initial){
		_duration = '0';
	}else{
		_duration = '350';
	}
	
	$('footer').setStyle('width', 0);
	
	setSize();
	
	var imgDivArray = $$(".splashImg");
	var imgArray = $$("img");
	
	for(var i=0; i<imgDivArray.length; i++){
		imgDivArray[i].set('morph', {duration: _duration, transition: _transition});
		imgDivArray[i].morph({width: _newH, height: _newH});
		
		imgArray[i].set('morph', {duration: _duration, transition: _transition});
		imgArray[i].morph({width: _newH, height: _newH});
	}
	
	var rows = $$('.row');
	if((_newH*2 + parseInt($('logo').getStyle('left')) + parseInt($('logo').getStyle('width'))) > _w){
		for(var i=0; i<rows.length; i++){
			rows[i].setStyle('width', _newH*2 + 1);
		}
	}else{
		for(var i=0; i<rows.length; i++){
			rows[i].setStyle('width', null);
		}		
	}
	
	if(_xPos<128) {_xPos=128;}
	
	if(initial) {
		$('main').setStyle('left', _xPos);
		
		var logoX = _xPos - _logoRightMargin;
		if(logoX < 10){
			logoX = 10 + logoX;
		}	
		$('logo').setStyle('left', logoX);
		$('logo').setStyle('top', (_newH - _logoHeight/2));
		$('description').setStyle('top', _newH - 11);	
		setTimeout(show, 550);
	}else{
		$('main').set('morph', {duration: _duration, transition: _transition, onComplete:function(){delayShow();}});
		$('main').morph({left: _xPos});
	}
}

function setFooter() {
	$('footer').set('morph', {duration: 200, transition: _transition});
	var fullWidth = parseInt($('description').getStyle('left')) + parseInt($('description').getStyle('width'));
	if(fullWidth<_w) {
		fullWidth = _w;
	}
	(function(){ $('footer').morph({width: fullWidth}); $('footer').fade('in'); }).delay(100);
}

function show() {
	var descX = parseInt($('description').getStyle('left')) + parseInt($('description').getStyle('width')) + 15;
	if(descX + 20 > _w){
		//descX = _w - 20 - parseInt($('description').getStyle('width'));
	}
	
	var rightX = parseInt($('main').getStyle('left')) + parseInt($('main').getStyle('width'))+15;
	var rightXAgg = rightX + parseInt($('description').getStyle('width'));
	
	if(rightXAgg + 20 > _w){
		//rightX = _w - 20 - parseInt($('description').getStyle('width'));
	}	
	
	
	$('contact').setStyle('left', rightX);
	$('description').setStyle('left', rightX);
	$('logo').fade('in');
	(function(){ $('main').fade('in'); }).delay(500);
	(function(){ $('contact').fade('in'); }).delay(700);
	(function(){ $('description').fade('in'); }).delay(700);
	(function(){ setFooter(); }).delay(500);
}

function delayShow() {
	$('logo').set('morph', {duration: _duration, transition: _transition});
	var logoX = _xPos - _logoRightMargin;
	if(logoX < 10){
		logoX = 10 + logoX;
	}	
	$('logo').morph({left: logoX, top: (_newH - _logoHeight/2)});
	$('contact').set('morph', {duration: _duration, transition: _transition});
	$('description').set('morph', {duration: _duration, transition: _transition});
	
	var rightX = parseInt($('main').getStyle('left')) + parseInt($('main').getStyle('width'))+15;
	var rightXAgg = rightX + parseInt($('description').getStyle('width'));
	
	if(rightXAgg + 20 > _w){
		//rightX = _w - 20 - parseInt($('description').getStyle('width'));
	}	
	
	$('contact').morph({left: rightX, top: 0});
	$('description').morph({left: rightX, top: _newH - 11});
	(function(){ setFooter(); }).delay(500);
}

function delayResize() {
	$('footer').fade('out');
	(function(){ reposition(); }).delay(350);
}

 
window.addEvent("domready", init);
window.addEvent("load", function(){reposition(true);});
window.addEvent("resize", delayResize);