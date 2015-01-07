/**
 *  Turn on Police Light When It Opens
 *
 *  Author: SmartThings
 */
definition(
    name: "Turn on Police Light When It Opens",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Turn something on when an open/close sensor opens.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section("When the door opens..."){
		input "contact1", "capability.contactSensor", title: "Where?"
	}
	section("Turn on the Fibaro Police Light..."){
		input "switches", "capability.switch", multiple: false
	}
}


def installed()
{
	subscribe(contact1, "contact.open", contactOpenHandler)
        subscribe(contact1, "contact.closed", contactClosedHandler)
}

def updated()
{
	unsubscribe()
	subscribe(contact1, "contact.open", contactOpenHandler)
        subscribe(contact1, "contact.closed", contactclosedHandler)
}

def contactOpenHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "Turning on switches: $switches"
	switches.police()
}

def contactclosedHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "Turning on switches: $switches"
	switches.off()
}
