import java.util.ArrayList;
import java.util.Collections;

public class BinaryTree {
    private Node root;
    private static class Node {
        Node left = null;
        Node right = null;
        UserType value;
        int size = 1;
        Node(UserType value) {
            this.value = value;
        }
    }

    public void insert (UserType val) {
        Node newnode = new Node(val);
        if (root == null) {
            root = newnode;
            return;
        }
        Node current = root;
        while (true) {
            current.size++;
            Comparator cmp = val.getTypeComparator();
            if (cmp.compare(current.value, val) > 0) {
                if (current.left == null) {
                    current.left = newnode;
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = newnode;
                    return;
                }
                current = current.right;
            }
        }
    }

    public boolean isEmpty() {
        return root == null;
    }
    private static int sizeOf(Node node) {
        return node == null ? 0 : node.size;
    }
    public int size() {
        return sizeOf(root);
    }
    private static int height(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }
    public int height() {
        return height(root);
    }
    private void checkIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Индекс " + index + " вне диапазона, размер дерева " + size());
        }
    }
    public UserType get(int index) {
        checkIndex(index);
        Node current = root;
        while (true) {
            int leftSize = sizeOf(current.left);
            if (index < leftSize) {
                current = current.left;
            } else if (index == leftSize) {
                return current.value;
            } else {
                index -= leftSize + 1;
                current = current.right;
            }
        }
    }
    public UserType remove(int index) {
        checkIndex(index);
        UserType removed = get(index);
        root = remove(root, index);
        return removed;
    }
    private Node remove(Node node, int index) {
        int leftSize = sizeOf(node.left);
        if (index < leftSize) {
            node.left = remove(node.left, index);
        } else if (index > leftSize) {
            node.right = remove(node.right, index - leftSize - 1);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            node.value = minValue(node.right);
            node.right = remove(node.right, 0);
        }
        node.size = 1 + sizeOf(node.left) + sizeOf(node.right);
        return node;
    }

    private static UserType minValue(Node node) {
        while (node.left != null) node = node.left;
        return node.value;
    }
    public void forEach(MyAction<UserType> action) {
        traverse(root, action);
    }

    private void traverse(Node node, MyAction<UserType> action) {
        if (node == null) return;
        traverse(node.left, action);
        action.toDo(node.value);
        traverse(node.right, action);
    }
    public BinaryTree balance() {
        UserType[] values = new UserType[size()];
        final int[] pos = {0};
        forEach(new MyAction<UserType>() {
            @Override
            public void toDo(UserType v) {
                values[pos[0]++] = v;
            }
        });
        BinaryTree result = new BinaryTree();
        result.root = build(values, 0, values.length - 1);
        return result;
    }
    private static Node build(UserType[] a, int left, int right) {
        if (left > right) return null;
        int mid = left + (right - left) / 2;
        Node node = new Node(a[mid]);
        node.left = build(a, left, mid - 1);
        node.right = build(a, mid + 1, right);
        node.size = right - left + 1;
        return node;
    }
    public ArrayList<UserType> toArrayList() {
        ArrayList<UserType> result = new ArrayList<>();
        forEach(new MyAction<UserType>() {
            @Override
            public void toDo(UserType v) {
                result.add(v);
            }
        });
        return result;
    }

    public ArrayList<UserType> sort(Comparator cmp) {
        if (cmp == null) {
            System.out.println("Ошибка: компаратор = null");
            return toArrayList();
        }
        ArrayList<UserType> list = toArrayList();
        list.sort(cmp::compare);
        return list;
    }

}
