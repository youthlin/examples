/** 所有类型的父类型 */
struct Any{
    native String toString();
}

struct String extends Any{
    char[] value;
    String toString(){
        return this;
    }
}

struct Printer extends Any{
    native void println(Any any);
}

struct Reader extends Any{
    native String readLine();
}

interface System{
    Reader in = Reader();
    Printer out = Printer();
}


export Any, String, System;
