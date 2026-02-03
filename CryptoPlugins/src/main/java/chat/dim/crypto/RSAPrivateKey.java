/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Albert Moky
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
package chat.dim.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import chat.dim.ext.GeneralCryptoHelper;
import chat.dim.format.PlainData;
import chat.dim.format.RSAKeys;
import chat.dim.protocol.AsymmetricAlgorithms;
import chat.dim.protocol.DecryptKey;
import chat.dim.protocol.EncryptKey;
import chat.dim.protocol.PublicKey;
import chat.dim.protocol.TransportableData;
import chat.dim.utils.CryptoUtils;

/**
 *  RSA Private Key
 *
 *  <blockquote><pre>
 *  keyInfo format: {
 *      "algorithm" : "RSA",
 *      "data"      : "..." // base64_encode()
 *  }
 *  </pre></blockquote>
 */
public final class RSAPrivateKey extends BasePrivateKey implements DecryptKey {

    private final java.security.interfaces.RSAPrivateKey privateKey;
    private final java.security.interfaces.RSAPublicKey publicKey;

    public RSAPrivateKey(Map<String, Object> dictionary) {
        super(dictionary);
        KeyPair keyPair = getKeyPair();
        privateKey = (java.security.interfaces.RSAPrivateKey) keyPair.getPrivate();
        publicKey = (java.security.interfaces.RSAPublicKey) keyPair.getPublic();
    }

    public static RSAPrivateKey newKey() throws NoSuchAlgorithmException {
        return newKey(1024);
    }
    public static RSAPrivateKey newKey(int sizeInBits) throws NoSuchAlgorithmException {
        Map<String, Object> info = new HashMap<>();
        info.put("algorithm", AsymmetricAlgorithms.ECC);

        // generate key pair
        KeyPairGenerator generator = CryptoUtils.getKeyPairGenerator(AsymmetricAlgorithms.RSA);
        generator.initialize(sizeInBits);
        KeyPair keyPair = generator.generateKeyPair();

        // -----BEGIN PUBLIC KEY-----
        String pkString = RSAKeys.encodePublicKey(keyPair.getPublic());
        // -----END PUBLIC KEY-----

        // -----BEGIN RSA PRIVATE KEY-----
        String skString = RSAKeys.encodePrivateKey(keyPair.getPrivate());
        // -----END RSA PRIVATE KEY-----

        // store keys
        info.put("data", pkString + "\r\n" + skString);
        // other parameters
        info.put("mode", "ECB");
        info.put("padding", "PKCS1");
        info.put("digest", "SHA256");

        // OK
        return new RSAPrivateKey(info);
    }

    private int keySize() {
        // TODO: get from key
        Integer size = getInteger("keySize");
        if (size != null) {
            return size;
        }
        return 1024 / 8; // 128
    }

    private KeyPair getKeyPair() {
        String data = getString("data");
        if (data == null) {
            throw new NullPointerException("RSA private key data not found");
        }
        // parse PEM file content
        java.security.PublicKey publicKey = RSAKeys.decodePublicKey(data);
        java.security.PrivateKey privateKey = RSAKeys.decodePrivateKey(data);
        return new KeyPair(publicKey, privateKey);
    }

    @Override
    public TransportableData getData() {
        if (privateKey == null) {
            throw new NullPointerException("RSA private key not found");
        }
        byte[] data = privateKey.getEncoded();
        return PlainData.create(data);
    }

    @Override
    public PublicKey getPublicKey() {
        if (publicKey == null) {
            throw new NullPointerException("public key not found");
        }
        // store public key in X.509 format
        String pem = RSAKeys.encodePublicKey(publicKey);

        Map<String, Object> keyInfo = new HashMap<>();
        keyInfo.put("algorithm", getAlgorithm());    // AsymmetricAlgorithms.RSA
        keyInfo.put("data", pem);
        keyInfo.put("mode", "ECB");
        keyInfo.put("padding", "PKCS1");
        keyInfo.put("digest", "SHA256");
        return new RSAPublicKey(keyInfo);
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, Map<String, Object> params) {
        if (ciphertext.length != keySize()) {
            throw new InvalidParameterException("RSA cipher text length error: " + ciphertext.length);
        }
        try {
            Cipher cipher = CryptoUtils.getCipher(CryptoUtils.RSA_ECB_PKCS1);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(ciphertext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] sign(byte[] data) {
        try {
            Signature signer = CryptoUtils.getSignature(CryptoUtils.RSA_SHA256);
            signer.initSign(privateKey);
            signer.update(data);
            return signer.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean matchEncryptKey(EncryptKey pKey) {
        return GeneralCryptoHelper.matchSymmetricKeys(pKey, this);
    }

}
