// Package Specification
package model;

// System imports
import java.util.Properties;
import java.util.Vector;

// Project imports
import exception.InvalidPrimaryKeyException;
import database.*;

/**
 * @author Caleb Butcher, Michael Carter
 * @since 2016-02-02
 */
//=========================================================
public class PatronCollection extends EntityBase
{
	private static final String myTableName = "Patron";

	private Vector<Patron> patrons;

	/**
	 * PatronCollection class constructor
	 */
	public PatronCollection()
	{
		super(myTableName);

		patrons = new Vector<Patron>();
	}

	/** */
	//-----------------------------------------------------------
	public void findPatronsOlderThan(String year) throws InvalidPrimaryKeyException
	{
		String query = String.format("SELECT * FROM %s WHERE dateOfBirth > '%s'", myTableName, year);
		
		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			patrons = new Vector<Patron>();

			for(int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextPatronData = (Properties)allDataRetrieved.elementAt(cnt);

				Patron patron = new Patron(nextPatronData);

				if(patron != null)
				{
					addPatron(patron);
				}
			}
		}
		else
		{
			throw new InvalidPrimaryKeyException("No patrons older than: " + year + " found");
		}
	}

	public void findPatronsYoungerThan(String year) throws InvalidPrimaryKeyException
	{
		String query = String.format("SELECT * FROM %s WHERE dateOfBirth < '%s'", myTableName, year);
		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			patrons = new Vector<Patron>();

			for(int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextPatronData = (Properties)allDataRetrieved.elementAt(cnt);

				Patron patron = new Patron(nextPatronData);

				if(patron != null)
				{
					addPatron(patron);
				}
			}
		}
		else
		{
			throw new InvalidPrimaryKeyException("No patrons newer than: " + year + " found");
		}
	}

	public void findPatronsAtZipCode(String zip) throws InvalidPrimaryKeyException
	{
		String query = String.format("SELECT * FROM %s WHERE zipCode = '%s'", myTableName, zip);
		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			patrons = new Vector<Patron>();

			for(int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextPatronData = (Properties)allDataRetrieved.elementAt(cnt);

				Patron patron = new Patron(nextPatronData);

				if(patron != null)
				{
					addPatron(patron);
				}
			}
		}
		else
		{
			throw new InvalidPrimaryKeyException("No patrons at zip code : " + zip + " found");
		}
	}

	public void findPatronsWithNameLike(String name) throws InvalidPrimaryKeyException
	{
		String query = String.format("SELECT * FROM %s WHERE name LIKE '%%%s%%'", myTableName, name);
		Vector allDataRetrieved = getSelectQueryResult(query);

		if (allDataRetrieved != null)
		{
			patrons = new Vector<Patron>();

			for(int cnt = 0; cnt < allDataRetrieved.size(); cnt++)
			{
				Properties nextPatronData = (Properties)allDataRetrieved.elementAt(cnt);

				Patron patron = new Patron(nextPatronData);

				if(patron != null)
				{
					addPatron(patron);
				}
			}
		}
		else
		{
			throw new InvalidPrimaryKeyException("No patrons with similar name to : " + name + " found");
		}
	}

	private int findIndexToAdd(Patron p)
	{
		int low = 0;
		int high = patrons.size()-1;
		int middle;

		while (low <= high)
		{
			middle = (low+high)/2;

			Patron midSession = patrons.elementAt(middle);

			int result = Patron.compare(p, midSession);

			if (result == 0)
			{
				return middle;
			}
			else if (result < 0)
			{
				high = middle-1;
			}
			else
			{
				low = middle+1;
			}
		}
		return low;
	}

	public Object getState(String key)
	{
		if (key.equals("Patrons"))
			return patrons;
		else if (key.equals("PatronList"))
			return this;
		return null;
	}

	public Patron retrieve(String patronId)
	{
		Patron retValue = null;
		for (int cnt = 0; cnt < patrons.size(); cnt++)
		{
			Patron nextPatron = patrons.elementAt(cnt);
			String nextPatronNum = (String)nextPatron.getState("patronId");
			if (nextPatronNum.equals(patronId) == true)
			{
				retValue = nextPatron;
				break;
			}
		}
		return retValue;
	}

	/** */
	//-----------------------------------------------------------
	public void updateState(String key, Object value)
	{
		stateChangeRequest(key, value);
	}

	/** */
	//-----------------------------------------------------------
	public void stateChangeRequest(String key, Object value)
	{
		myRegistry.updateSubscribers(key, this);
	}
	
	/** */
	//-----------------------------------------------------------
	private void addPatron(Patron p)
	{
		//patrons.add(a);
		int index = findIndexToAdd(p);
		patrons.insertElementAt(p, index); // To build up a collection sorted on some key
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
