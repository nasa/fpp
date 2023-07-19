#!/bin/sh

. ../../../scripts/test-utils.sh

fpp_to_json=../../../bin/fpp-to-json

update()
{
  args=$1
  infile=$2
  if test -n "$3"
  then
    outfile=$3
  else
    outfile=$infile
  fi
  echo "updating $outfile.ref.txt"
  $fpp_to_json $args $infile.fpp 2>&1 | remove_path_prefix > $outfile.ref.txt
  remove_path_prefix < fpp-ast.json >> $outfile.ref.txt
  remove_path_prefix < fpp-loc-map.json >> $outfile.ref.txt
  remove_path_prefix < fpp-analysis.json >> $outfile.ref.txt
  rm fpp-ast.json fpp-loc-map.json fpp-analysis.json
}

for file in `find . -name '*.ref.txt'`
do
  rm $file
done

update "" constants
update "" modules
update "" types
update "" enums
update "" ports
update "" simpleComponents
update "" specialPorts
update "" internalPorts
update "" commands
update "" events
update "" telemetry
update "" parameters
update "" constTypesComponents
update "" matchedPorts
update "" passiveComponent
update "" queuedComponets
update "" activeComponents
update "" simpleTopology
update "" importedTopologies

