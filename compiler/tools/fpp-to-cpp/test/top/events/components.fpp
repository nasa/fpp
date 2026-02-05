module M {

  passive component C {

    time get port timeGetOut

    event port eventOut

    text event port textEventOut

    event E severity activity low format "event E"

  }

  passive component EventManager {

    sync input port eventIn: Fw.Log

    sync input port textEventIn: Fw.LogText

  }

  passive component NoEvents {

    time get port timeGetOut

    event port eventOut

    text event port textEventOut

  }

}
