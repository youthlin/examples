import "Any.y" -> Any;
import "String.y" -> String;

struct Printer extends Any{
    void println(Any any);
}

struct Reader extends Any{
    String readLine();
}

interface System{
    Printer out = null;
    Reader in = null;
}

export System;
