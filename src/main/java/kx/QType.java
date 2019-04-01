package kx;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import kx.c.Dict;
import kx.c.Flip;
import kx.c.Minute;
import kx.c.Month;
import kx.c.Second;
import kx.c.Timespan;

import static kx.QConst.*;

public enum QType {
    BOOLEAN(0, Boolean.class,SIMPLE),
    BYTE(1, Byte.class,SIMPLE),
    SHORT(2, Short.class,SIMPLE),
    INTEGER(3, Integer.class,SIMPLE),
    LONG(4, Long.class,SIMPLE),
    FLOAT(5, Float.class,SIMPLE),
    DOUBLE(6, Double.class,SIMPLE),
    CHARACTER(7, Character.class ,SIMPLE),
    STRING(8, String.class ,SIMPLE),
    SQL_DATE(9, Date.class ,SIMPLE),
    SQL_TIME(10, Time.class,SIMPLE),
    SQL_TIMESTAMP(11, Timestamp.class,SIMPLE),
    DATE(12, java.util.Date.class,SIMPLE),
    KX_TIMESPAN(13, Timespan.class,SIMPLE),
    KX_MONTH(14, Month.class,SIMPLE),
    KX_MINUTE(15, Minute.class,SIMPLE),
    KX_SECOND(16, Second.class,SIMPLE),
    BOOLEAN_PRIM_ARRAY(17, boolean[].class, ARRAY),
    BYTE_PRIM_ARRAY(18, byte[].class,  ARRAY),
    SHORT_PRIM_ARRAY(19, short[].class,  ARRAY),
    INT_PRIM_ARRAY(20, int[].class,  ARRAY),
    LONG_PRIM_ARRAY(21, long[].class,  ARRAY),
    FLOAT_PRIM_ARRAY(22, float[].class,  ARRAY),
    DOUBLE_PRIM_ARRAY(23, double[].class,  ARRAY),
    CHAR_PRIM_ARRAY(24, char[].class,  ARRAY),
    STRING_ARRAY(25, String[].class,  OARRAY),
    SQL_DATE_ARRAY(26, Date[].class,  OARRAY),
    SQL_TIME_ARRAY(27, Time[].class,  OARRAY),
    SQL_TIMESTAMP_ARRAY(28, Timestamp[].class,  OARRAY),
    DATE_ARRAY(29, java.util.Date[].class,  OARRAY),
    OBJECT_ARRAY(30, Object[].class,  OARRAY),
    KX_TIMESPAN_ARRAY(31, Timespan[].class,  OARRAY),
    KX_MONTH_ARRAY(32, Month[].class,  OARRAY),
    KX_MINUTE_ARRAY(33, Minute[].class,  OARRAY),
    KX_SECOND_ARRAY(34, Second[].class,  OARRAY),
    KX_FLIP(35, Flip.class, COMPLEX),
    KX_DICT(36, Dict.class, COMPLEX),
    NO_TYPE(37, null, SIMPLE),
    CONFIG_PARAM(38, String.class, OTHER);



    private int id;
    private Class<?> classType;
    private int category;

    private QType(int id, Class<?> classType,int category) {
        this.id = id;
        this.setClassType(classType);
        this.category = category;
    }

    public int getId() {
        return this.id;
    }

    public Class<?> getClassType() {
        return this.classType;
    }


    public static QType getEnumFromType(Class<?> type) {
        if (type == null) {
            return null;
        }
        for (QType e : QType.values()) {
            if (type.equals(e.getClassType())) {
                return e;
            }
        }
        return null;
    }

    public static QType getEnumFromId(int id) {
        for (QType e : QType.values()) {
            if (id == e.getId()) {
                return e;
            }
        }
        return null;
    }

    public static QType getEnumFromString(String str) {
        if (str == null) {
            return null;
        }
        for (QType e : QType.values()) {
            if (str.equalsIgnoreCase(e.getClassType().getSimpleName())) {
                return e;
            }
        }
        return null;
    }

    public boolean isSimpleValue() {
        return category==SIMPLE;
    }

    public boolean isCollection() {
        return category==ARRAY || category==OARRAY || category == COMPLEX;
    }

    public boolean isObjectArray() {
        return category == OARRAY;
    }

    /**
     * returns whether the type is a primitive array which requires specific functionality in casting
     */
    public boolean isPrimitiveArray() {
        return category == ARRAY;
    }

    /**
     * return true if the type is a Dict or a Flip
     */
    public boolean isKXComplexObject() {
        return category == COMPLEX;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

}
