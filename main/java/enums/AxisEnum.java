package enums;

public enum AxisEnum {

    AXIS_X(1, "X轴"),
    AXIS_Y(2, "Y轴"),
    ;

    private int code;
    private String key;


    AxisEnum(int code, String key) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }
}
