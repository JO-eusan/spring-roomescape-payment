package roomescape.application.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.dto.response.WaitingResponse;
import roomescape.persistence.repository.WaitingRepository;

@Service
@RequiredArgsConstructor
public class WaitingAdminService {

    private final WaitingRepository waitingRepository;

    public List<WaitingResponse> getAllWaitings() {
        return waitingRepository.findAll().stream()
                .map(WaitingResponse::from)
                .collect(Collectors.toList());
    }

    public void rejectWaiting(Long id) {
        waitingRepository.rejectById(id);
    }
}
