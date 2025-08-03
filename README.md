# DIM Plugins (Java)


[![License](https://img.shields.io/github/license/dimchat/plugins-java)](https://github.com/dimchat/plugins-java/blob/master/LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/dimchat/plugins-java/pulls)
[![Platform](https://img.shields.io/badge/Platform-Java%208-brightgreen.svg)](https://github.com/dimchat/plugins-java/wiki)
[![Issues](https://img.shields.io/github/issues/dimchat/plugins-java)](https://github.com/dimchat/plugins-java/issues)
[![Repo Size](https://img.shields.io/github/repo-size/dimchat/plugins-java)](https://github.com/dimchat/plugins-java/archive/refs/heads/main.zip)
[![Tags](https://img.shields.io/github/tag/dimchat/plugins-java)](https://github.com/dimchat/plugins-java/tags)
[![Version](https://img.shields.io/maven-central/v/chat.dim/Plugins)](https://mvnrepository.com/artifact/chat.dim/Plugins)

[![Watchers](https://img.shields.io/github/watchers/dimchat/plugins-java)](https://github.com/dimchat/plugins-java/watchers)
[![Forks](https://img.shields.io/github/forks/dimchat/plugins-java)](https://github.com/dimchat/plugins-java/forks)
[![Stars](https://img.shields.io/github/stars/dimchat/plugins-java)](https://github.com/dimchat/plugins-java/stargazers)
[![Followers](https://img.shields.io/github/followers/dimchat)](https://github.com/orgs/dimchat/followers)

## Plugins

1. Data Coding
   * Base-58
   * Base-64
   * Hex
   * UTF-8
   * JsON
   * PNF _(Portable Network File)_
   * TED _(Transportable Encoded Data)_
2. Digest Digest
   * MD-5
   * SHA-1
   * SHA-256
   * Keccak-256
   * RipeMD-160
3. Cryptography
   * AES-256 _(AES/CBC/PKCS7Padding)_
   * RSA-1024 _(RSA/ECB/PKCS1Padding)_, _(SHA256withRSA)_
   * ECC _(Secp256k1)_
4. Address
   * BTC
   * ETH
5. Meta
   * MKM _(Default)_
   * BTC
   * ETH
6. Document
   * Visa _(User)_
   * Profile
   * Bulletin _(Group)_

## Extends

### Address

```java
package chat.dim.compat;

import chat.dim.mem.*;
import chat.dim.protocol.Address;

public class CompatibleAddressFactory extends BaseAddressFactory {

    @Override
    protected Address parse(String address) {
        if (address == null) {
            //throw new NullPointerException("address empty");
            assert false : "address empty";
            return null;
        }
        int len = address.length();
        if (len == 0) {
            assert false : "address empty";
            return null;
        } else if (len == 8) {
            // "anywhere"
            if (Address.ANYWHERE.equalsIgnoreCase(address)) {
                return Address.ANYWHERE;
            }
        } else if (len == 10) {
            // "everywhere"
            if (Address.EVERYWHERE.equalsIgnoreCase(address)) {
                return Address.EVERYWHERE;
            }
        }
        Address res;
        if (26 <= len && len <= 35) {
            res = BTCAddress.parse(address);
        } else if (len == 42) {
            res = ETHAddress.parse(address);
        } else {
            //throw new AssertionError("invalid address: " + address);
            res = null;
        }
        //
        //  TODO: parse for other types of address
        //
        if (res == null && 4 <= len && len <= 64) {
            res = new UnknownAddress(address);
        }
        assert res != null : "invalid address: " + address;
        return res;
    }

}
```

```java
package chat.dim.compat;

import chat.dim.protocol.Address;
import chat.dim.type.ConstantString;

public final class UnknownAddress extends ConstantString implements Address {

    public UnknownAddress(String string) {
        super(string);
    }

    @Override
    public int getNetwork() {
        return 0;
    }

}
```

### Meta

```java
package chat.dim.compat;

import java.util.Map;

import chat.dim.mkm.*;
import chat.dim.plugins.SharedAccountExtensions;
import chat.dim.protocol.Meta;

public final class CompatibleMetaFactory extends BaseMetaFactory {

    public CompatibleMetaFactory(String algorithm) {
        super(algorithm);
    }

    @Override
    public Meta parseMeta(Map<String, Object> meta) {
        Meta out;
        String type = SharedAccountExtensions.helper.getMetaType(meta, "");
        switch (type) {

            case "MKM":
            case "mkm":
            case "1":
                out = new DefaultMeta(meta);
                break;

            case "BTC":
            case "btc":
            case "2":
                out = new BTCMeta(meta);
                break;

            case "ETH":
            case "eth":
            case "4":
                out = new ETHMeta(meta);
                break;

            default:
                throw new IllegalArgumentException("unknown meta type: " + type);
        }
        return out.isValid() ? out : null;
    }
}
```

### Plugin Loader

```java
package chat.dim.compat;

import chat.dim.plugins.PluginLoader;
import chat.dim.protocol.*;

public class CommonPluginLoader extends PluginLoader {

    @Override
    protected void registerAddressFactory() {
        Address.setFactory(new CompatibleAddressFactory());
    }

    @Override
    protected void registerMetaFactories() {

        Meta.Factory mkm = new CompatibleMetaFactory(MetaType.MKM);
        Meta.Factory btc = new CompatibleMetaFactory(MetaType.BTC);
        Meta.Factory eth = new CompatibleMetaFactory(MetaType.ETH);

        Meta.setFactory("1", mkm);
        Meta.setFactory("2", btc);
        Meta.setFactory("4", eth);

        Meta.setFactory("mkm", mkm);
        Meta.setFactory("btc", btc);
        Meta.setFactory("eth", eth);

        Meta.setFactory("MKM", mkm);
        Meta.setFactory("BTC", btc);
        Meta.setFactory("ETH", eth);
    }

}
```

## Usage

You must load all plugins before your business run:

```java
package chat.dim.compat;

import chat.dim.plugins.ExtensionLoader;
import chat.dim.plugins.PluginLoader;

public class LibraryLoader implements Runnable {

    private final ExtensionLoader extensionLoader;
    private final PluginLoader pluginLoader;

    public LibraryLoader(ExtensionLoader extensionLoader, PluginLoader pluginLoader) {

        if (extensionLoader == null) {
            this.extensionLoader = new ExtensionLoader();
        } else {
            this.extensionLoader = extensionLoader;
        }

        if (pluginLoader == null) {
            this.pluginLoader = new CommonPluginLoader();
        } else {
            this.pluginLoader = pluginLoader;
        }
    }

    @Override
    public void run() {
        extensionLoader.run();
        pluginLoader.run();
    }
    
    public static void main(String[] args) {
        
        LibraryLoader loader = new LibraryLoader();
        loader.run();
        
        // do your jobs after all extensions & plugins loaded
    }

}
```

You must ensure that every ```Address``` you extend has a ```Meta``` type that can correspond to it one by one.

----

Copyright &copy; 2018-2025 Albert Moky
[![Followers](https://img.shields.io/github/followers/moky)](https://github.com/moky?tab=followers)