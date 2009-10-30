/**
* alex.nguyen@wk.com
*/
var key = "ABQIAAAAPTnXcVqfUi1sP5f6ICVgmxQc3j_nh1j2x--AEeCbdrwZ4YHIyBRCF9nK0yL6ZrPKbZn8ns-MYrEvrA";
function pageInit() {
    wknyc.section.currentSection = "post";
	$('lightboxImg').setStyles({display: 'block', padding: '20px'});
};

var blog;
google.load("feeds", "1");
 
function initialize() {
	var feed = new google.feeds.Feed("http://blog.wknyc.com/wp-rss2.php");
	var _html = $('blogPostMarkup').get('html');
	feed.load(function(result) {
		if (!result.error) {
			blog = result;
			_delay = 100;
			for (var i=0; i<blog.feed.entries.length; i++) {
				_delay += 250;
				var el = new Element('div', {'html':_html});
				
				el.getElement('h3').set('html', blog.feed.entries[i].title);
				el.getElement('.content').set('html', blog.feed.entries[i].content);
				el.getElement('.author').set('html', blog.feed.entries[i].author);
				el.getElement('.postDate').set('html', blog.feed.entries[i].publishedDate);
				
				
				
				el.injectBefore('blogPostMarkup');
				el.fade('hide');
				el.set('tween', {duration:300, transition:Fx.Transitions.linear.easeIn});
				el.tween.delay(_delay, el,['opacity', 1]);
			}
		}
	});
};



google.setOnLoadCallback(initialize);