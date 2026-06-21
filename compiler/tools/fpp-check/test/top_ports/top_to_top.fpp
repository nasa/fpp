port P

queued component Sender {
    internal port replyInternal()

    output port msgOut: P
    sync input port replyIn: P
}

passive component Receiver {
    sync input port msgIn: P
    output port replyOut: P
}

instance sender: Sender base id 1 queue size 10
instance receiver: Receiver base id 2

topology TopPorts {
    instance SenderTop
    instance ReceiverTop

    connections Top2Top {
        SenderTop.msgOut[0] -> ReceiverTop.msgIn[0]
        ReceiverTop.replyOut[0] -> SenderTop.replyIn
    }
}

topology SenderTop {
    instance sender

    port msgOut = sender.msgOut
    port replyIn = sender.replyIn
}

topology ReceiverTop {
    instance receiver

    port msgIn = receiver.msgIn
    port replyOut = receiver.replyOut
}
