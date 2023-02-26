package taskmanagerapp.manager;

import taskmanagerapp.tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private Node head;
    private Node tail;
    private int size;
    private final HashMap<Integer, Node> nodeHashMap;

    public InMemoryHistoryManager() {
        nodeHashMap = new HashMap<>();
        head = null;
        tail = null;
    }

    class Node{
        private final Task data;
        private Node next;
        private Node prev;

        public Node(Task data, Node next, Node prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    public void isContainsForDelete(Task task){
        int id = task.getId();
        if (nodeHashMap.containsKey(id)) {
            removeNode(nodeHashMap.get(id));
        }
    }
    @Override
    public void removeNode(Node node){
        Node nextNode = node.next;
        Node prevNode = node.prev;
        if (nextNode == null && prevNode == null){
            head = null;
            tail = null;
            return;
        }
        if (prevNode == null) {
            head = nextNode;
            nextNode.prev = null;
        } else if (nextNode == null) {
            tail = prevNode;
            prevNode.next = null;
        } else {
            nextNode.prev = prevNode;
            prevNode.next = nextNode;
        }
        nodeHashMap.remove(node.data.getId());
        size--;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return null;
    }

    @Override
    public void linkLast(Task task) {
        int id = task.getId();
        if (nodeHashMap.containsKey(id)) {
            removeNode(nodeHashMap.get(id));
        }
        final Node oldTail = tail;
        Node newNode = new Node(task, null, tail);
        tail = newNode;
        nodeHashMap.put(id, newNode);
        if (oldTail == null){
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
    }

    public void print() {
        Node tempNode = head;
        while (tempNode != null) {
            System.out.print(tempNode.data.getId() + " ");
            tempNode = tempNode.next;
        }
        System.out.print(" size: " + size);
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node temp = head;
        while (temp != null) {
            tasks.add(temp.data);
            temp = temp.next;
        }
        print();
        return tasks;
    }
}
