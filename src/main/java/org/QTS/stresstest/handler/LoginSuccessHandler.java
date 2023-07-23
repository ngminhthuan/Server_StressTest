package org.QTS.stresstest.handler;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.entity.EzyObject;
import com.tvd12.ezyfoxserver.client.handler.EzyLoginSuccessHandler;
import com.tvd12.ezyfoxserver.client.request.EzyAppAccessRequest;
import org.QTS.app.converter.EzyConverter;
import org.QTS.stresstest.LoginTracker;
import lombok.AllArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class LoginSuccessHandler extends EzyLoginSuccessHandler {

    protected final boolean connectUdp;

    @EzyAutoBind
    private  LoginTracker loginTrack;

    @Override
    protected void handleLoginSuccess(EzyData responseData) {
        if (connectUdp) {
            client.udpConnect(2611);
        } else {
            client.send(new EzyAppAccessRequest("QworldSever"));

        }
        String name  = extractStringInsideBrackets(responseData.toString());
        loginTrack.trackTimeLoginRespone(name);
    }

    public static String extractStringInsideBrackets(String inputString) {
        int startIndex = inputString.indexOf("[");
        if (startIndex != -1) {
            int endIndex = inputString.indexOf("]", startIndex + 1);
            if (endIndex != -1) {
                return inputString.substring(startIndex + 1, endIndex);
            }
        }
        return null;
    }

}
