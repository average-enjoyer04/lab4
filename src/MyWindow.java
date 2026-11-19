import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MyWindow extends JFrame {
    private final UserFactory factory = new UserFactory();
    private BinaryTree tree = new BinaryTree();
    private UserType prototype;

    private final JComboBox<String> typeCombo;
    private final JComboBox<UserType> itemCombo;
    private final JTextField inputField;
    private final JTextArea log;

    public MyWindow() {
        super("Л.р.4");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        JPanel top = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Тип:"));
        typeCombo = new JComboBox<>(factory.getTypeNameList().toArray(new String[0]));
        row1.add(typeCombo);

        row1.add(new JLabel("Данные:"));
        inputField = new JTextField(20);
        row1.add(inputField);

        JButton addBtn = new JButton("Добавить");
        row1.add(addBtn);
        top.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeBtn = new JButton("Удалить");
        JButton sortBtn = new JButton("Сортировать");
        JButton balanceBtn = new JButton("Балансировать");
        row2.add(removeBtn);
        row2.add(sortBtn);
        row2.add(balanceBtn);
        top.add(row2);

        add(top, BorderLayout.NORTH);

        itemCombo = new JComboBox<>();
        add(itemCombo, BorderLayout.CENTER);

        log = new JTextArea(12, 40);
        log.setEditable(false);
        add(new JScrollPane(log), BorderLayout.SOUTH);

        // ---------- Стартовый прототип ----------
        if (typeCombo.getItemCount() > 0) {
            prototype = factory.getBuilderByName((String) typeCombo.getSelectedItem());
            log.append("Стартовый тип: " + prototype.typeName() + "\n");
        }

        // ---------- Листенеры ----------

        // смена типа → новый прототип + СБРОС дерева
        typeCombo.addActionListener(e -> {
            String name = (String) typeCombo.getSelectedItem();
            prototype = factory.getBuilderByName(name);
            tree = new BinaryTree();              // ⚠️ дерево хранит один тип
            refreshList();
            log.append("Выбран тип: " + name + "\n");
        });

        // Добавить
        addBtn.addActionListener(e -> {
            String data = inputField.getText().trim();
            if (data.isEmpty()) {
                log.append("Ошибка: пустой ввод\n");
                return;
            }
            UserType item = (UserType) prototype.parseValue(data);
            if (item == null) {
                log.append("Ошибка парсинга: '" + data + "'\n");
                return;
            }
            tree.insert(item);
            refreshList();
            inputField.setText("");
            log.append("Добавлено: " + item + "\n");
        });

        // Удалить (по индексу из списка)
        removeBtn.addActionListener(e -> {
            int idx = itemCombo.getSelectedIndex();
            if (idx < 0) {
                log.append("Ошибка: ничего не выбрано\n");
                return;
            }
            UserType removed = tree.remove(idx);
            refreshList();
            log.append("Удалено: " + removed + "\n");
        });

        // Сортировать (по компаратору прототипа — он вернёт список)
        sortBtn.addActionListener(e -> {
            ArrayList<UserType> sorted = tree.sort(prototype.getTypeComparator());
            log.append("Отсортировано: " + sorted + "\n");
        });

        // Балансировать
        balanceBtn.addActionListener(e -> {
            tree = tree.balance();
            refreshList();
            log.append("Дерево сбалансировано, высота=" + tree.height() + "\n");
        });
    }

    // Заполнить itemCombo из дерева
    private void refreshList() {
        itemCombo.removeAllItems();
        for (UserType u : tree.toArrayList()) {
            itemCombo.addItem(u);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyWindow().setVisible(true));
    }
}