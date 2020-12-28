package net.commchina.xim.common.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.jim.core.utils.JsonKit;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: x-im
 * @author: hengxiaokang
 * @time 2020/10/21 12:49
 */
public class BeanUtils {

    public static <T> List<T> toArray(List<String> datas, Class<T> clazz){
        if(datas == null) {
            return null;
        }
        List<T> result  = new ArrayList<T>();
        for(String obj : datas){
            result.add(JsonKit.toBean(StringEscapeUtils.unescapeJava(obj.substring(1, obj.length() - 1)), clazz));
        }
        return result;
    }
}
