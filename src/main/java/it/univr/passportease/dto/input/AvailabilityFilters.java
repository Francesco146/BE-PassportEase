package it.univr.passportease.dto.input;

import it.univr.passportease.entity.Availability;
import it.univr.passportease.entity.enums.Status;
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
    /**
     * The offices name to filter the query by.
     */
    private List<String> officesName;
    /**
     * The request types to filter the query by.
     */
    private List<String> requestTypes;
    /**
     * The start date to filter the query by.
     */
    private Date startDate;
    /**
     * The end date to filter the query by.
     */
    private Date endDate;
    /**
     * The start time to filter the query by.
     */
    private LocalTime startTime;
    /**
     * The end time to filter the query by.
     */
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

        predicates.add(criteriaBuilder.equal(root.get("status"), Status.FREE));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * Checks if the start date, end date, start time and end time are valid.
     *
     * @return True if the end time is valid, that is if the start time is before the end time or the end time is null.
     */
    private boolean isEndTimeValid() {
        return endTime != null && (startTime == null || endTime.isAfter(startTime));
    }

    /**
     * Checks if the start date, end date, start time and end time are valid.
     *
     * @return True if the start time is valid, that is if the end time is after the start time or the start time is null.
     */
    private boolean isStartTimeValid() {
        return startTime != null && (endTime == null || startTime.isBefore(endTime));
    }

    /**
     * Checks if the start date and end date are valid.
     *
     * @return True if the end date is valid, that is if the start date is before the end date or the end date is null.
     */
    private boolean isEndDateValid() {
        return endDate != null && (startDate == null || endDate.after(startDate));
    }

    /**
     * Checks if the start date and end date are valid.
     *
     * @return True if the start date is valid, that is if the end date is after the start date or the start date is null.
     */
    private boolean isStartDateValid() {
        return startDate != null && (endDate == null || startDate.before(endDate));
    }

}
