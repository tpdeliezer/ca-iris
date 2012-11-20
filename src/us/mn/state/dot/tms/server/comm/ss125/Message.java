/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2012  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.server.comm.ss125;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ChecksumException;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.ControllerProperty;
import us.mn.state.dot.tms.server.comm.ParsingException;

/**
 * SS125 message
 *
 * @author Douglas Lau
 */
public class Message implements CommMessage {

	/** Maximum number of octets in message body */
	static protected final int MAX_BODY_OCTETS = 244;

	/** Serial output stream */
	protected final OutputStream output;

	/** Serial input stream */
	protected final InputStream input;

	/** Controller for message */
	protected final ControllerImpl ctrl;

	/** SS125 destination ID (drop address) */
	protected final short dest_id;

	/** Controller property */
	protected SS125Property prop;

	/** Create a new SS125 message.
	 * @param out Output stream to write message data.
	 * @param is Input stream to read message responses.
	 * @param c Controller to send messages. */
	public Message(OutputStream out, InputStream is, ControllerImpl c) {
		output = out;
		input = is;
		ctrl = c;
		dest_id = c.getDrop();
	}

	/** Add a controller property */
	public void add(ControllerProperty cp) {
		if(cp instanceof SS125Property)
			prop = (SS125Property)cp;
	}

	/** Query the controller properties.
	 * @throws IOException On any errors sending message or receiving
	 *         response */
	public void queryProps() throws IOException {
		assert prop != null;
		byte[] body = prop.formatBodyGet();
		byte[] header = prop.formatHeader(body, dest_id);
		doPoll(header, body);
		while(!prop.isComplete()) {
			try {
				prop.parsePayload(doResponse(header, body));
			}
			catch(SocketTimeoutException e) {
				if(prop.hasData()) {
					OpSS125.log(ctrl, "PARTIAL INTERVAL: " +
						prop.toString());
				}
				throw e;
			}
			header[8]++;	// Increment sequence number
			body[1]++;	// Sub ID is like a packet number
		}
	}

	/** Store the controller properties.
	 * @throws IOException On any errors sending a request or receiving
	 *         response */
	public void storeProps() throws IOException {
		assert prop != null;
		byte[] body = prop.formatBodySet();
		byte[] header = prop.formatHeader(body, dest_id);
		doPoll(header, body);
		prop.parseResult(doResponse(header, body));
	}

	/** Perform a message poll.
	 * @param header Header of message.
	 * @param body Body of message.
	 * @throws IOException On any errors sending message. */
	protected void doPoll(byte[] header, byte[] body) throws IOException {
		input.skip(input.available());
		BufferedOutputStream bos = new BufferedOutputStream(output,256);
		bos.write(header);
		bos.write(SS125Property.crc8(header));
		bos.write(body);
		bos.write(SS125Property.crc8(body));
		bos.flush();
	}

	/** Receive a message response.
	 * @param shead Header of message sent.
	 * @param sbody Body of message sent.
	 * @return Body of message received.
	 * @throws IOException On any errors receiving response. */
	protected byte[] doResponse(byte[] shead, byte[] sbody)
		throws IOException
	{
		prop.delayResponse();
		byte[] rhead = recvResponse(10);
		byte h_crc = recvResponse(1)[0];
		int n_body = parseHead(rhead, h_crc, shead);
		byte[] rbody = recvResponse(n_body);
		byte b_crc = recvResponse(1)[0];
		parseBody(rbody, b_crc, sbody);
		return rbody;
	}

	/** Receive part of a response.
	 * @param n_bytes Number of bytes to receive.
	 * @return Response received.
	 * @throws IOException On any errors receiving response. */
	protected byte[] recvResponse(int n_bytes) throws IOException {
		byte[] resp = new byte[n_bytes];
		int n_rcv = 0;
		while(n_rcv < n_bytes) {
			int r = input.read(resp, n_rcv, n_bytes - n_rcv);
			if(r <= 0)
				throw new EOFException("END OF STREAM");
			n_rcv += r;
		}
		return resp;
	}

	/** Parse a message response header.
	 * @param rhead Received response header.
	 * @param crc Received header crc.
	 * @param shead Sent message header.
	 * @return Number of bytes in response body.
	 * @throws ParsingException On any errors parsing response header. */
	protected int parseHead(byte[] rhead, byte crc, byte[] shead)
		throws ParsingException
	{
		assert rhead.length == 10;
		assert shead.length == 10;
		if(crc != SS125Property.crc8(rhead))
			throw new ChecksumException("HEADER");
		if(rhead[0] != 'Z')
			throw new ParsingException("SENTINEL");
		if(rhead[1] != '1')
			throw new ParsingException("VERSION");
		if(rhead[2] != shead[5])
			throw new ParsingException("DEST SUB ID");
		if(rhead[3] != shead[6])
			throw new ParsingException("DEST ID");
		if(rhead[4] != shead[7])
			throw new ParsingException("DEST ID");
		if(rhead[5] != shead[2])
			throw new ParsingException("SRC SUB ID");
		if(rhead[6] != shead[3])
			throw new ParsingException("SRC ID");
		if(rhead[7] != shead[4])
			throw new ParsingException("SRC ID");
		if(rhead[8] != shead[8] + 1)
			throw new ParsingException("SEQUENCE");
		int n_body = rhead[9] & 0xFF;
		if(n_body < 3 || n_body > MAX_BODY_OCTETS)
			throw new ParsingException("BODY SIZE");
		return n_body;
	}

	/** Parse a message response body.
	 * @param rbody Received response body.
	 * @param crc Received body crc.
	 * @param sbody Send message body.
	 * @throws ParsingException On any errors parsing response body. */
	protected void parseBody(byte[] rbody, byte crc, byte[] sbody)
		throws ParsingException
	{
		assert rbody.length >= 3;
		assert sbody.length >= 3;
		if(crc != SS125Property.crc8(rbody))
			throw new ChecksumException("BODY");
		if(rbody[0] != sbody[0])
			throw new ParsingException("MESSAGE ID");
		if(rbody[1] != sbody[1])
			throw new ParsingException("MESSAGE SUB ID");
		if(rbody[2] != sbody[2])
			throw new ParsingException("READ OR WRITE");
	}
}
