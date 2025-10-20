package org.example.ui;

public class ExitCommand extends ConsoleInterface {
    public ExitCommand(String parameter){ super(parameter); }
    @Override
    public void handle(String[] params){
        System.out.println("Koniec programu.");
        System.exit(0);
    }
}
