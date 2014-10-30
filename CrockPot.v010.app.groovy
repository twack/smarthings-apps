/*****************************************************************************
Device:  CrockPot.v010.app.groovy
Author:  twack@wackware.net
Version: 1.0
Date:    2013-04-06
Purpose: To control a crockpot using a switch. This app was written using
         GE outdoor lighting controller so there is functionality to see if
         the controller or power strip is running/plugged in. Typically the
         user will have a Zwave outlet. You need to use a dumb crockpot that
         only has a off/low/med/high physical switch. Set the crockpot to the
         desired level and plug into the switch.

Use License: Non-Profit Open Software License version 3.0 (NPOSL-3.0)
             http://opensource.org/licenses/NPOSL-3.0

******************************************************************************
                                Change Log

Version:  1.0
Date:     20130406
Change1:  Initial Release

******************************************************************************

Device Types Supported:	Switch

To-Do's:		Create a customer companion device that has tile with minutes
                left to cook.
                
Other Info:		This written to demo at San Francisco with SmartThings. 

******************************************************************************/
 

// Automatically generated. Make future change here.
definition(
    name: "SmartThings CrockPot Controller 1.0",
    namespace: "wackware",
    author: "todd@wackford.net",
    description: "CrockPots Will Win the Machine -v- Man Wars!",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png")

preferences {
	section("Crockpot Cooking Info..."){
    	input "startTime", "time", title: "Start time...", required: true
		input "onDuration", "decimal", title: "For how long...", required: true
        input "meal", "text", title: "Meal name (optional)...", required: false
	}
    section("CrockPot Notifications") {  	
        input "phone", "phone", title: "Text me at (optional)...", multiple: false, required: false
	}
    section("CrockPot Controller Switch") {
		input "switch1", "capability.switch", multiple: false, required: true
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	startUp()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unschedule()
	unsubscribe()
    startUp()
}

def startUp() {

	def usrMilliTime = timeToday(startTime)
    if(now() > usrMilliTime.time) {
    	log.debug "start time is not ok ${startTime}"
        communicateError("CROCKPOT ERROR: Your Starting Time is in the Past. Please update Starting Time")
        return
    } else {
    	log.debug "start time is ok ${startTime}"
    }
    
    state.onTimer = onDuration?.toDouble() * 60
    state.errorCount = 0
    log.debug "The user running time is: ${state.onTimer}"
    schedule(startTime, "turnOnDevice")
    subscribe(switch1, "switch.on", communicateOn)
    subscribe(switch1, "switch.off", communicateOff)
}

def shutdown(){
 	unschedule()
    unsubscribe()
    switch1.off()
}

def checkStatus() {
	if (state.onTimer > 0) {
    	log.debug "Timer = ${state.onTimer}"
        if (switchOK("on") == true) {
        	state.onTimer = state.onTimer - 1
            state.errorCount = 0
        	//here's where someday we'll update the display on the app/device for minutes left
        } else {
        	state.errorCount = state.errorCount + 1
        	switch1.on()
        }
    } else {
    	// we should not get here as the off event handler will kill this schedule
    	log.debug "Timer = ${state.onTimer}"
        switch1.off()
    }
    log.debug "We had ${state.errorCount - 1} errors calling device on"
    if(state.errorCount > 3) {
    	communicateCantStart()
        shutdown()
    }
}

def switchOK(value)
{
	def result = false
	for (it in (switch1 ?: [])) {
    	log.debug "SwitchState = ${it.currentSwitch}"
		if (it.currentSwitch == value) {
			result = true
			break
		}
	}
	result
}

def turnOnDevice() {
	schedule("0 * * * * ?", "checkStatus") // used to verify on and stay on for duration
    checkStatus()
}


def communicateError(msg) {
    if (phone != "") {
    	sendSms(phone, msg)    
    }
    
    def push = true
    if(push == true) {
    	sendPush(msg)
    }
}

def communicate(msg){
	log.debug "COMMUNICATING"

    if (phone != "") {
    	sendSms(phone, msg)    
    }
    
    def push = true //code like this so we can toggle it on or off during testing in program
    if(push == true) {
    	sendPush(msg)
    }
}

def communicateOn(evt){
	if (settings['meal'] == '""') {
        settings['meal'] = "Meal"
    }
    
	def msg = "A Smart CrockPot Says: I have started cooking the ${meal}. "
        msg+= "I will be finished in ${onDuration} hours."
    communicate(msg)
}

def communicateOff(evt) {
	if (settings['meal'] == '""') {
        settings['meal'] = "Meal"
    }
    
	def msg = "A Smart CrockPot Says: I have finished cooking the ${meal}. "
        msg+= "Bon Appetit!"
    communicate(msg)
    shutdown()
}

def communicateCantStart() {
	def msg = "Unable to talk to the Cloud. "
        msg+= "Please contact your CrockPot System Administrator. "
        msg+= "Or, check that I am plugged in."
	communicate(msg)
}
