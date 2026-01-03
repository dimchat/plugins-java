/* license: https://mit-license.org
 *
 *  DIMP : Decentralized Instant Messaging Protocol
 *
 *                                Written in 2022 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Albert Moky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ==============================================================================
 */
package chat.dim.ext;

import java.util.HashMap;
import java.util.Map;

import chat.dim.data.Converter;
import chat.dim.data.Wrapper;
import chat.dim.protocol.Address;
import chat.dim.protocol.Document;
import chat.dim.protocol.DocumentType;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;
import chat.dim.protocol.SignKey;
import chat.dim.protocol.TransportableData;
import chat.dim.protocol.VerifyKey;

/**
 *  Account GeneralFactory
 */
public class AccountGeneralFactory implements GeneralAccountHelper,
                                              AddressHelper, IDHelper,
                                              MetaHelper, DocumentHelper {

    private Address.Factory addressFactory = null;

    private ID.Factory idFactory = null;

    private final Map<String, Meta.Factory> metaFactories = new HashMap<>();

    private final Map<String, Document.Factory> _docsFactories = new HashMap<>();

    @Override
    public String getMetaType(Map<?, ?> meta, String defaultValue) {
        return Converter.getString(meta.get("type"), defaultValue);
    }

    @Override
    public String getDocumentType(Map<?, ?> doc, String defaultValue) {
        String type = Converter.getString(doc.get("type"), null);
        if (type != null && type.length() > 0 && !type.equals("*")) {
            return type;
        } else if (defaultValue != null) {
            return defaultValue;
        }
        // get type for did
        ID did = ID.parse(doc.get("did"));
        if (did == null) {
            assert false : "document error: " + doc;
            return null;
        } else if (did.isUser()) {
            return DocumentType.VISA;
        } else if (did.isGroup()) {
            return DocumentType.BULLETIN;
        } else {
            return DocumentType.PROFILE;
        }
    }

    //
    //  Address Helper
    //

    @Override
    public void setAddressFactory(Address.Factory factory) {
        addressFactory = factory;
    }

    @Override
    public Address.Factory getAddressFactory() {
        return addressFactory;
    }

    @Override
    public Address parseAddress(Object address) {
        if (address == null) {
            return null;
        } else if (address instanceof Address) {
            return (Address) address;
        }
        String str = Wrapper.getString(address);
        if (str == null) {
            assert false : "address error: " + address;
            return null;
        }
        Address.Factory factory = getAddressFactory();
        assert factory != null : "address factory not ready";
        return factory.parseAddress(str);
    }

    @Override
    public Address generateAddress(Meta meta, int network) {
        Address.Factory factory = getAddressFactory();
        assert factory != null : "address factory not ready";
        return factory.generateAddress(meta, network);
    }

    //
    //  ID Helper
    //

    @Override
    public void setIDFactory(ID.Factory factory) {
        idFactory = factory;
    }

    @Override
    public ID.Factory getIDFactory() {
        return idFactory;
    }

    @Override
    public ID parseID(Object did) {
        if (did == null) {
            return null;
        } else if (did instanceof ID) {
            return (ID) did;
        }
        String str = Wrapper.getString(did);
        if (str == null) {
            assert false : "ID error: " + did;
            return null;
        }
        ID.Factory factory = getIDFactory();
        assert factory != null : "ID factory not ready";
        return factory.parseID(str);
    }

    @Override
    public ID createID(String name, Address address, String terminal) {
        ID.Factory factory = getIDFactory();
        assert factory != null : "ID factory not ready";
        return factory.createID(name, address, terminal);
    }

    @Override
    public ID generateID(Meta meta, int network, String terminal) {
        ID.Factory factory = getIDFactory();
        assert factory != null : "ID factory not ready";
        return factory.generateID(meta, network, terminal);
    }

    //
    //  Meta Helper
    //

    @Override
    public void setMetaFactory(String type, Meta.Factory factory) {
        metaFactories.put(type, factory);
    }

    @Override
    public Meta.Factory getMetaFactory(String type) {
        return metaFactories.get(type);
    }

    @Override
    public Meta createMeta(String type, VerifyKey key, String seed, TransportableData fingerprint) {
        Meta.Factory factory = getMetaFactory(type);
        assert factory != null : "meta type not found: " + type;
        return factory.createMeta(key, seed, fingerprint);
    }

    @Override
    public Meta generateMeta(String type, SignKey sKey, String seed) {
        Meta.Factory factory = getMetaFactory(type);
        assert factory != null : "meta type not found: " + type;
        return factory.generateMeta(sKey, seed);
    }

    @Override
    public Meta parseMeta(Object meta) {
        if (meta == null) {
            return null;
        } else if (meta instanceof Meta) {
            return (Meta) meta;
        }
        Map<String, Object> info = Wrapper.getMap(meta);
        if (info == null) {
            assert false : "meta error: " + meta;
            return null;
        }
        String type = getMetaType(info, null);
        // assert type != null : "meta type error: " + meta;
        Meta.Factory factory = type == null ? null : getMetaFactory(type);
        if (factory == null) {
            // unknown meta type, get default meta factory
            factory = getMetaFactory("*");  // unknown
            if (factory == null) {
                assert false : "default meta factory not found: " + meta;
                return null;
            }
        }
        return factory.parseMeta(info);
    }

    //
    //  Document Helper
    //

    @Override
    public void setDocumentFactory(String type, Document.Factory factory) {
        _docsFactories.put(type, factory);
    }

    @Override
    public Document.Factory getDocumentFactory(String type) {
        return _docsFactories.get(type);
    }

    @Override
    public Document createDocument(String type, String data, TransportableData signature) {
        Document.Factory factory = getDocumentFactory(type);
        assert factory != null : "document type not found: " + type;
        return factory.createDocument(data, signature);
    }

    @Override
    public Document parseDocument(Object doc) {
        if (doc == null) {
            return null;
        } else if (doc instanceof Document) {
            return (Document) doc;
        }
        Map<String, Object> info = Wrapper.getMap(doc);
        if (info == null) {
            assert false : "document error: " + doc;
            return null;
        }
        String type = getDocumentType(info, null);
        // assert type != null : "document type error: " + doc;
        Document.Factory factory = type == null ? null : getDocumentFactory(type);
        if (factory == null) {
            // unknown document type, get default document factory
            factory = getDocumentFactory("*");  // unknown
            if (factory == null) {
                assert false : "default document factory not found: " + doc;
                return null;
            }
        }
        return factory.parseDocument(info);
    }

}
