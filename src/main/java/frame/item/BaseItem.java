package frame.item;

import javax.swing.*;

public abstract class BaseItem {

    public Icon icon;
    public boolean isEnable = true;

    public BaseItem(Icon icon) {
        this.icon = icon;
    }

//    public void use(ItemActionEvent action) {
//        if (action == null) return;
//        EventCenter.publish(action);
//    }
}
