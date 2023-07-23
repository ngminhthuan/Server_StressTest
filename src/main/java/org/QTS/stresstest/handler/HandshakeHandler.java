package org.QTS.stresstest.handler;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfoxserver.client.handler.EzyHandshakeHandler;
import com.tvd12.ezyfoxserver.client.request.EzyLoginRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import lombok.AllArgsConstructor;
import org.QTS.stresstest.LoginTracker;

@AllArgsConstructor
public class HandshakeHandler extends EzyHandshakeHandler {

    private final int count;

    @EzyAutoBind
    private LoginTracker loginTracker;
    @Override
    protected EzyRequest getLoginRequest() {
        loginTracker.trackLoginRequest("Player#" + count);
        loginTracker.trackLoginResponse("Player#"+count,false);
        return new EzyLoginRequest("homeTown", "Player#" + count, "123456");
    }
}
