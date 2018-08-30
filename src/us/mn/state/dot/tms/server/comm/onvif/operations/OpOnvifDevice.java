package us.mn.state.dot.tms.server.comm.onvif.operations;

import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.OnvifProperty;
import us.mn.state.dot.tms.server.comm.onvif.OnvifSessionMessenger;
import us.mn.state.dot.tms.server.comm.onvif.OpOnvif;
import us.mn.state.dot.tms.server.comm.onvif.properties.OnvifDeviceRebootProperty;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;

import java.io.IOException;

/**
 * An OpOnvifDevice sends OnvifDevice*Properties to the Device Service.
 *
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class OpOnvifDevice extends OpOnvif<OnvifProperty> {
	private DeviceRequest request;

	public OpOnvifDevice(
		DeviceImpl d,
		OnvifSessionMessenger session,
		DeviceRequest r)
	{
		super(PriorityLevel.URGENT, d, session, OnvifService.DEVICE);
		request = r;
	}

	/**
	 * More Device commands may be supported in the future, but Reboot is
	 * all the UI supports for now.
	 */
	@Override
	protected OnvifPhase phaseTwo() {
		return new Reboot();
	}

	protected class Reboot extends OnvifPhase {
		@Override
		protected OnvifProperty selectProperty() throws IOException {
			switch (request) {
			case RESET_DEVICE:
				return new OnvifDeviceRebootProperty(session);
			default:
				throw new IOException(
					"Unsupported: " + request);
			}
		}

		@Override
		protected OnvifPhase nextPhase() throws IOException {
			return null;
		}
	}
}
