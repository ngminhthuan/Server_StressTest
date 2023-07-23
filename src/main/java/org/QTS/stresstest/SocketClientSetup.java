package org.QTS.stresstest;


import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.entity.EzyObject;
import org.QTS.app.controller.NetworkPlayerRequestController;
import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.setup.EzyAppSetup;
import com.tvd12.ezyfoxserver.client.setup.EzySetup;
import org.QTS.stresstest.handler.AccessAppHandler;
import org.QTS.stresstest.handler.HandshakeHandler;
import org.QTS.stresstest.handler.LoginSuccessHandler;
import org.QTS.stresstest.handler.UdpHandshakeHandler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SocketClientSetup {

    private final AtomicInteger counter = new AtomicInteger();
    private final AtomicLong messageCount = new AtomicLong();

    @EzyAutoBind
    private LoginTracker loginTrack;
    private final ScheduledExecutorService executorService;

    public SocketClientSetup(String clientType) {
        this.executorService = EzyExecutors.newScheduledThreadPool(5, clientType + "-message-schedule");
        //this.executorService = EzyExecutors.newScheduledThreadPool(1, clientType + "-spawn-schedule");
        loginTrack = new LoginTracker();
        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
    }

    public void setup(EzyClient client, boolean useUdp) {
        int count = counter.incrementAndGet();
        EzySetup setup = client.setup();

        //setup.addDataHandler(EzyCommand.HANDSHAKE, new HandshakeHandler(count));
        setup.addDataHandler(EzyCommand.HANDSHAKE, new HandshakeHandler(count,loginTrack));

        //setup.addDataHandler(EzyCommand.LOGIN, new LoginSuccessHandler(useUdp));
        setup.addDataHandler(EzyCommand.LOGIN, new LoginSuccessHandler(useUdp,loginTrack));

        setup.addDataHandler(EzyCommand.UDP_HANDSHAKE, new UdpHandshakeHandler());

        //setup.addDataHandler(EzyCommand.APP_ACCESS, new AccessAppHandler(useUdp, count,executorService));
        setup.addDataHandler(EzyCommand.APP_ACCESS, new AccessAppHandler(useUdp, messageCount, executorService));
        EzyAppSetup appSetup = setup.setupApp("QworldSever");

        appSetup.addDataHandler("broadcastMessage", (app, data) -> {
            /*System.out.println("==================broadcastMessage==============");
			String command = ((EzyObject)data).get("message", String.class);
			System.out.println("tcp > server response: " + command);*/
        });
        appSetup.addDataHandler("udpBroadcastMessage", (app, data) -> {
            /*System.out.println("==================udpPlayerSpawn==============");
            String message = ((EzyObject)data).get("message", String.class);
            System.out.println("udp > server response: " + message);*/
        });
        /*appSetup.addDataHandler("tcpPlayerSpawn", (app, data) -> {
            System.out.println("==================tcpPlayerSpawn==============");
			String command = ((EzyObject)data).get("Command", String.class);
			System.out.println("tcp > server response: " + command);
        });
        appSetup.addDataHandler("udpPlayerSpawn", (app, data) -> {
            System.out.println("==================tcpPlayerSpawn==============");
			String command = ((EzyObject)data).get("Command", String.class);
			System.out.println("udp > server response: " + command);
        });*/

    }
}
/*public void setup(EzyClient client, boolean useUdp) {
        int count = counter.incrementAndGet();
        EzySetup setup = client.setup();
        setup.addDataHandler(EzyCommand.HANDSHAKE, new HandshakeHandler(count));
        setup.addDataHandler(EzyCommand.LOGIN, new LoginSuccessHandler(useUdp));
        setup.addDataHandler(EzyCommand.UDP_HANDSHAKE, new UdpHandshakeHandler());
        setup.addDataHandler(EzyCommand.APP_ACCESS, new AccessAppHandler(useUdp, messageCount, executorService));
        NetworkPlayerRequestController newhehe = new NetworkPlayerRequestController();
        EzyAppSetup appSetup = setup.setupApp("hello-world");
        appSetup.addDataHandler("broadcastMessage", (app, data) -> {
//			String message = ((EzyObject)data).get("message", String.class);
//			System.out.println("tcp > server response: " + message);
        });
        appSetup.addDataHandler("udpBroadcastMessage", (app, data) -> {
//			String message = ((EzyObject)data).get("message", String.class);
//			System.out.println("udp > server response: " + message);
        });
    }*/

