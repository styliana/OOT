package org.example.ui;

import org.example.util.Validators;
import java.util.Scanner;

public class MenuCommand extends ConsoleInterface {
    public MenuCommand(String parameter){ super(parameter); }

    @Override
    public void handle(String[] params) {
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("---- MENU ----");
            System.out.println("T - Wyświetl tabelę kursów");
            System.out.println("Z - Wymiana walut");
            System.out.println("0 - Wyjście");
            System.out.print("Wybierz: ");
            String line = sc.nextLine().trim().toUpperCase();

            if(!Validators.isMenuOption(line)){
                System.out.println("Nieznana opcja. Wybierz T, Z lub 0.");
                continue;
            }

            if(line.equals("T")) {
                if(next != null) next.handle(new String[]{"T"});
            }
            else if(line.equals("Z")) {
                if(next != null) next.handle(new String[]{"Z"});
            }
            else if(line.equals("0")) {
                if(next != null){
                    ConsoleInterface n = next;
                    while(n != null && !(n instanceof ExitCommand))
                        n = n.next;
                    if(n != null) n.handle(new String[]{"0"});
                }
                break;
            }
        }
    }
}
