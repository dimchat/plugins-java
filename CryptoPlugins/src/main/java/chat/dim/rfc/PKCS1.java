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
package chat.dim.rfc;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import chat.dim.protocol.AsymmetricAlgorithms;
import chat.dim.utils.CryptoUtils;


/*
 *  PKCS#1 -- https://tools.ietf.org/html/rfc3447
 */
public final class PKCS1 {
    private final byte[] data;
    private final boolean isPrivate;

    public PKCS1(byte[] data, boolean isPrivate) {
        this.data = data;
        this.isPrivate = isPrivate;
    }

    // TODO: convert PKCS#1 to X.509
    public byte[] toX509() throws NoSuchAlgorithmException, InvalidKeySpecException {
        /*
        byte[] header = X509.header;
        byte[] out = new byte[header.length + data.length];
        System.arraycopy(header, 0, out, 0, header.length);
        System.arraycopy(data, 0, out, header.length, data.length);
        return out;
        */
        KeyFactory keyFactory = CryptoUtils.getKeyFactory(AsymmetricAlgorithms.RSA);
        if (isPrivate) {
            // get public key data from private key data
            org.bouncycastle.asn1.pkcs.RSAPrivateKey privateKey;
            privateKey = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(data);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
            return keyFactory.generatePublic(keySpec).getEncoded();
        }
        org.bouncycastle.asn1.pkcs.RSAPublicKey publicKey;
        publicKey = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(data);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(publicKey.getModulus(), publicKey.getPublicExponent());
        return keyFactory.generatePublic(keySpec).getEncoded();
    }

    // convert PKCS#1 to PKCS#8
    public byte[] toPKCS8() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!isPrivate) {
            throw new InvalidKeySpecException("it's not private key data");
        }
        KeyFactory keyFactory = CryptoUtils.getKeyFactory(AsymmetricAlgorithms.RSA);
        org.bouncycastle.asn1.pkcs.RSAPrivateKey privateKey;
        privateKey = org.bouncycastle.asn1.pkcs.RSAPrivateKey.getInstance(data);
        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(privateKey.getModulus(), privateKey.getPublicExponent(),
                privateKey.getPrivateExponent(), privateKey.getPrime1(), privateKey.getPrime2(),
                privateKey.getExponent1(), privateKey.getExponent2(), privateKey.getCoefficient());
        return keyFactory.generatePrivate(keySpec).getEncoded();
    }

}
