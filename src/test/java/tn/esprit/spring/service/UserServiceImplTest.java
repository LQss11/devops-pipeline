package tn.esprit.spring.service;
import java.util.Date;
import java.util.List;
import java.util.Random;


import org.junit.jupiter.api.Order;
import tn.esprit.spring.entities.Role;
import tn.esprit.spring.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.services.IUserService;


@SpringBootTest
public class UserServiceImplTest {
	@Autowired
	IUserService us;
/*

*/
	public Role generateRandom(){
	Role[] values = Role.values();
	int length = values.length;
	int randomIndex = new Random().nextInt(length);
	return values[randomIndex];
	}
	@Test
	@Order(1)
	public void addUserTest(){

		User u =  new User("Salem","Affes",new Date(), generateRandom());
		Assertions.assertEquals(u,us.addUser(u));

	}
	@Test
	@Order(2)
	public void testRetrieveAllUsers(){
		List<User> listUsers = us.retrieveAllUsers();
		Assertions.assertEquals(listUsers.size(), listUsers.size());
	}
	@Test
	@Order(3)
	public void testRetrieveUser(){
		User user = us.retrieveUser("3");
		Assertions.assertEquals(1,us.retrieveUser("3").getId());
	}
	@Test
	@Order(4)
	public void testmodifyuser(){
		User expected = new User(3,"Salem", "Affes" ,  new Date(), Role.ADMINISTRATEUR);
		User user = us.updateUser(expected);
		Assertions.assertEquals(expected,user);
	}
	@Test
	@Order(5)
	public void testDeleteUser(){
		us.deleteUser("3");
		User user = us.retrieveUser("3");
		Assertions.assertNull(user);
	}



}
