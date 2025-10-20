package org.example.ui;

public abstract class ConsoleInterface {
    protected String parameter;
    protected ConsoleInterface next;
    public ConsoleInterface(String parameter){ this.parameter = parameter; }
    public void setNext(ConsoleInterface next){ this.next = next; }
    public abstract void handle(String[] params);
    protected void handleConclude(String[] params){ if (next != null) next.handle(params); }
}
