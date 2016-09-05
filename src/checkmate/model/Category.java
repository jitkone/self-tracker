/*
 * Task category
 */
package checkmate.model;

import java.util.*;

/**
 */
public class Category extends EffortCollection {
	
	private static Hashtable categories = new Hashtable();
	public static Category DEFAULT = new Category();
	
	private String name = "Misc";
	private Set tasks = new LinkedHashSet();
	
	public Category() {
		
	}
	public Category(String categoryName) {
		if(categoryName != null) {
			name = categoryName;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public long getTime() {
		Iterator it = tasks.iterator();
		long effort = 0;
		while(it.hasNext()) {
			Task t = (Task) it.next();
			effort += t.getTime();
		}
		return effort;
	}
	
	public long getTimeToday() {
		Iterator it = tasks.iterator();
		long effort = 0;
		while(it.hasNext()) {
			Task t = (Task) it.next();
			effort += t.getTimeToday();
		}
		return effort;
	}
	public long getWeekTime() {
		Iterator it = tasks.iterator();
		long effort = 0;
		while(it.hasNext()) {
			Task t = (Task) it.next();
			effort += t.getWeekTime();
		}
		return effort;
	}
	
	public String getSessionTimeAsString() {
        return "";
    }
	
	public long getMonthTime() {
		Iterator it = tasks.iterator();
		long effort = 0;
		while(it.hasNext()) {
			Task t = (Task) it.next();
			effort += t.getMonthTime();
		}
		return effort;
	}
	
	public long getYearTime() {
		Iterator it = tasks.iterator();
		long effort = 0;
		while(it.hasNext()) {
			Task t = (Task) it.next();
			effort += t.getYearTime();
		}
		return effort;
	}
	
	public String toString() {
		return getName();
	}
	public boolean equals(Object o) {
		try {
			Category c = (Category) o;
			return name.equals(c.getName());
		} catch (Exception e) {
			return false;
		}
	}
	
	public static Category get(String cname) {
		Object o = null;
		if(cname == null) {
			o = DEFAULT;
			
		} else {
			o = categories.get(cname);
			if(o == null) {
				o = new Category(cname);
				categories.put(cname, o);
			}
		}
		return (Category) o;
	}
	public static void removeCategories() {
		categories.clear();
	}
	
	public void addTask(Task t) {
		tasks.add(t);
	}
	public static Collection getCategories() {
		if(categories.isEmpty()) {
			categories.put(DEFAULT.getName(), DEFAULT);
		}
		return categories.values();
	}
}
