/**
 *  DimWithMe.app.groovy
 *  Dim With Me
 *
 *  Author: todd@wackford.net
 *  Date: 2013-11-12
 */
/**
 *  App Name:   Dim With Me
 *
 *  Author: 	Todd Wackford
 *				twack@wackware.net
 *  Date: 		2013-11-12
 *  Version: 	0.1
 *  
 *  Use this program with a virtual dimmer as the master for best results.
 *
 *  This app lets the user select from a list of dimmers to act as a triggering
 *  master for other dimmers or regular switches. Regular switches come on
 *  anytime the master dimmer is on or dimmer level is set to more than 0%.
 *  of the master dimmer.
 *
 *  Use Cases:
 *		You have a switch that does nothing much but makes a good master for 
 *  
 *
 *  Use License: Non-Profit Open Software License version 3.0 (NPOSL-3.0)
 *               http://opensource.org/licenses/NPOSL-3.0
 */

preferences {
	section("When This...") { //use this program with a virtual dimmer
		input "masters", "capability.switchLevel", 
			multiple: false, 
			title: "Master Dimmer Switch...", 
			required: true
	}

	section("Then these regular switches follow...") {
		input "slaves2", "capability.switch", 
			multiple: true, 
			title: "Slave Switch(es) Too...", 
			required: false
	}
    
    section("Then these dimmers follow...") {
		input "slaves", "capability.switchLevel", 
			multiple: true, 
			title: "Slave Dimmer(s) Switch Too...", 
			required: true
	}
}

def installed()
{
	subscribe(masters, "switch.on", switchOnHandler)
    subscribe(masters, "switch.off", switchOffHandler)
    subscribe(masters, "switch.setLevel", switchSetLevelHandler)
}

def updated()
{
	unsubscribe()
	subscribe(masters, "switch.on", switchOnHandler)
    subscribe(masters, "switch.off", switchOffHandler)
    subscribe(masters, "switch.setLevel", switchSetLevelHandler)
}

def switchSetLevelHandler(evt)
{	
    def level = evt.value.toFloat()
    level = level.toInteger()
    log.info "switchSetLevelHandler Event: ${level}"
    slaves?.setLevel(level)
}

def switchOffHandler(evt) {
	log.info "switchoffHandler Event: ${evt.value}"
    slaves?.off()
    slaves2?.off()
}

def switchOnHandler(evt) {
	log.info "switchOnHandler Event: ${evt.value}"
    def dimmerValue = masters.latestValue("level")
    slaves?.on()
    slaves2?.on()
}
