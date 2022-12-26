package cn.hashq.netpoststation.handler;

import cn.hashq.netpoststation.constant.ProtoConstant;
import cn.hashq.netpoststation.dto.ProtoMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Protobuf编码用
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
public class ProtobufEncoder extends MessageToByteEncoder<ProtoMsg.Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoMsg.Message msg, ByteBuf out) throws Exception {
        encode0(msg, out);
    }

    private void encode0(ProtoMsg.Message msg, ByteBuf out) {
        out.writeShort(ProtoConstant.MAGIC_CODE);
        out.writeShort(ProtoConstant.VERSION_CODE);
        byte[] bytes = msg.toByteArray();
        int length = bytes.length;
        log.info("encode length {}", length);
        out.writeInt(length);
        out.writeBytes(bytes);
    }
}
