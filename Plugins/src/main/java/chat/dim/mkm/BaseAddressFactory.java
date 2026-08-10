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

import chat.dim.mem.SharedAccountCache;
import chat.dim.protocol.Address;
import chat.dim.protocol.Meta;

/**
 *  Base Address Factory
 */
public class BaseAddressFactory implements Address.Factory {

    @Override
    public Address generateAddress(Meta meta, int network) {
        Address address = meta.generateAddress(network);
        if (address != null) {
            SharedAccountCache.addressCache.put(address.toString(), address);
        }
        return address;
    }

    @Override
    public Address parseAddress(String address) {
        Address add = SharedAccountCache.addressCache.get(address);
        if (add == null) {
            add = parse(address);
            if (add != null) {
                SharedAccountCache.addressCache.put(address, add);
            }
        }
        return add;
    }

    protected Address parse(String address) {
        int len = address == null ? 0 : address.length();
        //
        //  check broadcast address
        //
        if (len == 8) {
            // "anywhere"
            if (Address.ANYWHERE.equalsIgnoreCase(address)) {
                return Address.ANYWHERE;
            }
        } else if (len == 10) {
            // "everywhere"
            if (Address.EVERYWHERE.equalsIgnoreCase(address)) {
                return Address.EVERYWHERE;
            }
        }
        //
        //  checking normal address
        //
        if (26 <= len && len <= 35) {
            // BTC
            return BTCAddress.parse(address);
        } else if (len == 42) {
            // ETH
            return ETHAddress.parse(address);
        }
        //
        // TODO: other types of address
        //
        assert false : "invalid address: " + address;
        return null;
    }

}
