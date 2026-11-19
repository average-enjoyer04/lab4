import java.io.IOException;
import java.io.InputStreamReader;

public class UserDouble implements UserType {
    private double num;

    public void setNum(double num) {
        this.num = num;
    }

    public UserDouble() {
        this.num = 0.0;
    }
    public UserDouble(double num) {
        this.num = num;
    }
    public double getNum() {
        return num;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserDouble that)) return false;
        return Double.compare(num, that.num) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(num);
    }
    @Override
    public String toString() {
        return String.valueOf(num);
    }
    //реализация интерфейса
    @Override
    public String typeName() {
        return "UserDouble";
    }

    @Override
    public Object create() {
        return new UserDouble();
    }

    @Override
    public Object clone() {
        return new UserDouble(this.num);
    }

    @Override
    public Object readValue(InputStreamReader in) {
        StringBuilder sb = new StringBuilder();
        try {
            int c;
            while ((c = in.read()) != -1) {
                if (c == '\n') break;
                if (c != '\r') sb.append((char) c);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения: " + e.getMessage());
            return null;
        }
        if (sb.length() == 0) {
            System.out.println("Ошибка: Передана пустая строка!");
            return null;
        }
        return parseValue(sb.toString());
    }

    @Override
    public Object parseValue(String ss) {
        if (ss == null) {
            System.out.println("Ошибка: передана null-строка");
            return null;
        }
        try {
            double num = Double.parseDouble(ss.trim());
            return new UserDouble(num);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка парсинга: " + e.getMessage());
            return null;
        }
    }
    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> {
            UserDouble a = (UserDouble) o1;
            UserDouble b = (UserDouble) o2;
            return Double.compare(a.getNum(), b.getNum());
        };
    }
}
