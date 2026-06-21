package com.lightning.northstar.client.gui;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListScrollInput<T> extends SelectionScrollInput {

    private List<T> options = List.of();
    private Function<T, Component> formatter = value -> Component.literal(String.valueOf(value));

    public ListScrollInput(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ListScrollInput<T> formatter(Function<T, Component> formatter) {
        this.formatter = formatter;
        super.forOptions(options.isEmpty() ? List.of(Component.empty()) : options.stream().map(formatter).toList());
        return this;
    }

    public void setState(T state) {
        setState(options.indexOf(state));
    }

    public ListScrollInput<T> options(List<T> options) {
        this.options = new ArrayList<>(options);
        this.state = Math.max(Math.min(state, options.size() - 1), 0);
        super.forOptions(options.isEmpty() ? List.of(Component.empty()) : options.stream().map(formatter).toList());
        onChanged();
        return this;
    }

    public List<T> options() {
        return options;
    }

    public T get() {
        return options.isEmpty() ? null : options.get(state);
    }

    @Override
    public ScrollInput forOptions(List<? extends Component> options) {
        throw new RuntimeException("Operation not allowed, use #options(List<T>) instead.");
    }

}
