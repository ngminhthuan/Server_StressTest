package org.QTS.stresstest.handler;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.binding.annotation.EzyArrayBinding;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyObject;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.handler.EzyAppAccessHandler;
import org.QTS.app.converter.EzyConverter;
import lombok.AllArgsConstructor;
import org.QTS.common.models.CharacterAppearanceModel;
import org.QTS.common.models.NetworkCharacterModel;
import org.QTS.common.models.TransformModel;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
public class AccessAppHandler extends EzyAppAccessHandler {

    private final boolean useUdp;
    private final AtomicLong messageCount;
    private final ScheduledExecutorService executorService;
    /*private final int Count;*/


    @Override
    protected void postHandle(EzyApp app, EzyArray data) {
        //executorService.scheduleAtFixedRate(() -> sendMessage(app), 3, 5, TimeUnit.SECONDS);
        //executorService.scheduleAtFixedRate(() -> SpawnPlayer(app), 10, 5, TimeUnit.SECONDS);
    }

    private void sendMessage(EzyApp app) {
        if (useUdp) {
            app.send("udpBroadcastMessage", newMessageData());
        } else {
            app.send("broadcastMessage", newMessageData());
        }
    }
    private EzyObject newMessageData() {
        return EzyEntityFactory.newObjectBuilder()
                .append("message", "Message#" + messageCount.incrementAndGet())
                .build();
    }

    // Spawn Player :))
    private void SpawnPlayer(EzyApp app) {
        if (useUdp) {
            app.send("udpPlayerSpawn", newPlayerSpawn());
        } else {
            app.send("tcpPlayerSpawn", newPlayerSpawn());
        }
    }

    private EzyObject newPlayerSpawn() {

        EzyObject ModelObject = EzyConverter.marshaller.marshal(newPlayerData());

        EzyObject playerobject = EzyEntityFactory.newObjectBuilder()
                .append("Data", ModelObject)
                .append("Command", "SpawnInChannel")
                .build();
        EzyObject Client = EzyEntityFactory.newObjectBuilder()
                .append("clientData", playerobject).build();
        return Client;
    }

    @EzyAutoBind
    private NetworkCharacterModel newPlayerData() {
        NetworkCharacterModel playerData = new NetworkCharacterModel();

        playerData.setPlayfabId("a25121999"/*+Count*/);
        playerData.setName("NoName");
        playerData.setLevel(1);
        playerData.setCharacterId(1);
        playerData.setAvtId("100011");
        playerData.setFrameId("100011");
        playerData.setCurrentRoomId(012123213312);
        playerData.setTransform(newPlayerTransform());
        playerData.setCharacterAppearance(newAppearanceData());
        playerData.setGender("Female");
        playerData.setSkinColorId(0);

        return playerData;
    }
    @EzyAutoBind
    private TransformModel newPlayerTransform() {
        TransformModel newTransform = new TransformModel();
        newTransform.setAngle(0);
        newTransform.setPosX(0);
        newTransform.setPosY(0);
        newTransform.setPosZ(0);
        newTransform.setRotX(0);
        newTransform.setRotY(0);
        newTransform.setRotZ(0);

        return newTransform;
    }
    @EzyAutoBind
    private CharacterAppearanceModel newAppearanceData() {

        CharacterAppearanceModel newAppearance = new CharacterAppearanceModel();
        newAppearance.setFace(0);
        newAppearance.setHair(0);
        newAppearance.setShirt(0);
        newAppearance.setCoat(0);
        newAppearance.setPants(0);
        newAppearance.setShoes(0);
        newAppearance.setGloves(0);
        newAppearance.setGlass(0);
        newAppearance.setHat(0);
        newAppearance.setBag(0);
        newAppearance.setSuit(0);

        return newAppearance;
    }
}