package co.za.Main.ConsoleApplication;

import javax.swing.JOptionPane;

public class ConsoleLauncher {
    
    public static void main(String[] args) {
        // Give user choice between Swing and JavaFX
        String[] options = {"Swing GUI (Simple)", "JavaFX GUI (Modern)", "Cancel"};
        
        int choice = JOptionPane.showOptionDialog(
            null,
            "Choose GUI Framework:",
            "Console Application Launcher",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        switch (choice) {
            case 0: // Swing
                launchSwingGUI();
                break;
            case 1: // JavaFX
                launchJavaFXGUI();
                break;
            case 2: // Cancel
            default:
                System.out.println("Application cancelled.");
                System.exit(0);
        }
    }
    
    private static void launchSwingGUI() {
        System.out.println("Starting Swing GUI...");
        ConsoleGUIApplication.main(new String[]{});
    }
    
    private static void launchJavaFXGUI() {
        System.out.println("Starting JavaFX GUI...");
        try {
            // Check if JavaFX is available
            Class.forName("javafx.application.Application");
            ConsoleJavaFXApplication.main(new String[]{});
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                null,
                "JavaFX is not available. Please use Swing GUI or install JavaFX.\n" +
                "For JavaFX support, run: mvn clean javafx:run",
                "JavaFX Not Available",
                JOptionPane.ERROR_MESSAGE
            );
            // Fall back to Swing
            launchSwingGUI();
        }
    }
}