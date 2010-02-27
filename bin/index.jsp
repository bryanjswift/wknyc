<jsp:include page="/site/includes/header.html" />
<jsp:include page="/site/homeTemplate.html" />
<jsp:include page="/site/includes/footer.html" />
	<script type="text/javascript" src="http://www.google.com/jsapi?key=ABQIAAAAPTnXcVqfUi1sP5f6ICVgmxQc3j_nh1j2x--AEeCbdrwZ4YHIyBRCF9nK0yL6ZrPKbZn8ns-MYrEvrA"></script>
	<script type="text/javascript" src="/site/library/js/homeFeed.js"></script>
	<script type="text/javascript" src="/site/library/js/wknyc/site/home/Home.js"></script>
	<script type="text/javascript">
		var home;
		function pageInit() {
			wknyc.staticWorkListings = "/site/library/data/work.js";
			wknyc.cmsWorkListings = "/casestudy/list/json";
			home = new wknyc.site.Home();
		}
	</script>
</body>
</html>