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
package chat.dim.format;

import java.net.URI;
import java.util.Map;

import chat.dim.data.Converter;
import chat.dim.protocol.DecryptKey;
import chat.dim.protocol.PortableNetworkFile;
import chat.dim.protocol.TransportableData;
import chat.dim.type.Dictionary;

/**
 *  PNF - Portable Network File
 */
public final class BaseNetworkFile extends Dictionary implements PortableNetworkFile {

    private final PortableNetworkFileWrapper wrapper;

    public BaseNetworkFile(Map<String, Object> dictionary) {
        super(dictionary);
        wrapper = createWrapper();
    }

    public BaseNetworkFile(TransportableData data, String filename, URI url, DecryptKey key) {
        super();
        wrapper = createWrapper();
        // file data
        if (data != null) {
            wrapper.setData(data);
        }
        // file name
        if (filename != null) {
            wrapper.setFilename(filename);
        }
        // download URL
        if (url != null) {
            wrapper.setURL(url);
        }
        // decrypt key
        if (key != null) {
            wrapper.setPassword(key);
        }
    }

    protected PortableNetworkFileWrapper createWrapper() {
        PortableNetworkFileWrapper.Factory factory = SharedNetworkFormatAccess.pnfWrapperFactory;
        return factory.createPortableNetworkFileWrapper(super.toMap());
    }

    @Override
    public Map<String, Object> toMap() {
        // serialize data
        wrapper.toMap();
        return super.toMap();
    }

    /**
     *  file data
     */

    @Override
    public byte[] getData() {
        TransportableData ted = wrapper.getData();
        return ted == null ? null : ted.getData();
    }

    @Override
    public void setData(byte[] binary) {
        wrapper.setBinary(binary);
    }

    /**
     *  file name
     */

    @Override
    public String getFilename() {
        return wrapper.getFilename();
    }

    @Override
    public void setFilename(String name) {
        wrapper.setFilename(name);
    }

    /**
     *  download URL
     */

    @Override
    public URI getURL() {
        return wrapper.getURL();
    }

    @Override
    public void setURL(URI url) {
        wrapper.setURL(url);
    }

    /**
     *  decrypt key
     */

    @Override
    public DecryptKey getPassword() {
        return wrapper.getPassword();
    }

    @Override
    public void setPassword(DecryptKey key) {
        wrapper.setPassword(key);
    }

    /**
     *  encoding
     */

    @Override
    public String toString() {
        // serialize data
        Map<String, Object> info = toMap();
        String text = getURLString(info);
        if (text != null) {
            // only contains 'URL',
            // or this info can be built to a data URI
            return text;
        }
        // not a single URL, encode the entire dictionary
        return JSONMap.encode(info);
    }

    @Override
    public Object toObject() {
        // serialize data
        Map<String, Object> info = toMap();
        String text = getURLString(info);
        if (text != null) {
            // only contains 'URL',
            // or this info can be built to a data URI
            return text;
        }
        // not a single URL, return the entire dictionary
        return info;
    }

    private static String getURLString(Map<String, Object> info) {
        //
        //  check URL
        //
        String urlString = Converter.getString(info.get("URL"));
        if (urlString == null) {
            //
            //  check data URI
            //
            return DataURI.build(info);
        } else if (urlString.startsWith("data:")) {
            // 'data:...;...,...'
            return urlString;
        }
        //
        //  check extra params
        //
        int count = info.size();
        if (count == 1) {
            // if only contains 'URL' field, return the URL string directly
            return urlString;
        } else if (count == 2 && info.containsKey("filename")) {
            // ignore 'filename' field
            return urlString;
        } else {
            // not a single URL
            return null;
        }
    }

}
