package net.commchina.ximbiz.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jim.core.http.HttpConfig;
import org.jim.core.ws.WsConfig;
import org.jim.server.config.ImServerConfig;
import org.jim.server.config.ImServerConfigBuilder;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/10/20 13:11
 */
@Data
@AllArgsConstructor
public class NacosImServerConfigBuilder extends ImServerConfigBuilder<ImServerConfig,NacosImServerConfigBuilder> {

    private String bindIp;

    private Integer bindPort;
    private String httpPage;
    private Integer httpMaxLiveTime;
    private String httpScanPackages;
    private Integer heartbeatTimeout;
    private String store;
    private String cluster;

    @Override
    public NacosImServerConfigBuilder configHttp(HttpConfig httpConfig)throws Exception{
        httpConfig.setBindPort(this.bindPort);
        //设置web访问路径;html/css/js等的根目录，支持classpath:，也支持绝对路径
        httpConfig.setPageRoot(this.httpPage);
        //不缓存资源;
        httpConfig.setMaxLiveTimeOfStaticRes(this.httpMaxLiveTime);
        //设置j-im mvc扫描目录;
        httpConfig.setScanPackages(this.httpScanPackages.split(","));
        return this;
    }

    @Override
    public NacosImServerConfigBuilder configWs(WsConfig wsConfig)throws Exception{

        return this;
    }

    @Override
    protected NacosImServerConfigBuilder getThis() {
        return this;
    }

    @Override
    public ImServerConfig build()throws Exception {
        super.build();
        return ImServerConfig.newBuilder()
                .bindIp(this.bindIp)
                .bindPort(this.bindPort)
                .heartbeatTimeout(this.heartbeatTimeout)
                .isStore(this.store)
                .httConfig(this.httpConfig)
                .wsConfig(this.wsConfig)
                .serverListener(this.serverListener)
                .isCluster(this.cluster).build();
    }
}
