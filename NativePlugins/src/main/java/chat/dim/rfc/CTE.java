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

import chat.dim.format.Base64;


/*
 *  Content-Transfer-Encoding -- https://tools.ietf.org/html/rfc2045
 */
public final class CTE {

    static final int MIME_LINE_MAX_LEN = 76;

    /**
     *  6.7.  Quoted-Printable Content-Transfer-Encoding
     */
    public static String printable(byte[] data) {
        String base64 = Base64.encode(data);
        int length = base64.length();
        final String CR_LF = "\r\n";
        if (length > MIME_LINE_MAX_LEN && !base64.contains(CR_LF)) {
            StringBuilder sb = new StringBuilder();
            for (int beginIndex = 0, endIndex; beginIndex < length; beginIndex += MIME_LINE_MAX_LEN) {
                endIndex = beginIndex + MIME_LINE_MAX_LEN;
                if (endIndex < length) {
                    sb.append(base64, beginIndex, endIndex);
                    sb.append(CR_LF);
                } else {
                    sb.append(base64, beginIndex, length);
                    break;
                }
            }
            base64 = sb.toString();
        }
        return base64;
    }

}
