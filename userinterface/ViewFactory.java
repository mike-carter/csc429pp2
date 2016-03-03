package userinterface;

import impresario.IModel;

//==============================================================================
public class ViewFactory {

	public static View createView(String viewName, IModel model)
	{
		if(viewName.equals("LibrarianView") == true)
		{
			return new LibrarianView(model);
		}
		else if(viewName.equals("BookView") == true)
		{
			return new BookView(model);
		}
		else if(viewName.equals("PatronView") == true)
		{
			return new PatronView(model);
		}
		else if(viewName.equals("SearchBooksView") == true)
		{
			return new SearchBooksView(model);
		}
		else if(viewName.equals("BookCollectionView") == true)
		{
			return new BookCollectionView(model);
		}
		else
			return null;
	}


	/*
	public static Vector createVectorView(String viewName, IModel model)
	{
		if(viewName.equals("SOME VIEW NAME") == true)
		{
			//return [A NEW VECTOR VIEW OF THAT NAME TYPE]
		}
		else
			return null;
	}
	*/

}
