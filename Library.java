// specify the package

// system imports

//- GUI IMPORTS
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

//project imports
import event.Event;
import event.EventLog;
import common.PropertyFile;

import model.Librarian;
import userinterface.MainStageContainer;
import userinterface.WindowPosition;

public class Library extends Application
{
	private Librarian myLibrarian;

	private Stage mainStage;
	
	/**
	 * 
	 */
	//-----------------------------------------------------------
	@Override
	public void start(Stage primaryStage)
	{
		System.out.println("Library Version 1.00");
		System.out.println("Copyright 2004/2015 Caleb Butcher and Michael Carter");

		MainStageContainer.setStage(primaryStage, "Library System Version 1.00");
		mainStage = MainStageContainer.getInstance();

		mainStage.setOnCloseRequest(new EventHandler <WindowEvent>() {
				@Override
				public void handle(javafx.stage.WindowEvent event) {
					System.exit(0);
				}
			});

		try
		{
			myLibrarian = new Librarian();
		}
		catch(Exception exc)
		{
		   System.err.println("Library.Library - could not create Librarian!");
		   new Event(Event.getLeafLevelClassName(this),
					 "Library.<init>", "Unable to create Librarian object",
					 Event.ERROR);
		   exc.printStackTrace();
		}

		WindowPosition.placeCenter(mainStage);
		
		mainStage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
