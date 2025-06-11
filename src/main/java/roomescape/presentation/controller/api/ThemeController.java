package roomescape.presentation.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.business.service.ThemeService;
import roomescape.dto.request.ThemeRegister;
import roomescape.dto.response.ThemeResponse;

@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
@Tag(name = "테마 API", description = "테마 조회 및 관리 API")
public class ThemeController {

    private final ThemeService themeService;

    @Operation(summary = "전체 테마 조회", description = "등록된 모든 테마 목록을 조회합니다.")
    @GetMapping
    public List<ThemeResponse> getThemes() {
        return themeService.getAllThemes();
    }

    @Operation(summary = "인기 테마 조회", description = "특정 날짜 기준으로 인기 있는 테마를 조회합니다.")
    @GetMapping("/popular")
    public List<ThemeResponse> getPopularThemes(@RequestParam String date) {
        return themeService.getPopularThemes(date);
    }

    @Operation(summary = "테마 등록", description = "새로운 테마를 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponse addTheme(@RequestBody @Valid ThemeRegister request) {
        return themeService.saveTheme(request);
    }

    @Operation(summary = "테마 삭제", description = "ID로 테마를 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheme(@PathVariable("id") Long id) {
        themeService.deleteTheme(id);
    }
}
