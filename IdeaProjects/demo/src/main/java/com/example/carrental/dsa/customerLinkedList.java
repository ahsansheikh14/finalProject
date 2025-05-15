package com.example.carrental.dsa;

import com.example.carrental.models.customer;

public class customerLinkedList {
    private Node head;
    private int size = 0;

    private static class Node {
        customer data;
        Node next;

        Node(customer data) {
            this.data = data;
        }
    }

    public void add(customer c) {
        Node newNode = new Node(c);
        if (head == null) head = newNode;
        else {
            Node temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
        size++;
    }

    // Get customer by index
    public customer get(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    // Find customer by ID
    public customer findById(int customerId) {
        Node current = head;
        while (current != null) {
            if (current.data.getCustomerId() == customerId) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }
    
    // Get number of customers in the list
    public int size() {
        return size;
    }

    // Example: Print all customers
    public void printAll() {
        Node temp = head;
        while (temp != null) {
            System.out.println(temp.data.getName());
            temp = temp.next;
        }
    }
}