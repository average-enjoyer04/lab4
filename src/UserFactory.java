import java.util.ArrayList;

public class UserFactory {
    private final ArrayList<UserType> prototypes = new ArrayList<>();

    public UserFactory() {
        prototypes.add(new UserDouble());
        prototypes.add(new Gpscordinate());
    }

    public ArrayList<String> getTypeNameList() {
        ArrayList<String> names = new ArrayList<>();
        for (UserType p : prototypes) {
            names.add(p.typeName());
        }
        return names;
    }

    public UserType getBuilderByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Ошибка: имя типа = null");
        }
        for (UserType p : prototypes) {
            if (p.typeName().equals(name)) {
                return (UserType) p.clone();
            }
        }
        throw new IllegalArgumentException("Неизвестный тип: " + name);
    }
}