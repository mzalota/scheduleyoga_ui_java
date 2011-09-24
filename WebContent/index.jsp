<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Find schedule of yoga classes in your area</title>

<link href="css/main.css" rel="stylesheet" type="text/css"/>

<style type="text/css">
</style>

</head>
<body>
	<div class="wrapper">
		<%@ include file="../templates/header.html"%>
		<h1>See yoga schedule in your area</h1>


		<div class="topPanel">
			<h2>Featured cities:</h2>
			<ul>
				<li>
				<span class="carrot">›&nbsp;</span>
				<a href="/new-york/new-york-city.html"> New York City</a>
				</li>
			</ul>
		</div>
		<div class="mainPanel">
			<div id="left_panel">
				<%@ include file="../templates/left-panel.html"%>
			</div>
			
			<div id="right_panel">	
				<h2>Yoga Classes by State:</h2>
				<img src="/images/origs/stock-photo-14820450-correcting-exersize.jpg" style="float: right; margin: 4px" />
				<ul>
					<li><span class="carrot">›&nbsp;</span><a href="/alabama/">Alabama</a>
					</li>
					<li><span class="carrot">›&nbsp;</span><a href="/alaska/">Alaska</a>
					</li>
					<li><span class="carrot">›&nbsp;</span><a href="/new-york/">New York</a>
					</li>
				</ul>
			</div>
			<div style="clear: both"></div>
		</div>
	</div>
	<div style="clear: both"> </div>
	<div>	
		<%@ include file="../templates/footer.html"%>
	</div>
</body>
</html>