$(document).ready(function()
{
	$('.signup').click(function()
	{
		doSignup();
	});
});
function doSignup() {
	var lock = new Auth0Lock('${clientId}', '${clientDomain}', {
		auth: {
			redirectUrl: '${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, "")}' + '/callback',
			responseType: 'code',
			params: {
				state: '${state}',
                        // Learn about scopes: https://auth0.com/docs/scopes
                        scope: 'openid user_id name nickname email picture'
                    }
                }
            });
            // delay to allow welcome message..
            setTimeout(function () {
            	lock.show();
            }, 1500);
}