(* ----------------------------------------------------------------------
 * Locations.sml
 * ----------------------------------------------------------------------*)

 structure Locations :> LOCATIONS =
 struct

   local
     val hashFn = Word.fromInt
     fun cmpFn (x: int, y: int) = (x = y)
     val size = 1000
     val err = Error.InternalError "unknown location"
   in
     val table : (int, Loc.t) HashTable.hash_table = 
       HashTable.mkTable (hashFn, cmpFn) (size, err)
   end

   fun insert (node, loc) = HashTable.insert table (AstNode.id node, loc)

   fun lookup node =
     HashTable.lookup table (AstNode.id node)

   fun find node =
     HashTable.find table (AstNode.id node)

 end
