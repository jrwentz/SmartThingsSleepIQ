/**
 *  SmartThingsSleepIQ
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
 *
 */
definition(
    name: "SmartThingsSleepIQ",
    namespace: "jrwentz",
    author: "Jonathan Wentz",
    description: "Provides control and presence for a Sleep Number bed",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png") {
    appSetting "username"
    appSetting "password"
    appSetting "cookie"
}


preferences {
	//section("Title") {
		// TODO: put inputs here
	//}
    page(name: "mainPage", title: "Sleep Number SmartIQ")
}

def mainPage() {
	//TODO: Check if logged in
    //TODO: Route to Login Page
    
    def loggedIn = true
    def statusMessage = ""
    if(loggedIn){
        statusMessage = "Status: Authenticated"
    }
    else {
        statusMessage = "Status: Not Authenticated"
    }
    
    def beds = refreshBeds()
    
    section("App Info") {
    	paragraph "Sleep Number SmartIQ Integration\nAuthor:Jonathan Wentz"
    }
    
    section("SmartIQ Account Information") {
        
        //TODO: Get Bed State
    	paragraph "username: ${settings.username}\n" + statusMessage
        
    }
    
    section("Beds"){
    	paragraph title: "Connected Beds", "No Beds Connected"
    }
    
}

def authPage() {
}

def bedSelectPage() {
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

def refreshBeds(){
	log.debug "Refreshing beds..."
    def beds = []
    //TODO: Refresh bed info
    return beds
}