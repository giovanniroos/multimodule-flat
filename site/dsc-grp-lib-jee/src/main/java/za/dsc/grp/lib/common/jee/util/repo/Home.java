package za.dsc.grp.lib.common.jee.util.repo;


/**
 * Home is a marker interface which allows you create a set of dynamically generated JPA queries. you implement it by extending this interface into another interface,<br>
 * this interface then is passed to {@link HomeFactory#getEntityHome(Class, za.sita.ifms.common.jee.jpa.util.EntityDirectoryService)}.<br>
 * 
 * You then add methods to your extended interface which work as finder methods, these methods have the form: "[List<T>|T]  findBy[Property][And|Or][AnotherProperty]".<br><br>
 * 
 * For example you may have an Entity class <i>Person</i> for which you want to create a home interface, <i>Person</i> has two properties: <i>name</i> and <i>surname</i>; and you have two queries you<br>
 * wish to run against this Entity: one to search where <i>name</i> is equal to and one where <i>name</i> and <i>surname</i> is equal to and it returns a unique result<br><br>
 * 
 * The first step is to create an interface which extends {@link Home} and types to the <i>Person</i> object.:<br/><br/>
 * <code>
 * 
 * 	        public interface PersonHome extends Home<Person> {
 *         }
 * 
 * </code><br/><br/>
 * 
 * You then add two methods to the interface to represent the two queries:<br/><br/>
 * <code>
 * 
 * 	        public interface PersonHome extends Home<Person> {
 * 
 *                   
 *                   List<Person> findByName(String name);
 *                   
 *                   Person findByNameAndSurname(String name, String surname);
 *         }
 * 
 * </code><br/><br/>
 * 
 * Finally you then create an instance of this interface by passing it to the {@link HomeFactory#getEntityHome(Class, javax.persistence.EntityManager) and then calling the query<br>
 *  methods, under the covers the framework will generate JPAQL to match the format of the method name so in our example <i>findByName</i> will generate the following JPAQL:
 *  <i>select x from Person x where name = ?</i><br/><br/> 
 * 
 * 
 * <b>Notes</b>
 * <li>you can override the default JPAQL by specifying a named query on the entity in question and if you have an existing query which matches the method name with the class simple name appended ("Person.findByName"), it will first look for a named query with that name.</li>
 * <li>The parameters which are passed to the query must occur in the same order as they are specified in the method name</li><br/><br/>
 * 
 * @author julian
 *
 * @param <T> the entity object type to type the interface to
 */
public interface Home<T>
{

	

	
	
}
