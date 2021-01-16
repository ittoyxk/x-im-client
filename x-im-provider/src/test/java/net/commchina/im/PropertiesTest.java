package net.commchina.im;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Properties;

@Slf4j
public class PropertiesTest {


    @Test
    public void tets(){
        JSONObject jsonObject = JSONObject.parseObject("{\n" +
                "\t\"mail.smtp.auth\": \"true\",\n" +
                "\t\"mail.smtp.starttls.enable\": \"true\",\n" +
                "\t\"mail.smtp.starttls.required\": \"true\",\n" +
                "\t\"mail.smtp.socketFactory.port\": \"465\",\n" +
                "\t\"mail.smtp.socketFactory.class\": \"javax.net.ssl.SSLSocketFactory\",\n" +
                "\t\"mail.smtp.socketFactory.fallback\": \"false\"\n" +
                "}");
        Properties properties=new Properties();
        jsonObject.entrySet().stream().forEach(entry->{
            properties.put(entry.getKey(),entry.getValue());
        });

        log.info("p:{}",properties);


    }
}
