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
    void println(Any any);
}

struct Reader extends Any{
    String readLine();
}

interface System{
    Printer out = null;
    Reader in = null;
}


export Any, String, System;
