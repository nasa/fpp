# Test a transtitive dependence from an instance to a port
# through a component, using different namespaces
# Documents the fix to issue #110 on GitHub

locate component B.C at "transitive_instance_B.fpp"
locate port B.P at "transitive_instance_P.fpp"

module A {

  instance b: B.C base id 0x100

}
