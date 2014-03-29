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
		input "motion1", "capability.motionSensor", title: "Where?"
	}
  	section("Notifications") {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
		input "phone", "phone", title: "Send a Text Message?", required: false
	}
}

def installed()
{
	subscribe(motion1, "powerSource.battery", onBatteryPowerHandler)
}

def updated()
{
	unsubscribe()
	subscribe(motion1, "powerSource.battery", onBatteryPowerHandler)
}

def onBatteryPowerHandler(evt) {
	log.trace "$evt.value: $evt, $settings"
	def msg = "${motion1.label ?: motion1.name} detected going to battery power"
    
	log.debug "sending push"
	send(msg)
}

private send(msg) {
	if(sendPushMessage != "No") {
		log.debug("Sending push message")
		sendPush(msg)
	}

	if(phone) {
		log.debug("Sending text message")
		sendSms(phone, msg)
	}

	log.debug(msg)
}
