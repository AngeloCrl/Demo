package com.app.demo.user.repository;

import com.app.demo.car.model.Car;
import com.app.demo.user.dto.SearchRequestModel;
import com.app.demo.user.model.User;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> filterCriteria(SearchRequestModel request) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getFirstName())) {
                predicates.add(criteriaBuilder.equal(root.get("firstName"), request.getFirstName()));
            }
            if (StringUtils.isNotBlank(request.getLastName())) {
                predicates.add(criteriaBuilder.equal(root.get("lastName"), request.getLastName()));
            }
            if (ObjectUtils.isNotEmpty(request.getRole())) {
                predicates.add(criteriaBuilder.equal(root.join("roles").get("role"), request.getRole()));
            }
            if (ObjectUtils.isNotEmpty(request.getCarBrand())) {
                Subquery<User> subquery = criteriaQuery.subquery(User.class);
                Root<User> subRoot = subquery.from(User.class);
                Join<User, Car> carJoin = subRoot.join("cars");
                subquery.select(subRoot)
                        .where(criteriaBuilder.and(
                                criteriaBuilder.equal(carJoin.get("brand"), request.getCarBrand()),
                                criteriaBuilder.equal(subRoot, root)
                        ));
                predicates.add(criteriaBuilder.exists(subquery));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private UserSpecification() {
    }
}
