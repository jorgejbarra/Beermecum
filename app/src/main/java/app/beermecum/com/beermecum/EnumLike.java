package app.beermecum.com.beermecum;

public enum EnumLike {
    NOT_YET(0), LIKE(1), DISLIKE(-1);

    private int intValue;


    EnumLike(int intValue) {
        this.intValue = intValue;
    }

    public static EnumLike fromInt(Integer anInt) {
        switch (anInt) {
            case 1:
                return LIKE;
            case -1:
                return DISLIKE;
            default:
                return NOT_YET;
        }
    }

    public int toInt() {
        return intValue;
    }
}
