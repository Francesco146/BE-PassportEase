package it.univr.passportease.dto.input;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.service.userworker.UserWorkerQueryService;
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

/**
 * Filters to apply to the {@link Availability} query.
 * Used in {@link UserWorkerQueryService#getAvailabilities(AvailabilityFilters, Integer, Integer)}
 */
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


    /**
     * Creates a {@link Predicate} from the {@link AvailabilityFilters} object. The {@link Predicate} is used to filter
     * the {@link Availability} query.
     *
     * @param root            must not be {@literal null}. The root entity, i.e. {@link Availability}
     * @param query           must not be {@literal null}. The query to modify. Not used in this implementation
     * @param criteriaBuilder must not be {@literal null}. Builder used to construct the {@link Predicate}
     * @return {@link Predicate} to apply to the query.
     */
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

    /**
     * @return True if the end time is valid, that is if the start time is before the end time or the end time is null.
     */
    private boolean isEndTimeValid() {
        return endTime != null && (startTime == null || endTime.isAfter(startTime));
    }

    /**
     * @return True if the start time is valid, that is if the end time is after the start time or the start time is null.
     */
    private boolean isStartTimeValid() {
        return startTime != null && (endTime == null || startTime.isBefore(endTime));
    }

    /**
     * @return True if the end date is valid, that is if the start date is before the end date or the end date is null.
     */
    private boolean isEndDateValid() {
        return endDate != null && (startDate == null || endDate.after(startDate));
    }

    /**
     * @return True if the start date is valid, that is if the end date is after the start date or the start date is null.
     */
    private boolean isStartDateValid() {
        return startDate != null && (endDate == null || startDate.before(endDate));
    }

}
