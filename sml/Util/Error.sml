(* ----------------------------------------------------------------------
 * Error.sml
 * ----------------------------------------------------------------------*)

structure Error =
struct

  val toolOpt = ref (NONE : Tool.t option)

  datatype context = Context of {
    loc : Loc.t,
    writer : TextIO.outstream -> unit
  }

  exception InternalError of string
  exception RedefinitionError of {
    message : string,
    context : context,
    previousContext : context
  }
  exception SemanticError of {
    message : string,
    context : context
  }
  exception SyntaxError of Loc.t * string

  fun setTool tool = toolOpt := (SOME tool)

  fun write s = TextIO.output (TextIO.stdErr, s)

  fun displayTool () = 
    case !toolOpt of
         SOME (Tool.Tool tool) => write ((#name tool)^"\n")
       | NONE => ()

  fun displayLoc locOpt =
    case locOpt of
         SOME loc => write ((Loc.show loc)^"\n")
       | NONE => ()

  fun display locOpt msg = (
    displayTool ();
    displayLoc locOpt;
    msg TextIO.stdErr;
    write "\n"
  )

  fun msg s os = TextIO.output (os, s)

  fun displayStr locOpt s = display locOpt (msg s)

  fun abort locOpt s = (displayStr locOpt s; OS.Process.failure)

  fun show (InternalError s) = 
        displayStr NONE ("internal error: "^s)
    | show (RedefinitionError {message, context, previousContext} ) =
        let
          val Context c = context
          val Context pc = previousContext
          val previousLoc = Loc.show (#loc pc)
        in
          displayStr (SOME (#loc c)) (message^":");
          (#writer c) TextIO.stdErr;
          write ("previous occurrence was at "^previousLoc^": \n");
          (#writer pc) TextIO.stdErr
        end
    | show (SemanticError {message, context=Context c}) = (
        displayStr (SOME (#loc c)) (message^":");
        (#writer c) TextIO.stdErr
      )
    | show (SyntaxError (loc, s)) = displayStr (SOME loc) s
    | show (File.CannotOpen s) = displayStr NONE ("cannot open file "^s)
    | show (File.DoesNotExist s) = displayStr NONE ("file "^s^" does not exist")
    | show (IO.Io { name=name, ... }) =
        displayStr NONE ("I/O error occurred accessing file "^name)
    | show e = displayStr NONE "unknown error occurred"

  fun handleExn e = (show e; OS.Process.failure)

end
