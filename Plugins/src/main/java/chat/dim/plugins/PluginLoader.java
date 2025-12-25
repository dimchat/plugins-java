/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Albert Moky
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
package chat.dim.plugins;

import java.net.URI;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import chat.dim.crypto.AESKey;
import chat.dim.crypto.BaseKey;
import chat.dim.crypto.PlainKey;
import chat.dim.digest.MessageDigester;
import chat.dim.digest.SHA256;
import chat.dim.format.Base58;
import chat.dim.format.Base64;
import chat.dim.format.Base64Data;
import chat.dim.format.BaseNetworkFile;
import chat.dim.format.DataCoder;
import chat.dim.format.Hex;
import chat.dim.format.HexCoder;
import chat.dim.format.StringCoder;
import chat.dim.format.UTF8;
import chat.dim.mkm.BaseAddressFactory;
import chat.dim.mkm.BaseMetaFactory;
import chat.dim.mkm.GeneralDocumentFactory;
import chat.dim.mkm.IdentifierFactory;
import chat.dim.protocol.Address;
import chat.dim.protocol.DecryptKey;
import chat.dim.protocol.Document;
import chat.dim.protocol.DocumentType;
import chat.dim.protocol.EncodeAlgorithms;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;
import chat.dim.protocol.MetaType;
import chat.dim.protocol.PortableNetworkFile;
import chat.dim.protocol.SymmetricAlgorithms;
import chat.dim.protocol.SymmetricKey;
import chat.dim.protocol.TransportableData;

public class PluginLoader {

    /**
     *  Register plugins
     */
    public void load() {

        registerCoders();
        registerDigesters();

        registerSymmetricKeyFactories();

        registerEntityFactories();

    }

    /**
     *  Data coders
     */
    protected void registerCoders() {

        registerBase58Coder();
        registerBase64Coder();
        registerHexCoder();

        registerUTF8Coder();

        registerPNFFactory();
        registerTEDFactory();

    }
    protected void registerBase58Coder() {
        // Base58 coding
        Base58.coder = new DataCoder() {

            @Override
            public String encode(byte[] data) {
                return chat.dim.bitcoinj.Base58.encode(data);
            }

            @Override
            public byte[] decode(String string) {
                return chat.dim.bitcoinj.Base58.decode(string);
            }
        };
    }
    protected void registerBase64Coder() {
        // Base64 coding
        Base64.coder = new DataCoder() {

            @Override
            public String encode(byte[] data) {
                return java.util.Base64.getEncoder().encodeToString(data);
            }

            @Override
            public byte[] decode(String string) {
                return java.util.Base64.getDecoder().decode(string);
            }
        };
    }
    protected void registerHexCoder() {
        // HEX coding
        Hex.coder = new HexCoder();
    }
    protected void registerUTF8Coder() {
        // UTF8
        UTF8.coder = new StringCoder() {

            @SuppressWarnings("CharsetObjectCanBeUsed")
            @Override
            public byte[] encode(String string) {
                return string.getBytes(Charset.forName("UTF-8"));
            }

            @SuppressWarnings("CharsetObjectCanBeUsed")
            @Override
            public String decode(byte[] utf8) {
                return new String(utf8, Charset.forName("UTF-8"));
            }
        };
    }
    protected void registerPNFFactory() {
        // PNF
        PortableNetworkFile.setFactory(new PortableNetworkFile.Factory() {

            @Override
            public PortableNetworkFile createPortableNetworkFile(TransportableData data, String filename,
                                                                 URI url, DecryptKey key) {
                return new BaseNetworkFile(data, filename, url, key);
            }

            @Override
            public PortableNetworkFile parsePortableNetworkFile(Map<String, Object> pnf) {
                // check 'data', 'URL', 'filename'
                if (pnf.get("data") == null && pnf.get("URL") == null && pnf.get("filename") == null) {
                    // pnf.data and pnf.URL and pnf.filename should not be empty at the same time
                    assert false : "PNF error: " + pnf;
                    return null;
                }
                return new BaseNetworkFile(pnf);
            }
        });
    }
    protected void registerTEDFactory() {
        // TED
        TransportableData.Factory tedFactory = new TransportableData.Factory() {

            @Override
            public TransportableData createTransportableData(byte[] data) {
                return new Base64Data(data);
            }

            @Override
            public TransportableData parseTransportableData(Map<String, Object> ted) {
                // check 'data'
                if (ted.get("data") == null) {
                    // ted.data should not be empty
                    assert false : "TED error: " + ted;
                    return null;
                }
                // TODO: 1. check algorithm
                //       2. check data format
                return new Base64Data(ted);
            }
        };
        TransportableData.setFactory(EncodeAlgorithms.BASE_64, tedFactory);
        //TransportableData.setFactory(EncodeAlgorithms.DEFAULT, tedFactory);
        TransportableData.setFactory("*", tedFactory);
    }

    /**
     *  Message digesters
     */
    protected void registerDigesters() {

        registerSHA256Digester();

    }
    protected void registerSHA256Digester() {
        // SHA256
        SHA256.digester = new MessageDigester() {

            @Override
            public byte[] digest(byte[] data) {
                MessageDigest md;
                try {
                    md = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
                }
                md.reset();
                md.update(data);
                return md.digest();
            }
        };
    }

    /**
     *  Symmetric key parsers
     */
    protected void registerSymmetricKeyFactories() {

        registerAESKeyFactory();

        registerPlainKeyFactory();

    }
    protected void registerAESKeyFactory() {
        SymmetricKey.Factory aes = new SymmetricKey.Factory() {

            @Override
            public SymmetricKey generateSymmetricKey() {
                Map<String, Object> key = new HashMap<>();
                key.put("algorithm", SymmetricAlgorithms.AES);
                return new AESKey(key);
            }

            @Override
            public SymmetricKey parseSymmetricKey(Map<String, Object> key) {
                // check 'data', 'algorithm'
                if (key.get("data") == null || key.get("algorithm") == null) {
                    // key.data should not be empty
                    // key.algorithm should not be empty
                    assert false : "AES key error: " + key;
                    return null;
                }
                return new AESKey(key);
            }
        };
        SymmetricKey.setFactory(SymmetricAlgorithms.AES, aes);
        SymmetricKey.setFactory(AESKey.AES_CBC_PKCS7, aes);
        //SymmetricKey.setFactory("AES/CBC/PKCS7Padding", aes);
    }
    protected void registerPlainKeyFactory() {
        SymmetricKey.setFactory(SymmetricAlgorithms.PLAIN, new SymmetricKey.Factory() {

            @Override
            public SymmetricKey generateSymmetricKey() {
                return PlainKey.getInstance();
            }

            @Override
            public SymmetricKey parseSymmetricKey(Map<String, Object> key) {
                // check 'algorithm'
                String algorithm = BaseKey.getKeyAlgorithm(key);
                if (!SymmetricAlgorithms.PLAIN.equals(algorithm)) {
                    // algorithm not matched
                    assert false : "Plain key error: " + key;
                    return null;
                }
                return PlainKey.getInstance();
            }
        });
    }

    /**
     *  ID, Address, Meta, Document parsers
     */
    protected void registerEntityFactories() {

        registerIDFactory();
        registerAddressFactory();
        registerMetaFactories();
        registerDocumentFactories();

    }
    protected void registerIDFactory() {

        ID.setFactory(new IdentifierFactory());
    }
    protected void registerAddressFactory() {

        Address.setFactory(new BaseAddressFactory());
    }
    protected void registerMetaFactories() {

        setMetaFactory(MetaType.MKM, "mkm", null);
        setMetaFactory(MetaType.BTC, "btc", null);
        setMetaFactory(MetaType.ETH, "eth", null);
    }
    protected void setMetaFactory(String type, String alias, Meta.Factory factory) {
        if (factory == null) {
            factory = new BaseMetaFactory(type);
        }
        Meta.setFactory(type, factory);
        Meta.setFactory(alias, factory);
    }
    protected void registerDocumentFactories() {

        setDocumentFactory("*", null);
        setDocumentFactory(DocumentType.VISA, null);
        setDocumentFactory(DocumentType.PROFILE, null);
        setDocumentFactory(DocumentType.BULLETIN, null);
    }
    protected void setDocumentFactory(String type, Document.Factory factory) {
        if (factory == null) {
            factory = new GeneralDocumentFactory(type);
        }
        Document.setFactory(type, factory);
    }

}
