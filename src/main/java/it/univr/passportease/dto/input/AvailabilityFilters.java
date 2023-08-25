package it.univr.passportease.dto.input;

import it.univr.passportease.entity.Availability;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class AvailabilityFilters implements Specification<Availability> {
    private List<String> officesName;
    private List<String> requestTypes;
    private Date startDate;
    private Date endDate;
    private LocalTime startTime;
    private LocalTime endTime;


    @Override
    public Predicate toPredicate(@NotNull Root<Availability> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (officesName != null && !officesName.isEmpty())
            predicates.add(root.get("office").get("name").in(officesName));
        if (requestTypes != null && !requestTypes.isEmpty())
            predicates.add(root.get("request").get("requestType").get("name").in(requestTypes));

        if (isStartDateValid())
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate));
        if (isEndDateValid())
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate));
        if (isStartTimeValid())
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startTime));
        if (isEndTimeValid())
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), endTime));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private boolean isEndTimeValid() {
        return endTime != null && (startTime == null || endTime.isAfter(startTime));
    }

    private boolean isStartTimeValid() {
        return startTime != null && (endTime == null || startTime.isBefore(endTime));
    }

    private boolean isEndDateValid() {
        return endDate != null && (startDate == null || endDate.after(startDate));
    }

    private boolean isStartDateValid() {
        return startDate != null && (endDate == null || startDate.before(endDate));
    }

}
