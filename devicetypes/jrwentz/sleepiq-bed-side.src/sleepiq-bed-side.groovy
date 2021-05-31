/**
 *  SleepIQ.Bed.Side
 *
 *  Copyright 2021 Jonathan Wentz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
metadata {
	definition (name: "SleepIQ.Bed.Side", namespace: "jrwentz", author: "Jonathan Wentz", cstHandler: true) {
		capability "Presence Sensor"
		//capability "Sleep Sensor"
		capability "Switch"
		capability "Switch Level"
        
        attribute "bedId", "string"
        attribute "side", "string"
        attribute "sleeper", "string"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		// TODO: define your main and details tiles here
        standardTile("presence", "device.presence", width: 2, height: 2) {
			state("not present", label:'not present', icon:"st.presence.tile.not-present", backgroundColor:"#ffffff")
			state("present", label:'present', icon:"st.presence.tile.present", backgroundColor:"#00A0DC")
		}
        controlTile("levelSleepNumber", "device.level", "slider", width:2, height:1) {
        	state("level", action: "switch.setLevel")
        }
        valueTile("valueSleepNumber", "device.level", width: 2, height: 2) {
        	state "val", label: "${currentValue}", defaultState: true
        }
        
		main "presence"
		details "presence", "levelSleepNumber", "valueSleepNumber"
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'presence' attribute
	// TODO: handle 'sleeping' attribute
	// TODO: handle 'switch' attribute
	// TODO: handle 'level' attribute

}

def updateData(Boolean isInBed, Integer sleepNumber) {
	sendEvent(name: "presence", value: isInBed ? "present" : "not present")
    sendEvent(name: "level", value: sleepNumber)
}

// handle commands
def on() {
	log.debug "Executing 'on'"
	// TODO: handle 'on' command
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}

def setLevel() {
	log.debug "Executing 'setLevel'"
	// TODO: handle 'setLevel' command
}