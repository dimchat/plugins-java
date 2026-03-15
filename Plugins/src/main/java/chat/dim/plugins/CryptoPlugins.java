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

import java.util.Map;

import chat.dim.crypto.AESKey;
import chat.dim.crypto.BaseSymmetricKey;
import chat.dim.crypto.PlainKey;
import chat.dim.protocol.SymmetricAlgorithms;
import chat.dim.protocol.SymmetricKey;


// MixIn
public interface CryptoPlugins {

    // protected
    default void registerAESKeyFactory() {

        SymmetricKey.Factory aes = new SymmetricKey.Factory() {

            @Override
            public SymmetricKey generateSymmetricKey() {
                return AESKey.newKey();
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

    // protected
    default void registerPlainKeyFactory() {

        SymmetricKey.setFactory(SymmetricAlgorithms.PLAIN, new SymmetricKey.Factory() {

            @Override
            public SymmetricKey generateSymmetricKey() {
                return PlainKey.getInstance();
            }

            @Override
            public SymmetricKey parseSymmetricKey(Map<String, Object> key) {
                // check 'algorithm'
                String algorithm = BaseSymmetricKey.getKeyAlgorithm(key);
                if (!SymmetricAlgorithms.PLAIN.equals(algorithm)) {
                    // algorithm not matched
                    assert false : "Plain key error: " + key;
                    return null;
                }
                return PlainKey.getInstance();
            }
        });

    }

    /*/
    // protected
    void registerRSAKeyFactories();

    // protected
    void registerECCKeyFactories();
    /*/

}
