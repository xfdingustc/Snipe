package com.xfdingustc.snipe.control.mina;

import android.util.Xml;


import com.xfdingustc.snipe.control.SimpleOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.xmlpull.v1.XmlSerializer;

import java.util.Locale;

/**
 * Created by Xiaofei on 2016/7/4.
 */
public class VdtEncoder implements ProtocolEncoder, VdtProtocolConsts {
    private static final String TAG = VdtEncoder.class.getSimpleName();


    @Override
    public void dispose(IoSession session) throws Exception {

    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
//        Logger.t(TAG).d(message.toString());

        VdtCommand command = (VdtCommand) message;
        IoBuffer buff = IoBuffer.allocate(HEAD_SIZE).setAutoExpand(true);


        SimpleOutputStream sos = new SimpleOutputStream(1024);
        XmlSerializer xml = Xml.newSerializer();

        sos.reset();
        sos.writeZero(8);

        xml.setOutput(sos, "UTF-8");
        xml.startDocument("UTF-8", true);
        xml.startTag(null, XML_CCEV);

        xml.startTag(null, XML_CMD);
        String act = String.format(Locale.US, "ECMD%1$d.%2$d", command.domain, command.cmdType); // TODO : why US

        xml.attribute(null, XML_ACT, act);
        xml.attribute(null, XML_P1, command.parameter1);
        xml.attribute(null, XML_P2, command.parameter2);
        xml.endTag(null, XML_CMD);

        xml.endTag(null, XML_CCEV);
        xml.endDocument();


        int size = sos.getSize();
        if (size >= HEAD_SIZE) {
            sos.writei32(0, size);
            sos.writei32(4, size - HEAD_SIZE);
        } else {
            sos.writei32(0, HEAD_SIZE);
            // append is 0
            sos.clear(size, HEAD_SIZE - size);
            size = HEAD_SIZE;
        }


        buff.put(sos.getBuffer(), 0, size);
        buff.flip();

        out.write(buff);
        out.flush();

    }
}
