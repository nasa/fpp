pattern_command()
{
  run_test "-p $PWD" pattern_command && \
    diff_xml CmdRegPort CmdResponsePort CommandsComponent CComponent TTopologyApp
}
