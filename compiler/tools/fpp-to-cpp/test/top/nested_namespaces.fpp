module M {

  module N {

    module O {

      passive component C {

      }

    }

  }

}

instance c: M.N.O.C base id 0x100

module M {

  topology NestedNamespaces {

    instance c

  }

}
