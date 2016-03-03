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
import model.Book;

/** The class containing the Teller View  for the ATM application */
//==============================================================
public class BookView extends View
{	
	// GUI stuff
	private TextField titleField;
	private TextField authorField;
	private TextField pubYearField;
	private ComboBox statusBox;
	private Button doneButton;
	private Button submitButton;
	
	// For showing error message
	private MessageView statusLog;

	// constructor for this class -- takes a model object
	//----------------------------------------------------------
	public BookView(IModel book)
	{
		super(book, "BookView");
		
		// create a container for showing the contents
		VBox container = new VBox(10);

		container.setPadding(new Insets(15, 5, 5, 5));
		container.setPrefWidth(650);

		// create a Node (Text) for showing the title
		container.getChildren().add(createTitle());

		// create a Node (GridPane) for showing data entry fields
		container.getChildren().add(createFormContents());

		// Error message area
		container.getChildren().add(createStatusLog("                          "));

		getChildren().add(container);;

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
		Label titleLabel = new Label("Title: ");
		grid.add(titleLabel, 0, 0);
		
		titleField = new TextField();
		titleField.setOnAction(eventHandler);
		grid.add(titleField, 1, 0);


		Label authorLabel = new Label("Author: ");
		grid.add(authorLabel, 0, 1);

		authorField = new TextField();
		authorField.setOnAction(eventHandler);
		grid.add(authorField, 1, 1);


		Label pubYearLabel = new Label("Publication Year: ");
		grid.add(pubYearLabel, 0, 2);

		pubYearField = new TextField();
		pubYearField.setPromptText("yyyy");
		pubYearField.setOnAction(eventHandler);
		grid.add(pubYearField, 1, 2);

		ObservableList<String> options =
			//FXCollections.observableArrayList("Active", "Inactive");
			FXCollections.observableArrayList("In", "Out");
		statusBox = new ComboBox(options);
		//statusBox.setValue("Active");
		statusBox.setValue("In");
		grid.add(statusBox, 0, 3, 2, 1);
		

		HBox btnContainer = new HBox(10);
		btnContainer.setAlignment(Pos.BOTTOM_RIGHT);
		
		doneButton = new Button("Done");
 		doneButton.setOnAction(eventHandler);
		btnContainer.getChildren().add(doneButton);

		submitButton = new Button("Submit");
		submitButton.setOnAction(eventHandler);
		btnContainer.getChildren().add(submitButton);
		
		grid.add(btnContainer, 0, 5, 2, 1); 

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
			((Book)myModel).done();
		}

		// User selects Submit or hits Enter/Return on any text fields
		else
		{
			clearErrorMessage();

			String title = titleField.getText();
			String author = authorField.getText();
			String pubYear = pubYearField.getText();
			
			if ((title == null) || (title.isEmpty()))
			{
				displayErrorMessage("Please enter a title!");
				titleField.requestFocus();
			}
			else if ((author == null) || (author.isEmpty()))
			{
				displayErrorMessage("Please enter an author!");
				authorField.requestFocus();
			}
			else if ((pubYear == null) || (pubYear.isEmpty()))
			{
				displayErrorMessage("Please enter a publication year!");
				pubYearField.requestFocus();
			}
			else
			{
				int yearValue;
				
				try
				{
					yearValue = Integer.parseInt(pubYear);
				}
				catch (Exception e)
				{
					displayErrorMessage("Publication year needs to be a number!");
					pubYearField.requestFocus();
					return;
				}
				
				if (yearValue < 1800)
				{
					displayErrorMessage("Publication year is too low!");
					pubYearField.requestFocus();
				}
				else if (yearValue > 2016)
				{
					displayErrorMessage("Publication year is too high!");
					pubYearField.requestFocus();
				}
				else
				{
					String bookStatus = (String)statusBox.getValue();
					processBookUpdate(title, author, pubYear, bookStatus);
				}
			}
		}
	}

	private void processBookUpdate(String title, String author, String pubYear, String status)
	{
		Book book = (Book)myModel;
		
		Properties p = new Properties();
		p.setProperty("title", title);
		p.setProperty("author", author);
		p.setProperty("pubYear", pubYear);
		p.setProperty("bookStatus", status);

		book.setData(p);
		book.update();

		String message = (String)book.getState("UpdateStatusMessage");
		statusLog.displayMessage(message);
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
