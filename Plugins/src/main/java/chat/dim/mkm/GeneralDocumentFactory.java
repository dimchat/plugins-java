/* license: https://mit-license.org
 *
 *  Ming-Ke-Ming : Decentralized User Identity Authentication
 *
 *                                Written in 2020 by Moky <albert.moky@gmail.com>
 *
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
package chat.dim.mkm;

import java.util.Map;

import chat.dim.ext.SharedAccountExtensions;
import chat.dim.protocol.Document;
import chat.dim.protocol.DocumentType;
import chat.dim.protocol.TransportableData;

/**
 *  General Document Factory
 */
public class GeneralDocumentFactory implements Document.Factory {

    protected final String type;

    public GeneralDocumentFactory(String docType) {
        super();
        type = docType;
    }

    @Override
    public Document createDocument(String data, TransportableData signature) {
        if (data == null || data.isEmpty()) {
            assert signature == null : "document error: " + data + ", signature: " + signature;
            // create empty document
            return createEmptyDocument();
        }
        assert signature != null : "document error: " + data;
        // create document with data & signature from local storage
        return createValidDocument(data, signature);
    }
    protected Document createEmptyDocument() {
        String docType = type;
        Document out;
        switch (docType) {

            case DocumentType.VISA:
                out = new BaseVisa();
                break;

            case DocumentType.BULLETIN:
                out = new BaseBulletin();
                break;

            default:
                out = new BaseDocument(docType);
                break;
        }
        return out;
    }
    protected Document createValidDocument(String data, TransportableData signature) {
        String docType = type;
        Document out;
        switch (docType) {

            case DocumentType.VISA:
                out = new BaseVisa(data, signature);
                break;

            case DocumentType.BULLETIN:
                out = new BaseBulletin(data, signature);
                break;

            default:
                out = new BaseDocument(docType, data, signature);
                break;
        }
        assert out.isValid() : "document error: " + out;
        return out;
    }

    @Override
    public Document parseDocument(Map<String, Object> info) {
        // check 'did', 'data', 'signature'
        if (info.get("data") == null || info.get("signature") == null) {
            // doc.data should not be empty
            // doc.signature should not be empty
            assert false : "document error: " + info;
            return null;
        //} else if (info.get("did") == null) {
        //    // doc.did should not be empty
        //    assert false : "document error: " + info;
        //    return null;
        }

        // create document for type
        Document out;
        String docType = SharedAccountExtensions.helper.getDocumentType(info, null);
        switch (docType) {

            case DocumentType.VISA:
                out = new BaseVisa(info);
                break;

            case DocumentType.BULLETIN:
                out = new BaseBulletin(info);
                break;

            default:
                out = new BaseDocument(info);
                break;
        }
        return out;
    }

}
