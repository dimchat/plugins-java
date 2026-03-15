/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 Albert Moky
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
import java.util.Map;

import chat.dim.format.Base64Data;
import chat.dim.format.BaseData;
import chat.dim.format.EmbedData;
import chat.dim.format.PortableNetworkFile;
import chat.dim.protocol.DecryptKey;
import chat.dim.protocol.TransportableData;
import chat.dim.protocol.TransportableFile;
import chat.dim.rfc.DataURI;


// MixIn
public interface TransportablePlugins {

    // protected
    default void registerTEDFactory() {

        // TED
        TransportableData.setFactory(new TransportableData.Factory() {

            @Override
            public TransportableData parseTransportableData(String ted) {
                DataURI uri = DataURI.parse(ted);
                if (uri != null) {
                    String encoding = uri.head.encoding;
                    if (BaseData.BASE_64.equalsIgnoreCase(encoding)) {
                        // "data:image/jpeg;base64,..."
                        return EmbedData.create(uri);
                    }
                    // TODO: other encoding?
                    assert false : "TED encoding error: " + encoding;
                    return null;
                }
                // TODO: check Base-64 format
                // "{BASE64_ENCODED}"
                return Base64Data.create(ted);
            }
        });

    }

    // protected
    default void registerPNFFactory() {

        // PNF
        TransportableFile.setFactory(new TransportableFile.Factory() {

            @Override
            public TransportableFile createTransportableFile(TransportableData data, String filename,
                                                             URI url, DecryptKey key) {
                return new PortableNetworkFile(data, filename, url, key);
            }

            @Override
            public TransportableFile parseTransportableFile(Map<String, Object> pnf) {
                // check 'data', 'URL', 'filename'
                if (pnf.get("data") == null && pnf.get("URL") == null && pnf.get("filename") == null) {
                    // pnf.data and pnf.URL and pnf.filename should not be empty at the same time
                    assert false : "PNF error: " + pnf;
                    return null;
                }
                return new PortableNetworkFile(pnf);
            }
        });

    }

}
