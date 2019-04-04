package enums;

public enum MathEnum {

    ADD(1, "加"),
    REDUCE(-1, "减"),
    ;

    private int code;
    private String key;


    MathEnum(int code, String key) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }
}
