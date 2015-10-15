/**
 *  Trigger a Fibaro RGBW action when a contact sensor opens.
 *  User can optionaly pick what action is taken by the controller
 *  such as; turn green, blue or run the fireplace program. If the
 *  user does not pick an action, this app will run the next
 *  sequential action from the list (see below). The controller
 *  will be turned off whne the contact sensor closes.
 *
 *  Author: Todd Wackford
 */
definition(
    name: 			"Trigger Fibaro RGBW action from contact sensor",
    namespace: 		"smartthings",
    author:			"Todd Wackford",
    description:	"Trigger Fibaro RGBW when an contact open/close sensor opens...",
    category: 	 	"My Apps",
    iconUrl: 		"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: 		"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section(""){
		input "contact1", "capability.contactSensor", title: "When this door opens..."
		input "switches", "capability.switch", multiple: true, title: "Turn on this/these Fibaro Controller(s)..."
    	input(name: "switchAction", required: false, type: "enum", title: "And run this action (optional)", 
              options: ["red", "green", "blue", "white", "cyan", "magenta", "orange", "purple", "yellow",
        	            "pink", "coldWhite", "warmWhite", "fireplace", "storm", "deepfade", "litefade", "police"])
    }
}

def installed() {
	log.info "installed with $settings"
	subscribe(contact1, "contact.open", contactOpenHandler)
    subscribe(contact1, "contact.closed", contactClosedHandler)
    initialize()
}

def updated() {
	log.info "updated with $settings"
	unsubscribe()
	subscribe(contact1, "contact.open", contactOpenHandler)
    subscribe(contact1, "contact.closed", contactclosedHandler)
    initialize()
}

def initialize() {
	state.actionList = ["red", "green", "blue", "white", "cyan", "magenta", "orange", "purple", "yellow",
        	            "pink", "coldWhite", "warmWhite", "fireplace", "storm", "deepfade", "litefade", "police"]
}

def contactOpenHandler(evt) {
	if ( switchAction ) {
    	switches."${switchAction}"()
    } else {
    	def cnt = counterHandler()    
		log.trace "Turning on Controllers $switches with: ${state.actionList.get(cnt)}"
		switches."${state.actionList.get(cnt)}"()
    }
}

def contactclosedHandler(evt) {
	log.trace "Turning off Controllers: $switches"
	switches.off()
}

def counterHandler() {
    if ( (state.actionCounter == null) || (state.actionCounter >= (state.actionList.size() - 1)) )
    	state.actionCounter = 0
    else
    	state.actionCounter = state.actionCounter + 1   
    return state.actionCounter
}
