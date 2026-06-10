package com.never_give_up.automation.demo.factory.link;

import java.util.Stack;

public class MplsLabelFactory {
    private final Stack<Integer> labelStack = new Stack<>();

    public void push(int label) { labelStack.push(label); }
    public void swap(int label) { if (!labelStack.isEmpty()) labelStack.pop(); labelStack.push(label); }
    public void pop() { if (!labelStack.isEmpty()) labelStack.pop(); }
    public Integer top() { return labelStack.isEmpty() ? null : labelStack.peek(); }

    public void reset() { labelStack.clear(); }
}