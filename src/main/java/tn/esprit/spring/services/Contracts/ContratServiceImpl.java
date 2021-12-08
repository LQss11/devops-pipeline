package tn.esprit.spring.services.Contracts;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Contrat;
import tn.esprit.spring.repository.ContratRepository;

@Service
public class ContratServiceImpl implements IContratService {

  @Autowired
  ContratRepository contratRepository;

  private static final Logger l = LogManager.getLogger(
    ContratServiceImpl.class
  );

  @Override
  public List<Contrat> retrieveAllContrats() {
    List<Contrat> contrats = null;
    try {
      l.info("In Method retrieve all Contrats : ");

      contrats = (List<Contrat>) contratRepository.findAll();

      l.debug("connexion à la db ok!");

      for (Contrat contrat : contrats) {
        l.info("Contrat");
        l.info("Contrat {} : trouve:", contrat);
      }

      l.info("Out of Method retrieve all contrats with success");
    } catch (Exception e) {
      l.error("erreuur");
    }

    return contrats;
  }

  @Override
  public Contrat addContract(Contrat c) {
    l.info("In method Add Contrat");
    Contrat c_saved = contratRepository.save(c);
    l.info("Contrat Ajoute");
    return c_saved;
  }

  @Override
  public Contrat updateContrat(Contrat c) {
    l.info("update en cours");
    Contrat c_saved = contratRepository.save(c);
    l.info("update done!!");
    return c_saved;
  }

  @Override
  public void deleteContrat(String id) {
    l.info("Deleting...");
    contratRepository.deleteById(Long.parseLong(id));
    l.info("Contrat {} deleted succesfully!", id);
  }

  @Override
  public Contrat retrieveContrat(String id) {
    l.info("Recherche contrat en cours...");
    Contrat c = contratRepository.findById(Long.parseLong(id)).orElse(null);
    l.info("contrat {} retrieved", c);
    return c;
  }
}
