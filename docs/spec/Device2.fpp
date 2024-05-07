# Define the data structure for events that carry data
struct FaultData {
    id: U32
    data: U32
}

struct PowerData {
    level: F32
}

# Start of the state machine definition
state machine Device {

# Specify state machine events
    event RTI
    event Complete
    event Calibrate
    event Fault: FaultData
    event Drive
    event Stop
    event Resume
    event POR
    event PowerOn: PowerData
    event PowerOff

# Specify actions
    action setPower: PowerData
    action initPower
    action reportFault: FaultData
    action motorControl
    action doCalibrate
    action doSafe
    action doDiagnostics

# Specify guards
    guard coldStart
    guard noRecovery: FaultData
    guard calibrateReady

# Specify states and junctions

    initial junction j1

    transition j1 -> DeviceOff guard coldStart
    transition j1 -> DeviceOn action initPower

    state DeviceOff

    transition DeviceOff -> DeviceOn event PowerOn action setPower

    state DeviceOn {

        initial state Initializing

        state Idle

        state Calibrating {
            transition event RTI action doCalibrate
        }

        state Driving {
            transition event RTI action motorControl
        }

        transition Driving -> Idle event Stop
        transition Calibrating -> Idle action reportFault
        transition Calibrating -> Idle event Complete
        transition Idle -> Driving event Drive
        transition Idle -> Calibrating event Calibrate guard calibrateReady
        transition Initializing -> Idle event Complete

    }

    junction j2

    transition DeviceOn -> DeviceOn event POR
    transition DeviceOn -> j2 event Fault
    transition j2 -> Diagnostics guard noRecovery
    transition j2 -> Recovery action reportFault
    transition DeviceOn -> DeviceOff event PowerOff

    state Recovery {
        transition event RTI action doSafe
    }

    state Diagnostics {
        transition event RTI action doDiagnostics
    }

    transition Diagnostics -> DeviceOn event Resume
    transition Recovery -> Diagnostics event Complete

}
