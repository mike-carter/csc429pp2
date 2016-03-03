// specify the package
package userinterface;

// system imports
import java.util.Properties;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

// project imports
import impresario.IModel;
import model.Librarian;

/** The class containing the Teller View  for the ATM application */
//==============================================================
public class SearchBooksView extends View
{
	// GUI stuff
	private TextField searchField;
	private Button doneButton;
	private Button submitButton;
	
	// For showing error message
	private MessageView statusLog;

	// constructor for this class -- takes a model object
	//----------------------------------------------------------
	public SearchBooksView(IModel model)
	{
		super(model, "SearchBooksView");
		
		// create a container for showing the contents
		VBox container = new VBox(10);

		container.setPadding(new Insets(15, 5, 5, 5));

		// create a Node (Text) for showing the title
		container.getChildren().add(createTitle());

		// create a Node (GridPane) for showing data entry fields
		container.getChildren().add(createFormContents());

		// Error message area
		container.getChildren().add(createStatusLog("                          "));

		getChildren().add(container);

		// STEP 0: Be sure you tell your model what keys you are interested in
		myModel.subscribe("TransactionError", this);

		searchField.requestFocus();
	}

	// Create the label (Text) for the title of the screen
	//-------------------------------------------------------------
	private Node createTitle()
	{
		Text titleText = new Text("                            "+
								  "LIBRARY SYSTEM"+
								  "                            ");
		titleText.setFont(Font.font("Garamond", FontWeight.BOLD, 20));
		titleText.setTextAlignment(TextAlignment.CENTER);
		titleText.setFill(Color.BLACK);
		
		return titleText;
	}

	// Create the main form contents
	//-------------------------------------------------------------
	private GridPane createFormContents()
	{
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {	
				@Override
				public void handle(ActionEvent e) {
       		     	processAction(e);
				}
			};

		// Data entry field
		Label searchLabel = new Label("Search Books by Title: ");
		grid.add(searchLabel, 0, 0);
		
		searchField = new TextField();
		searchField.setOnAction(eventHandler);
		grid.add(searchField, 1, 0);
		
		HBox btnContainer = new HBox(10);
		btnContainer.setAlignment(Pos.CENTER);
		
		doneButton = new Button("Done");
 		doneButton.setOnAction(eventHandler);
		btnContainer.getChildren().add(doneButton);

		submitButton = new Button("Submit");
		submitButton.setOnAction(eventHandler);
		btnContainer.getChildren().add(submitButton);
		
		grid.add(btnContainer, 0, 2, 2, 1); 

		return grid;
	}

	

	// Create the status log field
	//-------------------------------------------------------------
	private MessageView createStatusLog(String initialMessage)
	{
		statusLog = new MessageView(initialMessage);

		return statusLog;
	}

	// This method processes events generated from our GUI components.
	// Make the ActionListeners delegate to this method
	//-------------------------------------------------------------
	public void processAction(Event evt)
	{
		Librarian myLib = (Librarian)myModel;
		
		// User selects Done
		if (evt.getSource().equals(doneButton))
		{
			myLib.done();
		}

		// User selects Submit or hits Enter/Return on any text fields
		else
		{
			String keyword = searchField.getText();
			if (keyword == null)
				keyword = "";

			myLib.searchBooks(keyword);

			String message = (String)myLib.getState("TransactionError");
			displayErrorMessage(message);
		}
	}
	

	//---------------------------------------------------------
	public void updateState(String key, Object value)
	{
		// STEP 6: Be sure to finish the end of the 'perturbation'
		// by indicating how the view state gets updated.
		if (key.equals("TransactionError") == true)
		{
			// display the passed text
			displayErrorMessage((String)value);
		}
	}

	/**
	 * Display error message
	 */
	//----------------------------------------------------------
	public void displayErrorMessage(String message)
	{
		statusLog.displayErrorMessage(message);
	}

	/**
	 * Clear error message
	 */
	//----------------------------------------------------------
	public void clearErrorMessage()
	{
		statusLog.clearErrorMessage();
	}
}
