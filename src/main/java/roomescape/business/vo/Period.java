package roomescape.business.vo;

import java.time.LocalDate;

public record Period(
    LocalDate startDate,
    LocalDate endDate) {

}
