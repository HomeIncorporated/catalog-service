package eu.nimble.service.catalogue.util.migration.r8;

import eu.nimble.service.catalogue.CatalogueServiceImpl;
import eu.nimble.service.catalogue.index.ItemIndexClient;
import eu.nimble.service.catalogue.persistence.util.CataloguePersistenceUtil;
import eu.nimble.service.model.ubl.catalogue.CatalogueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by suat on 18-Feb-19.
 */
@Component
public class CatalogueIndexLoader {

    private static final Logger logger = LoggerFactory.getLogger(CatalogueIndexLoader.class);

    @Autowired
    private ItemIndexClient itemIndexClient;

    public void indexCatalogues(String partyId) {
        // get catalogues
        List<CatalogueType> catalogues;
        if(partyId == null){
            catalogues = CataloguePersistenceUtil.getAllCatalogues();
        }
        else {
            catalogues = CataloguePersistenceUtil.getAllCataloguesForParty(partyId);
        }
        for(CatalogueType catalogue : catalogues) {
            try {
                itemIndexClient.indexCatalogue(catalogue);
            } catch (Exception e) {
                // do nothing
            }
        }
        logger.info("Indexing catalogues completed");
    }
}
