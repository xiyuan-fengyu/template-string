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

    protected Object _0;
    protected Object _1;
    protected Object _2;
    protected Object _3;
    protected Object _4;
    protected Object _5;
    protected Object _6;
    protected Object _7;
    protected Object _8;
    protected Object _9;
    protected Object _10;
    protected Object _11;
    protected Object _12;
    protected Object _13;
    protected Object _14;
    protected Object _15;
    protected Object _16;
    protected Object _17;
    protected Object _18;
    protected Object _19;
    protected Object _20;
    protected Object _21;
    protected Object _22;
    protected Object _23;
    protected Object _24;
    protected Object _25;
    protected Object _26;
    protected Object _27;
    protected Object _28;
    protected Object _29;
    protected Object _30;
    protected Object _31;
    protected Object _32;
    protected Object _33;
    protected Object _34;
    protected Object _35;
    protected Object _36;
    protected Object _37;
    protected Object _38;
    protected Object _39;
    protected Object _40;
    protected Object _41;
    protected Object _42;
    protected Object _43;
    protected Object _44;
    protected Object _45;
    protected Object _46;
    protected Object _47;
    protected Object _48;
    protected Object _49;
    protected Object _50;
    protected Object _51;
    protected Object _52;
    protected Object _53;
    protected Object _54;
    protected Object _55;
    protected Object _56;
    protected Object _57;
    protected Object _58;
    protected Object _59;
    protected Object _60;
    protected Object _61;
    protected Object _62;
    protected Object _63;

    public RawStringContext(Object ...args) {
        $num = args != null ? args.length : 0;
        for (int i = 0; i < $num; i++) {
            setContext(i, args[i]);
        }
    }

    private void setContext(int index, Object value) {
        try {
            Field field = RawStringContext.class.getDeclaredField("_" + index);
            field.setAccessible(true);
            field.set(this, value);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
