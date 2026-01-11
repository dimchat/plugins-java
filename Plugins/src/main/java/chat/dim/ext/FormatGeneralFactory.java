/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Albert Moky
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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import chat.dim.data.Converter;
import chat.dim.format.DataURI;
import chat.dim.format.JSONMap;
import chat.dim.protocol.DecryptKey;
import chat.dim.protocol.EncodeAlgorithms;
import chat.dim.protocol.PortableNetworkFile;
import chat.dim.protocol.TransportableData;
import chat.dim.type.Mapper;

/**
 *  Format GeneralFactory
 */
public class FormatGeneralFactory implements GeneralFormatHelper,
                                             PortableNetworkFileHelper,
                                             TransportableDataHelper {

    private final Map<String, TransportableData.Factory> tedFactories = new HashMap<>();

    private PortableNetworkFile.Factory pnfFactory = null;

    @Override
    public String getFormatAlgorithm(Map<?, ?> ted, String defaultValue) {
        return Converter.getString(ted.get("algorithm"), defaultValue);
    }

    ///
    ///   TED - Transportable Encoded Data
    ///

    @Override
    public void setTransportableDataFactory(String algorithm, TransportableData.Factory factory) {
        tedFactories.put(algorithm, factory);
    }

    @Override
    public TransportableData.Factory getTransportableDataFactory(String algorithm) {
        return tedFactories.get(algorithm);
    }

    @Override
    public TransportableData createTransportableData(byte[] data, String algorithm) {
        if (algorithm == null) {
            algorithm = EncodeAlgorithms.DEFAULT;
        }
        TransportableData.Factory factory = getTransportableDataFactory(algorithm);
        assert factory != null : "TED algorithm not support: " + algorithm;
        return factory.createTransportableData(data);
    }

    @Override
    public TransportableData parseTransportableData(Object ted) {
        if (ted == null) {
            return null;
        } else if (ted instanceof TransportableData) {
            return (TransportableData) ted;
        }
        // unwrap
        Map<String, Object> info = parseData(ted);
        if (info == null) {
            //assert false : "TED error: " + ted;
            return null;
        }
        String algo = getFormatAlgorithm(info, null);
        // assert algo != null : "TED error: " + ted;
        TransportableData.Factory factory = algo == null ? null : getTransportableDataFactory(algo);
        if (factory == null) {
            // unknown algorithm, get default factory
            factory = getTransportableDataFactory("*");  // unknown
            if (factory == null) {
                assert false : "default TED factory not found: " + ted;
                return null;
            }
        }
        return factory.parseTransportableData(info);
    }

    ///
    ///   PNF - Portable Network File
    ///

    @Override
    public void setPortableNetworkFileFactory(PortableNetworkFile.Factory factory) {
        pnfFactory = factory;
    }

    @Override
    public PortableNetworkFile.Factory getPortableNetworkFileFactory() {
        return pnfFactory;
    }

    @Override
    public PortableNetworkFile createPortableNetworkFile(TransportableData data, String filename,
                                                         URI url, DecryptKey password) {
        PortableNetworkFile.Factory factory = getPortableNetworkFileFactory();
        assert factory != null : "PNF factory not ready";
        return factory.createPortableNetworkFile(data, filename, url, password);
    }

    @Override
    public PortableNetworkFile parsePortableNetworkFile(Object pnf) {
        if (pnf == null) {
            return null;
        } else if (pnf instanceof PortableNetworkFile) {
            return (PortableNetworkFile) pnf;
        }
        // unwrap
        Map<String, Object> info = parseURL(pnf);
        if (info == null) {
            //assert false : "PNF error: " + pnf;
            return null;
        }
        PortableNetworkFile.Factory factory = getPortableNetworkFileFactory();
        assert factory != null : "PNF factory not ready";
        return factory.parsePortableNetworkFile(info);
    }

    //
    //  Convenience
    //

    /**
     *  Parse PNF
     */
    protected Map<String, Object> parseURL(Object pnf) {
        Map<String, Object> info = getMap(pnf);
        if (info == null) {
            // parse data URI from text string
            String text = getString(pnf);
            info = parseUri(text);
            if (info != null) {
                // data URI
                assert !text.contains("://") : "PNF data error: " + pnf;
                /*/
                if (text.startsWith("data:")) {
                    info.put("URI", text);
                }
                /*/
            } else if (text.contains("://")) {
                // [URL]
                info = new HashMap<>();
                info.put("URL", text);
            }
        }
        return info;
    }

    /**
     *  Parse TED
     */
    protected Map<String, Object> parseData(Object ted) {
        Map<String, Object> info = getMap(ted);
        if (info == null) {
            // parse data URI from text string
            String text = getString(ted);
            info = parseUri(text);
            if (info == null) {
                assert !text.contains("://") : "TED data error: " + ted;
                // [TEXT]
                info = new HashMap<>();
                info.put("data", text);
            }
        }
        return info;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getMap(Object data) {
        if (data instanceof Mapper) {
            return ((Mapper) data).toMap();
        } else if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        String text = getString(data);
        if (text.length() > 8 && text.startsWith("{") && text.endsWith("}")) {
            // from JSON string
            return JSONMap.decode(text);
        } else {
            return null;
        }
    }

    protected String getString(Object data) {
        return data instanceof String ? (String) data : data.toString();
    }

    protected Map<String, Object> parseUri(String text) {
        DataURI uri = DataURI.parse(text);
        return uri == null ? null : uri.toMap();
    }

}
