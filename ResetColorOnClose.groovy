/**
 *  Reset Color on Close
 *
 *  Copyright 2015 smartthings
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
 
definition (
    name: "Reset Color on Close",
    namespace: "smartthings",
    author: "smartthings",
    description: "Return color bulbs to previous setting on closure of contact sensor(s).",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)

preferences {
	section("When this/these contact(s) close...") {
		input "contacts", "capability.contactSensor", multiple: true
	}
	section("Return this light to the color at contact open...") {
		input "bulb", "capability.colorControl"
	}
}

def installed() {
	subscribe(contacts, "contact.open", contactOpenHandler)
    subscribe(contacts, "contact.closed", contactClosedHandler)
}

def updated() {
	unsubscribe()
	subscribe(contacts, "contact.open", contactOpenHandler)
    subscribe(contacts, "contact.closed", contactclosedHandler)
}

def contactOpenHandler(evt) {
    def values = [:]
	values = [ level: bulb.latestValue("level") as Integer,
               hex: bulb.latestValue("color"),
               saturation: bulb.latestValue("saturation"),
               hue: bulb.latestValue("hue")]
               
    atomicState.previousValues = values
	log.info "Previous values are: ${atomicState.previousValues}"
}

def contactclosedHandler(evt) {
	bulb.setColor(atomicState.previousValues)
}
