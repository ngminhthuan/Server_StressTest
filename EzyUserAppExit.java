package org.QTS.app.controller;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfoxserver.constant.EzyEventNames;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractAppEventController;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.event.EzyUserRemovedEvent;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import lombok.Setter;
import org.QTS.app.commands.NetworkPlayerCommand;
import org.QTS.app.commands.RoomCommand;
import org.QTS.app.service.commons.QworldCommonService;
import org.QTS.app.service.room.RoomService;
import org.QTS.common.GlobalData.CommonData;
import org.QTS.common.GlobalData.RoomData;
import org.QTS.common.models.NetworkCharacterModel;
import org.QTS.common.room.QPlayer;
import org.QTS.common.room.QRoom;

import java.util.List;

@Setter
@EzyRequestController
@EzyEventHandler(EzyEventNames.USER_REMOVED)
public class EzyUserAppExit  extends EzyAbstractAppEventController<EzyUserRemovedEvent> {

    @EzyAutoBind
    private EzyResponseFactory responseFactory;

    List<NetworkCharacterModel> infoList;
    private final RoomService roomService = new RoomService();
    private final QworldCommonService qworldCommonService = new QworldCommonService();

    @Override
    public void handle(EzyAppContext context, EzyUserRemovedEvent ez)
    {
        DisconnectAppImmediately(context, ez);

        // if player has joined the room
        QPlayer player = RoomData.instance().GetPlayerManager().getPlayer(ez.getUser().getName());
        if(player != null) {
            DisconnectRoomImmediately(player);
        }
    }

    private void DisconnectAppImmediately(EzyAppContext context, EzyUserRemovedEvent ez)
    {
        infoList = context.getProperty("PlayerInfoList");

        if (infoList != null) {
            for (int i = 0; i < infoList.size(); i++) {
                if (infoList.get(i).getPlayfabId().equals(ez.getUser().getName())) {
                    System.out.println("RemoveUser in disconnect server: " + infoList.get(i).getPlayfabId());
                    //infoList.remove(infoList.get(i));
                }
            }
            //context.setProperty("PlayerInfoList", infoList);
        }

        responseFactory.newObjectResponse()
                .command("NetworkPlayerRequest")
                .param("PlayfabId", ez.getUser().getName())
                .param("networkPlayerResponse", NetworkPlayerCommand.PlayerDisconnect)
                .users(context.getApp().getUserManager().getUserList())
                .execute();
    }

    private void DisconnectRoomImmediately(QPlayer player)
    {
        List<String> userList;
        System.out.println("room disconnect .-." + player.getName() + " " + player.getCurrentRoomId());
        QRoom room = roomService.getCurrentRoom(player.getName());
        roomService.removePlayer(player, room);

        userList = roomService.getAllPlayerNamesInRoom(room);

        if(room.isEmpty())
            roomService.closeRoom(room.getId());

        responseFactory.newObjectResponse()
                .command("RoomResponseHandle")
                .param("PlayfabId", player.getName())
                .param("roomRequestState", RoomCommand.LeaveRoom)
                .usernames(userList)
                .execute();
    }

    @EzyDoHandle("APP_EXIT")
    public void UserAppExit(EzyUser user, EzyAppContext context) // run to this when change channel, join home invitation, join room
    {
        // remove this user from player info in app list
        // remove from app by app.remove user
        System.out.println(user.getName() + " request to App Exit");
        context.getApp().getUserManager().removeUser(user);
        CommonData.instance().playersTransformData.remove(user.getName());

        qworldCommonService.removeInfoFromInfoList(context, user.getName());

        responseFactory.newObjectResponse()
                .command("ExitAppResponse")
                .user(user)
                .execute();

        responseFactory.newObjectResponse()
                .command("NetworkPlayerRequest")
                .param("PlayfabId", user.getName())
                .param("networkPlayerResponse", NetworkPlayerCommand.PlayerDisconnect)
                .users(context.getApp().getUserManager().getUserList())
                .execute();
    }
}
/*@EzyEventHandler(USER_REMOVED)
public class UserRemovedController extends EzyAbstractAppEventController<EzyUserRemovedEvent> {

    @Override
    public void handle(EzyAppContext ctx, EzyUserRemovedEvent event) {
        System.out.println("user removed: " + event.getUser());
    }
}*/
