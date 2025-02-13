module Components {
    @ Example Component for F Prime FSW framework.
    active component HelloWorld {

        # One async command/port is required for active components
        # This should be overridden by the developers with a useful command/port
        @ Command to issue greeting with maximum length of 20 characters
        async command SAY_HELLO(
            greeting: string size 20 @< Greeting to repeat in the Hello event
        )

        @ Greeting event with maximum greeting length of 20 characters
        event Hello(
            greeting: string size 20 @< Greeting supplied from the SAY_HELLO command
        ) severity activity high format "I say: {}"

        @ A count of the number of greetings issued
        telemetry GreetingCount: U32

        ##############################################################################
        #### Uncomment the following examples to start customizing your component ####
        ##############################################################################

        # @ Example async command
        # async command COMMAND_NAME(param_name: U32)

        # @ Example telemetry counter
        # telemetry ExampleCounter: U64

        # @ Example event
        # event ExampleStateEvent(example_state: Fw.On) severity activity high id 0 format "State set to {}"

        # @ Example port: receiving calls from the rate group
        # sync input port run: Svc.Sched

        # @ Example parameter
        # param PARAMETER_NAME: U32

        ###############################################################################
        # Standard AC Ports: Required for Channels, Events, Commands, and Parameters  #
        ###############################################################################
        @ Port for requesting the current time
        time get port timeCaller

        @ Port for sending command registrations
        command reg port cmdRegOut

        @ Port for receiving commands
        command recv port cmdIn

        @ Port for sending command responses
        command resp port cmdResponseOut

        @ Port for sending textual representation of events
        text event port logTextOut

        @ Port for sending events to downlink
        event port logOut

        @ Port for sending telemetry channels to downlink
        telemetry port tlmOut

        @ Port to return the value of a parameter
        param get port prmGetOut

        @Port to set the value of a parameter
        param set port prmSetOut

    }
}

module Main {
    port P

    passive component C {
        sync input port pIn: P
        output port pOut: P
        telemetry port tlmOut
        time get port timeGetOut
        telemetry T: U32
    }

    instance g: C base id 0x100
    instance h: C base id 0x200
    instance i: C base id 0x300
    instance j: C base id 0x400
    instance k: C base id 0x500
    instance l: C base id 0x600
    instance m: C base id 0x700

    topology b {
        instance g
        instance h
        instance i
        instance j
        instance k
        instance l
        instance m

        connections A {
            g.pOut -> h.pIn
            h.pOut -> i.pIn
            i.pOut -> j.pIn
            j.pOut -> k.pIn
            k.pOut -> l.pIn
            l.pOut -> m.pIn
        }

        command connections instance g

        event connections instance h

        param connections instance i

        telemetry connections instance j

        text event connections instance k

        time connections instance l

        health connections instance m

        telemetry packets P {

          packet P1 id 0 group 0 {
            g.T
            h.T
            i.T
          }

        } omit {
          j.T
          k.T
          l.T
        }

    }
}
