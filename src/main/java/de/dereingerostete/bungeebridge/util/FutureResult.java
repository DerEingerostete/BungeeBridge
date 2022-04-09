package de.dereingerostete.bungeebridge.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@ToString
@EqualsAndHashCode
public class FutureResult<T> {
    protected final @NotNull Request request;
    protected @Nullable Throwable throwable;
    protected @Nullable T result;
    protected boolean done;

    public FutureResult(@NotNull Request request) {
        this.request = request;
        done = false;
    }

    public void finish(T result) {
        this.result = result;
        done = true;
    }

    @ApiStatus.Internal
    public void setThrowable(@NotNull Throwable throwable) {
        this.throwable = throwable;
        done = true;
    }

}
