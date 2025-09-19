
import org.junit.Assert;
import org.junit.Test;

import chat.dim.compat.CompatibleMetaFactory;
import chat.dim.digest.SHA256;
import chat.dim.format.Base58;
import chat.dim.format.Base64;
import chat.dim.format.Hex;
import chat.dim.format.UTF8;
import chat.dim.protocol.MetaType;
import chat.dim.protocol.Meta;

public class CryptoTest {

    @Test
    public void testHash() {
        Log.info("Crypto test");

        String string = "moky";
        byte[] data = UTF8.encode(string);

        byte[] hash;
        String res;
        String exp;

        // sha256（moky）= cb98b739dd699aa44bb6ebba128d20f2d1e10bb3b4aa5ff4e79295b47e9ed76d
        hash = SHA256.digest(data);
        res = Hex.encode(hash);
        exp = "cb98b739dd699aa44bb6ebba128d20f2d1e10bb3b4aa5ff4e79295b47e9ed76d";
        Log.info("sha256(" + string + ") = " + res);
        Assert.assertEquals(exp, res);
    }

    @Test
    public void testEncode() {
        String string = "moky";
        byte[] data = UTF8.encode(string);

        String res;
        String exp;

        // base58(moky) = 3oF5MJ
        res = Base58.encode(data);
        exp = "3oF5MJ";
        Log.info("base58(" + string + ") = " + res);
        Assert.assertEquals(exp, res);

        // base64(moky) = bW9reQ==
        res =Base64.encode(data);
        exp = "bW9reQ==";
        Log.info("base64(" + string + ") = " + res);
        Assert.assertEquals(exp, res);
    }

    /**
     *  Meta factories
     */
    static void registerCompatibleMetaFactories() {

        Meta.Factory mkm = new CompatibleMetaFactory(MetaType.MKM);
        Meta.Factory btc = new CompatibleMetaFactory(MetaType.BTC);
        Meta.Factory eth = new CompatibleMetaFactory(MetaType.ETH);

        Meta.setFactory("1", mkm);
        Meta.setFactory("2", btc);
        Meta.setFactory("4", eth);

        Meta.setFactory("mkm", mkm);
        Meta.setFactory("btc", btc);
        Meta.setFactory("eth", eth);

        Meta.setFactory("MKM", mkm);
        Meta.setFactory("BTC", btc);
        Meta.setFactory("ETH", eth);
    }

    static {
        new LibraryLoader().run();

        registerCompatibleMetaFactories();
    }

}
