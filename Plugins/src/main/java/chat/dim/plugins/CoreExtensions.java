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

import chat.dim.ext.AccountGeneralFactory;
import chat.dim.ext.CommandGeneralFactory;
import chat.dim.ext.CryptoKeyGeneralFactory;
import chat.dim.ext.FormatGeneralFactory;
import chat.dim.ext.MessageGeneralFactory;
import chat.dim.ext.SharedAccountExtensions;
import chat.dim.ext.SharedCommandExtensions;
import chat.dim.ext.SharedCryptoExtensions;
import chat.dim.ext.SharedFormatExtensions;
import chat.dim.ext.SharedMessageExtensions;


// MixIn
public interface CoreExtensions {

    // protected
    default void registerCryptoHelpers() {

        // crypto
        CryptoKeyGeneralFactory cryptoHelper = new CryptoKeyGeneralFactory();
        SharedCryptoExtensions.symmetricHelper = cryptoHelper;
        SharedCryptoExtensions.privateHelper   = cryptoHelper;
        SharedCryptoExtensions.publicHelper    = cryptoHelper;
        SharedCryptoExtensions.helper          = cryptoHelper;

    }

    // protected
    default  void registerFormatHelpers() {

        // format
        FormatGeneralFactory formatHelper = new FormatGeneralFactory();
        SharedFormatExtensions.pnfHelper = formatHelper;
        SharedFormatExtensions.tedHelper = formatHelper;

    }

    // protected
    default  void registerAccountHelpers() {

        // mkm
        AccountGeneralFactory accountHelper = new AccountGeneralFactory();
        SharedAccountExtensions.addressHelper = accountHelper;
        SharedAccountExtensions.idHelper      = accountHelper;
        SharedAccountExtensions.metaHelper    = accountHelper;
        SharedAccountExtensions.docHelper     = accountHelper;
        SharedAccountExtensions.helper        = accountHelper;

    }

    // protected
    default  void registerMessageHelpers() {

        // dkd
        MessageGeneralFactory msgHelper = new MessageGeneralFactory();
        SharedMessageExtensions.contentHelper  = msgHelper;
        SharedMessageExtensions.envelopeHelper = msgHelper;
        SharedMessageExtensions.instantHelper  = msgHelper;
        SharedMessageExtensions.secureHelper   = msgHelper;
        SharedMessageExtensions.reliableHelper = msgHelper;
        SharedMessageExtensions.helper         = msgHelper;

    }

    // protected
    default  void registerCommandHelpers() {

        // cmd
        CommandGeneralFactory cmdHelper = new CommandGeneralFactory();
        SharedCommandExtensions.cmdHelper = cmdHelper;
        SharedCommandExtensions.helper    = cmdHelper;

    }

}
