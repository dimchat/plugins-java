
import chat.dim.plugins.ExtensionLoader;
import chat.dim.plugins.NativePluginLoader;
import chat.dim.plugins.PluginLoader;

public class LibraryLoader implements Runnable {

    private final ExtensionLoader extensionLoader;
    private final PluginLoader pluginLoader;
    private final NativePluginLoader nativePluginLoader;

    private boolean loaded = false;

    public LibraryLoader(ExtensionLoader extensionLoader, PluginLoader pluginLoader, NativePluginLoader nativeLoader) {
        if (extensionLoader == null) {
            this.extensionLoader = new ExtensionLoader();
        } else {
            this.extensionLoader = extensionLoader;
        }
        if (pluginLoader == null) {
            this.pluginLoader = new PluginLoader();
        } else {
            this.pluginLoader = pluginLoader;
        }
        if (nativeLoader == null) {
            this.nativePluginLoader = new NativePluginLoader();
        } else {
            this.nativePluginLoader = nativeLoader;
        }
    }

    public LibraryLoader() {
        this(null, null, null);
    }

    @Override
    public void run() {
        if (loaded) {
            // no need to load it again
            return;
        } else {
            // mark it to loaded
            loaded = true;
        }
        // try to load all plugins
        load();
    }

    protected void load() {
        extensionLoader.load();
        pluginLoader.load();
        nativePluginLoader.load();
    }

}
