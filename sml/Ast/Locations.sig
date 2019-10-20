(* ----------------------------------------------------------------------
 * Locations.sig
 * Manage locations of AST Nodes
 * ----------------------------------------------------------------------*)

 signature LOCATIONS =
 sig

   (* Insert a location *)
   val insert : 'a AstNode.t * Loc.t -> unit
   
   (* Get a location *)
   val lookup : 'a AstNode.t -> Loc.t
   val find : 'a AstNode.t -> Loc.t option

 end
