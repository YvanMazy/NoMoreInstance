package be.yvanmazy.nomoreinstance.object;

import org.jetbrains.annotations.Nullable;

public final class Wrapper<T> {

    private T value;

    public Wrapper(final @Nullable T value) {
        this.value = value;
    }

    public Wrapper() {
    }

    public @Nullable T getValue() {
        return this.value;
    }

    public void setValue(final @Nullable T value) {
        this.value = value;
    }

}