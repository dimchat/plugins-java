/* license: https://mit-license.org
 *
 *  DIMP : Decentralized Instant Messaging Protocol
 *
 *                                Written in 2026 by Moky <albert.moky@gmail.com>
 *
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

import chat.dim.mkm.BaseAddressFactory;
import chat.dim.mkm.BaseMetaFactory;
import chat.dim.mkm.GeneralDocumentFactory;
import chat.dim.mkm.IdentifierFactory;
import chat.dim.protocol.Address;
import chat.dim.protocol.Document;
import chat.dim.protocol.DocumentType;
import chat.dim.protocol.ID;
import chat.dim.protocol.Meta;
import chat.dim.protocol.MetaType;


// MixIn
public interface EntityPlugins {

    // protected
    default void registerIDFactory() {

        ID.setFactory(new IdentifierFactory());

    }

    // protected
    default void registerAddressFactory() {

        Address.setFactory(new BaseAddressFactory());

    }

    // protected
    default void registerMetaFactories() {

        setMetaFactory(MetaType.MKM, null);
        setMetaFactory(MetaType.BTC, null);
        setMetaFactory(MetaType.ETH, null);

    }
    // protected
    default void setMetaFactory(String type, Meta.Factory factory) {
        if (factory == null) {
            factory = new BaseMetaFactory(type);
        }
        Meta.setFactory(type, factory);
    }

    // protected
    default void registerDocumentFactories() {

        setDocumentFactory("*", null);
        setDocumentFactory(DocumentType.VISA, null);
        setDocumentFactory(DocumentType.PROFILE, null);
        setDocumentFactory(DocumentType.BULLETIN, null);

    }
    // protected
    default void setDocumentFactory(String type, Document.Factory factory) {
        if (factory == null) {
            factory = new GeneralDocumentFactory(type);
        }
        Document.setFactory(type, factory);
    }

}
