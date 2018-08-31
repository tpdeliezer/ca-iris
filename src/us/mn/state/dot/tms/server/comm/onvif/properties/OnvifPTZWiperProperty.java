package us.mn.state.dot.tms.server.comm.onvif.properties;

import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SendAuxiliaryCommand;
import us.mn.state.dot.tms.server.comm.onvif.generated.org.onvif.ver20.ptz.wsdl.SendAuxiliaryCommandResponse;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SessionNotStartedException;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.SoapTransmissionException;

import java.io.IOException;
import java.util.List;

/**
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OnvifPTZWiperProperty extends OnvifProperty {
	/** true if we should switch the wiper on, else false */
	private boolean switchOn;
	/**
	 * based on the onvif spec, these are the only foreseeable free form
	 * versions of these auxiliary commands
	 */
	private static String WIPER_OFF[] = {"tt:Wiper|Off", "wiperon"};
	private static String WIPER_ON[] = {"tt:Wiper|On", "wiperoff"};

	public OnvifPTZWiperProperty(
		OnvifSessionMessenger session, boolean switchOn)
	{
		super(session);
		this.switchOn = switchOn;
	}

	@Override
	protected void encodeStore() throws IOException {
		// ensure that the device supports wiper auxiliary command
		SendAuxiliaryCommand supportedCmd = findSupportedCmd();
		if (supportedCmd == null)
			throw new OperationNotSupportedException("Wiper");
		else
			doWiper(supportedCmd);
	}

	private void doWiper(SendAuxiliaryCommand cmd) throws IOException {
		response = session.makeRequest(cmd,
			SendAuxiliaryCommandResponse.class);
		if (switchOn)
			doneMsg = "Wiping";
	}

	private SendAuxiliaryCommand findSupportedCmd()
		throws SessionNotStartedException, SoapTransmissionException,
		ServiceNotSupportedException, OperationNotSupportedException
	{
		List<String> strCmds = session.getNodes().get(0)
			.getAuxiliaryCommands();
		return switchOn ?
			initCmd(matchAny(strCmds, WIPER_ON))
			: initCmd(matchAny(strCmds, WIPER_OFF));
	}

	/**
	 * @return a initialized AuxiliaryCommand if the param is not null
	 */
	private SendAuxiliaryCommand initCmd(String param)
		throws SessionNotStartedException
	{
		SendAuxiliaryCommand cmd = null;
		if (param != null) {
			cmd = new SendAuxiliaryCommand();
			cmd.setProfileToken(session.getMediaProfileTok());
			cmd.setAuxiliaryData(param);
		}
		return cmd;
	}

	/**
	 * @return the last String in reference that matches any String in
	 * 	findAny (null if none found)
	 */
	private String matchAny(List<String> reference, String[] findAny) {
		String match = null;
		if (reference != null && findAny != null) {
			for (String s : reference) {
				for (String find : findAny) {
					if (s.equalsIgnoreCase(find))
						match = s;
				}
			}
		}
		return match;
	}
}
