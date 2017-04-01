/**
 *  ThingBoard
 *
 *  Copyright 2017 Matthew Butt
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
    name: "ThingBoard",
    namespace: "preinvent",
    author: "Matthew Butt",
    description: "ThingBoard",
    category: "Convenience",
    iconUrl: "https://s3-us-west-1.amazonaws.com/thingboard/images/thingboard-60.png",
    iconX2Url: "https://s3-us-west-1.amazonaws.com/thingboard/images/thingboard-120.png",
    iconX3Url: "https://s3-us-west-1.amazonaws.com/thingboard/images/thingboard-120.png",
    oauth: true)


preferences {
  section ("Allow ThingBoard to control these switches...") {
    input "switches", "capability.switch", multiple: true, required: true
  }
  section ("Allow ThingBoard to control these switches...") {
    input "switchLevels", "capability.switchLevel", multiple: true, required: true
  }
  section ("Allow ThingBoard to control these temperature sensors...") {
    input "tempSensor", "capability.temperatureMeasurement", multiple: true, required: true
  }
  section ("Allow ThingBoard to access these contact sensors...") {
    input "contactSensor", "capability.contactSensor", multiple: true, required: true
  }
  section ("Allow ThingBoard to control these thermostats...") {
    input "thermostats", "capability.thermostat", multiple: true, required: true
  }
  section ("Allow ThingBoard to access these presence sensors...") {
    input "presence", "capability.presenceSensor", multiple: true, required: true
  }
}

mappings {
  path("/switches") {
    action: [
      GET: "listSwitches"
    ]
  }
  path("/switches/:id/:command") {
    action: [
      PUT: "updateSwitches"
    ]
  }
  path("/switches/:id/:command/:level") {
    action: [
      PUT: "updateSwitchLevel"
    ]
  }
  path("/thermostats/:id/:temp") {
    action: [
      PUT: "setThermostatHeatingTemp"
    ]
  }
  path("/all") {
  	action: [
    	GET: "listAll"
    ]
  }
}

def listAll() {
    def resp = []
    switches.each {
      resp << [name: it.displayName, id: it.getId(), value: it.currentValue("switch"), type: "switch"]
    }
    switchLevels.each {
      resp << [name: it.displayName, id: it.getId(), level: it.currentValue("level"), type: "switchLevel"]
    }
    tempSensor.each {
      resp << [name: it.displayName, id: it.getId(), value: it.currentValue("temperature"), type: "tempSensor"]
    }
    contactSensor.each {
      resp << [name: it.displayName, id: it.getId(), value: it.currentValue("contact"), type: "contactSensor"]
    }
    thermostats.each {
      resp << [name: it.displayName, id: it.getId(), value: it.currentValue("thermostatSetpoint"), type: "thermostat"]
    }
    presence.each {
      resp << [name: it.displayName, id: it.getId(), value: it.currentValue("presence"), type: "presenceSensor"]
    }
    return resp
}

def listSwitches() {
    def resp = []
    switches.each {
      resp << [name: it.displayName, id: it.getId(), value: it.currentValue("switch")]
    }
    return resp
}

void setThermostatHeatingTemp() {
	def temp = params.temp
    def id = params.id
    
    thermostats.each {
    	if (it.getId() == id) {
        	it.setHeatingSetpoint(temp);
        }
    }
}

void updateSwitches() {
    // use the built-in request object to get the command parameter
    def command = params.command
    def id = params.id

    switches.each {
    	if (it.getId() == id) {
        	if (command == "on") {
	        	it.on();
            } else if (command == "off") {
            	it.off();
            }
        }
	}
}

void updateSwitchLevel() {
    // use the built-in request object to get the command parameter
    def command = params.command
    def level = params.level
    def id = params.id

    switches.each {
    	if (it.getId() == id) {
            it.setLevel(level);
        	if (command == "on") {
	        	it.on();
            } else if (command == "off") {
            	it.off();
            }
        }
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
}

// TODO: implement event handlers