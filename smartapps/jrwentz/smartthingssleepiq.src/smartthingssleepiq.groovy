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
    //appSetting "username"
    //appSetting "password"
}


preferences {
	//section("Title") {
		// TODO: put inputs here
	//}
    page(name: "mainPage", title: "Sleep Number SleepIQ")
    page(name: "authPage", title: "SleepIQ Login")
    page(name: "authResultsPage", title: "SleepIQ Login Status")
    page(name: "bedSelectPage", title: "SleepIQ Bed Selection")
    page(name: "nameBedPage", title: "SleepIQ Bed Naming")
    page(name: "createDevicePage", title: "SleepIQ Device Creation")
}

def mainPage() {
	//TODO: Check if logged in
    //TODO: Route to Login Page
    
    def loggedIn = isLoggedIn()
    def statusMessage = ""
    if(!loggedIn){
        statusMessage = "Status: Not Authenticated"
        state.lastPage = "mainPage"
        return authPage()
    }
    else {
        statusMessage = "Status: Authenticated"
    }
    
    state.lastPage = "mainPage"
    
    dynamicPage(name: "mainPage", nextPage: "", uninstall: true, install: true) {
        def beds = refreshBeds()

        section("App Info") {
            paragraph "Sleep Number SmartIQ Integration\nAuthor: Jonathan Wentz"
        }

        section("SmartIQ Account Information") {

            paragraph "Username: ${settings.username}\n" + statusMessage
            //TODO: Allow Log Out

        }

        section("Beds") {
        	if(beds) {
            	paragraph title: "Connected Beds", "Found $beds.size bed(s)"
            } else {
            	paragraph title: "Connected Beds", "No Beds Connected"
            }
        }
    }
}

def authPage() {
	log.trace "[SmartThingsSleepIQ:authPage] "
	state.installMessage = ""
	return dynamicPage(name: "authPage", title: "Connect to SleepIQ", nextPage:"authResultsPage", uninstall:false, install: false, submitOnChange: true) {
		section("Login Credentials"){
			input("username", "email", title: "Username", description: "SmartIQ Username (email address)")
			input("password", "password", title: "Password", description: "SmartIQ password")
		}
	}
}

def authResultsPage() {
	log.trace "[SmartThingsSleepIQ:authResultsPage] Login result next page: ${state.lastPage}"
    if(login()) {
    	return bedSelectPage()
    } else {
    	return dynamicPage(name: "authResultsPage", title: "Login Error", install:false, uninstall:false) {
			section(""){
				paragraph "The username or password you entered is incorrect. Go back and try again. "
			}
		}
	}
}

//TODO: Get Bed Data
//TODO: Get Sleepers
//TODO: Swap to Params

def bedSelectPage() {
	log.trace "[SmartThingsSleepIQ:bedSelectPage]"
    def beds = getBedData()
    
    dynamicPage(name: "bedSelectPage") {
    	if(beds.size > 0) {
        	beds.each { bed ->
            	section("Bed ID: $bed.bedId") {
                	settings.bedId = bed.bedId
                	href page: "nameBedPage", title: "Name Bed Sides", description: "Name the right and left sides of the bed", params: [bedId: bed.bedId]
                }
            }
        } else {
        	section {
            	paragraph "No beds found"
            }
        }
    }
}

def nameBedPage(params) {
	log.trace "[SmartThingsSleepIQ:nameBedPage] params: ${params}"
    
    settings.newRightSideName = null
    settings.newLeftSideName = null
    
    dynamicPage(name: "nameBedPage", nextPage: "createDevicePage") {
		section {
			paragraph "Bed ID: $settings.bedId"
			input "newRightSideName", "text", title: "Right Side", description: "Who sleeps on the right side of the bed?", defaultValue: "", required: true
            input "newLeftSideName", "text", title: "Left Side", description: "Who sleeps on the left side of the bed?", defaultValue: "", required: true
		}
		section {
			href "createDevicePage", title: "Create Device", description: "Click here once you have entered the names", params: [bedId: params.bedId]
		}
    }
    
}

def createDevicePage(params) {
	log.trace "[SmartThingsSleepIQ:createDevicePage] bedId: $params.bedId"
    def rightDeviceId = "sleepiq.${settings.bedId}.right"
    def leftDeviceId = "sleepiq.${settings.bedId}.left"
    
    //TODO: Create Child Devices
    log.trace "[SmartThingsSleepIQ:createDevicePage] Creating SleepIQ Right Side: $rightDeviceId"
    log.trace "[SmartThingsSleepIQ:createDevicePage] Creating SleepIQ Left Side: $leftDeviceId"
    
    settings.newRightSideName = null
    settings.newLeftSideName = null
    
    mainPage()
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



//API Calls
private def ApiHost() { "prod-api.sleepiq.sleepnumber.com" }

private def ApiUriBase() { "https://prod-api.sleepiq.sleepnumber.com" }

private def ApiUserAgent() { "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36" }


private def login() {
  log.trace "[SmartThingsSleepIQ:login] Logging in..."
  state.session = null
  
  try {
    def loginParams = [
      uri: ApiUriBase() + '/rest/login',
      headers: [
        'Content-Type': 'application/json;charset=UTF-8',
        'Host': ApiHost(),
        'User-Agent': ApiUserAgent(),
        'DNT': '1',
      ],
      body: '{"login":"' + settings.username + '","password":"' + settings.password + '"}='
    ]
    httpPut(loginParams) { response ->
      if (response.status == 200) {
        log.trace "[SmartThingsSleepIQ:login] Login was successful"
        state.session = [:]
        state.session.key = response.data.key
        state.session.cookies = ''
        response.getHeaders('Set-Cookie').each {
          state.session.cookies = state.session.cookies + it.value.split(';')[0] + ';'
        }
      	//refreshBeds()
        return true
      } else {
        log.trace "[SmartThingsSleepIQ:login] Login failed: ($response.status) $response.data"
        state.session = null
        return false
      }
    }
  } catch(Exception e) {
    log.error "[SmartThingsSleepIQ:login] Login failed: Error ($e)"
    state.session = null
    return false
  }
}

private def isLoggedIn() {
	log.debug "[SmartThingsSleepIQ:isLoggedIn] Determining if the user needs to log in"
	if(!state.session || !state.session?.key){
    	log.trace "[SmartThingsSleepIQ:isLoggedIn] Session missing"
		return false
	}
    
    if(getBedData()) {
    	log.trace "[SmartThingsSleepIQ:isLoggedIn] Successfully refreshed BedData, so login is working"
    	return true
    } else {
    	log.trace "[SmartThingsSleepIQ:isLoggedIn] Could not refresh bed data, so we need to login again"
    	return false
    }
}


def refreshBeds() {
	if(!isLoggedIn()) {
    	login()
    }
    
    log.debug "[SmartThingsSleepIQ:refreshBeds] Refreshing beds..."
    def beds = getBedData()
    
    if(beds.size > 0) {
    	return beds
    } else {
    	return []
    }
}

def getBedData() {
	log.trace "[SmartThingsSleepIQ:getBedData]"
    def bedData = []
    
    try {
    	def familyStatusParams = [
        	uri: ApiUriBase() + '/rest/bed/familyStatus?_k=' + state.session?.key,
			headers: [
				'Content-Type': 'application/json;charset=UTF-8',
                'Host': ApiHost(),
                'User-Agent': ApiUserAgent(),
                'Cookie': state.session?.cookies,
                'DNT': '1'
            ]
        ]
        
        httpGet(familyStatusParams) { response -> 
			if (response.status == 200) {
            	log.trace "[SmartThingsSleepIQ:getBedData] Successfully retrieved familyStatus: $response.data"
				bedData = response.data.beds
			} else {
            	log.error "[SmartThingsSleepIQ:getBedData] Error getting familyStatus, REST request unsuccessful: ($response.status): $response.data"
                state.session = null
			}
        }
    } catch (Exception ex) {
    	log.error "[SmartThingsSleepIQ:getBedData] Error getting familyStatus: $ex"
    }
    
    return bedData
}

