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
import chat.dim.protocol.ID;
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

    protected String getType(String docType, ID did) {
        assert docType != null && docType.length() > 0 : "document type empty";
        if (!docType.equals("*")) {
            return docType;
        } else if (did.isGroup()) {
            return DocumentType.BULLETIN;
        } else if (did.isUser()) {
            return DocumentType.VISA;
        } else {
            return DocumentType.PROFILE;
        }
    }

    @Override
    public Document createDocument(ID did, String data, TransportableData signature) {
        String docType = getType(type, did);
        Document doc;
        if (data == null || data.isEmpty()) {
            assert signature == null : "document error: " + did + ", signature: " + signature;
            // create empty document
            switch (docType) {

                case DocumentType.VISA:
                    doc = new BaseVisa(did);
                    break;

                case DocumentType.BULLETIN:
                    doc = new BaseBulletin();
                    doc.setString("did", did);
                    break;

                default:
                    doc = new BaseDocument(docType);
                    doc.setString("did", did);
                    break;
            }
        } else {
            assert signature != null : "document error: " + did + ", data: " + data;
            // create document with data & signature from local storage
            switch (docType) {

                case DocumentType.VISA:
                    doc = new BaseVisa(did, data, signature);
                    break;

                case DocumentType.BULLETIN:
                    doc = new BaseBulletin(data, signature);
                    doc.setString("did", did);
                    break;

                default:
                    doc = new BaseDocument(docType, data, signature);
                    doc.setString("did", did);
                    break;
            }
        }
        return doc;
    }

    @Override
    public Document parseDocument(Map<String, Object> info) {
        // check 'did', 'data', 'signature'
        ID did = ID.parse(info.get("did"));
        if (did == null) {
            assert false : "document ID not found: " + info;
            return null;
        } else if (info.get("data") == null || info.get("signature") == null) {
            // doc.data should not be empty
            // doc.signature should not be empty
            assert false : "document error: " + info;
            return null;
        }
        String docType = SharedAccountExtensions.helper.getDocumentType(info, null);
        if (docType == null) {
            docType = getType("*", did);
        }

        Document doc;
        // create with document type
        switch (docType) {

            case DocumentType.VISA:
                doc = new BaseVisa(info);
                break;

            case DocumentType.BULLETIN:
                doc = new BaseBulletin(info);
                break;

            default:
                doc = new BaseDocument(info);
                break;
        }
        return doc;
    }

}
