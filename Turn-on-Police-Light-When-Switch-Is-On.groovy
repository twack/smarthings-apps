/**
 *  Turn on Police Light When Switch Is On
 *
 *  Author: Todd Wackford
 */
definition(
    name: 			"Turn on Police Light When Switch Is On",
    namespace: 		"smartthings",
    author: 		"twack",
    description: 	"Turn Fibaro Controller to Police lights program when a switch, real or virtual, is turned on.",
    category:		"My Apps",
    iconUrl: 		"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: 		"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)
preferences {
	section("When a Switch is turned on..."){
		input "switch", "capability.switch", title: "Which?"
	}
	section("Turn on this/these Fibaro Police Light(s)..."){
		input "switches", "capability.switch", multiple: true
	}
}
def installed() {
	subscribe(switch, "switch.on", switchOnHandler)
    subscribe(switch, "switch.off", switchOffHandler)
}
def updated() {
	unsubscribe()
	subscribe(switch, "switch.on", switchOnHandler)
    subscribe(switch, "switch.off", switchOffHandler)
}
def switchOnHandler(evt) {
	log.trace "Turning on switches: $switches"
	switches.police()
}
def switchOffHandler(evt) {
	log.trace "Turning on switches: $switches"
	switches.off()
}
