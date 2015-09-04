package net.vinote.smart.socket.protocol.p2p.filter;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;

import net.vinote.smart.socket.logger.RunLogger;
import net.vinote.smart.socket.protocol.DataEntry;
import net.vinote.smart.socket.protocol.enums.ReturnCodeEnum;
import net.vinote.smart.socket.protocol.p2p.message.BaseMessage;
import net.vinote.smart.socket.protocol.p2p.message.LoginAuthReq;
import net.vinote.smart.socket.protocol.p2p.message.LoginAuthResp;
import net.vinote.smart.socket.protocol.p2p.message.SecureSocketMessageReq;
import net.vinote.smart.socket.service.filter.SmartFilter;
import net.vinote.smart.socket.service.session.Session;
import net.vinote.smart.socket.transport.TransportSession;

/**
 * 鉴权过滤器
 * 
 * @author Seer
 * @version SecureFilter.java, v 0.1 2015年8月24日 下午6:43:50 Seer Exp.
 */
public class SecureFilter implements SmartFilter {
	private static final String SECURE_TOKEN = "SECURE_TOKEN";

	@Override
	public void filterDataEntrys(TransportSession session, List<DataEntry> d) {
	}

	@Override
	public void processFilter(Session session, DataEntry d) {
		Object token = session.getAttribute(SECURE_TOKEN);
		BaseMessage msg = (BaseMessage) d;
		if (token == null
				&& !(d instanceof LoginAuthReq || d instanceof SecureSocketMessageReq)) {
			LoginAuthResp resp = new LoginAuthResp(msg.getHead());
			resp.setReturnCode(ReturnCodeEnum.NEED_AUTH.getCode());
			resp.setReturnDesc(ReturnCodeEnum.NEED_AUTH.getDesc());
			RunLogger.getLogger().log(Level.WARNING,
					"client " + session.getLocalAddress() + " need login auth");
			try {
				session.sendWithoutResponse(resp);
			} catch (Exception e) {
				RunLogger.getLogger().log(e);
			} finally {
				session.invalidate();
			}
		}
	}

	@Override
	public void readFilter(TransportSession session, DataEntry d) {
	}

	@Override
	public void receiveFailHandler(TransportSession session, DataEntry d) {
	}

	@Override
	public void writeFilter(TransportSession session, ByteBuffer d) {
	}

}