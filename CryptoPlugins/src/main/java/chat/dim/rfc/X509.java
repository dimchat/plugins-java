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

import java.io.IOException;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;


/*
 *  X.509 -- https://tools.ietf.org/html/rfc5280
 */
public final class X509 {
    private final byte[] data;

    /*
    static byte[] header = { 48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127, -115, 0 };
    */

    public X509(byte[] data) {
        this.data = data;
    }

    // convert X.509 to PKCS#1
    public byte[] toPKCS1() throws IOException {
        /*
        int from = header.length;
        int to = data.length;
        int length = to - from;
        if (length <= 0) {
            throw new ArrayIndexOutOfBoundsException("public key data not in X.509 format");
        }
        byte[] pkcs1 = new byte[length];
        System.arraycopy(data, from, pkcs1, 0, length);
        return pkcs1;
        */
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(data);
        ASN1Primitive primitive = keyInfo.parsePublicKey();
        return primitive.getEncoded();
    }

}
