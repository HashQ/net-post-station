package cn.hashq.netpoststation.constant;

import lombok.Data;

public class ProtoConstant {

    public static final short MAGIC_CODE = 0x86;

    public static final short VERSION_CODE = 0x01;


    public enum ResultCode {
        SUCCESS(0), AUTH_FAILED(1);
        private int code;

        ResultCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
