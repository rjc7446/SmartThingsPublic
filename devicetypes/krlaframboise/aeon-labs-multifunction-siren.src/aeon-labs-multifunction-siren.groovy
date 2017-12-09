/**
 *  Aeon Labs Multifunction Siren v 1.7
 *      (Aeon Labs Siren - Model:ZW080-A17)
 *
 * (https://community.smartthings.com/t/release-aeon-labs-multifunction-siren/40652?u=krlaframboise)
 *
 *  Capabilities:
 *      Switch, Alarm, Tone, Music Player, Polling
 *
 *	Author: 
 *      Kevin LaFramboise (krlaframboise)
 *
 *	Changelog:
 *
 *	1.7 (07/22/2016)
 *    - Fixed fingerprint for non-secure hub v2
 *    - Fixed secure command check so that it accurately
 *      detects if its paired securely and works either way.
 *    - Code cleanup and additional validation.
 *
 *	1.6 (05/21/2016)
 *    - Removed poll check.
 *    - Moved lastPoll event creation into parse method 
 *      so that it gets raised every time the device responds.
 *    - Modified the poll command so that it only polls if the
 *      lastPoll event is old.
 *
 *	1.5 (05/12/2016)
 *    - Added Polling & Speech Synthesis functionality.
 *		-	Adding attribute for lastPoll
 *
 *	1.4.1 (05/10/2016)
 *		-	Bug fix for Smart Alarm music player device commands.
 *
 *	1.4 (03/05/2016)
 *		-	Enhanced Logging
 *		- Fixed bug with beep schedule cancellation.
 *
 *	1.3 (03/03/2016)
 *		-	Added startBeepDelayedAlarm command.
 *		- Fixed validation logging bug.
 *
 *	1.2 (02/29/2016)
 *		-	Fixed IOS UI issue with beep buttons.
 *		-	Added Music Player capability.
 *		-	Added TTS command support so that it can be used
 *			with SHM, Notify with Sound, and Rule Machine
 *		- Added alarm delay feature.
 *
 *	1.1 (02/28/2016)
 *		-	Logging Enhancements.
 *
 *	1.0 (02/28/2016)
 *		-	Initial Release
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
	definition (name: "Aeon Labs Multifunction Siren", namespace: "krlaframboise", author: "Kevin LaFramboise") {
		capability "Actuator"
		capability "Switch"
		capability "Alarm"
		capability "Tone"
		capability "Configuration"
		capability "Music Player"
		capability "Speech Synthesis"
		capability "Polling"

		attribute "status", "enum", ["off", "alarm", "customAlarm", "delayedAlarm", "beepDelayedAlarm", "beep", "beepSchedule", "customBeep", "customBeepSchedule"]
		
		attribute "lastPoll", "number"

		command "customAlarm", ["number", "number", "number"]
		command "delayedAlarm", ["number", "number", "number", "number"]
		command "customBeep", ["number", "number", "number", "number", "number"]
		command "startBeep"
		command "startBeepDelayedAlarm"
		command "startCustomBeep", ["number", "number", "number", "number", "number", "number", "number"]

		command "customBeep1"
		command "customBeep2"
		command "customBeep3"
		command "customBeep4"
		command "customBeep5"
		command "customBeep6"

		// Music and Sonos Related Commands
		command "playSoundAndTrack"
		command "playTrackAndRestore"
		command "playTrackAndResume"
		command "playTextAndResume"
		command "playTextAndRestore"

		fingerprint mfr: "0086", prod: "0104", model: "0050", deviceJoinName: "Aeon Labs Siren"
		
		fingerprint deviceId: "0x1005", inClusters: "0x5E,0x98,0x25,0x70,0x85,0x59,0x72,0x2B,0x2C,0x86,0x7A,0x73", outClusters: "0x5A,0x82"
	}

	simulator {
		reply "9881002001FF,9881002002": "command: 9881, payload: 002003FF"
		reply "988100200100,9881002002": "command: 9881, payload: 00200300"
	}

	preferences {
		input "sirenSound", "number", 
			title: "Siren Sound (1-5)", 
			defaultValue: 1, 
			range: "1..5",
			displayDuringSetup: true, 
			required: false
		input "sirenVolume", "number", 
			title: "Siren Volume (1-3)", 
			defaultValue: 1, 
			range: "1..3",
			displayDuringSetup: true, 
			required: false
		input "alarmDuration", "number", 
			title: "Turn siren off after: (seconds)\n(0=unlimited)", 
			defaultValue: 0, 
			displayDuringSetup: true, 
			required: false
		input "beepSound", "number", 
			title: "Beep Sound (1-5)", 
			defaultValue: 3, 
			range: "1..5",
			displayDuringSetup: true, 
			required: false
		input "beepVolume", "number", 
			title: "Beep Volume (1-3)", 
			defaultValue: 1, 
			range: "1..3",
			displayDuringSetup: true, 
			required: false
		input "beepRepeat", "number", 
			title: "Beep Repeat (1-100)", 
			defaultValue: 1, 
			range: "1..100",
			displayDuringSetup: true, 
			required: false
		input "beepRepeatDelay", "number", 
			title: "Time Between Beeps in Milliseconds", 
			defaultValue: 1000, 
			displayDuringSetup: true, 
			required: false
		input "beepLength", "number", 
			title: "Length of Beep in Milliseconds", 
			defaultValue: 100, 
			displayDuringSetup: true, 
			required: false
		input "beepEvery", "number", 
			title: "Scheduled Beep Every (seconds)", 
			defaultValue: 10,
			displayDuringSetup: true,
			required: false
		input "beepStopAfter", "number", 
			title: "Stop Scheduled Beep After (seconds)", 
			defaultValue: 60,
			displayDuringSetup: true,
			required: false
		input "useBeepDelayedAlarm", "bool",
			title: "Play Beep Schedule Before Sounding Alarm?",
			defaultValue: false,
			displayDuringSetup: true,
			required: false
		input "debugOutput", "bool", 
			title: "Enable debug logging?", 
			defaultValue: true, 
			displayDuringSetup: true, 
			required: false		
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"status", type: "generic", width: 6, height: 3, canChangeIcon: true){
			tileAttribute ("status", key: "PRIMARY_CONTROL") {
				attributeState "off", label:'off', action: "off", icon:"st.alarm.alarm.alarm", backgroundColor:"#ffffff"
				attributeState "alarm", label:'Alarm Sounding!', action: "off", icon:"st.alarm.alarm.alarm", backgroundColor:"#ff9999"
				attributeState "customAlarm", label:'Custom Alarm Sounding!', action: "off", icon:"", backgroundColor:"#ff9999"
				attributeState "delayedAlarm", label:'Delayed Alarm Active!', action: "off", icon:"", backgroundColor:"#ff9999"
				attributeState "beepDelayedAlarm", label:'Beep Delayed Alarm Active!', action: "off", icon:"", backgroundColor:"#ff9999"
				attributeState "beep", label:'Beeping!', action: "off", icon:"st.Entertainment.entertainment2", backgroundColor:"#99FF99"
				attributeState "beepSchedule", label:'Scheduled\nBeeping!', action: "off", icon:"", backgroundColor:"#99FF99"
				attributeState "customBeep", label:'Custom Beeping!', action: "off", icon:"", backgroundColor:"#694489"
				attributeState "customBeepSchedule", label:'Scheduled Custom Beeping!', action: "off", icon:"", backgroundColor:"#694489"				
			}
		}
		standardTile("playAlarm", "device.alarm", label: 'Alarm', width: 2, height: 2) {
			state "default", label:'Alarm', action: "both", icon:"st.alarm.alarm.alarm", backgroundColor: "#ff9999"
			state "both", label:'Stop', action: "off", icon:"st.alarm.alarm.alarm", backgroundColor: "#ffffff"
		}
		standardTile("playBeep", "device.status", label: 'Beep', width: 2, height: 2) {
			state "default", label:'Beep', action:"beep", icon:"st.Entertainment.entertainment2", backgroundColor: "#99FF99"
		}
		valueTile("playBeepSchedule", "device.status", label: 'Scheduled Beep', width: 2, height: 2) {
			state "default", label:'Start\nBeep', action:"startBeep",backgroundColor: "#99FF99"
			state "beepSchedule", label:'Stop\nBeep', action:"off", icon: "", backgroundColor: "#ffffff"
		}
		valueTile("playCustomBeep1", "device.status", label: 'Custom Beep 1', width: 2, height: 2, wordWrap: true) {
			state "default", label:'Beep\n1', action:"customBeep1",backgroundColor: "#694489"
		}
		valueTile("playCustomBeep2", "device.status", label: 'Custom Beep 2', width: 2, height: 2, wordWrap: true) {
			state "default", label:'Beep\n2', action:"customBeep2",backgroundColor: "#694489"
		}
		valueTile("playCustomBeep3", "device.status", label: 'Custom Beep 3', width: 2, height: 2, wordWrap: true) {
			state "default", label:'Beep\n3', action:"customBeep3",backgroundColor: "#694489"
		}
		valueTile("playCustomBeep4", "device.status", label: 'Custom Beep 4', width: 2, height: 2, wordWrap: true) {
			state "default", label:'Beep\n4', action:"customBeep4",backgroundColor: "#694489"
		}
		valueTile("playCustomBeep5", "device.status", label: 'Custom Beep 5', width: 2, height: 2, wordWrap: true) {
			state "default", label:'Beep\n5', action:"customBeep5",backgroundColor: "#694489"
		}
		valueTile("playCustomBeep6", "device.status", label: 'Custom Beep 6', width: 2, height: 2, wordWrap: true) {
			state "default", label:'Beep\n6', action:"customBeep6",backgroundColor: "#694489"
		}
		main "status"
		details(["status", "playAlarm", "playBeep", "playBeepSchedule", "playCustomBeep1", "playCustomBeep2", "playCustomBeep3", "playCustomBeep4", "playCustomBeep5", "playCustomBeep6"])
	}
}

def poll() {
	def result = []
	def minimumPollMinutes = 60
	def lastPoll = device.currentValue("lastPoll")
	if ((new Date().time - lastPoll) > (minimumPollMinutes * 60 * 1000)) {
		logDebug "Poll: Refreshing because lastPoll was more than ${minimumPollMinutes} minutes ago."
		result << versionGetCmd()
	}
	else {
		logDebug "Poll: Skipped because lastPoll was within ${minimumPollMinutes} minutes"
	}
	return result
}

def speak(text) {
	playText(text)
}

// Unsuported Music Player commands
def unmute() { handleUnsupportedCommand("unmute") }
def resumeTrack(map) { handleUnsupportedCommand("resumeTrack") }
def restoreTrack(map) { handleUnsupportedCommand("restoreTrack") }
def nextTrack() { handleUnsupportedCommand("nextTrack") }
def setLevel(number) { handleUnsupportedCommand("setLevel") }
def previousTrack() { handleUnsupportedCommand("previousTrack") }
def setTrack(string) { handleUnsupportedCommand("setTrack") }

// Turns siren off
def pause() { off() }
def stop() { off() }
def mute() { off() }

// Turns siren on
def play() { both() }

// Commands necessary for SHM, Notify w/ Sound, and Rule Machine TTS functionality.
def playSoundAndTrack(text, other, other2, other3) {
	playTrackAndResume(text, other, other2) 
}
def playTrackAndRestore(text, other, other2) {
	playTrackAndResume(text, other, other2) 
}
def playTrackAndResume(text, other, other2) {
	playText(getTextFromTTSUrl(text))
}
def getTextFromTTSUrl(ttsUrl) {
	def urlPrefix = "https://s3.amazonaws.com/smartapp-media/tts/"
	if (ttsUrl?.toString()?.toLowerCase()?.contains(urlPrefix)) {
		return ttsUrl.replace(urlPrefix,"").replace(".mp3","")
	}
	return ttsUrl
}

def playTextAndResume(text, other) { playText(text) }
def playTextAndRestore(text, other) { playText(text) }
def playTrack(text) { playText(text) }

def playText(text) {
	text = cleanTextCmd(text)
	def cmds
	switch (text) {
		case ["siren", "strobe", "both", "on", "play"]:
			cmds = both()
			break
		case ["stop", "off", "pause", "mute"]:
			cmds = off()
			break
		case "beep":
			cmds = beep()
			break
		case "startbeep":
			cmds = startBeep()
			break
		case "startbeepdelayedalarm":
			cmds = startBeepDelayedAlarm()
			break
		case "custombeep1":
			cmds = customBeep1()
			break
		case "custombeep2":
			cmds = customBeep2()
			break
		case "custombeep3":
			cmds = customBeep3()
			break
		case "custombeep4":
			cmds = customBeep4()
			break
		case "custombeep5":
			cmds = customBeep5()
			break
		case "custombeep6":
			cmds = customBeep6()
			break			
		default:
			if (text) {
				cmds = parseComplexCommand(text)
			}
	}
	if (!cmds) {
		logDebug "'$text' is not a valid command."
	}
	else {
		return cmds
	}
}

def cleanTextCmd(text) {
	return "$text".
		toLowerCase().
		replace(",", "_").
		replace(" ", "").
		replace("(", "").
		replace(")", "")
}

def parseComplexCommand(text) {	
	def cmds = []
	def args = getComplexCmdArgs(text)
	switch (args?.size()) {
		case 3:
			cmds += customAlarm(
				args[0],
				args[1],
				args[2])
			break
		case 4:
			cmds += delayedAlarm(
				args[0],
				args[1],
				args[2],
				args[3])
			break
		case 5:
			cmds += customBeep(
				args[0],
				args[1],
				args[2],
				args[3],
				args[4])
			break
		case 7:
			cmds += startCustomBeep(
				args[0],
				args[1],
				args[2],
				args[3],
				args[4],
				args[5],
				args[6])
			break
	}	
	return cmds
}

private getComplexCmdArgs(text) {
	for (prefix in ["customalarm", "delayedalarm","startcustombeep","custombeep"]) {
		text = removeCmdPrefix(text, prefix)
	}
	
	def args = text.tokenize("_")
	if (args.every { node -> isInt(node) }) {
		return args
	}
	else {
		return null
	}	
}

private removeCmdPrefix(text, prefix) {
	if (text.startsWith(prefix)) {
		return text.replace("$prefix_", "").replace(prefix, "")
	}
	else {
		return text
	}
}

// Turns on siren and strobe
def on() {
	both()
}

// Turns off siren and strobe
def off() {
	logDebug "Executing off() command"
	changeStatus("off")
	turnOff()
}

private turnOff() {	
	delayBetween([
		switchOffSetCmd(),
		switchGetCmd()
	], 100)
}

// Turns on siren and strobe
def strobe() {
	both()
}

// Turns on siren and strobe
def siren() {
	both()
}

// Turns on siren and strobe
def both() {
	logDebug "Executing both() command"
	
	if (settings.useBeepDelayedAlarm) {
		startBeepDelayedAlarm()
	}
	else {
		changeStatus("alarm")
		playDefaultAlarm()
	}
}

// Repeatedly plays the default beep based on the beepEvery and beepStopAfter settings and then turns on alarm.
def startBeepDelayedAlarm() {
	changeStatus("beepDelayedAlarm")
	startDefaultBeepSchedule()	
}

private playPendingAlarm() {
	state.alarmPending = false
	if (state.scheduledAlarm) {
		def sound = state.scheduledAlarm?.sound
		def volume = state.scheduledAlarm?.volume
		def duration = state.scheduledAlarm?.duration
		state.scheduledAlarm = null
		customAlarm(sound, volume, duration)
	}
	else {
		playDefaultAlarm()
	}		
}

private playDefaultAlarm() {
	playAlarm(settings.sirenSound, settings.sirenVolume, settings.alarmDuration)
}

// Plays sound at volume for duration.
def customAlarm(sound, volume, duration) {
	changeStatus("customAlarm")
	playAlarm(sound, volume, duration)
}

private playAlarm(sound, volume, duration) {
	def durationMsg = (duration && duration > 0) ? ", duration: $duration" : ""
	logDebug "Sounding Alarm: [sound: $sound, volume: $volume$durationMsg]"
	
	sound = validateSound(sound)
	volume = validateVolume(volume)
	duration = validateRange(duration, 0, 0, Integer.MAX_VALUE, "Alarm Duration")

	if (currentStatus() in ["alarm", "delayedAlarm", "beepDelayedAlarm"]) {
		sendEvent(name:"alarm", value: "both", isStateChange: true)
		sendEvent(name:"switch", value: "on", isStateChange: true, displayed: false)
	}
	
	def cmds = []
	cmds << sirenSoundVolumeSetCmd(sound, volume)

	if (duration > 0) {
		cmds << "delay ${duration * 1000}"
		cmds += turnOff()
	}

	return cmds	
}

def delayedAlarm(sound, volume, duration, delay) {
	changeStatus("delayedAlarm")
	startDelayedAlarm(sound, volume, duration, delay)	
}

private startDelayedAlarm(sound, volume, duration, delay) {
	state.scheduledAlarm = [
		"sound": sound,
		"volume": volume,
		"duration": duration
	]
	
	delay = validateRange(delay, 3, 1, Integer.MAX_VALUE, "delay")
	
	logDebug "Starting ${currentStatus()} [sound: $sound, volume: $volume, duration: $duration, delay: $delay]"
	
	def result = []
	result << "delay ${delay * 1000}"
	result << basicGetCmd()		
	return result
}

// Plays the default beep.
def beep() {
	changeStatus("beep")
	playDefaultBeep()	
}

private playDefaultBeep() {
	playBeep(
		settings.beepSound,
		settings.beepVolume,
		settings.beepRepeat,
		settings.beepRepeatDelay,
		settings.beepLength
	)
}

// Plays short beep.
def customBeep1() {
	customBeep(3, 1, 1, 0, 50)
}

// Plays medium beep
def customBeep2() {
	customBeep(3, 1, 1, 0, 100)
}

// Plays long beep
def customBeep3() {
	customBeep(3, 1, 1, 0, 250)
}

// Plays 3 short beeps
def customBeep4() {
	customBeep(3, 1, 3, 0, 50)
}

// Plays 3 medium beeps
def customBeep5() {
	customBeep(3, 1, 3, 100 , 100)
}

// Plays 3 long beeps
def customBeep6() {
	customBeep(3, 1, 3, 150, 200)
}

// Repeatedly plays the default beep based on the beepEvery and beepStopAfter settings.
def startBeep() {
	changeStatus("beepSchedule")
	startDefaultBeepSchedule()	
}

private startDefaultBeepSchedule() {
	startBeepSchedule(
		settings.beepEvery,
		settings.beepStopAfter,
		settings.beepSound,
		settings.beepVolume,
		settings.beepRepeat,
		settings.beepRepeatDelay,
		settings.beepLength
	)
}

// Repeatedly plays specified beep at specified in specified intervals.
def startCustomBeep(beepEverySeconds, stopAfterSeconds, sound, volume, repeat=1, repeatDelayMS=1000, beepLengthMS=100) {	
	changeStatus("customBeepSchedule")
	
	startBeepSchedule(beepEverySeconds, stopAfterSeconds, sound, volume, repeat, repeatDelayMS, beepLengthMS)	
}

private startBeepSchedule(beepEverySeconds, stopAfterSeconds, sound, volume, repeat, repeatDelayMS, beepLengthMS) {
	logDebug "Starting ${currentStatus()} [beepEverySeconds: $beepEverySeconds, stopAfterSeconds: $stopAfterSeconds]"
	
	state.beepSchedule = [
		"startTime": (new Date().time),
		"beepEvery": validateBeepEvery(beepEverySeconds),
		"stopAfter": validateBeepStopAfter(stopAfterSeconds),
		"sound": sound,
		"volume": volume,
		"repeat": repeat,
		"repeatDelay": repeatDelayMS,
		"beepLength": beepLengthMS
	]

	return playScheduledBeep()
}

private playScheduledBeep() {
	def beepSchedule = state.beepSchedule

	def cmds = []
	if (beepScheduleStillActive(beepSchedule?.startTime, beepSchedule?.stopAfter)) {
		cmds += playBeep(
			beepSchedule.sound,
			beepSchedule.volume,
			beepSchedule.repeat,
			beepSchedule.repeatDelay,
			beepSchedule.beepLength
		)
	}

	if (nextScheduledBeepStillActive()) {		
		if (beepSchedule.beepEvery > 0) {
			cmds << "delay ${beepSchedule.beepEvery * 1000}"
		}		
		cmds << basicGetCmd()
	}
	else {		
		state.beepSchedule = null
		state.beepScheduleRunning = false
		
		if (state.alarmPending) {		
			cmds += playPendingAlarm()
		}
		else {
			cmds += turnOff()
		}
	}
	return cmds
}

private nextScheduledBeepStillActive() {	
	def sched = state.beepSchedule
	
	if (sched?.beepEvery != null) {	
		def adjustedStartTime = (sched.startTime - (sched.beepEvery * 1000))
		return beepScheduleStillActive(adjustedStartTime, sched.stopAfter)
	} 
	else {		
		return false
	}
}

private beepScheduleStillActive(startTime, stopAfter) {
	if (startTime && stopAfter) {		
		def endTimeMS = startTime + (stopAfter * 1000)
		return (new Date().time < endTimeMS) && state.beepScheduleRunning
	}
	else {		
		return false
	}
}

// Plays specified beep.
def customBeep(sound, volume, repeat=1, repeatDelayMS=1000, beepLengthMS=100) {
	changeStatus("customBeep")
	playBeep(sound, volume, repeat, repeatDelayMS, beepLengthMS)
}

private playBeep(sound, volume, repeat, repeatDelayMS, beepLengthMS) {
	logDebug "${currentStatus()} [sound: $sound, volume: $volume, repeat: $repeat, repeatDelayMS: $repeatDelayMS, beepLengthMS: $beepLengthMS]"

	int maxMS = 18000
	sound = validateSound(sound, 3)
	volume = validateVolume(volume)
	beepLengthMS = validateRange(beepLengthMS, 100, 0, maxMS, "Beep Length")
	repeatDelayMS = validateRepeatDelay(repeatDelayMS, beepLengthMS, maxMS)
	repeat = validateRepeat(repeat, beepLengthMS, repeatDelayMS, maxMS)

	def cmds = []
	for (int repeatIndex = 1; repeatIndex <= repeat; repeatIndex++) {
		cmds << sirenSoundVolumeSetCmd(sound, volume)
		
		if (beepLengthMS > 0) {
			cmds << "delay $beepLengthMS"
		}
		
		cmds << switchOffSetCmd()
		
		if (repeat > 1 && repeatDelayMS > 0) {
			cmds << "delay $repeatDelayMS"
		}
	}

	if (!state.beepScheduleRunning && !state.alarmPending && currentStatus() != "off") {
		cmds << turnOff()
	}	
	return cmds
}

private changeStatus(newStatus) {
	def oldStatus = currentStatus()

	finalizeOldStatus(oldStatus, newStatus)
	
	if (newStatus in ["delayedAlarm", "beepDelayedAlarm"]) {
		state.alarmPending = true
	}
	
	if (newStatus in ["beepDelayedAlarm", "beepSchedule", "customBeepSchedule"]) {
		state.beepScheduleRunning = true
	}
	
	def displayStatus = (
		oldStatus != newStatus && 
		newStatus != "alarm" && 
		!(oldStatus in ["alarm", "delayedAlarm", "beepDelayedAlarm"]))
	
	sendEvent(name:"status", value: newStatus, isStateChange: true, displayed: displayStatus)
}

private finalizeOldStatus(oldStatus, newStatus) {
	if (state.alarmPending && 
	oldStatus in ["delayedAlarm", "beepDelayedAlarm"] &&
	!(newStatus in ["alarm", "customAlarm"])) {
		logDebug "Delayed Alarm Cancelled"			
	}
	else if (state.beepScheduleRunning) {
		if (nextScheduledBeepStillActive()) {
			logDebug "Beep Schedule Cancelled"
		}
		else {
			logDebug "Beep Schedule Completed"
		}
	}	
	state.alarmPending = false
	state.beepScheduleRunning = false		
	state.scheduledAlarm = null
	state.beepSchedule = null
}

// Stores preferences and displays device settings.
def updated() {
	if (!isDuplicateCommand(state.lastUpdated, 1000)) {
		state.lastUpdated = new Date().time
		
		def cmds = []		
		if (!state.useSecureCommands) {
			cmds << supportedSecurityGetCmd()	
		}
		
		cmds += configure()
		
		return response(delayBetween(cmds, 200))
	}
}

private isDuplicateCommand(lastExecuted, allowedMil) {
	!lastExecuted ? false : (lastExecuted + allowedMil > new Date().time) 
}

def configure() {
	def cmds = []	
	if (state.useSecureCommands != null) {
		logDebug "Secure Commands ${state.useSecureCommands ? 'Enabled' : 'Disabled'}"
		cmds << sendNotificationsSetCmd()
		cmds << manufacturerGetCmd()
		cmds << versionGetCmd()
		cmds << switchGetCmd()		
	}
	else {
		cmds << response(supportedSecurityGetCmd())
	}
	return cmds
}

// Parses incoming message warns if not paired securely
def parse(String description) {		
	if (description != null && description != "updated") {
		def cmd = zwave.parse(description, [0x25:1, 0x59:1, 0x70:1, 0x72:2, 0x85:2, 0x86:1, 0x98:1])

		if (cmd) {
			def result = zwaveEvent(cmd)
			
			result << createEvent(name: "lastPoll", value: new Date().time, displayed: false, isStateChange: true)
			
			return result
		}
		else {
			logDebug "Unable to parse: $description"
		}
	}
}

// Unencapsulates the secure command.
def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def result = []
	if (cmd) {
		def encapCmd = cmd.encapsulatedCommand([0x25:1, 0x59:1, 0x70:1, 0x72:2, 0x85:2, 0x86:1, 0x98:1])

		if (encapCmd) {	
			result = zwaveEvent(encapCmd)
		}
		else {
			log.debug "Unable to encapsulate: $cmd"
		}
	}
	return result
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport cmd) {
	state.useSecureCommands = true
	
	def cmds = []
	cmds << response("delay 2000")
	cmds += response(configure())
	
	return cmds
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	logDebug("$cmd")
	return []
}

def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd) {
	logDebug("$cmd")
	return []
}   

// Handles device reporting off and alarm turning on.
def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
	def result = []
	if (cmd.value == 0) {		
		changeStatus("off")
				
		def alarmDisplayed = (device.currentValue("alarm") == "both")	
		if (alarmDisplayed) {
			logDebug "Alarm is off"
		}
		
		result << createEvent(name:"alarm", value: "off", isStateChange: true, displayed: alarmDisplayed)
			
		result << createEvent(name:"switch", value: "off", isStateChange: true, displayed: false)		
	}
	return result
}

// Handles the scheduling of beeps.
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	def result = []
	result << response(playScheduledBeep())
	return result
}

// Writes unexpected commands to debug log
def zwaveEvent(physicalgraph.zwave.Command cmd) {
	logDebug("Unexpected Command: $cmd")
	def result = []
	result << createEvent(descriptionText: cmd.toString(), isStateChange: false)
	return result
}

private sirenSoundVolumeSetCmd(int sound, int volume) {
	//zwave.configurationV1.configurationSet(parameterNumber: 37, size: 2, configurationValue: [validateSound(sound), validateVolume(volume)])
	return configSetCmd(37, 2, [validateSound(sound), validateVolume(volume)])
}

private sendNotificationsSetCmd() {
	//return secureCmd(zwave.configurationV1.configurationSet(parameterNumber: 80, size: 1, scaledConfigurationValue: 0))
	return configSetCmd(80, 1, [0])
}

private configSetCmd(paramNumber, valSize, val) {
	return secureCmd(zwave.configurationV1.configurationSet(parameterNumber: paramNumber, size: valSize, configurationValue: val))
}

private switchOffSetCmd() {
	return secureCmd(zwave.switchBinaryV1.switchBinarySet(switchValue: 0))
}

private switchGetCmd() {
	return secureCmd(zwave.switchBinaryV1.switchBinaryGet())
}

private basicGetCmd() {
	return secureCmd(zwave.basicV1.basicGet())
}

private supportedSecurityGetCmd() {
	logDebug "Checking for Secure Command Support"
	
	state.useSecureCommands = true // force secure cmd			
	def cmd = secureCmd(zwave.securityV1.securityCommandsSupportedGet())
	state.useSecureCommands = false // reset secure cmd
	
	return cmd
}

private versionGetCmd() {
	return secureCmd(zwave.versionV1.versionGet())
}

private manufacturerGetCmd() {
	secureCmd(zwave.manufacturerSpecificV2.manufacturerSpecificGet())
}

private secureCmd(physicalgraph.zwave.Command cmd) {
	if (state.useSecureCommands) {		
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {		
		cmd.format()
	}
}

private int validateSound(sound, int defaultSound=1) {
	return validateRange(sound, defaultSound, 1, 5, "Sound")
}

private int validateVolume(volume, int defaultVolume=1) {
	return validateRange(volume, defaultVolume, 1, 3, "Volume")
}

private int validateRepeatDelay(repeatDelayMS, int beepLengthMS, int maxMS) {
	int repeatDelayMaxMS = (beepLengthMS == maxMS) ? 0 : (maxMS - beepLengthMS)
	return validateRange(repeatDelayMS, 1000, 0, repeatDelayMaxMS, "Repeat Delay")
}

private int validateRepeat(repeat, int beepLengthMS, int repeatDelayMS, int maxMS) {
	int combinedMS = (beepLengthMS + repeatDelayMS)
	int maxRepeat = (combinedMS >= maxMS) ? 0 : (maxMS / combinedMS).toInteger()
	return validateRange(repeat, 1, 0, maxRepeat, "Repeat")
}

private int validateBeepEvery(seconds) {
	validateRange(seconds, 10, 0, Integer.MAX_VALUE, "Beep Every")
}

private int validateBeepStopAfter(seconds) {
	validateRange(seconds, 60, 2, Integer.MAX_VALUE, "Beep Stop After")
}

private int validateRange(val, defaultVal, minVal, maxVal, desc) {
	def result
	def errorType = null
	if (isInt(val)) {
		result = val.toString().toInteger()
	}
	else {
		errorType = "invalid"
		result = defaultVal
	}
	
	if (result > maxVal) {
		errorType = "too high"
		result = maxVal
	} else if (result < minVal) {
		errorType = "too low"
		result = minVal
	} 

	if (errorType) {
		logDebug("$desc: $val is $errorType, using $result instead.")
	}
	return result
}

private isInt(val) {
	return val?.toString()?.isInteger() ? true : false
}

private currentStatus() {
	return device.currentValue("status") ? device.currentValue("status") : ""
}

private handleUnsupportedCmd(cmd) {
	logDebug "Command $cmd not supported"
}

private logDebug(msg) {
	if (settings.debugOutput != false) {
		log.debug "$msg"
	}
}