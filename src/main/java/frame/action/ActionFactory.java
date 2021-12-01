package frame.action;

public interface ActionFactory{
    //1: left
    //2: right
    //3: mid
    Action createAction(int x, int y, int mouseButton);
}
