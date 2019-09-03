import "Man.y" -> Man;
interface Color{
    int WHITE=0;
    int BLACK=0;
}
struct Cat{
    int color = Color.WHITE;
    Man owner;
}
export Cat;
