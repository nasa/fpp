#!/bin/sh

basic()
{
  update "-u basic-unconnected.out.txt" basic
  mv basic-unconnected.out.txt basic-unconnected.ref.txt
}
