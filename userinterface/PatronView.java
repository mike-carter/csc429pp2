// specify the package
package userinterface;

// system imports
import java.util.Properties;
import java.util.Enumeration;

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
import model.Patron;

/** The class containing the Teller View  for the ATM application */
//==============================================================
public class PatronView extends View
{	
	// GUI stuff
	private TextField nameField;
	private TextField addressField;
	private TextField cityField;
	private TextField stateCodeField;
	private TextField zipCodeField;
	private TextField emailField;
	private TextField dateOfBirthField;
	private ComboBox statusBox;
	private Button doneButton;
	private Button submitButton;
	
	// For showing error message
	private MessageView statusLog;

	// constructor for this class -- takes a model object
	//----------------------------------------------------------
	public PatronView(IModel book)
	{
		super(book, "PatronView");
		
		// create a container for showing the contents
		VBox container = new VBox(10);

		container.setPrefWidth(650);
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
		Label nameLabel = new Label("Name: ");
		grid.add(nameLabel, 0, 0);
		
		nameField = new TextField();
		nameField.setOnAction(eventHandler);
		grid.add(nameField, 1, 0);


		Label addressLabel = new Label("Street Address: ");
		grid.add(addressLabel, 0, 1);

		addressField = new TextField();
		addressField.setOnAction(eventHandler);
		grid.add(addressField, 1, 1);

		
		Label cityLabel = new Label("City: ");
		grid.add(cityLabel, 0, 2);

		cityField = new TextField();
		cityField.setOnAction(eventHandler);
		grid.add(cityField, 1, 2);


		Label stateCodeLabel = new Label("State: ");
		grid.add(stateCodeLabel, 0, 3);

		stateCodeField = new TextField();
		stateCodeField.setPromptText("ex. \"NY\" or \"KY\" ");
		stateCodeField.setOnAction(eventHandler);
		grid.add(stateCodeField, 1, 3);


		Label zipCodeLabel = new Label("Zip Code: ");
		grid.add(zipCodeLabel, 0, 4);

		zipCodeField = new TextField();
		zipCodeField.setOnAction(eventHandler);
		grid.add(zipCodeField, 1, 4);

		
		Label emailLabel = new Label("Email: ");
		grid.add(emailLabel, 0, 5);

		emailField = new TextField();
		emailField.setOnAction(eventHandler);
		grid.add(emailField, 1, 5);


		Label dateOfBirthLabel = new Label("Date Of Birth: ");
		grid.add(dateOfBirthLabel, 0, 6);

		dateOfBirthField = new TextField();
		dateOfBirthField.setPromptText("yyyy-mm-dd");
		dateOfBirthField.setOnAction(eventHandler);
		grid.add(dateOfBirthField, 1, 6);

		
		ObservableList<String> options =
			//FXCollections.observableArrayList("Active", "Inactive");
			FXCollections.observableArrayList("Act", "Inc");
		statusBox = new ComboBox(options);
		//statusBox.setValue("Active");
		statusBox.setValue("Act");
		grid.add(statusBox, 0, 7, 2, 1);
		

		HBox btnContainer = new HBox(10);
		btnContainer.setAlignment(Pos.BOTTOM_RIGHT);
		
		doneButton = new Button("Done");
 		doneButton.setOnAction(eventHandler);
		btnContainer.getChildren().add(doneButton);

		submitButton = new Button("Submit");
		submitButton.setOnAction(eventHandler);
		btnContainer.getChildren().add(submitButton);
		
		grid.add(btnContainer, 0, 9, 2, 1); 

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
		// User selects Done
		if (evt.getSource().equals(doneButton))
		{
			((Patron)myModel).done();
		}

		// User selects Submit or hits Enter/Return on any text fields
		else
		{
			clearErrorMessage();

			String name = nameField.getText();
			if ((name == null) || name.isEmpty())
			{
				displayErrorMessage("Please enter a name!");
				nameField.requestFocus();
				return;
			}

			String address = addressField.getText();
			if ((address == null) || address.isEmpty())
			{
				displayErrorMessage("Please enter a street address!");
				addressField.requestFocus();
				return;
			}
			
			String city = cityField.getText();
			if ((city == null) || city.isEmpty())
			{
				displayErrorMessage("Please enter a city!");
				cityField.requestFocus();
				return;
			}

			String stateCode = stateCodeField.getText();
			if ((stateCode == null) || stateCode.isEmpty())
			{
				displayErrorMessage("Please enter a state!");
				stateCodeField.requestFocus();
				return;
			}

			String zipCode = zipCodeField.getText();
			if ((zipCode == null) || zipCode.isEmpty())
			{
				displayErrorMessage("Please enter a Zip Code!");
				zipCodeField.requestFocus();
				return;
			}

			String email = emailField.getText();
			if ((email == null) || email.isEmpty())
			{
				displayErrorMessage("Please enter an email!");
				emailField.requestFocus();
				return;
			}

			String dateOfBirth = dateOfBirthField.getText();
			if ((dateOfBirth == null) || dateOfBirth.isEmpty())
			{
				displayErrorMessage("Please enter a date of birth!");
				dateOfBirthField.requestFocus();
				return;
			}

			try
			{
				int birthYear = Integer.parseInt(dateOfBirth.split("-", 2)[0]);
				if (birthYear < 1915)
				{
					displayErrorMessage("Patron is too old!");
					dateOfBirthField.requestFocus();
					return;
				}
				if (birthYear >= 1998)
				{
					displayErrorMessage("Patron is too young!");
					dateOfBirthField.requestFocus();
					return;
				}
			}
			catch (Exception e)
			{
				displayErrorMessage("Invalid date!");
				dateOfBirthField.requestFocus();
				return;
			}

			String status = (String)statusBox.getValue();

			// Insert new Patron
			
			Patron patron = (Patron)myModel;

			Properties p = new Properties();
			p.setProperty("name", name);
			p.setProperty("address", address);
			p.setProperty("city", city);
			p.setProperty("stateCode", stateCode);
			p.setProperty("zipcode", zipCode);
			p.setProperty("email", email);
			p.setProperty("dateOfBirth", dateOfBirth);
			p.setProperty("patronStatus", status);

			patron.setData(p);
			patron.update();

			String message = (String)patron.getState("UpdateStatusMessage");
			statusLog.displayMessage(message);
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
