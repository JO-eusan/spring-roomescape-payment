package roomescape.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.common.exception.DuplicatedException;
import roomescape.business.vo.LoginMember;
import roomescape.dto.request.UserReservationRegister;
import roomescape.dto.response.UserReservationResponse;
import roomescape.dto.response.ReservationTicketResponse;
import roomescape.infrastructure.db.MemberJpaRepository;
import roomescape.infrastructure.db.ThemeJpaRepository;
import roomescape.infrastructure.db.WaitingJpaRepository;
import roomescape.business.model.Member;
import roomescape.business.model.Reservation;
import roomescape.business.model.ReservationTicket;
import roomescape.business.model.ReservationTime;
import roomescape.business.model.Role;
import roomescape.business.model.Theme;
import roomescape.business.model.Waiting;
import roomescape.persistence.ReservationTicketRepository;
import roomescape.persistence.ReservationTimeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationTicketRepository reservationTicketRepository;

    @Autowired
    ReservationTimeRepository reservationTimeRepository;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    WaitingJpaRepository waitingJpaRepository;

    @Autowired
    private ThemeJpaRepository themeJpaRepository;

    @DisplayName("예약을 정상적으로 저장한다.")
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

        // when
        reservationService.saveReservation(request, loginMember);

        // then
        assertThat(reservationTicketRepository.findAll()).hasSize(1);
    }

    @DisplayName("이미 존재하는 예약 시간에 예약한다면 예외를 던진다")
    @Test
    void test4() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme(1L);
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now().plusDays(1);

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
            member.getEmail(), member.getRole());
        UserReservationRegister request = new UserReservationRegister(date,
            time.getId(), theme.getId(), "paymentKey", "orderId", 1000L);

        Reservation reservation = new Reservation(
            date,
            time,
            theme,
            member,
            LocalDate.now()
        );
        reservationTicketRepository.save(new ReservationTicket(reservation));

        // when && then
        assertThatThrownBy(
            () -> reservationService.saveReservation(request, loginMember))
            .isInstanceOf(DuplicatedException.class);
    }

    @DisplayName("당일 예약을 한다면 예외를 던진다")
    @Test
    void test5() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme(1L);
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now();

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
            member.getEmail(), member.getRole());
        UserReservationRegister request = new UserReservationRegister(date,
            time.getId(), theme.getId(), "paymentKey", "orderId", 1000L);

        // when && then
        assertThatThrownBy(
            () -> reservationService.saveReservation(request, loginMember))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("예약을 취소한다")
    @Test
    void test3() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme(1L);
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now().plusDays(1);

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
            member.getEmail(), member.getRole());

        ReservationTicket reservationTicket = reservationTicketRepository.save(
            new ReservationTicket(
                new Reservation(
                    LocalDate.now().plusDays(1),
                    time,
                    theme,
                    member,
                    LocalDate.now()
                )
            )
        );

        // when
        this.reservationService.cancelReservation(reservationTicket.getId());

        // then
        List<ReservationTicketResponse> reservations = this.reservationService.getAllReservations();
        assertThat(reservations).isEmpty();
    }

    @DisplayName("사용자가 예약한 예약 내역을 모두 가져온다")
    @Test
    void test6() {
        //given
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(12, 30));
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        Theme theme = new Theme("테마", "공포", "image");
        Theme savedTheme = themeJpaRepository.save(theme);

        Member member = new Member("도기", "email@gamil.com", "password", Role.ADMIN);
        Member savedMember = memberJpaRepository.save(member);

        ReservationTicket reservationTicket = new ReservationTicket(
            new Reservation(LocalDate.now().plusDays(1), savedReservationTime, savedTheme,
                savedMember, LocalDate.now()));
        ReservationTicket savedReservationTicket = reservationTicketRepository.save(
            reservationTicket);

        LoginMember loginMember = LoginMember.from(savedMember);

        //when
        List<UserReservationResponse> response = reservationService.getReservationsOfMember(
            loginMember);

        List<UserReservationResponse> comparedResponse = List.of(
            UserReservationResponse.from(savedReservationTicket));

        //then
        assertAll(
            () -> assertThat(response).hasSize(1),
            () -> assertThat(response).isEqualTo(comparedResponse)
        );
    }

    @DisplayName("사용자의 예약 내역 삭제 시에 가장 높은 우선순위의 웨이팅을 예약으로 전환해 저장한다")
    @Test
    void test7() {
        //given
        String emailOfAdministrator = "email@gmail.com";
        Member administrator = memberJpaRepository.save(
            new Member("이름", emailOfAdministrator, "password", Role.ADMIN));
        Member user = memberJpaRepository.save(
            new Member("사용자", "user@gmail.com", "password", Role.USER));

        Theme theme = themeJpaRepository.save(new Theme("새로운 테마", "새로운 설명", "썸네일"));
        ReservationTime reservationTime = reservationTimeRepository.save(
            new ReservationTime(LocalTime.of(12, 30)));

        Waiting firstWaiting = waitingJpaRepository.save(new Waiting(
            LocalDateTime.now(),
            new Reservation(
                LocalDate.now().plusDays(1),
                reservationTime,
                theme,
                user,
                LocalDate.now()
            )
        ));

        Waiting secondWaiting = waitingJpaRepository.save(new Waiting(
            LocalDateTime.now().plusHours(1),
            new Reservation(
                LocalDate.now().plusDays(1),
                reservationTime,
                theme,
                user,
                LocalDate.now()
            )
        ));

        ReservationTicket reservationTicket = reservationTicketRepository.save(
            new ReservationTicket(new Reservation(
                LocalDate.now().plusDays(1),
                reservationTime,
                theme,
                user,
                LocalDate.now()
            )));

        // when
        reservationService.cancelReservation(reservationTicket.getId());

        // then
        List<ReservationTicket> allReservationTickets = reservationTicketRepository.findAll();
        Optional<ReservationTicket> foundReservation = allReservationTickets.stream()
            .filter(reservation1 -> reservation1.getReservationTime().getId()
                .equals(reservationTime.getId()))
            .filter(reservation1 -> reservation1.getTheme().getId().equals(theme.getId()))
            .filter(reservation1 -> reservation1.getMember().getId().equals(user.getId()))
            .findAny();

        assertAll(
            () -> assertThat(waitingJpaRepository.findAll()).doesNotContain(firstWaiting),
            () -> assertThat(allReservationTickets).doesNotContain(reservationTicket),
            () -> assertThat(foundReservation).isPresent()
        );
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
}
