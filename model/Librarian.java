// specify the package
package model;

// system imports
import java.util.Hashtable;
import java.util.Properties;

import javafx.stage.Stage;
import javafx.scene.Scene;

// project imports
import impresario.IModel;
import impresario.ISlideShow;
import impresario.IView;
import impresario.ModelRegistry;

import exception.InvalidPrimaryKeyException;
import event.Event;
import userinterface.LibrarianView;
import userinterface.MainStageContainer;
import userinterface.View;
import userinterface.ViewFactory;
import userinterface.WindowPosition;

/** Class containing Librarian for the Library Application */
//===============================================================
public class Librarian implements IModel, IView
{
	private Properties dependencies;
	private ModelRegistry myRegistry;

	private Hashtable<String, Scene> myViews;
	private Stage myStage;
	
	private String transactionErrorMessage = "";

	/** */
	//-----------------------------------------------------------
	public Librarian()
	{
		myStage = MainStageContainer.getInstance();
		myViews = new Hashtable<String, Scene>();

		myRegistry = new ModelRegistry("Librarian");
		if(myRegistry == null)
		{
			new Event(Event.getLeafLevelClassName(this), "Librarian",
				"Could not instantiate Registry", Event.ERROR);
		}

		setDependencies();

		createAndShowLibrarianView();
	}

	/**
	 * Method called from client to get the value of a particular field
	 * held by the objects encapsulated by this object.
	 *
	 * @param	key	Name of database column (field) for which the client 
	 *          wants the value
	 *
	 * @return	Value associated with the field
	 */
	//----------------------------------------------------------
	public Object getState(String key)
	{
		if (key.equals("TransactionError"))
		{
			return transactionErrorMessage;
		}

		return "";
	}

	/** */
	//-----------------------------------------------------------
	private void setDependencies()
	{
		dependencies = new Properties();

		myRegistry.setDependencies(dependencies);
	}

	

	//-----------------------------------------------------------
	public void stateChangeRequest(String key, Object value)
	{
		if (key.equals("Exit"))
		{
		    System.exit(0);
		}
				 
		myRegistry.updateSubscribers(key, this);
	}

	/** Called via the IView relationship */
	//----------------------------------------------------------
	public void updateState(String key, Object value)
	{
		stateChangeRequest(key, value);
	}

	/** */
	//-----------------------------------------------------------
	public void createAndShowLibrarianView()
	{
		Scene currentScene = (Scene)myViews.get("LibrarianView");

		if (currentScene == null)
		{
			View newView = ViewFactory.createView("LibrarianView", this);
			currentScene = new Scene(newView);
			myViews.put("LibrarianView", currentScene); 
		}

		myStage.setScene(currentScene);
		myStage.sizeToScene();

		WindowPosition.placeCenter(myStage);
	}

	/** */
	//-----------------------------------------------------------
	public void createAndShowSearchBooksView()
	{
		Scene currentScene = (Scene)myViews.get("SearchBooksView");

		if (currentScene == null)
		{
			View newView = ViewFactory.createView("SearchBooksView", this);
			currentScene = new Scene(newView);
			myViews.put("SearchBooksView", currentScene); 
		}

		myStage.setScene(currentScene);
		myStage.sizeToScene();

		WindowPosition.placeCenter(myStage);
	}

	/** */
	//-----------------------------------------------------------
	public void createNewBook()
	{
		Book book = new Book(this);

		book.createAndShowBookView();
	}

	/** */
	//-----------------------------------------------------------
	public void createNewPatron()
	{
		Patron patron = new Patron(this);

		patron.createAndShowPatronView();
	}

	/** */
	//-----------------------------------------------------------
	public void searchBooks(String keyword)
	{
		BookCollection books = new BookCollection(this);

		transactionErrorMessage = "";
		try
		{
			books.findBooksWithTitleLike(keyword);
			books.createAndShowView();
		}
		catch (Exception exc)
		{
			transactionErrorMessage = exc.getMessage();
		}
	}

	/** */
	//-----------------------------------------------------------
	public void done()
	{
		createAndShowLibrarianView();
		
	}

	/** */
	//-----------------------------------------------------------
	public void subscribe(String key, IView subscriber)
	{
		myRegistry.subscribe(key, subscriber);
	}

	/** */
	//-----------------------------------------------------------
	public void unSubscribe(String key, IView subscriber)
	{
		myRegistry.unSubscribe(key, subscriber);
	}
}
