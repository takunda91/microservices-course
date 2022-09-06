package guru.springframework.common.model.events;

import guru.springframework.common.model.BeerDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewInventoryEvent extends BeerEvent {
    static final long serialVersionUID = 1003836716317636069L;

    public NewInventoryEvent(BeerDto beerDto) {
        super(beerDto);
    }
}
