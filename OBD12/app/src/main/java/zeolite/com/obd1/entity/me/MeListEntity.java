package zeolite.com.obd1.entity.me;

/**
 * Created by Zeolite on 16/1/21.
 */
public class MeListEntity {

    private String iconName;
    private String name;

    public MeListEntity(String iconName, String name) {
        this.iconName = iconName;
        this.name = name;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
