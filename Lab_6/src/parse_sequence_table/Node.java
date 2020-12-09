package parse_sequence_table;

public class Node {

    public static int globalId = 0;

    private int id;
    private String info;
    private int father;
    private int sibling;

    public Node() {
    }

    public Node(String info, int father, int sibling) {
        this.id = globalId++;
        this.info = info;
        this.father = father;
        this.sibling = sibling;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getFather() {
        return father;
    }

    public void setFather(int father) {
        this.father = father;
    }

    public int getSibling() {
        return sibling;
    }

    public void setSibling(int sibling) {
        this.sibling = sibling;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", info=" + info +
                ", father=" + father +
                ", sibling=" + sibling +
                '}';
    }
}
