(* ----------------------------------------------------------------------
 * AstNode.sml
 * ----------------------------------------------------------------------*)

 structure AstNode :> AST_NODE =
 struct

   type id = int

   type 'a t = {
     data: 'a,
     id: int
   }

   val id = ref 0

   fun node data = 
   let
     val node = { data=data, id=(!id) }
   in 
     id := (!id) + 1; node
   end

   fun nodeOpt dataOpt = Option.map node dataOpt

   fun nodeList lst = List.map node lst

   fun data { data, id } = data

   fun id { data, id } = id

   fun dataList lst = List.map data lst

   fun dataOpt nodeOpt = Option.map data nodeOpt

   structure Map = RedBlackMapFn (
                     struct 
                       type ord_key = int
                       val compare = Int.compare
                     end
                   )

 end
