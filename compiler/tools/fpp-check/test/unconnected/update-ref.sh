#!/bin/sh

basic()
{
  update "-u basic-unconnected.out.txt" basic
  mv basic-unconnected.out.txt basic-unconnected.ref.txt
}

internal()
{
  update "-u internal-unconnected.out.txt" internal
  mv internal-unconnected.out.txt internal-unconnected.ref.txt
}
