package roomescape.business.service.coordinator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.exception.PaymentClientException;
import roomescape.dto.LoginMember;
import roomescape.dto.request.UserReservationRegister;
import roomescape.dto.request.TossPaymentConfirm;
import roomescape.dto.response.TossPaymentResponse;
import roomescape.infrastructure.db.MemberJpaRepository;
import roomescape.infrastructure.db.ThemeJpaRepository;
import roomescape.infrastructure.db.TossPaymentJpaRepository;
import roomescape.infrastructure.payment.TossPaymentRestClient;
import roomescape.business.model.Member;
import roomescape.business.model.Reservation;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Role;
import roomescape.business.model.Theme;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationPaymentServiceTest {

    @Autowired
    ReservationPaymentService reservationPaymentService;

    @MockitoBean
    TossPaymentRestClient tossPaymentRestClient;

    @Autowired
    TossPaymentJpaRepository tossPaymentJpaRepository;

    @Autowired
    ReservationTicketRepository reservationTicketRepository;

    @Autowired
    ReservationTimeRepository reservationTimeRepository;

    @Autowired
    ThemeJpaRepository themeJpaRepository;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @DisplayName("예약과 결제를 정상적으로 저장한다.")
    @Test
    void test1() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme(1L);
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now().plusDays(1);

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
            member.getEmail(), member.getRole());
        UserReservationRegister request = new UserReservationRegister(date,
            time.getId(), theme.getId(), "paymentKey", "orderId", 1000L);

        TossPaymentResponse tossPaymentResponse = new TossPaymentResponse(
            "DONE", "paymentKey", "orderId"
        );

        when(tossPaymentRestClient.requestConfirmation(any(TossPaymentConfirm.class)))
            .thenReturn(tossPaymentResponse);

        // when
        reservationPaymentService.saveReservationWithPayment(request, loginMember);

        // then
        assertAll(() -> {
            assertThat(reservationTicketRepository.findAll()).hasSize(1);
            assertThat(tossPaymentJpaRepository.findAll()).hasSize(1);
        });
    }

    @DisplayName("결제 승인 결과가 DONE 이 아니면 예외를 던진다")
    @Test
    void test2() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme(1L);
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now().plusDays(1);

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
            member.getEmail(), member.getRole());
        UserReservationRegister request = new UserReservationRegister(date,
            time.getId(), theme.getId(), "paymentKey", "orderId", 1000L);

        TossPaymentResponse tossPaymentResponse = new TossPaymentResponse(
            "ABORTED", "paymentKey", "orderId"
        );

        when(tossPaymentRestClient.requestConfirmation(any(TossPaymentConfirm.class)))
            .thenReturn(tossPaymentResponse);

        // when & then
        assertThatThrownBy(
            () -> reservationPaymentService.saveReservationWithPayment(request,
                loginMember)).isInstanceOf(
            PaymentClientException.class);
    }

    private Member saveMember(Long tmp) {
        Member member = new Member("이름" + tmp, "이메일" + tmp, "비밀번호" + tmp, Role.USER);
        memberJpaRepository.save(member);

        return member;
    }

    private Theme saveTheme(Long tmp) {
        Theme theme = new Theme("이름" + tmp, "설명" + tmp, "썸네일" + tmp);
        themeJpaRepository.save(theme);

        return theme;
    }

    private ReservationTime saveTime(LocalTime reservationTime) {
        ReservationTime time = new ReservationTime(reservationTime);
        reservationTimeRepository.save(time);

        return time;
    }

    private ReservationTicket saveReservationTicket(
        LocalDate date, ReservationTime time, Theme theme, Member member) {
        Reservation reservation = new Reservation(date, time, theme, member, LocalDate.now());
        ReservationTicket reservationTicket = new ReservationTicket(reservation);

        return reservationTicketRepository.save(reservationTicket);
    }
}
