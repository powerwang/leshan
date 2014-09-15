/*
 * Copyright (c) 2013, Sierra Wireless
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of {{ project }} nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package leshan.server.lwm2m.impl.tlv;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import leshan.server.lwm2m.tlv.Tlv;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TlvEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(TlvEncoder.class);

    /**
     * Encodes an array of TLV.
     */
    public static ByteBuffer encode(Tlv[] tlvs) {
        int size = 0;

        LOG.trace("start");
        for (Tlv tlv : tlvs) {

            int length = tlvEncodedLength(tlv);
            size += tlvEncodedSize(tlv, length);
            LOG.trace("tlv size : {}", size);
        }
        LOG.trace("done, size : {}", size);
        ByteBuffer b = ByteBuffer.allocate(size);
        b.order(ByteOrder.BIG_ENDIAN);
        for (Tlv tlv : tlvs) {
            encode(tlv, b);
        }
        b.flip();
        return b;
    }

    /**
     * Encodes an integer value.
     */
    public static byte[] encodeInteger(Number number) {
        ByteBuffer iBuf = null;
        long lValue = number.longValue();
        if (lValue >= Byte.MIN_VALUE && lValue <= Byte.MAX_VALUE) {
            iBuf = ByteBuffer.allocate(1);
            iBuf.put((byte) lValue);
        } else if (lValue >= Short.MIN_VALUE && lValue <= Short.MAX_VALUE) {
            iBuf = ByteBuffer.allocate(2);
            iBuf.putShort((short) lValue);
        } else if (lValue >= Integer.MIN_VALUE && lValue <= Integer.MAX_VALUE) {
            iBuf = ByteBuffer.allocate(4);
            iBuf.putInt((int) lValue);
        } else {
            iBuf = ByteBuffer.allocate(8);
            iBuf.putLong(lValue);
        }
        return iBuf.array();
    }

    /**
     * Encodes a floating point value.
     */
    public static byte[] encodeFloat(Number number) {
        ByteBuffer fBuf = null;
        double dValue = number.doubleValue();
        if (dValue >= Float.MIN_VALUE && dValue <= Float.MAX_VALUE) {
            fBuf = ByteBuffer.allocate(4);
            fBuf.putFloat((float) dValue);
        } else {
            fBuf = ByteBuffer.allocate(8);
            fBuf.putDouble(dValue);
        }
        return fBuf.array();
    }

    /**
     * Encodes a boolean value.
     */
    public static byte[] encodeBoolean(boolean value) {
        return value ? new byte[] { 1 } : new byte[] { 0 };
    }

    /**
     * Encodes a string value.
     */
    public static byte[] encodeString(String value) {
        return value.getBytes(Charsets.UTF_8);
    }

    /**
     * Encodes a date value.
     */
    public static byte[] encodeDate(Date value) {
        ByteBuffer tBuf = ByteBuffer.allocate(4);
        tBuf.putInt((int) (value.getTime() / 1000L));
        return tBuf.array();
    }

    private static int tlvEncodedSize(Tlv tlv, int length) {
        int size = 1 /* HEADER */;
        size += (tlv.getIdentifier() < 65_536) ? 1 : 2; /* 8 bits or 16 bits identifiers */

        if (length < 8) {
            size += 0;
        } else if (length < 256) {
            size += 1;
        } else if (length < 65_536) {
            size += 2;
        } else if (length < 16_777_216) {
            size += 3;
        } else {
            throw new IllegalArgumentException("length should fit in max 24bits");
        }

        size += length;
        return size;
    }

    private static int tlvEncodedLength(Tlv tlv) {
        int length;
        switch (tlv.getType()) {
        case RESOURCE_VALUE:
        case RESOURCE_INSTANCE:
            length = tlv.getValue().length;
            break;
        default:
            length = 0;
            for (Tlv child : tlv.getChildren()) {
                int subLength = tlvEncodedLength(child);
                length += tlvEncodedSize(child, subLength);
            }
        }

        return length;
    }

    private static void encode(Tlv tlv, ByteBuffer b) {
        int length;
        length = tlvEncodedLength(tlv);
        int typeByte;

        switch (tlv.getType()) {
        case OBJECT_INSTANCE:
            typeByte = 0b00_000000;
            break;
        case RESOURCE_INSTANCE:
            typeByte = 0b01_000000;
            break;
        case MULTIPLE_RESOURCE:
            typeByte = 0b10_000000;
            break;
        case RESOURCE_VALUE:
            // encode the value
            typeByte = 0b11_000000;
            break;
        default:
            throw new IllegalArgumentException("unknown TLV type : '" + tlv.getType() + "'");
        }

        // encode identifier length
        typeByte |= (tlv.getIdentifier() < 65_536) ? 0b00_0000 : 0b10_0000;

        // type of length
        if (length < 8) {
            typeByte |= length;
        } else if (length < 256) {
            typeByte |= 0b0000_1000;
        } else if (length < 65_536) {
            typeByte |= 0b0001_0000;
        } else {
            typeByte |= 0b0001_1000;
        }

        // fill the buffer
        b.put((byte) typeByte);
        if (tlv.getIdentifier() < 65_536) {
            b.put((byte) tlv.getIdentifier());
        } else {
            b.putShort((short) tlv.getIdentifier());
        }

        // write length

        if (length >= 8) {
            if (length < 256) {
                b.put((byte) length);
            } else if (length < 65_536) {
                b.putShort((short) length);
            } else {
                int msb = (length & 0xFF_00_00) >> 16;
                b.put((byte) msb);
                b.putShort((short) (length & 0xFF_FF));
                typeByte |= 0b0001_1000;
            }
        }

        switch (tlv.getType()) {
        case RESOURCE_VALUE:
        case RESOURCE_INSTANCE:
            b.put(tlv.getValue());
            break;
        default:
            for (Tlv child : tlv.getChildren()) {
                encode(child, b);
            }
            break;
        }
    }
}