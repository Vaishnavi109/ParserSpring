import java.util.*;

public class ConcreteObserver implements Observer {
 
	protected String observerState;
	protected ConcreteSubject subject;

    public ConcreteObserver( ConcreteSubject theSubject )
    {
        this.subject = theSubject ;
    }
    
	public void update() {
	    // do nothing
	}

    public void showState()
    {
        System.out.println( "Observer: " + this.getClass().getName() + " = " + observerState );
    }
	 
}
 
                    


public class ConcreteSubject implements Subject {
 
	private String subjectState;
	 
	private Collection<Observer> observers = new ArrayList<Observer>() ;
	 
	public String getState() {
		return subjectState ;
	}
	 
	public void setState(String status) {
	    subjectState = status ;
        notifyObservers();
	}

	public void attach(Observer obj) {
	    observers.add(obj) ;
	}

	public void detach(Observer obj) {
        observers.remove(obj) ;
	}

	public void notifyObservers() {
        for (Observer obj  : observers)
        {
            obj.update();
        }
	}

    public void showState()
    {
        System.out.println( "Subject: " + this.getClass().getName() + " = " + subjectState );
    }
	 
}
 

public interface Observer {
 
	public abstract void update();
}
 

public class Optimist extends ConcreteObserver {
 
    public Optimist( ConcreteSubject sub )
    {
        super( sub ) ;
    }
    
	public void update() {
	    if ( subject.getState().equalsIgnoreCase("The Price of gas is at $5.00/gal")      )
        {
             observerState = "Great! It's time to go green." ;
        }
        else if ( subject.getState().equalsIgnoreCase( "The New iPad is out today" ) )
        {
            observerState = "Apple, take my money!" ;
        }
        else
        {
            observerState = ":)" ;
        }
	}
	 
}
 


public class Pessimist extends ConcreteObserver {

    public Pessimist( ConcreteSubject sub )
    {
        super( sub ) ;
    }

    public void update() {
        if ( subject.getState().equalsIgnoreCase("The Price of gas is at $5.00/gal") )
        {
            observerState = "This is the beginning of the end of the world!" ;
        }
        else if ( subject.getState().equalsIgnoreCase( "The New iPad is out today" ) )
        {
            observerState = "Not another iPad!"  ;
        }
        else
        {
            observerState = ":(" ;
        }
	}
	 
}
 

public interface Subject {
 
	public void attach(Observer obj);
	public void detach(Observer obj);
	public void notifyObservers();
}
 

public class TheEconomy extends ConcreteSubject {

    public TheEconomy()
    {
        super.setState("The Price of gas is at $5.00/gal");
    }
	 
}

/*

The Price of gas is at $5.00/gal
The New iPad is out today

*/

