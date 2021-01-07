package net.commchina.im;

import lombok.extern.slf4j.Slf4j;
import net.commchina.ximbiz.command.XimTcpHandshakeProcessor;
import net.commchina.ximbiz.command.XimWsHandshakeProcessor;
import net.commchina.ximbiz.command.handler.ReadStatusReqHandler;
import net.commchina.ximbiz.command.handler.RecallReqHandler;
import net.commchina.ximbiz.config.NacosImServerConfigBuilder;
import net.commchina.ximbiz.config.XimRedisMysqlMessageHelper;
import net.commchina.ximbiz.listener.XimGroupListener;
import net.commchina.ximbiz.listener.XimUserListener;
import net.commchina.ximbiz.service.XimAsyncChatMessageProcessor;
import net.commchina.ximbiz.service.XimLoginServiceProcessor;
import net.commchina.ximbiz.service.XimRecallCmdProcessor;
import org.apache.commons.lang3.StringUtils;
import org.jim.core.packets.Command;
import org.jim.core.utils.PropUtil;
import org.jim.server.JimServer;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.ChatReqHandler;
import org.jim.server.command.handler.HandshakeReqHandler;
import org.jim.server.command.handler.LoginReqHandler;
import org.jim.server.config.ImServerConfig;
import org.jim.server.protocol.ProtocolManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.tio.core.ssl.SslConfig;

/**
 * IM服务端DEMO演示启动类;
 *
 * @author xiaokang
 * @date 2018-04-05 23:50:25
 */
@Slf4j
@Order(10)
@Component
public class XimServerStart implements ApplicationRunner {

    @Value("${jim.bind.ip}")
    private String bindIp;
    @Value("${jim.port}")
    private Integer bindPort;
    @Value("${jim.http.page}")
    private String httpPage;
    @Value("${jim.http.max.live.time}")
    private Integer httpMaxLiveTime;
    @Value("${jim.http.scan.packages}")
    private String httpScanPackages;
    @Value("${jim.heartbeat.timeout}")
    private Integer heartbeatTimeout;
    @Value("${jim.store}")
    private Boolean store;
    @Value("${jim.cluster}")
    private Boolean cluster;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        serverStart();
    }

    public void serverStart() throws Exception
    {
        ImServerConfig imServerConfig = new NacosImServerConfigBuilder(bindIp, bindPort, httpPage, httpMaxLiveTime, httpScanPackages, heartbeatTimeout, store ? "on" : "off", cluster ? "on" : "off").build();
        //初始化SSL;(开启SSL之前,你要保证你有SSL证书哦...)
        initSsl(imServerConfig);
        //设置群组监听器，非必须，根据需要自己选择性实现;
        imServerConfig.setImGroupListener(new XimGroupListener());
        //设置绑定用户监听器，非必须，根据需要自己选择性实现;
        imServerConfig.setImUserListener(new XimUserListener());

        /*****************start 以下处理器根据业务需要自行添加与扩展，每个Command都可以添加扩展,此处为demo中处理**********************************/

        HandshakeReqHandler handshakeReqHandler = CommandManager.getCommand(Command.COMMAND_HANDSHAKE_REQ, HandshakeReqHandler.class);
        //添加自定义握手处理器;
        handshakeReqHandler.addMultiProtocolProcessor(new XimWsHandshakeProcessor());
        handshakeReqHandler.addMultiProtocolProcessor(new XimTcpHandshakeProcessor());
        LoginReqHandler loginReqHandler = CommandManager.getCommand(Command.COMMAND_LOGIN_REQ, LoginReqHandler.class);
        //添加登录业务处理器;
        loginReqHandler.setSingleProcessor(new XimLoginServiceProcessor());
        //添加用户业务聊天记录处理器，用户自己继承抽象类BaseAsyncChatMessageProcessor即可，以下为内置默认的处理器！
        ChatReqHandler chatReqHandler = CommandManager.getCommand(Command.COMMAND_CHAT_REQ, ChatReqHandler.class);
        chatReqHandler.setSingleProcessor(new XimAsyncChatMessageProcessor());
        /*****************end *******************************************************************************************/
         ProtocolManager.removeServerHandler("http");

        imServerConfig.setIsStore(store ? "on" : "off");
        imServerConfig.setMessageHelper(new XimRedisMysqlMessageHelper());
        JimServer jimServer = new JimServer(imServerConfig);


        //注册消息撤销Handler
        CommandManager.registerCommand(new RecallReqHandler());
        RecallReqHandler recallReqHandler = CommandManager.getCommand(Command.COMMAND_CANCEL_MSG_REQ, RecallReqHandler.class);
        recallReqHandler.setSingleProcessor(new XimRecallCmdProcessor());
        //注册消息状态Handler
        CommandManager.registerCommand(new ReadStatusReqHandler());

        jimServer.start();
        log.info("jim server start");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                jimServer.stop();
                log.info("jim server stop");
            }
        });
    }

    /**
     * 开启SSL之前，你要保证你有SSL证书哦！
     *
     * @param imServerConfig
     * @throws Exception
     */
    private static void initSsl(ImServerConfig imServerConfig) throws Exception
    {
        //开启SSL
        if (ImServerConfig.ON.equals(imServerConfig.getIsSSL())) {
            String keyStorePath = PropUtil.get("jim.key.store.path");
            String keyStoreFile = keyStorePath;
            String trustStoreFile = keyStorePath;
            String keyStorePwd = PropUtil.get("jim.key.store.pwd");
            if (StringUtils.isNotBlank(keyStoreFile) && StringUtils.isNotBlank(trustStoreFile)) {
                SslConfig sslConfig = SslConfig.forServer(keyStoreFile, trustStoreFile, keyStorePwd);
                imServerConfig.setSslConfig(sslConfig);
            }
        }
    }

}
