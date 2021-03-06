package us.mn.state.dot.tms.server.comm.onvif;

import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.DeviceImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationFailedException;
import us.mn.state.dot.tms.server.comm.onvif.properties.exceptions.OperationNotSupportedException;
import us.mn.state.dot.tms.server.comm.onvif.session.OnvifService;
import us.mn.state.dot.tms.server.comm.onvif.session.exceptions.ServiceNotSupportedException;

import java.io.IOException;

/**
 * An OpOnvif contains the logic for interpreting a specific IRIS client UI
 * request and converting it into one or more sequential OnvifProperties.
 * Sequential parts of the OpOnvif are sent using sequential OnvifPhases. In
 * the case of other Comms, more than one Property may be sent by an OpDevice,
 * but in the case of OpOnvifs, only one OnvifProperty is ever sent per OpOnvif.
 *
 * @author Wesley Skillern (Southwest Research Institue)
 */
public abstract class OpOnvif<T extends OnvifProperty> extends OpDevice<T> {
	protected final OnvifSessionMessenger session;
	private final OnvifService service;
	private final ControllerImpl controller;

	protected OpOnvif(
		PriorityLevel p, DeviceImpl d, OnvifSessionMessenger session,
		OnvifService service, ControllerImpl controller)
	{
		super(p, d);
		this.session = session;
		this.service = service;
		this.controller = controller;
	}

	/**
	 * @return An OnvifPhase returned is a single part of the sequence. A
	 * 	single phase may set/send multiple properties; however, this is
	 * 	generally not the case for OnvifPhases.
	 */
	@Override
	protected abstract OnvifPhase phaseTwo();

	protected abstract class OnvifPhase extends Phase<T> {
		// OnvifPhases only ever have one OnvifProperty, so it doesn't
		// make sense to queue them in the CommMessageImpl.
		// Furthermore, bypassing the usual add() and storeProps() of
		// the CommMessageImpl allows bypassing the null
		// checking on the input and output streams of the Messenger.
		// We don't use the streams directly for blocking soap calls.
		// Furthermore, most OnvifProperties have a response, so it
		// makes sense to encodeStore() and decodeStore() for each
		// OnvifProperty rather than doing all encodeStores() and
		// then all decodeStores().
		protected abstract T selectProperty() throws IOException;

		protected abstract OnvifPhase nextPhase() throws IOException;

		private T prop = null;

		private OnvifPhase next = null;

		protected OnvifPhase poll(CommMessage<T> mess)
			throws IOException
		{
			try {
				logPhase("Starting");
				session.selectService(service);
				prop = selectProperty();
				next = nextPhase();
				if (prop != null) {
					logPhase(prop.getClass().getSimpleName());
					if (prop.isQuery()) {
						mess.logQuery(prop);
						prop.encodeQuery(controller, null);
						prop.decodeQuery(controller, null);
					}
					else {
						mess.logStore(prop);
						prop.encodeStore(controller, null);
						prop.decodeStore(controller, null);
					}
					if (next == null)
						session.setStatus(prop.getDoneMsg());
				}
				return next;
			} catch (ServiceNotSupportedException
				| OperationNotSupportedException
				| OperationFailedException e) {
				// errors from which we can continue
				failed(e);
				return null;
			} catch (IOException e) {
				// unrecoverable errors
				failed(e);
				e.printStackTrace();
				throw e;
			} finally {
				report();
			}
		}

		private void failed(IOException e) {
			setFailed();
			logPhase(e.getMessage());
			session.setStatus(e.getMessage());
		}

		private void report() {
			String endStatus;
			if (prop == null)
				endStatus = "Ignored";
			else
				endStatus = "Completed";
			endStatus += "|";
			if (!isSuccess())
				endStatus += "Failed";
			else
				endStatus += "Success";
			endStatus += "|";
			if (next == null)
				endStatus += "Final";
			else
				endStatus += "Proceeding";
			logPhase(endStatus);
		}

		private void logPhase(String msg) {
			log(this.getClass().getSimpleName() + ": " + msg);
		}
	}

	protected void log(String msg) {
		session.log(msg, this);
	}
}
