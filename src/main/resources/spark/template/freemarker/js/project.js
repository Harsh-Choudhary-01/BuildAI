$(document).ready(function()
{
	$(window).bind('hashchange', function() { processHash(); });
});

function processHash()
{
	var hash = window.location.hash;
	if(hash === '#new')
	{
		$('.listHeader').fadeOut();
		$('.newHeader').fadeIn();
		$('div.projectList').fadeOut("fast");
		$('.newProjectForm').fadeIn();
	}
}