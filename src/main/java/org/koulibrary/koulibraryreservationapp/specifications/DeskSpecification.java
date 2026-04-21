package org.koulibrary.koulibraryreservationapp.specifications;

import org.koulibrary.koulibraryreservationapp.domains.DeskPolicy;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;
import org.koulibrary.koulibraryreservationapp.entities.Desk;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.springframework.data.jpa.domain.Specification;

public class DeskSpecification {

    public static Specification<Desk> deskPolicy(DeskPolicy deskPolicy) {

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("policy"), deskPolicy));
    }

    public static Specification<Desk> deskStatus(DeskStatus deskStatus) {

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), deskStatus));
    }

    public static Specification<Desk> hasDeskPowerSocket(boolean hasPowerSocket) {

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("hasPowerSocket"), hasPowerSocket));
    }

    public static Specification<Desk> saloon(Saloon saloon) {

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("saloon"), saloon));
    }


}
