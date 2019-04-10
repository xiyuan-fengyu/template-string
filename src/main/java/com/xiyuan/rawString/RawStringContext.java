package com.xiyuan.rawString;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by xiyuan_fengyu on 2019/4/10 16:30.
 */
@SuppressWarnings("unused")
public class RawStringContext implements Serializable {

    private static final long serialVersionUID = -1031036298286713119L;

    protected final int $num;

    protected Object arg0;
    protected Object arg1;
    protected Object arg2;
    protected Object arg3;
    protected Object arg4;
    protected Object arg5;
    protected Object arg6;
    protected Object arg7;
    protected Object arg8;
    protected Object arg9;
    protected Object arg10;
    protected Object arg11;
    protected Object arg12;
    protected Object arg13;
    protected Object arg14;
    protected Object arg15;
    protected Object arg16;
    protected Object arg17;
    protected Object arg18;
    protected Object arg19;
    protected Object arg20;
    protected Object arg21;
    protected Object arg22;
    protected Object arg23;
    protected Object arg24;
    protected Object arg25;
    protected Object arg26;
    protected Object arg27;
    protected Object arg28;
    protected Object arg29;
    protected Object arg30;
    protected Object arg31;
    protected Object arg32;
    protected Object arg33;
    protected Object arg34;
    protected Object arg35;
    protected Object arg36;
    protected Object arg37;
    protected Object arg38;
    protected Object arg39;
    protected Object arg40;
    protected Object arg41;
    protected Object arg42;
    protected Object arg43;
    protected Object arg44;
    protected Object arg45;
    protected Object arg46;
    protected Object arg47;
    protected Object arg48;
    protected Object arg49;
    protected Object arg50;
    protected Object arg51;
    protected Object arg52;
    protected Object arg53;
    protected Object arg54;
    protected Object arg55;
    protected Object arg56;
    protected Object arg57;
    protected Object arg58;
    protected Object arg59;
    protected Object arg60;
    protected Object arg61;
    protected Object arg62;
    protected Object arg63;

    public RawStringContext(Object ...args) {
        $num = args != null ? args.length : 0;
        for (int i = 0; i < $num; i++) {
            setArg(i, args[i]);
        }
    }

    private void setArg(int index, Object value) {
        try {
            Field field = RawStringContext.class.getDeclaredField("arg" + index);
            field.setAccessible(true);
            field.set(this, value);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
