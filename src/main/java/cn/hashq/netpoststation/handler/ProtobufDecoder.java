package cn.hashq.netpoststation.handler;

import cn.hashq.netpoststation.constant.ProtoConstant;
import cn.hashq.netpoststation.dto.ProtoMsg;
import cn.hashq.netpoststation.exception.InvalidFrameException;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * Protobuf解码用
 *
 * @author HashQ
 * @since 1.0
 */
@Slf4j
public class ProtobufDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object obj = decode0(ctx, in);
        if (Objects.nonNull(obj)) {
            out.add(obj);
        }
    }

    private Object decode0(ChannelHandlerContext ctx, ByteBuf in) throws InvalidFrameException, InvalidProtocolBufferException {
        in.markReaderIndex();
        if (in.readableBytes() < 8) {
            return null;
        }
        short magic = in.readShort();
        if (magic != ProtoConstant.MAGIC_CODE) {
            String error = "客户端口令不对:" + ctx.channel().remoteAddress();
            throw new InvalidFrameException(error);
        }
        int length = in.readInt();
        if (length < 0) {
            ctx.close();
        }
        if (length > in.readableBytes()) {
            in.resetReaderIndex();
            return null;
        }
        byte[] array = new byte[length];
        in.readBytes(array, 0, length);
        ProtoMsg.Message msg = ProtoMsg.Message.parseFrom(array);
        return msg;
    }
}
