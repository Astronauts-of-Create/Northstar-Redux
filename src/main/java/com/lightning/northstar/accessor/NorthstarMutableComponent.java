package com.lightning.northstar.accessor;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public interface NorthstarMutableComponent {

    default MutableComponent northstar$self() {
        return (MutableComponent) this;
    }

    default MutableComponent northstar$onClick(ClickEvent.Action action, String value) {
        MutableComponent self = northstar$self();
        return self.setStyle(self.getStyle().withClickEvent(new ClickEvent(action, value)));
    }

    default MutableComponent northstar$changePage(String page) {
        return northstar$onClick(ClickEvent.Action.CHANGE_PAGE, page);
    }

    default <T> MutableComponent northstar$onHover(HoverEvent.Action<T> action, T value) {
        MutableComponent self = northstar$self();
        return self.setStyle(self.getStyle().withHoverEvent(new HoverEvent(action, value)));
    }

    default MutableComponent northstar$color(int rgb) {
        MutableComponent self = northstar$self();
        return self.setStyle(self.getStyle().withColor(rgb));
    }

}
