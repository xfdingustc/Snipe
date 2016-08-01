package com.xfdingustc.snipe.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Xiaofei on 2015/9/22.
 */
public class ToStringUtils {
    public static String getString(Object object) {
        try {
            if (null == object) {
                return null;
            } else {

                Field[] field = object.getClass().getDeclaredFields();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < field.length && field.length > 0; i++) {
                    Field oneField = field[i];
                    oneField.setAccessible(true);
                    if ((oneField.getModifiers() & Modifier.STATIC) != Modifier.STATIC ){
                        sb.append(oneField.getName() + ": " + oneField.get(object) + "\t");
                    }
                }
                return sb.toString();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
