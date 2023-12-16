package nextstep.courses.domain.session;

import nextstep.courses.domain.session.constant.SessionTypeEnum;
import nextstep.courses.domain.session.constant.StatusEnum;
import nextstep.payments.domain.Payment;
import nextstep.users.domain.NsUser;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SessionTypeEnum type;
    private long fee;
    private StatusEnum status;
    private int maxEnrolledCount;
    private Image image;
    private int enrolledCount;
    private UUID uuid = UUID.randomUUID();

    public Session(LocalDateTime endDate, SessionTypeEnum type, int maxEnrolledCount, Image image) {
        this(0L, LocalDateTime.now(), endDate, type, 0, maxEnrolledCount, image);
    }

    public Session(LocalDateTime endDate, SessionTypeEnum type, long fee, int maxEnrolledCount, Image image) {
        this(0L, LocalDateTime.now(), endDate, type, fee, maxEnrolledCount, image);
    }

    public Session(Long id,
                   LocalDateTime startDate,
                   LocalDateTime endDate,
                   SessionTypeEnum type,
                   long fee,
                   int maxEnrolledCount,
                   Image image) {
           this.id = id;
           this.startDate = startDate;
           this.endDate = endDate;
           this.type = type;
           this.fee = fee;
           status = StatusEnum.READY;
           this.maxEnrolledCount = maxEnrolledCount;
           enrolledCount = 0;
           this.image = image;
    }

    public Payment enroll(NsUser user, int amount) {
        checkEnrollmentAvailability();
        if (type == SessionTypeEnum.FREE) {
            return enrollFreeSession(user);
        }
        return enrollPaidSession(user, amount);
    }

    private void checkEnrollmentAvailability() {
        if (!canEnroll()) {
            throw new IllegalArgumentException("강의를 신청할 수 없습니다. 강의가 모집 중일 때 신청해주세요.");
        }
    }

    private boolean canEnroll() {
        if (status == StatusEnum.OPEN) {
            return true;
        }
        return false;
    }

    private Payment enrollFreeSession(NsUser user) {
        enrolledCount += 1;
        return new Payment(uuid.toString(), id, user.getId(), 0L);
    }

    private Payment enrollPaidSession(NsUser user, int amount) {
        checkPaidSessionAvailability(amount);
        enrolledCount += 1;
        if (enrolledCount == maxEnrolledCount) {
            status = StatusEnum.CLOSED;
        }
        return new Payment(uuid.toString(), id, user.getId(), fee);
    }

    private void checkPaidSessionAvailability(int amount) {
        if (amount < fee) {
            throw new IllegalArgumentException("결제한 금액이 수강료보다 작습니다.");
        }
    }

    public void open() {
        status = StatusEnum.OPEN;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }
}