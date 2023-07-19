package salmon;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import java.util.HashMap;
import java.util.Map;

public class BossCounter extends Application {
	private TextField[] labels = new TextField[13];		
	private String[] eggNames = { "Power Egg", "Golden Egg" };
	private String[] lesserNames = { "Smallfry", "Chum", "Cohock" };
	private String[] extraNames = { "Snatcher", "Chinook", "Goldie", "Griller", "Mudmouth", "Mothership" };
	private String[] bossNames = { "Steelhead", "Flyfish", "Scrapper", "Steel Eel", "Stinger", "Maws", "Drizzler",
			"Fish Stick", "Flipper-Flopper", "Big Shot", "Slammin' Lid" };
	private String[] kingNames = { "Cohozuna", "Horrorboros" };
	
	Label confirmationLabel = new Label();
	private Map<String, Integer> enteredCounts = new HashMap<>();
	private int eggCount;
	private Label eggCounterLabel;

	@Override
	public void start(@SuppressWarnings("exports") Stage primaryStage) {
		VBox vbox = new VBox();
		vbox.setSpacing(5);

		// Create the File menu
		Menu fileMenu = new Menu("File");
		MenuItem loadItem = new MenuItem("Load");
		MenuItem saveItem = new MenuItem("Save");
		MenuItem resetItem = new MenuItem("Reset");
		fileMenu.getItems().addAll(loadItem, saveItem, resetItem);

		// Add actions to the menu items
		saveItem.setOnAction(e -> saveCountersToFile());
		loadItem.setOnAction(e -> loadCountersFromFile());
		resetItem.setOnAction(e -> resetCounters());

		// Create the Help menu
		Menu helpMenu = new Menu("Help");
		MenuItem aboutItem = new MenuItem("About BossCounter...");
		MenuItem enterShiftItem = new MenuItem("Enter Shift");
		helpMenu.getItems().addAll(aboutItem, enterShiftItem);

		aboutItem.setOnAction(e -> showAboutWindow());
		enterShiftItem.setOnAction(e -> showEnterShiftWindow());

		// Create the menu bar and add the File and Help menus
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);

		GridPane bossesGridPane = createBossesGridPane();
		GridPane kingsGridPane = createKingsGridPane();

		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Prevent tab closing

		Tab bossesTab = new Tab("Bosses", bossesGridPane);
		Tab kingsTab = new Tab("Kings", kingsGridPane);
		Tab tamagoTab = createTamagoTab();
		tabPane.getTabs().addAll(bossesTab, kingsTab, tamagoTab);

		vbox.getChildren().addAll(menuBar, confirmationLabel, tabPane);

		loadCountersFromFile();

		Scene scene = new Scene(vbox);
		primaryStage.setScene(scene);
		primaryStage.setTitle("BossCounter");
		
		// Handle window close event
	    primaryStage.setOnCloseRequest(event -> {
	        saveCountersToFile(); // Save counters before closing
	    });
		
		primaryStage.show();

		primaryStage.setMinWidth(primaryStage.getWidth() + 10);
		primaryStage.setMinHeight(primaryStage.getHeight());
	}

	private GridPane createBossesGridPane() {
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.TOP_LEFT);
		gridPane.setPadding(new Insets(10));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		for (int i = 0; i < bossNames.length; i++) {
			labels[i] = new TextField("0");
			labels[i].setPrefColumnCount(3); // Set the preferred column count to 3
			Image icon = new Image(getClass().getResourceAsStream("/Bosses/S3 " + bossNames[i] + " icon.png"));
			ImageView iconView = new ImageView(icon);
			iconView.setFitWidth(40);
			iconView.setFitHeight(40);

			Button plusButton = new Button("+");
			Button minusButton = new Button("-");
			LabelIncreaser labelIncreaser = new LabelIncreaser(labels[i]);

			plusButton.setOnAction(e -> {
				labelIncreaser.increment();
			});

			minusButton.setOnAction(e -> {
				labelIncreaser.decrement();
			});

			gridPane.add(iconView, 0, i);
			gridPane.add(new Label(bossNames[i]), 1, i);
			gridPane.add(plusButton, 2, i);
			gridPane.add(minusButton, 3, i);
			gridPane.add(labels[i], 4, i);

			plusButton.setEllipsisString("");
			minusButton.setEllipsisString("");
		}

		return gridPane;
	}

	private GridPane createKingsGridPane() {
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.TOP_LEFT);
		gridPane.setPadding(new Insets(10));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		for (int i = 0; i < kingNames.length; i++) {
			labels[i+bossNames.length] = new TextField("0");
			labels[i+bossNames.length].setPrefColumnCount(3); // Set the preferred column count to 3
			Image icon = new Image(getClass().getResourceAsStream("/Kings/S3 " + kingNames[i] + " icon.png"));
			ImageView iconView = new ImageView(icon);
			iconView.setFitWidth(40);
			iconView.setFitHeight(40);

			Button plusButton = new Button("+");
			Button minusButton = new Button("-");
			LabelIncreaser labelIncreaser = new LabelIncreaser(labels[i+bossNames.length]);

			plusButton.setOnAction(e -> {
				labelIncreaser.increment();
			});

			minusButton.setOnAction(e -> {
				labelIncreaser.decrement();
			});

			gridPane.add(iconView, 0, i);
			gridPane.add(new Label(kingNames[i]), 1, i);
			gridPane.add(plusButton, 2, i);
			gridPane.add(minusButton, 3, i);
			gridPane.add(labels[i+bossNames.length], 4, i);

			plusButton.setEllipsisString("");
			minusButton.setEllipsisString("");
		}

		return gridPane;
	}
	
	// Create the Tamago tab
	private Tab createTamagoTab() {
	    VBox tamagoVBox = new VBox(10);
	    tamagoVBox.setAlignment(Pos.CENTER);
	    tamagoVBox.setPadding(new Insets(10));

	    // Create the counter for the Egg
	    eggCounterLabel = new Label("" + eggCount);
	    eggCounterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

	    // Create the Cohozuna icon
	    Image cohozunaIcon = new Image(getClass().getResourceAsStream("/Kings/S3 Cohozuna icon.png"));
	    ImageView cohozunaIconView = new ImageView(cohozunaIcon);
	    cohozunaIconView.setFitWidth(100);
	    cohozunaIconView.setFitHeight(100);	   
	    
	    // Handle the click on the Cohozuna icon to increase the counter
	    cohozunaIconView.setOnMouseClicked(e -> {
	        eggCount++;
	        eggCounterLabel.setText("" + eggCount);
	        
	    });

	    tamagoVBox.getChildren().addAll(eggCounterLabel, cohozunaIconView);

	    Tab tamagoTab = new Tab("Tamago", tamagoVBox);

	    // Set the initial text for the eggCounterLabel
	    eggCounterLabel.setText("" + eggCount);

	    return tamagoTab;
	}

	private void saveCountersToFile() {
	    String fileName = "counters.txt";
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        for (int i = 0; i < bossNames.length; i++) {
	            String name = bossNames[i];
	            String value = labels[i].getText();
	            writer.write(name + ": " + value);
	            writer.newLine();
	        }

	        for (int i = 0; i < kingNames.length; i++) {
	            String name = kingNames[i];
	            String value = labels[i + bossNames.length].getText();
	            writer.write(name + ": " + value);
	            writer.newLine();
	        }

	        writer.write("\nEgg: " + eggCount);
	        confirmationLabel.setText(" Counters saved successfully!");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void loadCountersFromFile() {
	    String fileName = "counters.txt";
	    File file = new File(fileName);

	    try {
	        if (!file.exists()) {
	            // If the file doesn't exist, create it and set all counters to 0
	            file.createNewFile();
	            resetCounters();
	            return;
	        }

	        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	            String line;
	            eggCount = 0; // Reset the eggCount before reading from the file

	            while ((line = reader.readLine()) != null) {
	                String[] parts = line.split(": ");
	                if (parts.length == 2) {
	                    String name = parts[0];
	                    int value = Integer.parseInt(parts[1]);

	                    // Check if the line corresponds to a boss count
	                    for (int i = 0; i < bossNames.length; i++) {
	                        if (bossNames[i].equals(name)) {
	                            labels[i].setText(Integer.toString(value));
	                            break;
	                        }
	                    }

	                    // Check if the line corresponds to a king count
	                    for (int i = 0; i < kingNames.length; i++) {
	                        if (kingNames[i].equals(name)) {
	                            labels[i + bossNames.length].setText(Integer.toString(value));
	                            break;
	                        }
	                    }

	                    // Check if the line corresponds to the egg count
	                    if (name.equals("Egg")) {
	                        eggCount = value;
	                        eggCounterLabel.setText("" + eggCount);
	                    }
	                }
	            }

	            // Update the confirmation label here
	            confirmationLabel.setText(" Counters loaded successfully!");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void resetCounters() {
	    for (int i = 0; i < 13; i++) {
	        labels[i].setText("0");
	    }
	    eggCount = 0;
	    eggCounterLabel.setText("" + eggCount);
	    saveCountersToFile();
	    confirmationLabel.setText(" Counters reset to 0!");
	}
	
	// Method to create the custom window for entering boss counts during a shift
	private void showEnterShiftWindow() {
		Stage enterShiftStage = new Stage();
		enterShiftStage.initModality(Modality.APPLICATION_MODAL);
		enterShiftStage.setTitle("Enter Shift Boss Counts");
		enterShiftStage.setMinWidth(400);
		enterShiftStage.setMinHeight(400);

		GridPane enterShiftGrid = new GridPane();
		enterShiftGrid.setAlignment(Pos.CENTER);
		enterShiftGrid.setPadding(new Insets(10));
		enterShiftGrid.setHgap(10);
		enterShiftGrid.setVgap(10);

		// Create a separate row for each boss entry
		for (int i = 0; i < bossNames.length; i++) {
			String bossName = bossNames[i];
			TextField countTextField = new TextField();
			countTextField.setPromptText("0");
			countTextField.setPrefColumnCount(3);

			Image icon = new Image(getClass().getResourceAsStream("/Bosses/S3 " + bossName + " icon.png"));
			ImageView iconView = new ImageView(icon);
			iconView.setFitWidth(40);
			iconView.setFitHeight(40);

			enterShiftGrid.add(iconView, 0, i);
			enterShiftGrid.add(new Label(bossName), 1, i);
			enterShiftGrid.add(countTextField, 2, i);

			// Add a listener to store the entered count in the enteredCounts map
			countTextField.textProperty().addListener((observable, oldValue, newValue) -> {
				try {
					int count = Integer.parseInt(newValue);
					enteredCounts.put(bossName, count);
				} catch (NumberFormatException e) {
					// If the input is not a valid number, ignore it
					enteredCounts.put(bossName, 0);
				}
			});
		}
		
		 // Create a separate row for Cohozuna and Horrorboros entry using ComboBox
	    ObservableList<String> bossOptions = FXCollections.observableArrayList("None", "Cohozuna", "Horrorboros");
	    ComboBox<String> kingComboBox = new ComboBox<>(bossOptions);
	    kingComboBox.getSelectionModel().selectFirst(); // Select "None" by default
	    kingComboBox.setPrefWidth(80); // Set the preferred width for the dropdown menu

	    enterShiftGrid.add(new Label("Kings"), 1, 11);
	    enterShiftGrid.add(kingComboBox, 2, 11);

		// Create the "Enter" button for the custom window
		Button enterButton = new Button("Enter");
		enterButton.setOnAction(e -> {
			handleEnterShiftButton(kingComboBox.getValue());
			enterShiftStage.close(); // Close the window after clicking Enter
		});

		// Add Enter button to the grid
		enterShiftGrid.add(enterButton, 0, 13, 3, 1);

		Scene enterShiftScene = new Scene(enterShiftGrid);
		enterShiftStage.setScene(enterShiftScene);
		enterShiftStage.show();
	}

	// Handler for the "Enter" button in the Enter Shift window
	private void handleEnterShiftButton(String selectedKing) {
		// Iterate through the enteredCounts map and update the main counters
		for (int i = 0; i < bossNames.length; i++) {
			String bossName = bossNames[i];
			int countToAdd = enteredCounts.getOrDefault(bossName, 0);
			int currentCount = Integer.parseInt(labels[i].getText());
			int updatedCount = currentCount + countToAdd;
			labels[i].setText(Integer.toString(updatedCount));
		}
		
	    // Check if one of the kings is selected, then add one to its counter
		if (!selectedKing.equals("None")) {
	        int kingIndex = selectedKing.equals("Cohozuna") ? bossNames.length : bossNames.length + 1;
	        int currentKingCount = Integer.parseInt(labels[kingIndex].getText());
	        labels[kingIndex].setText(Integer.toString(currentKingCount + 1));
	    }
		
		// Reset the enteredCounts map to 0s
	    enteredCounts.clear();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private class LabelIncreaser {
		private TextField label;

		public LabelIncreaser(TextField label) {
			this.label = label;
		}

		public void increment() {
			int value = Integer.parseInt(label.getText());
			value++;
			label.setText(Integer.toString(value));
		}

		public void decrement() {
			int value = Integer.parseInt(label.getText());
			if (value > 0) { // Check if the value is greater than 0 before decrementing
				value--;
				label.setText(Integer.toString(value));
			}
		}
	}

	private void showAboutWindow() {
		Stage aboutStage = new Stage();
		aboutStage.initModality(Modality.APPLICATION_MODAL);
		aboutStage.setTitle("About BossCounter - Shachar700");
		aboutStage.setMinWidth(450); // Set the minimum width for the window
		aboutStage.setMinHeight(300); // Set the minimum width for the window

		VBox aboutVBox = new VBox(10);
		aboutVBox.setAlignment(Pos.CENTER);
		aboutVBox.setPadding(new Insets(10));

		// Title
		Text titleText = new Text("BossCounter");
		titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

		// Version and Date
		Text versionText = new Text("Version 0.1.0 - July 18th 2023");
		versionText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

		// App description
		Text descriptionText = new Text(
				"This is a boss salmonid counter application that helps you track boss counts in Splatoon 3 Salmon Run.");
		descriptionText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		descriptionText.setWrappingWidth(400); // Adjust the width as needed

		// Official documentation and GitHub link
		Hyperlink linksText = new Hyperlink("https://github.com/shachar700/BossCounter");
		linksText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		linksText.setOnAction(e -> openWebPage(linksText.getText()));

		// Set up HBox for label and URL
		HBox linksBox = new HBox(5); // Adjust spacing as needed
		Text officialDoc = new Text("Official Documentation: ");
		officialDoc.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		linksBox.getChildren().addAll(officialDoc, linksText);
		HBox.setHgrow(officialDoc, Priority.ALWAYS); // Ensure label takes minimum space

		// Changelog
		TextArea changelogArea = new TextArea("Changelog:\n" + "Version 0.1.0 - July 18th 2023\n"
				+ "- New features: Save, Load, Reset, Apply,\n" + "- Bug fixes\n" + "- Improvements");
		changelogArea.setEditable(false);
		changelogArea.setWrapText(true);
		changelogArea.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		ScrollPane changelogScrollPane = new ScrollPane(changelogArea);
		changelogScrollPane.setFitToHeight(true);
		changelogScrollPane.setFitToWidth(true);

		// Add all elements to the layout
		aboutVBox.getChildren().addAll(titleText, versionText, descriptionText, linksBox, changelogScrollPane);

		Scene aboutScene = new Scene(aboutVBox);
		aboutStage.setScene(aboutScene);

		aboutStage.show();
	}

	private void openWebPage(String url) {
		HostServices hostServices = getHostServices();
		if (hostServices != null) {
			hostServices.showDocument(url);
		}
	}
}