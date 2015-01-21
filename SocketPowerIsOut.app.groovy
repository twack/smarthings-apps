/**
 *  SocketPowerIsOut.groovy
 *
 *  Copyright 2015 Todd Wackford
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
definition(
    name: "Socket Power Is Out",
    namespace: "wackware",
    author: "Todd Wackford",
    description: "Alert me of an ac power loss on motion detector by detecting change from powered to battery. SmartThings hub and internet connection must be working so this does not work if whole house power is lost. The app works great for like a GFI or breaker trips in part of the house.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

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
