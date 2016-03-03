// Package Specification
package model;

// System imports
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.Hashtable;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Project imports
import exception.InvalidPrimaryKeyException;
import database.*;
import userinterface.MainStageContainer;
import userinterface.View;
import userinterface.ViewFactory;
import userinterface.WindowPosition;


/**
 * @author Caleb Butcher, Michael Carter
 * @since 2016-02-02
 */
//=========================================================
public class Patron extends EntityBase
{
    private static final String myTableName = "Patron";

    protected Properties dependencies;

    private String updateStatusMessage = "";

	protected Stage myStage;
	protected Hashtable<String, Scene> myViews;
	protected Librarian myLibrarian;

    /**
     * Patron class constructor: Primary key instantiation
     */
	@SuppressWarnings("unchecked")
	//-----------------------------------------------------------
    public Patron(String patronId) throws InvalidPrimaryKeyException
    {
		super(myTableName);

        setDependencies();

        String query = String.format("SELECT * FROM %s WHERE (patronId = '%s')", myTableName, patronId);

        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        //Must get at least one Patron
        if(allDataRetrieved != null)
		{
			int size = allDataRetrieved.size();

			//Should be only one patron. More will be an error
			if (size != 1)
			{
				throw new InvalidPrimaryKeyException(String.format("Multiple Patrons matching id : %s found", patronId));
			}
			else
			{
				//Copy all retrived data into persistence state
				Properties retrievedPatronData = allDataRetrieved.elementAt(0);
				persistentState = new Properties();

				Enumeration allKeys = retrievedPatronData.propertyNames();
				while(allKeys.hasMoreElements());
				{
					String nextKey = (String)allKeys.nextElement();
					String nextValue = retrievedPatronData.getProperty(nextKey);

					if (nextValue != null)
					{
						persistentState.setProperty(nextKey, nextValue);
					}
				}
			}
        }
        //If no Patron is found with this id throw an exception
        else
		{
			throw new InvalidPrimaryKeyException(String.format("No Patron matching ID : %s found", patronId));
        }
    }

    /**
     * Patron class constructor: Create new instance
     */
	//-----------------------------------------------------------
    public Patron(Properties props)
    {
		super(myTableName);

		setDependencies();

	    setData(props);
    }

	
	public Patron(Librarian lib)
	{
		super(myTableName);

		setDependencies();

		myStage = MainStageContainer.getInstance();
		myViews = new Hashtable<String, Scene>();

		myLibrarian = lib;
	}

	/** */
	//-----------------------------------------------------------
	public void setData(Properties props)
	{
		persistentState = new Properties();

		Enumeration allKeys = props.propertyNames();
		while (allKeys.hasMoreElements())
		{
			String nextKey = (String)allKeys.nextElement();
			String nextValue = props.getProperty(nextKey);

			if (nextValue != null)
			{
				persistentState.setProperty(nextKey, nextValue);
			}
		}
	}

	/** */
	//-----------------------------------------------------------
	public static int compare(Patron a, Patron b)
	{
		String aNum = (String)a.getState("name");
		String bNum = (String)b.getState("name");

		return aNum.compareTo(bNum);
	}

	/** */
	//-----------------------------------------------------------
	private void setDependencies()
	{
		dependencies = new Properties();

		myRegistry.setDependencies(dependencies);
	}
	
	/**
	 * Update Patron info in the database
	 */
	//-----------------------------------------------------------
	public void update()
	{
		updateStateInDatabase();
	}

	/** */
	//-----------------------------------------------------------
	private void updateStateInDatabase()
	{
		try
		{
			if(persistentState.getProperty("patronId") != null)
			{
				Properties whereClause = new Properties();

				whereClause.setProperty("patronId", persistentState.getProperty("patronId"));

				updatePersistentState(mySchema, persistentState, whereClause);

				updateStatusMessage = String.format("Data for Patron ID : %s updated successfully in database!",
													persistentState.getProperty("patronId"));
			}
			else
			{	
				Integer patronId = insertAutoIncrementalPersistentState(mySchema, persistentState);
				persistentState.setProperty("patronId", patronId.toString());
				updateStatusMessage = String.format("Data for new Patron : %s addedd successfully in database!",
													persistentState.getProperty("patronId"));
			}
		}
		catch (SQLException ex)
		{
         updateStatusMessage = "Error in adding Patron data to database";
		}
	}
	
	/**
	 * Gets the value of a specified property
	 */
	//-----------------------------------------------------------
	public Object getState(String key)
	{
        if (key.equals("UpdateStatusMessage") == true)
			return updateStatusMessage;

        return persistentState.getProperty(key);
	}

	/** */
	//-----------------------------------------------------------
	public void stateChangeRequest(String key, Object value)
	{
		myRegistry.updateSubscribers(key, this);
	}

	/** */
	//-----------------------------------------------------------
	public boolean isPatronActive()
	{
        String status = persistentState.getProperty("patronStatus");
        status = status.toLowerCase();

        if(status.equals("act"))
			return true;

        return false;
	}

	/** */
	//-----------------------------------------------------------
	public void setPatronInactive()
	{
        String status = persistentState.getProperty("patronStatus");
        status = status.toLowerCase();

        if(status.equals("act"))
			persistentState.setProperty("patronStatus", "inc");
	}

	/** */
	//-----------------------------------------------------------
	public void setPatronActive()
    {
		String status = persistentState.getProperty("patronStatus");
		status = status.toLowerCase();

		if (status.equals("inc"))
			persistentState.setProperty("patronStatus", "act");
	}

	/** */
	//-----------------------------------------------------------
	public void createAndShowPatronView()
	{
		Scene localScene = myViews.get("PatronView");
		if (localScene == null)
		{
			View newView = ViewFactory.createView("PatronView", this);
			localScene = new Scene(newView);
			myViews.put("PatronView", localScene);
		}

		myStage.setScene(localScene);
		myStage.sizeToScene();

		WindowPosition.placeCenter(myStage);
	}

	/** */
	//-----------------------------------------------------------
	public void done()
	{
		myLibrarian.done();
	}

	/** */
	//-----------------------------------------------------------
	protected void initializeSchema(String tableName)
	{
		if (mySchema == null)
	    {
			mySchema = getSchemaInfo(tableName);
		}
	}
}
