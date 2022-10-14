package tourGuide.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO regroupant les données à affichées lorsqu'on souhaite afficher les attractions proches d'une personne
 */
@Getter
@Setter
public class NearbyAttractionDto {

    private LocationTourGuide userLocationTourGuide;

    private List<AttractionClosestDto> closestAttractionsList;

}
