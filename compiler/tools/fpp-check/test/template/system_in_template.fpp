# A system definition may not appear in a template definition,
# even if the template is not expanded
module template T() {
  system S: D
}

deployment topology D {}
