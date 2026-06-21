module M {

  port P

  active component Active {

    async input port p: P

  }

  passive component Passive {

    output port p: P

  }

}
