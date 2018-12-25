package alex.myapplication.Utils;

import android.support.annotation.Nullable;

public class ListenerHandler<T>  {
    private T listener;

    public ListenerHandler(final T listener) {
        this.listener = listener;
    }

    @Nullable
    public T getListener() {
        return listener;
    }

    void unregister() {
        listener = null;
    }
}
