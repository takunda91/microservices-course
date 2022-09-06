package guru.springframework.msscbeerservice.services.inventory;

import guru.springframework.msscbeerservice.services.inventory.model.BeerInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Profile("local-discovery")
@Service
@RequiredArgsConstructor
public class InventoryServiceFeignClientImpl implements BeerInventoryService {

    private final InventoryServiceFeignClient inventoryServiceFeignClient;

    @Override
    public Integer getOnhandInventory(UUID beerId) {
        log.debug("Calling inventory service - BeerId : "+ beerId);
        ResponseEntity<List<BeerInventoryDto>> invOnHand = inventoryServiceFeignClient.getInvOnHand(beerId);

        Integer onHand = Objects.requireNonNull(invOnHand.getBody())
                .stream()
                .mapToInt(BeerInventoryDto::getQuantityOnHand)
                .sum();
        log.debug("Beer Id : " + beerId + " on hand is " + onHand);
        return onHand;

    }
}
