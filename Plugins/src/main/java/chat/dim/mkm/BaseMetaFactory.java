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
import chat.dim.format.UTF8;
import chat.dim.protocol.Meta;
import chat.dim.protocol.MetaType;
import chat.dim.protocol.PrivateKey;
import chat.dim.protocol.SignKey;
import chat.dim.protocol.TransportableData;
import chat.dim.protocol.VerifyKey;

/**
 *  Base Meta Factory
 */
public class BaseMetaFactory implements Meta.Factory {

    protected final String type;

    public BaseMetaFactory(String version) {
        super();
        type = version;
    }

    @Override
    public Meta generateMeta(SignKey sKey, String seed) {
        TransportableData fingerprint;
        if (seed == null || seed.length() == 0) {
            fingerprint = null;
        } else {
            byte[] data = UTF8.encode(seed);
            byte[] sig = sKey.sign(data);
            fingerprint = TransportableData.create(sig);
        }
        VerifyKey key = ((PrivateKey) sKey).getPublicKey();
        return createMeta(key, seed, fingerprint);
    }

    @Override
    public Meta createMeta(VerifyKey key, String seed, TransportableData fingerprint) {
        String version = type;
        Meta out;
        switch (version) {

            case MetaType.MKM:
                out = new DefaultMeta(version, key, seed, fingerprint);
                break;

            case MetaType.BTC:
                out = new BTCMeta(version, key);
                break;

            case MetaType.ETH:
                out = new ETHMeta(version, key);
                break;

            default:
                throw new IllegalArgumentException("unknown meta type: " + version);
        }
        assert out.isValid() : "meta error: " + out;
        return out;
    }

    @Override
    public Meta parseMeta(Map<String, Object> info) {
        // check 'type', 'key', 'seed', 'fingerprint'
        if (info.get("type") == null || info.get("key") == null) {
            // meta.type should not be empty
            // meta.key should not be empty
            assert false : "meta error: " + info;
            return null;
        } else if (info.get("seed") == null) {
            if (info.get("fingerprint") == null) {
                assert false : "meta error: " + info;
                return null;
            }
        } else if (info.get("fingerprint") == null) {
            assert false : "meta error: " + info;
            return null;
        }

        // create meta for type
        Meta out;
        String version = getMetaType(info);
        switch (version) {

            case MetaType.MKM:
                out = new DefaultMeta(info);
                break;

            case MetaType.BTC:
                out = new BTCMeta(info);
                break;

            case MetaType.ETH:
                out = new ETHMeta(info);
                break;

            default:
                throw new IllegalArgumentException("unknown meta type: " + version);
        }
        if (out.isValid()) {
            return out;
        }
        assert false : "meta error: " + info;
        return null;
    }

    protected String getMetaType(Map<String, Object> info) {
        // get type from meta info
        String version = SharedAccountExtensions.helper.getMetaType(info, null);
        if (version == null) {
            version = "";
        }
        return version;
    }

}
