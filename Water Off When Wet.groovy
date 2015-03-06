/**
 *
 *  Water Off When Wet
 * 
 *
 *                     DO NOT USE THIS APP TO SHUT OFF HOUSE MAIN WATER IF YOU HAVE
 *                                   A SPRINKLER SYSTEM IN YOUR HOME!
 *
 *
 * If you have a sprinkler system in your home, only install this app to control local valves like for your wash machine,
 * sink or toilet.
 *
 *
 *
 *
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
    name: "Water Off When Wet",
    namespace: "wackware",
    author: "Todd Wackford",
    description: "Shut a water valve off when moisture sensor senses water. Also send notifications.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/water_moisture.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/water_moisture@2x.png"
)

preferences {
	section("When there's water detected by...") {
		input "waterSensor", "capability.waterSensor", title: "Where?", multiple: true
	}
    section("Shut off this/these water valve(s)...") {
		input "waterValve", "capability.valve", title: "Which?", multiple: true
	}
	section("Send a notification to...") {
		input("recipients", "contact", title: "Recipients", description: "Send notifications to") {
			input "phone", "phone", title: "Phone number?", required: false
		}
	}
}

def installed() {
	subscribe(waterSensor, "water.wet", waterWetHandler)
}

def updated() {
	unsubscribe()
	subscribe(waterSensor, "water.wet", waterWetHandler)
}

def waterWetHandler(evt) {
	//shut off the water
    waterValve.close()
    
	def deltaSeconds = 60

	def timeAgo = new Date(now() - (1000 * deltaSeconds))
	def recentEvents = waterSensor.eventsSince(timeAgo)
	log.debug "Found ${recentEvents?.size() ?: 0} events in the last $deltaSeconds seconds"

	def alreadySentSms = recentEvents.count { it.value && it.value == "wet" } > 1

	if (alreadySentSms) {
		log.debug "SMS already sent to $phone within the last $deltaSeconds seconds"
	} else {
		def msg = "${waterSensor.displayName} is wet! Shutting valve(s) ${waterValve.displayName}"
		log.debug "$waterSensor is wet, shuting valve $waterValve, texting $phone"

		if (location.contactBookEnabled) {
			sendNotificationToContacts(msg, recipients)
		}
		else {
			sendPush(msg)
			if (phone) {
				sendSms(phone, msg)
			}
		}
	}
}
