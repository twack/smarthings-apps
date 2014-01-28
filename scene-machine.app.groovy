/**
 *  App Name:   Scene Machine
 *
 *  Author: 	Todd Wackford
 *				twack@wackware.net
 *  Date: 		2013-06-14
 *  Version: 	1.0
 *  
 *  Updated:    2013-07-25
 *  
 *  BF #1		Fixed bug where string null was being returned for non-dimmers and
 *              was trying to assign to variable.
 *  
 *  
 *  This app lets the user select from a list of switches or dimmers and record 
 *  their currents states as a Scene. It is suggested that you name the app   
 *  during install something like "Scene - Romantic Dinner" or   
 *  "Scene - Movie Time". Switches can be added, removed or set at new levels   
 *  by editing and updating the app from the smartphone interface.
 *
 *  Usage Note: GE-Jasco dimmers with ST is real buggy right now. Sometimes the levels
 *              get correctly, sometimes not.
 *              On/Off is OK.
 *              Other dimmers should be OK.
 *  
 */
preferences {
	section("Select switches ...") {
		input "switches", "capability.switch", multiple: true
	}
    
    //Uncomment this section below to test/change on the IDE. Smartphone does 
	//not need it.
	
    //section("Record New Scene?") {  	
	//	input "record", "enum", title: "New Scene...", multiple: false, 
	//         required: true, metadata:[values:['No','Yes']]
	//}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe(app) 
    getDeviceSettings()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
    subscribe(app)
    
   //if(record == "Yes") //uncomment this line to test/change stuff on the IDE
      getDeviceSettings()
}

def appTouch(evt) {
	log.debug "appTouch: $evt"
	setScene()
}

private setScene() {

	def i = 0
    def switchName  = ""
    def switchType  = ""
    def switchState = ""
    def dimmerValue = ""
    
	for(myData in state.lastSwitchData) {

    	switchName  = myData.switchName
    	switchType  = myData.switchType
    	switchState = myData.switchState
        if(myData.dimmerValue != "null")					//
   			dimmerValue = myData.dimmerValue.toInteger() 	//BF #1
        else												//
			dimmerValue = 0									//
        
        log.info "switchName: $switchName"
        log.info "switchType: $switchType"
        log.info "switchState: $switchState"
        log.info "dimmerValue: $dimmerValue"

		if((switchState == "on") && (switchType != "Dimmer Switch"))
        	switches[i].on()
            
        if((switchState == "on") && (switchType == "Dimmer Switch"))
            switches[i].setLevel(dimmerValue)
         
        if(switchState == "off")
        	switches[i].off()

        i++
        log.info "Device setting is Done-------------------"
    }
}

private getDeviceSettings() {

    def cnt = 0
    for(myCounter in switches) {
    	switches[cnt].refresh() //this was a try to get dimmer values (bug)
        switches[cnt].poll()
    	cnt++
    }
    
    state.lastSwitchData = [cnt]
    
	def i = 0
    def switchName  = ""
    def switchType  = ""
    def switchState = ""
    def dimmerValue = ""

	for(mySwitch in switches) {
        switchName  = mySwitch.device.toString()
        switchType  = mySwitch.name.toString()
        switchState = mySwitch.latestValue("switch").toString()
		//dimmerValue below returns null if it is not a dimmer
        dimmerValue = mySwitch.latestValue("level").toString()   
        
        state.lastSwitchData[i] = [switchName: switchName,
        						   switchType: switchType,
                                   switchState: switchState,
                                   dimmerValue: dimmerValue]

        log.debug "SwitchData: ${state.lastSwitchData[i]}"
        i++   
	}  
}
