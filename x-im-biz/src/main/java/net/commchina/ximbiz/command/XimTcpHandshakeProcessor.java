package net.commchina.ximbiz.command;

import org.jim.core.ImChannelContext;
import org.jim.core.ImConst;
import org.jim.core.ImPacket;
import org.jim.core.exception.ImException;
import org.jim.core.http.HttpRequest;
import org.jim.core.packets.Command;
import org.jim.core.packets.LoginReqBody;
import org.jim.core.utils.JsonKit;
import org.jim.server.JimServerAPI;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.LoginReqHandler;
import org.jim.server.processor.handshake.TcpHandshakeProcessor;

/**
 * @author xiaokang
 *
 */
public class XimTcpHandshakeProcessor extends TcpHandshakeProcessor {

	@Override
	public void onAfterHandshake(ImPacket packet, ImChannelContext imChannelContext) throws ImException
    {
		LoginReqHandler loginHandler = (LoginReqHandler) CommandManager.getCommand(Command.COMMAND_LOGIN_REQ);
		HttpRequest request = (HttpRequest)packet;
		String token = request.getParams().get("token") == null ? null : (String)request.getParams().get("token")[0];
		String userId = request.getParams().get("userId") == null ? null : (String)request.getParams().get("userId")[0];
		LoginReqBody loginBody = new LoginReqBody(token);
		loginBody.setUserId(userId);
		byte[] loginBytes = JsonKit.toJsonBytes(loginBody);
		request.setBody(loginBytes);
		try{
			request.setBodyString(new String(loginBytes, ImConst.CHARSET));
		}catch (Exception e){
			throw new ImException(e);
		}
		ImPacket loginRespPacket = loginHandler.handler(request, imChannelContext);
		if(loginRespPacket != null){
			JimServerAPI.send(imChannelContext, loginRespPacket);
		}
	}
}
