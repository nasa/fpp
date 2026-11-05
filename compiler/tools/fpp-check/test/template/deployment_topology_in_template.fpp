# A deployment topology may not appear in a template definition,
# even if the template is not expanded
module template T() {
  deployment topology D {}
}
