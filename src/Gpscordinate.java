import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Gpscordinate implements UserType{
    private double latitude;
    private double longitude;
    private LocalDateTime time;

    public Gpscordinate(){
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.time = LocalDateTime.now();
    }
    public Gpscordinate(double latitude, double longitude, LocalDateTime time) {
        if (checkLatitude(latitude)) this.latitude = latitude;
        if (checkLongitude(longitude)) this.longitude = longitude;
        if (checkTime(time)) {
            this.time = time;
        } else {
            this.time = LocalDateTime.now();
        }
    }
    public void setLatitude(double latitude) {
        if (checkLatitude(latitude)) this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        if (checkLongitude(longitude)) this.longitude = longitude;
    }
    public void setTime(LocalDateTime time) {
        if (checkTime(time)) {
            this.time = time;
        } else {
            this.time = LocalDateTime.now();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocalDateTime getTime() {
        return time;
    }
    public String getDms() {
        String latDir = latitude >= 0 ? "N" : "S";
        double absLat = Math.abs(latitude);
        int latDeg = (int) absLat;
        int latMin = (int) ((absLat - latDeg) * 60);
        double latSec = ((absLat - latDeg) * 60 - latMin) * 60;
        String lonDir = longitude >= 0 ? "E" : "W";
        double absLon = Math.abs(longitude);
        int lonDeg = (int) absLon;
        int lonMin = (int) ((absLon - lonDeg) * 60);
        double lonSec = ((absLon - lonDeg) * 60 - lonMin) * 60;

        return String.format("%d°%02d'%04.1f\"%s, %d°%02d'%04.1f\"%s", latDeg, latMin, latSec, latDir, lonDeg, lonMin, lonSec, lonDir);
    }
    private boolean checkLatitude (double latitude) {
        boolean result = true;
        if (latitude < -90.0 || latitude > 90) {
            System.out.println("Ошибка: Широта должна быть от -90 до 90");
            result = false;
        }
        return result;
    }
    private boolean checkLongitude (double longitude) {
        boolean result = true;
        if (longitude > 180.0 || longitude < -180.0){
            System.out.println("Ошибка: Долгота должна быть от -180 до 180");
            result = false;
        }
        return result;
    }
    private boolean checkTime (LocalDateTime time) {
        if (time == null) System.out.println("Ошибка: передано нулевое время");
        return time != null;
    }
    public double distanceTo(Gpscordinate other) {
        int R = 6371;
        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    public double speed (Gpscordinate other) {
        double distance = this.distanceTo(other);
        double speed;
        Duration duration = Duration.between(this.time, other.time);
        if (duration.isZero()){
            System.out.println("Время не отличается! Вернем 0");
            return 0.0;
        }
        if (duration.isNegative()) {
            System.out.println("Время передали не в порядке следования. Скорость вернется отрицательной!");
        }
        speed = 3600000.0 * distance / duration.toMillis();
        return speed;
    }
    public void saveText(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(latitude + ";" + longitude + ";" + time.toString());
            System.out.println("Сохранено в текстовый файл: " + filename);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void saveBinary(String filename) {
        try (DataOutputStream outs = new DataOutputStream(new FileOutputStream(filename))) {
            outs.writeDouble(latitude);
            outs.writeDouble(longitude);
            outs.writeUTF(time.toString());
            System.out.println("Сохранено в бинарный файл: " + filename);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static Gpscordinate loadBin(String filename) {
        try (DataInputStream inb = new DataInputStream(new FileInputStream(filename))) {
            double latitude = inb.readDouble();
            double longitude = inb.readDouble();
            LocalDateTime time = LocalDateTime.parse(inb.readUTF());
            return new Gpscordinate(latitude, longitude, time);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static Gpscordinate loadText(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(";");
                if (parts.length != 3) {
                    System.out.println("Ошибка: неверный формат строки в файле");
                    return null;
                }
                double latitude = Double.parseDouble(parts[0].trim());
                double longitude = Double.parseDouble(parts[1].trim());
                LocalDateTime time = LocalDateTime.parse(parts[2].trim());
                return new Gpscordinate(latitude, longitude, time);
            } else {
                System.out.println("Ошибка: Файл пуст!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Gpscordinate that)) return false;
        return Double.compare(latitude, that.latitude) == 0 && Double.compare(longitude, that.longitude) == 0 && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, time);
    }
    @Override
    public String toString() {
        return String.format("GPS: (Широта: %.4f, Долгота: %.4f) | Время: %s", latitude, longitude, time.toString());
    }

    //вот здесь изменения
    @Override
    public String typeName() {
        return "Gpscordinate";
    }

    @Override
    public Object create() {
        return new Gpscordinate();
    }

    @Override
    public Object clone() {
        return new Gpscordinate(this.latitude, this.longitude, this.time);
    }

    @Override
    public Object readValue(InputStreamReader in) {
        StringBuilder sb = new StringBuilder();   // объявляем снаружи
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
        String[] parts = ss.split(";");
        if (parts.length != 3) {
            System.out.println("Ошибка: неверный формат строки (ожидается lat;lon;time)");
            return null;
        }
        try {
            double latitude  = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());
            LocalDateTime time = LocalDateTime.parse(parts[2].trim());
            return new Gpscordinate(latitude, longitude, time);
        } catch (Exception e) {
            System.out.println("Ошибка парсинга: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> {
            Gpscordinate a = (Gpscordinate) o1;
            Gpscordinate b = (Gpscordinate) o2;
            int byTime = a.getTime().compareTo(b.getTime());
            if (byTime != 0) return byTime;
            int byLat = Double.compare(a.getLatitude(), b.getLatitude());
            if (byLat != 0) return byLat;
            return Double.compare(a.getLongitude(), b.getLongitude());
        };
    }
}
