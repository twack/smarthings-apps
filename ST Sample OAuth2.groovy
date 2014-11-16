/**
 *  OAuth2 Credentials Service Manager
 *
 *  Author: todd@wackford.net
 *  Date: 2014-03-05
 */

// Automatically generated. Make future change here.
definition(
    name: "OAuth2 Credentials Service Manager",
    namespace: "YourNameSpaceGoesHere",
    author: "barney@rubble.com",
    description: "Connect your Cloud Devices to SmartThings using OAuth2 credential methods.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    oauth: true)

preferences {
    page(name: "Credentials", title: "Fetch OAuth2 Credentials", content: "authPage", install: false)
}

mappings {
	path("/receivedToken") 	{ action: [ POST: "receivedToken", GET: "receivedToken"] }
	path("/receiveToken") 	{ action: [ POST: "receiveToken", GET: "receiveToken"] }
        
    // This is where you get call backs and process posted events from the vendor. This code will get errors until
    // you define a legit endpoint (below). Error example "Service Manager DOES NOT RESPOND TO UPDATED HANDLER"
    
	//path("/vendorEvents") 	{ action: [ POST: "vendorPostEventsHandler", GET: "vendorGetEventsHandler"] }
}

private getVendorName() 	{ "Super Widgets" }
private getVendorAuthPath()	{ "https://superapi.superwidgets.com/oauth2/authorize?" }
private getVendorTokenPath(){ "https://superapi.superwidgets.com/oauth2/token?" }
private getVendorIcon()		{ "https://s3.amazonaws.com/smartthings-device-icons/custom/super-widgets/beertap@2x.png" }
private getClientId() 		{ "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" } 
private getClientSecret() 	{ "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy" }

private getServerUrl() 		{ return "https://graph.api.smartthings.com" }


def authPage() {
	log.debug "In authPage"
    
    def description = null
  
    if (state.vendorAccessToken == null) {   
        log.debug "About to create access token."
		
        createAccessToken()
        description = "Tap to enter Credentials."
		
        def redirectUrl = oauthInitUrl()
		
        
        return dynamicPage(name: "Credentials", title: "Authorize Connection", nextPage: null, uninstall: false, install:false) {
               section { href url:redirectUrl, style:"embedded", required:false, title:"Connect to ${getVendorName()}:", description:description }
        }
    } else {
    	description = "Press 'Done' to proceed" 
 		
		return dynamicPage(name: "Credentials", title: "Credentials Accepted!", nextPage: null, uninstall: true, install:true) {
               section { href url: buildRedirectUrl("receivedToken"), style:"embedded", required:false, title:"${getVendorName()} is now connected to SmartThings!", description:description }
        }
    }
}

def oauthInitUrl() {
	log.debug "In oauthInitUrl"
    
	/* OAuth Step 1: Request access code with our client ID */

    state.oauthInitState = UUID.randomUUID().toString()
    
    def oauthParams = [ response_type: "code", 
                        client_id: getClientId(),
                        state: state.oauthInitState,
                        redirect_uri: buildRedirectUrl("receiveToken") ]
	
    return getVendorAuthPath() + toQueryString(oauthParams)
}

def buildRedirectUrl(endPoint) {
	log.debug "In buildRedirectUrl"

    return getServerUrl() + "/api/token/${state.accessToken}/smartapps/installations/${app.id}/${endPoint}"
}

def receiveToken() {
	log.debug "In receiveToken"
    
    def oauthParams = [ client_secret: getClientSecret(),
    				    grant_type: "authorization_code", 
                        code: params.code ]
                        
	def tokenUrl = getVendorTokenPath() + toQueryString(oauthParams)
	def params = [
	  uri: tokenUrl,
	]
     
    /* OAuth Step 2: Request access token with our client Secret and OAuth "Code" */
	httpPost(params) { response -> 
    
    	def data = response.data.data
        
    	state.vendorRefreshToken = data.refresh_token //these may need to be adjusted depending on depth of returned data
        state.vendorAccessToken = data.access_token
	}
     
    if ( !state.vendorAccessToken ) {  //We didn't get an access token, bail on install
    	return
    }
    
    /* OAuth Step 3: Use the access token to call into the vendor API throughout your code using state.vendorAccessToken. */
       
    def html = """
        <!DOCTYPE html>
        <html>
        <head>
        <meta name=viewport content="width=300px, height=100%">
        <title>${getVendorName()} Connection</title>
        <style type="text/css">
            @font-face {
                font-family: 'Swiss 721 W01 Thin';
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot');
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot?#iefix') format('embedded-opentype'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.woff') format('woff'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.ttf') format('truetype'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.svg#swis721_th_btthin') format('svg');
                font-weight: normal;
                font-style: normal;
            }
            @font-face {
                font-family: 'Swiss 721 W01 Light';
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot');
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot?#iefix') format('embedded-opentype'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.woff') format('woff'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.ttf') format('truetype'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.svg#swis721_lt_btlight') format('svg');
                font-weight: normal;
                font-style: normal;
            }
            .container {
                width: 560px;
                padding: 0px;
                /*background: #eee;*/
                text-align: center;
            }
            img {
                vertical-align: middle;
            }
            img:nth-child(2) {
                margin: 0 30px;
            }
            p {
                font-size: 2.2em;
                font-family: 'Swiss 721 W01 Thin';
                text-align: center;
                color: #666666;
                padding: 0 40px;
                margin-bottom: 0;
            }
        /*
            p:last-child {
                margin-top: 0px;
            }
        */
            span {
                font-family: 'Swiss 721 W01 Light';
            }
        </style>
        </head>
        <body>
            <div class="container">
                <img src=""" + getVendorIcon() + """ alt="Vendor icon" />
                <img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/connected-device-icn%402x.png" alt="connected device icon" />
                <img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/st-logo%402x.png" alt="SmartThings logo" />
                <p>We have located your """ + getVendorName() + """ account.</p>
                <p>Tap 'Done' to process your credentials.</p>
			</div>
        </body>
        </html>
        """
	render contentType: 'text/html', data: html
}

def receivedToken() {
	log.debug "In receivedToken"
    
    def html = """
        <!DOCTYPE html>
        <html>
        <head>
        <meta name="viewport" content="100%">
        <title>Withings Connection</title>
        <style type="text/css">
            @font-face {
                font-family: 'Swiss 721 W01 Thin';
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot');
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot?#iefix') format('embedded-opentype'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.woff') format('woff'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.ttf') format('truetype'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.svg#swis721_th_btthin') format('svg');
                font-weight: normal;
                font-style: normal;
            }
            @font-face {
                font-family: 'Swiss 721 W01 Light';
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot');
                src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot?#iefix') format('embedded-opentype'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.woff') format('woff'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.ttf') format('truetype'),
                     url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.svg#swis721_lt_btlight') format('svg');
                font-weight: normal;
                font-style: normal;
            }
            .container {
                width: 100%;
                padding: 0px;
                /*background: #eee;*/
                text-align: center;
            }
            img {
                vertical-align: middle;
            }
            img:nth-child(2) {
                margin: 0 10px;
            }
            p {
                font-size: 1.5em;
                font-family: 'Swiss 721 W01 Thin';
                text-align: center;
                color: #666666;
                padding: 0 40px;
                margin-bottom: 0;
            }
        /*
            p:last-child {
                margin-top: 0px;
            }
        */
            span {
                font-family: 'Swiss 721 W01 Light';
            }
        </style>
        </head>
        <body>
            <div class="container">
                <img src=""" + getVendorIcon() + """ alt="Vendor icon" style="width: 30%;max-height: 30%"/>
                <img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/connected-device-icn%402x.png" alt="connected device icon" style="width: 10%;max-height: 10%"/>
                <img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/st-logo%402x.png" alt="SmartThings logo" style="width: 30%;max-height: 30%"/>
                <p>Your Quirky account is now connected to SmartThings. Tap 'Done' to continue to choose devices.</p>
			</div>
        </body>
        </html>
        """
	render contentType: 'text/html', data: html
}

String toQueryString(Map m) {
        return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}



