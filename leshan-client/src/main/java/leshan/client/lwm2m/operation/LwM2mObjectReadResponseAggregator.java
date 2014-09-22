package leshan.client.lwm2m.operation;

import java.nio.ByteBuffer;

import leshan.server.lwm2m.impl.tlv.Tlv;
import leshan.server.lwm2m.impl.tlv.Tlv.TlvType;
import leshan.server.lwm2m.impl.tlv.TlvDecoder;

public class LwM2mObjectReadResponseAggregator extends LwM2mReadResponseAggregator {

	public LwM2mObjectReadResponseAggregator(final LwM2mExchange exchange, final int numExpectedResults) {
		super(exchange, numExpectedResults);
	}

	@Override
	protected Tlv createTlv(final int id, final LwM2mResponse response) {
		return new Tlv(TlvType.OBJECT_INSTANCE, TlvDecoder.decode(ByteBuffer.wrap(response.getResponsePayload())),
				null, id);
	}

}