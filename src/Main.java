import database.DatabaseSpese;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        DatabaseSpese db = new DatabaseSpese();
        boolean esci = false;

        System.out.println("\nBenvenuto nel tuo CLI Expense Tracker!");
        while (!esci) {
            try {
                System.out.println("\n--- MENU SPESE ---");
                System.out.println("1. Aggiungi nuova spesa");
                System.out.println("2. Visualizza tutte le spese");
                System.out.println("3. Calcola totale speso");
                System.out.println("0. Esci");
                System.out.print("Scegli un'opzione: ");

                int scelta = scan.nextInt();
                scan.nextLine();

                switch (scelta) {
                    case 1:
                        System.out.print("Inserisci la descrizione della spesa: ");
                        String desc = scan.nextLine();

                        System.out.print("Inserisci l'importo (€): ");
                        double importo = scan.nextDouble();
                        scan.nextLine();

                        System.out.print("Inserisci la categoria (es. cibo, bollette, hobby): ");
                        String categoria = scan.nextLine();

                        db.aggiungiSpesa(desc, importo, categoria);
                        break;
                    case 2:
                        db.stampaTutteLeSpese();
                        break;
                    case 3:
                        db.calcolaTotale();
                        break;
                    case 0:
                        esci = true;
                        System.out.println("Chiusura programma. A presto!");
                        break;
                    default:
                        System.out.println("Opzione non valida!");
                }
            } catch (Exception e) {
                System.out.println("Errore nell'input inserito. Riprova!");
                scan.nextLine();
            }
        }
        scan.close();
    }
}