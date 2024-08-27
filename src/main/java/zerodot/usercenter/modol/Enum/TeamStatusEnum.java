package zerodot.usercenter.modol.Enum;


/**
 * 队伍状态枚举
 */
public enum TeamStatusEnum {

    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密");


    private int values;

    private String text;

    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) return null;
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].getValues() == value){
                return values[i];
            }
        }
        return null;
    }
    TeamStatusEnum(int values, String text) {
        this.values = values;
        this.text = text;
    }

    public int getValues() {
        return values;
    }

    public void setValues(int values) {
        this.values = values;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
