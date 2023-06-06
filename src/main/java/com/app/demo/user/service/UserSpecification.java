package com.app.demo.user.service;

import com.app.demo.user.dto.SearchModel;
import com.app.demo.user.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> filterCriteria(SearchModel request) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getFirstName())) {
                predicates.add(criteriaBuilder.equal(root.get("firstName"), request.getFirstName()));
            }
            if (StringUtils.isNotBlank(request.getLastName())) {
                predicates.add(criteriaBuilder.equal(root.get("lastName"), request.getLastName()));
            }
            if (request.getRole() != null) {
                predicates.add(criteriaBuilder.equal(root.join("roles").get("role"), request.getRole()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private UserSpecification() {
    }

}
