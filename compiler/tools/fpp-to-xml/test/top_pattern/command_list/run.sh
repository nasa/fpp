pattern_command_list()
{
  run_test "-p $PWD" pattern_command_list && \
    diff_xml CmdRegPort CmdResponsePort CommandsComponent CComponent TTopologyApp
}
