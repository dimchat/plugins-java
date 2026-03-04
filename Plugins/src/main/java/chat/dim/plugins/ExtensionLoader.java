/* license: https://mit-license.org
 *
 *  DIMP : Decentralized Instant Messaging Protocol
 *
 *                                Written in 2022 by Moky <albert.moky@gmail.com>
 *
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Albert Moky
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

import chat.dim.dkd.BaseContent;
import chat.dim.dkd.BaseQuoteContent;
import chat.dim.dkd.BaseTextContent;
import chat.dim.dkd.CombineForwardContent;
import chat.dim.dkd.GeneralCommandFactory;
import chat.dim.dkd.GroupCommandFactory;
import chat.dim.dkd.HistoryCommandFactory;
import chat.dim.dkd.ListContent;
import chat.dim.dkd.NameCardContent;
import chat.dim.dkd.SecretContent;
import chat.dim.dkd.WebPageContent;
import chat.dim.dkd.asset.BaseMoneyContent;
import chat.dim.dkd.asset.TransferMoneyContent;
import chat.dim.dkd.cmd.BaseDocumentCommand;
import chat.dim.dkd.cmd.BaseMetaCommand;
import chat.dim.dkd.cmd.BaseReceiptCommand;
import chat.dim.dkd.file.AudioFileContent;
import chat.dim.dkd.file.BaseFileContent;
import chat.dim.dkd.file.ImageFileContent;
import chat.dim.dkd.file.VideoFileContent;
import chat.dim.dkd.group.ExpelGroupCommand;
import chat.dim.dkd.group.InviteGroupCommand;
import chat.dim.dkd.group.JoinGroupCommand;
import chat.dim.dkd.group.QuitGroupCommand;
import chat.dim.dkd.group.ResetGroupCommand;
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
import chat.dim.msg.MessageFactory;
import chat.dim.protocol.Command;
import chat.dim.protocol.Content;
import chat.dim.protocol.ContentType;
import chat.dim.protocol.Envelope;
import chat.dim.protocol.InstantMessage;
import chat.dim.protocol.ReliableMessage;
import chat.dim.protocol.SecureMessage;
import chat.dim.protocol.group.GroupCommand;

/**
 *  Core Extensions Loader
 */
public class ExtensionLoader {

    /**
     *  Register core factories
     */
    public void load() {

        registerCoreHelpers();

        registerMessageFactories();

        registerContentFactories();
        registerCommandFactories();

    }

    /**
     *  Core extensions
     */
    protected void registerCoreHelpers() {

        registerCryptoHelpers();
        registerFormatHelpers();

        registerAccountHelpers();

        registerMessageHelpers();
        registerCommandHelpers();

    }
    protected void registerCryptoHelpers() {
        // crypto
        CryptoKeyGeneralFactory cryptoHelper = new CryptoKeyGeneralFactory();
        SharedCryptoExtensions.symmetricHelper = cryptoHelper;
        SharedCryptoExtensions.privateHelper   = cryptoHelper;
        SharedCryptoExtensions.publicHelper    = cryptoHelper;
        SharedCryptoExtensions.helper          = cryptoHelper;
    }
    protected void registerFormatHelpers() {
        // format
        FormatGeneralFactory formatHelper = new FormatGeneralFactory();
        SharedFormatExtensions.pnfHelper = formatHelper;
        SharedFormatExtensions.tedHelper = formatHelper;
    }
    protected void registerAccountHelpers() {
        // mkm
        AccountGeneralFactory accountHelper = new AccountGeneralFactory();
        SharedAccountExtensions.addressHelper = accountHelper;
        SharedAccountExtensions.idHelper      = accountHelper;
        SharedAccountExtensions.metaHelper    = accountHelper;
        SharedAccountExtensions.docHelper     = accountHelper;
        SharedAccountExtensions.helper        = accountHelper;
    }
    protected void registerMessageHelpers() {
        // dkd
        MessageGeneralFactory msgHelper = new MessageGeneralFactory();
        SharedMessageExtensions.contentHelper  = msgHelper;
        SharedMessageExtensions.envelopeHelper = msgHelper;
        SharedMessageExtensions.instantHelper  = msgHelper;
        SharedMessageExtensions.secureHelper   = msgHelper;
        SharedMessageExtensions.reliableHelper = msgHelper;
        SharedMessageExtensions.helper         = msgHelper;
    }
    protected void registerCommandHelpers() {
        // cmd
        CommandGeneralFactory cmdHelper = new CommandGeneralFactory();
        SharedCommandExtensions.cmdHelper = cmdHelper;
        SharedCommandExtensions.helper    = cmdHelper;
    }

    /**
     *  Message factories
     */
    protected void registerMessageFactories() {

        // Envelope factory
        MessageFactory factory = new MessageFactory();
        Envelope.setFactory(factory);

        // Message factories
        InstantMessage.setFactory(factory);
        SecureMessage.setFactory(factory);
        ReliableMessage.setFactory(factory);
    }

    /**
     *  Core content factories
     */
    protected void registerContentFactories() {

        // Text
        Content.setFactory(ContentType.TEXT, BaseTextContent::new);

        // File
        Content.setFactory(ContentType.FILE, BaseFileContent::new);
        // Image
        Content.setFactory(ContentType.IMAGE, ImageFileContent::new);
        // Audio
        Content.setFactory(ContentType.AUDIO, AudioFileContent::new);
        // Video
        Content.setFactory(ContentType.VIDEO, VideoFileContent::new);

        // Web Page
        Content.setFactory(ContentType.PAGE, WebPageContent::new);

        // Name Card
        Content.setFactory(ContentType.NAME_CARD, NameCardContent::new);

        // Quote
        Content.setFactory(ContentType.QUOTE, BaseQuoteContent::new);

        // Money
        Content.setFactory(ContentType.MONEY, BaseMoneyContent::new);
        Content.setFactory(ContentType.TRANSFER, TransferMoneyContent::new);
        // ...

        // Command
        Content.setFactory(ContentType.COMMAND, new GeneralCommandFactory());

        // History Command
        Content.setFactory(ContentType.HISTORY, new HistoryCommandFactory());

        // Content Array
        Content.setFactory(ContentType.ARRAY, ListContent::new);

        // Combine and Forward
        Content.setFactory(ContentType.COMBINE_FORWARD, CombineForwardContent::new);

        // Top-Secret
        Content.setFactory(ContentType.FORWARD, SecretContent::new);

        // unknown content type
        Content.setFactory(ContentType.ANY, BaseContent::new);

    }

    /**
     *  Core command factories
     */
    protected void registerCommandFactories() {

        // Meta Command
        Command.setFactory(Command.META, BaseMetaCommand::new);

        // Documents Command
        Command.setFactory(Command.DOCUMENTS, BaseDocumentCommand::new);

        // Receipt Command
        Command.setFactory(Command.RECEIPT, BaseReceiptCommand::new);

        // Group Commands
        Command.setFactory("group", new GroupCommandFactory());
        Command.setFactory(GroupCommand.INVITE,  InviteGroupCommand::new);
        // 'expel' is deprecated (use 'reset' instead)
        Command.setFactory(GroupCommand.EXPEL,   ExpelGroupCommand::new);
        Command.setFactory(GroupCommand.JOIN,    JoinGroupCommand::new);
        Command.setFactory(GroupCommand.QUIT,    QuitGroupCommand::new);
        // 'query' is deprecated
        //Command.setFactory(GroupCommand.QUERY, QueryGroupCommand::new);
        Command.setFactory(GroupCommand.RESET,   ResetGroupCommand::new);
    }

}
