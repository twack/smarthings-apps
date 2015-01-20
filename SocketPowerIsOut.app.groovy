/**
 *
 *  SocketPowerIsOut.app.groovy
 *
 *  Alert me of an ac power loss on motion detector by detecting change from
 *  powered to battery. Smartthings hub and internet connection must be working
 *  so this does not work if whole house power is lost. The app works great for
 *  like a GFI or breaker trips in part of the house.
 *
 *  Author: todd@wackford.net
 *
 *  Date: 01-16-2013
 */
preferences {
	section("When there's AC power loss on..."){
		input "myDevice", "capability.battery", title: "Where?"
	}
	section("Via a push notification and a text message(optional)"){
    	input "pushAndPhone", "enum", title: "Send Text?", required: false, metadata: [values: ["Yes","No"]]
		input "phone1", "phone", title: "Phone Number (for Text, optional)", required: false
		
	}
}

def installed()
{
	subscribe(myDevice, "powerSource.battery", onBatteryPowerHandler)
}

def updated()
{
	unsubscribe()
	subscribe(myDevice, "powerSource.battery", onBatteryPowerHandler)
}

def onBatteryPowerHandler(evt) {
	log.trace "$evt.value: $evt, $settings"
	def msg = "${myDevice.label ?: myDevice.name} detected going to battery power"
    
	log.debug "sending push"
	sendPush(msg)
    
    if ( phone1 && pushAndPhone ) {
    	log.debug "sending SMS to ${phone1}"
    	sendSms(phone1, msg)
    }
}
