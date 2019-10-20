(* ----------------------------------------------------------------------
 * AstNode.sig
 * An AST node with an identifier
 * ----------------------------------------------------------------------*)

 signature AST_NODE =
 sig

   (* The type of a node identifier *)
   type id = int

   (* The type of a node with data of type 'a *)
   type 'a t

   (* Make a new node with the given data *)
   val node : 'a -> 'a t

   (* Make a node option *)
   val nodeOpt : 'a option -> 'a t option

   (* Make a list of nodes *)
   val nodeList : 'a list -> 'a t list

   (* Get the data out of a node *)
   val data : 'a t -> 'a

   (* Get the id out of a node *)
   val id: 'a t -> id

   (* Get the data out of a node list *)
   val dataList : 'a t list -> 'a list

   (* Get the data out of a node option *)
   val dataOpt : 'a t option -> 'a option

   (* A polymorphic ordered map from nodes to values *)
   structure Map : ORD_MAP

 end
