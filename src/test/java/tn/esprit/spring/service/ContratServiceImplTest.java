package tn.esprit.spring.service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.entities.Contrat;
import tn.esprit.spring.entities.Role;
import tn.esprit.spring.services.Contracts.IContratService;

@SpringBootTest
public class ContratServiceImplTest {

  @Autowired
  IContratService cr;

  /*

*/
  public String generateRandomTypes() {
    String[] values = { "CDI", "CDD", "CDT", "CTP" };
    int length = values.length;
    int randomIndex = new Random().nextInt(length);
    return values[randomIndex];
  }

  @Test
  @Order(1)
  public void addContratTest() {
    Contrat c = new Contrat(new Date(), generateRandomTypes(), 1000);
    Assertions.assertEquals(c, cr.addContract(c));
  }

  @Test
  @Order(2)
  public void testRetrieveAllContrats() {
    List<Contrat> listContrats = cr.retrieveAllContrats();
    Assertions.assertEquals(listContrats.size(), listContrats.size());
  }

  @Test
  @Order(3)
  public void testRetrieveContrat() {
    //String listContrats =Integer.toString(cr.retrieveAllContrats().size() - 1)   ;
    //Long listContrats = Long.parseLong(Integer.toString(cr.retrieveAllContrats().size() - 1))   ;
	 Long  listContrats = Long.parseLong("1");

    //Contrat contrat = cr.retrieveContrat(listContrats);

    Assertions.assertEquals(
      listContrats,
      cr.retrieveContrat(String.valueOf(listContrats)).getReference()
    );
  }

  @Test
  @Order(4)
  public void testmodifycontrat() {
    Contrat expected = new Contrat(new Date(), generateRandomTypes(), 2000);
    Contrat contrat = cr.updateContrat(expected);
    Assertions.assertEquals(expected, contrat);
  }
  //Uncomment to test the delete method
/*
  @Test
  @Order(5)
  public void testDeleteContrat() {
    //String listContrats =Integer.toString(cr.retrieveAllContrats().size() - 1)   ;
	//String listContrats = Integer.toString(cr.retrieveAllContrats().size() - 1)   ;
	  String listContrats = "33";
    cr.deleteContrat(listContrats);
    Contrat contrat = cr.retrieveContrat(listContrats);
    Assertions.assertNull(contrat);
  }
  */
}
