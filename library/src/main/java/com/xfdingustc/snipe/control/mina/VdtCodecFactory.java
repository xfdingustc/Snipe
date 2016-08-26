package com.xfdingustc.snipe.control.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by Xiaofei on 2016/7/4.
 */
public class VdtCodecFactory implements ProtocolCodecFactory {

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return new VdtDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return new VdtEncoder();
    }
}