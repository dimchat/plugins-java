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

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import chat.dim.crypto.ECCPrivateKey;
import chat.dim.crypto.ECCPublicKey;
import chat.dim.crypto.RSAPrivateKey;
import chat.dim.crypto.RSAPublicKey;
import chat.dim.protocol.AsymmetricAlgorithms;
import chat.dim.protocol.PrivateKey;
import chat.dim.protocol.PublicKey;
import chat.dim.utils.CryptoUtils;


// MixIn
public interface AsymmetricPlugins {

    // protected
    default void registerRSAKeyFactories() {

        // RSA Private Key
        PrivateKey.Factory rsaPri = new PrivateKey.Factory() {

            @Override
            public PrivateKey generatePrivateKey() {
                try {
                    return RSAPrivateKey.newKey();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public PrivateKey parsePrivateKey(Map<String, Object> key) {
                // check 'data', 'algorithm'
                if (key.get("data") == null || key.get("algorithm") == null) {
                    // key.data should not be empty
                    // key.algorithm should not be empty
                    assert false : "RSA key error: " + key;
                    return null;
                }
                return new RSAPrivateKey(key);
            }
        };
        PrivateKey.setFactory(AsymmetricAlgorithms.RSA, rsaPri);
        PrivateKey.setFactory(CryptoUtils.RSA_SHA256, rsaPri);
        PrivateKey.setFactory(CryptoUtils.RSA_ECB_PKCS1, rsaPri);

        // RSA Public Key
        PublicKey.Factory rsaPub = new PublicKey.Factory() {

            @Override
            public PublicKey parsePublicKey(Map<String, Object> key) {
                // check 'data', 'algorithm'
                if (key.get("data") == null || key.get("algorithm") == null) {
                    // key.data should not be empty
                    // key.algorithm should not be empty
                    assert false : "RSA key error: " + key;
                    return null;
                }
                return new RSAPublicKey(key);
            }
        };
        PublicKey.setFactory(AsymmetricAlgorithms.RSA, rsaPub);
        PublicKey.setFactory(CryptoUtils.RSA_SHA256, rsaPub);
        PublicKey.setFactory(CryptoUtils.RSA_ECB_PKCS1, rsaPub);

    }

    // protected
    default void registerECCKeyFactories() {

        // ECC Private Key
        PrivateKey.Factory eccPri = new PrivateKey.Factory() {

            @Override
            public PrivateKey generatePrivateKey() {
                return ECCPrivateKey.newKey();
            }

            @Override
            public PrivateKey parsePrivateKey(Map<String, Object> key) {
                // check 'data', 'algorithm'
                if (key.get("data") == null || key.get("algorithm") == null) {
                    // key.data should not be empty
                    // key.algorithm should not be empty
                    assert false : "ECC key error: " + key;
                    return null;
                }
                return new ECCPrivateKey(key);
            }
        };
        PrivateKey.setFactory(AsymmetricAlgorithms.ECC, eccPri);
        PrivateKey.setFactory(CryptoUtils.ECDSA_SHA256, eccPri);

        // ECC Public Key
        PublicKey.Factory eccPub = new PublicKey.Factory() {

            @Override
            public PublicKey parsePublicKey(Map<String, Object> key) {
                // check 'data', 'algorithm'
                if (key.get("data") == null || key.get("algorithm") == null) {
                    // key.data should not be empty
                    // key.algorithm should not be empty
                    assert false : "ECC key error: " + key;
                    return null;
                }
                return new ECCPublicKey(key);
            }
        };
        PublicKey.setFactory(AsymmetricAlgorithms.ECC, eccPub);
        PublicKey.setFactory(CryptoUtils.ECDSA_SHA256, eccPub);

    }

}
