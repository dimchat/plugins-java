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

import chat.dim.data.Wrapper;
import chat.dim.format.JSONMap;
import chat.dim.protocol.DecryptKey;
import chat.dim.protocol.TransportableData;
import chat.dim.protocol.TransportableFile;
import chat.dim.rfc.DataURI;
import chat.dim.type.Mapper;


/**
 *  Format GeneralFactory
 */
public class FormatGeneralFactory implements TransportableFileHelper,
                                             TransportableDataHelper {

    private TransportableData.Factory tedFactory = null;

    private TransportableFile.Factory pnfFactory = null;

    ///
    ///   TED - Transportable Encoded Data
    ///

    @Override
    public void setTransportableDataFactory(TransportableData.Factory factory) {
        tedFactory = factory;
    }

    @Override
    public TransportableData.Factory getTransportableDataFactory() {
        return tedFactory;
    }

    @Override
    public TransportableData parseTransportableData(Object ted) {
        if (ted == null) {
            return null;
        } else if (ted instanceof TransportableData) {
            return (TransportableData) ted;
        }
        // unwrap
        String str = Wrapper.getString(ted);
        if (str == null) {
            assert false : "TED error: " + ted;
            return null;
        }
        TransportableData.Factory factory = getTransportableDataFactory();
        assert factory != null : "TED factory not ready";
        return factory.parseTransportableData(str);
    }

    ///
    ///   PNF - Portable Network File
    ///

    @Override
    public void setTransportableFileFactory(TransportableFile.Factory factory) {
        pnfFactory = factory;
    }

    @Override
    public TransportableFile.Factory getTransportableFileFactory() {
        return pnfFactory;
    }

    @Override
    public TransportableFile createTransportableFile(TransportableData data, String filename,
                                                     URI url, DecryptKey password) {
        TransportableFile.Factory factory = getTransportableFileFactory();
        assert factory != null : "PNF factory not ready";
        return factory.createTransportableFile(data, filename, url, password);
    }

    @Override
    public TransportableFile parseTransportableFile(Object pnf) {
        if (pnf == null) {
            return null;
        } else if (pnf instanceof TransportableFile) {
            return (TransportableFile) pnf;
        }
        // unwrap
        Map<String, Object> info = getTransportableFileContent(pnf);
        if (info == null) {
            assert false : "PNF error: " + pnf;
            return null;
        }
        TransportableFile.Factory factory = getTransportableFileFactory();
        assert factory != null : "PNF factory not ready";
        return factory.parseTransportableFile(info);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getTransportableFileContent(Object pnf) {
        if (pnf instanceof Mapper) {
            return ((Mapper) pnf).toMap();
        } else if (pnf instanceof Map) {
            return (Map<String, Object>) pnf;
        }
        String text = Wrapper.getString(pnf);
        if (text == null || text.length() < 8) {
            assert false : "PNF error: " + pnf;
            return null;
        } else if (text.startsWith("{")) {
            // decode JSON string
            assert text.endsWith("}") : "PNF json error: " + pnf;
            return JSONMap.decode(text);
        }
        Map<String, Object> content = new HashMap<>();

        // 1. check for URL: "http://..."
        int pos = text.indexOf("://");
        if (0 < pos && pos < 8) {
            content.put("URL", text);
            return content;
        }

        content.put("data", text);
        // 2. check for data URI: "data:image/jpeg;base64,..."
        DataURI uri = DataURI.parse(text);
        if (uri != null) {
            String filename = uri.getFilename();
            if (filename != null) {
                content.put("filename", filename);
            }
        //} else {
        //    // 3. check for Base-64 encoded string?
        }

        return content;
    }

}
